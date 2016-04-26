package P2PGL.Util;

/**
 * Struct-like class for storing serialized String data and type name pair.
 */
public class SerializedData implements ISerializedData{
    /**
     * Serialized data in String format.
     */
    private String data;
    /**
     * String name of data type.
     * Example: String.class.getTypeName()
     */
    private String type;

    /** Create a serialized data container
     * @param data  Serialized data in String format
     * @param type  String name of data type
     */
    public SerializedData(String data, String type) {
        this.data = data;
        this.type = type;
    }

    /** Get serialized data
     * @return  Serialized data in String format.
     */
    public String GetData() { return data; }

    /** Get type of serialized data
     * @return  String name of data type
     */
    public String GetType() { return type; }
}
