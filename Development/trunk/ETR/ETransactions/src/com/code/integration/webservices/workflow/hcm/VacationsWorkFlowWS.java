package com.code.integration.webservices.workflow.hcm;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.vacations.Vacation;
import com.code.dal.orm.hcm.vacations.VacationData;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.hcm.vacations.WFVacation;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.RequestTypesEnum;
import com.code.enums.SubVacationTypesEnum;
import com.code.enums.VacationTypesEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.WSResponseBase;
import com.code.integration.responses.workflow.hcm.WSWFVacationResponse;
import com.code.integration.responses.workflow.hcm.WSWFVacationTaskInfo;
import com.code.integration.responses.workflow.hcm.WSWFVacationsTasksResponse;
import com.code.services.BaseService;
import com.code.services.config.ETRConfigurationService;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.UnitsService;
import com.code.services.hcm.VacationsService;
import com.code.services.integration.WSSessionsManagementService;
import com.code.services.security.SecurityService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.BaseWorkFlow;
import com.code.services.workflow.hcm.VacationsWorkFlow;

@WebService(targetNamespace = "http://integration.code.com/vacations",
	portName = "VacationsWorkFlowWSHttpPort")
public class VacationsWorkFlowWS {

    /************************************************* Initialize Requests *****************************************************/

    /*------------------------------------------------ Actions ------------------------------------------------------*/

    @WebMethod(operationName = "initSickVacationRequestForHIS")
    @WebResult(name = "initSickVacationRequestForHISResponse")
    public WSResponseBase initSickVacationRequestForHIS(@WebParam(name = "beneficiarySocialId") String beneficiarySocialId,
	    @WebParam(name = "subVacationType") int subVacationType,
	    @WebParam(name = "period") int period, @WebParam(name = "startDateString") String startDateString,
	    @WebParam(name = "locationFlag") int locationFlag, @WebParam(name = "location") String location,
	    @WebParam(name = "contactNumber") String contactNumber, @WebParam(name = "contactAddress") String contactAddress) {

	WSResponseBase response = new WSResponseBase();
	try {
	    if (beneficiarySocialId == null || beneficiarySocialId.isEmpty())
		throw new BusinessException("error_employeeIsMandatory");

	    EmployeeData beneficiary = EmployeesService.getEmployeeBySocialID(beneficiarySocialId);
	    if (beneficiary == null)
		throw new BusinessException("error_employeeNotExist");

	    validateSickVacationRequestData(subVacationType, period, startDateString, locationFlag, location, contactNumber, contactAddress);

	    WFVacation vacRequest = new WFVacation();
	    vacRequest.setRequestType(RequestTypesEnum.NEW.getCode());
	    vacRequest.setVacationTypeId(VacationTypesEnum.SICK.getCode());
	    vacRequest.setSubVacationType(subVacationType);

	    // Order of the next two lines is CRITICAL to set the endDate correctly.
	    vacRequest.setStartDateString(startDateString);
	    vacRequest.setPeriod(period);

	    vacRequest.setLocationFlag(locationFlag);
	    vacRequest.setLocation(location);
	    vacRequest.setContactNumber(contactNumber);
	    vacRequest.setContactAddress(contactAddress);

	    List<VacationData> lastVacations = VacationsService.getVacationsData(beneficiary.getEmpId(), VacationTypesEnum.SICK.getCode(), subVacationType);
	    if (lastVacations != null && !lastVacations.isEmpty()) {
		vacRequest.setOldVacationId(lastVacations.get(0).getId());
		if (lastVacations.size() > 1)
		    vacRequest.setSecondOldVacationId(lastVacations.get(1).getId());
		else
		    vacRequest.setSecondOldVacationId(null);
	    } else {
		vacRequest.setOldVacationId(null);
	    }

	    String[] proceeeIdAndTaskURL = getProcessIdAndTaskURL(beneficiary.getCategoryId(), RequestTypesEnum.NEW.getCode(), VacationTypesEnum.SICK.getCode(), subVacationType).split(",");

	    VacationsWorkFlow.initVacation(beneficiary, beneficiary, vacRequest, Long.parseLong(proceeeIdAndTaskURL[0]), null, proceeeIdAndTaskURL[1]);

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());

	    if (e instanceof BusinessException)
		response.setMessage(BaseService.getParameterizedMessage(e.getMessage(), ((BusinessException) e).getParams()));
	    else {
		response.setMessage(BaseService.getMessage("error_integrationError"));
		e.printStackTrace();
	    }
	}
	return response;
    }

    @WebMethod(operationName = "initVacationRequest")
    @WebResult(name = "initVacationRequestResponse")
    public WSResponseBase initVacationRequest(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "requesterId") long requesterId,
	    @WebParam(name = "vacationTypeId") long vacationTypeId,
	    @WebParam(name = "period") int period, @WebParam(name = "startDateString") String startDateString,
	    @WebParam(name = "locationFlag") int locationFlag, @WebParam(name = "location") String location,
	    @WebParam(name = "contactNumber") String contactNumber, @WebParam(name = "contactAddress") String contactAddress,
	    @WebParam(name = "attachmentId") String attachmentId) {

	return initVacationWorkFlow(sessionId, requesterId, RequestTypesEnum.NEW.getCode(), vacationTypeId, period, startDateString, locationFlag, location, contactNumber, contactAddress, null, null, attachmentId);
    }

    @WebMethod(operationName = "initModifyVacationRequest")
    @WebResult(name = "initModifyVacationRequestResponse")
    public WSResponseBase initModifyVacationRequest(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "requesterId") long requesterId,
	    @WebParam(name = "vacationTypeId") long vacationTypeId, @WebParam(name = "period") int period,
	    @WebParam(name = "notes") String notes, @WebParam(name = "reasons") String reasons,
	    @WebParam(name = "attachmentId") String attachmentId) {

	return initVacationWorkFlow(sessionId, requesterId, RequestTypesEnum.MODIFY.getCode(), vacationTypeId, period, null, 0, null, null, null, notes, reasons, attachmentId);
    }

    @WebMethod(operationName = "initCancelVacationRequest")
    @WebResult(name = "initCancelVacationRequestResponse")
    public WSResponseBase initCancelVacationRequest(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "requesterId") long requesterId,
	    @WebParam(name = "vacationTypeId") long vacationTypeId, @WebParam(name = "notes") String notes, @WebParam(name = "reasons") String reasons,
	    @WebParam(name = "attachmentId") String attachmentId) {

	return initVacationWorkFlow(sessionId, requesterId, RequestTypesEnum.CANCEL.getCode(), vacationTypeId, 0, null, 0, null, null, null, notes, reasons, attachmentId);
    }

    private WSResponseBase initVacationWorkFlow(String sessionId, long requesterId, int requestType, long vacationTypeId, int period, String startDateString,
	    int locationFlag, String location, String contactNumber, String contactAddress, String notes, String reasons, String attachmentId) {

	WSResponseBase response = new WSResponseBase();
	if (!WSSessionsManagementService.maintainSession(sessionId, requesterId, response))
	    return response;

	try {
	    EmployeeData requester = EmployeesService.getEmployeeData(requesterId);

	    if (requestType == RequestTypesEnum.NEW.getCode())
		validateVacationRequestData(vacationTypeId, period, startDateString, locationFlag, location, contactNumber, contactAddress);
	    else if (requestType == RequestTypesEnum.MODIFY.getCode())
		validateModifyVacationRequestData(vacationTypeId, period, requester.getCategoryId(), reasons);
	    else if (requestType == RequestTypesEnum.CANCEL.getCode())
		validateCancelVacationRequestData(vacationTypeId, requester.getCategoryId(), reasons);

	    WFVacation vacRequest = new WFVacation();
	    vacRequest.setRequestType(requestType);
	    vacRequest.setVacationTypeId(vacationTypeId);

	    if (requestType == RequestTypesEnum.NEW.getCode()) {
		// Order of the next two lines is CRITICAL to set the endDate correctly.
		vacRequest.setStartDateString(startDateString);
		vacRequest.setPeriod(period);

		vacRequest.setLocationFlag(locationFlag);
		vacRequest.setLocation(location);
		vacRequest.setContactNumber(contactNumber);
		vacRequest.setContactAddress(contactAddress);

		List<VacationData> lastVacations = VacationsService.getVacationsData(requesterId, vacationTypeId);
		if (lastVacations != null && !lastVacations.isEmpty())
		    vacRequest.setOldVacationId(lastVacations.get(0).getId());
		else
		    vacRequest.setOldVacationId(null);
		if (lastVacations.size() > 1)
		    vacRequest.setSecondOldVacationId(lastVacations.get(1).getId());
		else
		    vacRequest.setSecondOldVacationId(null);
	    } else {
		VacationData lastVacation = VacationsService.getLastVacationData(requesterId, vacationTypeId);
		if (lastVacation == null)
		    throw new BusinessException("error_noPrevVacation");

		vacRequest.setOldVacationId(lastVacation.getId());

		// Order of the next two lines is CRITICAL to set the endDate correctly.
		vacRequest.setStartDateString(lastVacation.getStartDateString());
		vacRequest.setPeriod(requestType == RequestTypesEnum.MODIFY.getCode() ? period : lastVacation.getPeriod());

		vacRequest.setLocationFlag(lastVacation.getLocationFlag());
		vacRequest.setLocation(lastVacation.getLocation());

		vacRequest.setNotes(notes);
		vacRequest.setReasons(reasons);
	    }

	    String[] proceeeIdAndTaskURL = getProcessIdAndTaskURL(requester.getCategoryId(), requestType, vacationTypeId, null).split(",");

	    VacationsWorkFlow.initVacation(requester, requester, vacRequest, Long.parseLong(proceeeIdAndTaskURL[0]), attachmentId, proceeeIdAndTaskURL[1]);

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());

	    if (e instanceof BusinessException)
		response.setMessage(BaseService.getParameterizedMessage(e.getMessage(), ((BusinessException) e).getParams()));
	    else {
		response.setMessage(BaseService.getMessage("error_integrationError"));
		e.printStackTrace();
	    }
	}
	return response;
    }

    @WebMethod(operationName = "initVacationJoining")
    @WebResult(name = "initVacationJoiningResponse")
    public WSResponseBase initVacationJoining(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "requesterId") long requesterId,
	    @WebParam(name = "beneficiaryId") long beneficiaryId, @WebParam(name = "vacationId") long vacationId,
	    @WebParam(name = "exceededDays") Integer exceededDays, @WebParam(name = "notes") String notes) {
	return initVacationJoiningWorkFlow(sessionId, requesterId, beneficiaryId, vacationId, exceededDays, notes);

    }

    private WSResponseBase initVacationJoiningWorkFlow(String sessionId, long requesterId, long beneficiaryId, long vacationId, Integer exceededDays, String notes) {

	WSResponseBase response = new WSResponseBase();
	if (!WSSessionsManagementService.maintainSession(sessionId, requesterId, response))
	    return response;

	try {
	    EmployeeData requester = EmployeesService.getEmployeeData(requesterId);
	    EmployeeData beneficiary = (requesterId == beneficiaryId) ? requester : EmployeesService.getEmployeeData(beneficiaryId);

	    Vacation vacation = VacationsService.getVacationById(vacationId);

	    WFVacation vacRequest = new WFVacation();
	    vacRequest.setRequestType(RequestTypesEnum.NEW.getCode());
	    vacRequest.setOldVacationId(vacation.getVacationId());
	    vacRequest.setVacationTypeId(vacation.getVacationTypeId());
	    vacRequest.setSubVacationType(vacation.getSubVacationType());
	    vacRequest.setStartDate(vacation.getStartDate());
	    vacRequest.setEndDate(vacation.getEndDate());
	    vacRequest.setPeriod(vacation.getPeriod());
	    vacRequest.setLocationFlag(vacation.getLocationFlag());
	    vacRequest.setLocation(vacation.getLocation());
	    vacRequest.setExceededDays((exceededDays == null || exceededDays == FlagsEnum.ALL.getCode()) ? 0 : exceededDays);
	    vacRequest.setNotes(notes);

	    String[] proceeeIdAndTaskURL = getProcessIdAndTaskURLVacJoining(requester.getCategoryId()).split(",");

	    VacationsWorkFlow.initVacationJoining(requester, beneficiary, vacRequest, Long.parseLong(proceeeIdAndTaskURL[0]), null, proceeeIdAndTaskURL[1]);

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());

	    if (e instanceof BusinessException)
		response.setMessage(BaseService.getParameterizedMessage(e.getMessage(), ((BusinessException) e).getParams()));
	    else {
		response.setMessage(BaseService.getMessage("error_integrationError"));
		e.printStackTrace();
	    }
	}
	return response;
    }

    private static String getProcessIdAndTaskURLVacJoining(long categoryId) {

	long processId = 0;
	if (categoryId == CategoriesEnum.OFFICERS.getCode()) {
	    processId = WFProcessesEnum.OFFICERS_VACATION_JOINING.getCode();
	} else if (categoryId == CategoriesEnum.SOLDIERS.getCode()) {
	    processId = WFProcessesEnum.SOLDIERS_VACATION_JOINING.getCode();
	} else {
	    processId = WFProcessesEnum.CIVILIANS_VACATION_JOINING.getCode();
	}

	String taskUrl = "/Vacations/VacationJoining.jsf?rootOpened=" + ETRConfigurationService.getFollowingProcessesMenuId();

	return processId + "," + taskUrl;
    }

    /*------------------------------------------------ Actions V2 ------------------------------------------------------*/

    public WSResponseBase initVacationRequest_V2(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "requesterId") long requesterId,
	    @WebParam(name = "beneficiaryId") long beneficiaryId,
	    @WebParam(name = "vacationTypeId") long vacationTypeId, @WebParam(name = "subVacationType") Integer subVacationType,
	    @WebParam(name = "period") int period, @WebParam(name = "startDateString") String startDateString,
	    @WebParam(name = "locationFlag") int locationFlag, @WebParam(name = "location") String location,
	    @WebParam(name = "contactNumber") String contactNumber, @WebParam(name = "contactAddress") String contactAddress,
	    @WebParam(name = "attachmentId") String attachmentId) {

	return initVacationWorkFlow_V2(sessionId, requesterId, beneficiaryId, RequestTypesEnum.NEW.getCode(), vacationTypeId, subVacationType, period, startDateString, locationFlag, location, contactNumber, contactAddress, null, null, attachmentId);
    }

    @WebMethod(operationName = "initModifyVacationRequest_v2")
    @WebResult(name = "initModifyVacationRequestResponse")
    public WSResponseBase initModifyVacationRequest_V2(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "requesterId") long requesterId,
	    @WebParam(name = "beneficiaryId") long beneficiaryId,
	    @WebParam(name = "vacationTypeId") long vacationTypeId, @WebParam(name = "subVacationType") Integer subVacationType,
	    @WebParam(name = "period") int period, @WebParam(name = "notes") String notes, @WebParam(name = "reasons") String reasons,
	    @WebParam(name = "attachmentId") String attachmentId) {

	return initVacationWorkFlow_V2(sessionId, requesterId, beneficiaryId, RequestTypesEnum.MODIFY.getCode(), vacationTypeId, subVacationType, period, null, 0, null, null, null, notes, reasons, attachmentId);
    }

    @WebMethod(operationName = "initCancelVacationRequest_v2")
    @WebResult(name = "initCancelVacationRequestResponse")
    public WSResponseBase initCancelVacationRequest_V2(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "requesterId") long requesterId,
	    @WebParam(name = "beneficiaryId") long beneficiaryId, @WebParam(name = "vacationTypeId") long vacationTypeId, @WebParam(name = "subVacationType") Integer subVacationType,
	    @WebParam(name = "notes") String notes, @WebParam(name = "reasons") String reasons, @WebParam(name = "attachmentId") String attachmentId) {

	return initVacationWorkFlow_V2(sessionId, requesterId, beneficiaryId, RequestTypesEnum.CANCEL.getCode(), vacationTypeId, subVacationType, 0, null, 0, null, null, null, notes, reasons, attachmentId);
    }

    private WSResponseBase initVacationWorkFlow_V2(String sessionId, long requesterId, long beneficiaryId, int requestType, long vacationTypeId, Integer subVacationType,
	    int period, String startDateString, int locationFlag, String location, String contactNumber, String contactAddress, String notes, String reasons, String attachmentId) {

	WSResponseBase response = new WSResponseBase();
	if (!WSSessionsManagementService.maintainSession(sessionId, requesterId, response))
	    return response;

	try {

	    EmployeeData requester = EmployeesService.getEmployeeData(requesterId);
	    EmployeeData beneficiary = (requesterId == beneficiaryId) ? requester : EmployeesService.getEmployeeData(beneficiaryId);

	    WFVacation vacRequest = new WFVacation();
	    vacRequest.setRequestType(requestType);
	    vacRequest.setVacationTypeId(vacationTypeId);

	    vacRequest.setSubVacationType(subVacationType == FlagsEnum.ALL.getCode() ? null : subVacationType);

	    // check if subVacationType allowed for the beneficiary in specific vacations types
	    validateEmployeeSubVacationType(vacationTypeId, vacRequest.getSubVacationType(), beneficiary.getCategoryId());

	    // check authorization after filling the request type and vacation type and sub-type
	    validateEmployeeAuthorization(requester, beneficiary, vacRequest);

	    if (requestType == RequestTypesEnum.NEW.getCode()) {
		// Order of the next two lines is CRITICAL to set the endDate correctly.
		vacRequest.setStartDateString(startDateString);
		vacRequest.setPeriod(period);

		vacRequest.setLocationFlag(locationFlag);
		vacRequest.setLocation(location);
		vacRequest.setContactNumber(contactNumber);
		vacRequest.setContactAddress(contactAddress);
	    } else {

		VacationData lastVacation = VacationsService.getLastVacationData(beneficiaryId, vacationTypeId);
		if (lastVacation == null)
		    throw new BusinessException("error_noPrevVacation");

		vacRequest.setOldVacationId(lastVacation.getId());

		// Order of the next two lines is CRITICAL to set the endDate correctly.
		vacRequest.setStartDateString(lastVacation.getStartDateString());
		vacRequest.setPeriod(requestType == RequestTypesEnum.MODIFY.getCode() ? period : lastVacation.getPeriod());

		vacRequest.setLocationFlag(lastVacation.getLocationFlag());
		vacRequest.setLocation(lastVacation.getLocation());

		vacRequest.setNotes(notes);
		vacRequest.setReasons(reasons);
	    }

	    // validate request data after filling all the data
	    if (requestType == RequestTypesEnum.NEW.getCode())
		validateVacationRequestData_V2(vacationTypeId, period, beneficiary.getCategoryId(), startDateString, locationFlag, location, contactNumber, contactAddress);
	    else if (requestType == RequestTypesEnum.MODIFY.getCode())
		validateModifyVacationRequestData_V2(vacationTypeId, period, beneficiary.getCategoryId(), reasons);
	    else if (requestType == RequestTypesEnum.CANCEL.getCode())
		validateCancelVacationRequestData_V2(vacationTypeId, beneficiary.getCategoryId(), reasons);

	    String[] proceeeIdAndTaskURL = getProcessIdAndTaskURL_V2(beneficiary.getCategoryId(), requestType, vacationTypeId, vacRequest.getSubVacationType()).split(",");

	    VacationsWorkFlow.initVacation(requester, beneficiary, vacRequest, Long.parseLong(proceeeIdAndTaskURL[0]), attachmentId, proceeeIdAndTaskURL[1]);

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());

	    if (e instanceof BusinessException)
		response.setMessage(BaseService.getParameterizedMessage(e.getMessage(), ((BusinessException) e).getParams()));
	    else {
		response.setMessage(BaseService.getMessage("error_integrationError"));
		e.printStackTrace();
	    }
	}
	return response;
    }

    /*------------------------------------------------ Validations --------------------------------------------------*/

    private static void validateSickVacationRequestData(int subVacationType, int period, String startDateString, int locationFlag, String location, String contactNumber, String contactAddress) throws BusinessException {

	if (subVacationType != SubVacationTypesEnum.SUB_TYPE_ONE.getCode() && subVacationType != SubVacationTypesEnum.SUB_TYPE_TWO.getCode())
	    throw new BusinessException("error_unsupportedSickVacationSubType");

	if (period <= 0)
	    throw new BusinessException("error_periodNotNegative");

	if (HijriDateService.getHijriDate(startDateString) == null)
	    throw new BusinessException("error_invalidHijriDate");

	if (locationFlag != LocationFlagsEnum.INTERNAL.getCode() && locationFlag != LocationFlagsEnum.EXTERNAL.getCode())
	    throw new BusinessException("error_unsupportedLocationFlag");

	if (location == null || location.isEmpty())
	    throw new BusinessException("error_locationMandatory");

	if (contactNumber == null || contactNumber.isEmpty())
	    throw new BusinessException("error_contactNumberMandatory");

	if (contactAddress == null || contactAddress.isEmpty())
	    throw new BusinessException("error_contactAddressMandatory");
    }

    private static void validateVacationRequestData(long vacationTypeId, int period, String startDateString, int locationFlag, String location, String contactNumber, String contactAddress) throws BusinessException {

	if (vacationTypeId != VacationTypesEnum.REGULAR.getCode() && vacationTypeId != VacationTypesEnum.COMPELLING.getCode() && vacationTypeId != VacationTypesEnum.FIELD.getCode())
	    throw new BusinessException("error_unsupportedVacationType");

	if (period <= 0)
	    throw new BusinessException("error_periodNotNegative");

	if (HijriDateService.getHijriDate(startDateString) == null)
	    throw new BusinessException("error_invalidHijriDate");

	if (locationFlag != LocationFlagsEnum.INTERNAL.getCode() && locationFlag != LocationFlagsEnum.EXTERNAL.getCode())
	    throw new BusinessException("error_unsupportedLocationFlag");

	if (location == null || location.isEmpty())
	    throw new BusinessException("error_locationMandatory");

	if (contactNumber == null || contactNumber.isEmpty())
	    throw new BusinessException("error_contactNumberMandatory");

	if (contactAddress == null || contactAddress.isEmpty())
	    throw new BusinessException("error_contactAddressMandatory");
    }

    private static void validateModifyVacationRequestData(long vacationTypeId, int period, long categoryId, String reasons) throws BusinessException {

	if (vacationTypeId != VacationTypesEnum.REGULAR.getCode())
	    throw new BusinessException("error_unsupportedVacationType");

	if (period <= 0)
	    throw new BusinessException("error_periodNotNegative");

	if (categoryId == CategoriesEnum.SOLDIERS.getCode() && (reasons == null || reasons.isEmpty()))
	    throw new BusinessException("error_requestJustificationsMandatory");
    }

    private static void validateCancelVacationRequestData(long vacationTypeId, long categoryId, String reasons) throws BusinessException {

	if (vacationTypeId != VacationTypesEnum.REGULAR.getCode() && vacationTypeId != VacationTypesEnum.COMPELLING.getCode() && vacationTypeId != VacationTypesEnum.FIELD.getCode())
	    throw new BusinessException("error_unsupportedVacationType");

	if (categoryId == CategoriesEnum.SOLDIERS.getCode() && (reasons == null || reasons.isEmpty()))
	    throw new BusinessException("error_requestJustificationsMandatory");
    }

    private static void validateVacationRequestData_V2(long vacationTypeId, int period, long categoryId, String startDateString, int locationFlag, String location, String contactNumber, String contactAddress) throws BusinessException {

	if (!VacationsService.isValidVacationType(categoryId, (int) vacationTypeId))
	    throw new BusinessException("error_unsupportedVacationType");

	if (period <= 0)
	    throw new BusinessException("error_periodNotNegative");

	if (HijriDateService.getHijriDate(startDateString) == null)
	    throw new BusinessException("error_invalidHijriDate");

	if (locationFlag != LocationFlagsEnum.INTERNAL.getCode() && locationFlag != LocationFlagsEnum.EXTERNAL.getCode())
	    throw new BusinessException("error_unsupportedLocationFlag");

	if (location == null || location.isEmpty())
	    throw new BusinessException("error_locationMandatory");

	if (contactNumber == null || contactNumber.isEmpty())
	    throw new BusinessException("error_contactNumberMandatory");

	if (contactAddress == null || contactAddress.isEmpty())
	    throw new BusinessException("error_contactAddressMandatory");
    }

    private static void validateModifyVacationRequestData_V2(long vacationTypeId, int period, long categoryId, String reasons) throws BusinessException {

	if (!VacationsService.isValidVacationType(categoryId, (int) vacationTypeId))
	    throw new BusinessException("error_unsupportedVacationType");

	if (period <= 0)
	    throw new BusinessException("error_periodNotNegative");

	if (categoryId == CategoriesEnum.SOLDIERS.getCode() && (reasons == null || reasons.isEmpty()))
	    throw new BusinessException("error_requestJustificationsMandatory");
    }

    private static void validateCancelVacationRequestData_V2(long vacationTypeId, long categoryId, String reasons) throws BusinessException {

	if (!VacationsService.isValidVacationType(categoryId, (int) vacationTypeId))
	    throw new BusinessException("error_unsupportedVacationType");

	if (categoryId == CategoriesEnum.SOLDIERS.getCode() && (reasons == null || reasons.isEmpty()))
	    throw new BusinessException("error_requestJustificationsMandatory");
    }

    private void validateEmployeeSubVacationType(long vacationTypeId, Integer subVacationType, long beneficiaryCategoryId) throws BusinessException {
	if ((vacationTypeId == VacationTypesEnum.EXCEPTIONAL.getCode() && (CategoriesEnum.PERSONS.getCode() == beneficiaryCategoryId || CategoriesEnum.USERS.getCode() == beneficiaryCategoryId
		|| CategoriesEnum.WAGES.getCode() == beneficiaryCategoryId || CategoriesEnum.MEDICAL_STAFF.getCode() == beneficiaryCategoryId))
		|| (vacationTypeId == VacationTypesEnum.SICK.getCode() && beneficiaryCategoryId == CategoriesEnum.SOLDIERS.getCode())) {

	    if (subVacationType == null)
		throw new BusinessException("error_subVacationMandatory");
	    else if (subVacationType != SubVacationTypesEnum.SUB_TYPE_ONE.getCode() && subVacationType != SubVacationTypesEnum.SUB_TYPE_TWO.getCode())
		throw new BusinessException("error_unsupportedSubVacationType");

	} else {
	    if (subVacationType != null)
		throw new BusinessException("error_unsupportedSubVacationType");
	}
    }

    private void validateEmployeeAuthorization(EmployeeData requester, EmployeeData beneficiary, WFVacation vacRequest) throws BusinessException {

	if (vacRequest.getRequestType() == RequestTypesEnum.NEW.getCode()) {

	    if ((!beneficiary.getEmpId().equals(requester.getEmpId())
		    && ((FlagsEnum.ON.getCode() != requester.getIsManager() || !beneficiary.getPhysicalUnitHKey().startsWith(UnitsService.getHKeyPrefix(requester.getPhysicalUnitHKey())))
			    && !isEmployeeAuthorizedRequestVacation(requester, beneficiary, vacRequest)))) {
		throw new BusinessException("error_notAuthorized");
	    }
	} else {
	    if ((!beneficiary.getEmpId().equals(requester.getEmpId()))
		    && !isEmployeeAuthorizedRequestModifyOrCancelVacation(requester, beneficiary, vacRequest)) {
		throw new BusinessException("error_notAuthorized");
	    }
	}
    }

    /*------------------------------------------------ Utilities ----------------------------------------------------*/

    private static String getProcessIdAndTaskURL(long categoryId, int requestType, long vacationTypeId, Integer subVacationType) {
	long processId = 0;
	int mode;
	String screenName = "";
	if (categoryId == CategoriesEnum.OFFICERS.getCode()) {
	    mode = 1;

	    if (vacationTypeId == VacationTypesEnum.REGULAR.getCode()) {
		if (requestType == RequestTypesEnum.NEW.getCode()) {
		    processId = WFProcessesEnum.OFFICERS_REGULAR_VACATION.getCode();
		    screenName = "RegularVacation";
		} else if (requestType == RequestTypesEnum.MODIFY.getCode()) {
		    processId = WFProcessesEnum.MODIFY_OFFICERS_REGULAR_VACATION.getCode();
		    screenName = "ModifyRegularVacation";
		} else if (requestType == RequestTypesEnum.CANCEL.getCode()) {
		    processId = WFProcessesEnum.CANCEL_OFFICERS_REGULAR_VACATION.getCode();
		    screenName = "CancelRegularVacation";
		}
	    } else if (vacationTypeId == VacationTypesEnum.COMPELLING.getCode()) {
		if (requestType == RequestTypesEnum.NEW.getCode()) {
		    processId = WFProcessesEnum.OFFICERS_COMPELLING_VACATION.getCode();
		    screenName = "CompellingVacation";
		} else if (requestType == RequestTypesEnum.CANCEL.getCode()) {
		    processId = WFProcessesEnum.CANCEL_OFFICERS_COMPELLING_VACATION.getCode();
		    screenName = "CancelCompellingVacation";
		}
	    } else if (vacationTypeId == VacationTypesEnum.FIELD.getCode()) {
		if (requestType == RequestTypesEnum.NEW.getCode()) {
		    processId = WFProcessesEnum.OFFICERS_FIELD_VACATION.getCode();
		    screenName = "FieldVacation";
		} else if (requestType == RequestTypesEnum.CANCEL.getCode()) {
		    processId = WFProcessesEnum.CANCEL_OFFICERS_FIELD_VACATION.getCode();
		    screenName = "CancelFieldVacation";
		}
	    } else if (vacationTypeId == VacationTypesEnum.SICK.getCode()) {
		if (requestType == RequestTypesEnum.NEW.getCode()) {
		    processId = WFProcessesEnum.OFFICERS_SICK_VACATION.getCode();
		    screenName = "SickVacation";
		}
	    }
	} else if (categoryId == CategoriesEnum.SOLDIERS.getCode()) {
	    mode = 2;

	    if (vacationTypeId == VacationTypesEnum.REGULAR.getCode()) {
		if (requestType == RequestTypesEnum.NEW.getCode()) {
		    processId = WFProcessesEnum.SOLDIERS_REGULAR_VACATION.getCode();
		    screenName = "RegularVacation";
		} else if (requestType == RequestTypesEnum.MODIFY.getCode()) {
		    processId = WFProcessesEnum.MODIFY_SOLDIERS_REGULAR_VACATION.getCode();
		    screenName = "ModifyRegularVacation";
		} else if (requestType == RequestTypesEnum.CANCEL.getCode()) {
		    processId = WFProcessesEnum.CANCEL_SOLDIERS_REGULAR_VACATION.getCode();
		    screenName = "CancelRegularVacation";
		}
	    } else if (vacationTypeId == VacationTypesEnum.COMPELLING.getCode()) {
		if (requestType == RequestTypesEnum.NEW.getCode()) {
		    processId = WFProcessesEnum.SOLDIERS_COMPELLING_VACATION.getCode();
		    screenName = "CompellingVacation";
		} else if (requestType == RequestTypesEnum.CANCEL.getCode()) {
		    processId = WFProcessesEnum.CANCEL_SOLDIERS_COMPELLING_VACATION.getCode();
		    screenName = "CancelCompellingVacation";
		}
	    } else if (vacationTypeId == VacationTypesEnum.FIELD.getCode()) {
		if (requestType == RequestTypesEnum.NEW.getCode()) {
		    processId = WFProcessesEnum.SOLDIERS_FIELD_VACATION.getCode();
		    screenName = "FieldVacation";
		} else if (requestType == RequestTypesEnum.CANCEL.getCode()) {
		    processId = WFProcessesEnum.CANCEL_SOLDIERS_FIELD_VACATION.getCode();
		    screenName = "CancelFieldVacation";
		}
	    } else if (vacationTypeId == VacationTypesEnum.SICK.getCode()) {
		if (requestType == RequestTypesEnum.NEW.getCode()) {
		    processId = WFProcessesEnum.SOLDIERS_SICK_VACATION.getCode();
		    screenName = "SickVacation";
		}
		if (subVacationType != null && subVacationType.equals(SubVacationTypesEnum.SUB_TYPE_TWO))
		    mode = 22;
	    }
	} else {
	    mode = 3;

	    if (vacationTypeId == VacationTypesEnum.REGULAR.getCode()) {
		if (requestType == RequestTypesEnum.NEW.getCode()) {
		    processId = WFProcessesEnum.EMPLOYEES_REGULAR_VACATION.getCode();
		    screenName = "RegularVacation";
		} else if (requestType == RequestTypesEnum.MODIFY.getCode()) {
		    processId = WFProcessesEnum.MODIFY_EMPLOYEES_REGULAR_VACATION.getCode();
		    screenName = "ModifyRegularVacation";
		} else if (requestType == RequestTypesEnum.CANCEL.getCode()) {
		    processId = WFProcessesEnum.CANCEL_EMPLOYEES_REGULAR_VACATION.getCode();
		    screenName = "CancelRegularVacation";
		}
	    } else if (vacationTypeId == VacationTypesEnum.SICK.getCode()) {
		if (requestType == RequestTypesEnum.NEW.getCode()) {
		    processId = WFProcessesEnum.EMPLOYEES_SICK_VACATION.getCode();
		    screenName = "SickVacation";
		}
	    }
	}

	String taskUrl = "/Vacations/" + screenName + ".jsf?mode=" + mode + "&rootOpened=" + ETRConfigurationService.getFollowingProcessesMenuId();

	return processId + "," + taskUrl;
    }

    private static String getProcessIdAndTaskURL_V2(long categoryId, int requestType, long vacationTypeId, Integer subVacationType) {
	long processId = 0;
	int mode;
	String screenName = "";
	if (categoryId == CategoriesEnum.OFFICERS.getCode()) {
	    mode = 1;

	    if (vacationTypeId == VacationTypesEnum.REGULAR.getCode()) {
		if (requestType == RequestTypesEnum.NEW.getCode()) {
		    processId = WFProcessesEnum.OFFICERS_REGULAR_VACATION.getCode();
		    screenName = "RegularVacation";
		} else if (requestType == RequestTypesEnum.MODIFY.getCode()) {
		    processId = WFProcessesEnum.MODIFY_OFFICERS_REGULAR_VACATION.getCode();
		    screenName = "ModifyRegularVacation";
		} else if (requestType == RequestTypesEnum.CANCEL.getCode()) {
		    processId = WFProcessesEnum.CANCEL_OFFICERS_REGULAR_VACATION.getCode();
		    screenName = "CancelRegularVacation";
		}
	    } else if (vacationTypeId == VacationTypesEnum.COMPELLING.getCode()) {
		if (requestType == RequestTypesEnum.NEW.getCode()) {
		    processId = WFProcessesEnum.OFFICERS_COMPELLING_VACATION.getCode();
		    screenName = "CompellingVacation";
		} else if (requestType == RequestTypesEnum.CANCEL.getCode()) {
		    processId = WFProcessesEnum.CANCEL_OFFICERS_COMPELLING_VACATION.getCode();
		    screenName = "CancelCompellingVacation";
		}
	    } else if (vacationTypeId == VacationTypesEnum.SICK.getCode()) {
		if (requestType == RequestTypesEnum.NEW.getCode()) {
		    processId = WFProcessesEnum.OFFICERS_SICK_VACATION.getCode();
		    screenName = "SickVacation";
		} else if (requestType == RequestTypesEnum.MODIFY.getCode()) {
		    processId = WFProcessesEnum.MODIFY_OFFICERS_SICK_VACATION.getCode();
		    screenName = "ModifySickVacation";
		} else if (requestType == RequestTypesEnum.CANCEL.getCode()) {
		    processId = WFProcessesEnum.CANCEL_OFFICERS_SICK_VACATION.getCode();
		    screenName = "CancelSickVacation";
		}
	    } else if (vacationTypeId == VacationTypesEnum.FIELD.getCode()) {
		if (requestType == RequestTypesEnum.NEW.getCode()) {
		    processId = WFProcessesEnum.OFFICERS_FIELD_VACATION.getCode();
		    screenName = "FieldVacation";
		} else if (requestType == RequestTypesEnum.CANCEL.getCode()) {
		    processId = WFProcessesEnum.CANCEL_OFFICERS_FIELD_VACATION.getCode();
		    screenName = "CancelFieldVacation";
		}
	    } else if (vacationTypeId == VacationTypesEnum.EXCEPTIONAL.getCode()) {
		if (requestType == RequestTypesEnum.NEW.getCode()) {
		    processId = WFProcessesEnum.OFFICERS_EXCEPTIONAL_VACATION.getCode();
		    screenName = "ExceptionalVacation";
		} else if (requestType == RequestTypesEnum.CANCEL.getCode()) {
		    processId = WFProcessesEnum.CANCEL_OFFICERS_EXCEPTIONAL_VACATION.getCode();
		    screenName = "CancelExceptionalVacation";
		}
	    }
	} else if (categoryId == CategoriesEnum.SOLDIERS.getCode()) {
	    mode = 2;

	    if (vacationTypeId == VacationTypesEnum.REGULAR.getCode()) {
		if (requestType == RequestTypesEnum.NEW.getCode()) {
		    processId = WFProcessesEnum.SOLDIERS_REGULAR_VACATION.getCode();
		    screenName = "RegularVacation";
		} else if (requestType == RequestTypesEnum.MODIFY.getCode()) {
		    processId = WFProcessesEnum.MODIFY_SOLDIERS_REGULAR_VACATION.getCode();
		    screenName = "ModifyRegularVacation";
		} else if (requestType == RequestTypesEnum.CANCEL.getCode()) {
		    processId = WFProcessesEnum.CANCEL_SOLDIERS_REGULAR_VACATION.getCode();
		    screenName = "CancelRegularVacation";
		}
	    } else if (vacationTypeId == VacationTypesEnum.COMPELLING.getCode()) {
		if (requestType == RequestTypesEnum.NEW.getCode()) {
		    processId = WFProcessesEnum.SOLDIERS_COMPELLING_VACATION.getCode();
		    screenName = "CompellingVacation";
		} else if (requestType == RequestTypesEnum.CANCEL.getCode()) {
		    processId = WFProcessesEnum.CANCEL_SOLDIERS_COMPELLING_VACATION.getCode();
		    screenName = "CancelCompellingVacation";
		}
	    } else if (vacationTypeId == VacationTypesEnum.SICK.getCode()) {
		if (subVacationType == SubVacationTypesEnum.SUB_TYPE_ONE.getCode()) {
		    if (requestType == RequestTypesEnum.NEW.getCode()) {
			processId = WFProcessesEnum.SOLDIERS_SICK_VACATION.getCode();
			screenName = "SickVacation";
		    } else if (requestType == RequestTypesEnum.MODIFY.getCode()) {
			processId = WFProcessesEnum.MODIFY_SOLDIERS_SICK_VACATION.getCode();
			screenName = "ModifySickVacation";
		    } else if (requestType == RequestTypesEnum.CANCEL.getCode()) {
			processId = WFProcessesEnum.CANCEL_SOLDIERS_SICK_VACATION.getCode();
			screenName = "CancelSickVacation";
		    }
		} else if (subVacationType == SubVacationTypesEnum.SUB_TYPE_TWO.getCode()) {
		    mode = 22;
		    if (requestType == RequestTypesEnum.NEW.getCode()) {
			processId = WFProcessesEnum.SOLDIERS_WORK_INJURY_SICK_VACATION.getCode();
			screenName = "SickVacation";
		    } else if (requestType == RequestTypesEnum.MODIFY.getCode()) {
			processId = WFProcessesEnum.MODIFY_SOLDIERS_WORK_INJURY_SICK_VACATION.getCode();
			screenName = "ModifySickVacation";
		    } else if (requestType == RequestTypesEnum.CANCEL.getCode()) {
			processId = WFProcessesEnum.CANCEL_SOLDIERS_WORK_INJURY_SICK_VACATION.getCode();
			screenName = "CancelSickVacation";
		    }
		}
	    } else if (vacationTypeId == VacationTypesEnum.FIELD.getCode()) {
		if (requestType == RequestTypesEnum.NEW.getCode()) {
		    processId = WFProcessesEnum.SOLDIERS_FIELD_VACATION.getCode();
		    screenName = "FieldVacation";
		} else if (requestType == RequestTypesEnum.CANCEL.getCode()) {
		    processId = WFProcessesEnum.CANCEL_SOLDIERS_FIELD_VACATION.getCode();
		    screenName = "CancelFieldVacation";
		}
	    } else if (vacationTypeId == VacationTypesEnum.EXCEPTIONAL.getCode()) {
		if (requestType == RequestTypesEnum.NEW.getCode()) {
		    processId = WFProcessesEnum.SOLDIERS_EXCEPTIONAL_VACATION.getCode();
		    screenName = "ExceptionalVacation";
		} else if (requestType == RequestTypesEnum.CANCEL.getCode()) {
		    processId = WFProcessesEnum.CANCEL_SOLDIERS_EXCEPTIONAL_VACATION.getCode();
		    screenName = "CancelExceptionalVacation";
		}
	    }
	} else {
	    mode = 3;

	    if (vacationTypeId == VacationTypesEnum.REGULAR.getCode()) {
		if (requestType == RequestTypesEnum.NEW.getCode()) {
		    processId = WFProcessesEnum.EMPLOYEES_REGULAR_VACATION.getCode();
		    screenName = "RegularVacation";
		} else if (requestType == RequestTypesEnum.MODIFY.getCode()) {
		    processId = WFProcessesEnum.MODIFY_EMPLOYEES_REGULAR_VACATION.getCode();
		    screenName = "ModifyRegularVacation";
		} else if (requestType == RequestTypesEnum.CANCEL.getCode()) {
		    processId = WFProcessesEnum.CANCEL_EMPLOYEES_REGULAR_VACATION.getCode();
		    screenName = "CancelRegularVacation";
		}
	    } else if (vacationTypeId == VacationTypesEnum.COMPELLING.getCode()) {
		if (requestType == RequestTypesEnum.NEW.getCode()) {
		    processId = WFProcessesEnum.EMPLOYEES_COMPELLING_VACATION.getCode();
		    screenName = "CompellingVacation";
		} else if (requestType == RequestTypesEnum.CANCEL.getCode()) {
		    processId = WFProcessesEnum.CANCEL_EMPLOYEES_COMPELLING_VACATION.getCode();
		    screenName = "CancelCompellingVacation";
		}
	    } else if (vacationTypeId == VacationTypesEnum.SICK.getCode()) {
		if (requestType == RequestTypesEnum.NEW.getCode()) {
		    processId = WFProcessesEnum.EMPLOYEES_SICK_VACATION.getCode();
		    screenName = "SickVacation";
		} else if (requestType == RequestTypesEnum.MODIFY.getCode()) {
		    processId = WFProcessesEnum.MODIFY_EMPLOYEES_SICK_VACATION.getCode();
		    screenName = "ModifySickVacation";
		} else if (requestType == RequestTypesEnum.CANCEL.getCode()) {
		    processId = WFProcessesEnum.CANCEL_EMPLOYEES_SICK_VACATION.getCode();
		    screenName = "CancelSickVacation";
		}
	    } else if (vacationTypeId == VacationTypesEnum.EXCEPTIONAL.getCode()) {
		if (subVacationType == SubVacationTypesEnum.SUB_TYPE_ONE.getCode()) {
		    mode = 31;
		    if (requestType == RequestTypesEnum.NEW.getCode()) {
			processId = WFProcessesEnum.EMPLOYEES_EXCEPTIONAL_FOR_CIRCUMSTANCES_VACATION.getCode();
			screenName = "ExceptionalVacation";
		    } else if (requestType == RequestTypesEnum.CANCEL.getCode()) {
			processId = WFProcessesEnum.CANCEL_EMPLOYEES_EXCEPTIONAL_FOR_CIRCUMSTANCES_VACATION.getCode();
			screenName = "CancelExceptionalVacation";
		    }
		} else if (subVacationType == SubVacationTypesEnum.SUB_TYPE_TWO.getCode()) {
		    mode = 32;
		    if (requestType == RequestTypesEnum.NEW.getCode()) {
			processId = WFProcessesEnum.EMPLOYEES_EXCEPTIONAL_FOR_ACCOMPANY_VACATION.getCode();
			screenName = "ExceptionalVacation";
		    } else if (requestType == RequestTypesEnum.CANCEL.getCode()) {
			processId = WFProcessesEnum.CANCEL_EMPLOYEES_EXCEPTIONAL_FOR_ACCOMPANY_VACATION.getCode();
			screenName = "CancelExceptionalVacation";
		    }
		}
	    }
	}

	String taskUrl = "/Vacations/" + screenName + ".jsf?mode=" + mode + "&rootOpened=" + ETRConfigurationService.getFollowingProcessesMenuId();

	return processId + "," + taskUrl;
    }

    /************************************************* Singular Actions ********************************************************/

    /*------------------------------------------------ Actions ------------------------------------------------------*/

    @WebMethod(operationName = "doVacationDM")
    @WebResult(name = "wfVacationDMResponse")
    public WSResponseBase doVacationDM(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "taskId") long taskId, @WebParam(name = "isApproved") boolean isApproved, @WebParam(name = "refuseReasons") String refuseReasons) {

	WSResponseBase response = new WSResponseBase();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    BaseWorkFlow.validateWFTaskRefuseReasonsAndNotes(isApproved ? WFActionFlagsEnum.APPROVE.getCode() : WFActionFlagsEnum.REJECT.getCode(), refuseReasons, null);

	    WFTask dmTask = BaseWorkFlow.getWFTaskById(taskId);
	    if (employeeId != dmTask.getAssigneeId() || dmTask.getAction() != null || !dmTask.getAssigneeWfRole().equals(WFTaskRolesEnum.DIRECT_MANAGER.getCode()))
		// TODO: change error message
		throw new BusinessException("error_integrationError");

	    if (!isApproved)
		dmTask.setRefuseReasons(refuseReasons);

	    WFInstance instance = BaseWorkFlow.getWFInstanceById(dmTask.getInstanceId());
	    EmployeeData requester = EmployeesService.getEmployeeData(instance.getRequesterId());
	    WFVacation vacRequest = VacationsWorkFlow.getWFVacationByInstanceId(instance.getInstanceId());
	    EmployeeData vacBeneficiary = EmployeesService.getEmployeeData(vacRequest.getBeneficiaryId());

	    VacationsWorkFlow.doVacationDM(requester, vacBeneficiary, instance, vacRequest, dmTask, isApproved);

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "doVacationSM")
    @WebResult(name = "wfVacationSMResponse")
    public WSResponseBase doVacationSM(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "taskId") long taskId, @WebParam(name = "approvalFlag") int approvalFlag,
	    @WebParam(name = "notes") String notes, @WebParam(name = "refuseReasons") String refuseReasons) {

	WSResponseBase response = new WSResponseBase();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    BaseWorkFlow.validateWFTaskRefuseReasonsAndNotes(approvalFlag, refuseReasons, notes);

	    WFTask smTask = BaseWorkFlow.getWFTaskById(taskId);
	    if (employeeId != smTask.getAssigneeId() || smTask.getAction() != null || !smTask.getAssigneeWfRole().equals(WFTaskRolesEnum.SIGN_MANAGER.getCode()))
		// TODO: change error message
		throw new BusinessException("error_integrationError");

	    if (notes != null && !notes.trim().isEmpty())
		smTask.setNotes(notes);

	    if (approvalFlag == WFActionFlagsEnum.REJECT.getCode())
		smTask.setRefuseReasons(refuseReasons);

	    WFInstance instance = BaseWorkFlow.getWFInstanceById(smTask.getInstanceId());
	    EmployeeData requester = EmployeesService.getEmployeeData(instance.getRequesterId());
	    WFVacation vacRequest = VacationsWorkFlow.getWFVacationByInstanceId(instance.getInstanceId());
	    EmployeeData vacBeneficiary = EmployeesService.getEmployeeData(vacRequest.getBeneficiaryId());

	    VacationsWorkFlow.doVacationSM(requester, vacBeneficiary, instance, vacRequest, smTask, approvalFlag);

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    /*------------------------------------------------ Queries ------------------------------------------------------*/

    @WebMethod(operationName = "getWFVacation")
    @WebResult(name = "wfVacationResponse")
    public WSWFVacationResponse getWFVacation(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "taskId") long taskId) {

	WSWFVacationResponse response = new WSWFVacationResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    WFTask wfTask = BaseWorkFlow.getWFTaskById(taskId);
	    if (employeeId != wfTask.getAssigneeId() || wfTask.getAction() != null)
		// TODO: change error message
		throw new BusinessException("error_integrationError");

	    WFVacation vacRequest = VacationsWorkFlow.getWFVacationByInstanceId(wfTask.getInstanceId());
	    response.setWfVacation(vacRequest);

	    EmployeeData vacBeneficiary = EmployeesService.getEmployeeData(vacRequest.getBeneficiaryId());
	    response.setEmployeeNameAndRank(vacBeneficiary.getRankDesc() + "/ " + vacBeneficiary.getName());
	    response.setEmployeeJobDescription(vacBeneficiary.getJobDesc());
	    response.setEmployeePhysicalUnitFullName(vacBeneficiary.getPhysicalUnitFullName());

	    if (vacRequest.getRequestType() == RequestTypesEnum.MODIFY.getCode()) {
		Vacation lastVacation = VacationsService.getVacationById(vacRequest.getOldVacationId());
		response.setLastVacationPeriod(lastVacation.getPeriod());
	    }

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    /************************************************* Collective Actions ******************************************************/

    /*------------------------------------------------ Actions ------------------------------------------------------*/

    @WebMethod(operationName = "doVacationsCollectiveDM")
    @WebResult(name = "vacationsCollectiveDMResponse")
    public WSResponseBase doVacationsCollectiveDM(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "tasksIds") String tasksIds) {

	return doVacationsCollectiveAction(sessionId, employeeId, tasksIds, 1);
    }

    @WebMethod(operationName = "doVacationsCollectiveSM")
    @WebResult(name = "vacationsCollectiveSMResponse")
    public WSResponseBase doVacationsCollectiveSM(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "tasksIds") String tasksIds) {

	return doVacationsCollectiveAction(sessionId, employeeId, tasksIds, 2);
    }

    private WSResponseBase doVacationsCollectiveAction(String sessionId, long employeeId, String tasksIds, int actionTypeFlag) {

	WSResponseBase response = new WSResponseBase();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    tasksIds = "," + tasksIds + ",";
	    ArrayList<Object> vacationsTasks = (ArrayList<Object>) VacationsWorkFlow.getWFVacationsTasks(employeeId, actionTypeFlag == 1 ? WFTaskRolesEnum.DIRECT_MANAGER.getCode() : WFTaskRolesEnum.SIGN_MANAGER.getCode());
	    List<Object> selectedVacationsTasks = new ArrayList<Object>();
	    for (Object vacationTaskObject : vacationsTasks) {
		if (tasksIds.contains("," + ((WFTask) ((Object[]) vacationTaskObject)[1]).getTaskId() + ","))
		    selectedVacationsTasks.add(vacationTaskObject);
	    }

	    if (selectedVacationsTasks.isEmpty())
		// TODO: change error message
		throw new BusinessException("error_integrationError");

	    VacationsWorkFlow.doVacationsCollectiveAction(selectedVacationsTasks, actionTypeFlag);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    if (e instanceof BusinessException) {
		response.setMessage(BaseService.getParameterizedMessage(e.getMessage(), ((BusinessException) e).getParams()));
	    } else {
		response.setMessage(BaseService.getMessage("error_integrationError"));
		e.printStackTrace();
	    }
	}
	return response;
    }

    /*------------------------------------------------ Queries ------------------------------------------------------*/

    @WebMethod(operationName = "getWFVacationsDMTasks")
    @WebResult(name = "wfVacationsDMTasksResponse")
    public WSWFVacationsTasksResponse getWFVacationsDMTasks(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId) {

	return getWFVacationsTasks(sessionId, employeeId, 1);
    }

    @WebMethod(operationName = "getWFVacationsSMTasks")
    @WebResult(name = "wfVacationsSMTasksResponse")
    public WSWFVacationsTasksResponse getWFVacationsSMTasks(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId) {

	return getWFVacationsTasks(sessionId, employeeId, 2);
    }

    private WSWFVacationsTasksResponse getWFVacationsTasks(String sessionId, long employeeId, int actionTypeFlag) {

	WSWFVacationsTasksResponse response = new WSWFVacationsTasksResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    // object[0] - WFVacation
	    // object[1] - WFTask
	    // object[2] - WFInstance
	    // object[3] - processName
	    // object[4] - requester
	    // object[5] - beneficiary
	    // object[6] - delegatingName
	    ArrayList<Object> vacationsTasks = (ArrayList<Object>) VacationsWorkFlow.getWFVacationsTasks(employeeId, actionTypeFlag == 1 ? WFTaskRolesEnum.DIRECT_MANAGER.getCode() : WFTaskRolesEnum.SIGN_MANAGER.getCode());
	    List<WSWFVacationTaskInfo> wfVacationsTasksList = new ArrayList<WSWFVacationTaskInfo>();

	    for (Object vacationTaskObject : vacationsTasks) {
		Object[] vacationTaskObjectArray = (Object[]) vacationTaskObject;
		WSWFVacationTaskInfo vacationTask = new WSWFVacationTaskInfo();

		WFVacation wfVacation = ((WFVacation) vacationTaskObjectArray[0]);
		vacationTask.setPeriod(wfVacation.getPeriod());
		vacationTask.setStartDateString(wfVacation.getStartDateString());
		vacationTask.setLocation(wfVacation.getLocation());

		vacationTask.setTaskId(((WFTask) vacationTaskObjectArray[1]).getTaskId());
		vacationTask.setProcessId(((WFInstance) vacationTaskObjectArray[2]).getProcessId());
		vacationTask.setProcessName((String) vacationTaskObjectArray[3]);
		vacationTask.setRequesterName(((EmployeeData) vacationTaskObjectArray[4]).getName());
		vacationTask.setBeneficiaryName(((EmployeeData) vacationTaskObjectArray[5]).getName());
		vacationTask.setDelegatingName((String) vacationTaskObjectArray[6]);

		wfVacationsTasksList.add(vacationTask);
	    }

	    response.setWfVacationsTasksList(wfVacationsTasksList);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_integrationError"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    private boolean isEmployeeAuthorizedRequestVacation(EmployeeData requester, EmployeeData beneficiary, WFVacation vacRequest) throws BusinessException {
	boolean isAuthorized = false;

	if (beneficiary.getPhysicalRegionId().longValue() == requester.getPhysicalRegionId().longValue()) {

	    long beneficiaryCategoryId = beneficiary.getCategoryId();
	    long vacationTypeId = vacRequest.getVacationTypeId();

	    if (CategoriesEnum.OFFICERS.getCode() == beneficiaryCategoryId) {
		if (VacationTypesEnum.REGULAR.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_OFFICERS_REGULAR_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_OFFICERS.getCode());
		} else if (VacationTypesEnum.COMPELLING.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_OFFICERS_COMPELLING_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_OFFICERS.getCode());
		} else if (VacationTypesEnum.SICK.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_OFFICERS_SICK_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_OFFICERS.getCode());
		} else if (VacationTypesEnum.FIELD.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_OFFICERS_FIELD_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_OFFICERS.getCode());
		} else if (VacationTypesEnum.EXCEPTIONAL.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_OFFICERS_EXCEPTIONAL_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_OFFICERS.getCode());
		}
	    } else if (CategoriesEnum.SOLDIERS.getCode() == beneficiaryCategoryId) {
		if (VacationTypesEnum.REGULAR.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_SOLDIERS_REGULAR_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_SOLDIERS.getCode());
		} else if (VacationTypesEnum.COMPELLING.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_SOLDIERS_COMPELLING_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_SOLDIERS.getCode());
		} else if (VacationTypesEnum.SICK.getCode() == vacationTypeId) {
		    if (SubVacationTypesEnum.SUB_TYPE_ONE.getCode() == vacRequest.getSubVacationType())
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_SOLDIERS_SICK_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_SOLDIERS.getCode());
		    else if (SubVacationTypesEnum.SUB_TYPE_TWO.getCode() == vacRequest.getSubVacationType())
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_SOLDIERS_WORK_INJURY_SICK_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_SOLDIERS.getCode());
		} else if (VacationTypesEnum.FIELD.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_SOLDIERS_FIELD_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_SOLDIERS.getCode());
		} else if (VacationTypesEnum.EXCEPTIONAL.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_SOLDIERS_EXCEPTIONAL_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_SOLDIERS.getCode());
		} else if (VacationTypesEnum.EXAM.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_SOLDIERS_EXAM_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_SOLDIERS.getCode());
		} else if (VacationTypesEnum.MATERNITY.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_SOLDIERS_MATERNITY_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_SOLDIERS.getCode());
		} else if (VacationTypesEnum.MOTHERHOOD.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_SOLDIERS_MOTHERHOOD_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_SOLDIERS.getCode());
		} else if (VacationTypesEnum.DEATH_WAITING_PERIOD.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_SOLDIERS_DEATH_WAITING_PERIOD_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_SOLDIERS.getCode());
		}
	    } else if (CategoriesEnum.CONTRACTORS.getCode() == beneficiaryCategoryId) {

		if (VacationTypesEnum.REGULAR.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_REGULAR_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		} else if (VacationTypesEnum.COMPELLING.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_CONTRACTORS_WITHOUT_SALARY_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		} else if (VacationTypesEnum.SICK.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_SICK_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		}
	    } else if (CategoriesEnum.PERSONS.getCode() == beneficiaryCategoryId || CategoriesEnum.USERS.getCode() == beneficiaryCategoryId
		    || CategoriesEnum.WAGES.getCode() == beneficiaryCategoryId || CategoriesEnum.MEDICAL_STAFF.getCode() == beneficiaryCategoryId) {

		if (VacationTypesEnum.REGULAR.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_REGULAR_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		} else if (VacationTypesEnum.COMPELLING.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_COMPELLING_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		} else if (VacationTypesEnum.SICK.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_SICK_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		} else if (VacationTypesEnum.EXCEPTIONAL.getCode() == vacationTypeId) {
		    if (SubVacationTypesEnum.SUB_TYPE_ONE.getCode() == vacRequest.getSubVacationType())
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_EXCEPTIONAL_FOR_CIRCUMSTANCES_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		    else if (SubVacationTypesEnum.SUB_TYPE_TWO.getCode() == vacRequest.getSubVacationType())
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_EXCEPTIONAL_FOR_ACCOMPANY_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		} else if (VacationTypesEnum.STUDY.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_STUDY_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		} else if (VacationTypesEnum.EXAM.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_EXAM_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		} else if (VacationTypesEnum.MATERNITY.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_MATERNITY_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		} else if (VacationTypesEnum.MOTHERHOOD.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_MOTHERHOOD_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		} else if (VacationTypesEnum.DEATH_WAITING_PERIOD.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_DEATH_WAITING_PERIOD_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		} else if (VacationTypesEnum.RELIEF.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_RELIEF_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		} else if (VacationTypesEnum.SPORTIVE.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_SPORTIVE_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		} else if (VacationTypesEnum.CULTURAL.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_CULTURAL_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		} else if (VacationTypesEnum.SOCIAL.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_SOCIAL_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		} else if (VacationTypesEnum.COMPENSATION.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_COMPENSATION_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		} else if (VacationTypesEnum.ORPHAN_CARE.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_ORPHAN_CARE_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		} else if (VacationTypesEnum.DEATH_OF_RELATIVE.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_DEATH_OF_RELATIVE_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		} else if (VacationTypesEnum.NEW_BABY.getCode() == vacationTypeId) {
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_EMPLOYEES_NEW_BABY_VACATION.getCode(), MenuActionsEnum.VAC_VACATION_REQUEST_ALL_EMPLOYEES.getCode());
		}
	    }
	}
	return isAuthorized;
    }

    private boolean isEmployeeAuthorizedRequestModifyOrCancelVacation(EmployeeData requester, EmployeeData beneficiary, WFVacation vacRequest) throws BusinessException {
	boolean isAuthorized = false;
	if (beneficiary.getPhysicalRegionId().longValue() == requester.getPhysicalRegionId().longValue()) {

	    long beneficiaryCategoryId = beneficiary.getCategoryId();
	    long vacationTypeId = vacRequest.getVacationTypeId();

	    if (VacationTypesEnum.REGULAR.getCode() == vacationTypeId) {
		if (CategoriesEnum.OFFICERS.getCode() == beneficiaryCategoryId) {
		    if (vacRequest.getRequestType() == RequestTypesEnum.MODIFY.getCode())
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_BENF_OFFICERS_MODIFY_REGULAR_VACATION.getCode(), MenuActionsEnum.VAC_REGULAR_MODIFY_REQUEST_BENF_OFFICERS.getCode());

		    else if (vacRequest.getRequestType() == RequestTypesEnum.CANCEL.getCode())
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_BENF_OFFICERS_CANCEL_REGULAR_VACATION.getCode(), MenuActionsEnum.VAC_REGULAR_CANCEL_REQUEST_BENF_OFFICERS.getCode());
		} else if (CategoriesEnum.SOLDIERS.getCode() == beneficiaryCategoryId) {

		    if (vacRequest.getRequestType() == RequestTypesEnum.MODIFY.getCode())
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_BENF_SOLDIERS_MODIFY_REGULAR_VACATION.getCode(), MenuActionsEnum.VAC_REGULAR_MODIFY_REQUEST_BENF_SOLDIERS.getCode());

		    else if (vacRequest.getRequestType() == RequestTypesEnum.CANCEL.getCode())
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_BENF_SOLDIERS_CANCEL_REGULAR_VACATION.getCode(), MenuActionsEnum.VAC_REGULAR_CANCEL_REQUEST_BENF_SOLDIERS.getCode());

		} else if (CategoriesEnum.PERSONS.getCode() == beneficiaryCategoryId || CategoriesEnum.USERS.getCode() == beneficiaryCategoryId
			|| CategoriesEnum.WAGES.getCode() == beneficiaryCategoryId || CategoriesEnum.MEDICAL_STAFF.getCode() == beneficiaryCategoryId
			|| CategoriesEnum.CONTRACTORS.getCode() == beneficiaryCategoryId) {
		    if (vacRequest.getRequestType() == RequestTypesEnum.MODIFY.getCode())
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_MODIFY_REGULAR_VACATION.getCode(), MenuActionsEnum.VAC_REGULAR_MODIFY_REQUEST_BENF_EMPLOYEES.getCode());

		    else if (vacRequest.getRequestType() == RequestTypesEnum.CANCEL.getCode())
			isAuthorized = SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.VAC_BENF_EMPLOYEES_CANCEL_REGULAR_VACATION.getCode(), MenuActionsEnum.VAC_REGULAR_CANCEL_REQUEST_BENF_EMPLOYEES.getCode());
		}
	    }
	}

	return isAuthorized;
    }

}