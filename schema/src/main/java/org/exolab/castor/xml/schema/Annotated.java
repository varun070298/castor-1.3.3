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
 * $Id: Annotated.java 7996 2008-12-16 08:25:44Z wguttmn $
 */

package org.exolab.castor.xml.schema;

import java.util.Enumeration;
import java.util.Vector;

/**
 * A base class used for XML Structures that support annotations.
 *
 * @author <a href="mailto:kvisco@intalio.com">Keith Visco</a>
 * @version $Revision: 7996 $ $Date: 2006-04-14 04:14:43 -0600 (Fri, 14 Apr 2006) $ 
**/
public abstract class Annotated extends Structure {
    
    /**
     * The Annotations of this Annotated structure.
    **/
    private Vector<Annotation> _annotations = new Vector<Annotation>(1);
    
   /**
     * Adds the given Annotation to this Annotated Structure.
     * @param annotation the Annotation to add
    **/
    public void addAnnotation(final Annotation annotation) {
        _annotations.addElement(annotation);
    }
    
    /**
     * Returns an Enumeration of the Annotations contained within
     * this Annotated type.
     * @return an Enumeration of the Annotation contained within 
     * this Annotated type
    **/
    public Enumeration<Annotation> getAnnotations() {
        return _annotations.elements();
    }
    
    /**
     * Removes the given Annotation from this Annotated Structure.
     * @param annotation the Annotation to remove
    **/
    public void removeAnnotation(final Annotation annotation) {
        _annotations.removeElement(annotation);
    }
    
}
