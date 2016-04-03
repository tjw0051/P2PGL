package P2PGL.Config;

/**
 * Created by t_j_w on 03/04/2016.
 */
public class AckMessageConfig implements IAckMessageConfig{
    private static final long timeout = 200L;
    private static final int resends = 3;

    public AckMessageConfig() {}

    /** Returns the elapsed time before a message is resent, if an
     * acknowledgement has not been received.
     * @return  timeout
     */
    public long GetTimeout() { return timeout; }

    /** The maximum amount of times a message is resent, without receiving
     *  acknowledgement, before it is discarded.
     * @return maximum number of resends
     */
    public int GetResends() { return resends; }
}
