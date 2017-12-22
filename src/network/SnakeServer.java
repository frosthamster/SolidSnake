package network;

import java.awt.Frame;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import model.game.Game;
import model.game.GameSettings;
import model.utils.Direction;

public class SnakeServer {

  public static final int port = 7272;
  private ServerSocket serverSocket;
  private Game game;
  private Frame currentFrame;
  private boolean isRunning = true;
  private Direction[] directions = new Direction[3];

  public SnakeServer() {
    for (int i=0;i<3;i++)
      directions[i] = Direction.None;

    try {
      serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      throw new RuntimeException();
    }
  }

  public void start() {
    while (isRunning) {
      Socket clientSocket;
      try {
        clientSocket = serverSocket.accept();
        new ObjectOutputStream(clientSocket.getOutputStream())
            .writeObject(new SProtocolMessage(MessageType.SetSettings, true));
        SProtocolMessage response = Utils.getResponse(new ObjectInputStream(clientSocket.getInputStream()));
        game = new Game((GameSettings) response.getData());

      } catch (IOException e) {
        throw new RuntimeException();
      }

      new Thread(new ClientHandler(this, clientSocket, directions, 1));
    }
  }

  public synchronized void stop() {
    isRunning = false;
    try {
      this.serverSocket.close();
    } catch (IOException e) {
      throw new RuntimeException("Error closing server", e);
    }
  }

  public synchronized Frame getCurrentFrame() {
    return currentFrame;
  }
}
