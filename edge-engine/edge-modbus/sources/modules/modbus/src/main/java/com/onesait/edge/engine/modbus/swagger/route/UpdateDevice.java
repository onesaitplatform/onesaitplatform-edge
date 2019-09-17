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
@Path("/modbus/api/devices/{id}")
@Produces("application/json")
public class UpdateDevice implements Route {

	@PUT
	@ApiOperation(value = "Update device", nickname = "UpdateDevice", tags = "Devices")
	@ApiImplicitParams({
			@ApiImplicitParam(required = true, dataType = "string", name = "Authorization", paramType = "header"),
			@ApiImplicitParam(required = true, dataType = "string", name = "id", paramType = "path"),
			@ApiImplicitParam(required = true, dataType = "com.onesait.edge.engine.modbus.model.Device", paramType = "body")
			})
	@ApiResponses(value = {
			@ApiResponse(code = 204, message = "No Content"),
			@ApiResponse(code = 400, message = "Bad Request", response = ErrorResponse.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)
			})
	
	public Object handle(@ApiParam(hidden = true) Request request, @ApiParam(hidden = true) Response response)
			throws Exception {
		return null;
	}
}