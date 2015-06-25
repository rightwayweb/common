package com.zitego.style;

import java.io.*;

/**
 * A style sheet that applies server wide.
 *
 * @author John Glorioso
 * @version $Id: ServerStyleSheet.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 * @see StyleSheet
 */
public class ServerStyleSheet
{
	/** The StyleSheet to be used everywhere. */
	protected static StyleSheet _sheet = new StyleSheet();

	/**
	 * Loads the properties object from the specified file.
	 *
	 * @param String The name of the properties file to be loaded.
	 * @throws FileNotFoundException if the file path is invalid.
	 * @throws IOException if an error occurs loading the properties.
	 */
	public static void loadPropertiesNoClear(String strFile) throws FileNotFoundException, IOException
	{
		loadProperties(strFile, false);
	}

	/**
	 * Loads the properties object from the specified file.  Overwrites
	 * previous properties.
	 *
	 * @param String The name of the properties file to be loaded.
	 * @throws FileNotFoundException if the file path is invalid.
	 * @throws IOException if an error occurs loading the properties.
	 */
	public static void loadProperties(String strFile) throws FileNotFoundException, IOException
	{
		loadProperties(strFile, true);
	}

	/**
	 * Loads the properties object from the specified file.  Overwrites
	 * previous properties.
	 *
	 * @param String The name of the properties file to be loaded.
	 * @param boolean Whether to clear the properties all present.
	 * @throws FileNotFoundException if the file path is invalid.
	 * @throws IOException if an error occurs loading the properties.
	 */
	public static void loadProperties(String strFile, boolean clear) throws FileNotFoundException, IOException
	{
		_sheet.loadProperties(new FileReader(strFile), clear);
	}

	/**
	 * Debug utility to list all properties.
	 *
	 * @return String
	 */
	public static String dumpProperties()
	{
		return _sheet.dumpProperties();
	}

	/**
	 * Formats text within HTML tags according to a given style.  Substyles
	 * from the properties file are automatically nested in the HTML.
	 *
	 * @param String The style (from the properties file) in which to format
	 *               the text.
	 * @param String The text string to be formatted.
	 *
	 * @return String
	 */
	public static String getHTML(String style, String text)
	{
		return _sheet.getHtml(style, text, null);
	}

	/**
	 * Formats text within HTML tags according to a given style, allowing
	 * for extra HTML to be inserted in the style tag (possibly overriding
	 * attributes from the style itself).  Substyles from the properties file
	 * are automatically nested in the HTML.
	 *
	 * @param String The style (from the properties file) in which to format
	 *               the text.
	 * @param String The text string to be formatted.
	 * @param String Additional HTML text to be inserted within the opening
	 *               style tag. The extra HTML is inserted at the start of the
	 *               tag, since both Netscape and IE seem to obey the first instance
	 *               of a property. This HTML is not applied to any substyle tags.
	 *
	 * @return String
	 */
	public static String getHTML(String style, String text, String extra_html)
	{
		return _sheet.getHtml(style, text, extra_html);
	}

	/**
	 * Return the opening HTML tag for the given style.  Substyles
	 * from the properties file are automatically nested in the HTML.
	 *
	 * @param String The style (from the properties file) in which to format
	 *               the text.
	 *
	 * @return String
	 */
	public static String getHTMLStartTag(String style)
	{
		return _sheet.getHtmlStartTag(style, null);
	}

	/**
	 * Return the opening HTML tag for the given style, allowing for extra
	 * HTML to be inserted in the style tag (possibly overriding attributes
	 * from the style itself).  Substyles from the properties file are
	 * automatically nested in the HTML.
	 *
	 * @param String The style (from the properties file) in which to format
	 *               the text.
	 * @param String Additional HTML text to be inserted within the opening
	 *               style tag. The extra HTML is inserted at the start of the
	 *               tag, since both Netscape and IE seem to obey the first instance
	 *               of a property. This HTML is not applied to any substyle tags.
	 *
	 * @return String
	 */
	public static String getHTMLStartTag(String style, String extra_html)
	{
		return _sheet.getHtmlStartTag(style, extra_html);
	}

	/**
	 * Formats the closing HTML </style> tag corresponding to given style.
	 * Substyles from the properties file are automatically nested in the HTML.
	 *
	 * @param String The style (from the properties file) in which to format
	 *               the text.
	 *
	 * @return String
	 */
	public static String getHTMLEndTag(String style)
	{
		return _sheet.getHtmlEndTag(style);
	}
}