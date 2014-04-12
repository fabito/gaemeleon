package org.github.fabito.gaemeleon.core;

import java.util.Iterator;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;

import com.google.appengine.api.NamespaceManager;

/**
 * {@link Configuration} decorator which leverages {@link NamespaceManager} to enforce a specific namespace. 
 *
 * By default the default namespace is enforced.
 * Useful for multi tenant application which needs configurations per tenant. 
 *  
 * @author fabio
  */
public class NamespaceConfiguration extends AbstractConfiguration {

	private Configuration decorated;
	private String namespace = null;

	public NamespaceConfiguration(Configuration delegate) {
		this.decorated = delegate;
	}

	public NamespaceConfiguration(Configuration delegate, String namespace) {
		this.decorated = delegate;
		this.namespace = namespace;
	}

	@Override
	public boolean isEmpty() {
		return withinNamespace(namespace, new Work<Boolean> () {
			@Override
			public Boolean run() {
				return decorated.isEmpty();
			}
		});	}

	@Override
	public boolean containsKey(final String key) {
		return withinNamespace(namespace, new Work<Boolean> () {
			@Override
			public Boolean run() {
				return decorated.containsKey(key);
			}
		});
	}

	@Override
	public Object getProperty(final String key) {
		return withinNamespace(namespace, new Work<Object> () {
			@Override
			public Object run() {
				return decorated.getProperty(key);
			}
		});
	}

	@Override
	public Iterator<String> getKeys() {
		return withinNamespace(namespace, new Work<Iterator<String>> () {
			@Override
			public Iterator<String> run() {
				return decorated.getKeys();
			}
		});		
	}

	@Override
	protected void addPropertyDirect(final String key, final Object value) {
		withinNamespace(namespace, new VoidWork() {
			@Override
			void vrun() {
				decorated.addProperty(key, value);
			}
		});
	}
	
	static interface Work<T> {
		public T run();
	}
	
	static abstract class VoidWork implements Work<Void> {
		@Override
		public Void run() {
			vrun();
			return null;
		}
		abstract void vrun();
	}
 	
	public static <R> R withinNamespace(String namespace, Work<R> work) {
		String oldNamespace = NamespaceManager.get();
		NamespaceManager.set(namespace);
		try {
			R r = work.run();
			return r;
		} finally {
			NamespaceManager.set(oldNamespace);
		}
	}

}
