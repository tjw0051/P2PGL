package UnitTests;

import P2PGL.*;
import P2PGL.DHT.KademliaFacade;
import P2PGL.Profile.IProfile;
import P2PGL.Profile.Profile;
import kademlia.JKademliaNode;
import kademlia.node.KademliaId;

import org.junit.Test;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by t_j_w on 03/03/2016.
 */
public class ConnectionTest {
    JKademliaNode server;
    Connection connection;
    Profile _profile;

    @org.junit.Test
    public void testConnect() throws Exception {
        //Create bootstrap node.
        try {
            Connect(4440, 4441);
        } catch(Exception e) {
            fail();
        }
    }
    /*
    @Test
    public void testIncrementKey() throws Exception {
        KademliaId kadId = new KademliaId("hello000000000000000");
        String idString = kadId.toString(); // 68656C6C6F303030303030303030303030303030
        //KademliaId incrKadId = Connection.IncrementKey(kadId);

        String incrIdString = incrKadId.toString();
        assertEquals(incrIdString, "68656C6C6F303030303030303030303030303031");
    }
    */
    private void CreateConnection(int port) {
        _profile = new Profile(java.net.InetAddress.getLoopbackAddress(), port, "key");
        connection = new Connection(_profile, new KademliaFacade());
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

    @Test
    public void testUDPChannel() throws Exception {
        Profile profile1 = new Profile(InetAddress.getLoopbackAddress(), 4001, 4002, "channel", "profile1", new Key());
        Connection connection1 = new Connection(profile1, new KademliaFacade(profile1));
        connection1.Connect();

        Profile profile2 = new Profile(InetAddress.getLoopbackAddress(), 4003, 4004, "channel", "profile2", new Key());
        Connection connection2 = new Connection(profile2, new KademliaFacade(profile2));
        connection2.Connect("profile1", InetAddress.getLoopbackAddress(), 4001);

        connection1.StartUDPChannel("channel");
        connection2.StartUDPChannel("channel");
        System.out.println("connection1 peers:");

        connection1.Broadcast("Hello2");
        connection2.Broadcast("Hello1");
        Thread.sleep(100);
        String message1 = (String) connection1.GetUDPChannel().ReadNext();
        String message2 = (String) connection2.GetUDPChannel().ReadNext();
        System.out.println("messg1: " + message1 + " | Messg2: " + message2);
    }

    @org.junit.Test
    public void testGet() throws Exception {
        try {
            Connect(4454, 4455);
            //connection.Store("newKey00000000000000", "hello");
            connection.Store(new Key("newKey00000000000000"), "hello");
            String data = connection.Get(new Key("newKey00000000000000"), String.class);
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
            connection.Store(new Key("newKey00000000000000"), "hello");
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
        IKey[] users = connection.ListUsers();
        List<IKey> userList = Arrays.asList(users);
        IKey clientId = connection.GetId();
        Key serverId = new Key(server.getNode().getNodeId());

        assertTrue(userList.size() == 2);
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
            IKey profKey = connection.StoreProfile();
            Thread.sleep(200);
            //IProfile localProfile = connection.GetLocalProfile();
            IProfile prof = connection.GetProfile(_profile.GetKey());
            //IProfile localProfile = connection.GetLocalProfile();
            assertTrue(prof.GetKey().toString().equals(_profile.GetKey().toString()));
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