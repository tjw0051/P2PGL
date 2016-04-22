package P2PGL.Profile;

import P2PGL.IP2PGLFactory;
import P2PGL.Util.IKey;

import java.util.*;

/**
 * Maintains a cache of IProfiles with timestamps for when they were added.
 */
public class ProfileCache implements IProfileCache {

    /**
     * Stores a list of IProfiles the time they are added.
     */
    private List<IProfile> profilesAtTime;

    /**
     * Create an empty profile cache.
     */
    public ProfileCache() {
        profilesAtTime = new ArrayList<>();
    }

    /** Create a profile cache initialized with a list of profiles.
     * @param profile   List of profiles.
     */
    public ProfileCache(IProfile[] profile) {
        this();
        Add(profile);
    }

    /**
     * Add a single IProfile to the cache.
     *
     * @param profile Profile to add.
     */
    public void Add(IProfile profile) {
        profilesAtTime.add(profile);
    }

    /**
     * Add an array of IProfiles to the cache.
     *
     * @param profiles IProfiles to add to cache.
     */
    public void Add(IProfile[] profiles) {
        for(IProfile profile: profiles) {
            profilesAtTime.add(profile);
        }
    }

    /**
     * Returns all IProfiles in the cache.
     *
     * @return Collection of IProfiles.
     */
    public List<IProfile> Get() {
        return profilesAtTime;
    }

    /**
     * GetHybridConnection IProfile from cache by name.
     *
     * @param name  Name of profile.
     *
     * @return  First IProfile in cache with matching name.
     */
    public synchronized IProfile Get(String name) {
        for(IProfile prof : profilesAtTime) {
            if(prof.GetName().equals(name)) {
                return prof;
            }
        }
        return null;
    }

    public IProfile Get(IKey key) {
        for(IProfile prof : profilesAtTime) {
            if(prof.GetKey().equals(key)) {
                return prof;
            }
        }
        return null;
    }

    /** Check if cache contains a profile with matching IKey.
     * @param key IKey to search for
     * @return  Returns true if a matching profile is found.
     */
    public boolean Contains(IKey key) {
        for(IProfile prof : profilesAtTime) {
            if(prof.GetKey().equals(key)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Removes IProfile in the cache with matching key.
     *
     * @param key  Key of profile to remove.
     */
    public void Remove(IKey key) {
        Iterator iter = profilesAtTime.iterator();
        while (iter.hasNext()) {
            if(((IProfile)iter.next()).GetKey().equals(key))
                iter.remove();
        }
    }

    /**
     * Clear the cache.
     */
    public void Clear() {
        profilesAtTime.clear();
    }

}
