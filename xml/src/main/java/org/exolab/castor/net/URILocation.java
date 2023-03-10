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
 * $Id: URILocation.java 5951 2006-05-30 22:18:48Z bsnyder $
 */
package org.exolab.castor.net;


import java.io.*;

/**
 * An interface for handling URIs
 * @author <a href="mailto:kvisco@intalio.com">Keith Visco</a>
 * @version $Revision: 5951 $ $Date: 2005-03-05 06:42:06 -0700 (Sat, 05 Mar 2005) $
**/
public abstract class URILocation {


	/**
	 * Returns the absolute URI for this URILocation
	 *
	 * @return the absolute URI for this URILocation
	 * @see #getRelativeURI
	 * @see #getBaseURI
	**/
	public abstract String getAbsoluteURI();

	/**
	 * Returns the base location of this URILocation.
	 * If this URILocation is an URL, the base location
	 * will be equivalent to the document base for the URL.
	 *
	 * @return the base location of this URILocation
	 * @see #getAbsoluteURI
	 * @see #getRelativeURI
	**/
	public abstract String getBaseURI();

	/**
	 * Returns a Reader for the resource represented
	 * by this URILocation.
	 *
	 * @return a Reader for the resource represented by
	 * this URILocation
	 * @exception java.io.IOException
	**/
	public abstract Reader getReader()
	    throws java.io.IOException;

	/**
	 * Returns the relative URI for this URILocation
	 *
	 * @return the relative URI for this URILocation
	 * @see #getAbsoluteURI
	 * @see #getBaseURI
	**/
	public abstract String getRelativeURI();

	/**
	 * Returns the String representation of
	 * this URILocation.
	 *
	 * @return the String representation of this URILocation
	**/
	public String toString() {
	    return getAbsoluteURI();
	}

} //-- URILocation
