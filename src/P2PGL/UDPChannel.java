package P2PGL;

import java.net.InetAddress;
import java.util.List;

/**
 * A communication channel that broadcasts messages to all users
 * in the channel.
 */
public class UDPChannel implements IUDPChannel {
    private String name;
    private InetAddress ipAddress;
    private int port;

    List<IProfile> users;

    public UDPChannel(String name, InetAddress ipAddress, int port) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public String GetName() { return name; }


    /**
     * Add a user to the channel.
     * @param profile   profile of user.
     */
    public void Add(IProfile profile) {

    }

    class ListenThread {}
}
