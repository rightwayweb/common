package com.zitego.util;

import java.io.Serializable;

/**
 * This represents a simple object with a name and value. This is similar to what you might
 * see in an html select option.
 *
 * @author John Glorioso
 * @version $Id: NameValueObject.java,v 1.2 2009/12/12 17:07:12 jglorioso Exp $
 */
public class NameValueObject extends Object implements Serializable
{
	/** The name of the object. */
	protected String _name = null;
	/** The value of the object. */
	protected String _value = null;

	/**
	 * Constructs a new object with value and name.
	 *
	 * @param String The value of the option.
	 * @param String The name of the option.
	 */
	public NameValueObject(String value, String name)
	{
		super();
		setValue(value);
		setName(name);
	}

	/**
	 * Sets the name.
	 *
	 * @param String The name.
	 */
	public void setName(String name)
	{
		_name = name;
	}

	/**
	 * Sets the value fo the object.
	 *
	 * @param String The value of the object.
	 */
	public void setValue(String value)
	{
		_value = value;
	}

	/**
	 * Returns the name of the option.
	 *
	 * @return String
	 */
	public String getName()
	{
		return _name;
	}

	/**
	 * Returns the value of the object.
	 *
	 * @return String
	 */
	public String getValue()
	{
		return _value;
	}
}