package com.zitego.util;

/**
 * Any class that wishes to reveal all getters when the toString method is
 * called should extend this class. If a subclass of an Information class
 * wants the original behavior back, just override toString to call
 * originalToString.
 *
 * @author John Glorioso
 * @version $Id: InformationEntity.java,v 1.2 2008/04/16 18:46:00 jglorioso Exp $
 */
public class InformationEntity
{
    //Make sure we dont recurse infinitely
    private int _internalCount = 0;

    /**
	 * Returns a String representation of this object.
	 *
	 * @return String
	 */
	public String toString()
	{
	    if (_internalCount++ > 0) return "[ALREADY CALLED]";
		StringBuffer ret = new StringBuffer();
		Class c = getClass();
		java.lang.reflect.Method[] methods = c.getMethods();
		ret.append(c.getName()).append(": \n");
		for (int i=0; i<methods.length; i++)
		{
			String name = methods[i].getName();
			if ( (name.indexOf("get") == 0 || name.indexOf("is") == 0) && methods[i].getParameterTypes().length == 0 )
			{
				try
				{
					ret.append("\t").append(name).append("=").append(methods[i].invoke(this, (Object[])null)).append("\n");
				}
				catch (Exception e)
				{
					ret.append("\t").append(name).append("=").append(e).append("\n");
				}
			}
		}
		return ret.toString();
	}

	/**
	 * Returns the original version of toString.
	 *
	 * @return String
	 */
	public String originalToString()
	{
	    return super.toString();
	}
}