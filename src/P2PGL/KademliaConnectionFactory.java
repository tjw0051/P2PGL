package P2PGL;

import P2PGL.DHT.KademliaFacade;
import P2PGL.Profile.IProfile;
import P2PGL.UDP.UDPChannel;

/**
 * Created by t_j_w on 31/03/2016.
 */
public class KademliaConnectionFactory {
    public static Connection Get(IProfile profile) {
        Connection conn = new Connection(profile,
                new KademliaFacade(profile),
                new UDPChannel(profile, profile.GetUDPPort()));
        return conn;
    }
}
