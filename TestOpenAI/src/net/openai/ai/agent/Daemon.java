/*****************************************************************************
 * net.openai.ai.agent.Daemon
 *****************************************************************************
 * @author  Thornhalo
 * 2001 OpenAI Labs
 *****************************************************************************
 * $Log: Daemon.java,v $
 * Revision 1.29  2002/10/24 04:18:52  thornhalo
 * Minor bug fixes and updates to the RemoteClassLoader example.
 *
 * Revision 1.28  2002/10/24 03:50:53  thornhalo
 * Implemented the return to sender if the remote class loader is not enabled.
 *
 * Revision 1.27  2002/10/22 01:47:53  thornhalo
 * Added the "verbose" and "remoteClassLoading" options to the config file.  Still need to actually add the capability to disable remote classloading in the Daemon itself, though.
 *
 * Revision 1.26  2002/10/21 02:47:05  thornhalo
 * First-round implementation of the Daemon config file option.
 *
 * Revision 1.25  2002/10/15 03:06:42  thornhalo
 * implemented the -s and -a options
 *
 * Revision 1.24  2002/10/15 02:35:54  thornhalo
 * Added the cvs log tags to all of the files.
 *
 *****************************************************************************/
package net.openai.ai.agent;

import java.io.*;
import java.net.*;
import java.util.*;


/**
 * This is the "portal" that allows the transmission of Agents between hosts.
 */
public final class Daemon implements AgentConstants {

    /** The command-line option to specify the port. */
    public static final String OPTION_PORT = "-p";

    /** The command-line option to specify the list of agents to start. */
    public static final String OPTION_AGENTS = "-a";

    /** The command-line option to specify the list of services to start. */
    public static final String OPTION_SERVICES = "-s";

    /** The command-line option to specify the XML configuration file. */
    public static final String OPTION_CONFIG = "-c";

    /** The command-line option to specify the output log file. */
    public static final String OPTION_LOG = "-o";

    /** The command-line option to specify that the output should be echoed
	to stdout as well.  This option is automatically enabled if no logging
	file is given. */
    public static final String OPTION_VERBOSE = "-v";


    /** The local host IP that the Daemon is running on. */
    static String hostIP;

    /** The local host name that the Daemon is running on. */
    static String hostName;

    /** The port that the Daemon is running on. */
    private int port;

    /** A hashtable of ports mapped to running Daemons. */
    private static Hashtable daemonTable = new Hashtable();

    /** A hashtable mapping Agents to AgentThreads. */
    private Hashtable agentTable = new Hashtable();

    /** A hashtable mapping DaemonServices to AgentThreads. */
    private Hashtable serviceTable = new Hashtable();

    /** A map of running public DaemonService descriptions to the services. */
    private Hashtable publicServices = new Hashtable();

    /** A list of running public DaemonServices. */
    private Vector servicesList = new Vector();

    /** A hashtable mapping InetAddresses and ports to DaemonSockets. */
    private Hashtable connectionTable = new Hashtable();

    /** The DaemonServer for the Daemon. */
    private DaemonServer server = null;

    /** The time the Daemon started. */
    private long startTime;

    /** The number of agents transmitted. */
    private long numTransmitted = 0;

    /** The number of agents received. */
    private long numReceived = 0;

    /** The current number of agents. */
    private long numAgents = 0;

    /** The maximum number of running agents. */
    private long maxAgents = 0;

    /** The total number of agents executed locally. */
    private long totalAgents = 0;

    /** The PrintStream to use for writing messages. */
    private DaemonLogger out = null;

    /** The flag that indicates if the remote class loader is enabled. */
    private boolean remoteClassLoading = true;

    
    // initialize the static host variables.
    static {
	try {
	    InetAddress localHost = InetAddress.getLocalHost();
	    hostName = new String(localHost.getHostName());
	    hostIP = new String(localHost.getHostAddress());
	} catch(UnknownHostException uhe) {
	    uhe.printStackTrace();
	}
    }


    /**
     * Constructs a Daemon for running on a particular port.
     *
     * @param port The port to run the Daemon on.
     */
    private Daemon(int port) throws IOException {
	this.port = port;
	server = new DaemonServer(this, port);
	startAgent(server);
	try {
	    Runtime.getRuntime().addShutdownHook(new DaemonShutdown(this));
	} catch(Exception e) {
	    displayMessage("WARNING: Failed to register shutdown: " + e);
	}
	startTime = System.currentTimeMillis();
    }

    /**
     * Returns an instance of a Daemon running on a particular port.  If a
     * Daemon is already running on that port, it is returned.  If there is
     * no Daemon running, a new one is started.
     *
     * @return The Daemon running on the given port.
     */
    public static final Daemon getInstance(int port) throws IOException {
	Daemon daemon = null;
	synchronized(daemonTable) {
	    Integer portInt = new Integer(port);
	    daemon = (Daemon)daemonTable.get(portInt);
	    if(daemon == null) {
		daemon = new Daemon(port);
		daemonTable.put(portInt, daemon);
	    }
	}
	return daemon;
    }

    /**
     * Returns a list of ports that Daemons are running on.
     *
     * @return A list of ports that known Daemons are running on in this VM.
     */
    public static final int[] getDaemonPorts() {
	Enumeration keys = null;
	int size = 0;
	synchronized(daemonTable) {
	    keys = daemonTable.keys();
	    size = daemonTable.size();
	}
	int[] ports = new int[size];
	for(int i = 0; i < size; i++) {
	    Integer port = (Integer)keys.nextElement();
	    ports[i] = port.intValue();
	}
	return ports;
    }

    /**
     * Returns the host IP for the local host that the Daemon is running on.
     *
     * @return The host IP for the local host.
     */
    public static final String getHostIP() {
	return hostIP;
    }

    /**
     * Returns the hostname for the local host that the Daemon is running on.
     *
     * @return The hostname for the local host.
     */
    public static final String getHostName() {
	return hostName;
    }

    /**
     * Returns the port that the Daemon is running on.
     *
     * @return The port for the Daemon.
     */
    public final int getPort() {
	return port;
    }

    /**
     * Returns a list of active services associated with this Daemon.
     *
     * @return A list of active DaemonServices.
     */
    public final DaemonService[] getServices() {
	DaemonService[] retValue;
	Vector temp;
	synchronized(serviceTable) {
	    temp = (Vector)servicesList.clone();
	}
	int size = temp.size();
	retValue = new DaemonService[size];
	for(int i = 0; i < size; i++)
	    retValue[i] = (DaemonService)temp.elementAt(i);
	return retValue;
    }

    /**
     * Returns a description of the Daemon as a DaemonDescription.
     *
     * @return A DaemonDescription object describing the Daemon.
     */
    public final DaemonDescription getDescription() {
	DaemonService[] services = getServices();
	String[] srvcs = new String[services.length];
	for(int i = 0; i < services.length; i++)
	    srvcs[i] = services[i].getShortDescription();
	return new DaemonDescription(hostIP, hostName, port, srvcs);
    }

    /**
     * Returns a handle to the PrintStream that the Daemon will be printing
     * to.
     *
     * @return The PrintStream that the Daemon will print to.
     */
    public final PrintStream getLogStream() {
	return (out == null ? System.out : out);
    }

    /**
     * Starts a service in its own Thread.  Alternatively, the
     * <code>startAgent</code> method can be used, but that will simply check
     * the type and call the <code>startService</code> method.
     *
     * @param service The DaemonService to start.
     */
    public final void startService(DaemonService service) {
	synchronized(serviceTable) {
	    if(serviceTable.get(service) == null) {
		service.daemon = this;
		AgentThread serviceThread = new AgentThread(service, this);
		serviceTable.put(service, serviceThread);
		if(service.isPublic) {
		    String srvDesc = service.getShortDescription();
		    Vector srv = (Vector)publicServices.get(srvDesc);
		    if(srv == null)
			srv = new Vector();
		    srv.add(service);
		    publicServices.put(srvDesc, srv);
		    servicesList.addElement(service);
		}
		serviceThread.start();
	    } else {
		displayMessage("ERROR: Service " + service.getAgentID() +
			       " already running!");
		return;
	    }
	}
	displayMessage("Started service " + service.getShortDescription());
    }

    /**
     * Starts an agent in its own Thread.
     *
     * @param agent The Agent to start.
     */
    public final void startAgent(Agent agent) {
	if(agent instanceof DaemonService) {
	    startService((DaemonService)agent);
	} else {
	    synchronized(agentTable) {
		if(agentTable.get(agent) == null) {
		    agent.daemon = this;
		    AgentThread agentThread = new AgentThread(agent, this);
		    agentTable.put(agent, agentThread);
		    agentThread.start();
		    numAgents++;
		    totalAgents++;
		    if(numAgents > maxAgents)
			maxAgents++;
		} else {
		    System.err.println("Daemon.startAgent: ERROR: Agent " +
				       agent.getAgentID() +
				       " already running!");
		}
	    }
	}
    }

    /**
     * Called whenever an AgentThread is finished.
     *
     * @param agentThread The AgentThread that is finished.
     */
    final void removeAgentThread(AgentThread agentThread) {
	if(agentThread.agent instanceof DaemonService) {
	    displayMessage("Service stopped: " +
			   agentThread.agent.getShortDescription());
	    synchronized(serviceTable) {
		agentThread.agent.daemon = null;
		if(((DaemonService)agentThread.agent).isPublic) {
		    String srvDesc = agentThread.agent.getShortDescription();
		    Vector srv = (Vector)publicServices.get(srvDesc);
		    srv.removeElement(agentThread.agent);
		    if(srv.size() == 0)
			publicServices.remove(srvDesc);
		    servicesList.remove(agentThread.agent);
		}
		serviceTable.remove(agentThread.agent);
	    }
	} else {
	    synchronized(agentTable) {
		numAgents--;
		agentThread.agent.daemon = null;
		agentTable.remove(agentThread.agent);
	    }
	}
	//System.gc();
    }

    /**
     * Returns the number of active agents for this Daemon.
     */
    public final int getAgentCount() {
	synchronized(agentTable) {
	    return agentTable.size();
	}
    }

    /**
     * Constructs a key for an InetAddress and port.
     */
    final String getKey(InetAddress host, int port) {
	return host.getHostName() + "/" + host.getHostAddress() + ":" + port;
    }

    /**
     * Returns a DaemonSocket for a particular IP/port.
     */
    final DaemonSocket getConnection(String host, int port)
	throws IOException, UnknownHostException {
	return getConnection(InetAddress.getByName(host), port);
    }

    /**
     * Returns a DaemonSocket for a particular IP/port.
     */
    final DaemonSocket getConnection(InetAddress host, int port)
	throws IOException {
	String key = getKey(host, port);
	DaemonSocket connection = null;
	synchronized(connectionTable) {
	    connection = (DaemonSocket)connectionTable.get(key);
	    if(connection == null) {
		connection = new DaemonSocket(this, new Socket(host, port));
		connectionTable.put(key, connection);
		startAgent(connection);
	    }
	}
	return connection;
    }

    /**
     * Adds a DaemonSocket to the Daemon once it has been connected.
     *
     * @param connection The Connection to add.
     */
    final void addConnection(DaemonSocket connection) {
	synchronized(connectionTable) {
	    connectionTable.put(getKey(connection.socket.getInetAddress(),
				       connection.socket.getPort()),
				connection);
	    startAgent(connection);
	}
    }

    /**
     * Disconnects a DaemonSocket from the Daemon.
     *
     * @param connection The DaemonSocket to detatch.
     */
    final void removeConnection(DaemonSocket connection) {
	synchronized(connectionTable) {
	    connectionTable.remove(getKey(connection.socket.getInetAddress(),
					  connection.socket.getPort()));
	}
    }

    /**
     * Moves an agent from one machine to another.
     */
    final void transmitAgent(MobileAgent agent, String host, int port)
	throws IOException, UnknownHostException {
	transmitAgent(agent, host, port, Agent.AGENT_CONTINUE);
    }

    /**
     * Moves an agent from one machine to another.
     */
    final void transmitAgent(MobileAgent agent, String host, int port,
			     int runState)
	throws IOException, UnknownHostException {
	Thread ct = Thread.currentThread();
	if((ct instanceof AgentThread) &&
	   (((AgentThread)ct).agent == agent))
	    getConnection(host, port).transmitAgent(agent, runState);
	else
	    throw new IOException("Transmission must take place within the " +
				  "MobileAgent");
	numTransmitted++;
    }

    /**
     * Moves an agent from one machine to another.
     */
    final void transmitAgent(MobileAgent agent, InetAddress host, int port)
	throws IOException {
	transmitAgent(agent, host, port, Agent.AGENT_CONTINUE);
    }

    /**
     * Moves an agent from one machine to another.
     */
    final void transmitAgent(MobileAgent agent, InetAddress host, int port,
			     int runState)
	throws IOException {
	Thread ct = Thread.currentThread();
	if((ct instanceof AgentThread) &&
	   (((AgentThread)ct).agent == agent))
	    getConnection(host, port).transmitAgent(agent, runState);
	else
	    throw new IOException("Transmission must take place within the " +
				  "MobileAgent");
	numTransmitted++;
    }

    /**
     * Sends a gram-wrapped agent from one machine to another.
     */
    private final void transmitGram(AgentGram gram, InetAddress host, int port)
	throws IOException {
	getConnection(host, port).transmitGram(gram);
    }

    /**
     * Sends a gram-wrapped agent from one machine to another.
     */
    private final void transmitGram(AgentGram gram, String host, int port)
	throws IOException {
	getConnection(host, port).transmitGram(gram);
    }

    /**
     * Receives an agent from another host.
     */
    final void receiveAgent(MobileAgent agent) {
	receiveAgent(agent, AGENT_START);
    }

    /**
     * Receives an agent from another host.
     */
    final void receiveAgent(MobileAgent agent, int runState) {
	agent.runState = runState;
	startAgent(agent);
	numReceived++;
    }

    /**
     * Receives a DaemonDescription from another Daemon.
     */
    final void receiveDescription(DaemonDescription desc, DaemonSocket sock) {
	displayMessage("Received description: " + desc);
	String[] services = desc.getServices();
	for(int i = 0; i < services.length; i++) {
	    displayMessage("   - service: " + services[i]);
	}
    }

    /**
     * Receives an AgentGram from another host.
     */
    final void receiveAgentGram(AgentGram gram, DaemonSocket s) {
	try {
	    ByteArrayInputStream buff = new ByteArrayInputStream(gram.data);
	    ObjectInputStream in = new DaemonObjectInputStream(buff);
	    MobileAgent agent = (MobileAgent)in.readObject();
	    receiveAgent(agent, gram.runState);
	} catch(ClassCastException cce) {
	    displayMessage("Not a MobileAgent: " + cce);
	} catch(ClassNotFoundException cnfe) {
	    String classname = cnfe.getMessage();
	    if(remoteClassLoading) {
		displayMessage("Class " + classname + " not found.  " +
			       "Attempting retrieval from " + s.hostName);
		gram.port = port;
		gram.classname = classname;
		DaemonClassLoader.instance.queueAgent(gram);
		startAgent(new ClassRetrievalAgent(s.hostName, s.port,
						   classname));
	    } else {
		try {
		    gram.runState = AgentConstants.AGENT_RETURN_TO_SENDER;
		    transmitGram(gram, s.hostName, s.port);
		} catch(IOException ioe) {
		    displayMessage("Cannot return to sender " + s.hostName +
				   ":" + s.port + " type=" + classname);
		}
	    }
	} catch(OptionalDataException ode) {
	    displayMessage("Not object: " + ode);
	} catch(IOException ioe) {
	    displayMessage("Error handling gram: " + ioe);
	}
    }

    /**
     * Shuts down the Daemon.
     */
    public final void shutdown() {
	long stopTime = System.currentTimeMillis();
	displayMessage("");
	displayMessage("<<==== SHUTDOWN on " + port + " ====>>");
	long upTime = stopTime - startTime;
	synchronized(daemonTable) {
	    daemonTable.remove(new Integer(port));
	    try {
		displayMessage(" * Closing server");
		server.serverSocket.close();
	    } catch(Exception e) {
		displayMessage("WARNING: Exception during shutdown: " + e);
	    }
	}
	synchronized(connectionTable) {
	    Enumeration keys = connectionTable.keys();
	    while(keys.hasMoreElements()) {
		DaemonSocket socket =
		    (DaemonSocket)connectionTable.get(keys.nextElement());
		try {
		    Socket s = socket.socket;
		    displayMessage(" * Closing connection to " +
				   s.getInetAddress().getHostName() + ":" +
				   s.getPort());
		    s.close();
		} catch(Exception e) {
		    displayMessage("WARNING: Exception during shutdown: " + e);
		}
	    }
	}
	long shipwrecked = 0;
	synchronized(agentTable) {
	    shipwrecked = agentTable.size();
	}
	displayMessage("   ---- stats ----");
	displayMessage("   @ Daemon uptime (sec) : " + (upTime / 1000.0));
	displayMessage("   @ Agents received     : " + numReceived);
	displayMessage("   @ Agents transmitted  : " + numTransmitted);
	displayMessage("   @ Maximum agents      : " + maxAgents);
	displayMessage("   @ Total agents        : " + totalAgents);
	displayMessage("   @ Shipwrecked         : " + shipwrecked);
	displayMessage("");
    }

    /**
     * Prints out a "Daemon message".
     *
     * @param msg The message to print.
     */
    final void displayMessage(String msg) {
	getLogStream().println("[DAEMON " + port + "] " + msg);
    }

    /**
     * Returns true if the argument is a valid directive, false otherwise.
     *
     * @param directive The directive to verify.
     * @return True if the argument is a valid directive, false otherwise.
     */
    private static final boolean isValidDirective(String directive) {
	if(directive == null)
	    return false;
	if(directive.length() == 0)
	    return false;
	if(directive.charAt(0) != '-')
	    return false;
	return true;
    }

    /**
     * Parses the argument list and returns a Hashtable of parameters to use
     * for the Daemon.
     *
     * @param args The argument list to parse.
     * @return A Hashtable of parameters to use for the Daemon.
     */
    private static final Hashtable parseParams(String[] args) {
	if(args.length == 0)
	    return null;
	int argNum = 0;
	int lastArg = argNum;
	Hashtable params = new Hashtable();
	while(argNum <= args.length) {
	    if((argNum == args.length) ||
	       (args[argNum].charAt(0) == '-')) {
		if(argNum != lastArg) {
		    String opts = "";
		    for(int i = lastArg + 1; i < argNum; i++)
			opts += args[i] + " ";
		    String directive = args[lastArg];
		    if(!isValidDirective(directive))
			throw new IllegalArgumentException(directive);
		    String prev = (String)params.get(directive);
		    if(prev != null)
			opts = prev + " " + opts;
		    params.put(directive, opts);
		}
		lastArg = argNum;
	    }
	    argNum++;
	}
	return params;
    }

    /**
     * Returns a list of arguments for a given option inside of the given
     * hashtable.
     *
     * @param table The parameter table.
     * @param opt   The option to look for.
     */
    private static final Vector getOptionList(Hashtable table, String opt) {
	return getOptionList(table, opt, null);
    }

    /**
     * Returns a list of arguments for a given option inside of the given
     * hashtable.
     *
     * @param table The parameter table.
     * @param opt   The option to look for.
     */
    private static final Vector getOptionList(Hashtable table, String opt,
					      Vector returnValue) {
	String args = (String)table.remove(opt);
	if(args == null)
	    return returnValue;
	if(returnValue == null)
	    returnValue = new Vector();
	StringTokenizer tokenizer = new StringTokenizer(args);
	while(tokenizer.hasMoreTokens())
	    returnValue.addElement(tokenizer.nextToken());
	System.out.println("   " + opt + "   " + returnValue);
	return returnValue;
    }

    /**
     * Prints out usage information for running the Daemon directly.
     */
    private static final void printUsage() {
	System.out.println();
	System.out.println("Usage: java " + Daemon.class.getName() +
			   " <options>");
	System.out.println();
	System.out.println("Where <options> includes:");
	System.out.println("   " + OPTION_PORT + " <port number>");
	System.out.println("   " + OPTION_SERVICES + " <service list>");
	System.out.println("   " + OPTION_AGENTS + " <agent list>");
	System.out.println("   " + OPTION_CONFIG + " <XML config file>");
	System.out.println("   " + OPTION_LOG + " <output log file>");
	System.out.println("   " + OPTION_VERBOSE + " <verbose true/false>");
	System.out.println();
    }

    /**
     * Starts up a Daemon with the given options contained in the Hashtable.
     * All options must be stored in the Hashtable as Strings.
     * <br>
     * <b>FIXME: add more comments.</b>
     */
    public static final void startDaemon(Hashtable options)
	throws IOException, IllegalArgumentException {

	// use a copy of the table since we're going to be modifying it
	options = (Hashtable)options.clone();

	// get the config file (if any)
	DaemonConfig config = null;
	Vector configList = getOptionList(options, OPTION_CONFIG);
	if((configList != null) && (configList.size() > 0)) {
	    if(configList.size() > 1)
		throw new IllegalArgumentException("Only one config file may" +
						   " be given with the " +
						   OPTION_CONFIG + " option.");
	    String configFile = (String)configList.elementAt(0);
	    config = DaemonConfig.parse(configFile);
	}

	// get the port to run the Daemon on
	int port = 0;
	if(config != null)
	    port = config.getPort();
	Vector portList = getOptionList(options, OPTION_PORT);
	if((port == 0) && ((portList == null) || (portList.size() == 0)))
	    throw new IllegalArgumentException("No port specified.");
	if((portList != null) && (portList.size() > 1))
	    throw new IllegalArgumentException("Only one port can be " +
					       "specified with the " +
					       OPTION_PORT + " option.");
	if((portList != null) && (portList.size() > 0)) {
	    String portString = (String)portList.elementAt(0);
	    try {
		port = Integer.parseInt(portString);
	    } catch(NumberFormatException nfe) {
		throw new IllegalArgumentException("Failed to parse port " +
						   "number:" + portString);
	    }
	}
	    // get the output logfile
	Vector outputList = getOptionList(options, OPTION_LOG);
	String outputFile = null;
	if(config != null)
	    outputFile = config.getLog();
	if((outputList != null) && (outputList.size() > 1))
	    throw new IllegalArgumentException("Only one output file can be " +
					       "specified with the " +
					       OPTION_LOG + " option.");
	if((outputList != null) && (outputList.size() > 0))
	    outputFile = (String)outputList.elementAt(0);

	// get the verbosity flag if the output file is given
	Vector verbosityList = getOptionList(options, OPTION_VERBOSE);
	boolean verbose = true;
	if(config != null)
	    verbose = config.getVerbose();
	if((verbosityList != null) && (verbosityList.size() > 0)) {
	    if(verbosityList.size() > 1)
		throw new IllegalArgumentException("Only one verbose flag " +
						   "can be given with the " +
						   OPTION_VERBOSE +" option.");
	    String verbosityString = (String)verbosityList.elementAt(0);
	    verbosityString = verbosityString.toLowerCase();
	    if((verbosityString.equals("false")) ||
	       (verbosityString.equals("off")) ||
	       (verbosityString.equals("0")))
		verbose = false;
	    else if((verbosityString.equals("true")) ||
		    (verbosityString.equals("on")) ||
		    (verbosityString.equals("1")))
		verbose = true;
	    else
		throw new IllegalArgumentException("Unknown verbosity flag:" +
						   verbosityString);
	}
	if((outputFile == null) && !verbose) {
	    System.out.println("No output file specified - enabling " +
			       "verbose mode.");
	    verbose = true;
	}
	
	// get the list of services to start
	Vector serviceList = getOptionList(options, OPTION_SERVICES,
					   (config == null) ? null :
					   config.getServices());

	// get the list of agents to start
	Vector agentList = getOptionList(options, OPTION_AGENTS,
					 (config == null) ? null :
					 config.getAgents());

	// see if there were any other options passed in
	Enumeration leftOvers = options.keys();
	if(leftOvers.hasMoreElements())
	    throw new IllegalArgumentException("Uknown option: " +
					       leftOvers.nextElement());

	// start it all up!
	FileOutputStream logStream = null;
	if(outputFile != null)
	    logStream = new FileOutputStream(outputFile);
	Daemon daemon = Daemon.getInstance(port);
	if(config != null)
	    daemon.remoteClassLoading = config.getRemoteClassLoading();
	daemon.out = new DaemonLogger(logStream);
	daemon.out.setEchoEnabled(verbose);

	// start the services
	if(serviceList != null) {
	    int numServices = serviceList.size();
	    for(int i = 0; i < numServices; i++) {
		try {
		    Object serviceDesc = serviceList.elementAt(i);
		    DaemonService service = null;
		    if(serviceDesc instanceof ServiceParams) {
			ServiceParams params = (ServiceParams)serviceDesc;
			daemon.displayMessage("Loading service: " +
					      params.getType());
			service = (DaemonService)params.newInstance();
		    } else if(serviceDesc instanceof String) {
			String serviceName = (String)serviceDesc;
			daemon.displayMessage("Loading service: " +
					      serviceName);
			Class clazz = Class.forName(serviceName);
			service = (DaemonService)clazz.newInstance();
		    } else {
			String message = "Failed to construct service from " +
			    "a " + serviceDesc.getClass().getName();
			throw new IllegalArgumentException(message);
		    }
		    daemon.startService(service);
		} catch(Exception e) {
		    e.printStackTrace();
		}
	    }
	}
	// start the agents
	if(agentList != null) {
	    int numAgents = agentList.size();
	    for(int i = 0; i < numAgents; i++) {
		try {
		    Object agentDesc = agentList.elementAt(i);
		    Agent agent = null;
		    if(agentDesc instanceof AgentParams) {
			AgentParams params = (AgentParams)agentDesc;
			daemon.displayMessage("Loading agent: " +
					      params.getType());
			agent = (Agent)params.newInstance();
		    } else if(agentDesc instanceof String) {
			String agentName = (String)agentDesc;
			daemon.displayMessage("Loading agent: " +
					      agentName);
			Class clazz = Class.forName(agentName);
			agent = (Agent)clazz.newInstance();
		    } else {
			String message = "Failed to construct agent from a " +
			    agentDesc.getClass().getName();
			throw new IllegalArgumentException(message);
		    }
		    daemon.startAgent(agent);
		} catch(Exception e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    /**
     * The main entry point for the OsMoSys application.
     */
    public static void main(String[] args) throws Exception {
	Hashtable params = parseParams(args);
	if(params == null) {
	    printUsage();
	    System.exit(-1);
	}

	try {
	    startDaemon(params);
	} catch(IllegalArgumentException iae) {
	    System.out.println(iae.getMessage());
	    printUsage();
	    System.exit(-1);
	}
    }
}
