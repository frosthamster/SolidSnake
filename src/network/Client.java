package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import model.game.GameFrame;
import model.utils.Direction;

public class Client {

  private ObjectOutputStream out;
  private ObjectInputStream in;
  private Socket socket;
  private int playersCount;

  public Client(InetAddress host, int port)
      throws IOException, TooManyPlayersException {
    Socket socket = new Socket(host, port);
    this.socket = socket;
    out = new ObjectOutputStream(socket.getOutputStream());
    in = new ObjectInputStream(socket.getInputStream());

    SProtocolMessage response = Utils.getResponse(in);
    if (response.getType() == MessageType.TooManyPlayers) {
      throw new TooManyPlayersException();
    }
    playersCount = (int) response.getData();
  }

  public GameFrame getCurrentFrame() throws IOException {
    System.out.println("get frame from server");
    SProtocolMessage response = Utils.getResponse(in);

    if (response.getType() == MessageType.Disconnect) {
      throw new IOException();
    }

    if (response.getType() != MessageType.FrameData) {
      throw new IOException();
    }
    return (GameFrame) response.getData();
  }

  public void close() {
    try {
      in.close();
      out.close();
      socket.close();
    } catch (IOException e) {
      throw new RuntimeException();
    }
  }

  public void makeTurn(Direction direction) throws IOException {
    if(direction != Direction.None)
      out.writeObject(new SProtocolMessage(MessageType.MakeTurn, direction));
  }

  public int getPlayersCount(){
    return playersCount;
  }
}
