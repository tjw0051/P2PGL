package P2PGL.Profile;

import P2PGL.Util.IKey;

import java.lang.reflect.Type;
import java.net.InetAddress;

/**
 * Descriptor for a node in the network.
 */
public interface IProfile {
    /**
     * @return  InetAddress GetHybridConnection IP address of profile owner.
     */
    InetAddress GetIPAddress();

    /**
     * @return  int    Port of profile owner.
     */
    int GetPort();

    /**
     * @return int  UDP Port of profile owner.
     */
    int GetLocalChannelPort();

    /**
     * @return  String name of local channel
     */
    String GetLocalChannelName();

    /** Set the local channel name
     * @param channelName   Name of channel
     */
    void SetLocalChannel(String channelName);

    /**
     * @return  String  GetHybridConnection Key of profile owner.
     */
    IKey GetKey();

    /**
     * @return  Name of profile owner
     */
    String GetName();

    /**
     * @return  Type    Must return type of profile (e.g. Profile.class)
     */
    Type GetType();
}
