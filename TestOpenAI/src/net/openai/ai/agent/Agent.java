/*****************************************************************************
 * net.openai.ai.agent.Agent
 *****************************************************************************
 * @author  thornhalo
 * @date    Mon Mar  5 19:18:24 CST 2001
 * 2001 OpenAI Labs
 *****************************************************************************
 * $Log: Agent.java,v $
 * Revision 1.17  2002/10/15 02:35:54  thornhalo
 * Added the cvs log tags to all of the files.
 *
 *****************************************************************************/
package net.openai.ai.agent;

import java.io.*;
import java.net.*;


/**
 * This class is the base class for all Agents.  Even though this class
 * implements the Serializable interface, it is not "mobile".  If an instance
 * of this class is sent to a host, it will not be automatically started like
 * the MobileAgent class will.  The reason that it does implement the
 * Serializable interface is to retain inner data for when the MobileAgent
 * migrates.  If it did not, this data will be lost in the serialization
 * process.
 *
 * @version $Id: Agent.java,v 1.17 2002/10/15 02:35:54 thornhalo Exp $
 * <br>
 * History:
 * <br>    $Log: Agent.java,v $
 * <br>    Revision 1.17  2002/10/15 02:35:54  thornhalo
 * <br>    Added the cvs log tags to all of the files.
 * <br>
 * <br>    Revision 1.16  2002/02/26 14:56:46  thornhalo
 * <br>    added the $Id: Agent.java,v 1.17 2002/10/15 02:35:54 thornhalo Exp $ and $Log: Agent.java,v $
 * <br>    added the $Id:$ and Revision 1.17  2002/10/15 02:35:54  thornhalo
 * <br>    added the $Id:$ and Added the cvs log tags to all of the files.
 * <br>    added the $Id:$ and CVS tags
 * <br>
 */
public abstract class Agent implements Serializable, AgentConstants {

    /** The host machine name that spawned the agent. */
    private String spawnHostName = Daemon.hostName;

    /** The host machine IP that spawned the agent. */
    private String spawnHostIP = Daemon.hostIP;

    /** The local time that the agent was spawned in milliseconds. */
    private long spawnTime = System.currentTimeMillis();;

    /** The number of the agent created on the local host. */
    private long agentNumber = agentCounter++;

    /** The agent's unique ID. */
    private transient String ID;

    /** The Daemon responsible for the agent. */
    transient Daemon daemon = null;

    /** The run state for the agent.  I wanted to make this transient, but
	the agent would't maintain its runState if saved out to a file. */
    int runState = AGENT_START;

    /** The agent number counter. */
    private static long agentCounter = 0;


    /**
     * Constructs a new Agent.
     */
    public Agent() {
    }

    /**
     * Returns the run state for the Agent as defined in the
     * <code>AgentConstants</code> interface.
     *
     * @see AgentConstants
     * @return The run state for the Agent.
     */
    protected final int getRunState() {
	return runState;
    }

    /**
     * Implement this method to provide functionality for the Agent.
     */
    protected abstract void executeAgent() throws Exception;

    /**
     * Returns the Daemon that started the Agent or received the agent from
     * another host.
     *
     * @return The Daemon associated with the Agent.
     */
    protected final Daemon getDaemon() {
	return daemon;
    }

    /**
     * Returns the name of the host that the agent was spawned on.
     *
     * @return The name of the host that the agent was spawned on.
     */
    public final String getSpawnHostName() {
	return spawnHostName;
    }

    /**
     * Returns the IP of the host that the agent was spawned on.
     *
     * @return The IP of the host that the agent was spawned on.
     */
    public final String getSpawnHostIP() {
	return spawnHostIP;
    }

    /**
     * Returns the time the agent was spawned on the spawning host.
     *
     * @return The spawning time in milliseconds.
     */
    public final long getSpawnTime() {
	return spawnTime;
    }

    /**
     * Returns the number of the agent as it was created on its source host.
     *
     * @return The number of the agent as it was created on its source host.
     */
    public final long getAgentNumber() {
	return agentNumber;
    }

    /**
     * Returns the unique ID of that the Agent was assigned at its spawn time.
     *
     * @return The unique ID of the Agent.
     */
    public final String getAgentID() {
	if(ID == null) {
	    synchronized(spawnHostName) {
		if(ID == null) {
		    ID = spawnHostName + "/" + spawnHostIP + "-" + spawnTime +
			"-" + agentNumber;// + this.getClass().getName();
		}
	    }
	}
	return ID;
    }

    /**
     * Returns a description of the Agent.
     *
     * @return A description of the Agent.
     */
    public String getAgentDescription() {
	return this.getClass().getName();
    }

    /**
     * Returns a short description of the Agent.  This is most commonly a
     * simple name.
     *
     * @return A short description of the Agent.
     */
    public String getShortDescription() {
	return this.getClass().getName();
    }

    /**
     * Causes the Agent to halt execution.
     */
    protected void halt() {
	throw new HaltException();
    }

    /**
     * Returns the Agent as a String.  The default value is the same as the
     * <code>getAgentID()</code> method.
     *
     * @return The Agent as a String.
     */
    public String toString() {
	return getAgentID();
    }

    /**
     * Returns the log stream for the local Daemon.
     *
     * @return The log stream for the local Daemon.
     */
    protected final PrintStream getLogStream() {
	return daemon.getLogStream();
    }

    /**
     * Displays a message.  This must be called from within the Agent's thread.
     *
     * @param msg The message to display.
     */
    protected final void displayMessage(String msg) {
	getLogStream().println("[AGENT " + getShortDescription() + "] " + msg);
    }
}
