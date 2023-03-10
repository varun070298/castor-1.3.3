/*
 * Copyright 2006 Werner Guttmann
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
 */
package org.castor.cpa.persistence.convertor;

/**
 * @author <a href="mailto:wguttmn AT codehaus DOT org">Werner Guttmann</a>
 * @version $Revision: 8994 $ $Date: 2011-08-02 01:40:59 +0200 (Di, 02 Aug 2011) $
 */
public class EnumToOrdinal extends AbstractSimpleTypeConvertor {
    //-----------------------------------------------------------------------------------
    
    /**
     * Creates an instance of this class.
     */
    public EnumToOrdinal() {
        super(Enum.class, int.class);
    }
    
    //-----------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public final Object convert(final Object object) {
        try {
            return object.getClass().getMethod("ordinal").invoke(object);
        } catch (Exception ex) {
            return null;
        }
    }

    //-----------------------------------------------------------------------------------
}
