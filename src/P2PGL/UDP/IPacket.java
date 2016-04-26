package P2PGL.UDP;

import P2PGL.Util.IKey;


/**
 * IPacket for sending data across local channel
 */
public interface IPacket {
    /** Get JSON message in packet.
     * @return  JSON message
     */
    String GetMessage();

    /** Get deserialized type of JSON message
     * @return  deserialized type of message
     */
    String GetType();

    /** Get sender of packet
     * @return sender
     */
    IKey GetSender();

    /** Get channel name of packet owner.
     * @return  channel name
     */
    String GetChannel();

    /** Assign a unique acknowledgemnt key, flagging packet
     *  as requiring acknowledgement from recipient.
     * @param key   unique acknowledgement key.
     */
    void SetAckKey(IKey key);

    /** Unique acknowledgement key
     * @return Ack key
     */
    IKey GetAckKey();
}
