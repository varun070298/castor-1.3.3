/*
 * Copyright 2006 Le Duc Bao, Ralf Joachim
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
package org.castor.ddlgen;

import org.castor.ddlgen.schemaobject.Field;
import org.castor.ddlgen.schemaobject.ForeignKey;
import org.castor.ddlgen.schemaobject.Index;
import org.castor.ddlgen.schemaobject.PrimaryKey;
import org.castor.ddlgen.schemaobject.Schema;
import org.castor.ddlgen.schemaobject.Table;

/**
 * The SchemaFactory handles the creation for various schema objects. It helps the
 * AbstractGenerator to dynamically extract schema information for specific database.
 * This interface need to be implemented for every supported database engine.
 * 
 * @author <a href="mailto:leducbao AT gmail DOT com">Le Duc Bao</a>
 * @author <a href="mailto:ralf DOT joachim AT syscon DOT eu">Ralf Joachim</a>
 * @version $Revision: 8993 $ $Date: 2011-08-02 01:28:52 +0200 (Di, 02 Aug 2011) $
 * @since 1.1
 */
public interface SchemaFactory {
    //--------------------------------------------------------------------------

    /**
     * Create schema objects.
     * 
     * @return New schema object.
     */
    Schema createSchema();

    /**
     * Create table objects.
     * 
     * @return New table object.
     */
    Table createTable();

    /**
     * Create field objects.
     * 
     * @return New field object.
     */
    Field createField();

    /**
     * Create foreign key objects.
     * 
     * @return New foreign key object.
     */
    ForeignKey createForeignKey();

    /**
     * Create index objects.
     * 
     * @return New index object.
     */
    Index createIndex();

    /**
     * Create primary key objects.
     * 
     * @return New primary key object.
     */
    PrimaryKey createPrimaryKey();

    //--------------------------------------------------------------------------
}
