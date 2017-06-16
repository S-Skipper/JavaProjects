/*****************************************************************************
 * net.openai.ai.agent.DaemonSocket
 *****************************************************************************
 * @author  Thornhalo
 * 2001 OpenAI Labs
 *****************************************************************************
 * $Log: DaemonServer.java,v $
 * Revision 1.6  2002/10/15 02:35:55  thornhalo
 * Added the cvs log tags to all of the files.
 *
 *****************************************************************************/
package net.openai.ai.agent;

import java.io.*;
import java.net.*;
import java.util.*;


/**
 * This is a wrapper around a ServerSocket.  This is implemented as an Agent
 * because of the Garbage Collecting capabilities built into the AgentThread.
 */
final class DaemonServer extends DaemonService {
    
    /** A handle to the parent Daemon. */
    private Daemon daemon = null;

    /** The ServerSocket. */
    ServerSocket serverSocket = null;

    /** The port. */
    private int port;


    /**
     * Constructs a new DaemonServer for the given port.
     */
    DaemonServer(Daemon daemon, int port) throws IOException {
	super(false);
	if(daemon == null)
	    throw new NullPointerException("Null Daemon");
	this.daemon = daemon;
	this.port = port;
	serverSocket = new ServerSocket(port);
    }

    /**
     * The main body of the DaemonServer.
     */
    protected final void executeAgent() throws Exception {
	try {
	    while(true) {
		Socket socket = serverSocket.accept();
		daemon.addConnection(new DaemonSocket(daemon, socket));
	    }
	} catch(SocketException se) {
	    displayMessage(se.toString());
	}
    }

    /**
     * Returns a description of the service.
     */
    public final String getShortDescription() {
	return "ConnectionListener on " + port;
    }

    /**
     * Returns a description of the service.
     */
    public final String getAgentDescription() {
	return getShortDescription();
    }
}
