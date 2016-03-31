package P2PGL.EventListener;

import P2PGL.IKey;
import P2PGL.UDP.UDPPacket;

import java.net.InetAddress;

/**
 * Listener for reporting when a new message has been received.
 */
public interface MessageReceivedListener {
    /** Returns Object obj of Class messageType from IKey sender
     * @param obj   Deserialized Object of Class messageType.
     * @param messageType   Class of obj.
     * @param sender    IKey of sender
     * {@code if(messageType.equals(PlayerState.class)) {PlayerState state = (PlayerState)obj; }}
     */
    void MessageReceivedListener(Object obj, Class messageType, IKey sender);
}
