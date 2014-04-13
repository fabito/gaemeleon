package com.github.fabito.gaemeleon.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.github.fabito.gaemeleon.core.AppVersionConfiguration;
import com.github.fabito.gaemeleon.core.DatastoreConfiguration;
import com.google.appengine.api.utils.SystemProperty;
import com.google.common.collect.ImmutableMap;

public class AppVersionConfigurationTest extends BaseConfigurationTest<AppVersionConfiguration> {

	private static final String MAJOR_VERSION = "1-0-69";
	private static final String MINOR_VERSION = "2014-04-13T03:33:14Z";

	protected Map<String, String> initialConfigurationForVersion = ImmutableMap
			.<String, String> of(MAJOR_VERSION + AppVersionConfiguration.DEFAULT_SEPARATOR + "p1", "nv1",
					MAJOR_VERSION + AppVersionConfiguration.DEFAULT_SEPARATOR + "p2", "nv2",
					MAJOR_VERSION + AppVersionConfiguration.DEFAULT_SEPARATOR + "p3", "nv3",
					"np4", "nv4");

    @Before
    public void before() {
        SystemProperty.applicationVersion.set(MAJOR_VERSION + AppVersionConfiguration.DEFAULT_SEPARATOR + MINOR_VERSION);
		addConfigurations(initialConfigurationForVersion);
    }

	@Override
	AppVersionConfiguration configuration() {
		return new AppVersionConfiguration(new DatastoreConfiguration(datastoreService));
	}

	@Override
	Map<String, String> initialValues() {
		return initialConfigurationForVersion;
	}

	@Test
	public void existentPropertyWithoutVersionPrefix() {
		assertThat(configuration.getString("p1"), is("nv1"));
		assertThat(configuration.getString("p2"), is("nv2"));
		assertThat(configuration.getString("p3"), is("nv3"));
	}

	@Test
	public void prefixedProperties() {
		assertNull(configuration.getString("np4"));
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
	
}