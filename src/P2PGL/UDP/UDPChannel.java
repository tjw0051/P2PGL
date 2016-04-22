package P2PGL.UDP;

import P2PGL.EventListener.MessageReceivedListener;
import P2PGL.EventListener.NewContactListener;
import P2PGL.P2PGL;
import P2PGL.Util.IKey;
import P2PGL.Util.InterfaceAdapter;
import P2PGL.Profile.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.CharArrayReader;
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
    private Queue<IPacket> incomingQueue;
    private List<MessageReceivedListener> messageReceivedListeners;
    private List<NewContactListener> newContactListeners;
    private Thread listenThread;
    private IProfileCache profileCache;
    private boolean listening;
    private Gson gson;
    private IProfile profile;
    private Random rand;

    private Thread ackMessageOperationThread;
    private AckMessageOperation ackMessageOperation;

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
        profileCache = P2PGL.GetInstance().GetFactory().GetProfileCache();
        listening = false;
        GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(IKey.class, new InterfaceAdapter<IKey>());
        this.gson = gsonBuilder.create();
        this.rand = new Random();

        ackMessageOperation = new AckMessageOperation(this);
        ackMessageOperationThread = new Thread(ackMessageOperation);
    }

    /** Set the current user profile
     * @param profile to set.
     */
    public void SetProfile(IProfile profile) {
        this.profile = profile;
    }

    /** Start listening to incoming messages
     */
    public void Listen() throws SocketException {
        this.channelName = profile.GetLocalChannelName();
        boolean completed;
        ListenThread listener = new ListenThread(port);
        listenThread = new Thread(listener);
        listening = true;
        listenThread.start();
        while(listener.connected != true) {
            if(listener.exceptionThrown != null) {
                throw (SocketException)listener.exceptionThrown;
            }
        }

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
    protected void MessageReceived(IPacket packet) {
        try {
            Class c = Class.forName(packet.GetType());
            Object obj = gson.fromJson(packet.GetMessage(), c);

            if(!messageReceivedListeners.isEmpty()) {
                for (MessageReceivedListener listener : messageReceivedListeners) {
                    listener.MessageReceivedListener(obj, c, packet.GetSender());
                }
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
    protected void NewContactListener(IKey key) {
        System.out.println("New Contact found");
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
        Send(profile, SerializeData(obj, type, this.profile.GetKey(), this.profile.GetLocalChannelName()));
    }

    /** Send a message to a single peer
     * @param profile   Profile of peer to send message to
     * @param udpPacket Packet to send
     * @throws IOException  Error sending message to peer
     */
    public void Send(IProfile profile, IPacket udpPacket) throws IOException{
        byte[] bytes = SerializePacket(udpPacket).getBytes();
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, profile.GetIPAddress(), profile.GetLocalChannelPort());
        socket.send(packet);
    }

    /** Send a message requiring acknowledgement of receipt to a single peer
     * @param profile   Profile of peer to send message to
     * @param obj   Object to send
     * @param type  Type of object
     * @throws IOException  Error sending message to peer
     */
    public void SendAck(IProfile profile, Object obj, Type type) throws IOException {
        IPacket packet = SerializeData(obj, type, this.profile.GetKey(), this.profile.GetLocalChannelName());
        packet.SetAckKey(P2PGL.GetInstance().GetFactory().GetKey());
        ackMessageOperation.Add(packet, profile);
    }

    /** Serialize data and store in Json serialized UDP packet
     * @param obj   Object to serialize
     * @param type  Type of object
     * @param key   Key of sender (this)
     * @param channelName   Channel name message is sent on.
     * @return  Json serialized UDP packet.
     */
    protected IPacket SerializeData(Object obj, Type type, IKey key, String channelName) {
        String data = gson.toJson(obj, type);
        //IPacket packet = new UDPPacket(data, type.getTypeName(), key, channelName);
        IPacket packet = P2PGL.GetInstance().GetFactory().GetPacket(data, type.getTypeName(), key, channelName);
        return packet;
    }

    /** Serialize UDP Packet to Json
     * @param packet    packet to serialize
     * @return
     */
    protected String SerializePacket(IPacket packet) {
        return gson.toJson(packet, P2PGL.GetInstance().GetFactory().GetPacket().getClass());
    }

    /** Deserialize UDP Packet
     * @param ser   Json UDP Packet
     * @return  UDPPacket Object
     */
    protected IPacket DeserializePacket(String ser) {
        return gson.fromJson(ser, P2PGL.GetInstance().GetFactory().GetPacket().getClass());
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
            Send(entry, obj, type);
        }
    }

    /** Send a message  requiring acknowledgement of receipt to all
     *  clients in the channel (profile cache)
     * @param obj   Object to be sent.
     * @param type  Type of obj.
     * @throws IOException  Error sending message.
     */
    public void BroadcastAck(Object obj, Type type) throws IOException {
        Collection<IProfile> profiles = profileCache.Get();
        Iterator iter = profiles.iterator();
        while(iter.hasNext()) {
            IProfile entry = (IProfile)iter.next();
            SendAck(entry, obj, type);
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
        IPacket packet = incomingQueue.remove();
        return gson.fromJson(packet.GetMessage(), (Type)Class.forName(packet.GetType()));
    }

    /** Read next object in the queue without removing it.
     * @param <T>   Type of object held in UDPPacket
     * @return  Object held in UDPPacket
     * @throws ClassNotFoundException   Object cannot be cast to any type found.
     */
    public <T> T PeekNext() throws ClassNotFoundException{
        IPacket packet = incomingQueue.peek();
        return gson.fromJson(packet.GetMessage(), (Type)Class.forName(packet.GetType()));
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
    protected class ListenThread implements Runnable{
        DatagramSocket socket;
        int port;
        public boolean connected;
        public Exception exceptionThrown;

        public ListenThread(int port) {
            this.port = port;
        }

        public void run() {
                try {
                    socket = new DatagramSocket(port);
                    connected = true;
                } catch (SocketException se) {
                    System.out.println("Socket exception creating socket");
                    exceptionThrown = se;
                    //TODO: Handle socket exception
                }
                byte[] buffer = new byte[10000];
                DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

                while(listening) {
                    try {
                        if(receivedPacket != null) {
                            socket.receive(receivedPacket);
                            System.out.println("Packet Received");
                            byte[] receivedData = receivedPacket.getData();
                            String serializedData = new String(receivedData, 0, receivedPacket.getLength());
                            IPacket packet = DeserializePacket(serializedData);
                            if (packet.GetChannel().equals(channelName)) {
                                System.out.println("correct channel");
                                if (packet.GetMessage() != "ack")
                                    incomingQueue.add(packet);

                            /* Listeners require extra processing so are run in a separate thread
                                to prevent ListenThread from blocking */
                                //new Thread(new Runnable() {
                                    //@Override
                                    //public void run() {
                                        MessageReceived(packet);

                                        if (!profileCache.Contains(packet.GetSender()))
                                            NewContactListener(packet.GetSender());
                                        else {
                                            //process ack messages
                                            if (packet.GetAckKey() != null) {
                                                try {
                                                    ProcessAck(packet);
                                                } catch (IOException ioe) {
                                                    System.out.println("Error processing ack");
                                                    //TODO: handle - error sending ack
                                                }
                                            }

                                        }
                                    //}
                                //}).start();
                            }
                        }
                    } catch(IOException ioe) {
                        //TODO Handle - thrown at socket.receive - error receiving
                        System.out.println("Error receiving packets");
                    }
                }
                if(!listening) {
                    socket.close();
                    socket.disconnect();
                    connected = false;
                    exceptionThrown = null;
                }
        }

        /** Called when a packet contains an acknowledgement key.
         *  -   If the packet requires acknowledgement, an ack message
         *      is sent back.
         *  -   If the packet IS an acknowledgement, the AckMessageOperation
         *      is called, to stop further resends.
         * @param packet
         */
        private void ProcessAck(IPacket packet) throws IOException {
            if(packet.GetMessage().equals("ack")) {
                ackMessageOperation.AckReceived(packet.GetAckKey());
            }
            else {
                //IPacket ackPacket = new UDPPacket("ack", "", profile.GetKey(), channelName);
                IPacket ackPacket = P2PGL.GetInstance().GetFactory()
                        .GetPacket("ack", "", profile.GetKey(), channelName);
                ackPacket.SetAckKey(packet.GetAckKey());
                Send(profileCache.Get(packet.GetSender()), ackPacket);
            }
        }
    }

    /**
     * Stop listening to incoming messages
     */
    public void Stop() {
        ackMessageOperation.Stop();
        listening = false;
    }
}
