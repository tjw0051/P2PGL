package P2PGL.UDP;

import P2PGL.EventListener.MessageReceivedListener;
import P2PGL.EventListener.NewContactListener;
import P2PGL.Util.IKey;
import P2PGL.Util.InterfaceAdapter;
import P2PGL.ConnectionFactory;
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
public class UDPChannel implements ILocalChannel {
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

    /** Create a new UDP channel on port in profile.GetLocalChannelPort.
     * @param profile   Profile of this peer.
     */
    public UDPChannel(IProfile profile) {
        this(profile, profile.GetLocalChannelPort());
    }

    /** Create a UDP channel on the defined port.
     * @param profile   Profile of this node
     * @param port  Port to listen to incoming messsages
     */
    public UDPChannel(IProfile profile, int port) {
        this.profile = profile;
        this.port = port;
        messageReceivedListeners = new ArrayList<>();
        newContactListeners = new ArrayList<>();
        incomingQueue = new LinkedList<>();
        profileCache = ConnectionFactory.GetProfileCache();
        listening = false;
        GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(IKey.class, new InterfaceAdapter<IKey>());
        this.gson = gsonBuilder.create();
        this.rand = new Random();
    }

    /** Start listening to incoming messages
     * @param channelName   Channel name to listen on
     */
    public void Listen(String channelName) {
        this.channelName = channelName;
        listenThread = new Thread(new ListenThread(port));
        listening = true;
        listenThread.start();
        //Start listening to messages, create event for receive trigger.
    }

    /** Add a listener to be informed when a message is received.
     * @param listener  Class implementing MessageReceivedListener
     */
    public void AddMessageListener(MessageReceivedListener listener) {
        messageReceivedListeners.add(listener);
    }

    /** Remove message listener
     * @param listener  Listener to remove
     */
    public void RemoveMessageListener(MessageReceivedListener listener) { messageReceivedListeners.remove(listener); }

    /** Called when listener Thread receives a new message.
     * @param packet   Serialized JSON message received.
     */
    private void MessageReceived(UDPPacket packet) {
        try {
            Class C = Class.forName(packet.type);
            Object obj = gson.fromJson(packet.message, C);

            for(MessageReceivedListener listener : messageReceivedListeners) {
                listener.MessageReceivedListener(obj, C, packet.sender);
            }
        } catch (ClassNotFoundException cnfe) {
        }

    }

    /** Add a listener to be informed when a message is received from an unknown key.
     * @param listener  Class implementing NewContactListener
     */
    public void AddContactListener(NewContactListener listener) { newContactListeners.add(listener); }

    /** Remove contact listener
     * @param listener listener to remove
     */
    public void RemoveContactListener(NewContactListener listener) { newContactListeners.remove(listener); }


    /** Calls NewContactListeners when a message is received from an unknown contact.
     * @param key
     */
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

    /**
     * Clear contacts in channel
     */
    public void ClearContacts() {
        profileCache.Clear();
    }

    /** Send a message to a single peer
     * @param profile   Profile of peer to send message to
     * @param obj   Object to send
     * @param type  Type of object
     * @throws IOException  Error sending message to peer
     */
    public void Send(IProfile profile, Object obj, Type type) throws IOException{
        //Send(SerializePacket(obj, type, this.profile.GetKey(), this.profile.GetLocalChannelName()).getBytes());
        byte[] bytes = SerializePacket(obj, type, this.profile.GetKey(), this.profile.GetLocalChannelName()).getBytes();
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, profile.GetIPAddress(), profile.GetLocalChannelPort());
        socket.send(packet);
    }

    /** Serialize data and store in Json serialized UDP packet
     * @param obj   Object to serialize
     * @param type  Type of object
     * @param key   Key of sender (this)
     * @param channelName   Channel name message is sent on.
     * @return  Json serialized UDP packet.
     */
    private String SerializePacket(Object obj, Type type, IKey key, String channelName) {
        String data = gson.toJson(obj, type);
        UDPPacket packet = new UDPPacket(data, type.getTypeName(), key, channelName);
        return SerializePacket(packet);
    }

    /** Serialize UDP Packet to Json
     * @param packet    packet to serialize
     * @return
     */
    private String SerializePacket(UDPPacket packet) {
        return gson.toJson(packet, UDPPacket.class);
    }

    /** Deserialize UDP Packet
     * @param ser   Json UDP Packet
     * @return  UDPPacket Object
     */
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
            try {
                Send(entry, obj, type);
            } catch (IOException ioe) {
                System.out.println("Cannot send to: " + entry.GetName());
            }
        }
    }

    /** Read next message in incoming message queue and remove it from the queue
     * @param <T>   Type of object
     * @return  Deserialized object
     * @throws ClassNotFoundException   Class cannot be found to deserialize message.
     */
    public  <T> T ReadNext() throws ClassNotFoundException {
        if(incomingQueue.isEmpty())
            return null;
        UDPPacket packet = incomingQueue.remove();
        return gson.fromJson(packet.message, (Type)Class.forName(packet.type));
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

    /**
     * Clear incoming messages queue.
     */
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
                        byte[] receivedData = receivedPacket.getData();
                        String serializedData = new String(receivedData, 0, receivedPacket.getLength());
                        UDPPacket packet = DeserializePacket(serializedData);
                        if(packet.channel.equals(channelName)) {
                            incomingQueue.add(packet);

                            /* Listeners require extra processing so are run in a separate thread
                                to prevent ListenThread from blocking */
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    MessageReceived(packet);
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

    /**
     * Stop listening to incoming messages
     */
    public void Stop() {
        listening = false;
    }
}
