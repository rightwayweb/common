package com.zitego.util;

import java.text.Format;
import java.text.FieldPosition;
import java.text.ParsePosition;

/**
 * A format for a boolean value.
 *
 * @author John Glorioso
 * @version $Id: BooleanFormat.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class BooleanFormat extends Format
{
	/** The String value for true. */
	protected String _true = null;
	/** The String value for false. */
	protected String _false = null;

	/**
	 * Creates a new Boolean format with a string to return for true and a
	 * string to return for false.
	 *
	 * @param t The value for true.
	 * @param f The value for false.
	 */
	public BooleanFormat(String t, String f)
	{
		super();
		_true = t;
		_false = f;
	}

	/**
	 * Formats the given object and returns the true or false string value. If
	 * the object is a Boolean it evaluates the booleanValue of it. If it is a
	 * number, it will return the true value for a positive number and the false
	 * value for non-positive. If it is anything else, it will return the true
	 * value if it is not null and the false value otherwise.
	 *
	 * @param obj The object to format.
	 * @param toAppendTo What to append the formatted value to.
	 * @param notUsed Not used.
	 * @return String
	 */
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition notUsed)
	{
		if (obj instanceof Boolean)
		{
			return toAppendTo.append( format(((Boolean)obj).booleanValue()) );
		}
		else if (obj instanceof Number)
		{
		    return toAppendTo.append( format(((Number)obj).floatValue() > 0) );
		}
		else if (obj instanceof String)
		{
		    return toAppendTo.append( format(("1".equals((String)obj) || "true".equalsIgnoreCase((String)obj))) );
		}
		else
		{
			return toAppendTo.append( format((obj != null)) );
		}
	}

	/**
	 * Formats the given boolean and returns the true or false string.
	 *
	 * @param val The value to format.
	 * @return String
	 */
	public String format(boolean val)
	{
		return (val ? _true : _false);
	}

	/**
	 * Parses a String to return the boolean value.
	 *
	 * Note: pos is ignored and exists for proper implementation of the base class
	 * @param source The String to parse.
	 * @param pos ParsePosition The index is updated to the length of the string.
	 * @return Object
	 */
	public Object parseObject(String source, ParsePosition pos)
	{
        Object ret = null;
		if ( _true.equalsIgnoreCase(source) ) ret = new Boolean(true);
		else if (_false.equalsIgnoreCase(source) ) ret = new Boolean(false);
		if (ret != null) pos.setIndex( source.length() );
		return ret;
	}
}