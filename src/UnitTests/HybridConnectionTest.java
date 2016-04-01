package UnitTests;

import P2PGL.Connection.HybridConnection;
import P2PGL.Connection.IHybridConnection;
import P2PGL.ConnectionFactory;
import P2PGL.Profile.IProfile;
import P2PGL.Profile.Profile;
import P2PGL.Util.IKey;
import P2PGL.Util.Key;
import org.junit.Test;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by t_j_w on 01/04/2016.
 */
public class HybridConnectionTest {

    IProfile clientProfile, serverProfile;
    IHybridConnection clientConnection, serverConnection;

    @Test
    public void testConnect() {
        Connect(5000, 5002);
    }

    private void Connect(int serverPort, int clientPort) {
        serverProfile = new Profile(InetAddress.getLoopbackAddress(),
                serverPort, serverPort+1, "channel0", "server", new Key());
        serverConnection = ConnectionFactory.GetHybridConnection(serverProfile);

        clientProfile = new Profile(InetAddress.getLoopbackAddress(),
                clientPort, clientPort+1, "channel0", "client", new Key());
        clientConnection = ConnectionFactory.GetHybridConnection(clientProfile);
        try {
            serverConnection.Connect();
        } catch (IOException ioe) {
            fail("Error connecting server");
        }
        try {
            clientConnection.Connect("server", InetAddress.getLoopbackAddress(), serverPort);
        } catch (IOException ioe) {
            fail("Error connecting client");
        }
    }

    @Test
    public void testIsConnected() throws Exception {
        Connect(5004, 5006);
        assertTrue("Client is not connected", clientConnection.isConnected());
        assertTrue("Server is not connected", serverConnection.isConnected());
    }

    @Test
    public void testDisconnect() {
        Connect(5008, 5010);
        try {
            clientConnection.Disconnect();
        } catch (IOException ioe) {
            fail("Error disconnecting client");
        }

        try {
            serverConnection.Disconnect();
        } catch (IOException e) {
            fail("Error disconnecting server");
        }
    }

    @Test
    public void testJoinLocalChannel() {
        Connect(5012, 5014);
        joinLocal();
    }

    private void joinLocal() {
        try {
            clientConnection.JoinLocalChannel("channel0");
        } catch (IOException e) {
            fail("Error connecting client to local channel");
        }
        try {
            serverConnection.JoinLocalChannel("channel0");
        } catch (IOException e) {
            fail("Error connecting server to local channel");
        }
    }

    @Test
    public void testBroadcast() throws  InterruptedException{
        Connect(5016, 5018);
        joinLocal();
        try {
            clientConnection.Broadcast("Hello", String.class);
        } catch (IOException e) {
            fail("Error broadcasting message");
        }
        try {
            Thread.sleep(100);
            String message = serverConnection.GetLocalChannel().ReadNext();
            assertTrue(message.equals("Hello"));
        } catch (ClassNotFoundException e) {
            fail("Class not found");
        }

    }

    @Test
    public void testStore() throws Exception {
        Connect(5020, 5022);
        joinLocal();
        store();
    }

    private IKey store() {
        IKey key = new Key("score");

        try {
            clientConnection.Store(key, "20", String.class);
        } catch (IOException e) {
            fail("Error storing data in DHT");
        }
        return key;
    }

    @Test
    public void testGet() throws Exception {
        Connect(5024, 5026);
        joinLocal();
        IKey key = store();

        String message = "";
        try {
            long time = System.currentTimeMillis();
            while(System.currentTimeMillis() - time < 2000 && message.equals("")) {
                message = serverConnection.Get(key, String.class);
            }
            assertTrue(message.equals("20"));
        } catch (IOException e) {
            fail("Error getting data from DHT");
        } catch (ClassNotFoundException e) {
            fail("Cannot deserialize data to String type");
        }
    }

    @Test
    public void testListUsers() throws Exception {
        Connect(5028, 5030);
        IKey[] keys = clientConnection.ListUsers();
        assertTrue("Length is: " + keys.length, keys.length == 3);
        boolean clientFound = false, serverFound = false;
        System.out.println("client key: " + clientConnection.GetKey().toString());
        for(IKey key : keys) {
            System.out.println("Key: " + key.toString());
            if(key.Equals(clientConnection.GetKey()))
                clientFound = true;
            if(key.Equals(serverConnection.GetKey()))
                serverFound = true;
        }
        assertTrue("Client not found", clientFound);
        assertTrue("Server not found", serverFound);
    }

    @Test
    public void testGetKey() throws Exception {
        Connect(5032, 5034);
        assertTrue(clientConnection.GetKey() != null);
    }

    @Test
    public void testGetLocalProfile() throws Exception {
        Connect(5036, 5038);
        assertTrue(clientConnection.GetLocalProfile().GetKey().Equals(clientProfile.GetKey()));
    }

    @Test
    public void testSetLocalProfile() throws Exception {
        Connect(5040, 5042);
        clientConnection.SetLocalProfile(serverProfile);
        assertTrue(clientConnection.GetLocalProfile().GetKey().Equals(serverProfile.GetKey()));
    }

    @Test
    public void testGetProfile() throws Exception {
        Connect(5044, 5046);
        IProfile prof = null;
        IKey serverKey = serverProfile.GetKey();
        long time = System.currentTimeMillis();
        while(System.currentTimeMillis() - time < 2000 && prof == null) {
            prof = clientConnection.GetProfile(serverKey);
        }
        System.out.println("Time taken: " + (System.currentTimeMillis() - time) + "ms");
        assertTrue("No profile found", prof != null);
        assertTrue(prof.GetKey().Equals(serverProfile.GetKey()));
    }

    @Test
    public void testGetProfiles() throws Exception {
        Connect(5048, 5050);
        IKey[] keys = clientConnection.ListUsers();
        List<IProfile> profiles = null;
        long time = System.currentTimeMillis();
        while(System.currentTimeMillis() - time < 2000 && profiles == null) {
            profiles = clientConnection.GetProfiles(keys);
        }
        System.out.println("Time taken: " + (System.currentTimeMillis() - time) + "ms");

        boolean serverFound = false;
        for(IProfile profs : profiles) {
            if(profs.GetKey().Equals(serverProfile.GetKey()))
                serverFound = true;
        }
        assertTrue("server profile not retrieved", serverFound);
    }
    /*
    @Test
    public void testNewContactListener() throws Exception {

    }

    @Test
    public void testAddMessageListener() throws Exception {

    }

    @Test
    public void testRemoveMessageListener() throws Exception {

    }

    @Test
    public void testMessageReceivedListener() throws Exception {

    }
    */
    @Test
    public void testGetLocalChannel() throws Exception {
        Connect(5052, 5054);
        joinLocal();
        assertTrue(clientConnection.GetLocalChannel() != null);
    }
}