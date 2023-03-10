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
 * Copyright 1999-2002 (C) Intalio, Inc. All Rights Reserved.
 */
package org.exolab.javasource;

/**
 * Holds information about a given annotation type element.
 *
 * @author <a href="mailto:andrew DOT fawcett AT coda DOTcom">Andrew Fawcett</a>
 * @version $Revision: 6669 $ $Date: 2006-04-25 16:09:10 -0600 (Tue, 25 Apr 2006) $
 */
public final class JAnnotationTypeElement implements JMember {
    //--------------------------------------------------------------------------

    private String      _name;
    private JDocComment _comment;
    private JType       _type;
    private JModifiers  _modifiers;
    private String      _default;

    //--------------------------------------------------------------------------

    /**
     * Constructs a JAnnotationTypeElement with a given name and type.
     *
     * @param name Name of this new JAnnotatedTypeElement.
     * @param type Type of this new JAnnotatedTypeElement.
     */
    public JAnnotationTypeElement(final String name, final JType type) {
        setName(name);
        
        _type = type;
        _modifiers = new JModifiers();
        _comment = new JDocComment();
        _comment.appendComment("Element " + name);
    }

    //--------------------------------------------------------------------------

    /**
     * Returns the modifiers for this JAnnotationTypeElement.
     *
     * @return The modifiers for this JAnnotationTypeElement.
     */
    public JModifiers getModifiers() {
        return _modifiers;
    }

    /**
     * Sets the name of this JAnnotationTypeElement.
     *
     * @param name The name of this JAnnotationTypeElement.
     */
    public void setName(final String name) {
        if (!JNaming.isValidJavaIdentifier(name)) {
            String err = "'" + name + "' is ";
            if (JNaming.isKeyword(name)) {
                err += "a reserved word and may not be used as "
                    + " a field name.";
            } else {
                err += "not a valid Java identifier.";
            }
            throw new IllegalArgumentException(err);
        }
        _name = name;
    }

    /**
     * Returns the name of this JAnnotationTypeElement.
     *
     * @return The name of this JAnnotationTypeElement.
     */
    public String getName() {
        return _name;
    }

    /**
     * Returns the JType representing the type of this JAnnotationTypeElement.
     *
     * @return The JType representing the type of this JAnnotationTypeElement.
     */
    public JType getType() {
        return _type;
    }

    /**
     * Returns the initialization string for this JAnnotationTypeElement.
     *
     * @return The initialization string for this JAnnotationTypeElement.
     */
    public String getDefaultString() {
        return _default;
    }

    /**
     * Sets the initialization string for this JAnnotationTypeElement. This
     * method allows some flexibility in declaring default values.
     *
     * @param defaultString The default string for this member.
     */
    public void setDefaultString(final String defaultString) {
        _default = defaultString;
    }

    /**
     * Sets the JavaDoc comment describing this member.
     *
     * @param comment The JDocComment for this member.
     */
    public void setComment(final JDocComment comment) {
        _comment = comment;
    }

    /**
     * Sets the JavaDoc comment describing this member.
     *
     * @param comment The JDocComment for this member.
     */
    public void setComment(final String comment) {
        if (_comment == null) {
            _comment = new JDocComment();
        }
        _comment.setComment(comment);
    }

    /**
     * Returns the JavaDoc comment describing this member.
     *
     * @return The comment describing this member, or null if no comment has
     *         been set.
     */
    public JDocComment getComment() {
        return _comment;
    }

    //--------------------------------------------------------------------------

    /**
     * Outputs the annotation type element to the provided JSourceWriter.
     *
     * @param jsw the JSourceWriter to print this element to
     */
    public void print(final JSourceWriter jsw) {
        if (_comment != null) { _comment.print(jsw); }
        jsw.write(_type.toString());
        jsw.write(" ");
        jsw.write(_name);
        jsw.write("()");
        if (_default != null) {
            jsw.write(" default ");
            jsw.write(_default);
        }
        jsw.write(";");
    }

    //--------------------------------------------------------------------------
}
