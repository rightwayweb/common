package com.zitego.style;

import java.util.*;
import java.io.*;

/**
 * Provides functionality similar to Html style sheets, but more portable.
 * The methods in this class are all static, with the intent that they'll
 * be used within JSP pages.
 *
 * @author John Glorioso
 * @version $Id: StyleSheet.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class StyleSheet
{
	/** This holds the styles as style as the key and a props object with the values. */
	protected Hashtable _propValues = new Hashtable();
	/** The properties object used to hold the styles. */
	protected Properties _props = new Properties();

	/**
	 * Creates a new empty style sheet.
	 */
	public StyleSheet() { }

	/**
	 * Creates a new style sheet with the properties as a string.
	 *
	 * @param String The style properties.
	 * @throws IOException if an error occurs loading the properties.
	 */
	public StyleSheet(String styleProps) throws IOException
	{
		loadProperties(new StringReader(styleProps), true);
	}

	/**
	 * Loads the properties object from the specified file.
	 *
	 * @param String The name of the properties file to be loaded.
	 * @throws FileNotFoundException if the file path is invalid.
	 * @throws IOException if an error occurs loading the properties.
	 */
	public void loadPropertiesNoClear(String strFile) throws FileNotFoundException, IOException
	{
		loadProperties(new InputStreamReader( new FileInputStream(strFile) ), false);
	}

	/**
	 * Loads the properties object from the specified file.  Overwrites
	 * previous properties.
	 *
	 * @param String The name of the properties file to be loaded.
	 * @throws FileNotFoundException if the file path is invalid.
	 * @throws IOException if an error occurs loading the properties.
	 */
	public void loadProperties(String strFile) throws FileNotFoundException, IOException
	{
		loadProperties(new InputStreamReader(new FileInputStream(strFile)), true);
	}

	/**
	 * Loads the properties object from the specified file.  Overwrites
	 * previous properties.  Tags in the properties file should be of the form
 	 *
	 *     style.keyword=value
	 *
	 * The "tag" and "substyle" keywords have special meanings.
	 * "tag" indicates the Html tag that will be generated, e.g. "td" or "font"
	 * "substyle" indicates another style intended to be nested within this one.
	 *
	 * Exceptions (e.g., file not found) are not handled in the most
	 * robust fashion, i.e., an error is printed to stdout.
	 *
	 * @param Reader To read the properties
	 * @param boolean Whether to clear the properties all present.
	 * @throws IOException if an error occurs loading the properties.
	 */
	public void loadProperties(Reader reader, boolean clear) throws IOException
	{
		if (clear)
		{
			_props.clear();
			_propValues.clear();
		}

		BufferedReader in = new BufferedReader(reader);
		String s;
		while ( (s=in.readLine()) != null )
		{
			if (s.length() > 0 && s.charAt(0) != '#')
			{
				String key, value, origStyle, style, text, newtext;
				StringTokenizer st = new StringTokenizer(s, "=");
				int tokens = st.countTokens();
				if (tokens > 1)
				{
					key = st.nextToken();
					value = st.nextToken();
				}
				else if (tokens == 1)
				{
					// use empty string as value
					key = st.nextToken();
					value = "";
				}
				else
				{
					key = s;
					value = "";
				}
				st = new StringTokenizer(key, ".");
				tokens = st.countTokens();
				if (tokens == 2)
				{
					origStyle = st.nextToken();
					key = st.nextToken();
					if ( key.equalsIgnoreCase("tag") )
					{
						style = origStyle + "_tag";
						text = value;
					}
					else if ( key.equalsIgnoreCase("substyle") )
					{
						style = origStyle + "_substyle";
						text = value;
					}
					else
					{
						newtext = key + "=\"" + value + "\" ";
						style = origStyle;
						text = _props.getProperty(style);
						text = (text == null) ? newtext : (text + newtext);
					}
					_props.setProperty(style, text);
					//See if the propValues has this style already
					Properties styleValues = (Properties)_propValues.get(origStyle);
					if (styleValues == null) styleValues = new Properties();
					styleValues.setProperty(key, value);
					_propValues.put(origStyle, styleValues);
				}
				// else bad format for key -- should be "style.keyword"
			}
		}
	}

	/**
	 * Debug utility to list all properties.
	 *
	 * @return String
	 */
	public String dumpProperties()
	{
		StringBuffer s = new StringBuffer();
		int count = 0;
		for (Enumeration e=_propValues.keys(); e.hasMoreElements();)
		{
			String key = (String)e.nextElement();
			Properties styleProps = (Properties)_propValues.get(key);
			for (Enumeration e2=styleProps.propertyNames(); e2.hasMoreElements(); count++)
			{
				String prop = (String)e2.nextElement();
				s.append( (count > 0 ? "\r\n" : "") ).append(key).append(".").append(prop)
				 .append("=").append( styleProps.getProperty(prop) );
			}
		}
		return s.toString();
	}

	/**
	 * Formats text within Html tags according to a given style.  Substyles
	 * from the properties file are automatically nested in the Html.
	 *
	 * @param String The style (from the properties file) in which to format
	 *               the text.
	 * @param String The text string to be formatted.
	 *
	 * @return String
	 */
	public String getHtml(String style, String text)
	{
		return getHtml(style, text, null);
	}

	/**
	 * Formats text within Html tags according to a given style, allowing
	 * for extra Html to be inserted in the style tag (possibly overriding
	 * attributes from the style itself).  Substyles from the properties file
	 * are automatically nested in the Html.
	 *
	 * @param String The style (from the properties file) in which to format
	 *               the text.
	 * @param String The text string to be formatted.
	 * @param String Additional Html text to be inserted within the opening
	 *               style tag. The extra Html is inserted at the start of the
	 *               tag, since both Netscape and IE seem to obey the first instance
	 *               of a property. This Html is not applied to any substyle tags.
	 *
	 * @return String
	 */
	public String getHtml(String style, String text, String extra_html)
	{
		String result = "";
		if (text != null)
		{
			result = getHtmlStartTag(style, extra_html);
			result += text;
			result += getHtmlEndTag(style);
		}
		return result;
	}

	/**
	 * Return the opening Html tag for the given style.  Substyles
	 * from the properties file are automatically nested in the Html.
	 *
	 * @param String The style (from the properties file) in which to format
	 *               the text.
	 *
	 * @return String
	 */
	public String getHtmlStartTag(String style)
	{
		return getHtmlStartTag(style, null);
	}

	/**
	 * Return the opening Html tag for the given style, allowing for extra
	 * Html to be inserted in the style tag (possibly overriding attributes
	 * from the style itself).  Substyles from the properties file are
	 * automatically nested in the Html.
	 *
	 * @param String The style (from the properties file) in which to format
	 *               the text.
	 * @param String Additional Html text to be inserted within the opening
	 *               style tag. The extra Html is inserted at the start of the
	 *               tag, since both Netscape and IE seem to obey the first instance
	 *               of a property. This Html is not applied to any substyle tags.
	 *
	 * @return String
	 */
	public String getHtmlStartTag(String style, String extra_html)
	{
		String value = _props.getProperty(style);
		String tag = _props.getProperty(style + "_tag");
		String result = "";

		if (tag != null)
		{
			result += "<" + tag;

			if (extra_html != null) result += " " + extra_html;
			if (value != null) result += " " + value;
			result += ">";

			String substyle = _props.getProperty(style + "_substyle");
			if (substyle != null) result += getHtmlStartTag(substyle);
		}
		return result;
	}

	/**
	 * Formats the closing Html </style> tag corresponding to given style.
	 * Substyles from the properties file are automatically nested in the Html.
	 *
	 * @param String The style (from the properties file) in which to format
	 *               the text.
	 *
	 * @return String
	 */
	public String getHtmlEndTag(String style)
	{
		String tag = _props.getProperty(style + "_tag");
		String result = "";

		if (tag != null)
		{
			String substyle = _props.getProperty(style + "_substyle");
			if (substyle != null) result += getHtmlEndTag(substyle);
			result += "</" + tag + ">";
		}
		return result;
	}

	/**
	 * Returns the style attribute.
	 *
	 * @param String The style.
	 * @param String The attribute.
	 * @return String
	 */
	public String getStyleAttribute(String style, String attr)
	{
		return ( (Properties)_propValues.get(style) ).getProperty(attr);
	}

	/**
	 * Sets the properties of the object.
	 *
	 * @param Hashtable
	 */
	public void setProperties(Hashtable props)
	{
		_props.clear();
		_propValues = props;

		for (Enumeration e=_propValues.keys(); e.hasMoreElements();)
		{
			String style = (String)e.nextElement();
			Properties styleProps = (Properties)_propValues.get(style);
			for (Enumeration e2=styleProps.propertyNames(); e2.hasMoreElements();)
			{
				String prop = (String)e2.nextElement();
				String val = styleProps.getProperty(prop);
				String newStyle = null, text = null, newtext = null;
				if ( prop.equalsIgnoreCase("tag") )
				{
					newStyle = style + "_tag";
					text = val;
				}
				else if ( prop.equalsIgnoreCase("substyle") )
				{
					newStyle = style + "_substyle";
					text = val;
				}
				else
				{
					newtext = prop + "=\"" + val + "\" ";
					newStyle = style;
					text = _props.getProperty(newStyle);
					text = (text == null) ? newtext : (text + newtext);
				}
				_props.setProperty(newStyle, text);
			}
		}
	}

	/**
	 * Returns the properties of the object.
	 *
	 * @return Hashtable
	 */
	public Hashtable getProperties()
	{
		return _propValues;
	}
}