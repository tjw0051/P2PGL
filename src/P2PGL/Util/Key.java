package P2PGL.Util;

import kademlia.node.KademliaId;

import java.util.Arrays;

/**
 * Created by t_j_w on 17/03/2016.
 */
public class Key implements IKey {
    KademliaId kademliaId;

    public Key() {
        kademliaId = new KademliaId();
    }

    public Key(byte[] bytes) {
        this.kademliaId = new KademliaId(bytes);
    }

    public Key(String key) {
        this.kademliaId = new KademliaId(Format(key));
    }

    public String toString() {
        return kademliaId.toString();
    }

    //TODO: finish
    public String Format(String key) {
        if(key.length() > 20)
            throw new IllegalArgumentException("Key must be < 20 characters long");
        if(key.length() < 20)
            return String.format("%-20s", key).replace(' ', '0');
        else
            return key;
    }

    public boolean Equals(IKey key) {
        return Arrays.equals(this.ToBytes(), key.ToBytes());
    }

    public IKey Next() {
        byte[] bytes = Arrays.copyOf(kademliaId.getBytes(), kademliaId.getBytes().length);
        bytes[bytes.length - 1]++;
        return new Key(bytes);
    }

    public byte[] ToBytes() {
        return kademliaId.getBytes();
    }
}
