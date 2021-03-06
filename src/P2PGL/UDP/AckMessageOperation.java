package P2PGL.UDP;

import P2PGL.Config.IAckMessageConfig;
import P2PGL.P2PGL;
import P2PGL.Profile.IProfile;
import P2PGL.Util.IKey;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Operation for processing packets requiring acknowledgement of receipt.
 */
public class AckMessageOperation implements Runnable {
    private final long timeout;
    private final int maxResends;

    protected ILocalChannel localChannel;

    protected List<AckMessage> packets;
    protected List<AckMessage> newPackets;

    protected List<IKey> ackKeys;
    protected List<IKey> newAckKeys;

    private boolean running;

    /** Create Ack Message Operation for processing acknowledgement messages.
     * @param localChannel  Channel to send messages on.
     */
    public AckMessageOperation(ILocalChannel localChannel) {
        IAckMessageConfig config = P2PGL.GetInstance().GetFactory().GetAckConfig();
        this.timeout = config.GetTimeout();
        this.maxResends = config.GetResends();
        packets = new ArrayList<>();
        newPackets = new ArrayList<>();
        ackKeys = new ArrayList<>();
        newAckKeys = new ArrayList<>();
        this.localChannel = localChannel;
        running = false;
    }

    /**
     * Iterates through processing acknowledgement messages and packet
     * messages.
     */
    public void run() {
        while(running == true && (!packets.isEmpty() || !newPackets.isEmpty())) {
            try {
                ProcessPackets();
            } catch (IOException ioe) {}
        }
        if(!running) {
            packets.clear();
            newPackets.clear();
            ackKeys.clear();
            newAckKeys.clear();
        }
    }

    /** Add a packet to the queue for outgoing messages
     * @param packet
     * @param profile
     */
    public void Add(IPacket packet, IProfile profile) {
        newPackets.add(new AckMessage(packet, profile));
        if(!running) {
            running = true;
            run();
        }
    }

    /** Adds a new acknowledgement to the queue
     * @param key   Acknowledgement key.
     */
    public void AckReceived(IKey key) {
        newAckKeys.add(key);
    }

    /**
     * Iterates through packets:
     * -    If an acknowledgement has been received for a packet, it
     *      is removed from the queue and not resent.
     * -    If a packet has not been sent it is given a starting time.
     *
     * -    If the packet has been resent the maximum number of times,
     *      it is removed from the queue and not resent.
     *
     * -    If a packet has not been acknowledged within the timeout time,
     *      it is resent, its resend count is increased, and its time sent
     *      is set to the current time.
     */
    protected void ProcessPackets() throws IOException{
        long currentTime;

        /*  Add new packets and new acknowledgements to main queue  */
        if(!newPackets.isEmpty()) {
            packets.addAll(newPackets);
            newPackets.clear();
        }
        if(!newAckKeys.isEmpty())
            ackKeys.addAll(newAckKeys);

        Iterator iter = packets.iterator();
        while(iter.hasNext()) {
            AckMessage message = (AckMessage) iter.next();

            /*  Process Ack Messages  */

            boolean found = false;
            Iterator keyIter = ackKeys.iterator();
            while (keyIter.hasNext() && found == false) {
                IKey ackKey = (IKey) keyIter.next();
                if(ackKey.equals(message.GetPacket().GetAckKey())) {
                    found = true;
                    keyIter.remove();
                }
            }
            if(found)
                iter.remove();
            else {

            /*  Process Packets */

                currentTime = System.currentTimeMillis();
                if (message.GetTimeSent() == 0L)
                    message.SetTimeSent(currentTime);

                if (message.GetResends() >= maxResends) {
                    iter.remove();
                } else if ((currentTime - message.GetTimeSent()) > timeout) {
                    localChannel.Send(message.GetProfile(), message.GetPacket());
                    message.SetTimeSent(currentTime);
                    message.IncrementResends();
                }
            }
        }
    }

    /** Check if acknowledgement operation is running.
     * @return  operation is running.
     */
    public boolean isRunning() { return running; }

    /**
     * Stop the acknowledgement operation.
     */
    public void Stop() { running = false; }

}
