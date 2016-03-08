package P2PGL;

import kademlia.dht.KadContent;
import kademlia.node.KademliaId;

import java.lang.reflect.Type;
import java.net.InetAddress;

/**
 * Created by Tom.
 */
public interface IProfile {
    /**
     * @return  InetAddress Get IP address of profile owner.
     */
    InetAddress GetIPAddress();

    /**
     * @return  int    Port of profile owner.
     */
    int GetPort();

    /**
     * @return  String  Get Key of profile owner.
     */
    KademliaId GetKey();

    String GetName();

    /**
     * @return  Type    Must return type of profile (e.g. Profile.class)
     */
    Type GetType();
}
