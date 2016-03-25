package P2PGL.Profile;

import P2PGL.IKey;
import P2PGL.Key;

import java.lang.reflect.Type;
import java.net.InetAddress;

/**
 * Created by t_j_w on 03/03/2016.
 */
public class Profile implements IProfile {

    InetAddress address;
    int port;
    int udpPort;
    String name;
    IKey key;

    public Profile(InetAddress address, int port, int udpPort, String name, IKey key) {
        this.address = address;
        this.port = port;
        this.udpPort = udpPort;
        this.name = name;
        this.key = key;
    }

    public Profile(InetAddress address, int port, String name, IKey key) {
        this(address, port, port+1, name, key);
    }

    public Profile(InetAddress address, int port, String name) {
        this(address, port, port+1, name, new Key());
    }

    public InetAddress GetIPAddress() { return address; }

    public int GetPort() { return port; }

    public int GetUDPPort() { return udpPort; }

    public String GetName() { return name; }

    public IKey GetKey() { return key; }

    public Type GetType() { return Profile.class; }
}