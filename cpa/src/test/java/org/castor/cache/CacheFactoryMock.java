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
package org.castor.cache;

/**
 * Cache to test access to distributed caches (Coherence, FKCache and JCache)
 * without having their implementations available.
 * 
 * @version $Revision: 9041 $ $Date: 2011-08-16 11:51:17 +0200 (Di, 16 Aug 2011) $
 * @since 1.0
 */
public final class CacheFactoryMock<K, V> extends AbstractCacheFactory<K, V> {
    //--------------------------------------------------------------------------
    
    private String _cacheClassName = CacheMock.class.getName();
    
    /**
     * @see org.castor.cache.CacheFactory#getCacheType()
     */
    public String getCacheType() { return CacheMock.TYPE; }
    
    /**
     * @see org.castor.cache.CacheFactory#getCacheClassName()
     */
    public String getCacheClassName() { return _cacheClassName; }
    
    /**
     * Set classname of the cache to create (to produce exceptions at test).
     * 
     * @param cacheClassName Classname of the cache to create.
     */
    public void setCacheClassName(final String cacheClassName) {
        _cacheClassName = cacheClassName;
    }
    
    //--------------------------------------------------------------------------
}
