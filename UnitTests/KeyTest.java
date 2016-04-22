import P2PGL.Util.IKey;
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
        assertArrayEquals(kadId.getBytes(), key.ToBytes());
        //assertTrue(new KademliaId(key.ToBytes()) != null);
    }

    @Test
    public void testToString() throws Exception {
        KademliaId kadId = new KademliaId();
        Key key = new Key(kadId.getBytes());
        assertTrue(new KademliaId(key.ToBytes()).toString().equals(kadId.toString()));
    }

    @org.junit.Test
    public void testPadKeyValid() throws Exception {
        String key = "hello";
        Key aKey = new Key();
        String paddedKey = aKey.Format(key);
        assertEquals(key + "000000000000000", paddedKey);
    }

    @org.junit.Test
    public void testPadKeyInvalid() throws Exception {
        String key = "hello0000000000000001";
        Key aKey = new Key();
        String paddedKey = aKey.Format(key);
        assertEquals("hello000000000000000", paddedKey);
    }

    @org.junit.Test
    public void testPadKeyErroneous() throws Exception {
        String key = "";
        Key aKey = new Key();
        try {
            String paddedKey = aKey.Format(key);
        } catch (IllegalArgumentException iae) {
            return;
        }
        fail("Illegal Argument Exception not thrown");
    }

    @org.junit.Test
    public void testEqualityValid() throws Exception {
        IKey key1 = new Key();
        byte[] bytes = key1.ToBytes();
        IKey key2 = new Key(bytes);
        assertEquals(key1, key2);
    }

    @org.junit.Test
    public void testEqualityInvalid() throws Exception {
        IKey key1 = new Key();
        IKey key2 = new Key();
        assertNotEquals(key1, key2);
    }
}