/*****************************************************************************
 * net.openai.ai.agent.DaemonSocket
 *****************************************************************************
 * @author  Thornhalo
 * 2001 OpenAI Labs
 *****************************************************************************
 * $Log: DaemonSocket.java,v $
 * Revision 1.14  2002/10/24 03:50:53  thornhalo
 * Implemented the return to sender if the remote class loader is not enabled.
 *
 * Revision 1.13  2002/10/15 02:35:55  thornhalo
 * Added the cvs log tags to all of the files.
 *
 *****************************************************************************/
package net.openai.ai.agent;

import java.io.*;
import java.net.*;
import java.util.*;


/**
 * This is a Socket-based connection to another machine.  This is implemented
 * as a DaemonService because of the Garbage Collecting capabilities built into
 * the AgentThread and Daemon.
 */
final class DaemonSocket extends DaemonService {

    /** A handle to the parent Daemon. */
    private Daemon daemon;

    /** The Socket that is connected to another machine. */
    Socket socket;

    /** The output stream. */
    private ObjectOutputStream outputStream;

    /** The input stream. */
    private ObjectInputStream inputStream;

    /** The default timeout in milliseconds. */
    private static final int TIMEOUT = 1000 * 6000;

    /** The hostname. */
    String hostName;

    /** The port. */
    int port;

    /** A counter for determining when to reset the output stream. */
    int resetCount = 0;


    /**
     * Constructs a new DaemonSocket for the given Socket.
     *
     * @param daemon The Daemon whom we're working for.
     * @param socket The socket for the connection.
     */
    DaemonSocket(Daemon daemon, Socket socket) {
	super(false);
	if(daemon == null)
	    throw new NullPointerException("Null Daemon");
	if(socket == null)
	    throw new NullPointerException("Null Socket");
	this.daemon = daemon;
	this.socket = socket;
	this.hostName = socket.getInetAddress().getHostName();
	this.port = socket.getPort();
	try {
	    socket.setSoTimeout(TIMEOUT);
	} catch(SocketException se) {
	    se.printStackTrace();
	}
    }

    /**
     * Runs this connection.
     */
    protected final void executeSocket() {
	try {
	    transmitDescription(daemon.getDescription());
	    //DSInputStream ds = new DSInputStream(socket.getInputStream());
	    inputStream = new ObjectInputStream(socket.getInputStream());
	    while(true) {
		/*
		  // This appears to have no effect on "resetting" the timeout,
		  // so I'm just gonna cut it out for right now.
		try {
		    socket.setSoTimeout(TIMEOUT);
		} catch(SocketException se) {
		    se.printStackTrace();
		}
		*/
		try {
		    Object o = inputStream.readObject();
		    if(o instanceof AgentGram) {
			daemon.receiveAgentGram((AgentGram)o, this);
		    } else if(o instanceof DaemonDescription) {
			daemon.receiveDescription((DaemonDescription)o, this);
		    } else {
			String msg = "Ignoring object (" +
			    o.getClass().getName() + ") from " + hostName;
			daemon.displayMessage(msg);
		    }
		} catch(ClassNotFoundException cnfe) {
		    daemon.displayMessage("Class not found: " + cnfe);
		} catch(OptionalDataException ode) {
		    daemon.displayMessage("Not object: " + ode);
		}
	    }
	} catch(InterruptedIOException iioe) {
	    daemon.displayMessage("Connection timed out to: " + hostName);
	    /*
	} catch(SocketException se) {
	    daemon.displayMessage("Connection closed to: " + hostName);
	} catch(EOFException eofe) {
	    daemon.displayMessage("Connection closed to: " + hostName);
	    */
	} catch(IOException ioe) {
	    daemon.displayMessage("Connection closed to: " + hostName +
				  "  Reason: " + ioe.getClass().getName());
	} catch(Exception e) {
	    e.printStackTrace();
	}
	try {
	    daemon.removeConnection(this);
	    if(inputStream != null)
		inputStream.close();
	    if(outputStream != null)
		outputStream.close();
	    inputStream = null;
	    outputStream = null;
	    socket.shutdownInput();
	    socket.shutdownOutput();
	} catch(Exception e2) {
	    daemon.displayMessage("Error while shutting down connection: " +
				  e2);
	    //e2.printStackTrace();
	}
    }

    /**
     * Runs this agent.
     */
    protected final void executeAgent() {
	try {
	    executeSocket();
	} catch(Exception e) {
	}
    }

    /**
     * Sends the Agent off via this connection.
     *
     * @param agent The Agent to send.
     */
    final void transmitAgent(MobileAgent agent, int runState)
	throws IOException {
	try {
	    if(outputStream == null) {
		synchronized(this) {
		    if(outputStream == null)
			outputStream =
			    new ObjectOutputStream(socket.getOutputStream());
		}
	    }
	    synchronized(this) {
		outputStream.writeObject(new AgentGram(agent, runState));
		resetCount++;
		if(resetCount >= 7) {
		    outputStream.flush();
		    outputStream.reset();
		    resetCount = 0;
		}
	    }
	} catch(IOException ioe) {
	    throw ioe;
	} catch(Exception e) {
	    throw new IOException("" + e);
	}
    }

    /**
     * Sends the AgentGram off via this connection.
     *
     * @param agent The Agent to send.
     */
    final void transmitGram(AgentGram gram)
	throws IOException {
	try {
	    if(outputStream == null) {
		synchronized(this) {
		    if(outputStream == null)
			outputStream =
			    new ObjectOutputStream(socket.getOutputStream());
		}
	    }
	    synchronized(this) {
		outputStream.writeObject(gram);
		resetCount++;
		if(resetCount >= 7) {
		    outputStream.flush();
		    outputStream.reset();
		    resetCount = 0;
		}
	    }
	} catch(IOException ioe) {
	    throw ioe;
	} catch(Exception e) {
	    throw new IOException("" + e);
	}
    }

    /**
     * Sends a DaemonDescription off via this connection.
     *
     * @param agent The Agent to send.
     */
    final void transmitDescription(DaemonDescription description)
	throws IOException {
	try {
	    if(outputStream == null) {
		synchronized(this) {
		    if(outputStream == null)
			outputStream =
			    new ObjectOutputStream(socket.getOutputStream());
		}
	    }
	    synchronized(this) {
		outputStream.writeObject(description);
		resetCount++;
		if(resetCount >= 7) {
		    outputStream.flush();
		    outputStream.reset();
		    resetCount = 0;
		}
	    }
	} catch(IOException ioe) {
	    throw ioe;
	} catch(Exception e) {
	    throw new IOException("" + e);
	}
    }

    /**
     * Returns a description of the service.
     */
    public final String getShortDescription() {
	return "SocketConnection to " + hostName + " on " + port;
    }

    /**
     * Returns a description of the service.
     */
    public final String getAgentDescription() {
	return getShortDescription();
    }
}
