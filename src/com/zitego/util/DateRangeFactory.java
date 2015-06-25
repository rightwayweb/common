package com.zitego.util;

import java.util.*;

/**
 * A factory class for creating date ranges.
 *
 * @author John Glorioso
 * @version $Id: DateRangeFactory.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class DateRangeFactory
{
    public static void main(String args[])
    {
        DateRange r1 = DateRangeFactory.getLastWeek( TimeZone.getTimeZone("GMT") );
        DateRange r2 = DateRangeFactory.getWeekToDate( TimeZone.getTimeZone("GMT") );
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        sdf.setTimeZone( TimeZone.getTimeZone("GMT") );

        System.out.println( "r1 Start = " + sdf.format(r1.getStartDate().getTime()) );
        System.out.println( "r1 End = " + sdf.format(r1.getEndDate().getTime()) );

        System.out.println( "r2 Start = " + sdf.format(r2.getStartDate().getTime()) );
        System.out.println( "r2 End = " + sdf.format(r2.getEndDate().getTime()) );
    }

	/**
	 * Builds a DateRange object based on the specified timezone and type.
	 *
	 * @param DateRangeType The type of date range to build.
	 * @param TimeZone The timezone to use to build the DateRange.
	 * @return DateRange
	 */
	public static DateRange getDateRange(DateRangeType type, TimeZone tz)
	{
		if (type == DateRangeType.NONE ) return getAllAvailableDates(tz);
		else if (type == DateRangeType.WTD) return getWeekToDate(tz);
		else if (type == DateRangeType.MTD) return getMonthToDate(tz);
		else if (type == DateRangeType.YTD) return getYearToDate(tz);
		else if (type == DateRangeType.YESTERDAY) return getYesterday(tz);
		else if (type == DateRangeType.TODAY) return getToday(tz);
		else if (type == DateRangeType.LAST_7_DAYS) return getLast7Days(tz);
		else if (type == DateRangeType.LAST_WEEK) return getLastWeek(tz);
		else if (type == DateRangeType.LAST_MONTH) return getLastMonth(tz);
		else if (type == DateRangeType.MBL) return getMonthBeforeLast(tz);
		else if (type == DateRangeType.MBMBL) return getMonthBeforeMonthBeforeLast(tz);
		else if (type == DateRangeType.LAST_THREE_MONTHS) return getLastThreeMonths(tz);
		else return null;
	}

	/**
	 * Builds a DateRange object given 2 dates.
	 *
	 * @param Calendar The start date.
	 * @param Calendar The end date.
	 * @return DateRange
	 */
	public static DateRange buildDateRange(Calendar fromDate, Calendar toDate)
	{
        return new DateRange(fromDate, toDate, DateRangeType.CUSTOM);
	}

	/**
	 * Returns a date range with all available dates.
	 *
	 * @param TimeZone The timezone to build the DateRange with.
	 * @return DateRange
	 */
	protected static DateRange getAllAvailableDates(TimeZone timezone)
	{
	    GregorianCalendar from = new GregorianCalendar(timezone);
	    GregorianCalendar to = new GregorianCalendar(timezone);
		from.set(0, 0, 0, 0, 0, 0);

		return new DateRange(from, to, DateRangeType.NONE);
	}

	/**
	 * Returns a week to date DateRange.
	 *
	 * @param TimeZone The timezone to build the DateRange with.
	 * @return DateRange
	 */
	protected static DateRange getWeekToDate(TimeZone timeZone)
	{
		GregorianCalendar from = new GregorianCalendar(timeZone);
		GregorianCalendar to = new GregorianCalendar(timeZone);
		GregorianCalendar today = new GregorianCalendar(timeZone);

		int year = today.get(Calendar.YEAR);
		int month = today.get(Calendar.MONTH);
		int day = today.get(Calendar.DATE);

		GregorianCalendar firstDayOfWeek = new GregorianCalendar(timeZone);
		firstDayOfWeek.set(year, month, day, 0, 0, 0);

		while (firstDayOfWeek.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
		{
			firstDayOfWeek.roll(Calendar.DAY_OF_WEEK, -1);
		}

		from = firstDayOfWeek;

		return new DateRange(from, to, DateRangeType.WTD);
	}

	/**
	 * Returns a month to date DateRange.
	 *
	 * @param TimeZone The timezone to build the DateRange with.
	 * @return DateRange
	 */
	protected static DateRange getMonthToDate(TimeZone timeZone)
	{
		Calendar from = new GregorianCalendar(timeZone);
		GregorianCalendar today = new GregorianCalendar(timeZone);
		from.set(today.get(Calendar.YEAR),today.get(Calendar.MONTH),1,0,0,0);
		return new DateRange(from, today, DateRangeType.MTD);
	}

	/**
	 * Returns a year to date DateRange.
	 *
	 * @param TimeZone The timezone to build the DateRange with.
	 * @return DateRange
	 */
	protected static DateRange getYearToDate(TimeZone timeZone)
	{
		Calendar from = new GregorianCalendar(timeZone);
		GregorianCalendar today = new GregorianCalendar(timeZone);
		from.set(today.get(Calendar.YEAR),0,1,0,0,0);
		return new DateRange(from, today, DateRangeType.YTD);
	}

	/**
	 * Returns a DateRange for yesterday.
	 *
	 * @param TimeZone The timezone to build the DateRange with.
	 * @return DateRange
	 */
	protected static DateRange getYesterday(TimeZone timeZone)
	{
		GregorianCalendar from = new GregorianCalendar(timeZone);
		GregorianCalendar to= new GregorianCalendar(timeZone);
		GregorianCalendar today = new GregorianCalendar(timeZone);
		from.set(today.get(Calendar.YEAR),today.get(Calendar.MONTH),today.get(Calendar.DATE)-1,0,0,0);
		to.set(today.get(Calendar.YEAR),today.get(Calendar.MONTH),today.get(Calendar.DATE)-1,23,59,59);
		return new DateRange(from, to, DateRangeType.YESTERDAY);
	}

	/**
	 * Returns a DateRange for today.
	 *
	 * @param TimeZone The timezone to build the DateRange with.
	 * @return DateRange
	 */
	protected static DateRange getToday(TimeZone timeZone)
	{
		GregorianCalendar from = new GregorianCalendar(timeZone);
		GregorianCalendar today = new GregorianCalendar(timeZone);
		from.set(today.get(Calendar.YEAR),today.get(Calendar.MONTH),today.get(Calendar.DATE),0,0,0);
		return new DateRange(from, today, DateRangeType.TODAY);
	}

	/**
	 * Returns a DateRange for the last 7 days.
	 *
	 * @param TimeZone The timezone to build the DateRange with.
	 * @return DateRange
	 */
	protected static DateRange getLast7Days(TimeZone timeZone)
	{
		GregorianCalendar from = new GregorianCalendar(timeZone);
		GregorianCalendar today = new GregorianCalendar(timeZone);
		from.set(today.get(Calendar.YEAR),today.get(Calendar.MONTH),today.get(Calendar.DATE)-6,0,0,0);
		return new DateRange(from, today, DateRangeType.LAST_7_DAYS);
	}

	/**
	 * Returns a DateRange for last week. A week is defined as Sunday to Saturday.
	 *
	 * @param TimeZone The timezone to build the DateRange with.
	 * @return DateRange
	 */
	protected static DateRange getLastWeek(TimeZone timeZone)
	{
		GregorianCalendar today = new GregorianCalendar(timeZone);
		GregorianCalendar lastDayOfWeek = (GregorianCalendar)today.clone();
		lastDayOfWeek.add(Calendar.DATE,-1);

		while (lastDayOfWeek.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
		{
			lastDayOfWeek.add(Calendar.DATE, -1);
		}
		lastDayOfWeek.set(Calendar.HOUR, 11);
        lastDayOfWeek.set(Calendar.HOUR_OF_DAY, 23);
		lastDayOfWeek.set(Calendar.MINUTE, 59);
		lastDayOfWeek.set(Calendar.SECOND, 59);

		if (lastDayOfWeek.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
		{
			// The set causes the date to jump ahead one, we need to account for this
			lastDayOfWeek.add(Calendar.DATE, -1);
		}

		GregorianCalendar firstDayOfWeek = (GregorianCalendar)lastDayOfWeek.clone();
		firstDayOfWeek.add(Calendar.DATE,-6);

		firstDayOfWeek.set(Calendar.HOUR, 0);
        firstDayOfWeek.set(Calendar.HOUR_OF_DAY, 0);
		firstDayOfWeek.set(Calendar.MINUTE, 0);
		firstDayOfWeek.set(Calendar.SECOND, 0);


		return new DateRange(firstDayOfWeek, lastDayOfWeek, DateRangeType.LAST_WEEK);
	}

	/**
	 * Returns a DateRange for last month
	 *
	 * @param TimeZone The timezone to build the DateRange with.
	 * @return DateRange
	 */
	protected static DateRange getLastMonth(TimeZone timeZone)
	{
		int daysInMonth[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
		GregorianCalendar today = new GregorianCalendar(timeZone);
		GregorianCalendar lastDayOfMonth = null;
		GregorianCalendar firstDayOfMonth = null;
		int month = today.get(Calendar.MONTH);
		int year = today.get(Calendar.YEAR);

		if (month == Calendar.JANUARY)
		{
			month = Calendar.DECEMBER;
			year--;
		}
		else
		{
			month--;
		}

		firstDayOfMonth = new GregorianCalendar(year, month, 1, 0, 0, 0);
		if (firstDayOfMonth.isLeapYear(year) && month == Calendar.JANUARY)
		{
			daysInMonth[1]++;
		}
		lastDayOfMonth = new GregorianCalendar(year, month, daysInMonth[month],23,59,59);
		firstDayOfMonth.setTimeZone(timeZone);
		lastDayOfMonth.setTimeZone(timeZone);

		return new DateRange(firstDayOfMonth, lastDayOfMonth, DateRangeType.LAST_MONTH);
	}

	/**
	 * Returns a DateRange for the month before last.
	 *
	 * @param TimeZone The timezone to build the DateRange with.
	 * @return DateRange
	 */
	protected static DateRange getMonthBeforeLast(TimeZone timeZone)
	{
		GregorianCalendar to = new GregorianCalendar(timeZone);
		GregorianCalendar from = new GregorianCalendar(timeZone);
		from.add(Calendar.MONTH, -2);
		from.set(from.get(Calendar.YEAR),from.get(Calendar.MONTH),1,0,0,0);
		to.add(Calendar.MONTH,-1);
		to.set(to.get(Calendar.YEAR),to.get(Calendar.MONTH),1,23,59,59);
		to.add(Calendar.DATE,-1);
		return new DateRange(from, to, DateRangeType.MBL);
	}

	/**
	 * Returns a DateRange for the month before month before last.
	 *
	 * @param TimeZone The timezone to build the DateRange with.
	 * @return DateRange
	 */
	protected static DateRange getMonthBeforeMonthBeforeLast(TimeZone timeZone)
	{
		GregorianCalendar to= new GregorianCalendar(timeZone);
		GregorianCalendar from = new GregorianCalendar(timeZone);
		from.add(Calendar.MONTH,-3);
		from.set(from.get(Calendar.YEAR),from.get(Calendar.MONTH),1,0,0,0);
		to.add(Calendar.MONTH,-2);
		to.set(to.get(Calendar.YEAR),to.get(Calendar.MONTH),1,23,59,59);
		to.add(Calendar.DATE,-1);
		return new DateRange(from, to, DateRangeType.MBMBL);
	}

	/**
	 * Returns a DateRange for the last 3 months.
	 *
	 * @param TimeZone The timezone to build the DateRange with.
	 * @return DateRange
	 */
	protected static DateRange getLastThreeMonths(TimeZone timeZone)
	{
		GregorianCalendar to= new GregorianCalendar(timeZone);
		GregorianCalendar from = new GregorianCalendar(timeZone);
		from.add(Calendar.MONTH,-3);
		from.set(from.get(Calendar.YEAR),from.get(Calendar.MONTH),1,0,0,0);
		to.set(to.get(Calendar.YEAR),to.get(Calendar.MONTH),1,23,59,59);
		to.add(Calendar.DATE,-1);
		return new DateRange(from, to, DateRangeType.LAST_THREE_MONTHS);
	}
}
