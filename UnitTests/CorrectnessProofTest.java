import P2PGL.Connection.IHybridConnection;
import P2PGL.P2PGL;
import P2PGL.Profile.IProfile;
import P2PGL.Profile.Profile;
import P2PGL.Util.IKey;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Test used to demonstrate complexity of P2PGL algorithm.
 * This is not a standard unit test, and is disabled for regular testing
 * due to long running time.
 */
public class CorrectnessProofTest {
    List<IHybridConnection> connections;

    int startPort = 40000;
    int count = 0;
    int profileRequests = 200;

    //  Test Disabled for regular unit and integration testing
    //@Test
    public void DHTGetPerformanceTest() {
        long total = 0;

        int[] tests = { 5, 10, 15, 20, 25, 30 };
        for(int t = 50; t < 100; t++) {
            count = t;
            total = GetProfilesTest();
            long avg = total / profileRequests;
            System.out.println(avg);
        }
    }

    public long GetProfilesTest() {
        connections = new ArrayList<>();

        Random rand = new Random();

        IHybridConnection  bootstrapNode = CreateConnection(Integer.toString(startPort), startPort);
        try {
            bootstrapNode.Connect();
        } catch (IOException ioe) {
            fail("Error connecting bootstrap node");
        }

        for(int i = 1; i < count; i++) {
            int port = startPort + i;
            IHybridConnection conn = CreateConnection(Integer.toString(port), port);
            try {
                conn.Connect(Integer.toString(startPort), InetAddress.getLoopbackAddress(), startPort);
                //System.out.println("Node: " + port + " connected.");
                connections.add(conn);
            } catch (IOException ioe) {
                //i -= 1;
                fail("Error connecting node: " + i);
            }
        }

        long total = 0;
        IKey bootstrapKey = bootstrapNode.GetKey();
        for(int i = 0; i < profileRequests; i++) {
            try {
                //Get a random profile number to retreive that is not the current
                //connection's profile
                int getter = rand.nextInt(connections.size());
                int profNumber = getter;
                while(profNumber == getter) {
                    profNumber = rand.nextInt(connections.size());
                }
                long startTime, endTime = 0;
                IProfile prof;
                IHybridConnection getterConn = connections.get(getter);
                IKey keyToGet = connections.get(profNumber).GetKey();

                startTime = System.nanoTime();
                prof = getterConn.GetProfile(keyToGet);
                while(prof == null) {}
                endTime = System.nanoTime();

                long timeTaken = (endTime - startTime);
                total += timeTaken;
            } catch (IOException ioe) {
                fail("Error retrieving profile");
                ioe.printStackTrace();
            }
        }

        try {
            bootstrapNode.Disconnect();
        } catch (IOException ioe) {
            fail("Error disconnecting bootstrap node");
        }
        for(IHybridConnection conn : connections) {
            try {
                conn.Disconnect();
            } catch (IOException ioe) {
                fail("Error disconnecting node");
            }
        }
        return total;
    }

    /** Create a hybrid connection using name and port for profile
     * @param name  Name of connection
     * @param port  Port for connection
     * @return
     */
    public IHybridConnection CreateConnection(String name, int port) {
        IProfile profile = new Profile(InetAddress.getLoopbackAddress(), port, name);
        return P2PGL.GetInstance().GetFactory().GetHybridConnection(profile);

    }
}
