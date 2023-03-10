/*
 * Copyright 2007 Ralf Joachim
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
 * Convert <code>Double</code> to <code>Float</code>.
 * 
 * @author <a href="mailto:ralf DOT joachim AT syscon DOT eu">Ralf Joachim</a>
 * @version $Revision: 8994 $ $Date: 2011-08-02 01:40:59 +0200 (Di, 02 Aug 2011) $
 * @since 1.1.3
 */
public final class DoubleToFloat extends AbstractSimpleTypeConvertor {
    //-----------------------------------------------------------------------------------

    /**
     * Default constructor.
     */
    public DoubleToFloat() {
        super(Double.class, Float.class);
    }

    //-----------------------------------------------------------------------------------
    
    /**
     * {@inheritDoc}
     */
    public Object convert(final Object object) {
        return new Float(((Double) object).floatValue());
    }

    //-----------------------------------------------------------------------------------
}
