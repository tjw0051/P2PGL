package P2PGL;
import P2PGL.IProfile;
import java.net.InetAddress;

/**
 * Created by t_j_w on 03/03/2016.
 */
public class Profile implements IProfile{

    InetAddress address;
    int port;
    String key;

    public Profile(InetAddress address, int port, String key) {
        this.address = address;
        this.port = port;
        this.key = key;
    }
    public InetAddress GetIPAddress() { return address; }

    public int GetPort() { return port; }

    public String GetKey() { return key; }
}