package com.github.fabito.gaemeleon.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationResourceTest {

	ConfigurationResource resource;
	
	@Before
	public void setup() throws Throwable {
		resource = new ConfigurationResource(new PropertiesConfiguration("test.properties"));
	}
	
	@Test
	public void shouldFindProperty() {
		Response r = resource.get("p1");
		assertThat(r.getStatus(), is(Status.OK.getStatusCode()));
		Property p = (Property) r.getEntity();
		assertProperty(p, "p1", "v1");
	}

	@Test
	public void shouldNotFindProperty() {
		Response r = resource.get("2133212");
		assertThat(r.getStatus(), is(Status.NOT_FOUND.getStatusCode()));
	}
	
	@Test
	public void shouldListAll() {
		List<Property> props = resource.listAll();
		assertThat(props.size(), is(3));
		for (int i = 0; i < props.size(); i++) {
			assertProperty(props.get(i), "p"+(i+1), "v"+(i+1));
		}
	}
	
	private void assertProperty(Property p, String expectedKey, String expectedValue) {
		assertThat(p.getKey(), is(expectedKey));
		assertThat((String)p.getValue(), is(expectedValue));
	}

}