/*****************************************************************************
 * net.openai.ai.agent.DaemonObjectInputStream
 *****************************************************************************
 * @author  Thornhalo
 * 2001 OpenAI Labs
 *****************************************************************************
 * $Log: DaemonObjectInputStream.java,v $
 * Revision 1.5  2002/10/15 02:35:55  thornhalo
 * Added the cvs log tags to all of the files.
 *
 *****************************************************************************/
package net.openai.ai.agent;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;


/**
 * A special ObjectInputStream that will use the DaemonClassLoader for
 * resolution.
 */
final class DaemonObjectInputStream extends ObjectInputStream {
    
    /**
     * Constructs an DaemonObjectInpustStream for the given InputStream.
     *
     * @param in The InputStream to pull data from.
     */
    DaemonObjectInputStream(InputStream in)
	throws IOException, StreamCorruptedException {
	super(in);
    }
    
    /**
     * Attempts to resolve the class.
     *
     * @param v The ObjectStreamClass that contains the class reference.
     */
    protected final Class resolveClass(ObjectStreamClass v)
	throws ClassNotFoundException {
	boolean debug = true;
	return Class.forName(v.getName(), true,
			     DaemonClassLoader.instance);
    }
    
    /**
     * Attempts to resolve a proxy class.
     *
     * @param interfaces The list of interface names that were deserialized in
     *                   the proxy class descriptor.
     */
    /*
    protected Class resolveProxyClass(String[] interfaces)
	throws IOException, ClassNotFoundException {
	System.out.println("???????? Resolving proxy class .. interfaces: " +
			   interfaces);
	Class[] classObjs = new Class[interfaces.length];
	for (int i = 0; i < interfaces.length; i++) {
	    classObjs[i] = Class.forName(interfaces[i], false,
					 DaemonClassLoader.instance);
	}
	try {
	    return Proxy.getProxyClass(DaemonClassLoader.instance, classObjs);
	} catch (IllegalArgumentException e) {
	    throw new ClassNotFoundException(null, e);
	}
    }
    */
}
