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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.castor.cache.Cache;

/**
 * @author <a href="mailto:ralf DOT joachim AT syscon DOT eu">Ralf Joachim</a>
 * @version $Revision: 9040 $ $Date: 2011-08-16 08:26:59 +0200 (Di, 16 Aug 2011) $
 * @since 1.0
 */
public final class TestNoCache extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("NoCache Tests");

        suite.addTest(new TestNoCache("test"));

        return suite;
    }

    public TestNoCache(final String name) { super(name); }
    
    public void test() {
        assertEquals("none", NoCache.TYPE);

        Cache<String, String> cache = new NoCache<String, String>();
        assertTrue(cache instanceof NoCache);

        assertEquals("none", cache.getType());
        
        assertNull(cache.put("first key", "first value"));
        assertNull(cache.put("first key", "first value"));
        assertNull(cache.put("second key", "second value"));

        assertNull(cache.remove("second key"));
        
        assertEquals(0, cache.size());
        
        assertEquals(true, cache.isEmpty());
        
        assertEquals(false, cache.containsKey("first key"));
        
        assertEquals(false, cache.containsValue("first value"));
        
        assertNull(cache.get("first key"));
        
        Map<String, String> map = new HashMap<String, String>();
        map.put("second key", "second value");
        map.put("third key", "third value");
        cache.putAll(map);
        assertEquals(0, cache.size());
        
        cache.clear();
        assertEquals(0, cache.size());
        
        Set<String> keys = cache.keySet();
        assertEquals(0, keys.size());

        Collection<String> values = cache.values();
        assertEquals(0, values.size());

        Set<Map.Entry<String, String>> entries = cache.entrySet();
        assertEquals(0, entries.size());
    }
}
