package P2PGL.Profile;

import P2PGL.Util.IKey;

import java.lang.reflect.Type;
import java.net.InetAddress;

/**
 * Descriptor for a node in the network.
 */
public interface IProfile {
    /** Get IP of Profile
     * @return  InetAddress GetHybridConnection IP address of profile owner.
     */
    InetAddress GetIPAddress();

    /** Get port of DHT
     * @return  int    Port of profile owner.
     */
    int GetPort();

    /** Get port of local channel
     * @return int  UDP Port of profile owner.
     */
    int GetLocalChannelPort();

    /** Get local channel name
     * @return  String name of local channel
     */
    String GetLocalChannelName();

    /** Set the local channel name
     * @param channelName   Name of channel
     */
    void SetLocalChannel(String channelName);

    /** Get profile key
     * @return  String  GetHybridConnection Key of profile owner.
     */
    IKey GetKey();

    /** Get name
     * @return  Name of profile owner
     */
    String GetName();

    /** Get type of this class - Needed for serialization and sending.
     *  over DHT.
     * @return  Type    Must return type of profile (e.g. Profile.class)
     */
    Type GetType();
}
