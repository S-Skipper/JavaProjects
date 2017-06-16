/*****************************************************************************
 * net.openai.ai.agent.DaemonLogger
 *****************************************************************************
 * @author  Thornhalo
 * 2002 OpenAI Labs
 *****************************************************************************
 * $Log: DaemonLogger.java,v $
 * Revision 1.3  2002/10/15 02:35:55  thornhalo
 * Added the cvs log tags to all of the files.
 *
 *****************************************************************************/
package net.openai.ai.agent;

import java.io.*;
import java.net.*;
import java.util.*;


/**
 * This is the print stream that will be used for logging to a file or to
 * stdout for the Daemon.
 */
final class DaemonLogger extends PrintStream {

    /** Indicates that the DaemonLogger should echo to the screen. */
    private boolean echoEnabled = false;

    /** Indicates that the DaemonLogger is writing to stdout or stderr, so
	the echoEnabled should be ignored. */
    private boolean writingToStd = false;


    /**
     * Constructs a new DaemonLogger for the given OutputStream.
     *
     * @param os The output stream to write to.
     */
    DaemonLogger(OutputStream os) {
	super(os == null ? System.out : os);
	if((os == null) || (os == System.out) || (os == System.err))
	    writingToStd = true;
    }

    /**
     * Constructs a new DaemonLogger with stdout as the OutputStream.
     */
    DaemonLogger() {
	this(null);
    }

    /**
     * Sets the "echo enabled" status for the logger.
     *
     * @param enabled True to enable echo, false to disable.
     */
    final void setEchoEnabled(boolean enabled) {
	this.echoEnabled = enabled;
    }

    /**
     * Returns the "echo enabled" status for the logger.
     *
     * @return True if echo is enabled, false otherwise.
     */
    final boolean getEchoEnabled() {
	return echoEnabled;
    }

    /**
     * Writes len bytes from the specified byte array starting at offset off
     * to this DaemonLogger.
     *
     * @param b   The data.
     * @param off The start offset in the data.
     * @param len The number of bytes to write.
     */
    public final void write(byte[] b, int off, int len) {
	if(writingToStd) {
	    super.write(b, off, len);
	} else {
	    super.write(b, off, len);
	    if(echoEnabled)
		System.out.write(b, off, len);
	}
    }
}
