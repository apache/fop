/*
 * $Id$
 * Copyright (C) 2001 The Apache Software Foundation. All rights reserved.
 * For details on use and redistribution please refer to the
 * LICENSE file included with these sources.
 */

package org.apache.fop.layoutmgr;


import org.apache.fop.area.Area;
import org.apache.fop.area.Resolveable;
import org.apache.fop.area.PageViewport;

/**
 * The interface for all LayoutManagers.
 */
public interface LayoutManager {
    public boolean generatesInlineAreas();
    public Area getParentArea (Area childArea);
    public void addChild (Area childArea);
    public void setParentLM(LayoutManager lm);

    /**
     * Return true if the next area which would be generated by this
     * LayoutManager could start a new line (or flow for block-level FO).
     */
    public boolean canBreakBefore(LayoutContext lc);

    /**
     * Generate and return the next break possibility.
     * @param context The layout context contains information about pending
     * space specifiers from ancestor areas or previous areas, reference
     * area inline-progression-dimension and various other layout-related
     * information.
     */
    public BreakPoss getNextBreakPoss(LayoutContext context);


    /**
     * Return a value indicating whether this LayoutManager has laid out
     * all its content (or generated BreakPossibilities for all content.)
     */
    public boolean isFinished();

    /**
     * Set a flag indicating whether the LayoutManager has laid out all
     * its content. This is generally called by the LM itself, but can
     * be called by a parentLM when backtracking.
     */
    public void setFinished(boolean isFinished);

    /**
     * Tell the layout manager to add all the child areas implied
     * by Position objects which will be returned by the
     * Iterator.
     */
    public void addAreas(PositionIterator posIter, LayoutContext context);

    public void init();

    public void resetPosition(Position position);

    public void getWordChars(StringBuffer sbChars, Position bp1,
                             Position bp2);

    /**
     * Get the string of the current page number.
     *
     * @return the string for the current page number
     */
    public String getCurrentPageNumber();

    /**
     * Resolve the id reference.
     * This is called by an area looking for an id reference.
     * If the id reference is not found then it should add a resolveable object.
     *
     * @param ref the id reference
     * @return the page containing the id reference or null if not found
     */
    public PageViewport resolveRefID(String ref);

    /**
     * Add an id to the page.
     * @todo add the location of the area on the page
     *
     * @param id the id reference to add.
     */
    public void addIDToPage(String id);

    /**
     * Add an unresolved area.
     * The is used to add a resolveable object to the page for a given id.
     *
     * @param id the id reference this object needs for resolving
     * @param res the resolveable object
     */
    public void addUnresolvedArea(String id, Resolveable res);

    /**
     * Add the marker.
     * A number of formatting objects may contain markers. This
     * method is used to add those markers to the page.
     *
     * @param name the marker class name
     * @param lm the layout manager of the marker child
     * @param start true if the formatting object is starting false is finishing
     */
    public void addMarker(String name, LayoutManager lm, boolean start);

    /**
     * Retrieve a marker.
     * This method is used when retrieve a marker.
     *
     * @param name the class name of the marker
     * @param pos the retrieve position
     * @param boundary the boundary for retrieving the marker
     * @return the layout manaager of the retrieved marker if any
     */
    public LayoutManager retrieveMarker(String name, int pos, int boundary);

}
