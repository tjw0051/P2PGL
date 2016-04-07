package P2PGL.UDP;

import P2PGL.Util.IKey;

/**
 * Created by t_j_w on 04/04/2016.
 */
public interface IPacket {
    String GetMessage();

    String GetType();

    IKey GetSender();

    String GetChannel();

    void SetAckKey(IKey key);

    IKey GetAckKey();
}
