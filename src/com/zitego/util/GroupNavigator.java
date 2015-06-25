package com.zitego.util;

import java.util.*;
import java.io.Serializable;

/**
 * This class is for navigating lists or arrays of objects. It groups "chunks"
 * of the list or array based on the group size that has been specified by
 * the caller. Either a List or an Object[] can be used here.
 *
 * @author John Glorioso
 * @version $Id: GroupNavigator.java,v 1.3 2012/12/30 07:01:19 jglorioso Exp $
 */
public class GroupNavigator implements Serializable
{
    /** An optional id for this navigator. */
    private String _id;
    /** The group/chunk size. */
    private int _groupSize = -1;
    /** The list. */
    private List _list;
    /** The current group/chunk we are on. */
    private int _currentGroup = 0;
    /** The total number of groups for non-cached group navigators. */
    private int _nonCachedGroupCount = 0;
    /** The total size of the list for non-cached group navigators. */
    private int _nonCachedTotalSize = 0;

    /**
     * Creates a new navigator with a list the group/chunk size.
     *
     * @param Object The list.
     * @param int The group/chunk size.
     * @throws IllegalArgumentException if the list is null.
     */
    public GroupNavigator(List l, int size) throws IllegalArgumentException
    {
        this(null, l, size);
    }

    /**
     * Creates a new navigator with an id, list, and the group/chunk size.
     *
     * @param String The id for this navigator.
     * @param Object The list.
     * @param int The group/chunk size.
     * @throws IllegalArgumentException if the list is null.
     */
    public GroupNavigator(String id, List l, int size) throws IllegalArgumentException
    {
        if (l == null) throw new IllegalArgumentException("List cannot be null");
        _id = id;
        _list = l;
        setGroupSize(size);
    }

    /**
     * Creates a new navigator with an array and the group/chunk size.
     *
     * @param Object[] The array.
     * @param int The group/chunk size.
     * @throws IllegalArgumentException if the array is null.
     */
    public GroupNavigator(Object[] a, int size) throws IllegalArgumentException
    {
        this(null, a, size);
    }

    /**
     * Creates a new navigator with an id, array, and the group/chunk size.
     *
     * @param String The id.
     * @param Object[] The array.
     * @param int The group/chunk size.
     * @throws IllegalArgumentException if the array is null.
     */
    public GroupNavigator(String id, Object[] a, int size) throws IllegalArgumentException
    {
        if (a == null) throw new IllegalArgumentException("Array cannot be null");
        _id = id;
        _list = Arrays.asList(a);
        setGroupSize(size);
    }

    /**
     * Creates a new navigator with an id, a partial array, and the group/chunk size, and
     * the start index of where this group starts. The array will be less than or equal to
     * the total group size depending on the group count passed in. This is to allow a
     * navigator of many pages with data only populated in the current page. Calling next
     * group or previous group will return empty lists. This is a no caching implementation.
     *
     * @param String The id.
     * @param Object[] The array.
     * @param int The group/chunk size.
     * @param int The total number of groups for non-cached group navigators.
     * @param int The total size of the list were it cached.
     * @throws IllegalArgumentException if the array is null.
     */
    public GroupNavigator(String id, Object[] a, int size, int groupCount, int totalSize) throws IllegalArgumentException
    {
        if (a == null) throw new IllegalArgumentException("Array cannot be null");
        _id = id;
        _list = Arrays.asList(a);
        setGroupSize(size);
        _nonCachedGroupCount = groupCount;
        _nonCachedTotalSize = totalSize;
    }

    /**
     * Returns the id.
     *
     * @return String
     */
    public String getId()
    {
        return _id;
    }

    /**
     * Returns the original full list.
     *
     * @return List
     */
    public List getOriginalList()
    {
        return _list;
    }

    /**
     * Returns the original array in Object[] form.
     *
     * @return Object[]
     */
    public Object[] getOriginalArray()
    {
        return _list.toArray();
    }

    /**
     * Sets the group/chunk size. If the size is less than 1, then the size of
     * the entire group is used.
     *
     * @param int The number of results in each group/chunk.
     */
    public void setGroupSize(int size)
    {
        if (size < 1) _groupSize = _list.size();
        else _groupSize = size;
    }

    /**
     * Returns the group size.
     *
     * @return int
     */
    public int getGroupSize()
    {
        return _groupSize;
    }

    /**
     * Returns the number of groups/chunks in the entire group.
     *
     * @return int
     */
    public int getGroupCount()
    {
        if (_nonCachedGroupCount > 0) return _nonCachedGroupCount;
        else
        {
            int size = _list.size();
            double count = (double)size / (double)_groupSize;
            //In case there is a fractional group, round up
            if ( (double)(int)count < count ) return ( (int)count )+1;
            else return (int)count;
        }
    }

    /**
     * Returns the current group index starting at 0.
     *
     * @return int
     */
    public int getCurrentGroupIndex()
    {
        return _currentGroup;
    }

    /**
     * Returns whether there is a previous index available before the given one. This
     * checks against the entire list, not the current group.
     *
     * @param int The index.
     * @return boolean
     */
    public boolean hasPreviousIndex(int index)
    {
        return (index > 0);
    }

    /**
     * Returns whether there is an index available beyond the given one. This
     * checks against the entire list, not the current group.
     *
     * @param int The index.
     * @return boolean
     */
    public boolean hasNextIndex(int index)
    {
        if (_nonCachedGroupCount > 0) return index < _nonCachedGroupCount - 1;
        else return ( index < (_list.size()-1) );
    }

    /**
     * Move the current group to the supplied group number.
     *
     * @param int The group to set as current.
     */
    public void setCurrentGroup(int group)
    {
        if (group == _currentGroup) return;
        if ( group >= getGroupCount() ) group = getGroupCount()-1;
        else if (group < 0) group = 0;
        _currentGroup = group;
    }

    /**
     * Returns whether we have a next group to go to.
     *
     * @return boolean
     */
    public boolean hasNextGroup()
    {
        return ( _currentGroup+1 < getGroupCount() );
    }

    /**
     * Move to the next available group. If the current group is the last group, then an
     * IllegalStateException is thrown.
     *
     * @throws IllegalStateException if the current group is the last group.
     */
    public void nextGroup()
    {
        if ( !hasNextGroup() )
        {
            throw new IllegalStateException("Current group is last group, cannot move next");
        }
        _currentGroup++;
    }

    /**
     * Returns whether we have a previous group to go to.
     *
     * @return boolean
     */
    public boolean hasPreviousGroup()
    {
        return (_currentGroup > 0);
    }

    /**
     * Move to the previous available group. If the current group is the first group, then an
     * IllegalStateException is thrown.
     *
     * @throws IllegalStateException if the current group is the first group.
     */
    public void previousGroup()
    {
        if ( !hasPreviousGroup() )
        {
            throw new IllegalStateException("Current group is first group, cannot move previous");
        }
        _currentGroup--;
    }

    /**
     * Returns the current group as a List based on the group index.
     *
     * @return List
     */
    public List getCurrentGroup()
    {
        int start = getStartIndex();
        if (start < 0) return new ArrayList(0);
        return _list.subList( start, getEndIndex()+1 );
    }

    /**
     * Returns the start index of the current group. It returns -1, if the
     * list size is 0.
     *
     * @return int
     */
    public int getStartIndex()
    {
        if (_list.size() == 0) return -1;
        else if (_nonCachedGroupCount > 0) return 0;
        else return _currentGroup * _groupSize;
    }

    /**
     * Returns the end index of the current group. It returns -1, if the
     * list size is 0.
     *
     * @return int
     */
    public int getEndIndex()
    {
        if (_list.size() == 0) return -1;
        int endIndex = getStartIndex() + _groupSize - 1;
        if ( endIndex >= _list.size() ) endIndex = _list.size()-1;
        return endIndex;
    }

    /**
     * Returns the total size of the list.
     *
     * @return int
     */
    public int getTotalSize()
    {
        if (_nonCachedTotalSize > 0) return _nonCachedTotalSize;
        else return _list.size();
    }
}
