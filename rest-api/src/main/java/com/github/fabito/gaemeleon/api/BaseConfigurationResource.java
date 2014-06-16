package com.github.fabito.gaemeleon.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.configuration.Configuration;

public class BaseConfigurationResource {

	protected static final Logger LOGGER = Logger
				.getLogger(ConfigurationResource.class.getSimpleName());
	
	protected Configuration configuration;

	public BaseConfigurationResource(Configuration configuration) {
		super();
		this.configuration = configuration;
	}

	protected boolean isNullOrEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public List<Property> listAll() {
		List<Property> result = new ArrayList<>();
		for (Iterator<String> iterator = configuration.getKeys(); iterator
				.hasNext();) {
			String k = iterator.next();
			result.add(new Property(k, configuration.getProperty(k)));
		}
		return result;
	}

	public Response get(@PathParam("propertyName") String propertyName) {
		Object propertyValue = configuration.getProperty(propertyName);
		if (propertyValue == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		Property property = new Property(propertyName, propertyValue);
		return Response.ok(property).build();
	}

	public Response put(@PathParam("propertyName") String propertyName, Property property) {
		if (property == null || isNullOrEmpty(property.getKey())) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		LOGGER.finer("Got property = " + property);
		if (configuration.containsKey(propertyName)) {
			LOGGER.finer("config contains proeprty: " + propertyName);
			configuration.setProperty(propertyName, property.getValue());
			return Response.noContent().build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	public Response delete(@PathParam("propertyName") String propertyName) {
		Object propertyValue = configuration.getProperty(propertyName);
		if (propertyValue == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		configuration.clearProperty(propertyName);
		return Response.noContent().build();
	}
	
	public Response post(Property property) {
		if (property == null || isNullOrEmpty(property.getKey())) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		LOGGER.finer("Got property = " + property);
		configuration.addProperty(property.getKey(), property.getValue());
		return Response.status(Status.CREATED).build();
	}
	
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
}