package network;

import java.io.IOException;
import java.io.ObjectInputStream;

public class Utils {
  public static SProtocolMessage getResponse(ObjectInputStream in) throws IOException {
    try {
      return (SProtocolMessage) in.readObject();
    } catch (ClassNotFoundException e) {
      throw new RuntimeException();
    }
  }
}
