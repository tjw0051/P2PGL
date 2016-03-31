package P2PGL.Exceptions;

/**
 *
 */
public class ContentNotFoundException extends Exception {
        public ContentNotFoundException() {
        }

    /** Thrown when data cannot be found on DHT.
     * @param message Exception message.
     */
        public ContentNotFoundException(String message) {
            super(message);
        }
}
