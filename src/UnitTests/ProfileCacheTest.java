package UnitTests;

import P2PGL.IKey;
import P2PGL.Key;
import P2PGL.Profile.IProfile;
import P2PGL.Profile.Profile;
import P2PGL.Profile.ProfileCache;
import org.junit.Test;

import java.net.InetAddress;

import static org.junit.Assert.*;

/**
 * Created by t_j_w on 24/03/2016.
 */
public class ProfileCacheTest {

    @Test
    public void testAdd() {
        ProfileCache profileCache = AddEntry();
    }

    private ProfileCache AddEntry() {
        ProfileCache profileCache = new ProfileCache();
        profileCache.Add(new Profile(InetAddress.getLoopbackAddress(), 4000, "hello"));
        return profileCache;
    }

    @Test
    public void Get() {
        ProfileCache profileCache = AddEntry();
        IProfile prof = profileCache.Get("hello");
        assertTrue(prof != null);
    }

    @Test
    public void testContains() throws Exception {
        ProfileCache cache = new ProfileCache();
        IKey key1 = new Key("test");
        IProfile prof = new Profile(InetAddress.getLoopbackAddress(), 100, "name", key1);
        IKey key2 = new Key("test");
        cache.Add(prof);

        assertTrue(cache.Contains(key2));
    }
}