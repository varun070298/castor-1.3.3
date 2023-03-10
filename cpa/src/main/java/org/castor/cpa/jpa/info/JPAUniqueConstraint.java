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

import javax.persistence.UniqueConstraint;

/**
 * @author <a href="mailto:wguttmn AT codehaus DOT org">Werner Guttmann</a>
 * @version $Revision: 8994 $ $Date: 2011-08-02 01:40:59 +0200 (Di, 02 Aug 2011) $
 */
public class JPAUniqueConstraint {
    //-----------------------------------------------------------------------------------
    
    private String[] _columnNames;
    
    //-----------------------------------------------------------------------------------
    
    public static JPAUniqueConstraint extract(final UniqueConstraint uniqueConstraint) {
        JPAUniqueConstraint constraint = new JPAUniqueConstraint();
        
        String[] colNames = new String[]{};
        int i = 0;
        for (String columnName : uniqueConstraint.columnNames()) {
            colNames[i++] = columnName;
        }
        
        return constraint;
    }
    
    public String[] getColumnNames() {
        return _columnNames;
    }
    
    public void setColumnNames(final String[] columnNames) {
        _columnNames = columnNames;
    }
    
    //-----------------------------------------------------------------------------------
}
