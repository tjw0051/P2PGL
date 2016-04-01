package UnitTests;

import P2PGL.Util.Key;
import P2PGL.Profile.IProfile;
import P2PGL.Profile.Profile;
import P2PGL.UDP.UDPChannel;
import org.junit.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static org.junit.Assert.*;

/**
 * Created by t_j_w on 18/03/2016.
 */
public class UDPChannelTest extends UDPChannel{

    UDPChannel udpChannel;

    public UDPChannelTest(IProfile profile) {
        super(profile);
    }

    @Test
    public void testListen() throws Exception {
        String msg = "hello";
        CreateUDPChannel(5000);
        udpChannel.Listen("channel");
        //Thread.sleep(50);
        DatagramSocket serverSock = new DatagramSocket();
        //serverSock.connect(InetAddress.getLoopbackAddress(), 5000);
        byte[] message = super.SerializePacket(msg, String.class, new Key(), "channel").getBytes(); //msg.getBytes();
        DatagramPacket packet = new DatagramPacket(message, message.length, InetAddress.getLoopbackAddress(), 5000);
        serverSock.send(packet);
        long time = System.currentTimeMillis();
        String queueItem = udpChannel.ReadNext();
        while(System.currentTimeMillis() - time < 5000 && queueItem == null) {
            serverSock.send(packet);
            queueItem = udpChannel.ReadNext();
        }
        //boolean answ = queueItem.equals(msg);
        assertTrue(queueItem.equals(msg));
    }

    @Test
    public void testAddListener() throws Exception {

    }

    @Test
    public void testAdd() throws Exception {

    }

    @Test
    public void testSend() {
        CreateUDPChannel(5002);
        IProfile serverProfile = new Profile(InetAddress.getLoopbackAddress(), 5010, "channel_1");
        UDPChannel serverUDP = new UDPChannel(serverProfile, 5010);
        serverUDP.Listen("channel");
        //UDPChannel is profile port + 1 : profile must be set to 5009.
        Profile profile = new Profile(InetAddress.getLoopbackAddress(), 5009, "channel_0");
        try {
            udpChannel.Send(profile, "hi", String.class);
        } catch (IOException ioe) {
            fail("Error sending message");
        }
        //Thread.sleep(50);
        String message = "";
        try {
            message = serverUDP.ReadNext();
        } catch (ClassNotFoundException cnfe) {
            fail("Error deserializing message");
        }
        long time = System.currentTimeMillis();
        while(System.currentTimeMillis() - time < 5000 && message == null) {
            try {
                udpChannel.Send(profile, "hi", String.class);
                message = serverUDP.ReadNext();
            } catch (IOException ioe) {
                fail("Error sending message");
            } catch (ClassNotFoundException cnfe) {
                fail("Error deserializing message");
            }
        }
        assertTrue(message.equals("hi"));
    }

    @Test
    public void testReadNext() throws Exception {

    }

    @Test
    public void testPeekNext() throws Exception {

    }

    private void CreateUDPChannel(int port) {
        IProfile prof = new Profile(InetAddress.getLoopbackAddress(), 5010, "channel_0");
        prof.SetLocalChannel("channel");
        udpChannel = new UDPChannel(prof, port);
    }
}