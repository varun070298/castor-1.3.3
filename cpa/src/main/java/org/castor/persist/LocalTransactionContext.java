/*
 * Copyright 2005 Ralf Joachim
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
package org.castor.persist;

import java.sql.SQLException;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.core.util.Messages;
import org.castor.cpa.persistence.sql.connection.ConnectionFactory;
import org.castor.cpa.persistence.sql.engine.CastorConnection;
import org.exolab.castor.jdo.ConnectionFailedException;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.TransactionAbortedException;
import org.exolab.castor.persist.LockEngine;

/**
 * A transaction context is required in order to perform operations
 * against the database. The transaction context is mapped to {@link
 * javax.transaction.Transaction} for the ODMG API and into
 * {@link javax.transaction.xa.XAResource} for XA databases. The only
 * way to begin a new transaction is through the creation of a new
 * transaction context. All database access must be performed through
 * a transaction context.
 *
 * @author <a href="mailto:ralf DOT joachim AT syscon DOT eu">Ralf Joachim</a>
 * @version $Revision: 8994 $ $Date: 2011-08-02 01:40:59 +0200 (Di, 02 Aug 2011) $
 * @since 1.0
 */
public final class LocalTransactionContext extends AbstractTransactionContext {
    /** Log instance used for outputting debug statements. */
    private static final Log LOG = LogFactory.getLog(LocalTransactionContext.class);

    /**
     * Create a new transaction context.
     * 
     * @param db Database instance
     */
    public LocalTransactionContext(final Database db) {
        super(db);
    }

    /**
     * {@inheritDoc}
     */
    protected CastorConnection createConnection(final LockEngine engine)
    throws ConnectionFailedException {
        // Get a new connection from the engine. Since the engine has no
        // transaction association, we must do this sort of round trip. An attempt
        // to have the transaction association in the engine inflates the code size
        // in other places.
        try {
            ConnectionFactory factory = engine.getDatabaseContext().getConnectionFactory();
            CastorConnection castorConn = factory.createCastorConnection();
            castorConn.setAutoCommit(false);
            return castorConn;
        } catch (SQLException ex) {
            throw new ConnectionFailedException(Messages.format("persist.nested", ex), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void commitConnections() throws TransactionAbortedException {
        try {
            // Go through all the connections opened in this transaction, commit and
            // close them one by one.
            for (Iterator<CastorConnection> iter = connectionsIterator(); iter.hasNext(); ) {
                // Checkpoint can only be done if transaction is not running under
                // transaction monitor
                iter.next().commit();
            }
        } catch (SQLException ex) {
            throw new TransactionAbortedException(
                    Messages.format("persist.nested", ex), ex);
        } finally {
            for (Iterator<CastorConnection> iter = connectionsIterator(); iter.hasNext(); ) {
                try {
                    iter.next().close();
                } catch (SQLException ex) {
                    LOG.warn("SQLException at close JDBC Connection instance.", ex);
                }
            }
            clearConnections();
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void rollbackConnections() {
        // Go through all the connections opened in this transaction, rollback and
        // close them one by one. Ignore errors.
        for (Iterator<CastorConnection> iter = connectionsIterator(); iter.hasNext(); ) {
            CastorConnection conn = iter.next();
            try {
                conn.rollback();
                LOG.debug("Connection rolled back");
                conn.close();
                LOG.debug("Connection closed");
            } catch (SQLException ex) {
                LOG.warn("SQLException at rollback/close JDBC Connection instance.", ex);
            }
        }
        clearConnections();
    }

    /**
     * {@inheritDoc}
     */
    protected void closeConnections() throws TransactionAbortedException { }
}
