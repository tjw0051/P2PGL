package P2PGL.UDP;

import P2PGL.EventListener.MessageReceivedListener;
import P2PGL.EventListener.NewContactListener;
import P2PGL.IKey;
import P2PGL.InterfaceAdapter;
import P2PGL.Profile.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

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
    private String channelName;
    private Queue<UDPPacket> incomingQueue;
    private List<MessageReceivedListener> messageReceivedListeners;
    private List<NewContactListener> newContactListeners;
    private Thread listenThread;
    private IProfileCache profileCache;
    private boolean listening;
    private Gson gson;
    private IProfile profile;
    private Random rand;

    public UDPChannel(IProfile profile, int port) {
        this.profile = profile;
        this.port = port;
        messageReceivedListeners = new ArrayList<>();
        newContactListeners = new ArrayList<>();
        incomingQueue = new LinkedList<>();
        profileCache = new ProfileCache();
        listening = false;
        GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(IKey.class, new InterfaceAdapter<IKey>());
        this.gson = gsonBuilder.create();
        this.rand = new Random();
    }

    public void Listen(String channelName) {
        this.channelName = channelName;
        listenThread = new Thread(new ListenThread(port));
        listening = true;
        listenThread.start();
        //Start listening to messages, create event for receive trigger.
    }

    public void addMessageListener(MessageReceivedListener listener) {
        messageReceivedListeners.add(listener);
    }

    /** Called when listener Thread receives a new message.
     * @param messageType   Serialized JSON message received.
     */
    private void MessageReceived(String messageType, IKey sender) {
        for(MessageReceivedListener listener : messageReceivedListeners) {
            listener.MessageReceivedListener(messageType, sender);
        }
    }
    //TODO: remove functions for listeners
    public void AddContactListener(NewContactListener listener) { newContactListeners.add(listener); }

    private void NewContactListener(IKey key) {
        for(NewContactListener listener : newContactListeners) {
            listener.NewContactListener(key);
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
        //Send(SerializePacket(obj, type, this.profile.GetKey(), this.profile.GetUDPChannel()).getBytes());
        byte[] bytes = SerializePacket(obj, type, this.profile.GetKey(), this.profile.GetUDPChannel()).getBytes();
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, profile.GetIPAddress(), profile.GetUDPPort());
        socket.send(packet);
    }
    /*
    private void Send(byte[] bytes) throws IOException{
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, profile.GetIPAddress(), profile.GetUDPPort());
        socket.send(packet);
    }
    */

    public String SerializePacket(Object obj, Type type, IKey key, String channelName) {
        String data = gson.toJson(obj, type);
        UDPPacket packet = new UDPPacket(data, type.getTypeName(), key, channelName);
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
                        if(packet.channel.equals(channelName)) {
                            incomingQueue.add(packet);

                            /* Listeners require extra processing so are run in a separate thread
                                to prevent ListenThread from being blocked */
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    MessageReceived(packet.type, packet.sender);
                                    //TODO: If update from unknown player, add to cache.
                                    if(!profileCache.Contains(packet.sender))
                                        NewContactListener(packet.sender);
                                }
                            }).start();
                        }
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
