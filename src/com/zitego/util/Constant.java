package com.zitego.util;

import java.io.Serializable;
import java.util.Vector;

/**
 * A class to be extended that represents a constant.
 *
 * @author John Glorioso
 * @version $Id: Constant.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class Constant implements Serializable
{
    /* The int value of the constant. */
    private int _value;
    /** The description of the constant. */
    private String _description;

    /**
     * For extending. It does nothing.
     */
    protected Constant() {}

    /**
     * Creates a constant with a description and value.
     *
     * @param int The value.
     * @param String The description.
     */
    public Constant(int value, String label)
    {
        _value = value;
        _description = label;
    }

    /**
     * For child classes to use.
     *
     * @param String The description.
     */
    public void setDescription(String desc)
    {
        _description = desc;
    }

    /**
     * Returns the description.
     *
     * @return String
     */
    public String getDescription()
    {
        return _description;
    }

    /**
     * Sets the value for the constant.
     *
     * @param int The value.
     */
    public void setValue(int value)
    {
        _value = value;
    }

    /**
     * Returns the value of this constant.
     *
     * @return int
     */
    public int getValue()
    {
        return _value;
    }

    /**
     * Returns the Vector of types. This needs to be extended to get back a
     * populated vector.
     *
     * @return Vector
     */
    public Vector getTypes()
    {
        //Extend this
        throw new IllegalStateException("getTypes must be implemented.");
    }

    /**
     * Returns an Constant based on the id passed in. If the id does not match the id of
     * a constant, then we return null. If there are two constants with the same id, then
     * the first one is returned.
     *
     * @param int The constant id.
     * @param Vector The storage Vector.
     * @return Constant
     */
    protected static Constant evaluate(int id, Vector constants)
    {
        if (constants == null) return null;

        int count = constants.size();
        for (int i=0; i<count; i++)
        {
            Constant ret = (Constant)constants.elementAt(i);
            if ( id == ret.getValue() ) return ret;
        }
        return null;
    }

    /**
     * Returns an Constant based on the description passed in. If the description does not
     * match the description of a constant, then we return null. If there are two constants
     * with the same description, then the first one is returned.
     *
     * @param String The description.
     * @param Vector The storage Vector.
     * @return Constant
     */
    protected static Constant evaluate(String name, Vector constants)
    {
        if (constants == null || name == null) return null;

        int count = constants.size();
        for (int i=0; i<count; i++)
        {
            Constant ret = (Constant)constants.elementAt(i);
            if ( name.equals(ret.getDescription()) ) return ret;
        }
        return null;
    }

    public String toString()
    {
        return "[" + getDescription() + "," + getValue() + "]";
    }
}