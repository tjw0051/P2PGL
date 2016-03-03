package P2PGL;

import com.google.gson.Gson;
import kademlia.dht.KadContent;
import kademlia.node.KademliaId;

/**
 * Created by t_j_w on 03/03/2016.
 */
public class Data implements KadContent {
    public static final transient String TYPE = "MessageContent";
    private KademliaId key;
    private final long createdTimestamp;
    private long lastUpdatedTimestamp;
    private String ownerId;
    private String data;

    public Data() {
        this.createdTimestamp = this.lastUpdatedTimestamp = System.currentTimeMillis() / 1000L;
    }

    public Data(String ownerId, KademliaId key, String data) {
        this.createdTimestamp = this.lastUpdatedTimestamp = System.currentTimeMillis() / 1000L;
        this.key = key;
        this.data = data;
    }

    public void setData(String data) { this.data = data; }

    public String getData() { return data; }

    @Override
    public KademliaId getKey() {
        return key;
    }

    @Override
    public String getType() {
        return TYPE;
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
