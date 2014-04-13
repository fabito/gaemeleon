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

	private static final Property PROPERTY = new Property("p1", "new v1");
	private ConfigurationResource resource;
	private PropertiesConfiguration configuration;
	
	@Before
	public void setup() throws Throwable {
		configuration = new PropertiesConfiguration("test.properties");
		resource = new ConfigurationResource(configuration);
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
	public void shouldReturnBadRequest() {
		Response r = resource.put("2133212", null);
		assertThat(r.getStatus(), is(Status.BAD_REQUEST.getStatusCode()));
		
		r = resource.put(null, null);
		assertThat(r.getStatus(), is(Status.BAD_REQUEST.getStatusCode()));

		r = resource.put("", null);
		assertThat(r.getStatus(), is(Status.BAD_REQUEST.getStatusCode()));

	}

	@Test
	public void shouldReturnNotFound() {
		Response r = resource.put("2133212", PROPERTY);
		assertThat(r.getStatus(), is(Status.NOT_FOUND.getStatusCode()));
	}

	@Test
	public void shouldReturnCreated() {
		Response r = resource.put(PROPERTY.getKey(), PROPERTY);
		assertThat(r.getStatus(), is(Status.CREATED.getStatusCode()));
		assertThat(configuration.getString(PROPERTY.getKey()), is(PROPERTY.getValue()));
	}

	@Test
	public void shouldCreateNew() {
		Property property2 = new Property("baseUrl", "www.fabito.com");
		Response r = resource.post(property2);
		assertThat(r.getStatus(), is(Status.CREATED.getStatusCode()));
		assertThat(configuration.getString(property2.getKey()), is(property2.getValue()));
	}

	@Test
	public void postShould40X() {
		Response r = resource.post(null);
		assertThat(r.getStatus(), is(Status.BAD_REQUEST.getStatusCode()));
		
		r = resource.post(new Property(null, null));
		assertThat(r.getStatus(), is(Status.BAD_REQUEST.getStatusCode()));
		
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