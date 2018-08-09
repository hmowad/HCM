package com.code.services.buswfcoop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.code.dal.orm.hcm.employees.Employee;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.movements.MovementTransaction;
import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.dal.orm.hcm.promotions.PromotionReportDetail;
import com.code.dal.orm.hcm.terminations.TerminationRecordDetail;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.hcm.missions.WFMissionDetail;
import com.code.dal.orm.workflow.hcm.movements.WFMovement;
import com.code.dal.orm.workflow.hcm.organization.jobs.WFJob;
import com.code.dal.orm.workflow.hcm.recruitments.WFRecruitment;
import com.code.dal.orm.workflow.hcm.retirements.WFDisclaimerData;
import com.code.dal.orm.workflow.hcm.terminations.WFTerminationCancellationMovement;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.PromotionsTypesEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.BaseService;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.JobsService;
import com.code.services.hcm.MovementsService;
import com.code.services.hcm.PromotionsService;
import com.code.services.hcm.TerminationsService;
import com.code.services.workflow.hcm.JobsWorkFlow;
import com.code.services.workflow.hcm.MissionsWorkFlow;
import com.code.services.workflow.hcm.MovementsWorkFlow;
import com.code.services.workflow.hcm.RecruitmentsWorkFlow;
import com.code.services.workflow.hcm.RetirementsWorkFlow;
import com.code.services.workflow.hcm.TerminationsWorkflow;
import com.code.services.workflow.hcm.TrainingEmployeesWorkFlow;
import com.code.services.workflow.hcm.VacationsWorkFlow;

public class EmployeesJobsConflictValidator extends BaseService {

    private EmployeesJobsConflictValidator() {
    }

    public static void validateEmployeesJobsConflicts(Long[] empsIds, Long[] jobsIds, int transactionClass, boolean isCancellation, Long excludedInstanceId, long... categoryId) throws BusinessException {

	if ((empsIds == null || empsIds.length == 0) && (jobsIds == null || jobsIds.length == 0))
	    return;

	if (transactionClass == TransactionClassesEnum.RECRUITMENT.getCode()) {

	    checkRunningRecruitmentsRequests(empsIds, jobsIds, excludedInstanceId);
	    checkRunningMovementsRequests(null, jobsIds, null);
	    checkRunningPromotionsRequests(null, jobsIds, null, null);
	    checkRunningTerminations(empsIds, jobsIds, null);
	    checkRunningJobsRequests(jobsIds, null);

	    checkFutureEffectMoveTransactions(null, jobsIds, false);
	    checkRunningDislaimerRequests(empsIds, null);

	} else if (transactionClass == TransactionClassesEnum.MOVEMENTS.getCode()) {

	    checkRunningRecruitmentsRequests(null, jobsIds, null);
	    checkRunningMovementsRequests(empsIds, jobsIds, excludedInstanceId);
	    checkRunningPromotionsRequests(empsIds, jobsIds, null, null);
	    checkRunningTerminations(empsIds, jobsIds, null);
	    checkRunningJobsRequests(jobsIds, null);

	    if (!isCancellation)
		checkFutureEffectMoveTransactions(empsIds, jobsIds, false);

	    if (jobsIds != null && jobsIds.length > 0)
		checktFutureEffectTerminationTransactions(empsIds);

	} else if (transactionClass == TransactionClassesEnum.PROMOTIONS.getCode()) {

	    checkRunningRecruitmentsRequests(null, jobsIds, null);
	    checkRunningMovementsRequests(empsIds, jobsIds, null);
	    checkRunningPromotionsRequests(empsIds, jobsIds, excludedInstanceId, null);
	    if (isCancellation) {
		checkRunningMissionsRequests(empsIds, null);
		checkRunningDislaimerRequests(empsIds, null);
		checkRunningTrainingsRequests(empsIds, null);
		checkRunningVacationsRequests(empsIds, null);
	    }

	    if (categoryId != null && categoryId.length != 0 && categoryId[0] == CategoriesEnum.OFFICERS.getCode())
		checkRunningTerminations(empsIds, jobsIds, null);
	    else if (isCancellation)
		checkRunningTerminations(empsIds, null, null);
	    else
		checkRunningTerminations(null, jobsIds, null);

	    checkRunningJobsRequests(jobsIds, null);

	    checkFutureEffectMoveTransactions(empsIds, jobsIds, false);
	    checktFutureEffectTerminationTransactions(empsIds);

	} else if (transactionClass == TransactionClassesEnum.TERMINATIONS.getCode()) {

	    checkRunningRecruitmentsRequests(null, jobsIds, null);
	    checkRunningMovementsRequests(empsIds, jobsIds, null);
	    checkRunningPromotionsRequests(empsIds, null, null, new Long[] { PromotionsTypesEnum.PROMOTION_CANCELLATION.getCode() });

	    if (categoryId != null && categoryId.length != 0 && categoryId[0] == CategoriesEnum.OFFICERS.getCode())
		checkRunningPromotionsRequests(empsIds, jobsIds, null, null);
	    else
		checkRunningPromotionsRequests(null, jobsIds, null, null);

	    checkRunningTerminations(empsIds, jobsIds, excludedInstanceId);
	    checkRunningJobsRequests(jobsIds, null);

	    checkFutureEffectMoveTransactions(empsIds, jobsIds, true);

	    if (!isCancellation)
		checktFutureEffectTerminationTransactions(empsIds);
	    else
		checkRunningDislaimerRequests(empsIds, null);

	} else if (transactionClass == TransactionClassesEnum.JOBS.getCode()) {

	    checkRunningRecruitmentsRequests(null, jobsIds, null);
	    checkRunningMovementsRequests(null, jobsIds, null);
	    checkRunningPromotionsRequests(null, jobsIds, null, null);
	    checkRunningTerminations(null, jobsIds, null);
	    checkRunningJobsRequests(jobsIds, excludedInstanceId);

	    checkFutureEffectMoveTransactions(null, jobsIds, false);

	} else if (transactionClass == TransactionClassesEnum.RETIREMENTS.getCode()) {
	    checkRunningDislaimerRequests(empsIds, excludedInstanceId);
	    checkRunningTerminationCancellationMovements(empsIds, null, null);
	    checkRunningPromotionsRequests(empsIds, null, null, new Long[] { PromotionsTypesEnum.PROMOTION_CANCELLATION.getCode() });
	}
    }

    private static void checkRunningRecruitmentsRequests(Long[] employeesIds, Long[] jobsIds, Long excludedInstanceId) throws BusinessException {

	if ((employeesIds == null || employeesIds.length == 0) && (jobsIds == null || jobsIds.length == 0))
	    return;

	List<WFRecruitment> wfRecruitmentsRunningRequests = RecruitmentsWorkFlow.getRunningRequests(employeesIds, jobsIds, excludedInstanceId);
	if (wfRecruitmentsRunningRequests.size() > 0) {
	    Set<Long> employeesIdsSet = getSetByArray(employeesIds);
	    Set<Long> jobsIdsSet = getSetByArray(jobsIds);
	    List<Long> conflictEmployeesIds = new ArrayList<Long>();
	    List<Long> conflictJobsIds = new ArrayList<Long>();
	    for (WFRecruitment wfRecruitment : wfRecruitmentsRunningRequests) {
		if (employeesIdsSet.contains(wfRecruitment.getEmployeeId()))
		    conflictEmployeesIds.add(wfRecruitment.getEmployeeId());
		if (jobsIdsSet.contains(wfRecruitment.getJobId()))
		    conflictJobsIds.add(wfRecruitment.getJobId());
	    }

	    constructBusinessException(conflictEmployeesIds, conflictJobsIds, "error_conflictRecruitmentsEmployees", "error_conflictRecruitmentsJobs", "error_conflictRecruitmentsJobsEmployees");
	}
    }

    private static void checkRunningMovementsRequests(Long[] employeesIds, Long[] jobsIds, Long excludedInstanceId) throws BusinessException {

	if ((employeesIds == null || employeesIds.length == 0) && (jobsIds == null || jobsIds.length == 0))
	    return;

	List<WFMovement> wfMovementsRunningRequests = MovementsWorkFlow.getRunningRequests(employeesIds, jobsIds, excludedInstanceId);
	if (wfMovementsRunningRequests.size() > 0) {
	    Set<Long> employeesIdsSet = getSetByArray(employeesIds);
	    Set<Long> jobsIdsSet = getSetByArray(jobsIds);
	    List<Long> conflictEmployeesIds = new ArrayList<Long>();
	    List<Long> conflictJobsIds = new ArrayList<Long>();
	    for (WFMovement wfMovement : wfMovementsRunningRequests) {
		if (employeesIdsSet.contains(wfMovement.getEmployeeId()))
		    conflictEmployeesIds.add(wfMovement.getEmployeeId());
		if (employeesIdsSet.contains(wfMovement.getReplacementEmployeeId()))
		    conflictEmployeesIds.add(wfMovement.getReplacementEmployeeId());
		if (jobsIdsSet.contains(wfMovement.getJobId()))
		    conflictJobsIds.add(wfMovement.getJobId());
		if (jobsIdsSet.contains(wfMovement.getFreezeJobId()))
		    conflictJobsIds.add(wfMovement.getFreezeJobId());
	    }

	    constructBusinessException(conflictEmployeesIds, conflictJobsIds, "error_conflictMovementsEmployees", "error_conflictMovementsJobs", "error_conflictMovementsJobsEmployees");
	}
    }

    // Excluded promotions report Id is passed using the excluded instance id from the promotions module only, other modules pass null.
    private static void checkRunningPromotionsRequests(Long[] employeesIds, Long[] jobsIds, Long excludedReportId, Long[] promotionTypesIds) throws BusinessException {

	if ((employeesIds == null || employeesIds.length == 0) && (jobsIds == null || jobsIds.length == 0))
	    return;

	List<PromotionReportDetail> promotionReportDetails = PromotionsService.searchRunningReports(employeesIds, jobsIds, excludedReportId, promotionTypesIds);

	if (promotionReportDetails.size() > 0) {
	    Set<Long> employeesIdsSet = getSetByArray(employeesIds);
	    Set<Long> jobsIdsSet = getSetByArray(jobsIds);
	    List<Long> conflictEmployeesIds = new ArrayList<Long>();
	    List<Long> conflictJobsIds = new ArrayList<Long>();
	    for (PromotionReportDetail promotionReportDetail : promotionReportDetails) {
		if (employeesIdsSet.contains(promotionReportDetail.getEmpId()))
		    conflictEmployeesIds.add(promotionReportDetail.getEmpId());
		if (jobsIdsSet.contains(promotionReportDetail.getNewJobId()))
		    conflictJobsIds.add(promotionReportDetail.getNewJobId());
		if (jobsIdsSet.contains(promotionReportDetail.getFreezedJobId()))
		    conflictJobsIds.add(promotionReportDetail.getFreezedJobId());
	    }

	    constructBusinessException(conflictEmployeesIds, conflictJobsIds, "error_conflictPromotionsEmployees", "error_conflictPromotionsJobs", "error_conflictPromotionsJobsEmployees");
	}
    }

    private static void checkRunningTerminations(Long[] employeesIds, Long[] jobsIds, Long excludedInstanceId) throws BusinessException {

	if ((employeesIds == null || employeesIds.length == 0) && (jobsIds == null || jobsIds.length == 0))
	    return;

	if (employeesIds != null && employeesIds.length > 0) {
	    checkRunningTerminationsRecords(employeesIds, excludedInstanceId);
	    checkRunningTerminationsRequests(employeesIds, excludedInstanceId);
	    checkRunningTerminationCancellationMovements(employeesIds, jobsIds, excludedInstanceId);
	} else {
	    checkRunningTerminationCancellationMovements(employeesIds, jobsIds, excludedInstanceId);
	}
    }

    private static void checkRunningTerminationsRecords(Long[] employeesIds, Long excludedInstanceId) throws BusinessException {

	List<TerminationRecordDetail> terminationRecordDetails = TerminationsService.getRunningRecords(employeesIds, excludedInstanceId);
	if (terminationRecordDetails.size() > 0) {
	    List<Long> conflictEmployeesIds = new ArrayList<Long>();
	    for (TerminationRecordDetail terminationRecordDetail : terminationRecordDetails)
		conflictEmployeesIds.add(terminationRecordDetail.getEmpId());

	    constructBusinessException(conflictEmployeesIds, null, "error_conflictTerminationsRecordEmployees", null, null);
	}
    }

    private static void checkRunningTerminationsRequests(Long[] employeesIds, Long excludedInstanceId) throws BusinessException {

	List<WFInstance> wfInstances = TerminationsWorkflow.getRunningRequests(employeesIds, excludedInstanceId);
	if (wfInstances.size() > 0) {
	    List<Long> conflictEmployeesIds = new ArrayList<Long>();
	    for (WFInstance wfInstance : wfInstances)
		conflictEmployeesIds.add(wfInstance.getRequesterId());

	    constructBusinessException(conflictEmployeesIds, null, "error_conflictTerminationsRequestEmployees", null, null);
	}
    }

    private static void checkRunningTerminationCancellationMovements(Long[] employeesIds, Long[] jobsIds, Long excludedInstanceId) throws BusinessException {

	List<WFTerminationCancellationMovement> wfTerminationsCancellationMovements = TerminationsWorkflow.getRunningTerminationCancellationMovements(employeesIds, jobsIds, excludedInstanceId);
	if (wfTerminationsCancellationMovements.size() > 0) {
	    Set<Long> employeesIdsSet = getSetByArray(employeesIds);
	    Set<Long> jobsIdsSet = getSetByArray(jobsIds);
	    List<Long> conflictEmployeesIds = new ArrayList<Long>();
	    List<Long> conflictJobsIds = new ArrayList<Long>();
	    for (WFTerminationCancellationMovement wfTerminationsCancellationMovement : wfTerminationsCancellationMovements) {
		if (employeesIdsSet.contains(wfTerminationsCancellationMovement.getEmpId()))
		    conflictEmployeesIds.add(wfTerminationsCancellationMovement.getEmpId());
		if (jobsIdsSet.contains(wfTerminationsCancellationMovement.getJobId()))
		    conflictJobsIds.add(wfTerminationsCancellationMovement.getJobId());
	    }

	    constructBusinessException(conflictEmployeesIds, conflictJobsIds, "error_conflictTerminationsCancellationRequestEmployees", "error_conflictTerminationsCancellationRequestJobs", "error_conflictTerminationsCancellationRequestJobsEmployees");
	}
    }

    private static void checkFutureEffectMoveTransactions(Long[] employeesIds, Long[] jobsIds, boolean moveFlag) throws BusinessException {

	if ((employeesIds == null || employeesIds.length == 0) && (jobsIds == null || jobsIds.length == 0))
	    return;

	List<MovementTransaction> movementsTransactions = MovementsService.getFutureEffectMoveTransactions(employeesIds, jobsIds, moveFlag);
	if (movementsTransactions.size() > 0) {
	    Set<Long> employeesIdsSet = getSetByArray(employeesIds);
	    Set<Long> jobsIdsSet = getSetByArray(jobsIds);
	    List<Long> conflictEmployeesIds = new ArrayList<Long>();
	    List<Long> conflictJobsIds = new ArrayList<Long>();
	    for (MovementTransaction movementsTransaction : movementsTransactions) {
		if (employeesIdsSet.contains(movementsTransaction.getEmployeeId()))
		    conflictEmployeesIds.add(movementsTransaction.getEmployeeId());
		if (jobsIdsSet.contains(movementsTransaction.getJobId()))
		    conflictJobsIds.add(movementsTransaction.getJobId());
		if (jobsIdsSet.contains(movementsTransaction.getFreezeJobId()))
		    conflictJobsIds.add(movementsTransaction.getFreezeJobId());
	    }

	    constructBusinessException(conflictEmployeesIds, conflictJobsIds, "error_conflictFutureMovementsEmployees", "error_conflictFutureMovementsJobs", "error_conflictFutureMovementsJobsEmployees");
	}
    }

    private static void checktFutureEffectTerminationTransactions(Long[] employeesIds) throws BusinessException {

	if (employeesIds == null || employeesIds.length == 0)
	    return;

	List<Employee> employees = EmployeesService.getFutureServiceTerminatedEmployees(employeesIds);
	if (employees.size() > 0) {
	    List<Long> conflictEmployeesIds = new ArrayList<Long>();
	    for (Employee employee : employees)
		conflictEmployeesIds.add(employee.getId());

	    constructBusinessException(conflictEmployeesIds, null, "error_conflictFutureTerminationsTransationEmployees", null, null);
	}
    }

    private static void checkRunningJobsRequests(Long[] jobsIds, Long excludedInstanceId) throws BusinessException {

	if (jobsIds == null || jobsIds.length == 0)
	    return;

	List<WFJob> wfJobsRunningRequests = JobsWorkFlow.getRunningRequests(jobsIds, excludedInstanceId);
	if (wfJobsRunningRequests.size() > 0) {
	    List<Long> conflictJobsIds = new ArrayList<Long>();
	    for (WFJob wfJob : wfJobsRunningRequests) {
		conflictJobsIds.add(wfJob.getJobId());
	    }
	    constructBusinessException(null, conflictJobsIds, null, "error_conflictJobs", null);
	}
    }

    private static void checkRunningDislaimerRequests(Long[] empsIds, Long excludedInstanceId) throws BusinessException {

	if (empsIds == null || empsIds.length == 0)
	    return;

	List<WFDisclaimerData> wfRunningDisclaimerData = RetirementsWorkFlow.getRunningDisclaimerRequests(empsIds, excludedInstanceId);
	if (wfRunningDisclaimerData.size() > 0) {
	    List<Long> conflictDisclaimerEmpsIds = new ArrayList<Long>();
	    for (WFDisclaimerData wfDisclaimerData : wfRunningDisclaimerData) {
		conflictDisclaimerEmpsIds.add(wfDisclaimerData.getEmpId());
	    }
	    constructBusinessException(conflictDisclaimerEmpsIds, null, "error_runningDisclaimerRequestExists", null, null);
	}
    }

    private static void checkRunningMissionsRequests(Long[] employeesIds, Long excludedInstanceId) throws BusinessException {

	if (employeesIds == null || employeesIds.length == 0)
	    return;

	List<WFMissionDetail> wfMissionssRunningRequests = MissionsWorkFlow.validateRunningMissionRequests(employeesIds, null, null, excludedInstanceId);
	if (wfMissionssRunningRequests.size() > 0) {
	    Set<Long> employeesIdsSet = getSetByArray(employeesIds);
	    List<Long> conflictEmployeesIds = new ArrayList<Long>();
	    for (WFMissionDetail wfMission : wfMissionssRunningRequests) {
		if (employeesIdsSet.contains(wfMission.getEmpId()))
		    conflictEmployeesIds.add(wfMission.getEmpId());
	    }
	    constructBusinessException(conflictEmployeesIds, null, "error_wfMissionsRequestsConflict", null, null);
	}
    }

    private static void checkRunningTrainingsRequests(Long[] employeesIds, Long excludedInstanceId) throws BusinessException {

	if (employeesIds == null || employeesIds.length == 0)
	    return;

	List<WFTrainingData> wfTrainingData = TrainingEmployeesWorkFlow.getRunningWFTrainingDataByEmployeesIds(employeesIds, excludedInstanceId == null ? FlagsEnum.ALL.getCode() : excludedInstanceId);
	if (wfTrainingData.size() > 0) {
	    Set<Long> employeesIdsSet = getSetByArray(employeesIds);
	    List<Long> conflictEmployeesIds = new ArrayList<Long>();
	    for (WFTrainingData WFTraining : wfTrainingData) {
		if (employeesIdsSet.contains(WFTraining.getEmployeeId()))
		    conflictEmployeesIds.add(WFTraining.getEmployeeId());
	    }
	    constructBusinessException(conflictEmployeesIds, null, "error_conflictTrainigsEmployees", null, null);
	}
    }

    private static void checkRunningVacationsRequests(Long[] employeesIds, Long excludedInstanceId) throws BusinessException {

	if (employeesIds == null || employeesIds.length == 0)
	    return;
	VacationsWorkFlow.validateVacationRunningWFInstances(employeesIds[0], null);
    }

    private static void constructBusinessException(List<Long> employeesIds, List<Long> jobsIds, String employeesOnlyErrorMessage, String jobsOnlyErrorMessage, String employeesJobsErrorMessage) throws BusinessException {

	String comma = ",\n";

	StringBuffer employeesNames = new StringBuffer();
	StringBuffer jobsCodes = new StringBuffer();

	String[] errorMessageArgs = new String[(employeesIds != null && employeesIds.size() > 0 && jobsIds != null && jobsIds.size() > 0) ? 2 : 1];
	int errorMessageArgsIndex = 0;

	if (employeesIds != null && employeesIds.size() > 0) {
	    List<EmployeeData> employees = EmployeesService.getEmployeesByEmpsIds(employeesIds.toArray(new Long[employeesIds.size()]));

	    for (EmployeeData employeeData : employees) {
		employeesNames.append(employeeData.getName());
		employeesNames.append(comma);
	    }
	    errorMessageArgs[errorMessageArgsIndex++] = employeesNames.substring(0, employeesNames.length() - 2);
	}

	if (jobsIds != null && jobsIds.size() > 0) {
	    List<JobData> jobs = JobsService.getJobsByJobsIds(jobsIds.toArray(new Long[jobsIds.size()]));

	    for (JobData jobData : jobs) {
		jobsCodes.append(jobData.getCode());
		jobsCodes.append(" - ");
		jobsCodes.append(jobData.getName());
		jobsCodes.append(comma);
	    }

	    errorMessageArgs[errorMessageArgsIndex] = jobsCodes.substring(0, jobsCodes.length() - 2);
	}

	throw new BusinessException((employeesIds != null && employeesIds.size() > 0 && jobsIds != null && jobsIds.size() > 0) ? employeesJobsErrorMessage : ((employeesIds != null && employeesIds.size() > 0) ? employeesOnlyErrorMessage : jobsOnlyErrorMessage), errorMessageArgs);
    }

    private static Set<Long> getSetByArray(Long[] ids) {
	if (ids == null)
	    return new HashSet<Long>();
	else
	    return new HashSet<Long>(Arrays.asList(ids));
    }
}
