package com.github.fabito.gaemeleon.api;

import java.util.NoSuchElementException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
 
@Provider
public class ExceptionHttpStatusResolver implements ExceptionMapper<NoSuchElementException> {

	@Override
	public Response toResponse(NoSuchElementException exception) {
		return Response.status(Status.NOT_FOUND).build();
	}
 
}