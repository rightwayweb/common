package com.zitego.util;

import java.util.ArrayList;

/**
 * Encapsulates primitive types and creates an array of Objects out of them.
 *
 * @author John Glorioso
 * @version $Id: ObjectArray.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class ObjectArray extends ArrayList
{
    /**
     * Creates a new ObjectArray from an array of ints.
     *
     * @param int[] The array.
     */
    public ObjectArray(int[] in)
    {
        if (in == null) in = new int[0];
        for (int i=0; i<in.length; i++)
        {
            add( new Integer(in[i]) );
        }
    }

    /**
     * Creates a new ObjectArray from an array of shorts.
     *
     * @param short[] The array.
     */
    public ObjectArray(short[] in)
    {
        if (in == null) in = new short[0];
        for (int i=0; i<in.length; i++)
        {
            add( new Short(in[i]) );
        }
    }

    /**
     * Creates a new ObjectArray from an array of longs.
     *
     * @param long[] The array.
     */
    public ObjectArray(long[] in)
    {
        if (in == null) in = new long[0];
        for (int i=0; i<in.length; i++)
        {
            add( new Long(in[i]) );
        }
    }

    /**
     * Creates a new ObjectArray from an array of doubles.
     *
     * @param double[] The array.
     */
    public ObjectArray(double[] in)
    {
        if (in == null) in = new double[0];
        for (int i=0; i<in.length; i++)
        {
            add( new Double(in[i]) );
        }
    }

    /**
     * Creates a new ObjectArray from an array of floats.
     *
     * @param float[] The array.
     */
    public ObjectArray(float[] in)
    {
        if (in == null) in = new float[0];
        for (int i=0; i<in.length; i++)
        {
            add( new Float(in[i]) );
        }
    }

    /**
     * Creates a new ObjectArray from an array of chars.
     *
     * @param char[] The array.
     */
    public ObjectArray(char[] in)
    {
        if (in == null) in = new char[0];
        for (char i=0; i<in.length; i++)
        {
            add( new Character(in[i]) );
        }
    }

    /**
     * Creates a new ObjectArray from an array of bytes.
     *
     * @param byte[] The array.
     */
    public ObjectArray(byte[] in)
    {
        if (in == null) in = new byte[0];
        for (byte i=0; i<in.length; i++)
        {
            add( new Byte(in[i]) );
        }
    }
}