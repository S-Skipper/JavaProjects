/*****************************************************************************
 * net.openai.ai.agent.HaltException
 *****************************************************************************
 * @author  thornhalo
 * @date    Mon Mar  5 19:18:24 CST 2001
 * 2001 OpenAI Labs
 *****************************************************************************
 * $Log: HaltException.java,v $
 * Revision 1.3  2002/10/15 02:35:55  thornhalo
 * Added the cvs log tags to all of the files.
 *
 *****************************************************************************/
package net.openai.ai.agent;


/**
 * A special type of exception that will halt the execution of an agent.
 */
final class HaltException extends ThreadDeath {
}
