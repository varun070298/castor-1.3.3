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
 *
 * $Id: FacetUnmarshaller.java 7492 2008-03-11 13:40:42Z wguttmn $
 */

package org.exolab.castor.xml.schema.reader;

//-- imported classes and packages
import org.exolab.castor.xml.AttributeSet;
import org.exolab.castor.xml.Namespaces;
import org.exolab.castor.xml.XMLException;
import org.exolab.castor.xml.schema.Annotation;
import org.exolab.castor.xml.schema.Facet;
import org.exolab.castor.xml.schema.FacetFactory;
import org.exolab.castor.xml.schema.SchemaContext;
import org.exolab.castor.xml.schema.SchemaException;
import org.exolab.castor.xml.schema.SchemaNames;

/**
 * A class for Unmarshalling facets
 * @author <a href="mailto:kvisco@intalio.com">Keith Visco</a>
 * @version $Revision: 7492 $ $Date: 2003-03-03 00:05:44 -0700 (Mon, 03 Mar 2003) $
**/
public class FacetUnmarshaller extends ComponentReader {


      //--------------------/
     //- Member Variables -/
    //--------------------/

    /**
     * The current ComponentReader.
    **/
    private ComponentReader unmarshaller = null;

    /**
     * The current branch depth
    **/
    private int depth = 0;

    /**
     * The Facet we are constructing
    **/
    private Facet _facet = null;

    /**
     * The element name of the Facet currently being unmarshalled
    **/
    private String _elementName = null;

      //----------------/
     //- Constructors -/
    //----------------/

    /**
     * Creates a new FacetUnmarshaller.
     * @param schemaContext the {@link SchemaContext} to get some configuration settings from
     * @param name the name of the Facet
     * @param atts the AttributeList
    **/
    public FacetUnmarshaller (
            final SchemaContext schemaContext,
            final String name,
            final AttributeSet atts) 
    throws XMLException {
        super(schemaContext);

        _elementName = name;

        if (!isFacet(name)) {
            String err = "'" + name + "' is not a valid or supported facet.";
            throw new IllegalArgumentException(err);
        }

        _facet = FacetFactory.getInstance().createFacet(
                name, atts.getValue(SchemaNames.VALUE_ATTR));

    } //-- FacetUnmarshaller

      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the name of the element that this ComponentReader
     * handles
     * @return the name of the element that this ComponentReader
     * handles
    **/
    public String elementName() {
        return _elementName;
    } //-- elementName

    /**
     *
    **/
    public Facet getFacet() {
        return _facet;
    } //-- getArchetype

    /**
     * Returns the Object created by this ComponentReader
     * @return the Object created by this ComponentReader
    **/
    public Object getObject() {
        return getFacet();
    } //-- getObject

    /**
     * Signals the start of an element with the given name.
     *
     * @param name the NCName of the element. It is an error
     * if the name is a QName (ie. contains a prefix).
     * @param namespace the namespace of the element. This may be null.
     * Note: A null namespace is not the same as the default namespace unless
     * the default namespace is also null.
     * @param atts the AttributeSet containing the attributes associated
     * with the element.
     * @param nsDecls the namespace declarations being declared for this 
     * element. This may be null.
    **/
    public void startElement(String name, String namespace, AttributeSet atts,
        Namespaces nsDecls)
        throws XMLException
    {
        //-- Do delagation if necessary
        if (unmarshaller != null) {
            unmarshaller.startElement(name, namespace, atts, nsDecls);
            ++depth;
            return;
        }

        if (SchemaNames.ANNOTATION.equals(name)) {
            unmarshaller = new AnnotationUnmarshaller(getSchemaContext(), atts);
        }
        else illegalElement(name);

    } //-- startElement

    /**
     * Signals to end of the element with the given name.
     *
     * @param name the NCName of the element. It is an error
     * if the name is a QName (ie. contains a prefix).
     * @param namespace the namespace of the element.
    **/
    public void endElement(String name, String namespace)
        throws XMLException
    {
        //-- Do delagation if necessary
        if ((unmarshaller != null) && (depth > 0)) {
            unmarshaller.endElement(name, namespace);
            --depth;
            return;
        }

        if (unmarshaller == null)
            throw new SchemaException("missing start element: " + name);
        else if (SchemaNames.ANNOTATION.equals(name)) {
            Annotation annotation = (Annotation)unmarshaller.getObject();
            _facet.addAnnotation(annotation);
        }

    } //-- endElement

    public void characters(char[] ch, int start, int length)
        throws XMLException
    {
        //-- Do delagation if necessary
        if (unmarshaller != null) {
            unmarshaller.characters(ch, start, length);
        }
    } //-- characters

    protected static boolean isFacet(String name) {

        if (Facet.ENUMERATION.equals(name))   return true;
        if (Facet.LENGTH.equals(name))        return true;
        if (Facet.PATTERN.equals(name))       return true;
        if (Facet.MAX_EXCLUSIVE.equals(name)) return true;
        if (Facet.MIN_EXCLUSIVE.equals(name)) return true;
        if (Facet.MAX_INCLUSIVE.equals(name)) return true;
        if (Facet.MIN_INCLUSIVE.equals(name)) return true;
        if (Facet.MAX_LENGTH.equals(name))    return true;
        if (Facet.MIN_LENGTH.equals(name))    return true;
        if (Facet.WHITESPACE.equals(name))    return true;
        if (Facet.TOTALDIGITS.equals(name))   return true;
        if (Facet.FRACTIONDIGITS.equals(name))return true;
        return false;
    } //-- isFacet

} //-- FacetUnmarshaller
