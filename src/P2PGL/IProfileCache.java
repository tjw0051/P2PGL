package P2PGL;

/**
 * Created by t_j_w on 18/03/2016.
 */
public interface IProfileCache {
    void Add(IProfile profile);
    void Add(IProfile[] profiles);
    IProfile Get(String name);
    void Clear();

}
