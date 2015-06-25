package com.zitego.util;

import java.util.Vector;

/**
 * This class defines "canned" date ranges. For example, last month, or last week.
 *
 * @author John Glorioso
 * @version $Id: DateRangeType.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class DateRangeType extends Constant
{
    public final static DateRangeType NONE = new DateRangeType("All Dates");
    public final static DateRangeType WTD = new DateRangeType("Week to date");
    public final static DateRangeType MTD = new DateRangeType("Month to date");
    public final static DateRangeType YTD = new DateRangeType("Year to date");
    public final static DateRangeType YESTERDAY = new DateRangeType("Yesterday");
    public final static DateRangeType TODAY = new DateRangeType("Today");
    public final static DateRangeType CUSTOM = new DateRangeType("Custom");
    public final static DateRangeType LAST_MONTH = new DateRangeType("Last month");
    public final static DateRangeType LAST_WEEK = new DateRangeType("Last week");
    public final static DateRangeType LAST_7_DAYS = new DateRangeType("Last 7 days");
    public final static DateRangeType MBL = new DateRangeType("Month Before Last");
    public final static DateRangeType MBMBL = new DateRangeType("Month Before Month Before Last");
    public final static DateRangeType LAST_THREE_MONTHS = new DateRangeType("Last Three Months");
    /** Gets incremented as format types are initialized. */
    private static int _nextId = 0;
    /** To keep track of each type. */
    private static Vector _types;

    /**
     * Creates a new DateRangeType given the description.
     *
     * @param String The description.
     */
    private DateRangeType(String label)
    {
        super(_nextId++, label);
        if (_types == null) _types = new Vector();
        _types.add(this);
    }

    /**
     * Returns an DateRangeType based on the id passed in. If the id does not match the id of
     * a constant, then we return null.
     *
     * @param int The constant id.
     * @return DateRangeType
     */
    public static DateRangeType evaluate(int id)
    {
        return (DateRangeType)Constant.evaluate(id, _types);
    }

    /**
     * Returns an Constant based on the description passed in. If the description does not
     * match the description of a constant, then we return null.
     *
     * @param String The description.
     * @return DateRangeType
     */
    protected static DateRangeType evaluate(String name)
    {
        return (DateRangeType)Constant.evaluate(name, _types);
    }

    public Vector getTypes()
    {
        return _types;
    }
}