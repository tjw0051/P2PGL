package P2PGL.Connection;

import P2PGL.EventListener.MessageReceivedListener;
import P2PGL.Util.IKey;
import P2PGL.Profile.IProfile;
import P2PGL.UDP.ILocalChannel;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.List;

/**
 * A hybrid connection utilizing a DHT implementation and a method of local, direct communication.
 */
public interface IHybridConnection {

    /** Create a DHT node without bootstrapping it. Can be used to create the inital server.
     * @throws IOException  Thrown if a connection cannot be made to the server
     */
    void Connect() throws IOException;
    /** Connect to DHT through bootstrap server.
     * @param serverName    Bootstrap server name
     * @param serverAddress Bootstrap server address
     * @param serverPort    Bootstrap server port
     * @throws IOException  Thrown if a connection cannot be made to the server
     */
    void Connect(String serverName, InetAddress serverAddress, int serverPort) throws IOException;


    /** Disconnect from local and DHT network.
     * @throws IOException  Error disconnecting.
     */
    void Disconnect() throws IOException;

    /** Join a local communcation channel
     * @param channelName   Name/Identifier of channel
     * @throws IOException  Cannot connect to local channel
     */
    void JoinLocalChannel(String channelName) throws IOException;

    /** Returns the local channel
     * @return  local channel
     */
    ILocalChannel GetLocalChannel();

    /** Broadcast a message to all peers in the local channel.
     * @param obj   Object to send
     * @param type  Type of object - used for serialization
     *              @see Gson
     * @throws IOException  Error broadcasting message
     */
    void Broadcast(Object obj, Type type) throws IOException;

    /** Get data from DHT
     * @param key   Key where data is stored.
     * @param type  Type of data
     * @param <T>   Type of data
     * @return  Deserialized data from dht.
     * @throws IOException  Cannot get data from DHT
     * @throws ClassNotFoundException   Type name does not match any type found at runtime
     *                                  to deserialize to.
     */
    <T> T Get(IKey key, Type type) throws IOException, ClassNotFoundException;

    /** Store data in DHT
     * @param destKey   Key to store data at
     * @param obj   Object to be stored
     * @param type  Type of object.
     *              Example: {@code String.class}
     * @throws IOException  Error storing data on DHT
     */
    void Store(IKey destKey, Object obj, Type type) throws IOException;

    /** List users connected to DHT
     * @return  Keys for each user.
     */
    IKey[] ListUsers();

    /** Get Key for this node
     * @return  Key for this node
     */
    IKey GetKey();

    /**
     * @return Profile of this node.
     */
    IProfile GetLocalProfile();

    /** Set the profile of this node and store it in the DHT
     * @param profile   new Profile
     * @throws IOException  Error storing profile in DHT
     */
    void SetLocalProfile(IProfile profile) throws IOException;

    /**
     * Retrieve the profile of user with KademliaId userKey.
     * @see HybridConnection#StoreProfile()
     * @param userKey   KademliaId of user
     * @return  Profile of user. Returns null if profile cannot be found.
     * @throws IOException  Thrown if a get operation cannot be performed (check connection).
     */
    IProfile GetProfile(IKey userKey) throws IOException;

    /** Get list of profiles from DHT. Can be used in combination with {@code ListUsers()}
     *  to retreive all profiles.
     * @param keys  List of keys to find profiles for.
     * @return  profiles found matching keys
     * @throws IOException  Error retrieving profiles.
     */
    List<IProfile> GetProfiles(IKey[] keys) throws IOException;

    /** Add listeners for when a new message is received by local channel
     * @param listener  Class implementing MessageReceivedListener
     */
    void AddMessageListener(MessageReceivedListener listener);

    /** Remove message listener
     * @param listener  listener to remove
     */
    void RemoveMessageListener(MessageReceivedListener listener);
}
