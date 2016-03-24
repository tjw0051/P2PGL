package UnitTests;

import P2PGL.Key;
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
        Key key = new Key(kadId);
        assertTrue(key.getKademliaId() != null);
    }

    @Test
    public void testToString() throws Exception {
        KademliaId kadId = new KademliaId();
        Key key = new Key(kadId);
        assertTrue(key.getKademliaId().toString().equals(kadId.toString()));
    }

    @org.junit.Test
    public void testPadKey() throws Exception {
        String key = "hello";
        String paddedKey = Key.PadKey(key);
        assertEquals(key + "000000000000000", paddedKey);
    }
}