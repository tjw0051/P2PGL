package P2PGL;

import java.net.InetAddress;

/**
 * Created by Tom.
 */
public interface IProfile {
    InetAddress GetIPAddress();
    int GetPort();
    String GetKey();
}
