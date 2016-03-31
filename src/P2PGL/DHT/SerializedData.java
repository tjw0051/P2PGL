package P2PGL.DHT;

import com.sun.media.jfxmedia.events.PlayerStateEvent;

/**
 * Object for storing data-key pair.
 */
public class SerializedData implements ISerializedData{
    /**
     * Serialized data in String format.
     */
    private String data;
    /**
     * String name of data type.
     * @example String.class.getTypeName()
     */
    private String type;

    /**
     * @param data  Serialized data in String format
     * @param type  String name of data type
     */
    public SerializedData(String data, String type) {
        this.data = data;
        this.type = type;
    }

    /**
     * @return  Serialized data in String format.
     */
    public String GetData() { return data; }

    /**
     * @return  String name of data type
     */
    public String GetType() { return type; }
}
