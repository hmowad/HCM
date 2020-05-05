package com.code.services.hcm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.TransactionType;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.movements.MovementTransactionData;
import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.dal.orm.hcm.payroll.PayrollSalary;
import com.code.dal.orm.hcm.terminations.TerminationReason;
import com.code.dal.orm.hcm.terminations.TerminationRecord;
import com.code.dal.orm.hcm.terminations.TerminationRecordData;
import com.code.dal.orm.hcm.terminations.TerminationRecordDetail;
import com.code.dal.orm.hcm.terminations.TerminationRecordDetailData;
import com.code.dal.orm.hcm.terminations.TerminationTransaction;
import com.code.dal.orm.hcm.terminations.TerminationTransactionData;
import com.code.dal.orm.log.EmployeeLog;
import com.code.dal.orm.workflow.hcm.terminations.WFTerminationCancellationMovementData;
import com.code.enums.AdminDecisionsEnum;
import com.code.enums.CategoriesEnum;
import com.code.enums.EmployeeStatusEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.JobStatusEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RanksEnum;
import com.code.enums.ReportNamesEnum;
import com.code.enums.TerminationAbsenceTypeEnum;
import com.code.enums.TerminationDeathTypeEnum;
import com.code.enums.TerminationDueDateYearsEnum;
import com.code.enums.TerminationNationalityLossReasonEnum;
import com.code.enums.TerminationReasonsEnum;
import com.code.enums.TerminationRecordOfficersStatusEnum;
import com.code.enums.TerminationRecordStatusEnum;
import com.code.enums.TerminationRequestTypeEnum;
import com.code.enums.TerminationTypeFlagEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.integration.responses.payroll.AdminDecisionEmployeeData;
import com.code.services.BaseService;
import com.code.services.buswfcoop.BusinessWorkflowCooperation;
import com.code.services.buswfcoop.EmployeesJobsConflictValidator;
import com.code.services.config.ETRConfigurationService;
import com.code.services.cor.ETRCorrespondence;
import com.code.services.integration.PayrollEngineService;
import com.code.services.log.LogService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;

public class TerminationsService extends BaseService {

    public static final float HIJRI_MONTH = 354.0f / 12;

    // private constructor
    private TerminationsService() {
    }

    /*********************************************** Test Purpose panel ***********************************************************/
    private static boolean checkDatesDays(Date startDate, Date endDate, Float maxValue, Float minValue) {

	// if (startDate.after(endDate))
	// return false;
	float difference = HijriDateService.hijriDateDiff(startDate, endDate);
	// case between dates
	if (maxValue != null && minValue != null) {
	    if (difference > maxValue || difference < minValue)
		return false;
	} else if (maxValue != null) {
	    if (difference > maxValue)
		return false;
	} else if (minValue != null) {
	    if (difference < minValue)
		return false;
	} else {
	    return false;
	}

	return true;
    }

    private static boolean checkDatesYears(Date startDate, Date endDate, Float maxValue, Float minValue) {

	// if (startDate.after(endDate))
	// return false;
	float difference = HijriDateService.hijriDateDiffByHijriYear(startDate, endDate);
	// case between dates
	if (maxValue != null && minValue != null) {
	    if (difference > maxValue || difference < minValue)
		return false;
	} else if (maxValue != null) {
	    if (difference > maxValue)
		return false;
	} else if (minValue != null) {
	    if (difference < minValue)
		return false;
	} else {
	    return false;
	}

	return true;
    }

    /*********************************************** Construct ***********************************************************/
    private static void constructTerminationRecord(TerminationRecordData terminationRecordData) throws BusinessException {
	// set status
	if (terminationRecordData.getStatus() == null)
	    terminationRecordData.setStatus(TerminationRecordStatusEnum.CURRENT.getCode());

	Long[] categoriesIds = null;

	if (terminationRecordData.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) || terminationRecordData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode())) {
	    categoriesIds = new Long[] { terminationRecordData.getCategoryId() };
	} else
	    categoriesIds = CommonService.getCivilCategoriesIdsArray();
	// Generate next RecordNumber
	terminationRecordData.setRecordNumber(generateNextRecordNumber(categoriesIds));
	terminationRecordData.setRecordDate(HijriDateService.getHijriSysDate());
    }

    private static List<TerminationRecordDetailData> constructTerminationRecordDetails(TerminationRecordData terminationRecordData, List<EmployeeData> employeeDataList) throws BusinessException {

	List<TerminationRecordDetailData> terminationRecordDetailDataList = new ArrayList<TerminationRecordDetailData>();
	for (EmployeeData employee : employeeDataList) {
	    TerminationRecordDetailData terminationRecordDetailData = new TerminationRecordDetailData();

	    terminationRecordDetailData.setEmpId(employee.getEmpId());

	    terminationRecordDetailData.setRecordId(terminationRecordData.getId());

	    terminationRecordDetailData.setEmpName(employee.getName());
	    terminationRecordDetailData.setEmpRankId(employee.getRankId());

	    terminationRecordDetailData.setEmpRankDescription(employee.getRankDesc());
	    terminationRecordDetailData.setEmpMilitaryNumber(employee.getMilitaryNumber());
	    terminationRecordDetailData.setEmpBirthDate(employee.getBirthDate());

	    terminationRecordDetailData.setEmpJobCode(employee.getJobCode());
	    terminationRecordDetailData.setEmpJobName(employee.getJobDesc());

	    terminationRecordDetailData.setEmpPhysicalUnitId(employee.getPhysicalUnitId());
	    terminationRecordDetailData.setEmpOfficialUnitId(employee.getOfficialUnitId());
	    terminationRecordDetailData.setEmpOfficialUnitFullName(employee.getOfficialUnitFullName());

	    terminationRecordDetailData.setEmpTerminationDueDate(employee.getServiceTerminationDueDate());
	    terminationRecordDetailData.setEmpLastExtensionEndDate(employee.getLastExtensionEndDate());

	    if (terminationRecordData.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {
		terminationRecordDetailData.setStatus(TerminationRecordOfficersStatusEnum.UNDER_REVIEW.getCode());
	    } else if (terminationRecordData.getCategoryId().equals(CategoriesEnum.PERSONS.getCode())) {
		if (terminationRecordData.getReasonId() == TerminationReasonsEnum.PERSONS_TERMINATION_REQUEST.getCode())
		    terminationRecordDetailData.setTerminationRequestType(TerminationRequestTypeEnum.RETIREMENT.getCode());
		else if (terminationRecordData.getReasonId() == TerminationReasonsEnum.PERSONS_ABSENCE.getCode())
		    terminationRecordDetailData.setAbsenceType(TerminationAbsenceTypeEnum.CONTINUOUS_DAYS.getCode());
		else if (terminationRecordData.getReasonId() == TerminationReasonsEnum.PERSONS_NATIONALITY_LOSS.getCode())
		    terminationRecordDetailData.setNationalityLossType(TerminationNationalityLossReasonEnum.NOT_OFFICIALLY_ISSUED.getCode());
	    }

	    terminationRecordDetailData.setEmpRecruitmentDate(employee.getRecruitmentDate());
	    terminationRecordDetailData.setEmpPromotionDueDate(employee.getPromotionDueDate());
	    terminationRecordDetailData.setEmpLastPromotionDate(employee.getLastPromotionDate());

	    terminationRecordDetailDataList.add(terminationRecordDetailData);
	}

	return terminationRecordDetailDataList;
    }

    public static List<TerminationTransactionData> constructTerminationTransactions(TerminationRecordData terminationRecordData, List<TerminationRecordDetailData> terminationRecordDetailDataList, long tansactionTypeId) throws BusinessException {

	List<TerminationTransactionData> terminationTransactionList = new ArrayList<TerminationTransactionData>();

	for (TerminationRecordDetailData terminationRecordDetailData : terminationRecordDetailDataList) {
	    EmployeeData employee = EmployeesService.getEmployeeData(terminationRecordDetailData.getEmpId());
	    TerminationTransactionData terminationTransaction = new TerminationTransactionData();

	    terminationTransaction.setRecordDetailId(terminationRecordDetailData.getId());
	    terminationTransaction.setEmpId(terminationRecordDetailData.getEmpId());
	    terminationTransaction.setCategoryId(terminationRecordData.getCategoryId());

	    PayrollSalary payrollSalary = PayrollsService.getPayrollSalary(employee.getRankId(), employee.getDegreeId());
	    terminationTransaction.setBasicSalary(payrollSalary != null ? payrollSalary.getBasicSalary() : null);
	    terminationTransaction.setDegreeId(employee.getDegreeId());
	    terminationTransaction.setDegreeDesc(employee.getDegreeDesc());

	    terminationTransaction.setServiceTerminationDate(terminationRecordDetailData.getServiceTerminationDate());
	    terminationTransaction.setStatus(terminationRecordDetailData.getStatus());

	    terminationTransaction.setRemarks(terminationRecordDetailData.getRemarks());
	    terminationTransaction.setReferring(terminationRecordDetailData.getReferring());
	    terminationTransaction.setAttachments(terminationRecordData.getAttachments());
	    terminationTransaction.setTerminationRequestReason(terminationRecordDetailData.getTerminationRequestReason());
	    terminationTransaction.setTerminationRequestType(terminationRecordDetailData.getTerminationRequestType());
	    terminationTransaction.setDesiredTerminationDate(terminationRecordDetailData.getDesiredTerminationDate());
	    terminationTransaction.setMinistryRequestNumber(terminationRecordDetailData.getMinistryRejectionNumber());
	    terminationTransaction.setMinistryRequestDate(terminationRecordDetailData.getMinistryRequestDate());
	    terminationTransaction.setMinistryRejectionNumber(terminationRecordDetailData.getMinistryRejectionNumber());
	    terminationTransaction.setMinistryRejectionDate(terminationRecordDetailData.getMinistryRejectionDate());

	    terminationTransaction.setExtensionStartDate(terminationRecordDetailData.getExtensionStartDate());
	    terminationTransaction.setExtensionEndDate(terminationRecordDetailData.getExtensionEndDate());
	    terminationTransaction.setExtensionPeriodDays(terminationRecordDetailData.getExtensionPeriodDays());
	    terminationTransaction.setExtensionPeriodMonths(terminationRecordDetailData.getExtensionPeriodMonths());
	    terminationTransaction.setExtensionFlag(terminationRecordDetailData.getExtensionFlag());
	    terminationTransaction.setJudgmentFlag(terminationRecordDetailData.getJudgmentFlag());
	    terminationTransaction.setSpecializationPeriodFlag(terminationRecordDetailData.getSpecializationPeriodFlag());
	    terminationTransaction.setForeignerMarriageFlag(terminationRecordDetailData.getForeignerMarriageFlag());

	    terminationTransaction.setDismissType(terminationRecordDetailData.getDismissType());
	    terminationTransaction.setDismissDecisionNumber(terminationRecordDetailData.getDismissDecisionNumber());
	    terminationTransaction.setDismissDecisionDate(terminationRecordDetailData.getDismissDecisionDate());
	    terminationTransaction.setDismissStatus(terminationRecordDetailData.getDismissStatus());
	    terminationTransaction.setDismissResumptionNumber(terminationRecordDetailData.getDismissResumptionNumber());
	    terminationTransaction.setDismissResumptionYear(terminationRecordDetailData.getDismissResumptionYear());
	    terminationTransaction.setDismissApprovalNumber(terminationRecordDetailData.getDismissApprovalNumber());
	    terminationTransaction.setDismissApprovalDate(terminationRecordDetailData.getDismissApprovalDate());
	    terminationTransaction.setDismissBasedOn(terminationRecordDetailData.getDismissBasedOn());
	    terminationTransaction.setDismissBasedOnDesc(terminationRecordDetailData.getDismissBasedOnDesc());
	    terminationTransaction.setDismissJudgment(terminationRecordDetailData.getDismissJudgment());

	    terminationTransaction.setDeathCertNumber(terminationRecordDetailData.getDeathCertNumber());
	    terminationTransaction.setDeathCertDate(terminationRecordDetailData.getDeathCertDate());
	    terminationTransaction.setDeathCertIssuePlace(terminationRecordDetailData.getDeathCertIssuePlace());
	    terminationTransaction.setDeathDate(terminationRecordDetailData.getDeathDate());
	    terminationTransaction.setDeathInDutyFlag(terminationRecordDetailData.getDeathInDutyFlag());
	    terminationTransaction.setDeathType(terminationRecordDetailData.getDeathType());
	    terminationTransaction.setDeathInOperations(terminationRecordDetailData.getDeathInOperations());

	    terminationTransaction.setAbsenceType(terminationRecordDetailData.getAbsenceType());
	    terminationTransaction.setAbsenceStartDate(terminationRecordDetailData.getAbsenceStartDate());
	    terminationTransaction.setAbsencePeriodDays(terminationRecordDetailData.getAbsencePeriodDays());
	    terminationTransaction.setAbsenceReturnDate(terminationRecordDetailData.getAbsenceReturnDate());

	    terminationTransaction.setDisqualificationDrugsFlag(terminationRecordDetailData.getDisqualificationDrugsFlag());
	    terminationTransaction.setDisqualificationReason(terminationRecordDetailData.getDisqualificationReason());
	    terminationTransaction.setDisqualificationDrugsType(terminationRecordDetailData.getDisqualificationDrugsType());
	    terminationTransaction.setMedicalCaseDesc(terminationRecordDetailData.getMedicalCaseDesc());
	    terminationTransaction.setMedicalWorkDisability(terminationRecordDetailData.getMedicalWorkDisability());

	    terminationTransaction.setJobCancelationDate(terminationRecordDetailData.getJobCancelationDate());
	    terminationTransaction.setNationalityLossType(terminationRecordDetailData.getNationalityLossType());
	    terminationTransaction.setNationalityLossReason(terminationRecordDetailData.getNationalityLossReason());
	    terminationTransaction.setContractorBasedOnType(terminationRecordDetailData.getContractorBasedOnType());
	    terminationTransaction.setContractorTerminationReason(terminationRecordDetailData.getContractorTerminationReason());

	    terminationTransaction.setJobId(employee.getJobId());
	    terminationTransaction.setJobCode(terminationRecordDetailData.getEmpJobCode());
	    terminationTransaction.setJobName(terminationRecordDetailData.getEmpJobName());
	    terminationTransaction.setJobClassificationId(employee.getJobClassificationId());

	    terminationTransaction.setTransEmpRankDesc(terminationRecordDetailData.getEmpRankDescription());
	    terminationTransaction.setTransEmpJobClassJobCode(employee.getJobClassificationCode());
	    terminationTransaction.setTransEmpJobClassJobDesc(employee.getJobClassificationDesc());
	    terminationTransaction.setTransEmpUnitFullName(terminationRecordDetailData.getEmpOfficialUnitFullName());
	    terminationTransaction.setTransEmpUnitId(terminationRecordDetailData.getEmpOfficialUnitId());

	    terminationTransaction.setTransactionTypeId(tansactionTypeId);
	    terminationTransaction.setReasonId(terminationRecordData.getReasonId());

	    if (terminationRecordData.getCategoryId().longValue() == CategoriesEnum.OFFICERS.getCode())
		terminationTransaction.setEflag(FlagsEnum.OFF.getCode());
	    else
		terminationTransaction.setEflag(FlagsEnum.ON.getCode());
	    terminationTransaction.setMigFlag(FlagsEnum.OFF.getCode());
	    terminationTransaction.setDecisionNumber(terminationRecordDetailData.getDecisionNumber());
	    terminationTransaction.setDecisionDate(terminationRecordDetailData.getDecisionDate());

	    terminationTransaction.setInternalCopies(terminationRecordData.getInternalCopies());
	    terminationTransaction.setExternalCopies(terminationRecordData.getExternalCopies());
	    terminationTransaction.setApprovedId(terminationRecordData.getApprovedId());
	    terminationTransaction.setDecisionApprovedId(terminationRecordData.getDecisionApprovedId());
	    terminationTransaction.setOriginalDecisionApprovedId(terminationRecordData.getOriginalDecisionApprovedId());
	    terminationTransaction.setInjuryInDutyFlag(terminationRecordDetailData.getInjuryInDutyFlag());
	    terminationTransaction.setInjuryType(terminationRecordDetailData.getInjuryType());

	    terminationTransactionList.add(terminationTransaction);
	}
	return terminationTransactionList;

    }

    public static TerminationTransactionData constructTerminationCancellationMovementTransaction(EmployeeData employee, JobData empJob, Long processId, WFTerminationCancellationMovementData wfTerminationCancellationMovementData, Long transactionTypeId, Long categoryId) throws BusinessException {

	TerminationTransactionData terminationTransaction = new TerminationTransactionData();

	// load movement transaction in case of movement only and set jobName, jodCode, unitName
	if (WFProcessesEnum.CIVILIANS_TERMINATION_MOVEMENT.getCode() == processId.longValue()) {
	    MovementTransactionData movementTransactionData = MovementsService.getExternalMoveTransactionByEmployeeId(wfTerminationCancellationMovementData.getEmpId());

	    terminationTransaction.setJobName(movementTransactionData.getTransEmpJobName());
	    terminationTransaction.setJobCode(movementTransactionData.getTransEmpJobCode());
	    terminationTransaction.setTransEmpUnitFullName(movementTransactionData.getTransEmpUnitFullName());

	    terminationTransaction.setJobClassificationId(movementTransactionData.getJobClassificationId());
	    terminationTransaction.setTransEmpJobClassJobCode(movementTransactionData.getTransEmpJobClassJobCode());
	    terminationTransaction.setTransEmpJobClassJobDesc(movementTransactionData.getTransEmpJobClassJobDesc());
	} else {
	    terminationTransaction.setJobName(empJob.getName());
	    terminationTransaction.setJobCode(empJob.getCode());
	    terminationTransaction.setTransEmpUnitFullName(empJob.getUnitFullName());

	    terminationTransaction.setJobClassificationId(empJob.getClassificationId());
	    terminationTransaction.setTransEmpJobClassJobCode(empJob.getClassificationJobCode());
	    terminationTransaction.setTransEmpJobClassJobDesc(empJob.getClassificationJobDescription());
	}

	terminationTransaction.setCategoryId(categoryId);
	terminationTransaction.setEmpId(wfTerminationCancellationMovementData.getEmpId());
	PayrollSalary payrollSalary = PayrollsService.getPayrollSalary(employee.getRankId(), employee.getDegreeId());
	terminationTransaction.setBasicSalary(payrollSalary != null ? payrollSalary.getBasicSalary() : null);
	terminationTransaction.setDegreeId(employee.getDegreeId());
	terminationTransaction.setDegreeDesc(employee.getDegreeDesc());
	terminationTransaction.setReferring(wfTerminationCancellationMovementData.getReferring());
	terminationTransaction.setCancelTransactionId(wfTerminationCancellationMovementData.getCancelTransactionId());
	terminationTransaction.setCancelTransactionReason(wfTerminationCancellationMovementData.getReasons());

	terminationTransaction.setTransactionTypeId(transactionTypeId);

	terminationTransaction.setMovedTo(wfTerminationCancellationMovementData.getMovedTo());
	terminationTransaction.setMovementJoiningDate(wfTerminationCancellationMovementData.getMovementJoiningDate());
	terminationTransaction.setMovementJoiningDesc(wfTerminationCancellationMovementData.getMovementJoiningDesc());
	terminationTransaction.setDisclaimerDate(wfTerminationCancellationMovementData.getDisclaimerDate());
	terminationTransaction.setServiceTerminationDate(wfTerminationCancellationMovementData.getServiceTerminationDate());

	terminationTransaction.setJobId(wfTerminationCancellationMovementData.getJobId());

	terminationTransaction.setTransEmpRankDesc(employee.getRankDesc());

	terminationTransaction.setInternalCopies(wfTerminationCancellationMovementData.getInternalCopies());
	terminationTransaction.setExternalCopies(wfTerminationCancellationMovementData.getExternalCopies());

	terminationTransaction.setEflag(FlagsEnum.ON.getCode());
	terminationTransaction.setMigFlag(FlagsEnum.OFF.getCode());

	return terminationTransaction;

    }

    /*********************************************** Termination Records/RecordDetail operations ***********************************************************/

    public static void addTerminationRecordAndTerminationDetails(TerminationRecordData terminationRecordData, List<TerminationRecordDetailData> terminationRecordDetailDataList, Long rankId, Date empTerminationDueDateFrom, Date empTerminationDueDateTo, long loginEmpId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	boolean newRecord = false;
	boolean singularRecord = true;
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    if (terminationRecordData.getId() == null)
		newRecord = true;

	    saveTerminationRecord(terminationRecordData, loginEmpId, session);

	    // check if this record is singular or collective
	    List<EmployeeData> employeeDataList = new ArrayList<EmployeeData>();
	    if (newRecord) {
		// Get Eligible Employees for Termination
		if ((terminationRecordData.getReasonId().equals(TerminationReasonsEnum.OFFICERS_REACHING_RETIREMENT_AGE.getCode()))
			|| (terminationRecordData.getReasonId().equals(TerminationReasonsEnum.SOLDIERS_REACHING_RETIREMENT_AGE.getCode()))
			|| (terminationRecordData.getReasonId().equals(TerminationReasonsEnum.PERSONS_REACHING_RETIREMENT_AGE.getCode()))) {
		    employeeDataList = getTerminationEligibleEmployees(terminationRecordData, rankId, empTerminationDueDateFrom, empTerminationDueDateTo);
		    singularRecord = false;
		} else if (terminationRecordData.getReasonId().equals(TerminationReasonsEnum.OFFICERS_COMPLETION_PERIOD_CURRENT_RANK.getCode())) {
		    employeeDataList = getTerminationEligibleEmployeesCompletedPeriodCurrentRank(terminationRecordData, rankId, empTerminationDueDateFrom, empTerminationDueDateTo);
		    singularRecord = false;
		}
	    }

	    if (!singularRecord) {
		List<TerminationRecordDetailData> newTerminationRecordDetailDataList = constructTerminationRecordDetails(terminationRecordData, employeeDataList);
		addModifyTerminationRecordDetail(terminationRecordData, newTerminationRecordDetailDataList, loginEmpId, session);
		terminationRecordDetailDataList.addAll(newTerminationRecordDetailDataList);

	    }
	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    if (newRecord) {
		terminationRecordData.setId(null);
		for (TerminationRecordDetailData recordDetailData : terminationRecordDetailDataList)
		    recordDetailData.setId(null);
	    }
	    throw new BusinessException(e.getMessage(), e.getParams());
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    if (newRecord) {
		terminationRecordData.setId(null);
		for (TerminationRecordDetailData recordDetailData : terminationRecordDetailDataList)
		    recordDetailData.setId(null);
	    }
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    // save one detail in case of modification of this detail
    public static TerminationRecordDetailData addCollectiveReasonsTerminationRecordDetail(TerminationRecordData terminationRecordData, List<TerminationRecordDetailData> terminationRecordDetailDataList, Long selectedEmployeeId, long loginEmpId, CustomSession session) throws BusinessException {

	// 1- check if selectedEmployee has been already added
	for (int i = 0; i < terminationRecordDetailDataList.size(); i++) {
	    if (terminationRecordDetailDataList.get(i).getEmpId().equals(selectedEmployeeId)) {
		throw new BusinessException("error_empDuplicateSameTerminationRecord");
	    }
	}

	EmployeeData employee = EmployeesService.getEmployeeData(selectedEmployeeId);

	if (employee.getServiceTerminationDueDate() == null)
	    throw new BusinessException("error_terminationDueDateNotFound");

	if (terminationRecordData.getReasonId().longValue() == TerminationReasonsEnum.OFFICERS_REACHING_RETIREMENT_AGE.getCode()) {
	    if (employee.getRankId().longValue() < RanksEnum.MAJOR_GENERAL.getCode() || employee.getRankId().longValue() > RanksEnum.LIEUTENANT.getCode())
		throw new BusinessException("error_terminationReachingAgeInvalidRanks");
	} else if (terminationRecordData.getReasonId().longValue() == TerminationReasonsEnum.SOLDIERS_REACHING_RETIREMENT_AGE.getCode()) {
	    if (employee.getRankId().longValue() < RanksEnum.PRIME_SERGEANTS.getCode() || employee.getRankId().longValue() > RanksEnum.SOLDIER.getCode())
		throw new BusinessException("error_terminationSoldierReachingAgeInvalidRanks");
	}

	if (terminationRecordData.getReasonId().longValue() == TerminationReasonsEnum.OFFICERS_COMPLETION_PERIOD_CURRENT_RANK.getCode()) {
	    if (employee.getRankId().longValue() < RanksEnum.MAJOR_GENERAL.getCode() || employee.getRankId().longValue() > RanksEnum.LIEUTENANT_COLONEL.getCode())
		throw new BusinessException("error_terminationCompletionPeriodCurrentRankInvalidRanks");

	    // compute empcurrentRankPeriodEnd depending on lastpromotionDate/recruitmentDate and employee's rank
	    Date empCurrentRankEndDate;
	    // check rank to compute terminationDueDate
	    if (employee.getRankId().equals(RanksEnum.MAJOR_GENERAL.getCode()))
		empCurrentRankEndDate = HijriDateService.addSubHijriMonthsDays(employee.getLastPromotionDate() != null ? employee.getLastPromotionDate() : employee.getRecruitmentDate(), ETRConfigurationService.getTerminationsOfficersRankPeriodMajorGeneralYearsMax() * 12, 0);
	    else
		empCurrentRankEndDate = HijriDateService.addSubHijriMonthsDays(employee.getLastPromotionDate() != null ? employee.getLastPromotionDate() : employee.getRecruitmentDate(), ETRConfigurationService.getTerminationsOfficersRankPeriodYearsMax() * 12, 0);

	    // check if this employee will end his rank period after 2 years, throw exception
	    if (HijriDateService.hijriDateDiffByHijriYear(HijriDateService.getHijriSysDate(), empCurrentRankEndDate) > ETRConfigurationService.getTerminationsServiceTerminationDueDateInYears())
		throw new BusinessException("error_terminationOfficersTerminationCompletionPeriod", new Object[] { ETRConfigurationService.getTerminationsServiceTerminationDueDateInYears() });
	} else { // Reaching retirement age For all categories
	    if (employee.getLastExtensionEndDate() != null && HijriDateService.hijriDateDiffByHijriYear(HijriDateService.getHijriSysDate(), employee.getLastExtensionEndDate()) > ETRConfigurationService.getTerminationsServiceTerminationDueDateInYears())
		throw new BusinessException("error_terminationDueDateViolation", new Object[] { ETRConfigurationService.getTerminationsServiceTerminationDueDateInYears() });
	    if (employee.getLastExtensionEndDate() == null && HijriDateService.hijriDateDiffByHijriYear(HijriDateService.getHijriSysDate(), employee.getServiceTerminationDueDate()) > ETRConfigurationService.getTerminationsServiceTerminationDueDateInYears())
		throw new BusinessException("error_terminationDueDateViolation", new Object[] { ETRConfigurationService.getTerminationsServiceTerminationDueDateInYears() });

	}

	EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(new Long[] { selectedEmployeeId }, null, TransactionClassesEnum.TERMINATIONS.getCode(), false, terminationRecordData.getId() == null ? null : terminationRecordData.getId() * -1, terminationRecordData.getCategoryId());

	List<EmployeeData> employeeDataList = new ArrayList<EmployeeData>();
	employeeDataList.add(employee);

	List<TerminationRecordDetailData> newTerminationRecordDetailDataList = constructTerminationRecordDetails(terminationRecordData, employeeDataList);

	addModifyTerminationRecordDetail(terminationRecordData, newTerminationRecordDetailDataList, loginEmpId, session);
	terminationRecordDetailDataList.addAll(newTerminationRecordDetailDataList);
	return newTerminationRecordDetailDataList.get(0);
    }

    // Add the detail in case of any reason Except Retirement Age.
    public static void addSingularReasonsTerminationRecordDetail(TerminationRecordData terminationRecordData, List<TerminationRecordDetailData> terminationRecordDetailDataList, Long selectedEmployeeId, long loginEmpId) throws BusinessException {

	// excludedInstanceId is sent as recordId here as it's used for both request and record cases, although for record we haven't added employee to the record yet
	// but it will not affect it at all as no jobs are sent
	if (terminationRecordData.getTypeFlag().equals(TerminationTypeFlagEnum.RECORD.getCode()))
	    EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(new Long[] { selectedEmployeeId }, null, TransactionClassesEnum.TERMINATIONS.getCode(), false, terminationRecordData.getId() == null ? null : terminationRecordData.getId() * -1, terminationRecordData.getCategoryId());

	List<EmployeeData> employeeDataList = new ArrayList<EmployeeData>();
	EmployeeData employee = EmployeesService.getEmployeeData(selectedEmployeeId);
	employeeDataList.add(employee);

	// addTerminationRecordDetails(terminationRecordData, terminationRecordDetailDataList, employeeDataList);
	List<TerminationRecordDetailData> newTerminationRecordDetailDataList = constructTerminationRecordDetails(terminationRecordData, employeeDataList);
	terminationRecordDetailDataList.clear();
	terminationRecordDetailDataList.addAll(newTerminationRecordDetailDataList);
    }

    public static void saveTerminationRecord(TerminationRecordData terminationRecordData, long loginEmpId, CustomSession... useSession) throws BusinessException {

	// 1. Construct TerminationRecord (just in case it's new record)
	if (terminationRecordData.getId() == null)
	    constructTerminationRecord(terminationRecordData);
	// 2. add TerminationRecord
	addModifyTerminationRecord(terminationRecordData, loginEmpId, useSession);
    }

    public static void closeTerminationRecords(Long[] recordsIds, CustomSession session) throws BusinessException {
	try {
	    List<TerminationRecord> terminationRecordsList = searchTerminationReports(recordsIds);
	    for (TerminationRecord terminationRecordItr : terminationRecordsList) {
		terminationRecordItr.setStatus(TerminationRecordStatusEnum.CLOSED.getCode());
		DataAccess.updateEntity(terminationRecordItr, session);
	    }
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static void saveCollectiveOfficersTerminationRecordDetails(TerminationRecordData terminationRecordData, List<TerminationRecordDetailData> collectiveRecordDetailDataList, String collectiveOfficersDecisionNumber, Date collectiveOfficersDecisionDate, List<TerminationRecordDetailData> originalTerminationRecordDetailDataList, long loginEmpId) throws BusinessException {
	if (collectiveOfficersDecisionNumber == null || collectiveOfficersDecisionNumber.trim().isEmpty() || collectiveOfficersDecisionDate == null || collectiveOfficersDecisionDate.equals(""))
	    throw new BusinessException("error_terminationDecisionNumberAndDate");

	if (!HijriDateService.isValidHijriDate(collectiveOfficersDecisionDate))
	    throw new BusinessException("error_invalidDecisionDate");

	HashMap<Long, Long> oldEmployeesTerminationRecordDetailsStatusMap = new HashMap<Long, Long>();
	for (int i = 0; i < collectiveRecordDetailDataList.size(); i++) {
	    oldEmployeesTerminationRecordDetailsStatusMap.put(collectiveRecordDetailDataList.get(i).getEmpId(), collectiveRecordDetailDataList.get(i).getStatus());
	    changeOfficersCollectiveRecordDetails(collectiveRecordDetailDataList.get(i), collectiveOfficersDecisionNumber, collectiveOfficersDecisionDate, TerminationRecordOfficersStatusEnum.TERMINATED.getCode());
	    setCollectiveRecordsServiceTerminationDate(collectiveRecordDetailDataList.get(i), terminationRecordData.getReasonId());
	}
	try {
	    saveOfficersTerminationRecordDetails(terminationRecordData, collectiveRecordDetailDataList, originalTerminationRecordDetailDataList, loginEmpId);
	} catch (Exception e) {
	    // roll back changes made to promotion report details
	    for (int i = 0; i < collectiveRecordDetailDataList.size(); i++) {
		changeOfficersCollectiveRecordDetails(collectiveRecordDetailDataList.get(i), null, null, oldEmployeesTerminationRecordDetailsStatusMap.get(collectiveRecordDetailDataList.get(i).getEmpId()));
	    }
	    if (e instanceof BusinessException) {
		throw new BusinessException(e.getMessage(), ((BusinessException) e).getParams());
	    } else {
		e.printStackTrace();
		throw new BusinessException("error_general");
	    }
	}
    }

    private static void changeOfficersCollectiveRecordDetails(TerminationRecordDetailData terminationRecordDetailData, String collectiveOfficersDecisionNumber, Date collectiveOfficersDecisionDate, Long Status) throws BusinessException {
	terminationRecordDetailData.setStatus(Status);
	terminationRecordDetailData.setDecisionNumber(collectiveOfficersDecisionNumber);
	terminationRecordDetailData.setDecisionDate(collectiveOfficersDecisionDate);

	// reset extension data
	terminationRecordDetailData.setExtensionStartDate(null);
	terminationRecordDetailData.setExtensionEndDate(null);
	terminationRecordDetailData.setExtensionPeriodDays(null);
	terminationRecordDetailData.setExtensionPeriodMonths(null);

	terminationRecordDetailData.setTerminationRequestType(null);

	if (terminationRecordDetailData.getStatus().longValue() != TerminationRecordOfficersStatusEnum.TERMINATED.getCode())
	    terminationRecordDetailData.setServiceTerminationDate(null);

    }

    public static void saveOfficersTerminationRecordDetails(TerminationRecordData terminationRecordData, List<TerminationRecordDetailData> terminationRecordDetailDataList, List<TerminationRecordDetailData> originalTerminationRecordDetailDataList, long loginEmpId, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    validateTerminationRecordDetails(terminationRecordData, terminationRecordDetailDataList);
	    addModifyTerminationRecordDetail(terminationRecordData, terminationRecordDetailDataList, loginEmpId, session);

	    if (terminationRecordDetailDataList.get(0).getStatus().equals(TerminationRecordOfficersStatusEnum.EXTENDED.getCode()) || terminationRecordDetailDataList.get(0).getStatus().equals(TerminationRecordOfficersStatusEnum.TERMINATED.getCode())) {
		int tansactionTypeCode = terminationRecordDetailDataList.get(0).getStatus().equals(TerminationRecordOfficersStatusEnum.EXTENDED.getCode()) ? TransactionTypesEnum.STE_EXTENSTION.getCode() : TransactionTypesEnum.STE_TERMINATION.getCode();
		doOfficersTerminationIntegration(terminationRecordData, terminationRecordDetailDataList, loginEmpId, CommonService.getTransactionTypeByCodeAndClass(tansactionTypeCode, TransactionClassesEnum.TERMINATIONS.getCode()).getId(), session);
	    }
	    boolean recordClosedFlag = true;
	    for (TerminationRecordDetailData terminationRecordDetailDataItr : originalTerminationRecordDetailDataList) {
		if (!terminationRecordDetailDataItr.getStatus().equals(TerminationRecordOfficersStatusEnum.EXTENDED.getCode()) && !terminationRecordDetailDataItr.getStatus().equals(TerminationRecordOfficersStatusEnum.TERMINATED.getCode())) {
		    recordClosedFlag = false;
		    break;
		}
	    }
	    if (recordClosedFlag) {
		terminationRecordData.setStatus(TerminationRecordStatusEnum.CLOSED.getCode());
		saveTerminationRecord(terminationRecordData, loginEmpId, session);
	    }

	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    throw new BusinessException(e.getMessage(), e.getParams());
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

    public static void setCollectiveRecordsServiceTerminationDate(TerminationRecordDetailData selectedTerminationRecordDetailData, Long terminationReasonId) throws BusinessException {
	// in case there are no extensions, set serviceTerminationDate to empServiceTerminationDueDate
	try {
	    if (selectedTerminationRecordDetailData.getEmpLastExtensionEndDate() == null) {
		// in case of Officers Reaching Retirement Age
		if (terminationReasonId == TerminationReasonsEnum.OFFICERS_REACHING_RETIREMENT_AGE.getCode()) {
		    selectedTerminationRecordDetailData.setServiceTerminationDate(selectedTerminationRecordDetailData.getEmpTerminationDueDate());
		}
		// in case of Officers Completion Period current rank
		else if (terminationReasonId == TerminationReasonsEnum.OFFICERS_COMPLETION_PERIOD_CURRENT_RANK.getCode()) {
		    Date empCurrentRankEndDate;
		    // check rank to compute terminationDueDate
		    if (selectedTerminationRecordDetailData.getEmpRankId().equals(RanksEnum.MAJOR_GENERAL.getCode()))
			empCurrentRankEndDate = HijriDateService.addSubHijriMonthsDays(selectedTerminationRecordDetailData.getEmpLastPromotionDate() != null ? selectedTerminationRecordDetailData.getEmpLastPromotionDate() : selectedTerminationRecordDetailData.getEmpRecruitmentDate(), ETRConfigurationService.getTerminationsOfficersRankPeriodMajorGeneralYearsMax() * 12, 0);
		    else
			empCurrentRankEndDate = HijriDateService.addSubHijriMonthsDays(selectedTerminationRecordDetailData.getEmpLastPromotionDate() != null ? selectedTerminationRecordDetailData.getEmpLastPromotionDate() : selectedTerminationRecordDetailData.getEmpRecruitmentDate(), ETRConfigurationService.getTerminationsOfficersRankPeriodYearsMax() * 12, 0);

		    selectedTerminationRecordDetailData.setServiceTerminationDate(empCurrentRankEndDate);
		}
	    } else { // In case there are extensions for this detail
		selectedTerminationRecordDetailData.setServiceTerminationDate(selectedTerminationRecordDetailData.getEmpLastExtensionEndDate());
	    }
	} catch (BusinessException e) {
	    throw new BusinessException("error_general");
	}
    }

    public static void saveExtensionTransactionRecord(TerminationTransactionData terminationTransactionData, EmployeeData employee, long loginEmpId, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	validateTerminationExtensionRegistration(terminationTransactionData);

	// excludedInstanceId is sent as null here, because we haven't added employee to the record yet
	EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(new Long[] { terminationTransactionData.getEmpId() }, null, TransactionClassesEnum.TERMINATIONS.getCode(), false, null, terminationTransactionData.getCategoryId());

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    if (employee.getLastExtensionEndDate() != null) {
		terminationTransactionData.setExtensionStartDate(HijriDateService.addSubHijriDays(employee.getLastExtensionEndDate(), 1));
		Date extensionNewEndDate = HijriDateService.addSubHijriMonthsDays(terminationTransactionData.getExtensionStartDate(), terminationTransactionData.getExtensionPeriodMonths(), 0);
		terminationTransactionData.setExtensionEndDate(extensionNewEndDate);
		employee.setLastExtensionEndDate(extensionNewEndDate);
	    } else {
		if (employee.getServiceTerminationDueDate() != null) {
		    terminationTransactionData.setExtensionStartDate(employee.getServiceTerminationDueDate());
		    Date extensionEndDate = HijriDateService.addSubHijriMonthsDays(terminationTransactionData.getExtensionStartDate(), terminationTransactionData.getExtensionPeriodMonths(), 0);
		    terminationTransactionData.setExtensionEndDate(extensionEndDate);
		    employee.setLastExtensionEndDate(extensionEndDate);
		} else
		    throw new BusinessException("error_terminationDueDateNotFound");
	    }

	    TransactionType transactionType = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.STE_EXTENSTION.getCode(), TransactionClassesEnum.TERMINATIONS.getCode());
	    terminationTransactionData.setTransactionTypeId(transactionType.getId());
	    terminationTransactionData.setEflag(FlagsEnum.OFF.getCode());
	    terminationTransactionData.setMigFlag(FlagsEnum.OFF.getCode());
	    List<TerminationTransactionData> terminationTransactions = new ArrayList<TerminationTransactionData>();
	    terminationTransactions.add(terminationTransactionData);
	    addTerminationTransaction(terminationTransactions, loginEmpId, null, session);
	    EmployeesService.updateEmployee(employee, session);

	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    throw new BusinessException(e.getMessage(), e.getParams());
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    terminationTransactionData.setId(null);
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}

    }

    /*********************************************** Termination Prerequisite Operations ***********************************************************/

    public static void updateTerminationEmployeesDates(EmployeeData employeeData, Date terminationDueDate, long loginEmpId) throws BusinessException {

	validateTerminationPrerequisiteEmployeesDates(employeeData, terminationDueDate);

	employeeData.getEmployee().setSystemUser(loginEmpId + "");
	employeeData.setServiceTerminationDueDate(terminationDueDate);
	EmployeesService.updateEmployee(employeeData);
    }

    private static void validateTerminationPrerequisiteEmployeesDates(EmployeeData employeeData, Date terminationDueDate) throws BusinessException {

	if (employeeData == null)
	    throw new BusinessException("error_employeeDataError");

	if (terminationDueDate == null)
	    throw new BusinessException("error_terminationDueDateIsMandatory");
	if (!HijriDateService.isValidHijriDate(terminationDueDate))
	    throw new BusinessException("error_invalidTerminationDueDate");

	if (employeeData.getRecruitmentDate() == null || !terminationDueDate.after(employeeData.getRecruitmentDate()))
	    throw new BusinessException("error_terminationDueDateMustBeAfterRecuitmentDate");
    }

    /*********************************************** Database operations operations ***********************************************************/
    private static void addModifyTerminationRecord(TerminationRecordData terminationRecordData, long loginEmpId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    terminationRecordData.getTerminationRecord().setSystemUser(loginEmpId + "");
	    if (terminationRecordData.getId() != null) { // Update
		DataAccess.updateEntity(terminationRecordData.getTerminationRecord(), session);
	    } else { // Add
		DataAccess.addEntity(terminationRecordData.getTerminationRecord(), session);
		terminationRecordData.setId(terminationRecordData.getTerminationRecord().getId());
	    }

	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    terminationRecordData.setId(null);
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void addModifyTerminationRecordDetail(TerminationRecordData terminationRecordData, List<TerminationRecordDetailData> terminationRecordDetailDataList, long loginEmpId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (TerminationRecordDetailData terminationRecordDetailData : terminationRecordDetailDataList) {
		if (terminationRecordDetailData.getId() != null) { // Update
		    terminationRecordDetailData.getTerminationRecordDetail().setSystemUser(loginEmpId + "");
		    DataAccess.updateEntity(terminationRecordDetailData.getTerminationRecordDetail(), session);
		} else {
		    terminationRecordDetailData.setRecordId(terminationRecordData.getId());
		    DataAccess.addEntity(terminationRecordDetailData.getTerminationRecordDetail(), session);
		    terminationRecordDetailData.setId(terminationRecordDetailData.getTerminationRecordDetail().getId());
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

    public static void addTerminationTransaction(List<TerminationTransactionData> terminationTransactionDataList, long loginEmpId, String subject, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (TerminationTransactionData terminationTransactionData : terminationTransactionDataList) {

		if (CategoriesEnum.OFFICERS.getCode() != terminationTransactionData.getCategoryId().longValue() && subject != null) {
		    String[] etrCorInfo = ETRCorrespondence.doETRCorOut(subject, session);
		    terminationTransactionData.setDecisionNumber(etrCorInfo[0]);
		    terminationTransactionData.setDecisionDateString(etrCorInfo[1]);
		}
		terminationTransactionData.getTerminationTransaction().setSystemUser(loginEmpId + "");
		DataAccess.addEntity(terminationTransactionData.getTerminationTransaction(), session);
		terminationTransactionData.setId(terminationTransactionData.getTerminationTransaction().getId());
	    }
	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    for (TerminationTransactionData terminationTransactionData : terminationTransactionDataList) {
		terminationTransactionData.setId(null);
	    }
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    private static void deleteTerminationRecord(List<TerminationRecordDetailData> terminationRecordDetailDataList, TerminationRecordData terminationRecordData, long loginEmpId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    // 1. Delete all details
	    for (TerminationRecordDetailData terminationRecordDetailData : terminationRecordDetailDataList) {

		terminationRecordDetailData.getTerminationRecordDetail().setSystemUser(loginEmpId + "");
		DataAccess.deleteEntity(terminationRecordDetailData.getTerminationRecordDetail(), session);
	    }

	    // 2. Delete terminationRecordData
	    terminationRecordData.getTerminationRecord().setSystemUser(loginEmpId + "");
	    DataAccess.deleteEntity(terminationRecordData.getTerminationRecord(), session);

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

    public static void deleteTerminationRecordDetail(List<TerminationRecordDetailData> terminationRecordDetailDataList, TerminationRecordDetailData terminationRecordDetailData, long loginEmpId, CustomSession session) throws BusinessException {
	if (terminationRecordDetailData.getId() == null) {
	    terminationRecordDetailDataList.remove(terminationRecordDetailData);
	    return;
	}

	try {
	    terminationRecordDetailData.getTerminationRecordDetail().setSystemUser(loginEmpId + "");
	    DataAccess.deleteEntity(terminationRecordDetailData.getTerminationRecordDetail(), session);
	    terminationRecordDetailDataList.remove(terminationRecordDetailData);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static void deleteTerminationRecordAndDetails(TerminationRecordData terminationRecordData, Long mode, long loginEmpId) throws BusinessException {

	if (mode == CategoriesEnum.OFFICERS.getCode() && terminationRecordData.getTypeFlag().intValue() == TerminationTypeFlagEnum.REQUEST.getCode())
	    throw new BusinessException("error_deleteTerminationRecordDetailsTypeRequest");

	if (mode == CategoriesEnum.OFFICERS.getCode() && terminationRecordData.getStatus().equals(TerminationRecordStatusEnum.CURRENT.getCode())) {
	    Long detailCount = getCountTerminationRecordDetailStatus(terminationRecordData.getId(), new Long[] { TerminationRecordOfficersStatusEnum.UNDER_REVIEW.getCode() });
	    if (detailCount > 0)
		throw new BusinessException("error_deleteTerminationRecordDetailsCount");
	}
	List<TerminationRecordDetailData> terminationRecordDetailDataList = getTerminationRecordDetailsByRecordId(terminationRecordData.getId());

	deleteTerminationRecord(terminationRecordDetailDataList, terminationRecordData, loginEmpId);

    }

    public static void validateTerminationsPromotionsIntegration(List<EmployeeData> employees, List<Long> empsRanksIds) throws BusinessException {

	String employeesString = "", comma = "";
	String errorString = "";
	for (int i = 0; i < employees.size(); i++) {
	    // Employee Validation
	    if (!employees.get(i).getRankId().equals(empsRanksIds.get(i))) {
		errorString = "error_terminationFaildbyPromotion";
		employeesString += comma + employees.get(i).getName();
		comma = ", ";
		continue;
	    }
	}
	if (!errorString.isEmpty() && !employeesString.isEmpty())
	    throw new BusinessException(errorString, new Object[] { employeesString });
    }

    private static void doOfficersTerminationIntegration(TerminationRecordData terminationRecordData, List<TerminationRecordDetailData> terminationRecordDetailDataList, Long loginEmpId, long tansactionTypeId, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    Long[] empsIds = new Long[terminationRecordDetailDataList.size()];
	    HashMap<Long, TerminationRecordDetailData> employeesTerminationRecordDetailDataMap = new HashMap<Long, TerminationRecordDetailData>();

	    for (int i = 0; i < terminationRecordDetailDataList.size(); i++) {
		empsIds[i] = terminationRecordDetailDataList.get(i).getEmpId();
		employeesTerminationRecordDetailDataMap.put(empsIds[i], terminationRecordDetailDataList.get(i));
	    }

	    List<EmployeeData> tempEmployees = EmployeesService.getEmployeesByEmpsIds(empsIds);
	    List<Long> empsRanksIds = new ArrayList<Long>();

	    for (int i = 0; i < tempEmployees.size(); i++) {
		empsRanksIds.add(employeesTerminationRecordDetailDataMap.get(tempEmployees.get(i).getEmpId()).getEmpRankId());
	    }

	    validateTerminationsPromotionsIntegration(tempEmployees, empsRanksIds);
	    EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(empsIds, null, TransactionClassesEnum.TERMINATIONS.getCode(), false, terminationRecordData.getId() * -1, terminationRecordData.getCategoryId());

	    List<TerminationTransactionData> terminationTransactionDataList = constructTerminationTransactions(terminationRecordData, terminationRecordDetailDataList, tansactionTypeId);
	    List<TerminationTransactionData> nonFutureTerminationTransaction = new ArrayList<>();
	    for (TerminationTransactionData terminationTransactionData : terminationTransactionDataList) {
		if (terminationTransactionData.getStatus() == TerminationRecordOfficersStatusEnum.TERMINATED.getCode()) {
		    if (terminationTransactionData.getServiceTerminationDate() != null && !terminationTransactionData.getServiceTerminationDate().after(HijriDateService.getHijriSysDate()))
			nonFutureTerminationTransaction.add(terminationTransactionData);
		}
	    }

	    // save transaction
	    addTerminationTransaction(terminationTransactionDataList, loginEmpId, null, session);

	    // Termination and Employee Integration
	    if (terminationRecordDetailDataList.get(0).getStatus().equals(TerminationRecordOfficersStatusEnum.EXTENDED.getCode())) {
		tempEmployees.get(0).setLastExtensionEndDate(employeesTerminationRecordDetailDataMap.get(tempEmployees.get(0).getEmpId()).getExtensionEndDate());
		EmployeesService.updateEmployee(tempEmployees.get(0), session);
	    } else if (terminationRecordDetailDataList.get(0).getStatus().equals(TerminationRecordOfficersStatusEnum.TERMINATED.getCode())) {
		for (int i = 0; i < tempEmployees.size(); i++) {
		    tempEmployees.get(i).setServiceTerminationDate(employeesTerminationRecordDetailDataMap.get(tempEmployees.get(i).getEmpId()).getServiceTerminationDate());
		}
		terminateEmployeeService(tempEmployees, session);
		logTerminatedEmployeeData(nonFutureTerminationTransaction, session);
	    }

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    throw e;
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

    private static List<EmployeeData> getTerminationEligibleEmployees(TerminationRecordData terminationRecordData, Long rankId, Date empTerminationDueDateFrom, Date empTerminationDueDateTo) throws BusinessException {
	return searchTerminationEligibleEmployees(terminationRecordData.getCategoryId(), terminationRecordData.getRegionId(), rankId, empTerminationDueDateFrom, empTerminationDueDateTo);
    }

    private static List<EmployeeData> getTerminationEligibleEmployeesCompletedPeriodCurrentRank(TerminationRecordData terminationRecordData, Long rankId, Date empTerminationDueDateFrom, Date empTerminationDueDateTo) throws BusinessException {
	return searchTerminationEligibleEmployeesCompletedPeriodCurrentRank(terminationRecordData.getRegionId(), rankId, empTerminationDueDateFrom, empTerminationDueDateTo);
    }

    public static List<TerminationRecordDetailData> getTerminationRecordDetailsByRecordId(Long recordId) throws BusinessException {
	return searchTerminationRecordsDetails(recordId, null, (long) FlagsEnum.ALL.getCode());
    }

    public static List<TerminationTransactionData> getTerminationTransactions(Long empId, String decisionNumber, Date fromDate, Date toDate, Long[] categoriesIds) throws BusinessException {

	List<TerminationTransactionData> terminationTransactions = searchTerminationTransactions(null, empId, null, decisionNumber, fromDate, toDate, categoriesIds);

	if (terminationTransactions.size() > 0)
	    return terminationTransactions;

	return null;
    }

    public static TerminationTransactionData getTerminationTransactionById(Long transactionId) throws BusinessException {
	return searchTerminationTransactionsById(transactionId);
    }

    public static List<TerminationTransaction> getEmployeeTerminationTransactions(long empId, Date serviceTerminationDateFrom, Date serviceTerminationDateTo) throws BusinessException {
	return searchEmployeeTerminationTransactions(empId, serviceTerminationDateFrom, serviceTerminationDateTo);
    }

    private static List<TerminationTransaction> searchEmployeeTerminationTransactions(long empId, Date serviceTerminationDateFrom, Date serviceTerminationDateTo) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_TERMINATION_TYPE_CODE", TransactionTypesEnum.STE_TERMINATION.getCode());
	    qParams.put("P_CANCELATION_TYPE_CODE", TransactionTypesEnum.STE_TERMINATION_CANCELLATION.getCode());
	    qParams.put("P_FROM_DATE", HijriDateService.getHijriDateString(serviceTerminationDateFrom));
	    qParams.put("P_TO_DATE", HijriDateService.getHijriDateString(serviceTerminationDateTo));
	    return DataAccess.executeNamedQuery(TerminationTransaction.class, QueryNamesEnum.HCM_SEARCH_EMPLOYEE_TERMINATION_TRANSACTIONS.getCode(), qParams);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static TerminationTransactionData getTerminationTransactionByRecordDetailId(Long recordDetailId) throws BusinessException {

	List<TerminationTransactionData> terminationTransactions = searchTerminationTransactions(recordDetailId, null, null, null, null, null, null);
	if (terminationTransactions.size() > 0)
	    return terminationTransactions.get(0);

	return null;
    }

    private static TerminationTransaction getTerminationTransactionsByCancelTransactionId(Long cancelTransactionId) throws BusinessException {
	List<TerminationTransaction> terminationTransactionList = searchTerminationTransactionsByCancelTransactionId(cancelTransactionId);
	if (terminationTransactionList == null || terminationTransactionList.isEmpty())
	    return null;

	return terminationTransactionList.get(0);
    }

    public static List<TerminationTransactionData> getTerminationExtensionTransactionsByEmpId(Long empId) throws BusinessException {
	return searchTerminationTransactions(null, empId, TransactionTypesEnum.STE_EXTENSTION.getCode(), null, null, null, null);
    }

    public static List<TerminationTransactionData> getTerminationsHistory(Long empId) throws BusinessException {
	List<TerminationTransactionData> terminationTransactions = searchTerminationTransactions(null, empId, null, null, null, null, null);

	if (terminationTransactions.size() > 0)
	    return terminationTransactions;

	return null;
    }

    private static Integer generateNextRecordNumber(Long[] categoriesId) throws BusinessException {

	int currentYear = HijriDateService.getHijriDateYear(HijriDateService.getHijriSysDate());
	Integer recordNumber = getLastRecordNumberForCategoryAndYear(categoriesId, currentYear) + 1;

	return recordNumber;
    }

    /************************************** Conflict validator **************************************/

    public static List<TerminationRecordDetail> getRunningRecords(Long[] empsIds, Long excludedRecordId) throws BusinessException {
	try {
	    if (empsIds == null || empsIds.length == 0)
		return new ArrayList<TerminationRecordDetail>();

	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_EXCLUDED_RECORD_ID", (excludedRecordId == null || excludedRecordId.longValue() > 0) ? FlagsEnum.ALL.getCode() : excludedRecordId.longValue() * -1);
	    qParams.put("P_EMPS_IDS", empsIds);

	    return DataAccess.executeNamedQuery(TerminationRecordDetail.class, QueryNamesEnum.HCM_GET_RUNNING_TERMINATION_RECORDS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*********************************************** Terminations Validation ***********************************************************/

    public static void validateTerminationRecordDetails(TerminationRecordData terminationRecordData, List<TerminationRecordDetailData> terminationRecordDetailDataList) throws BusinessException {

	// validate business rules according to category
	if (CategoriesEnum.OFFICERS.getCode() == terminationRecordData.getCategoryId().longValue())
	    validateTerminationOfficersRecordDetails(terminationRecordData, terminationRecordDetailDataList);
	else if (CategoriesEnum.SOLDIERS.getCode() == terminationRecordData.getCategoryId().longValue())
	    validateTerminationSoldiersRecordDetails(terminationRecordData, terminationRecordDetailDataList);
	else
	    validateTerminationCiviliansRecordDetails(terminationRecordData, terminationRecordDetailDataList);
    }

    private static void validateTerminationOfficersRecordDetails(TerminationRecordData terminationRecordData, List<TerminationRecordDetailData> terminationRecordDetailDataList) throws BusinessException {

	String employeesString = "", comma = "";
	String errorString = "";
	String extraErrorParameterString = "";

	// validate all required fields first
	long reasonId = terminationRecordData.getReasonId();

	for (TerminationRecordDetailData terminationRecordDetailDataItr : terminationRecordDetailDataList) {

	    if (terminationRecordDetailDataItr.getEmpId() == null)
		throw new BusinessException("error_transactionDataError");

	    // Employee Validation
	    if ((errorString.isEmpty() || errorString.equals("error_employeeOfficialUnitNotFound")) && terminationRecordDetailDataItr.getEmpOfficialUnitFullName() == null) {
		errorString = "error_employeeOfficialUnitNotFound";
		employeesString += comma + terminationRecordDetailDataItr.getEmpName();
		comma = ", ";
		continue;
	    }

	    // General validation for all Termination Reasons of Officers
	    if (terminationRecordDetailDataItr.getStatus().longValue() != TerminationRecordOfficersStatusEnum.UNDER_REVIEW.getCode()) {
		if ((errorString.isEmpty() || errorString.equals("error_noJudjmentIsMandatoryOfficers")) && terminationRecordDetailDataItr.getJudgmentFlagBoolean() != null && terminationRecordDetailDataItr.getJudgmentFlagBoolean()) {
		    errorString = "error_noJudjmentIsMandatoryOfficers";
		    employeesString += comma + terminationRecordDetailDataItr.getEmpName();
		    comma = ", ";
		    continue;
		}
	    }

	    if (terminationRecordDetailDataItr.getStatus().longValue() == TerminationRecordOfficersStatusEnum.MINISTRY_SENT.getCode()) {
		if ((errorString.isEmpty() || errorString.equals("error_ministryRequestNumberIsMandatory")) && terminationRecordDetailDataItr.getMinistryRequestNumber() == null || terminationRecordDetailDataItr.getMinistryRequestNumber().trim().isEmpty()) {
		    errorString = "error_ministryRequestNumberIsMandatory";
		    employeesString += comma + terminationRecordDetailDataItr.getEmpName();
		    comma = ", ";
		    continue;
		}

		if ((errorString.isEmpty() || errorString.equals("error_ministryRequestDateIsMandatory")) && terminationRecordDetailDataItr.getMinistryRequestDate() == null) {
		    errorString = "error_ministryRequestDateIsMandatory";
		    employeesString += comma + terminationRecordDetailDataItr.getEmpName();
		    comma = ", ";
		    continue;
		}

		if ((errorString.isEmpty() || errorString.equals("error_invalidMinistryRequestDate")) && !HijriDateService.isValidHijriDate(terminationRecordDetailDataItr.getMinistryRequestDate())) {
		    errorString = "error_invalidMinistryRequestDate";
		    employeesString += comma + terminationRecordDetailDataItr.getEmpName();
		    comma = ", ";
		    continue;
		}

		if ((errorString.isEmpty() || errorString.equals("error_ministryRequestDateViolation")) && terminationRecordDetailDataItr.getMinistryRequestDate() != null && terminationRecordDetailDataItr.getMinistryRequestDate().after(HijriDateService.getHijriSysDate())) {
		    errorString = "error_ministryRequestDateViolation";
		    employeesString += comma + terminationRecordDetailDataItr.getEmpName();
		    comma = ", ";
		    continue;
		}
	    } else if (terminationRecordDetailDataItr.getStatus().longValue() == TerminationRecordOfficersStatusEnum.TERMINATED.getCode() || terminationRecordDetailDataItr.getStatus().longValue() == TerminationRecordOfficersStatusEnum.EXTENDED.getCode()) {
		if (errorString.isEmpty() && (terminationRecordDetailDataItr.getDecisionNumber() == null || terminationRecordDetailDataItr.getDecisionNumber().trim().isEmpty()))
		    throw new BusinessException("error_decNumberMandatory");

		if (errorString.isEmpty() && terminationRecordDetailDataItr.getDecisionDate() == null)
		    throw new BusinessException("error_decDateMandatory");

		if (errorString.isEmpty() && !HijriDateService.isValidHijriDate(terminationRecordDetailDataItr.getDecisionDate()))
		    throw new BusinessException("error_invalidDecisionDate");

		if (errorString.isEmpty() && terminationRecordDetailDataItr.getDecisionDate() != null && terminationRecordDetailDataItr.getDecisionDate().after(HijriDateService.getHijriSysDate()))
		    throw new BusinessException("error_decDateViolation");

		if ((errorString.isEmpty() || errorString.equals("error_decisionDateLessThanMinistryRequestDate")) && terminationRecordDetailDataItr.getMinistryRequestDate() != null && terminationRecordDetailDataItr.getDecisionDate().before(terminationRecordDetailDataItr.getMinistryRequestDate())) {
		    errorString = "error_decisionDateLessThanMinistryRequestDate";
		    employeesString += comma + terminationRecordDetailDataItr.getEmpName();
		    comma = ", ";
		    continue;
		}
	    }

	    if (terminationRecordDetailDataItr.getStatus().longValue() == TerminationRecordOfficersStatusEnum.TERMINATED.getCode()) {
		if ((errorString.isEmpty() || errorString.equals("error_serviceTerminationDateMandatory")) && terminationRecordDetailDataItr.getServiceTerminationDate() == null) {
		    errorString = "error_serviceTerminationDateMandatory";
		    employeesString += comma + terminationRecordDetailDataItr.getEmpName();
		    comma = ", ";
		    continue;
		}
		if ((errorString.isEmpty() || errorString.equals("error_invalidServiceTerminationDate")) && !HijriDateService.isValidHijriDate(terminationRecordDetailDataItr.getServiceTerminationDate())) {
		    errorString = "error_invalidServiceTerminationDate";
		    employeesString += comma + terminationRecordDetailDataItr.getEmpName();
		    comma = ", ";
		    continue;
		}
	    }

	    if (reasonId == TerminationReasonsEnum.OFFICERS_REACHING_RETIREMENT_AGE.getCode() || reasonId == TerminationReasonsEnum.OFFICERS_COMPLETION_PERIOD_CURRENT_RANK.getCode()) {
		if (terminationRecordDetailDataItr.getStatus().longValue() == TerminationRecordOfficersStatusEnum.EXTENDED.getCode()) {
		    if ((errorString.isEmpty() || errorString.equals("error_extensionDateMandatory")) && terminationRecordDetailDataItr.getExtensionEndDate() == null) {
			errorString = "error_extensionDateMandatory";
			employeesString += comma + terminationRecordDetailDataItr.getEmpName();
			comma = ", ";
			continue;
		    }

		    if ((errorString.isEmpty() || errorString.equals("error_invalidExtensionEndDate")) && !HijriDateService.isValidHijriDate(terminationRecordDetailDataItr.getExtensionEndDate())) {
			errorString = "error_invalidExtensionEndDate";
			employeesString += comma + terminationRecordDetailDataItr.getEmpName();
			comma = ", ";
			continue;
		    }

		    float extensionPeriod = terminationRecordDetailDataItr.getExtensionPeriodMonths() * HIJRI_MONTH + terminationRecordDetailDataItr.getExtensionPeriodDays();
		    if ((errorString.isEmpty() || errorString.equals("error_serviceExtensionPeriodMaxOfficers")) && (extensionPeriod <= 0 || extensionPeriod > ETRConfigurationService.getTerminationsOfficersExtensionPeriodMonthsMax() * HIJRI_MONTH)) {
			errorString = "error_serviceExtensionPeriodMaxOfficers";
			employeesString += comma + terminationRecordDetailDataItr.getEmpName();
			extraErrorParameterString = ETRConfigurationService.getTerminationsOfficersExtensionPeriodMonthsMax() + "";
			comma = ", ";
			continue;
		    }
		}
	    }
	}

	// error_steCollective
	if (!errorString.isEmpty() && !employeesString.isEmpty()) {
	    if (errorString.equals("error_employeeOfficialUnitNotFound") || errorString.equals("error_noJudjmentIsMandatoryOfficers"))
		throw new BusinessException(errorString, new Object[] { employeesString });
	    else {
		if (terminationRecordDetailDataList.size() == 1)
		    throw new BusinessException(errorString, extraErrorParameterString.isEmpty() ? null : new Object[] { extraErrorParameterString });
		else
		    throw new BusinessException("error_steCollective", new Object[] { extraErrorParameterString.isEmpty() ? getMessage(errorString) : getParameterizedMessage(errorString, extraErrorParameterString), employeesString });
	    }
	}

	TerminationRecordDetailData terminationRecodDetailDataItr = terminationRecordDetailDataList.get(0);
	if (reasonId != TerminationReasonsEnum.OFFICERS_REACHING_RETIREMENT_AGE.getCode() && reasonId != TerminationReasonsEnum.OFFICERS_COMPLETION_PERIOD_CURRENT_RANK.getCode()) {
	    if (reasonId == TerminationReasonsEnum.OFFICERS_TERMINATION_REQUEST.getCode()) {

		if (terminationRecordData.getTypeFlag().intValue() == TerminationTypeFlagEnum.RECORD.getCode() && terminationRecodDetailDataItr.getDesiredTerminationDate() == null)
		    throw new BusinessException("error_desiredTerminationDateIsMandatory");

		if (terminationRecordData.getTypeFlag().intValue() == TerminationTypeFlagEnum.RECORD.getCode() && !HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getDesiredTerminationDate()))
		    throw new BusinessException("error_invalidDesiredTerminationDate");

		if (terminationRecodDetailDataItr.getDesiredTerminationDate() != null && !checkDatesYears(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getDesiredTerminationDate(), ETRConfigurationService.getTerminationsOfficersRequestDesiredTerminationDateRecordDateDiffYearsMax(), ETRConfigurationService.getTerminationsOfficersRequestDesiredTerminationDateRecordDateDiffYearsMin() * -1))
		    throw new BusinessException("error_desiredTerminationDateAndRecordDateMaxPeriodOfficers", new Object[] { ETRConfigurationService.getTerminationsOfficersRequestDesiredTerminationDateRecordDateDiffYearsMin(), ETRConfigurationService.getTerminationsOfficersRequestDesiredTerminationDateRecordDateDiffYearsMax() });

		if (terminationRecodDetailDataItr.getStatus().longValue() == TerminationRecordOfficersStatusEnum.TERMINATED.getCode()) {
		    if (terminationRecodDetailDataItr.getDesiredTerminationDate() != null && terminationRecodDetailDataItr.getServiceTerminationDate() != null && terminationRecodDetailDataItr.getServiceTerminationDate().before(terminationRecodDetailDataItr.getDesiredTerminationDate())
			    || !checkDatesYears(terminationRecodDetailDataItr.getDesiredTerminationDate(), terminationRecodDetailDataItr.getServiceTerminationDate(), ETRConfigurationService.getTerminationsOfficersRequestDesiredAndServiceTerminationDateValidYearsMax(), null))
			throw new BusinessException("error_serviceTerminationDateMaxPeriodOfficers", new Object[] { ETRConfigurationService.getTerminationsOfficersRequestDesiredAndServiceTerminationDateValidYearsMax() });

		} else if (terminationRecodDetailDataItr.getStatus().longValue() == TerminationRecordOfficersStatusEnum.REJECTED.getCode()) {
		    if (terminationRecodDetailDataItr.getMinistryRejectionNumber() == null || terminationRecodDetailDataItr.getMinistryRejectionNumber().trim().isEmpty())
			throw new BusinessException("error_ministryRejectionNumberIsMandatory");

		    if (terminationRecodDetailDataItr.getMinistryRejectionDate() == null)
			throw new BusinessException("error_ministryRejectionDateIsMandatory");

		    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getMinistryRejectionDate()))
			throw new BusinessException("error_invalidMinistryRejectionDate");

		    if (terminationRecodDetailDataItr.getMinistryRejectionDate() != null && terminationRecodDetailDataItr.getMinistryRejectionDate().after(HijriDateService.getHijriSysDate()))
			throw new BusinessException("error_ministryRejectionDateViolation");
		}
	    } else {// Other reasons
		if (terminationRecodDetailDataItr.getStatus().longValue() == TerminationRecordOfficersStatusEnum.TERMINATED.getCode()) {
		    if (!checkDatesYears(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getServiceTerminationDate(), ETRConfigurationService.getTerminationsOfficersServiceTerminationDateRecordDateDiffYearsMax(), ETRConfigurationService.getTerminationsOfficersServiceTerminationDateRecordDateDiffYearsMin() * -1))
			throw new BusinessException("error_serviceTerminationDateMin", new Object[] { ETRConfigurationService.getTerminationsOfficersServiceTerminationDateRecordDateDiffYearsMin(), ETRConfigurationService.getTerminationsOfficersServiceTerminationDateRecordDateDiffYearsMax() });
		}
	    }
	}
    }

    private static void validateTerminationSoldiersRecordDetails(TerminationRecordData terminationRecordData, List<TerminationRecordDetailData> terminationRecordDetailDataList) throws BusinessException {

	String employeesString = "", comma = "";
	String errorString = "";

	long reasonId = terminationRecordData.getReasonId();

	for (TerminationRecordDetailData terminationRecodDetailDataItr : terminationRecordDetailDataList) {

	    if (terminationRecodDetailDataItr.getEmpId() == null)
		throw new BusinessException("error_transactionDataError");

	    // Employee Validation
	    if ((errorString.isEmpty() || errorString.equals("error_employeeOfficialUnitNotFound")) && (terminationRecodDetailDataItr.getEmpOfficialUnitFullName() == null || terminationRecodDetailDataItr.getEmpOfficialUnitFullName().length() == 0)) {
		errorString = "error_employeeOfficialUnitNotFound";
		employeesString += comma + terminationRecodDetailDataItr.getEmpName();
		comma = ", ";
		continue;
	    }
	}

	if (!errorString.isEmpty() && !employeesString.isEmpty())
	    throw new BusinessException(errorString, new Object[] { employeesString });

	TerminationRecordDetailData terminationRecodDetailDataItr = terminationRecordDetailDataList.get(0);

	if (reasonId == TerminationReasonsEnum.SOLDIERS_TERMINATION_REQUEST.getCode()) {
	    if (terminationRecodDetailDataItr.getJudgmentFlagBoolean() == null || terminationRecodDetailDataItr.getJudgmentFlagBoolean() == false)
		throw new BusinessException("error_noJudjmentIsMandatorySoldiers");

	    if (terminationRecodDetailDataItr.getSpecializationPeriodFlagBoolean() == null || terminationRecodDetailDataItr.getSpecializationPeriodFlagBoolean() == false)
		throw new BusinessException("error_specializationPeriodFlagBooleanIsMandatory");

	    if (terminationRecodDetailDataItr.getDesiredTerminationDate() == null)
		throw new BusinessException("error_desiredTerminationDateIsMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getDesiredTerminationDate()))
		throw new BusinessException("error_invalidDesiredTerminationDate");

	    if (terminationRecodDetailDataItr.getDesiredTerminationDate().before(HijriDateService.getHijriSysDate()))
		throw new BusinessException("error_desiredTerminationDateBeforeToday");

	    if (terminationRecodDetailDataItr.getEmpRecruitmentDate() == null)
		throw new BusinessException("error_recruitmentDateIsMandatory");

	    if (!checkDatesYears(terminationRecodDetailDataItr.getEmpRecruitmentDate(), terminationRecodDetailDataItr.getDesiredTerminationDate(), null, ETRConfigurationService.getTerminationsSoldierServicePeriodBeforeTerminationYearsMin()))
		throw new BusinessException("error_soldierServicePeriodBeforeTerminationYearsMin", new Object[] { ETRConfigurationService.getTerminationsSoldierServicePeriodBeforeTerminationYearsMin() });

	    if (!checkDatesDays(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getDesiredTerminationDate(), ETRConfigurationService.getTerminationsSoldiersPeriodUntilDesiredTerminationDateDaysMax(), ETRConfigurationService.getTerminationsSoldiersPeriodUntilDesiredTerminationDateDaysMin()))
		throw new BusinessException("error_desiredTerminationDatePeriodForSoldiers", new Object[] { ETRConfigurationService.getTerminationsSoldiersPeriodUntilDesiredTerminationDateDaysMin(), ETRConfigurationService.getTerminationsSoldiersPeriodUntilDesiredTerminationDateDaysMax() });

	} else if (reasonId == TerminationReasonsEnum.SOLDIERS_DISPENSING_SERVICES.getCode()) {
	    if ((terminationRecodDetailDataItr.getReferring() == null || terminationRecodDetailDataItr.getReferring().trim().isEmpty()) && terminationRecodDetailDataItr.getForeignerMarriageFlagBoolean() != null && terminationRecodDetailDataItr.getForeignerMarriageFlagBoolean() == true)
		throw new BusinessException("error_refferingIsMandatory");

	} else if (reasonId == TerminationReasonsEnum.SOLDIERS_DISMISS.getCode()) {
	    if (terminationRecodDetailDataItr.getDismissType() == null)
		throw new BusinessException("error_dismissType");

	    if (terminationRecodDetailDataItr.getDismissDecisionNumber() == null || terminationRecodDetailDataItr.getDismissDecisionNumber().trim().isEmpty())
		throw new BusinessException("error_dismissDecisionNum");

	    if (terminationRecodDetailDataItr.getDismissDecisionDate() == null)
		throw new BusinessException("error_dismissDecisionDateIsMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getDismissDecisionDate()))
		throw new BusinessException("error_invalidDismissDecisionDate");

	    if (terminationRecodDetailDataItr.getDismissStatus() == null)
		throw new BusinessException("error_dismissDecisionStatus");

	    if (terminationRecodDetailDataItr.getDismissResumptionNumber() == null || terminationRecodDetailDataItr.getDismissResumptionNumber().trim().isEmpty())
		throw new BusinessException("error_dismissResumptionNumber");

	    if (terminationRecodDetailDataItr.getDismissResumptionYear() == null || terminationRecodDetailDataItr.getDismissResumptionYear().toString().length() < 4)
		throw new BusinessException("error_dismissForYear");

	    if (terminationRecodDetailDataItr.getDismissApprovalNumber() == null || terminationRecodDetailDataItr.getDismissApprovalNumber().trim().isEmpty())
		throw new BusinessException("error_dismissApprovalNumber");

	    if (terminationRecodDetailDataItr.getDismissApprovalDate() == null)
		throw new BusinessException("error_dismissApprovalDateIsMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getDismissApprovalDate()))
		throw new BusinessException("error_invalidDismissApprovalDate");

	    if (terminationRecodDetailDataItr.getDismissBasedOn() == null || terminationRecodDetailDataItr.getDismissBasedOn().intValue() == FlagsEnum.ALL.getCode())
		throw new BusinessException("error_dismissBasedOn");

	    if (!checkDatesYears(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getDismissDecisionDate(), ETRConfigurationService.getTerminationsSoldiersDismissDateYearsMax(), ETRConfigurationService.getTerminationsSoldiersDismissDateYearsMin() * -1))
		throw new BusinessException("error_dismissDecisionDatePeriodForSoldiers", new Object[] { ETRConfigurationService.getTerminationsSoldiersDismissDateYearsMin(), ETRConfigurationService.getTerminationsSoldiersDismissDateYearsMax() });

	    int resumptionDateYears = terminationRecodDetailDataItr.getDismissResumptionYear() - HijriDateService.getHijriDateYear(terminationRecordData.getRecordDate());
	    if (resumptionDateYears > ETRConfigurationService.getTerminationsSoldiersDismissDateYearsMax() || resumptionDateYears < ETRConfigurationService.getTerminationsSoldiersDismissDateYearsMin() * -1)
		throw new BusinessException("error_dismissResumptionDatePeriodForSoldiers", new Object[] { ETRConfigurationService.getTerminationsSoldiersDismissDateYearsMin(), ETRConfigurationService.getTerminationsSoldiersDismissDateYearsMax() });

	    if (!checkDatesYears(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getDismissApprovalDate(), ETRConfigurationService.getTerminationsSoldiersDismissDateYearsMax(), ETRConfigurationService.getTerminationsSoldiersDismissDateYearsMin() * -1))
		throw new BusinessException("error_dismissApprovalDatePeriodForSoldiers", new Object[] { ETRConfigurationService.getTerminationsSoldiersDismissDateYearsMin(), ETRConfigurationService.getTerminationsSoldiersDismissDateYearsMax() });

	    if ((terminationRecodDetailDataItr.getDismissApprovalDate().before(terminationRecodDetailDataItr.getDismissDecisionDate()) || (HijriDateService.getHijriDateYear(terminationRecodDetailDataItr.getDismissDecisionDate()) > terminationRecodDetailDataItr.getDismissResumptionYear()) || (HijriDateService.getHijriDateYear(terminationRecodDetailDataItr.getDismissApprovalDate()) < terminationRecodDetailDataItr.getDismissResumptionYear())))
		throw new BusinessException("error_dismissDatesOrderPeriodForSoldiers");

	} else if (reasonId == TerminationReasonsEnum.SOLDIERS_DEATH.getCode()) {
	    if (terminationRecodDetailDataItr.getReferring() == null || terminationRecodDetailDataItr.getReferring().trim().isEmpty())
		throw new BusinessException("error_deathRefferingTo");

	    if (terminationRecodDetailDataItr.getDeathCertNumber() == null || terminationRecodDetailDataItr.getDeathCertNumber().trim().isEmpty())
		throw new BusinessException("error_deathCertNumber");

	    if (terminationRecodDetailDataItr.getDeathCertDate() == null)
		throw new BusinessException("error_deathCertDateIsMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getDeathCertDate()))
		throw new BusinessException("error_invalidDeathCertDate");

	    if (terminationRecodDetailDataItr.getDeathDate() == null)
		throw new BusinessException("error_deathDateIsMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getDeathDate()))
		throw new BusinessException("error_invalidDeathDate");

	    if (terminationRecodDetailDataItr.getServiceTerminationDate() == null)
		throw new BusinessException("error_serviceTerminationDateMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getServiceTerminationDate()))
		throw new BusinessException("error_invalidServiceTerminationDate");

	    if (terminationRecodDetailDataItr.getDeathCertIssuePlace() == null || terminationRecodDetailDataItr.getDeathCertIssuePlace().trim().isEmpty())
		throw new BusinessException("error_deathIssuedForm");

	    if (terminationRecodDetailDataItr.getDeathInDutyFlag() == null)
		throw new BusinessException("error_deathInOperationflag");

	    if (terminationRecodDetailDataItr.getDeathInDutyFlag() != null && terminationRecodDetailDataItr.getDeathInDutyFlag() == FlagsEnum.ON.getCode() && terminationRecodDetailDataItr.getDeathType() == null)
		throw new BusinessException("error_deathReason");

	    if (terminationRecodDetailDataItr.getDeathInDutyFlag() != null && terminationRecodDetailDataItr.getDeathInDutyFlag() == FlagsEnum.ON.getCode() && terminationRecodDetailDataItr.getDeathType().intValue() == TerminationDeathTypeEnum.MARTYRDOM.getCode() && (terminationRecodDetailDataItr.getDeathInOperations() == null || terminationRecodDetailDataItr.getDeathInOperations().trim().isEmpty()))
		throw new BusinessException("error_deathInOperation");

	    if (!checkDatesYears(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getDeathDate(), ETRConfigurationService.getTerminationsSoldiersTerminationDeathDateYearsMax(), ETRConfigurationService.getTerminationsSoldiersTerminationDeathDateYearsMin() * -1))
		throw new BusinessException("error_deathDateForSoldiers", new Object[] { ETRConfigurationService.getTerminationsSoldiersTerminationDeathDateYearsMin() });

	    if (!checkDatesYears(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getServiceTerminationDate(), ETRConfigurationService.getTerminationsSoldiersTerminationDeathDateYearsMax(), ETRConfigurationService.getTerminationsSoldiersTerminationDeathDateYearsMin() * -1))
		throw new BusinessException("error_deathServiceTerminationDateForSoldiers", new Object[] { ETRConfigurationService.getTerminationsSoldiersTerminationDeathDateYearsMin() });

	    if (!checkDatesYears(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getDeathCertDate(), ETRConfigurationService.getTerminationsSoldiersTerminationDeathDateYearsMax(), ETRConfigurationService.getTerminationsSoldiersTerminationDeathDateYearsMin() * -1))
		throw new BusinessException("error_deathCertDateForSoldiers", new Object[] { ETRConfigurationService.getTerminationsSoldiersTerminationDeathDateYearsMin() });

	    if (((terminationRecodDetailDataItr.getServiceTerminationDate().before(terminationRecodDetailDataItr.getDeathDate())) || (terminationRecodDetailDataItr.getDeathCertDate().before(terminationRecodDetailDataItr.getServiceTerminationDate())) || (terminationRecodDetailDataItr.getDeathCertDate().before(terminationRecodDetailDataItr.getDeathDate()))))
		throw new BusinessException("error_deathDatesOrderForSoldiers");

	} else if (reasonId == TerminationReasonsEnum.SOLDIERS_ABSENCE.getCode()) {
	    if (terminationRecodDetailDataItr.getReferring() == null || terminationRecodDetailDataItr.getReferring().trim().isEmpty())
		throw new BusinessException("error_refferingToIsMandatory");

	    if (terminationRecodDetailDataItr.getAbsenceType() == null)
		throw new BusinessException("error_absenceType");

	    if (terminationRecodDetailDataItr.getAbsenceStartDate() == null)
		throw new BusinessException("error_absenceStartDateIsMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getAbsenceStartDate()))
		throw new BusinessException("error_invalidAbsenceStartDate");

	    if ((terminationRecodDetailDataItr.getAbsenceType().intValue() == TerminationAbsenceTypeEnum.CONTINUOUS_DAYS.getCode() || terminationRecodDetailDataItr.getAbsenceType().longValue() == TerminationAbsenceTypeEnum.DISCONTINUOUS_DAYS.getCode()) && terminationRecodDetailDataItr.getAbsencePeriodDays() == null)
		throw new BusinessException("error_absencePeriodDaysId");

	    if (terminationRecodDetailDataItr.getAbsenceType().intValue() == TerminationAbsenceTypeEnum.ATTEND_WITHOUT_EXCUSE.getCode()) {
		if (terminationRecodDetailDataItr.getAbsenceReturnDate() == null)
		    throw new BusinessException("error_absenceAttendedDateIsMandatory");

		if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getAbsenceReturnDate()))
		    throw new BusinessException("error_invalidAbsenceAttendedDate");
	    }

	    if (terminationRecodDetailDataItr.getServiceTerminationDate() == null)
		throw new BusinessException("error_serviceTerminationDateMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getServiceTerminationDate()))
		throw new BusinessException("error_invalidServiceTerminationDate");

	    if (terminationRecodDetailDataItr.getAbsencePeriodDays() != null) {
		if (terminationRecodDetailDataItr.getAbsenceType().intValue() == TerminationAbsenceTypeEnum.CONTINUOUS_DAYS.getCode() && terminationRecodDetailDataItr.getAbsencePeriodDays() < ETRConfigurationService.getTerminationsSoldiersAbsenceConnectedPeriodDaysMin())
		    throw new BusinessException("error_absenceConnectedDaysOrderForSoldiers", new Object[] { ETRConfigurationService.getTerminationsSoldiersAbsenceConnectedPeriodDaysMin() });
		if (terminationRecodDetailDataItr.getAbsenceType().intValue() == TerminationAbsenceTypeEnum.DISCONTINUOUS_DAYS.getCode() && terminationRecodDetailDataItr.getAbsencePeriodDays() < ETRConfigurationService.getTerminationsSoldiersAbsenceDisconnectedPeriodDaysMin())
		    throw new BusinessException("error_absenceDisconnectedDaysOrderForSoldiers", new Object[] { ETRConfigurationService.getTerminationsSoldiersAbsenceDisconnectedPeriodDaysMin() });
	    }

	    if (!checkDatesYears(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getAbsenceStartDate(), ETRConfigurationService.getTerminationsSoldiersAbsenceStartYearsMax(), ETRConfigurationService.getTerminationsSoldiersAbsenceStartYearsMin() * -1))
		throw new BusinessException("error_absenceStartDateForSoldiers", new Object[] { ETRConfigurationService.getTerminationsSoldiersAbsenceStartYearsMin() });

	    if (terminationRecodDetailDataItr.getAbsencePeriodDays() != null && Math.abs(HijriDateService.hijriDateDiff(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getAbsenceStartDate())) < terminationRecodDetailDataItr.getAbsencePeriodDays())
		throw new BusinessException("error_absencePeriodDays");

	    if ((terminationRecodDetailDataItr.getServiceTerminationDate().before(terminationRecodDetailDataItr.getAbsenceStartDate()) || !checkDatesDays(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getServiceTerminationDate(), ETRConfigurationService.getTerminationsSoldiersAbsenceServiceTerminationDaysMax(), null)))
		throw new BusinessException("error_absenceServiceTerminationDateForSoldiers", new Object[] { ETRConfigurationService.getTerminationsSoldiersAbsenceServiceTerminationDaysMax() });

	    if (terminationRecodDetailDataItr.getAbsenceType().intValue() == TerminationAbsenceTypeEnum.ATTEND_WITHOUT_EXCUSE.getCode() && (terminationRecordData.getRecordDate().before(terminationRecodDetailDataItr.getAbsenceReturnDate()) || !terminationRecodDetailDataItr.getAbsenceReturnDate().after(terminationRecodDetailDataItr.getAbsenceStartDate())))
		throw new BusinessException("error_absenceDateOrderForSoldiers");

	} else if (reasonId == TerminationReasonsEnum.SOLDIERS_LOSS.getCode()) {
	    if (terminationRecodDetailDataItr.getReferring() == null || terminationRecodDetailDataItr.getReferring().trim().isEmpty())
		throw new BusinessException("error_refferingToIsMandatory");

	    if (terminationRecodDetailDataItr.getServiceTerminationDate() == null)
		throw new BusinessException("error_serviceTerminationDateMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getServiceTerminationDate()))
		throw new BusinessException("error_invalidServiceTerminationDate");

	    if (!checkDatesYears(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getServiceTerminationDate(), ETRConfigurationService.getTerminationsSoldiersLossServiceTerminationYearsMax(), ETRConfigurationService.getTerminationsSoldiersLossServiceTerminationYearsMin() * -1))
		throw new BusinessException("error_lossServiceTerminationDateForSoldiers", new Object[] { ETRConfigurationService.getTerminationsSoldiersLossServiceTerminationYearsMin() });

	} else if (reasonId == TerminationReasonsEnum.SOLDIERS_LACK_MEDICAL_FITNESS.getCode()) {
	    if (terminationRecodDetailDataItr.getReferring() == null || terminationRecodDetailDataItr.getReferring().trim().isEmpty())
		throw new BusinessException("error_refferingToIsMandatory");

	    if (terminationRecodDetailDataItr.getServiceTerminationDate() == null)
		throw new BusinessException("error_serviceTerminationDateMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getServiceTerminationDate()))
		throw new BusinessException("error_invalidServiceTerminationDate");

	    if (!checkDatesYears(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getServiceTerminationDate(), ETRConfigurationService.getTerminationsSoldiersLackMedicalServiceTerminationYearsMax(), ETRConfigurationService.getTerminationsSoldiersLackMedicalServiceTerminationYearsMin() * -1))
		throw new BusinessException("error_medicalServiceTeminationDateForSoldiers", new Object[] { ETRConfigurationService.getTerminationsSoldiersLackMedicalServiceTerminationYearsMax(), ETRConfigurationService.getTerminationsSoldiersLackMedicalServiceTerminationYearsMin() });
	    if (terminationRecodDetailDataItr.getInjuryInDutyFlag() != null && terminationRecodDetailDataItr.getInjuryInDutyFlag() == FlagsEnum.ON.getCode() && terminationRecodDetailDataItr.getInjuryType() == null)
		throw new BusinessException("error_injuryReason");
	    if (terminationRecodDetailDataItr.getInjuryInDutyFlag() == null)
		throw new BusinessException("error_injuryInOperationflag");

	} else if (reasonId == TerminationReasonsEnum.SOLDIERS_FOREIGNER_MARRIAGE.getCode()) {

	    if (terminationRecodDetailDataItr.getReferring() == null || terminationRecodDetailDataItr.getReferring().trim().isEmpty())
		throw new BusinessException("error_refferingToIsMandatory");

	    if (terminationRecodDetailDataItr.getServiceTerminationDate() == null)
		throw new BusinessException("error_serviceTerminationDateMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getServiceTerminationDate()))
		throw new BusinessException("error_invalidServiceTerminationDate");

	    if (!checkDatesYears(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getServiceTerminationDate(), ETRConfigurationService.getTerminationsSoldiersForeignerMarriageServiceTerminationYearsMax(), ETRConfigurationService.getTerminationsSoldiersForeignerMarriageServiceTerminationYearsMin() * -1))
		throw new BusinessException("error_foreignerMarriageServiceTerminationDateForSoldiers", new Object[] { ETRConfigurationService.getTerminationsSoldiersForeignerMarriageServiceTerminationYearsMin(), ETRConfigurationService.getTerminationsSoldiersForeignerMarriageServiceTerminationYearsMax() });

	    if (terminationRecodDetailDataItr.getEmpRecruitmentDate() == null)
		throw new BusinessException("error_recruitmentDateIsMandatory");

	    if (!checkDatesYears(terminationRecodDetailDataItr.getEmpRecruitmentDate(), terminationRecodDetailDataItr.getServiceTerminationDate(), null, ETRConfigurationService.getTerminationsSoldiersForeignerMarriageRecruitmentTerminationYearsMax()))
		throw new BusinessException("error_foreignerMarriageDateForSoldiers", new Object[] { ETRConfigurationService.getTerminationsSoldiersForeignerMarriageRecruitmentTerminationYearsMax() });

	} else if (reasonId == TerminationReasonsEnum.SOLDIERS_DISQUALIFICATION_MILITARY_SERVICE.getCode()) {
	    if (terminationRecodDetailDataItr.getReferring() == null || terminationRecodDetailDataItr.getReferring().trim().isEmpty())
		throw new BusinessException("error_refferingToIsMandatory");

	    if (terminationRecodDetailDataItr.getDisqualificationDrugsFlagBoolean() != null && terminationRecodDetailDataItr.getDisqualificationDrugsFlagBoolean() == true && (terminationRecodDetailDataItr.getDisqualificationDrugsType() == null || terminationRecodDetailDataItr.getDisqualificationDrugsType().trim().isEmpty()))
		throw new BusinessException("error_disqualificationDrugType");

	    if (terminationRecodDetailDataItr.getDisqualificationDrugsFlagBoolean() == null || terminationRecodDetailDataItr.getDisqualificationDrugsFlagBoolean() == false && (terminationRecodDetailDataItr.getDisqualificationReason() == null || terminationRecodDetailDataItr.getDisqualificationReason().trim().isEmpty()))
		throw new BusinessException("error_disqualificationReason");

	    if (terminationRecodDetailDataItr.getEmpRecruitmentDate() == null)
		throw new BusinessException("error_recruitmentDateIsMandatory");

	    if ((!checkDatesYears(terminationRecodDetailDataItr.getEmpRecruitmentDate(), terminationRecordData.getRecordDate(), ETRConfigurationService.getTerminationsSoldiersDisqualificationMilitaryRecruitmentRecordDateYearsMax(), null) || terminationRecodDetailDataItr.getEmpRecruitmentDate().after(terminationRecordData.getRecordDate())))
		throw new BusinessException("error_disqualificationRecruitmentDateForSoldiers", new Object[] { ETRConfigurationService.getTerminationsSoldiersDisqualificationMilitaryRecruitmentRecordDateYearsMax() });
	}
    }

    private static void validateTerminationCiviliansRecordDetails(TerminationRecordData terminationRecordData, List<TerminationRecordDetailData> terminationRecordDetailDataList) throws BusinessException {

	String employeesString = "", comma = "";
	String errorString = "";

	long reasonId = terminationRecordData.getReasonId();

	for (TerminationRecordDetailData terminationRecodDetailDataItr : terminationRecordDetailDataList) {
	    if (terminationRecodDetailDataItr.getEmpId() == null)
		throw new BusinessException("error_transactionDataError");

	    if ((errorString.isEmpty() || errorString.equals("error_employeeOfficialUnitNotFound")) && (terminationRecodDetailDataItr.getEmpOfficialUnitFullName() == null || terminationRecodDetailDataItr.getEmpOfficialUnitFullName().length() == 0)) {
		errorString = "error_employeeOfficialUnitNotFound";
		employeesString += comma + terminationRecodDetailDataItr.getEmpName();
		comma = ", ";
		continue;
	    }
	}

	if (!errorString.isEmpty() && !employeesString.isEmpty()) {
	    if (errorString.equals("error_employeeOfficialUnitNotFound"))
		throw new BusinessException(errorString, new Object[] { employeesString });
	}

	TerminationRecordDetailData terminationRecodDetailDataItr = terminationRecordDetailDataList.get(0);

	if (reasonId == TerminationReasonsEnum.PERSONS_TERMINATION_REQUEST.getCode()) {
	    if (terminationRecodDetailDataItr.getEmpRecruitmentDate() == null)
		throw new BusinessException("error_recruitmentDateIsMandatory");

	    if (terminationRecodDetailDataItr.getDesiredTerminationDate() == null)
		throw new BusinessException("error_desiredTerminationDateIsMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getDesiredTerminationDate()))
		throw new BusinessException("error_invalidDesiredTerminationDate");

	    if ((terminationRecodDetailDataItr.getJudgmentFlagBoolean() == null || terminationRecodDetailDataItr.getJudgmentFlagBoolean() == false) && !checkDatesYears(terminationRecodDetailDataItr.getEmpRecruitmentDate(), terminationRecodDetailDataItr.getDesiredTerminationDate(), null, ETRConfigurationService.getTerminationsCiviliansTerminationRequestReqDesiredDiffYearsMin()))
		throw new BusinessException("error_noJudjmentIsMandatory");

	    if (!checkDatesYears(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getDesiredTerminationDate(), ETRConfigurationService.getTerminationsCiviliansRecordDesiredTerminationDateRecordDateDiffYearsMax(), ETRConfigurationService.getTerminationsCiviliansRecordDesiredTerminationDateRecordDateDiffYearsMin() * -1))
		throw new BusinessException("error_recordDesiredTerminationDatePeriodForPersons", new Object[] { ETRConfigurationService.getTerminationsCiviliansRecordDesiredTerminationDateRecordDateDiffYearsMin(), ETRConfigurationService.getTerminationsCiviliansRecordDesiredTerminationDateRecordDateDiffYearsMax() });

	} else if (reasonId == TerminationReasonsEnum.PERSONS_LACK_MEDICAL_FITNESS.getCode()) {
	    if (terminationRecodDetailDataItr.getReferring() == null || terminationRecodDetailDataItr.getReferring().trim().isEmpty())
		throw new BusinessException("error_refferingToIsMandatory");

	    if (terminationRecodDetailDataItr.getMedicalCaseDesc() == null || terminationRecodDetailDataItr.getMedicalCaseDesc().trim().isEmpty())
		throw new BusinessException("error_MedicalDiagnoasesIsMandatory");

	    if (terminationRecodDetailDataItr.getMedicalWorkDisability() == null || terminationRecodDetailDataItr.getMedicalWorkDisability().trim().isEmpty())
		throw new BusinessException("error_medicalWorkDisability");

	    if (terminationRecodDetailDataItr.getServiceTerminationDate() == null)
		throw new BusinessException("error_serviceTerminationDateMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getServiceTerminationDate()))
		throw new BusinessException("error_invalidServiceTerminationDate");

	    if (terminationRecodDetailDataItr.getServiceTerminationDate().after(terminationRecordData.getRecordDate()) || !checkDatesDays(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getServiceTerminationDate(), null, ETRConfigurationService.getTerminationsCiviliansLackMedicalTerminationDateMax() * -1))
		throw new BusinessException("error_serviceTerminationDateAfterToday", new Object[] { ETRConfigurationService.getTerminationsCiviliansLackMedicalTerminationDateMax() });

	} else if (reasonId == TerminationReasonsEnum.PERSONS_ABSENCE.getCode()) {
	    if (terminationRecodDetailDataItr.getReferring() == null || terminationRecodDetailDataItr.getReferring().trim().isEmpty())
		throw new BusinessException("error_refferingToIsMandatory");

	    if (terminationRecodDetailDataItr.getAbsenceType() == null)
		throw new BusinessException("error_absenceType");

	    if (terminationRecodDetailDataItr.getAbsencePeriodDays() == null)
		throw new BusinessException("error_absencePeriodDaysId");

	    if (terminationRecodDetailDataItr.getAbsenceStartDate() == null)
		throw new BusinessException("error_absenceStartDateIsMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getAbsenceStartDate()))
		throw new BusinessException("error_invalidAbsenceStartDate");

	    if (terminationRecodDetailDataItr.getAbsenceType().equals(TerminationAbsenceTypeEnum.CONTINUOUS_DAYS.getCode()) && terminationRecodDetailDataItr.getAbsencePeriodDays() < ETRConfigurationService.getTerminationsCiviliansAbsenceContinuousDaysMin())
		throw new BusinessException("error_terminationAbcenseContinousDaysMin", new Object[] { ETRConfigurationService.getTerminationsCiviliansAbsenceContinuousDaysMin() });

	    if (terminationRecodDetailDataItr.getAbsenceType().equals(TerminationAbsenceTypeEnum.DISCONTINUOUS_DAYS.getCode()) && terminationRecodDetailDataItr.getAbsencePeriodDays() < ETRConfigurationService.getTerminationsCiviliansAbsenceDiscontinuousDaysMin())
		throw new BusinessException("error_terminationAbcenseDisContinousDaysMin", new Object[] { ETRConfigurationService.getTerminationsCiviliansAbsenceDiscontinuousDaysMin() });

	    if (terminationRecodDetailDataItr.getServiceTerminationDate() == null)
		throw new BusinessException("error_serviceTerminationDateMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getServiceTerminationDate()))
		throw new BusinessException("error_invalidServiceTerminationDate");

	    if (terminationRecodDetailDataItr.getAbsenceStartDate().after(terminationRecordData.getRecordDate()) || !checkDatesYears(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getAbsenceStartDate(), null, ETRConfigurationService.getTerminationsCiviliansAbsenceYearsMax() * -1))
		throw new BusinessException("error_absenceDateAfterRecordDate", new Object[] { ETRConfigurationService.getTerminationsCiviliansAbsenceYearsMax() });

	    if (terminationRecodDetailDataItr.getServiceTerminationDate().before(terminationRecodDetailDataItr.getAbsenceStartDate()) || !checkDatesDays(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getServiceTerminationDate(), ETRConfigurationService.getTerminationsCiviliansAbsenceTerminationDateDaysMax(), null))
		throw new BusinessException("error_teminationDateAndStartAbsence", new Object[] { ETRConfigurationService.getTerminationsCiviliansAbsenceTerminationDateDaysMax() });

	    if (Math.abs(HijriDateService.hijriDateDiff(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getAbsenceStartDate())) < terminationRecodDetailDataItr.getAbsencePeriodDays())
		throw new BusinessException("error_absencePeriodDays");

	} else if (reasonId == TerminationReasonsEnum.PERSONS_JUDGMENT.getCode()) {
	    if (terminationRecodDetailDataItr.getReferring() == null || terminationRecodDetailDataItr.getReferring().trim().isEmpty())
		throw new BusinessException("error_refferingToIsMandatory");

	    if (terminationRecodDetailDataItr.getDismissDecisionNumber() == null || terminationRecodDetailDataItr.getDismissDecisionNumber().trim().isEmpty())
		throw new BusinessException("error_dismissDecisionNumber");

	    if (terminationRecodDetailDataItr.getDismissDecisionDate() == null)
		throw new BusinessException("error_dismissDecisionDateCiviliansIsMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getDismissDecisionDate()))
		throw new BusinessException("error_invalidCiviliansDismissDecisionDate");

	    if (terminationRecodDetailDataItr.getDismissJudgment() == null || terminationRecodDetailDataItr.getDismissJudgment().trim().isEmpty())
		throw new BusinessException("error_dismissJudgment");

	    if (terminationRecodDetailDataItr.getServiceTerminationDate() == null)
		throw new BusinessException("error_serviceTerminationDateMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getServiceTerminationDate()))
		throw new BusinessException("error_invalidServiceTerminationDate");

	    if (terminationRecodDetailDataItr.getDismissDecisionDate().after(terminationRecordData.getRecordDate()) || !checkDatesYears(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getDismissDecisionDate(), null, ETRConfigurationService.getTerminationsCiviliansDismissDateTerminationDiffMax() * -1))
		throw new BusinessException("error_terminationDismissDateAndDecisionDate", new Object[] { ETRConfigurationService.getTerminationsCiviliansDismissDateTerminationDiffMax() });

	    if (terminationRecodDetailDataItr.getServiceTerminationDate().after(terminationRecodDetailDataItr.getDismissDecisionDate()) || !checkDatesYears(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getServiceTerminationDate(), null, ETRConfigurationService.getTerminationsCiviliansDismissServiceDateRecordDateDiffMax() * -1))
		throw new BusinessException("error_terminationDismissDateAndServiceTerminationDate", new Object[] { ETRConfigurationService.getTerminationsCiviliansDismissServiceDateRecordDateDiffMax() });

	} else if (reasonId == TerminationReasonsEnum.PERSONS_DISMISS_PUBLIC_INTEREST.getCode()) {
	    if (terminationRecodDetailDataItr.getReferring() == null || terminationRecodDetailDataItr.getReferring().trim().isEmpty())
		throw new BusinessException("error_refferingToIsMandatory");

	    if (terminationRecodDetailDataItr.getServiceTerminationDate() == null)
		throw new BusinessException("error_serviceTerminationDateMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getServiceTerminationDate()))
		throw new BusinessException("error_invalidServiceTerminationDate");

	    if (!checkDatesYears(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getServiceTerminationDate(), ETRConfigurationService.getTerminationsCiviliansPersonsDismissPublicInterestTerminationDateYearsMax(), ETRConfigurationService.getTerminationsCiviliansPersonsDismissPublicInterestTerminationDateYearsMin() * -1))
		throw new BusinessException("error_personsServiceTerminationDateDismissPublicInterest", new Object[] { ETRConfigurationService.getTerminationsCiviliansPersonsDismissPublicInterestTerminationDateYearsMin(), ETRConfigurationService.getTerminationsCiviliansPersonsDismissPublicInterestTerminationDateYearsMax() });

	} else if (reasonId == TerminationReasonsEnum.PERSONS_DISQUALIFICATION.getCode()) {
	    if (terminationRecodDetailDataItr.getReferring() == null || terminationRecodDetailDataItr.getReferring().trim().isEmpty())
		throw new BusinessException("error_refferingToIsMandatory");

	    if (terminationRecodDetailDataItr.getDisqualificationReason() == null || terminationRecodDetailDataItr.getDisqualificationReason().trim().isEmpty())
		throw new BusinessException("error_disqualificationReason");

	    if (terminationRecodDetailDataItr.getServiceTerminationDate() == null)
		throw new BusinessException("error_serviceTerminationDateMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getServiceTerminationDate()))
		throw new BusinessException("error_invalidServiceTerminationDate");

	    if (!checkDatesYears(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getServiceTerminationDate(), ETRConfigurationService.getTerminationsCiviliansDisqualificationMilitaryServiceDateRecordDateDiffYearsMax(), ETRConfigurationService.getTerminationsCiviliansDisqualificationMilitaryServiceDateRecordDateDiffYearsMin() * -1))
		throw new BusinessException("error_personsServiceTerminationDateDisqualification", new Object[] { ETRConfigurationService.getTerminationsCiviliansDisqualificationMilitaryServiceDateRecordDateDiffYearsMin(), ETRConfigurationService.getTerminationsCiviliansDisqualificationMilitaryServiceDateRecordDateDiffYearsMax() });

	} else if (reasonId == TerminationReasonsEnum.PERSONS_JOB_CANCELLATION.getCode()) {
	    if (terminationRecodDetailDataItr.getReferring() == null || terminationRecodDetailDataItr.getReferring().trim().isEmpty())
		throw new BusinessException("error_refferingToIsMandatory");

	    if (terminationRecodDetailDataItr.getJobCancelationDate() == null)
		throw new BusinessException("error_jobCancelationDateMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getJobCancelationDate()))
		throw new BusinessException("error_invalidJobCancelationDate");

	    if (terminationRecodDetailDataItr.getServiceTerminationDate() == null)
		throw new BusinessException("error_serviceTerminationDateMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getServiceTerminationDate()))
		throw new BusinessException("error_invalidServiceTerminationDate");

	    if (!checkDatesYears(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getJobCancelationDate(), ETRConfigurationService.getTerminationsCiviliansJobCancelationDateYearsMax(), ETRConfigurationService.getTerminationsCiviliansJobCancelationDateYearsMin() * -1))
		throw new BusinessException("error_personsJobCancelDateIssue", new Object[] { ETRConfigurationService.getTerminationsCiviliansJobCancelationDateYearsMin(), ETRConfigurationService.getTerminationsCiviliansJobCancelationDateYearsMax() });

	    if (!checkDatesYears(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getServiceTerminationDate(), ETRConfigurationService.getTerminationsCiviliansJobCancelationTerminationDateYearsMax(), ETRConfigurationService.getTerminationsCiviliansJobCancelationTerminationDateYearsMin() * -1))
		throw new BusinessException("error_personsServiceTerminationDateJobCancelReson", new Object[] { ETRConfigurationService.getTerminationsCiviliansJobCancelationTerminationDateYearsMin(), ETRConfigurationService.getTerminationsCiviliansJobCancelationTerminationDateYearsMax() });

	} else if (reasonId == TerminationReasonsEnum.PERSONS_DEATH.getCode()) {
	    if (terminationRecodDetailDataItr.getDeathCertIssuePlace() == null || terminationRecodDetailDataItr.getDeathCertIssuePlace().trim().isEmpty())
		throw new BusinessException("error_deathCertIssuePlace");

	    if (terminationRecodDetailDataItr.getDeathCertNumber() == null || terminationRecodDetailDataItr.getDeathCertNumber().trim().isEmpty())
		throw new BusinessException("error_deathCertNumber");

	    if (terminationRecodDetailDataItr.getDeathCertDate() == null)
		throw new BusinessException("error_deathCertDateIsMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getDeathCertDate()))
		throw new BusinessException("error_invalidDeathCertDate");

	    if (terminationRecodDetailDataItr.getDeathDate() == null)
		throw new BusinessException("error_deathDateIsMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getDeathDate()))
		throw new BusinessException("error_invalidDeathDate");

	    if (terminationRecodDetailDataItr.getServiceTerminationDate() == null)
		throw new BusinessException("error_serviceTerminationDateMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getServiceTerminationDate()))
		throw new BusinessException("error_invalidServiceTerminationDate");

	    if ((terminationRecodDetailDataItr.getDeathDate().after(terminationRecodDetailDataItr.getServiceTerminationDate()) || terminationRecodDetailDataItr.getServiceTerminationDate().after(terminationRecodDetailDataItr.getDeathCertDate()) || terminationRecodDetailDataItr.getDeathDate().after(terminationRecordData.getRecordDate()) || terminationRecodDetailDataItr.getServiceTerminationDate().after(terminationRecordData.getRecordDate())
		    || terminationRecodDetailDataItr.getDeathCertDate().after(terminationRecordData.getRecordDate())
		    || !checkDatesYears(terminationRecodDetailDataItr.getDeathCertDate(), terminationRecordData.getRecordDate(), ETRConfigurationService.getTerminationsCiviliansDeathDeathDateYearsMax(), null)
		    || !checkDatesYears(terminationRecodDetailDataItr.getServiceTerminationDate(), terminationRecordData.getRecordDate(), ETRConfigurationService.getTerminationsCiviliansDeathDeathDateYearsMax(), null)
		    || !checkDatesYears(terminationRecodDetailDataItr.getDeathDate(), terminationRecordData.getRecordDate(), ETRConfigurationService.getTerminationsCiviliansDeathDeathDateYearsMax(), null)))
		throw new BusinessException("error_personsterminationDateDeathReason", new Object[] { ETRConfigurationService.getTerminationsCiviliansDeathDeathDateYearsMax() });

	} else if (reasonId == TerminationReasonsEnum.PERSONS_NATIONALITY_LOSS.getCode()) {
	    if (terminationRecodDetailDataItr.getReferring() == null || terminationRecodDetailDataItr.getReferring().trim().isEmpty())
		throw new BusinessException("error_refferingToIsMandatory");

	    if (terminationRecodDetailDataItr.getNationalityLossType() == null)
		throw new BusinessException("error_nationaltyLossType");

	    if (terminationRecodDetailDataItr.getNationalityLossType() == TerminationNationalityLossReasonEnum.NATIONALITY_LOSS.getCode())
		if (terminationRecodDetailDataItr.getNationalityLossType().equals(TerminationNationalityLossReasonEnum.NATIONALITY_LOSS.getCode()) && (terminationRecodDetailDataItr.getNationalityLossReason() == null || terminationRecodDetailDataItr.getNationalityLossReason().trim().isEmpty()))
		    throw new BusinessException("error_nationalityLossReason");

	    if (terminationRecodDetailDataItr.getServiceTerminationDate() == null)
		throw new BusinessException("error_serviceTerminationDateMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getServiceTerminationDate()))
		throw new BusinessException("error_invalidServiceTerminationDate");

	    if (!checkDatesYears(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getServiceTerminationDate(), ETRConfigurationService.getTerminationsCiviliansNationalityLossTerminationDateYearsMax(), ETRConfigurationService.getTerminationsCiviliansNationalityLossTerminationDateYearsMin() * -1))
		throw new BusinessException("error_personsTerminationDateNationaltyLossReson", new Object[] { ETRConfigurationService.getTerminationsCiviliansNationalityLossTerminationDateYearsMin(), ETRConfigurationService.getTerminationsCiviliansNationalityLossTerminationDateYearsMax() });

	} else if (reasonId == TerminationReasonsEnum.CONTRACTORS_END_CONTRACT.getCode()) {
	    if (terminationRecodDetailDataItr.getReferring() == null || terminationRecodDetailDataItr.getReferring().trim().isEmpty())
		throw new BusinessException("error_refferingToIsMandatory");

	    if (terminationRecodDetailDataItr.getContractorTerminationReason() == null || terminationRecodDetailDataItr.getContractorTerminationReason().trim().isEmpty())
		throw new BusinessException("error_terminationContractorReason");

	    if (terminationRecodDetailDataItr.getServiceTerminationDate() == null)
		throw new BusinessException("error_serviceContractorTerminationDateMandatory");

	    if (!HijriDateService.isValidHijriDate(terminationRecodDetailDataItr.getServiceTerminationDate()))
		throw new BusinessException("error_invalidServiceContractorTerminationDate");

	    if (!checkDatesYears(terminationRecordData.getRecordDate(), terminationRecodDetailDataItr.getServiceTerminationDate(), ETRConfigurationService.getTerminationsContractorsEndContractTerminationDateYearsMax(), ETRConfigurationService.getTerminationsContractorsEndContractTerminationDateYearsMin() * -1))
		throw new BusinessException("error_serviceContractorTerminationDateBeforeToday", new Object[] { ETRConfigurationService.getTerminationsContractorsEndContractTerminationDateYearsMin(), ETRConfigurationService.getTerminationsContractorsEndContractTerminationDateYearsMax() });
	}
    }

    private static void validateTerminationExtensionRegistration(TerminationTransactionData terminationTransactionData) throws BusinessException {

	if (terminationTransactionData.getEmpId() == null)
	    throw new BusinessException("error_employeeMandatory");

	if (terminationTransactionData.getDecisionNumber() == null || terminationTransactionData.getDecisionNumber().equals(""))
	    throw new BusinessException("error_decNumberMandatory");

	if (!HijriDateService.isValidHijriDate(terminationTransactionData.getDecisionDate()))
	    throw new BusinessException("error_invalidDecisionDate");

	if (terminationTransactionData.getDecisionDate().after(HijriDateService.getHijriSysDate()))
	    throw new BusinessException("error_decDateViolation");

	if (terminationTransactionData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode())) {
	    if (terminationTransactionData.getExtensionPeriodMonths() == null)
		throw new BusinessException("error_monthsMandatorySoldier");

	    if (terminationTransactionData.getExtensionPeriodMonths() <= 0 || terminationTransactionData.getExtensionPeriodMonths() > ETRConfigurationService.getTerminationsSoldiersTerminationExtensionRegistrationMax())
		throw new BusinessException("error_extensionMonthsLimitSoldiers", new Object[] { ETRConfigurationService.getTerminationsSoldiersTerminationExtensionRegistrationMax() });
	} else {
	    if (terminationTransactionData.getExtensionPeriodMonths() == null)
		throw new BusinessException("error_monthsMandatoryCivilains");

	    if (terminationTransactionData.getExtensionPeriodMonths() <= 0 || terminationTransactionData.getExtensionPeriodMonths() > ETRConfigurationService.getTerminationsCiviliansTerminationExtensionRegistrationMax())
		throw new BusinessException("error_extensionMonthsLimitCivilians", new Object[] { ETRConfigurationService.getTerminationsCiviliansTerminationExtensionRegistrationMax() });
	}

    }

    /****************************************** scheduler methods ******************************************/

    public static void executeScheduledServiceTerminationTransactions() {
	try {
	    List<EmployeeData> emps = EmployeesService.getEmployeesForFutureServiceTermination();
	    for (EmployeeData emp : emps) {

		List<EmployeeData> employees = new ArrayList<EmployeeData>(1);
		employees.add(emp);

		CustomSession session = DataAccess.getSession();
		try {
		    session.beginTransaction();
		    List<TerminationTransactionData> employeeTerminationTransactions = new ArrayList<>();
		    for (EmployeeData employeeData : employees) {
			employeeTerminationTransactions.add(getEffectiveTerminationTransaction(employeeData.getEmpId()));
		    }
		    doEmployeesServiceTerminationEffect(employees, session);
		    logTerminatedEmployeeData(employeeTerminationTransactions, session);
		    session.commitTransaction();
		} catch (Exception e) {
		    e.printStackTrace();
		    session.rollbackTransaction();
		} finally {
		    session.close();
		}
	    }
	} catch (BusinessException e) {
	    e.printStackTrace();
	}
    }

    /**
     * only change employee[status ,job,physical unit] remove the employee id from units he manages officially or physically
     * 
     * @param employees
     *            employees to terminate their services
     * @param useSession
     *            optional CustomSession to be used with database transaction
     * @throws BusinessException
     */
    public static void terminateEmployeeService(List<EmployeeData> employees, CustomSession... useSession) throws BusinessException {
	validateTerminateEmployeeService(employees);
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    List<EmployeeData> effectedTerminatedEmployees = new ArrayList<EmployeeData>();
	    for (EmployeeData emp : employees) {
		// don't do the effect if termination date is in the future
		if (!emp.getServiceTerminationDate().after(HijriDateService.getHijriSysDate()))
		    effectedTerminatedEmployees.add(emp);
		else
		    EmployeesService.updateEmployee(emp, session);
	    }

	    doEmployeesServiceTerminationEffect(effectedTerminatedEmployees, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    if (e instanceof BusinessException)
		throw (BusinessException) e;
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    private static void doEmployeesServiceTerminationEffect(List<EmployeeData> employees, CustomSession session) throws BusinessException {
	if (employees == null || employees.size() == 0)
	    return;
	try {
	    Long empsIds[] = new Long[employees.size()];
	    for (int i = 0; i < employees.size(); i++) {
		empsIds[i] = employees.get(i).getEmpId();

		JobData empJob = JobsService.getJobById(employees.get(i).getJobId());
		boolean sameUnitFlag = employees.get(i).getPhysicalUnitId() == null || employees.get(i).getOfficialUnitId().equals(employees.get(i).getPhysicalUnitId());
		boolean physicalUnitChangeFlag = false, officalUnitChangeFlag = false;
		UnitData empOfficalUnit = UnitsService.getUnitById(employees.get(i).getOfficialUnitId());
		UnitData empPhysicalUnit = empOfficalUnit;

		if (!sameUnitFlag)
		    empPhysicalUnit = UnitsService.getUnitById(employees.get(i).getPhysicalUnitId());

		employees.get(i).setStatusId(EmployeeStatusEnum.SERVICE_TERMINATED.getCode());
		employees.get(i).setPhysicalUnitId(null);
		employees.get(i).setJobId(null);
		EmployeesService.updateEmployee(employees.get(i), session);

		JobsService.changeJobStatus(empJob, JobStatusEnum.VACANT.getCode(), session);

		if (empOfficalUnit.getOfficialManagerId() != null && empOfficalUnit.getOfficialManagerId().equals(employees.get(i).getEmpId())) {
		    empOfficalUnit.setOfficialManagerId(null);
		    officalUnitChangeFlag = true;
		}
		if (empPhysicalUnit.getPhysicalManagerId() != null && empPhysicalUnit.getPhysicalManagerId().equals(employees.get(i).getEmpId())) {
		    empPhysicalUnit.setPhysicalManagerId(null);
		    physicalUnitChangeFlag = true;
		}

		List<UnitData> units = new ArrayList<UnitData>();

		if (officalUnitChangeFlag)
		    units.add(empOfficalUnit);

		if (physicalUnitChangeFlag && !(sameUnitFlag && officalUnitChangeFlag))
		    units.add(empPhysicalUnit);

		if (units.size() > 0)
		    UnitsService.modifyUnitsManagers(units, false, session);
	    }

	    BusinessWorkflowCooperation.invalidateEmployeesInboxAndDelegations(empsIds, TransactionClassesEnum.TERMINATIONS.getCode(), session);

	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static void validateTerminateEmployeeService(List<EmployeeData> employees) throws BusinessException {
	String employeesString = "", comma = "";
	String errorString = "";

	for (EmployeeData employee : employees) {
	    if ((errorString.isEmpty() || errorString.equals("error_employeeStatusInNotSuitable"))
		    && !(employee.getStatusId().equals(EmployeeStatusEnum.ON_DUTY_UNDER_RECRUITMENT.getCode()) || employee.getStatusId().equals(EmployeeStatusEnum.ON_DUTY.getCode()) || employee.getStatusId().equals(EmployeeStatusEnum.SUBJOINED.getCode()) || employee.getStatusId().equals(EmployeeStatusEnum.PERSONS_SUBJOINED.getCode()) || employee.getStatusId().equals(EmployeeStatusEnum.ASSIGNED.getCode()) || employee.getStatusId().equals(EmployeeStatusEnum.MANDATED.getCode())
			    || employee.getStatusId().equals(EmployeeStatusEnum.SECONDMENTED.getCode()) || employee.getStatusId().equals(EmployeeStatusEnum.ASSIGNED_EXTERNALLY.getCode()) || employee.getStatusId().equals(EmployeeStatusEnum.PERSONS_SUBJOINED_EXTERNALLY.getCode()) || employee.getStatusId().equals(EmployeeStatusEnum.SUBJOINED_EXTERNALLY.getCode()))) {
		errorString = "error_employeeStatusInNotSuitable";
		employeesString += comma + employee.getName();
		comma = ", ";
		continue;
	    }

	    if ((errorString.isEmpty() || errorString.equals("error_serviceTerminationDateMandatory")) && employee.getServiceTerminationDate() == null) {
		errorString = "error_serviceTerminationDateMandatory";
		employeesString += comma + employee.getName();
		comma = ", ";
		continue;
	    }
	    if ((errorString.isEmpty() || errorString.equals("error_invalidHijriServiceTerminationDate")) && !HijriDateService.isValidHijriDate(employee.getServiceTerminationDate())) {
		errorString = "error_invalidHijriServiceTerminationDate";
		employeesString += comma + employee.getName();
		comma = ", ";
		continue;
	    }
	}

	if (!errorString.isEmpty() && !employeesString.isEmpty())
	    throw new BusinessException("error_steCollective", new Object[] { getMessage(errorString), employeesString });
    }

    /************************************************ Terminations Logging *******************************************************/

    public static void logTerminatedEmployeeData(List<TerminationTransactionData> terminationTransactionsList, CustomSession session) throws BusinessException {
	for (TerminationTransactionData terminationTransactionData : terminationTransactionsList) {
	    EmployeeLog log = new EmployeeLog.Builder().setJobId(null).setPhysicalUnitId(null).setOfficialUnitId(null).setBasicJobNameId(null).constructCommonFields(terminationTransactionData.getEmpId(), FlagsEnum.OFF.getCode(), terminationTransactionData.getDecisionNumber(), terminationTransactionData.getDecisionDate(), terminationTransactionData.getServiceTerminationDate(), DataAccess.getTableName(TerminationTransaction.class)).build();
	    LogService.logEmployeeData(log, session);
	}
    }

    /************************************************ Payroll Integration *******************************************************/
    public static void doPayrollIntegration(List<TerminationTransactionData> terminationTransactionsList, Integer resendFlag, CustomSession session) throws BusinessException {
	try {
	    Long adminDecisionId = null;
	    TerminationTransactionData cancelledTerminationTransaction = null;
	    if (terminationTransactionsList.get(0).getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {
		return;
	    } else if (terminationTransactionsList.get(0).getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode())) {
		if (terminationTransactionsList.get(0).getTransactionTypeId().equals(CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.STE_TERMINATION_CANCELLATION.getCode(), TransactionClassesEnum.TERMINATIONS.getCode()).getId())) {
		    adminDecisionId = AdminDecisionsEnum.SOLDIERS_TERMINATION_DECISION_CANCELLATION.getCode();
		}
	    } else {
		if (terminationTransactionsList.get(0).getTransactionTypeId().equals(CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.STE_TERMINATION.getCode(), TransactionClassesEnum.TERMINATIONS.getCode()).getId())) {
		    adminDecisionId = AdminDecisionsEnum.CIVILLIANS_TERMINATION_RECORD.getCode();
		} else if (terminationTransactionsList.get(0).getTransactionTypeId().equals(CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.STE_TERMINATION_CANCELLATION.getCode(), TransactionClassesEnum.TERMINATIONS.getCode()).getId())) {
		    adminDecisionId = AdminDecisionsEnum.CIVILLIANS_TERMINATION_DECISION_CANCELLATION.getCode();
		    cancelledTerminationTransaction = TerminationsService.getTerminationTransactionById(terminationTransactionsList.get(0).getCancelTransactionId());
		}
	    }
	    if (adminDecisionId != null) {
		String gregDecisionDateString = null;
		String gregTerminationDateString = null;
		List<AdminDecisionEmployeeData> adminDecisionEmployeeDataList = new ArrayList<AdminDecisionEmployeeData>();
		EmployeeData employee = null;
		session.flushTransaction();
		for (TerminationTransactionData terminationTransactionData : terminationTransactionsList) {
		    if (terminationTransactionData.getServiceTerminationDateString() == null) // Cancellation
			gregTerminationDateString = HijriDateService.hijriToGregDateString(TerminationsService.getTerminationTransactionById(terminationTransactionData.getCancelTransactionId()).getServiceTerminationDateString());
		    else
			gregTerminationDateString = HijriDateService.hijriToGregDateString(terminationTransactionData.getServiceTerminationDateString());
		    employee = EmployeesService.getEmployeeData(terminationTransactionData.getEmpId());
		    if ((employee == null || employee.getPhysicalUnitId() == null) && terminationTransactionData.getTransEmpUnitId() == null) {
			throw new BusinessException("error_noUnitIdInTerminationTransaction");
		    }
		    adminDecisionEmployeeDataList.add(new AdminDecisionEmployeeData(terminationTransactionData.getEmpId(), employee.getName(), terminationTransactionData.getId(), terminationTransactionData.getCancelTransactionId(), gregTerminationDateString, null, terminationTransactionData.getDecisionNumber(), cancelledTerminationTransaction != null && cancelledTerminationTransaction.getDecisionNumber() != null ? cancelledTerminationTransaction.getDecisionNumber() : null));
		}
		gregDecisionDateString = HijriDateService.hijriToGregDateString(terminationTransactionsList.get(0).getDecisionDateString());
		PayrollEngineService.doPayrollIntegration(adminDecisionId, terminationTransactionsList.get(0).getCategoryId(), gregTerminationDateString, adminDecisionEmployeeDataList, employee != null && employee.getPhysicalUnitId() != null ? employee.getPhysicalUnitId() : terminationTransactionsList.get(0).getTransEmpUnitId(), gregDecisionDateString, DataAccess.getTableName(TerminationTransaction.class), resendFlag, FlagsEnum.OFF.getCode(), session);
	    }
	} catch (BusinessException e) {
	    throw e;
	}
    }

    public static void payrollIntegrationFailureHandle(String decisionNumber, Date decisionDate, CustomSession session) throws BusinessException {
	List<TerminationTransactionData> terminationTransactionDataList = getTerminationTransactionsByDecisionNumberAndDecisionDate(decisionNumber, decisionDate);
	if (terminationTransactionDataList != null && terminationTransactionDataList.size() != 0) {
	    if (PayrollEngineService.getIntegrationWithAllowanceAndDeductionFlag().equals(FlagsEnum.ON.getCode()))
		doPayrollIntegration(terminationTransactionDataList, FlagsEnum.ON.getCode(), session);
	} else {
	    throw new BusinessException("error_transactionDataRetrievingError");
	}
    }

    /************************************************ Terminations Integration *******************************************************/
    public static Date calculateEmpTerminationDueDate(long categoryId, long rankId, Date birthDate) throws BusinessException {
	final int numberOfMoth = 12;
	Date serviceTerminaitonDueDate = null;

	try {
	    if (categoryId == CategoriesEnum.OFFICERS.getCode()) {
		if (rankId == RanksEnum.MAJOR_GENERAL.getCode())
		    serviceTerminaitonDueDate = HijriDateService.addSubHijriMonthsDays(birthDate, TerminationDueDateYearsEnum.MAJOR_GENERAL.getCode() * numberOfMoth, 0);
		else if (rankId == RanksEnum.BRIGADIER.getCode())
		    serviceTerminaitonDueDate = HijriDateService.addSubHijriMonthsDays(birthDate, TerminationDueDateYearsEnum.BRIGADIER.getCode() * numberOfMoth, 0);
		else if (rankId == RanksEnum.COLONEL.getCode())
		    serviceTerminaitonDueDate = HijriDateService.addSubHijriMonthsDays(birthDate, TerminationDueDateYearsEnum.COLONEL.getCode() * numberOfMoth, 0);
		else if (rankId == RanksEnum.LIEUTENANT_COLONEL.getCode())
		    serviceTerminaitonDueDate = HijriDateService.addSubHijriMonthsDays(birthDate, TerminationDueDateYearsEnum.LIEUTENANT_COLONEL.getCode() * numberOfMoth, 0);
		else if (rankId == RanksEnum.MAJOR.getCode())
		    serviceTerminaitonDueDate = HijriDateService.addSubHijriMonthsDays(birthDate, TerminationDueDateYearsEnum.MAJOR.getCode() * numberOfMoth, 0);
		else if (rankId == RanksEnum.CAPTAIN.getCode())
		    serviceTerminaitonDueDate = HijriDateService.addSubHijriMonthsDays(birthDate, TerminationDueDateYearsEnum.CAPTAIN.getCode() * numberOfMoth, 0);
		else if (rankId == RanksEnum.FIRST_LIEUTENANT.getCode())
		    serviceTerminaitonDueDate = HijriDateService.addSubHijriMonthsDays(birthDate, TerminationDueDateYearsEnum.FIRST_LIEUTENANT.getCode() * numberOfMoth, 0);
		else if (rankId == RanksEnum.LIEUTENANT.getCode())
		    serviceTerminaitonDueDate = HijriDateService.addSubHijriMonthsDays(birthDate, TerminationDueDateYearsEnum.LIEUTENANT.getCode() * numberOfMoth, 0);
	    } else if (categoryId == CategoriesEnum.SOLDIERS.getCode()) {
		if (rankId == RanksEnum.PRIME_SERGEANTS.getCode())
		    serviceTerminaitonDueDate = HijriDateService.addSubHijriMonthsDays(birthDate, TerminationDueDateYearsEnum.PRIME_SERGEANTS.getCode() * numberOfMoth, 0);
		else if (rankId == RanksEnum.STAFF_SERGEANT.getCode())
		    serviceTerminaitonDueDate = HijriDateService.addSubHijriMonthsDays(birthDate, TerminationDueDateYearsEnum.STAFF_SERGEANT.getCode() * numberOfMoth, 0);
		else if (rankId == RanksEnum.SERGEANT.getCode())
		    serviceTerminaitonDueDate = HijriDateService.addSubHijriMonthsDays(birthDate, TerminationDueDateYearsEnum.SERGEANT.getCode() * numberOfMoth, 0);
		else if (rankId == RanksEnum.UNDER_SERGEANT.getCode())
		    serviceTerminaitonDueDate = HijriDateService.addSubHijriMonthsDays(birthDate, TerminationDueDateYearsEnum.UNDER_SERGEANT.getCode() * numberOfMoth, 0);
		else if (rankId == RanksEnum.CORPORAL.getCode())
		    serviceTerminaitonDueDate = HijriDateService.addSubHijriMonthsDays(birthDate, TerminationDueDateYearsEnum.CORPORAL.getCode() * numberOfMoth, 0);
		else if (rankId == RanksEnum.CAPTAIN.getCode())
		    serviceTerminaitonDueDate = HijriDateService.addSubHijriMonthsDays(birthDate, TerminationDueDateYearsEnum.CAPTAIN.getCode() * numberOfMoth, 0);
		else if (rankId == RanksEnum.FIRST_SOLDIER.getCode())
		    serviceTerminaitonDueDate = HijriDateService.addSubHijriMonthsDays(birthDate, TerminationDueDateYearsEnum.FIRST_SOLDIER.getCode() * numberOfMoth, 0);
		else if (rankId == RanksEnum.SOLDIER.getCode())
		    serviceTerminaitonDueDate = HijriDateService.addSubHijriMonthsDays(birthDate, TerminationDueDateYearsEnum.SOLDIER.getCode() * numberOfMoth, 0);
	    } else
		serviceTerminaitonDueDate = HijriDateService.addSubHijriMonthsDays(birthDate, TerminationDueDateYearsEnum.PERSONS.getCode() * numberOfMoth, 0);

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

	return serviceTerminaitonDueDate;
    }

    /*********************************************** Terminations Queries ***********************************************************/
    public static List<TerminationTransactionData> getTerminationTransactionsAfterDecisionDate(Long empId, Date decisiondate) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId == null ? FlagsEnum.ALL.getCode() : empId);
	    if (decisiondate == null) {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_DECISION_DATE", HijriDateService.getHijriSysDateString());
	    } else {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_DECISION_DATE", HijriDateService.getHijriDateString(decisiondate));
	    }
	    return DataAccess.executeNamedQuery(TerminationTransactionData.class, QueryNamesEnum.HCM_GET_TERMINATION_TRANSACTIONS_AFTER_DECISION_DATE.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<EmployeeData> searchTerminationEligibleEmployees(Long categoryId, Long regionId, Long rankId, Date empTerminationDueDateFrom, Date empTerminationDueDateTo) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_CATEGORY_ID", categoryId);
	    qParams.put("P_REGION_ID", regionId == null ? FlagsEnum.ALL.getCode() : regionId);
	    qParams.put("P_RANK_ID", rankId == null ? FlagsEnum.ALL.getCode() : rankId);
	    if (empTerminationDueDateFrom == null) {
		qParams.put("P_TERMINATION_DUE_DATE_FROM_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_TERMINATION_DUE_DATE_FROM", HijriDateService.getHijriSysDateString());
	    } else {
		qParams.put("P_TERMINATION_DUE_DATE_FROM_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_TERMINATION_DUE_DATE_FROM", HijriDateService.getHijriDateString(empTerminationDueDateFrom));
	    }

	    qParams.put("P_TERMINATION_DUE_DATE_TO", HijriDateService.getHijriDateString(empTerminationDueDateTo));

	    return DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_GET_TERMINATION_ELIGIBILITY_EMPLOYEES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<EmployeeData> searchTerminationEligibleEmployeesCompletedPeriodCurrentRank(Long regionId, Long rankId, Date empTerminationDueDateFrom, Date empTerminationDueDateTo) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_REGION_ID", regionId == null ? FlagsEnum.ALL.getCode() : regionId);
	    qParams.put("P_RANK_ID", rankId == null ? FlagsEnum.ALL.getCode() : rankId);

	    Date lastPromotionDateFrom = null;
	    Date lastPromotionDateFromMajorGeneral = null;
	    if (empTerminationDueDateFrom == null) {
		qParams.put("P_LAST_PRM_DATE_FROM_FLAG", FlagsEnum.ALL.getCode());
		lastPromotionDateFrom = HijriDateService.getHijriSysDate();
		lastPromotionDateFromMajorGeneral = HijriDateService.getHijriSysDate();
	    } else {
		lastPromotionDateFrom = HijriDateService.addSubHijriMonthsDays(empTerminationDueDateFrom, ETRConfigurationService.getTerminationsOfficersRankPeriodYearsMax() * -12, 0);
		lastPromotionDateFromMajorGeneral = HijriDateService.addSubHijriMonthsDays(empTerminationDueDateFrom, ETRConfigurationService.getTerminationsOfficersRankPeriodMajorGeneralYearsMax() * -1 * 12, 0);
		qParams.put("P_LAST_PRM_DATE_FROM_FLAG", FlagsEnum.ON.getCode());
	    }

	    Date lastPromotionDateTo = HijriDateService.addSubHijriMonthsDays(empTerminationDueDateTo, ETRConfigurationService.getTerminationsOfficersRankPeriodYearsMax() * -12, 0);
	    Date lastPromotionDateToMajorGeneral = HijriDateService.addSubHijriMonthsDays(empTerminationDueDateTo, ETRConfigurationService.getTerminationsOfficersRankPeriodMajorGeneralYearsMax() * -1 * 12, 0);

	    qParams.put("P_LAST_PRM_DATE_FROM", HijriDateService.getHijriDateString(lastPromotionDateFrom));
	    qParams.put("P_LAST_PRM_DATE_FROM_MJR_GNRL", HijriDateService.getHijriDateString(lastPromotionDateFromMajorGeneral));
	    qParams.put("P_LAST_PRM_DATE_TO", HijriDateService.getHijriDateString(lastPromotionDateTo));
	    qParams.put("P_LAST_PRM_DATE_TO_MJR_GNRL", HijriDateService.getHijriDateString(lastPromotionDateToMajorGeneral));

	    return DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_GET_TERMINATION_ELIGIBILITY_EMPLOYEES_BY_COMPLETION_PERIOD_CURRENT_RANK.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<TerminationRecordDetailData> searchTerminationRecordsDetails(Long recordId, String empName, Long status) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_RECORD_ID", recordId == null ? FlagsEnum.ALL.getCode() : recordId);
	    qParams.put("P_STATUS", status == null ? FlagsEnum.ALL.getCode() : status);
	    qParams.put("P_EMP_NAME", (empName == null || empName.equals("") || empName.equals(FlagsEnum.ALL.getCode() + "")) ? FlagsEnum.ALL.getCode() + "" : "%" + empName + "%");

	    return DataAccess.executeNamedQuery(TerminationRecordDetailData.class, QueryNamesEnum.HCM_GET_TERMINATION_RECORD_DETAILS.getCode(), qParams);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static Integer getLastRecordNumberForCategoryAndYear(Long[] categories, int year) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    String firstDateOfYear = "01/01/" + year;
	    String endDateOfYear = "01/01/" + (year + 1);
	    qParams.put("P_CATEGORY_ID", categories);
	    qParams.put("P_YEAR_FROM", firstDateOfYear);
	    qParams.put("P_YEAR_TO", endDateOfYear);
	    return DataAccess.executeNamedQuery(Integer.class, QueryNamesEnum.HCM_GET_MAX_TERMINATION_RECORD_NUMBER.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<TerminationReason> getTerminationReasons(Long mode) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_CATEGORY_ID", mode == null ? FlagsEnum.ALL.getCode() : mode);
	    return DataAccess.executeNamedQuery(TerminationReason.class, QueryNamesEnum.HCM_GET_TERMINATION_REASONS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static Long getCountTerminationRecordDetailStatus(Long recordId, Long[] statuses) throws BusinessException {
	try {

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_RECORD_ID", recordId == null ? FlagsEnum.ALL.getCode() : recordId);
	    if (statuses != null && statuses.length > 0) {
		qParams.put("P_STATUSES_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_STATUSES", statuses);
	    } else {
		qParams.put("P_STATUSES_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_STATUSES", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_TERMINATION_RECORDS_DETAIL_STATUS.getCode(), qParams).get(0).longValue();
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<TerminationRecord> searchTerminationReports(Long[] recordsIds) throws BusinessException {
	if (recordsIds == null || recordsIds.length == 0)
	    return new ArrayList<TerminationRecord>();
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_RECORDS_IDS", recordsIds);

	    return DataAccess.executeNamedQuery(TerminationRecord.class, QueryNamesEnum.HCM_GET_TERMINATION_RECORDS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<TerminationRecordData> getTerminationRecordsData(String recordNumber, Date recordDateFrom, Date recordDateTo, Date retirementDueDateFrom, Date retirementDueDateTo, Long rankId, Long status, Long reasonId, Long empId, Long categoryId, Long regionId) throws BusinessException {
	return searchTerminationRecordsData(recordNumber, recordDateFrom, recordDateTo, retirementDueDateFrom, retirementDueDateTo, rankId, status, reasonId, empId, categoryId, regionId);
    }

    public static List<TerminationRecordData> getTerminationRecordsDataByEmpId(Long empId) throws BusinessException {
	return searchTerminationRecordsData(null, null, null, null, null, null, null, null, empId, null, null);
    }

    public static TerminationRecordData getTerminationRecordDataById(Long recordId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_ID", recordId);

	    return DataAccess.executeNamedQuery(TerminationRecordData.class, QueryNamesEnum.HCM_GET_TERMINATION_RECORDS_DATA_BY_ID.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<TerminationRecordData> searchTerminationRecordsData(String recordNumber, Date recordDateFrom, Date recordDateTo, Date retirementDueDateFrom, Date retirementDueDateTo, Long rankId, Long status, Long reasonId, Long empId, Long categoryId, Long regionId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_RECORD_NUMBER", (recordNumber == null || recordNumber.equals("")) ? (FlagsEnum.ALL.getCode() + "") : recordNumber);
	    if (recordDateFrom != null) {
		qParams.put("P_RECORD_DATE_FROM_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_RECORD_DATE_FROM", HijriDateService.getHijriDateString(recordDateFrom));
	    } else {
		qParams.put("P_RECORD_DATE_FROM_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_RECORD_DATE_FROM", HijriDateService.getHijriSysDateString());
	    }
	    if (recordDateTo != null) {
		qParams.put("P_RECORD_DATE_TO_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_RECORD_DATE_TO", HijriDateService.getHijriDateString(recordDateTo));
	    } else {
		qParams.put("P_RECORD_DATE_TO_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_RECORD_DATE_TO", HijriDateService.getHijriSysDateString());
	    }
	    if (retirementDueDateFrom != null) {
		qParams.put("P_TERMINATION_DUE_DATE_FROM_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_TERMINATION_DUE_DATE_FROM", HijriDateService.getHijriDateString(retirementDueDateTo));
	    } else {
		qParams.put("P_TERMINATION_DUE_DATE_FROM_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_TERMINATION_DUE_DATE_FROM", HijriDateService.getHijriSysDateString());
	    }
	    if (retirementDueDateTo != null) {
		qParams.put("P_TERMINATION_DUE_DATE_TO_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_TERMINATION_DUE_DATE_TO", HijriDateService.getHijriDateString(retirementDueDateTo));
	    } else {
		qParams.put("P_TERMINATION_DUE_DATE_TO_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_TERMINATION_DUE_DATE_TO", HijriDateService.getHijriSysDateString());
	    }
	    qParams.put("P_RANK_ID", rankId == null ? FlagsEnum.ALL.getCode() : rankId);
	    qParams.put("P_CATEGORY_ID", categoryId == null ? FlagsEnum.ALL.getCode() : categoryId);
	    qParams.put("P_REGION_ID", regionId == null ? FlagsEnum.ALL.getCode() : regionId);
	    qParams.put("P_STATUS", status == null ? FlagsEnum.ALL.getCode() : status);
	    qParams.put("P_REASON_ID", reasonId == null ? FlagsEnum.ALL.getCode() : reasonId);
	    qParams.put("P_EMP_ID", empId == null ? FlagsEnum.ALL.getCode() : empId);

	    return DataAccess.executeNamedQuery(TerminationRecordData.class, QueryNamesEnum.HCM_GET_TERMINATION_RECORDS_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static TerminationTransactionData getEffectiveTerminationTransaction(long empId) throws BusinessException {

	TerminationTransactionData lastTerminationTransaction = getLastTerminationTransaction(empId);

	if (lastTerminationTransaction != null) {
	    MovementTransactionData lastExternalMoveTransaction = MovementsService.getExternalMoveTransactionByEmployeeId(empId);

	    if (lastExternalMoveTransaction != null && lastExternalMoveTransaction.getExecutionDate().after(lastTerminationTransaction.getDecisionDate()))
		return null;
	}

	return lastTerminationTransaction;
    }

    private static TerminationTransactionData getLastTerminationTransaction(long empId) throws BusinessException {
	List<TerminationTransactionData> lastTerminationTransactions = searchLastTerminationTransactions(empId);
	return (lastTerminationTransactions == null || lastTerminationTransactions.isEmpty()) ? null : lastTerminationTransactions.get(0);
    }

    private static List<TerminationTransactionData> searchLastTerminationTransactions(long empId) throws BusinessException {
	try {

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_TERMINATION_TRANS_TYPE_CODE", TransactionTypesEnum.STE_TERMINATION.getCode());

	    return DataAccess.executeNamedQuery(TerminationTransactionData.class, QueryNamesEnum.HCM_GET_LAST_TERMINATION_TRANSACTION.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<Object> getTerminationTransactionsDataByUnitHKey(String unitHKey) throws DatabaseException {

	Map<String, Object> qParams = new HashMap<String, Object>();
	qParams.put("P_UNIT_HKEY", unitHKey);

	return DataAccess.executeNamedQuery(Object.class, QueryNamesEnum.HCM_GET_TERMINATION_TRANSACTIONS_BY_UNIT_HKEY.getCode(), qParams);
    }

    private static List<TerminationTransactionData> searchTerminationTransactions(Long recordDetailId, Long empId, Integer transactionTypeCode, String decisionNumber, Date fromDate, Date toDate, Long[] categoriesIds) throws BusinessException {
	try {

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_RECORD_DETAIL_ID", recordDetailId == null ? FlagsEnum.ALL.getCode() : recordDetailId);
	    qParams.put("P_EMP_ID", empId == null ? FlagsEnum.ALL.getCode() : empId);
	    qParams.put("P_TRANSACTION_TYPE_CODE", transactionTypeCode == null ? FlagsEnum.ALL.getCode() : transactionTypeCode);

	    qParams.put("P_DECISION_NUMBER", (decisionNumber == null || decisionNumber.length() == 0) ? FlagsEnum.ALL.getCode() : decisionNumber);
	    if (fromDate != null) {
		qParams.put("P_FROM_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_FROM_DATE", HijriDateService.getHijriDateString(fromDate));
	    } else {
		qParams.put("P_FROM_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_FROM_DATE", HijriDateService.getHijriSysDateString());
	    }
	    if (toDate != null) {
		qParams.put("P_TO_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_TO_DATE", HijriDateService.getHijriDateString(toDate));
	    } else {
		qParams.put("P_TO_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_TO_DATE", HijriDateService.getHijriSysDateString());
	    }

	    if (categoriesIds != null && categoriesIds.length > 0) {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_CATEGORIES_IDS", categoriesIds);
	    } else {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_CATEGORIES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }

	    return DataAccess.executeNamedQuery(TerminationTransactionData.class, QueryNamesEnum.HCM_GET_TERMINATION_TRANSACTIONS.getCode(), qParams);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static TerminationTransaction getLastTerminationMovementTransaction(long empId) throws BusinessException {
	List<TerminationTransaction> lastTerminationMovementTransactions = searchLastTerminationMovementTransactions(empId);
	return (lastTerminationMovementTransactions == null || lastTerminationMovementTransactions.isEmpty()) ? null : lastTerminationMovementTransactions.get(0);
    }

    private static List<TerminationTransaction> searchLastTerminationMovementTransactions(long empId) throws BusinessException {
	try {

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_TRANSACTION_TYPE_CODE", TransactionTypesEnum.STE_TERMINATION_MOVEMENT.getCode());

	    return DataAccess.executeNamedQuery(TerminationTransaction.class, QueryNamesEnum.HCM_SEARCH_LAST_TERMINATION_MOVEMENT_TRANSACTION.getCode(), qParams);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static TerminationTransactionData searchTerminationTransactionsById(Long transactionId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ID", transactionId);
	    return DataAccess.executeNamedQuery(TerminationTransactionData.class, QueryNamesEnum.HCM_GET_TERMINATION_TRANSACTIONS_BY_ID.getCode(), qParams).get(0);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<TerminationTransaction> searchTerminationTransactionsByCancelTransactionId(Long cancelTransactionId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_CANCEL_TRANSACTION_ID", cancelTransactionId);
	    return DataAccess.executeNamedQuery(TerminationTransaction.class, QueryNamesEnum.HCM_GET_TERMINATION_TRANSACTIONS_BY_CANCEL_TRANSACTION_ID.getCode(), qParams);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<TerminationTransactionData> getTerminationTransactionsByDecisionNumberAndDecisionDate(String decisionNumber, Date decisionDate) throws BusinessException {
	List<TerminationTransactionData> terminationTransactions = searchTerminationTransactions(null, null, null, decisionNumber, decisionDate, decisionDate, null);
	return terminationTransactions;
    }

    public static TerminationTransactionData searchTerminationMovementTransaction(Long transactionId, Long empId, Date movementJoiningDate, Date disclaimerDate, Date serviceTerminationDate, String movementJoiningDesc) throws BusinessException {
	try {

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    // TODO:
	    // 1- make all parameters nullable !!
	    // 2- make this method private and make a wrapper for it
	    qParams.put("P_EMP_ID", empId == null ? FlagsEnum.ALL.getCode() : empId);
	    qParams.put("P_MOVEMENT_JOINING_DATE", HijriDateService.getHijriDateString(movementJoiningDate));
	    qParams.put("P_DISCLAIMER_DATE", HijriDateService.getHijriDateString(disclaimerDate));
	    qParams.put("P_SERVICE_TERMINATION_DATE", HijriDateService.getHijriDateString(serviceTerminationDate));
	    qParams.put("P_MVT_JOINING_DESC", ("%" + movementJoiningDesc + "%"));

	    return DataAccess.executeNamedQuery(TerminationTransactionData.class, QueryNamesEnum.HCM_GET_TERMINATION_MOVEMENT_TRANSACTIONS.getCode(), qParams).get(0);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*********************************************** Terminations Printing ***********************************************************/
    /**
     * That method print officers, soldiers or civilians retirement eligibility using some parameters as follow
     * 
     * @param mode
     *            Category mode ( officers, Soldiers and Employees)
     * @param reasonType
     *            Termination reason type
     * @param rankId
     *            Rank Id
     * @param regionId
     *            Region Id (Riyadh, Makaa ...)
     * @param fromDate
     *            search from this date for who Eligible to termination not mandatory
     * @param toDate
     *            search to this date for who Eligible to termination this date is mandatory
     * @param isStatisical
     *            to change between the soldiers reports.
     * @return report bytes
     * @throws BusinessException
     */
    public static byte[] getTerminationReportsBytes(long categoryId, int reportType, Long reasonId, long regionId, String unitFullName, Long rankId, Date serviceTerminationDateFrom, Date serviceTerminationDateTo, Date terminationDueDateFrom, Date terminationDueDateTo, Date extensionDateFrom, Date extensionDateTo, Date decisionDateFrom, Date decisionDateTo, Integer graduationGroupNumber) throws BusinessException {
	if ((reportType == 1 || reportType == 2 || reportType == 8) && serviceTerminationDateFrom != null && serviceTerminationDateFrom.after(serviceTerminationDateTo))
	    throw new BusinessException("error_reportTerminationDateFromMustBefore");
	if ((reportType == 3 || reportType == 4) && extensionDateFrom != null && extensionDateFrom.after(extensionDateTo))
	    throw new BusinessException("error_reportExtensionDateFromMustBefore");
	if ((reportType == 5 || reportType == 6) && terminationDueDateFrom != null && terminationDueDateFrom.after(terminationDueDateTo))
	    throw new BusinessException("error_terminationDateFromMustBefore");

	String reportName = "";
	final int TERMINATION_REPORT = 1;
	final int TERMINATION_STATISTICAL_REPORT_BY_REGION = 2;
	final int EXTENSION_REPORT = 3;
	final int EXTENSION_STATISTICAL_REPORT = 4;
	final int TERMINATIONS_RETIREMENT_ELIGIBLITY_INQUIRY_REPORT = 5;
	final int TERMINATIONS_SOLDIERS_STATISTICAL_REPORT = 6;
	final int TERMINATIONS_CONTRACTORS_REPORT = 7;
	final int TERMINATION_STATISTICAL_REPORT_BY_REASON = 8;

	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    if (reportType == TERMINATION_REPORT || reportType == TERMINATION_STATISTICAL_REPORT_BY_REGION || reportType == EXTENSION_REPORT || reportType == EXTENSION_STATISTICAL_REPORT || reportType == TERMINATION_STATISTICAL_REPORT_BY_REASON) {
		String rankDesc = (rankId == FlagsEnum.ALL.getCode()) ? null : CommonService.getRankById(rankId).getDescription();
		parameters.put("P_CATEGORY_ID", categoryId);
		parameters.put("P_REGION_DESC", regionId == FlagsEnum.ALL.getCode() ? regionId + "" : CommonService.getRegionById(regionId).getDescription());
		parameters.put("P_UNIT_FULL_NAME", unitFullName == null || unitFullName.trim().isEmpty() ? FlagsEnum.ALL.getCode() + "" : unitFullName);
		parameters.put("P_EMP_RANK_DESC", rankDesc == null ? FlagsEnum.ALL.getCode() + "" : rankDesc);

		parameters.put("P_DEC_DATE_FROM", decisionDateFrom == null ? null : HijriDateService.getHijriDateString(decisionDateFrom));
		parameters.put("P_DEC_DATE_TO", HijriDateService.getHijriDateString(decisionDateTo));

		if (reportType == TERMINATION_REPORT || reportType == TERMINATION_STATISTICAL_REPORT_BY_REGION || reportType == TERMINATION_STATISTICAL_REPORT_BY_REASON) {
		    parameters.put("P_REASON_ID", reasonId == null ? (long) FlagsEnum.ALL.getCode() : reasonId);
		    parameters.put("P_FROM_DATE", serviceTerminationDateFrom == null ? null : HijriDateService.getHijriDateString(serviceTerminationDateFrom));
		    parameters.put("P_TO_DATE", HijriDateService.getHijriDateString(serviceTerminationDateTo));

		    if (reportType == TERMINATION_REPORT) {
			reportName = ReportNamesEnum.TERMINATIONS_SERVICE_TERMINATED_INQUIRY.getCode();
		    } else if (reportType == TERMINATION_STATISTICAL_REPORT_BY_REGION) {
			if (categoryId == CategoriesEnum.MEDICAL_STAFF.getCode())
			    reportName = ReportNamesEnum.TERMINATIONS_MEDICAL_STAFF_SERVICE_TERMINATED_INQUIRY_REGIONS_STATISTICAL.getCode();
			else
			    reportName = ReportNamesEnum.TERMINATIONS_SERVICE_TERMINATED_INQUIRY_REGIONS_STATISTICAL.getCode();
		    } else if (reportType == TERMINATION_STATISTICAL_REPORT_BY_REASON) {
			if (categoryId == CategoriesEnum.MEDICAL_STAFF.getCode())
			    reportName = ReportNamesEnum.TERMINATIONS_MEDICAL_STAFF_SERVICE_TERMINATED_INQUIRY_REASONS_STATISTICAL.getCode();
			else
			    reportName = ReportNamesEnum.TERMINATIONS_SERVICE_TERMINATED_INQUIRY_REASONS_STATISTICAL.getCode();
		    }
		} else if (reportType == EXTENSION_REPORT || reportType == EXTENSION_STATISTICAL_REPORT) {
		    parameters.put("P_REGION_ID", regionId);
		    parameters.put("P_FROM_DATE", extensionDateFrom == null ? null : HijriDateService.getHijriDateString(extensionDateFrom));
		    parameters.put("P_TO_DATE", HijriDateService.getHijriDateString(extensionDateTo));

		    if (reportType == EXTENSION_REPORT) {
			reportName = ReportNamesEnum.TERMINATIONS_EXTENSION_INQUIRY.getCode();
		    } else if (reportType == EXTENSION_STATISTICAL_REPORT) {
			if (categoryId == CategoriesEnum.MEDICAL_STAFF.getCode())
			    reportName = ReportNamesEnum.TERMINATIONS_MEDICAL_STAFF_EXTENSION_INQUIRY_STATISTICAL.getCode();
			else
			    reportName = ReportNamesEnum.TERMINATIONS_EXTENSION_INQUIRY_STATISTICAL.getCode();
		    }
		}

	    } else if (reportType == TERMINATIONS_RETIREMENT_ELIGIBLITY_INQUIRY_REPORT || reportType == TERMINATIONS_SOLDIERS_STATISTICAL_REPORT || reportType == TERMINATIONS_CONTRACTORS_REPORT) {
		if (reportType == TERMINATIONS_RETIREMENT_ELIGIBLITY_INQUIRY_REPORT || reportType == TERMINATIONS_SOLDIERS_STATISTICAL_REPORT) {
		    parameters.put("P_CATEGORY_ID", reportType == TERMINATIONS_SOLDIERS_STATISTICAL_REPORT ? FlagsEnum.ALL.getCode() : categoryId);
		    parameters.put("P_REASON_TYPE", reasonId == null ? FlagsEnum.ALL.getCode() : reasonId);
		    parameters.put("P_REGION_ID", (Long) regionId == null ? FlagsEnum.ALL.getCode() : regionId);
		    parameters.put("P_RANK_ID", rankId == null ? FlagsEnum.ALL.getCode() : rankId);
		    parameters.put("P_FROM_DATE", terminationDueDateFrom == null ? null : HijriDateService.getHijriDateString(terminationDueDateFrom));
		    parameters.put("P_TO_DATE", HijriDateService.getHijriDateString(terminationDueDateTo));
		    parameters.put("P_GRADUATION_GROUP_NUMBER", graduationGroupNumber == null ? FlagsEnum.ALL.getCode() : graduationGroupNumber);
		}
		if (reportType == TERMINATIONS_RETIREMENT_ELIGIBLITY_INQUIRY_REPORT) {
		    if (categoryId == CategoriesEnum.OFFICERS.getCode())
			reportName = ReportNamesEnum.TERMINATIONS_OFFICERS_RETIREMENT_ELIGIBLE_INQUIRY.getCode();
		    else if (categoryId == CategoriesEnum.SOLDIERS.getCode())
			reportName = ReportNamesEnum.TERMINATIONS_SOLDIERS_RETIREMENT_ELIGIBLE_INQUIRY.getCode();
		    else // CIVILIANS
			reportName = ReportNamesEnum.TERMINATIONS_CIVILIANS_RETIREMENT_ELIGIBLE_INQUIRY.getCode();

		} else if (reportType == TERMINATIONS_SOLDIERS_STATISTICAL_REPORT) {
		    reportName = ReportNamesEnum.TERMINATIONS_SOLDIERS_RETIREMENT_ELIGIBLE_INQUIRY_STATISTICAL.getCode();
		} else {
		    reportName = ReportNamesEnum.TERMINATIONS_CONTRACTORS_RECORD.getCode();
		}
	    }

	    parameters.put("P_PRINT_DATE", HijriDateService.getHijriSysDateString());
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}

    }

    public static byte[] getTerminationRecordBytes(Long recordId, Long mode, Long reasonId, String empName, Long recordStatus) throws BusinessException {
	String reportName = "";
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    parameters.put("P_RECORD_ID", recordId);
	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());

	    if (mode == CategoriesEnum.OFFICERS.getCode()) {
		if (reasonId == TerminationReasonsEnum.OFFICERS_REACHING_RETIREMENT_AGE.getCode() || reasonId == TerminationReasonsEnum.OFFICERS_COMPLETION_PERIOD_CURRENT_RANK.getCode()) {
		    parameters.put("P_EMP_NAME", empName == null ? "" : empName);
		    parameters.put("P_DETAIL_STATUS", recordStatus == null || recordStatus == -1 ? null : recordStatus);
		    reportName = ReportNamesEnum.TERMINATIONS_OFFICERS_RECORD.getCode();
		} else
		    reportName = ReportNamesEnum.TERMINATIONS_OFFICERS_RECORD_OTHER_REASONS.getCode();

	    } else if (mode == CategoriesEnum.SOLDIERS.getCode()) {

		parameters.put("P_EMP_NAME", empName);
		reportName = ReportNamesEnum.TERMINATIONS_SOLDIERS_RECORD.getCode();

	    } else if (mode == CategoriesEnum.PERSONS.getCode()) {

		parameters.put("P_EMP_NAME", empName);
		reportName = ReportNamesEnum.TERMINATIONS_CIVILIANS_RECORD.getCode();
	    }

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    public static byte[] getCancellationTerminationDecisionBytes(Long cancelTransactionId) throws BusinessException {
	String reportName = "";
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    TerminationTransaction terminationTransaction = getTerminationTransactionsByCancelTransactionId(cancelTransactionId);

	    parameters.put("P_TRANSACTION_ID", terminationTransaction.getId());

	    if (terminationTransaction.getCategoryId().longValue() == CategoriesEnum.SOLDIERS.getCode())
		reportName = ReportNamesEnum.TERMINATIONS_DECISION_SOLDIERS_CANCEL_TRANSACTION.getCode();
	    else if (terminationTransaction.getCategoryId().longValue() == CategoriesEnum.PERSONS.getCode() || terminationTransaction.getCategoryId().longValue() == CategoriesEnum.MEDICAL_STAFF.getCode() || terminationTransaction.getCategoryId().longValue() == CategoriesEnum.USERS.getCode() || terminationTransaction.getCategoryId().longValue() == CategoriesEnum.WAGES.getCode() || terminationTransaction.getCategoryId().longValue() == CategoriesEnum.CONTRACTORS.getCode())
		reportName = ReportNamesEnum.TERMINATIONS_DECISION_CIVILIANS_CANCEL_TRANSACTION.getCode();
	    else
		throw new BusinessException("error_reportPrintingError");

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}

    }

    public static byte[] getTerminationDecisionBytes(Long transactionId, long mode, Integer transactionTypeCode) throws BusinessException {
	String reportName = "";
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    parameters.put("P_TRANSACTION_ID", transactionId);

	    // Termination
	    if (TransactionTypesEnum.STE_TERMINATION.getCode() == transactionTypeCode) {
		if (mode == CategoriesEnum.SOLDIERS.getCode())
		    reportName = ReportNamesEnum.TERMINATIONS_DECISION_SOLDIERS.getCode();
		// I have added Contractors in case of Request Termination
		else if (mode == CategoriesEnum.PERSONS.getCode() || mode == CategoriesEnum.CONTRACTORS.getCode())
		    reportName = ReportNamesEnum.TERMINATIONS_DECISION_CIVILIANS.getCode();
	    }
	    // Termination Movement
	    else if (TransactionTypesEnum.STE_TERMINATION_MOVEMENT.getCode() == transactionTypeCode) {
		reportName = ReportNamesEnum.TERMINATIONS_DECISION_CIVILIANS_TERMINATION_MOVEMENT.getCode();
	    } else if (TransactionTypesEnum.STE_TERMINATION_CANCELLATION.getCode() == transactionTypeCode) {
		if (mode == CategoriesEnum.SOLDIERS.getCode())
		    reportName = ReportNamesEnum.TERMINATIONS_DECISION_SOLDIERS_CANCEL_TRANSACTION.getCode();
		else if (mode == CategoriesEnum.PERSONS.getCode())
		    reportName = ReportNamesEnum.TERMINATIONS_DECISION_CIVILIANS_CANCEL_TRANSACTION.getCode();
	    } else
		throw new BusinessException("error_reportPrintingError");

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}

    }

}
