package P2PGL.Util;

/**
 * Struct-like class for storing serialized String data and type name pair.
 */
public interface ISerializedData {
    /** Serialized data in String form
     * @return  String data.
     */
    String GetData();

    /** Name of data type
     * @return  String type name
     */
    String GetType();
}
