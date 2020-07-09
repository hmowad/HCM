package com.code.integration.webservices.hcm;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.hcm.WSEmployeeBasicInfoResponse;
import com.code.services.BaseService;
import com.code.services.hcm.EmployeesService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/employees")
public class EmployeeBasicInfoRestWS {
    @GET
    @Path("/getEmployeeBasicInfo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEmployeeBasicInfoByEmployeeId(@QueryParam("employeeId") String employeeId) {
	try {
	    EmployeeData employee = EmployeesService.getEmployeeData(Long.parseLong(employeeId));
	    if (employee == null)
		throw new BusinessException("error_employeeDataError");
	    Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
	    return Response.status(Status.OK).entity(gson.toJson(new WSEmployeeBasicInfoResponse(employee.getName(), employee.getManagerName(), employee.getJobDesc(), employee.getSocialID(), employee.getPhysicalUnitFullName(), employee.getOfficialMobileNumber(), employee.getDirectPhoneNumber(), employee.getPrivateMobileNumber(), employee.getPhoneExt(), employee.getShieldMobileNumber(), employee.getIpPhoneExt(), employee.getEmail()))).build();
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		return Response.status(Status.EXPECTATION_FAILED.getStatusCode()).entity(BaseService.getMessage(e.getMessage())).build();
	    else {
		e.printStackTrace();
		return Response.status(Status.EXPECTATION_FAILED.getStatusCode()).entity(e.getClass().getSimpleName()).build();
	    }
	}
    }
}
