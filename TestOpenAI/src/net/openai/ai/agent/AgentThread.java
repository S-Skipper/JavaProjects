/*****************************************************************************
 * net.openai.ai.agent.AgentThread
 *****************************************************************************
 * @author  thornhalo
 * @date    Mon Mar  5 19:18:24 CST 2001
 * 2001 OpenAI Labs
 *****************************************************************************
 * $Log: AgentThread.java,v $
 * Revision 1.3  2002/10/15 02:35:54  thornhalo
 * Added the cvs log tags to all of the files.
 *
 *****************************************************************************/
package net.openai.ai.agent;


/**
 * This is the actual Thread that the agent will be running in.
 */
final class AgentThread extends Thread {

    /** The agent that is running in this thread. */
    Agent agent;

    /** The daemon that started the thread. */
    Daemon daemon;


    /**
     * Constructs a new AgentThread for the Agent.
     */
    AgentThread(Agent agent, Daemon daemon) {
	if(agent == null)
	    throw new NullPointerException("Null Agent");
	if(daemon == null)
	    throw new NullPointerException("Null Daemon");
	this.agent = agent;
	this.daemon = daemon;
    }

    /**
     * The body of code that is run to execute the agent.
     */
    public final void run() {
	yield();
	try {
	    agent.executeAgent();
	    throw new HaltException();
	} catch(Exception e) {
	    e.printStackTrace();
	    daemon.displayMessage("Agent " + agent.getAgentID() +
				  " died abnormally.");
	} catch(HaltException he) {
	}
	daemon.removeAgentThread(this);
	agent = null;
    }
}
