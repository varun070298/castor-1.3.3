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
 * $Id: FieldInfoFactory.java 8145 2009-03-29 22:07:00Z rjoachim $
 */
package org.exolab.castor.builder.factory;

import java.security.InvalidParameterException;

import org.castor.xml.JavaNaming;
import org.castor.xml.JavaNamingImpl;
import org.exolab.castor.builder.SourceGeneratorConstants;
import org.exolab.castor.builder.info.CollectionInfo;
import org.exolab.castor.builder.info.CollectionInfoJ2;
import org.exolab.castor.builder.info.CollectionInfoJ2Collection;
import org.exolab.castor.builder.info.CollectionInfoJ2Set;
import org.exolab.castor.builder.info.CollectionInfoJ2SortedSet;
import org.exolab.castor.builder.info.CollectionInfoODMG30;
import org.exolab.castor.builder.info.FieldInfo;
import org.exolab.castor.builder.info.IdentityInfo;
import org.exolab.castor.builder.types.XSType;

/**
 * This class is used as a factory to create all the FieldInfo objects used by
 * the source generator. You may override the FieldInfo classes and this factory
 * for specific adaptions.
 *
 * @author <a href="mailto:frank.thelen@poet.de">Frank Thelen</a>
 * @author <a href="mailto:blandin@intalio.com">Arnaud Blandin</a>
 * @version $Revision: 8145 $ $Date: 2005-03-05 06:42:06 -0700 (Sat, 05 Mar 2005) $
 */
public class FieldInfoFactory {
    /** The default collection name. */
    private String _default = null;
    /** A flag indicating that "extra" accessor methods should be created for
     * returning and setting a reference to the underlying collection. */
    private boolean _extraMethods = false;
    /** The reference suffix to use. */
    private String _referenceSuffix = null;
    /** If true, code for bound properties will be generated. */
    private boolean _bound = false;
    
    /* The FieldMemberAndAccessorFactories */
    private FieldMemberAndAccessorFactory _fieldMemberAndAccessorFactory;
    private CollectionMemberAndAccessorFactory _collectionMemberAndAccessorFactory;
    private CollectionJ2MemberAndAccessorFactory _collectionJ2MemberAndAccessorFactory;
    private CollectionJ2NoIndexMemberAndAccessorFactory _collectionJ2NoIndexMemberAndAccessorFactory;
    private CollectionODMG30MemberAndAccessorFactory _collectionODMG30MemberAndAccessorFactory;
    private IdentityMemberAndAccessorFactory _identityMemberAndAccessorFactory;
    
    /**
     * The {@link JavaNaming} to use.
     * @since 1.1.3
     */
    private JavaNaming _javaNaming;

    /**
     * Creates a new FieldInfoFactory. The default collection used will be
     * Java 1 type.
     */
    public FieldInfoFactory () {
        this("vector");
    }

    /**
     * Creates a new FieldInfoFactory of the given type.
     * @param collectionName The type for the FieldInfoFactory.
     */
    public FieldInfoFactory(final String collectionName) {
        super();
        if (!(collectionName.equals(SourceGeneratorConstants.FIELD_INFO_VECTOR)
                || collectionName.equals(SourceGeneratorConstants.FIELD_INFO_ARRAY_LIST)
                || collectionName.equals(SourceGeneratorConstants.FIELD_INFO_ODMG))) {
            throw new IllegalArgumentException(collectionName
                    + " is currently not a supported Java collection.");
        }
        _default = collectionName;

        _javaNaming = new JavaNamingImpl();
        
        this._fieldMemberAndAccessorFactory = new FieldMemberAndAccessorFactory(
                _javaNaming);
        this._collectionMemberAndAccessorFactory = new CollectionMemberAndAccessorFactory(
                _javaNaming);
        this._collectionJ2MemberAndAccessorFactory = new CollectionJ2MemberAndAccessorFactory(
                _javaNaming);
        this._collectionJ2NoIndexMemberAndAccessorFactory = new CollectionJ2NoIndexMemberAndAccessorFactory(
                _javaNaming);
        this._collectionODMG30MemberAndAccessorFactory = new CollectionODMG30MemberAndAccessorFactory(
                _javaNaming);
        this._identityMemberAndAccessorFactory = new IdentityMemberAndAccessorFactory(
                _javaNaming);
        
    }

    /**
     * Creates an {@link IdentityInfo} instance for the given name. 
     * @param name Identity field name.
     * @return The {@link IdentityInfo} instance just created.
     */
    public IdentityInfo createIdentity (final String name) {
        IdentityInfo idInfo = new IdentityInfo(name, this._identityMemberAndAccessorFactory);
        if (_bound) { idInfo.setBound(_bound); }
        return idInfo;
    }


    /**
     * Creates a {@link CollectionInfo} instance based upon the various parameters provided.
     * @param contentType Content type of the collection.
     * @param name Name of the collection member.
     * @param elementName Name of the (content) element.
     * @param javaNaming the Java naming to be used
     * @param usejava50 Whether we are targeting Java 5.0 or above or not
     * @return A {@link CollectionInfo} instance representing a collection typed member.
     * @see #createCollection(XSType, String, String, String, boolean)
     */
    public CollectionInfo createCollection(final XSType contentType, final String name,
                                           final String elementName, final JavaNaming javaNaming, 
                                           final boolean usejava50) {
        return createCollection(contentType, name, elementName, _default, javaNaming, usejava50);
    }

    /**
     * Creates a {@link CollectionInfo} instance based upon the various parameters provided.
     * @param contentType Content type of the collection.
     * @param name Name of the collection member.
     * @param elementName Name of the (content) element.
     * @param collectionName Name of the collection.
     * @param javaNaming the Java naming to be used
     * @param useJava50 Whether we are targeting Java 5.0 or above or not
     * @return A {@link CollectionInfo} instance representing a collection typed member.
     */
    public CollectionInfo createCollection(final XSType contentType, final String name,
                                           final String elementName, final String collectionName,
                                           final JavaNaming javaNaming, final boolean useJava50) {
        String temp = collectionName;
        if (temp == null || temp.length() == 0) { temp = _default; }

        final CollectionInfo cInfo;
        if (temp.equalsIgnoreCase(SourceGeneratorConstants.FIELD_INFO_VECTOR)) {
             cInfo = new CollectionInfo(contentType, name, elementName, useJava50, 
                     this._collectionMemberAndAccessorFactory, this._fieldMemberAndAccessorFactory);
        } else if (temp.equalsIgnoreCase(SourceGeneratorConstants.FIELD_INFO_ARRAY_LIST)) {
             cInfo = new CollectionInfoJ2(contentType, name, elementName, "arraylist", useJava50, 
                     this._collectionJ2MemberAndAccessorFactory, this._fieldMemberAndAccessorFactory);
        } else if (temp.equalsIgnoreCase(SourceGeneratorConstants.FIELD_INFO_ODMG)) {
             cInfo = new CollectionInfoODMG30(contentType, name, elementName, useJava50, 
                     this._collectionODMG30MemberAndAccessorFactory, this._fieldMemberAndAccessorFactory);
        } else if (temp.equalsIgnoreCase(SourceGeneratorConstants.FIELD_INFO_COLLECTION)) {
            cInfo = new CollectionInfoJ2Collection(contentType, name, elementName, useJava50, 
                    this._collectionJ2NoIndexMemberAndAccessorFactory, this._fieldMemberAndAccessorFactory);
        } else if (temp.equalsIgnoreCase(SourceGeneratorConstants.FIELD_INFO_SET)) {
            cInfo = new CollectionInfoJ2Set(contentType, name, elementName, useJava50, 
                    this._collectionJ2NoIndexMemberAndAccessorFactory, this._fieldMemberAndAccessorFactory);
        } else if (temp.equalsIgnoreCase(SourceGeneratorConstants.FIELD_INFO_SORTED_SET)) {
            cInfo = new CollectionInfoJ2SortedSet(contentType, name, elementName, useJava50, 
                    this._collectionJ2NoIndexMemberAndAccessorFactory, this._fieldMemberAndAccessorFactory);
        } else {
            throw new InvalidParameterException("Unrecognized collection type: " + temp);
        }

        //--not sure it is pluggable enough, it is not really beautiful to specify
        //--the collection to use here
        cInfo.setCreateExtraMethods(_extraMethods);
        if (_referenceSuffix != null) {
            cInfo.setReferenceMethodSuffix(_referenceSuffix);
        }
        if (_bound) { cInfo.setBound(true); }
        return cInfo;
    }

    /**
     * Creates a {@link FieldInfo} instance for the given {@link XSType} and
     * its name. 
     * @param type {@link XSType} of the field.
     * @param name Field name.
     * @return The {@link FieldInfo} instance just created.
     */
    public FieldInfo createFieldInfo(final XSType type, final String name) {
        FieldInfo fieldInfo = new FieldInfo(type, name, this._fieldMemberAndAccessorFactory);
        if (_bound) { fieldInfo.setBound(true); }
        return fieldInfo;
    }

    /**
     * Sets whether or not the fields should be bound properties.
     *
     * @param bound a boolean that when true indicates the FieldInfo should have
     *        the bound property enabled.
     */
    public final void setBoundProperties(final boolean bound) {
        _bound = bound;
    }

    /**
     * Sets whether or not to create extra collection methods for accessing the
     * actual collection.
     *
     * @param extraMethods a boolean that when true indicates that extra
     *        collection accessor methods should be created. False by default.
     * @see org.exolab.castor.builder.FieldInfoFactory#setReferenceMethodSuffix
     */
    public final void setCreateExtraMethods(final boolean extraMethods) {
        _extraMethods = extraMethods;
    }

    /**
     * Sets the method suffix (ending) to use when creating the extra collection
     * methods.
     *
     * @param suffix the method suffix to use when creating the extra collection
     *        methods. If null or emtpty the default value, as specified in
     *        CollectionInfo will be used.
     * @see org.exolab.castor.builder.FieldInfoFactory#setCreateExtraMethods
     */
    public final void setReferenceMethodSuffix(final String suffix) {
        _referenceSuffix = suffix;
    }
}
