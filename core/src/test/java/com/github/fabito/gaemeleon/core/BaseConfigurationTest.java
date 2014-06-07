package com.github.fabito.gaemeleon.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.fabito.gaemeleon.core.DatastoreConfiguration;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.repackaged.com.google.common.base.Strings;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.ImmutableMap;

public abstract class BaseConfigurationTest<T extends Configuration> {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
				new LocalDatastoreServiceTestConfig());
	
	protected T configuration;
	protected DatastoreService datastoreService;
	protected Map<String, String> initialConfiguration = ImmutableMap
				.<String, String> of(
						"p1", "v1", 
						"p2", "v2", 
						"p3", "v3", 
						"p4", "v4",
						"p5", Strings.repeat("v", DatastoreConfiguration.STRING_MAX_LENGTH  + 1)
						);

	public BaseConfigurationTest() {
		super();
	}

	@Before
	public void setup() {
		helper.setUp();
		datastoreService = DatastoreServiceFactory.getDatastoreService();
		addConfigurations(initialConfiguration);
		configuration = configuration();
	}

	abstract T configuration();

	@After
	public void tearDown() {
		helper.tearDown();
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
				DatastoreConfiguration.DEFAULT_PROPERTY_VALUE, val(value));
		
		
		datastoreService.put(configurationEntity);
	}
	
	private Object val(String value) {
		if (value.length() > 500) {
			return new Text(value);
		}
		return value;
	}

	@Test
	public void nonExistentPropertyShouldReturnNull() {
		assertThat(configuration.getString("p10"), nullValue());
	}

	@Test
	public void getKeysShouldReturnRightValue() {
		for (Iterator<String> it1 = configuration.getKeys(), it2 = initialValues()
				.keySet().iterator(); it1.hasNext() && it2.hasNext();) {
			assertThat(it1.next(), is(it2.next()));
		}
	}

	abstract Map<String, String> initialValues();

	@Test
	public void isEmptyShouldReturnFalse() {
		assertThat(configuration.isEmpty(), is(false));
	}

	protected void removeAll() {
		Query q = new Query(DatastoreConfiguration.DEFAULT_ENTITY_KIND).setKeysOnly();
		PreparedQuery pq = datastoreService.prepare(q);
		for (Entity result : pq.asIterable()) {
			Key key = result.getKey();
			System.out.println(key.getNamespace());
			datastoreService.delete(key);
		} 
	}

}