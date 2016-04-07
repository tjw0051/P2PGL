package P2PGL.UDP;

import P2PGL.Profile.IProfile;
import P2PGL.Util.IKey;

/**
 * Created by t_j_w on 02/04/2016.
 */
public class AckMessage {

    private IPacket packet;
    public IPacket GetPacket() { return packet; }

    private IProfile profile;
    public IProfile GetProfile() { return profile; }

    private long timeSent;
    public long GetTimeSent() { return timeSent; }
    public void SetTimeSent(long timeSent) { this.timeSent = timeSent; }

    private int resends;
    public int GetResends() { return resends; }
    public void IncrementResends() { resends++; }

    public AckMessage(IPacket packet, IProfile profile) {
        this.packet = packet;
        this.profile = profile;
    }
}
