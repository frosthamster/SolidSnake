package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import model.game.GameFrame;
import model.utils.Direction;

public class ClientHandler implements Runnable {

  private Socket clientSocket;
  private ObjectOutputStream out;
  private ObjectInputStream in;
  private boolean running = true;
  private SnakeServer server;
  private Direction direction = Direction.None;
  private final Direction[] directions;
  private int number;
  private GameFrame currentFrame;
  private Map<MessageType, Consumer<Object>> handlers = new HashMap<MessageType, Consumer<Object>>() {{
    put(MessageType.MakeTurn, o -> handleMakeTurn(o));
  }};

  public ClientHandler(SnakeServer server, Socket clientSocket, Direction[] directions, int number) {
    try {
      clientSocket.setSoTimeout(100);
    } catch (SocketException e) {
      e.printStackTrace();
    }

    try {
      out = new ObjectOutputStream(clientSocket.getOutputStream());
      in = new ObjectInputStream(clientSocket.getInputStream());
    } catch (IOException e) {
      throw new RuntimeException();
    }

    this.server = server;
    this.clientSocket = clientSocket;
    this.directions = directions;
    this.number = number;
  }

  private void handleMakeTurn(Object data) {
    direction = (Direction) data;
  }

  private synchronized boolean isRunning() {
    return running;
  }

  public synchronized void stop() {
    running = false;

    try {
      clientSocket.close();
    } catch (IOException e) {
      throw new RuntimeException();
    }
  }

  @Override
  public void run() {
    SProtocolMessage response;

    while (isRunning()) {
      try {
        response = Utils.getResponse(in);
      } catch (IOException e) {
        continue;
      }

      handlers.get(response.getType()).accept(response.getData());
      GameFrame newFrame = server.getCurrentFrame();
      if(currentFrame != newFrame)
        try {
          out.writeObject(new SProtocolMessage(MessageType.FrameData, newFrame));
          currentFrame = newFrame;
        } catch (IOException e) {
          throw new RuntimeException();
        }

      synchronized (directions) {
        directions[number] = direction;
      }
    }
  }
}
