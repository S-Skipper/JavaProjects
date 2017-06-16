package examples.bounce;

/*****************************************************************************
 * AgentTest.java
 *****************************************************************************
 * @author  Thorn Halo
 * 2001 OpenAI Labs
 *****************************************************************************/

import net.openai.ai.agent.*;
import java.io.*;
import java.net.*;


/**
 * This is an example class for having two daemons up in the same JVM and
 * having agents "bounce" between them.
 */
public class SingleHostBounceTest {

    /**
     * The main entry point of this test application.
     * Usage: java SingeHostBounceTest <port1> <port2>
     */
    public static final void main(String args[]) {

	// The two ports
	int port = 7780;
	int destPort = 7790;

	// check to see that we have an argument for the IP or hostname
	if(args.length < 2) {
	    System.out.println("Usage: java SingleHostBounceTest " +
			       "<port1> <port2>");
	    System.exit(-1);
	}

	// get the local port
	try {
	    port = Integer.parseInt(args[0]);
	} catch(NumberFormatException nfe) {
	    System.out.println("Failed to parse port number: " + args[0]);
	    System.exit(-1);
	}

	// get the remote port
	try {
	    destPort = Integer.parseInt(args[1]);
	} catch(NumberFormatException nfe) {
	    System.out.println("Failed to parse port number: " + args[1]);
	    System.exit(-1);
	}

	// start the daemons
	try {
	    Daemon.getInstance(port).startAgent(new BounceAgent(destPort));
	    Daemon.getInstance(destPort).startAgent(new BounceAgent(port));
	} catch(Exception e) {
	    e.printStackTrace();
	    System.out.println("Failed to start daemon: " + e);
	    System.exit(-1);
	}
    }
}
