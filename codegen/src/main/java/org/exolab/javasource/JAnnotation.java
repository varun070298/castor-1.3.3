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

import java.lang.reflect.Array;
import java.util.LinkedHashMap;

/**
 * JAnnotation represents a single annotation against a code element. The
 * methods described on the JAnnotatedElement interface are used to associate
 * JAnnotation's with various other objects in this package describing Java code
 * elements.
 * <p>
 * The print method outputs annotations in various forms (as described in the
 * Java Language Specification Third Edition) based on the methods called.
 * <p>
 * For "Marker Annotation", construct with the appropriate JAnnotationType.
 * <pre>
 *   JAnnotationType preliminaryType = new JAnnotationType("Preliminary");
 *   JAnnotation preliminary = new JAnnotation(preliminaryType);
 * </pre>
 * outputs
 * <pre>
 *   &#064;Preliminary()
 * </pre>
 * For "Single Element Annotation", construct as above and call the
 * setValue(value) method to set the value of the "value" element of the
 * annotation type.
 * <pre>
 *   JAnnotationType copyrightType = new JAnnotationType("Copyright");
 *   JAnnotation copyright = new JAnnotation(copyrightType);
 *   copyright.setValue("\"2002 Yoyodyne Systems, Inc., All rights reserved.\"");
 * </pre>
 * outputs
 * <pre>
 *   &#064;Copyright("2002 Yoyodyne Propulsion Systems, Inc., All rights reserved.")
 * </pre>
 * For "Normal Annotation," construct as above then call the appropriate
 * setValue methods that accept an "elementName" parameter.
 * <pre>
 *   JAnnotationType requestType = new JAnnotationType("RequestForEnhancement");
 *   JAnnotation request = new JAnnotation(requestType);
 *   request.setElementValue("id", "2868724");
 *   request.setElementValue("synopsis", "\"Provide time-travel functionality\"");
 *   request.setElementValue("engineer", "\"Mr. Peabody\"");
 *   request.setElementValue("date", "\"4/1/2004\"");
 * </pre>
 * outputs
 * <pre>
 *   &#064;RequestForEnhancement(
 *       id       = 2868724,
 *       sysopsis = "Provide time-travel functionality",
 *       engineer = "Mr. Peabody",
 *       date     = "4/1/2004")
 * </pre>
 * "Complex" annotations are also supported via the various setValue methods
 * that take a JAnnotation object.
 * <pre>
 *   JAnnotationType nameType = new JAnnotationType("Name");
 *   JAnnotationType authorType = new JAnnotationType("Author");
 *   JAnnotation author = new JAnnotation(authorType);
 *   JAnnotation name = new JAnnotation(nameType);
 *   name.setElementValue("first", "\"Joe\"");
 *   name.setElementValue("last", "\"Hacker\"");
 *   author.setValue(name);
 * </pre>
 * outputs
 * <pre>
 *   &#064;Author(&#064;Name(
 *       first = "Joe",
 *       last  = "Hacker"))
 * </pre>
 * Finally annotation elements whose types are arrays are supported via the
 * setValue methods that take arrays:
 * <pre>
 *   JAnnotationType endorsersType = new JAnnotationType("Endorsers");
 *   JAnnotation endorsers = new JAnnotation(endorsersType);
 *   endorsers.setValue(new String[] { "\"Children\"", "\"Unscrupulous dentists\""});
 * </pre>
 * outputs
 * <pre>
 *   &#064;Endorsers(
 *       {
 *           "Children",
 *           "Unscrupulous dentists"
 *       })
 * </pre>
 * Note: Conditional element values are not currently supported. However the
 * setValue methods taking String values can be used to output this construct
 * literally if desired.
 *
 * @author <a href="mailto:andrew DOT fawcett AT coda DOTcom">Andrew Fawcett</a>
 * @version $Revision: 8009 $ $Date: 2006-04-25 16:09:10 -0600 (Tue, 25 Apr 2006) $
 */
public final class JAnnotation {

    /** 
     * Name of a single element. 
     */
    public static final String VALUE = "value";

    /** 
     * Annotation type referenced by this annotation. 
     */
    private JAnnotationType _annotationType;

    /** 
     * Element values associated with this JAnnotation, contains String,
     *  String[], JAnnotation and JAnnotation[] objects. 
     */
    private LinkedHashMap<Object, Object> _elementValues = 
        new LinkedHashMap<Object, Object>();

    /**
     * Constructs a JAnnotation for the given annotation type.
     *
     * @param annotationType Annotation type.
     */
    public JAnnotation(final JAnnotationType annotationType) {
        _annotationType = annotationType;
    }

    /**
     * Returns the JAnnotationType associated with this JAnnotation.
     *
     * @return The JAnnotationType associated with this JAnnotation..
     */
    public JAnnotationType getAnnotationType() {
        return _annotationType;
    }

    /**
     * Sets the "value" annotation element value.
     *
     * @param stringValue Literal String value.
     */
    public void setValue(final String stringValue) {
        _elementValues.put(VALUE, stringValue);
    }

    /**
     * Sets the "value" annotation element value as a list.
     *
     * @param stringValue Array of literal String values.
     */
    public void setValue(final String[] stringValue) {
        _elementValues.put(VALUE, stringValue);
    }

    /**
     * Sets the "value" annotation element value as an annotation.
     *
     * @param annotationValue JAnnotation to be used as this JAnnotation's value.
     */
    public void setValue(final JAnnotation annotationValue) {
        _elementValues.put(VALUE, annotationValue);
    }

    /**
     * Sets the "value" annotation element value as a list of annotation values.
     *
     * @param annotationValues Array of JAnnotations to be used as this
     *        JAnnotation's value.
     */
    public void setValue(final JAnnotation[] annotationValues) {
        _elementValues.put(VALUE, annotationValues);
    }

    /**
     * Adds an annotation element name=value pair.
     *
     * @param elementName Name of this annotation element.
     * @param stringValue Value of this annotation element.
     */
    public void setElementValue(final String elementName, final String stringValue) {
        _elementValues.put(elementName, stringValue);
    }

    /**
     * Adds an annotation element name=list pair.
     *
     * @param elementName Name of this annotation element.
     * @param stringValues String array value of this annotation element.
     */
    public void setElementValue(final String elementName, final String[] stringValues) {
        _elementValues.put(elementName, stringValues);
    }

    /**
     * Adds an annotation element name=annotation pair.
     *
     * @param elementName Name of this annotation element.
     * @param annotationValue Annotation to be used as the value.
     */
    public void setElementValue(final String elementName, final JAnnotation annotationValue) {
        _elementValues.put(elementName, annotationValue);
    }

    /**
     * Adds an annotation element name=array of annotations.
     *
     * @param elementName Name of this annotation element.
     * @param annotationValues Array of annotations to be used as the value.
     */
    public void setElementValue(final String elementName,
            final JAnnotation[] annotationValues) {
        _elementValues.put(elementName, annotationValues);
    }

    /**
     * Returns the annotation element value when it is a String.
     *
     * @return The annotation element value.
     */
    public String getValue() {
        Object elementValue = getElementValueObject(VALUE);
        if (elementValue instanceof String) { return (String) elementValue; }
        throw new IllegalStateException("'value' element is not of type String.");
    }

    /**
     * Returns the annotation element value when it is an annotation.
     *
     * @return The annotation element value when it is an annotation.
     */
    public JAnnotation getValueAnnotation() {
        Object elementValue = getElementValueObject(VALUE);
        if (elementValue instanceof JAnnotation) { return (JAnnotation) elementValue; }
        throw new IllegalStateException("'value' element is not of type JAnnotation.");
    }

    /**
     * For the provided element name, returns the annotation element value when
     * it is a String.
     *
     * @param elementName Element to return the value of.
     * @return The annotation element value.
     */
    public String getElementValue(final String elementName) {
        Object elementValue = getElementValueObject(elementName);
        if (elementValue instanceof String) { return (String) elementValue; }
        throw new IllegalStateException("'" + elementName + "' element is not of type String.");
    }

    /**
     * For the provided element name, returns the annotation element value when
     * it is an array of String.
     *
     * @param elementName Element to return the value of.
     * @return The annotation element value.
     */
    public String[] getElementValueList(final String elementName) {
        Object elementValue = getElementValueObject(elementName);
        if (elementValue instanceof String[]) { return (String[]) elementValue; }
        throw new IllegalStateException("'" + elementName + "' element is not of type String[].");
    }

    /**
     * Returns the given annotation element value as Object, typically used if
     * the value type is not known. This will either be a String or JAnnotation
     * or an array of String or an array of JAnnotation.
     *
     * @param elementName Element to return the value of.
     * @return Annotation element value as Object.
     */
    public Object getElementValueObject(final String elementName) {
        return _elementValues.get(elementName);
    }

    /**
     * For the provided element name, returns the annotation element value when
     * it is a JAnnotation.
     *
     * @param elementName Element to return the value of.
     * @return The annotation element value.
     */
    public JAnnotation getElementValueAnnotation(final String elementName) {
        Object elementValue = getElementValueObject(elementName);
        if (elementValue instanceof JAnnotation) { return (JAnnotation) elementValue; }
        throw new IllegalStateException(
                "'" + elementName + "' element is not of type JAnnotation.");
    }

    /**
     * For the provided element name, returns the annotation element value when
     * it is an array of JAnnotation.
     *
     * @param elementName Element to return the value of.
     * @return The annotation element value.
     */
    public JAnnotation[] getElementValueAnnotationList(final String elementName) {
        Object elementValue = getElementValueObject(elementName);
        if (elementValue instanceof JAnnotation[]) {
            return (JAnnotation[]) elementValue;
        }
        throw new IllegalStateException(
                "'" + elementName + "' element is not of type JAnnotation[].");
    }

    /**
     * Returns the names of the elements set by this annotation.
     *
     * @return Array of element names.
     */
    public String[] getElementNames() {
        return _elementValues.keySet().toArray(
                new String[_elementValues.size()]);
    }

    /**
     * Prints the source code for this JAnnotation to the given JSourceWriter.
     *
     * @param jsw the JSourceWriter to print to. Must not be null.
     */
    public void print(final JSourceWriter jsw) {
        jsw.write("@");
        jsw.write(_annotationType.getLocalName());
        jsw.write("(");
        // Single element annotation?
        String[] elementNames = getElementNames();
        if (elementNames.length == 1 && elementNames[0].equals(VALUE)) {
            // Just output value
            printElementValue(jsw, getElementValueObject(VALUE));
        } else if (elementNames.length > 0) {
            // Max element name length?
            int maxLength = 0;
            for (int i = 0; i < elementNames.length; i++) {
                int elementNameLength = elementNames[i].length();
                if (elementNameLength > maxLength) { maxLength = elementNameLength; }
            }
            // Output element name and values
            jsw.writeln();
            jsw.indent();
            for (int i = 0; i < elementNames.length; i++) {
                int elementNameLength = elementNames[i].length();
                // Output element name with padding
                jsw.write(elementNames[i]);
                for (int p = 0; p < maxLength - elementNameLength; p++) {
                    jsw.write(" ");
                }
                // Assignment operator
                jsw.write(" = ");
                // Value
                printElementValue(jsw, getElementValueObject(elementNames[i]));
                if (i < elementNames.length - 1) {
                    jsw.write(",");
                    jsw.writeln();
                }
            }
            jsw.unindent();
        }
        jsw.write(")");
    }

    /**
     * Prints annotation element value according to its type: String, String[],
     * JAnnotation or JAnnotation[].
     *
     * @param jsw the JSourceWriter to print to. Must not be null.
     * @param elementValue element value to print
     */
    private void printElementValue(final JSourceWriter jsw, final Object elementValue) {
        // String?
        if (elementValue instanceof String) {
            jsw.write((String) elementValue);
            return;
        } else if (elementValue instanceof JAnnotation) {
            JAnnotation annotation = (JAnnotation) elementValue;
            annotation.print(jsw);
            return;
        } else if (elementValue.getClass().isArray()) {
            // Short hand for single item list
            int listLength = Array.getLength(elementValue);
            if (listLength == 1) {
                printElementValue(jsw, Array.get(elementValue, 0));
                return;
            }
            // Output list items
            jsw.indent();
            jsw.writeln();
            jsw.write("{");
            jsw.writeln();
            jsw.indent();
            for (int i = 0; i < listLength; i++) {
                printElementValue(jsw, Array.get(elementValue, i));
                if (i < listLength - 1) { jsw.write(","); }
                jsw.writeln();
            }
            jsw.unindent();
            jsw.write("}");
            jsw.unindent();
            return;
        }
        throw new IllegalArgumentException("'" + elementValue + "' was not expected.");
    }

}
