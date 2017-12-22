package network;

import java.io.Serializable;

public class SProtocolMessage implements Serializable {

  private MessageType type;
  private Object data;

  public SProtocolMessage(MessageType type, Object data) {
    this.type = type;
    this.data = data;
  }

  public MessageType getType() {
    return type;
  }

  public Object getData() {
    return data;
  }
}
