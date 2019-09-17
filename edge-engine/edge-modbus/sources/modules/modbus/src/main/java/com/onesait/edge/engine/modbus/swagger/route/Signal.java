package com.onesait.edge.engine.modbus.swagger.route;

import javax.ws.rs.GET;
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
@Path("/modbus/api/devices/{id}/signals/{signalid}")
@Produces("application/json")
public class Signal implements Route {

	@GET
	@ApiOperation(value = "Get signal of a device", nickname = "signal", tags = "Signals")
	@ApiImplicitParams({
			@ApiImplicitParam(required = true, dataType = "string", name = "Authorization", paramType = "header"),
			@ApiImplicitParam(required = true, dataType = "string", name = "id", paramType = "path"),
			@ApiImplicitParam(required = true, dataType = "string", name = "signalid", paramType = "path")
			})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = com.onesait.edge.engine.modbus.model.Signal.class),
			@ApiResponse(code = 404, message = "Not Found", response = ErrorResponse.class)
			})
	
	public Object handle(@ApiParam(hidden = true) Request request, @ApiParam(hidden = true) Response response)
			throws Exception {
		return null;
	}
}
