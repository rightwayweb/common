package com.zitego.util;

import java.text.Format;

/**
 * Formats any constant and optionally returns a default value if the format method returns
 * null.
 *
 * @author John Glorioso
 * @version $Id: FormattedConstant.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class FormattedConstant extends Constant
{
    /** The formatter to use. */
    protected Format _format;
    /** The default value to use if null is encountered when formatting. */
    protected String _default;

    /**
     * Creates a new formatted constant with a description and a value.
     *
     * @param String The description.
     * @param int The value.
     */
    public FormattedConstant(String desc, int value)
    {
        super(value, desc);
    }

    /**
     * Creates a new formatted constant with a description, a value, and a format.
     *
     * @param String The description.
     * @param int The value.
     * @param Format The format to use (can be null).
     */
    public FormattedConstant(String desc, int value, Format format)
    {
        super(value, desc);
        _format = format;
    }

    /**
     * Creates a new formatted constant with a description, a value, a format, and a default value.
     *
     * @param String The description.
     * @param int The value.
     * @param Format The format to use (can be null).
     * @param String The default.
     */
    public FormattedConstant(String desc, int value, Format format, String def)
    {
        super(value, desc);
        _format = format;
        _default = def;
    }

    /**
     * Returns the format for this constant.
     *
     * @return Format
     */
    public Format getFormat()
    {
        return _format;
    }

    /**
     * Formats the given int passed in.
     *
     * @param int The value to format.
     */
    public String format(int val)
    {
        return format( new Integer(val) );
    }

    /**
     * Formats the given double passed in.
     *
     * @param double The value to format.
     */
    public String format(double val)
    {
        return format( new Double(val) );
    }

    /**
     * Formats the given byte passed in.
     *
     * @param byte The value to format.
     */
    public String format(byte val)
    {
        return format( new Byte(val) );
    }

    /**
     * Formats the given char passed in.
     *
     * @param char The value to format.
     */
    public String format(char val)
    {
        return format( new Character(val) );
    }

    /**
     * Formats the given float passed in.
     *
     * @param flat The value to format.
     */
    public String format(float val)
    {
        return format( new Float(val) );
    }

    /**
     * Formats the given short passed in.
     *
     * @param short The value to format.
     */
    public String format(short val)
    {
        return format( new Short(val) );
    }

    /**
     * Formats the given long passed in.
     *
     * @param long The value to format.
     */
    public String format(long val)
    {
        return format( new Long(val) );
    }

	/**
	 * Formats the Object passed in. If the format is null, then the object is returned as toString.
	 *
	 * @param Object The value to format.
	 * @return String
	 */
	public String format(Object val)
	{
		if (val == null)
		{
		    if (_default == null) return null;
		    else return _default;
		}
		else
		{
		    if (_format == null) return val.toString();
		    else return _format.format(val);
		}
	}
}