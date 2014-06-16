package com.github.fabito.gaemeleon.api;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.apache.commons.configuration.Configuration;

/**
 * Exposes REST operations for general {@link Configuration} maintenance.
 * 
 * @author fabio
 */
public class BulkConfigurationResource {

	private BaseConfigurationResource delegate;
	
	@Inject
	public BulkConfigurationResource(Configuration configuration) {
		delegate = new BaseConfigurationResource(configuration);
	}
	
	@GET
	public List<Property> listAll() {
		return delegate.listAll();
	}

	@GET
	@Path("/{propertyName}")
	public Response get(@PathParam("propertyName") String propertyName) {
		return delegate.get(propertyName);
	}

	@PUT
	@Path("/{propertyName}")
	public Response put(@PathParam("propertyName") String propertyName, Property property) {
		return delegate.put(propertyName, property);
	}

	@DELETE
	@Path("/{propertyName}")
	public Response delete(@PathParam("propertyName") String propertyName) {
		return delegate.delete(propertyName);
	}
	
	@POST
	public Response post(List<Property> properties) {
		return delegate.post(properties);
	}

}