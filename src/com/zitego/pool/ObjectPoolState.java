package com.zitego.pool;

import java.util.Hashtable;

/**
 * A simple data structure containing information about the state of an object pool.
 *
 * @author John Glorioso
 * @version $Id: ObjectPoolState.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class ObjectPoolState
{
    /** The number of objects currently in use. */
    public long objectsInUse;
    /** The number of objects currently available. */
    public long objectsAvailable;
    /** The number of objects that have been expired. */
    public long objectsExpired;
    /** The pool's configuration. */
    public ObjectPoolConfig poolConfig;
    /** The hashtable to contain those objects that are in use. */
    public Hashtable inUseHash;

    public String toString()
    {
        StringBuffer ret = new StringBuffer()
            .append("[InUse: ").append(objectsInUse).append(", Available: ").append(objectsAvailable)
            .append(", Expired: ").append(objectsExpired).append("]");
        return ret.toString();
    }
}