/*
 * Copyright 2005 Tim Telcik, Werner Guttmann, Ralf Joachim
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

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.cache.AbstractCacheFactory;

/**
 * Implements {@link org.castor.cache.CacheFactory} for the {@link CoherenceCache}
 * implementation of {@link org.castor.cache.Cache}.
 *
 * @param <K> the type of keys maintained by cache
 * @param <V> the type of cached values
 *
 * @author <a href="mailto:ttelcik AT hbf DOT com DOT au">Tim Telcik</a>
 * @author <a href="mailto:werner DOT guttmann AT gmx DOT net">Werner Guttmann</a>
 * @author <a href="mailto:ralf DOT joachim AT syscon DOT eu">Ralf Joachim</a>
 * @version $Revision: 9041 $ $Date: 2011-08-16 11:51:17 +0200 (Di, 16 Aug 2011) $
 * @since 1.0
 */
public final class CoherenceCacheFactory<K, V> extends AbstractCacheFactory<K, V> {
    /** The <a href="http://jakarta.apache.org/commons/logging/">Jakarta Commons
     *  Logging </a> instance used for all logging. */
    private static final Log LOG = LogFactory.getLog(CoherenceCacheFactory.class);

    /**
     * {@inheritDoc}
     */
    public String getCacheType() { return CoherenceCache.TYPE; }

    /**
     * {@inheritDoc}
     */
    public String getCacheClassName() { return CoherenceCache.class.getName(); }

    /**
     * {@inheritDoc}
     */
    public void shutdown() { shutdown(CoherenceCache.IMPLEMENTATION); }

    /**
     * Normally called to shutdown CoherenceCache. To be able to test the method
     * without having <code>com.tangosol.net.CacheFactory</code> implementation,
     * it can also be called with a test implementations classname.
     *
     * @param implementation Cache implementation classname to shutdown.
     */
    public synchronized void shutdown(final String implementation) {
        if (!isInitialized()) { return; }
        try {
            ClassLoader ldr = this.getClass().getClassLoader();
            Class<?> cls = ldr.loadClass(implementation);
            if (cls != null) {
                Method method = cls.getMethod("shutdown", (Class[]) null);
                method.invoke(null, (Object[]) null);
            }
        } catch (Exception e) {
            LOG.error("Problem shutting down Coherence cluster member", e);
        }
    }
}
