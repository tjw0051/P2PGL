package P2PGL.Connection;

import P2PGL.DHT.IDHTFacade;
import P2PGL.Util.ISerializedData;
import P2PGL.EventListener.MessageReceivedListener;
import P2PGL.EventListener.NewContactListener;
import P2PGL.Exceptions.ContentNotFoundException;
import P2PGL.Util.IKey;
import P2PGL.Profile.IProfile;
import P2PGL.UDP.ILocalChannel;
import P2PGL.Util.InterfaceAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.istack.internal.Nullable;


import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;


/**
 * A Hybrid connection utilising DHT and Local communication
 * @author Thomas Walker
 */
public class HybridConnection implements IHybridConnection, NewContactListener, MessageReceivedListener{
    private IProfile profile;
    private Gson gson;
    private IDHTFacade dht;
    private ILocalChannel localChannel;
    private List<MessageReceivedListener> messageReceivedListeners;


    /** Create a new Hybrid connection using a DHT implementation and local channel implementation
     * @see P2PGL.ConnectionFactory
     * @param profile   Profile containing descriptor for this node.
     * @param dhtImplementation DHT implementation to be used.
     * @param localChannel    Local channel implementation to use.
     */
    public HybridConnection(IProfile profile, IDHTFacade dhtImplementation, ILocalChannel localChannel) {
        this.profile = profile;
        GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(IKey.class, new InterfaceAdapter<IKey>());
        gson = gsonBuilder.create();
        dht = dhtImplementation;
        dht.SetProfile(profile);

        //Create Local Channel
        messageReceivedListeners = new ArrayList<>();
        this.localChannel = localChannel;
        localChannel.AddContactListener(this);
        localChannel.AddMessageListener(this);
    }

    /** Connect to DHT and store profile without bootstrapping to a server.
     *  Can be used to start initial server.
     * @throws IOException  Error connecting to server
     */
    public void Connect() throws IOException {
        dht.Connect();
        StoreProfile();
    }

    /**
     * KademliaFacade to node.
     * @param serverName    Name of server.
     * @param destIPAddress IP Address of server.
     * @param destPort      Port number of server.
     * @throws IOException  Error connecting to server.
     */
    public void Connect(String serverName, InetAddress destIPAddress, int destPort) throws IOException {
        try {
            dht.Connect(serverName, destIPAddress, destPort);
            StoreProfile();
        } catch(IOException ioe) {
            throw ioe;
        }
    }

    /** Check if DHT is connected.
     * @return  True if DHT is connected.
     */
    public boolean isConnected() {
        return dht.isConnected();
    }

    /** Disconnect from DHT and local channel
     * @throws IOException  Error disconnecting
     */
    //TODO: Remove profile and other pieces of player.
    public void Disconnect() throws IOException {
        try {
            dht.Disconnect();
            localChannel.Stop();
        } catch(IOException ioe) {
            ioe.printStackTrace();
            throw ioe;
        }
    }

    /** Join a local channel with name channelName.
     * @param channelName Name/Identifier of channel
     * @throws IOException  Cannot join local channel
     */
    //TODO: Split into start /change/join etc.
    public void JoinLocalChannel(String channelName) throws IOException{
        //Clear current contacts and incoming messages
        localChannel.Stop();
        localChannel.ClearContacts();
        localChannel.ClearQueue();

        //Update profile with new channel
        profile.SetLocalChannel(channelName);
        StoreProfile();

        //Add contacts from new channel
        List<IProfile> profiles = GetProfiles(ListUsers());
        for(IProfile profile : profiles) {
            if(profile.GetLocalChannelName().equals(channelName))
                localChannel.Add(profile);
        }
        localChannel.Listen(channelName);
    }

    /** Broadcast a message to all peers in the local channel.
     * @param obj   Object to send
     * @param type  Type of object - used for serialization
     *              @see Gson
     * @throws IOException  Error broadcasting message
     */
    public void Broadcast(Object obj, Type type) throws IOException{
        localChannel.Broadcast(obj, type);
    }

    /** Get data from DHT
     * @param key   Key where data is stored.
     * @param type  Type of data
     * @param <T>   Type of data
     * @return  Deserialized data from dht.
     * @throws IOException  Cannot get data from DHT
     * @throws ClassNotFoundException   Type name does not match any type found at runtime
     *                                  to deserialize to.
     */
    @Nullable
    public <T> T Get(IKey key, Type type) throws IOException, ClassNotFoundException {
        try {
            ISerializedData serializedData = dht.Get(key, type.getTypeName());
            if (serializedData == null)
                return null;
            return gson.fromJson(serializedData.GetData(), (Type) Class.forName(serializedData.GetType()));
        } catch (ContentNotFoundException cnfe) {
            return null;
        }
    }

    /** Store data in DHT
     * @param destKey   Key to store data at
     * @param obj   Object to be stored
     * @param type  Type of object.
     *              Example: {@code String.class}
     * @throws IOException  Error storing data on DHT
     */
    public void Store(IKey destKey, Object obj, Type type) throws IOException{
        dht.Store(destKey, gson.toJson(obj, type), type.getTypeName());
    }

     /**
     * Store user profile on DHT. Destination name of profile is
     * this nodeId with last byte incremented by 1.
     * @return  Key profile is stored at.
     * @throws IOException
     */
    protected IKey StoreProfile() throws IOException {
        IKey profileKey = profile.GetKey().Next();
        dht.Store(profileKey, gson.toJson(profile, profile.GetType()), profile.getClass().getTypeName());
        return profileKey;
    }

    /** List users connected to DHT
     * @return  Keys for each user.
     */
    //TODO: return list
    public IKey[] ListUsers() {
        return dht.ListUsers();
    }

    /** Get Key for this node
     * @return  Key for this node
     */
    public IKey GetKey() {
        return dht.GetId();
        //return new Key(node.getNode().getNodeId());
    }

    /**
     * @return Profile of this node.
     */
    public IProfile GetLocalProfile() {
        return profile;
    }

    /** Set the profile of this node and store it in the DHT
     * @param profile   new Profile
     * @throws IOException  Error storing profile in DHT
     */
    public void SetLocalProfile(IProfile profile) throws IOException {
        this.profile = profile;
        StoreProfile();
    }

    /**
     * Retrieve the profile of user with KademliaId userKey.
     * @see HybridConnection#StoreProfile()
     * @param userKey   KademliaId of user
     * @return  Profile of user. Returns null if profile cannot be found.
     * @throws IOException  Thrown if a get operation cannot be performed (check connection).
     */
    @Nullable
    public IProfile GetProfile(IKey userKey) throws IOException {
        try {
            IProfile prof = Get(userKey.Next(), profile.GetType());
            if(prof == null)
                return null;
            return prof;
        } catch(ClassNotFoundException cnfe) {
            throw new Error("Profile class cannot be found. " +
                    "Check P2PGL library is not missing IProfile or Profile");
        }
    }

    /** Get list of profiles from DHT. Can be used in combination with {@code ListUsers()}
     *  to retreive all profiles.
     * @param keys  List of keys to find profiles for.
     * @return  profiles found matching keys
     * @throws IOException  Error retrieving profiles.
     */
    public List<IProfile> GetProfiles(IKey[] keys) throws IOException {
        List<IProfile> profiles = new ArrayList<IProfile>();
        for(int i = 0; i < keys.length; i++) {
            try {
                IProfile prof = GetProfile(keys[i]);
                if(prof != null && !prof.GetKey().Equals(profile.GetKey()))
                    profiles.add(prof);
            } catch (IOException ioe) {
                throw ioe;
            }
        }
        return profiles;
    }

    /** Listener called when localChannel receives messages from an unknown key.
     * @param key Key of the unknown contact.
     */
    @Override
    public void NewContactListener(IKey key) {
        try {
            localChannel.Add(GetProfile(key));
        } catch (IOException ioe) {
        }
    }

    /** Add listeners for when a new message is received by local channel
     * @param listener  Class implementing MessageReceivedListener
     */
    public void AddMessageListener(MessageReceivedListener listener) {
        messageReceivedListeners.add(listener);
    }

    /** Remove message listener
     * @param listener  listener to remove
     */
    public void RemoveMessageListener(MessageReceivedListener listener) {
        messageReceivedListeners.remove(listener);
    }

    /** Listener called when localChannel receives a new message
     * @param obj         Deserialized Object of Class messageType.
     * @param messageType Class of obj.
     * @param sender      IKey of sender
     */
    @Override
    public void MessageReceivedListener(Object obj, Class messageType, IKey sender) {
        for(MessageReceivedListener listener : messageReceivedListeners)
            listener.MessageReceivedListener(obj, messageType, sender);
    }

    /** Returns the local channel
     * @return  local channel
     */
    public ILocalChannel GetLocalChannel() { return localChannel; }

    public static void main(String[] args) { }
}
