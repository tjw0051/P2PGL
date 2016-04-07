import P2PGL.Connection.IHybridConnection;
import P2PGL.P2PGLFactory;
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
public class P2PGLFactoryTest {

    @Test
    public void testGetHybridConnection() {
        P2PGLFactory p2PGLFactory = new P2PGLFactory();
        IProfile prof = new Profile(InetAddress.getLoopbackAddress(), 2500, 2501, "channel", "name", new Key());
        IHybridConnection conn = p2PGLFactory.GetHybridConnection(prof);
        assertTrue("HybridConnection was not created", conn != null);
    }

    @Test
    public void testGetSerializedData() throws Exception {
        P2PGLFactory p2PGLFactory = new P2PGLFactory();
        ISerializedData data = p2PGLFactory.GetSerializedData("data", "typename");
        assertTrue("SerializedData was not created correctly", data.GetData().equals("data") &&
        data.GetType().equals("typename"));
    }

    @Test
    public void testGetProfileCache() throws Exception {
        P2PGLFactory p2PGLFactory = new P2PGLFactory();
        IProfileCache cache = p2PGLFactory.GetProfileCache();
        assertTrue("Profile cache not created", cache != null);
    }

    @Test
    public void testGetKey() throws Exception {
        P2PGLFactory p2PGLFactory = new P2PGLFactory();
        IKey key = p2PGLFactory.GetKey();
        assertTrue("Key not created", key != null);
    }

    @Test
    public void testGetKeyParams() throws Exception {
        P2PGLFactory p2PGLFactory = new P2PGLFactory();
        KademliaId id = new KademliaId();
        IKey key = p2PGLFactory.GetKey(id.getBytes());
        assertTrue("Key not initialized correctly", Arrays.equals(key.ToBytes(), id.getBytes()));
    }

    @Test
    public void testGetKeys() throws Exception {
        P2PGLFactory p2PGLFactory = new P2PGLFactory();
        IKey[] keys = p2PGLFactory.GetKeys(10);
        assertTrue("keys not created", keys != null);
    }
}