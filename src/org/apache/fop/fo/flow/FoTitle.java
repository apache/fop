/*
 * $Id$
 * Copyright (C) 2001 The Apache Software Foundation. All rights reserved.
 * For details on use and redistribution please refer to the
 * LICENSE file included with these sources.
 *
 * @author <a href="mailto:pbwest@powerup.com.au">Peter B. West</a>
 */

package org.apache.fop.fo.flow;

// FOP
import org.apache.fop.fo.PropNames;
import org.apache.fop.fo.PropertySets;
import org.apache.fop.fo.FObjectNames;
import org.apache.fop.fo.FObjects;
import org.apache.fop.fo.FONode;
import org.apache.fop.fo.FOTree;
import org.apache.fop.fo.expr.PropertyException;
import org.apache.fop.xml.XMLEvent;
import org.apache.fop.xml.FoXMLEvent;
import org.apache.fop.xml.SyncedFoXmlEventsBuffer;
import org.apache.fop.xml.UnexpectedStartElementException;
import org.apache.fop.apps.FOPException;
import org.apache.fop.datastructs.TreeException;
import org.apache.fop.datatypes.PropertyValue;
import org.apache.fop.datatypes.Ints;
import org.apache.fop.messaging.MessageHandler;

import java.util.HashMap;
import java.util.BitSet;

/**
 * Implements the fo:simple-page-master flow object
 */
public class FoTitle extends FONode {

    private static final String tag = "$Name$";
    private static final String revision = "$Revision$";

    /** Map of <tt>Integer</tt> indices of <i>sparsePropsSet</i> array.
        It is indexed by the FO index of the FO associated with a given
        position in the <i>sparsePropsSet</i> array. See
        {@link org.apache.fop.fo.FONode#sparsePropsSet FONode.sparsePropsSet}.
     */
    private static final HashMap sparsePropsMap;

    /** An <tt>int</tt> array of of the applicable property indices, in
        property index order. */
    private static final int[] sparseIndices;

    /** The number of applicable properties.  This is the size of the
        <i>sparsePropsSet</i> array. */
    private static final int numProps;

    static {
        // Collect the sets of properties that apply
        BitSet propsets = new BitSet();
        propsets.or(PropertySets.accessibilitySet);
        propsets.or(PropertySets.auralSet);
        propsets.or(PropertySets.backgroundSet);
        propsets.or(PropertySets.borderSet);
        propsets.or(PropertySets.paddingSet);
        propsets.or(PropertySets.fontSet);
        propsets.or(PropertySets.marginInlineSet);
        propsets.set(PropNames.COLOR);
        propsets.set(PropNames.LINE_HEIGHT);
        propsets.set(PropNames.VISIBILITY);

        // Map these properties into sparsePropsSet
        // sparsePropsSet is a HashMap containing the indicies of the
        // sparsePropsSet array, indexed by the FO index of the FO slot
        // in sparsePropsSet.
        sparsePropsMap = new HashMap();
        numProps = propsets.cardinality();
        sparseIndices = new int[numProps];
        int propx = 0;
        for (int next = propsets.nextSetBit(0);
                next >= 0;
                next = propsets.nextSetBit(next + 1)) {
            sparseIndices[propx] = next;
            sparsePropsMap.put
                        (Ints.consts.get(next), Ints.consts.get(propx++));
        }
    }

    /** The <tt>SyncedFoXmlEventsBuffer</tt> from which events are drawn. */
    private SyncedFoXmlEventsBuffer xmlevents;

    /**
     * Construct an fo:title node, and build the fo:title subtree.
     * <p>Content model for fo:title: (#PCDATA|%inline;)*
     * @param foTree the FO tree being built
     * @param parent the parent FONode of this node
     * @param event the <tt>FoXMLEvent</tt> that triggered the creation of
     * this node
     */
    public FoTitle(FOTree foTree, FONode parent, FoXMLEvent event)
        throws TreeException, FOPException
    {
        super(foTree, FObjectNames.TITLE, parent, event,
              FONode.TITLE_SET, sparsePropsMap, sparseIndices);
        xmlevents = foTree.getXmlevents();
        FoXMLEvent ev = null;
        do {
            try {
                ev = xmlevents.expectOutOfLinePcdataOrInline();
            } catch(UnexpectedStartElementException e) {
                MessageHandler.logln
                        ("Ignoring unexpected Start Element: "
                                                         + ev.getQName());
                ev = xmlevents.getStartElement();
                ev = xmlevents.getEndElement(ev);
            }
            if (ev != null) {
                // Generate the flow object
                FObjects.fobjects.makeFlowObject
                            (foTree, this, ev, FONode.TITLE_SET);
                ev = xmlevents.getEndElement(ev);
            }
        } while (ev != null);

        makeSparsePropsSet();
    }

}
