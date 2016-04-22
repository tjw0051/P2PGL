package P2PGL;

import P2PGL.Config.IAckMessageConfig;
import P2PGL.Config.KademliaConfig;
import P2PGL.Connection.IHybridConnection;
import P2PGL.DHT.IDHTFacade;
import P2PGL.Profile.IProfile;
import P2PGL.Profile.IProfileCache;
import P2PGL.UDP.ILocalChannel;
import P2PGL.UDP.IPacket;
import P2PGL.Util.IKey;
import P2PGL.Util.ISerializedData;

/**
 * Factory to create objects for setting up a Kademlia DHT.
 * To create a custom factory for custom class implementations,
 * inject it into P2PGL using P2PGL.SetFactory.
 */
public interface IP2PGLFactory {

    /** Builder Method to create a new Hybrid Connection
     *  using the Kademlia DHT and a UDP local channel.
     * @param profile   Config for setting up Connection
     * @return  Newly created HybridConnection
     */
    IHybridConnection GetHybridConnection(IProfile profile);

    /** Factory method for creating DHT Facade concretion.
     * @param profile Config for setting up DHT
     * @return  Newly created DHT Facade.
     */
    public IDHTFacade GetDHTFacade(IProfile profile);

    /** Factory method for creating Local Channel concretion.
     * @param profile Config for setting up Local Channel
     * @return  Newly created Local Channel.
     */
    public ILocalChannel GetLocalChannel(IProfile profile);

    /** Creates a SerializedData instance
     * @param data  serialized data
     * @param type  Type of data.
     * @return  Newly created SerializedData
     */
    ISerializedData GetSerializedData(String data, String type);

    /** Creates a new ProfileCache instance
     * @return  Newly created ProfileCache
     */
    IProfileCache GetProfileCache();

    /** Creates a new Key instance
     * @return  Newly created Key
     */
    IKey GetKey();

    /** Creats a new key instance initialised with a byte array
     * @param bytes Key parameter
     * @return  Newly created Key
     */
    IKey GetKey(byte[] bytes);

    /** Creates an array of Key instances
     * @param count Size of Key array
     * @return  Newly created Key Array
     */
    IKey[] GetKeys(int count);

    /** Returns a class containing configuration for sending ack messages
     * @return Ack Message Configuration.
     */
    IAckMessageConfig GetAckConfig();

    IPacket GetPacket(String message, String type, IKey sender, String channel);

    IPacket GetPacket();

    KademliaConfig GetDHTConfig();
}
