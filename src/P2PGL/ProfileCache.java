package P2PGL;

import java.util.*;

/**
 * Maintains a cache of IProfiles with timestamps for when they were added.
 */
public class ProfileCache implements IProfileCache {

    /**
     * Stores a list of IProfiles the time they are added.
     */
    private Map<Long, IProfile> profilesAtTime;

    public ProfileCache() {
        profilesAtTime = new HashMap<>();
        profileNames = new ArrayList<>();
    }

    /**
     * Maintains a list of names for each profile stored to reduce processing
     * each time GetNames() or Contains() is called.
     */
    private List<String> profileNames;

    public ProfileCache(IProfile[] profile) {
        this();
        Add(profile);
        AddNames(profile);
    }

    /**
     * Add a single IProfile to the cache.
     *
     * @param profile Profile to add.
     */
    public void Add(IProfile profile) {
        profilesAtTime.put(GetTime(), profile);
        profileNames.add(profile.GetName());
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
            AddNames(profiles);
        }
    }

    private void AddNames(IProfile[] profiles) {
        for(IProfile prof: profiles) {
            profileNames.add(prof.GetName());
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
     * Get IProfile from cache by name.
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

    /**
     * @return String   List of names for each IProfile
     */
    public List<String> GetNames() {
        return profileNames;
    }

    public boolean Contains(String name) {
        return profileNames.contains(name);
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
                profileNames.remove(iterStep);
            }
            iterStep++;
        }
    }

    /**
     * Clear the cache.
     */
    public void Clear() {
        profilesAtTime.clear();
        profileNames.clear();
    }

    /**
     * Get the current time.
     *
     * @return Current time in milliseconds.
     */
    private Long GetTime() {
        return System.currentTimeMillis();
    }

}
