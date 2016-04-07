package P2PGL.Config;

import kademlia.KadConfiguration;


import java.io.File;

/**
 * Configuration for Kademlia.
 * Javadoc from KadConfiguration Interface.
 */
public class KademliaConfig implements KadConfiguration {

    private final long restoreInterval;
    private final long responseTimeout;
    private final long operationTimeout;
    private final int maxConcurrentMessages;
    private final int k;
    private final int replacementCacheSize;
    private final int stale;

    public KademliaConfig(long restoreInterval, long responseTimeout,
                          long operationTimeout, int maxConcurrentMessages,
                          int k, int replacementCacheSize, int stale) {
        this.restoreInterval = restoreInterval;
        this.responseTimeout = responseTimeout;
        this.operationTimeout = operationTimeout;
        this.maxConcurrentMessages = maxConcurrentMessages;
        this.k = k;
        this.replacementCacheSize = replacementCacheSize;
        this.stale = stale;
    }

    /**
     * @return Interval in milliseconds between execution of RestoreOperations.
     */
    @Override
    public long restoreInterval() {
        return this.restoreInterval;
    }

    /**
     * If no reply received from a node in this period (in milliseconds)
     * consider the node unresponsive.
     *
     * @return The time it takes to consider a node unresponsive
     */
    @Override
    public long responseTimeout() {
        return this.responseTimeout;
    }

    /**
     * @return Maximum number of milliseconds for performing an operation.
     */
    @Override
    public long operationTimeout() {
        return this.operationTimeout;
    }

    /**
     * @return Maximum number of concurrent messages in transit.
     */
    @Override
    public int maxConcurrentMessagesTransiting() {
        return this.maxConcurrentMessages;
    }

    /**
     * @return K-Value used throughout Kademlia
     */
    @Override
    public int k() {
        return this.k;
    }

    /**
     * @return Size of replacement cache.
     */
    @Override
    public int replacementCacheSize() {
        return this.replacementCacheSize;
    }

    /**
     * @return # of times a node can be marked as stale before it is actually removed.
     */
    @Override
    public int stale() {
        return this.stale;
    }

    /**
     * Creates the folder in which this node data is to be stored.
     *  - Currently using same method as DefaultConfiguration
     * @return The folder path
     */
    @Override
    public String getNodeDataFolder(String ownerId) {
        String path = System.getProperty("user.home") + File.separator + "kademlia";
        File folder = new File(path);
        if(!folder.isDirectory()) {
            folder.mkdir();
        }
        File ownerFolder = new File(folder + File.separator + ownerId);
        if(!ownerFolder.isDirectory()) {
            ownerFolder.mkdir();
        }
        return ownerFolder.toString();
    }

    /**
     * @return Whether we're in a testing or production system.
     */
    @Override
    public boolean isTesting() {
        return false;
    }


}
