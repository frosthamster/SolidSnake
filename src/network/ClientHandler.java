package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import model.utils.Direction;

public class ClientHandler implements Runnable {

  private ObjectOutputStream out;
  private ObjectInputStream in;
  private boolean running = true;
  private SnakeServer server;
  private Direction direction = Direction.None;
  private final Direction[] directions;
  private int number;
  private Map<MessageType, Consumer<Object>> handlers = new HashMap<MessageType, Consumer<Object>>() {{
    put(MessageType.MakeTurn, o -> handleMakeTurn(o));
  }};

  public ClientHandler(SnakeServer server, ObjectInputStream in, ObjectOutputStream out,
      Direction[] directions, int number) {
    this.in = in;
    this.out = out;
    this.server = server;
    this.directions = directions;
    this.number = number;

    server.addFrameChangedHandler(frame -> {
      System.out.println("send frame to client");
      try {
        out.writeObject(new SProtocolMessage(MessageType.FrameData, frame));
      } catch (IOException e) {
        stop();
      }

      if(frame == null)
        stop();
    });
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
      out.writeObject(new SProtocolMessage(MessageType.Disconnect, null));
    } catch (IOException e) {
    }
  }

  @Override
  public void run() {
    SProtocolMessage response;

    while (isRunning()) {
      try {
        System.out.println("wait client command");
        response = Utils.getResponse(in);
      } catch (IOException e) {
        continue;
      }

      handlers.get(response.getType()).accept(response.getData());

      synchronized (directions) {
        directions[number] = direction;
      }
    }
  }
}
