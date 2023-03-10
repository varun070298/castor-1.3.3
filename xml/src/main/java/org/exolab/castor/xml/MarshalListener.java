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
 * $Id: MarshalListener.java 8653 2010-07-12 10:40:06Z wguttmn $
 */

package org.exolab.castor.xml;


/**
 * An interface to allow external "listening" to objects when
 * they are being marshalled for various tracking purposes and
 * potential modification, and to prevent an object from
 * being marshalled if necessary. An implementation of
 * this interface may be registered with the Marshaller.
 *
 * @author <a href="mailto:kvisco@intalio.com">Keith Visco</a>
 * @author <a href="mailto:Jeff.Norris@jpl.nasa.gov">Jeff Norris</a>
 * @version $Revision: 8653 $ $Date: 2003-03-03 00:05:44 -0700 (Mon, 03 Mar 2003) $
**/
public interface MarshalListener {
    
    /**
     * This method is called before an object
     * is to be marshalled.
     * 
     * In case of unchecked exceptions being thrown, those will be caught and 
     * logged, but the marshalling process will not be interrupted.
     *
     * @param object the Object about to be marshalled.
     * @return false if the object should not be
     * marshalled.
    **/
    public boolean preMarshal(Object object);

    /**
     * This method is called after an object
     * has been marshalled. If #preMarshal returned
     * false for a given Object, this method will
     * not be called for that Object as marshalling
     * will not take place.
     *
     * In case of unchecked exceptions being thrown, those will be caught and 
     * logged, but the marshalling process will not be interrupted.
     *
     * @param object the Object that was marshalled.
    **/
    public void postMarshal(Object object);
     
} //-- MarshalListener