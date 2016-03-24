package P2PGL;

import kademlia.node.KademliaId;

/**
 * Created by t_j_w on 17/03/2016.
 */
public class Key implements IKey {
    KademliaId kademliaId;

    public Key() {
        kademliaId = new KademliaId();
    }
    public Key(KademliaId kademliaId) {
        this.kademliaId = kademliaId;
    }
    public Key(String key) {
        this.kademliaId = new KademliaId(PadKey(key));
    }

    public KademliaId getKademliaId() {
        return kademliaId;
    }
    public String toString() {
        return kademliaId.toString();
    }

    //TODO: finish
    public static String PadKey(String key) {
        if(key.length() > 20)
            throw new IllegalArgumentException("Key must be < 20 characters long");
        if(key.length() < 20)
            return String.format("%-20s", key).replace(' ', '0');
        else
            return key;
    }
}
