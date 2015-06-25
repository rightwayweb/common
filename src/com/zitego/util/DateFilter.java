package com.zitego.util;

/**
 * This class represents a date range filter.
 *
 * @author John Glorioso
 * @version $Id: DateFilter.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class DateFilter
{
	/** The date range being used. */
	protected DateRange _range;

    /**
	 * Constructs a new date filter.
	 */
	public DateFilter() { }

	/**
	 * Constructs a new filter with the specified date range.
	 *
	 * @param DateRange The date range.
	 * @throws IllegalArgumentException if the range is null.
	 */
	public DateFilter(DateRange range) throws IllegalArgumentException
	{
		setDateRange(range);
	}

	/**
	 * Sets the date range.
	 *
	 * @param Date The date range
	 * @throws IllegalArgumentException if the range is null.
	 */
	public void setDateRange(DateRange range) throws IllegalArgumentException
	{
		if (range == null) throw new IllegalArgumentException("date range cannot be null");
		_range = range;
	}

	/**
	 * Returns the date range.
	 *
	 * @return DateRange
	 */
	public DateRange getDateRange()
	{
		return _range;
	}
}