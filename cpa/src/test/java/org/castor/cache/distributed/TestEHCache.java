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

import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.castor.cache.Cache;
import org.castor.cache.CacheAcquireException;

/**
 * @author <a href="mailto:werner DOT guttmann AT gmx DOT net">Werner Guttmann</a>
 * @version $Revision: 9041 $ $Date: 2011-08-16 11:51:17 +0200 (Di, 16 Aug 2011) $
 * @since 1.0
 */
public final class TestEHCache extends TestCase {
    
    private static final boolean DISABLE_LOGGING = true;
    
    public static Test suite() {
        TestSuite suite = new TestSuite("EHCache Tests");

        suite.addTest(new TestEHCache("testStaticProperties"));
        suite.addTest(new TestEHCache("testConstructor"));
        suite.addTest(new TestEHCache("testGetType"));
        suite.addTest(new TestEHCache("testInitialize"));
        suite.addTest(new TestEHCache("testClose"));

        return suite;
    }

    public TestEHCache(final String name) { super(name); }

    public void testStaticProperties() {
        assertEquals("ehcache", EHCache.TYPE);
        assertEquals("net.sf.ehcache.CacheManager", EHCache.IMPLEMENTATION);
    }

    public void testConstructor() throws Exception {
        Cache<String, String> c = new EHCache<String, String>();
        assertTrue(c instanceof EHCache);
    }

    public void testGetType() throws Exception {
        Cache<String, String> c = new EHCache<String, String>();
        assertEquals("ehcache", c.getType());
    }

    public void testInitialize() throws Exception {
        Logger logger = Logger.getLogger(EHCache.class);
        Level level = logger.getLevel();
        
        EHCache<String, String> c = new EHCache<String, String>();
        int counter = DistributedCacheFactoryMock.getCounter();
        
        Properties params = new Properties();
        params.put(Cache.PARAM_NAME, "dummy ehcache");
        
        if (DISABLE_LOGGING) { logger.setLevel(Level.FATAL); }

        try {
            DistributedCacheFactoryMock.setException(new Exception("dummy"));
            c.initialize(DistributedCacheFactoryMock.class.getName(), params);
            fail("Failed to trow exception at initialize of EHCache instance");
        } catch (CacheAcquireException ex) {
            assertEquals(counter, DistributedCacheFactoryMock.getCounter());
        }
        
        logger.setLevel(level);
    }

    public void testClose() throws Exception {
        Cache<String, String> c = new EHCache<String, String>();
        int counter = DistributedCacheMock.getCounter();
        
        c.close();
        assertEquals(counter, DistributedCacheMock.getCounter());
    }
}