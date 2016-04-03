package P2PGL.Config;

/**
 * Defines configuration for Messages requiring acknowledgement.
 */
public interface IAckMessageConfig {

    /** Returns the elapsed time before a message is resent, if an
     * acknowledgement has not been received.
     * @return  timeout
     */
    long GetTimeout();

    /** The maximum amount of times a message is resent, without receiving
     *  acknowledgement, before it is discarded.
     * @return maximum number of resends
     */
    int GetResends();
}
