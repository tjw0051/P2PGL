package UnitTests;

import P2PGL.Connection;
import P2PGL.Profile;
import kademlia.JKademliaNode;
import kademlia.node.KademliaId;

import org.junit.Test;

/**
 * Created by t_j_w on 03/03/2016.
 */
public class ConnectionTest {

    @org.junit.Test
    public void testConnect() throws Exception {
        //Create bootstrap node.
        try {
            JKademliaNode b = new JKademliaNode("bootstrap", new KademliaId("bootstrapserver00000"), 4441);
            Profile profile = new Profile(java.net.InetAddress.getLoopbackAddress(), "key");
            Connection connection = new Connection(profile);
            connection.Connect("bootstrapserver00000", java.net.InetAddress.getLoopbackAddress(), 4441);
        } catch(Exception e) {

        }
    }

    @org.junit.Test
    public void testDisconnect() throws Exception {

    }

    @org.junit.Test
    public void testStore() throws Exception {

    }

    @org.junit.Test
    public void testGet() throws Exception {

    }

    @org.junit.Test
    public void testSend() throws Exception {

    }

    @org.junit.Test
    public void testSend1() throws Exception {

    }

    @org.junit.Test
    public void testReadMessageStream() throws Exception {

    }
}