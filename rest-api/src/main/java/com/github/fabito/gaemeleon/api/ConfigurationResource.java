package com.github.fabito.gaemeleon.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.configuration.Configuration;

import com.google.common.base.Strings;

/**
 * Exposes REST operations for general {@link Configuration} maitenenace.
 * 
 * @author fabio
 */
public class ConfigurationResource {
	
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
	public Response put(@PathParam("propertyName") String propertyName, Property property) {

		if (property == null || Strings.isNullOrEmpty(property.getKey())) {
			return Response.status(Status.BAD_REQUEST) .build();
		}
		
		if (configuration.containsKey(propertyName)) {
			configuration.setProperty(propertyName, property.getValue());
			return Response.status(Status.CREATED) .build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	@POST
	public Response post(Property property) {
		
		if (property == null || Strings.isNullOrEmpty(property.getKey())) {
			return Response.status(Status.BAD_REQUEST) .build();
		}
		
		configuration.addProperty(property.getKey(), property.getValue());
		return Response.status(Status.CREATED) .build();
	}

}