package P2PGL;

import P2PGL.Connection.IHybridConnection;
import P2PGL.Profile.IProfile;

/**
 * Main class of P2PGL - Provides the factory for creating
 * concrete classes to P2PGL. To use a custom factory,
 * inject it with SetFactory;
 */
public class P2PGL {
    private static P2PGL p2pgl;

    /** Provides a singleton of P2PGL.
     * @return Singleton instance
     */
    public static P2PGL GetInstance() {
        if(p2pgl == null)
            p2pgl = new P2PGL();
        return p2pgl;
    }

    /**
     * Factory of this P2PGL instance.
     */
    private IP2PGLFactory factory;

    private P2PGL() {}

    /** Set the factory used for creating concrete classes
     * @param factory   Factory to use.
     */
    public void SetFactory(IP2PGLFactory factory) {
        this.factory = factory;
    }

    /** Get the factory used for generating concrete types
     * @return Factory - returns default P2PGLFactory
     *          if no custom factory is used.
     */
    public IP2PGLFactory GetFactory() {
        if(factory == null)
            factory = new P2PGLFactory();
        return factory;
    }

    /** Convenience function for getting a new connection from the
     *  current factory.
     * @param profile   Profile for this user
     * @return  new Hybrid Connection
     */
    public IHybridConnection GetConnection(IProfile profile) {
        return GetFactory().GetHybridConnection(profile);
    }
}
