package examples.bounce;


/*****************************************************************************
 * MultipleHostBounceTest
 *****************************************************************************
 * @author  Thorn Halo
 * 2001 OpenAI Labs
 *****************************************************************************/
import net.openai.ai.agent.*;
import java.io.*;


/**
 * This is a simple test of the Agent system.  Basically, you start up two
 * instances of these - one one each of two machines and pass in the IP or
 * hostname of the other machine.  A handful of Agents are then spawned on
 * each machine and bounce back and forth between the two machines.
 *
 * The test isn't limited to two hosts, you could have a ring of hosts as well:
 *     A --> B --> C --> A
 */
public class MultipleHostBounceTest {

    /** A handle to the local Daemon - for convenience purposes. */
    public static Daemon daemon = null;

    /** The IP or hostname that was passed in on the command line as the
	target machine that the local host will send Agents to. */
    public static String dest = null;

    /** The port to use on the local machine and to connect to on the
	target machine. */
    public static int port = 7788;

    /** The port on the remote machine to connect to.  This is only set if
	specified on the command line. */
    public static int destPort = port;

    
    /**
     * The main entry point of this test application.
     * Usage: java MultipleHostBounceTest destination
     */
    public static final void main(String args[]) {

	// check to see that we have an argument for the IP or hostname
	if(args.length < 3) {
	    System.out.println("Usage: java MultipleHostBounceTest " +
			       "<local port> <remote host> <remote port>");
	    System.exit(-1);
	}

	// get the local port
	try {
	    port = Integer.parseInt(args[0]);
	} catch(NumberFormatException nfe) {
	    System.out.println("Failed to parse port number: " + args[0]);
	    System.exit(-1);
	}

	// get a handle to the target host's IP or name
	dest = args[1];

	// get the remote port
	try {
	    destPort = Integer.parseInt(args[2]);
	} catch(NumberFormatException nfe) {
	    System.out.println("Failed to parse port number: " + args[2]);
	    System.exit(-1);
	}

	// start the daemon
	try {
	    startDaemon(port, destPort);
	} catch(Exception e) {
	    e.printStackTrace();
	    System.out.println("Failed to start daemon: " + e);
	    System.exit(-1);
	}

	System.out.println("Local Port: " + port);
	System.out.println("Remote Daemon: " + dest + ":" + destPort);
    }

    /**
     * Starts up the daemon with the specified local and remote addresses.
     */
    private static void startDaemon(int localPort, int remotePort)
	throws IOException {
	daemon = Daemon.getInstance(localPort);

	MultipleHostBounceTest.port = localPort;
	MultipleHostBounceTest.destPort = remotePort;

	// Create and launch some agents
	for(int i = 0; i < 5; i++) {
	    MHBAgent agent = new MHBAgent();
	    daemon.startAgent(agent);
	}
    }

    /**
     * Returns a handle to the destination host.  This is the next host
     * that an agent will migrate to when it reaches the local host.
     */
    public static synchronized String getDestHost() {
	return dest;
    }

    /**
     * The Agent that will bounce between the two machines.
     */
    public static class MHBAgent extends MobileAgent {

	/** The agent counter. */
	private static int agentCount = 0;

	/** The source port. */
	private int sourcePort = MultipleHostBounceTest.port;

	/** The agent number on the local host. */
	private int agentNumber = agentCount++;

	/** The bounce counter. */
	private int bounceCounter = 0;

	/**
	 * Constucts a new MHBAgent.
	 */
	public MHBAgent() {
	}

	/**
	 * The "body" of the agent - what the agent does.  In this case, it
	 * bounces from machine to machine printing a message every
	 * 100 times that it hits its originating host.
	 */
	protected void executeAgent() {
	    if(sourcePort == MultipleHostBounceTest.port)
		bounceCounter++;
	    if((bounceCounter % 25) == 0) {
		String msg = "bounce #" + bounceCounter + " ==> " + toString();
		if(sourcePort == MultipleHostBounceTest.port)
		    msg += " is home";
		else
		    msg += " is away";
		System.out.println(msg);
	    }
	    while(true) {
		try {
		    migrateTo(MultipleHostBounceTest.getDestHost(),
			      MultipleHostBounceTest.destPort);
		    // any code after this should *never* get executed
		    System.out.println("<<<<< ERROR IN MIGRATION CODE >>>>>");
		} catch(Exception e) {
		    System.err.println("Error Migrating: " + e);
		    try {
			Thread.sleep(5000);
		    } catch(InterruptedException ie) {
		    }
		}
	    }
	}

	/**
	 * Returns this Agent as a String.
	 */
	public String toString() {
	    return "" + agentNumber + "." + sourcePort;
	}
    }
}
