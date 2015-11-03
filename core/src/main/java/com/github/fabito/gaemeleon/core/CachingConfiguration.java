package com.github.fabito.gaemeleon.core;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class CachingConfiguration extends AbstractConfiguration {

    private final Cache<String, Object> cache;
    private final Configuration delegate;

    public CachingConfiguration(Configuration delegate) {
        this.delegate = Preconditions.checkNotNull(delegate);
        this.cache = CacheBuilder.newBuilder()
                // TODO externalize expiration setting
                // TODO externalize eager loading strategy setting
                .build();
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
            return cache.get(key, new Callable<Object>() {
                @Override
                public Object call()  {
                    return delegate.getProperty(key);
                }
            });
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
