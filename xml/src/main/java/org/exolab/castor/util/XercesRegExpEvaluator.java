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
 * Copyright 2002 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id: XercesRegExpEvaluator.java 8788 2011-02-20 08:11:10Z wguttmn $
 */

package org.exolab.castor.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
 
 /**
  * An implementation of the XercesRegExpEvaluator that uses the
  * Regular Expression library in Xerces. For more information
  * about the Xerces Regular Expression library please visit:
  * <a href="
http://xml.apache.org/xerces-j/apiDocs/org/apache/xerces/utils/regex/RegularExpression.html
">
  * 
http://xml.apache.org/xerces-j/apiDocs/org/apache/xerces/utils/regex/RegularExpression.html
</a>
  *
  * @author <a href="mailto:kvisco@intalio.com">Keith Visco</a>
  * @author <a href="mailto:tora@debian.org">Takashi Okamoto</a>
  * @version $Revision: 8788 $ $Date: 2006-01-16 13:22:58 -0700 (Mon, 16 Jan 2006) $
 **/
 public class XercesRegExpEvaluator 
    implements RegExpEvaluator
{
    private static final Log LOG = LogFactory.getLog(XercesRegExpEvaluator.class);
     
    private static final String BOL = "^";
    private static final String EOL = "$";
    
    private static final String CLASS_NAME = "org.apache.xerces.impl.xpath.regex.RegularExpression";
    private static final String OLD_CLASS_NAME = "org.apache.xerces.utils.regex.RegularExpression";
    
    /**
     * The Regular expression
    **/
    // RegularExpression _regexp = null;
    Object _regexp = null;

    private Constructor<?> _constructor;
    
    /**
     * Name of the actual class used for regular expression matching.
     */
    private String className;

    /**
     * Creates a new XercesRegExpEvaluator
    **/
    public XercesRegExpEvaluator() {
        super();
        
        Class<?> regexpClass = null;
        try {
            regexpClass = Class.forName(CLASS_NAME);
            _constructor = regexpClass.getConstructor( new Class[] { String.class } );
            className = CLASS_NAME;
        } catch (ClassNotFoundException e) {
            try{
              regexpClass = Class.forName(OLD_CLASS_NAME);
              _constructor = regexpClass.getConstructor( new Class[] { String.class } );
              className = OLD_CLASS_NAME;
            } catch (ClassNotFoundException e2) {
              LOG.error("Problem loading class " + this.className, e2);
              throw new IllegalAccessError("Problem loading class " + this.className + ": " + e.getMessage());
            } catch (SecurityException e2) {
              LOG.error("Problem accessing constructor of class " + this.className, e2);
              throw new IllegalAccessError("Problem accessing constructor of class " + this.className + ": " + e.getMessage());
            } catch (NoSuchMethodException e2) {
              LOG.error("Problem locating constructor of class " + this.className, e2);
              throw new IllegalAccessError("class " + this.className + ": " + e.getMessage());
            }
        } catch (SecurityException e) {
            LOG.error("Problem accessing constructor of class " + this.className, e);
            throw new IllegalAccessError("Problem accessing constructor of class " + this.className + ": " + e.getMessage());
        } catch (NoSuchMethodException e) {
            LOG.error("Problem locating constructor of class " + this.className, e);
            throw new IllegalAccessError("class " + this.className + ": " + e.getMessage());
        }

    } //-- XercesRegExpEvaluator
    
    /**
     * Sets the regular expression to match against during
     * a call to #matches
     *
     * @param rexpr the regular expression
    **/
    public void setExpression(String rexpr) {
        
        if (rexpr != null) {
            try {
                _regexp = _constructor.newInstance(new Object[] { BOL + rexpr + EOL } );
            } catch (Exception e) {
                LOG.error("Problem invoking constructor on " + this.className, e);
                String err = "XercesRegExp Syntax error: "
                    + e.getMessage()
                    + " ; error occured with the following "
                    + "regular expression: " + rexpr;
                throw new IllegalArgumentException(err);
            }
        } else {
            _regexp = null;
        }
    } //-- setExpression
    
    /**
     * Returns true if the given String is matched by the 
     * regular expression of this RegExpEvaluator
     *
     * @param value the String to check the production of
     * @return true if the given string matches the regular
     * expression of this RegExpEvaluator
     * @see #setExpression
    **/
    public boolean matches(String value)
    {
        if (_regexp != null) {
            // return _regexp.matches(value); 
            Method method;
            try {
                method = _regexp.getClass().getMethod("matches", new Class[] { String.class } );
                return ((Boolean) method.invoke(_regexp, new Object[] { value } )).booleanValue();
            } catch (SecurityException e) {
                LOG.error("Security problem accessing matches(String) method of class " + this.className, e);
            } catch (NoSuchMethodException e) {
                LOG.error("Method matches(String) of class " + this.className + " could not be found.", e);
            } catch (IllegalArgumentException e) {
                LOG.error("Invalid argument provided to method matches(String) of class " + this.className, e);
            } catch (IllegalAccessException e) {
                LOG.error("Illegal acces to method matches(String) of class " + this.className, e);
            } catch (InvocationTargetException e) {
                LOG.error("Invalid invocation of method matches(String) of class " + this.className, e);
            }
        }
        return true;
    } //-- matches
    
 } //-- XercesRegExpEvaluator