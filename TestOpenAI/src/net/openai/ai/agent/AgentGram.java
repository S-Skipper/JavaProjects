/*****************************************************************************
 * net.openai.ai.agent.AgentGram
 *****************************************************************************
 * @author  Thornhalow
 * 2001 OpenAI Labs
 *****************************************************************************
 * $Log: AgentGram.java,v $
 * Revision 1.6  2002/10/24 03:50:53  thornhalo
 * Implemented the return to sender if the remote class loader is not enabled.
 *
 * Revision 1.5  2002/10/15 02:35:54  thornhalo
 * Added the cvs log tags to all of the files.
 *
 *****************************************************************************/
package net.openai.ai.agent;

import java.io.*;
import java.net.*;
import java.util.*;


/**
 * This class serves as a type of datagram for MobileAgents.  I was hoping
 * to get around using something like this, but this is the only way I could
 * think of that would allow the remote classloading scheme I'm hoping to
 * achieve.
 */
final class AgentGram implements Serializable, AgentConstants {

    /** The serialized agent data. */
    byte[] data;

    /** This is used solely to keep track of which daemon on the local host
	needs to resolve the class for the agent data. */
    transient int port;

    /** This is also used for class resolution. */
    transient String classname;

    /** The run state for the agent upon startup. */
    int runState = AGENT_START;


    /**
     * Constructs a new AgentGram for the MobileAgent.
     *
     * @param agent The agent to send.
     */
    AgentGram(MobileAgent agent, int runState) throws IOException {
	ByteArrayOutputStream buff = new ByteArrayOutputStream();
	ObjectOutputStream oos = new ObjectOutputStream(buff);
	oos.writeObject(agent);
	data = buff.toByteArray();
	this.runState = runState;
    }
}
