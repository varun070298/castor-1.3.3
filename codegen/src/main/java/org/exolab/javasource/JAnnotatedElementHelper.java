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
 * Copyright 2001-2002 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id: JAnnotatedElementHelper
 */
package org.exolab.javasource;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Implements JAnnotatedElement interface on behalf of other classes in this
 * package that implement this interface.
 *
 * @author <a href="mailto:andrew DOT fawcett AT coda DOTcom">Andrew Fawcett</a>
 * @version $Revision: 8009 $ $Date: 2006-04-25 16:09:10 -0600 (Tue, 25 Apr 2006) $
 */
public class JAnnotatedElementHelper implements JAnnotatedElement {

    /** 
     * Stores annotations associated with the source element containing this helper. 
     */
    private LinkedHashMap<String, JAnnotation> _annotations = 
        new LinkedHashMap<String, JAnnotation>();

    /**
     * Creates a JAnnodatedElementHelper.
     */
    public JAnnotatedElementHelper() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public final JAnnotation getAnnotation(final JAnnotationType annotationType) {
        if (_annotations == null) { return null; }
        return _annotations.get(annotationType.getName());
    }

    /**
     * {@inheritDoc}
     */
    public final JAnnotation[] getAnnotations() {
        return _annotations.values().toArray(new JAnnotation[_annotations.size()]);
    }

    /**
     * {@inheritDoc}
     */
    public final boolean isAnnotationPresent(final JAnnotationType annotationType) {
        return _annotations.containsKey(annotationType.getName());
    }

    /**
     * {@inheritDoc}
     */
    public final void addAnnotation(final JAnnotation annotation) {
        if (isAnnotationPresent(annotation.getAnnotationType())) {
            throw new IllegalArgumentException(
                    "Annotation for '" + annotation.getAnnotationType().getName()
                    + "' already added.");
        }
        String annotationType = annotation.getAnnotationType().getName();
        _annotations.put(annotationType, annotation);
    }

    /**
     * {@inheritDoc}
     */
    public final JAnnotation removeAnnotation(final JAnnotationType annotationType) {
        if (!isAnnotationPresent(annotationType)) {
            throw new IllegalArgumentException(
                    "Annotation for '" + annotationType.getName() + "' not present.");
        }
        return _annotations.remove(annotationType.getName());
    }

    /**
     * {@inheritDoc}
     */
    public final boolean hasAnnotations() {
        return _annotations.size() > 0;
    }

    /**
     * Outputs the list of annotations maintained by this object.
     *
     * @param jsw the JSourceWriter to print the annotations to
     * @return true if at least one annotation was printed, false otherwise.
     */
    public final boolean printAnnotations(final JSourceWriter jsw) {
        boolean printed = false;
        Iterator<JAnnotation> annotations = _annotations.values().iterator();
        while (annotations.hasNext()) {
            JAnnotation annotation = annotations.next();
            annotation.print(jsw);
            jsw.writeln();
            printed = true;
        }
        return printed;
    }

}
