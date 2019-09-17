package com.onesait.edge.engine.modbus.swagger.route;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.onesait.edge.engine.modbus.util.ErrorResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import spark.Request;
import spark.Response;
import spark.Route;

@Api
@Path("/modbus/api/devices/{id}/signals/{signalid}/{value}")
@Produces("application/json")
public class SendCommandValue implements Route {

	@PUT
	@ApiOperation(value = "Send Command Value", nickname = "sendCommandValue", tags = "Commands")
	@ApiImplicitParams({
			@ApiImplicitParam(required = true, dataType = "string", name = "Authorization", paramType = "header"),
			@ApiImplicitParam(required = true, dataType = "string", name = "id", paramType = "path"),
			@ApiImplicitParam(required = true, dataType = "string", name = "signalid", paramType = "path"),
			@ApiImplicitParam(required = true, dataType = "string", name = "value", paramType = "path")
			})
	@ApiResponses(value = {
			@ApiResponse(code = 202, message = "Accepted"),
			@ApiResponse(code = 400, message = "Bad Request", response = ErrorResponse.class),
			})
	
	public Object handle(@ApiParam(hidden = true) Request request, @ApiParam(hidden = true) Response response)
			throws Exception {
		return null;
	}
}