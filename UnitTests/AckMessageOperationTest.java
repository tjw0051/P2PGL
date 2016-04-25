import P2PGL.EventListener.MessageReceivedListener;
import P2PGL.EventListener.NewContactListener;
import P2PGL.Profile.IProfile;
import P2PGL.UDP.*;
import P2PGL.Util.IKey;
import P2PGL.Util.Key;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by t_j_w on 17/04/2016.
 */
public class AckMessageOperationTest {

    @Test
    public void testRun() throws Exception {
        ILocalChannel testchan = new TestChannel();
        AckMessageOperation ackOper = new AckMessageOperation(testchan);
        try {
            ackOper.run();
        } catch (Exception e) {
            fail("exception thrown running ack operation");
        }
    }

    @Test
    public void testAdd() throws Exception {
        TestChannel channel = new TestChannel();
        AckMessageOperation oper = new AckMessageOperation(channel);
        oper.Add(null, null);
        while(channel.sendCalled == 0) {}
        assertTrue(channel.sendCalled != 0);
        oper.Stop();
    }

    @Test
    public void testAckReceivedValid() throws Exception {
        TestChannel channel = new TestChannel();
        AckMessageOperation oper = new AckMessageOperation(channel);
        IPacket packet = new UDPPacket("hello", "String", new Key(), "channel");
        IKey ackKey = new Key();
        packet.SetAckKey(ackKey);

        oper.AckReceived(ackKey);
        oper.Add(packet, null);

        Thread.sleep(100);
        if(channel.sendCalled != 0)
            fail("Message was resent after acknowledgement was received");
    }

    @Test
    public void testAckReceivedInvalid() throws Exception {
        TestChannel channel = new TestChannel();
        AckMessageOperation oper = new AckMessageOperation(channel);
        IPacket packet = new UDPPacket("hello", "String", new Key(), "channel");
        IKey ackKey = new Key();
        packet.SetAckKey(ackKey);

        oper.Add(packet, null);
        while(true) {
            if(channel.sendCalled == 1) {
                System.out.println("First message sent");
            }
            if(channel.sendCalled == 2) {
                System.out.println("Message Sent twice");
            }
            if(channel.sendCalled == 3) {
                System.out.println("Message Sent three times");
                assertEquals(3, channel.sendCalled);
                break;
            }
        }
    }

    @Test
    public void testIsRunning() throws Exception {
        TestChannel channel = new TestChannel();
        AckMessageOperation oper = new AckMessageOperation(channel);
        oper.Add(null, null);
        assertTrue(oper.isRunning());
    }

    @Test
    public void testStop() throws Exception {
        TestChannel channel = new TestChannel();
        AckMessageOperation oper = new AckMessageOperation(channel);
        oper.Add(null, null);
        oper.Stop();
        assertFalse(oper.isRunning());
    }

    /**********************************************************
     *
     *             Dependencies for Tests
     *
     *********************************************************/

    public class TestChannel implements ILocalChannel {

        public int sendCalled = 0;

        public TestChannel() {}

        @Override
        public void Broadcast(Object obj, Type type) throws IOException {

        }

        @Override
        public void BroadcastAck(Object obj, Type type) throws IOException {

        }

        @Override
        public void Send(IProfile profile, Object obj, Type type) throws IOException {

        }

        @Override
        public void Send(IProfile profile, IPacket udpPacket) throws IOException {
            sendCalled++;
            //sendCalled.add(new Key());
        }

        @Override
        public void SendAck(IProfile profile, Object obj, Type type) throws IOException {

        }

        @Override
        public void AddContactListener(NewContactListener listener) {

        }

        @Override
        public void RemoveContactListener(NewContactListener listener) {

        }

        @Override
        public void AddMessageListener(MessageReceivedListener listener) {

        }

        @Override
        public void RemoveMessageListener(MessageReceivedListener listener) {

        }

        @Override
        public void Stop() {

        }

        @Override
        public void Add(IProfile profile) {

        }

        @Override
        public void Remove(IKey key) {

        }

        @Override
        public void SetProfile(IProfile profile) {

        }

        @Override
        public void Listen() {

        }

        @Override
        public void ClearQueue() {

        }

        @Override
        public void ClearContacts() {

        }

        @Override
        public <T> T ReadNext() throws ClassNotFoundException {
            return null;
        }

        @Override
        public <T> T PeekNext() throws ClassNotFoundException {
            return null;
        }

        @Override
        public boolean isConnected() {
            return false;
        }
    }
}