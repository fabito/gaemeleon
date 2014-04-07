package org.github.fabito.commons.configuration.appengine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;

import com.google.appengine.api.utils.SystemProperty;

public class AppVersionConfiguration extends AbstractConfiguration {

	public static final String DEFAULT_SEPARATOR = ".";
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
		return decorated.getProperty(key(key));
	}

	private String key(String key) {
		return version().concat(key);
	}

	@Override
	public Iterator<String> getKeys() {
		List<String> keys = new ArrayList<>();
		for (Iterator<String> iterator = decorated.getKeys(); iterator.hasNext();) {
			String k = iterator.next();
			if (k.startsWith(version())) {
				keys.add(k);
			}
		}	
		return keys.iterator();
	}

	@Override
	protected void addPropertyDirect(String key, Object value) {
		decorated.addProperty(key(key), value);
	}
	
	private String version() {
        return SystemProperty.applicationVersion.get().concat(DEFAULT_SEPARATOR);
	}

}
