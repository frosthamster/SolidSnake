package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import model.game.GameFrame;
import model.game.GameSettings;
import model.utils.Direction;

public class Client {
  private ObjectOutputStream out;
  private ObjectInputStream in;
  private Socket socket;

  public Client(GameSettings settings, InetAddress host, int port)
      throws IOException, TooManyPlayersException {
    Socket socket = new Socket(host, port);
    this.socket = socket;
    out = new ObjectOutputStream(socket.getOutputStream());
    in = new ObjectInputStream(socket.getInputStream());

    SProtocolMessage response = Utils.getResponse(in);
    if(response.getType() == MessageType.TooManyPlayers)
      throw new TooManyPlayersException();

    boolean settingsRequired = false;
    if (response.getType() == MessageType.SetSettings) {
      settingsRequired = (boolean) response.getData();
    }

    if (settingsRequired) {
      out.writeObject(new SProtocolMessage(MessageType.SetSettings, settings));
    }
  }

  public GameFrame getCurrentFrame() throws IOException {
    System.out.println("get frame from server");
    SProtocolMessage response = Utils.getResponse(in);
    if(response.getType() != MessageType.FrameData)
      throw new IOException();
    return (GameFrame) response.getData();
  }

  public void close(){
    try {
      in.close();
      out.close();
      socket.close();
    } catch (IOException e) {
      throw new RuntimeException();
    }
  }

  public void makeTurn(Direction direction) throws IOException {
    out.writeObject(new SProtocolMessage(MessageType.MakeTurn, direction));
  }
}
