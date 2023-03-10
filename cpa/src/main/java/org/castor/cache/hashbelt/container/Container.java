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

import java.util.Iterator;
import java.util.Map;

/**
 * An interface describing implementation strategies for containers on the hashbelt;
 * containers hold objects that are in the hashbelt, and provide the time-based
 * grouping that allows the container to be efficiently dealt with as a group of
 * objects to be expired.
 * <p>
 * According to the AbstractHashblet's implementations, if the container can't
 * be protected by AbstractHashblet.lock(), the implementations of this interface need 
 * to be appropriately synchronized.
 * 
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of cached values
 * 
 * @author <a href="mailto:gblock AT ctoforaday DOT com">Gregory Block</a>
 * @author <a href="mailto:ralf DOT joachim AT syscon DOT eu">Ralf Joachim</a>
 * @version $Revision: 9040 $ $Date: 2011-08-16 08:26:59 +0200 (Di, 16 Aug 2011) $
 * @since 1.0
 */
public interface Container<K, V> extends Map<K, V> {
    /**
     * Set the timestamp of this container to System.currentTimeMillis().
     */
    void updateTimestamp();
    
    /**
     * Returns the timestamp of this container.
     * 
     * @return The timestamp.
     */
    long getTimestamp();
    
    /**
     * Returns an iterator over the keys contained in this container. If the container
     * is modified while an iteration is in progress, the results of the iteration
     * is not affected and vice-versa.
     *
     * @return An iterator over the keys currently contained in the container.
     */
    Iterator<K> keyIterator();
    
    /**
     * Returns an iterator over the values contained in this container. If the container
     * is modified while an iteration is in progress, the results of the iteration
     * is not affected and vice-versa.
     *
     * @return An iterator over the values currently contained in the container.
     */
    Iterator<V> valueIterator();
}
