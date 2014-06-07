package com.github.fabito.gaemeleon.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.configuration.Configuration;

/**
 * Exposes REST operations for general {@link Configuration} maintenance.
 * 
 * @author fabio
 */
public class BulkConfigurationResource {

	private static final Logger LOGGER = Logger
			.getLogger(BulkConfigurationResource.class.getSimpleName());

	private Configuration configuration;

	@Inject
	public BulkConfigurationResource(Configuration configuration) {
		super();
		this.configuration = configuration;
	}

	@GET
	public List<Property> listAll() {
		List<Property> result = new ArrayList<>();
		for (Iterator<String> iterator = configuration.getKeys(); iterator
				.hasNext();) {
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
		Property property = new Property(propertyName, propertyValue);
		return Response.ok(property).build();
	}
	
	@DELETE
	@Path("/{propertyName}")
	public Response delete(@PathParam("propertyName") String propertyName) {
		Object propertyValue = configuration.getProperty(propertyName);
		if (propertyValue == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		configuration.clearProperty(propertyName);
		return Response.ok().build();
	}

	@PUT
	@Path("/{propertyName}")
	public Response put(@PathParam("propertyName") String propertyName,
			Property property) {
		if (property == null || isNullOrEmpty(property.getKey())) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		LOGGER.finer("Got property = " + property);
		if (configuration.containsKey(propertyName)) {
			LOGGER.finer("config contains proeprty: " + propertyName);
			configuration.setProperty(propertyName, property.getValue());
			return Response.status(Status.CREATED).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	private Response post(Property property) {
		if (property == null || isNullOrEmpty(property.getKey())) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		LOGGER.finer("Got property = " + property);
		configuration.addProperty(property.getKey(), property.getValue());
		return Response.status(Status.CREATED).build();
	}
	
	@POST
	public Response post(List<Property> properties) {
		if (properties == null || properties.isEmpty()) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		Response resp = null;
		for (Property property : properties) {
			resp = post(property);
		}
		return resp;
	}

	private boolean isNullOrEmpty(String str) {
		return str == null || str.length() == 0;
	}
}