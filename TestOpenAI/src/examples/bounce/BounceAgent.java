package examples.bounce;

/*****************************************************************************
 * BounceAgent
 *****************************************************************************
 * @author  Thorn Halo
 * 2001 OpenAI Labs
 *****************************************************************************/
import net.openai.ai.agent.*;
import java.io.*;
import java.net.*;


/**
 * A simple MobileAgent that will bounce between two Daemons.
 */
public class BounceAgent extends MobileAgent {

    /** The destination host. */
    private String destHost;

    /** The destination port. */
    private int destPort;

    /** The agent's home host. null means that it is not set - yet. */
    private String homeHost = null;

    /** The agent's home port.  -1 means that it is not set - yet. */
    private int homePort = -1;


    /**
     * Constructs a new BounceAgent for the given destination host and port.
     *
     * @param destHost The remote host to migrate to and back.
     * @param destPort The remote port to migrate through.
     */
    public BounceAgent(String destHost, int destPort) {
	this.destHost = destHost;
	this.destPort = destPort;
    }

    /**
     * Constructs a new BounceAgent that will connect to a Daemon on the
     * local host.
     *
     * @param destPort The port to migrate through.
     */
    public BounceAgent(int destPort) {
	this(Daemon.getHostName(), destPort);
    }

    /**
     * Returns true if this agent is "at home" and false otherwise.
     */
    protected final boolean isHome() {
	try {
	    InetAddress local = InetAddress.getLocalHost();
	    String localName = local.getHostName();
	    return (localName.equals(homeHost) && 
		    (getDaemon().getPort() == homePort));
	} catch(UnknownHostException uhe) {
	    uhe.printStackTrace();
	}
	return false;
    }

    /**
     * Returns the target host.
     */
    private String getTargetHost() {
	if(homeHost == null)
	    homeHost = getSpawnHostName();
	if(isHome())
	    return destHost;
	return getSpawnHostName();
    }

    /**
     * Returns the target port.
     */
    private int getTargetPort() {
	// If this is the first time it has been called, set the home port
	// value.
	// NOTE: This is safe only if called from executeAgent()
	if(homePort == -1)
	    homePort = getDaemon().getPort();

	if(isHome())
	    return destPort;
	return homePort;
    }

    /**
     * The "body" of the agent - what the agent does.
     */
    protected void executeAgent() {
	doPremigration();
	while(true) {
	    try {
		migrateTo(getTargetHost(), getTargetPort());
	    } catch(Exception e) {
		displayMessage("Error Migrating: " + e);
		try {
		    Thread.sleep(3000);
		} catch(InterruptedException ie) {
		}
	    }
	}
    }

    /**
     * Performs some task before it migrates.
     */
    protected void doPremigration() {
	if(isHome())
	    displayMessage(getAgentID() + " is home.  RunState=" +
			   getRunState());
	else
	    displayMessage(getAgentID() + " is away.  RunState=" +
			   getRunState());
	try {
	    Thread.sleep(250);
	} catch(InterruptedException ie) {
	}
    }

    /**
     * The main entry point of this test application.
     * Usage: java BounceAgent <local port> <remote host> <remote port>
     */
    public static final void main(String args[]) {

	int localPort = 7780;
	String destHost = null;
	int destPort = 7790;

	// check to see that we have an argument for the IP or hostname
	if(args.length < 3) {
	    System.out.println("Usage: java BounceAgent " +
			       "<local port> <remote host> <remote port>");
	    System.exit(-1);
	}

	// get the local port
	try {
	    localPort = Integer.parseInt(args[0]);
	} catch(NumberFormatException nfe) {
	    System.out.println("Failed to parse port number: " + args[0]);
	    System.exit(-1);
	}

	// get a handle to the target host's IP or name
	destHost = args[1];

	// get the remote port
	try {
	    destPort = Integer.parseInt(args[2]);
	} catch(NumberFormatException nfe) {
	    System.out.println("Failed to parse port number: " + args[2]);
	    System.exit(-1);
	}

	System.out.println("Local Port: " + localPort);
	System.out.println("Remote Daemon: " + destHost + ":" + destPort);

	// start the daemon and agents
	try {
	    Daemon d = Daemon.getInstance(localPort);
	    d.startAgent(new BounceAgent(destHost, destPort));
	} catch(Exception e) {
	    e.printStackTrace();
	    System.out.println("Failed to start daemon: " + e);
	    System.exit(-1);
	}
    }
}
