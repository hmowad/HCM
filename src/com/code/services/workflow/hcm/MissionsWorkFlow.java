package com.code.services.workflow.hcm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.missions.MissionData;
import com.code.dal.orm.hcm.missions.MissionDetailData;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFPosition;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.hcm.missions.WFMissionData;
import com.code.dal.orm.workflow.hcm.missions.WFMissionDetail;
import com.code.dal.orm.workflow.hcm.missions.WFMissionDetailData;
import com.code.enums.CategoriesEnum;
import com.code.enums.CategoryModesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.ReportNamesEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.UnitTypesEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFInstanceStatusEnum;
import com.code.enums.WFPositionsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskActionsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.buswfcoop.EmployeesTransactionsConflictValidator;
import com.code.services.config.ETRConfigurationService;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.MissionsService;
import com.code.services.hcm.UnitsService;
import com.code.services.security.SecurityService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.BaseWorkFlow;

/**
 * Service to control the flow of missions requests.
 * 
 */

public class MissionsWorkFlow extends BaseWorkFlow {

    /**
     * Private constructor to prevent instantiation
     * 
     */
    private MissionsWorkFlow() {
    }

    /*
     * ********************************** Work flow **********************************
     */

    /**
     * Starts mission request and do the following .
     * 
     * <ul>
     * <li>Validate on missions request data</li>
     * <li>Creates a workflow instance for the process</li>
     * <li>Creates a workflow task for the process</li>
     * </ul>
     * 
     * @param requester
     *            Employee that started the mission request
     * @param mission
     *            {@link MissionData} Object view that contain all Data gotten by User from UI
     * @param missionDetails
     *            List of {@link MissionDetailData} that contains employee mission data that needed to be handled
     * @param processId
     *            Process Id , it could be officers missions , soldiers mission , or civilians mission
     * @param attachments
     *            Attachments sent with request
     * @param taskUrl
     *            Url of the workflow tasks
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see WFProcessesEnum
     * @see #addWFInstance(long, long, Date, Date, int, String, CustomSession)
     */

    public static void initMission(EmployeeData requester, WFMissionData mission, List<WFMissionDetailData> missionDetails, long processId, String attachments, String taskUrl) throws BusinessException {

	validateMissionRequestData(requester, mission, missionDetails, processId, null, null);

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    WFInstance instance = addWFInstance(processId, requester.getEmpId(), curDate, curHijriDate, WFInstanceStatusEnum.RUNNING.getCode(), attachments, getMissionEmployeesInstanceBeneficiariesIds(missionDetails), session);

	    addWFTask(instance.getInstanceId(), getDelegate(requester.getManagerId(), processId, requester.getEmpId()), requester.getManagerId(), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);

	    saveWFMission(mission, missionDetails, instance.getInstanceId(), session);

	    session.commitTransaction();
	} catch (Exception e) {
	    session.rollbackTransaction();

	    mission.setInstanceId(null);
	    for (WFMissionDetailData missionDetailData : missionDetails) {
		missionDetailData.setInstanceId(null);
	    }

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    /**
     * Handles actions of direct manager to mission request such as :
     * <ul>
     * <li>Approval of the request send it to next direct manager</li>
     * <li>The rejection of the request close it</li>
     * </ul>
     * 
     * @param requester
     *            Employee that started mission request
     * @param instance
     *            Current mission instance
     * @param mission
     *            {@link MissionData} Object view that contain all Data gotten by User from UI
     * @param missionDetails
     *            List of {@link MissionDetailData} requests need to be handled
     * @param dmTask
     *            Current direct manager for the current task
     * @param isApproved
     *            Flag represents action taken by direct manager in this task
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */

    public static void doMissionDM(EmployeeData requester, WFInstance instance, WFMissionData mission, List<WFMissionDetailData> missionDetails, WFTask dmTask, boolean isApproved) throws BusinessException {
	if (isApproved)
	    validateMissionRequestData(requester, mission, missionDetails, instance.getProcessId(), instance, dmTask);

	CustomSession session = DataAccess.getSession();

	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();
	    EmployeeData missionEmp = EmployeesService.getEmployeeData(missionDetails.get(0).getEmpId());

	    if (isApproved) {
		EmployeeData curDM = EmployeesService.getEmployeeData(dmTask.getOriginalId());

		if (missionEmp.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) { // General Region.
		    if (instance.getProcessId().equals(WFProcessesEnum.OFFICERS_MISSION.getCode())) { // Officers.
			if (curDM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode() || (mission.getLocationFlag().equals(LocationFlagsEnum.EXTERNAL.getCode()) && EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode())) {

			    mission.setApprovedId(dmTask.getOriginalId());

			    if (mission.getLocationFlag().equals(LocationFlagsEnum.INTERNAL.getCode()) && !hasExceptionalApprovalInfo(missionDetails) && !mission.getHajjFlagBoolean()) {
				EmployeeData presidencyAssistant = getPresidencyMilitaryAssistance();
				completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(presidencyAssistant.getEmpId(), instance.getProcessId(), requester.getEmpId()), presidencyAssistant.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
			    } else {
				EmployeeData missionManager = getMissionManager(missionEmp);
				completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(missionManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), missionManager.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
			    }

			    mission = handleInternalCopiesAndExternalCopies(instance, mission, missionDetails);
			} else {
			    if (mission.getLocationFlag().equals(LocationFlagsEnum.INTERNAL.getCode()) && EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
				if (!hasPassedRules(missionDetails, true, ETRConfigurationService.getMissionOfficersInternalVicePresidentPeriodMax(), true, ETRConfigurationService.getMissionOfficersInternalDMVicePresidentRankLessThan(), true))
				    curDM.setManagerId(getVicePresidencyId());
			    }

			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			}
		    } else if (instance.getProcessId().equals(WFProcessesEnum.SOLDIERS_MISSION.getCode())) { // Soldiers
			if (curDM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
			    mission.setApprovedId(dmTask.getOriginalId());
			    EmployeeData missionManager = getMissionManager(missionEmp);
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(missionManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), missionManager.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
			    mission = handleInternalCopiesAndExternalCopies(instance, mission, missionDetails);
			} else {
			    if (mission.getLocationFlag().equals(LocationFlagsEnum.INTERNAL.getCode()) && EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
				if (!hasPassedRules(missionDetails, true, ETRConfigurationService.getMissionSoldiersInternalVicePresidentPeriodMax(), false, 0, false))
				    curDM.setManagerId(getVicePresidencyId());
			    }

			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			}
		    } else { // Civilians.
			if (curDM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
			    mission.setApprovedId(dmTask.getOriginalId());
			    EmployeeData missionManager = getMissionManager(missionEmp);
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(missionManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), missionManager.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
			    mission = handleInternalCopiesAndExternalCopies(instance, mission, missionDetails);
			} else {
			    if (mission.getLocationFlag().equals(LocationFlagsEnum.INTERNAL.getCode()) && EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
				if (!hasPassedRules(missionDetails, true, ETRConfigurationService.getMissionCiviliansInternalVicePresidentPeriodMax(), true, ETRConfigurationService.getMissionCiviliansInternalDMVicePresidentRankLessThan(), true))
				    curDM.setManagerId(getVicePresidencyId());
			    }

			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			}
		    }
		} else { // Another region.
		    if (instance.getProcessId().equals(WFProcessesEnum.OFFICERS_MISSION.getCode())) { // Officers.
			if (curDM.getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
			    mission.setApprovedId(dmTask.getOriginalId());
			    EmployeeData missionManager = getMissionManager(missionEmp);
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(missionManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), missionManager.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
			    mission = handleInternalCopiesAndExternalCopies(instance, mission, missionDetails);

			} else {
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			}
		    } else { // Soldiers or Civilians.
			if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
			    mission.setApprovedId(dmTask.getOriginalId());
			    EmployeeData missionManager = getMissionManager(missionEmp);
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(missionManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), missionManager.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
			    mission = handleInternalCopiesAndExternalCopies(instance, mission, missionDetails);

			} else {
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			}
		    }
		}
	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, dmTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
	    }

	    saveWFMission(mission, missionDetails, instance.getInstanceId(), session);
	    updateWFInstanceBeneficiaries(instance.getInstanceId(), getMissionEmployeesInstanceBeneficiariesIds(missionDetails), session);

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

    /**
     * Redirect mission request to reviewer employee to review it
     * 
     * @param requester
     *            Employee that started mission process
     * @param instance
     *            Current mission instance
     * @param mrTask
     *            Current manager redirect for the current task
     * @param reviewerEmpId
     *            Reviewer employee id
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */

    public static void doMissionMR(EmployeeData requester, WFInstance instance, WFTask mrTask, long reviewerEmpId) throws BusinessException {
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

    /**
     * Handles actions and reviews of reviewer employee and apply reviewer's changes in mission and mission details
     * 
     * @param requester
     *            Employee that started mission process
     * @param mission
     *            {@link MissionData} Object view that contain all Data gotten by User from UI
     * @param missionDetails
     *            List of {@link MissionDetailData} requests need to be handled
     * @param instance
     *            Current mission instance
     * @param reTask
     *            Current reviewer employee for the current task
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */

    public static void doMissionRE(EmployeeData requester, WFMissionData mission, List<WFMissionDetailData> missionDetails, WFInstance instance, WFTask reTask) throws BusinessException {
	validateMissionRequestData(requester, mission, missionDetails, instance.getProcessId(), instance, reTask);

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    EmployeeData missionEmp = EmployeesService.getEmployeeData(missionDetails.get(0).getEmpId());

	    EmployeeData missionManager = getMissionManager(missionEmp);

	    completeWFTask(reTask, WFTaskActionsEnum.REVIEW.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(missionManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), missionManager.getEmpId(), reTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);

	    saveWFMission(mission, missionDetails, instance.getInstanceId(), session);
	    updateWFInstanceBeneficiaries(instance.getInstanceId(), getMissionEmployeesInstanceBeneficiariesIds(missionDetails), session);

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

    /**
     * Handles actions of sign manager for the mission request such as :
     * <ul>
     * <li>Approval of the request send it to next sign manager</li>
     * <li>Return back the request to reviewer employee</li>
     * <li>The rejection of the request close it</li>
     * </ul>
     * 
     * @param requester
     *            Employee that started mission request
     * @param instance
     *            Current mission instance
     * @param mission
     *            {@link MissionData} Object view that contain all Data gotten by User from UI
     * @param missionDetails
     *            List of {@link MissionDetailData} requests need to be handled
     * @param smTask
     *            Current task handled by sign manager
     * @param approvalFlag
     *            Flag represents action taken by sign manager
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */

    public static void doMissionSM(EmployeeData requester, WFInstance instance, WFMissionData mission, List<WFMissionDetailData> missionDetails, WFTask smTask, int approvalFlag) throws BusinessException {

	if (approvalFlag == WFActionFlagsEnum.RETURN_REVIEWER.getCode() && instance.getProcessId().equals(WFProcessesEnum.OFFICERS_MISSION.getCode()) && mission.getLocationFlag() == FlagsEnum.OFF.getCode() && requester.getPhysicalRegionId().longValue() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() && !mission.getHajjFlagBoolean())
	    throw new BusinessException("error_missionReturnToReviewerNotAvailable");

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();
	    EmployeeData missionEmp = EmployeesService.getEmployeeData(missionDetails.get(0).getEmpId());
	    EmployeeData curDM = EmployeesService.getEmployeeData(smTask.getOriginalId());

	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {
		if (RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == missionEmp.getPhysicalRegionId()) { // General region.
		    if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
			closeMissionWorkFlow(requester, instance, mission, missionDetails, smTask, session);
		    } else {
			completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curDM.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
		    }

		} else { // Another region.
		    if (instance.getProcessId().equals(WFProcessesEnum.OFFICERS_MISSION.getCode())) { // Officers
			if ((curDM.getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode() && mission.getLocationFlag().equals(LocationFlagsEnum.INTERNAL.getCode()) && !hasPassedRules(missionDetails, true, ETRConfigurationService.getMissionOfficersInternalRegionCommanderPeriodMax(), true, ETRConfigurationService.getMissionOfficersInternalSMRegionCommanderRankLessThan(), true)) ||
				curDM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
			    closeMissionWorkFlow(requester, instance, mission, missionDetails, smTask, session);
			} else {
			    completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curDM.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
			}
		    } else if (instance.getProcessId().equals(WFProcessesEnum.SOLDIERS_MISSION.getCode())) { // Soldiers
			if ((curDM.getUnitTypeCode().intValue() == UnitTypesEnum.REGION_COMMANDER.getCode() && mission.getLocationFlag().equals(LocationFlagsEnum.INTERNAL.getCode()) && !hasPassedRules(missionDetails, true, ETRConfigurationService.getMissionSoldiersInternalRegionCommanderPeriodMax(), false, 0, false)) ||
				curDM.getUnitTypeCode().intValue() == UnitTypesEnum.ASSISTANT.getCode()) {
			    closeMissionWorkFlow(requester, instance, mission, missionDetails, smTask, session);
			} else if (curDM.getUnitTypeCode().intValue() == UnitTypesEnum.PRESIDENCY.getCode() || (curDM.getUnitTypeCode().intValue() == UnitTypesEnum.REGION_COMMANDER.getCode() && mission.getLocationFlag().equals(LocationFlagsEnum.EXTERNAL.getCode()))) {
			    EmployeeData presidencyAssistant = getPresidencyMilitaryAssistance();
			    completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(presidencyAssistant.getEmpId(), instance.getProcessId(), requester.getEmpId()), presidencyAssistant.getEmpId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
			} else {
			    if (mission.getLocationFlag().equals(LocationFlagsEnum.INTERNAL.getCode()) && EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
				if (!hasPassedRules(missionDetails, true, ETRConfigurationService.getMissionSoldiersInternalVicePresidentPeriodMax(), false, 0, false))
				    curDM.setManagerId(getVicePresidencyId());
			    }

			    completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curDM.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
			}
		    } else { // Civilians.
			if ((curDM.getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode() && mission.getLocationFlag().equals(LocationFlagsEnum.INTERNAL.getCode()) && !hasPassedRules(missionDetails, true, ETRConfigurationService.getMissionCiviliansInternalRegionCommanderPeriodMax(), false, 0, false)) ||
				curDM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
			    closeMissionWorkFlow(requester, instance, mission, missionDetails, smTask, session);
			} else {
			    if (mission.getLocationFlag().equals(LocationFlagsEnum.INTERNAL.getCode()) && EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
				if (!hasPassedRules(missionDetails, true, ETRConfigurationService.getMissionCiviliansInternalVicePresidentPeriodMax(), true, ETRConfigurationService.getMissionCiviliansInternalSMVicePresidentRankLessThan(), false))
				    curDM.setManagerId(getVicePresidencyId());
			    }
			    completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curDM.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
			}
		    }
		}
	    } else if (approvalFlag == WFActionFlagsEnum.RETURN_REVIEWER.getCode()) {
		WFTask reviewerTask = getWFInstanceTasksByRole(instance.getInstanceId(), WFTaskRolesEnum.REVIEWER_EMP.getCode()).get(0);
		completeWFTask(smTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(reviewerTask.getOriginalId(), instance.getProcessId(), requester.getEmpId()), reviewerTask.getOriginalId(), smTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), "1", session);
	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, smTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
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

    /***********************************************************************************************************************/

    /**
     * Insert or update missions requests
     * 
     * @param mission
     *            {@link MissionData} Object view that contain all Data gotten by User from UI
     * @param missionDetails
     *            List of {@link MissionDetailData} requests need to be handled
     * @param instanceId
     *            Current instance id
     * @param useSession
     *            the {@link CustomSession} is optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */

    private static void saveWFMission(WFMissionData mission, List<WFMissionDetailData> missionDetails, long instanceId, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    if (mission.getInstanceId() != null) { // Update

		DataAccess.updateEntity(mission.getWfMission(), session);

		for (WFMissionDetailData missionDetailData : missionDetails) {
		    if (missionDetailData.getInstanceId() != null) { // Update
			DataAccess.updateEntity(missionDetailData.getWfMissionDetail(), session);
		    } else {
			missionDetailData.setInstanceId(instanceId);
			DataAccess.addEntity(missionDetailData.getWfMissionDetail(), session);
		    }
		}

	    } else {
		mission.setInstanceId(instanceId);
		DataAccess.addEntity(mission.getWfMission(), session);

		for (WFMissionDetailData missionDetailData : missionDetails) {
		    missionDetailData.setInstanceId(instanceId);
		    DataAccess.addEntity(missionDetailData.getWfMissionDetail(), session);
		}
	    }

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    /**
     * Handles creation of internal and external copies of this decision based on businees requirments
     * 
     * @param instance
     *            Current mission instance
     * @param mission
     *            {@link MissionData} Object view that contain all Data gotten by User from UI
     * @param missionDetails
     *            List of {@link MissionDetailData} requests need to be handled
     * @return Modified mission object {@link WFMissionData}
     * 
     * @throws DatabaseException
     *             if any DB exceptions or errors occurs
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */

    private static WFMissionData handleInternalCopiesAndExternalCopies(WFInstance instance, WFMissionData mission, List<WFMissionDetailData> missionDetails) throws BusinessException {

	String internalCopies = "";
	String externalCopies = "";
	HashSet<Long> internalCopiesSet = new HashSet<Long>();
	HashSet<String> externalCopiesSet = new HashSet<String>();
	List<String> externalCopiesList = new ArrayList<String>();
	String copyTo = getMessage("label_copyTo");

	EmployeeData missionEmp = EmployeesService.getEmployeeData(missionDetails.get(0).getEmpId());
	EmployeeData missionManager = getMissionManager(missionEmp);

	// add externals
	// 1- add the mission manager
	externalCopiesSet.add(copyTo + missionManager.getPhysicalUnitFullName());
	externalCopiesList.add(copyTo + missionManager.getPhysicalUnitFullName());

	// 2- add the units of the employees at the mission
	for (WFMissionDetailData wfMissionDetailData : missionDetails) {
	    if (externalCopiesSet.add(copyTo + wfMissionDetailData.getEmpUnitDesc()))
		externalCopiesList.add(copyTo + wfMissionDetailData.getEmpUnitDesc());
	}

	// add internals
	// 1- add all mission employees
	// 2- add all mission employees direct managers
	String missionEmpsString = "", comma = "";
	for (WFMissionDetailData wfMissionDetailData : missionDetails) {
	    internalCopiesSet.add(wfMissionDetailData.getEmpId());

	    missionEmpsString += comma + wfMissionDetailData.getEmpId();
	    comma = ",";
	}
	List<EmployeeData> missionEmployees = EmployeesService.getEmployeesByIdsString(missionEmpsString);
	for (EmployeeData missionEmployee : missionEmployees) {
	    if (missionEmployee.getManagerId() == null)
		continue;
	    internalCopiesSet.add(missionEmployee.getManagerId());
	}

	// set to the internal copies string
	comma = "";
	Iterator<Long> internalIterator = internalCopiesSet.iterator();
	while (internalIterator.hasNext()) {
	    internalCopies = internalCopies.concat(comma + internalIterator.next().toString());
	    comma = ",";
	}

	// set to the external copies string
	String newLine = "";
	for (String externalCopy : externalCopiesList) {
	    externalCopies = externalCopies.concat(newLine + externalCopy);
	    newLine = "\n";
	}

	mission.setInternalCopies(internalCopies);
	mission.setExternalCopies(externalCopies);
	return mission;
    }

    /**
     * Close the mission request when the last sign manager approve the request, it do the following :
     * 
     * <ul>
     * <li>Closes the workflow</li>
     * <li>Do the mission integration between the mission workflow data and the transaction data</li>
     * <li>Send notifications to copies</li>
     * </ul>
     * 
     * @param requester
     *            Employee that started mission request
     * @param instance
     *            The current mission instance
     * @param mission
     *            {@link MissionData} Object view that contain all Data gotten by User from UI
     * @param missionDetails
     *            List of {@link MissionDetailData} requests need to be handled
     * @param smTask
     *            Current sign manager task being handled
     * @param session
     *            Optional database session to be used
     * @throws DatabaseException
     *             if any DB exceptions or errors occurs
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */

    private static void closeMissionWorkFlow(EmployeeData requester, WFInstance instance, WFMissionData mission, List<WFMissionDetailData> missionDetails, WFTask smTask, CustomSession session) throws BusinessException, DatabaseException {
	EmployeeData missionEmp = EmployeesService.getEmployeeData(missionDetails.get(0).getEmpId());
	EmployeeData signManager = EmployeesService.getEmployeeData(smTask.getOriginalId());

	if (missionEmp.getPhysicalRegionId().longValue() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) {
	    if (instance.getProcessId().equals(WFProcessesEnum.OFFICERS_MISSION.getCode()) || instance.getProcessId().equals(WFProcessesEnum.CIVILIANS_MISSION.getCode())) {
		mission.setOriginalDecisionApprovedId(signManager.getManagerId());
	    } else {
		mission.setOriginalDecisionApprovedId(smTask.getOriginalId());
	    }
	} else {
	    if (instance.getProcessId().equals(WFProcessesEnum.CIVILIANS_MISSION.getCode()) && signManager.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
		mission.setOriginalDecisionApprovedId(signManager.getManagerId());
	    } else {
		mission.setOriginalDecisionApprovedId(smTask.getOriginalId());
	    }
	}

	mission.setDecisionApprovedId(smTask.getOriginalId());
	mission.setDecisionRegionId(signManager.getPhysicalRegionId());
	mission.setDirectedToJobName(adjustDirectedToJobName(missionEmp, signManager));

	DataAccess.updateEntity(mission, session);

	doMissionIntegration(mission, missionDetails, instance, session);

	// Handle notifications.
	List<Long> categoriesIds = new ArrayList<Long>();
	HashSet<Long> categoriesSet = new HashSet<Long>();
	List<Long> additionalIds = new ArrayList<Long>();
	List<Long> beneficairyEmployeesIds = new ArrayList<Long>();

	categoriesSet.add(missionEmp.getCategoryId());
	for (WFMissionDetailData wfMissionDetailData : missionDetails) {
	    beneficairyEmployeesIds.add(wfMissionDetailData.getEmpId());
	    if (instance.getProcessId().longValue() == WFProcessesEnum.CIVILIANS_MISSION.getCode()) {
		categoriesSet.add(CommonService.getRankById(wfMissionDetailData.getEmpRankId()).getCategoryId());
	    }
	}
	categoriesIds.addAll(Arrays.asList(categoriesSet.toArray(new Long[categoriesSet.size()])));

	if (mission.getInternalCopies() != null)
	    additionalIds.addAll(Arrays.asList(getMissionCopiesNotifications(mission.getInternalCopies())));

	Long[] notificationsEmpsIds = computeInstanceNotifications(categoriesIds, mission.getDecisionRegionId(), instance.getProcessId(), beneficairyEmployeesIds, additionalIds);
	closeWFInstanceByAction(requester.getEmpId(), instance, smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), notificationsEmpsIds, session);
    }

    /**
     * 
     * Do mission integration by fillfull all missions transaction data from the finished missions workflow data
     * 
     * <ul>
     * <li>Construct mission transaction from request after request goes all steps of approval</li>
     * <li>Insert mission transactions into database</li>
     * </ul>
     * 
     * @param mission
     *            {@link MissionData} Object view that contain all Data gotten by User from UI
     * @param missionDetails
     *            List of {@link MissionDetailData} requests need to be handled
     * @param instance
     *            The current mission instance
     * @param useSession
     *            The {@link CustomSession} is optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */

    private static void doMissionIntegration(WFMissionData mission, List<WFMissionDetailData> missionDetails, WFInstance instance, CustomSession useSession) throws BusinessException {

	try {
	    MissionData missionTransaction = constructMissionTransaction(mission, instance, useSession);
	    List<MissionDetailData> MissionDetailsTransactions = constructMissionDetailsTransactions(missionDetails, useSession);

	    MissionsService.addMissionAndDetails(missionTransaction, MissionDetailsTransactions, getWFProcess(instance.getProcessId()).getProcessName(), useSession);

	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * 
     * Parse comma separated String that represent mission copies for sending notifications for employees ids generated from the parsing
     * 
     * @param copies
     *            A string comma separated represents mission copies to send notifications
     * @return Array of copies which will receive notifications
     * @throws DatabaseException
     *             if any DB exceptions or errors occurs
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */

    private static Long[] getMissionCopiesNotifications(String copies) {

	Long[] empsGroup = null;
	if (copies == null || copies.equals(""))
	    return null;

	String[] list = copies.split(",");
	if (list.length > 0) {
	    empsGroup = new Long[list.length];
	    for (int i = 0; i < list.length; i++) {
		empsGroup[i] = Long.parseLong(list[i].trim());
	    }
	}

	return empsGroup;
    }

    /**
     * 
     * Adjust direction to job name based on category and region of mission employees, which will be printed at mission decisoin
     * 
     * @param missionEmp
     *            Mission employee parameter to get category and region
     * @param signManager
     *            Sign manager employee parameter to get his region
     * @return String representing job name to be directed to.
     * @throws BusinessException
     * 
     * @see CategoriesEnum , RegionsEnum
     */

    private static String adjustDirectedToJobName(EmployeeData missionEmp, EmployeeData signManager) throws BusinessException {
	// long categoryId = missionEmp.getCategoryId();
	// long regionId = missionEmp.getPhysicalRegionId();
	//
	// if (regionId == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) { // General region.
	// Long officialManagerId;
	// if (categoryId == CategoriesEnum.OFFICERS.getCode())
	// officialManagerId = UnitsService.getUnitById(getWFPosition(WFPositionsEnum.MILITARY_AFFAIRS_ASSISTANT.getCode(), regionId).getUnitId()).getOfficialManagerId();
	//
	// else if (categoryId == CategoriesEnum.SOLDIERS.getCode())
	// officialManagerId = UnitsService.getUnitById(getWFPosition(WFPositionsEnum.SOLDIERS_HUMAN_RESOURCES_MANAGER.getCode(), regionId).getUnitId()).getOfficialManagerId();
	//
	// else
	// return null;
	//
	// if (officialManagerId == null)
	// throw new BusinessException("error_notValidPosition");
	// return EmployeesService.getEmployeesByEmpsIds(new Long[] { officialManagerId }).get(0).getJobDesc();
	// }
	// // Another region.
	// else if (categoryId == CategoriesEnum.OFFICERS.getCode() || categoryId == CategoriesEnum.SOLDIERS.getCode()) {
	// if (signManager.getPhysicalRegionId().longValue() != RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) {
	// Long officialManagerId;
	// if (signManager.getPhysicalRegionId().equals(RegionsEnum.ACADEMY.getCode()))
	// officialManagerId = UnitsService.getUnitById(getWFPosition(WFPositionsEnum.REGION_ADMINISTRATION_AND_FINANCIAL_MANAGER.getCode(), signManager.getPhysicalRegionId()).getUnitId()).getOfficialManagerId();
	// else
	// officialManagerId = UnitsService.getUnitById(getWFPosition(WFPositionsEnum.REGION_MISSION_DIRECTED_TO.getCode(), signManager.getPhysicalRegionId()).getUnitId()).getOfficialManagerId();
	//
	// if (officialManagerId == null)
	// throw new BusinessException("error_notValidPosition");
	// return EmployeesService.getEmployeesByEmpsIds(new Long[] { officialManagerId }).get(0).getJobDesc();
	//
	// } else {
	// return UnitsService.getUnitsByTypeAndRegion(UnitTypesEnum.REGION_COMMANDER.getCode(), regionId).get(0).getName();
	// }
	// }
	//
	// return null;

	long categoryId = missionEmp.getCategoryId();
	long regionId = missionEmp.getPhysicalRegionId();

	if (regionId == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) { // General region.
	    if (categoryId == CategoriesEnum.OFFICERS.getCode())
		return getMessage("label_presidencyAssistant");
	    else if (categoryId == CategoriesEnum.SOLDIERS.getCode())
		return getMessage("label_generalMangerDirector");
	    else
		return null;
	}
	// Another region.
	if (categoryId == CategoriesEnum.OFFICERS.getCode() || categoryId == CategoriesEnum.SOLDIERS.getCode()) {
	    if (signManager.getPhysicalRegionId().longValue() != RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) {
		return signManager.getPhysicalRegionId().equals(RegionsEnum.ACADEMY.getCode()) ? getMessage("label_managerOfFinancialAffairs") : getMessage("label_managerOfHumanResource");
	    } else if (regionId == RegionsEnum.ALJOOF_REGION.getCode()) {
		return getMessage("label_commanderOfBordersGraudsAljoof");
	    } else if (regionId == RegionsEnum.ALMADEENAH_ALMONAWRAH_REGION.getCode()) {
		return getMessage("label_commanderOfBordersGraudsAlMadinaElMonara");
	    } else if (regionId == RegionsEnum.ASEER_REGION.getCode()) {
		return getMessage("label_commanderOfBordersGraudsAseer");
	    } else if (regionId == RegionsEnum.EASTERN_REGION.getCode()) {
		return getMessage("label_commanderOfBordersGraudsEastrn");
	    } else if (regionId == RegionsEnum.NORTHERN_REGION.getCode()) {
		return getMessage("label_commanderOfBordersGraudsNorth");
	    } else if (regionId == RegionsEnum.JAZAN_REGION.getCode()) {
		return getMessage("label_commanderOfBordersGraudsJazan");
	    } else if (regionId == RegionsEnum.TABOOK_REGION.getCode()) {
		return getMessage("label_commanderOfBordersGraudsTabouk");
	    } else if (regionId == RegionsEnum.MAKKAH_ALMOKARRAMAH_REGION.getCode()) {
		return getMessage("label_commanderOfBordersGraudsMAKAAELMOKARAMA");
	    } else if (regionId == RegionsEnum.NAJRAN_REGION.getCode()) {
		return getMessage("label_commanderOfBordersGraudsNajran");
	    } else if (regionId == RegionsEnum.ACADEMY.getCode()) {
		return getMessage("label_commanderOfAcademy");
	    }
	}

	return null;
    }

    /**
     * Adjust mission details data ( dates and periods ) based on two flags representing if there are any changes regarding start date and road period
     * 
     * @param mission
     *            {@link MissionData} Object view that contain all Data gotten by User from UI
     * @param missionDetails
     *            List of {@link MissionDetailData} requests need to be handled
     * @param startDateChangeFlag
     *            A flag represents if there is any change regarding start date
     * @param roadPeriodChangeFlag
     *            A flag represents if there is any change regarding road period
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */

    public static void adjustMissionDetailsData(WFMissionData mission, List<WFMissionDetailData> missionDetails, boolean startDateChangeFlag, boolean roadPeriodChangeFlag) throws BusinessException {
	for (WFMissionDetailData missionDetailData : missionDetails) {
	    if (roadPeriodChangeFlag) {
		missionDetailData.setRoadPeriod(mission.getRoadPeriod());
		missionDetailData.setEndDate(HijriDateService.getHijriDate(MissionsService.calculateMissionEndDate(missionDetailData.getStartDateString(), missionDetailData.getPeriod(), missionDetailData.getRoadPeriod())));
	    } else {
		missionDetailData.setPeriod(mission.getPeriod());
		missionDetailData.setRoadPeriod(mission.getRoadPeriod());
		missionDetailData.setStartDate(mission.getStartDate());
		missionDetailData.setEndDate(mission.getEndDate());
	    }

	    if (startDateChangeFlag)
		missionDetailData.setBalance(MissionsService.calculateEmpMissionsBalance(missionDetailData.getEmpId(), missionDetailData.getStartDate()));
	}
    }

    private static List<Long> getMissionEmployeesInstanceBeneficiariesIds(List<WFMissionDetailData> missionDetails) throws BusinessException {

	List<Long> instanceBeneficiariesIds = new ArrayList<Long>();
	for (WFMissionDetailData missionDetail : missionDetails) {
	    instanceBeneficiariesIds.add(missionDetail.getEmpId());
	}

	return instanceBeneficiariesIds;
    }

    /************************************************ Validation *******************************************************/

    /**
     * Validate mission request data according to many cases :
     * <ul>
     * <li>Check if there are details in this request.</li>
     * <li>Check if there are other mission requests under processing</li>
     * <li>Check that at least one employee uses mission period and date</li>
     * <li>Check repeated mission details</li>
     * <li>Check mission employees balances</li>
     * <li>Check for missions overlapping.</li>
     * <li>Check mission requesting privileges</li>
     * </ul>
     * 
     * @param requester
     *            Employee that started the mission request
     * @param mission
     *            {@link MissionData} Object view that contain all Data gotten by User from UI
     * @param missionDetails
     *            List of {@link MissionDetailData} requests need to be handled
     * @param processId
     *            Process Id , it may be officers missions , soldiers mission , or civilians mission
     * @param instance
     *            Current mission instance
     * @param curTask
     *            Current task
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see WFProcessesEnum
     */

    private static void validateMissionRequestData(EmployeeData requester, WFMissionData mission, List<WFMissionDetailData> missionDetails, long processId, WFInstance instance, WFTask curTask) throws BusinessException {

	if (missionDetails == null || missionDetails.size() == 0)
	    throw new BusinessException("error_missionEmpsMandatory");

	Long[] empsIds = new Long[missionDetails.size()];
	String[] empsNames = new String[missionDetails.size()];
	String[] startDatesString = new String[missionDetails.size()];
	String[] endDatesString = new String[missionDetails.size()];

	for (int i = 0; i < missionDetails.size(); i++) {
	    empsIds[i] = missionDetails.get(i).getEmpId();
	    empsNames[i] = missionDetails.get(i).getEmpName();
	    startDatesString[i] = missionDetails.get(i).getStartDateString();
	    endDatesString[i] = missionDetails.get(i).getEndDateString();
	}

	EmployeesTransactionsConflictValidator.validateEmployeesTransactionsConflicts(empsIds, empsNames, TransactionClassesEnum.MISSIONS.getCode(), TransactionTypesEnum.MISSION_DECISION.getCode(), FlagsEnum.ALL.getCode(), processId, startDatesString, endDatesString, null, instance == null ? null : instance.getInstanceId());

	// Used to make sure that at least one employee uses the mission period and date.
	boolean missionPeriodAndDateUsedFlag = false;

	// Used to authorize who can send requests.
	boolean requstingAdmin = isRequesterAuthorizedToSendAll(requester, processId);
	boolean requesterAuthorizedToSendFlag = requstingAdmin;

	// Used to make sure that there is no repeated employee in the request.
	HashSet<Long> employeesIDs = new HashSet<Long>();

	for (WFMissionDetailData missionDetailData : missionDetails) {
	    if (!employeesIDs.add(missionDetailData.getEmpId()))
		throw new BusinessException("error_repeatedEmployee", new Object[] { missionDetailData.getEmpName() });

	    // Check for period
	    if (missionDetailData.getPeriod() == null || missionDetailData.getPeriod().intValue() <= 0)
		throw new BusinessException("error_periodMandatoryParametrized", new Object[] { missionDetailData.getEmpName() });

	    if (missionDetailData.getExceptionalApprovalDate() != null && (missionDetailData.getExceptionalApprovalNumber() == null || missionDetailData.getExceptionalApprovalNumber().equals("")))
		throw new BusinessException("error_exceptionalApprovalNumberMandatory", new Object[] { missionDetailData.getEmpName() });

	    if (missionDetailData.getExceptionalApprovalDate() == null && (missionDetailData.getExceptionalApprovalNumber() != null && !missionDetailData.getExceptionalApprovalNumber().equals("")))
		throw new BusinessException("error_exceptionalApprovalDateMandatory", new Object[] { missionDetailData.getEmpName() });
	    if (!(missionDetailData.getEndDateString().equals(MissionsService.calculateMissionEndDate(missionDetailData.getStartDateString(), missionDetailData.getPeriod(), missionDetailData.getRoadPeriod()))))
		throw new BusinessException("error_endDateIncompitableWithPeriodAndStartDate");
	    if (missionPeriodAndDateUsedFlag == false && missionDetailData.getPeriod().equals(mission.getPeriod()) && missionDetailData.getStartDateString().equals(mission.getStartDateString()))
		missionPeriodAndDateUsedFlag = true;

	    if (!requstingAdmin && processId != WFProcessesEnum.OFFICERS_MISSION.getCode() && processId != WFProcessesEnum.SOLDIERS_MISSION.getCode() && missionDetailData.getEmpId().equals(requester.getEmpId()) && !EmployeesService.getEmployeeDirectManager(requester.getEmpId()).getUnitTypeCode().equals(UnitTypesEnum.PRESIDENCY.getCode()))
		throw new BusinessException("error_requesterNotAllowedToRequestMissionsForHimself");

	    if (requesterAuthorizedToSendFlag == false) {
		if ((processId == WFProcessesEnum.OFFICERS_MISSION.getCode() || processId == WFProcessesEnum.SOLDIERS_MISSION.getCode()) && missionDetailData.getEmpId().equals(requester.getEmpId())) {
		    requesterAuthorizedToSendFlag = true;
		} else if (requester.getIsManager().equals(FlagsEnum.ON.getCode()) && missionDetailData.getEmpPhysicalUnitHKey().startsWith(UnitsService.getHKeyPrefix(requester.getPhysicalUnitHKey()))) {
		    requesterAuthorizedToSendFlag = true;
		} else {
		    try {
			if (EmployeesService.getEmployeeDirectManager(missionDetailData.getEmpId()).getUnitTypeCode().equals(UnitTypesEnum.PRESIDENCY.getCode())) {
			    requesterAuthorizedToSendFlag = true;
			}
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
	    }

	    // Check mission details balance.
	    MissionsService.validateEmpMissionBalance(missionDetailData.getEmpId(), missionDetailData.getEmpName(), missionDetailData.getStartDate(), missionDetailData.getEndDate(), missionDetailData.getPeriod(), missionDetailData.getExceptionalApprovalDateString(), missionDetailData.getExceptionalApprovalNumber(), mission.getRoadPeriod() == null ? 0 : mission.getRoadPeriod(), mission.getHajjFlagBoolean() == null ? false : mission.getHajjFlagBoolean().booleanValue(),
		    mission.getDoubleBalanceFlagBoolean() == null ? false : mission.getDoubleBalanceFlagBoolean().booleanValue());

	    if (missionDetailData.getStartDate().before(HijriDateService.getHijriDate(ETRConfigurationService.getMissionActivationStartDate())))
		throw new BusinessException("error_missionStartDateInvalid", new Object[] { missionDetailData.getEmpName(), ETRConfigurationService.getMissionActivationStartDate() });
	}
	// check end date compatibility with start date and period
	if (!(mission.getEndDateString().equals(MissionsService.calculateMissionEndDate(mission.getStartDateString(), mission.getPeriod(), mission.getRoadPeriod()))))
	    throw new BusinessException("error_endDateIncompitableWithPeriodAndStartDate");
	if (missionPeriodAndDateUsedFlag == false)
	    throw new BusinessException("error_missionPeriodAndDateNotUsed");

	if (requesterAuthorizedToSendFlag == false) {
	    if (processId == WFProcessesEnum.OFFICERS_MISSION.getCode())
		throw new BusinessException("error_requesterNotAuthorizedToSend");

	    throw new BusinessException("error_nonOfficerRequesterNotAuthorizedToSend");
	}

	// Check VP privileges conditions.
	try {
	    if (curTask != null &&
		    mission.getLocationFlag().equals(LocationFlagsEnum.INTERNAL.getCode()) &&
		    EmployeesService.getEmployeeData(missionDetails.get(0).getEmpId()).getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {
		long lastDirectManagerId = curTask.getOriginalId();
		if (curTask.getAssigneeWfRole().equals(WFTaskRolesEnum.REVIEWER_EMP.getCode())) {
		    List<WFTask> wfTasks = getWFInstanceTasksByRole(instance.getInstanceId(), WFTaskRolesEnum.DIRECT_MANAGER.getCode());
		    lastDirectManagerId = wfTasks.get(wfTasks.size() - 1).getOriginalId();
		}

		if (lastDirectManagerId == getVicePresidencyId()) {
		    if (instance.getProcessId().equals(WFProcessesEnum.OFFICERS_MISSION.getCode()) && hasPassedRules(missionDetails, true, ETRConfigurationService.getMissionOfficersInternalVicePresidentPeriodMax(), true, ETRConfigurationService.getMissionOfficersInternalDMVicePresidentRankLessThan(), true)) {
			throw new BusinessException("error_vicePresidentViolateMissionOfficerRules", new Object[] { ETRConfigurationService.getMissionOfficersInternalVicePresidentPeriodMax(), CommonService.getRankById(ETRConfigurationService.getMissionOfficersInternalDMVicePresidentRankLessThan()).getDescription() });
		    } else if (instance.getProcessId().equals(WFProcessesEnum.SOLDIERS_MISSION.getCode()) && hasPassedRules(missionDetails, true, ETRConfigurationService.getMissionSoldiersInternalVicePresidentPeriodMax(), false, 0, false)) {
			throw new BusinessException("error_vicePresidentViolateMissionSoldierRules", new Object[] { ETRConfigurationService.getMissionSoldiersInternalVicePresidentPeriodMax() });
		    } else if (instance.getProcessId().equals(WFProcessesEnum.CIVILIANS_MISSION.getCode()) && hasPassedRules(missionDetails, true, ETRConfigurationService.getMissionCiviliansInternalVicePresidentPeriodMax(), true, ETRConfigurationService.getMissionCiviliansInternalDMVicePresidentRankLessThan(), true)) {
			throw new BusinessException("error_vicePresidentViolateMissionCivilianRules", new Object[] { ETRConfigurationService.getMissionCiviliansInternalVicePresidentPeriodMax(), CommonService.getRankById(ETRConfigurationService.getMissionCiviliansInternalDMVicePresidentRankLessThan()).getDescription() });
		    }
		}
	    }
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Validate that none of mission employees has any current missions under processing
     * 
     * @param empsIds
     *            Arrays of employee ids representing ids of mission details
     * @param startDateString
     *            start date String to check when other modules are calling this method
     * @param endDateString
     *            end date String to check when other modules are calling this method
     * @param excludedInstanceId
     *            Excluded current mission instance id
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     */
    public static List<WFMissionDetail> validateRunningMissionRequests(Long[] empsIds, String startDateString, String endDateString, Long excludedInstanceId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_BENEFICIARY_IDS", empsIds);

	    qParams.put("P_EXCLUDED_INSTANCE_ID", excludedInstanceId == null ? FlagsEnum.ALL.getCode() : excludedInstanceId);
	    if (startDateString == null || endDateString == null) {
		qParams.put("P_DATES_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_START_DATE", HijriDateService.getHijriSysDate());
		qParams.put("P_END_DATE", HijriDateService.getHijriSysDate());
	    } else {
		qParams.put("P_DATES_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_START_DATE", startDateString);
		qParams.put("P_END_DATE", endDateString);
	    }

	    return DataAccess.executeNamedQuery(WFMissionDetail.class, QueryNamesEnum.WF_MISSIONS_DETAILS_VALIDATE_RUNNING_PROCESSES.getCode(), qParams);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Check if requester employee has the authority to send all missions requests for certain of these categories ( officers, soldiers , or civilians )
     * 
     * @param requester
     *            Employee that started the mission request
     * @param processId
     *            Process Id , it may be officers missions , soldiers mission , or civilians mission
     * @return Boolean represents if this action is granted for this employee or not
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see WFProcessesEnum
     */

    private static boolean isRequesterAuthorizedToSendAll(EmployeeData requester, long processId) throws BusinessException {
	if (processId == WFProcessesEnum.OFFICERS_MISSION.getCode()) {
	    return SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.MSN_REQUEST_OFFICERS_SEND.getCode(), MenuActionsEnum.MSN_REQUEST_SEND_ALL_OFFICERS.getCode());
	} else if (processId == WFProcessesEnum.SOLDIERS_MISSION.getCode()) {
	    return SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.MSN_REQUEST_SOLDIERS_SEND.getCode(), MenuActionsEnum.MSN_REQUEST_SEND_ALL_SOLDIERS.getCode());
	} else if (processId == WFProcessesEnum.CIVILIANS_MISSION.getCode()) {
	    return SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), MenuCodesEnum.MSN_REQUEST_PERSONS_SEND.getCode(), MenuActionsEnum.MSN_REQUEST_SEND_ALL_PERSONS.getCode());
	}

	return false;
    }

    // ***********************************************************************************************************************/

    /**
     * Construct mission transaction data using {@link WFMissionData} object
     * 
     * @param mission
     *            {@link MissionData} Object view that contain all Data gotten by User from UI
     * @param instance
     *            Current mission instance
     * @param session
     *            The {@link CustomSession} is optional parameter used to access the database if no other session opened
     * @return {@link MissionData} transaction
     * 
     * @throws Exception
     *             if any exceptions or errors occurs
     */
    private static MissionData constructMissionTransaction(WFMissionData mission, WFInstance instance, CustomSession session) {

	MissionData missionTransaction = new MissionData();

	missionTransaction.setPurpose(mission.getPurpose());
	missionTransaction.setPreviousFlag(mission.getPreviousFlag());
	missionTransaction.setPreviousDescription(mission.getPreviousDescription());
	missionTransaction.setPreviousExecutedFlag(mission.getPreviousExecutedFlag());
	missionTransaction.setRelatedFlag(mission.getRelatedFlag());
	missionTransaction.setNotExecutedRelatedFlag(mission.getNotExecutedRelatedFlag());
	missionTransaction.setRelatedDescription(mission.getRelatedDescription());
	missionTransaction.setBasedOnFlag(mission.getBasedOnFlag());
	missionTransaction.setBasedOnNumber(mission.getBasedOnNumber());
	missionTransaction.setBasedOnDate(mission.getBasedOnDate());
	missionTransaction.setResultReportFlag(mission.getResultReportFlag());
	missionTransaction.setResultLetterFlag(mission.getResultLetterFlag());
	missionTransaction.setResultOtherFlag(mission.getResultOtherFlag());
	missionTransaction.setResultLetterDescription(mission.getResultLetterDescription());
	missionTransaction.setResultOtherDescription(mission.getResultOtherDescription());

	missionTransaction.setReferring(mission.getReferring());
	missionTransaction.setLocationFlag(mission.getLocationFlag());
	missionTransaction.setLocation(mission.getLocation());
	missionTransaction.setRegionsCodes(mission.getRegionsCodes());
	missionTransaction.setDestination(mission.getDestination());
	missionTransaction.setEmpExternalFlag(mission.getEmpExternalFlag());
	missionTransaction.setEmpExternalStatus(mission.getEmpExternalStatus());
	missionTransaction.setPeriod(mission.getPeriod());
	missionTransaction.setRoadPeriod(mission.getRoadPeriod());
	missionTransaction.setStartDate(mission.getStartDate());
	missionTransaction.setEndDate(mission.getEndDate());
	missionTransaction.setMinisteryApprovalNumber(mission.getMinisteryApprovalNumber());
	missionTransaction.setMinisteryApprovalDate(mission.getMinisteryApprovalDate());
	missionTransaction.setMinisteryApprovalDateString(mission.getMinisteryApprovalDateString());
	missionTransaction.setRoadLine(mission.getRoadLine());
	missionTransaction.setDoubleBalanceFlag(mission.getDoubleBalanceFlag());
	missionTransaction.setHajjFlag(mission.getHajjFlag());
	missionTransaction.setRemarks(mission.getRemarks());
	missionTransaction.setInternalCopies(mission.getInternalCopies());
	missionTransaction.setExternalCopies(mission.getExternalCopies());
	missionTransaction.setApprovedId(mission.getApprovedId());
	missionTransaction.setDecisionApprovedId(mission.getDecisionApprovedId());
	missionTransaction.setOriginalDecisionApprovedId(mission.getOriginalDecisionApprovedId());
	missionTransaction.setAttachments(instance.getAttachments());
	missionTransaction.setDecisionRegionId(mission.getDecisionRegionId());
	missionTransaction.setDirectedToJobName(mission.getDirectedToJobName());

	if (instance.getProcessId() == WFProcessesEnum.OFFICERS_MISSION.getCode()) {
	    missionTransaction.setCategoryMode(CategoryModesEnum.OFFICERS.getCode());
	} else if (instance.getProcessId() == WFProcessesEnum.SOLDIERS_MISSION.getCode()) {
	    missionTransaction.setCategoryMode(CategoryModesEnum.SOLDIERS.getCode());
	} else if (instance.getProcessId() == WFProcessesEnum.CIVILIANS_MISSION.getCode()) {
	    missionTransaction.setCategoryMode(CategoryModesEnum.CIVILIANS.getCode());
	}

	// E-flag, Migration flag, decision number and decision date are set by
	// mission service

	return missionTransaction;
    }

    /**
     * Construct mission details transactions data using List of {@link WFMissionDetailData}
     * 
     * @param missionDetails
     *            List of {@link MissionDetailData} requests need to be handled
     * @param session
     *            The {@link CustomSession} is optional parameter used to access the database if no other session opened
     * @return List of {@link MissionDetailData} transactions
     * @throws Exception
     *             if any exceptions or errors occurs
     */

    private static List<MissionDetailData> constructMissionDetailsTransactions(List<WFMissionDetailData> missionDetails, CustomSession session) {

	List<MissionDetailData> MissionDetailsTransactions = new ArrayList<MissionDetailData>();

	for (WFMissionDetailData missionDetail : missionDetails) {
	    MissionDetailData missionDetailTransaction = new MissionDetailData();

	    // mission Id set by mission service
	    missionDetailTransaction.setEmpId(missionDetail.getEmpId());
	    missionDetailTransaction.setEmpName(missionDetail.getEmpName());
	    missionDetailTransaction.setTransEmpUnitDesc(missionDetail.getEmpUnitDesc());
	    missionDetailTransaction.setTransEmpJobCode(missionDetail.getEmpJobCode());
	    missionDetailTransaction.setTransEmpJobDesc(missionDetail.getEmpJobDesc());
	    missionDetailTransaction.setTransEmpRankDesc(missionDetail.getEmpRankDesc() + ((missionDetail.getEmpRankTitleDesc()) == null ? "" : " " + missionDetail.getEmpRankTitleDesc()));
	    missionDetailTransaction.setBalance(missionDetail.getBalance());
	    missionDetailTransaction.setPeriod(missionDetail.getPeriod());
	    missionDetailTransaction.setRoadPeriod(missionDetail.getRoadPeriod());
	    missionDetailTransaction.setStartDate(missionDetail.getStartDate());
	    missionDetailTransaction.setEndDate(missionDetail.getEndDate());
	    // transfer in details period to actual period and dates to actual
	    // dates
	    missionDetailTransaction.setActualPeriod(missionDetail.getPeriod());
	    missionDetailTransaction.setActualStartDate(missionDetail.getStartDate());
	    missionDetailTransaction.setActualEndDate(missionDetail.getEndDate());
	    missionDetailTransaction.setExceptionalApprovalNumber(missionDetail.getExceptionalApprovalNumber());
	    missionDetailTransaction.setExceptionalApprovalDate(missionDetail.getExceptionalApprovalDate());
	    missionDetailTransaction.setRoadLine(missionDetail.getRoadLine());
	    missionDetailTransaction.setAbsenceFlag(FlagsEnum.OFF.getCode());
	    missionDetailTransaction.setRemarks(missionDetail.getRemarks());
	    missionDetailTransaction.setMilitaryNumber(missionDetail.getEmpMilitaryNumber());

	    MissionDetailsTransactions.add(missionDetailTransaction);
	}

	return MissionDetailsTransactions;
    }

    /**
     * Add mission detail and set its fields using {@link WFMissionData} Object, and employee data
     * 
     * @param empId
     *            Employee id that will be used to get employee Object to assign some data from it to mission detail
     * @param mission
     *            {@link MissionData} Object view that contain all Data gotten by User from UI
     * @return {@link MissionDetailData} mission detail data that will be added to current mission
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */

    public static WFMissionDetailData addWFMissionDetail(long empId, WFMissionData mission) throws BusinessException {

	// set mission detail with employee data and mission data and balance

	EmployeeData employee = EmployeesService.getEmployeeData(empId);
	if (employee == null)
	    throw new BusinessException("error_employeeDataError");

	WFMissionDetailData empMissionDetail = new WFMissionDetailData();

	// fill employee mission details & regarding officers empMilitaryNumber
	// will be filled
	empMissionDetail.setEmpId(employee.getEmpId());
	empMissionDetail.setEmpName(employee.getName());

	empMissionDetail.setEmpMilitaryNumber(employee.getMilitaryNumber());

	empMissionDetail.setEmpUnitDesc(employee.getPhysicalUnitFullName());
	empMissionDetail.setEmpPhysicalUnitHKey(employee.getPhysicalUnitHKey());

	empMissionDetail.setEmpJobCode(employee.getJobCode());
	empMissionDetail.setEmpJobDesc(employee.getJobDesc());

	empMissionDetail.setEmpRankId(employee.getRankId());
	empMissionDetail.setEmpRankDesc(employee.getRankDesc());
	empMissionDetail.setEmpRankTitleDesc(employee.getRankTitleDesc());

	// calculate balance even if negative value it will be catch in
	// validating data!!!. may be user change master mission data
	empMissionDetail.setBalance(MissionsService.calculateEmpMissionsBalance(employee.getEmpId(), mission.getStartDate()));

	empMissionDetail.setPeriod(mission.getPeriod());
	empMissionDetail.setRoadPeriod(mission.getRoadPeriod());
	empMissionDetail.setStartDate(mission.getStartDate());
	empMissionDetail.setEndDate(mission.getEndDate());

	empMissionDetail.setRoadLine(mission.getRoadLine());

	return empMissionDetail;
    }

    /**
     * Delete mission detail from List of mission details and also remove it from DB
     * 
     * @param missionDetail
     *            Mission detail to be removed from the list
     * @param missionDetails
     *            List of mission details {@link MissionDetailData}
     * @param useSession
     *            The {@link CustomSession} is optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */

    public static void deleteWFMissionDetail(WFMissionDetailData missionDetail, List<WFMissionDetailData> missionDetails, CustomSession... useSession) throws BusinessException {

	if (missionDetail.getInstanceId() == null) {
	    missionDetails.remove(missionDetail);
	    return;
	}

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.deleteEntity(missionDetail.getWfMissionDetail(), session);
	    deleteWFInstanceBeneficiaries(missionDetail.getInstanceId(), Arrays.asList(missionDetail.getEmpId()), session);

	    if (!isOpenedSession)
		session.commitTransaction();

	    missionDetails.remove(missionDetail);

	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}

    }

    /***********************************************************************************************************************/

    /**
     * Check if any of mission details has exceptional approval information
     * 
     * @param missionDetails
     *            List of mission details {@link MissionDetailData}
     * @return A flag that represents if this list has any item with exceptional information of not
     */

    private static boolean hasExceptionalApprovalInfo(List<WFMissionDetailData> missionDetails) {
	for (WFMissionDetailData missionDetailData : missionDetails) {
	    if (missionDetailData.getExceptionalApprovalDate() != null && missionDetailData.getExceptionalApprovalNumber() != null) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Check if any of the mission details has passed one of these rules
     * <ul>
     * <li>Maximum period</li>
     * <li>Maximum rank id</li>
     * <li>Check if employee that have president role is direct manager</li>
     * </ul>
     * 
     * @param missionDetails
     *            List of mission details {@link MissionDetailData}
     * @param checkMaxPeriod
     *            A flag representing if we will consider maximum period check or not
     * @param maxPeriod
     *            Maximum period allowed
     * @param checkMaxRank
     *            A flag representing if we will consider maximum rank check or not
     * @param maxRankId
     *            Maximum rank allowed
     * @param checkPresidencyAsDM
     *            A flag representing if we will consider president as direct manager check or not
     * @return A boolean flag representing if this list of mission details has padded rules or not
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     */

    private static boolean hasPassedRules(List<WFMissionDetailData> missionDetails, boolean checkMaxPeriod, int maxPeriod, boolean checkMaxRank, long maxRankId, boolean checkPresidencyAsDM) throws BusinessException {
	for (WFMissionDetailData mdd : missionDetails) {
	    if (checkMaxPeriod && mdd.getPeriod().intValue() > maxPeriod)
		return true;

	    if (checkMaxRank && mdd.getEmpRankId().longValue() <= maxRankId)
		return true;

	    if (checkPresidencyAsDM && EmployeesService.getEmployeeDirectManager(mdd.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode())
		return true;
	}

	return false;
    }

    /**
     * Get Employee data of presidency military assistance position
     * 
     * @return Employee data of presidency military assistance
     * @throws DatabaseException
     *             if any DB exceptions or errors occurs
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see WFPositionsEnum
     */

    private static EmployeeData getPresidencyMilitaryAssistance() throws BusinessException {
	WFPosition position = getWFPosition(WFPositionsEnum.MILITARY_AFFAIRS_ASSISTANT.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
	return EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());
    }

    /**
     * Get vice presidency id by vice president position
     * 
     * @return vice presidency id
     * 
     * @throws DatabaseException
     *             if any DB exceptions or errors occurs
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see WFPositionsEnum
     */

    private static long getVicePresidencyId() throws BusinessException {
	WFPosition position = getWFPosition(WFPositionsEnum.VICE_PRESIDENT.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
	return EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId()).getEmpId();
    }

    /**
     * Get mission manager using category and position of missions regions of mission employee
     * 
     * @param missionEmp
     *            Mission employee data {@link EmployeeData}
     * @return Employee data of mission manager
     * 
     * @throws DatabaseException
     *             if any DB exceptions or errors occurs
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see CategoriesEnum
     */

    private static EmployeeData getMissionManager(EmployeeData missionEmp) throws BusinessException {
	WFPosition position = new WFPosition();
	long categoryId = missionEmp.getCategoryId();
	long regionId = missionEmp.getPhysicalRegionId();

	if (CategoriesEnum.OFFICERS.getCode() == categoryId) {
	    if (RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == regionId)
		position = getWFPosition(WFPositionsEnum.OFFICERS_ALLOWANCES_UNIT_MANAGER.getCode(), regionId);
	    else if (RegionsEnum.MAKKAH_ALMOKARRAMAH_REGION.getCode() == regionId)
		position = getWFPosition(WFPositionsEnum.REGION_PAYROLL_UNIT_MANAGER.getCode(), regionId);
	    else
		position = getWFPosition(WFPositionsEnum.REGION_OFFICERS_AFFAIRS_UNIT_MANAGER.getCode(), regionId);
	} else if (CategoriesEnum.SOLDIERS.getCode() == categoryId) {
	    if (RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == regionId)
		position = getWFPosition(WFPositionsEnum.SOLDIERS_ALLOWANCES_UNIT_MANAGER.getCode(), regionId);
	    else if (RegionsEnum.MAKKAH_ALMOKARRAMAH_REGION.getCode() == regionId)
		position = getWFPosition(WFPositionsEnum.REGION_PAYROLL_UNIT_MANAGER.getCode(), regionId);
	    else
		position = getWFPosition(WFPositionsEnum.REGION_SOLDIERS_AFFAIRS_UNIT_MANAGER.getCode(), regionId);
	} else {
	    if (RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == regionId)
		position = getWFPosition(WFPositionsEnum.CIVILIANS_ALLOWANCES_UNIT_MANAGER.getCode(), regionId);
	    else
		position = getWFPosition(WFPositionsEnum.REGION_CIVILIANS_AFFAIRS_UNIT_MANAGER.getCode(), regionId);

	}

	return EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());
    }

    /***********************************************************************************************************************/

    /**
     * Get all tasks associated with assignee employee and assignee workflow role
     * 
     * @param assigneeId
     *            Id of assignee employee for that mission to task action
     * @param assigneeWfRole
     *            Assignee workflow role
     * @return List of Mission tasks associated with these parameters
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see WFTaskRolesEnum
     */

    public static List<Object> getWFMissionsTasks(Long assigneeId, String assigneeWfRole) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ASSIGNEE_ID", assigneeId);
	    qParams.put("P_ASSIGNEE_WF_ROLE", assigneeWfRole);
	    return DataAccess.executeNamedQuery(Object.class, QueryNamesEnum.WF_GET_WFMISSIONS_TASKS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Get {@link WFMissionData} using current mission workflow instance
     * 
     * @param instanceId
     *            Id of current mission workflow instance
     * @return {@link WFMissionData}
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */

    public static WFMissionData getWFMissionByInstanceId(long instanceId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCE_ID", instanceId);
	    return DataAccess.executeNamedQuery(WFMissionData.class, QueryNamesEnum.WF_GET_MISSION_BY_INSTANCE_ID.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Wrapper for {@link #searchWFMissionDetailsByInstanceId(Long[])}, it get List of {@link WFMissionDetailData} using mission workflow instance
     * 
     * @param instanceId
     *            Id of current mission workflow instance
     * @return List of {@link WFMissionDetailData} mission workflow instance id
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see #searchWFMissionDetailsByInstanceId(Long[])
     */

    public static List<WFMissionDetailData> getWFMissionDetailsByInstanceId(long instanceId) throws BusinessException {
	return searchWFMissionDetailsByInstanceId(new Long[] { instanceId });
    }

    /**
     * Get Map of mission workflow instances and corresponding List of {@link WFMissionDetailData} using Array of instance ids
     * 
     * @param instanceIds
     *            Array of mission workflow instance ids
     * @return Map of instance id and its corresponding List of {@link WFMissionDetailData}
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */

    public static Map<Long, List<WFMissionDetailData>> getWFMissionDetailsByInstanceIds(Long[] instanceIds) throws BusinessException {
	List<WFMissionDetailData> wfMissionDetailsData = searchWFMissionDetailsByInstanceId(instanceIds);
	Map<Long, List<WFMissionDetailData>> wfMissionDetails = new HashMap<Long, List<WFMissionDetailData>>();

	for (WFMissionDetailData wfMissionDetailData : wfMissionDetailsData) {
	    List<WFMissionDetailData> wfMissionDetailDataList = wfMissionDetails.get(wfMissionDetailData.getInstanceId());
	    if (wfMissionDetailDataList == null) {
		wfMissionDetailDataList = new ArrayList<WFMissionDetailData>();
		wfMissionDetailDataList.add(wfMissionDetailData);
		wfMissionDetails.put(wfMissionDetailData.getInstanceId(), wfMissionDetailDataList);
	    } else {
		wfMissionDetailDataList.add(wfMissionDetailData);
	    }
	}
	return wfMissionDetails;
    }

    /**
     * Get List of {@link WFMissionDetailData} using Array of mission workflow instance ids
     * 
     * @param instanceIds
     *            Array of mission workflow instance ids
     * @return List of {@link WFMissionDetailData}
     * @throws BusinessException
     *             if any exceptions or errors occurs
     */

    private static List<WFMissionDetailData> searchWFMissionDetailsByInstanceId(Long[] instanceIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCE_ID", instanceIds);
	    return DataAccess.executeNamedQuery(WFMissionDetailData.class, QueryNamesEnum.WF_GET_MISSION_DETAILS_BY_INSTANCE_ID.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*-----------------------------------------------------WFMission Print---------------------------------------------*/

    /**
     * print individual or collective workflow mission data using employee instance Id and category mode
     * 
     * @param instanceId
     *            Instance id
     * @param categoryMode
     *            Category mode (officers missions, soldiers missions, or civilians missions)
     * @return Array of bytes to print
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see CategoriesEnum
     */

    public static byte[] getWfMissionDataBytes(Long instanceId, Integer categoryMode) throws BusinessException {
	try {
	    String reportName = "";
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    List<WFMissionDetailData> wfMissionDetailDataList = MissionsWorkFlow.getWFMissionDetailsByInstanceId(instanceId);
	    if (categoryMode.longValue() == CategoriesEnum.OFFICERS.getCode()) {
		if (wfMissionDetailDataList != null && wfMissionDetailDataList.size() > 1) {
		    reportName = ReportNamesEnum.MISSIONS_OFFICERS_DATA_COLLECTIVE.getCode();
		} else {
		    reportName = ReportNamesEnum.MISSIONS_OFFICERS_DATA.getCode();
		}
	    } else if (categoryMode.longValue() == CategoriesEnum.SOLDIERS.getCode()) {
		if (wfMissionDetailDataList != null && wfMissionDetailDataList.size() > 1) {
		    reportName = ReportNamesEnum.MISSIONS_SOLDIERS_DATA_COLLECTIVE.getCode();
		} else {
		    reportName = ReportNamesEnum.MISSIONS_SOLDIERS_DATA.getCode();
		}
	    } else {
		if (wfMissionDetailDataList != null && wfMissionDetailDataList.size() > 1) {
		    reportName = ReportNamesEnum.MISSIONS_CIVILIANS_DATA_COLLECTIVE.getCode();
		} else {
		    reportName = ReportNamesEnum.MISSIONS_CIVILIANS_DATA.getCode();
		}
	    }

	    parameters.put("P_INSTANCE_ID", instanceId);
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

}
