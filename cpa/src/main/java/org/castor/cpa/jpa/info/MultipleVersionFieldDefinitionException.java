/*
 * Copyright 2010 Werner Guttmann
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
package org.castor.cpa.jpa.info;

/**
 * Defines an exception related to defining multiple version fields.
 * 
 * @author <a href=" mailto:wguttmn AT codehaus DOT org">Werner Guttmann</a>
 * @version $Revision: 8994 $ $Date: 2011-08-02 01:40:59 +0200 (Di, 02 Aug 2011) $
 * @since 1.3.2
 */
public class MultipleVersionFieldDefinitionException extends Exception {
    //-----------------------------------------------------------------------------------

    /** Serial version UID. */
    private static final long serialVersionUID = 1172019711964808828L;
    
    //-----------------------------------------------------------------------------------

    /**
     * Creates an instance of this class.
     */
    public MultipleVersionFieldDefinitionException() {
        super();
    }
    
    /**
     * Creates an instance of this class, with an error message being used to indicate
     * the underlying problem.
     * @param message The underlying problem.
     */
    public MultipleVersionFieldDefinitionException(final String message) {
        super(message);
    }    
    
    //-----------------------------------------------------------------------------------
}
