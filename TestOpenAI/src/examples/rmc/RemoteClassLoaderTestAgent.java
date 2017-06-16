package examples.rmc;

/*****************************************************************************
 * RemoteClassLoaderTestAgent
 *****************************************************************************
 * @author  Thorn Halo
 * 2002 OpenAI Labs
 *****************************************************************************/
import java.io.*;
import net.openai.ai.agent.*;


/**
 * This Agent automatically generates code for a new agent type, loads the
 * agent and sends it off to a remote host.  It is suggested that to run this
 * test, you either run the destination Daemon on a different machine that
 * does't share the local directory, or you create a directory and run the
 * local instance of this test there.  The key is to keep the newly generated
 * Agents from being in the same classpath as the other Daemon.
 */
public class RemoteClassLoaderTestAgent extends Agent {

    /** The destination host for the agents. */
    String destHost = null;

    /** The port to enter through. */
    int destPort = 0;

    /**
     * Constructs a new RemoteClassLoaderTestAgent for the given
     * destination host and port.
     *
     * @param destHost The destination host for the new agents.
     * @param destPort The port to enter through.
     */
    public RemoteClassLoaderTestAgent(String destHost, int destPort) {
	this.destHost = destHost;
	this.destPort = destPort;
    }

    /**
     * The main body of this agent.  This method will generate new agent types
     * and send those agents over to the remote Daemon.
     */
    protected void executeAgent() throws Exception {
	int agentClassCounter = 1;
	String baseClassname = "RMCL_TestAgent";
	while(true) {
	    String classname = baseClassname + agentClassCounter;
	    File javaFile = new File(classname + ".java");
	    String javaCode = constructJavaCode(classname);
	    displayMessage("Building: " + classname + "\n" + javaCode);
	    try {
		FileOutputStream fis = new FileOutputStream(javaFile);
		fis.write(javaCode.getBytes());
		fis.close();
		
		String javaCmd = "javac " + javaFile;
		displayMessage("Calling: " + javaCmd);
		Process compile =
		    Runtime.getRuntime().exec(javaCmd);
		int retValue = compile.waitFor();
		if(retValue == 0) {
		    displayMessage("Build successful: " + classname);
		    try {
			Class clazz = Class.forName(classname);
			for(int i = 0; i < 5; i++) {
			    MobileAgent agent =
				(MobileAgent)clazz.newInstance();
			    getDaemon().startAgent(agent);
			}
		    } catch(Exception e) {
			displayMessage("" + e);
		    } 
		}
		agentClassCounter++;
	    } catch(IOException ioe) {
		System.out.println("Failed to write: " + javaFile);
	    } catch(InterruptedException ie) {
		System.out.println("Compile interrupted!");
	    }
	    try {
		Thread.sleep(5000);
	    } catch(InterruptedException ie) {
	    }
	}
    }

    /**
     * Construct Java source code for a new agent class.
     *
     * @param classname The name of the class.
     */
    private String constructJavaCode(String classname) {
	String code = 
	    "import java.io.*;\n" +
	    "import net.openai.ai.agent.*;" +
	    "\n" +
	    "public class " + classname + " extends MigrateAndHaltAgent {\n" +
	    "    public " + classname + "() {\n" +
	    "        super(\"" + destHost + "\", " + destPort + ");\n" +
	    "    }\n" +
	    "}\n";
	return code;
    }

    /**
     * The main entry point of this test application.
     * Usage: java RemoteClassLoaderTestAgent <local port> <remote host>
     *        <remote port>
     */
    public static final void main(String args[]) {

	int localPort = 7780;
	String destHost = null;
	int destPort = 7790;

	// check to see that we have an argument for the IP or hostname
	if(args.length < 3) {
	    System.out.println("Usage: java RemoteClassLoaderTestAgent " +
			       "<local port> <remote host> <remote port>");
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

	System.out.println("Local Port: " + localPort);
	System.out.println("Remote Daemon: " + destHost + ":" + destPort);

	// start the daemon and agents
	try {
	    Daemon d = Daemon.getInstance(localPort);
	    d.startAgent(new RemoteClassLoaderTestAgent(destHost, destPort));
	} catch(Exception e) {
	    e.printStackTrace();
	    System.out.println("Failed to start daemon: " + e);
	    System.exit(-1);
	}
    }
}
