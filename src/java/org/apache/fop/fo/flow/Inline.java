/*
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id$ */

package org.apache.fop.fo.flow;

// XML
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;

// FOP
import org.apache.fop.apps.FOPException;
import org.apache.fop.fo.CharIterator;
import org.apache.fop.fo.FONode;
import org.apache.fop.fo.FObjMixed;
import org.apache.fop.fo.InlineCharIterator;
import org.apache.fop.layoutmgr.AddLMVisitor;
import org.apache.fop.fo.properties.CommonAccessibility;
import org.apache.fop.fo.properties.CommonAural;
import org.apache.fop.fo.properties.CommonBackground;
import org.apache.fop.fo.properties.CommonBorderAndPadding;
import org.apache.fop.fo.properties.CommonMarginInline;
import org.apache.fop.fo.properties.CommonRelativePosition;

/**
 * Class modelling the fo:inline object. See Sec. 6.6.7 of the XSL-FO Standard.
 */
public class Inline extends FObjMixed {

    // Textdecoration
    /** is this text underlined? */
    protected boolean underlined = false;
    /** is this text overlined? */
    protected boolean overlined = false;
    /** is this text lined through? */
    protected boolean lineThrough = false;

    /**
     * @param parent FONode that is the parent of this object
     */
    public Inline(FONode parent) {
        super(parent);
    }

    /**
     * @see org.apache.fop.fo.FObj#addProperties
     */
    protected void addProperties(Attributes attlist) throws SAXParseException {
        super.addProperties(attlist);

        if (parent.getName().equals("fo:flow")) {
            throw new SAXParseException("inline formatting objects cannot"
                                   + " be directly under flow", locator);
        }

        setupID();

        int textDecoration = this.propertyList.get(PR_TEXT_DECORATION).getEnum();

        if (textDecoration == TextDecoration.UNDERLINE) {
            this.underlined = true;
        }

        if (textDecoration == TextDecoration.OVERLINE) {
            this.overlined = true;
        }

        if (textDecoration == TextDecoration.LINE_THROUGH) {
            this.lineThrough = true;
        }
        
        getFOInputHandler().startInline(this);
    }

    /**
     * @see org.apache.fop.fo.FONode#end
     */
    protected void endOfNode() throws SAXParseException {
        getFOInputHandler().endInline(this);
    }

    /**
     * @see org.apache.fop.fo.FObjMixed#charIterator
     */
    public CharIterator charIterator() {
        return new InlineCharIterator(this, propMgr.getBorderAndPadding());
    }

    public String getName() {
        return "fo:inline";
    }
    
    /**
     * @see org.apache.fop.fo.FObj#getNameId()
     */
    public int getNameId() {
        return FO_INLINE;
    }
}
