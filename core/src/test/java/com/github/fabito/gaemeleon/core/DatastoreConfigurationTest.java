package com.github.fabito.gaemeleon.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.Test;

import com.github.fabito.gaemeleon.core.DatastoreConfiguration;
import com.google.appengine.repackaged.com.google.common.base.Strings;

public class DatastoreConfigurationTest extends BaseConfigurationTest<DatastoreConfiguration> {

	protected DatastoreConfiguration configuration() {
		return new DatastoreConfiguration(datastoreService);
	}

	@Test
	public void existentLongPropertyShouldReturnRightValue() {
		assertThat(configuration.getString("p5"), is(Strings.repeat("v", DatastoreConfiguration.STRING_MAX_LENGTH + 1)));
	}
	
	
	@Test
	public void existentPropertyShouldReturnRightValue() {
		assertThat(configuration.getString("p1"), is("v1"));
		assertThat(configuration.getString("p2"), is("v2"));
		assertThat(configuration.getString("p3"), is("v3"));
	}

	@Test
	public void addPropertyDirect() {
		String key = "ppp";
		String value = "aaa";
		assertThat(configuration.getString(key), nullValue());
		configuration.addPropertyDirect(key, value);
		assertThat(configuration.getString(key), is(value));
	}

	@Test
	public void isEmptyShouldReturnTrue() {
		removeAll();
		assertThat(configuration.isEmpty(), is(true));
	}

	@Test
	public void containsKey() {
		assertThat(configuration.containsKey("p1"), is(true));
	}
	
	@Test
	public void clearProperty() {
		assertThat(configuration.getString("p1"), is("v1"));
		configuration.clearProperty("p1");
		assertThat(configuration.getString("p1"), nullValue());
	}
	
	@Test(expected=NoSuchElementException.class)
	public void getPropertyWithThrowExceptionOnMissingFlagEnabled() {
		configuration.setThrowExceptionOnMissing(true);
		configuration.getString("NONONONON");
	}

	@Test
	public void getStringWithThrowExceptionOnMissingFlagEnabled() {
		configuration.setThrowExceptionOnMissing(true);
		assertThat(configuration.getString("NONONONON", "teste"), is("teste"));
	}
	
	@Override
	Map<String, String> initialValues() {
		return initialConfiguration;
	}

}