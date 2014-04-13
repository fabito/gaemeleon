package com.github.fabito.gaemeleon.core;

import static com.github.fabito.gaemeleon.core.NamespaceConfiguration.withinNamespace;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.fabito.gaemeleon.core.AppVersionConfiguration;
import com.github.fabito.gaemeleon.core.DatastoreConfiguration;
import com.github.fabito.gaemeleon.core.MemcacheConfiguration;
import com.github.fabito.gaemeleon.core.NamespaceConfiguration;
import com.github.fabito.gaemeleon.core.NamespaceConfiguration.VoidWork;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.ImmutableMap;

public class MultitenantConfigurationTest {

	private static final String VERSION = "1-0-69";

	private static final String NAMESPACE = "ciandt.com";

	protected Map<String, String> initialConfiguration = ImmutableMap
			.<String, String> of(
					prefixWithVersion("p1"), "version_ns_default",
					prefixWithVersion("p8"), "version_ns_default8"
					);
	
	protected Map<String, String> initialConfiguration2 = ImmutableMap
			.<String, String> of(
					"p1", "ns_default",
					"p2", "ns_default2",
					"p7", "ns_default7"
					);
	
	protected Map<String, String> initialConfiguration3 = ImmutableMap
			.<String, String> of(
					prefixWithVersion("p1"), "version_tenant", 
					prefixWithVersion("p2"), "version_tenant2",
					prefixWithVersion("p3"), "version_tenant3",
					prefixWithVersion("p6"), "version_tenant6"
					);
	
	protected Map<String, String> initialConfiguration4 = ImmutableMap
			.<String, String> of(
					"p1", "tenant", 
					"p2", "tenant2",
					"p3", "tenant3",
					"p4", "tenant4",
					"p5", "tenant5"
					);
	
	
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
				new LocalDatastoreServiceTestConfig(), new LocalMemcacheServiceTestConfig());
	
	@Before
	public void setup() {
		helper.setUp();
        SystemProperty.applicationVersion.set(VERSION);
        addConfigurations(initialConfiguration);
        addConfigurations(initialConfiguration2);
		withinNamespace(NAMESPACE, new VoidWork() {
			@Override
			void vrun() {
				addConfigurations(initialConfiguration3);
				addConfigurations(initialConfiguration4);
			}
		});
	}

	private String prefixWithVersion(String string) {
		return VERSION + "." + string;
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}
	
	Configuration cfg = cfg();

	@Test
	public void write() {
		cfg.addProperty("ptestd", "ptestvalued");
		withinNamespace(NAMESPACE, new VoidWork() {
			@Override
			void vrun() {
				cfg.addProperty("ptest", "ptestvalue");
				assertThat(cfg.getString("ptest"), is("ptestvalue"));
				assertThat(cfg.getString("ptestd"), is("ptestvalued"));
			}
		});
	}
	
	@Test
	public void read() {
		withinNamespace(NAMESPACE, new VoidWork() {
			@Override
			void vrun() {
				assertThat(cfg.getString("p1"), is("version_ns_default"));
				assertThat(cfg.getString("p2"), is("ns_default2"));
				assertThat(cfg.getString("p3"), is("version_tenant3"));
				assertThat(cfg.getString("p4"), is("tenant4"));
				assertThat(cfg.getString("p5"), is("tenant5"));
				assertThat(cfg.getString("p6"), is("version_tenant6"));
				assertThat(cfg.getString("p7"), is("ns_default7"));
				assertThat(cfg.getString("p8"), is("version_ns_default8"));
			}
		});
	}


	private Configuration cfg() {
		CompositeConfiguration config = new CompositeConfiguration();
		config.addConfiguration(new AppVersionConfiguration(
				new NamespaceConfiguration(ds())));
		config.addConfiguration(new NamespaceConfiguration(ds()));
		config.addConfiguration(new AppVersionConfiguration(ds()));
		config.addConfiguration(ds());
		return new MemcacheConfiguration(config, MemcacheServiceFactory.getMemcacheService());
	}

	public Configuration ds() {
		return new DatastoreConfiguration(
				datastoreService());
	}

	private DatastoreService datastoreService() {
		return DatastoreServiceFactory.getDatastoreService();
	}
	
	protected void addConfigurations(Map<String, String> props) {
		for (Entry<String, String> e : props.entrySet()) {
			addConfiguration(e.getKey(), e.getValue());
		}
	}

	protected void addConfiguration(String key, String value) {
		Entity configurationEntity = new Entity(
				DatastoreConfiguration.DEFAULT_ENTITY_KIND, key);
		configurationEntity.setUnindexedProperty(
				DatastoreConfiguration.DEFAULT_PROPERTY_VALUE, value);
		datastoreService().put(configurationEntity);
	}
	
	protected void deleteConfigurations() {
		Query q = new Query(DatastoreConfiguration.DEFAULT_ENTITY_KIND).setKeysOnly();
		PreparedQuery pq = datastoreService().prepare(q);
		for (Entity result : pq.asIterable()) {
			Key key = result.getKey();
			datastoreService().delete(key);
		} 
	}

}
