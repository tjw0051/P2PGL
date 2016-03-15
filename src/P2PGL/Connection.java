package P2PGL;

import com.google.gson.Gson;
import com.sun.istack.internal.Nullable;
import kademlia.JKademliaNode;
import kademlia.dht.GetParameter;
import kademlia.dht.KademliaStorageEntry;
import kademlia.node.KademliaId;
import kademlia.node.Node;
import kademlia.routing.Contact;
import kademlia.routing.KademliaRoutingTable;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;


/**
 * Main Class of P2PGL.
 * @author Thomas Walker
 */
public class Connection {
    private IProfile profile;
    private JKademliaNode node;
    private ListenThread listenThread;
    private Gson gson;
    private List<UDPChannel> udpChannels;

    /**
     * Instantiate a new connection to a network.
     * @param profile   Connection parameters for peer (e.g. port, IP Address, name)
     */
    public Connection(Profile profile) {
        this.profile = profile;
        gson = new Gson();
    }

    /**
     * Connect to node.
     * @param serverName    Name of server.
     * @param destIPAddress IP Address of server.
     * @param destPort      Port number of server.
     * @throws IOException  Error connecting to server.
     */
    public void Connect(String serverName, InetAddress destIPAddress, int destPort) throws IOException {
        try {
            node = new JKademliaNode(profile.GetName(), profile.GetKey(), profile.GetPort());
            Node bootstrapNode = new Node(new KademliaId(PadKey(serverName)), destIPAddress, destPort);
            node.bootstrap(bootstrapNode);
            StoreProfile();
        } catch(IOException ioe) {
            throw ioe;
        }
    }

    /**
     * Generates a KademliaId name with the last byte incremented by 1
     * @see Connection#StoreProfile()
     * @param kadId ID to be incremented
     * @return  Incremented ID.
     */
    public static KademliaId IncrementKey(KademliaId kadId) {
        byte[] bytes = Arrays.copyOf(kadId.getBytes(), kadId.getBytes().length);
        bytes[bytes.length - 1]++;
        return new KademliaId(bytes);
    }

    //TODO: Remove profile and other pieces of player.
    public void Disconnect() throws IOException {
        try {
            node.shutdown(false);
        } catch(IOException ioe) {
            throw ioe;
        }
    }

    /**
     * Get a global data object from the DHT.
     * @param id    Key of data.
     * @return      data String value.
     * @throws IOException  Thrown when a get operation cannot be performed (check connection).
     */
    @Nullable
    public String Get(String id) throws IOException {
        return Get(new KademliaId(PadKey(id)));
    }

    /**
     * Get a global data object from the DHT.
     * @param key    Key of data.
     * @return      data String value.
     * @throws IOException  Thrown when a get operation cannot be performed (check connection).
     */
    @Nullable
    public String Get(KademliaId key) throws IOException {
        GetParameter getParameter = new GetParameter(key, "String");
        try {
            KademliaStorageEntry entry = node.get(getParameter);
            Data data = (new Data()).fromSerializedForm(entry.getContent());
            return data.getData();
        } catch(kademlia.exceptions.ContentNotFoundException notFoundE) {
            return null;
        }
    }

    /**
     * Store string data at destination name on DHT.
     * @param destKey       name to store data at.
     * @param stringData    String data to store.
     * @throws IOException  Thrown when a put operation cannot be performed (check connection).
     */
    public void Store(KademliaId destKey, String stringData) throws IOException{
        Data data = new Data(node.getNode().getNodeId().toString(), destKey, stringData, "String");
        Store(data);
    }

    /**
     * Store string data at destination name on DHT.
     * @param destKey       name to store data at.
     * @param stringData    String data to store.
     * @throws IOException  Thrown when a put operation cannot be performed (check connection).
     */
    public void Store(String destKey, String stringData) throws IOException{
        Data data = new Data(node.getNode().getNodeId().toString(), new KademliaId(PadKey(destKey)), stringData, "String");
        Store(data);
    }

    /**
     * Store string data at destination name on DHT.
     * @param data      Data to be stored.
     * @throws IOException  Thrown when a put operation cannot be performed (check connection).
     */
    public void Store(Data data) throws IOException{
        try {
            node.put(data);
        } catch(IOException ioe) {
            throw ioe;
        }
    }

    /**
     * Store user profile on DHT. Destination name of profile is
     * this nodeId with last byte incremented by 1.
     * @see Connection#IncrementKey(KademliaId)
     * @return  Key profile is stored at.
     * @throws IOException
     */
    public KademliaId StoreProfile() throws IOException {
        KademliaId profileKey = IncrementKey(GetId());
        String jsonProfile = gson.toJson(profile, profile.GetType());
        Store(profileKey, jsonProfile);
        return profileKey;
    }

    //TODO: return list
    public String[] ListUsers() {
        KademliaRoutingTable routingTable = node.getRoutingTable();
        List<Contact> routingContacts =  routingTable.getAllContacts();
        String[] users = new String[routingContacts.size()];
        for(int i = 0; i < routingContacts.size(); i++) {
            users[i] = routingContacts.get(i).getNode().getNodeId().toString();
        }
        return users;
    }

    //TODO: finish
    public static String PadKey(String key) {
        if(key.length() > 20)
            throw new IllegalArgumentException("Key must be < 20 characters long");
        if(key.length() < 20)
            return String.format("%-20s", key).replace(' ', '0');
        else
            return key;
    }

    public KademliaId GetId() {
        return node.getNode().getNodeId();
    }

    /**
     * @return Profile of this node.
     */
    public IProfile GetLocalProfile() {
        return profile;
    }

    /**
     * Retrieve the profile of user with KademliaId userKey.
     * @see Connection#StoreProfile()
     * @param userKey   KademliaId of user
     * @return  Profile of user. Returns null if profile cannot be found.
     * @throws IOException  Thrown if a get operation cannot be performed (check connection).
     */
    @Nullable
    public IProfile GetProfile(KademliaId userKey) throws IOException {
        KademliaId profileKey = IncrementKey(userKey);
        String profileJson = Get(profileKey);
        if(profileJson == null)
            return null;
        return gson.fromJson(profileJson, profile.GetType());
    }

    public IProfile GetProfile(String userKey) throws IOException {
        return GetProfile(new KademliaId(userKey));
    }

    public class ListenThread extends Thread {

    }

    public static void main(String[] args) {
    }

}
