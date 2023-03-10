/*
 * Copyright 2005, 2006 Gregory Block, Ralf Joachim
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
package org.castor.cache.hashbelt.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;

/**
 * A very basic, HashMap-based implementation of the hashmap container strategy,
 * using nothing more than a basic hashmap to store key/value pairs. This works
 * well for lots of gets and a reasonably high volume of removes; if few removes
 * are required, and iterators are important to your particluar use-case of the
 * cache, it's better to use the FastIteratingContainer, which can handle
 * iterating at a higher speed, still has a map for accessing hash values, but has
 * a higher removal cost.
 * 
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of cached values
 * 
 * @author <a href="mailto:gblock AT ctoforaday DOT com">Gregory Block</a>
 * @author <a href="mailto:ralf DOT joachim AT syscon DOT eu">Ralf Joachim</a>
 * @version $Revision: 9040 $ $Date: 2011-08-16 08:26:59 +0200 (Di, 16 Aug 2011) $
 * @since 1.0
 */
public final class MapContainer<K, V> extends HashMap<K, V> implements Container<K, V> {
    //--------------------------------------------------------------------------
    
    /** SerialVersionUID. */
    private static final long serialVersionUID = -7215860376133906243L;
    
    /** Timestamp of this container. */
    private long _timestamp = 0;
    
    //--------------------------------------------------------------------------
    // additional operations of container interface

    /**
     * {@inheritDoc}
     */
    public void updateTimestamp() { _timestamp = System.currentTimeMillis(); }
    
    /**
     * {@inheritDoc}
     */
    public long getTimestamp() { return _timestamp; }
    
    /**
     * {@inheritDoc}
     */
    public Iterator<K> keyIterator() {
        return new ArrayList<K>(keySet()).iterator();
    }
    
    /**
     * {@inheritDoc}
     */
    public Iterator<V> valueIterator() {
        return new ArrayList<V>(values()).iterator();
    }
    
    //--------------------------------------------------------------------------
}
