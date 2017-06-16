/*****************************************************************************
 * net.openai.ai.agent.MobileAgent
 *****************************************************************************
 * @author  thornhalo
 * @date    Mon Mar  5 19:18:24 CST 2001
 * 2001 OpenAI Labs
 *****************************************************************************
 * $Log: MobileAgent.java,v $
 * Revision 1.5  2002/10/15 02:35:55  thornhalo
 * Added the cvs log tags to all of the files.
 *
 *****************************************************************************/
package net.openai.ai.agent;

import java.io.*;
import java.net.*;


/**
 * This is the base class for all Mobile Agents.  In order to utilize the
 * mobility of the Agent, you must make sure that all data that is not
 * transient is able to be Serialized.  See the Java SDK Javadocs for more
 * information on Serialization (java.io.Serializable).
 */
public abstract class MobileAgent extends Agent implements Serializable {

    /**
     * Constructs a new MobileAgent.
     */
    public MobileAgent() {
    }

    /**
     * Causes the Agent to migrate from one machine to another.  This can only
     * be called from within a MobileAgent's execution.  If the migration is
     * successful, the agent's execution is immediately halted.
     *
     * @param host The destination host.
     * @param port The port on the destination host to enter through.
     * @throws IOException If an IOException has occurred during the
     *                     transmission.
     * @throws UnknownHostException If the host name is not known.
     */
    protected final void migrateTo(String host, int port)
	throws IOException, UnknownHostException {
	try {
	    daemon.transmitAgent(this, host, port);
	} catch(IOException ioe) {
	    // This is a bit of load-balancing to make sure that you don't
	    // hammer the Daemon trying repeatedly to migrate.
	    try {
		Thread.sleep(5);
	    } catch(InterruptedException ie) {
	    }
	    throw ioe;
	}
	halt();
    }

    /**
     * Causes the Agent to migrate from one machine to another.  This can only
     * be called from within a MobileAgent's execution.  If the migration is
     * successful, the agent's execution is immediately halted.
     *
     * @param host The destination host.
     * @param port The port on the destination host to enter through.
     * @throws IOException If an IOException has occurred during the
     *                     transmission.
     */
    protected final void migrateTo(InetAddress host, int port)
	throws IOException {
	try {
	    daemon.transmitAgent(this, host, port);
	} catch(IOException ioe) {
	    // This is a bit of load-balancing to make sure that you don't
	    // hammer the Daemon trying repeatedly to migrate.
	    try {
		Thread.sleep(5);
	    } catch(InterruptedException ie) {
	    }
	    throw ioe;
	}
	halt();
    }
}
