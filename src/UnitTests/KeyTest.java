package UnitTests;

import P2PGL.Util.Key;
import kademlia.node.KademliaId;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by t_j_w on 17/03/2016.
 */
public class KeyTest {

    @Test
    public void testGetKademliaId() throws Exception {
        KademliaId kadId = new KademliaId();
        Key key = new Key(kadId.getBytes());
        assertTrue(new KademliaId(key.ToBytes()) != null);
    }

    @Test
    public void testToString() throws Exception {
        KademliaId kadId = new KademliaId();
        Key key = new Key(kadId.getBytes());
        assertTrue(new KademliaId(key.ToBytes()).toString().equals(kadId.toString()));
    }

    @org.junit.Test
    public void testPadKey() throws Exception {
        String key = "hello";
        Key aKey = new Key();
        String paddedKey = aKey.Format(key);
        assertEquals(key + "000000000000000", paddedKey);
    }
}