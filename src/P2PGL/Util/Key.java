package P2PGL.Util;

import kademlia.node.KademliaId;

import java.security.InvalidParameterException;
import java.util.Arrays;

/**
 * IKey implementation for storing a Kademlia ID
 */
public class Key implements IKey {
    KademliaId kademliaId;

    /**
     * Create a new random key.
     */
    public Key() {
        kademliaId = new KademliaId();
    }

    /** Create a new Key from byte array
     * @param bytes key bytes
     */
    public Key(byte[] bytes) {
        this.kademliaId = new KademliaId(bytes);
    }

    /** Create a new key from string. String is formatted first.
     * @param key   key String
     */
    public Key(String key) {
        this.kademliaId = new KademliaId(Format(key));
    }

    /** String format of Key
     * @return  String format of key
     */
    public String toString() {
        return kademliaId.toString();
    }

    /** Applies appropriate formatting to a string to be used as a key.
     * @see Key
     * @param key   String to be formatted
     * @return  Formatted String
     */
    public String Format(String key) {
        String newKey = key;
        if(key == "")
            throw new IllegalArgumentException("Key cannot be empty string");
        if(newKey.length() > 20)
            newKey = newKey.substring(0, 19);
        if(newKey.length() < 20)
            return String.format("%-20s", newKey).replace(' ', '0');
        else
            return newKey;
    }

    /**Check for equality between two keys.
     * @param key   Key to compare to this key
     * @return  True if keys are equal
     */
    @Override
    public boolean equals(Object key) {
        if(this == key)
            return true;

        if(key == null)
            return false;

        if(!Key.class.isAssignableFrom(key.getClass()))
            return false;

        return Arrays.equals(this.ToBytes(), ((IKey)key).ToBytes());
    }

    /** The key proceeding this key.
     * @see Key
     * @return  Key proceeding this key.
     */
    public IKey Next() {
        byte[] bytes = Arrays.copyOf(kademliaId.getBytes(), kademliaId.getBytes().length);
        bytes[bytes.length - 1]++;
        return new Key(bytes);
    }

    /** Key in bytes
     * @return  The byre format of the key.
     */
    public byte[] ToBytes() {
        return kademliaId.getBytes();
    }
}
