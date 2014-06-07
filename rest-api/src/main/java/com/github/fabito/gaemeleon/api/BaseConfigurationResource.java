package com.github.fabito.gaemeleon.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.configuration.Configuration;

public class BaseConfigurationResource {

	protected static final Logger LOGGER = Logger
				.getLogger(ConfigurationResource.class.getSimpleName());
	protected Configuration configuration;

	public BaseConfigurationResource() {
		super();
	}

	protected boolean isNullOrEmpty(String str) {
		return str == null || str.length() == 0;
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

	@PUT
	@Path("/{propertyName}")
	public Response put(@PathParam("propertyName") String propertyName, Property property) {
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

}