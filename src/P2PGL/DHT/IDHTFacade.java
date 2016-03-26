package P2PGL.DHT;

import P2PGL.IKey;
import P2PGL.Profile.IProfile;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Facade to hide DHT functions, allowing Kademlia to be swapped
 * for a different DHT implementation (e.g. Chord, Tapestry)
 */
public interface IDHTFacade {

    void SetProfile(IProfile profile);
    void Connect() throws IOException;
    void Connect(String serverName, InetAddress serverAddress, int serverPort) throws IOException;
    boolean isConnected();
    void Disconnect() throws IOException;
    void Store(IKey key, String data) throws IOException;
    //TODO: Use IKey for keys instead of strings.
    String Get(IKey key) throws IOException;
    IKey[] ListUsers();
    IKey GetId();
}
