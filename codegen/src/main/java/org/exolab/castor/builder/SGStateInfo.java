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
 * Copyright 1999-2003 (C) Intalio, Inc. All Rights Reserved.
 *
 * This file was originally developed by Keith Visco during the
 * course of employment at Intalio Inc.
 * All portions of this file developed by Keith Visco after Jan 19 2005 are
 * Copyright (C) 2005 Keith Visco. All Rights Reserved.
 *
 * $Id: SGStateInfo.java 7996 2008-12-16 08:25:44Z wguttmn $
 */
package org.exolab.castor.builder;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.exolab.castor.builder.util.ClassInfoResolverImpl;
import org.exolab.castor.mapping.xml.MappingRoot;
import org.exolab.castor.util.dialog.ConsoleDialog;
import org.exolab.castor.util.dialog.Dialog;
import org.exolab.castor.xml.schema.Annotated;
import org.exolab.castor.xml.schema.Schema;
import org.exolab.javasource.JClass;

/**
 * A class for maintaining state for the SourceGenerator.
 *
 * @author <a href="mailto:keith AT kvisco DOT com">Keith Visco</a>
 * @version $Revision: 7996 $ $Date: 2005-06-22 22:13:21 -0600 (Wed, 22 Jun 2005) $
 */
public final class SGStateInfo extends ClassInfoResolverImpl {

    /** An empty Enumeration to be returned whenever we need an empty Enumeration. */
    private static final Enumeration<String> EMPTY_ENUMERATION = new Vector<String>(0).elements();
    /** The SourceGenerator is still generating source. */
    public static final int NORMAL_STATUS = 0;
    /** The SourceGenerator has been stopped by an error or by the user. */
    public static final int STOP_STATUS   = 1;

    /** The in memory mapping files for each package. */
    private Hashtable<String, MappingRoot> _mappings = null;
    /** The in memory package listings for each package. */
    private Hashtable<String, Properties> _packageListings = null;
    
    /** The package used when creating new classes. */
    private String _packageName;
    
    /** Keeps track of which JClass files have been processed. */
    private Vector<JClass>      _processed   = null;
    /** true if existing source files should not be silently overwritten. */
    private boolean     _promptForOverwrite = true;
    /** The schema we are generating source code for. */
    private Schema      _schema = null;
    /** true if non-fatal warnings should be suppressed. */
    private boolean     _suppressNonFatalWarnings = false;
    /** true if source generation should provide verbose output on its progress. */
    private boolean     _verbose     = false;
    /** The SourceFactory state. */
    private FactoryState _currentFactoryState = null;
    /** This class allows user interaction when Castor wants to ask a question. */
    private Dialog _dialog = null;
    /** The source generator whose state we track. */
    private SourceGenerator _sgen = null;
    /** Current status of the source generator. */
    private int _status = NORMAL_STATUS;
    /** Maps annotations to generated source code. */
    private Map<Annotated, JClass[]> _sourcesByComponent = new HashMap<Annotated, JClass[]>();

    /**
     * A cache of already generated classes (by their class name).
     */
    private Map<String, JClass> _sourcesByName = new HashMap<String, JClass>();

    /**
     * A cache of already generated classes (as part of an imported schema),
     * keyed by their class name.
     */
    private Map<String, JClass> _importedSourcesByName = new HashMap<String, JClass>();

    /**
     * Creates a new SGStateInfo.
     *
     * @param schema the Schema to generate source for
     * @param sgen the SourceGenerator instance
     */
    protected SGStateInfo(final Schema schema, final SourceGenerator sgen) {
        super();
        _schema      = schema;
        _packageName = "";
        _processed   = new Vector<JClass>();
        _dialog      = new ConsoleDialog();
        _sgen        = sgen;
    }
    
    /**
     * Get package used when creating new classes.
     * 
     * @return Package used when creating new classes.
     */
    public String getPackageName() {
        return _packageName;
    }
    
    /**
     * Set package used when creating new classes.
     * 
     * @param packageName Package used when creating new classes.
     */
    protected void setPackageName(final String packageName) {
        _packageName = packageName;
    }

    /**
     * Binds the given Annotated structure with its generated source classes.
     *
     * @param annotated the Annotated structure to add JClass bindings for
     * @param classes the JClass[] to bind
     */
    public void bindSourceCode(final Annotated annotated, final JClass[] classes) {
        _sourcesByComponent.put(annotated, classes);
        for (int i = 0; i < classes.length; i++) {
            JClass jClass = classes[i];
            if (jClass != null) {
                _sourcesByName.put(jClass.getName(), jClass);
            }
        }
    } //-- bindSourceCode

    /**
     * Stores generated sources as processed within an imported schema.
     * @param importedSourcesByName Generated sources as processed within an imported schema.
     */
    public void storeImportedSourcesByName(final Map<String, JClass> importedSourcesByName) {
        _importedSourcesByName.putAll(importedSourcesByName);
    } //-- storeImportedSourcesByName

    /**
     * Returns the processed JClass with the given name. If no such JClass has
     * been marked as processed, null is returned.
     *
     * @param className
     *            the JClass name to check against
     * @return the JClass with the given name
     */
    JClass getProcessed(final String className) {
        for (int i = 0; i < _processed.size(); i++) {
            JClass jClass = _processed.elementAt(i);
            if (jClass.getName().equals(className)) {
                return jClass;
            }
        }
        return null;
    } //-- getProcessed

    /**
     * Returns the array of JClass for the given Annotated structure or null if
     * no bindings have been specified for the given Structure.
     *
     * @param annotated
     *            the Annotated structure to search
     * @return the JClass array
     */
    public JClass[] getSourceCode(final Annotated annotated) {
        return _sourcesByComponent.get(annotated);
    } //-- getSourceCode

    /**
     * Returns the JClass with the given name or null if no bindings have been
     * specified for a JClass with the name.
     *
     * @param className
     *            the name of the JClass
     * @return the JClass if found
     */
    public JClass getSourceCode(final String className) {
        return _sourcesByName.get(className);
    } //-- getSourceCode

    /**
     * Returns the JClass with the given name or null if no bindings have been
     * specified for a JClass with the name. This method consults with JClass
     * instances imported through a Schema import only.
     *
     * @param className
     *            the name of the JClass
     * @return the (imported) JClass if found
     */
    public JClass getImportedSourceCode(final String className) {
        return _importedSourcesByName.get(className);
    } //-- getImportedSourceCode


    /**
     * Returns the Mapping file associated with the given filename.
     *
     * @param filename
     *            The filename to search for a Mapping File association
     * @return the Mapping file.
     */
    public MappingRoot getMapping(final String filename) {
        if (_mappings != null && filename != null) {
            return _mappings.get(filename);
        }
        return null;
    } //-- getMapping

    /**
     * Returns the CDRFile (Properties file) associated with the given filename.
     *
     * @param filename filename of the CDR file to be processed
     * @return the Properties file.
     */
    public Properties getCDRFile(final String filename) {
        if (_packageListings != null && filename != null) {
            return _packageListings.get(filename);
        }
        return null;
    }

    /**
     * Returns the set of CDR file names.
     *
     * @return the set of CDR file names.
     */
    public Enumeration<String> getCDRFilenames() {
        if (_packageListings == null) {
            return EMPTY_ENUMERATION;
        }
        return _packageListings.keys();
    } //-- getCDRFilenames

    /**
     * Returns the set of mapping filenames.
     *
     * @return the set of mapping filenames.
     */
    public Enumeration<String> getMappingFilenames() {
        if (_mappings == null) {
            return EMPTY_ENUMERATION;
        }
        return _mappings.keys();
    } //-- getMappingFilenames

    /**
     * Returns the current status.
     *
     * @return the current status.
     */
    public int getStatusCode() {
        return _status;
    } //-- getStatusCode

    /**
     * Marks the given JClass as having been processed.
     *
     * @param jClass the JClass to mark as having been processed.
     */
    void markAsProcessed(final JClass jClass) {
        //String className = jClass.getName();
        if (!_processed.contains(jClass)) {
            _processed.addElement(jClass);
        }
    } //-- markAsProcessed

    /**
     * Returns true if the given JClass has been marked as processed.
     *
     * @param jClass the JClass to check for being marked as processed
     * @return true if the given JClass has been marked as processed.
     */
    boolean processed(final JClass jClass) {
        return _processed.contains(jClass);
    } //-- processed

    /**
     * Returns true if a JClass with the given name has been marked as
     * processed.
     *
     * @param className the JClass name to check against
     * @return true if a JClass with the given name has been marked as processed
     */
    boolean processed(final String className) {
        for (int i = 0; i < _processed.size(); i++) {
            JClass jClass = _processed.elementAt(i);
            if (jClass.getName().equals(className)) {
                return true;
            }
        }
        return false;
    } //-- processed

    /**
     * Returns true if existing source files should be prompted before being
     * overwritten.
     *
     * @return true if existing source files should be prompted before being
     *         overwritten
     */
    boolean promptForOverwrite() {
        return _promptForOverwrite;
    } //-- promptForOverwrite

    /**
     * Sets whether or not existing source files should be silently overwritten
     * or whether the user should be prompted first.
     *
     * @param promptForOverwrite true if existing files should not be silently
     *        overwritten.
     */
    void setPromptForOverwrite(final boolean promptForOverwrite) {
        this._promptForOverwrite = promptForOverwrite;
    } //-- setPromptForOverwrite

    /**
     * Returns a reference to the schema for which we are generating source.
     * @return a reference to the schema for which we are generating source.
     */
    Schema getSchema() {
        return _schema;
    } //-- getSchema

    /**
     * Returns the SourceGenerator instance being used.
     * @return the SourceGenerator instance being used.
     */
    public SourceGenerator getSourceGenerator() {
        return _sgen;
    } //-- getSourceGenerator

    /**
     * Returns true if non-fatal warnings should be suppressed.
     * @return true if non-fatal warnings should be suppressed.
     */
    public boolean getSuppressNonFatalWarnings() {
        return _suppressNonFatalWarnings;
    }

    /**
     * Sets whether non-fatal warnings should be supporessed.
     * @param suppressNonFatalWarnings true if non-fatal warnings should be supporessed
     */
    void setSuppressNonFatalWarnings(final boolean suppressNonFatalWarnings) {
        _suppressNonFatalWarnings = suppressNonFatalWarnings;
    }

    /**
     * Sets the CDR (ClassDescriptorResolver) file associated with the given
     * filename.
     *
     * @param filename the filename associated with the CDR file
     * @param props the Properties file
     */
    public void setCDRFile(final String filename, final Properties props) {
        if (filename == null) {
            String err = "The argument 'filename' must not be null.";
            throw new IllegalArgumentException(err);
        }

        if (_packageListings == null) {
            _packageListings = new Hashtable<String, Properties>();
        }

        if (props == null) {
            _packageListings.remove(filename);
        } else {
            _packageListings.put(filename, props);
        }
    } //-- setCDRFile

    /**
     * Sets the Mapping file associated with the given filename.
     *
     * @param filename the filename associated with the Mapping
     * @param mapping the MappingRoot
     */
    public void setMapping(final String filename, final MappingRoot mapping) {
        if (filename == null) {
            String err = "The argument 'filename' must not be null.";
            throw new IllegalArgumentException(err);
        }

        if (_mappings == null) {
            _mappings = new Hashtable<String, MappingRoot>();
        }

        if (mapping == null) {
            _mappings.remove(filename);
        } else {
            _mappings.put(filename, mapping);
        }
    } //-- setMapping

    /**
     * Returns the Dialog used for interacting with the user.
     *
     * @return the Dialog, or null if none has been set.
     */
    public Dialog getDialog() {
        return _dialog;
    } //-- getConsoleDialog

    /**
     * Sets the Dialog used for interacting with the user.
     *
     * @param dialog the Dialog to use
     */
    void setDialog(final Dialog dialog) {
        _dialog = dialog;
    } //-- setDialog

//  /**
//   * Sets the XMLBindingComponent that this SGStateInfo is working on.
//   */
//  void setXMLBindingComponent(XMLBindingComponent compo) {
//      _bindingComponent = compo;
//  }
//
//  /**
//   * Returns the XMLBindingComponent this SGStateInfo is working on.
//   *
//   * @return the XMLBindingComponent this SGStateInfo is working on.
//   */
//  XMLBindingComponent getXMLBindingComponent() {
//      return _bindingComponent;
//  }

    /**
     * Sets the current status code to the given one.
     *
     * @param status the new status code
     */
    public void setStatusCode(final int status) {
        _status = status;
    } //-- setStatusCode

    /**
     * Sets whether or not the source code generator prints additional messages
     * during generating source code.
     *
     * @param verbose a boolean, when true indicates to print additional
     *        messages
     */
    void setVerbose(final boolean verbose) {
        this._verbose = verbose;
    } //-- setVerbose

    /**
     * Returns the value of the verbose flag. A true value indicates that
     * additional messages may be printed during processing.
     *
     * @return the value of the verbose flag.
     */
    public boolean verbose() {
        return this._verbose;
    } //-- verbose

    /**
     * Returns the current FactoryState that holds information about the classes
     * being generated.
     *
     * @return the current FactoryState
     */
    public FactoryState getCurrentFactoryState() {
        return _currentFactoryState;
    }

    /**
     * Sets the current FactoryState.
     * @param state the current FactoryState
     * @see #getCurrentFactoryState
     */
    public void setCurrentFactoryState(final FactoryState state) {
       _currentFactoryState = state;
    }

//  protected JClass getJClass(String name) {
//      if (name == null) return null;
//      JClass jClass = (JClass)classTypes.get(name);
//
//      if (jClass == null) {
//          jClass = new JClass(name);
//          classTypes.put(name, jClass);
//      }
//      return jClass;
//  }

    /**
     * Returns the sources as generated through XML schema imports.
     * @return the sources as generated through XML schema imports.
     */
    public Map<String, JClass> getImportedSourcesByName() {
        return _importedSourcesByName;
    }

    /**
     * Returns the sources as generated through XML schema imports.
     * @return the sources as generated through XML schema imports.
     */
    public Map<String, JClass> getSourcesByName() {
        return _sourcesByName;
    }

} //-- SGStateInfo
