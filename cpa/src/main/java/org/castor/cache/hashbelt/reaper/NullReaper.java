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

import org.castor.cache.hashbelt.container.Container;

/**
 * The simplest of all possible expiration handlers. It does nothing.
 * <p>
 * Having said that, here's what happens when we do nothing:
 * <ol>
 * <li>The hashbelt loses a reference to the object.</li>
 * <li>Garbage collection has a chance to work.</li>
 * </ol>
 * <p>
 * The point is that, even when we do nothing, the hashbelt changes.
 * 
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of cached values
 * 
 * @author <a href="mailto:gblock AT ctoforaday DOT com">Gregory Block</a>
 * @version $Revision: 9040 $ $Date: 2011-08-16 08:26:59 +0200 (Di, 16 Aug 2011) $
 * @since 1.0
 */
public final class NullReaper<K, V> extends AbstractReaper<K, V> {
    /**
     * {@inheritDoc}
     */
    public void handleExpiredContainer(final Container<K, V> expiredContainer) { }
}
