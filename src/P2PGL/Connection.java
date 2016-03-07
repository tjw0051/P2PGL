package P2PGL;

import com.google.gson.Gson;
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
 * Created by Tom.
 */
public class Connection {
    private IProfile profile;
    private JKademliaNode node;
    private ListenThread listenThread;
    private Gson gson;

    public Connection(IProfile profile) {
        this.profile = profile;
        gson = new Gson();
    }

    public void Connect(String serverName, InetAddress destIPAddress, int destPort) throws IOException {
        try {
            node = new JKademliaNode(profile.GetKey(), new KademliaId(), profile.GetPort());
            Node bootstrapNode = new Node(new KademliaId(serverName), destIPAddress, destPort);
            node.bootstrap(bootstrapNode);
        } catch(IOException ioe) {
            throw ioe;
        }
    }

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

    public String Get(String id) throws IOException {
        return Get(new KademliaId(id));
    }

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

    public void Store(KademliaId destKey, String stringData) throws IOException{
        Data data = new Data(node.getNode().getNodeId().toString(), destKey, stringData, "String");
        Store(data);
    }

    public void Store(String destKey, String stringData) throws IOException{
        Data data = new Data(node.getNode().getNodeId().toString(), new KademliaId(destKey), stringData, "String");
        Store(data);
    }
    public void Store(Data data) throws IOException{
        try {
            node.put(data);
        } catch(IOException ioe) {
            throw ioe;
        }
    }

    public KademliaId StoreProfile() throws IOException {
        KademliaId profileKey = IncrementKey(GetId());
        String jsonProfile = gson.toJson(profile);
        Store(profileKey, jsonProfile);
        return profileKey;
    }

    public String ReadMessageStream() {
        return "";
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
        return String.format("%-20s", key).replace(' ', '0');
    }

    public KademliaId GetId() {
        return node.getNode().getNodeId();
    }

    public IProfile GetLocalProfile() {
        return profile;
    }

    public IProfile GetProfile(KademliaId userKey) throws IOException {
        KademliaId profileKey = IncrementKey(userKey);
        String profileJson = Get(profileKey);
        if(profileJson == null)
            return null;
        return gson.fromJson(profileJson, IProfile.class);
    }

    public class ListenThread extends Thread {

    }

    public static void main(String[] args) {
    }

}
