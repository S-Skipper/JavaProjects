/*****************************************************************************
 * net.openai.ai.agent.AgentConstants
 *****************************************************************************
 * @author  thornhalo
 * @date    Thu Jan 31 20:11:02 EST 2002
 * 2002 OpenAI Labs
 *****************************************************************************
 * $Log: AgentConstants.java,v $
 * Revision 1.2  2002/10/15 02:35:54  thornhalo
 * Added the cvs log tags to all of the files.
 *
 *****************************************************************************/
package net.openai.ai.agent;


/**
 * This interface contains several constants that will be used by the Agent
 * System.  Primarily, these will be used by the Agents.  These constants
 * are implemented in an interface in order to add more constants available
 * to the Agents and not have to change the Agent class itself.
 */
public interface AgentConstants {

    /** The initial run state for agents. */
    public static final int AGENT_START = 0;

    /** The default state after a migration. */
    public static final int AGENT_CONTINUE = 1;

    /** The run state if an agent has been rejected by another Daemon. */
    public static final int AGENT_RETURN_TO_SENDER = 2;
}
