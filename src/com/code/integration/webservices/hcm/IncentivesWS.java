package com.code.integration.webservices.hcm;

import java.util.ArrayList;
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

import com.code.dal.orm.hcm.incentives.IncentivePort;
import com.code.dal.orm.hcm.missions.MissionDetailData;
import com.code.dal.orm.setup.GovernmentalCommitteeCategory;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.integration.requests.eservices.IncentiveRequest;
import com.code.integration.responses.hcm.WSMissionDetailDataResponse;
import com.code.services.BaseService;
import com.code.services.hcm.IncentivesService;
import com.code.services.hcm.MissionsService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/IncentivesWS")
public class IncentivesWS {
    @POST
    @Path("/addIncentiveTransaction")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addIncentiveTransaction(String incentiveRequestString) {
	Gson gson = new GsonBuilder().setDateFormat("mm/MM/yyyy").create();
	try {
	    IncentiveRequest incentiveRequest = gson.fromJson(incentiveRequestString, IncentiveRequest.class);
	    IncentivesService.addIncentiveTransaction(incentiveRequest);
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		return Response.status(Status.EXPECTATION_FAILED).entity(BaseService.getMessage(e.getMessage())).build();
	    else
		return Response.status(Status.EXPECTATION_FAILED).entity(e.getClass().getSimpleName()).build();
	}
	return Response.status(Status.OK).build();
    }

    @GET
    @Path("/getMissionsDetails")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMissionsDetailsWithNoIncentives(@QueryParam("employeeId") String employeeId) {
	List<MissionDetailData> missionsDetailsData = null;
	Gson gson = new GsonBuilder().setPrettyPrinting().create();

	try {
	    missionsDetailsData = MissionsService.getMissionsDetailsWithNoIncentivesByEmpId(Long.parseLong(employeeId));
	} catch (Exception e) {
	    return Response.status(Status.EXPECTATION_FAILED).entity(e.getClass().getSimpleName()).build();
	}

	// construct response
	List<WSMissionDetailDataResponse> wsMissionsDetailsData = constructRespone(missionsDetailsData);
	return Response.status(Status.OK).entity(gson.toJson(wsMissionsDetailsData)).build();
    }

    private List<WSMissionDetailDataResponse> constructRespone(List<MissionDetailData> missionsDetailsData) {
	List<WSMissionDetailDataResponse> wsMissionsDetailsData = new ArrayList<WSMissionDetailDataResponse>();

	// construct response
	for (MissionDetailData missionDetailData : missionsDetailsData) {
	    WSMissionDetailDataResponse wsMissionDetailData = new WSMissionDetailDataResponse();
	    wsMissionDetailData.setId(missionDetailData.getId());
	    wsMissionDetailData.setDecisionNumber(missionDetailData.getMissionDecisionNumber());
	    wsMissionDetailData.setDecisionDate(missionDetailData.getMissionDecisionDateString());
	    wsMissionDetailData.setLocationFlag(missionDetailData.getMissionLocationFlag().equals(FlagsEnum.ON.getCode()));
	    wsMissionDetailData.setDestination(missionDetailData.getMissionDestination());
	    wsMissionDetailData.setPurpose(missionDetailData.getMissionPurpose());
	    wsMissionDetailData.setPeriod(missionDetailData.getPeriod());
	    wsMissionDetailData.setStartDate(missionDetailData.getStartDateString());
	    wsMissionDetailData.setEndDate(missionDetailData.getEndDateString());

	    wsMissionsDetailsData.add(wsMissionDetailData);
	}

	return wsMissionsDetailsData;
    }

    @GET
    @Path("/getPorts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIncentivePorts() {
	List<IncentivePort> incentivePorts = null;
	Gson gson = new GsonBuilder().setPrettyPrinting().create();

	try {
	    incentivePorts = IncentivesService.getIncentivePorts();
	} catch (DatabaseException e) {
	    return Response.status(Status.EXPECTATION_FAILED).entity(DatabaseException.class.getSimpleName()).build();
	}

	return Response.status(Status.OK).entity(gson.toJson(incentivePorts)).build();
    }

    @GET
    @Path("/getGovCommitteesCategories")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGovernmentalCommitteesCategories() {
	List<GovernmentalCommitteeCategory> governmentalCommitteesCategories = null;
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	try {
	    governmentalCommitteesCategories = IncentivesService.getGovernmentalCommitteesCategories();
	} catch (DatabaseException e) {
	    return Response.status(Status.EXPECTATION_FAILED).entity(DatabaseException.class.getSimpleName()).build();
	}

	return Response.status(Status.OK).entity(gson.toJson(governmentalCommitteesCategories)).build();
    }
}
