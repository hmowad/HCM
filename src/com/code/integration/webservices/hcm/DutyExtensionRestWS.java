package com.code.integration.webservices.hcm;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.code.dal.orm.hcm.dutyextension.DutyExtensionTransaction;
import com.code.dal.orm.hcm.dutyextension.DutyExtensionTransactionData;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.hcm.WSTerminatedEmployeesResponse;
import com.code.services.BaseService;
import com.code.services.hcm.DutyExtensionService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/extension")
public class DutyExtensionRestWS {

    @GET
    @Path("/getTerminatedEmployees")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServiceTerminatedEmployeesByDepartmentId(@QueryParam("departmentId") Long departmentId) {

	try {
	    List<WSTerminatedEmployeesResponse> wsTerminatedEmployeesResponseList = DutyExtensionService.getTerminatedEmployeesByParentUnitId(departmentId);
	    Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
	    return Response.status(Status.OK.getStatusCode()).entity(gson.toJson(wsTerminatedEmployeesResponseList == null || wsTerminatedEmployeesResponseList.size() == 0 ? "" : wsTerminatedEmployeesResponseList)).build();
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		return Response.status(Status.EXPECTATION_FAILED.getStatusCode()).entity(BaseService.getMessage(e.getMessage())).build();
	    else {
		e.printStackTrace();
		return Response.status(Status.EXPECTATION_FAILED.getStatusCode()).entity(e.getClass().getSimpleName()).build();
	    }
	}
    }

    @GET
    @Path("/getLastDutyExtension")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDutyExtensionByEmpId(@QueryParam("employeeId") Long employeeId) {

	try {
	    DutyExtensionTransactionData dutyExtensionTransactionData = DutyExtensionService.getDutyExtensionTransactionDataByEmpId(employeeId);
	    Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
	    return Response.status(Status.OK.getStatusCode()).entity(gson.toJson(dutyExtensionTransactionData == null ? "" : dutyExtensionTransactionData)).build();
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		return Response.status(Status.EXPECTATION_FAILED.getStatusCode()).entity(BaseService.getMessage(e.getMessage())).build();
	    else {
		e.printStackTrace();
		return Response.status(Status.EXPECTATION_FAILED.getStatusCode()).entity(e.getClass().getSimpleName()).build();
	    }
	}
    }

    @POST
    @Path("/addDutyExtension")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postDutyExtensionTransaction(String transaction) {
	try {
	    Gson gson = new GsonBuilder().setPrettyPrinting().create();
	    DutyExtensionService.addExtensionTransaction(gson.fromJson(transaction, DutyExtensionTransaction.class));
	    return Response.status(Status.OK.getStatusCode()).build();
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		return Response.status(Status.EXPECTATION_FAILED.getStatusCode()).entity(BaseService.getMessage(e.getMessage())).build();
	    else {
		e.printStackTrace();
		return Response.status(Status.EXPECTATION_FAILED.getStatusCode()).entity(e.getClass().getSimpleName()).build();
	    }
	}
    }

    @POST
    @Path("/addDutyReExtension")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postDutyReExtensionTransaction(String transaction) {
	try {
	    Gson gson = new GsonBuilder().setPrettyPrinting().create();
	    DutyExtensionService.addReExtensionTransaction(gson.fromJson(transaction, DutyExtensionTransaction.class));
	    return Response.status(Status.OK.getStatusCode()).build();
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
