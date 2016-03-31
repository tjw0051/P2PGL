package P2PGL.UDP;

import P2PGL.EventListener.MessageReceivedListener;
import P2PGL.EventListener.NewContactListener;
import P2PGL.Profile.IProfile;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by t_j_w on 07/03/2016.
 */
public interface IUDPChannel {
    String toString();
    void Broadcast(Object obj,Type type) throws IOException;
    void AddContactListener(NewContactListener listener);
    void AddMessageListener(MessageReceivedListener listener);
    void Stop();
    void Add(IProfile profile);
    void Listen(String channelName);
    void ClearQueue();
    void ClearContacts();
    <T> T ReadNext() throws ClassNotFoundException;
    <T> T PeekNext() throws ClassNotFoundException;
}
