package P2PGL;

import P2PGL.DHT.IDHTFacade;
import P2PGL.DHT.KademliaFacade;
import P2PGL.EventListener.MessageReceivedListener;
import P2PGL.EventListener.NewContactListener;
import P2PGL.Profile.IProfile;
import P2PGL.Profile.Profile;
import P2PGL.Profile.ProfileCache;
import P2PGL.UDP.UDPChannel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import kademlia.dht.DHT;
import kademlia.node.KademliaId;


import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;


/**
 * Main Class of P2PGL.
 * @author Thomas Walker
 */
public class Connection implements NewContactListener, MessageReceivedListener{
    private IProfile profile;
    //private JKademliaNode node;
    private Gson gson;
    private IDHTFacade dht;
    private IKey key;
    private UDPChannel udpChannel;
    private List<MessageReceivedListener> messageReceivedListeners;

    /**
     * Instantiate a new connection to a network.
     * @param profile   Connection parameters for peer (e.g. port, IP Address, name)
     */
    public Connection(IProfile profile, IDHTFacade dhtImplementation) {
        this.profile = profile;
        GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(IKey.class, new InterfaceAdapter<IKey>());
        gson = gsonBuilder.create();
        dht = dhtImplementation;
        dht.SetProfile(profile);

        //Create UDP
        messageReceivedListeners = new ArrayList<>();
        udpChannel = new UDPChannel(profile, profile.GetUDPPort());
        udpChannel.AddContactListener(this);
        udpChannel.addMessageListener(this);
    }

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

    public boolean isConnected() {
        return dht.isConnected();
    }

    //TODO: Remove profile and other pieces of player.
    public void Disconnect() throws IOException {
        try {
            dht.Disconnect();
            udpChannel.Stop();
        } catch(IOException ioe) {
            ioe.printStackTrace();
            throw ioe;
        }
    }

    public void StartUDPChannel(String channelName) throws IOException{
        //Clear current contacts and incoming messages
        udpChannel.Stop();
        udpChannel.ClearContacts();
        udpChannel.ClearQueue();

        //Update profile with new channel
        profile.SetUDPChannel(channelName);
        StoreProfile();

        //Add contacts from new channel
        List<IProfile> profiles = GetProfiles(ListUsers());
        for(IProfile profile : profiles) {
            if(profile.GetUDPChannel().equals(channelName))
                udpChannel.Add(profile);
        }
        udpChannel.Listen(channelName);
    }

    public void Broadcast(Object obj, Type type) throws IOException{
        udpChannel.Broadcast(obj, type);
    }

    public void Broadcast(String message) throws IOException{
        udpChannel.Broadcast(message);
    }

    @Nullable
    public String Get(IKey key) throws IOException {
        return dht.Get(key);
    }

    /**
     * Store string data at destination name on DHT.
     * @param destKey       name to store data at.
     * @param stringData    String data to store.
     * @throws IOException  Thrown when a put operation cannot be performed (check connection).
     */

    public void Store(IKey destKey, String stringData) throws IOException{
        //Data data = new Data(node.getNode().getNodeId().toString(), new KademliaId(Key.Format(destKey)), stringData, "String");
        //Store(data);
        dht.Store(destKey, stringData);
    }

     /**
     * Store user profile on DHT. Destination name of profile is
     * this nodeId with last byte incremented by 1.
     * @return  Key profile is stored at.
     * @throws IOException
     */
    public IKey StoreProfile() throws IOException {
        //TODO: incremennt Key
        IKey profileKey = profile.GetKey().Next();
        dht.Store(profileKey, gson.toJson(profile, profile.GetType()));
        return profileKey;
    }

    //TODO: return list
    public IKey[] ListUsers() {
        return dht.ListUsers();
    }



    public IKey GetId() {
        return dht.GetId();
        //return new Key(node.getNode().getNodeId());
    }

    /**
     * @return Profile of this node.
     */
    public IProfile GetLocalProfile() {
        return profile;
    }

    /**
     * Retrieve the profile of user with KademliaId userKey.
     * @see Connection#StoreProfile()
     * @param userKey   KademliaId of user
     * @return  Profile of user. Returns null if profile cannot be found.
     * @throws IOException  Thrown if a get operation cannot be performed (check connection).
     */
    @Nullable
    public IProfile GetProfile(IKey userKey) throws IOException {
        //KademliaId profileKey = IncrementKey(userKey.kademliaId);
        //String profileJson = Get(profileKey);
        String profileJson = Get(userKey.Next());
        if(profileJson == null)
            return null;
        return gson.fromJson(profileJson, profile.GetType());
    }

    public List<IProfile> GetProfiles(IKey[] keys) throws IOException{
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

    public static void main(String[] args) { }

    @Override
    public void NewContactListener(IKey key) {
        try {
            udpChannel.Add(GetProfile(key));
        } catch (IOException ioe) {

        }
    }

    public void AddMessageListener(MessageReceivedListener messageReceivedListener) {
        messageReceivedListeners.add(messageReceivedListener);
    }

    @Override
    public void MessageReceivedListener(String messageType, IKey sender) {
        for(MessageReceivedListener listener : messageReceivedListeners)
            listener.MessageReceivedListener(messageType, sender);
    }

    public UDPChannel GetUDPChannel() { return udpChannel; }
}
