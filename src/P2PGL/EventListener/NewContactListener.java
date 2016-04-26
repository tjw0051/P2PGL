package P2PGL.EventListener;

import P2PGL.Util.IKey;

/**
 * Listener for when a message has been received by an unknown contact.
 */
public interface NewContactListener {
    /** Triggered when Channel receives a message from an unknown contact.
     * @param key   Key of the unknown contact.
     */
    void NewContactListener(IKey key);
}
