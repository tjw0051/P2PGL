package P2PGL.Profile;

import P2PGL.IKey;
import P2PGL.Key;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal implementation of IProfile.
 */
public class Profile implements IProfile {

    InetAddress address;
    int port;
    int udpPort;
    String udpChannel;
    String name;
    IKey key;

    /** Create a Profile and set starting parameters
     * @param address   Address of profile owner
     * @param port  Port of profile owner
     * @param udpPort   UDP Port of profile owner
     * @param udpChannel    Name of UDP Channel that profile owner is a member of
     * @param name  Name of Profile owner
     * @param key   Key of profile owner
     */
    public Profile(InetAddress address, int port, int udpPort, String udpChannel, String name, IKey key) {
        this.address = address;
        this.port = port;
        this.udpPort = udpPort;
        this.udpChannel = udpChannel;
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
        this(address, port, port+1, null, name, new Key());
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
    public int GetUDPPort() { return udpPort; }

    /**
     * @return Get UDP Channel name that profile owner is a member of.
     */
    public String GetUDPChannel() { return udpChannel; }

    /**
     * @param udpChannel    Set UDP Channel name.
     */
    public void SetUDPChannel(String udpChannel) { this.udpChannel = udpChannel; }

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