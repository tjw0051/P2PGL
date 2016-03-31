package P2PGL.DHT;

import P2PGL.Exceptions.ContentNotFoundException;
import P2PGL.IKey;
import P2PGL.Profile.IProfile;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Facade to hide DHT functions, allowing Kademlia to be swapped
 * for a different DHT implementation (e.g. Chord, Tapestry)
 */
public interface IDHTFacade {

    /** Set the profile used for this connection
     * @param profile   Profile containing connection details (port, name, key)
     */
    void SetProfile(IProfile profile);

    /** Start a DHT node without connecting to an existing node
     * @example Used to create a server.
     * @throws IOException  Cannot create DHT node.
     */
    void Connect() throws IOException;

    /** Create a DHT node and bootstrap to a network using name, address and port
     *  of the bootstrap server.
     * @param serverName    Name of server
     * @param serverAddress Address of server
     * @param serverPort    Port of server
     * @throws IOException  Cannot connect to bootstrap server
     */
    void Connect(String serverName, InetAddress serverAddress, int serverPort) throws IOException;

    /**
     * @return  Returns True if the DHT is connected.
     */
    boolean isConnected();

    /** Disconnect the DHT node from the network.
     * @throws IOException  Error disconnecting
     */
    void Disconnect() throws IOException;

    /** Store data of Type type on the hash table at key.
     * @param key   Key the data is stored under
     * @param data  Serialized string data to be stored on dht.
     * @param type  Type of data.
     * @throws IOException  Error storing data on DHT
     */
    void Store(IKey key, String data, String type) throws IOException;

    /** Get data of Type type stored at key.
     * @param key   Key that the data is stored under
     * @param type  String type name of the data
     * @return  The data in an ISerializedData object.
     * @throws IOException  Error getting data from DHT.
     */
    //TODO: Use IKey for keys instead of strings.
    ISerializedData Get(IKey key, String type) throws IOException, ContentNotFoundException;

    /** List users connected to DHT.
     * @return  IKeys of users
     */
    IKey[] ListUsers();

    /** Get the ID of this node.
     * @return ID of node.
     */
    IKey GetId();
}
