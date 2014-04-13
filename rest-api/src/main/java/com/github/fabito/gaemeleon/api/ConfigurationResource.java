package com.github.fabito.gaemeleon.api;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.configuration.Configuration;

public class ConfigurationResource {
	
	@Context
	UriInfo uriInfo;
	
	private Configuration configuration;
	
	@Inject
	public ConfigurationResource(Configuration configuration) {
		super();
		this.configuration = configuration;
	}

	@GET
	public List<Property> listAll() {
		List<Property> result = new ArrayList<>();
		for (Iterator<String> iterator = configuration.getKeys(); iterator.hasNext();) {
			String k = iterator.next();
			result.add(new Property(k, configuration.getProperty(k)));
		}
		return result;
	}
	
	@GET
	@Path("/{propertyName}")
	public Response get(@PathParam("propertyName") String propertyName) {
		Object propertyValue = configuration.getProperty(propertyName);
		if (propertyValue == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		Property property = new Property (propertyName, propertyValue);
		return Response.ok(property).build();
	}
	
	@PUT
	@Path("/{propertyName}")
	public Response put(@PathParam("propertyName") String propertyName) {
		Object value = null;
		configuration.addProperty(propertyName, value);
		Object values = null;
		UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
		URI location = uriBuilder.build(values);
		return Response.created(location).build();
	}
}