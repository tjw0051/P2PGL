package UnitTests;

import P2PGL.Connection.IHybridConnection;
import P2PGL.ConnectionFactory;
import P2PGL.Profile.IProfile;
import P2PGL.Profile.IProfileCache;
import P2PGL.Profile.Profile;
import P2PGL.Util.IKey;
import P2PGL.Util.ISerializedData;
import P2PGL.Util.Key;
import kademlia.node.KademliaId;
import org.junit.Test;

import java.net.InetAddress;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by t_j_w on 01/04/2016.
 */
public class ConnectionFactoryTest {

    @Test
    public void testGetHybridConnection() {
        IProfile prof = new Profile(InetAddress.getLoopbackAddress(), 2500, 2501, "channel", "name", new Key());
        IHybridConnection conn = ConnectionFactory.GetHybridConnection(prof);
        assertTrue("HybridConnection was not created", conn != null);
    }

    @Test
    public void testGetSerializedData() throws Exception {
        ISerializedData data = ConnectionFactory.GetSerializedData("data", "typename");
        assertTrue("SerializedData was not created correctly", data.GetData().equals("data") &&
        data.GetType().equals("typename"));
    }

    @Test
    public void testGetProfileCache() throws Exception {
        IProfileCache cache = ConnectionFactory.GetProfileCache();
        assertTrue("Profile cache not created", cache != null);
    }

    @Test
    public void testGetKey() throws Exception {
        IKey key = ConnectionFactory.GetKey();
        assertTrue("Key not created", key != null);
    }

    @Test
    public void testGetKeyParams() throws Exception {
        KademliaId id = new KademliaId();
        IKey key = ConnectionFactory.GetKey(id.getBytes());
        assertTrue("Key not initialized correctly", Arrays.equals(key.ToBytes(), id.getBytes()));
    }

    @Test
    public void testGetKeys() throws Exception {
        IKey[] keys = ConnectionFactory.GetKeys(10);
        assertTrue("keys not created", keys != null);
    }
}