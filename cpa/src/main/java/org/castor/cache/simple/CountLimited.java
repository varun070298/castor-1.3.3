/*
 * Copyright 2005 Thomas Yip, Werner Guttmann, Ralf Joachim
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.castor.cache.AbstractBaseCache;
import org.castor.cache.CacheAcquireException;

/**
 * CountLimited is a count limted least-recently-used <tt>Map</tt>. Every object being
 * put in the Map will live until the map is full. If the map is full, the least recently
 * used object will be disposed. 
 * <p>
 * The capacity is passed to the cache at initialization by the individual cache property
 * <b>capacity</b> which defines the maximum number of objects the cache can hold. If not
 * specified a default capacity of 30 objects will be used.
 *
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of cached values
 * 
 * @author <a href="mailto:tyip AT leafsoft DOT com">Thomas Yip</a>
 * @author <a href="mailto:werner DOT guttmann AT gmx DOT net">Werner Guttmann</a>
 * @author <a href="mailto:ralf DOT joachim AT syscon DOT eu">Ralf Joachim</a>
 * @version $Revision: 9040 $ $Date: 2011-08-16 08:26:59 +0200 (Di, 16 Aug 2011) $
 */
public final class CountLimited<K, V> extends AbstractBaseCache<K, V> {
    //--------------------------------------------------------------------------

    /** The type of the cache. */
    public static final String TYPE = "count-limited";
    
    /** Mapped initialization parameter <code>capacity</code>. */
    public static final String PARAM_CAPACITY = "capacity";

    /** Default capacity of cache. */
    public static final int DEFAULT_CAPACITY = 30;
    
    /** Status of cache entries that can be replaced at next put. */
    private static final int LRU_OLD = 0;

    /** Status of new cache entries that should not be replaced. */
    private static final int LRU_NEW = 1;
    
    /** Map keys to positions. */
    private HashMap<K, Integer> _mapKeyPos = null;
    
    /** Array of keys. */
    private K[] _keys = null;
    
    /** Array of values. */
    private V[] _values = null;
    
    /** Array with status of the entries. */
    private int[] _status = null;
    
    /** Real capacity of this cache. */
    private int _capacity = DEFAULT_CAPACITY;

    /** Current position to check if value can be replaced at put. */
    private int _cur = 0;
    
    /** ReadWriteLock to synchronize access to cache. */
    private final ReentrantReadWriteLock _lock = new ReentrantReadWriteLock();
    
    //--------------------------------------------------------------------------
    // operations for life-cycle management of cache
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void initialize(final Properties params) throws CacheAcquireException {
        super.initialize(params);
        
        String param = params.getProperty(PARAM_CAPACITY);
        try {
            if (param != null) { _capacity = Integer.parseInt(param); }
            if (_capacity <= 0) { _capacity = DEFAULT_CAPACITY; }
        } catch (NumberFormatException ex) {
            _capacity = DEFAULT_CAPACITY;
        }

        _mapKeyPos = new HashMap<K, Integer>(_capacity);
        _keys = (K[]) new Object[_capacity];
        _values = (V[]) new Object[_capacity];
        _status = new int[_capacity];
    }

    //--------------------------------------------------------------------------
    // getters/setters for cache configuration

    /**
     * {@inheritDoc}
     */
    public String getType() { return TYPE; }
    
    /**
     * Get real capacity of this cache.
     * 
     * @return Real capacity of this cache.
     */
    public int getCapacity() { return _capacity; }

    //--------------------------------------------------------------------------
    // query operations of map interface

    /**
     * {@inheritDoc}
     */
    public int size() {
        try {
            _lock.readLock().lock();
            return _mapKeyPos.size();
        } finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        try {
            _lock.readLock().lock();
            return _mapKeyPos.isEmpty();
        } finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsKey(final Object key) {
        try {
            _lock.readLock().lock();
            return _mapKeyPos.containsKey(key);
        } finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsValue(final Object value) {
        try {
            _lock.readLock().lock();
            for (Integer pos : _mapKeyPos.values()) {
                if (pos != null) {
                    if (value == null) {
                        if (_values[pos.intValue()] == null) { return true; }  
                    } else {
                        if (value.equals(_values[pos.intValue()])) { return true; }  
                    }
                }
            }
            return false;
        } finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    public V get(final Object key) {
        try {
            _lock.writeLock().lock();
            Integer pos = _mapKeyPos.get(key);
            if (pos == null) { return null; }
            int intPos = pos.intValue();
            _status[intPos] = LRU_NEW;
            return _values[intPos]; 
        } finally {
            _lock.writeLock().unlock();
        }
    }
    
    //--------------------------------------------------------------------------
    // modification operations of map interface
    
    /**
     * {@inheritDoc}
     */
    public V put(final K key, final V value) {
        try {
            _lock.writeLock().lock();
            Integer pos = _mapKeyPos.get(key);
            if (pos != null) {
                int intPos = pos.intValue();
                V old = _values[intPos];
                _values[intPos] = value;
                _status[intPos] = LRU_NEW;
                return old;
            }
            // skip to first position with LRU_OLD status.
            while (_status[_cur] == LRU_NEW) {
                _status[_cur] = LRU_OLD;
                _cur++;
                if (_cur >= _capacity) { _cur = 0; }
            }
            
            if (_keys[_cur] != null) {
                pos = _mapKeyPos.remove(_keys[_cur]);
            } else {
                pos = new Integer(_cur);
            }
            
            _keys[_cur] = key;
            _values[_cur] = value;
            _status[_cur] = LRU_NEW;
            _mapKeyPos.put(key, pos);
            
            _cur++;
            if (_cur >= _capacity) { _cur = 0; }
            
            return null;
        } finally {
            _lock.writeLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    public V remove(final Object key) {
        try {
            _lock.writeLock().lock();
            Integer pos = _mapKeyPos.remove(key);
            if (pos == null) { return null; }
            int intPos = pos.intValue();
            V old = _values[intPos];
            _keys[intPos] = null;
            _values[intPos] = null;
            _status[intPos] = LRU_OLD;
            return old;
        } finally {
            _lock.writeLock().unlock();
        }
    }
    
    //--------------------------------------------------------------------------
    // bulk operations of map interface
    
    /**
     * {@inheritDoc}
     */
    public void putAll(final Map<? extends K, ? extends V> map) {
        for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        _lock.writeLock().lock();
        _mapKeyPos.clear();
        for (int intPos = 0; intPos < _capacity; intPos++) {
            _keys[intPos] = null;
            _values[intPos] = null;
            _status[intPos] = LRU_OLD;
        }
        _lock.writeLock().unlock();
    }

    //--------------------------------------------------------------------------
    // view operations of map interface
    
    /**
     * {@inheritDoc}
     */
    public Set<K> keySet() {
        try {
            _lock.readLock().lock();
            return Collections.unmodifiableSet(_mapKeyPos.keySet());
        } finally {
            _lock.readLock().unlock();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Collection<V> values() {
        Collection<V> col = new ArrayList<V>();
        _lock.readLock().lock();
        for (Integer pos : _mapKeyPos.values()) {
            if (pos != null) {
                col.add(_values[pos.intValue()]);
            }
        }
        _lock.readLock().unlock();
        return Collections.unmodifiableCollection(col);
    }

    /**
     * {@inheritDoc}
     */
    public Set<Entry<K, V>> entrySet() {
        Map<K, V> map = new HashMap<K, V>();
        _lock.readLock().lock();
        for (Integer pos : _mapKeyPos.values()) {
            if (pos != null) {
                map.put(_keys[pos.intValue()], _values[pos.intValue()]);
            }
        }
        _lock.readLock().unlock();
        return Collections.unmodifiableSet(map.entrySet());
    }

    //--------------------------------------------------------------------------
}
