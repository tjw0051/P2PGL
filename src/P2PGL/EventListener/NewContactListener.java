package P2PGL.EventListener;

import P2PGL.IKey;

/**
 * Listener for when a message has been received by an unknown contact.
 */
public interface NewContactListener {
    /**
     * @param key   Key of the unknown contact.
     */
    void NewContactListener(IKey key);
}
