/*****************************************************************************
 * net.openai.ai.agent.DaemonConfig
 *****************************************************************************
 * @author  Thornhalo
 * 2002 OpenAI Labs
 *****************************************************************************
 * $Log: DaemonConfig.java,v $
 * Revision 1.6  2002/10/26 01:26:55  thornhalo
 * Removed debugging code and added more JavaDoc comments.
 *
 * Revision 1.5  2002/10/24 04:18:52  thornhalo
 * Minor bug fixes and updates to the RemoteClassLoader example.
 *
 * Revision 1.4  2002/10/22 01:47:53  thornhalo
 * Added the "verbose" and "remoteClassLoading" options to the config file.  Still need to actually add the capability to disable remote classloading in the Daemon itself, though.
 *
 * Revision 1.3  2002/10/21 02:47:05  thornhalo
 * First-round implementation of the Daemon config file option.
 *
 * Revision 1.2  2002/10/19 17:17:25  thornhalo
 * Finished the xml parsing for the Daemon configuration file.
 *
 * Revision 1.1  2002/10/18 02:13:32  thornhalo
 * initial check-in
 *
 *****************************************************************************/
package net.openai.ai.agent;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * This class is used to load the XML-based configuration for the Daemon.
 */
public final class DaemonConfig implements Serializable {

    /** The port to use. */
    private int port = 0;

    /** The log to use. */
    private String log = null;

    /** The verbosity flag. */
    private boolean verbose = false;

    /** The remote classloading flag. */
    private boolean remoteClassLoading = true;

    /** The list of allowed hosts. */
    private Vector allowed = new Vector();

    /** The list of denied hosts. */
    private Vector denied = new Vector();

    /** The list of agents to start up. */
    private Vector agents = new Vector();

    /** The list of services to start up. */
    private Vector services = new Vector();

    /** The original document that the configuration was built from. */
    private Document doc = null;

    /**
     * Constructs a new DaemonConfig.
     *
     * @param doc The Document object to construct the DaemonConfig from.
     */
    private DaemonConfig(Document doc) {
	this.doc = doc;

	Element daemon = doc.getDocumentElement();
	if(daemon.getTagName().equals("daemon")) {
	    // Get the port if specified.
	    try {
		this.port = Integer.parseInt(daemon.getAttribute("port"));
	    } catch(NumberFormatException nfe) {
		System.err.println(nfe);
	    } catch(NullPointerException npe) {
	    }
	    
	    // get the verbosity flag if specified.
	    String verbose = daemon.getAttribute("verbose");
	    if((verbose != null) && (verbose.length() > 0))
		this.verbose = (new Boolean(verbose)).booleanValue();

	    // get the removeClassLoading flag if specified.
	    String remoteClassLoading =
		daemon.getAttribute("remoteClassLoading");
	    if((remoteClassLoading != null) &&
	       (remoteClassLoading.length() > 0))
		this.remoteClassLoading =
		    (new Boolean(remoteClassLoading)).booleanValue();

	    // Get the log file if specified.
	    this.log = daemon.getAttribute("log");
	    if((this.log != null) && (this.log.length() == 0))
		this.log = null;

	    // Get the list of allowed hosts.
	    NodeList allows = daemon.getElementsByTagName("allow");
	    if(allows != null) {
		int numAllow = allows.getLength();
		for(int i = 0; i < numAllow; i++) {
		    Element allow = (Element)allows.item(i);
		    allowed.addElement(allow.getAttribute("host"));
		}
	    }

	    // Get the list of denied hosts.
	    NodeList denys = daemon.getElementsByTagName("deny");
	    if(denys != null) {
		int numDenys = denys.getLength();
		for(int i = 0; i < numDenys; i++) {
		    Element deny = (Element)denys.item(i);
		    denied.addElement(deny.getAttribute("host"));
		}
	    }

	    // Get the list of agents
	    NodeList agents = daemon.getElementsByTagName("agent");
	    if(agents != null) {
		int numAgents = agents.getLength();
		for(int i = 0; i < numAgents; i++) {
		    Element agent = (Element)agents.item(i);
		    AgentParams params =
			new AgentParams(agent.getAttribute("class"));
		    NodeList props = agent.getElementsByTagName("property");
		    if(props != null) {
			int numProps = props.getLength();
			for(int j = 0; j < numProps; j++) {
			    Element prop = (Element)props.item(j);
			    params.setValue(prop.getAttribute("name"),
					    prop.getAttribute("value"));
			}
		    }
		    this.agents.addElement(params);
		}
	    }

	    // Get the list of services
	    NodeList services = daemon.getElementsByTagName("service");
	    if(services != null) {
		int numServices = services.getLength();
		for(int i = 0; i < numServices; i++) {
		    Element service = (Element)services.item(i);
		    ServiceParams params =
			new ServiceParams(service.getAttribute("class"));
		    String makePublic = service.getAttribute("public");
		    if((makePublic != null) && (makePublic.length() > 0))
			params.setPublic((new Boolean(makePublic)).
					 booleanValue());
		    NodeList props = service.getElementsByTagName("property");
		    if(props != null) {
			int numProps = props.getLength();
			for(int j = 0; j < numProps; j++) {
			    Element prop = (Element)props.item(j);
			    params.setValue(prop.getAttribute("name"),
					    prop.getAttribute("value"));
			}
		    }
		    this.services.addElement(params);
		}
	    }
	}
    }

    /**
     * Returns the port to use.  If not specified, 0 is returned.
     *
     * @return The port for the Daemon to use.
     */
    public final int getPort() {
	return port;
    }

    /**
     * Returns the name of the log file to use or null if not specified.
     *
     * @return The name of the log file for the Daemon to use.
     */
    public final String getLog() {
	return log;
    }

    /**
     * Returns the verbosity flag.
     *
     * @return Whether or not the Daemon should be verbose.
     */
    public final boolean getVerbose() {
	return verbose;
    }

    /**
     * Returns the remote classloading flag.
     *
     * @return Whether or not the Daemon should enable remote class loading.
     */
    public final boolean getRemoteClassLoading() {
	return remoteClassLoading;
    }

    /**
     * Returns the list of allowed hosts as a Vector of Strings.
     *
     * @return The list of allowed hosts.
     */
    public final Vector getAllowed() {
	synchronized(allowed) {
	    return (Vector)allowed.clone();
	}
    }

    /**
     * Returns the list of denied hosts as a Vector of Strings.
     *
     * @return The list of denied hosts.
     */
    public final Vector getDenied() {
	synchronized(denied) {
	    return (Vector)denied.clone();
	}
    }

    /**
     * Returns the list of AgentParams that represent the list of agents
     * to start up.
     *
     * @return The list of AgentParams that will be used to start up agents.
     */
    public final Vector getAgents() {
	synchronized(agents) {
	    return (Vector)agents.clone();
	}
    }

    /**
     * Returns the list of ServiceParams that represent the list of daemon
     * services to start up.
     *
     * @return The list of ServiceParams that will be used to start up
     *         services.
     */
    public final Vector getServices() {
	synchronized(services) {
	    return (Vector)services.clone();
	}
    }

    /**
     * Returns a copy of the original Document that was used to construct
     * the DaemonConfig.
     *
     * @return The Document that was used ton construct the DaemonConfig.
     */
    public final Document getDoc() {
	synchronized(doc) {
	    return (Document)doc.cloneNode(true);
	}
    }

    /**
     * Parses an XML file and returns a DaemonConfig representing that
     * file.
     *
     * @param file The XML file to parse.
     * @return The DaemonConfig or null if an error is encountered.
     */
    public static final DaemonConfig parse(String file)
	throws IOException {
	return parse(new File(file));
    }

    /**
     * Parses an xml file and returns a DaemonConfig representing that
     * file.
     *
     * @param file The XML file to parse.
     * @return The DaemonConfig or null if an error is encountered.
     */
    public static final DaemonConfig parse(File file)
	throws IOException {
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	try {
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document        doc     = builder.parse(file);
	    return new DaemonConfig(doc);
	} catch(ParserConfigurationException pce) {
	    pce.printStackTrace();
	} catch(SAXException se) {
	    se.printStackTrace();
	}
	return null;
    }
}
