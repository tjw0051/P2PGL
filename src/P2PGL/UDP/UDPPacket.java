package P2PGL.UDP;

import P2PGL.Util.IKey;

/**
 * Created by t_j_w on 21/03/2016.
 */
public class UDPPacket {
    /**
     * JSON serialization of type.
     */
    public String message;

    /**
     * Deserialized Type of message.
     */
    public String type;

    /**
     * Key of sender
     */
    public IKey sender;

    /**
     * Name of UDP Channel
     */
    public String channel;

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
}
