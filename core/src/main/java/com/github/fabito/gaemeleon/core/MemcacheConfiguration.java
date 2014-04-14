package com.github.fabito.gaemeleon.core;

import java.util.Iterator;
import java.util.logging.Logger;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;

import com.google.appengine.api.memcache.MemcacheService;

/**
 * {@link Configuration} implementation which is actually just a decorator
 * Could (and should) be used to decorate the {@link DatastoreConfiguration} to avoid unnecessary hits to the datastore.
 * 
 * @author fabio
 *
 */
public class MemcacheConfiguration extends AbstractConfiguration {

	private static final Logger LOGGER = Logger.getLogger(MemcacheConfiguration.class.getSimpleName());
	
	private Configuration delegate;
	private MemcacheService memcacheService;
	private final static String DEFAULT_MEMCACHE_KEY_PREFIX = MemcacheConfiguration.class.getName() + "#";
	private String memcacheKeyPrefix;

	public MemcacheConfiguration(final Configuration delegate, final MemcacheService memcacheService) {
		this(delegate, memcacheService, DEFAULT_MEMCACHE_KEY_PREFIX);
	}

	public MemcacheConfiguration(final Configuration delegate, final MemcacheService memcacheService, final String memcacheKeyPrefix) {
		super();
		this.delegate = delegate;
		this.memcacheService = memcacheService;
		this.memcacheKeyPrefix = memcacheKeyPrefix;
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public boolean containsKey(final String key) {
		if ( !memcacheService.contains(cacheKey(key)) ) {
			return delegate.containsKey(key);
		}
		return true;
	}

	protected String cacheKey(final String key) {
		return memcacheKeyPrefix + key;
	}

	@Override
	public Object getProperty(final String key) {
		LOGGER.finer("Fetching property from memcache: " + key);
		Object propertyValue = memcacheService.get(cacheKey(key));
		if ( propertyValue == null ) {
			LOGGER.finer("Property not found in memcache: " + key + ". Delegating...");
			propertyValue = delegate.getProperty(key);
			if ( propertyValue != null ) {
				memcacheService.put(cacheKey(key), propertyValue);
			}
		}
		return propertyValue;
	}

	@Override
	protected void addPropertyDirect(final String key, final Object value) {
		delegate.addProperty(key, value);
	}

	@Override
	public Iterator<String> getKeys() {
		return delegate.getKeys();
	}

}