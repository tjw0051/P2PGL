package P2PGL.UDP;

import P2PGL.IKey;

import java.lang.reflect.Type;

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

    public IKey sender;

    public UDPPacket() {}

    public UDPPacket(String message, String type, IKey sender) {
        this.message = message;
        this.type = type;
        this.sender = sender;
    }
}
