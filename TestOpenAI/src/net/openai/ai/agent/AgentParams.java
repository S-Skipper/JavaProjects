/*****************************************************************************
 * net.openai.ai.agent.AgentParams
 *****************************************************************************
 * @author  Thornhalo
 * 2002 OpenAI Labs
 *****************************************************************************
 * $Log: AgentParams.java,v $
 * Revision 1.3  2002/10/21 02:47:04  thornhalo
 * First-round implementation of the Daemon config file option.
 *
 * Revision 1.2  2002/10/19 17:17:25  thornhalo
 * Finished the xml parsing for the Daemon configuration file.
 *
 * Revision 1.1  2002/10/19 02:44:42  thornhalo
 * initial check-in
 *
 *****************************************************************************/
package net.openai.ai.agent;

import java.io.*;
import java.net.*;
import java.util.*;
import java.beans.*;
import java.lang.reflect.*;


/**
 * This class is used to represent the parameters to construct a particular
 * type of agent.
 */
public class AgentParams implements Serializable {

    /** The type of agent. */
    private String type = null;

    /** The list of property names. */
    private Vector properties = new Vector();
    
    /** A mapping of parameter names to their values as text. */
    private HashMap valueMap = new HashMap();

    /**
     * Constructs a new AgentParams object.
     *
     * @param type The fully-qualified class name for the agent type.
     */
    public AgentParams(String type) {
	if((type == null) || (type.length() == 0))
	    throw new NullPointerException("Type cannot be null or empty!");
	this.type = type;
    }

    /**
     * Returns the fully-qualified class name for the agent type.
     */
    public final String getType() {
	return type;
    }

    /**
     * Returns the list of property names to be set for the agent.
     */
    public final Vector getProperties() {
	synchronized(properties) {
	    return (Vector)properties.clone();
	}
    }

    /**
     * Returns the value of a particular property as text.
     */
    public final String getValue(String name) {
	synchronized(properties) {
	    return (String)valueMap.get(name);
	}
    }

    /**
     * Adds a property to the list of properties.
     */
    public final void setValue(String name, String value) {
	synchronized(properties) {
	    if(!properties.contains(name))
		properties.addElement(name);
	    valueMap.put(name, value);
	}
    }

    /**
     * Constructs a new Agent from the type and properties that the AgentParams
     * object contains.
     */
    public Agent newInstance() {
	Agent agent = null;
	Vector properties = getProperties();
	try {
	    Class clazz = Class.forName(type);
	    agent = (Agent)clazz.newInstance();
	    BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
	    PropertyDescriptor[] descs = beanInfo.getPropertyDescriptors();
	    for(int i = 0; i < descs.length; i++) {
		PropertyDescriptor desc = descs[i];
		String name = desc.getName();
		if(properties.contains(name)) {
		    String asText = getValue(name);
		    Class pClazz = desc.getPropertyType();
		    PropertyEditor editor =
			PropertyEditorManager.findEditor(pClazz);
		    editor.setAsText(asText);
		    Object value = editor.getValue();
		    Method setter = desc.getWriteMethod();
		    Object args[] = {value};
		    setter.invoke(agent, args);
		}
	    }
	} catch(Exception e) {
	    e.printStackTrace();
	}
	return agent;
    }

    /**
     * Returns a String representation of this instance.
     */
    public String toString() {
	return "<" + type + ">" + valueMap;
    }
}
