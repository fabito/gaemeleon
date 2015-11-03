package com.github.fabito.gaemeleon.core;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CachingConfiguration extends AbstractConfiguration {

    private final LoadingCache<String, Object> cache;
    private final Configuration delegate;

    public CachingConfiguration(final Configuration delegate) {
        this.delegate = Preconditions.checkNotNull(delegate);
        this.cache = CacheBuilder.newBuilder()
                // TODO externalize expiration setting
                .expireAfterAccess(10, TimeUnit.MINUTES)
                // TODO externalize eager loading strategy setting
                .build(new CacheLoader<String, Object>() {

                    public Object load(String key) { // no checked exception
                        return delegate.getProperty(key);
                    }

                    @Override
                    public Map<String, Object> loadAll(Iterable<? extends String> keys) throws Exception {
                        return super.loadAll(keys);
                    }

                });
    }

    @Override
    protected void addPropertyDirect(String key, Object value) {

    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(String key) {
        return delegate.containsKey(key);
    }

    @Override
    public Object getProperty(final String key) {
        try {
            return cache.get(key);
        } catch (ExecutionException e) {
            throw new ConfigurationRuntimeException(e);
        }
    }

    @Override
    public Iterator<String> getKeys() {
        return delegate.getKeys();
    }

    @VisibleForTesting
    Cache<String, Object> getCache() {
        return this.cache;
    }

}
