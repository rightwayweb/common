package com.zitego.util;

import java.util.*;

/**
 * This class represents a date range with a start and end date. The start
 * date must be a valid date prior to the end date.
 *
 */
public class DateRange implements java.io.Serializable
{
    /** The start date. */
    private Calendar _startDate;
    /** The end date. */
    private Calendar _endDate;
    /** The type. */
    private DateRangeType _type;

    /**
     * Creates a new DateRange with the dates provided.
     *
     * @param Calendar The start date.
     * @param Calendar The end date.
     * @throws IllegalArgumentException if the date range is invalid.
     */
    public DateRange(Calendar start, Calendar end)
    {
        this(start, end, DateRangeType.CUSTOM);
    }

    /**
     * Creates a new DateRange using the provided dates and type.
     *
     * @param Calendar The start date.
     * @param Calendar The end date.
     * @param DateRangeType The type of date range this is.
     * @throws IllegalArgumentException if the date range is invalid.
     */
    public DateRange(Calendar start, Calendar end, DateRangeType type) throws IllegalArgumentException
    {
        if (start == null) throw new IllegalArgumentException("Start date cannot be null");
        if (end == null) throw new IllegalArgumentException("End date cannot be null");

        setStartDate(start);
        setEndDate(end);
        setType(type);
    }

    /**
     * Sets the start date. The date provided date will automatically be converted to the
     * timezone of the daterange.
     *
     * @param Calendar The date to set.
     * @throws IllegalArgumentException when the start date is not before the end date.
     */
    public void setStartDate(Calendar dt) throws IllegalArgumentException
    {
        if ( _endDate != null && _endDate.before(dt) )
        {
            throw new IllegalArgumentException( "Start date cannot be after end date: " + _endDate.toString() );
        }
        _startDate = dt;
    }

    /**
     * Returns the start date.
     *
     * @return Date
     */
    public Calendar getStartDate()
    {
        return _startDate;
    }

    /**
     * Sets the end date. The date provided will be automatically converted to the timezone
     * of the daterange.
     *
     * @param Calendar The date to set.
     * @throws IllegalArgumentException when the start date is after the end date.
     */
    public void setEndDate(Calendar dt) throws IllegalArgumentException
    {
        if ( _startDate != null && _startDate.after(dt) )
        {
            throw new IllegalArgumentException( "End date cannot be before start date: " + _startDate.toString() );
        }
        _endDate = dt;
    }

    /**
     * Returns the end date.
     *
     * @return Date
     */
    public Calendar getEndDate()
    {
        return _endDate;
    }

    /**
     * Sets the type of daterange this is.
     *
     * @param DateRangeType The type.
     * @throws IllegalArgumentException if the type is null.
     */
    public void setType(DateRangeType type) throws IllegalArgumentException
    {
        if( type == null ) throw new IllegalArgumentException("type cannot be null");
        _type = type;
    }

    /**
     * Returns the type.
     *
     * @return DateRangeType
     */
    public DateRangeType getType()
    {
        return _type;
    }

    /**
     * Sets the Timezone.
     *
     * @param TimeZone The time zone to set.
     */
    public void setTimeZone(TimeZone timezone)
    {
        try
        {
            //build new filter in new timezone
            Calendar newFrom = (Calendar) _startDate.getClass().newInstance();
            newFrom.setTimeZone(timezone);
            Calendar newTo = (Calendar) _endDate.getClass().newInstance();
            newTo.setTimeZone(timezone);

            newFrom.set
            (
                _startDate.get(Calendar.YEAR), _startDate.get(Calendar.MONTH), _startDate.get(Calendar.DATE),
                _startDate.get(Calendar.HOUR_OF_DAY), _startDate.get(Calendar.MINUTE), _startDate.get(Calendar.SECOND)
            );
            newTo.set
            (
                _endDate.get(Calendar.YEAR), _endDate.get(Calendar.MONTH), _endDate.get(Calendar.DATE),
                _endDate.get(Calendar.HOUR_OF_DAY), _endDate.get(Calendar.MINUTE), _endDate.get(Calendar.SECOND)
            );
            _startDate = newFrom;
            _endDate = newTo;
        }
        catch(InstantiationException ie) { }
        catch(IllegalAccessException ie) { }
    }

    /**
     * Returns the time zone.
     *
     * @return TimeZone
     */
    public TimeZone getTimeZone()
    {
        return _startDate.getTimeZone();
    }

    /**
     * Returns the time zone offset, in hours, from GMT modified for day light savings.
     *
     * @return int
     */
    public int getTimeZoneOffset()
    {
        return _startDate.get(Calendar.ZONE_OFFSET) / 1000 / 60 / 60;
    }

    /**
     * Returns the number of days in the date range.
     *
     * @return int
     */
    public int getNumberOfDays()
    {
        return (int)Math.ceil
        (
            ( (double)getEndDate().getTime().getTime() - (double)getStartDate().getTime().getTime() )
            / 1000d / 60d / 60d / 24d
        );
    }
}
