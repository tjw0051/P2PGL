package P2PGL.Profile;

import P2PGL.Util.IKey;

import java.util.Collection;

/**
 * Cache of profiles for maintaining a list of contacts.
 */
public interface IProfileCache {
    /** Add profile to list of contacts.
     * @param profile   profile of contact
     */
    void Add(IProfile profile);

    /** Add multiple profiles to list of contacts.
     * @param profiles  profiles of contacts.
     */
    void Add(IProfile[] profiles);

    /** GetHybridConnection first profile of matching name
     * @param name  Name of profile to retrieve.
     * @return  IProfile of contact. Returns null if no profile can be found.
     */
    IProfile Get(String name);

    /** GetHybridConnection all contacts stored in cache.
     * @return  Collection of IProfiles
     */
    Collection<IProfile> Get();

    /** Check if the cache contains a profile with matching key.
     * @param key   IKey to search for
     * @return  Returns true if a matching profile can be found.
     */
    boolean Contains(IKey key);

    /**
     * Clear all contacts in the cache.
     */
    void Clear();

}
