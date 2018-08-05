package com.code.services.workflow.hcm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.vacations.Vacation;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFPosition;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.hcm.vacations.WFVacation;
import com.code.dal.orm.workflow.hcm.vacations.WFVacationData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.RequestTypesEnum;
import com.code.enums.UnitTypesEnum;
import com.code.enums.VacationTypesEnum;
import com.code.enums.WFInstanceStatusEnum;
import com.code.enums.WFPositionsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskActionsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.config.ETRConfigurationService;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.UnitsService;
import com.code.services.hcm.VacationsService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.BaseWorkFlow;

public class VacationsWorkFlow extends BaseWorkFlow {

    /**
     * Constructs a newly allocated <code>VacationsWorkFlow</code> object.
     */
    private VacationsWorkFlow() {
    }

    /********************************************* Vacations WorkFlow ************************************************/
    /*--------------------------------------------------- Steps -----------------------------------------------------*/

    public static void initVacation(EmployeeData requester, EmployeeData vacBeneficiary, WFVacation vacRequest, long processId, String attachments, String taskUrl) throws BusinessException {

	Long originalId = (requester.getEmpId().equals(vacBeneficiary.getEmpId())) ? requester.getManagerId() : requester.getEmpId();
	vacRequest.setBeneficiaryId(vacBeneficiary.getEmpId());

	validateVacationRequestData(vacBeneficiary, vacRequest);

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    WFInstance instance = addWFInstance(processId, requester.getEmpId(), curDate, curHijriDate, WFInstanceStatusEnum.RUNNING.getCode(), attachments, Arrays.asList(vacRequest.getBeneficiaryId()), session);

	    addWFTask(instance.getInstanceId(), getDelegate(originalId, processId, vacBeneficiary.getEmpId()), originalId, curDate, curHijriDate, taskUrl, WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);

	    vacRequest.setInstanceId(instance.getInstanceId());
	    DataAccess.addEntity(vacRequest, session);

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
     * If direct manager rejects then close instance and notify requester If direct manager approves then send to its manager until reach assistant or higher role
     * 
     * @param requester
     * @param instance
     * @param vacRequest
     * @param dmTask
     * @param isApproved
     * @throws BusinessException
     */
    public static void doVacationDM(EmployeeData requester, EmployeeData vacBeneficiary, WFInstance instance, WFVacation vacRequest, WFTask dmTask, boolean isApproved) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (isApproved) {
		long categoryId = vacBeneficiary.getCategoryId();
		long vacationTypeId = vacRequest.getVacationTypeId();

		boolean isWorkFlowClosed = false;
		EmployeeData curDM = EmployeesService.getEmployeeData(dmTask.getOriginalId());

		if (CategoriesEnum.OFFICERS.getCode() == categoryId) {
		    if (VacationTypesEnum.REGULAR.getCode() == vacationTypeId || VacationTypesEnum.COMPELLING.getCode() == vacationTypeId) {
			// The beneficiary is related to the general region.
			if (RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == vacBeneficiary.getPhysicalRegionId()) {
			    if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
				vacRequest.setApprovedId(dmTask.getOriginalId());

				EmployeeData vacManager = getVacationManager(vacBeneficiary, vacationTypeId);
				completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacManager.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacManager.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
			    } else {
				completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), vacBeneficiary.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			    }
			} else { // The beneficiary is related to another region.
				 // The beneficiary is the region commander or one of his assistants.
			    if (vacBeneficiary.getUnitTypeCode().intValue() == UnitTypesEnum.REGION_COMMANDER.getCode()
				    && EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
				vacRequest.setApprovedId(dmTask.getOriginalId());

				EmployeeData vacManager = getVacationManager(vacBeneficiary, vacationTypeId);
				completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacManager.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacManager.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
			    } else if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
				// The beneficiary is not the region commander neither one of his assistants.
				vacRequest.setApprovedId(dmTask.getOriginalId());

				EmployeeData vacManager = getVacationManager(vacBeneficiary, vacationTypeId);
				completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacManager.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacManager.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
			    } else {
				completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), vacBeneficiary.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			    }
			}
		    } else if (VacationTypesEnum.SICK.getCode() == vacationTypeId) {
			// The beneficiary is related to the general region.
			if (RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == vacBeneficiary.getPhysicalRegionId()) {
			    if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
				vacRequest.setApprovedId(dmTask.getOriginalId());

				// New or modify sick vacation with period greater than the configured days.
				if ((RequestTypesEnum.NEW.getCode() == vacRequest.getRequestType().intValue() || RequestTypesEnum.MODIFY.getCode() == vacRequest.getRequestType().intValue())
					&& vacRequest.getPeriod() > ETRConfigurationService.getVacationMaxSickVacationPeriodApprovedDirectly()) {
				    EmployeeData medicalEmployee = getMedicalEmployee(vacBeneficiary);
				    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(medicalEmployee.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), medicalEmployee.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MEDICAL_EMP.getCode(), "1", session);
				} else {
				    EmployeeData vacManager = getVacationManager(vacBeneficiary, vacationTypeId);
				    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacManager.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacManager.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
				}
			    } else {
				completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), vacBeneficiary.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			    }
			} else { // The beneficiary is related to another region.
				 // The beneficiary is the region commander or one of his assistants.
			    if (vacBeneficiary.getUnitTypeCode().intValue() == UnitTypesEnum.REGION_COMMANDER.getCode() &&
				    EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
				vacRequest.setApprovedId(dmTask.getOriginalId());

				// New or modify sick vacation with period greater than configured days.
				if ((RequestTypesEnum.NEW.getCode() == vacRequest.getRequestType().intValue() || RequestTypesEnum.MODIFY.getCode() == vacRequest.getRequestType().intValue())
					&& vacRequest.getPeriod() > ETRConfigurationService.getVacationMaxSickVacationPeriodApprovedDirectly()) {
				    EmployeeData medicalEmployee = getMedicalEmployee(vacBeneficiary);
				    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(medicalEmployee.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), medicalEmployee.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MEDICAL_EMP.getCode(), "1", session);
				} else {
				    EmployeeData vacManager = getVacationManager(vacBeneficiary, vacationTypeId);
				    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacManager.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacManager.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
				}
			    } else if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
				// The beneficiary is not the region commander neither one of his assistants.
				vacRequest.setApprovedId(dmTask.getOriginalId());

				// New or modify sick vacation with period greater than configured days.
				if ((RequestTypesEnum.NEW.getCode() == vacRequest.getRequestType().intValue()
					|| RequestTypesEnum.MODIFY.getCode() == vacRequest.getRequestType().intValue())
					&& vacRequest.getPeriod() > ETRConfigurationService.getVacationMaxSickVacationPeriodApprovedDirectly()) {
				    EmployeeData medicalEmployee = getMedicalEmployee(vacBeneficiary);
				    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(medicalEmployee.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), medicalEmployee.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MEDICAL_EMP.getCode(), "1", session);
				} else {
				    EmployeeData vacManager = getVacationManager(vacBeneficiary, vacationTypeId);
				    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacManager.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacManager.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
				}
			    } else {
				completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), vacBeneficiary.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			    }
			}
		    } else if (VacationTypesEnum.FIELD.getCode() == vacationTypeId) {
			if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
			    vacRequest.setApprovedId(dmTask.getOriginalId());

			    EmployeeData vacManager = getVacationManager(vacBeneficiary, vacationTypeId);
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacManager.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacManager.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
			} else {
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), vacBeneficiary.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			}
		    } else if (VacationTypesEnum.EXCEPTIONAL.getCode() == vacationTypeId) {
			if (curDM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
			    vacRequest.setApprovedId(dmTask.getOriginalId());

			    EmployeeData vacManager = getVacationManager(vacBeneficiary, vacationTypeId);
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacManager.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacManager.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
			} else if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()
				&& !vacBeneficiary.getManagerId().equals(curDM.getManagerId())
				&& vacBeneficiary.getRankId() > ETRConfigurationService.getVacationOfficersExceptionalDmAllVpRanksGreaterThan()) {
			    // Send to the VICE PRESIDENT
			    WFPosition vicePresidentPosition = getWFPosition(WFPositionsEnum.VICE_PRESIDENT.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
			    EmployeeData vicePresident = EmployeesService.getEmployeeByPosition(vicePresidentPosition.getUnitId(), vicePresidentPosition.getEmpId());
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vicePresident.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vicePresident.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			} else {
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), vacBeneficiary.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			}
		    }
		} else if (CategoriesEnum.SOLDIERS.getCode() == categoryId) {
		    if (VacationTypesEnum.REGULAR.getCode() == vacationTypeId || VacationTypesEnum.MATERNITY.getCode() == vacationTypeId
			    || VacationTypesEnum.MOTHERHOOD.getCode() == vacationTypeId || VacationTypesEnum.DEATH_WAITING_PERIOD.getCode() == vacationTypeId) {
			// The beneficiary is related to the general region.
			if (RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == vacBeneficiary.getPhysicalRegionId()) {
			    if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
				vacRequest.setApprovedId(dmTask.getOriginalId());

				WFPosition position = getWFPosition(WFPositionsEnum.SOLDIERS_INFORMATION_UNIT_VACATIONS_PERSONNEL.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
				EmployeeData vacEmployee = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());

				// New or modify external regular/maternity/motherhood/deathWaitingPeriod vacation for soldiers.
				if ((RequestTypesEnum.NEW.getCode() == vacRequest.getRequestType().intValue() || RequestTypesEnum.MODIFY.getCode() == vacRequest.getRequestType().intValue())
					&& LocationFlagsEnum.EXTERNAL.getCode() == vacRequest.getLocationFlag().intValue()) {
				    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacEmployee.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacEmployee.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP_REDIRECT.getCode(), "1", session);
				} else {
				    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacEmployee.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacEmployee.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), "1", session);
				}
			    } else {
				completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), vacBeneficiary.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			    }
			} else { // The beneficiary is related to another region.
			    if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
				vacRequest.setApprovedId(dmTask.getOriginalId());

				EmployeeData vacManager = getVacationManager(vacBeneficiary, vacationTypeId);

				// New or modify external regular/maternity/motherhood/deathWaitingPeriod vacation for soldiers.
				if ((RequestTypesEnum.NEW.getCode() == vacRequest.getRequestType().intValue() || RequestTypesEnum.MODIFY.getCode() == vacRequest.getRequestType().intValue())
					&& LocationFlagsEnum.EXTERNAL.getCode() == vacRequest.getLocationFlag().intValue()) {
				    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacManager.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacManager.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP_REDIRECT.getCode(), "1", session);
				} else {
				    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacManager.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacManager.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
				}
			    } else {
				completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), vacBeneficiary.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			    }
			}
		    } else if (VacationTypesEnum.COMPELLING.getCode() == vacationTypeId || VacationTypesEnum.EXAM.getCode() == vacationTypeId) {
			// The beneficiary is related to the general region.
			if (RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == vacBeneficiary.getPhysicalRegionId()) {
			    if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
				vacRequest.setApprovedId(dmTask.getOriginalId());

				// Closed by the last direct manager if the vacation is internal compelling or exam for soldiers.
				if (LocationFlagsEnum.INTERNAL.getCode() == vacRequest.getLocationFlag().intValue()) {
				    isWorkFlowClosed = true;
				} else {
				    WFPosition position = getWFPosition(WFPositionsEnum.SOLDIERS_INFORMATION_UNIT_VACATIONS_PERSONNEL.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
				    EmployeeData vacEmployee = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());

				    // New external compelling vacation for soldiers.
				    if (RequestTypesEnum.NEW.getCode() == vacRequest.getRequestType().intValue() && LocationFlagsEnum.EXTERNAL.getCode() == vacRequest.getLocationFlag().intValue()) {
					completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacEmployee.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacEmployee.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP_REDIRECT.getCode(), "1", session);
				    } else {
					completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacEmployee.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacEmployee.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), "1", session);
				    }
				}
			    } else {
				completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), vacBeneficiary.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			    }
			} else { // The beneficiary is related to another region.
			    if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
				// Closed by the last direct manager if he is the region commander if the vacation is internal compelling or exam for soldiers.
				if (LocationFlagsEnum.INTERNAL.getCode() == vacRequest.getLocationFlag().intValue()) {
				    if (curDM.getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
					vacRequest.setApprovedId(dmTask.getOriginalId());
					isWorkFlowClosed = true;
				    } else {
					completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), vacBeneficiary.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
				    }
				} else {
				    vacRequest.setApprovedId(dmTask.getOriginalId());
				    EmployeeData vacManager = getVacationManager(vacBeneficiary, vacationTypeId);
				    // New external compelling vacation for soldiers.
				    if (RequestTypesEnum.NEW.getCode() == vacRequest.getRequestType().intValue() && LocationFlagsEnum.EXTERNAL.getCode() == vacRequest.getLocationFlag().intValue()) {
					completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacManager.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacManager.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP_REDIRECT.getCode(), "1", session);
				    } else {
					completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacManager.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacManager.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
				    }
				}
			    } else {
				completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), vacBeneficiary.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			    }
			}
		    } else if (VacationTypesEnum.SICK.getCode() == vacationTypeId) {
			// The beneficiary is related to the general region.
			if (vacBeneficiary.getPhysicalRegionId() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) {
			    if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
				vacRequest.setApprovedId(dmTask.getOriginalId());

				// New or modify sick vacation with period greater than configured days.
				if ((RequestTypesEnum.NEW.getCode() == vacRequest.getRequestType().intValue() || RequestTypesEnum.MODIFY.getCode() == vacRequest.getRequestType().intValue())
					&& vacRequest.getPeriod() > ETRConfigurationService.getVacationMaxSickVacationPeriodApprovedDirectly()) {
				    EmployeeData medicalEmployee = getMedicalEmployee(vacBeneficiary);
				    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(medicalEmployee.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), medicalEmployee.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MEDICAL_EMP.getCode(), "1", session);
				} else {
				    WFPosition position = getWFPosition(WFPositionsEnum.SOLDIERS_FOLLOW_UP_UNIT_VACATIONS_PERSONNEL.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
				    EmployeeData vacEmployee = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());
				    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacEmployee.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacEmployee.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), "1", session);
				}
			    } else {
				completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), vacBeneficiary.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			    }
			} else { // The beneficiary is related to another region.
			    if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
				vacRequest.setApprovedId(dmTask.getOriginalId());

				// New or modify sick vacation with period greater than configured days.
				if ((RequestTypesEnum.NEW.getCode() == vacRequest.getRequestType().intValue() || RequestTypesEnum.MODIFY.getCode() == vacRequest.getRequestType().intValue())
					&& vacRequest.getPeriod() > ETRConfigurationService.getVacationMaxSickVacationPeriodApprovedDirectly()) {
				    EmployeeData medicalEmployee = getMedicalEmployee(vacBeneficiary);
				    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(medicalEmployee.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), medicalEmployee.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MEDICAL_EMP.getCode(), "1", session);
				} else {
				    EmployeeData vacManager = getVacationManager(vacBeneficiary, vacationTypeId);
				    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacManager.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacManager.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
				}
			    } else {
				completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), vacBeneficiary.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			    }
			}
		    } else if (VacationTypesEnum.FIELD.getCode() == vacationTypeId) {
			if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
			    vacRequest.setApprovedId(dmTask.getOriginalId());

			    EmployeeData vacManager = getVacationManager(vacBeneficiary, vacationTypeId);
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacManager.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacManager.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
			} else {
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), vacBeneficiary.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			}
		    } else if (VacationTypesEnum.EXCEPTIONAL.getCode() == vacationTypeId) {

			if (curDM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
			    vacRequest.setApprovedId(dmTask.getOriginalId());

			    WFPosition position = getWFPosition(WFPositionsEnum.SOLDIERS_INFORMATION_UNIT_VACATIONS_PERSONNEL.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
			    EmployeeData vacEmployee = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());

			    // New external exceptional vacation for soldiers.
			    if (RequestTypesEnum.NEW.getCode() == vacRequest.getRequestType().intValue() && LocationFlagsEnum.EXTERNAL.getCode() == vacRequest.getLocationFlag().intValue()) {
				completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacEmployee.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacEmployee.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP_REDIRECT.getCode(), "1", session);
			    } else {
				completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacEmployee.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacEmployee.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), "1", session);
			    }
			} else if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
			    // Send to the VICE PRESIDENT
			    WFPosition vicePresidentPosition = getWFPosition(WFPositionsEnum.VICE_PRESIDENT.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
			    EmployeeData vicePresident = EmployeesService.getEmployeeByPosition(vicePresidentPosition.getUnitId(), vicePresidentPosition.getEmpId());
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vicePresident.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vicePresident.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			} else {
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), vacBeneficiary.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			}
		    }
		} else if ((CategoriesEnum.PERSONS.getCode() == categoryId)
			|| (CategoriesEnum.USERS.getCode() == categoryId)
			|| (CategoriesEnum.WAGES.getCode() == categoryId)
			|| (CategoriesEnum.CONTRACTORS.getCode() == categoryId)
			|| (CategoriesEnum.MEDICAL_STAFF.getCode() == categoryId)) {

		    if (VacationTypesEnum.STUDY.getCode() == vacationTypeId || VacationTypesEnum.EXCEPTIONAL.getCode() == vacationTypeId) {
			// The beneficiary is related to the general region or to another region.
			if (curDM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
			    vacRequest.setApprovedId(dmTask.getOriginalId());

			    EmployeeData vacManager = getVacationManager(vacBeneficiary, vacationTypeId);
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacManager.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacManager.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
			} else if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()
				&& !vacBeneficiary.getManagerId().equals(curDM.getManagerId())
				&& vacBeneficiary.getRankId() > ETRConfigurationService.getVacationCiviliansExceptionalStudyDmAllVpRanksGreaterThan()) {
			    // Send to the VICE PRESIDENT
			    WFPosition vicePresidentPosition = getWFPosition(WFPositionsEnum.VICE_PRESIDENT.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
			    EmployeeData vicePresident = EmployeesService.getEmployeeByPosition(vicePresidentPosition.getUnitId(), vicePresidentPosition.getEmpId());
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vicePresident.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vicePresident.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			} else {
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), vacBeneficiary.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			}
		    } else {
			// The beneficiary is related to the general region.
			if (RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == vacBeneficiary.getPhysicalRegionId()) {
			    if (EmployeesService.getEmployeeDirectManager(curDM.getManagerId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
				vacRequest.setApprovedId(dmTask.getOriginalId());

				EmployeeData vacManager = getVacationManager(vacBeneficiary, vacationTypeId);
				completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacManager.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacManager.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
			    } else {
				completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), vacBeneficiary.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			    }
			} else { // The beneficiary is related to another region.
			    if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
				vacRequest.setApprovedId(dmTask.getOriginalId());

				EmployeeData vacManager = getVacationManager(vacBeneficiary, vacationTypeId);
				completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacManager.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacManager.getEmpId(), dmTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
			    } else {
				completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), vacBeneficiary.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			    }
			}
		    }
		}

		if (isWorkFlowClosed)
		    closeVacationWorkFlow(requester, vacBeneficiary, instance, vacRequest, dmTask, curDM.getEmpId(), curDM.getPhysicalRegionId(), session);

	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, dmTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
	    }

	    DataAccess.updateEntity(vacRequest, session);
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
    public static void doVacationMR(EmployeeData requester, EmployeeData vacBeneficiary, WFInstance instance, WFTask mrTask, long reviewerEmpId) throws BusinessException {
	try {
	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    completeWFTask(mrTask, WFTaskActionsEnum.REDIRECT.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(reviewerEmpId, instance.getProcessId(), vacBeneficiary.getEmpId()), reviewerEmpId, mrTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), "1");
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    // Reviewer Employee
    public static void doVacationRE(EmployeeData requester, EmployeeData vacBeneficiary, WFInstance instance, WFVacation vacRequest, WFTask reTask, boolean isApproved) throws BusinessException {

	if (isApproved) {
	    if (vacRequest.getContractualYearStartDate() != null)
		VacationsService.validateContractualYearAndBalance(constructVacationTransaction(vacRequest, vacBeneficiary, null), vacBeneficiary);

	    validateReviewerEmployeeReferring(vacRequest, vacBeneficiary);
	}

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (isApproved) {
		EmployeeData vacManager = EmployeesService.getEmployeeDirectManager(reTask.getOriginalId());
		completeWFTask(reTask, WFTaskActionsEnum.REVIEW.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacManager.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacManager.getEmpId(), reTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);

		if (vacRequest.getContractualYearStartDate() != null || vacRequest.getReferring() != null)
		    DataAccess.updateEntity(vacRequest, session);
	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, reTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
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

    // Reviewer Employee Redirect
    public static void doVacationRER(EmployeeData requester, EmployeeData vacBeneficiary, WFInstance instance, WFVacation vacRequest, WFTask rerTask, boolean isApproved) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (isApproved) {
		EmployeeData securityEmployee = getSecurityEmployee(vacBeneficiary, vacRequest.getVacationTypeId());
		completeWFTask(rerTask, WFTaskActionsEnum.REDIRECT_SECURITY.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(securityEmployee.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), securityEmployee.getEmpId(), rerTask.getTaskUrl(), WFTaskRolesEnum.SECURITY_EMP.getCode(), "1", session);
	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, rerTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
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

    // Security Employee
    public static void doVacationSE(EmployeeData requester, EmployeeData vacBeneficiary, WFInstance instance, WFVacation vacRequest, WFTask seTask, boolean isApproved) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (isApproved) {
		WFTask reviewerEmpRedirectTask = getWFInstanceTasksByRole(instance.getInstanceId(), WFTaskRolesEnum.REVIEWER_EMP_REDIRECT.getCode()).get(0);

		if (RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == vacBeneficiary.getPhysicalRegionId()
			|| vacRequest.getVacationTypeId() == VacationTypesEnum.EXCEPTIONAL.getCode()) {
		    completeWFTask(seTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(reviewerEmpRedirectTask.getOriginalId(), instance.getProcessId(), vacBeneficiary.getEmpId()), reviewerEmpRedirectTask.getOriginalId(), seTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), "1", session);
		} else {
		    completeWFTask(seTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(reviewerEmpRedirectTask.getOriginalId(), instance.getProcessId(), vacBeneficiary.getEmpId()), reviewerEmpRedirectTask.getOriginalId(), seTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
		}
	    } else {
		Long[] notificationEmployeesIds = null;
		if (vacBeneficiary.getPhysicalRegionId() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) {
		    // Send notification to the Archives and information department manager.
		    WFPosition position = getWFPosition(WFPositionsEnum.SOLDIERS_ARCHIVES_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
		    EmployeeData archivesInformationDepartmentManager = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());
		    notificationEmployeesIds = new Long[] { archivesInformationDepartmentManager.getEmpId() };
		}
		closeWFInstanceByAction(requester.getEmpId(), instance, seTask, WFTaskActionsEnum.REJECT.getCode(), notificationEmployeesIds, session);
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

    // Medical Affairs Employee
    public static void doVacationMAE(EmployeeData requester, EmployeeData vacBeneficiary, WFInstance instance, WFVacation vacRequest, WFTask maeTask, boolean isApproved) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    long categoryId = vacBeneficiary.getCategoryId();
	    long vacationTypeId = vacRequest.getVacationTypeId();

	    if (isApproved) {
		if (CategoriesEnum.OFFICERS.getCode() == categoryId) {
		    EmployeeData vacManager = getVacationManager(vacBeneficiary, vacationTypeId);

		    completeWFTask(maeTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacManager.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacManager.getEmpId(), maeTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
		} else if (CategoriesEnum.SOLDIERS.getCode() == categoryId) {
		    if (RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == vacBeneficiary.getPhysicalRegionId()) {
			WFPosition position = getWFPosition(WFPositionsEnum.SOLDIERS_FOLLOW_UP_UNIT_VACATIONS_PERSONNEL.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
			EmployeeData vacEmployee = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());

			completeWFTask(maeTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacEmployee.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacEmployee.getEmpId(), maeTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), "1", session);
		    } else {
			EmployeeData vacManager = getVacationManager(vacBeneficiary, vacationTypeId);

			completeWFTask(maeTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(vacManager.getEmpId(), instance.getProcessId(), vacBeneficiary.getEmpId()), vacManager.getEmpId(), maeTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), "1", session);
		    }
		}
	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, maeTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
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
    // approvalFlag: 1 approve - 0 reject - 2 returnReviewer
    public static void doVacationSM(EmployeeData requester, EmployeeData vacBeneficiary, WFInstance instance, WFVacation vacRequest, WFTask smTask, int approvalFlag) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (approvalFlag == 1) {
		long categoryId = vacBeneficiary.getCategoryId();
		long vacationTypeId = vacRequest.getVacationTypeId();

		boolean isWorkFlowClosed = false;
		EmployeeData curSM = EmployeesService.getEmployeeData(smTask.getOriginalId());
		long originalDecisionApprovedId = smTask.getOriginalId();

		if (CategoriesEnum.OFFICERS.getCode() == categoryId) {
		    if (vacationTypeId == VacationTypesEnum.FIELD.getCode()) {
			if (curSM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
			    // Approved by PRESIDENCY if the beneficiary is (LIEUTENANT_GENERAL||BRIGADIER||MAJOR_GENERAL).
			    originalDecisionApprovedId = curSM.getManagerId();
			    isWorkFlowClosed = true;
			} else if (curSM.getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()
				&& vacBeneficiary.getUnitTypeCode().intValue() != UnitTypesEnum.REGION_COMMANDER.getCode()
				&& vacBeneficiary.getRankId() > ETRConfigurationService.getVacationOfficersFieldSmRegionsRcRanksGreaterThan()) {
			    // Approved by the region commander if the beneficiary is NOT (LIEUTENANT_GENERAL||BRIGADIER||MAJOR_GENERAL).
			    isWorkFlowClosed = true;
			}
		    } else if (vacationTypeId == VacationTypesEnum.EXCEPTIONAL.getCode()) {
			// Approved by PRESIDENCY.
			if (curSM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode())
			    isWorkFlowClosed = true;
		    } else {
			// The beneficiary is related to the general region.
			if (RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == vacBeneficiary.getPhysicalRegionId()) {
			    // Approved by PRESIDENCY if the beneficiary is reporting directly to him.
			    // Approved by VISE PRESIDENT if the beneficiary is (LIEUTENANT_GENERAL||BRIGADIER||MAJOR_GENERAL) and not reporting directly to the general manager.
			    if (curSM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
				originalDecisionApprovedId = curSM.getManagerId();
				isWorkFlowClosed = true;
			    } else if (curSM.getUnitTypeCode().intValue() >= UnitTypesEnum.ASSISTANT.getCode()
				    && vacBeneficiary.getRankId() < ETRConfigurationService.getVacationOfficersAllExceptFieldAndExceptionalSmHqVpRanksLessThan()
				    && !vacBeneficiary.getManagerId().equals(curSM.getManagerId())) {
				// Send to the VICE PRESIDENT
				// if the beneficiary is MAJOR_GENERAL or higher and he is not reporting directly to the general manager.
				WFPosition vicePresidentPosition = getWFPosition(WFPositionsEnum.VICE_PRESIDENT.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
				curSM.setManagerId(EmployeesService.getEmployeeByPosition(vicePresidentPosition.getUnitId(), vicePresidentPosition.getEmpId()).getEmpId());
			    } else if (curSM.getUnitTypeCode().intValue() >= UnitTypesEnum.ASSISTANT.getCode()
				    && vacBeneficiary.getRankId() > ETRConfigurationService.getVacationOfficersAllExceptFieldAndExceptionalSmHqAssistantRanksGreaterThan()
				    && !vacBeneficiary.getManagerId().equals(curSM.getManagerId())) {
				// Approved by ASSISTANT if the beneficiary is NOT (LIEUTENANT_GENERAL||BRIGADIER||MAJOR_GENERAL) and not reporting directly to PRESIDENCY.

				if (VacationTypesEnum.REGULAR.getCode() == vacationTypeId || VacationTypesEnum.SICK.getCode() == vacationTypeId) {
				    originalDecisionApprovedId = curSM.getManagerId();
				}
				isWorkFlowClosed = true;
			    }
			} else { // The beneficiary is related to another region.
				 // Approved by PRESIDENCY if the beneficiary is the region commander or one of his assistants.
				 // Approved by VICE PRESIDENT if the beneficiary is (LIEUTENANT_GENERAL||BRIGADIER||MAJOR_GENERAL).
			    if (curSM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
				originalDecisionApprovedId = curSM.getManagerId();
				isWorkFlowClosed = true;
			    } else if (curSM.getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()
				    && vacBeneficiary.getUnitTypeCode().intValue() != UnitTypesEnum.REGION_COMMANDER.getCode()
				    && vacBeneficiary.getRankId() < ETRConfigurationService.getVacationOfficersAllExceptFieldAndExceptionalSmRegionsVpRanksLessThan()) {
				// Send to the VICE PRESIDENT
				WFPosition vicePresidentPosition = getWFPosition(WFPositionsEnum.VICE_PRESIDENT.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
				curSM.setManagerId(EmployeesService.getEmployeeByPosition(vicePresidentPosition.getUnitId(), vicePresidentPosition.getEmpId()).getEmpId());
			    } else if (curSM.getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()
				    && vacBeneficiary.getUnitTypeCode().intValue() != UnitTypesEnum.REGION_COMMANDER.getCode()
				    && vacBeneficiary.getRankId() > ETRConfigurationService.getVacationOfficersAllExceptFieldAndExceptionalSmRegionsRcRanksGreaterThan()) {
				isWorkFlowClosed = true;
			    }
			}
		    }
		} else if (CategoriesEnum.SOLDIERS.getCode() == categoryId) {
		    if (vacationTypeId == VacationTypesEnum.EXCEPTIONAL.getCode()) {
			if (curSM.getUnitTypeCode().intValue() >= UnitTypesEnum.ASSISTANT.getCode()) {
			    isWorkFlowClosed = true;
			}
		    } else {
			// The beneficiary is related to the general region.
			if (RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == vacBeneficiary.getPhysicalRegionId()) {
			    if (curSM.getUnitTypeCode().intValue() >= UnitTypesEnum.ASSISTANT.getCode()) {
				isWorkFlowClosed = true;
			    }
			} else { // The beneficiary is related to another region.
			    if (curSM.getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
				isWorkFlowClosed = true;
			    }
			}
		    }
		} else if (CategoriesEnum.PERSONS.getCode() == categoryId
			|| CategoriesEnum.USERS.getCode() == categoryId
			|| CategoriesEnum.WAGES.getCode() == categoryId
			|| CategoriesEnum.CONTRACTORS.getCode() == categoryId
			|| CategoriesEnum.MEDICAL_STAFF.getCode() == categoryId) {

		    if (vacationTypeId == VacationTypesEnum.STUDY.getCode() || vacationTypeId == VacationTypesEnum.EXCEPTIONAL.getCode()) {
			// Approved by PRESIDENCY if the vacation is EXCEPTIONAL or STUDY.
			if (curSM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode())
			    isWorkFlowClosed = true;
		    } else {
			// The beneficiary is related to the general region.
			if (RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == vacBeneficiary.getPhysicalRegionId()) {
			    // Approved by PRESIDENCY if the beneficiary has rank greater than 13 or reporting directly to him.
			    // Approved by VICE PRESIDENT if the beneficiary has rank 13 and doesn't report to the general manager.
			    if (curSM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
				originalDecisionApprovedId = curSM.getManagerId();
				isWorkFlowClosed = true;
			    } else if (EmployeesService.getEmployeeDirectManager(curSM.getManagerId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()
				    && vacBeneficiary.getRankId() > ETRConfigurationService.getVacationCiviliansAllExceptStudyAndExceptionalSmHqEmpAffGmRanksGreaterThan()
				    && !vacBeneficiary.getManagerId().equals(EmployeesService.getEmployeeDirectManager(curSM.getManagerId()).getEmpId())) {
				// Approved by EMPLOYEES AFFAIRS GENERAL MANAGER
				isWorkFlowClosed = true;
			    } else if (EmployeesService.getEmployeeDirectManager(curSM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()
				    && vacBeneficiary.getRankId() > ETRConfigurationService.getVacationCiviliansAllExceptStudyAndExceptionalSmHqExecFinAffGmRanksGreaterThan()
				    && !vacBeneficiary.getManagerId().equals(curSM.getManagerId())) {
				// Approved by EXECUTIVE and FINANCIAL AFFAIRS GENERAL MANAGER
				isWorkFlowClosed = true;
			    } else if (EmployeesService.getEmployeeDirectManager(curSM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()
				    && vacBeneficiary.getRankId() > ETRConfigurationService.getVacationCiviliansAllExceptStudyAndExceptionalSmHqVpRanksGreaterThan()
				    && !vacBeneficiary.getManagerId().equals(curSM.getManagerId())) {
				// Send to the VICE PRESIDENT
				WFPosition vicePresidentPosition = getWFPosition(WFPositionsEnum.VICE_PRESIDENT.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
				curSM.setManagerId(EmployeesService.getEmployeeByPosition(vicePresidentPosition.getUnitId(), vicePresidentPosition.getEmpId()).getEmpId());
			    }
			} else { // The beneficiary is related to another region.
				 // Approved by PRESIDENCY if the beneficiary has rank greater than 13.
			    if (curSM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
				isWorkFlowClosed = true;
			    } else if (curSM.getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()
				    && vacBeneficiary.getRankId() > ETRConfigurationService.getVacationCiviliansAllExceptStudyAndExceptionalSmRegionsRcRanksGreaterThan()) {
				// Approved by the region commander if the beneficiary has rank less or equal than 13.
				isWorkFlowClosed = true;
			    }
			}
		    }
		}

		if (isWorkFlowClosed) {
		    closeVacationWorkFlow(requester, vacBeneficiary, instance, vacRequest, smTask, originalDecisionApprovedId, curSM.getPhysicalRegionId(), session);
		} else {
		    // Send to next manager
		    completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curSM.getManagerId(), instance.getProcessId(), vacBeneficiary.getEmpId()), curSM.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
		}

	    } else if (approvalFlag == 2) {
		WFTask reviewerTask = getWFInstanceTasksByRole(instance.getInstanceId(), WFTaskRolesEnum.REVIEWER_EMP.getCode()).get(0);
		completeWFTask(smTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(reviewerTask.getOriginalId(), instance.getProcessId(), vacBeneficiary.getEmpId()), reviewerTask.getOriginalId(), smTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), "1", session);
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

    /*------------------------------------------------ Integration --------------------------------------------------*/

    private static void closeVacationWorkFlow(EmployeeData requester, EmployeeData vacBeneficiary, WFInstance instance, WFVacation vacRequest, WFTask finalTask, long originalDecisionApprovedId, long decisionRegionId, CustomSession session) throws BusinessException, DatabaseException {
	vacRequest.setDecisionApprovedId(finalTask.getOriginalId());
	vacRequest.setOriginalDecisionApprovedId(originalDecisionApprovedId);
	vacRequest.setDecisionRegionId(decisionRegionId);
	DataAccess.updateEntity(vacRequest, session);

	doVacationIntegration(vacRequest, vacBeneficiary, instance.getProcessId(), instance.getAttachments(), session);

	try {

	    List<Long> categoriesIds = new ArrayList<Long>();
	    categoriesIds.add(vacBeneficiary.getCategoryId());
	    List<Long> beneficairyEmployeesIds = new ArrayList<Long>();
	    beneficairyEmployeesIds.add(vacBeneficiary.getEmpId());

	    closeWFInstanceByAction(requester.getEmpId(), instance, finalTask, (finalTask.getAssigneeWfRole().equals(WFTaskRolesEnum.DIRECT_MANAGER.getCode()) ? WFTaskActionsEnum.APPROVE.getCode() : WFTaskActionsEnum.SUPER_SIGN.getCode()), computeInstanceNotifications(categoriesIds, decisionRegionId, instance.getProcessId(), beneficairyEmployeesIds, beneficairyEmployeesIds), session);

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static void doVacationIntegration(WFVacation vacRequest, EmployeeData vacBeneficiary, long processId, String attachments, CustomSession useSession) throws BusinessException {
	Vacation vacation = constructVacationTransaction(vacRequest, vacBeneficiary, attachments);
	VacationsService.handleVacRequest(vacation, vacBeneficiary, getWFProcess(processId).getProcessName(), useSession);
    }

    /*------------------------------------------------ Validations --------------------------------------------------*/

    private static void validateVacationRequestData(EmployeeData vacBeneficiary, WFVacation vacRequest) throws BusinessException {

	VacationsService.validateVacationRules(constructVacationTransaction(vacRequest, vacBeneficiary, null), vacBeneficiary);
    }

    public static void validateRunningVacationRequests(long beneficiaryId, long vacationTypeId) throws BusinessException {
	Long[] selectedGroup = null;

	if (vacationTypeId == VacationTypesEnum.REGULAR.getCode()
		|| vacationTypeId == VacationTypesEnum.EXCEPTIONAL.getCode()
		|| vacationTypeId == VacationTypesEnum.ACCOMPANY.getCode()) {
	    // Regular Vacation
	    List<Long> regularExceptionalAccompanyGroup = new ArrayList<Long>();
	    regularExceptionalAccompanyGroup.add(WFProcessesEnum.OFFICERS_REGULAR_VACATION.getCode());
	    regularExceptionalAccompanyGroup.add(WFProcessesEnum.MODIFY_OFFICERS_REGULAR_VACATION.getCode());
	    regularExceptionalAccompanyGroup.add(WFProcessesEnum.CANCEL_OFFICERS_REGULAR_VACATION.getCode());

	    regularExceptionalAccompanyGroup.add(WFProcessesEnum.SOLDIERS_REGULAR_VACATION.getCode());
	    regularExceptionalAccompanyGroup.add(WFProcessesEnum.MODIFY_SOLDIERS_REGULAR_VACATION.getCode());
	    regularExceptionalAccompanyGroup.add(WFProcessesEnum.CANCEL_SOLDIERS_REGULAR_VACATION.getCode());

	    regularExceptionalAccompanyGroup.add(WFProcessesEnum.EMPLOYEES_REGULAR_VACATION.getCode());
	    regularExceptionalAccompanyGroup.add(WFProcessesEnum.MODIFY_EMPLOYEES_REGULAR_VACATION.getCode());
	    regularExceptionalAccompanyGroup.add(WFProcessesEnum.CANCEL_EMPLOYEES_REGULAR_VACATION.getCode());

	    // Exceptional Vacation
	    regularExceptionalAccompanyGroup.add(WFProcessesEnum.OFFICERS_EXCEPTIONAL_VACATION.getCode());
	    regularExceptionalAccompanyGroup.add(WFProcessesEnum.CANCEL_OFFICERS_EXCEPTIONAL_VACATION.getCode());

	    regularExceptionalAccompanyGroup.add(WFProcessesEnum.SOLDIERS_EXCEPTIONAL_VACATION.getCode());
	    regularExceptionalAccompanyGroup.add(WFProcessesEnum.CANCEL_SOLDIERS_EXCEPTIONAL_VACATION.getCode());

	    regularExceptionalAccompanyGroup.add(WFProcessesEnum.EMPLOYEES_EXCEPTIONAL_FOR_CIRCUMSTANCES_VACATION.getCode());
	    regularExceptionalAccompanyGroup.add(WFProcessesEnum.CANCEL_EMPLOYEES_EXCEPTIONAL_FOR_CIRCUMSTANCES_VACATION.getCode());

	    regularExceptionalAccompanyGroup.add(WFProcessesEnum.EMPLOYEES_EXCEPTIONAL_FOR_ACCOMPANY_VACATION.getCode());
	    regularExceptionalAccompanyGroup.add(WFProcessesEnum.CANCEL_EMPLOYEES_EXCEPTIONAL_FOR_ACCOMPANY_VACATION.getCode());

	    // Accompany Vacation
	    regularExceptionalAccompanyGroup.add(WFProcessesEnum.EMPLOYEES_ACCOMPANY_VACATION.getCode());
	    regularExceptionalAccompanyGroup.add(WFProcessesEnum.CANCEL_EMPLOYEES_ACCOMPANY_VACATION.getCode());

	    selectedGroup = regularExceptionalAccompanyGroup.toArray(new Long[regularExceptionalAccompanyGroup.size()]);

	} else if (vacationTypeId == VacationTypesEnum.COMPELLING.getCode()) {
	    // Compelling Vacation
	    List<Long> compellingGroup = new ArrayList<Long>();
	    compellingGroup.add(WFProcessesEnum.OFFICERS_COMPELLING_VACATION.getCode());
	    compellingGroup.add(WFProcessesEnum.CANCEL_OFFICERS_COMPELLING_VACATION.getCode());

	    compellingGroup.add(WFProcessesEnum.SOLDIERS_COMPELLING_VACATION.getCode());
	    compellingGroup.add(WFProcessesEnum.CANCEL_SOLDIERS_COMPELLING_VACATION.getCode());

	    compellingGroup.add(WFProcessesEnum.EMPLOYEES_COMPELLING_VACATION.getCode());
	    compellingGroup.add(WFProcessesEnum.CANCEL_EMPLOYEES_COMPELLING_VACATION.getCode());

	    compellingGroup.add(WFProcessesEnum.CONTRACTORS_WITHOUT_SALARY_VACATION.getCode());
	    compellingGroup.add(WFProcessesEnum.CANCEL_CONTRACTORS_WITHOUT_SALARY_VACATION.getCode());

	    selectedGroup = compellingGroup.toArray(new Long[compellingGroup.size()]);

	} else if (vacationTypeId == VacationTypesEnum.SICK.getCode()) {
	    // Sick Vacation
	    List<Long> sickGroup = new ArrayList<Long>();
	    sickGroup.add(WFProcessesEnum.OFFICERS_SICK_VACATION.getCode());
	    sickGroup.add(WFProcessesEnum.MODIFY_OFFICERS_SICK_VACATION.getCode());
	    sickGroup.add(WFProcessesEnum.CANCEL_OFFICERS_SICK_VACATION.getCode());

	    sickGroup.add(WFProcessesEnum.SOLDIERS_SICK_VACATION.getCode());
	    sickGroup.add(WFProcessesEnum.MODIFY_SOLDIERS_SICK_VACATION.getCode());
	    sickGroup.add(WFProcessesEnum.CANCEL_SOLDIERS_SICK_VACATION.getCode());

	    sickGroup.add(WFProcessesEnum.EMPLOYEES_SICK_VACATION.getCode());
	    sickGroup.add(WFProcessesEnum.MODIFY_EMPLOYEES_SICK_VACATION.getCode());
	    sickGroup.add(WFProcessesEnum.CANCEL_EMPLOYEES_SICK_VACATION.getCode());

	    // Work Injury Sick Vacation
	    sickGroup.add(WFProcessesEnum.SOLDIERS_WORK_INJURY_SICK_VACATION.getCode());
	    sickGroup.add(WFProcessesEnum.MODIFY_SOLDIERS_WORK_INJURY_SICK_VACATION.getCode());
	    sickGroup.add(WFProcessesEnum.CANCEL_SOLDIERS_WORK_INJURY_SICK_VACATION.getCode());

	    selectedGroup = sickGroup.toArray(new Long[sickGroup.size()]);

	} else if (vacationTypeId == VacationTypesEnum.FIELD.getCode()) {
	    // Field Vacation
	    List<Long> fieldGroup = new ArrayList<Long>();
	    fieldGroup.add(WFProcessesEnum.OFFICERS_FIELD_VACATION.getCode());
	    fieldGroup.add(WFProcessesEnum.CANCEL_OFFICERS_FIELD_VACATION.getCode());

	    fieldGroup.add(WFProcessesEnum.SOLDIERS_FIELD_VACATION.getCode());
	    fieldGroup.add(WFProcessesEnum.CANCEL_SOLDIERS_FIELD_VACATION.getCode());

	    selectedGroup = fieldGroup.toArray(new Long[fieldGroup.size()]);

	} else if (vacationTypeId == VacationTypesEnum.STUDY.getCode()) {
	    // Study Vacation
	    List<Long> studyGroup = new ArrayList<Long>();
	    studyGroup.add(WFProcessesEnum.EMPLOYEES_STUDY_VACATION.getCode());
	    studyGroup.add(WFProcessesEnum.CANCEL_EMPLOYEES_STUDY_VACATION.getCode());

	    selectedGroup = studyGroup.toArray(new Long[studyGroup.size()]);

	} else if (vacationTypeId == VacationTypesEnum.EXAM.getCode()) {
	    // Exam Vacation
	    List<Long> examGroup = new ArrayList<Long>();
	    examGroup.add(WFProcessesEnum.SOLDIERS_EXAM_VACATION.getCode());
	    examGroup.add(WFProcessesEnum.CANCEL_SOLDIERS_EXAM_VACATION.getCode());

	    examGroup.add(WFProcessesEnum.EMPLOYEES_EXAM_VACATION.getCode());
	    examGroup.add(WFProcessesEnum.CANCEL_EMPLOYEES_EXAM_VACATION.getCode());

	    selectedGroup = examGroup.toArray(new Long[examGroup.size()]);

	} else if (vacationTypeId == VacationTypesEnum.RELIEF.getCode()) {
	    // Relief Vacation
	    List<Long> reliefGroup = new ArrayList<Long>();
	    reliefGroup.add(WFProcessesEnum.EMPLOYEES_RELIEF_VACATION.getCode());
	    reliefGroup.add(WFProcessesEnum.CANCEL_EMPLOYEES_RELIEF_VACATION.getCode());

	    selectedGroup = reliefGroup.toArray(new Long[reliefGroup.size()]);

	} else if (vacationTypeId == VacationTypesEnum.SPORTIVE.getCode() || vacationTypeId == VacationTypesEnum.CULTURAL.getCode() || vacationTypeId == VacationTypesEnum.SOCIAL.getCode()) {
	    // Sportive Vacation & Cultural Vacation & Social Vacation (Added to the same group because they have the same balance)
	    List<Long> sportiveCulturalSocialGroup = new ArrayList<Long>();
	    sportiveCulturalSocialGroup.add(WFProcessesEnum.EMPLOYEES_SPORTIVE_VACATION.getCode());
	    sportiveCulturalSocialGroup.add(WFProcessesEnum.CANCEL_EMPLOYEES_SPORTIVE_VACATION.getCode());

	    sportiveCulturalSocialGroup.add(WFProcessesEnum.EMPLOYEES_CULTURAL_VACATION.getCode());
	    sportiveCulturalSocialGroup.add(WFProcessesEnum.CANCEL_EMPLOYEES_CULTURAL_VACATION.getCode());

	    sportiveCulturalSocialGroup.add(WFProcessesEnum.EMPLOYEES_SOCIAL_VACATION.getCode());
	    sportiveCulturalSocialGroup.add(WFProcessesEnum.CANCEL_EMPLOYEES_SOCIAL_VACATION.getCode());

	    selectedGroup = sportiveCulturalSocialGroup.toArray(new Long[sportiveCulturalSocialGroup.size()]);

	} else if (vacationTypeId == VacationTypesEnum.COMPENSATION.getCode()) {
	    // Compensation Vacation
	    List<Long> compensationGroup = new ArrayList<Long>();
	    compensationGroup.add(WFProcessesEnum.EMPLOYEES_COMPENSATION_VACATION.getCode());
	    compensationGroup.add(WFProcessesEnum.CANCEL_EMPLOYEES_COMPENSATION_VACATION.getCode());

	    selectedGroup = compensationGroup.toArray(new Long[compensationGroup.size()]);

	} else if (vacationTypeId == VacationTypesEnum.MATERNITY.getCode()) {
	    // Maternity Vacation
	    List<Long> maternityGroup = new ArrayList<Long>();
	    maternityGroup.add(WFProcessesEnum.SOLDIERS_MATERNITY_VACATION.getCode());
	    maternityGroup.add(WFProcessesEnum.CANCEL_SOLDIERS_MATERNITY_VACATION.getCode());

	    maternityGroup.add(WFProcessesEnum.EMPLOYEES_MATERNITY_VACATION.getCode());
	    maternityGroup.add(WFProcessesEnum.CANCEL_EMPLOYEES_MATERNITY_VACATION.getCode());

	    selectedGroup = maternityGroup.toArray(new Long[maternityGroup.size()]);

	} else if (vacationTypeId == VacationTypesEnum.MOTHERHOOD.getCode()) {
	    // Motherhood Vacation
	    List<Long> motherhoodGroup = new ArrayList<Long>();
	    motherhoodGroup.add(WFProcessesEnum.SOLDIERS_MOTHERHOOD_VACATION.getCode());
	    motherhoodGroup.add(WFProcessesEnum.CANCEL_SOLDIERS_MOTHERHOOD_VACATION.getCode());

	    motherhoodGroup.add(WFProcessesEnum.EMPLOYEES_MOTHERHOOD_VACATION.getCode());
	    motherhoodGroup.add(WFProcessesEnum.CANCEL_EMPLOYEES_MOTHERHOOD_VACATION.getCode());

	    selectedGroup = motherhoodGroup.toArray(new Long[motherhoodGroup.size()]);

	} else if (vacationTypeId == VacationTypesEnum.ORPHAN_CARE.getCode()) {
	    // Orphan Care Vacation
	    List<Long> orphanCareGroup = new ArrayList<Long>();
	    orphanCareGroup.add(WFProcessesEnum.EMPLOYEES_ORPHAN_CARE_VACATION.getCode());
	    orphanCareGroup.add(WFProcessesEnum.CANCEL_EMPLOYEES_ORPHAN_CARE_VACATION.getCode());

	    selectedGroup = orphanCareGroup.toArray(new Long[orphanCareGroup.size()]);

	} else if (vacationTypeId == VacationTypesEnum.DEATH_WAITING_PERIOD.getCode()) {
	    // Death Waiting Period Vacation
	    List<Long> deathWaitingPeriodGroup = new ArrayList<Long>();
	    deathWaitingPeriodGroup.add(WFProcessesEnum.SOLDIERS_DEATH_WAITING_PERIOD_VACATION.getCode());
	    deathWaitingPeriodGroup.add(WFProcessesEnum.CANCEL_SOLDIERS_DEATH_WAITING_PERIOD_VACATION.getCode());

	    deathWaitingPeriodGroup.add(WFProcessesEnum.EMPLOYEES_DEATH_WAITING_PERIOD_VACATION.getCode());
	    deathWaitingPeriodGroup.add(WFProcessesEnum.CANCEL_EMPLOYEES_DEATH_WAITING_PERIOD_VACATION.getCode());

	    selectedGroup = deathWaitingPeriodGroup.toArray(new Long[deathWaitingPeriodGroup.size()]);

	} else if (vacationTypeId == VacationTypesEnum.DEATH_OF_RELATIVE.getCode()) {
	    // Death of Relative Vacation
	    List<Long> deathOfRelativeGroup = new ArrayList<Long>();
	    deathOfRelativeGroup.add(WFProcessesEnum.EMPLOYEES_DEATH_OF_RELATIVE_VACATION.getCode());
	    deathOfRelativeGroup.add(WFProcessesEnum.CANCEL_EMPLOYEES_DEATH_OF_RELATIVE_VACATION.getCode());

	    selectedGroup = deathOfRelativeGroup.toArray(new Long[deathOfRelativeGroup.size()]);

	} else if (vacationTypeId == VacationTypesEnum.NEW_BABY.getCode()) {
	    // New Baby Vacation
	    List<Long> newBabyGroup = new ArrayList<Long>();
	    newBabyGroup.add(WFProcessesEnum.EMPLOYEES_NEW_BABY_VACATION.getCode());
	    newBabyGroup.add(WFProcessesEnum.CANCEL_EMPLOYEES_NEW_BABY_VACATION.getCode());

	    selectedGroup = newBabyGroup.toArray(new Long[newBabyGroup.size()]);
	}

	validateVacationRunningWFInstances(beneficiaryId, selectedGroup);
    }

    public static long validateVacationRunningWFDatesConflict(long beneficiaryId, String startDateString, String endDateString) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_BENEFICIARY_ID", beneficiaryId);
	    qParams.put("P_START_DATE", startDateString);
	    qParams.put("P_END_DATE", endDateString);

	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.WF_VALIDATE_VACATION_CHECK_DATES_CONFLICT.getCode(), qParams).get(0);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static void validateReviewerEmployeeReferring(WFVacation vacRequest, EmployeeData vacBeneficiary) throws BusinessException {
	if (VacationTypesEnum.REGULAR.getCode() != vacRequest.getVacationTypeId()
		&& VacationTypesEnum.COMPELLING.getCode() != vacRequest.getVacationTypeId()
		&& VacationTypesEnum.SICK.getCode() != vacRequest.getVacationTypeId()
		&& (CategoriesEnum.PERSONS.getCode() == vacBeneficiary.getCategoryId()
			|| CategoriesEnum.USERS.getCode() == vacBeneficiary.getCategoryId()
			|| CategoriesEnum.WAGES.getCode() == vacBeneficiary.getCategoryId()
			|| CategoriesEnum.MEDICAL_STAFF.getCode() == vacBeneficiary.getCategoryId())
		&& (vacRequest.getReferring() == null || vacRequest.getReferring().isEmpty()))
	    throw new BusinessException("error_referringMandatory");
    }

    /*------------------------------------------------ Utilities --------------------------------------------------*/

    private static Vacation constructVacationTransaction(WFVacation vacRequest, EmployeeData vacBeneficiary, String attachments) throws BusinessException {

	Vacation vacation = new Vacation();

	if (vacRequest.getRequestType() == RequestTypesEnum.MODIFY.getCode() || vacRequest.getRequestType() == RequestTypesEnum.CANCEL.getCode())
	    vacation.setVacationId(vacRequest.getOldVacationId());

	vacation.setStatus(vacRequest.getRequestType());

	vacation.setEmpId(vacRequest.getBeneficiaryId());
	vacation.setVacationTypeId(vacRequest.getVacationTypeId());
	vacation.setSubVacationType(vacRequest.getSubVacationType());
	vacation.setStartDateString(vacRequest.getStartDateString());
	vacation.setEndDateString(vacRequest.getEndDateString());
	vacation.setPeriod(vacRequest.getPeriod());
	vacation.setLocationFlag(vacRequest.getLocationFlag());
	vacation.setLocation(vacRequest.getLocation());
	vacation.setContactNumber(vacRequest.getContactNumber());
	vacation.setContactAddress(vacRequest.getContactAddress());

	vacation.setApprovedId(vacRequest.getApprovedId());

	vacation.setDecisionApprovedId(vacRequest.getDecisionApprovedId());
	vacation.setDecisionRegionId(vacRequest.getDecisionRegionId());
	vacation.setOriginalDecisionApprovedId(vacRequest.getOriginalDecisionApprovedId());

	vacation.setReferring(vacRequest.getReferring());
	vacation.setAttachments(attachments);

	if ((vacBeneficiary.getCategoryId() == CategoriesEnum.OFFICERS.getCode() || vacBeneficiary.getCategoryId() == CategoriesEnum.SOLDIERS.getCode())
		&& vacRequest.getVacationTypeId() == VacationTypesEnum.SICK.getCode()
		&& vacRequest.getRequestType() == RequestTypesEnum.MODIFY.getCode()
		&& vacRequest.getLocationFlag() == LocationFlagsEnum.INTERNAL_EXTERNAL.getCode()) {

	    vacation.setExtLocation(vacRequest.getExtLocation());
	    vacation.setExtPeriod(vacRequest.getExtPeriod());
	    vacation.setExtStartDateString(vacRequest.getExtStartDateString());
	    vacation.setExtEndDateString(vacRequest.getExtEndDateString());
	}

	if (vacBeneficiary.getCategoryId() == CategoriesEnum.CONTRACTORS.getCode()
		&& vacRequest.getVacationTypeId() == VacationTypesEnum.REGULAR.getCode()
		&& (vacRequest.getRequestType() == RequestTypesEnum.NEW.getCode() || vacRequest.getRequestType() == RequestTypesEnum.MODIFY.getCode())) {
	    vacation.setContractualYearStartDateString(vacRequest.getContractualYearStartDateString());
	    vacation.setContractualYearEndDateString(vacRequest.getContractualYearEndDateString());
	}

	return vacation;
    }

    private static EmployeeData getVacationManager(EmployeeData vacBeneficiary, long vacationTypeId) throws BusinessException {
	WFPosition position = new WFPosition();
	long categoryId = vacBeneficiary.getCategoryId();
	long regionId = vacBeneficiary.getPhysicalRegionId();
	long unitTypeId = vacBeneficiary.getUnitTypeCode().intValue();

	if (regionId == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) {
	    if (CategoriesEnum.OFFICERS.getCode() == categoryId)
		position = getWFPosition(WFPositionsEnum.OFFICERS_ARCHIVES_UNIT_MANAGER.getCode(), regionId);
	    else if (CategoriesEnum.PERSONS.getCode() == categoryId || (CategoriesEnum.MEDICAL_STAFF.getCode() == categoryId))
		position = getWFPosition(WFPositionsEnum.PERSONS_UNIT_MANAGER.getCode(), regionId);
	    else if ((CategoriesEnum.USERS.getCode() == categoryId) || (CategoriesEnum.WAGES.getCode() == categoryId))
		position = getWFPosition(WFPositionsEnum.USERS_WAGES_UNIT_MANAGER.getCode(), regionId);
	    else if (CategoriesEnum.CONTRACTORS.getCode() == categoryId)
		position = getWFPosition(WFPositionsEnum.CONTRACTORS_UNIT_MANAGER.getCode(), regionId);
	} else {
	    if (CategoriesEnum.OFFICERS.getCode() == categoryId
		    && (unitTypeId == UnitTypesEnum.REGION_COMMANDER.getCode() || vacationTypeId == VacationTypesEnum.EXCEPTIONAL.getCode()))
		position = getWFPosition(WFPositionsEnum.OFFICERS_ARCHIVES_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
	    else if (CategoriesEnum.OFFICERS.getCode() == categoryId && unitTypeId != UnitTypesEnum.REGION_COMMANDER.getCode())
		position = getWFPosition(WFPositionsEnum.REGION_OFFICERS_AFFAIRS_UNIT_MANAGER.getCode(), regionId);
	    else if (CategoriesEnum.SOLDIERS.getCode() == categoryId)
		position = getWFPosition(WFPositionsEnum.REGION_SOLDIERS_AFFAIRS_UNIT_MANAGER.getCode(), regionId);
	    else if ((CategoriesEnum.PERSONS.getCode() == categoryId || CategoriesEnum.MEDICAL_STAFF.getCode() == categoryId)
		    && (vacationTypeId == VacationTypesEnum.EXCEPTIONAL.getCode() || vacationTypeId == VacationTypesEnum.STUDY.getCode()))
		position = getWFPosition(WFPositionsEnum.PERSONS_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
	    else if ((CategoriesEnum.USERS.getCode() == categoryId || CategoriesEnum.WAGES.getCode() == categoryId)
		    && (vacationTypeId == VacationTypesEnum.EXCEPTIONAL.getCode() || vacationTypeId == VacationTypesEnum.STUDY.getCode()))
		position = getWFPosition(WFPositionsEnum.USERS_WAGES_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
	    else if (CategoriesEnum.PERSONS.getCode() == categoryId
		    || CategoriesEnum.USERS.getCode() == categoryId
		    || CategoriesEnum.WAGES.getCode() == categoryId
		    || CategoriesEnum.CONTRACTORS.getCode() == categoryId
		    || CategoriesEnum.MEDICAL_STAFF.getCode() == categoryId)
		position = getWFPosition(WFPositionsEnum.REGION_CIVILIANS_AFFAIRS_UNIT_MANAGER.getCode(), regionId);
	}
	return EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());
    }

    private static EmployeeData getMedicalEmployee(EmployeeData vacBeneficiary) throws BusinessException {
	WFPosition position = new WFPosition();
	long categoryId = vacBeneficiary.getCategoryId();

	if (CategoriesEnum.OFFICERS.getCode() == categoryId)
	    position = getWFPosition(WFPositionsEnum.OFFICERS_MEDICAL_UNIT_VACATIONS_PERSONNEL.getCode(), vacBeneficiary.getPhysicalRegionId());
	else if (CategoriesEnum.SOLDIERS.getCode() == categoryId)
	    position = getWFPosition(WFPositionsEnum.SOLDIERS_MEDICAL_UNIT_VACATIONS_PERSONNEL.getCode(), vacBeneficiary.getPhysicalRegionId());

	return EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());
    }

    private static EmployeeData getSecurityEmployee(EmployeeData vacBeneficiary, long vacationTypeId) throws BusinessException {
	WFPosition position = getWFPosition(WFPositionsEnum.SOLDIERS_SECURITY_UNIT_VACATIONS_PERSONNEL.getCode(), vacationTypeId == VacationTypesEnum.EXCEPTIONAL.getCode() ? RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() : vacBeneficiary.getPhysicalRegionId());
	return EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());
    }

    /*------------------------------------------------ Queries --------------------------------------------------*/

    public static WFVacation getWFVacationByInstanceId(long instanceId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCE_ID", instanceId);
	    List<WFVacation> result = DataAccess.executeNamedQuery(WFVacation.class, QueryNamesEnum.WF_GET_WFVACATION_BY_INSTANCE_ID.getCode(), qParams);
	    if (result.isEmpty())
		return null;
	    else
		return result.get(0);
	} catch (Exception e) {
	    throw new BusinessException("error_general");
	}
    }

    public static List<Object> getWFVacationsTasks(Long assigneeId, String assigneeWfRole) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ASSIGNEE_ID", assigneeId);
	    qParams.put("P_ASSIGNEE_WF_ROLE", assigneeWfRole);
	    return DataAccess.executeNamedQuery(Object.class, QueryNamesEnum.WF_GET_WFVACATIONS_TASKS.getCode(), qParams);
	} catch (Exception e) {
	    throw new BusinessException("error_general");
	}
    }

    public static List<WFVacationData> getUnitRunningWFVacationsData(String hkey, boolean includeChildren, Long[] categoriesIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_PREFIX_HKEY", includeChildren ? UnitsService.getHKeyPrefix(hkey) + "%" : hkey);
	    qParams.put("P_EXCLUDED_PROCESSES_IDS", new Long[] { WFProcessesEnum.OFFICERS_VACATION_JOINING.getCode(),
		    WFProcessesEnum.SOLDIERS_VACATION_JOINING.getCode(), WFProcessesEnum.CIVILIANS_VACATION_JOINING.getCode() });
	    if (categoriesIds != null && categoriesIds.length > 0) {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_CATEGORIES_IDS", categoriesIds);
	    } else {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_CATEGORIES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }

	    return DataAccess.executeNamedQuery(WFVacationData.class, QueryNamesEnum.WF_GET_UNIT_RUNNING_WF_VACACTIONS_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    throw new BusinessException("error_general");
	}
    }

    public static Long countUnitRunningWFVacationsData(String hkey, Long[] categoriesIds, String selectedDate) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_PREFIX_HKEY", UnitsService.getHKeyPrefix(hkey) + "%");
	    qParams.put("P_SELECTED_DATE", selectedDate);
	    qParams.put("P_EXCLUDED_PROCESSES_IDS", new Long[] { WFProcessesEnum.OFFICERS_VACATION_JOINING.getCode(),
		    WFProcessesEnum.SOLDIERS_VACATION_JOINING.getCode(), WFProcessesEnum.CIVILIANS_VACATION_JOINING.getCode() });
	    if (categoriesIds != null && categoriesIds.length > 0) {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_CATEGORIES_IDS", categoriesIds);
	    } else {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_CATEGORIES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }

	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.WF_COUNT_UNIT_RUNNING_WF_VACACTIONS_DATA.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    throw new BusinessException("error_general");
	}
    }

    /***************************************** Vacations Joining WorkFlow ********************************************/
    /*--------------------------------------------------- Steps -----------------------------------------------------*/

    public static void initVacationJoining(EmployeeData requester, EmployeeData vacBeneficiary, WFVacation vacRequest, long processId, String attachments, String taskUrl) throws BusinessException {

	Long originalId = (requester.getEmpId().equals(vacBeneficiary.getEmpId())) ? requester.getManagerId() : requester.getEmpId();
	vacRequest.setBeneficiaryId(vacBeneficiary.getEmpId());

	validateVacationJoiningRequest(false, vacBeneficiary, vacRequest);

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    WFInstance instance = addWFInstance(processId, requester.getEmpId(), curDate, curHijriDate, WFInstanceStatusEnum.RUNNING.getCode(), attachments, Arrays.asList(vacRequest.getBeneficiaryId()), session);

	    addWFTask(instance.getInstanceId(), getDelegate(originalId, processId, vacBeneficiary.getEmpId()), originalId, curDate, curHijriDate, taskUrl, WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);

	    vacRequest.setInstanceId(instance.getInstanceId());
	    DataAccess.addEntity(vacRequest, session);

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

    public static void doVacationJoiningDM(EmployeeData requester, EmployeeData vacBeneficiary, WFInstance instance, WFVacation vacRequest, WFTask dmTask, boolean isApproved) throws BusinessException {
	if (isApproved)
	    validateVacationJoiningRequest(true, vacBeneficiary, vacRequest);

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (isApproved) {
		long categoryId = vacBeneficiary.getCategoryId();

		EmployeeData curDM = EmployeesService.getEmployeeData(dmTask.getOriginalId());

		boolean isWorkFlowClosed = false;

		if (CategoriesEnum.OFFICERS.getCode() == categoryId || CategoriesEnum.SOLDIERS.getCode() == categoryId) {
		    // The beneficiary is related to the general region.
		    if (RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == vacBeneficiary.getPhysicalRegionId()) {
			if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
			    isWorkFlowClosed = true;
			} else {
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), vacBeneficiary.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			}
		    } else { // The beneficiary is related to another region.
			if (vacBeneficiary.getUnitTypeCode().intValue() == UnitTypesEnum.REGION_COMMANDER.getCode()
				&& EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
			    // The beneficiary is the region commander or one of his assistants.
			    isWorkFlowClosed = true;
			} else if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
			    // The beneficiary is not the region commander neither one of his assistants.
			    isWorkFlowClosed = true;
			} else {
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), vacBeneficiary.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			}
		    }
		} else if ((CategoriesEnum.PERSONS.getCode() == categoryId)
			|| (CategoriesEnum.USERS.getCode() == categoryId)
			|| (CategoriesEnum.WAGES.getCode() == categoryId)
			|| (CategoriesEnum.CONTRACTORS.getCode() == categoryId)
			|| (CategoriesEnum.MEDICAL_STAFF.getCode() == categoryId)) {

		    // The beneficiary is related to the general region.
		    if (RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == vacBeneficiary.getPhysicalRegionId()) {
			if (EmployeesService.getEmployeeDirectManager(curDM.getManagerId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
			    isWorkFlowClosed = true;
			} else {
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), vacBeneficiary.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			}
		    } else { // The beneficiary is related to another region.
			if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
			    isWorkFlowClosed = true;
			} else {
			    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), vacBeneficiary.getEmpId()), curDM.getManagerId(), dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
			}
		    }
		}

		if (isWorkFlowClosed) {
		    vacRequest.setApprovedId(dmTask.getOriginalId());
		    closeVacationJoiningWorkFlow(requester, vacBeneficiary, instance, vacRequest, dmTask, curDM.getPhysicalRegionId(), session);
		}

	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, dmTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
	    }

	    DataAccess.updateEntity(vacRequest, session);
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

    /*------------------------------------------------ Integration --------------------------------------------------*/

    private static void closeVacationJoiningWorkFlow(EmployeeData requester, EmployeeData vacBeneficiary, WFInstance instance, WFVacation vacRequest, WFTask finalTask, long decisionRegionId, CustomSession session) throws BusinessException, DatabaseException {

	doVacationJoiningIntegration(vacRequest, requester, vacBeneficiary, instance.getProcessId(), instance.getAttachments(), session);

	try {

	    List<Long> categoriesIds = new ArrayList<Long>();
	    categoriesIds.add(vacBeneficiary.getCategoryId());
	    List<Long> beneficairyEmployeesIds = new ArrayList<Long>();
	    beneficairyEmployeesIds.add(vacBeneficiary.getEmpId());

	    closeWFInstanceByAction(requester.getEmpId(), instance, finalTask, WFTaskActionsEnum.APPROVE.getCode(), computeInstanceNotifications(categoriesIds, decisionRegionId, instance.getProcessId(), beneficairyEmployeesIds, null), session);

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static void doVacationJoiningIntegration(WFVacation vacRequest, EmployeeData requester, EmployeeData vacBeneficiary, long processId, String attachments, CustomSession session) throws BusinessException {
	VacationsService.updateVacationJoiningDate(vacRequest.getOldVacationId(), HijriDateService.getHijriDate(vacRequest.getJoiningDateString()), vacRequest.getApprovedId(), session);
    }

    /*------------------------------------------------ Validations --------------------------------------------------*/

    private static void validateVacationJoiningRequest(boolean validateDataOnlyFlag, EmployeeData vacBeneficiary, WFVacation vacRequest) throws BusinessException {
	VacationsService.validateVacationJoiningData(validateDataOnlyFlag, vacBeneficiary.getEmpId(), vacRequest.getVacationTypeId(), vacRequest.getSubVacationType(), vacRequest.getEndDateString(), vacRequest.getExceededDays());

	if (!validateDataOnlyFlag)
	    validateRunningVacationJoiningRequests(vacBeneficiary.getEmpId(), vacBeneficiary.getCategoryId());
    }

    private static void validateRunningVacationJoiningRequests(long beneficiaryId, long categoryId) throws BusinessException {
	validateVacationRunningWFInstances(beneficiaryId, (categoryId == CategoriesEnum.OFFICERS.getCode()) ? new Long[] { WFProcessesEnum.OFFICERS_VACATION_JOINING.getCode() } : ((categoryId == CategoriesEnum.SOLDIERS.getCode() ? new Long[] { WFProcessesEnum.SOLDIERS_VACATION_JOINING.getCode() } : new Long[] { WFProcessesEnum.CIVILIANS_VACATION_JOINING.getCode() })));
    }

    /*************************************************** Commons *****************************************************/
    /*--------------------------------------------------- Collective -----------------------------------------------------*/
    public static void doVacationsCollectiveAction(List<Object> vacationsTasks, int actionTypeFlag) throws BusinessException {

	StringBuffer unsuccessfulTaskIdsIfAny = new StringBuffer();
	String comma = ", ";
	int unsuccessfulTasksCount = 0;

	for (Object obj : vacationsTasks) {
	    WFTask task = null;
	    try {
		WFVacation vacRequest = (WFVacation) (((Object[]) obj)[0]);
		task = (WFTask) (((Object[]) obj)[1]);
		WFInstance instance = (WFInstance) (((Object[]) obj)[2]);
		EmployeeData requester = (EmployeeData) (((Object[]) obj)[4]);
		EmployeeData beneficiary = (EmployeeData) (((Object[]) obj)[5]);

		if (actionTypeFlag == 1) {
		    if (instance.getProcessId().equals(WFProcessesEnum.OFFICERS_VACATION_JOINING.getCode())
			    || instance.getProcessId().equals(WFProcessesEnum.SOLDIERS_VACATION_JOINING.getCode())
			    || instance.getProcessId().equals(WFProcessesEnum.CIVILIANS_VACATION_JOINING.getCode()))

			doVacationJoiningDM(requester, beneficiary, instance, vacRequest, task, true);
		    else
			doVacationDM(requester, beneficiary, instance, vacRequest, task, true);

		} else if (actionTypeFlag == 2) {
		    doVacationSM(requester, beneficiary, instance, vacRequest, task, 1);
		}
	    } catch (BusinessException e) {
		unsuccessfulTaskIdsIfAny.append(task.getTaskId());
		unsuccessfulTaskIdsIfAny.append(comma);
		unsuccessfulTasksCount++;
	    }
	}

	if (unsuccessfulTasksCount > 0)
	    throw new BusinessException("error_thereAreErrorsForNOfTasks", new Object[] { unsuccessfulTasksCount, unsuccessfulTaskIdsIfAny.substring(0, unsuccessfulTaskIdsIfAny.length() - 2) });
    }

    /*------------------------------------------------ Queries --------------------------------------------------*/

    public static void validateVacationRunningWFInstances(long beneficiaryId, Long[] processesIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_BENEFICIARY_ID", beneficiaryId);

	    if (processesIds != null && processesIds.length > 0) {
		qParams.put("P_PROCESSES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_PROCESSES_IDS", processesIds);
	    } else {
		qParams.put("P_PROCESSES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_PROCESSES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }

	    Long count = DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.WF_VALIDATE_VACATION_RUNNING_PROCESSES.getCode(), qParams).get(0);
	    if (count > 0)
		throw new BusinessException("error_anotherRequestUnderProcessing");
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }
}