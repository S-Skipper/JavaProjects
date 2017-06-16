/*****************************************************************************
 * net.openai.ai.agent.ServiceParams
 *****************************************************************************
 * @author  Thornhalo
 * 2002 OpenAI Labs
 *****************************************************************************
 * $Log: ServiceParams.java,v $
 * Revision 1.3  2002/10/21 02:47:05  thornhalo
 * First-round implementation of the Daemon config file option.
 *
 * Revision 1.2  2002/10/19 17:17:25  thornhalo
 * Finished the xml parsing for the Daemon configuration file.
 *
 * Revision 1.1  2002/10/19 02:49:40  thornhalo
 * initial check-in
 *
 *****************************************************************************/
package net.openai.ai.agent;

/**
 * This class is used to represent the parameters to construct a particular
 * type of daemon service.
 */
public class ServiceParams extends AgentParams {

    /** Indicates whether or not a service will be "public". */
    private boolean makePublic = true;

    /**
     * Constructs a new ServiceParams object
     */
    public ServiceParams(String type) {
	super(type);
    }

    /**
     * Sets whether or not the constructed service will be public.
     */
    public final void setPublic(boolean makePublic) {
	this.makePublic = makePublic;
    }

    /**
     * Returns whether or not the constructed service will be public.
     */
    public final boolean getPublic() {
	return this.makePublic;
    }

    /**
     * Constructs a new DaemonService from the type and properties that the
     * ServiceParams object contains.
     */
    public Agent newInstance() {
	DaemonService service = (DaemonService)super.newInstance();
	service.isPublic = makePublic;
	return service;
    }

    /**
     * Returns a String representation of this instance.
     */
    public String toString() {
	StringBuffer buff = new StringBuffer(super.toString());
	if(makePublic)
	    buff.append("public");
	return buff.toString();
    }
}
