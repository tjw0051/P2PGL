package P2PGL;

import P2PGL.Connection.HybridConnection;
import P2PGL.Connection.IHybridConnection;
import P2PGL.Util.ISerializedData;
import P2PGL.DHT.KademliaFacade;
import P2PGL.Util.SerializedData;
import P2PGL.Profile.IProfile;
import P2PGL.Profile.IProfileCache;
import P2PGL.Profile.ProfileCache;
import P2PGL.UDP.UDPChannel;
import P2PGL.Util.IKey;
import P2PGL.Util.Key;

/**
 * Factory to create objects for setting up a Kademlia DHT.
 */
public class ConnectionFactory {
    public static IHybridConnection GetHybridConnection(IProfile profile) {
        IHybridConnection conn = new HybridConnection(profile,
                new KademliaFacade(profile),
                new UDPChannel(profile, profile.GetLocalChannelPort()));
        return conn;
    }

    public static ISerializedData GetSerializedData(String data, String type) {
        return new SerializedData(data, type);
    }

    public static IProfileCache GetProfileCache() {
        return new ProfileCache();
    }

    public static IKey GetKey() {
        return new Key();
    }

    public static IKey GetKey(byte[] bytes) {
        return new Key(bytes);
    }

    public static IKey[] GetKeys(int count) {
        return new Key[count];
    }
}
