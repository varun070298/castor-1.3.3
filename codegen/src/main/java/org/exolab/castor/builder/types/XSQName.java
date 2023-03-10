/*
 * Copyright 2007 Arnaud Blandin, Ralf Joachim
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.exolab.castor.builder.types;

import org.exolab.javasource.JClass;
import org.exolab.javasource.JSourceCode;
import org.exolab.javasource.JType;

/**
 * The xsd:QName XML Schema type. A QName (prefix:NCName) is mapped in memory {URI}NCName.
 *
 * @author <a href="mailto:blandin AT intalio DOT com">Arnaud Blandin</a>
 * @author <a href="mailto:ralf DOT joachim AT syscon DOT eu">Ralf Joachim</a>
 * @version $Revision: 6907 $ $Date: 2005-03-05 06:42:06 -0700 (Sat, 05 Mar 2005) $
 */
public final class XSQName extends AbstractLengthFacet {
    //--------------------------------------------------------------------------

    /** Name of this XSType. */
    public static final String NAME = "QName";
    
    /** Type number of this XSType. */
    public static final short TYPE = XSType.QNAME_TYPE;

    /** The JType represented by this XSType. */
    private static final JType JTYPE = new JClass("java.lang.String");

    //--------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public String getName() { return NAME; }

    /**
     * {@inheritDoc}
     */
    public short getType() { return TYPE; }
    
    /**
     * {@inheritDoc}
     */
    public boolean isPrimitive() { return false; }
    
    /**
     * {@inheritDoc}
     */
    public boolean isDateTime() { return false; }
    
    /**
     * {@inheritDoc}
     */
    public JType getJType() { return JTYPE; }

    /**
     * {@inheritDoc}
     */
    public String newInstanceCode() {
        return "new java.lang.String();";
    }
    
    /**
     * {@inheritDoc}
     */
    public String createToJavaObjectCode(final String variableName) {
        return variableName;
    }
    
    /**
     * {@inheritDoc}
     */
    public String createFromJavaObjectCode(final String variableName) {
        return "(java.lang.String) " + variableName;
    }

    //--------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void validationCode(final JSourceCode jsc,
            final String fixedValue, final String validatorInstanceName) {
        jsc.add("org.exolab.castor.xml.validators.NameValidator typeValidator;\n"
              + "typeValidator = new org.exolab.castor.xml.validators.NameValidator(\n"
              + " org.exolab.castor.xml.XMLConstants.NAME_TYPE_QNAME);\n"
              + "{0}.setValidator(typeValidator);", validatorInstanceName);

        if (fixedValue != null) {
            jsc.add("typeValidator.setFixed(" + fixedValue + ");");
        }

        codePatternFacet(jsc, "typeValidator");
        codeWhiteSpaceFacet(jsc, "typeValidator");
        codeLengthFacet(jsc, "typeValidator");
    }

    //--------------------------------------------------------------------------
}
