package P2PGL.EventListener;

import P2PGL.IKey;
import P2PGL.UDP.UDPPacket;

import java.net.InetAddress;

/**
 * Created by t_j_w on 17/03/2016.
 */
public interface MessageReceivedListener {
    void MessageReceivedListener(Object obj, Class messageType, IKey sender);
}
