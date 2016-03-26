package P2PGL.DHT;

import P2PGL.*;
import P2PGL.Profile.IProfile;
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
import java.util.function.BooleanSupplier;

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

    @Override
    public void SetProfile(IProfile profile) {
        this.name = profile.GetName();
        this.key = new KademliaId(profile.GetKey().ToBytes());
        this.port = profile.GetPort();
    }

    @Override
    public void Connect(String serverName, InetAddress serverAddress, int serverPort) throws IOException{
        node = new JKademliaNode(name, key, port);
        Node bootstrapNode = new Node(new KademliaId(PadKey(serverName)), serverAddress, serverPort);
        node.bootstrap(bootstrapNode);
    }

    @Override
    public void Connect() throws IOException {
        node = new JKademliaNode(name, key, port);
    }

    public boolean isConnected() {
        return node.getServer().isRunning();
    }

    @Override
    public void Disconnect() throws IOException{
        node.shutdown(false);
        node = null;
    }

    @Override
    public void Store(IKey key, String data) throws IOException{
        Data kadData = new Data(node.getNode().getNodeId().toString(), new KademliaId(key.ToBytes()), data, "String");
        node.put(kadData);
    }

    @Override
    public String Get(IKey key) throws IOException {
        GetParameter getParameter = new GetParameter(new KademliaId(key.ToBytes()), "String");
        try {
            KademliaStorageEntry entry = node.get(getParameter);
            Data data = (new Data()).fromSerializedForm(entry.getContent());
            return data.getData();
        } catch(kademlia.exceptions.ContentNotFoundException notFoundE) {
            //TODO: Handle exception - custom P2PGL exception to wrap kademlia exception
            //System.out.println("Content not found");
            return null;
        }
    }

    @Override
    public IKey[] ListUsers() {
        KademliaRoutingTable routingTable = node.getRoutingTable();
        List<Contact> routingContacts =  routingTable.getAllContacts();
        //String[] users = new String[routingContacts.size()];
        IKey[] users = new Key[routingContacts.size()];
        for(int i = 0; i < routingContacts.size(); i++) {
            users[i] = new Key(routingContacts.get(i).getNode().getNodeId());
        }
        return users;
    }

    @Override
    public IKey GetId() {
        return new Key(node.getNode().getNodeId());
    }

    public static String PadKey(String key) {
        if(key.length() > 20)
            throw new IllegalArgumentException("Key must be < 20 characters long");
        if(key.length() < 20)
            return String.format("%-20s", key).replace(' ', '0');
        else
            return key;
    }
}
