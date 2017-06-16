package examples.benchmark;

/*****************************************************************************
 * BenchmarkTest
 *****************************************************************************
 * @author  Thorn Halo
 * 2001 OpenAI Labs
 *****************************************************************************/
import net.openai.ai.agent.*;
import java.io.*;
import java.net.*;


/**
 * A simple MobileAgent that will bounce between two machines.
 *
 * NOTE: This class needs to be rewritten to "sychronize" the two Daemons more
 *       reliably.  I'm not going to do that for now since it looks like we may
 *       be using JUnit and JMeter for testing/benchmarking in the near future.
 */
public class BenchmarkTest extends MobileAgent {

    /** Signal that the agents should exit and not try to return. */
    public static boolean shouldQuit = false;

    /** The destination host. */
    private String destHost;

    /** The destination port. */
    private int destPort;

    /** The home port. */
    private int homePort = -1;

    /** The payload. */
    private byte[] payload = null;


    /**
     * Constructs a new BenchmarkTest for the given destination host and port.
     *
     * @param destHost    The remote host to migrate to and back.
     * @param destPort    The remote port to migrate through.
     * @param payloadSize The size in bytes of the payload.
     */
    public BenchmarkTest(String destHost, int destPort, int payloadSize) {
	//displayMessage("Constructing");
	this.destHost = destHost;
	this.destPort = destPort;
	this.payload = new byte[payloadSize];
    }

    /**
     * Returns true if this agent is "at home" and false otherwise.
     */
    protected final boolean isHome() {
	if(homePort == -1)
	    homePort = getDaemon().getPort();
	if(getDaemon().getPort() == homePort) {
	    try {
		InetAddress local = InetAddress.getLocalHost();
		if(getSpawnHostIP().equals(local.getHostAddress()))
		    return true;
	    } catch(UnknownHostException uhe) {
		uhe.printStackTrace();
		halt();
	    }
	}
	return false;
    }

    /**
     * Returns the target host.
     */
    protected String getTargetHost() {
	if(homePort == -1)
	    homePort = getDaemon().getPort();
	if(isHome())
	    return destHost;
	return getSpawnHostIP();
    }

    /**
     * Returns the target port.
     */
    protected int getTargetPort() {
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
	boolean messageDisplayed = false;
	while(!shouldQuit) {
	    try {
		migrateTo(getTargetHost(), getTargetPort());
	    } catch(Exception e) {
		if(messageDisplayed) {
		} else {
		    displayMessage(getAgentID() + "(" + shouldQuit +
				   ") Error Migrating: " + e);
		    messageDisplayed = true;
		}
	    }
	}
	//getDaemon().getLogStream().println("$" + getAgentID() + " done");
	System.out.print('$');
    }

    /**
     * The main entry point of this test application.
     * Usage: java BenchmarkTest <local port> <remote host> <remote port>
     *                           <num agens>
     */
    public static final void main(String args[]) {

	int localPort = 7780;
	String destHost = null;
	int destPort = 7790;
	int numAgents = 1;
	int payloadSize = 1;

	// check to see that we have an argument for the IP or hostname
	if(args.length < 5) {
	    System.out.println("Usage: java BenchmarkTest " +
			       "<local port> <remote host> <remote port> " +
			       "<num agents> <payload size bytes>");
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

	// get the number of agents to start
	try {
	    numAgents = Integer.parseInt(args[3]);
	} catch(NumberFormatException nfe) {
	    System.out.println("Failed to parse number of agents: " + args[3]);
	    System.exit(-1);
	}

	// get the size of the payload
	try {
	    payloadSize = Integer.parseInt(args[4]);
	} catch(NumberFormatException nfe) {
	    System.out.println("Failed to parse payload size: " + args[4]);
	    System.exit(-1);
	}

	System.out.println("<==== Running Benchmark Test ====>");
	System.out.println("@ Local Port       : " + localPort);
	System.out.println("@ Remote Daemon    : " + destHost + ":" +destPort);
	System.out.println("@ Number of agents : " + numAgents);
	System.out.println("@ Payload size     : " + payloadSize);
	System.out.println();

	System.out.print("Spawning ");
	Agent[] agents = new Agent[numAgents];
	int tick = numAgents / 50;
	if(tick == 0)
	    tick = 1;
	for(int i = 0; i < numAgents; i++) {
	    agents[i] = new BenchmarkTest(destHost, destPort, payloadSize);
	    if((i % tick) == 0)
		System.out.print('.');
	}
	System.out.println();
	System.out.println();

	// start the daemon and agents
	Daemon daemon = null;
	try {
	    daemon = Daemon.getInstance(localPort);
	    daemon.startAgent(new BenchmarkTest(destHost, destPort, 0) {
		    protected void executeAgent() {
			while(isHome()) {
			    try {
				migrateTo(this.getTargetHost(),
					  this.getTargetPort());
			    } catch(Exception e) {
			    }
			}
			System.out.print(">>>>> Signalling to start");
			BenchmarkTest.shouldQuit = false;
			System.out.println(" done");
		    }
		});
	    for(int i = 0; i < numAgents; i++)
		daemon.startAgent(agents[i]);
	} catch(Exception e) {
	    e.printStackTrace();
	    System.out.println("Failed to start daemon: " + e);
	    System.exit(-1);
	}

	try {
	    Thread.sleep(5 * 60 * 1000);
	} catch(InterruptedException ie) {
	}
	System.out.println("Launching shutdown ...");
	daemon.startAgent(new BenchmarkTest(destHost, destPort, 0) {
		boolean doHome = false;
		protected void executeAgent() {
		    if(doHome) {
			System.out.print(">>>>> Signalling local stop");
			BenchmarkTest.shouldQuit = true;
			System.out.println(" done");
		    } else {
			if(!isHome()) {
			    System.out.print(">>>>> Signalling to stop");
			    BenchmarkTest.shouldQuit = true;
			    System.out.println(" done");
			    doHome = true;
			}
			while(true) {
			    try {
				migrateTo(this.getTargetHost(),
					  this.getTargetPort());
			    } catch(Exception e) {
			    }
			}
		    }
		}
	    });
	System.out.println("Waiting for local signal ...");
	while(!BenchmarkTest.shouldQuit) {}
	System.exit(0);
    }
}
