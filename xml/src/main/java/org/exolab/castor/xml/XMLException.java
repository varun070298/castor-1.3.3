/*
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
 * $Id: XMLException.java 8870 2011-04-22 12:25:47Z wguttmn $
 */
package org.exolab.castor.xml;

import org.exolab.castor.core.exceptions.CastorException;
import org.exolab.castor.xml.location.Location;

/**
 * An exception that is used to signal an error that has occurred during
 * marshaling or unmarshaling.
 *
 * @author <a href="mailto:kvisco@intalio.com">Keith Visco</a>
 * @version $Revision: 8870 $ $Date: 2006-04-25 15:08:23 -0600 (Tue, 25 Apr 2006) $
 */
public class XMLException extends CastorException {
    /** SerialVersionUID */
    private static final long serialVersionUID = 7512918645754995146L;

    /** 
     * The location for this Exception. 
     */
    private Location  _location   = null;
    
    /** 
     * The error code for this exception. 
     */
    private int errorCode = -1;

    /**
     * Creates a new instance of this class with no message or nested exception.
     */
    public XMLException() {
        super();
    }

    /**
     * Creates a new {@link XMLException} instance with the given message.
     *
     * @param message the message for this Exception
     */
    public XMLException(final String message) {
        super(message);
    }

    /**
     * Creates a new XMLException with the given nested Exception.
     *
     * @param exception the nested exception
     */
    public XMLException(final Throwable exception) {
        super(exception);
    }

    /**
     * Creates a new XMLException with the given message and error code.
     *
     * @param message the message for this Exception
     * @param errorCode the errorCode for this Exception
     * 
     * @deprecated
     */
    public XMLException(final String message, final int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Creates a new XMLException with the given message and nested Exception.
     *
     * @param message the detail message for this Exception
     * @param exception the nested exception
     */
    public XMLException(final String message, final Throwable exception) {
        super(message, exception);
    }

    /**
     * Creates a new XMLException with the given message, nested Exception, and
     * errorCode.
     *
     * @param message the detail message for this exception
     * @param exception the nested exception
     * @param errorCode the errorCode for this Exception
     * 
     * @deprecated
     */
    public XMLException(final String message, final Throwable exception, final int errorCode) {
        super(message, exception);
        this.errorCode = errorCode;
    }

    /**
     * Sets the location information for this Exception.
     *
     * @param location The location information for this validation exception.
     */
    public void setLocation(final Location location) {
        _location = location;
    }

    /**
     * Returns the String representation of this Exception.
     *
     * @return the String representation of this Exception.
     */
    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append(this.getClass().getName());
        
        String msg = this.getMessage();
        if (msg != null) {
            buff.append(": ").append(msg);
        }
        
        if (this._location != null) {
            buff.append("{").append(this._location).append("}");
        }
        
        return buff.toString();
    }

    /**
     * Returns the error code for this Exception, or -1 if no error code exists.
     *
     * @return the error code for this Exception, or -1 if no error code exists
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the error code for this Exception.
     *
     * @param errorCode the error code
     */
    public void setErrorCode(final int errorCode) {
        this.errorCode = errorCode;
    }

}
