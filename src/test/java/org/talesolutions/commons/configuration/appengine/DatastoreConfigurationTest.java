package org.talesolutions.commons.configuration.appengine;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talesolutions.commons.configuration.appengine.DatastoreConfiguration;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.ImmutableMap;

public class DatastoreConfigurationTest {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());

	private DatastoreConfiguration config;
	private DatastoreService datastoreService;

	private Map<String, String> initialConfiguration = ImmutableMap
			.<String, String> of("p1", "v1", "p2", "v2", "p3", "v3", "p4", "v4");

	@Before
	public void setup() {
		helper.setUp();
		datastoreService = DatastoreServiceFactory.getDatastoreService();
		addConfigurations(initialConfiguration);
		config = new DatastoreConfiguration(datastoreService);
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Test
	public void existentPropertyShouldReturnRightValue() {
		assertThat(config.getString("p1"), is("v1"));
		assertThat(config.getString("p2"), is("v2"));
		assertThat(config.getString("p3"), is("v3"));
	}

	@Test
	public void nonExistentPropertyShouldReturnNull() {
		assertThat(config.getString("p10"), nullValue());
	}

	@Test
	public void getKeysShouldReturnRightValue() {
		for (Iterator<String> it1 = config.getKeys(), it2 = initialConfiguration
				.keySet().iterator(); it1.hasNext() && it2.hasNext();) {
			assertThat(it1.next(), is(it2.next()));
		}
	}

	@Test
	public void isEmptyShouldReturnFalse() {
		assertThat(config.isEmpty(), is(false));
	}

	@Test
	public void addPropertyDirect() {
		String key = "ppp";
		String value = "aaa";
		assertThat(config.getString(key), nullValue());
		config.addPropertyDirect(key, value);
		assertThat(config.getString(key), is(value));
	}

	// @Test
	public void isEmptyShouldReturnTrue() {
		assertThat(config.isEmpty(), is(true));
	}

	@Test
	public void containsKey() {
		assertThat(config.containsKey("p1"), is(true));
	}

	@Test
	public void containsKeyShouldReturnFalse() {
		assertThat(config.containsKey("53454353"), is(false));
	}

	private void addConfigurations(Map<String, String> props) {
		for (Entry<String, String> e : props.entrySet()) {
			addConfiguration(e.getKey(), e.getValue());
		}
	}

	private void addConfiguration(String key, String value) {
		Entity configurationEntity = new Entity(
				DatastoreConfiguration.DEFAULT_ENTITY_KIND, key);
		configurationEntity.setUnindexedProperty(
				DatastoreConfiguration.DEFAULT_PROPERTY_VALUE, value);
		datastoreService.put(configurationEntity);
	}

}
