package P2PGL.Profile;

import P2PGL.IKey;
import P2PGL.Profile.IProfile;

import java.util.Collection;

/**
 * Created by t_j_w on 18/03/2016.
 */
public interface IProfileCache {
    void Add(IProfile profile);
    void Add(IProfile[] profiles);
    IProfile Get(String name);
    Collection<IProfile> Get();
    boolean Contains(IKey key);
    void Clear();

}
