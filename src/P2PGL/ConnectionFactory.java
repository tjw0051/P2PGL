package P2PGL;

import P2PGL.Connection.HybridConnection;
import P2PGL.Connection.IHybridConnection;
import P2PGL.Util.ISerializedData;
import P2PGL.DHT.KademliaFacade;
import P2PGL.Util.SerializedData;
import P2PGL.Profile.IProfile;
import P2PGL.Profile.IProfileCache;
import P2PGL.Profile.ProfileCache;
import P2PGL.UDP.UDPChannel;
import P2PGL.Util.IKey;
import P2PGL.Util.Key;

/**
 * Factory to create objects for setting up a Kademlia DHT.
 */
public class ConnectionFactory {
    /** Create a new Hybrid Connection using the Kademlia DHT and a UDP local channel
     * @param profile   Config for setting up Connection
     * @return  Newly created HybridConnection
     */
    public static IHybridConnection GetHybridConnection(IProfile profile) {
        IHybridConnection conn = new HybridConnection(profile,
                new KademliaFacade(profile),
                new UDPChannel(profile, profile.GetLocalChannelPort()));
        return conn;
    }

    /** Creates a SerializedData instance
     * @param data  serialized data
     * @param type  Type of data.
     * @return  Newly created SerializedData
     */
    public static ISerializedData GetSerializedData(String data, String type) {
        return new SerializedData(data, type);
    }

    /** Creates a new ProfileCache instance
     * @return  Newly created ProfileCache
     */
    public static IProfileCache GetProfileCache() {
        return new ProfileCache();
    }

    /** Creates a new Key instance
     * @return  Newly created Key
     */
    public static IKey GetKey() {
        return new Key();
    }

    /** Creats a new key instance initialised with a byte array
     * @param bytes Key parameter
     * @return  Newly created Key
     */
    public static IKey GetKey(byte[] bytes) {
        return new Key(bytes);
    }

    /** Creates an array of Key instances
     * @param count Size of Key array
     * @return  Newly created Key Array
     */
    public static IKey[] GetKeys(int count) {
        return new Key[count];
    }
}
