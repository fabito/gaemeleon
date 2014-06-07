package com.github.fabito.gaemeleon.api;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.configuration.Configuration;

/**
 * Exposes REST operations for general {@link Configuration} maintenance.
 * 
 * @author fabio
 */
public class BulkConfigurationResource extends BaseConfigurationResource {

	@Inject
	public BulkConfigurationResource(Configuration configuration) {
		super();
		this.configuration = configuration;
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

}