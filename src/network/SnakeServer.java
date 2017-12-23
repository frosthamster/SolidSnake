package network;

import app.Settings;
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
import model.utils.Direction;

public class SnakeServer implements Runnable {

  public static final int port = 7272;
  private ServerSocket serverSocket;
  private Game game;
  private GameFrame currentFrame;
  private boolean isRunning = true;
  private int currentConnection;
  private Direction[] directions;
  private Settings settings;
  private Timer timer = new Timer();
  private ArrayList<Thread> clientHandlersThreads = new ArrayList<>();
  private ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
  private ArrayList<FrameChangedHandler> frameChangedHandlers = new ArrayList<>();

  public SnakeServer(Settings settings) {
    this.settings = settings;
    directions = new Direction[settings.getGameplaySettings().getSnakesAmount()];

    for (int i = 0; i < settings.getGameplaySettings().getSnakesAmount(); i++) {
      directions[i] = Direction.None;
    }

    game = new Game(settings.getGameplaySettings());

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
    timer.cancel();
    for(ClientHandler ch : clientHandlers)
      ch.stop();
    try {
      this.serverSocket.close();
      game = null;
      timer.cancel();
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

        if (currentConnection > directions.length - 1) {
          out.writeObject(new SProtocolMessage(MessageType.TooManyPlayers, null));
          continue;
        } else {
          out.writeObject(new SProtocolMessage(MessageType.Ok, null));
        }
      } catch (IOException e) {
        e.printStackTrace();
        continue;
      }

      if (currentConnection == 0) {

        timer.schedule(new TimerTask() {
          @Override
          public void run() {
            try {
              System.out.println("timer tick");
              if(game == null)
                return;
              synchronized (game) {
                currentFrame = game.makeTurn(directions);
                notifyFrameChanged(currentFrame);
              }
              out.writeObject(new SProtocolMessage(MessageType.FrameData, currentFrame));
              if (currentFrame == null) {
                for (Thread th : clientHandlersThreads) {
                  try {
                    th.join();
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                }
                stop();
              }
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }, 0, settings.getSpeed());
      }

      ClientHandler clientHandler = new ClientHandler(this, in, out, directions, currentConnection);
      clientHandlers.add(clientHandler);
      Thread clientHandlerThread = new Thread(clientHandler);
      clientHandlersThreads.add(clientHandlerThread);
      clientHandlerThread.start();
      currentConnection++;
    }
  }
}
