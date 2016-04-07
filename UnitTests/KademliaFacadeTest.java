import P2PGL.DHT.KademliaFacade;
import P2PGL.Util.IKey;
import P2PGL.Util.Key;
import P2PGL.Profile.Profile;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;

import static org.junit.Assert.*;

/**
 * Created by t_j_w on 25/03/2016.
 */
public class KademliaFacadeTest extends KademliaFacade{
    KademliaFacade serverKad, clientKad;
    Key clientKey, serverKey;

    /** Test connecting a client and server node.
     * @throws Exception    Error connecting nodes.
     */
    @Test
    public void testConnect() throws Exception {
        Connect(4300, 4301);
    }

    private void Connect(int serverPort, int clientPort) {
        serverKey = new Key("server");
        //System.out.println("serverKey: " + serverKey);
        serverKad = new KademliaFacade(
                new Profile(InetAddress.getLoopbackAddress(), serverPort, "server", serverKey));
        try {
            serverKad.Connect();
        } catch (IOException ioe) {
            fail("Could not connect server.");
        }
        clientKey = new Key("client");
        //System.out.println("clientKey: " + clientKey);
        clientKad = new KademliaFacade(
                new Profile(InetAddress.getLoopbackAddress(), clientPort, "client", clientKey));
        try {
            clientKad.Connect("server", InetAddress.getLoopbackAddress(), serverPort);
        } catch (IOException ioe) {
            fail("Cannot connect client to server.");
        }
        assertTrue(clientKad.isConnected());
    }

    /** Test disconnecting client and server from dht.
     * @throws Exception    could not disconnect.
     */
    @Test
    public void testDisconnect() throws Exception {
        Connect(4302, 4303);
        try {
            clientKad.Disconnect();
            serverKad.Disconnect();
        } catch (IOException ioe) {
            fail("Error disconnecting");
        }
    }

    /** Test storing data on dht.
     * @throws Exception    data could not be stored.
     */
    @Test
    public void testStore() throws Exception {
        Connect(4304, 4305);
        Store();
    }

    /**
     * Store data on dht.
     */
    private void Store() {
        try {
            clientKad.Store(new Key("testdata"), "hello", String.class.getTypeName());
        } catch (IOException ioe) {
            fail("IO Error storing data");
        }
    }

    /** Test getting data from DHT.
     * @throws Exception    Error getting data
     */
    @Test
    public void testGet() throws Exception {
        Connect(4306, 4307);
        Store();
        try {
            String data = clientKad.Get(new Key("testdata"), String.class.getTypeName()).GetData();
            assertTrue(data.equals("hello"));
        } catch (IOException ioe) {
            fail("Error getting data");
        }
    }

    /** Test listing users on dht.
     * @throws Exception    IO Exception
     */
    @Test
    public void testListUsers() throws Exception {
        Connect(4308, 4309);
        Key clientKey2 = new Key("client");
        Key serverKey2 = new Key("server");
        IKey[] users = clientKad.ListUsers();
        boolean clientFound = false, serverFound = false;
        for(int i = 0; i < users.length; i++) {
            if(users[i].Equals(clientKey2))
                clientFound = true;
            if(users[i].Equals(serverKey2))
                serverFound = true;
        }
        assertTrue("Client was not found", clientFound);
        assertTrue("Server was not found", serverFound);
    }

    /** Test that the nodeId is the correct ID that was set when it was created.
     * @throws Exception    Error connecting.
     */
    @Test
    public void testGetId() throws Exception {
        Connect(4310, 4311);
        IKey nodeId = clientKad.GetId();
        assertTrue(nodeId.toString().equals(clientKey.toString()));
    }

    @Test
    public void testPadKeyShort() throws Exception {
        String testLong = PadKey("hello");
        assertTrue(testLong.length() <= 20);
    }

    @Test
    public void testPadKeyLong() throws Exception {
        String testShort = PadKey("1234567890123456789012345");
        assertTrue(testShort.length() <= 20);
    }
}