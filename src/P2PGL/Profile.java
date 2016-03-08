package P2PGL;

import kademlia.node.KademliaId;

import java.lang.reflect.Type;
import java.net.InetAddress;

/**
 * Created by t_j_w on 03/03/2016.
 */
public class Profile implements IProfile{

    InetAddress address;
    int port;
    String name;
    KademliaId key;

    public Profile(InetAddress address, int port, String name, KademliaId key) {
        this.address = address;
        this.port = port;
        this.name = name;
    }

    public Profile(InetAddress address, int port, String name) {
        this.address = address;
        this.port = port;
        this.name = name;
        this.key = new KademliaId();
    }

    public InetAddress GetIPAddress() { return address; }

    public int GetPort() { return port; }

    public String GetName() { return name; }

    public KademliaId GetKey() { return key; }

    public Type GetType() { return Profile.class; }
}