package P2PGL.UDP;

import P2PGL.Profile.IProfile;
import P2PGL.Util.IKey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Operation for processing packets requiring acknowledgement of receipt.
 */
public class AckMessageOperation implements Runnable {
    private long timeout = 200L; // 200 milliseconds
    private int maxResends = 3;

    private UDPChannel udpChannel;

    private List<AckMessage> packets;
    private List<AckMessage> newPackets;

    private List<IKey> ackKeys;
    private List<IKey> newAckKeys;

    private boolean running;

    public AckMessageOperation(UDPChannel udpChannel) {
        packets = new ArrayList<>();
        newPackets = new ArrayList<>();
        ackKeys = new ArrayList<>();
        newAckKeys = new ArrayList<>();
        this.udpChannel = udpChannel;
        running = false;
    }

    public void run() {
        while(running == true && (!packets.isEmpty() || !newPackets.isEmpty())) {
            ProcessAcks();
            try {
                ProcessPackets();
            } catch (IOException ioe) {
                //TODO: handle - error sending at udp.send
            }
        }
    }

    public void Add(UDPPacket packet, IProfile profile) {
        newPackets.add(new AckMessage(packet, profile));
        if(!running) {
            running = true;
            run();
        }
    }

    /**
     * @param key   Acknowledgement key.
     */
    public void AckReceived(IKey key) {
        newAckKeys.add(key);
    }

    /**
     * Checks if an acknowledgement has been received for packets sent.
     * If a packet has been acknowledged, it is removed from the queue
     * and not resent.
     */
    protected void ProcessAcks() {
        if(!newAckKeys.isEmpty())
            ackKeys.addAll(newAckKeys);

        Iterator keyIter = ackKeys.iterator();
        while(keyIter.hasNext()) {
            boolean found = false;
            IKey ackKey = (IKey) keyIter.next();
            Iterator messageIter = packets.iterator();
            while(messageIter.hasNext()) {
                IKey messageKey = ((AckMessage) messageIter.next()).GetPacket().GetAckKey();
                if(ackKey.Equals(messageKey)) {
                    found = true;
                    messageIter.remove();
                    break;
                }
            }
            if(found)
                keyIter.remove();
        }
    }

    /**
     * Iterates through packets:
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
        long currentTime = System.currentTimeMillis();
        if(!newPackets.isEmpty()) {
            packets.addAll(newPackets);
            newPackets.clear();
        }
        Iterator iter = packets.iterator();
        while(iter.hasNext()) {
            AckMessage message = (AckMessage) iter.next();
            currentTime = System.currentTimeMillis();

            if(message.GetTimeSent() == 0L)
                message.SetTimeSent(currentTime);

            if(message.GetResends() >= maxResends) {
                //Trigger event or exception
                iter.remove();
            }
            else if(currentTime - message.GetTimeSent() > timeout) {
                    udpChannel.Send(message.GetProfile(), message.GetPacket());
                    message.SetTimeSent(currentTime);
                    message.IncrementResends();
            }
        }
    }

    public boolean isRunning() { return running; }

    public void Stop() { running = false; }

}
