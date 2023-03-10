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
 * Copyright 1999 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id: ClassInfoResolverImpl.java 7943 2008-10-02 16:51:02Z wguttmn $
 */
package org.exolab.castor.builder.util;

import java.util.Enumeration;
import java.util.Hashtable;

import org.exolab.castor.builder.ClassInfoResolver;
import org.exolab.castor.builder.info.ClassInfo;

/**
 * A class for "caching" ClassInfo's which later need to be resolved
 * (retrieved).
 *
 * @author <a href="mailto:kvisco@intalio.com">Keith Visco</a>
 * @version $Revision: 7943 $ $Date: 2003-03-03 00:05:44 -0700 (Mon, 03 Mar 2003) $
 */
public class ClassInfoResolverImpl implements ClassInfoResolver {
    /** ClassInfo cache. */
    private Hashtable<Object, ClassInfo> _cache = null;

    /**
     * Default constructor.
     */
    public ClassInfoResolverImpl() {
        _cache = new Hashtable<Object, ClassInfo>();
    }

    /**
     * Adds the given Reference to this ClassInfo resolver.
     *
     * @param key the key to bind a reference to.
     * @param classInfo the ClassInfo which is being referenced.
     */
    public final void bindReference(final Object key, final ClassInfo classInfo) {
        if (key == null) {
            String err = "null passed as argument to ClassInfoResolver#bindReference";
            throw new NullPointerException(err);
        }
        _cache.put(key, classInfo);
    }

    /**
     * Returns all the keys used for binding ClassInfo objects.
     *
     * @return an Enumeration over the keys using for binding ClassInfo objects.
     */
    public final Enumeration<?> keys() {
        return _cache.keys();
    }

    /**
     * Returns the ClassInfo which has been bound to the given key.
     *
     * @param key the object to which the ClassInfo has been bound.
     * @return the ClassInfo which has been bound to the given key.
     */
    public final ClassInfo resolve(final Object key) {
        return _cache.get(key);
    }

}
