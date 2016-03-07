package UnitTests;

import P2PGL.Connection;
import P2PGL.Profile;
import kademlia.JKademliaNode;
import kademlia.node.KademliaId;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadFactory;

import static org.junit.Assert.*;

/**
 * Created by t_j_w on 03/03/2016.
 */
public class ConnectionTest {
    JKademliaNode server;
    Connection connection;


    @org.junit.Test
    public void testConnect() throws Exception {
        //Create bootstrap node.
        try {
            Connect(4440, 4441);
        } catch(Exception e) {
            fail();
        }
    }

    @Test
    public void testIncrementKey() throws Exception {
        KademliaId kadId = new KademliaId("hello000000000000000");
        String idString = kadId.toString(); // 68656C6C6F303030303030303030303030303030
        KademliaId incrKadId = Connection.IncrementKey(kadId);
        String incrIdString = incrKadId.toString();
        assertEquals(incrIdString, "68656C6C6F303030303030303030303030303031");
    }

    private void CreateConnection(int port) {
        Profile profile = new Profile(java.net.InetAddress.getLoopbackAddress(), port, "key");
        connection = new Connection(profile);
    }

    private void Connect(int clientPort, int serverPort) throws Exception {
        try {
            Thread.sleep(200);
            server = new JKademliaNode("bootstrap", new KademliaId("bootstrapserver00000"), serverPort);
            CreateConnection(clientPort);
            connection.Connect("bootstrapserver00000", java.net.InetAddress.getLoopbackAddress(), serverPort);
            System.out.println("Connected!");
        } catch(Exception e) {
            fail();
        }
    }

    @org.junit.Test
    public void testGet() throws Exception {
        try {
            Connect(4454, 4455);
            connection.Store("newKey00000000000000", "hello");
            String data = connection.Get("newKey00000000000000");
            assertEquals(data, "hello");
        } catch(Exception e) {
            fail();
        }
    }

    @org.junit.Test
    public void testDisconnect() throws Exception {
        try {
            Connect(4442, 4443);
            connection.Disconnect();
        } catch(Exception e) {
            fail();
        }
    }

    @org.junit.Test
    public void testStore() throws Exception {
        try {
            Connect(4444, 4445);
            connection.Store("newKey00000000000000", "hello");
        } catch(Exception e) {
            fail();
        }
    }

    @Test
    public void testStoreProfile() throws Exception {
        try {
            Connect(4450, 4451);
            connection.StoreProfile();
        } catch (Exception e) {
            fail();
        }
    }


    @Test
    public void testListUsers() throws Exception {
        Connect(4446, 4447);
        String[] users = connection.ListUsers();
        List<String> userList = Arrays.asList(users);
        String clientId = connection.GetId().toString();
        String serverId = server.getNode().getNodeId().toString();

        assertTrue(userList.size() == 2);
        assertTrue(userList.contains(clientId));
        assertTrue(userList.contains(serverId));
    }

    @org.junit.Test
    public void testPadKey() throws Exception {
        String key = "hello";
        String paddedKey = connection.PadKey(key);
        assertEquals(key + "000000000000000", paddedKey);
    }

    @Test
    public void testGetId() throws Exception {
        Connect(4448, 4449);
        String id = connection.GetId().toString();
        assertTrue(id != null);
    }

    @Test
    public void testGetProfile() throws Exception {
        try {
            Connect(4452, 4453);
            KademliaId profKey = connection.StoreProfile();
            Thread.sleep(200);
            Profile prof = (Profile) connection.GetProfile(connection.GetId());
            Profile localProfile = (Profile) connection.GetLocalProfile();
            assertTrue(prof.GetKey().toString().equals(localProfile.GetKey().toString()));
        } catch (Exception e) {
            fail();
        }
    }

    @org.junit.Test
    public void testSend() throws Exception {

    }

    @org.junit.Test
    public void testReadMessageStream() throws Exception {

    }
}