package com.code.services.hcm;

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
import com.code.dal.orm.hcm.vacations.VacationConfiguration;
import com.code.dal.orm.hcm.vacations.VacationData;
import com.code.dal.orm.hcm.vacations.VacationLog;
import com.code.dal.orm.hcm.vacations.VacationType;
import com.code.dal.orm.workflow.WFPosition;
import com.code.enums.AdminDecisionsEnum;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.GendersEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.PaidVacationTypesEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.ReportNamesEnum;
import com.code.enums.RequestTypesEnum;
import com.code.enums.SubVacationTypesEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.VacationTypesEnum;
import com.code.enums.WFPositionsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.integration.responses.payroll.AdminDecisionEmployeeData;
import com.code.services.BaseService;
import com.code.services.buswfcoop.BusinessWorkflowCooperation;
import com.code.services.buswfcoop.EmployeesTransactionsConflictValidator;
import com.code.services.config.ETRConfigurationService;
import com.code.services.integration.PayrollEngineService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.BaseWorkFlow;

/**
 * The class <code>VacationsService</code> provides methods for handling the Internal and External vacations requests (Regular, Compelling and Sick vacations) for Officers, Soldiers and Employees.
 * 
 * These methods are calculating the balance, validating the request, manipulating the vacation request data, obtaining the vacation types and the vacations' transactions history.
 */
public class VacationsService extends BaseService {

    /**
     * Constructs a newly allocated <code>VacationsService</code> object.
     */
    private VacationsService() {
    }

    /*---------------------------------------------------------- Vacation ------------------------------------------------------*/

    /**
     * Handles an approved vacation request to insert, modify or cancel a vacation transaction.
     * 
     * @param request
     *            the {@link Vacation} transaction that will be inserted at the DB
     * @param paidVacationType
     *            the paid vacation type {@link PaidVacationTypesEnum}
     * @param vacationBeneficiary
     *            the {@link EmployeeData} represents the vacation beneficiary
     * @param externalCopies
     *            a <code>String</code> contains the external copies
     * @param subject
     *            a <code>String</code> contains the workflow process name
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs while inserting, modifying, or canceling a vacation request
     * 
     * @see RequestTypesEnum
     */
    public static void handleVacRequest(Vacation request, EmployeeData vacationBeneficiary, Integer skipWFFlag, String subject, CustomSession... useSession) throws BusinessException {
	if (request.getStatus() == RequestTypesEnum.NEW.getCode()) {
	    // Order of the next two lines is CRITICAL
	    request.setRelatedDeductedBalance(VacationsBusinessRulesService.getRelatedDeductedBalance(vacationBeneficiary, request.getVacationTypeId(), request.getStartDate(), request.getPeriod()));
	    request.setPaidVacationType(VacationsBusinessRulesService.getPaidVacationType(request.getVacationTypeId(), request.getSubVacationType(), vacationBeneficiary, request.getStartDate(), request.getRelatedDeductedBalance()));

	    request.setDecisionData(VacationsBusinessRulesService.getDecisionData(request.getVacationTypeId(), vacationBeneficiary, request.getStartDateString()));

	    VacationsDataHandlingService.insertVacData(request, vacationBeneficiary, skipWFFlag, subject, useSession);
	} else if (request.getStatus() == RequestTypesEnum.MODIFY.getCode()) {
	    VacationsDataHandlingService.modifyVacData(request, vacationBeneficiary, skipWFFlag, subject, useSession);
	} else if (request.getStatus() == RequestTypesEnum.CANCEL.getCode()) {
	    VacationsDataHandlingService.cancelVacData(request, vacationBeneficiary, skipWFFlag, subject, useSession);
	}
	if (PayrollEngineService.getIntegrationWithAllowanceAndDeductionFlag().equals(FlagsEnum.ON.getCode()))
	    doPayrollIntegration(request, FlagsEnum.OFF.getCode(), useSession);
    }

    public static void payrollIntegrationFailureHandle(Date decisionDate, String decisionNumber, CustomSession session) throws BusinessException {
	Vacation vacation = getVacationByDecisionDateAndDecisionNumber(decisionDate, decisionNumber);
	if (vacation != null)
	    doPayrollIntegration(vacation, FlagsEnum.ON.getCode(), session);
	else
	    throw new BusinessException("error_transactionDataRetrievingError");
    }

    private static void doPayrollIntegration(Vacation request, Integer resendFlag, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : null;
	if (session == null)
	    throw new BusinessException("error_general");
	session.flushTransaction();
	Long adminDecisionId = null;
	String originalVacationDecisionNumber = null;
	EmployeeData employee = EmployeesService.getEmployeeData(request.getEmpId());
	Long unitId = employee.getPhysicalUnitId();
	if (employee.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {
	    if (request.getJoiningDate() != null) { // in case of vacation joining
		originalVacationDecisionNumber = request.getDecisionNumber();
		if (request.getVacationTypeId().equals(VacationTypesEnum.SICK.getCode())) {
		    if (request.getPaidVacationType().equals(PaidVacationTypesEnum.HALF_PAID.getCode())) {
			adminDecisionId = AdminDecisionsEnum.OFFICERS_HALF_PAID_SICK_VACATION_JOINING.getCode();
		    }
		}
	    } else {
		if (request.getStatus() == RequestTypesEnum.NEW.getCode()) {
		    if (request.getVacationTypeId().equals(VacationTypesEnum.REGULAR.getCode())) {
			adminDecisionId = AdminDecisionsEnum.OFFICERS_REGULAR_VACATION_REQUEST.getCode();
			unitId = BusinessWorkflowCooperation.getVacationManager(employee, request.getVacationTypeId()).getPhysicalUnitId();
		    } else if (request.getVacationTypeId().equals(VacationTypesEnum.COMPELLING.getCode())) {
			adminDecisionId = AdminDecisionsEnum.OFFICERS_COMPELLING_VACATION_REQUEST.getCode();
			unitId = BusinessWorkflowCooperation.getVacationManager(employee, request.getVacationTypeId()).getPhysicalUnitId();
		    } else if (request.getVacationTypeId().equals(VacationTypesEnum.SICK.getCode())) {
			if (request.getPaidVacationType().equals(PaidVacationTypesEnum.FULL_PAID.getCode())) {
			    adminDecisionId = AdminDecisionsEnum.OFFICERS_FULL_PAID_SICK_VACATION_REQUEST.getCode();
			} else if (request.getPaidVacationType().equals(PaidVacationTypesEnum.HALF_PAID.getCode())) {
			    adminDecisionId = AdminDecisionsEnum.OFFICERS_HALF_PAID_SICK_VACATION_REQUEST.getCode();
			}
		    }
		} else if (request.getStatus() == RequestTypesEnum.MODIFY.getCode()) {
		    request = VacationsService.getVacationById(request.getVacationId());
		    originalVacationDecisionNumber = VacationsService.getVacationLogByVacationIdAndStatus(request.getVacationId(), RequestTypesEnum.NEW.getCode()).getDecisionNumber();
		    if (request.getVacationTypeId().equals(VacationTypesEnum.REGULAR.getCode())) {
			adminDecisionId = AdminDecisionsEnum.OFFICERS_MODIFY_REGULAR_VACATION_REQUEST.getCode();
		    } else if (request.getVacationTypeId().equals(VacationTypesEnum.SICK.getCode())) {
			if (request.getPaidVacationType().equals(PaidVacationTypesEnum.FULL_PAID.getCode())) {
			    adminDecisionId = AdminDecisionsEnum.OFFICERS_MODIFY_FULL_PAID_SICK_VACATION_REQUEST.getCode();
			} else if (request.getPaidVacationType().equals(PaidVacationTypesEnum.HALF_PAID.getCode())) {
			    adminDecisionId = AdminDecisionsEnum.OFFICERS_MODIFY_HALF_PAID_SICK_VACATION_REQUEST.getCode();
			}
		    }
		} else if (request.getStatus() == RequestTypesEnum.CANCEL.getCode()) {
		    request = VacationsService.getVacationById(request.getVacationId());
		    originalVacationDecisionNumber = VacationsService.getVacationLogByVacationIdAndStatus(request.getVacationId(), RequestTypesEnum.NEW.getCode()).getDecisionNumber();
		    if (request.getVacationTypeId().equals(VacationTypesEnum.REGULAR.getCode())) {
			adminDecisionId = AdminDecisionsEnum.OFFICERS_CANCEL_REGULAR_VACATION_REQUEST.getCode();
		    } else if (request.getVacationTypeId().equals(VacationTypesEnum.COMPELLING.getCode())) {
			adminDecisionId = AdminDecisionsEnum.OFFICERS_CANCEL_COMPELLING_VACATION_REQUEST.getCode();
		    } else if (request.getVacationTypeId().equals(VacationTypesEnum.SICK.getCode())) {
			if (request.getPaidVacationType().equals(PaidVacationTypesEnum.FULL_PAID.getCode())) {
			    adminDecisionId = AdminDecisionsEnum.OFFICERS_CANCEL_FULL_PAID_SICK_VACATION_REQUEST.getCode();
			} else if (request.getPaidVacationType().equals(PaidVacationTypesEnum.HALF_PAID.getCode())) {
			    adminDecisionId = AdminDecisionsEnum.OFFICERS_CANCEL_HALF_PAID_SICK_VACATION_REQUEST.getCode();
			}
		    }
		}
	    }
	} else if (employee.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode())) {
	    if (request.getJoiningDate() == null) {
		if (request.getStatus() == RequestTypesEnum.NEW.getCode()) {
		    if (request.getVacationTypeId().equals(VacationTypesEnum.REGULAR.getCode())) {
			adminDecisionId = AdminDecisionsEnum.SOLDIERS_REGULAR_VACATION_REQUEST.getCode();
			unitId = BusinessWorkflowCooperation.getVacationManager(employee, request.getVacationTypeId()).getPhysicalUnitId();
		    } else if (request.getVacationTypeId().equals(VacationTypesEnum.COMPELLING.getCode())) {
			adminDecisionId = AdminDecisionsEnum.SOLDIERS_COMPELLING_VACATION_REQUEST.getCode();
			unitId = BusinessWorkflowCooperation.getVacationManager(employee, request.getVacationTypeId()).getPhysicalUnitId();
		    }
		}
	    }
	}
	if (adminDecisionId != null) {
	    String gregDecisionDateString = HijriDateService.hijriToGregDateString(request.getDecisionDateString());
	    String gregVacationStartDateString = request.getJoiningDate() != null ? HijriDateService.hijriToGregDateString(HijriDateService.getHijriDateString(request.getJoiningDate())) : HijriDateService.hijriToGregDateString(request.getStartDateString());
	    String gregVacationEndDateString = (request.getJoiningDate() != null || adminDecisionId.equals(AdminDecisionsEnum.OFFICERS_CANCEL_HALF_PAID_SICK_VACATION_REQUEST.getCode())) ? null : HijriDateService.hijriToGregDateString(request.getEndDateString());
	    String requestDecisionNumber = request.getJoiningDate() != null ? System.currentTimeMillis() + "" : request.getDecisionNumber();
	    List<AdminDecisionEmployeeData> adminDecisionEmployeeDataList = new ArrayList<AdminDecisionEmployeeData>(
		    Arrays.asList(new AdminDecisionEmployeeData(employee.getEmpId(), employee.getName(), request.getVacationId(), null, gregVacationStartDateString, gregVacationEndDateString, requestDecisionNumber, originalVacationDecisionNumber)));
	    PayrollEngineService.doPayrollIntegration(adminDecisionId, employee.getCategoryId(), gregVacationStartDateString, adminDecisionEmployeeDataList, request.getAttachments(), unitId, gregDecisionDateString, DataAccess.getTableName(Vacation.class), resendFlag, request.getJoiningDate() != null ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode(), session);
	}
    }

    /**
     * Applies the vacation request business rules before handling the request.
     * 
     * @param vacationBeneficiary
     *            the employee ID of the requester who makes a vacation request
     * @param requestType
     *            the vacation request type which is (1) New, (2) Modify or (4) Cancel vacation
     * @param vacationTypeId
     *            the vacation type which is Regular, Compelling, or Sick vacation
     * @param startDateString
     *            a <code>String</code> containing the vacation start hijri date in mm/MM/yyyy format
     * @param endDateString
     *            a <code>String</code> containing the vacation end hijri date in mm/MM/yyyy format
     * @param period
     *            the vacation period in days
     * @param location
     *            a <code>String</code> containing the vacation location which represents the country (or countries) which the employee will spend the vacation in
     * @param locationFlag
     *            the flag indicates whether the vacation is (0) internal, (1) external or (2) internal external
     * @param extStartDateStringIfAny
     *            a <code>String</code> containing the external vacation start hijri date in mm/MM/yyyy format in case if the vacation is Internal_External
     * @param extEndDateStringIfAny
     *            a <code>String</code> containing the external vacation end hijri date in mm/MM/yyyy format in case if the vacation is Internal_External
     * @throws BusinessException
     *             if any error occurs or if any business rule is broken
     * 
     * @see VacationTypesEnum
     */
    public static void validateVacationRules(Vacation request, EmployeeData vacationBeneficiary, Integer skipEVacationValidation, Integer skipEmpsVacationConflicts) throws BusinessException {
	VacationsBusinessRulesService.validateVacationDates(request);
	VacationsBusinessRulesService.validateVacationLocation(request);
	if (skipEmpsVacationConflicts == FlagsEnum.ON.getCode()) {
	    EmployeesTransactionsConflictValidator
		    .validateEmployeesTransactionsConflicts(
			    new Long[] { vacationBeneficiary.getEmpId() }, new String[] { vacationBeneficiary.getName() },
			    TransactionClassesEnum.VACATIONS.getCode(), request.getStatus(),
			    request.getVacationTypeId(), FlagsEnum.ALL.getCode(), new String[] { request.getStartDateString() }, new String[] { request.getEndDateString() },
			    request.getStatus().equals(RequestTypesEnum.MODIFY.getCode()) ? request.getVacationId() : null, null);
	}
	if (ETRConfigurationService.getVacationRequestRequireJoining() == FlagsEnum.ON.getCode() && request.getStatus() == RequestTypesEnum.NEW.getCode())
	    VacationsBusinessRulesService.validatePreviousVacationJoining(vacationBeneficiary.getEmpId(), vacationBeneficiary.getCategoryId(), request.getStartDate());

	if (request.getStatus() == RequestTypesEnum.MODIFY.getCode() || request.getStatus() == RequestTypesEnum.CANCEL.getCode())
	    VacationsBusinessRulesService.validateModifyAndCancelEVacation(request.getVacationId(), request.getStatus(), skipEVacationValidation);

	if (request.getStatus() == RequestTypesEnum.NEW.getCode() || request.getStatus() == RequestTypesEnum.MODIFY.getCode()) {

	    VacationsBusinessRulesService.validateAbsenceOfLaterVacations(vacationBeneficiary.getEmpId(), request.getVacationTypeId(), request.getStartDateString());

	    if (request.getVacationTypeId() == VacationTypesEnum.REGULAR.getCode())
		VacationsBusinessRulesService.validateRegularVacationRules(request, vacationBeneficiary);
	    else if (request.getVacationTypeId() == VacationTypesEnum.COMPELLING.getCode())
		VacationsBusinessRulesService.validateCompellingVacationRules(request, vacationBeneficiary);
	    else if (request.getVacationTypeId() == VacationTypesEnum.SICK.getCode())
		VacationsBusinessRulesService.validateSickVacationRules(request, vacationBeneficiary);
	    else if (request.getVacationTypeId() == VacationTypesEnum.FIELD.getCode())
		VacationsBusinessRulesService.validateFieldVacationRules(request, vacationBeneficiary);
	    else if (request.getVacationTypeId() == VacationTypesEnum.EXCEPTIONAL.getCode())
		VacationsBusinessRulesService.validateExceptionalVacationRules(request, vacationBeneficiary);
	    else if (request.getVacationTypeId() == VacationTypesEnum.STUDY.getCode())
		VacationsBusinessRulesService.validateStudyVacationRules(request, vacationBeneficiary);
	    else if (request.getVacationTypeId() == VacationTypesEnum.EXAM.getCode())
		VacationsBusinessRulesService.validateExamVacationRules(request, vacationBeneficiary);
	    else if (request.getVacationTypeId() == VacationTypesEnum.ACCOMPANY.getCode())
		VacationsBusinessRulesService.validateAccompanyVacationRules(request, vacationBeneficiary);
	    else if (request.getVacationTypeId() == VacationTypesEnum.RELIEF.getCode())
		VacationsBusinessRulesService.validateReliefVacationRules(request, vacationBeneficiary);
	    else if (request.getVacationTypeId() == VacationTypesEnum.SPORTIVE.getCode()
		    || request.getVacationTypeId() == VacationTypesEnum.CULTURAL.getCode()
		    || request.getVacationTypeId() == VacationTypesEnum.SOCIAL.getCode())
		VacationsBusinessRulesService.validateSportiveCultureSocialVacationRules(request, vacationBeneficiary);
	    else if (request.getVacationTypeId() == VacationTypesEnum.COMPENSATION.getCode())
		VacationsBusinessRulesService.validateCompensationVacationRules(request, vacationBeneficiary);
	    else if (request.getVacationTypeId() == VacationTypesEnum.MATERNITY.getCode())
		VacationsBusinessRulesService.validateMaternityVacationRules(request, vacationBeneficiary);
	    else if (request.getVacationTypeId() == VacationTypesEnum.MOTHERHOOD.getCode())
		VacationsBusinessRulesService.validateMotherhoodVacationRules(request, vacationBeneficiary);
	    else if (request.getVacationTypeId() == VacationTypesEnum.ORPHAN_CARE.getCode())
		VacationsBusinessRulesService.validateOrphanCareVacationRules(request, vacationBeneficiary);
	    else if (request.getVacationTypeId() == VacationTypesEnum.DEATH_WAITING_PERIOD.getCode())
		VacationsBusinessRulesService.validateDeathWaitingPeriodVacationRules(request, vacationBeneficiary);
	    else if (request.getVacationTypeId() == VacationTypesEnum.DEATH_OF_RELATIVE.getCode())
		VacationsBusinessRulesService.validateDeathOfRelativeVacationRules(request, vacationBeneficiary);
	    else if (request.getVacationTypeId() == VacationTypesEnum.NEW_BABY.getCode())
		VacationsBusinessRulesService.validateNewBabyVacationRules(request, vacationBeneficiary);
	}
    }

    public static String inquireVacationBalance(EmployeeData employee, long vacationTypeId, Date balanceInquiryDate) throws BusinessException {
	String vacationBalance = "";
	if (vacationTypeId == VacationTypesEnum.SICK.getCode()) {

	    if (employee.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()) {

		String sickVacationBalance = VacationsService.calculateVacationBalance(vacationTypeId, SubVacationTypesEnum.SUB_TYPE_ONE.getCode(), FlagsEnum.ALL.getCode(), employee, balanceInquiryDate);
		String workInjurySickVacationBalance = VacationsService.calculateVacationBalance(vacationTypeId, SubVacationTypesEnum.SUB_TYPE_TWO.getCode(), FlagsEnum.ALL.getCode(), employee, balanceInquiryDate);

		vacationBalance = getMessage("label_sickVacation") + " : " + sickVacationBalance
			+ "\n" + getMessage("label_workInjurySickVacation") + " : " + workInjurySickVacationBalance;

	    } else
		vacationBalance = VacationsService.calculateVacationBalance(vacationTypeId, SubVacationTypesEnum.SUB_TYPE_ONE.getCode(), FlagsEnum.ALL.getCode(), employee, balanceInquiryDate);

	} else if (vacationTypeId == VacationTypesEnum.EXCEPTIONAL.getCode()) {
	    if (employee.getCategoryId() == CategoriesEnum.OFFICERS.getCode() || employee.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()) {
		vacationBalance = VacationsService.calculateVacationBalance(vacationTypeId, null, FlagsEnum.ALL.getCode(), employee, balanceInquiryDate);

	    } else if (employee.getCategoryId() == CategoriesEnum.PERSONS.getCode() || employee.getCategoryId() == CategoriesEnum.USERS.getCode()
		    || employee.getCategoryId() == CategoriesEnum.WAGES.getCode() || employee.getCategoryId() == CategoriesEnum.MEDICAL_STAFF.getCode()) {

		String exceptionalForCircumstancesVacationBalance = VacationsService.calculateVacationBalance(vacationTypeId, SubVacationTypesEnum.SUB_TYPE_ONE.getCode(), FlagsEnum.ALL.getCode(), employee, balanceInquiryDate);
		String exceptionalForAccompanyVacationBalance = VacationsService.calculateVacationBalance(vacationTypeId, SubVacationTypesEnum.SUB_TYPE_TWO.getCode(), FlagsEnum.ALL.getCode(), employee, balanceInquiryDate);

		vacationBalance = getMessage("label_forCircumstances") + " : " + exceptionalForCircumstancesVacationBalance
			+ "\n" + getMessage("label_forAccompany") + " : " + exceptionalForAccompanyVacationBalance;
	    }
	} else if (vacationTypeId == VacationTypesEnum.SPORTIVE.getCode()
		|| vacationTypeId == VacationTypesEnum.CULTURAL.getCode()
		|| vacationTypeId == VacationTypesEnum.SOCIAL.getCode()) {

	    String internalVacationBalance = VacationsService.calculateVacationBalance(vacationTypeId, FlagsEnum.ALL.getCode(), LocationFlagsEnum.INTERNAL.getCode(), employee, balanceInquiryDate);
	    String externalVacationBalance = VacationsService.calculateVacationBalance(vacationTypeId, FlagsEnum.ALL.getCode(), LocationFlagsEnum.EXTERNAL.getCode(), employee, balanceInquiryDate);

	    vacationBalance = getMessage("label_locIn") + " : " + internalVacationBalance
		    + "\n" + getMessage("label_locOut") + " : " + externalVacationBalance;

	} else {
	    vacationBalance = VacationsService.calculateVacationBalance(vacationTypeId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), employee, balanceInquiryDate);
	}
	return vacationBalance;
    }

    public static String calculateVacationBalance(long vacationTypeId, Integer subVacationType, Integer locationFlag, EmployeeData emp, Date balanceToDate) throws BusinessException {

	if (vacationTypeId == VacationTypesEnum.REGULAR.getCode())
	    return (int) VacationsBusinessRulesService.calculateEmpRegularBalance(emp, balanceToDate, null, -1) + "";

	else if (vacationTypeId == VacationTypesEnum.COMPELLING.getCode())
	    return VacationsBusinessRulesService.calculateEmpCompellingBalance(emp, balanceToDate, null) + "";

	else if (vacationTypeId == VacationTypesEnum.SICK.getCode()
		&& (subVacationType == SubVacationTypesEnum.SUB_TYPE_ONE.getCode()
			|| (subVacationType == SubVacationTypesEnum.SUB_TYPE_TWO.getCode() && emp.getCategoryId() == CategoriesEnum.SOLDIERS.getCode())))
	    return VacationsBusinessRulesService.getEmpSickBalance(emp, subVacationType, balanceToDate, null);

	else if (vacationTypeId == VacationTypesEnum.FIELD.getCode()
		&& (emp.getCategoryId() == CategoriesEnum.OFFICERS.getCode() || emp.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()))
	    return VacationsBusinessRulesService.calculateEmpFieldBalance(emp, balanceToDate, null) + "";

	else if (vacationTypeId == VacationTypesEnum.EXCEPTIONAL.getCode()
		&& (((emp.getCategoryId() == CategoriesEnum.OFFICERS.getCode() || emp.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()) && subVacationType == null)
			|| ((emp.getCategoryId() == CategoriesEnum.PERSONS.getCode() || emp.getCategoryId() == CategoriesEnum.USERS.getCode()
				|| emp.getCategoryId() == CategoriesEnum.WAGES.getCode() || emp.getCategoryId() == CategoriesEnum.MEDICAL_STAFF.getCode()) && subVacationType != null)))
	    return VacationsBusinessRulesService.calculateEmpExceptionalBalance(emp, subVacationType, balanceToDate, null) + "";

	else if (vacationTypeId == VacationTypesEnum.ACCOMPANY.getCode()
		&& (emp.getCategoryId() == CategoriesEnum.PERSONS.getCode() || emp.getCategoryId() == CategoriesEnum.USERS.getCode()
			|| emp.getCategoryId() == CategoriesEnum.WAGES.getCode() || emp.getCategoryId() == CategoriesEnum.MEDICAL_STAFF.getCode()))
	    return VacationsBusinessRulesService.getEmpAccompanyBalance(emp, balanceToDate, null);

	else if (vacationTypeId == VacationTypesEnum.RELIEF.getCode()
		&& (emp.getCategoryId() == CategoriesEnum.PERSONS.getCode() || emp.getCategoryId() == CategoriesEnum.USERS.getCode()
			|| emp.getCategoryId() == CategoriesEnum.WAGES.getCode() || emp.getCategoryId() == CategoriesEnum.MEDICAL_STAFF.getCode()))
	    return VacationsBusinessRulesService.calculateEmpReliefBalance(emp, balanceToDate, null) + "";

	else if ((vacationTypeId == VacationTypesEnum.SPORTIVE.getCode()
		|| vacationTypeId == VacationTypesEnum.CULTURAL.getCode()
		|| vacationTypeId == VacationTypesEnum.SOCIAL.getCode())
		&& (emp.getCategoryId() == CategoriesEnum.PERSONS.getCode() || emp.getCategoryId() == CategoriesEnum.USERS.getCode()
			|| emp.getCategoryId() == CategoriesEnum.WAGES.getCode() || emp.getCategoryId() == CategoriesEnum.MEDICAL_STAFF.getCode()))
	    return VacationsBusinessRulesService.calculateSportiveCultureSocialBalance(emp, vacationTypeId, locationFlag, balanceToDate, null) + "";

	else if (vacationTypeId == VacationTypesEnum.MOTHERHOOD.getCode()
		&& emp.getGender().equals(GendersEnum.FEMALE.getCode())
		&& (emp.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()
			|| emp.getCategoryId() == CategoriesEnum.PERSONS.getCode() || emp.getCategoryId() == CategoriesEnum.USERS.getCode()
			|| emp.getCategoryId() == CategoriesEnum.WAGES.getCode() || emp.getCategoryId() == CategoriesEnum.MEDICAL_STAFF.getCode()))
	    return VacationsBusinessRulesService.calculateEmpMotherhoodBalance(emp, balanceToDate, null) + "";

	else if (vacationTypeId == VacationTypesEnum.ORPHAN_CARE.getCode()
		&& emp.getGender().equals(GendersEnum.FEMALE.getCode())
		&& (emp.getCategoryId() == CategoriesEnum.PERSONS.getCode() || emp.getCategoryId() == CategoriesEnum.USERS.getCode()
			|| emp.getCategoryId() == CategoriesEnum.WAGES.getCode() || emp.getCategoryId() == CategoriesEnum.MEDICAL_STAFF.getCode()))
	    return VacationsBusinessRulesService.calculateEmpOrphanCareBalance(emp, balanceToDate, null) + "";

	else
	    return "";
    }

    /**
     * Validates the contractual year for a new or modified vacation for a contractor.
     * 
     * @param empId
     *            a <code>long</code> value containing the beneficiary id of this vacation
     * @param requestType
     *            an <code>int</code> value containing the vacation request type (New / Modify / Cancel)
     * @param vacationType
     *            an <code>int</code> value containing the vacation type (Regular / Compelling / Sick)
     * @param period
     *            an <code>int</code> value containing the requested vacation period
     * @param startDate
     *            a {@link Date} containing the vacation start date
     * @param contractualYearStartDate
     *            a {@link Date} containing the suggested contractual year start date by the reviewer employee
     * @param contractualYearEndDate
     *            a {@link Date} containing the suggested contractual year end date by the reviewer employee
     * @throws BusinessException
     *             <ol>
     *             <li>if the suggested contractual year by the reviewer employee is not a valid contractual year.</li>
     *             <li>if the suggested contractual year by the reviewer employee doesn't enclose the vacation start date or it is not the previous contractual year for the one which enclose the vacation start date.</li>
     *             <li>if the requested vacation period plus the summation of the periods of previous not cancelled vacations at the same suggested contractual year is greater than 30.</li>
     *             </ol>
     * 
     * @see RequestTypesEnum
     * @see CategoriesEnum
     * @see VacationTypesEnum
     */
    public static void validateContractualYearAndBalance(Vacation request, EmployeeData vacationBeneficiary) throws BusinessException {
	VacationsBusinessRulesService.validateContractualYearAndBalance(request, vacationBeneficiary);
    }

    /**
     * Calculates the start date and end date of the contractual year for a specific vacation start date given the recruitment date of the contractor who requests the vacation.
     * 
     * @param recruitmentDateString
     *            a <code>String</code> containing the recruitment hijri date in mm/MM/yyyy format
     * @param vacationStartDateString
     *            a <code>String</code> containing the vacation start hijri date in mm/MM/yyyy format
     * @return an array of 2 Strings the first <code>String</code> contains the contractual year start date and the second <code>String</code> contains the contractual year end date
     * @throws BusinessException
     *             if any error occurs
     */
    public static String[] calculateContractualYearStartAndEndDates(EmployeeData vacationBeneficiary, String vacationStartDateString) throws BusinessException {
	return VacationsBusinessRulesService.calculateContractualYearStartAndEndDates(vacationBeneficiary, vacationStartDateString, null);
    }

    /**
     * Gets the last vacation for an employee.
     * 
     * @param empId
     *            the employee ID to get his last vacation
     * @param vacationTypeId
     *            the vacation type is (1) Regular, (5) Compelling or (2) Sick
     * @return the last {@link Vacation} object for this employee
     * @throws NoDataException
     *             if there is no vacations for this employee
     * @throws BusinessException
     *             if a database error occurs
     */
    public static VacationData getLastVacationData(long empId, long vacationTypeId) throws BusinessException {
	List<VacationData> vacationData = getVacationsData(empId, vacationTypeId, FlagsEnum.ALL.getCode());
	return vacationData.isEmpty() ? null : vacationData.get(0);
    }

    public static VacationData getLastVacationData(long empId, long vacationTypeId, Integer subVacationType) throws BusinessException {
	List<VacationData> vacationData = getVacationsData(empId, vacationTypeId, subVacationType);
	return vacationData.isEmpty() ? null : vacationData.get(0);
    }

    public static List<VacationData> getVacationsData(long empId, long vacationTypeId) throws BusinessException {
	return getVacationsData(empId, vacationTypeId, FlagsEnum.ALL.getCode());
    }

    /**
     * Gets the last vacation for an employee.
     * 
     * @param empId
     *            the employee ID to get his last vacation
     * @param vacationTypeId
     *            the vacation type is (1) Regular, (5) Compelling or (2) Sick
     * @return the last {@link Vacation} object for this employee
     * @throws NoDataException
     *             if there is no vacations for this employee
     * @throws BusinessException
     *             if a database error occurs
     */
    public static List<VacationData> getVacationsData(long empId, long vacationTypeId, Integer subVacationType) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_VACATION_TYPE_ID", vacationTypeId);
	    qParams.put("P_SUB_VACATION_TYPE", subVacationType == null ? FlagsEnum.ALL.getCode() : subVacationType);

	    List<VacationData> result = DataAccess.executeNamedQuery(VacationData.class, QueryNamesEnum.HCM_GET_LAST_VACATION_DATA.getCode(), qParams);
	    return result;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static Vacation getVacationByDecisionDateAndDecisionNumber(Date decisionDate, String decisionNumber) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    if (decisionDate == null) {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ALL.getCode() + "");
		qParams.put("P_DECISION_DATE", HijriDateService.getHijriSysDateString());
	    } else {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ON.getCode() + "");
		qParams.put("P_DECISION_DATE", HijriDateService.getHijriDateString(decisionDate));
	    }
	    qParams.put("P_DECISION_NUMBER", decisionNumber == null ? FlagsEnum.ALL.getCode() : decisionNumber);

	    List<Vacation> result = DataAccess.executeNamedQuery(Vacation.class, QueryNamesEnum.HCM_GET_VACATION_BY_DECISION_DATE_AND_DECISION_NUMBER.getCode(), qParams);
	    return result != null && result.size() > 0 ? result.get(0) : null;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /************************************** get President and VicePresident For BeneficiaryVacationsTypes Method ********************************/
    public static String getPresidencyManagers() throws BusinessException {
	WFPosition vicePresidentPosition = BaseWorkFlow.getWFPosition(WFPositionsEnum.VICE_PRESIDENT.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
	EmployeeData vicePresident = EmployeesService.getEmployeeByPosition(vicePresidentPosition.getUnitId(), vicePresidentPosition.getEmpId());
	StringBuilder emp = new StringBuilder();
	return emp.append(vicePresident.getEmpId() + "," + vicePresident.getManagerId()).toString();
    }

    /**
     * Gets a vacation Data object by its ID.
     * 
     * @param vacationId
     *            a <code>long</code> containing the vacation ID to load its data
     * @return the {@link VacationData} object with this ID
     * @throws BusinessException
     *             if any error occurs
     */
    public static Vacation getVacationById(long vacationId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_VACATION_ID", vacationId);
	    List<Vacation> result = DataAccess.executeNamedQuery(Vacation.class, QueryNamesEnum.HCM_GET_VACATION_BY_ID.getCode(), qParams);
	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets a vacation Data object by its ID.
     * 
     * @param vacationId
     *            a <code>long</code> containing the vacation ID to load its data
     * @return the {@link VacationData} object with this ID
     * @throws BusinessException
     *             if any error occurs
     */
    public static VacationData getVacationDataById(long id) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_VACATION_ID", id);
	    List<VacationData> result = DataAccess.executeNamedQuery(VacationData.class, QueryNamesEnum.HCM_GET_VACATION_DATA_BY_ID.getCode(), qParams);
	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets the current vacations for specific unit(s) for specific employee categories.
     * 
     * @param hkey
     *            the HKey used to get the vacations of the unit(s) with this HKey
     * @param includeChildren
     *            a flag indicates whether if the unit's children will be included in the result or not
     * @param categoriesIds
     *            an array of <code>Long</code> containing the employee categories to get their vacations
     * @return List contains the current {@link VacationData} objects of the specified unit(s)
     * @throws BusinessException
     *             if database error occurs
     */
    public static List<VacationData> getUnitCurrentVacationsData(String hkey, boolean includeChildren, Long[] categoriesIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_PREFIX_HKEY", includeChildren ? UnitsService.getHKeyPrefix(hkey) + "%" : hkey);
	    qParams.put("P_TODAY_DATE", HijriDateService.getHijriSysDateString());
	    if (categoriesIds != null && categoriesIds.length > 0) {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_CATEGORIES_IDS", categoriesIds);
	    } else {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_CATEGORIES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }
	    return DataAccess.executeNamedQuery(VacationData.class, QueryNamesEnum.HCM_GET_UNIT_CURRENT_VACACTIONS_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets the count of current vacations for specific unit(s) for specific employee categories.
     * 
     * @param hkey
     *            the HKey used to get the vacations of the unit(s) with this HKey
     * @param categoriesIds
     *            an array of <code>Long</code> containing the employee categories to get their vacations count
     * @param selectedDate
     *            a <code>String</code> containing a selected date between the start and end date of a vacation to be counted, in mm/MM/yyyy format
     * @return the count of current vacations of the specified unit(s)
     * @throws BusinessException
     *             if database error occurs
     */
    public static Long countUnitCurrentVacationsData(String hkey, Long[] categoriesIds, String selectedDate) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_PREFIX_HKEY", UnitsService.getHKeyPrefix(hkey) + "%");
	    qParams.put("P_SELECTED_DATE", selectedDate);
	    if (categoriesIds != null && categoriesIds.length > 0) {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_CATEGORIES_IDS", categoriesIds);
	    } else {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_CATEGORIES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_UNIT_CURRENT_VACACTIONS_DATA.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Searches for vacations with specific employee, status, type, and decision number.
     * 
     * @param empId
     *            the employee ID to get his vacations which matches the search criteria
     * @param status
     *            the vacation status is (1) Running, (2) Done or (3) Completed
     * @param vacationTypeId
     *            the vacation type is (1) Regular, (5) Compelling or (2) Sick
     * @param decisionNumber
     *            the vacation decision number
     * @return List contains the {@link Vacation} objects which match the search criteria
     * @throws BusinessException
     *             if database error occurs
     */
    public static List<Vacation> searchVacations(long empId, int status, long vacationTypeId, int decisionNumber) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_STATUS", status);
	    qParams.put("P_VACATION_TYPE_ID", vacationTypeId);
	    qParams.put("P_DEC_NO", decisionNumber);

	    return DataAccess.executeNamedQuery(Vacation.class, QueryNamesEnum.HCM_SEARCH_VACATIONS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Searches for vacations with specific employee, start date, status, and type.
     * 
     * @param empId
     *            the employee ID to get his vacations which matches the search criteria
     * @param startDateString
     *            the vacation start date in mm/MM/yyyy format
     * @param vacationTypeId
     *            the vacation type is (1) Regular, (5) Compelling or (2) Sick
     * @param status
     *            a list of statuses contains one or more of vacation status [(1) Running, (2) Done or (3) Completed]
     * @return the {@link Vacation} object which matches the search criteria
     * @throws BusinessException
     *             if database error occurs
     */
    public static Vacation getVacationByBeneficiaryAndStartDate(long empId, String startDateString, int vacationTypeId, Integer[] status) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_START_DATE", startDateString);
	    qParams.put("P_VACATION_TYPE_ID", vacationTypeId);
	    qParams.put("P_STATUS", status);

	    List<Vacation> result = DataAccess.executeNamedQuery(Vacation.class, QueryNamesEnum.HCM_GET_VACATION_BY_BENEFICIARY_AND_START_DATE.getCode(), qParams);
	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static VacationLog getVacationLogByVacationIdAndStatus(long vacationId, Integer status) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_VACATION_ID", vacationId);
	    qParams.put("P_STATUS", status == null ? FlagsEnum.ALL.getCode() : status);

	    List<VacationLog> result = DataAccess.executeNamedQuery(VacationLog.class, QueryNamesEnum.HCM_GET_VACATION_LOG_BY_VACATION_ID_AND_STATUS.getCode(), qParams);
	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets the not cancelled vacations history of a given employee.
     * 
     * @param empId
     *            the employee ID to get his vacations history
     * @return List contains the {@link VacationData} of this employee
     * @throws BusinessException
     *             if database error occurs
     */
    public static List<VacationData> getVacationTransactionsHistory(long empId) throws BusinessException {
	try {
	    Map<String, Object> queryParam = new HashMap<String, Object>();
	    queryParam.put("P_EMP_ID", empId);
	    return DataAccess.executeNamedQuery(VacationData.class, QueryNamesEnum.HCM_GET_VACATION_DATA_TRANSACTIONS_HISTORY.getCode(), queryParam);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static Long countVacationByDecisionNumber(String decisionNumber, Long vacationId) throws BusinessException {
	try {
	    Map<String, Object> queryParam = new HashMap<String, Object>();
	    queryParam.put("P_DECISION_NUMBER", decisionNumber);
	    queryParam.put("P_VACATION_ID", vacationId == null ? FlagsEnum.ALL.getCode() : vacationId);
	    List<Long> count = DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_VACATION_BY_DECISION_NUMBER.getCode(), queryParam);
	    return (count.isEmpty() || count == null ? 0 : count.get(0));
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    public static Date[] getSickVacationsFrameStartAndEndDate(EmployeeData emp, Integer subVacationType, Date StartDate) throws BusinessException {
	return VacationsBusinessRulesService.getSickVacationsFrameStartAndEndDate(emp, subVacationType, StartDate);
    }

    /**
     * Checks if there is any dates conflict for this employee vacations.
     * 
     * @param empId
     *            the employee ID of the requester who makes a vacation request
     * @param startDateString
     *            a <code>String</code> containing the vacation start hijri date in mm/MM/yyyy format
     * @param endDateString
     *            a <code>String</code> containing the vacation end hijri date in mm/MM/yyyy format
     * @param excludedVacationId
     *            a <code>Long</code> containing a vacation transaction Id to be excluded.
     * @throws BusinessException
     *             if there is any dates conflict or any general error occurs
     */
    public static long checkDatesConflict(long empId, String startDateString, String endDateString, Long excludedVacationId) throws BusinessException {
	return VacationsBusinessRulesService.checkDatesConflict(empId, startDateString, endDateString, excludedVacationId);
    }

    public static boolean checkPreviousVacationContinuity(long beneficiaryId, long beneficiaryCategoryId, Date vacationStartDate) throws BusinessException {
	return VacationsBusinessRulesService.checkPreviousVacationContinuity(beneficiaryId, beneficiaryCategoryId, vacationStartDate);
    }

    /*---------------------------------------------------------- Vacation Joining ----------------------------------------------*/

    /**
     * Updates the vacation joining date.
     * 
     * @param vacationTransactionId
     *            the vacation transaction ID to be updated
     * @param joiningDate
     *            the new date to be set to the vacation transaction in mm/MM/yyyy format
     * @param userId
     *            a <code>long</code> containing the user ID who makes the transaction. This ID is used for auditing.
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    public static void updateVacationJoiningDate(long vacationTransactionId, Date joiningDate, long userId, CustomSession... useSession) throws BusinessException {
	Vacation vacation = VacationsService.getVacationById(vacationTransactionId);
	VacationsDataHandlingService.updateVacationJoiningDate(vacation, joiningDate, userId, useSession);
	doPayrollIntegration(vacation, FlagsEnum.OFF.getCode(), useSession);
    }

    /**
     * Gets the last vacation without joining date for an employee.
     * 
     * @param empId
     *            the employee ID to get his last vacation
     * 
     * @return the last {@link Vacation} object for this employee
     * @throws BusinessException
     */
    public static VacationData getLastVacationWithoutJoiningDate(long empId) throws BusinessException {
	return VacationsBusinessRulesService.getLastVacationBeforeCurrentDate(empId, true);
    }

    /**
     * Gets the last vacation before given date
     * 
     * @param empId
     *            the employee ID to get his last vacation
     * 
     * 
     * @return the last {@link Vacation} object for this employee
     * @throws BusinessException
     */
    public static VacationData getLastVacationBeforeCurrentDate(long empId) throws BusinessException {
	return VacationsBusinessRulesService.getLastVacationBeforeCurrentDate(empId, false);
    }

    public static void validateVacationJoiningData(boolean validateDataOnlyFlag, Long empId, Long vacationTypeId, Integer subVacationType, String endDateString, Integer exceededDays) throws BusinessException {
	VacationsBusinessRulesService.validateVacationJoiningData(validateDataOnlyFlag, empId, vacationTypeId, subVacationType, endDateString, exceededDays);
    }

    /*---------------------------------------------------------- Vacation Type -------------------------------------------------*/

    /**
     * Gets all vacation types from the database.
     * 
     * @return List contains all the {@link VacationType} objects
     * @throws BusinessException
     *             of any error occurs
     */
    public static List<VacationType> getVacationTypes(long categoryId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_CATEGORY_ID", categoryId == FlagsEnum.ALL.getCode() ? FlagsEnum.ALL.getCode() + "" : "%," + categoryId + ",%");
	    return DataAccess.executeNamedQuery(VacationType.class, QueryNamesEnum.HCM_GET_VACATION_TYPES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*----------------------------------------------------- Vacation Configuration --------------------------------------*/

    public static void modifyVacationConfiguration(VacationConfiguration vacationConfiguration, VacationConfiguration originalVacationConfiguration, CustomSession... useSession) throws BusinessException {
	VacationsDataHandlingService.modifyVacationConfiguration(vacationConfiguration, originalVacationConfiguration, useSession);
    }

    public static List<Object> searchVacationsConfigurations(Long categoryId, Long vacationTypeId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_CATEGORY_ID", categoryId);
	    qParams.put("P_VACATION_TYPE_ID", vacationTypeId);
	    return DataAccess.executeNamedQuery(Object.class, QueryNamesEnum.HCM_SEARCH_VACATIONS_CONFIGURATIONS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*---------------------------------------------------------- Reports -------------------------------------------------------*/

    /**
     * Gets the bytes to be sent to the report to print the vacation decision.
     * 
     * @param vacationId
     *            the vacation ID to display it in the decision report
     * @param vacationTypeId
     *            the vacation type is (1) Regular, (5) Compelling or (2) Sick
     * @param employeeCategoryId
     *            the employee category is (1) Officer, (2) Soldier or (3, 4, 5, 6, 9) Employee
     * @return an array of bytes to be sent to the report
     * @throws BusinessException
     *             if any error occurs
     */
    public static byte[] getVacationDecisionBytes(long vacationId, long vacationTypeId, long employeeCategoryId) throws BusinessException {
	try {
	    String reportName = "";

	    if (vacationTypeId == VacationTypesEnum.REGULAR.getCode()) {
		if (employeeCategoryId == CategoriesEnum.OFFICERS.getCode()) {
		    reportName = ReportNamesEnum.VACATIONS_DECISION_OFFICERS_REGULAR.getCode();
		} else if (employeeCategoryId == CategoriesEnum.SOLDIERS.getCode()) {
		    reportName = ReportNamesEnum.VACATIONS_DECISION_SOLDIERS_REGULAR.getCode();
		} else if (employeeCategoryId == CategoriesEnum.PERSONS.getCode() || employeeCategoryId == CategoriesEnum.CONTRACTORS.getCode() || employeeCategoryId == CategoriesEnum.WAGES.getCode() || employeeCategoryId == CategoriesEnum.USERS.getCode() || employeeCategoryId == CategoriesEnum.MEDICAL_STAFF.getCode()) {
		    reportName = ReportNamesEnum.VACATIONS_DECISION_CIVILIANS_REGULAR.getCode();
		}
	    } else if (vacationTypeId == VacationTypesEnum.COMPELLING.getCode()) {
		if (employeeCategoryId == CategoriesEnum.OFFICERS.getCode()) {
		    reportName = ReportNamesEnum.VACATIONS_DECISION_OFFICERS_COMPELLING.getCode();
		} else if (employeeCategoryId == CategoriesEnum.SOLDIERS.getCode()) {
		    reportName = ReportNamesEnum.VACATIONS_DECISION_SOLDIERS_COMPELLING.getCode();
		} else
		    reportName = ReportNamesEnum.VACATIONS_DECISION_CIVILIANS_COMPELLING.getCode();
	    } else if (vacationTypeId == VacationTypesEnum.SICK.getCode()) {
		if (employeeCategoryId == CategoriesEnum.OFFICERS.getCode()) {
		    reportName = ReportNamesEnum.VACATIONS_DECISION_OFFICERS_SICK.getCode();
		} else if (employeeCategoryId == CategoriesEnum.SOLDIERS.getCode()) {
		    reportName = ReportNamesEnum.VACATIONS_DECISION_SOLDIERS_SICK.getCode();
		} else if (employeeCategoryId == CategoriesEnum.PERSONS.getCode() || employeeCategoryId == CategoriesEnum.CONTRACTORS.getCode() || employeeCategoryId == CategoriesEnum.WAGES.getCode() || employeeCategoryId == CategoriesEnum.USERS.getCode() || employeeCategoryId == CategoriesEnum.MEDICAL_STAFF.getCode()) {
		    reportName = ReportNamesEnum.VACATIONS_DECISION_CIVILIANS_SICK.getCode();
		}
	    } else if (vacationTypeId == VacationTypesEnum.FIELD.getCode()) {

		if (employeeCategoryId == CategoriesEnum.OFFICERS.getCode())
		    reportName = ReportNamesEnum.VACATIONS_DECISION_OFFICERS_FIELD.getCode();
		else if (employeeCategoryId == CategoriesEnum.SOLDIERS.getCode())
		    reportName = ReportNamesEnum.VACATIONS_DECISION_SOLDIERS_FIELD.getCode();

	    } else if (vacationTypeId == VacationTypesEnum.EXCEPTIONAL.getCode()) {

		if (employeeCategoryId == CategoriesEnum.OFFICERS.getCode())
		    reportName = ReportNamesEnum.VACATIONS_DECISION_OFFICERS_EXCEPTIONAL.getCode();
		else if (employeeCategoryId == CategoriesEnum.SOLDIERS.getCode())
		    reportName = ReportNamesEnum.VACATIONS_DECISION_SOLDIERS_EXCEPTIONAL.getCode();
		else if (employeeCategoryId == CategoriesEnum.PERSONS.getCode() || employeeCategoryId == CategoriesEnum.WAGES.getCode()
			|| employeeCategoryId == CategoriesEnum.USERS.getCode() || employeeCategoryId == CategoriesEnum.MEDICAL_STAFF.getCode())
		    reportName = ReportNamesEnum.VACATIONS_DECISION_CIVILIANS_EXCEPTIONAL.getCode();

	    } else if (vacationTypeId == VacationTypesEnum.ACCOMPANY.getCode()) {

		if (employeeCategoryId == CategoriesEnum.PERSONS.getCode() || employeeCategoryId == CategoriesEnum.WAGES.getCode()
			|| employeeCategoryId == CategoriesEnum.USERS.getCode() || employeeCategoryId == CategoriesEnum.MEDICAL_STAFF.getCode())
		    reportName = ReportNamesEnum.VACATIONS_DECISION_CIVILIANS_ACCOMPANY.getCode();

	    } else if (vacationTypeId == VacationTypesEnum.STUDY.getCode() || vacationTypeId == VacationTypesEnum.EXAM.getCode()
		    || vacationTypeId == VacationTypesEnum.MATERNITY.getCode() || vacationTypeId == VacationTypesEnum.MOTHERHOOD.getCode()
		    || vacationTypeId == VacationTypesEnum.DEATH_WAITING_PERIOD.getCode() || vacationTypeId == VacationTypesEnum.RELIEF.getCode()
		    || vacationTypeId == VacationTypesEnum.SPORTIVE.getCode() || vacationTypeId == VacationTypesEnum.CULTURAL.getCode()
		    || vacationTypeId == VacationTypesEnum.SOCIAL.getCode() || vacationTypeId == VacationTypesEnum.COMPENSATION.getCode()
		    || vacationTypeId == VacationTypesEnum.ORPHAN_CARE.getCode() || vacationTypeId == VacationTypesEnum.DEATH_OF_RELATIVE.getCode()
		    || vacationTypeId == VacationTypesEnum.NEW_BABY.getCode()) {

		if (employeeCategoryId == CategoriesEnum.SOLDIERS.getCode())
		    reportName = ReportNamesEnum.VACATIONS_DECISION_SOLDIERS_MANY_VACATIONS.getCode();
		else if (employeeCategoryId == CategoriesEnum.PERSONS.getCode() || employeeCategoryId == CategoriesEnum.WAGES.getCode()
			|| employeeCategoryId == CategoriesEnum.USERS.getCode() || employeeCategoryId == CategoriesEnum.MEDICAL_STAFF.getCode())
		    reportName = ReportNamesEnum.VACATIONS_DECISION_CIVILIANS_MANY_VACATIONS.getCode();
	    }
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    parameters.put("P_VACATION_ID", vacationId);

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    /***
     * Gets the bytes to be sent to the report to print joining document.
     * 
     * @param vacationId
     *            the vacation ID to display it in the joining report
     * @return
     * @throws BusinessException
     */
    public static byte[] getJoiningDocumentBytes(long vacationId) throws BusinessException {
	return getVacationsReportsBytes(70, (long) FlagsEnum.ALL.getCode(), null, null, null, (long) FlagsEnum.ALL.getCode(), null, null,
		(long) FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), (long) FlagsEnum.ALL.getCode(), null, null,
		null, null, null, null, FlagsEnum.ALL.getCode() + "", null, vacationId, false, null);
    }

    /**
     * Prints any report related to the vacation module.
     * 
     * @param reportType
     *            an <code>int</code> containing the type of the report (10) vacations transactions statistics report. (20) vacations requests and decisions report.
     * @param regionId
     *            a <code>long</code> containing the ID of the region to get the vacations in it
     * @param regionDesc
     *            a <code>String</code> containing the description of the region to be displayed in the report
     * @param selectedUnitHKey
     *            a <code>String</code> containing the HKey of the unit to get the vacations in it or its children
     * @param unitFullName
     *            a <code>String</code> containing the name of the unit to be displayed in the report
     * @param categoryId
     *            a <code>long</code> containing the employee category which is (1) Officer, (2) Soldier or (3, 4, 5, 6, 9) Employee
     * @param fromDate
     *            a <code>Date</code> containing the from hijri date, in mm/MM/yyyy format, to start searching from it
     * @param toDate
     *            a <code>Date</code> containing the to hijri date, in mm/MM/yyyy format, to end searching at it
     * @param vacationTypeId
     *            the vacation type is (1) Regular, (5) Compelling or (2) Sick
     * @param vacationStatusFlag
     *            an int containing a flag to indicate whether to get the (1) under processing or (2) approved vacations
     * @param reportTitle
     *            a <code>String</code> containing the title of the report
     * @return an array of bytes to be sent to the report
     * @throws BusinessException
     *             if any error occurs
     */
    public static byte[] getVacationsReportsBytes(int reportType, long regionId, String regionDesc, String selectedUnitHKey, String unitFullName, long categoryId,
	    Date fromDate, Date toDate, long vacationTypeId, int vacationStatusFlag, Long employeeId, String employeeName, String employeeRankDesc,
	    Long fromRankId, String fromRankDesc, Long toRankId, String toRankDesc, String decisionNumber, Date decisionDate, Long vacationId, boolean includeChildren, String reportTitle) throws BusinessException {
	try {
	    String reportName = "";
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    String hijriSysDateString = HijriDateService.getHijriSysDateString();

	    if (reportType == 10)
		reportName = ReportNamesEnum.VACATIONS_TRANSACTIONS_STATISTICAL.getCode();
	    else if (reportType == 20) {
		reportName = ReportNamesEnum.VACATIONS_TRANSACTIONS_AND_REQUESTS.getCode();

		parameters.put("P_PHYSICAL_UNIT_HKEY_PREFIX", UnitsService.getHKeyPrefix(selectedUnitHKey));
		parameters.put("P_PHYSICAL_UNIT_FULL_NAME", unitFullName);
		parameters.put("P_VACATION_TYPE_ID", vacationTypeId);
		parameters.put("P_VACATIONS_STATUS_FLAG", vacationStatusFlag);

		parameters.put("P_FROM_RANK_ID", fromRankId == null ? -1 : fromRankId);
		parameters.put("P_TO_RANK_ID", toRankId == null ? -1 : toRankId);

		parameters.put("P_FROM_RANK_DESC", fromRankDesc);
		parameters.put("P_TO_RANK_DESC", toRankDesc);
	    } else if (reportType == 30) {
		reportName = ReportNamesEnum.VACATIONS_EMPLOYEE_VACATIONS.getCode();

		parameters.put("P_EMPLOYEE_ID", employeeId);
		parameters.put("P_EMPLOYEE_NAME", employeeName);
		parameters.put("P_EMPLOYEE_RANK_DESC", employeeRankDesc);
		parameters.put("P_VACATION_TYPE_ID", vacationTypeId);
	    } else if (reportType == 40) {
		reportName = ReportNamesEnum.VACATIONS_WITHOUT_JOINING_DATE.getCode();

		parameters.put("P_PHYSICAL_UNIT_HKEY_PREFIX", UnitsService.getHKeyPrefix(selectedUnitHKey));
		parameters.put("P_PHYSICAL_UNIT_FULL_NAME", unitFullName);
	    } else if (reportType == 50) {
		reportName = ReportNamesEnum.VACATIONS_JOINING_DATE_EXCEEDERS.getCode();

		parameters.put("P_VACATION_TYPE_ID", vacationTypeId);
		parameters.put("P_EMPLOYEE_ID", employeeId == null ? FlagsEnum.ALL.getCode() : employeeId);
		parameters.put("P_PHYSICAL_UNIT_HKEY_PREFIX", UnitsService.getHKeyPrefix(selectedUnitHKey));
		parameters.put("P_PHYSICAL_UNIT_FULL_NAME", unitFullName);
	    } else if (reportType == 70) {
		reportName = ReportNamesEnum.VACATIONS_JOINING_DOCUMENT.getCode();

		parameters.put("P_VACATION_ID", vacationId == null ? (long) FlagsEnum.ALL.getCode() : vacationId);

		parameters.put("P_EMPLOYEE_ID", employeeId);
		parameters.put("P_DECISION_NUMBER", decisionNumber);
		parameters.put("P_DECISION_DATE", decisionDate == null ? hijriSysDateString : HijriDateService.getHijriDateString(decisionDate));
	    } else if (reportType == 80) {
		reportName = ReportNamesEnum.VACATIONS_TRANSACTIONS_AND_REQUESTS_STATISTICAL.getCode();
		parameters.put("P_UNIT_HKEY_PREFIX", includeChildren ? UnitsService.getHKeyPrefix(selectedUnitHKey) + "%" : selectedUnitHKey);
		parameters.put("P_UNIT_FULL_NAME", unitFullName);
		parameters.put("P_INCLUDE_CHILDREN", includeChildren ? 1 : 0);
		parameters.put("P_EXCLUDED_PROCESSES_IDS", WFProcessesEnum.OFFICERS_VACATION_JOINING.getCode() + "," +
			WFProcessesEnum.SOLDIERS_VACATION_JOINING.getCode() + "," + WFProcessesEnum.CIVILIANS_VACATION_JOINING.getCode());
	    }

	    parameters.put("P_REGION_ID", regionId);
	    parameters.put("P_REGION_DESC", regionDesc);
	    parameters.put("P_CATEGORY_ID", categoryId);
	    // fromDate should be not null in reports 10, 20
	    if (fromDate != null) {
		parameters.put("P_FROM_DATE_FLAG", FlagsEnum.ON.getCode());
		parameters.put("P_FROM_DATE", HijriDateService.getHijriDateString(fromDate));
	    } else {
		parameters.put("P_FROM_DATE_FLAG", FlagsEnum.ALL.getCode());
		parameters.put("P_FROM_DATE", hijriSysDateString);
	    }

	    // toDate should be not null in reports 10, 20
	    if (toDate != null) {
		parameters.put("P_TO_DATE_FLAG", FlagsEnum.ON.getCode());
		parameters.put("P_TO_DATE", HijriDateService.getHijriDateString(toDate));
	    } else {
		parameters.put("P_TO_DATE_FLAG", FlagsEnum.ALL.getCode());
		parameters.put("P_TO_DATE", hijriSysDateString);
	    }

	    parameters.put("P_SYS_DATE", hijriSysDateString);
	    parameters.put("P_REPORT_TITLE", reportTitle);

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

}
