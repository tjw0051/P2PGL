package P2PGL;

import P2PGL.Config.AckMessageConfig;
import P2PGL.Config.IAckMessageConfig;
import P2PGL.Config.KademliaConfig;
import P2PGL.Connection.HybridConnection;
import P2PGL.Connection.IHybridConnection;
import P2PGL.DHT.IDHTFacade;
import P2PGL.Profile.Profile;
import P2PGL.UDP.ILocalChannel;
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
import java.net.InetAddress;

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
                GetDHTFacade(profile),
                GetLocalChannel(profile));
        return conn;
    }

    /** Factory method for creating DHT Facade concretion.
     * @param profile Config for setting up DHT
     * @return  Newly created DHT Facade.
     */
    public IDHTFacade GetDHTFacade(IProfile profile) {
        return new KademliaFacade(profile);
    }

    /** Factory method for creating Local Channel concretion.
     * @param profile Config for setting up Local Channel
     * @return  Newly created Local Channel.
     */
    public ILocalChannel GetLocalChannel(IProfile profile) {
        return new UDPChannel(profile, profile.GetLocalChannelPort());
    }

    /** Creates a SerializedData instance
     * @param data  serialized data
     * @param type  Type of data.
     * @return  Newly created SerializedData
     */
    public ISerializedData GetSerializedData(String data, String type) {
        return new SerializedData(data, type);
    }

    /** Create a new Profile instance
     * @param address   Address of profile owner
     * @param port  Port of profile owner
     * @param localPort   UDP Port of profile owner
     * @param localChannel    Name of UDP Channel that profile owner is a member of
     * @param name  Name of Profile owner
     * @param key   Key of profile owner
     * @return new Profile instance.
     */
    public IProfile GetProfile(InetAddress address, int port, int localPort,
                               String localChannel, String name, IKey key) {
        return new Profile(address, port, localPort, localChannel, name, key);
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

    /** Returns an IPacket concretion initailised with parameters.
     * @param message   Packet message
     * @param type  Type of packet
     * @param sender    Packet sender
     * @param channel   Channel of packet sender
     * @return  New IPacket implementation
     */
    public IPacket GetPacket(String message, String type, IKey sender, String channel) {
        return new UDPPacket(message, type, sender, channel);
    }

    /** Returns an IPacket concretion.
     * @return  New IPacket implementation
     */
    public IPacket GetPacket() {
        return new UDPPacket();
    }

    /** Get Configuration for DHT
     * @return DHT config
     */
    public KademliaConfig GetDHTConfig() {
        return new KademliaConfig(60000L, 2000L, 2000L, 10, 5, 3, 1);
    }
}
