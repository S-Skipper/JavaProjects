/*****************************************************************************
 * net.openai.ai.agent.DaemonShutdown
 *****************************************************************************
 * @author  Thornhalo
 * 2001 OpenAI Labs
 *****************************************************************************
 * $Log: DaemonShutdown.java,v $
 * Revision 1.2  2002/10/15 02:35:55  thornhalo
 * Added the cvs log tags to all of the files.
 *
 *****************************************************************************/
package net.openai.ai.agent;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * A class that will handle the cleanup of shutting down the Daemon.
 */
final class DaemonShutdown extends Thread {

    /** The Daemon to shutdown. */
    private Daemon daemon;

    
    /**
     * Constructs a new DaemonShutdown.
     *
     * @param daemon The Daemon to shutdown.
     */
    DaemonShutdown(Daemon daemon) {
	if(daemon == null)
	    throw new NullPointerException("Null Daemon");
	this.daemon = daemon;
    }

    /**
     * Runs the shutdown sequence.
     */
    public final void run() {
	daemon.shutdown();
    }
}
