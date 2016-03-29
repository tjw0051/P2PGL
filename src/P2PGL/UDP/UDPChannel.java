package P2PGL.UDP;

import P2PGL.EventListener.MessageReceivedListener;
import P2PGL.IKey;
import P2PGL.InterfaceAdapter;
import P2PGL.Profile.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.*;

/**
 * A communication channel that broadcasts messages to all users
 * in the channel.
 */
public class UDPChannel implements IUDPChannel {
    private int port;
    private Queue<UDPPacket> incomingQueue;
    private List<MessageReceivedListener> messageReceivedListeners;
    private Thread listenThread;
    private IProfileCache profileCache;
    private boolean listening;
    private Gson gson;
    private IProfile profile;

    public UDPChannel(IProfile profile, int port) {
        this.profile = profile;
        this.port = port;
        messageReceivedListeners = new ArrayList<>();
        profileCache = new ProfileCache();
        listening = false;
        GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(IKey.class, new InterfaceAdapter<IKey>());
        this.gson = gsonBuilder.create();
    }

    public void Listen() {
        listening = true;
        listenThread = new Thread(new ListenThread(port));
        listenThread.start();
        //Start listening to messages, create event for receive trigger.
    }

    public void addListener(MessageReceivedListener listener) {
        messageReceivedListeners.add(listener);
    }

    /** Called when listener Thread receives a new message.
     * @param messageType   Serialized JSON message received.
     */
    private void MessageReceived(String messageType, IKey sender) {
        for(MessageReceivedListener listener : messageReceivedListeners) {
            listener.MessageReceived(messageType, sender);
        }
    }

    /**
     * Add a user to the channel.
     * @param profile   profile of user.
     */
    public void Add(IProfile profile) {
        profileCache.Add(profile);
    }

    public boolean Contains(IKey user) {
        return profileCache.Contains(user);
    }

    public void ClearContacts() {
        profileCache.Clear();
    }

    public void Send(IProfile profile, String message) throws IOException{
        Send(profile, message, String.class);
    }

    public void Send(IProfile profile, Object obj, Type type) throws IOException{
        byte[] bytes = SerializePacket(obj, type).getBytes();
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, profile.GetIPAddress(), profile.GetUDPPort());
        socket.send(packet);
    }

    public String SerializePacket(Object obj, Type type) {
        String data = gson.toJson(obj, type);
        UDPPacket packet = new UDPPacket(data, type.getTypeName(), profile.GetKey());
        return SerializePacket(packet);
    }

    public String SerializePacket(UDPPacket packet) {
        return gson.toJson(packet, UDPPacket.class);
    }

    public UDPPacket DeserializePacket(String ser) {
        return gson.fromJson(ser, UDPPacket.class);
    }

    /** Send a message to all clients in the channel (profile cache)
     * @param obj   Object to be sent.
     * @param type  Type of obj.
     * @throws IOException  Error sending message.
     */
    public void Broadcast(Object obj, Type type) throws IOException{
        Collection<IProfile> profiles = profileCache.Get();
        Iterator iter = profiles.iterator();
        while(iter.hasNext()) {
            IProfile entry = (IProfile)iter.next();
            //If the message cannot be sent to the player, remove
            //the player from the cache.

           // if(Send(entry, obj, type) == -1)
                //profileCache.Remove(entry.GetName());
            try {
                Send(entry, obj, type);
            } catch (IOException ioe) {
                System.out.println("Cannot send to: " + entry.GetName());
            }
        }
    }

    public void Broadcast(String message) throws IOException{
        Broadcast(message, String.class);
    }

    /** Read next UDPPacket in queue and remove it.
     * @return
     */
    public UDPPacket ReadNextPacket() {
        if(!incomingQueue.isEmpty())
            return incomingQueue.remove();
        else
            return null;
    }

    /** Read next object in queue and remove it.
     * @param <T>
     * @return
     * @throws ClassNotFoundException
     */
    public  <T> T ReadNext() throws ClassNotFoundException {
        if(incomingQueue.isEmpty())
            return null;
        UDPPacket packet = incomingQueue.remove();
        return gson.fromJson(packet.message, (Type)Class.forName(packet.type));
    }

    /** Read next UDPPacket in the queue without removing it.
     * @return
     */
    public UDPPacket PeekNextPacket() {
        return incomingQueue.peek();
    }

    /** Read next object in the queue without removing it.
     * @param <T>   Type of object held in UDPPacket
     * @return  Object held in UDPPacket
     * @throws ClassNotFoundException   Object cannot be cast to any type found.
     */
    public <T> T PeekNext() throws ClassNotFoundException{
        UDPPacket packet = incomingQueue.peek();
        return gson.fromJson(packet.message, (Type)Class.forName(packet.type));
    }

    public void ClearQueue() {
        incomingQueue.clear();
    }

    /**
     * Listens to incoming UDP messages.
     */
    private class ListenThread implements Runnable{
        DatagramSocket socket;
        int port;
        public ListenThread(int port) {
            incomingQueue = new LinkedList<>();
            this.port = port;
        }

        public void run() {
            try {
                socket = new DatagramSocket(port);
                byte[] buffer = new byte[10000];
                DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

                while(listening) {
                    try {
                        socket.receive(receivedPacket);
                        System.out.println("Message received");
                        byte[] receivedData = receivedPacket.getData();
                        String serializedData = new String(receivedData, 0, receivedPacket.getLength());
                        UDPPacket packet = DeserializePacket(serializedData);
                        incomingQueue.add(packet);
                        MessageReceived(packet.type, packet.sender);
                        //TODO: If update from unknown player, add to cache.
                    } catch(IOException ioe) {
                        System.out.println("IOException");
                    }
                }
                if(!listening) {
                    socket.disconnect();
                    socket.close();
                }
            } catch(SocketException se) {
                System.out.println("Socket Exception");
                //TODO: do something if socket exception.
            }
        }
    }
    public void Stop() {
        listening = false;
    }
}
