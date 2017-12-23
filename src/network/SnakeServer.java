package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import model.game.Game;
import model.game.GameFrame;
import model.game.GameSettings;
import model.utils.Direction;

public class SnakeServer implements Runnable {

  public static final int port = 7272;
  private ServerSocket serverSocket;
  private Game game;
  private GameFrame currentFrame;
  private boolean isRunning = true;
  private int currentConnection;
  private Direction[] directions = new Direction[2];
  private Timer timer = new Timer();
  private ArrayList<Thread> clientHandlers = new ArrayList<>();
  private ArrayList<FrameChangedHandler> frameChangedHandlers = new ArrayList<>();

  public SnakeServer() {
    for (int i = 0; i < 2; i++) {
      directions[i] = Direction.None;
    }
    try {
      serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      throw new RuntimeException();
    }
  }

  private void notifyFrameChanged(GameFrame frame) {
    for (FrameChangedHandler handler : frameChangedHandlers) {
      handler.onFrameChanged(frame);
    }
  }

  public void addFrameChangedHandler(FrameChangedHandler handler) {
    frameChangedHandlers.add(handler);
  }

  public synchronized void stop() {
    isRunning = false;
    try {
      this.serverSocket.close();
      game = null;
    } catch (IOException e) {
      throw new RuntimeException("Error closing server", e);
    }
  }

  @Override
  public void run() {
    while (isRunning) {
      Socket clientSocket;
      ObjectInputStream in;
      ObjectOutputStream out;
      try {
        System.out.println("wait client");
        clientSocket = serverSocket.accept();
        clientSocket.setSoTimeout(50);
      } catch (IOException e) {
        System.out.println("Server stopped");
        return;
      }

      try {
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());

        if(currentConnection > 1){
          out.writeObject(new SProtocolMessage(MessageType.TooManyPlayers, null));
          continue;
        }

        out.writeObject(new SProtocolMessage(MessageType.SetSettings, game == null));
        if (game == null) {
          System.out.println("wait settings");
          SProtocolMessage response = Utils.getResponse(in);
          GameSettings settings = (GameSettings) response.getData();
          game = new Game(settings);

          timer.schedule(new TimerTask() {
            @Override
            public void run() {
              try {
                System.out.println("timer tick");
                synchronized (game) {
                  currentFrame = game.makeTurn(directions);
                  notifyFrameChanged(currentFrame);
                }
                out.writeObject(new SProtocolMessage(MessageType.FrameData, currentFrame));
                if (currentFrame == null) {
                  for (Thread th : clientHandlers) {
                    try {
                      th.join();
                    } catch (InterruptedException e) {
                      e.printStackTrace();
                    }
                  }
                  timer.cancel();
                  stop();
                }
              } catch (IOException e) {
                e.printStackTrace();
              }
            }
          }, 0, 100);
        }
      } catch (IOException e) {
        e.printStackTrace();
        continue;
      }

      Thread clientHandler = new Thread(
          new ClientHandler(this, in, out, directions, currentConnection));
      clientHandlers.add(clientHandler);
      clientHandler.start();
      currentConnection++;
    }
  }
}
