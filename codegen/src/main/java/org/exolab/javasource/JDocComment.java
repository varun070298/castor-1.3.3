/**
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright
 *    statements and notices.  Redistributions must also contain a
 *    copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the
 *    above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. The name "Exolab" must not be used to endorse or promote
 *    products derived from this Software without prior written
 *    permission of Intalio, Inc.  For written permission,
 *    please contact info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Intalio, Inc. Exolab is a registered
 *    trademark of Intalio, Inc.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY INTALIO, INC. AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * INTALIO, INC. OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 1999-2003 (C) Intalio, Inc. All Rights Reserved.
 */
package org.exolab.javasource;

import java.util.Enumeration;
import java.util.Vector;

/**
 * A class that "SOMEWHAT" represents a JavaDoc Comment.
 *
 * @author <a href="mailto:keith AT kvisco DOT com">Keith Visco</a>
 * @version $Revision: 7943 $ $Date: 2005-02-26 17:30:28 -0700 (Sat, 26 Feb 2005) $
 */
public final class JDocComment {
    //--------------------------------------------------------------------------

    /** An ordered list of descriptors. */
    private Vector<JDocDescriptor> _descriptors = null;
    
    /** The internal buffer for this JDocComment. */
    private StringBuffer _comment     = null;

    //--------------------------------------------------------------------------

    /**
     * Creates a new JavaDoc Comment.
     */
    public JDocComment() {
        super();
        
        _descriptors = new Vector<JDocDescriptor>();
        _comment     = new StringBuffer();
    }

    /**
     * Creates a new JavaDoc Comment and initializie it with given JDocDescriptor.
     * 
     * @param jdesc The JDocDescriptor to add.
     */
    public JDocComment(final JDocDescriptor jdesc) {
        super();
        
        _descriptors = new Vector<JDocDescriptor>();
        _comment = new StringBuffer();
        
        addDescriptor(jdesc);
    }

    //--------------------------------------------------------------------------

    /**
     * Adds the given JDocDescriptor to this JDocComment.
     *
     * @param jdesc The JDocDescriptor to add.
     */
    public void addDescriptor(final JDocDescriptor jdesc) {
        if (jdesc == null) { return; }
        //-- on the fly sorting of descriptors
        if (_descriptors.size() == 0) {
            _descriptors.addElement(jdesc);
            return;
        }

        for (int i = 0; i < _descriptors.size(); i++) {
            JDocDescriptor jdd = _descriptors.elementAt(i);

            short compare = jdesc.compareTo(jdd);

            switch (compare) {
                case  0: // equal
                    _descriptors.insertElementAt(jdesc, i + 1);
                    return;
                case -1: //-- less than
                    _descriptors.insertElementAt(jdesc, i);
                    return;
                default:
                    //-- keep looking
                    break;
            }
        }

        //-- if we make it here we need to add
        _descriptors.addElement(jdesc);
    }

    /**
     * Appends the provided comment String to this JDocComment.
     *
     * @param comment The comment to append.
     */
    public void appendComment(final String comment) {
        _comment.append(comment);
    }

    /**
     * Returns the String value of this JDocComment.
     *
     * @return The String value of this JDocComment.
     */
    public String getComment() {
        return _comment.toString();
    }

    /**
     * Sets the comment String of this JDocComment.
     *
     * @param comment The comment String of this JDocComment.
     */
    public void setComment(final String comment) {
        _comment.setLength(0);
        _comment.append(comment);
    }

    /**
     * Returns an Enumeration of the parameters of this JDocComment.
     *
     * @return An Enumeration of the parameters of this JDocComment.
     */
    public Enumeration<JDocDescriptor> getDescriptors() {
        return _descriptors.elements();
    }

    /**
     * Returns the length of the JavaDoc comment in characters.
     *
     * @return The length of the JavaDoc comment in characters.
     */
    public int getLength() {
        return _comment.length();
    }

    /**
     * Returns the Parameter Descriptor associated with the given name.
     *
     * @param name The name whose ParamDescriptor is being searched for.
     * @return the Parameter Descriptor associated with the given name.
     */
    public JDocDescriptor getParamDescriptor(final String name) {
        if (name == null) { return null; }

        for (int i = 0; i < _descriptors.size(); i++) {
            JDocDescriptor jdd
                = _descriptors.elementAt(i);
            if (jdd.getType() == JDocDescriptor.PARAM) {
                if (name.equals(jdd.getName())) { return jdd; }
            }
        }
        return null;
    }

    //--------------------------------------------------------------------------

    /**
     * Prints this JavaDoc comment using the given JSourceWriter.
     *
     * @param jsw The JSourceWriter to print to.
     */
    public void print(final JSourceWriter jsw) {
        //-- I reuse JComment for printing
        JComment jComment = new JComment(JComment.JAVADOC_STYLE);

        jComment.setComment(_comment.toString());

        //-- force a separating "*" for readability
        if (_descriptors.size() > 0) {
            jComment.appendComment("\n");
        }

        for (int i = 0; i < _descriptors.size(); i++) {
            jComment.appendComment("\n");
            jComment.appendComment(_descriptors.elementAt(i).toString());
        }
        jComment.print(jsw);
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("/**\n");
        sb.append(" * ");
        sb.append(" */\n");
        return sb.toString();
    }

    //--------------------------------------------------------------------------
}
