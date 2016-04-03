package P2PGL.Profile;

import P2PGL.P2PGL;
import P2PGL.Util.IKey;

import java.lang.reflect.Type;
import java.net.InetAddress;

/**
 * Minimal implementation of IProfile.
 */
public class Profile implements IProfile {

    InetAddress address;
    int port;
    int udpPort;
    String localChannel;
    String name;
    IKey key;

    /** Create a Profile and set starting parameters
     * @param address   Address of profile owner
     * @param port  Port of profile owner
     * @param udpPort   UDP Port of profile owner
     * @param localChannel    Name of UDP Channel that profile owner is a member of
     * @param name  Name of Profile owner
     * @param key   Key of profile owner
     */
    public Profile(InetAddress address, int port, int udpPort, String localChannel, String name, IKey key) {
        this.address = address;
        this.port = port;
        this.udpPort = udpPort;
        this.localChannel = localChannel;
        this.name = name;
        this.key = key;
    }

    /** Create a Profile and set starting parameters. UDP port is port+1 and UDP channel is null.
     * @param address   Address of profile owner
     * @param port  Port of profile owner
     * @param name  Name of Profile owner
     * @param key   Key of profile owner
     */
    public Profile(InetAddress address, int port, String name, IKey key) {
        this(address, port, port+1, null, name, key);
    }

    /** Create a Profile and set starting parameters. UDP port = port+1, UDP channel = null, key = new Key().
     * @param address   Address of profile owner
     * @param port  Port of profile owner
     * @param name  Name of Profile owner
     */
    public Profile(InetAddress address, int port, String name) {
        this(address, port, port+1, null, name, P2PGL.GetInstance().GetFactory().GetKey());
    }

    /**
     * @return  IP Address of Profile owner
     */
    public InetAddress GetIPAddress() { return address; }

    /**
     * @return  Port of profile owner.
     */
    public int GetPort() { return port; }

    /**
     * @return UDP Port of profile owner. Default is port + 1 unless specified.
     */
    public int GetLocalChannelPort() { return udpPort; }

    /**
     * @return GetHybridConnection UDP Channel name that profile owner is a member of.
     */
    public String GetLocalChannelName() { return localChannel; }

    /**
     * @param udpChannel    Set UDP Channel name.
     */
    public void SetLocalChannel(String udpChannel) { this.localChannel = udpChannel; }

    /**
     * @return  Name of profile owner.
     */
    public String GetName() { return name; }

    /**
     * @return  Key of profile owner.
     */
    public IKey GetKey() { return key; }

    /**
     * @return Type of IProfile implementation - required for serialization.
     */
    public Type GetType() { return Profile.class; }
}