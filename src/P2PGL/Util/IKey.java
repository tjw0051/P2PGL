package P2PGL.Util;

/**
 * Created by t_j_w on 24/03/2016.
 */
public interface IKey {
    /** String format of Key
     * @return  String format of key
     */
    String toString();

    /** The key proceeding this key.
     * @see Key
     * @return  Key proceeding this key.
     */
    IKey Next();

    /** Key in bytes
     * @return  The byre format of the key.
     */
    byte[] ToBytes();

    /** Applies appropriate formatting to a string to be used as a key.
     * @see Key
     * @param key   String to be formatted
     * @return  Formatted String
     */
    String Format(String key);

    /**Check for equality between two keys.
     * @param key   Key to compare to this key
     * @return  True if keys are equal
     */
    @Override
    boolean equals(Object key);
}
