package P2PGL;

import java.lang.reflect.Type;
import java.net.InetAddress;

/**
 * Created by Tom.
 */
public interface IProfile {
    /**
     * @return  InetAddress Get IP address of profile owner.
     */
    InetAddress GetIPAddress();

    /**
     * @return  int    Port of profile owner.
     */
    int GetPort();

    /**
     * @return  String  Get Key of profile owner.
     */
    String GetKey();

    /**
     * @return  Type    Must return type of profile (e.g. Profile.class)
     */
    Type GetType();
}
