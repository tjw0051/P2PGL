package P2PGL;

import P2PGL.Config.AckMessageConfig;
import P2PGL.Config.IAckMessageConfig;
import P2PGL.Config.KademliaConfig;
import P2PGL.Connection.HybridConnection;
import P2PGL.Connection.IHybridConnection;
import P2PGL.UDP.IPacket;
import P2PGL.UDP.UDPPacket;
import P2PGL.Util.ISerializedData;
import P2PGL.DHT.KademliaFacade;
import P2PGL.Util.SerializedData;
import P2PGL.Profile.IProfile;
import P2PGL.Profile.IProfileCache;
import P2PGL.Profile.ProfileCache;
import P2PGL.UDP.UDPChannel;
import P2PGL.Util.IKey;
import P2PGL.Util.Key;

import java.io.File;

/**
 * Factory to create objects for setting up a Kademlia DHT.
 */
public class P2PGLFactory implements IP2PGLFactory {

    public P2PGLFactory() {}
    /** Create a new Hybrid Connection using the Kademlia DHT and a UDP local channel
     * @param profile   Config for setting up Connection
     * @return  Newly created HybridConnection
     */
    public IHybridConnection GetHybridConnection(IProfile profile) {
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
    public ISerializedData GetSerializedData(String data, String type) {
        return new SerializedData(data, type);
    }

    /** Creates a new ProfileCache instance
     * @return  Newly created ProfileCache
     */
    public IProfileCache GetProfileCache() {
        return new ProfileCache();
    }

    /** Creates a new Key instance
     * @return  Newly created Key
     */
    public IKey GetKey() {
        return new Key();
    }

    /** Creats a new key instance initialised with a byte array
     * @param bytes Key parameter
     * @return  Newly created Key
     */
    public IKey GetKey(byte[] bytes) {
        return new Key(bytes);
    }

    /** Creates an array of Key instances
     * @param count Size of Key array
     * @return  Newly created Key Array
     */
    public IKey[] GetKeys(int count) {
        return new Key[count];
    }

    /** Returns a class containing configuration for sending ack messages
     * @return Ack Message Configuration.
     */
    public IAckMessageConfig GetAckConfig() { return new AckMessageConfig(); }

    public IPacket GetPacket(String message, String type, IKey sender, String channel) {
        return new UDPPacket(message, type, sender, channel);
    }

    public IPacket GetPacket() {
        return new UDPPacket();
    }

    public KademliaConfig GetDHTConfig() {
        return new KademliaConfig(60000L, 2000L, 2000L, 10, 5, 3, 1);
    }
}
