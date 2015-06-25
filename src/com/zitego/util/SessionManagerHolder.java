package com.zitego.util;

/**
 * This is an interface to be implemented by any class that has a session manager.
 * Classes like SessionObjectTag can use it to retrieve a session manager.
 *
 * @author John Glorioso
 * @version $Id: SessionManagerHolder.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public interface SessionManagerHolder
{
    /**
     * Returns a SessionManager object.
     *
     * @return SessionManager
     */
    public SessionManager getSessionManager();
}