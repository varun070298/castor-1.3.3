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
 * Copyright 2000 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id: CollectionInfoODMG30.java 7700 2008-07-08 20:09:29Z wguttmn $
 */
package org.exolab.castor.builder.info;

import org.exolab.castor.builder.SourceGeneratorConstants;
import org.exolab.castor.builder.factory.FieldMemberAndAccessorFactory;
import org.exolab.castor.builder.info.nature.XMLInfoNature;
import org.exolab.castor.builder.types.XSCollectionFactory;
import org.exolab.castor.builder.types.XSListType;
import org.exolab.castor.builder.types.XSType;

/**
 * A helper used for generating source that deals with Collections.
 * @author <a href="mailto:frank.thelen@poet.de">Frank Thelen</a>
 * @author <a href="mailto:bernd.deichmann@poet.de">Bernd Deichmann</a>
 * @version $Revision: 7700 $ $Date: 2006-02-23 01:08:24 -0700 (Thu, 23 Feb 2006) $
 */
public final class CollectionInfoODMG30 extends CollectionInfo {

    /**
     * Creates a new CollectionInfoODMG30.
     *
     * @param contentType the content type of the collection, ie. the type of
     *        objects that the collection will contain
     * @param name the name of the Collection
     * @param elementName the element name for each element in collection
     * @param useJava50 true if code is supposed to be generated for Java 5
     * @param memberAndAccessorFactory the FieldMemberAndAccessorFactory to be used  
     * @param contentMemberAndAccessorFactory the factory for the content 
     */
    public CollectionInfoODMG30(final XSType contentType, final String name,
                                final String elementName, final boolean useJava50,
                                final FieldMemberAndAccessorFactory memberAndAccessorFactory,
                                final FieldMemberAndAccessorFactory contentMemberAndAccessorFactory) {
        super(contentType, name, elementName, useJava50, 
                memberAndAccessorFactory, contentMemberAndAccessorFactory);
        final XSListType collection = 
            XSCollectionFactory.createCollection(SourceGeneratorConstants.FIELD_INFO_ODMG,
                        contentType, useJava50);
        if (hasNature(XMLInfoNature.class.getName())) {
            XMLInfoNature xmlNature = new XMLInfoNature(this);
            xmlNature.setSchemaType(collection);
        }
    }

}
