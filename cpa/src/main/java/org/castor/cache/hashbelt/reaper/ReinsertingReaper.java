/*
 * Copyright 2005, 2006 Gregory Block
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
package org.castor.cache.hashbelt.reaper;

import java.util.Iterator;

import org.castor.cache.hashbelt.container.Container;

/**
 * A hybrid of the notifying and refreshing reaper; like the notifying reaper,
 * this calls handleExpiredObject for the object; like the refreshing reaper, it
 * then reinserts it in the front of the expiration system. Unlike the refreshing
 * reaper, it always returns the exact same object to the front of the expiration
 * system.
 * <p>
 * Useful for alerts and announcements. E.g. suppose you're supposed to send
 * someone an update every 15 minutes. Use this one and an object that sends the
 * message inside its "expire" method.
 * 
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of cached values
 * 
 * @author <a href="mailto:gblock AT ctoforaday DOT com">Gregory Block</a>
 * @version $Revision: 9040 $ $Date: 2011-08-16 08:26:59 +0200 (Di, 16 Aug 2011) $
 * @since 1.0
 */
public abstract class ReinsertingReaper<K, V> extends AbstractReaper<K, V> {
    /**
     * {@inheritDoc}
     */
    public final void handleExpiredContainer(final Container<K, V> expiredContainer) {
        Iterator<K> iter = expiredContainer.keyIterator();
        while (iter.hasNext()) {
            K key = iter.next();
            V value = expiredContainer.get(key);
            handleExpiredObject(value);
            getCache().put(key, value);
        }
    }

    /**
     * Methode called with an object that has expired before it is garbage collected.
     * 
     * @param expiredObject The object that has expired.
     */
    protected abstract void handleExpiredObject(final V expiredObject);
}
