/*****************************************************************************
 * net.openai.ai.agent.DaemonClassLoader
 *****************************************************************************
 * @author  Thornhalo
 * 2001 OpenAI Labs
 *****************************************************************************
 * $Log: DaemonClassLoader.java,v $
 * Revision 1.9  2002/10/15 02:35:54  thornhalo
 * Added the cvs log tags to all of the files.
 *
 *****************************************************************************/
package net.openai.ai.agent;

import java.util.*;
import java.io.*;
import java.util.jar.*;
import java.util.zip.*;
import java.lang.reflect.*;
import java.security.SecureClassLoader;


/**
 * This is a ClassLoader for the Daemon.  Primarily, this class is used for
 * caching raw class data to be forwarded to other hosts that request it
 * and for resolving agents that the local host doesn't have definitions for.
 */
final class DaemonClassLoader extends SecureClassLoader {

    /** A class for holding the source of classes. */
    private static final class ClassSource {
	
	/** The source host. */
	String host;

	/** The source host port. */
	int port;

	/** The local Daemon port. */
	int daemonPort;

	/** The data. */
	byte[] data;

	/**
	 * Constructs a new ClassSource for the given host and port.
	 */
	ClassSource(String host, int port, int daemonPort, byte[] data) {
	    this.host = host;
	    this.port = port;
	    this.daemonPort = daemonPort;
	    this.data = data;
	}
    }

    /** The single instance of the ClassLoader. */
    static DaemonClassLoader instance = new DaemonClassLoader();

    /** A "cache" of data. */
    private Hashtable cache = new Hashtable();

    /** The "queue" of unresolved agent data. */
    private Vector queue = new Vector();

    /** Set this flag to enable/disable the remote class loading. */
    private boolean enabled = true;


    /**
     * Constructs a new DaemonClassLoader with the system ClassLoader as
     * the delagatee.
     */
    DaemonClassLoader() {
    }

    /**
     * Displays a message.
     */
    private final void displayMessage(String msg) {
	System.out.println("[CLASSLOADER] " + msg);
    }

    /**
     * Sets the class loader to to enabled or disabled.
     *
     * @param enabled The new enabled status for the class loader.
     */
    public final void setEnabled(boolean enabled) {
	this.enabled = enabled;
    }

    /**
     * Returns the enabled status for the class loader.
     *
     * @return The enabled status for the class loader.
     */
    public final boolean getEnabled() {
	return enabled;
    }

    /**
     * Queues an agent that needs to be resolved.  If the class loader is not
     * enabled, the AgentGram is ignored.
     *
     * @param gram The AgentGram that contains the serialized agent.
     */
    final void queueAgent(AgentGram gram) {
	if(enabled) {
	    if(!resolveAgent(gram)) {
		synchronized(queue) {
		    queue.addElement(gram);
		}
	    }
	}
    }

    /**
     * Attempts to resolve the agents contained in the AgentGram queue.
     */
    private void resolveQueue() {
	synchronized(queue) {
	    int size = queue.size();
	    for(int i = 0; i < queue.size();) {
		if(resolveAgent((AgentGram)queue.elementAt(i))) {
		    queue.removeElementAt(i);
		} else {
		    i++;
		}
	    }
	}
    }

    /**
     * Attempts to resolve the MobileAgent contained in an AgentGram.
     */
    private boolean resolveAgent(AgentGram gram) {
	boolean resolved = true;
	Daemon daemon = null;
	try {
	    daemon = Daemon.getInstance(gram.port);
	    ByteArrayInputStream buff = new ByteArrayInputStream(gram.data);
	    ObjectInputStream in = new DaemonObjectInputStream(buff);
	    MobileAgent agent = (MobileAgent)in.readObject();
	    displayMessage("Starting agent: " + agent.getShortDescription());
	    daemon.receiveAgent(agent);
	} catch(ClassCastException cce) {
	    displayMessage("!!! Not a MobileAgent: " + cce);
	} catch(ClassNotFoundException cnfe) {
	    String classname = cnfe.getMessage();
	    classname = classname.replace('/', '.');
	    if(classname.endsWith(";"))
		classname = classname.substring(1, classname.length() - 1);
	    if(!classname.equals(gram.classname)) {
		ClassSource source = null;
		synchronized(cache) {
		    source = (ClassSource)cache.get(gram.classname);
		}
		displayMessage("Retrieving class " + classname + " from " +
			       source.host);
		daemon.startAgent(new ClassRetrievalAgent(source.host,
							  source.port,
							  classname));
	    }
	    resolved = false;
	} catch(NoClassDefFoundError ncdfe) {
	    String classname = ncdfe.getMessage();
	    classname = classname.replace('/', '.');
	    if(classname.endsWith(";"))
		classname = classname.substring(1, classname.length() - 1);
	    if(!classname.equals(gram.classname)) {
		ClassSource source = null;
		synchronized(cache) {
		    source = (ClassSource)cache.get(gram.classname);
		}
		displayMessage("Retrieving class " + classname + " from " +
			       source.host);
		daemon.startAgent(new ClassRetrievalAgent(source.host,
							  source.port,
							  classname));
	    }
	    resolved = false;
	} catch(OptionalDataException ode) {
	    displayMessage("!!! Not object: " + ode);
	} catch(IOException ioe) {
	    displayMessage("!!! Error handling gram: " + ioe);
	    ioe.printStackTrace();
	}
	return resolved;
    }

    /**
     * Saves out the class data to a file.
     */
    private final void saveClassData(File file, byte[] data) {
	    try {
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(data, 0, data.length);
		fos.close();
	    } catch(IOException ioe) {
	    }
    }

    /**
     * Receives a ClassRetrievalAgent and attempts to resolve the class data
     * it contains and launch any queued agents it can now instantiate.
     */
    final void receiveClassRetrievalAgent(ClassRetrievalAgent agent) {
	if(!enabled) {
	    displayMessage("Warning: ignoring class data for " +
			   agent.className + " from " + agent.classSource);
	    return;
	}
	displayMessage("Received class data: " + agent.className +
		       " length: " + agent.classData.length);
	synchronized(cache) {
	    if(cache.get(agent.className) == null)
		cache.put(agent.className,
			  new ClassSource(agent.classSource,
					  agent.classSourcePort,
					  agent.daemon.getPort(),
					  agent.classData));
	}
	try {
	    //displayMessage("
	    Class.forName(agent.className, true, this);
	    //displayMessage("Agent Class.forName succeeded: " +
	    //	   agent.className);
	    resolveQueue();
	} catch(ClassNotFoundException cnfe) {
	    Daemon daemon = agent.daemon;
	    String classname = cnfe.getMessage();
	    classname = classname.replace('/', '.');
	    if(classname.endsWith(";"))
		classname = classname.substring(1, classname.length() - 1);
	    if(!classname.equals(agent.className)) {
		ClassSource source = null;
		synchronized(cache) {
		    source = (ClassSource)cache.get(agent.className);
		}
		displayMessage("Retrieving class " + classname + " from " +
			       source.host);
		daemon.startAgent(new ClassRetrievalAgent(source.host,
							  source.port,
							  classname));
	    }
	} catch(NoClassDefFoundError ncdfe) {
	    Daemon daemon = agent.daemon;
	    String classname = ncdfe.getMessage();
	    classname = classname.replace('/', '.');
	    if(classname.endsWith(";"))
		classname = classname.substring(1, classname.length() - 1);
	    if(!classname.equals(agent.className)) {
		ClassSource source = null;
		synchronized(cache) {
		    source = (ClassSource)cache.get(agent.className);
		}
		displayMessage("Retrieving class " + classname + " from " +
			       source.host);
		daemon.startAgent(new ClassRetrievalAgent(source.host,
							  source.port,
							  classname));
	    }
	} catch(Exception e) {
	    e.printStackTrace();
	} catch(Throwable t) {
	    t.printStackTrace();
	}
    }

    /**
     * Returns a list of jar/zip files or directories in which to find a class
     * definition from the classpath.
     */
    private final Vector getClassPath() {
	String classpath = System.getProperty("java.class.path");
	Vector returnValue = null;
	if(classpath != null) {
	    StringTokenizer pathTokenizer =
		new StringTokenizer(classpath, File.pathSeparator);
	    while(pathTokenizer.hasMoreTokens()) {
		String entry = pathTokenizer.nextToken();
		if(entry.length() > 0) {
		    if(returnValue == null)
			returnValue = new Vector();
		    returnValue.addElement(entry);
		}
	    }
	}
	return returnValue;
    }

    /**
     * Returns the Class for a given classname.
     *
     * @param name The name of the Class.
     * @return The Class for the given classname.
     */
    protected final Class findClass(String name)
	throws ClassNotFoundException {

	// find the loaded class if it exists already
	//Class c = findLoadedClass(name);
	//if(c != null)
	//return c;

	//displayMessage("Looking for class: " + name);

	// find the class definition if it exists in the cache
	byte[] b = getClassData(name);
	if(b == null)
	    throw new ClassNotFoundException(name);
	
	// we made it this far, now try to define and resolve the class
	try {
	    Class c = defineClass(name, b, 0, b.length);
	    //displayMessage("Defined: " + name);
	    resolveClass(c);
	    //displayMessage("Resolved: " + name + " length: " + b.length);
	    if(Proxy.isProxyClass(c))
		displayMessage("ProxyClass: " + name);
	    return c;
	} catch(NoClassDefFoundError ncdfe) {
	    String classname = ncdfe.getMessage();
	    throw new ClassNotFoundException(classname);
	    //displayMessage("!!! No class definition found: " + classname);
	    //ncdfe.printStackTrace();
	} catch(LinkageError le) {
	    displayMessage("!!! Linkage error: " + le);
	} catch(Exception e) {
	    e.printStackTrace();
	}

	throw new ClassNotFoundException(name);
    }

    /**
     * Returns the class data if it exists for a class.
     *
     * @param classname The name of the class.
     * @return The raw class data or null if not found.
     */
    final byte[] getClassData(String classname) {
	synchronized(cache) {
	    ClassSource s = (ClassSource)cache.get(classname);
	    if(s != null)
		return s.data;
	    byte[] b = findClassData(classname);
	    if(b != null)
		cache.put(classname, new ClassSource(null, 0, 0, b));
	    return b;
	}
    }

    /**
     * Attempts to load the raw class data for a class from the classpath.
     *
     * @param classname The name of the class.
     * @return The raw class data or null if not found.
     */
    private final byte[] findClassData(String classname) {
	Vector path = getClassPath();
	if(path == null)
	    return null;
	int pathSize = path.size();
	for(int i = 0; i < pathSize; i++) {
	    String filename = (String)path.elementAt(i);
	    byte[] b = findClassData(new File(filename), classname);
	    if(b != null)
		return b;
	}
	return null;
    }

    /**
     * Attempts to load class data from a file or directory.
     *
     * @param file The file or directory.
     * @return The raw class data or null if it is not found.
     */
    private final byte[] findClassData(File file, String classname) {
	if(!file.exists())
	    return null;
	if(!file.canRead())
	    return null;
	if(file.isDirectory()) {
	    classname = classname.replace('.', File.separatorChar) + ".class";
	    file = new File(file, classname);
	    if(file.exists()) {
		displayMessage("Local fetch: " + file);
		try {
		    return load(new FileInputStream(file));
		} catch(IOException ioe) {
		    ioe.printStackTrace();
		}
	    }
	} else if(file.isFile()) {
	    try {
		JarFile jarfile = new JarFile(file);
		//printEntries(jarfile);
		classname = classname.replace('.', '/') + ".class";
		JarEntry jarentry = jarfile.getJarEntry(classname);
		if(jarentry == null)
		    return null;
		displayMessage("Local fetch: " + classname + " from " + file);
		return load(jarfile.getInputStream(jarentry));
	    } catch(IOException ioe) {
	    }
	}
	return null;
    }

    /**
     * For debugging.
     */
    private final void printEntries(JarFile jarfile) {
	try {
	    Enumeration entries = jarfile.entries();
	    while(entries.hasMoreElements()) {
		ZipEntry entry = (ZipEntry)entries.nextElement();
		displayMessage("> Entry: " + entry.getName());
	    }
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Loads all data from a stream.
     *
     * @param is The stream to load from.
     * @return An array of bytes loaded from the stream.
     */
    private final byte[] load(InputStream is) throws IOException {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	int c = is.read();
	while(c != -1) {
	    baos.write(c);
	    c = is.read();
	}
	return baos.toByteArray();
    }

    /**
     * A main for testing.
     */
    public static void main(String args[]) throws Exception {
	Vector path = instance.getClassPath();
	System.out.println("Classpath: " + path);
	byte[] classdata =
	    instance.findClassData(args[0]);
	if(classdata != null) {
	    FileOutputStream fos = new FileOutputStream("temp.clz");
	    fos.write(classdata, 0, classdata.length);
	    fos.close();
	} else {
	    System.out.println("Failed to find class data.");
	}
    }
}
