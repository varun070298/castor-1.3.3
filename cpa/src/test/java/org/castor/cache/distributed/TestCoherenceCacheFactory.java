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
package org.castor.cache.distributed;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.castor.cache.Cache;
import org.castor.cache.CacheAcquireException;
import org.castor.cache.CacheFactory;

/**
 * @author <a href="mailto:ralf DOT joachim AT syscon DOT eu">Ralf Joachim</a>
 * @version $Revision: 9041 $ $Date: 2011-08-16 11:51:17 +0200 (Di, 16 Aug 2011) $
 * @since 1.0
 */
public final class TestCoherenceCacheFactory extends TestCase {
    private static final boolean DISABLE_LOGGING = true;
    
    public static Test suite() {
        TestSuite suite = new TestSuite("CoherenceCacheFactory Tests");

        suite.addTest(new TestCoherenceCacheFactory("testConstructor"));
        suite.addTest(new TestCoherenceCacheFactory("testGetCacheType"));
        suite.addTest(new TestCoherenceCacheFactory("testGetCacheClassName"));
        suite.addTest(new TestCoherenceCacheFactory("testGetCache"));
        suite.addTest(new TestCoherenceCacheFactory("testShutdown"));

        return suite;
    }

    public TestCoherenceCacheFactory(final String name) { super(name); }

    public void testConstructor() {
        CacheFactory<String, String> cf = new CoherenceCacheFactory<String, String>();
        assertTrue(cf instanceof CoherenceCacheFactory);
    }

    public void testGetCacheType() {
        CacheFactory<String, String> cf = new CoherenceCacheFactory<String, String>();
        assertEquals("coherence", cf.getCacheType());
    }

    public void testGetCacheClassName() {
        CacheFactory<String, String> cf = new CoherenceCacheFactory<String, String>();
        String classname = "org.castor.cache.distributed.CoherenceCache";
        assertEquals(classname, cf.getCacheClassName());
    }

    public void testGetCache() {
        CacheFactory<String, String> cf = new CoherenceCacheFactory<String, String>();
        try {
            Cache<String, String> c = cf.getCache(null);
            assertTrue(c instanceof CoherenceCache);
        } catch (CacheAcquireException ex) {
            fail("Failed to get instance of CoherenceCache from factroy");
        }
    }

    public void testShutdown() {
        Logger logger = Logger.getLogger(CoherenceCacheFactory.class);
        Level level = logger.getLevel();
        
        CoherenceCacheFactory<String, String> cf = new CoherenceCacheFactory<String, String>();
        try {
            cf.getCache(null);
        } catch (CacheAcquireException ex) {
            fail("Failed to get instance of CoherenceCache from factroy");
        }
        int counter = DistributedCacheFactoryMock.getCounter();
        
        if (DISABLE_LOGGING) { logger.setLevel(Level.FATAL); }

        DistributedCacheFactoryMock.setException(null);
        cf.shutdown(Object.class.getName());
        assertEquals(counter, DistributedCacheFactoryMock.getCounter());
        
        DistributedCacheFactoryMock.setException(new Exception("dummy"));
        cf.shutdown(DistributedCacheFactoryMock.class.getName());
        assertEquals(counter, DistributedCacheFactoryMock.getCounter());

        logger.setLevel(level);
        
        DistributedCacheFactoryMock.setException(null);
        cf.shutdown(DistributedCacheFactoryMock.class.getName());
        assertEquals(counter + 1, DistributedCacheFactoryMock.getCounter());
    }
}
