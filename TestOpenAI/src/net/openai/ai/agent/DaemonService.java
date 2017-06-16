/*****************************************************************************
 * net.openai.ai.agent.DaemonService
 *****************************************************************************
 * @author  thornhalo
 * @date    Mon Mar  5 19:18:24 CST 2001
 * 2001 OpenAI Labs
 *****************************************************************************
 * $Log: DaemonService.java,v $
 * Revision 1.4  2002/10/15 02:35:55  thornhalo
 * Added the cvs log tags to all of the files.
 *
 *****************************************************************************/
package net.openai.ai.agent;


/**
 * This is the base class for all Services used by the Daemon.  I'm putting
 * this class here for possible future use of "public" services for the Daemon
 * that the SecurityManager will treat specially.  Right now, the Daemon won't
 * add DaemonServices to its list of Agents in order to hide them as agents.
 */
public abstract class DaemonService extends Agent {

    /** Indicates whether or not this is a "public" service or not. */
    boolean isPublic = true;

    
    /**
     * Constructs a new DaemonService that will be "public".  This is
     * equivalent to using <code>DaemonService(true)</code>.
     */
    public DaemonService() {
	this(true);
    }

    /**
     * Constructs a new DaemonService with the given "public" flag.
     *
     * @param makePublic Indicates whether or not the DaemonService will
     *                   be publicly accessible through the Daemon.getServices
     *                   method.
     * @see Daemon#getServices()
     */
    public DaemonService(boolean makePublic) {
	isPublic = makePublic;
    }
}
