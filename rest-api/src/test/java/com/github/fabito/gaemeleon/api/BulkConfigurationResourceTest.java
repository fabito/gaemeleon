package com.github.fabito.gaemeleon.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class BulkConfigurationResourceTest {

	private static final Property PROPERTY = new Property("p1", "new v1");
	private BulkConfigurationResource resource;
	private PropertiesConfiguration configuration;
	
	@Before
	public void setup() throws Throwable {
		configuration = new PropertiesConfiguration("test.properties");
		resource = new BulkConfigurationResource(configuration);
	}

	@Test
	public void shouldCreateNew() {
		Property property2 = new Property("baseUrl", "www.fabito.com");
		Response r = resource.post(Lists.newArrayList(property2));
		assertThat(r.getStatus(), is(Status.CREATED.getStatusCode()));
		assertThat(configuration.getString(property2.getKey()), is(property2.getValue()));
	}

	@Test
	public void postShould40X() {
		Response r = resource.post(null);
		assertThat(r.getStatus(), is(Status.BAD_REQUEST.getStatusCode()));
		
		r = resource.post(Lists.newArrayList(new Property(null, null)));
		assertThat(r.getStatus(), is(Status.BAD_REQUEST.getStatusCode()));
		
	}
	
	private void assertProperty(Property p, String expectedKey, String expectedValue) {
		assertThat(p.getKey(), is(expectedKey));
		assertThat((String)p.getValue(), is(expectedValue));
	}

}