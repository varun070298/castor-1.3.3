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
 * Copyright 1999 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id: SaxUnmarshaller.java 5951 2006-05-30 22:18:48Z bsnyder $
 */

package org.exolab.castor.xml.schema.reader;

//-- imported classes and packages
import org.exolab.castor.xml.schema.Resolver;
import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * The base class for unmarshallers
 * @author <a href="mailto:kvisco@intalio.com">Keith Visco</a>
 * @version $Revision: 5951 $ $Date: 2006-04-14 04:14:43 -0600 (Fri, 14 Apr 2006) $ 
**/
public abstract class SaxUnmarshaller 
    implements DocumentHandler, org.xml.sax.ErrorHandler
{

    
      //--------------------/
     //- Member Variables -/
    //--------------------/
    
    /**
     * The document locator
    **/
    protected Locator _locator = null;
    
    /**
     * The resolver to be used for resolving id references
    **/
    private Resolver _resolver;

      //----------------/
     //- Constructors -/
    //----------------/

    public SaxUnmarshaller() {
        super();
    } //-- SaxUnmarshaller

      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the name of the element that this SaxUnmarshaller
     * handles
     * @return the name of the element that this SaxUnmarshaller
     * handles
    **/
    public abstract String elementName();
    
    /**
     * Returns the Object created by this Unmarshaller
     * @return the Object created by this Unmarshaller
    **/
    public abstract Object getObject();
    
    /**
     * Called to signal an end of unmarshalling. This method should
     * be overridden to perform any necessary clean up by an unmarshaller
    **/
    public void finish() throws SAXException {}
    
    public Locator getDocumentLocator() {
        return _locator;
    } //-- getLocator
    
    /**
     * Returns the resolver used for resolving id references.
     * @return the resolver used for resolving id references.
    **/
    public Resolver getResolver() {
        return _resolver;
    } //-- getResolver
    
    /**
     * Sets the Resolver to be used for resolving id references
     * @param resolver the Resolver to be used for resolving
     * id references
    **/
    public void setResolver(Resolver resolver) {
        _resolver = resolver;
    } //-- setResolver

    /**
     * Determines if the given sequence of characters consists
     * of whitespace characters
     * @param chars an array of characters to check for whitespace
     * @param start the start index into the character array
     * @param length the number of characters to check
     * @return true if the characters specficied consist only
     * of whitespace characters
    **/
    public static boolean isWhiteSpace(char[] chars, int start, int length) {
        int max = start+length;
        for (int i = start; i < max; i++) {
            char ch = chars[i];
            switch(ch) {
                case ' ':
                case '\n':
                case '\t':
                case '\r':
                    break;
                default:
                    return false;
            }
        }
        return true;
    } //-- isWhiteSpace
   
    /**
     * This method is called for a general error.
     * @param err the error message to report
     * @exception org.xml.sax.SAXException always thrown.
    **/
    public void error(String err) 
        throws org.xml.sax.SAXException
    {
            
        if (_locator != null) {
            err += "\n   line: " + _locator.getLineNumber();
        }
        
        throw new SAXException(err);
    } //-- error
    
    /**
     * This method is called when an illegal Attribute is encountered.
     * @param attName the name of the illegal attribute.
     * @exception org.xml.sax.SAXException always thrown.
    **/
    public void illegalAttribute(String attName) 
        throws org.xml.sax.SAXException
    {
        String err = "Illegal attribute '" + attName + 
            "' found on element <" + elementName() + ">.";
            
        if (_locator != null) {
            err += "\n   line: " + _locator.getLineNumber();
        }
        
        throw new SAXException(err);
    } //-- illegalAttribute
    
    /**
     * This method is called when an illegal Element is encountered.
     * @param name the name of the illegal element
     * @exception org.xml.sax.SAXException always thrown.
    **/
    public void illegalElement(String name) 
        throws org.xml.sax.SAXException
    {
        String err = "Illegal element '" + name + 
            "' found as child of <" + elementName() + ">.";
            
        if (_locator != null) {
            err += "\n   line: " + _locator.getLineNumber();
        }
        
        throw new SAXException(err);
    } //-- illegalElement
   
   
    /**
     * This method is called when an element which may only
     * be defined once, is redefined.
     * @param name the name of the element
     * @exception org.xml.sax.SAXException always thrown.
    **/
    public void redefinedElement(String name) 
        throws org.xml.sax.SAXException
    {
        redefinedElement(name, null);
    } //-- redefinedElement

    /**
     * This method is called when an element which may only
     * be defined once, is redefined.
     * @param name the name of the element
     * @exception org.xml.sax.SAXException always thrown.
    **/
    public void redefinedElement(String name, String xtraInfo) 
        throws org.xml.sax.SAXException
    {
        String err = "redefintion of element '" + name + 
            "' within element <" + elementName() + ">.";
            
        if (_locator != null) {
            err += "\n   line: " + _locator.getLineNumber();
        }
        
        if (xtraInfo != null) {
            err += "\n   " + xtraInfo;
        }
        
        throw new SAXException(err+"\n");
    } //-- redefinedElement
    
    /**
     * This method is called when an out of order element is encountered
     * @exception org.xml.sax.SAXException always thrown.
    **/
    public void outOfOrder(String name) 
        throws org.xml.sax.SAXException 
    {
        StringBuffer err = new StringBuffer("out of order element <");
        err.append(name);
        err.append("> found in <");
        err.append(elementName());
        err.append(">.");
        throw new SAXException(err.toString());
    }
    
    /**
     * Converts the given String to an int
     * @param str the String to convert to an int
     * @return the int derived from the given String
     * @exception IllegalArgumentException when the given
     * String does not represent a valid int
    **/
    public static int toInt(String str) 
        throws IllegalArgumentException
    {
        try {
            return Integer.parseInt(str);
        }
        catch(NumberFormatException nfe) {
            String err = str+" is not a valid integer. ";
            throw new IllegalArgumentException(err);
        }
    } //-- toInt
    
    
    //---------------------------------------/
    //- org.xml.sax.DocumentHandler methods -/
    //---------------------------------------/
    
    public void characters(char[] ch, int start, int length) 
        throws org.xml.sax.SAXException
    {
        //-- do nothing
        
    } //-- characters
    
    public void endDocument()
        throws org.xml.sax.SAXException
    {
        //-- do nothing
        
    } //-- endDocument
    
    public void endElement(String name) 
        throws org.xml.sax.SAXException
    {
        //-- do nothing
        
    } //-- endElement


    public void ignorableWhitespace(char[] ch, int start, int length) 
        throws org.xml.sax.SAXException
    {
        //-- do nothing
        
    } //-- ignorableWhitespace

    public void processingInstruction(String target, String data) 
        throws org.xml.sax.SAXException
    {
        //-- do nothing

    } //-- processingInstruction
    
    public void setDocumentLocator(Locator locator) {
        this._locator = locator;
    } //-- setDocumentLocator
    
    public void startDocument()
        throws org.xml.sax.SAXException
    {
        //-- do nothing
        
    } //-- startDocument

    
    public void startElement(String name, AttributeList atts) 
        throws org.xml.sax.SAXException
    {
        //-- do nothing
        
    } //-- startElement
    

    //------------------------------------/
    //- org.xml.sax.ErrorHandler methods -/
    //------------------------------------/
    
    public void error(SAXParseException exception)
        throws org.xml.sax.SAXException
    {
        throw exception;
        
    } //-- error
    
    public void fatalError(SAXParseException exception)
        throws org.xml.sax.SAXException
    {
        throw exception;
        
    } //-- fatalError
    
    
    public void warning(SAXParseException exception)
        throws org.xml.sax.SAXException
    {
        throw exception;
        
    } //-- warning
    
} //-- SaxUnmarshaller

