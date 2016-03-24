package P2PGL;

import kademlia.JKademliaNode;
import kademlia.dht.GetParameter;
import kademlia.dht.KademliaStorageEntry;
import kademlia.node.KademliaId;
import kademlia.node.Node;
import kademlia.routing.Contact;
import kademlia.routing.KademliaRoutingTable;

import java.net.InetAddress;

/**
 * Created by t_j_w on 24/03/2016.
 */
public class KademliaFacade implements IDHTFacade{

    private JKademliaNode node;

    KademliaFacade() {

    }

    @Override
    public void Connect(String serverName, InetAddress serverAddress, int serverPort) {
        node = new JKademliaNode(profile.GetName(), profile.GetKey().getKademliaId(), profile.GetPort());
        Node bootstrapNode = new Node(new KademliaId(Key.PadKey(serverName)), destIPAddress, destPort);
        node.bootstrap(bootstrapNode);
    }

    @Override
    public void Disconnect() {

    }

    @Override
    public void Store() {

    }

    @Override
    public void Get() {

    }
}
