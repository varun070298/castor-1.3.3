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
 * Copyright 2001 (C) Intalio, Inc. All Rights Reserved.
 *
 * $Id: GroupNaming.java 7943 2008-10-02 16:51:02Z wguttmn $
 */
package org.exolab.castor.builder;

import java.util.Hashtable;

import org.castor.xml.JavaNaming;
import org.exolab.castor.xml.schema.ComplexType;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.Group;
import org.exolab.castor.xml.schema.ModelGroup;
import org.exolab.castor.xml.schema.Structure;

/***
 * A simple class used for creating class names for unnamed Groups in XML Schema.
 *
 * @author <a href="mailto:kvisco@intalio.com">Keith Visco</a>
 * @version $Revision: 7943 $ $Date: 2006-04-25 15:08:23 -0600 (Tue, 25 Apr 2006) $
 */
public final class GroupNaming {

    /**
     * A HashTable of HashTables that keeps track of group names for each
     * package.
     */
    private Hashtable<String, Hashtable<Group, String>> _packageGroupNames = null;

    /** 
     * JavaNaming to be used. 
     * @since 1.1.3
     */
    private JavaNaming _javaNaming;


    /**
     * Creates a new GroupNaming.
     * @param javaNaming the {@link JavaNaming} to be used
     */
    public GroupNaming(final JavaNaming javaNaming) {
        _packageGroupNames = new Hashtable<String, Hashtable<Group, String>>();
        // JavaNaming was introduced @since 1.1.3
        _javaNaming = javaNaming;
    } //-- GroupNaming

    /**
     * Returns the qualified group name for a given Group instance and a package
     * name; null if there isn't one already.
     *
     * @param group A Group instance
     * @param packageName A package name
     * @return The fully qualified group name for the Group instance/package
     *         name combination
     */
    private String getGroupName(final Group group, final String packageName) {
        Hashtable<Group, String> groupNames = _packageGroupNames.get(packageName);
        if (groupNames == null) {
            return null;
        }
        return groupNames.get(group);
    }

    /**
     * Sets the qualified group name for a given Group instance and package
     * name.
     *
     * @param group A Group instance
     * @param packageName A package name
     * @param name The fully qualified group name for the Group instance/package
     *        name combination
     */
    private void putGroupName(final Group group, final String packageName, final String name) {
        Hashtable<Group, String> groupNames = _packageGroupNames.get(packageName);
        if (groupNames == null) {
            groupNames = new Hashtable<Group, String>();
            _packageGroupNames.put(packageName, groupNames);
        }
        groupNames.put(group, name);
    }

    /**
     * Checks whether for a given group and package name combination an entry
     * already exists.
     *
     * @param packageName A package name
     * @param name A group name.
     * @return True if a mapping already exists
     */
    private boolean containsGroupName(final String packageName, final String name) {
        Hashtable<Group, String> groupNames = _packageGroupNames.get(packageName);
        if (groupNames == null) {
            return false;
        }
        return groupNames.contains(name);
    }

    /**
     * Creates a class name for the given Group. A null value will be returned
     * if a name cannot be created for the group.
     *
     * @param group the group to create a class name for
     * @param packageName the package name for this group
     * @return the class name for the given Group.
     */
    public String createClassName(final Group group, final String packageName) {
        String name = group.getName();
        if (name != null) {
            return _javaNaming.toJavaClassName(name);
        }
        name = getGroupName(group, packageName);
        if (name != null) {
            return name;
        }

        Structure parent = group.getParent();
        if (parent == null) {
            return null;
        }

        boolean addOrder = true;
        switch(parent.getStructureType()) {
            case Structure.GROUP:
                name = createClassName((Group) parent, packageName);
                break;
            case Structure.MODELGROUP:
                name = ((ModelGroup) parent).getName();
                name = _javaNaming.toJavaClassName(name);
                addOrder = false;
                break;
            case Structure.COMPLEX_TYPE:
                name = getClassName((ComplexType) parent);
                addOrder = false;
                break;
            default:
                break;
        }

        if (name != null) {
            if (addOrder) {
                String order = group.getOrder().toString();
                name += _javaNaming.toJavaClassName(order);
            }

            int count = 2;
            String tmpName = name;
            while (containsGroupName(packageName, name)) {
                name = tmpName + count;
                ++count;
            }
            putGroupName(group, packageName, name);
        }
        return name;
    } //-- createClassName

    /**
     * Returns the class name for the given ComplexType.
     * <p>
     * If the ComplexType instance is named, simply return the name of the
     * ComplexType.
     * <p>
     * If it is not named (in other words, if it is an anonymous ComplexType
     * definition), check for the name of the containing element (definition).
     *
     * @param complexType the ComplexType for which to return a class name
     * @return the class name for the given ComplexType
     */
    private String getClassName(final ComplexType complexType) {
        //-- if complexType has name, simply return it
        String name = complexType.getName();
        if (name != null) {
            return _javaNaming.toJavaClassName(name);
        }

        //-- otherwise get name of containing element
        Structure parent = complexType.getParent();
        if (parent != null && parent.getStructureType() == Structure.ELEMENT) {
            name = ((ElementDecl) parent).getName();
        }

        if (name != null) {
            name = _javaNaming.toJavaClassName(name);
        }
        return name;
    } //-- getName

} //-- class GroupNaming
