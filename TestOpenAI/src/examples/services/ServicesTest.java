package examples.services;

/*****************************************************************************
 * BenchmarkTest
 *****************************************************************************
 * @author  Thorn Halo
 * 2002 OpenAI Labs
 *****************************************************************************/
import net.openai.ai.agent.*;
import java.io.*;
import java.net.*;


/**
 * This class is for testing access to DaemonServices.
 */
public class ServicesTest extends DaemonService {

    String desc;

    /**
     * Constructs a new ServicesTest DaemonService.
     */
    public ServicesTest(String desc, boolean makePublic) {
	super(makePublic);
	this.desc = desc;
    }

    /**
     * Returns a short description of this service.
     */
    public String getShortDescription() {
	return desc;
    }

    /**
     * Executes this service
     */
    public void executeAgent() {
	try {
	    Thread.sleep(5000);
	} catch(InterruptedException ie) {
	}
    }

    /**
     * The main entry point of this test application.
     * Usage: java ServicesTest <local port>
     */
    public static final void main(String args[]) throws Exception {

	int localPort = 7780;

	// check to see that we have an argument for the IP or hostname
	if(args.length < 1) {
	    System.out.println("Usage: java ServicesTest <local port>");
	    System.exit(-1);
	}

	// get the local port
	try {
	    localPort = Integer.parseInt(args[0]);
	} catch(NumberFormatException nfe) {
	    System.out.println("Failed to parse port number: " + args[0]);
	    System.exit(-1);
	}

	Daemon daemon = Daemon.getInstance(localPort);
	daemon.startService(new ServicesTest("publicService", true));
	daemon.startService(new ServicesTest("privateService", false));
	for(int i = 0; i < 10; i++) {
	    Thread.sleep(1000);
	    DaemonDescription desc = daemon.getDescription();
	    String[] services = desc.getServices();
	    for(int j = 0; j < services.length; j++) {
		System.out.println(" Service: " + services[j]);
	    }
	}

	System.exit(0);
    }
    
}
