package com.zitego.http;

import java.io.File;

/**
 * This represents a simple request field object with a name and value. The name can
 * be null or an empty string in which case, only the field will be returned in a
 * toString call. The value is an object in the event that the data is not a string
 * (it's binary).
 *
 * @author John Glorioso
 * @version $Id: RequestField.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class RequestField
{
	/** The name of the object. */
	protected String _name = null;
	/** The file name. */
	protected String _filename = null;
	/** The value of the object. */
	protected Object _value = null;
	/** The mime type of the value. Default is text/plain. */
	protected String _mimeType = "text/plain";

	/**
	 * Constructs a new object with a name and value.
	 *
	 * @param String The name of the field.
	 * @param String The value of the field.
	 * @throws IllegalArgumentException if the name is null.
	 */
	public RequestField(String name, String value) throws IllegalArgumentException
	{
		setName(name);
		setValue(value);
	}

	/**
	 * Constructs a new object with name, value, and mimetype.
	 *
	 * @param String The name of the field.
	 * @param String The filename.
	 * @param File The value file.
	 * @param String The mimetype.
	 * @throws IllegalArgumentException if the name or filename are null.
	 */
	public RequestField(String name, String filename, File value, String mimetype) throws IllegalArgumentException
	{
		setName(name);
		setFilename(filename);
		setValue(value);
		setMimeType(mimetype);
	}

	/**
	 * Sets the name of the field.
	 *
	 * @param String The name.
	 */
	public void setName(String name)
	{
	    if ("".equals(name) || name == null) throw new IllegalArgumentException("Field name cannot be empty");
		else _name = name;
	}

	/**
	 * Returns the name of the field.
	 *
	 * @return String
	 */
	public String getName()
	{
		return _name;
	}

	/**
	 * Sets the filename of the file.
	 *
	 * @param String The name of the file.
	 * @throws IllegalArgumentException if the value is null.
	 */
	public void setFilename(String name) throws IllegalArgumentException
	{
	    if ("".equals(name) || name == null) throw new IllegalArgumentException("Filename cannot be empty");
	    _filename = name;
	}

	/**
	 * Returns the filename.
	 *
	 * @return String
	 */
	public String getFilename()
	{
		return _filename;
	}

	/**
	 * Sets the value of the text field.
	 *
	 * @param String The value of the text field.
	 */
	public void setValue(String value)
	{
	    if ( "".equals(value) ) value = null;
	    _value = value;
	}

	/**
	 * Sets the value of the file.
	 *
	 * @param File The file.
	 */
	public void setValue(File value)
	{
	    _value = value;
	}

	/**
	 * Returns the value of the text field.
	 *
	 * @return String
	 */
	public String getValue()
	{
		return (_value instanceof String ? (String)_value : null);
	}

	/**
	 * Returns the file.
	 *
	 * @return File
	 */
	public File getFile()
	{
	    return (_value instanceof File ? (File)_value : null);
	}

	/**
	 * Sets the mime type.
	 *
	 * @param String The mime type.
	 */
	public void setMimeType(String type)
	{
	    _mimeType = type;
	}

	/**
	 * Returns the mime type of the option.
	 *
	 * @return String
	 */
	public String getMimeType()
	{
		return _mimeType;
	}
}