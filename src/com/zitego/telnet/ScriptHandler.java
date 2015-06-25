package com.zitego.telnet;

import java.util.Vector;

/**
 * A script handler, that tries to match strings and returns true when
 * it finds the string it searched for.
 *
 * @author John Glorioso
 * @version $Id: ScriptHandler.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 * @deprecated Not longer used.
 */
public class ScriptHandler
{
    /** The current position in the match. */
    private int _matchPosition;
    /** The bytes to look for. */
    private byte[] _match;
    /** Whether we are done or not. */
    private boolean _done;

    /**
     * Setup the parser using the passed string.
     *
     * @param String The string to look for.
     * @throws IllegalArgumentException if the string is null.
     */
    public ScriptHandler(String match) throws IllegalArgumentException
    {
        if (match == null) throw new IllegalArgumentException("Match string cannot be null");
        _match = match.getBytes();
        reset();
    }

    /**
     * Resets for another match.
     */
    public void reset()
    {
        _matchPosition = 0;
        _done = false;
    }

    /**
     * Try to match the byte array against the match string. Returns true if
     * the string was found and false if not.
     *
     * @param byte[] The array of bytes to match against.
     * @param int The amount of bytes in the array.
     * @return boolean
     */
    public boolean match(byte[] s, int length)
    {
        if (_done) return true;
        for (int i=0; !_done && i < length; i++)
        {
            if (s[i] == _match[_matchPosition])
            {
                //The whole thing matched so, return the match answer
                //and reset to use the next match
                if(++_matchPosition >= _match.length)
                {
                    _done = true;
                    return true;
                }
            }
            else
            {
                _matchPosition = 0;
            }
        }
        return false;
    }
}