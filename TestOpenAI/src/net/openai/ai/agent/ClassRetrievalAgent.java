/*****************************************************************************
 * net.openai.ai.agent.ClassRetrievalAgent
 *****************************************************************************
 * @author  Thornhalo
 * 2001 OpenAI Labs
 *****************************************************************************
 * $Log: ClassRetrievalAgent.java,v $
 * Revision 1.8  2002/10/15 02:35:54  thornhalo
 * Added the cvs log tags to all of the files.
 *
 *****************************************************************************/
package net.openai.ai.agent;

import java.io.*;
import java.net.*;
import java.util.*;


/**
 * This is an agent that will be used to retrieve Class definition data from
 * a remote host.
 */
final class ClassRetrievalAgent extends MobileAgent {

    /** indicates that we're at the starting state. */
    private static final int START = 0;

    /** indicates that we're at the RETRIEVAL state. */
    private static final int RETRIEVAL = 1;

    /** indicates that we're at the RETURNED state. */
    private static final int RETURNED = 2;

    /** The port the agent came in on. */
    private int homePort;

    /** our internal state. */
    private int state = START;

    /** The name of the class that we're looking for. */
    String className;

    /** The host that has the Class source. */
    String classSource;

    /** The port of the class source. */
    int classSourcePort;

    /** The class data. */
    byte[] classData = null;


    /**
     * Constructs a new ClassRetrievalAgent.
     */
    ClassRetrievalAgent(String classSource, int classSourcePort,
			String className) {
	this.classSource = classSource;
	this.classSourcePort = classSourcePort;
	this.className = className;
    }

    /**
     * The main body of the Agent.
     */
    protected final void executeAgent() {
	switch(state) {
	case START:
	    doStartTask();
	    break;
	case RETRIEVAL:
	    doRetrievalTask();
	    break;
	case RETURNED:
	    doReturnedTask();
	    break;
	default:
	    displayMessage("INVALID State: " + state);
	    halt();
	}
    }

    /**
     * What the agent should to for its start task.
     */
    private final void doStartTask() {
	state = RETRIEVAL;
	homePort = getDaemon().getPort();
	for(int i = 0; i < 6; i++) {
	    try {
		migrateTo(classSource, classSourcePort);
	    } catch(Exception e) {
		try {
		    Thread.sleep(10000);
		} catch(InterruptedException ie) {
		}
	    }
	}
    }

    /**
     * What the agent should do for its retrieval task.
     */
    private final void doRetrievalTask() {
	classData = DaemonClassLoader.instance.getClassData(className);
	if(classData == null) {
	    displayMessage("Failed to find: " + className);
	    halt();
	}
	state = RETURNED;
	try {
	    migrateTo(getSpawnHostName(), homePort);
	} catch(Exception e) {
	    displayMessage("Cannot return class " + className + ": " + e);
	    halt();
	}
    }

    /**
     * What the agent should do upon returning.
     */
    private final void doReturnedTask() {
	DaemonClassLoader.instance.receiveClassRetrievalAgent(this);
    }

    /**
     * Returns a shot description of the Agent.
     */
    public final String getShortDescription() {
	return "ClassRetriever";
    }
}
