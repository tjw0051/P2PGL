package P2PGL.DHT;

import P2PGL.*;
import P2PGL.Exceptions.ContentNotFoundException;
import P2PGL.Profile.IProfile;
import P2PGL.Util.IKey;
import P2PGL.Util.ISerializedData;
import kademlia.JKademliaNode;
import kademlia.dht.GetParameter;
import kademlia.dht.KademliaStorageEntry;
import kademlia.node.KademliaId;
import kademlia.node.Node;
import kademlia.routing.Contact;
import kademlia.routing.KademliaRoutingTable;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

/**
 * Wrapper for Kademlia functions.
 */
public class KademliaFacade implements IDHTFacade {

    private JKademliaNode node;
    private String name;
    private KademliaId key;
    private int port;

    public KademliaFacade() {}

    public KademliaFacade(IProfile profile) {
        SetProfile(profile);
    }

    /** Set the name, key and port for this node from an IProfile
     * @param profile Profile containing connection details (port, name, key)
     */
    @Override
    public void SetProfile(IProfile profile) {
        this.name = profile.GetName();
        this.key = new KademliaId(profile.GetKey().ToBytes());
        this.port = profile.GetPort();
    }

    /** Create Kademlia node and bootstrap to server with serverName, serverAddress, serverPort.
     * @param serverName    Name of server
     * @param serverAddress Address of server
     * @param serverPort    Port of server
     * @throws IOException  Could not Connect to DHT
     */
    @Override
    public void Connect(String serverName, InetAddress serverAddress, int serverPort) throws IOException{

        node = new JKademliaNode(name,
                new Node(key, InetAddress.getLoopbackAddress(), port),
                port,
                P2PGL.GetInstance().GetFactory().GetDHTConfig());
        //node = new JKademliaNode(name, key, port);
        Node bootstrapNode = new Node(new KademliaId(
                P2PGL.GetInstance().GetFactory().GetKey().Format(serverName)),
                        serverAddress, serverPort);
        node.bootstrap(bootstrapNode);
    }

    /** Create a Kademlia node without bootstrapping to an existing network.
     *  Can be used to create a bootstrap server.
     * @throws IOException  Could not connect to DHT
     */
    @Override
    public void Connect() throws IOException {
        node = new JKademliaNode(name, key, port);
    }

    /** Check if the node is still running
     * @return  True if node is running
     */
    public boolean isConnected() {
        if(node != null)
            return node.getServer().isRunning();
        else
            return false;
    }

    /** Shutdown Kademlia node
     * @throws IOException  Error shutting down node.
     */
    @Override
    public void Disconnect() throws IOException{
        node.stopRefreshOperation();
        node.shutdown(false);
        node = null;
    }

    /** Store data on DHT at KademliaId(key.toBytes)
     * @param key  Key the data is stored under
     * @param data Serialized string data to be stored on dht.
     * @param type Type of data.
     * @throws IOException  Error storing data
     */
    @Override
    public void Store(IKey key, String data, String type) throws IOException{
        Data kadData = new Data(node.getNode().getNodeId().toString(), new KademliaId(key.ToBytes()), data, type);
        node.put(kadData);
    }

    /** GetHybridConnection data of Type type from DHT stored at IKey key.
     * @param key  Key that the data is stored under
     * @param type String type name of the data
     * @return  Serialized data and type
     * @throws IOException  Could not get data from DHT
     * @throws ContentNotFoundException Key not found in DHT.
     */
    @Override
    public ISerializedData Get(IKey key, String type) throws IOException, ContentNotFoundException {
        GetParameter getParameter = new GetParameter(new KademliaId(key.ToBytes()), type);
        try {
            KademliaStorageEntry entry = node.get(getParameter);
            Data data = (new Data()).fromSerializedForm(entry.getContent());
            //return new SerializedData(data.getData(), data.getType());
            return P2PGL.GetInstance().GetFactory().GetSerializedData(data.getData(), data.getType());
        } catch(kademlia.exceptions.ContentNotFoundException notFoundE) {
            throw new ContentNotFoundException("Data at key: " + key.toString() + " could not be found.");
        }
    }

    /** List users connected to DHT.
     * @return  IKey[] array of users.
     */
    @Override
    public IKey[] ListUsers() {
        KademliaRoutingTable routingTable = node.getRoutingTable();
        List<Contact> routingContacts =  routingTable.getAllContacts();
        //String[] users = new String[routingContacts.size()];
        //IKey[] users = new Key[routingContacts.size()];
        IKey[] users = P2PGL.GetInstance().GetFactory().GetKeys(routingContacts.size());
        for(int i = 0; i < routingContacts.size(); i++) {
            //users[i] = new Key(routingContacts.get(i).getNode().getNodeId().getBytes());
            users[i] = P2PGL.GetInstance().GetFactory().GetKey(routingContacts.get(i).getNode().getNodeId().getBytes());
        }
        return users;
    }

    /** GetHybridConnection the ID of this node
     * @return  ID of node
     */
    @Override
    public IKey GetId() {
        //return new Key(node.getNode().getNodeId().getBytes());
        return P2PGL.GetInstance().GetFactory().GetKey(node.getNode().getNodeId().getBytes());
    }

    /** Extend or shorten a string to 20 characters - required by KademliaId node IDs.
     * @param key   String to be formatted.
     * @return  Formatted string of 20 characters.
     */
    protected static String PadKey(String key) {
        if(key.length() > 20)
            key = key.substring(0, 19);
            //throw new IllegalArgumentException("Key must be < 20 characters long");
        if(key.length() < 20)
            return String.format("%-20s", key).replace(' ', '0');
        else
            return key;
    }
}
