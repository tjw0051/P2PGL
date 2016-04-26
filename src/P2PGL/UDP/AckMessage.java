package P2PGL.UDP;

import P2PGL.Profile.IProfile;
import P2PGL.Util.IKey;


/**
 * Contained for data relating to acknowledgement message.
 */
public class AckMessage {

    private IPacket packet;

    /** Get Packet of message
     * @return message packet
     */
    public IPacket GetPacket() { return packet; }

    private IProfile profile;

    /** Get profile of message sender
     * @return  message sender profile
     */
    public IProfile GetProfile() { return profile; }

    private long timeSent;

    /** Get time when ack was sent
     * @return  Time sent in milliseconds
     */
    public long GetTimeSent() { return timeSent; }

    /** Set time when message was sent
     * @param timeSent  time message sent in milliseconds.
     */
    public void SetTimeSent(long timeSent) { this.timeSent = timeSent; }

    private int resends;

    /** Get number of times message has been resent.
     * @return  message resend count
     */
    public int GetResends() { return resends; }

    /**
     * Increment the resend count
     */
    public void IncrementResends() { resends++; }

    /** Store an acknowledgement message with data
     * @param packet    Packet of message
     * @param profile   Profile of sender
     */
    public AckMessage(IPacket packet, IProfile profile) {
        this.packet = packet;
        this.profile = profile;
    }
}
