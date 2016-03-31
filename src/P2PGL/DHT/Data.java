package P2PGL.DHT;

import com.google.gson.Gson;
import kademlia.dht.KadContent;
import kademlia.node.KademliaId;


/**
 *  Implementation of KadContent from Kademlia for storing data on the DHT.
 *  @see kademlia.dht.KadContent
 */
public class Data implements KadContent {
    private String type = "Data";
    private KademliaId key;
    private final long createdTimestamp;
    private long lastUpdatedTimestamp;
    private String ownerId;
    private String data;

    public Data() {
        this.createdTimestamp = this.lastUpdatedTimestamp = System.currentTimeMillis() / 1000L;
    }

    public Data(String ownerId, KademliaId key, String data, String type) {
        this.createdTimestamp = this.lastUpdatedTimestamp = System.currentTimeMillis() / 1000L;
        this.key = key;
        this.data = data;
        this.type = type;
    }

    public void setData(String data) { this.data = data; }

    public String getData() { return data; }

    @Override
    public KademliaId getKey() {
        return key;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    @Override
    public long getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    @Override
    public String getOwnerId() {
        return ownerId;
    }

    @Override
    public byte[] toSerializedForm() {
        Gson gson = new Gson();
        return gson.toJson(this).getBytes();
    }

    @Override
    public Data fromSerializedForm(byte[] bytes) {
        Gson gson = new Gson();
        Data val = (Data) gson.fromJson(new String(bytes), Data.class);
        return val;
    }
}
