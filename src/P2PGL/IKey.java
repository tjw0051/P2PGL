package P2PGL;

/**
 * Created by t_j_w on 24/03/2016.
 */
public interface IKey {
    String toString();
    IKey Next();
    byte[] ToBytes();
    String Format(String key);
    boolean Equals(IKey key);
}
