package P2PGL.UDP;

import P2PGL.EventListener.MessageReceivedListener;
import P2PGL.EventListener.NewContactListener;
import P2PGL.Profile.IProfile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by t_j_w on 07/03/2016.
 */
public interface ILocalChannel {
    /** Broadcast a message to all contacts in profile cache
     * @param obj   Object to broadcast
     * @param type  Type of object
     * @throws IOException  Error broadcasting message
     */
    void Broadcast(Object obj,Type type) throws IOException;

    /** Send a message  requiring acknowledgement of receipt to all
     *  clients in the channel (profile cache)
     * @param obj   Object to be sent.
     * @param type  Type of obj.
     * @throws IOException  Error sending message.
     */
    void BroadcastAck(Object obj, Type type) throws IOException;

    /** Send a message to a single peer
     * @param profile   Profile of peer to send message to
     * @param obj   Object to send
     * @param type  Type of object
     * @throws IOException  Error sending message to peer
     */
    void Send(IProfile profile, Object obj, Type type) throws IOException;

    /** Send a message to a single peer
     * @param profile   Profile of peer to send message to
     * @param udpPacket Packet to send
     * @throws IOException  Error sending message to peer
     */
    public void Send(IProfile profile, IPacket udpPacket) throws IOException;

    /** Send a message requiring acknowledgement of receipt to a single peer
     * @param profile   Profile of peer to send message to
     * @param obj   Object to send
     * @param type  Type of object
     * @throws IOException  Error sending message to peer
     */
    void SendAck(IProfile profile, Object obj, Type type) throws IOException;

    /** Add a listener to be informed when a message is received from an unknown key.
     * @param listener  Class implementing NewContactListener
     */
    void AddContactListener(NewContactListener listener);

    /** Remove contact listener
     * @param listener listener to remove
     */
    void RemoveContactListener(NewContactListener listener);

    /** Add a listener to be informed when a message is received.
     * @param listener  Class implementing MessageReceivedListener
     */
    void AddMessageListener(MessageReceivedListener listener);

    /** Remove message listener
     * @param listener  Listener to remove
     */
    void RemoveMessageListener(MessageReceivedListener listener);

    /**
     * Stop listening to incoming messages
     */
    void Stop();

    /** Add a new contact to the channel
     * @param profile   profile of contact
     */
    void Add(IProfile profile);

    /** Set the current user profile.
     * @param profile to set.
     */
    void SetProfile(IProfile profile);

    /** Start listening to incoming messages
     */
    void Listen() throws SocketException;

    /**
     * Clear incoming messages queue.
     */
    void ClearQueue();

    /**
     * Clear contacts in channel
     */
    void ClearContacts();

    /** Read next message in incoming message queue and remove it from the queue
     * @param <T>   Type of object
     * @return  Deserialized object
     * @throws ClassNotFoundException   Class cannot be found to deserialize message.
     */
    <T> T ReadNext() throws ClassNotFoundException;

    /** Read next message in incoming message queue without removing it
     * @param <T>   Type of object
     * @return  Deserialized object
     * @throws ClassNotFoundException   Class cannot be found to deserialize message.
     */
    <T> T PeekNext() throws ClassNotFoundException;
}
