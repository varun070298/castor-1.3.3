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
package org.castor.cache.simple;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.castor.cache.Cache;
import org.castor.cache.CacheAcquireException;
import org.castor.cache.CacheFactory;

/**
 * @author <a href="mailto:ralf DOT joachim AT syscon DOT eu">Ralf Joachim</a>
 * @version $Revision: 9041 $ $Date: 2011-08-16 11:51:17 +0200 (Di, 16 Aug 2011) $
 * @since 1.0
 */
public final class TestTimeLimitedFactory extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("TimeLimitedFactory Tests");

        suite.addTest(new TestTimeLimitedFactory("testConstructor"));
        suite.addTest(new TestTimeLimitedFactory("testGetCacheType"));
        suite.addTest(new TestTimeLimitedFactory("testGetCacheClassName"));
        suite.addTest(new TestTimeLimitedFactory("testGetCache"));
        suite.addTest(new TestTimeLimitedFactory("testShutdown"));

        return suite;
    }

    public TestTimeLimitedFactory(final String name) { super(name); }

    public void testConstructor() {
        CacheFactory<String, String> cf = new TimeLimitedFactory<String, String>();
        assertTrue(cf instanceof TimeLimitedFactory);
    }

    public void testGetCacheType() {
        CacheFactory<String, String> cf = new TimeLimitedFactory<String, String>();
        assertEquals("time-limited", cf.getCacheType());
    }

    public void testGetCacheClassName() {
        CacheFactory<String, String> cf = new TimeLimitedFactory<String, String>();
        String classname = "org.castor.cache.simple.TimeLimited";
        assertEquals(classname, cf.getCacheClassName());
    }

    public void testGetCache() {
        CacheFactory<String, String> cf = new TimeLimitedFactory<String, String>();
        try {
            Cache<String, String> c = cf.getCache(null);
            assertTrue(c instanceof TimeLimited);
        } catch (CacheAcquireException ex) {
            fail("Failed to get instance of TimeLimited from factroy");
        }
    }

    public void testShutdown() {
        CacheFactory<String, String> cf = new TimeLimitedFactory<String, String>();
        cf.shutdown();
    }
}
