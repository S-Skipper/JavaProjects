package examples.rmc;

/*****************************************************************************
 * MigrateAndHaltAgent
 *****************************************************************************
 * @author  Thorn Halo
 * 2001 OpenAI Labs
 *****************************************************************************/
import net.openai.ai.agent.*;
import java.io.*;
import java.net.*;


/**
 * A MobileAgent that will migrate to another machine and perform some action.
 */
public class MigrateAndHaltAgent extends MobileAgent {

    /** The destination host. */
    private String destHost;

    /** The destination port. */
    private int destPort;

    /** The port of the daemon on the spawning host. */
    private int homePort = -1;


    /**
     * Constructs a new MigrateAndHaltAgent for the given destination host
     * and port.
     *
     * @param destHost The destination host.
     * @param destPort The port to enter on the destination host.
     */
    public MigrateAndHaltAgent(String destHost, int destPort) {
	this.destHost = destHost;
	this.destPort = destPort;
    }

    /**
     * What this agent will do on the "spawning" machine.
     */
    protected void spawnHostAction() {
	displayMessage(getAgentID() + " at spawn host.");
    }

    /**
     * What this agent will do on the destination host.
     */
    protected void destinationHostAction() {
	displayMessage(getAgentID() + " at destination host.");
    }

    /**
     * Returns true if this agent is "at home" and false otherwise.
     */
    protected final boolean isHome() throws UnknownHostException {
	if(homePort == -1) {
	    homePort = getDaemon().getPort();
	    return true;
	}
	InetAddress local = InetAddress.getLocalHost();
	String localName = local.getHostName();
	return (localName.equals(getSpawnHostName()) && 
		(getDaemon().getPort() == homePort));
    }

    /**
     * The main body of the agent.
     */
    protected final void executeAgent() throws Exception {
	if(getRunState() == AGENT_RETURN_TO_SENDER) {
	    displayMessage("Rejected");
	    halt();
	}
	try {
	    if(isHome()) {
		spawnHostAction();
		migrateTo(destHost, destPort);
	    } else {
		destinationHostAction();
	    }
	} catch(IOException ioe) {
	    displayMessage("Failed to migrate: " + ioe);
	}
    }
}
