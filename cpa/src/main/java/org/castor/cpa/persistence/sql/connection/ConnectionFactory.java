/*
 * Copyright 2005 Werner Guttmann, Ralf Joachim
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
package org.castor.cpa.persistence.sql.connection;

import java.sql.Connection;
import java.sql.SQLException;

import org.castor.cpa.persistence.sql.engine.CastorConnection;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.persist.spi.PersistenceFactory;

/**
 * Interface for creation of new JDBC Connection instances.
 * 
 * @author <a href="mailto:werner DOT guttmann AT gmx DOT net">Werner Guttmann</a>
 * @author <a href="mailto:ralf DOT joachim AT syscon DOT eu">Ralf Joachim</a>
 * @version $Revision: 8994 $ $Date: 2011-08-02 01:40:59 +0200 (Di, 02 Aug 2011) $
 * @since 0.9.9
 */
public interface ConnectionFactory {
    //--------------------------------------------------------------------------

    /**
     * Initialize the concrete factory.
     * 
     * @param factory PersistenceFactory needed to construct CastorConnection.
     * @throws MappingException If concrete factory could not be initialized.
     */
    void initializeFactory(final PersistenceFactory factory) throws MappingException;

    /**
     * Creates a new JDBC Connection instance.
     * 
     * @return A JDBC Connection.
     * @throws SQLException If the JDBC connection cannot be created.
     */
    Connection createConnection () throws SQLException;

    /**
     * Creates a new CastorConnection instance.
     * 
     * @return A CastorConnection instance.
     * @throws SQLException If the CastorConnection cannot be created.
     */
    CastorConnection createCastorConnection() throws SQLException;

    //--------------------------------------------------------------------------
}
