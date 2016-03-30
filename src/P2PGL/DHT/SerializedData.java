package P2PGL.DHT;

/**
 * Created by t_j_w on 30/03/2016.
 */
public class SerializedData implements ISerializedData{
    public String data;
    public String type;

    public SerializedData(String data, String type) {
        this.data = data;
        this.type = type;
    }

    public String GetData() { return data; }
    public String GetType() { return type; }
}
