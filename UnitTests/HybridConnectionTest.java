import P2PGL.Connection.IHybridConnection;
import P2PGL.P2PGL;
import P2PGL.Profile.IProfile;
import P2PGL.Profile.Profile;
import P2PGL.Util.IKey;
import P2PGL.Util.Key;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by t_j_w on 01/04/2016.
 */
public class HybridConnectionTest {

    IProfile clientProfile, serverProfile;
    IHybridConnection clientConnection, serverConnection;

    @Test
    public void testConnectValid() {
        if(Connect(5000, 5002, true) != 0)
            fail("Error connecting");
    }

    @Test
    public void testConnectInvalid() {
        if(Connect(5000, 5000, true) == 0)
            fail("Error not thrown with invalid ports");
    }

    @Test
    public void testConnectErroneous() {
        if(Connect(70000, 70002, true) == 0)
            fail("Error not thrown with ports out of range");
    }

    private int Connect(int serverPort, int clientPort, boolean conn) {
        serverProfile = new Profile(InetAddress.getLoopbackAddress(),
                serverPort, serverPort+1, "channel0", "server", new Key());
        serverConnection = P2PGL.GetInstance().GetConnection(serverProfile);



        clientProfile = new Profile(InetAddress.getLoopbackAddress(),
                clientPort, clientPort+1, "channel0", "client", new Key());
        clientConnection = P2PGL.GetInstance().GetConnection(clientProfile);
        if(conn) {
            try {
                serverConnection.Connect();
            } catch (IOException ioe) {
                return -1;
            } catch (IllegalArgumentException iae) {
                return -2;
            }
            try {
                clientConnection.Connect("server", InetAddress.getLoopbackAddress(), serverPort);
            } catch (IOException ioe) {
                return -1;
            } catch (IllegalArgumentException iae) {
                return -2;
            }
        }
        return 0;
    }

    @Test
    public void testIsConnected() throws Exception {
        Connect(5004, 5006, true);
        assertTrue("Client is not connected", clientConnection.isConnected());
        assertTrue("Server is not connected", serverConnection.isConnected());
    }

    @Test
    public void testDisconnectValid() {
        if(Connect(5008, 5010, true) != 0)
            fail("Error connecting");

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
    public void testDisconnectErroneous() {
        if(Connect(5008, 5010, false) != 0)
            fail("Error connecting");

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
    public void testJoinLocalChannelValid() {
        if(Connect(5012, 5014, true) != 0)
            fail("Error connecting");
        if(joinLocal() != 0)
            fail("Error connecting client to local channel");
    }

    @Test
    public void testJoinLocalChannelInvalid() {
        if(Connect(2000, 2000, true) == 0)
            fail("Error connecting");
        if(joinLocal() != -2)
            fail("Unexpected error");
    }

    private int joinLocal() {
        try {
            clientConnection.JoinLocalChannel("channel0");
        } catch (IOException e) {
            return -1;
        } catch (NullPointerException npe) {
            return -2;
        }
        try {
            serverConnection.JoinLocalChannel("channel0");
        } catch (IOException e) {
            return -1;
        } catch (NullPointerException npe) {
            return -2;
        }
        return 0;
    }

    @Test
    public void testBroadcast() throws  InterruptedException{
        Connect(5016, 5018, true);
        joinLocal();

        String message = null;
        long time = System.currentTimeMillis();
        while(System.currentTimeMillis() - time < 2000 && message == null ) {
            try {
                clientConnection.Broadcast("Hello", String.class);
            } catch (IOException e) {
                fail("Error broadcasting message");
            }

            try {
            message = serverConnection.GetLocalChannel().ReadNext();
            } catch (ClassNotFoundException e) {
                fail("Class not found");
            }
        }
        assertTrue(message.equals("Hello"));


    }

    @Test
    public void testStore() throws Exception {
        Connect(5020, 5022, true);
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
        Connect(5024, 5026, true);
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
        Connect(5028, 5030, true);
        IKey[] keys = clientConnection.ListUsers();
        assertTrue("Length is: " + keys.length, keys.length == 3);
        boolean clientFound = false, serverFound = false;
        //System.out.println("client key: " + clientConnection.GetKey().toString());
        for(IKey key : keys) {
            //System.out.println("Key: " + key.toString());
            if(key.equals(clientConnection.GetKey()))
                clientFound = true;
            if(key.equals(serverConnection.GetKey()))
                serverFound = true;
        }
        assertTrue("Client not found", clientFound);
        assertTrue("Server not found", serverFound);
    }

    @Test
    public void testGetKey() throws Exception {
        Connect(5032, 5034, true);
        assertTrue(clientConnection.GetKey() != null);
    }

    @Test
    public void testGetLocalProfileValid() throws Exception {
        Connect(5036, 5038, true);
        assertTrue(clientConnection.GetLocalProfile().GetKey().equals(clientProfile.GetKey()));
    }

    @Test
    public void testGetLocalProfileInvalid() throws Exception {
        Connect(5062, 5064, true);
        assertFalse(clientConnection.GetLocalProfile().GetKey().equals(new Key()));
    }

    @Test
    public void testSetLocalProfile() throws Exception {
        Connect(5040, 5042, true);
        clientConnection.SetLocalProfile(serverProfile);
        assertTrue(clientConnection.GetLocalProfile().GetKey().equals(serverProfile.GetKey()));
    }

    @Test
    public void testGetProfileValid() throws Exception {
        Connect(5044, 5046, true);
        IProfile prof = null;
        IKey serverKey = serverProfile.GetKey();
        long time = System.currentTimeMillis();
        while(System.currentTimeMillis() - time < 2000 && prof == null) {
            prof = clientConnection.GetProfile(serverKey);
        }
        System.out.println("Time taken: " + (System.currentTimeMillis() - time) + "ms");
        assertTrue("No profile found", prof != null);
        assertTrue(prof.GetKey().equals(serverProfile.GetKey()));
    }

    @Test
    public void testGetProfileInvalid() throws Exception {
        Connect(5058, 5060, true);
        IProfile prof = null;
        IKey serverKey = serverProfile.GetKey();
        long time = System.currentTimeMillis();
        while(System.currentTimeMillis() - time < 2000 && prof == null) {
            prof = clientConnection.GetProfile(serverKey);
        }
        System.out.println("Time taken: " + (System.currentTimeMillis() - time) + "ms");
        assertTrue("No profile found", prof != null);
        assertFalse(prof.GetKey().equals(new Key()));
    }

    @Test
    public void testGetProfiles() throws Exception {
        Connect(5048, 5050, true);
        IKey[] keys = clientConnection.ListUsers();
        List<IProfile> profiles = null;
        long time = System.currentTimeMillis();
        while(System.currentTimeMillis() - time < 2000 && profiles == null) {
            profiles = clientConnection.GetProfiles(keys);
        }
        System.out.println("Time taken: " + (System.currentTimeMillis() - time) + "ms");

        boolean serverFound = false;
        for(IProfile profs : profiles) {
            if(profs.GetKey().equals(serverProfile.GetKey()))
                serverFound = true;
        }
        assertTrue("server profile not retrieved", serverFound);
    }
    @Test
    public void testGetLocalChannel() throws Exception {
        Connect(5052, 5054, true);
        joinLocal();
        assertTrue(clientConnection.GetLocalChannel() != null);
    }
}