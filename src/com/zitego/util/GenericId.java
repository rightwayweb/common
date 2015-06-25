package com.zitego.util;

/**
 * This is a class that represents a generic id. The id is an object that
 * may or may not have a primitive value It is up to the caller to decide which
 * method to call.
 *
 * @author John Glorioso
 */
public class GenericId
{
    /** The root id value. */
    private Object _value;

    /**
     * Creates a new id with the given root object value.
     *
     * @param Object The root object id.
     * @throws IllegalArgumentException if the id is null.
     */
    public GenericId(Object rootId)
    {
        setValue(rootId);
    }

    /**
     * Sets the id value by checking to see what kind of object this is.
     *
     * @param Object The root object id.
     * @throws IllegalArgumentException if the id is null.
     */
    public void setValue(Object val)
    {
        if (val == null) throw new IllegalArgumentException("value cannot be null");
        _value = val;
    }

    /**
     * Returns the id as an int.
     *
     * @return int
     * @throws ClassCastException if the id cannot be converted to an int.
     */
    public int getIntValue() throws ClassCastException
    {
        return ( (Integer)_value ).intValue();
    }

    /**
     * Returns the id as a long.
     *
     * @return long
     * @throws ClassCastException if the id cannot be converted to an long
     */
    public long getLongValue() throws ClassCastException
    {
        return ( (Long)_value ).longValue();
    }

    /**
     * Returns the id as a short.
     *
     * @return short
     * @throws ClassCastException if the id cannot be converted to a short.
     */
    public short getShortValue() throws ClassCastException
    {
        return ( (Short)_value ).shortValue();
    }

    /**
     * Returns the id as a byte.
     *
     * @return byte
     * @throws ClassCastException if the id cannot be converted to a byte.
     */
    public byte getByteValue()
    {
        return ( (Byte)_value ).byteValue();
    }

    /**
     * Returns the id as a float.
     *
     * return float
     * @throws ClassCastException if the id cannot be converted to a float.
     */
    public float getFloatValue() throws ClassCastException
    {
        return ( (Float)_value ).floatValue();
    }

    /**
     * Returns the id as a double.
     *
     * @return double
     * @throws ClassCastException if the id cannot be converted to a double.
     */
    public double getDoubleValue() throws ClassCastException
    {
        return ( (Double)_value ).doubleValue();
    }

    /**
     * Returns the id as a string.
     *
     * @return String
     */
    public String getStringValue()
    {
        return _value.toString();
    }

    /**
     * Returns the id as an object.
     *
     * @return Object
     */
    public Object getValue()
    {
        return _value;
    }
}