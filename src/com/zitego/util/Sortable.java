package com.zitego.util;

/**
 * This is a simple class that any sortable object can implement. The only thing
 * that must be maintained in the Sortable class is the sort direction. Classes
 * should use this interface's ASCENDING and DESCENDING properties to keep track
 * or sort direction.
 *
 * @author John Glorioso
 * @version $Id: Sortable.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public interface Sortable
{
    public static final int ASCENDING = 1;
    public static final int DESCENDING = -1;

    /**
     * Sets the sort direction to ascending.
     */
    public void setSortAscending();

    /**
     * Sets the sort direction to descending.
     */
    public void setSortDescending();

    /**
     * Returns the sort direction. Descending is < 0 and ascending is >= 0.
     *
     * @return int
     */
    public int getSortDirection();

    /**
     * Sets the sort column.
     *
     * @param SortColumn The property to sort on.
     */
    public void setSortColumn(SortColumn col);

    /**
     * Returns the column we are sorting on.
     *
     * @return SortColumn
     */
    public SortColumn getSortColumn();

    /**
     * Performs the actual sort operation.
     */
    public void sort();

}