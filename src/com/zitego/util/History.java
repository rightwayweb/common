package com.zitego.util;

import java.util.Vector;

/**
 * This class simply keeps track of the String urls in history. Both forward
 * and backward.
 *
 * @author John Glorioso
 * @version $Id: History.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class History extends Vector
{
    /** The history index. */
    private int _index = -1;

    /**
     * Creates a new history object.
     */
    public History()
    {
        super();
    }

    /**
     * Adds a new history entry It doesn't care if it is already in there or not.
     *
     * @param String The entry.
     */
    public void add(String entry)
    {
        super.add(entry);
        _index++;
        if ( _index < size() ) removeRange(_index, size()-1);
    }

    /**
     * Returns whether they can move forward in history from the current spot.
     *
     * @return boolean
     */
    public boolean canMoveForward()
    {
        return _index < size()-1;
    }

    /**
     * Returns whether they can move backward in history from the current spot.
     *
     * @return boolean
     */
    public boolean canMoveBackward()
    {
        return _index > 0;
    }

    /**
     * Moves backward.
     */
    public void moveBackward()
    {
        _index--;
    }

    /**
     * Moves forward.
     */
    public void moveForward()
    {
        _index++;
    }

    /**
     * Returns the current history entry.
     *
     * @return String
     */
    public String getCurrentEntry()
    {
        return (String)get(_index);
    }

    /**
     * Returns the last entry in history or null if it does not exist.
     *
     * @return String
     */
    public String getLastEntry()
    {
        return (canMoveBackward() ? (String)get(_index-1) : null);
    }

    /**
     * Returns the next entry in history or null if it does not exist.
     *
     * @return String
     */
    public String getNextEntry()
    {
        return (canMoveForward() ? (String)get(_index+1) : null);
    }
}