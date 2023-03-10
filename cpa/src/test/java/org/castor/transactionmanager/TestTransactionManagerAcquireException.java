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
package org.castor.transactionmanager;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author <a href="mailto:ralf DOT joachim AT syscon DOT eu">Ralf Joachim</a>
 * @version $Revision: 8994 $ $Date: 2011-08-02 01:40:59 +0200 (Di, 02 Aug 2011) $
 * @since 1.0
 */
public final class TestTransactionManagerAcquireException extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("CacheAcquireException Tests");

        suite.addTest(new TestTransactionManagerAcquireException("test"));

        return suite;
    }

    public TestTransactionManagerAcquireException(final String name) { super(name); }

    public void test() {
        Exception ex;
        TransactionManagerAcquireException tmae;
        
        ex = new TransactionManagerAcquireException("some reason");
        assertTrue(ex instanceof TransactionManagerAcquireException);
        assertEquals("some reason", ex.getMessage());
        tmae = (TransactionManagerAcquireException) ex;
        assertNull(tmae.getCause());
        
        Exception root = new Exception("root exception");
        ex = new TransactionManagerAcquireException("other reason", root);
        assertTrue(ex instanceof TransactionManagerAcquireException);
        assertEquals("other reason", ex.getMessage());
        tmae = (TransactionManagerAcquireException) ex;
        assertEquals(root, tmae.getCause());
    }
}
