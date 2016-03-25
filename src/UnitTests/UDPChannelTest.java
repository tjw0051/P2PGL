package UnitTests;

import P2PGL.Profile.IProfile;
import P2PGL.Profile.Profile;
import P2PGL.UDP.UDPChannel;
import org.junit.Test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static org.junit.Assert.*;

/**
 * Created by t_j_w on 18/03/2016.
 */
public class UDPChannelTest {

    UDPChannel udpChannel;

    @Test
    public void testListen() throws Exception {
        String msg = "hello";
        CreateUDPChannel(5000);
        udpChannel.Listen();
        //Thread.sleep(50);
        DatagramSocket serverSock = new DatagramSocket();
        //serverSock.connect(InetAddress.getLoopbackAddress(), 5000);
        byte[] message = udpChannel.SerializePacket(msg, String.class).getBytes(); //msg.getBytes();
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
    public void testSend() throws Exception {
        CreateUDPChannel(5002);
        IProfile prof = new Profile(InetAddress.getLoopbackAddress(), 5010, "channel_1");
        UDPChannel serverUDP = new UDPChannel(prof, 5010);
        serverUDP.Listen();
        //UDPChannel is profile port + 1 : profile must be set to 5009.
        Profile profile = new Profile(InetAddress.getLoopbackAddress(), 5009, "channel_0");
        udpChannel.Send(profile, "hi");
        //Thread.sleep(50);
        String message = serverUDP.ReadNext();
        long time = System.currentTimeMillis();
        while(System.currentTimeMillis() - time < 5000 && message == null) {
            udpChannel.Send(profile, "hi");
            message = serverUDP.ReadNext();
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
        udpChannel = new UDPChannel(prof, port);
    }
}