package com.github.fabito.gaemeleon.core;

import java.util.Iterator;
import java.util.logging.Logger;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;

import com.google.appengine.api.utils.SystemProperty;

/**
 * {@link Configuration} decorator which prepends the current version the
 * property's key.
 * 
 * It uses {@link SystemProperty}'s applicationVersion.get() method to fetch the
 * current runtime version. Useful when having different values per version is
 * needed.
 * 
 * @author fabio
 * 
 */
public class AppVersionConfiguration extends AbstractConfiguration {

	public static final String DEFAULT_SEPARATOR = ".";

	private static final Logger LOGGER = Logger
			.getLogger(AppVersionConfiguration.class.getSimpleName());

	private Configuration decorated;

	public AppVersionConfiguration(Configuration delegate) {
		this.decorated = delegate;
	}

	@Override
	public boolean isEmpty() {
		return !getKeys().hasNext();
	}

	@Override
	public boolean containsKey(String key) {
		return decorated.containsKey(key(key));
	}

	@Override
	public Object getProperty(String key) {
		String prefixedKey = key(key);
		LOGGER.finer("Fetching property using prefixed key: " + prefixedKey);
		return decorated.getProperty(prefixedKey);
	}

	private String key(String key) {
		return prefix().concat(key);
	}

	@Override
	public Iterator<String> getKeys() {
		return decorated.getKeys();
	}

	@Override
	protected void addPropertyDirect(String key, Object value) {
		decorated.addProperty(key, value);
	}

	private String prefix() {
		String prefix = version().concat(DEFAULT_SEPARATOR);
		LOGGER.finer("Using prefix: " + prefix);
		return prefix;
	}

	private String version() {
		String version = SystemProperty.applicationVersion.get();
		LOGGER.finer("Current version: " + version);
		int indexOfDot = version.indexOf(DEFAULT_SEPARATOR);
		return version == null ? "" : indexOfDot == -1 ? version : version
				.substring(0, indexOfDot);
	}
	
	@Override
	protected void clearPropertyDirect(String key) {
		decorated.clearProperty(key);
	}

}