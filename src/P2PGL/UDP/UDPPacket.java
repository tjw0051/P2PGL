package P2PGL.UDP;

import P2PGL.Util.IKey;

/**
 * Packet for sending data across UDP channel
 */
public class UDPPacket implements IPacket {
    /**
     * JSON serialization of type.
     */
    private String message;
    public String GetMessage() { return message; }

    /**
     * Deserialized Type of message.
     */
    private String type;
    public String GetType() { return type; }

    /**
     * Key of sender
     */
    private IKey sender;
    public IKey GetSender() { return sender; }

    /**
     * Name of UDP Channel
     */
    private String channel;
    public String GetChannel() { return channel; }

    private IKey ackKey;
    public void SetAckKey(IKey key) { ackKey = key; }
    public IKey GetAckKey() { return ackKey; }

    /** UDP packet for storing a Json serialized data and type name.
     * @param message   Json serialized object
     *                  @see com.google.gson.Gson
     * @param type      String name of object type
     *                  Example: {@code String.class.getTypeName()}
     * @param sender    Key of sender (this)
     * @param channel   UDP channel destination for packet
     */
    public UDPPacket(String message, String type, IKey sender, String channel) {
        this.message = message;
        this.type = type;
        this.sender = sender;
        this.channel = channel;
    }

    public UDPPacket() {}
}
