package P2PGL.Profile;

import P2PGL.Util.IKey;

import java.util.*;

/**
 * Maintains a cache of IProfiles with timestamps for when they were added.
 */
public class ProfileCache implements IProfileCache {

    /**
     * Stores a list of IProfiles the time they are added.
     */
    private Map<Long, IProfile> profilesAtTime;

    /**
     * Create an empty profile cache.
     */
    public ProfileCache() {
        profilesAtTime = new HashMap<>();
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
        profilesAtTime.put(GetTime(), profile);
    }

    /**
     * Add an array of IProfiles to the cache.
     *
     * @param profiles IProfiles to add to cache.
     */
    public void Add(IProfile[] profiles) {
        for(IProfile profile: profiles) {
            Long time = GetTime();
            profilesAtTime.put(time, profile);
        }
    }

    /**
     * Returns all IProfiles in the cache.
     *
     * @return Collection of IProfiles.
     */
    public Collection<IProfile> Get() {
        return profilesAtTime.values();
    }

    /**
     * GetHybridConnection IProfile from cache by name.
     *
     * @param name  Name of profile.
     *
     * @return  First IProfile in cache with matching name.
     */
    public IProfile Get(String name) {
        for(Map.Entry<Long, IProfile> entry : profilesAtTime.entrySet()) {
            if(entry.getValue().GetName().equals(name)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /** Check if cache contains a profile with matching IKey.
     * @param key IKey to search for
     * @return  Returns true if a matching profile is found.
     */
    public boolean Contains(IKey key) {
        Iterator iter = profilesAtTime.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            //if(((IProfile)entry.getValue()).GetKey().ToBytes() == key.ToBytes()) {
            if(((IProfile)entry.getValue()).GetKey().Equals(key))
                return true;
        }
        return false;
    }


    /**
     * Removes the first IProfile in the cache with matching name.
     *
     * @param name  Name of profile to remove.
     */
    public void Remove(String name) {
        Iterator iter = profilesAtTime.entrySet().iterator();
        int iterStep = 0;
        while(iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            if(((IProfile) entry.getValue()).GetName().equals(name)) {
                iter.remove();
            }
            iterStep++;
        }
    }

    /**
     * Clear the cache.
     */
    public void Clear() {
        profilesAtTime.clear();
    }

    /**
     * GetHybridConnection the current time.
     *
     * @return Current time in milliseconds.
     */
    private Long GetTime() {
        return System.currentTimeMillis();
    }

}
