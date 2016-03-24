package P2PGL;

import P2PGL.EventListener.MessageReceivedListener;
import com.google.gson.Gson;
import com.sun.istack.internal.Nullable;


import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Main Class of P2PGL.
 * @author Thomas Walker
 */
public class Connection{
    private IProfile profile;
    private JKademliaNode node;
    private Gson gson;
    private KademliaFacade kademlia;

    /**
     * Instantiate a new connection to a network.
     * @param profile   Connection parameters for peer (e.g. port, IP Address, name)
     */
    public Connection(IProfile profile) {
        this.profile = profile;
        gson = new Gson();
    }

    /**
     * KademliaFacade to node.
     * @param serverName    Name of server.
     * @param destIPAddress IP Address of server.
     * @param destPort      Port number of server.
     * @throws IOException  Error connecting to server.
     */
    public void Connect(String serverName, InetAddress destIPAddress, int destPort) throws IOException {
        try {
            node = new JKademliaNode(profile.GetName(), profile.GetKey().getKademliaId(), profile.GetPort());
            Node bootstrapNode = new Node(new KademliaId(Key.PadKey(serverName)), destIPAddress, destPort);
            node.bootstrap(bootstrapNode);
            StoreProfile();
        } catch(IOException ioe) {
            throw ioe;
        }
    }

    public void Connect() throws IOException {
        try {
            node = new JKademliaNode(profile.GetName(), profile.GetKey().getKademliaId(), profile.GetPort());
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
            node = null;
        } catch(IOException ioe) {
            ioe.printStackTrace();
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
        //return Get(new KademliaId(PadKey(id)));
        return Get(new Key(id));
    }

    @Nullable
    public String Get(Key key) throws IOException {
        return Get(key.kademliaId);
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
            System.out.println("Content not found");
            return null;
        }
    }

    public void Store(Key destKey, String stringData) throws IOException {
        Store(destKey.getKademliaId(), stringData);
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
        Data data = new Data(node.getNode().getNodeId().toString(), new KademliaId(Key.PadKey(destKey)), stringData, "String");
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
        KademliaId profileKey = IncrementKey(GetId().kademliaId);
        String jsonProfile = gson.toJson(profile, profile.GetType());
        Store(profileKey, jsonProfile);
        return profileKey;
    }

    //TODO: return list
    public Key[] ListUsers() {
        KademliaRoutingTable routingTable = node.getRoutingTable();
        List<Contact> routingContacts =  routingTable.getAllContacts();
        //String[] users = new String[routingContacts.size()];
        Key[] users = new Key[routingContacts.size()];
        for(int i = 0; i < routingContacts.size(); i++) {
            users[i] = new Key(routingContacts.get(i).getNode().getNodeId());
        }
        return users;
    }



    public Key GetId() {
        return new Key(node.getNode().getNodeId());
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
    public IProfile GetProfile(Key userKey) throws IOException {
        KademliaId profileKey = IncrementKey(userKey.kademliaId);
        String profileJson = Get(profileKey);
        if(profileJson == null)
            return null;
        return gson.fromJson(profileJson, profile.GetType());
    }

    public IProfile GetProfile(String userKey) throws IOException {
        return GetProfile(new Key(userKey));
    }

    public List<IProfile> GetProfiles(Key[] keys) throws IOException{
        List<IProfile> profiles = new ArrayList<IProfile>();
        for(int i = 0; i < keys.length; i++) {
            try {
                IProfile prof = GetProfile(keys[i]);
                if(prof != null)
                    profiles.add(prof);
            } catch (IOException ioe) {
                throw ioe;
            }
        }
        return profiles;
    }

    public static void main(String[] args) {
    }

}
