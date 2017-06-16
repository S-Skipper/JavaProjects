package dtt;

/*****************************************************************************
 * SimpleAgentTest
 *****************************************************************************
 * @author  thornhalo
 * @date    Mon Mar  5 19:18:24 CST 2001
 * 2001 OpenAI Labs
 *****************************************************************************/
import net.openai.ai.agent.*;


/**
 * This class is a Simple Test of a non-mobile agent.
 */
public class SimpleAgentTest extends Agent {

    /**
     * The main body of the Agent.  This is what the Agent will
     * actually do when it is executed.
     */
    protected void executeAgent() {
	System.out.println(getAgentID() + " starting");
	try {
	    Thread.sleep(2000);
	} catch(InterruptedException ie) {
	}
	System.out.println(getAgentID() + " finished");
    }
    
    /**
     * The main entry point for the application.
     */
    public static void main(String args[]) throws Exception {
	Daemon daemon = Daemon.getInstance(7788);
	daemon.startAgent(new SimpleAgentTest());
	daemon.startAgent(new SimpleAgentTest());
    }
}
