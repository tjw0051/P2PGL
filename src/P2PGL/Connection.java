package P2PGL;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import kademlia.JKademliaNode;
import kademlia.dht.GetParameter;
import kademlia.dht.KademliaStorageEntry;
import kademlia.node.KademliaId;
import kademlia.node.Node;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;



/**
 * Created by Tom.
 */
public class Connection {
    private IProfile profile;
    private JKademliaNode node;
    private ListenThread listenThread;

    public Connection(IProfile profile) {
        this.profile = profile;
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

    public void Disconnect() throws IOException {
        try {
            node.shutdown(false);
        } catch(IOException ioe) {
            throw ioe;
        }
    }

    public void Store(String dataKey, String serializedData) {
    }

    public String Get(String id) throws IOException, kademlia.exceptions.ContentNotFoundException {
        GetParameter getParameter = new GetParameter(new KademliaId(id), "Data");
        try {
            KademliaStorageEntry entry = node.get(getParameter);
            Data data = (new Data()).fromSerializedForm(entry.getContent());
            return data.getData();
        } catch(IOException ioe) {
            throw ioe;
        } catch(kademlia.exceptions.ContentNotFoundException notFoundE) {
            throw notFoundE;
        }
    }

    public void Send(String destKey, String stringData) {
        Data data = new Data(node.getNode().getNodeId().toString(), new KademliaId(destKey), stringData);
    }
    public void Send(Data data) throws IOException{
        try {
            node.put(data);
        } catch(IOException ioe) {
            throw ioe;
        }
    }

    public String ReadMessageStream() {
        return "";
    }

    //public List<String> ListUsers() { }

    public class ListenThread extends Thread {

    }

    public static void main(String[] args) {

    }

}
