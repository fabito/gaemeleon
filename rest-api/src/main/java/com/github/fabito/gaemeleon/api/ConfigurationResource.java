package com.github.fabito.gaemeleon.api;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.configuration.Configuration;

/**
 * Exposes REST operations for general {@link Configuration} maitenenace.
 * 
 * @author fabio
 */
public class ConfigurationResource extends BaseConfigurationResource {

	@Inject
	public ConfigurationResource(Configuration configuration) {
		super(configuration);
	}

	@POST
	public Response post(Property property) {
		if (property == null || isNullOrEmpty(property.getKey())) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		LOGGER.finer("Got property = " + property);
		configuration.addProperty(property.getKey(), property.getValue());
		return Response.status(Status.CREATED).build();
	}
}