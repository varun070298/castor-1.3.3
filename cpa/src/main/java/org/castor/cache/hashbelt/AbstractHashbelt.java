/*
 * Copyright 2005 Gregory Block, Ralf Joachim
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
package org.castor.cache.hashbelt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.cache.AbstractBaseCache;
import org.castor.cache.CacheAcquireException;
import org.castor.cache.hashbelt.container.Container;
import org.castor.cache.hashbelt.container.MapContainer;
import org.castor.cache.hashbelt.reaper.AbstractReaper;
import org.castor.cache.hashbelt.reaper.NullReaper;

/**
 * An abstract, core implementation of the hashbelt functionality; individual
 * implementations will differ on the underlying behavior.
 * <p>
 * A hashbelt has six important values which get set at initialization:
 * <dl>
 * <dt>containers
 * <dd>The number of containers in the conveyor belt. For example: If a box
 * will drop off of the conveyor belt every 30 seconds, and you want a cache
 * that lasts for 5 minutes, you want 5 / 30 = 6 containers on the belt. Every
 * 30 seconds, another, clean container goes on the front of the conveyor belt,
 * and everything in the last belt gets discarded. If not specified 10 containers
 * are used by default.
 * <br/>
 * For systems with fine granularity, you are free to use a large number of
 * containers; but the system is most efficient when the user decides on a
 * "sweet spot" determining both the number of containers to be managed on the
 * whole and the optimal number of buckets in those containers for managing. This
 * is ultimately a performance/accuracy tradeoff with the actual discard-from-cache
 * time being further from the mark as the rotation time goes up. Also the number
 * of objects discarded at once when capacity limit is reached depends upon the
 * number of containers.
 * <dt>capacity
 * <dd>Maximum capacity of the whole cache. If there are, for example, ten
 * containers on the belt and the capacity has been set to 1000, each container
 * will hold a maximum of 1000/10 objects. Therefore if the capacity limit is
 * reached and the last container gets droped from the belt there are up to 100
 * objects discarted at once. By default the capacity is set to 0 which causes
 * capacity limit to be ignored so the cache can hold an undefined number of
 * objects.
 * <dt>ttl
 * <dd>The maximum time an object lifes in cache. If the are, for example, ten
 * containers and ttl is set to 300 seconds (5 minutes), a new container will be
 * put in front of the belt every 300/10 = 30 seconds while another is dropped at
 * the end at the same time. Due to the granularity of 30 seconds, everything just
 * until 5 minutes 30 seconds will also end up in this box. The default value for
 * ttl is 60 seconds. If ttl is set to 0 which means that objects life in cache
 * for unlimited time and may only discarded by a capacity limit.
 * <dt>monitor
 * <dd>The monitor intervall in minutes when hashbelt cache rports the current
 * number of containers used and objects cached. If set to 0 (default) monitoring
 * is disabled.
 * <dt>container-class
 * <dd>The implementation of <b>org.castor.cache.hashbelt.container.Container</b>
 * interface to be used for all containers of the cache. Castor provides the following
 * 3 implementations of the Container interface.<br/>
 * org.castor.cache.hashbelt.container.FastIteratingContainer<br/>
 * org.castor.cache.hashbelt.container.MapContainer<br/>
 * org.castor.cache.hashbelt.container.WeakReferenceContainer<br/>
 * If not specified the MapContainer will be used as default.
 * <dt>reaper-class
 * <dd>Specific reapers yield different behaviors. The GC reaper, the default,
 * just dumps the contents to the garbage collector. However, custom
 * implementations may want to actually do something when a bucket drops off the
 * end; see the javadocs on other available reapers to find a reaper strategy
 * that meets your behavior requirements. Apart of the default
 * <b>org.castor.cache.hashbelt.reaper.NullReaper</b> we provide 3 abstract
 * implementations of <b>org.castor.cahe.hashbelt.reaper.Reaper</b> interface:<br/>
 * org.castor.cache.hashbelt.reaper.NotifyingReaper<br/>
 * org.castor.cache.hashbelt.reaper.RefreshingReaper<br/>
 * org.castor.cache.hashbelt.reaper.ReinsertingReaper<br/>
 * to be extended by your custom implementation.
 * </dl>
 * 
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of cached values
 * 
 * @author <a href="mailto:gblock AT ctoforaday DOT com">Gregory Block</a>
 * @author <a href="mailto:ralf DOT joachim AT syscon DOT eu">Ralf Joachim</a>
 * @version $Revision: 9041 $ $Date: 2011-08-16 11:51:17 +0200 (Di, 16 Aug 2011) $
 * @since 1.0
 */
public abstract class AbstractHashbelt<K, V> extends AbstractBaseCache<K, V> {
    //--------------------------------------------------------------------------

    /** The <a href="http://jakarta.apache.org/commons/logging/">Jakarta
     *  Commons Logging</a> instance used for all logging. */
    private static final Log LOG = LogFactory.getLog(AbstractHashbelt.class);
    
    /** Mapped initialization parameter <code>containers</code>. */
    public static final String PARAM_CONTAINERS = "containers";

    /** Mapped initialization parameter <code>container-class</code>. */
    public static final String PARAM_CONTAINER_CLASS = "container-class";

    /** Mapped initialization parameter <code>reaper-class</code>. */
    public static final String PARAM_REAPER_CLASS = "reaper-class";

    /** Mapped initialization parameter <code>capacity</code>. */
    public static final String PARAM_CAPACITY = "capacity";

    /** Mapped initialization parameter <code>ttl</code>. */
    public static final String PARAM_TTL = "ttl";

    /** Mapped initialization parameter <code>monitor</code>. */
    public static final String PARAM_MONITOR = "monitor";

    /** Default number of containers for cache. */
    public static final int DEFAULT_CONTAINERS = 10;
    
    /** Default container class. */
    @SuppressWarnings("rawtypes")
    public static final Class<? extends Container> DEFAULT_CONTAINER_CLASS = MapContainer.class;
    
    /** Default reaper class. */
    @SuppressWarnings("rawtypes")
    public static final Class<? extends AbstractReaper> DEFAULT_REAPER_CLASS = NullReaper.class;
    
    /** Default capacity of cache. */
    public static final int DEFAULT_CAPACITY = 0;
    
    /** Default ttl of cache in seconds. */
    public static final int DEFAULT_TTL = 60;
    
    /** Default monitor interval of cache in minutes. */
    public static final int DEFAULT_MONITOR = 0;
    
    /** Milliseconds per second. */
    private static final long ONE_SECOND = 1000;
    
    /** Milliseconds per minute. */
    private static final long ONE_MINUTE = 60 * ONE_SECOND;
    
    //--------------------------------------------------------------------------
    
    /** ReadWriteLock to synchronize access to cache. */
    private final ReentrantReadWriteLock _lock = new ReentrantReadWriteLock();
    
    /** The internal array of containers building the cache. */
    private ArrayList<Container<K, V>> _cache = new ArrayList<Container<K, V>>();
    
    /** The internal array of empty conatiners to be used for cache on demand. */
    private ArrayList<Container<K, V>> _pool = new ArrayList<Container<K, V>>();
    
    /** Number of containers currently available in pool. */
    private int _poolCount;
    
    /** Real capacity limit of this cache. If set to Integer.MAX_VALUE the capacity
     *  of the cache is not restricted. The capacity needs to be greater then twice
     *  the number of containers. */
    private int _cacheCapacity;

    /** Approximat number of entries in this cache. */
    private int _cacheSize = 0;

    /** Target number of containers for this cache. The real number of containers
     *  may vary between 0 and twice the target number. */
    private int _containerTarget;

    /** Real number of containers in the cache. */
    private int _containerCount = 0;
    
    /** Capacity limit of a container. */
    private int _containerCapacity;

    /** Real ttl of the entries in this cache. */
    private int _ttl;
    
    /** The reaper to pass all expired containers to. */
    private AbstractReaper<K, V> _reaper;

    /** Real monitor interval. */
    private int _monitor;

    /** Timer to expire containers and the objects they contain after ttl. */
    private Timer _expirationTimer;

    /** Timer to monitor container count and cache size every monitor interval. */
    private Timer _monitoringTimer;

    //--------------------------------------------------------------------------
    // operations for life-cycle management of cache
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public final void initialize(final Properties params)
    throws CacheAcquireException {
        super.initialize(params);
        
        String param;
        
        try {
            param = params.getProperty(PARAM_CONTAINERS);
            if (param != null) { _containerTarget = Integer.parseInt(param); }
            if (_containerTarget <= 0) { _containerTarget = DEFAULT_CONTAINERS; }
        } catch (NumberFormatException ex) {
            _containerTarget = DEFAULT_CONTAINERS;
        }

        try {
            Class<? extends  Container> cls = DEFAULT_CONTAINER_CLASS;
            param = params.getProperty(PARAM_CONTAINER_CLASS);
            if ((param != null) && !"".equals(param)) {
                cls = (Class<? extends Container>) Class.forName(param);
            }

            _poolCount = 2 * _containerTarget;
            for (int i = 0; i < _poolCount; i++) {
                _pool.add(cls.newInstance());
            }
        } catch (Exception ex) {
            String msg = "Failed to instantiate hashbelt container.";
            throw new CacheAcquireException(msg, ex);
        }

        try {
            Class<? extends AbstractReaper> cls = DEFAULT_REAPER_CLASS;
            param = params.getProperty(PARAM_REAPER_CLASS);
            if ((param != null) && !"".equals(param)) {
                cls = (Class<? extends AbstractReaper>) Class.forName(param);
            }

            _reaper = cls.newInstance();
            _reaper.setCache(this);
        } catch (Exception ex) {
            String msg = "Failed to instantiate hashbelt reaper.";
            throw new CacheAcquireException(msg, ex);
        }

        try {
            param = params.getProperty(PARAM_CAPACITY);
            if (param != null) { _cacheCapacity = Integer.parseInt(param); }
            if (_cacheCapacity < 0) { _cacheCapacity = DEFAULT_CAPACITY; }
        } catch (NumberFormatException ex) {
            _cacheCapacity = DEFAULT_CAPACITY;
        }
        int minCapacity = 2 * _containerTarget;
        if ((_cacheCapacity > 0) && (_cacheCapacity < minCapacity)) {
            _cacheCapacity = minCapacity;
        }

        _containerCapacity = _cacheCapacity / _containerTarget;
        
        try {
            param = params.getProperty(PARAM_TTL);
            if (param != null) { _ttl = Integer.parseInt(param); }
            if (_ttl < 0) { _ttl = DEFAULT_TTL; }
        } catch (NumberFormatException ex) {
            _ttl = DEFAULT_TTL;
        }

        if (_ttl > 0) {
            long periode = (_ttl * ONE_SECOND) / _containerTarget;
            _expirationTimer = new Timer(true);
            _expirationTimer.schedule(new ExpirationTask<K, V>(this), periode, periode);
        }

        try {
            param = params.getProperty(PARAM_MONITOR);
            if (param != null) { _monitor = Integer.parseInt(param); }
            if (_monitor < 0) { _monitor = DEFAULT_MONITOR; }
        } catch (NumberFormatException ex) {
            _monitor = DEFAULT_MONITOR;
        }

        if (_monitor > 0) {
            long periode = _monitor * ONE_MINUTE;
            _monitoringTimer = new Timer(true);
            _monitoringTimer.schedule(new MonitoringTask<K, V>(this), periode, periode);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final void close() {
        if (_monitoringTimer != null) {
            _monitoringTimer.cancel();
            _monitoringTimer = null;
        }
        
        _monitor = 0;
        
        if (_expirationTimer != null) {
            _expirationTimer.cancel();
            _expirationTimer = null;
        }
        
        _ttl = 0;

        clear();

        _containerCapacity = 0;
        _cacheCapacity = 0;
        _reaper = null;
        _poolCount = 0;
        _pool = null;
        _containerTarget = 0;
        
        super.close();
    }

    //--------------------------------------------------------------------------
    // getters/setters for cache configuration

    /**
     * Get real capacity of this cache.
     * 
     * @return Real capacity of this cache.
     */
    public final int getCapacity() { return _cacheCapacity; }

    /**
     * Get real ttl of this cache.
     * 
     * @return Real ttl of this cache.
     */
    public final int getTTL() { return _ttl; }

    //--------------------------------------------------------------------------
    // query operations of map interface

    /**
     * {@inheritDoc}
     */
    public final int size() {
        try {
            _lock.readLock().lock();
            return _cacheSize;
        } finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    public final boolean isEmpty() {
        try {
            _lock.readLock().lock();
            return (_cacheSize == 0);
        } finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    public final boolean containsKey(final Object key) {
        if (key == null) { throw new NullPointerException("key"); }
      
        _lock.readLock().lock();
        try {
            for (int i = 0; i < _containerCount; i++) {
                if (_cache.get(i).containsKey(key)) { 
                    return true; 
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
    public final boolean containsValue(final Object value) {
        if (value == null) { throw new NullPointerException("value"); }

        _lock.readLock().lock();
        try {
            for (int i = 0; i < _containerCount; i++) {
                if (_cache.get(i).containsValue(value)) { 
                    return true; 
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
    public final void clear() {
        _lock.writeLock().lock();
        while (_containerCount > 0) { 
            expireCacheContainer(); 
        }
        _lock.writeLock().unlock();
    }

    //--------------------------------------------------------------------------
    // view operations of map interface
    
    /**
     * {@inheritDoc}
     */
    public final Set<K> keySet() {
        _lock.readLock().lock();
        try {
            Set<K> set = new HashSet<K>(_cacheSize);
            for (int i = 0; i < _containerCount; i++) {
                set.addAll(_cache.get(i).keySet());
            }
            return set;
        } finally {
            _lock.readLock().unlock();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final Collection<V> values() {
        _lock.readLock().lock();
        try {
            Collection<V> col = new ArrayList<V>(_cacheSize);
            for (int i = 0; i < _containerCount; i++) {
                col.addAll(_cache.get(i).values());
            }
            return col;
        } finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    public final Set<Entry<K, V>> entrySet() {
        _lock.readLock().lock();
        try {
            Map<K, V> map = new HashMap<K, V>(_cacheSize);
            for (int i = 0; i < _containerCount; i++) {
                map.putAll(_cache.get(i));
            }
            return map.entrySet();
        } finally {
            _lock.readLock().unlock();
        }
    }

    //--------------------------------------------------------------------------
    // protected methods for concrete implementations
    
    /**
     * Get reference to the ReadWriteLock of this cache instance.
     * 
     * @return ReadWriteLock to synchronize access to cache.
     */
    protected final ReentrantReadWriteLock lock() { return _lock; }
    
    /**
     * Get object currently associated with given key from cache. Take care to acquire a
     * read or write lock before calling this method and release the lock thereafter.
     * 
     * @param key The key to return the associated object for.
     * @return The object associated with given key.
     */
    protected final V getObjectFromCache(final Object key) {
        V result;
        for (int i = 0; i < _containerCount; i++) {
            result = _cache.get(i).get(key);
            if (result != null) { return result; }
        }
        return null;
    }
    
    /**
     * Put given value with given key in cache. Return the object previously associated
     * with key. Take care to acquire a write lock before calling this method and release
     * the lock thereafter.
     * 
     * @param key The key to associate the given value with.
     * @param value The value to associate with given key.
     * @return The object previously associated with given key. <code>null</code> will
     *         be returned if no value has been associated with key.
     */
    protected final V putObjectIntoCache(final K key, final V value) {
        // We first check if a new container have to be created. This is the case
        // if there is none or if we have a capacity limit and the head container
        // holds the maximum allowed number of entries.
        if ((_containerCount == 0) || ((_cacheCapacity > 0)
                && (_cache.get(0).size() >= _containerCapacity))) {
            addCacheContainer();
        }
        
        // Then we can put the new or updated one to the head of the belt.
        V result = _cache.get(0).put(key, value);
        
        if (result != null) { return result; }

        // If result is null we have to search the other containers of the
        // cache if they contain an entry for the key, in which case we have
        // to remove that old entry.
        for (int i = 1; (i < _containerCount) && (result == null); i++) {
            result = _cache.get(i).remove(key);
        }
        
        if (result != null) { return null; }
        
        // If result still is null we have added a new entry and need to
        // increment size of the whole cache. As size has increased we also
        // need to check if size exceeds capacity limit, in which case we
        // have to expire the oldest container of the cache.
        _cacheSize++;

        if (_cacheCapacity > 0) {
            while (_cacheCapacity < _cacheSize) {
                expireCacheContainer();
            }
        }
        
        return result;
    }
    
    /**
     * Remove any available association for given key. Take care to acquire a write lock
     * before calling this method and release the lock thereafter.
     * 
     * @param key The key to remove any previously associate value for.
     * @return The object previously associated with given key. <code>null</code> will
     *         be returned if no value has been associated with key.
     */
    protected final V removeObjectFromCache(final Object key) {
        for (int i = 0; i < _containerCount; i++) {
            V result = _cache.get(i).remove(key);
            if (result != null) {
                _cacheSize--;
                return result;
            }
        }
        return null;
    }
    
    //--------------------------------------------------------------------------
    // private helper methods
    
    /**
     * Recalculate the number of entries in the cache by summing the size of all
     * containers.
     */
    private void recalcCacheSize() {
        int size = 0; 
        for (int i = 0; i < _containerCount; i++) {
            size += _cache.get(i).size();
        }
        _cacheSize = size;
    }
    
    /**
     * Add an empty container from the pool to the cache.
     */
    private void addCacheContainer() {
        Container<K, V> temp = _pool.get(--_poolCount);
        _cache.add(0, temp);
        _containerCount++;
    }
    
    /**
     * Remove the oldest container from the cache and pass it to the configured reaper
     * to do its expiration work. Then clear the container and put it back into the
     * pool for further use.
     */
    private void expireCacheContainer() {
        Container<K, V> expired = _cache.get(--_containerCount);

        _cache.remove(expired);
        _cacheSize -= expired.size();
        
        _reaper.handleExpiredContainer(expired);
        
        expired.clear();
        
        _pool.set(_poolCount++, expired);
    }
    
    /**
     * Check the containers of the cache if their ttl has been expired. If the ttl of
     * the oldest container has expired the expireCacheContainer() method is called to
     * remove it. After removing it, the next container will be checked until one is
     * found thats ttl has not expired.
     */
    private void timeoutCacheContainers() {
        long timeout = System.currentTimeMillis() - _ttl;
        while ((_containerCount > 0)
                && (_cache.get(_containerCount - 1).getTimestamp() <= timeout)) {
            expireCacheContainer();
        }
    }
    
    //--------------------------------------------------------------------------
    // private helper classes

    /**
     * TimerTask that checks if ttl of containers in the cache has expired. If some
     * of them have expired they are removed. One new container will be added to
     * the head of the cache. In addition the size of the cache is recalculated. This
     * is not reqired for containers that hold strong references to their objects
     * but for those containers holding soft or weak references its the only way to
     * adjust the size with regard to the objects that have been garbage collected.
     * <p>
     * The interval this task will be executed is set to the ttl divided by the
     * number of containers in the cache. If ttl has been configured to be 0 the
     * ExpirationTask will never be executed.
     */
    private static class ExpirationTask<K, V> extends TimerTask {
        /** Reference to the hashbelt this ExpirationTask belongs to. */
        private AbstractHashbelt<K, V> _owner;
        
        /**
         * Construct a new ExpirationTask for the given hashbelt.
         * 
         * @param owner The hashbelt this ExpirationTask belongs to.
         */
        public ExpirationTask(final AbstractHashbelt<K, V> owner) { _owner = owner; }
        
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            try {
                _owner._lock.writeLock().lockInterruptibly();
                
                _owner.timeoutCacheContainers();
                _owner.addCacheContainer();
                _owner.recalcCacheSize();
            } catch (ThreadDeath t) {
                LOG.debug("Stopping expiration thread: " + _owner.getName());
                throw t;
            } catch (Throwable t) {
                LOG.error("Caught exception during expiration: " + _owner.getName(), t);
                if (t instanceof VirtualMachineError) { throw (VirtualMachineError) t; }
            } finally {
                _owner._lock.writeLock().unlock();
            }
        }
    }
    
    /**
     * TimerTask that logs the number of containers and objects in the cache at the
     * interval configured by the monitor parameter. If monitor parameter has been
     * set to 0 the MonitoringTask will never be executed.
     */
    private static class MonitoringTask<K, V> extends TimerTask {
        /** Reference to the hashbelt this MonitoringTask belongs to. */
        private AbstractHashbelt<K, V> _owner;
        
        /**
         * Construct a new MonitoringTask for the given hashbelt.
         * 
         * @param owner The hashbelt this MonitoringTask belongs to.
         */
        public MonitoringTask(final AbstractHashbelt<K, V> owner) { _owner = owner; }
        
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            try {
                _owner._lock.readLock().lockInterruptibly();
                
                LOG.info("Cache '" + _owner.getName() + "' "
                       + "currently holds " + _owner._containerCount + " containers "
                       + "with " + _owner._cacheSize + " objects.");
            } catch (ThreadDeath t) {
                LOG.debug("Stopping monitoring thread: " + _owner.getName());
                throw t;
            } catch (Throwable t) {
                LOG.error("Caught exception during monitoring: " + _owner.getName(), t);
                if (t instanceof VirtualMachineError) { throw (VirtualMachineError) t; }
            } finally {
                _owner._lock.readLock().unlock();
            }
        }
    }
    
    //--------------------------------------------------------------------------
}
