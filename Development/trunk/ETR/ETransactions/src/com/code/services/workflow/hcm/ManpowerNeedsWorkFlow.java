package com.code.services.workflow.hcm;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.jobs.ManpowerNeedData;
import com.code.dal.orm.hcm.organization.jobs.ManpowerNeedDetailData;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFPosition;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.hcm.organization.jobs.WFManpowerNeed;
import com.code.enums.ManpowerNeedStatusEnum;
import com.code.enums.ManpowerNeedTypesEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.UnitTypesEnum;
import com.code.enums.WFInstanceStatusEnum;
import com.code.enums.WFPositionsEnum;
import com.code.enums.WFTaskActionsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.ManpowerNeedsService;
import com.code.services.hcm.UnitsService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.BaseWorkFlow;

public class ManpowerNeedsWorkFlow extends BaseWorkFlow {

    private ManpowerNeedsWorkFlow() {

    }

    /********************************************* Manpower Needs Request WorkFlow ************************************************/
    /*--------------------------------------------------------- Steps ------------------------------------------------------------*/

    public static void initManpowerNeedRequest(EmployeeData requester, ManpowerNeedData manpowerNeedRequestData, List<ManpowerNeedDetailData> manpowerNeedDetailsData, long processId, String taskUrl) throws BusinessException {

	ManpowerNeedsService.validateManpowerNeedRequest(manpowerNeedRequestData, manpowerNeedDetailsData);
	validateManpowerNeedsRunningRequests(manpowerNeedRequestData, manpowerNeedDetailsData);

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    // add the manpower need request and its details data
	    ManpowerNeedsService.saveManpowerNeedRequest(manpowerNeedRequestData, manpowerNeedDetailsData, requester.getEmpId());

	    // add the request instance
	    WFInstance instance = addWFInstance(processId, requester.getEmpId(), curDate, curHijriDate, WFInstanceStatusEnum.RUNNING.getCode(), null, Arrays.asList(requester.getEmpId()), session);

	    // add the first task of the added instance
	    addWFTask(instance.getInstanceId(), getDelegate(requester.getManagerId(), processId, requester.getEmpId()), requester.getManagerId(), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);

	    // add the workflow request
	    WFManpowerNeed manpowerNeedRequest = new WFManpowerNeed();
	    manpowerNeedRequest.setInstanceId(instance.getInstanceId());
	    manpowerNeedRequest.setManpowerNeedId(manpowerNeedRequestData.getId());
	    DataAccess.addEntity(manpowerNeedRequest, session);

	    session.commitTransaction();
	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    // Direct Manager
    public static void doManpowerNeedRequestDM(EmployeeData requester, WFInstance instance, ManpowerNeedData manpowerNeedRequestData, WFTask dmTask, boolean isApproved) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (isApproved) {
		EmployeeData curDM = EmployeesService.getEmployeeData(dmTask.getOriginalId());

		// The request is related to the general region
		if (RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == manpowerNeedRequestData.getRequestingRegionId()) {
		    if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
			// if the current direct manager is related to the general manager, approve the request and send it to the manager redirect
			ManpowerNeedsService.updateManpowerNeedData(manpowerNeedRequestData, manpowerNeedRequestData.getStatus(), dmTask.getOriginalId(), null, dmTask.getAssigneeId(), session);

			EmployeeData manpowerOrganizationDepartmentManager = getManpowerOrganizationDepartmentManager(manpowerNeedRequestData.getRequestingRegionId());
			completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(manpowerOrganizationDepartmentManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), manpowerOrganizationDepartmentManager.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
		    } else {
			// if the current direct manager is not related to the general manager, send it to the next direct manager
			completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
		    }
		}
		// The request is related to another region
		else {
		    if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
			// if the current direct manager is related to the region commander, approve the request and send it to the manager redirect
			ManpowerNeedsService.updateManpowerNeedData(manpowerNeedRequestData, manpowerNeedRequestData.getStatus(), dmTask.getOriginalId(), null, dmTask.getAssigneeId(), session);

			EmployeeData manpowerOrganizationDepartmentManager = getManpowerOrganizationDepartmentManager(manpowerNeedRequestData.getRequestingRegionId());
			completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(manpowerOrganizationDepartmentManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), manpowerOrganizationDepartmentManager.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
		    } else {
			// if the current direct manager is not related to the region commander, send it to the next direct manager
			completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
		    }
		}
	    } else {
		closeManpowerNeedWorkFlowByRejection(requester, instance, manpowerNeedRequestData, dmTask, session);
	    }

	    session.commitTransaction();
	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    // Manager Redirect
    public static void doManpowerNeedRequestMR(EmployeeData requester, WFInstance instance, WFTask mrTask, long reviewerEmpId) throws BusinessException {
	try {
	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    completeWFTask(mrTask, WFTaskActionsEnum.REDIRECT.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(reviewerEmpId, instance.getProcessId(), requester.getEmpId()), reviewerEmpId, mrTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), "1");
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    // Reviewer Employee
    public static void doManpowerNeedRequestRE(EmployeeData requester, WFInstance instance, ManpowerNeedData manpowerNeedRequestData, List<ManpowerNeedDetailData> manpowerNeedDetailsData, WFTask reTask, boolean isApproved) throws BusinessException {

	if (isApproved)
	    ManpowerNeedsService.validateManpowerNeedRequestSuggestedCounts(manpowerNeedDetailsData);

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (manpowerNeedRequestData.getAttachments() != null && !manpowerNeedRequestData.getAttachments().isEmpty())
		ManpowerNeedsService.updateAttachments(manpowerNeedRequestData, session);

	    if (isApproved) {
		ManpowerNeedsService.updateManpowerNeedRequestSuggestedCounts(manpowerNeedDetailsData, reTask.getAssigneeId(), session);

		EmployeeData reviewerEmployee = EmployeesService.getEmployeeData(reTask.getOriginalId());

		// send the request to the sign manager
		completeWFTask(reTask, WFTaskActionsEnum.REVIEW.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(reviewerEmployee.getManagerId(), instance.getProcessId(), requester.getEmpId()), reviewerEmployee.getManagerId(), reTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
	    } else {
		closeManpowerNeedWorkFlowByRejection(requester, instance, manpowerNeedRequestData, reTask, session);
	    }
	    session.commitTransaction();
	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    // Sign Manager
    // approvalFlag: 1:submit to study, 0:reject, 2:returnReviewer
    public static void doManpowerNeedRequestSM(EmployeeData requester, WFInstance instance, ManpowerNeedData manpowerNeedRequestData, WFTask smTask, int approvalFlag) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (approvalFlag == 1) {

		EmployeeData curSM = EmployeesService.getEmployeeData(smTask.getOriginalId());

		// The request is related to the general region
		if (RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == manpowerNeedRequestData.getRequestingRegionId()) {
		    if (EmployeesService.getEmployeeDirectManager(curSM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
			// if the current sign manager is related to the general manager, approve the request
			closeManpowerNeedWorkFlowBySubmittingToStudy(requester, instance, manpowerNeedRequestData, smTask, session);
		    } else {
			// if the current sign manager is not related to the general manager, send it to the next sign manager
			completeWFTask(smTask, WFTaskActionsEnum.SUBMIT_TO_STUDY.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curSM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curSM.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
		    }
		}
		// The request is related to another region
		else {
		    if (EmployeesService.getEmployeeDirectManager(curSM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
			// if the current sign manager is related to the region commander, approve the request
			closeManpowerNeedWorkFlowBySubmittingToStudy(requester, instance, manpowerNeedRequestData, smTask, session);
		    } else {
			// if the current sign manager is not related to the region commander, send it to the next sign manager
			completeWFTask(smTask, WFTaskActionsEnum.SUBMIT_TO_STUDY.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curSM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curSM.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
		    }
		}
	    } else if (approvalFlag == 2) {
		WFTask reviewerTask = getWFInstanceTasksByRole(instance.getInstanceId(), WFTaskRolesEnum.REVIEWER_EMP.getCode()).get(0);
		completeWFTask(smTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(reviewerTask.getOriginalId(), instance.getProcessId(), requester.getEmpId()), reviewerTask.getOriginalId(), smTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), "1", session);
	    } else {
		closeManpowerNeedWorkFlowByRejection(requester, instance, manpowerNeedRequestData, smTask, session);
	    }

	    session.commitTransaction();
	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    session.rollbackTransaction();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    /*--------------------------------------------------------- Validations ------------------------------------------------------*/

    private static void validateManpowerNeedsRunningRequests(ManpowerNeedData manpowerNeedData, List<ManpowerNeedDetailData> manpowerNeedDetailsData) throws BusinessException {

	List<String> unitsHKeysList = UnitsService.getAncestorsHkeys(manpowerNeedData.getRequestingUnitHKey(), 1);

	// if there is an under processing request by a specific unit, then it's not allowed to make another request by this unit or its ancestors.
	String[] unitsHKeys = new String[unitsHKeysList.size()];
	if (countUnderProcessingRequestsFromUnitAndAncestors(unitsHKeysList.toArray(unitsHKeys), manpowerNeedData.getCategoryId()) > 0)
	    throw new BusinessException("error_ThereIsAnotherUnderProcessingRequestFromTheUnitOrItsAncestors");

	for (ManpowerNeedDetailData manpowerNeedDetailData : manpowerNeedDetailsData) { // TODO Redundant validation because only one unit will take a privilege to request.
	    // if there is an under processing request for a specific unit, then it's not allowed to make another request for this unit or its children.
	    if (counUnderProcessingRequestsToUnitAndChildren(manpowerNeedDetailData.getUnitHKey(), manpowerNeedData.getCategoryId()) > 0)
		throw new BusinessException("error_ThereIsAnotherUnderProcessingRequestToTheUnitOrItsChildren");
	}
    }

    /*--------------------------------------------------------- Queries ----------------------------------------------------------*/

    private static long countUnderProcessingRequestsFromUnitAndAncestors(String[] unitsHKeys, long categoryId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_UNITS_HKEYS", unitsHKeys);
	    qParams.put("P_CATEGORY_ID", categoryId);
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_UNDER_PROCESSING_MANPOWER_NEED_DATA_REQUESTS_FROM_UNIT.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static long counUnderProcessingRequestsToUnitAndChildren(String unitHKey, long categoryId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_UNIT_HKEY_PREFIX", UnitsService.getHKeyPrefix(unitHKey) + "%");
	    qParams.put("P_CATEGORY_ID", categoryId);
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_UNDER_PROCESSING_MANPOWER_NEED_DATA_REQUESTS_TO_UNIT.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /********************************************* Manpower Needs Study WorkFlow **************************************************/
    /*--------------------------------------------------------- Steps ------------------------------------------------------------*/

    public static void initManpowerNeedStudy(EmployeeData requester, ManpowerNeedData manpowerNeedStudyData, List<ManpowerNeedData> manpowerNeedRequestsAndStudies, Map<Long, List<ManpowerNeedDetailData>> manpowerNeedRequestsAndStudiesDetailsMap, List<ManpowerNeedDetailData> manpowerNeedStudyDetails, long processId, String taskUrl) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    // save the study
	    ManpowerNeedsService.saveManpowerNeedStudy(manpowerNeedStudyData, manpowerNeedRequestsAndStudies, manpowerNeedRequestsAndStudiesDetailsMap, manpowerNeedStudyDetails, requester.getEmpId());

	    // set the status of the manpower need study to be UNDER_PROCESSING
	    ManpowerNeedsService.updateManpowerNeedData(manpowerNeedStudyData, ManpowerNeedStatusEnum.UNDER_PROCESSING.getCode(), null, null, requester.getEmpId(), session);

	    // add the request instance
	    WFInstance instance = addWFInstance(processId, requester.getEmpId(), curDate, curHijriDate, WFInstanceStatusEnum.RUNNING.getCode(), null, Arrays.asList(requester.getEmpId()), session);

	    // add the first task of the added instance
	    if (ManpowerNeedTypesEnum.STUDY.getCode() == manpowerNeedStudyData.getNeedType())
		addWFTask(instance.getInstanceId(), getDelegate(requester.getManagerId(), processId, requester.getEmpId()), requester.getManagerId(), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode(), "1", session);
	    else if (ManpowerNeedTypesEnum.MAIN_STUDY.getCode() == manpowerNeedStudyData.getNeedType())
		addWFTask(instance.getInstanceId(), getDelegate(requester.getManagerId(), processId, requester.getEmpId()), requester.getManagerId(), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);

	    // add the workflow request
	    WFManpowerNeed manpowerNeedStudy = new WFManpowerNeed();
	    manpowerNeedStudy.setInstanceId(instance.getInstanceId());
	    manpowerNeedStudy.setManpowerNeedId(manpowerNeedStudyData.getManpowerNeed().getId());
	    DataAccess.addEntity(manpowerNeedStudy, session);

	    session.commitTransaction();
	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    // Secondary Sign Manager (Study)
    public static void doManpowerNeedStudySSM(EmployeeData requester, WFInstance instance, ManpowerNeedData manpowerNeedStudyData, WFTask ssmTask, int approvalFlag) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (approvalFlag == 1) {
		EmployeeData curSSM = EmployeesService.getEmployeeData(ssmTask.getOriginalId());

		if (curSSM.getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
		    // send the request to the manager redirect if the current secondary sign manager is the region commander
		    EmployeeData manpowerOrganizationDepartmentManager = getManpowerOrganizationDepartmentManager(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
		    completeWFTask(ssmTask, WFTaskActionsEnum.SUBMIT_TO_STUDY.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(manpowerOrganizationDepartmentManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), manpowerOrganizationDepartmentManager.getEmpId(), ssmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
		} else {
		    // send the request to the next secondary sign manager
		    completeWFTask(ssmTask, WFTaskActionsEnum.SUBMIT_TO_STUDY.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curSSM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curSSM.getManagerId(), ssmTask.getTaskUrl(), WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode(), "1", session);
		}
	    } else if (approvalFlag == 2) {
		// return to the requester who is the secondary reviewer employee
		completeWFTask(ssmTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(instance.getRequesterId(), instance.getProcessId(), requester.getEmpId()), instance.getRequesterId(), ssmTask.getTaskUrl(), WFTaskRolesEnum.SECONDARY_REVIEWER_EMP.getCode(), "1", session);
	    } else {
		ManpowerNeedsService.deleteOrRejectManpowerNeedStudy(manpowerNeedStudyData, false, session);
		closeManpowerNeedWorkFlowByRejection(requester, instance, manpowerNeedStudyData, ssmTask, session);
	    }
	    session.commitTransaction();
	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    // Secondary Reviewer Employee (Study)
    public static void doManpowerNeedStudySRE(EmployeeData requester, WFInstance instance, ManpowerNeedData manpowerNeedStudyData, List<ManpowerNeedData> manpowerNeedRequestsAndStudies, Map<Long, List<ManpowerNeedDetailData>> manpowerNeedRequestsAndStudiesDetailsMap, List<ManpowerNeedDetailData> manpowerNeedStudyDetails, WFTask sreTask, boolean isApproved) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (manpowerNeedStudyData.getAttachments() != null && !manpowerNeedStudyData.getAttachments().isEmpty())
		ManpowerNeedsService.updateAttachments(manpowerNeedStudyData, session);

	    if (isApproved) {
		ManpowerNeedsService.saveManpowerNeedStudy(manpowerNeedStudyData, manpowerNeedRequestsAndStudies, manpowerNeedRequestsAndStudiesDetailsMap, manpowerNeedStudyDetails, sreTask.getAssigneeId());

		EmployeeData reviewerEmployee = EmployeesService.getEmployeeData(sreTask.getOriginalId());
		completeWFTask(sreTask, WFTaskActionsEnum.REVIEW.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(reviewerEmployee.getManagerId(), instance.getProcessId(), requester.getEmpId()), reviewerEmployee.getManagerId(), sreTask.getTaskUrl(), WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode(), "1", session);
	    } else {
		closeManpowerNeedWorkFlowByRejection(requester, instance, manpowerNeedStudyData, sreTask, session);
	    }
	    session.commitTransaction();
	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    // Manager Redirect (Study)
    public static void doManpowerNeedStudyMR(EmployeeData requester, WFInstance instance, WFTask mrTask, long reviewerEmpId) throws BusinessException {
	try {
	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    completeWFTask(mrTask, WFTaskActionsEnum.REDIRECT.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(reviewerEmpId, instance.getProcessId(), requester.getEmpId()), reviewerEmpId, mrTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), "1");
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    // Reviewer Employee (Study and Main Study)
    public static void doManpowerNeedStudyRE(EmployeeData requester, WFInstance instance, ManpowerNeedData manpowerNeedStudyData, List<ManpowerNeedData> manpowerNeedRequestsAndStudies, Map<Long, List<ManpowerNeedDetailData>> manpowerNeedRequestsAndStudiesDetailsMap, List<ManpowerNeedDetailData> manpowerNeedStudyDetails, WFTask reTask, boolean isApproved) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (manpowerNeedStudyData.getAttachments() != null && !manpowerNeedStudyData.getAttachments().isEmpty())
		ManpowerNeedsService.updateAttachments(manpowerNeedStudyData, session);

	    if (isApproved) {
		ManpowerNeedsService.saveManpowerNeedStudy(manpowerNeedStudyData, manpowerNeedRequestsAndStudies, manpowerNeedRequestsAndStudiesDetailsMap, manpowerNeedStudyDetails, reTask.getAssigneeId());

		EmployeeData reviewerEmployee = EmployeesService.getEmployeeData(reTask.getOriginalId());
		completeWFTask(reTask, WFTaskActionsEnum.REVIEW.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(reviewerEmployee.getManagerId(), instance.getProcessId(), requester.getEmpId()), reviewerEmployee.getManagerId(), reTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
	    } else {
		closeManpowerNeedWorkFlowByRejection(requester, instance, manpowerNeedStudyData, reTask, session);
	    }
	    session.commitTransaction();
	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    // Sign Manager (Study and Main Study)
    // approvalFlag: 1:approve/submit to study, 0:reject, 2:returnReviewer
    public static void doManpowerNeedStudySM(EmployeeData requester, WFInstance instance, ManpowerNeedData manpowerNeedStudyData, WFTask smTask, int approvalFlag) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (approvalFlag == 1) {

		EmployeeData curSM = EmployeesService.getEmployeeData(smTask.getOriginalId());

		if (EmployeesService.getEmployeeDirectManager(curSM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
		    if (ManpowerNeedTypesEnum.STUDY.getCode() == manpowerNeedStudyData.getNeedType())
			closeManpowerNeedWorkFlowBySubmittingToStudy(requester, instance, manpowerNeedStudyData, smTask, session);
		    else if (ManpowerNeedTypesEnum.MAIN_STUDY.getCode() == manpowerNeedStudyData.getNeedType())
			closeManpowerNeedWorkFlowByApproval(requester, instance, manpowerNeedStudyData, smTask, session);
		} else {
		    String action = WFTaskActionsEnum.SUBMIT_TO_STUDY.getCode(); // Study
		    if (ManpowerNeedTypesEnum.MAIN_STUDY.getCode() == manpowerNeedStudyData.getNeedType())
			action = WFTaskActionsEnum.SUPER_SIGN.getCode(); // Main Study

		    // send the request to the next sign manager
		    completeWFTask(smTask, action, curDate, curHijriDate, instance.getInstanceId(), getDelegate(curSM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curSM.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
		}
	    } else if (approvalFlag == 2) {
		if (ManpowerNeedTypesEnum.STUDY.getCode() == manpowerNeedStudyData.getNeedType()) {
		    // return to the reviewer employee
		    WFTask reviewerTask = getWFInstanceTasksByRole(instance.getInstanceId(), WFTaskRolesEnum.REVIEWER_EMP.getCode()).get(0);
		    completeWFTask(smTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(reviewerTask.getOriginalId(), instance.getProcessId(), requester.getEmpId()), reviewerTask.getOriginalId(), smTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), "1", session);
		} else if (ManpowerNeedTypesEnum.MAIN_STUDY.getCode() == manpowerNeedStudyData.getNeedType()) {
		    // return to the requester who is the reviewer employee
		    completeWFTask(smTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(instance.getRequesterId(), instance.getProcessId(), requester.getEmpId()), instance.getRequesterId(), smTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), "1", session);
		}
	    } else {
		ManpowerNeedsService.deleteOrRejectManpowerNeedStudy(manpowerNeedStudyData, false, session);
		closeManpowerNeedWorkFlowByRejection(requester, instance, manpowerNeedStudyData, smTask, session);
	    }

	    session.commitTransaction();
	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    session.rollbackTransaction();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    /********************************************* Commons ************************************************************************/
    /*-------------------------------------------- Close Workflow ----------------------------------------------------------------*/

    private static void closeManpowerNeedWorkFlowBySubmittingToStudy(EmployeeData requester, WFInstance instance, ManpowerNeedData manpowerNeedData, WFTask finalTask, CustomSession session) throws BusinessException {
	try {
	    // set the status of the request to be SUBMITTED_TO_STUDY and set the decision approved id
	    ManpowerNeedsService.updateManpowerNeedData(manpowerNeedData, ManpowerNeedStatusEnum.SUBMITTED_TO_STUDY.getCode(), manpowerNeedData.getApprovedId(), finalTask.getOriginalId(), finalTask.getAssigneeId(), session);

	    // close the workflow by submitting the request to study
	    closeWFInstanceByAction(requester.getEmpId(), instance, finalTask, WFTaskActionsEnum.SUBMIT_TO_STUDY.getCode(), null, session);
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static void closeManpowerNeedWorkFlowByApproval(EmployeeData requester, WFInstance instance, ManpowerNeedData manpowerNeedData, WFTask finalTask, CustomSession session) throws BusinessException {
	try {
	    // set the status of the request to be APPROVED and set the decision approved id
	    ManpowerNeedsService.updateManpowerNeedData(manpowerNeedData, ManpowerNeedStatusEnum.APPROVED.getCode(), manpowerNeedData.getApprovedId(), finalTask.getOriginalId(), finalTask.getAssigneeId(), session);

	    // close the workflow by approving the request
	    closeWFInstanceByAction(requester.getEmpId(), instance, finalTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), null, session);
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static void closeManpowerNeedWorkFlowByRejection(EmployeeData requester, WFInstance instance, ManpowerNeedData manpowerNeedData, WFTask finalTask, CustomSession session) throws BusinessException {
	try {
	    // set the status of the request to be REJECTED
	    ManpowerNeedsService.updateManpowerNeedData(manpowerNeedData, ManpowerNeedStatusEnum.REJECTED.getCode(), manpowerNeedData.getApprovedId(), null, finalTask.getAssigneeId(), session);

	    // close the workflow by rejecting the request
	    closeWFInstanceByAction(requester.getEmpId(), instance, finalTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*-------------------------------------------- Queries -----------------------------------------------------------------------*/

    public static WFManpowerNeed getWFManpowerNeedByInstanceId(long instanceId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCE_ID", instanceId);
	    List<WFManpowerNeed> result = DataAccess.executeNamedQuery(WFManpowerNeed.class, QueryNamesEnum.WF_GET_WFMANPOWER_NEED_BY_INSTANCE_ID.getCode(), qParams);
	    if (result.isEmpty())
		return null;
	    else
		return result.get(0);
	} catch (Exception e) {
	    throw new BusinessException("error_general");
	}
    }

    /*-------------------------------------------- Utilities ---------------------------------------------------------------------*/

    private static EmployeeData getManpowerOrganizationDepartmentManager(long regionId) throws BusinessException {
	WFPosition position = getWFPosition(regionId == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() ? WFPositionsEnum.PLANNING_MANPOWER_UNIT_MANAGER.getCode() : WFPositionsEnum.REGION_PLANNING_ORGANIZATION_UNIT_MANAGER.getCode(), regionId);
	return EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());
    }
}