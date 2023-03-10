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
 * THIS SOFTWARE IS PROVIDED BY THE CASTOR PROJECT AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * THE CASTOR PROJECT OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 2005 (C) Keith Visco. All Rights Reserved.
 *
 * Created on Feb 26, 2005
 *
 * $Id: ResolverException.java 8870 2011-04-22 12:25:47Z wguttmn $
 */
package org.exolab.castor.xml;

/**
 * The exception class thrown by the ClassDescriptorResolver
 *
 * @author <a href="mailto:keith (at) kvisco (dot) com">Keith Visco</a>
 * @version $Revision: 8870 $ $Date: $
 */
public class ResolverException extends XMLException {
    /** SerialVersionUID. */
    private static final long serialVersionUID = -8800218775708296399L;

    /**
     * Creates a new {@link ResolverException} with no message or nested exception.
     */
    public ResolverException() {
        super();
    }

    /**
     * Creates a new {@link ResolverException} with the given message.
     *
     * @param message the message for this exception.
     */
    public ResolverException(final String message) {
        super(message);
    }

    /**
     * Creates a new {@link ResolverException} with the given nested exception.
     *
     * @param exception the nested exception
     */
    public ResolverException(final Throwable exception) {
        super(exception);
    }

    /**
     * Creates a new {@link ResolverException} with the given message and error code.
     *
     * @param message the message for this exception.
     * @param errorCode the error code for this exception.
     * 
     * @deprecated
     */
    public ResolverException(final String message, final int errorCode) {
        super(message, errorCode);
    }

    /**
     * Creates a new {@link ResolverException} with the given message and nested
     * exception.
     *
     * @param message the message for this exception.
     * @param exception the nested exception.
     */
    public ResolverException(final String message, final Throwable exception) {
        super(message, exception);
    }

    /**
     * Creates a new {@link ResolverException} with the given message, nested exception,
     * and error code.
     *
     * @param message the message for this exception.
     * @param exception the nested exception.
     * @param errorCode the error code for this exception.
     * 
     * @deprecated
     */
    public ResolverException(final String message, final Throwable exception, final int errorCode) {
        super(message, exception, errorCode);
    }

}
