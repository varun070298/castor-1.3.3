/*
 * Copyright 2005 Werner Guttmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Id: ManyToManyRelationResolver.java 8994 2011-08-01 23:40:59Z rjoachim $
 */
package org.castor.persist.resolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.castor.persist.ProposedEntity;
import org.castor.persist.TransactionContext;
import org.castor.persist.UpdateFlags;
import org.castor.persist.proxy.LazyCollection;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.persist.ClassMolder;
import org.exolab.castor.persist.ClassMolderHelper;
import org.exolab.castor.persist.FieldMolder;
import org.exolab.castor.persist.OID;
import org.exolab.castor.persist.spi.Identity;

/**
 * Implementation of {@link org.castor.persist.resolver.ResolverStrategy} for M:N relations.
 * 
 * @author <a href="mailto:werner DOT guttmann AT gmx DOT net">Werner Guttmann</a>
 * @since 0.9.9
 */
public final class ManyToManyRelationResolver extends ManyRelationResolver {
    
    /**
     * Creates an instance of ManyToManyRelationResolver.
     * 
     * @param classMolder Associated ClassMolder.
     * @param fieldMolder Associated FieldMolder.
     * @param fieldIndex Field index within all fields of parent class molder.
     */
    public ManyToManyRelationResolver(final ClassMolder classMolder,
            final FieldMolder fieldMolder, final int fieldIndex) {
        super(classMolder, fieldMolder, fieldIndex);
    }
    
    /**
     * @see org.castor.persist.resolver.ResolverStrategy
     *      #markCreate(org.castor.persist.TransactionContext,
     *      org.exolab.castor.persist.OID, java.lang.Object)
     */
    public boolean markCreate(final TransactionContext tx, final OID oid, final Object object)
    throws PersistenceException {
        boolean updateCache = false;
        // create relation if the relation table
        ClassMolder fieldClassMolder = _fieldMolder.getFieldClassMolder();
        Object objects = _fieldMolder.getValue(object, tx.getClassLoader());
        if (objects != null) {
            Iterator itor = ClassMolderHelper.getIterator(objects);
            // many-to-many relation is never dependent relation
            while (itor.hasNext()) {
                Object objectToCreate = itor.next();
                // TODO[ASE]: investigate whether this requires 'cascading update' as well.
                if (isCascadingCreate(tx) && !tx.isRecorded(objectToCreate)) {
                    tx.markCreate(fieldClassMolder, objectToCreate, null);
                    updateCache = true;
                }
            }
        }
        return updateCache;
    }
    
    
    /**
     * @see org.castor.persist.resolver.ResolverStrategy
     *      #markDelete(org.castor.persist.TransactionContext, java.lang.Object,
     *      java.lang.Object)
     */
    public void markDelete(final TransactionContext tx, final Object object,
            final Object field)
    throws PersistenceException {
        // delete the relation in relation table too
        /*
         * _fhs[i].getRelationLoader().deleteRelation(
         * tx.getConnection(oid.getLockEngine()), oid.getIdentity() );
         */

        ClassMolder fieldClassMolder = _fieldMolder.getFieldClassMolder();
        // markDelete mix with prestore
        // so, store is not yet called, and only the loaded (or created)
        // relation have to be deleted.
        // not really. cus, the other created relation, may already
        // has reference to this object. so, how to deal with that?
        if (field != null) {
            ArrayList alist = (ArrayList) field;
            for (int j = 0; j < alist.size(); j++) {
                Identity fid = (Identity) alist.get(j);
                Object fetched = null;
                if (fid != null) {
                    fetched = tx.fetch(fieldClassMolder, fid, null);
                    if (fetched != null) {
                        fieldClassMolder.removeRelation(tx, fetched,
                                _classMolder, object);
                    }
                }
            }
        }

        Iterator itor = ClassMolderHelper.getIterator(_fieldMolder.getValue(
                object, tx.getClassLoader()));
        while (itor.hasNext()) {
            Object fobject = itor.next();
            if (fobject != null && tx.isPersistent(fobject)) {
                fieldClassMolder.removeRelation(tx, fobject, _classMolder,
                        object);
            }
        }
    }
    
    
    /**
     * @see org.castor.persist.resolver.ResolverStrategy
     *      #preStore(org.castor.persist.TransactionContext,
     *      org.exolab.castor.persist.OID, java.lang.Object, int,
     *      java.lang.Object)
     */
    public UpdateFlags preStore(final TransactionContext tx, final OID oid,
            final Object object, final int timeout, final Object field) 
    throws PersistenceException {
        ClassMolder fieldClassMolder = _fieldMolder.getFieldClassMolder();
        Object value = _fieldMolder.getValue(object, tx.getClassLoader());
        
        Iterator<Identity> removedItor;
        Iterator addedItor;
        if (!(value instanceof LazyCollection)) {
            List orgFields = (List) field;

            Collection<Identity> removed = ClassMolderHelper.getRemovedIdsList(tx,
                    orgFields, value, fieldClassMolder);
            removedItor = removed.iterator();

            Collection added = ClassMolderHelper.getAddedEntitiesList(tx,
                    orgFields, value, fieldClassMolder);
            addedItor = added.iterator();
        } else {
        	LazyCollection lazy = (LazyCollection) value;

            // RelationCollection has to clean up its state at the end of the transaction
            tx.addTxSynchronizable(lazy);

            removedItor = lazy.getRemovedIdsList().iterator();
            addedItor = lazy.getAddedEntitiesList().iterator();
        }
        
        UpdateFlags flags = new UpdateFlags();

        if (removedItor.hasNext()) {
            if (_fieldMolder.isStored() && _fieldMolder.isCheckDirty()) {
                flags.setUpdatePersist(true);
            }
            flags.setUpdateCache(true);

            while (removedItor.hasNext()) {
                Identity removedId = removedItor.next();
                
                // must be loaded in transaction, so that the related object
                // is properly locked and updated before we delete it.
                if (!tx.isDeletedByOID(fieldClassMolder.getLockEngine(),
                        new OID(fieldClassMolder, removedId))) {
                    ProposedEntity proposedValue = new ProposedEntity(fieldClassMolder);
                    Object removedEntity = tx.load(removedId, proposedValue, null);

                    if (removedEntity != null && tx.isPersistent(removedEntity)) {
                        tx.writeLock(removedEntity, 0);

                        _fieldMolder.getRelationLoader().deleteRelation(
                                tx.getConnection(fieldClassMolder.getLockEngine()),
                                oid.getIdentity(), removedId);

                        fieldClassMolder.removeRelation(tx, removedEntity, _classMolder, object);
                    }
                }
            }
        }

        if (addedItor.hasNext()) {
            if (_fieldMolder.isStored() && _fieldMolder.isCheckDirty()) {
                flags.setUpdatePersist(true);
            }
            flags.setUpdateCache(true);

            while (addedItor.hasNext()) {
                Object addedEntity = addedItor.next();
                tx.markModified(addedEntity, false, true);

                if (tx.isPersistent(addedEntity)) {
                    _fieldMolder.getRelationLoader().createRelation(
                            tx.getConnection(fieldClassMolder.getLockEngine()),
                            oid.getIdentity(),
                            fieldClassMolder.getIdentity(tx, addedEntity));
                } else {
                    if (isCascadingCreate(tx)) {
                        if (!tx.isRecorded(addedEntity)) {
                            tx.markCreate(fieldClassMolder, addedEntity, null);
                        }
                    }
                }
            }
        }

        return flags;
    }
    
    /**
     * @see org.castor.persist.resolver.ResolverStrategy
     *      #update(org.castor.persist.TransactionContext,
     *      org.exolab.castor.persist.OID, java.lang.Object,
     *      org.exolab.castor.mapping.AccessMode, java.lang.Object)
     */
    public void update(final TransactionContext tx, final OID oid,
            final Object object, final AccessMode suggestedAccessMode,
            final Object field)
    throws PersistenceException {
        List values = (List) field;
        ClassMolder fieldClassMolder = _fieldMolder.getFieldClassMolder();
        if (isCascadingUpdate(tx)) {
            List<Identity> newSetOfIds = new ArrayList<Identity>();

            // iterate the collection of this data object field
            Iterator itor = ClassMolderHelper.getIterator(_fieldMolder
                    .getValue(object, tx.getClassLoader()));
            while (itor.hasNext()) {
                Object element = itor.next();
                Identity actualIdentity = fieldClassMolder.getActualIdentity(tx, element);
                newSetOfIds.add(actualIdentity);
                if (!tx.isRecorded(element)) {
                    tx.markUpdate(fieldClassMolder, element, null);
                }
            }
            // load all old objects for comparison in the preStore state
            if (values != null) {
                for (int j = 0; j < values.size(); j++) {
                    if (!newSetOfIds.contains(values.get(j))) {
                        // load all the dependent object in cache for
                        // modification check at commit time.
                        ProposedEntity proposedValue = new ProposedEntity(fieldClassMolder);
                        tx.load((Identity) values.get(j), proposedValue, suggestedAccessMode);
                    }
                }
            }
        }
    }
    
    /**
     * @see org.castor.persist.resolver.ManyRelationResolver#postCreate(
     *      org.castor.persist.TransactionContext,
     *      org.exolab.castor.persist.OID, java.lang.Object, java.lang.Object,
     *      org.exolab.castor.persist.spi.Identity)
     */
    public Object postCreate(final TransactionContext tx, final OID oid,
            final Object object, final Object field, final Identity createdId) 
        throws PersistenceException {
        ClassMolder fieldClassMolder = _fieldMolder.getFieldClassMolder();
        Object o = _fieldMolder.getValue(object, tx.getClassLoader());
        Object result = field;
        if (o != null) {
            result = ClassMolderHelper.getIdsList(tx, fieldClassMolder, o);
            Iterator itor = ClassMolderHelper.getIterator(o);
            while (itor.hasNext()) {
                Object oo = itor.next();
                //TODO[ASE]: another option would be to change this if clause!
                //	   tx.isPersistent only checks with the tracker but not with the
                //	   persistent storage! If this check would work properly
                //	   most of the problems would be saved too
                //	   but we might also have to check that we are not going to store
                //	   the same relation twice what is possible if both objects are already
                //	   created due to another relation they occur in!
                if (tx.isPersistent(oo)) {
                    _fieldMolder.getRelationLoader().createRelation(
                            tx.getConnection(fieldClassMolder.getLockEngine()), createdId,
                            fieldClassMolder.getIdentity(tx, oo));
                }
            }
        }
        return result;
    }

    /**
     * @inheritDoc
     */
    public boolean updateWhenNoTimestampSet(
            final TransactionContext tx, 
            final OID oid, 
            final Object object, 
            final AccessMode suggestedAccessMode) 
    throws PersistenceException {
        boolean updateCache = false;
        // create relation if the relation table
        ClassMolder fieldClassMolder = _fieldMolder.getFieldClassMolder();
        Object value = _fieldMolder.getValue(object, tx.getClassLoader());
        if (value != null) {
            Iterator itor = ClassMolderHelper.getIterator(value);
            // many-to-many relation is never dependent relation
            while (itor.hasNext()) {
                Object oo = itor.next();
                if (isCascadingUpdate(tx) && !tx.isRecorded(oo)) {
                    if (tx.markUpdate(fieldClassMolder, oo, null)) {
                        updateCache = true;
                    }
                }
            }
        }
        return updateCache;
    }

}
