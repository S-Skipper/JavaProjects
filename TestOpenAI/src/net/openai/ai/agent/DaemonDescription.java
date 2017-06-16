/*****************************************************************************
 * net.openai.ai.agent.DaemonDescription
 *****************************************************************************
 * @author  Thornhalo
 * 2001 OpenAI Labs
 *****************************************************************************
 * $Log: DaemonDescription.java,v $
 * Revision 1.3  2002/10/15 02:35:55  thornhalo
 * Added the cvs log tags to all of the files.
 *
 *****************************************************************************/
package net.openai.ai.agent;

import java.io.*;
import java.util.*;


/**
 * This class encapsulates description data about a Daemon.  This will most
 * commonly be used by an agent to find out what DaemonServices are available
 * to an agent on both local and remote Daemons.
 */
public final class DaemonDescription implements Serializable {

    /** The IP address of the remote Daemon. */
    private String address;

    /** The hostname of the remote Daemon. */
    private String host;

    /** The port of the remote Daemon. */
    private int port;

    /** A list of known services for the remote Daemon. */
    private String[] services;

    
    /**
     * Constructs a DaemonDescription with the given list of properties.
     *
     * @param address  The local host address for the Daemon.
     * @param host     The local host name for the Daemon.
     * @param port     The port the Daemon is listening on.
     * @param services The list of services the Daemon has.
     */
    DaemonDescription(String address, String host, int port,
		      String[] services) {
	this.address = address;
	this.host = host;
	this.port = port;
	this.services = (String[])services.clone();
    }

    /**
     * Returns the IP address for the DaemonDescription.
     *
     * @return The IP address.
     */
    public final String getHostIP() {
	return address;
    }

    /**
     * Returns the hostname for the DaemonDescription.
     *
     * @return The hostname.
     */
    public final String getHostName() {
	return host;
    }

    /**
     * Returns the port for the DaemonDescription.
     *
     * @return The port.
     */
    public final int getPort() {
	return port;
    }

    /**
     * Returns the list of services for the DaemonDescription as an array
     * of Strings.
     *
     * @return The list of services for the DaemonDescription.
     */
    public final String[] getServices() {
	return (String[])services.clone();
    }

    /**
     * Returns a String representation of this Object.
     *
     * @return A String representation of this Object.
     */
    public final String toString() {
	return "Daemon-" + host + "/" + address + ":" + port;
    }
}
