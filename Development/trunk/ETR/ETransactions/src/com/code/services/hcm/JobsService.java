package com.code.services.hcm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.organization.jobs.BasicJobNameData;
import com.code.dal.orm.hcm.organization.jobs.JobClassification;
import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.dal.orm.hcm.organization.jobs.JobTransaction;
import com.code.dal.orm.hcm.organization.jobs.JobTransactionData;
import com.code.dal.orm.hcm.organization.jobs.JobsBalanceData;
import com.code.dal.orm.log.EmployeeLog;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.GeneralSpecializationsEnum;
import com.code.enums.JobReservationStatusEnum;
import com.code.enums.JobStatusEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.ReportNamesEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.buswfcoop.BusinessWorkflowCooperation;
import com.code.services.buswfcoop.EmployeesJobsConflictValidator;
import com.code.services.log.LogService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;

/**
 * The class <code>JobsService</code> provides methods for handling the jobs operations such as add, rename, freeze, unfreeze, scale, move, reserve, unreserve, or cancel jobs.
 * 
 * These methods are validating the job operation, manipulating the job data, obtaining the job classifications and job transactions.
 * 
 */
public class JobsService extends BaseService {

    /**
     * A constant holding the minimum sequence for the job series and code.
     */
    private static final int MIN_JOB_SEQUENCE = 00001;

    /**
     * A constant holding the maximum sequence for the job series and code.
     */
    private static final int MAX_JOB_SEQUENCE = 99999;

    private static final int JOB_SEQUENCE_LENGTH = 5;

    private static final String FORBIDDEN_JOB_SEQUENCE = "00000";

    /**
     * A constant holding the format of the sequence for the job series and code which should be five digits.
     */
    private static final String JOB_SEQUENCE_FORMAT = "%05d";

    private static final String CONTRACTORS_BASE_RANK = "600";

    /**
     * Constructs a newly allocated <code>JobsService</code> object.
     */
    private JobsService() {
    }

    /*---------------------------------------------- Jobs operations-------------------------------------------------------------*/

    public static void handleJobsTransactions(List<JobData> jobsToAdd, List<JobData> jobsToRename, List<JobData> jobsToCancel, List<JobData> jobsToFreeze,
	    List<JobData> jobsToUnfreeze, List<JobData> jobsToScale, List<JobData> jobsToMove, List<JobData> jobsToModifyMinorSpecs,
	    String decisionNumber, Date decisionDate, Date executionDate, Long userId,
	    boolean validateJobsConflicts, boolean validateJobsStatusForScale, boolean validateJobsStatusForFreeze, boolean validateJobsBalances, CustomSession... useSession) throws BusinessException {

	JobTransaction decisionData = new JobTransaction();
	decisionData.setDecisionNumber(decisionNumber);
	decisionData.setDecisionDate(decisionDate);
	decisionData.setExecutionDate(executionDate);
	decisionData.setTransactionUserId(userId);
	decisionData.seteFlag(FlagsEnum.OFF.getCode());

	handleJobsTransactions(jobsToAdd, jobsToRename, jobsToCancel, jobsToFreeze, jobsToUnfreeze, jobsToScale, jobsToMove, jobsToModifyMinorSpecs,
		decisionData, validateJobsConflicts, validateJobsStatusForScale, validateJobsStatusForFreeze, validateJobsBalances, useSession);
    }

    public static void handleJobsTransactions(List<JobData> jobsToAdd, List<JobData> jobsToRename, List<JobData> jobsToCancel, List<JobData> jobsToFreeze,
	    List<JobData> jobsToUnfreeze, List<JobData> jobsToScale, List<JobData> jobsToMove, List<JobData> jobsToModifyMinorSpecs,
	    JobTransaction decisionData, CustomSession... useSession) throws BusinessException {

	handleJobsTransactions(jobsToAdd, jobsToRename, jobsToCancel, jobsToFreeze, jobsToUnfreeze, jobsToScale, jobsToMove, jobsToModifyMinorSpecs,
		decisionData, false, true, true, true, useSession);
    }

    /**
     * Handles any job transaction by validating the data then inserting and updating the job data and finally adding a transaction to keep the history for all jobs operations. </br>
     * The transactions are:
     * <ul>
     * <li>Adding new job</li>
     * <li>Renaming a job</li>
     * <li>Canceling a job</li>
     * <li>Freezing a job for Soldiers only for one of these reasons like (Formation, Designation, Transfer, Promotion, Another)</li>
     * <li>UnFreezing a job for Soldiers only</li>
     * <li>Scaling a job up or down</li>
     * <li>Moving a job from unit to another unit for Employees only</li>
     * </ul>
     * 
     * @param jobsToAdd
     *            a <code>List</code> of {@link JobData} to be added
     * @param jobsToRename
     *            a <code>List</code> of {@link JobData} to be renamed
     * @param jobsToCancel
     *            a <code>List</code> of {@link JobData} to be canceled
     * @param jobsToFreeze
     *            a <code>List</code> of {@link JobData} to be frozen
     * @param jobsToUnfreeze
     *            a <code>List</code> of {@link JobData} to be unfrozen
     * @param jobsToScale
     *            a <code>List</code> of {@link JobData} to be scaled
     * @param jobsToMove
     *            a <code>List</code> of {@link JobData} to be moved
     * @param decisionNumber
     *            the job transaction decision number
     * @param decisionDate
     *            the job transaction decision date in mm/MM/yyyy format
     * @param executionDate
     *            the job transaction decision execution date in mm/MM/yyyy format
     * @param userId
     *            the ID of the user who performed the job transaction
     * @param validateJobsConflicts
     *            a flag indicates whether to check if all the job lists don't have conflicts among each others or don't apply this check
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any validation fails or if any general error occurs
     */
    private static void handleJobsTransactions(List<JobData> jobsToAdd, List<JobData> jobsToRename, List<JobData> jobsToCancel, List<JobData> jobsToFreeze,
	    List<JobData> jobsToUnfreeze, List<JobData> jobsToScale, List<JobData> jobsToMove, List<JobData> jobsToModifyMinorSpecs,
	    JobTransaction decisionData, boolean validateJobsConflicts, boolean validateJobsStatusForScale, boolean validateJobsStatusForFreeze, boolean validateJobsBalances, CustomSession... useSession) throws BusinessException {

	adjustJobsTransactionsData(jobsToAdd, jobsToRename, jobsToScale, decisionData);

	List<JobsBalanceData> jobsBalances = validateJobs(jobsToAdd, jobsToRename, jobsToCancel, jobsToFreeze, jobsToUnfreeze, jobsToScale, jobsToMove, jobsToModifyMinorSpecs,
		decisionData.getDecisionNumber(), decisionData.getDecisionDate(), decisionData.getExecutionDate(),
		validateJobsConflicts, validateJobsStatusForScale, validateJobsStatusForFreeze, validateJobsBalances);

	Map<Long, List<String>> regionsNewSerials = new HashMap<Long, List<String>>();
	if ((jobsToAdd != null && jobsToAdd.size() > 0 &&
		(CategoriesEnum.OFFICERS.getCode() == jobsToAdd.get(0).getCategoryId().longValue()
			|| CategoriesEnum.SOLDIERS.getCode() == jobsToAdd.get(0).getCategoryId().longValue()))

		|| (jobsToMove != null && jobsToMove.size() > 0 &&
			(CategoriesEnum.OFFICERS.getCode() == jobsToMove.get(0).getCategoryId().longValue()
				|| CategoriesEnum.SOLDIERS.getCode() == jobsToMove.get(0).getCategoryId().longValue()))) {
	    regionsNewSerials = getRegionsNewSerials(jobsToAdd, jobsToMove);
	}

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    if (jobsToAdd != null && jobsToAdd.size() > 0)
		addJobs(jobsToAdd, decisionData, regionsNewSerials, session);

	    if (jobsToRename != null && jobsToRename.size() > 0)
		renameJobs(jobsToRename, decisionData, session);

	    if (jobsToCancel != null && jobsToCancel.size() > 0)
		cancelJobs(jobsToCancel, decisionData, session);

	    if (jobsToFreeze != null && jobsToFreeze.size() > 0)
		freezeJobs(jobsToFreeze, decisionData, session);

	    if (jobsToUnfreeze != null && jobsToUnfreeze.size() > 0)
		unfreezeJobs(jobsToUnfreeze, decisionData, session);

	    if (jobsToScale != null && jobsToScale.size() > 0)
		scaleJobs(jobsToScale, decisionData, session);

	    if (jobsToMove != null && jobsToMove.size() > 0)
		moveJobs(jobsToMove, decisionData, regionsNewSerials, session);

	    if (jobsToModifyMinorSpecs != null && jobsToModifyMinorSpecs.size() > 0)
		modifyJobsMinorSpecs(jobsToModifyMinorSpecs, decisionData, session);

	    // To handle the concurrency over the jobs balances.
	    if (validateJobsBalances)
		JobsBalancesService.modifyJobsBalances(jobsBalances, session);

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

    private static void adjustJobsTransactionsData(List<JobData> jobsToAdd, List<JobData> jobsToRename, List<JobData> jobsToScale, JobTransaction decisionData) {
	if (jobsToAdd != null && jobsToAdd.size() > 0)
	    adjustCiviliansJobsSerials(jobsToAdd, false);

	if (jobsToRename != null && jobsToRename.size() > 0)
	    adjustCiviliansJobsSerials(jobsToRename, false);

	if (jobsToScale != null && jobsToScale.size() > 0)
	    adjustCiviliansJobsSerials(jobsToScale, true);

	if (decisionData.getDecisionNumber() != null)
	    decisionData.setDecisionNumber(decisionData.getDecisionNumber().trim());
    }

    private static void adjustCiviliansJobsSerials(List<JobData> jobsData, boolean isScaleTransaction) {
	if (jobsData.get(0).getCategoryId() != null && CategoriesEnum.OFFICERS.getCode() != jobsData.get(0).getCategoryId().longValue()
		&& CategoriesEnum.SOLDIERS.getCode() != jobsData.get(0).getCategoryId().longValue()) {
	    for (JobData jobData : jobsData) {
		if (CategoriesEnum.CONTRACTORS.getCode() == jobData.getCategoryId().longValue())
		    jobData.setSerial(CONTRACTORS_BASE_RANK + jobData.getSequence());
		else if (CategoriesEnum.MEDICAL_STAFF.getCode() == jobData.getCategoryId().longValue())
		    jobData.setSerial(CommonService.getRankById(isScaleTransaction ? jobData.getNewRankId() : jobData.getRankId()).getSubCategoryId() + jobData.getSequence());
		else
		    jobData.setSerial((isScaleTransaction ? jobData.getNewRankId() : jobData.getRankId()) + jobData.getSequence());
	    }
	}
    }

    /**
     * Creates a list of new jobs then adds a transaction of type JOB_CREATION for each created job.
     * 
     * @param jobsToAdd
     *            a <code>List</code> of {@link JobData} to be added
     * @param decisionNumber
     *            the job transaction decision number
     * @param decisionDate
     *            the job transaction decision date in mm/MM/yyyy format
     * @param executionDate
     *            the job transaction decision execution date in mm/MM/yyyy format
     * @param userId
     *            the ID of the user who performed the job transaction
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    private static void addJobs(List<JobData> jobsData, JobTransaction decisionData, Map<Long, List<String>> regionsNewSerials, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (JobData jobData : jobsData) {
		if (CategoriesEnum.OFFICERS.getCode() == jobData.getCategoryId().longValue()
			|| CategoriesEnum.SOLDIERS.getCode() == jobData.getCategoryId().longValue()) {
		    String serial = regionsNewSerials.get(jobData.getRegionId()).get(0);
		    regionsNewSerials.get(jobData.getRegionId()).remove(0);
		    jobData.setSerial(serial);
		    jobData.setCode(jobData.getRankId() + serial.substring(1)); // Replace the category at the serial with the rank.
		} else {
		    jobData.setCode(jobData.getRankId() + CommonService.getRegionById(jobData.getRegionId()).getCode() + jobData.getSequence());
		}

		if (jobData.getCategoryId().longValue() != CategoriesEnum.SOLDIERS.getCode())
		    jobData.setApprovedFlag(FlagsEnum.ON.getCode());
		else if (jobData.getApprovedFlag().intValue() == FlagsEnum.OFF.getCode())
		    jobData.setStatus(JobStatusEnum.FROZEN.getCode());

		jobData.setReservationStatus(JobReservationStatusEnum.UN_RESERVED.getCode());

		if (jobData.getCategoryId().longValue() != CategoriesEnum.OFFICERS.getCode() && jobData.getCategoryId().longValue() != CategoriesEnum.SOLDIERS.getCode())
		    jobData.setGeneralSpecialization(GeneralSpecializationsEnum.OVERLAND.getCode());

		jobData.setExecutionDate(decisionData.getExecutionDate());

		DataAccess.addEntity(jobData.getJob(), session);
		jobData.setId(jobData.getJob().getId());
		addJobTransaction(jobData, TransactionTypesEnum.JOB_CONSTRUCTION.getCode(), decisionData,
			jobData.getCode(), jobData.getBasicJobNameId(), jobData.getUnitId(), jobData.getUnitFullName(), jobData.getRankId(), jobData.getMinorSpecializationId(), useSession);
	    }

	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    for (JobData jobData : jobsData) {
		jobData.setId(null);
		jobData.setCode(null);
		jobData.setSerial(null);
	    }

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    /**
     * Changes the names of a specific list of jobs then adds a transaction of type JOB_RENAME for each renamed job.
     * 
     * @param jobsData
     *            a <code>List</code> of {@link JobData} to be renamed
     * @param decisionNumber
     *            the job transaction decision number
     * @param decisionDate
     *            the job transaction decision date in mm/MM/yyyy format
     * @param executionDate
     *            the job transaction decision execution date in mm/MM/yyyy format
     * @param userId
     *            the ID of the user who performed the job transaction
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    private static void renameJobs(List<JobData> jobsData, JobTransaction decisionData, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	Map<Long, String> jobsOriginalCodes = new HashMap<Long, String>();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (JobData jobData : jobsData) {
		Long transBasicJobNameId = jobData.getBasicJobNameId();
		String transCode = jobData.getCode();

		jobData.setBasicJobNameId(jobData.getNewBasicJobNameId());
		jobData.setName(jobData.getNewName());
		jobsOriginalCodes.put(jobData.getId(), jobData.getCode());

		if (CategoriesEnum.OFFICERS.getCode() != jobData.getCategoryId().longValue()
			&& CategoriesEnum.SOLDIERS.getCode() != jobData.getCategoryId().longValue())
		    jobData.setCode(jobData.getCode().substring(0, 5) + jobData.getSequence());

		if (jobData.getCategoryId().longValue() == CategoriesEnum.PERSONS.getCode() && jobData.getNewClassificationId() != null)
		    jobData.setClassificationId(jobData.getNewClassificationId());

		jobData.setExecutionDate(decisionData.getExecutionDate());
		DataAccess.updateEntity(jobData.getJob(), session);
		addJobTransaction(jobData, TransactionTypesEnum.JOB_RENAME.getCode(), decisionData,
			transCode, transBasicJobNameId, jobData.getUnitId(), jobData.getUnitFullName(), jobData.getRankId(), jobData.getMinorSpecializationId(), session);

		if (jobData.getStatus().intValue() == JobStatusEnum.OCCUPIED.getCode()) {
		    EmployeeLog employeeLog = new EmployeeLog.Builder()
			    .setBasicJobNameId(jobData.getBasicJobNameId())
			    .constructCommonFields(jobData.getEmployeeId(), FlagsEnum.ON.getCode(), decisionData.getDecisionNumber(), decisionData.getDecisionDate(), HijriDateService.getHijriSysDate(), DataAccess.getTableName(JobTransaction.class))
			    .build();
		    LogService.logEmployeeData(employeeLog, session);

		}
	    }

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    for (JobData jobData : jobsData) {
		String originalJobCode = jobsOriginalCodes.get(jobData.getId());
		jobData.setCode(originalJobCode);
	    }

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    /**
     * Cancels a list of jobs then adds a transaction of type JOB_CANCELLATION for each canceled job.
     * 
     * @param jobsData
     *            a <code>List</code> of {@link JobData} to be canceled
     * @param decisionNumber
     *            the job transaction decision number
     * @param decisionDate
     *            the job transaction decision date in mm/MM/yyyy format
     * @param executionDate
     *            the job transaction decision execution date in mm/MM/yyyy format
     * @param userId
     *            the ID of the user who performed the job transaction
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    private static void cancelJobs(List<JobData> jobsData, JobTransaction decisionData, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (JobData jobData : jobsData) {
		jobData.setSerial(null);
		jobData.setStatus(JobStatusEnum.CANCELED.getCode());
		jobData.setExecutionDate(decisionData.getExecutionDate());
		DataAccess.updateEntity(jobData.getJob(), session);
		addJobTransaction(jobData, TransactionTypesEnum.JOB_CANCELLATION.getCode(), decisionData,
			jobData.getCode(), jobData.getBasicJobNameId(), jobData.getUnitId(), jobData.getUnitFullName(), jobData.getRankId(), jobData.getMinorSpecializationId(), session);
	    }

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

    /**
     * Freezes a list of jobs then adds a transaction of type JOB_FREEZE for each freezed job.
     * 
     * @param jobsData
     *            a <code>List</code> of {@link JobData} to be frozen
     * @param decisionNumber
     *            the job transaction decision number
     * @param decisionDate
     *            the job transaction decision date in mm/MM/yyyy format
     * @param executionDate
     *            the job transaction decision execution date in mm/MM/yyyy format
     * @param userId
     *            the ID of the user who performed the job transaction
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    private static void freezeJobs(List<JobData> jobsData, JobTransaction decisionData, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (JobData jobData : jobsData) {
		jobData.setStatus(JobStatusEnum.FROZEN.getCode());
		jobData.setApprovedFlag(FlagsEnum.OFF.getCode());
		jobData.setExecutionDate(decisionData.getExecutionDate());

		jobData.setReservationRemarks(null);
		jobData.setReservationStatus(JobReservationStatusEnum.UN_RESERVED.getCode());

		DataAccess.updateEntity(jobData.getJob(), session);
		addJobTransaction(jobData, TransactionTypesEnum.JOB_FREEZE.getCode(), decisionData,
			jobData.getCode(), jobData.getBasicJobNameId(), jobData.getUnitId(), jobData.getUnitFullName(), jobData.getRankId(), jobData.getMinorSpecializationId(), session);
	    }

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

    /**
     * UnFreezes a list of jobs then adds a transaction of type JOB_UNFREEZE for each unfreezed job.
     * 
     * @param jobsData
     *            a <code>List</code> of {@link JobData} to be unfrozen
     * @param decisionNumber
     *            the job transaction decision number
     * @param decisionDate
     *            the job transaction decision date in mm/MM/yyyy format
     * @param executionDate
     *            the job transaction decision execution date in mm/MM/yyyy format
     * @param userId
     *            the ID of the user who performed the job transaction
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    private static void unfreezeJobs(List<JobData> jobsData, JobTransaction decisionData, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (JobData jobData : jobsData) {
		jobData.setStatus(JobStatusEnum.VACANT.getCode());
		jobData.setApprovedFlag(FlagsEnum.ON.getCode());
		jobData.setExecutionDate(decisionData.getExecutionDate());
		DataAccess.updateEntity(jobData.getJob(), session);
		addJobTransaction(jobData, TransactionTypesEnum.JOB_UNFREEZE.getCode(), decisionData,
			jobData.getCode(), jobData.getBasicJobNameId(), jobData.getUnitId(), jobData.getUnitFullName(), jobData.getRankId(), jobData.getMinorSpecializationId(), session);
	    }

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

    /**
     * Scales up or down a list of jobs then adds a transaction of type JOB_SCALE_UP or JOB_SCALE_DOWN for each scaled job.
     * 
     * @param jobsData
     *            a <code>List</code> of {@link JobData} to be scaled
     * @param decisionNumber
     *            the job transaction decision number
     * @param decisionDate
     *            the job transaction decision date in mm/MM/yyyy format
     * @param executionDate
     *            the job transaction decision execution date in mm/MM/yyyy format
     * @param userId
     *            the ID of the user who performed the job transaction
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    private static void scaleJobs(List<JobData> jobsData, JobTransaction decisionData, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	Map<Long, String> jobsOriginalCodes = new HashMap<Long, String>();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (JobData jobData : jobsData) {
		// Note: The greater the rank code the lower the rank
		boolean isScaleUp = (jobData.getRankId().longValue() > jobData.getNewRankId().longValue()) ? true : false;

		Long transBasicJobNameId = jobData.getBasicJobNameId();
		String transCode = jobData.getCode();
		Long transRankId = jobData.getRankId();

		jobData.setRankId(jobData.getNewRankId());
		jobsOriginalCodes.put(jobData.getId(), jobData.getCode());

		if (CategoriesEnum.OFFICERS.getCode() == jobData.getCategoryId().longValue()
			|| CategoriesEnum.SOLDIERS.getCode() == jobData.getCategoryId().longValue()) {
		    jobData.setCode(jobData.getNewRankId() + jobData.getCode().substring(3));
		} else {
		    jobData.setCode(jobData.getNewRankId() + CommonService.getRegionById(jobData.getRegionId()).getCode() + jobData.getSequence());
		}

		if (jobData.getCategoryId().longValue() == CategoriesEnum.PERSONS.getCode())
		    jobData.setClassificationId(jobData.getNewClassificationId());

		jobData.setExecutionDate(decisionData.getExecutionDate());

		if (jobData.getNewBasicJobNameId() != null) {
		    jobData.setBasicJobNameId(jobData.getNewBasicJobNameId());
		    jobData.setName(jobData.getNewName());
		}

		DataAccess.updateEntity(jobData.getJob(), session);

		addJobTransaction(jobData, isScaleUp ? TransactionTypesEnum.JOB_SCALE_UP.getCode() : TransactionTypesEnum.JOB_SCALE_DOWN.getCode(), decisionData,
			transCode, transBasicJobNameId, jobData.getUnitId(), jobData.getUnitFullName(), transRankId, jobData.getMinorSpecializationId(), session);

		if (jobData.getStatus().intValue() == JobStatusEnum.OCCUPIED.getCode()) {
		    EmployeeLog employeeLog = new EmployeeLog.Builder()
			    .setRankId(jobData.getRankId())
			    .setBasicJobNameId(jobData.getBasicJobNameId())
			    .constructCommonFields(jobData.getEmployeeId(), FlagsEnum.ON.getCode(), decisionData.getDecisionNumber(), decisionData.getDecisionDate(), HijriDateService.getHijriSysDate(), DataAccess.getTableName(JobTransaction.class))
			    .build();
		    LogService.logEmployeeData(employeeLog, session);
		}
	    }

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    for (JobData jobData : jobsData) {
		String originalJobCode = jobsOriginalCodes.get(jobData.getId());
		jobData.setCode(originalJobCode);
	    }

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    /**
     * Moves a list of jobs then adds a transaction of type JOB_MOVE for each moved job.
     * 
     * @param jobsData
     *            a <code>List</code> of {@link JobData} to be moved
     * @param decisionNumber
     *            the job transaction decision number
     * @param decisionDate
     *            the job transaction decision date in mm/MM/yyyy format
     * @param executionDate
     *            the job transaction decision execution date in mm/MM/yyyy format
     * @param userId
     *            the ID of the user who performed the job transaction
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    private static void moveJobs(List<JobData> jobsData, JobTransaction decisionData, Map<Long, List<String>> regionsNewSerials, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (JobData jobData : jobsData) {
		Long transUnitId = jobData.getUnitId();
		String transUnitFullName = jobData.getUnitFullName();
		String transCode = jobData.getCode();

		jobData.setUnitId(jobData.getNewUnitId());
		jobData.setUnitFullName(jobData.getNewUnitFullName());
		jobData.setExecutionDate(decisionData.getExecutionDate());

		if (!jobData.getRegionId().equals(jobData.getNewRegionId())) {
		    if (CategoriesEnum.OFFICERS.getCode() == jobData.getCategoryId().longValue()
			    || CategoriesEnum.SOLDIERS.getCode() == jobData.getCategoryId().longValue()) {
			String serial = regionsNewSerials.get(jobData.getNewRegionId()).get(0);
			regionsNewSerials.get(jobData.getNewRegionId()).remove(0);
			jobData.setSerial(serial);
			jobData.setCode(jobData.getRankId() + serial.substring(1)); // Replace the category at the serial with the rank.
		    } else {
			jobData.setCode(jobData.getCode().substring(0, 3) + CommonService.getRegionById(jobData.getNewRegionId()).getCode() + jobData.getCode().substring(5));
		    }
		    jobData.setRegionId(jobData.getNewRegionId());
		}

		DataAccess.updateEntity(jobData.getJob(), session);

		addJobTransaction(jobData, TransactionTypesEnum.JOB_MOVE.getCode(), decisionData,
			transCode, jobData.getBasicJobNameId(), transUnitId, transUnitFullName, jobData.getRankId(), jobData.getMinorSpecializationId(), session);
	    }

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

    private static void modifyJobsMinorSpecs(List<JobData> jobsData, JobTransaction decisionData, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (JobData jobData : jobsData) {
		Long transMinorSpecId = jobData.getMinorSpecializationId();
		Long transBasicJobNameId = jobData.getBasicJobNameId();

		jobData.setMinorSpecializationId(jobData.getNewMinorSpecializationId());
		jobData.setExecutionDate(decisionData.getExecutionDate());

		if (jobData.getNewBasicJobNameId() != null) {
		    jobData.setBasicJobNameId(jobData.getNewBasicJobNameId());
		    jobData.setName(jobData.getNewName());
		}
		DataAccess.updateEntity(jobData.getJob(), session);
		addJobTransaction(jobData, TransactionTypesEnum.JOB_MODIFY_MINOR_SPECIALIZATION.getCode(), decisionData,
			jobData.getCode(), transBasicJobNameId, jobData.getUnitId(), jobData.getUnitFullName(), jobData.getRankId(), transMinorSpecId, session);

		if (jobData.getStatus().intValue() == JobStatusEnum.OCCUPIED.getCode() && jobData.getNewBasicJobNameId() != null) {
		    EmployeeLog employeeLog = new EmployeeLog.Builder()
			    .setBasicJobNameId(jobData.getBasicJobNameId())
			    .constructCommonFields(jobData.getEmployeeId(), FlagsEnum.ON.getCode(), decisionData.getDecisionNumber(), decisionData.getDecisionDate(), HijriDateService.getHijriSysDate(), DataAccess.getTableName(JobTransaction.class))
			    .build();
		    LogService.logEmployeeData(employeeLog, session);
		}
	    }

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

    /**
     * Reserves a list of jobs for employees and soldiers only then adds a transaction of type JOB_RESERVE for each reserved job.
     * 
     * @param jobsToReserve
     *            a <code>List</code> of {@link JobData} to be reserved
     * @param reservationRemarks
     *            a <code>String</code> containing the reservation remarks
     * @param userId
     *            the ID of the user who performed the job transaction
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    public static void reserveJobs(List<JobData> jobsToReserve, int reservationStatus, String reservationRemarks, JobTransaction decisionData, boolean validateJobsConflict, CustomSession... useSession) throws BusinessException {

	validateReserveJobs(jobsToReserve, reservationRemarks, validateJobsConflict);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (JobData jobData : jobsToReserve) {
		jobData.setReservationRemarks(reservationRemarks);
		jobData.setReservationStatus(reservationStatus);
		DataAccess.updateEntity(jobData.getJob(), session);
		addJobTransaction(jobData, TransactionTypesEnum.JOB_RESERVE.getCode(), decisionData,
			jobData.getCode(), jobData.getBasicJobNameId(), jobData.getUnitId(), jobData.getUnitFullName(), jobData.getRankId(), jobData.getMinorSpecializationId(), session);
	    }
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

    /**
     * UnReserves a list of jobs for employees and soldiers only then adds a transaction of type JOB_UNRESERVE for each unreserved job.
     * 
     * @param jobsToUnReserve
     *            a <code>List</code> of {@link JobData} to be unreserved
     * @param userId
     *            the ID of the user who performed the job transaction
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    public static void unreserveJobs(List<JobData> jobsToUnReserve, JobTransaction decisionData, boolean validateJobsConflict, CustomSession... useSession) throws BusinessException {

	validateUnreserveJobs(jobsToUnReserve, validateJobsConflict);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (JobData jobData : jobsToUnReserve) {
		jobData.setReservationRemarks(null);
		jobData.setReservationStatus(JobReservationStatusEnum.UN_RESERVED.getCode());
		DataAccess.updateEntity(jobData.getJob(), session);
		addJobTransaction(jobData, TransactionTypesEnum.JOB_UNRESERVE.getCode(), decisionData,
			jobData.getCode(), jobData.getBasicJobNameId(), jobData.getUnitId(), jobData.getUnitFullName(), jobData.getRankId(), jobData.getMinorSpecializationId(), session);
	    }

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

    /**
     * Changes the status of the job which may be (vacant, occupied, frozen, canceled)
     * 
     * @param jobData
     *            a {@link JobData} object represents the job to change its status
     * @param newStatus
     *            an <code>int</code> containing the new value of the status
     * @param session
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any database error occurs
     */
    public static void changeJobStatus(JobData jobData, int newStatus, CustomSession session) throws BusinessException {
	try {
	    if (JobStatusEnum.OCCUPIED.getCode() == newStatus) {
		jobData.setReservationStatus(JobReservationStatusEnum.UN_RESERVED.getCode());
		jobData.setReservationRemarks(null);
	    }

	    jobData.setStatus(newStatus);
	    DataAccess.updateEntity(jobData.getJob(), session);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Moves all jobs from some units to a specific unit. It's used when merging units {@link com.code.services.hcm.UnitsService#mergeUnit(com.code.dal.orm.hcm.organization.units.UnitData, List, String, Date, Long, CustomSession...)}.
     * 
     * @param unitsIds
     *            a list of <code>Long</code> containing the IDs of the units to move their jobs from them
     * @param toUnitId
     *            a <code>long</code> value containing the ID of the unit to move the jobs to it
     * @param decisionNumber
     *            the job transaction decision number
     * @param decisionDate
     *            the job transaction decision date in mm/MM/yyyy format
     * @param userId
     *            the ID of the user who performed the job transaction
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<JobData> moveAllJobsFromUnitsToUnit(Long[] unitsIds, long toUnitId, String toUnitFullName, String decisionNumber, Date decisionDate, Long userId, CustomSession... useSession) throws BusinessException {
	List<JobData> unitsJobs = getJobsByUnitsIds(unitsIds);
	if (unitsJobs.size() == 0)
	    return new ArrayList<>();

	for (JobData jd : unitsJobs) {
	    jd.setNewUnitId(toUnitId);
	    jd.setNewUnitFullName(toUnitFullName);
	    jd.setNewRegionId(jd.getRegionId());
	}

	JobTransaction decisionData = new JobTransaction();
	decisionData.setDecisionNumber(decisionNumber);
	decisionData.setDecisionDate(decisionDate);
	decisionData.setExecutionDate(HijriDateService.getHijriSysDate());
	decisionData.setTransactionUserId(userId);
	decisionData.seteFlag(FlagsEnum.OFF.getCode());

	moveJobs(unitsJobs, decisionData, null, useSession);
	return unitsJobs;
    }

    private static Map<Long, List<String>> getRegionsNewSerials(List<JobData> jobsToAdd, List<JobData> jobsToMove) throws BusinessException {
	try {
	    Map<Long, Long> regionsNewSerialsNeeds = new HashMap<Long, Long>();

	    if (jobsToAdd != null) {
		for (JobData job : jobsToAdd) {
		    if (!regionsNewSerialsNeeds.containsKey(job.getRegionId())) {
			regionsNewSerialsNeeds.put(job.getRegionId(), 1L);
		    } else {
			regionsNewSerialsNeeds.put(job.getRegionId(), regionsNewSerialsNeeds.get(job.getRegionId()) + 1);
		    }
		}
	    }

	    if (jobsToMove != null) {
		for (JobData job : jobsToMove) {
		    if (!job.getRegionId().equals(job.getNewRegionId())) {
			if (!regionsNewSerialsNeeds.containsKey(job.getNewRegionId())) {
			    regionsNewSerialsNeeds.put(job.getNewRegionId(), 1L);
			} else {
			    regionsNewSerialsNeeds.put(job.getNewRegionId(), regionsNewSerialsNeeds.get(job.getNewRegionId()) + 1);
			}
		    }
		}
	    }

	    if (regionsNewSerialsNeeds.size() == 0) {
		return null;
	    }

	    Set<Long> neededRegionsIdsSet = regionsNewSerialsNeeds.keySet();
	    Long[] neededRegionsIds = neededRegionsIdsSet.toArray(new Long[neededRegionsIdsSet.size()]);

	    Long[] categoriesIds;
	    if ((jobsToAdd != null && jobsToAdd.size() > 0 && jobsToAdd.get(0).getCategoryId() == CategoriesEnum.OFFICERS.getCode())
		    || (jobsToMove != null && jobsToMove.size() > 0 && jobsToMove.get(0).getCategoryId() == CategoriesEnum.OFFICERS.getCode()))
		categoriesIds = CommonService.getOfficerCategoriesIdsArray();
	    else if ((jobsToAdd != null && jobsToAdd.size() > 0 && jobsToAdd.get(0).getCategoryId() == CategoriesEnum.SOLDIERS.getCode())
		    || (jobsToMove != null && jobsToMove.size() > 0 && jobsToMove.get(0).getCategoryId() == CategoriesEnum.SOLDIERS.getCode()))
		categoriesIds = CommonService.getSoldiersCategoriesIdsArray();
	    else
		categoriesIds = CommonService.getCivilCategoriesIdsArray();

	    return generateJobsSerials(categoriesIds, neededRegionsIds, regionsNewSerialsNeeds);
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    private static Map<Long, List<String>> generateJobsSerials(Long[] categoriesIds, Long[] regionsIds, Map<Long, Long> regionsNewSerialsNeeds) throws BusinessException {
	Map<Long, List<String>> regionsNewSerials = new HashMap<Long, List<String>>();

	Map<Long, List<String>> regionsJobsSerialsWhereGapsAfterThem = getJobsSerialsWhereGapsAfterThem(categoriesIds, regionsIds);
	Map<Long, List<String>> regionsJobsSerialsWhereGapsBeforeThem = getJobsSerialsWhereGapsBeforeThem(categoriesIds, regionsIds);

	for (Long regionId : regionsNewSerialsNeeds.keySet()) {
	    List<String> regionJobsSerials = new ArrayList<String>();
	    List<String> regionJobsSerialsWhereGapsAfterThem = regionsJobsSerialsWhereGapsAfterThem.get(regionId);
	    List<String> regionJobsSerialsWhereGapsBeforeThem = regionsJobsSerialsWhereGapsBeforeThem.get(regionId);

	    if (regionJobsSerialsWhereGapsAfterThem == null)
		regionJobsSerialsWhereGapsAfterThem = new ArrayList<String>();
	    if (regionJobsSerialsWhereGapsBeforeThem == null)
		regionJobsSerialsWhereGapsBeforeThem = new ArrayList<String>();

	    // if the after list is empty then add to it one element 0
	    // if the first element at the after array is greater than one and this element is greater than or equal the first element at the before array,
	    // then there are gaps from 1 to this element, then add 0 at the top of the array
	    if (regionJobsSerialsWhereGapsAfterThem.size() == 0
		    || (Long.parseLong(regionJobsSerialsWhereGapsAfterThem.get(0)) > MIN_JOB_SEQUENCE
			    && regionJobsSerialsWhereGapsBeforeThem.size() > 0
			    && Long.parseLong(regionJobsSerialsWhereGapsAfterThem.get(0)) >= Long.parseLong(regionJobsSerialsWhereGapsBeforeThem.get(0))))
		regionJobsSerialsWhereGapsAfterThem.add(0, (MIN_JOB_SEQUENCE - 1) + "");

	    // if the before list is empty then add to it one element 100000
	    // if last element at the before array is less than 99999, then there are gaps from this element to 99999,
	    // then add 100000 at the end of the before list
	    if (regionJobsSerialsWhereGapsBeforeThem.size() == 0
		    || (regionJobsSerialsWhereGapsBeforeThem.size() > 0 && Long.parseLong(regionJobsSerialsWhereGapsBeforeThem.get(regionJobsSerialsWhereGapsBeforeThem.size() - 1)) < MAX_JOB_SEQUENCE))
		regionJobsSerialsWhereGapsBeforeThem.add((MAX_JOB_SEQUENCE + 1) + "");

	    for (int i = 0; i < regionJobsSerialsWhereGapsBeforeThem.size(); i++) {
		Long diff = Long.parseLong(regionJobsSerialsWhereGapsBeforeThem.get(i)) - Long.parseLong(regionJobsSerialsWhereGapsAfterThem.get(i));
		for (int j = 1; j < diff; j++) {
		    Long sequence = Long.parseLong(regionJobsSerialsWhereGapsAfterThem.get(i)) + j;
		    String serial = ((categoriesIds[0] != CategoriesEnum.OFFICERS.getCode() && categoriesIds[0] != CategoriesEnum.SOLDIERS.getCode()) ? CategoriesEnum.PERSONS.getCode() : categoriesIds[0]) + CommonService.getRegionById(regionId).getCode() + String.format(JOB_SEQUENCE_FORMAT, sequence);
		    regionJobsSerials.add(serial);
		    regionsNewSerialsNeeds.put(regionId, regionsNewSerialsNeeds.get(regionId) - 1);
		    if (regionsNewSerialsNeeds.get(regionId) == 0)
			break;
		}
		if (regionsNewSerialsNeeds.get(regionId) == 0)
		    break;
	    }

	    regionsNewSerials.put(regionId, regionJobsSerials);
	}

	return regionsNewSerials;
    }

    private static Map<Long, List<String>> getJobsSerialsWhereGapsAfterThem(Long[] categoriesIds, Long[] regionsIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_CATEGORIES_IDS", categoriesIds);
	    qParams.put("P_JOB_REGIONS_IDS", regionsIds);

	    List<String> jobsSerialsWhereGapsAfterThem = DataAccess.executeNamedQuery(String.class, QueryNamesEnum.HCM_GET_JOBS_SERIALS_WHERE_GAPS_AFTER_THEM.getCode(), qParams);

	    Map<Long, List<String>> regionsJobsSerialsWhereGapsAfterThem = new HashMap<Long, List<String>>();
	    for (String serial : jobsSerialsWhereGapsAfterThem) {
		Long regionId = Long.parseLong(serial.substring(1, 3));
		String sequence = serial.substring(3);

		if (!regionsJobsSerialsWhereGapsAfterThem.containsKey(regionId)) {
		    List<String> regionSequences = new ArrayList<String>();
		    regionSequences.add(sequence);
		    regionsJobsSerialsWhereGapsAfterThem.put(regionId, regionSequences);
		} else {
		    regionsJobsSerialsWhereGapsAfterThem.get(regionId).add(sequence);
		}
	    }

	    return regionsJobsSerialsWhereGapsAfterThem;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static Map<Long, List<String>> getJobsSerialsWhereGapsBeforeThem(Long[] categoriesIds, Long[] regionsIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_CATEGORIES_IDS", categoriesIds);
	    qParams.put("P_JOB_REGIONS_IDS", regionsIds);

	    List<String> jobsSerialsWhereGapsBeforeThem = DataAccess.executeNamedQuery(String.class, QueryNamesEnum.HCM_GET_JOBS_SERIALS_WHERE_GAPS_BEFORE_THEM.getCode(), qParams);

	    Map<Long, List<String>> regionsJobsSerialsWhereGapsBeforeThem = new HashMap<Long, List<String>>();
	    for (String serial : jobsSerialsWhereGapsBeforeThem) {
		Long regionId = Long.parseLong(serial.substring(1, 3));
		String sequence = serial.substring(3);

		if (!regionsJobsSerialsWhereGapsBeforeThem.containsKey(regionId)) {
		    List<String> regionSequences = new ArrayList<String>();
		    regionSequences.add(sequence);
		    regionsJobsSerialsWhereGapsBeforeThem.put(regionId, regionSequences);
		} else {
		    regionsJobsSerialsWhereGapsBeforeThem.get(regionId).add(sequence);
		}
	    }

	    return regionsJobsSerialsWhereGapsBeforeThem;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*---------------------------------------------- Jobs Validations ----------------------------------------------------------*/

    public static void validateJobsToFreezeOccupiedJobsToScale(List<JobData> jobsToFreeze, List<JobData> jobsToScale) throws BusinessException {
	validateJobs(null, null, null, jobsToFreeze, null, jobsToScale, null, null,
		"-", HijriDateService.getHijriSysDate(), HijriDateService.getHijriSysDate(), false, false, true, true);
    }

    public static void validateVacantOrOccupiedJobsToFreezeJobsToUnfreeze(List<JobData> jobsToFreeze, List<JobData> jobsToUnfreeze) throws BusinessException {
	validateJobs(null, null, null, jobsToFreeze, jobsToUnfreeze, null, null, null,
		"-", HijriDateService.getHijriSysDate(), HijriDateService.getHijriSysDate(), false, false, false, true);
    }

    public static void validateJobs(List<JobData> jobsToAdd, List<JobData> jobsToRename, List<JobData> jobsToCancel, List<JobData> jobsToFreeze,
	    List<JobData> jobsToUnfreeze, List<JobData> jobsToScale, List<JobData> jobsToMove, List<JobData> jobsToModifyMinorSpecs) throws BusinessException {

	validateJobs(jobsToAdd, jobsToRename, jobsToCancel, jobsToFreeze, jobsToUnfreeze, jobsToScale, jobsToMove, jobsToModifyMinorSpecs,
		"-", HijriDateService.getHijriSysDate(), HijriDateService.getHijriSysDate(), false, true, true, true);
    }

    /**
     * Validates jobs transactions. </br>
     * The transactions are:
     * <ul>
     * <li>Adding new job</li>
     * <li>Renaming a job</li>
     * <li>Canceling a job</li>
     * <li>Freezing a job for Soldiers only for one of these reasons like (Formation, Designation, Transfer, Promotion, Another)</li>
     * <li>UnFreezing a job for Soldiers only</li>
     * <li>Scaling a job up or down</li>
     * <li>Moving a job from unit to another unit for Employees only</li>
     * </ul>
     * 
     * @param jobsToAdd
     *            a <code>List</code> of {@link JobData} to be added
     * @param jobsToRename
     *            a <code>List</code> of {@link JobData} to be renamed
     * @param jobsToCancel
     *            a <code>List</code> of {@link JobData} to be canceled
     * @param jobsToFreeze
     *            a <code>List</code> of {@link JobData} to be frozen
     * @param jobsToUnfreeze
     *            a <code>List</code> of {@link JobData} to be unfrozen
     * @param jobsToScale
     *            a <code>List</code> of {@link JobData} to be scaled
     * @param jobsToMove
     *            a <code>List</code> of {@link JobData} to be moved
     * @param validateJobsConflicts
     *            a flag indicates whether to check if all the job lists don't have conflicts among each others or don't apply this check
     * @throws BusinessException
     *             if any validation fails or if any general error occurs
     */
    private static List<JobsBalanceData> validateJobs(List<JobData> jobsToAdd, List<JobData> jobsToRename, List<JobData> jobsToCancel, List<JobData> jobsToFreeze,
	    List<JobData> jobsToUnfreeze, List<JobData> jobsToScale, List<JobData> jobsToMove, List<JobData> jobsToModifyMinorSpecs,
	    String decisionNumber, Date decisionDate, Date executionDate, boolean validateJobsConflicts, boolean validateJobsStatusForScale, boolean validateJobsStatusForFreeze, boolean validateJobsBalances) throws BusinessException {

	if (decisionNumber == null || decisionNumber.isEmpty())
	    throw new BusinessException("error_decNumberMandatory");

	if (decisionDate == null)
	    throw new BusinessException("error_decDateMandatory");

	if (executionDate == null)
	    throw new BusinessException("error_executionDateIsMandatory");

	validateOldHijriDate(decisionDate);
	validateOldHijriDate(executionDate);

	List<JobsBalanceData> jobsBalances = new ArrayList<JobsBalanceData>();
	if (validateJobsBalances)
	    jobsBalances = JobsBalancesService.validateJobsBalances(jobsToAdd, jobsToCancel, jobsToFreeze, jobsToUnfreeze, jobsToScale, jobsToMove);

	if (jobsToAdd != null && jobsToAdd.size() > 0)
	    validateAddJobs(jobsToAdd);

	if (validateJobsConflicts) {
	    Long[] jobsIds = getJobsIds(jobsToRename, jobsToCancel, jobsToFreeze, jobsToUnfreeze, jobsToScale, jobsToMove, jobsToModifyMinorSpecs);
	    EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(null, jobsIds, TransactionClassesEnum.JOBS.getCode(), false, null);
	}

	if (jobsToRename != null && jobsToRename.size() > 0)
	    validateRenameJobs(jobsToRename);

	if (jobsToCancel != null && jobsToCancel.size() > 0)
	    validateCancelJobs(jobsToCancel);

	if (jobsToFreeze != null && jobsToFreeze.size() > 0)
	    validateFreezeJobs(jobsToFreeze, validateJobsStatusForFreeze);

	if (jobsToUnfreeze != null && jobsToUnfreeze.size() > 0)
	    validateUnfreezeJobs(jobsToUnfreeze);

	if (jobsToScale != null && jobsToScale.size() > 0)
	    validateScaleJobs(jobsToScale, validateJobsStatusForScale);

	if (jobsToMove != null && jobsToMove.size() > 0)
	    validateMoveJobs(jobsToMove);

	if (jobsToModifyMinorSpecs != null && jobsToModifyMinorSpecs.size() > 0)
	    validateModifyMinorSpecsJobs(jobsToModifyMinorSpecs);

	return jobsBalances;
    }

    public static Long[] getJobsIds(List<JobData> jobsToRename, List<JobData> jobsToCancel, List<JobData> jobsToFreeze, List<JobData> jobsToUnfreeze, List<JobData> jobsToScale, List<JobData> jobsToMove, List<JobData> jobsToModifyMinorSpecs) throws BusinessException {
	Set<Long> jobsIdsSet = new HashSet<Long>();
	if (jobsToRename != null)
	    for (JobData jobToRename : jobsToRename) {
		jobsIdsSet.add(jobToRename.getId());
	    }
	if (jobsToCancel != null)
	    for (JobData jobToCancel : jobsToCancel) {
		jobsIdsSet.add(jobToCancel.getId());
	    }
	if (jobsToFreeze != null)
	    for (JobData jobToFreeze : jobsToFreeze) {
		jobsIdsSet.add(jobToFreeze.getId());
	    }
	if (jobsToUnfreeze != null)
	    for (JobData jobToUnfreeze : jobsToUnfreeze) {
		jobsIdsSet.add(jobToUnfreeze.getId());
	    }
	if (jobsToScale != null)
	    for (JobData jobToScale : jobsToScale) {
		jobsIdsSet.add(jobToScale.getId());
	    }
	if (jobsToMove != null)
	    for (JobData jobToMove : jobsToMove) {
		jobsIdsSet.add(jobToMove.getId());
	    }
	if (jobsToModifyMinorSpecs != null)
	    for (JobData jobToModifyMinorSpecs : jobsToModifyMinorSpecs) {
		jobsIdsSet.add(jobToModifyMinorSpecs.getId());
	    }

	return jobsIdsSet.toArray(new Long[jobsIdsSet.size()]);
    }

    /**
     * Validates the jobs mandatory fields then validates the following rules:
     * <ul>
     * <li>Job status must be vacant or frozen after creation.</li>
     * <li>Job code must be consist of 10 digits.</li>
     * <li>Job code should be matching the selected rank.</li>
     * <li>Job code should be matching the selected unit's region.</li>
     * </ul>
     * 
     * @param jobsData
     *            a <code>List</code> of {@link JobData} to be validated
     * @throws BusinessException
     *             if any validation fails
     */
    private static void validateAddJobs(List<JobData> jobsData) throws BusinessException {
	HashSet<String> jobsSerialsSet = new HashSet<String>();
	for (JobData jobData : jobsData) {
	    // check mandatory fields
	    if (jobData.getCategoryId() == null || jobData.getBasicJobNameId() == null)
		throw new BusinessException("error_jobNameIsMandatory");

	    if (jobData.getCategoryId() != null && !jobData.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) && !jobData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode())) {
		if (jobData.getSequence() == null || jobData.getSequence().length() == 0)
		    throw new BusinessException("error_jobSequenceIsMandatory");
		if (jobData.getSequence().length() != JOB_SEQUENCE_LENGTH || jobData.getSequence().equals(FORBIDDEN_JOB_SEQUENCE))
		    throw new BusinessException("error_jobSequenceLengthShouldBe5Digits");
	    }
	    if (jobData.getRankId() == null)
		throw new BusinessException("error_rankIsMandatory");
	    if (jobData.getMinorSpecializationId() == null)
		throw new BusinessException("error_minorSpecializationIsMandatory");
	    if (jobData.getUnitId() == null || jobData.getRegionId() == null)
		throw new BusinessException("error_unitIsMandatory");
	    if ((jobData.getCategoryId().longValue() == CategoriesEnum.OFFICERS.getCode() || jobData.getCategoryId().longValue() == CategoriesEnum.SOLDIERS.getCode()) && jobData.getGeneralSpecialization() == null)
		throw new BusinessException("error_generalSpecializationIsMandatory");
	    if (jobData.getGeneralType() == null)
		throw new BusinessException("error_typeIsMandatory");
	    if (jobData.getStatus() == null)
		throw new BusinessException("error_statusIsMandatory");
	    if (jobData.getCategoryId().longValue() == CategoriesEnum.SOLDIERS.getCode() && jobData.getApprovedFlag() == null)
		throw new BusinessException("error_approvalStatusIsMandatory");
	    if (jobData.getCategoryId().longValue() == CategoriesEnum.PERSONS.getCode() && jobData.getClassificationId() == null)
		throw new BusinessException("error_classificationIsMandatory");

	    // check status
	    if (jobData.getStatus().intValue() != JobStatusEnum.VACANT.getCode() && jobData.getStatus().intValue() != JobStatusEnum.FROZEN.getCode())
		throw new BusinessException("error_jobStatusMustBeVacantOrFrozenJustAfterCreation");

	    // check the uniqueness within the new added jobs
	    if (CategoriesEnum.OFFICERS.getCode() != jobData.getCategoryId().longValue()
		    && CategoriesEnum.SOLDIERS.getCode() != jobData.getCategoryId().longValue()) {
		if (!jobsSerialsSet.add(jobData.getSerial()))
		    throw new BusinessException("error_jobCodeRepeated");
	    }
	}

	if (jobsSerialsSet.size() > 0) {
	    String[] serials = new String[jobsSerialsSet.size()];
	    if (getJobsCountBySerials(jobsSerialsSet.toArray(serials)) > 0)
		throw new BusinessException("error_jobCodeRepeated");
	}
    }

    /**
     * Checks if a list of jobs can be renamed or not by validating the following rules:
     * <ul>
     * <li>Check if the job is reserved or not where it's not allowed to do any operation on a reserved job.</li>
     * <li>The new name is mandatory.</li>
     * <li>The new name should be different from the original name.</li>
     * <li>The new classification should match the rank, for the PERSONS category only.</li>
     * </ul>
     * 
     * @param jobsData
     *            a <code>List</code> of {@link JobData} to be validated
     * @throws BusinessException
     *             if any validation fails
     */
    private static void validateRenameJobs(List<JobData> jobsData) throws BusinessException {
	validateJobReserveFlag(jobsData);

	HashSet<String> jobsSerialsSet = new HashSet<String>();
	for (JobData jobData : jobsData) {
	    if (jobData.getNewBasicJobNameId() == null)
		throw new BusinessException("error_newNameIsMandatory");

	    if (jobData.getNewBasicJobNameId().equals(jobData.getBasicJobNameId()))
		throw new BusinessException("error_newNameMustBeDifferentFromOriginalName");

	    if (!jobData.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) && !jobData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode())) {
		if (jobData.getSequence() == null || jobData.getSequence().length() == 0)
		    throw new BusinessException("error_jobNewSequenceIsMandatory");
		if (jobData.getSequence().length() != JOB_SEQUENCE_LENGTH || jobData.getSequence().equals(FORBIDDEN_JOB_SEQUENCE))
		    throw new BusinessException("error_jobNewSequenceLengthShouldBe5Digits");
	    }

	    String actualJobRank = (16 - Long.parseLong((jobData.getRankId() + "").substring(1))) + "";
	    if (actualJobRank.length() == 1)
		actualJobRank = "0" + actualJobRank;
	    if (jobData.getCategoryId().longValue() == CategoriesEnum.PERSONS.getCode() && jobData.getNewClassificationJobCode() != null && jobData.getNewClassificationJobCode().length() > 0 && !(jobData.getNewClassificationJobCode().substring(5).equals(actualJobRank)))
		throw new BusinessException("error_newClassificationJobCodeIsNotMatchingTheRankCode");

	    // check the uniqueness within the renamed jobs
	    if (CategoriesEnum.OFFICERS.getCode() != jobData.getCategoryId().longValue()
		    && CategoriesEnum.SOLDIERS.getCode() != jobData.getCategoryId().longValue()
		    && !jobData.getCode().substring(5).equals(jobData.getSequence())) {
		if (!jobsSerialsSet.add(jobData.getSerial()))
		    throw new BusinessException("error_jobCodeRepeated");
	    }
	}

	if (jobsSerialsSet.size() > 0) {
	    String[] serials = new String[jobsSerialsSet.size()];
	    if (getJobsCountBySerials(jobsSerialsSet.toArray(serials)) > 0)
		throw new BusinessException("error_jobCodeRepeated");
	}
    }

    /**
     * Checks if a list of jobs can be canceled or not by validating the following rules:
     * <ul>
     * <li>Check if the job is reserved or not where it's not allowed to do any operation on a reserved job.</li>
     * <li>The job status should be vacant or frozen.</li>
     * </ul>
     * 
     * @param jobsData
     *            a <code>List</code> of {@link JobData} to be validated
     * @throws BusinessException
     *             if any validation fails
     */
    private static void validateCancelJobs(List<JobData> jobsData) throws BusinessException {
	validateJobReserveFlag(jobsData);

	for (JobData jobData : jobsData) {
	    if (jobData.getStatus().intValue() != JobStatusEnum.VACANT.getCode() && jobData.getStatus().intValue() != JobStatusEnum.FROZEN.getCode())
		throw new BusinessException("error_jobCancellationIsAllowedOnlyForVacantAndFrozen");
	}
    }

    /**
     * Checks if a list of jobs can be freezed or not by validating the following rules:
     * <ul>
     * <li>Check if the job is reserved or not where it's not allowed to do any operation on a reserved job.</li>
     * <li>The job status should be vacant.</li>
     * </ul>
     * 
     * @param jobsData
     *            a <code>List</code> of {@link JobData} to be validated
     * @throws BusinessException
     *             if any validation fails
     */
    private static void validateFreezeJobs(List<JobData> jobsData, boolean validateJobsStatusForFreeze) throws BusinessException {
	validateJobReserveFlag(jobsData);
	for (JobData jobData : jobsData) {
	    if (validateJobsStatusForFreeze && jobData.getStatus().intValue() != JobStatusEnum.VACANT.getCode())
		throw new BusinessException("error_jobFreezeIsAllowedOnlyForVacantAndApprovedOnes");
	}
    }

    /**
     * Checks if a list of jobs can be unfreezed or not by validating the following rules:
     * <ul>
     * <li>Check if the job is reserved or not where it's not allowed to do any operation on a reserved job.</li>
     * <li>The job should be frozen and not approved.</li>
     * </ul>
     * 
     * @param jobsData
     *            a <code>List</code> of {@link JobData} to be validated
     * @throws BusinessException
     *             if any validation fails
     */
    private static void validateUnfreezeJobs(List<JobData> jobsData) throws BusinessException {
	validateJobReserveFlag(jobsData);
	for (JobData jobData : jobsData) {
	    if (jobData.getApprovedFlag().intValue() == FlagsEnum.ON.getCode())
		throw new BusinessException("error_jobUnfreezeIsAllowedOnlyForNotApprovedOnes");
	}
    }

    /**
     * Checks if a list of jobs can be scaled or not by validating the following rules:
     * <ul>
     * <li>Check if the job is reserved or not where it's not allowed to do any operation on a reserved job.</li>
     * <li>Check if the new rank is null.</li>
     * <li>Check if the new classification is null, for the PERSONS category only.</li>
     * <li>Check if the job is not frozen where the scaling is allowed only for the not approved jobs, for the soldiers only.</li>
     * <li>Check if the job is not vacant where the scaling is allowed only for the vacant jobs, for the employees only.</li>
     * <li>The new classification should match the new rank, for the PERSONS category only.</li>
     * </ul>
     * 
     * @param jobsData
     *            a <code>List</code> of {@link JobData} to be validated
     * @param prmotion
     *            boolean to check if the promotion module is the caller one to bypass some conditions
     * @throws BusinessException
     *             if any validation fails
     */
    private static void validateScaleJobs(List<JobData> jobsData, boolean validateJobsStatusForScale) throws BusinessException {
	validateJobReserveFlag(jobsData);

	HashSet<String> jobsSerialsSet = new HashSet<String>();
	for (JobData jobData : jobsData) {
	    if (jobData.getNewRankId() == null)
		throw new BusinessException("error_newRankIsMandatory");

	    if (jobData.getNewRankId().equals(jobData.getRankId()))
		throw new BusinessException("error_newRankMustBeDifferentFromOriginalOne");

	    if (!jobData.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) && !jobData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode())) {
		if (jobData.getSequence() == null || jobData.getSequence().length() == 0)
		    throw new BusinessException("error_jobNewSequenceIsMandatory");
		if (jobData.getSequence().length() != JOB_SEQUENCE_LENGTH || jobData.getSequence().equals(FORBIDDEN_JOB_SEQUENCE))
		    throw new BusinessException("error_jobNewSequenceLengthShouldBe5Digits");
	    }

	    if (jobData.getCategoryId().longValue() == CategoriesEnum.PERSONS.getCode() && jobData.getNewClassificationId() == null)
		throw new BusinessException("error_classificationIsMandatory");

	    if (validateJobsStatusForScale && (jobData.getCategoryId().longValue() == CategoriesEnum.SOLDIERS.getCode() && jobData.getStatus().intValue() != JobStatusEnum.FROZEN.getCode()))
		throw new BusinessException("error_soldiersJobsScalingIsAllowedOnlyForNotApprovedOnes");

	    if (validateJobsStatusForScale && (!(jobData.getCategoryId().longValue() == CategoriesEnum.OFFICERS.getCode() || jobData.getCategoryId().longValue() == CategoriesEnum.SOLDIERS.getCode()) && jobData.getStatus().intValue() != JobStatusEnum.VACANT.getCode()))
		throw new BusinessException("error_personsJobsScalingIsAllowedOnlyForVacantOnes");

	    String actualJobNewRank = (16 - Long.parseLong((jobData.getNewRankId() + "").substring(1))) + "";
	    if (actualJobNewRank.length() == 1)
		actualJobNewRank = "0" + actualJobNewRank;
	    if (jobData.getCategoryId().longValue() == CategoriesEnum.PERSONS.getCode() && jobData.getNewClassificationJobCode() != null && jobData.getNewClassificationJobCode().length() > 0 && !jobData.getNewClassificationJobCode().substring(5).equals(actualJobNewRank))
		throw new BusinessException("error_newClassificationJobCodeIsNotMatchingTheNewRankCode");

	    // check the uniqueness within the new added jobs
	    if (CategoriesEnum.OFFICERS.getCode() != jobData.getCategoryId().longValue() && CategoriesEnum.SOLDIERS.getCode() != jobData.getCategoryId().longValue()) {
		if (!jobsSerialsSet.add(jobData.getSerial()))
		    throw new BusinessException("error_jobCodeRepeated");
	    }
	}

	if (jobsSerialsSet.size() > 0) {
	    String[] serials = new String[jobsSerialsSet.size()];
	    if (getJobsCountBySerials(jobsSerialsSet.toArray(serials)) > 0)
		throw new BusinessException("error_jobCodeRepeated");
	}
    }

    /**
     * Checks if a list of jobs can be moved or not by validating the following rules:
     * <ul>
     * <li>Check if the job is reserved or not where it's not allowed to do any operation on a reserved job.</li>
     * <li>Check if the new unit, to move the jobs to it, is null.</li>
     * <li>Check if the new unit is not the same as the old unit where the jobs should be moved to a new unit.</li>
     * <li>Check if the job is canceled or not where it's not allowed to move the canceled jobs.</li>
     * </ul>
     * 
     * @param jobsData
     *            a <code>List</code> of {@link JobData} to be validated
     * @throws BusinessException
     *             if any validation fails
     */
    private static void validateMoveJobs(List<JobData> jobsData) throws BusinessException {
	validateJobReserveFlag(jobsData);
	for (JobData jobData : jobsData) {
	    if (jobData.getNewUnitId() == null)
		throw new BusinessException("error_newUnitIsMandatory");

	    if (jobData.getNewUnitId().equals(jobData.getUnitId()))
		throw new BusinessException("error_newUnitMustBeDifferent");

	    if (jobData.getStatus().intValue() == JobStatusEnum.CANCELED.getCode())
		throw new BusinessException("error_jobMoveIsNotAllowedForCanceledOnes");

	    if (jobData.getStatus().intValue() == JobStatusEnum.OCCUPIED.getCode()
		    && jobData.getEmployeeId() != null && jobData.getEmployeeId().equals(UnitsService.getUnitById(jobData.getUnitId()).getOfficialManagerId()))
		throw new BusinessException("error_jobMoveIsNotAllowedIfJobUnitOfficialManagerIsTheJobEmployee");
	}
    }

    /**
     * Checks if the jobs minor specialization can be changed or not by validating the following rules:
     * <ul>
     * <li>Check if the job is reserved or not where it's not allowed to do any operation on a reserved job.</li>
     * <li>The new minor specs is mandatory.</li>
     * <li>The new minor specs should be different from the original one.</li>
     * <li>The jobs to modify should be approved.</li>
     * <li>The new name if exist should be different from the old one.</li>
     * </ul>
     * 
     * @param jobsData
     *            a <code>List</code> of {@link JobData} to be validated
     * @throws BusinessException
     *             if any validation fails
     */
    private static void validateModifyMinorSpecsJobs(List<JobData> jobsData) throws BusinessException {
	validateJobReserveFlag(jobsData);
	for (JobData jobData : jobsData) {
	    if (jobData.getNewMinorSpecializationId() == null)
		throw new BusinessException("error_newMinorSpecIsMandatory");

	    if (jobData.getNewMinorSpecializationId().equals(jobData.getMinorSpecializationId()))
		throw new BusinessException("error_newMinorSpecMustBeDifferentFromOriginalMinorSpec");

	    if (jobData.getApprovedFlag().intValue() != FlagsEnum.ON.getCode())
		throw new BusinessException("error_modifyMinorSpecsAllowedOnlyForApprovedJobs");

	    if (jobData.getNewBasicJobNameId() != null && jobData.getNewBasicJobNameId().equals(jobData.getBasicJobNameId()))
		throw new BusinessException("error_newNameMustBeDifferentFromOriginalName");
	}
    }

    /**
     * Checks if a job is reserved or not where it's not allowed to perform any transaction on a reserved job.
     * 
     * @param jobsData
     *            a <code>List</code> of {@link JobData} to check their reserved flag
     * @throws BusinessException
     *             if any job from the list of jobs is reserved
     */
    private static void validateJobReserveFlag(List<JobData> jobsData) throws BusinessException {
	for (JobData jobData : jobsData) {
	    if (jobData.getId() == null)
		throw new BusinessException("error_jobIsMandatory");

	    if (JobReservationStatusEnum.RESERVED.getCode() == jobData.getReservationStatus().intValue()
		    || JobReservationStatusEnum.RESERVED_FOR_RECRUITMENT.getCode() == jobData.getReservationStatus().intValue())
		throw new BusinessException("error_noTransactionsAllowedForReservedJobs");
	}
    }

    /**
     * Checks if the job to be reserved is already reserved, not vacant or not frozen where it's allowed only to reserve the unreserved vacant and frozen jobs.
     * 
     * @param jobData
     *            a {@link JobData} object to be validated
     * @throws BusinessException
     *             if the validation fails
     */
    public static void validateReserveJobs(List<JobData> jobsData, String reservationRemarks, boolean validateJobsConflict) throws BusinessException {

	if (reservationRemarks == null || reservationRemarks.trim().isEmpty())
	    throw new BusinessException("error_reservationRemarksRequired");

	List<Long> jobIdsList = new ArrayList<Long>();
	for (JobData jobData : jobsData) {

	    if ((JobReservationStatusEnum.RESERVED.getCode() == jobData.getReservationStatus().intValue()
		    || JobReservationStatusEnum.RESERVED_FOR_RECRUITMENT.getCode() == jobData.getReservationStatus().intValue()
		    || JobReservationStatusEnum.RESERVED_FOR_PROMOTION.getCode() == jobData.getReservationStatus().intValue())
		    || !(jobData.getStatus().intValue() == JobStatusEnum.VACANT.getCode() || jobData.getStatus().intValue() == JobStatusEnum.FROZEN.getCode()))
		throw new BusinessException("error_jobReserveIsAllowedOnlyForUnreservedVacantAndFrozenOnes");

	    jobIdsList.add(jobData.getId());
	}

	if (validateJobsConflict)
	    EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(null, jobIdsList.toArray(new Long[jobIdsList.size()]), TransactionClassesEnum.JOBS.getCode(), false, null);
    }

    /**
     * Checks if the job to be unreserved is reserved or not where it's not allowed to unreserved a not reserved job.
     * 
     * @param jobData
     *            a {@link JobData} object to be validated
     * @throws BusinessException
     *             if the validation fails
     */
    public static void validateUnreserveJobs(List<JobData> jobsData, boolean validateJobsConflict) throws BusinessException {

	List<Long> jobIdsList = new ArrayList<Long>();
	for (JobData jobData : jobsData) {
	    if (JobReservationStatusEnum.UN_RESERVED.getCode() == jobData.getReservationStatus().intValue())
		throw new BusinessException("error_jobIsNotReserved");

	    jobIdsList.add(jobData.getId());
	}

	if (validateJobsConflict)
	    EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(null, jobIdsList.toArray(new Long[jobIdsList.size()]), TransactionClassesEnum.JOBS.getCode(), false, null);

    }

    /*---------------------------------------------- Jobs Queries -------------------------------------------------*/

    /**
     * Gets all vacant and frozen jobs at a specific unit.
     * 
     * </br>
     * wrapper for {@link #searchJobs(long, long, Integer[], String, String, String, Long[], Date, Date, int, int, String, long, long, long)}.
     * 
     * @param unitId
     *            a <code>Long</code> containing the ID of the unit to get its vacant and frozen jobs
     * @return a <code>List</code> of {@link JobData} with status vacant or frozen
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<JobData> getVacantAndFozenJobsByUnitId(Long unitId) throws BusinessException {
	return searchJobs(FlagsEnum.ALL.getCode(), unitId, new Integer[] { JobStatusEnum.VACANT.getCode(), JobStatusEnum.FROZEN.getCode() }, null, null, null, null, null, null, FlagsEnum.ALL.getCode(), null, null, (long) FlagsEnum.ALL.getCode(), (long) FlagsEnum.ALL.getCode(), (long) FlagsEnum.ALL.getCode());
    }

    /**
     * Gets all not canceled jobs with specific unit, code, name and categories.
     * 
     * </br>
     * wrapper for {@link #searchJobs(long, long, Integer[], String, String, String, Long[], Date, Date, int, int, String, long, long, long)}.
     * 
     * @param unitFullName
     *            a <code>String</code> containing the full name of the unit to get its jobs
     * @param code
     *            a <code>String</code> containing the job code to search for it
     * @param name
     *            a <code>String</code> containing the job name to search for it
     * @param categoriesIds
     *            a <code>List</code> of <code>Long</code> containing the categories IDs to search for the jobs with these categories
     * @return a <code>List</code> of {@link JobData} which matches the search criteria
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<JobData> getNotCancelledJobs(String unitFullName, String code, String name, Long[] categoriesIds, long regionId) throws BusinessException {
	return searchJobs(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), new Integer[] { JobStatusEnum.VACANT.getCode(), JobStatusEnum.OCCUPIED.getCode(), JobStatusEnum.FROZEN.getCode() }, unitFullName, code, name, categoriesIds, null, null, FlagsEnum.ALL.getCode(), new Integer[] { JobReservationStatusEnum.UN_RESERVED.getCode() }, null, (long) FlagsEnum.ALL.getCode(), regionId, (long) FlagsEnum.ALL.getCode());
    }

    /**
     * Gets all frozen jobs with specific unit, code and name.
     * 
     * </br>
     * wrapper for {@link #searchJobs(long, long, Integer[], String, String, String, Long[], Date, Date, int, int, String, long, long, long)}.
     * 
     * @param unitFullName
     *            a <code>String</code> containing the full name of the unit to get its jobs
     * @param code
     *            a <code>String</code> containing the job code to search for it
     * @param name
     *            a <code>String</code> containing the job name to search for it
     * @return a <code>List</code> of {@link JobData} which matches the search criteria
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<JobData> getFrozenJobs(String unitFullName, String code, String name, long regionId) throws BusinessException {
	return searchJobs(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), new Integer[] { JobStatusEnum.FROZEN.getCode() }, unitFullName, code, name, null, null, null, FlagsEnum.ALL.getCode(), new Integer[] { JobReservationStatusEnum.UN_RESERVED.getCode() }, null, (long) FlagsEnum.ALL.getCode(), regionId, (long) FlagsEnum.ALL.getCode());
    }

    /**
     * Gets all vacant jobs with specific unit, code, name, categories and rank.
     * 
     * </br>
     * wrapper for {@link #searchJobs(long, long, Integer[], String, String, String, Long[], Date, Date, int, int, String, long, long, long)}.
     * 
     * @param unitFullName
     *            a <code>String</code> containing the full name of the unit to get its jobs
     * @param code
     *            a <code>String</code> containing the job code to search for it
     * @param name
     *            a <code>String</code> containing the job name to search for it
     * @param categoriesIds
     *            a <code>List</code> of <code>Long</code> containing the categories IDs to search for the jobs with these categories
     * @param rankId
     *            a <code>long</code> value containing the job rank to search for it
     * @param includeFrozen
     *            a <code>boolean</code> to indicate whether to include the frozen jobs or not
     * @param reservedFlag
     *            a <code>boolean</code> to indicate whether to get 1:reserved, 0:non-reserved or -1:all jobs
     * @return a <code>List</code> of {@link JobData} which matches the search criteria
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<JobData> getVacantJobs(String unitFullName, String code, String name, Long[] categoriesIds, long rankId, long regionId, boolean includeFrozen, boolean reservedFlag) throws BusinessException {
	Integer[] status = includeFrozen ? new Integer[] { JobStatusEnum.VACANT.getCode(), JobStatusEnum.FROZEN.getCode() } : new Integer[] { JobStatusEnum.VACANT.getCode() };
	Integer[] reservationStatus = reservedFlag ? new Integer[] { JobReservationStatusEnum.RESERVED.getCode(), JobReservationStatusEnum.RESERVED_FOR_RECRUITMENT.getCode(), JobReservationStatusEnum.RESERVED_FOR_PROMOTION.getCode() } : new Integer[] { JobReservationStatusEnum.UN_RESERVED.getCode() };
	return searchJobs(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), status, unitFullName, code, name, categoriesIds, null, null, FlagsEnum.ALL.getCode(), reservationStatus, null, rankId, regionId, (long) FlagsEnum.ALL.getCode());
    }

    /**
     * Gets all jobs by specific unit full name, job code, job name, categories, unit HKey, job status, and region.
     * 
     * </br>
     * wrapper for {@link #searchJobs(long, long, Integer[], String, String, String, Long[], Date, Date, int, int, String, long, long, long)}.
     * 
     * @param unitFullName
     *            a <code>String</code> containing the full name of the unit to get its jobs
     * @param code
     *            a <code>String</code> containing the job code to search for it
     * @param name
     *            a <code>String</code> containing the job name to search for it
     * @param categoriesIds
     *            a <code>List</code> of <code>Long</code> containing the categories IDs to search for the jobs with these categories
     * @param unitHKey
     *            a <code>String</code> containing the HKey of the unit to get its jobs
     * @param status
     *            a <code>List</code> of <code>Integer</code> containing the jobs statues to search for them
     * @param regionId
     *            a <code>long</code> value containing the region ID to get the jobs in it
     * @return a <code>List</code> of {@link JobData} which matches the search criteria
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<JobData> getJobsByUnitHKey(String unitFullName, String code, String name, Long[] categoriesIds, String unitHKey, Integer[] status, long regionId) throws BusinessException {
	return searchJobs(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), status, unitFullName, code, name, categoriesIds, null, null, FlagsEnum.ALL.getCode(), new Integer[] { JobReservationStatusEnum.UN_RESERVED.getCode() }, unitHKey, (long) FlagsEnum.ALL.getCode(), regionId, (long) FlagsEnum.ALL.getCode());
    }

    /**
     * Gets all vacant jobs with specific unit full name, job code, job name, categories, rank, region, minor specialization, and unit HKey.
     * 
     * </br>
     * wrapper for {@link #searchJobs(long, long, Integer[], String, String, String, Long[], Date, Date, int, int, String, long, long, long)}.
     * 
     * @param unitFullName
     *            a <code>String</code> containing the full name of the unit to get its jobs
     * @param code
     *            a <code>String</code> containing the job code to search for it
     * @param name
     *            a <code>String</code> containing the job name to search for it
     * @param categoriesIds
     *            a <code>List</code> of <code>Long</code> containing the categories IDs to search for the jobs with these categories
     * @param rankId
     *            a <code>long</code> value containing the job rank to search for it
     * @param regionId
     *            a <code>long</code> value containing the region ID to get the jobs in it
     * @param minorSpecId
     *            a <code>long</code> value containing the minor specialization of a job to search for it
     * @param unitHKey
     *            a <code>String</code> containing the HKey of the unit to get its jobs
     * @return a <code>List</code> of {@link JobData} which matches the search criteria
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<JobData> getVacantJobsByRecruitmentInfo(String unitFullName, String code, String name, long categoryId, long rankId, long regionId, long minorSpecId, String unitHKey) throws BusinessException {
	Integer[] status = new Integer[] { JobStatusEnum.VACANT.getCode() };
	Integer[] reservationStatus = (CategoriesEnum.SOLDIERS.getCode() == categoryId) ? new Integer[] { JobReservationStatusEnum.RESERVED_FOR_RECRUITMENT.getCode() } : new Integer[] { JobReservationStatusEnum.UN_RESERVED.getCode() };
	return searchJobs(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), status, unitFullName, code, name, new Long[] { categoryId }, null, null, FlagsEnum.ALL.getCode(), reservationStatus, unitHKey, rankId, regionId, minorSpecId);
    }

    public static List<JobData> getVacantJobsByPromotionInfo(String unitFullName, String code, String name, long categoryId, long rankId, long regionId, long minorSpecId, String unitHKey) throws BusinessException {
	Integer[] status = new Integer[] { JobStatusEnum.VACANT.getCode() };
	Integer[] reservationStatus = new Integer[] { JobReservationStatusEnum.RESERVED_FOR_PROMOTION.getCode() };
	return searchJobs(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), status, unitFullName, code, name, new Long[] { categoryId }, null, null, FlagsEnum.ALL.getCode(), reservationStatus, unitHKey, rankId, regionId, minorSpecId);
    }

    /**
     * Gets all occupied jobs with specific unit full name, job code, job name, categories, and rank.
     * 
     * </br>
     * wrapper for {@link #searchJobs(long, long, Integer[], String, String, String, Long[], Date, Date, int, int, String, long, long, long)}.
     * 
     * @param unitFullName
     *            a <code>String</code> containing the full name of the unit to get its jobs
     * @param code
     *            a <code>String</code> containing the job code to search for it
     * @param name
     *            a <code>String</code> containing the job name to search for it
     * @param categoriesIds
     *            a <code>List</code> of <code>Long</code> containing the categories IDs to search for the jobs with these categories
     * @param rankId
     *            a <code>long</code> value containing the job rank to search for it
     * @return a <code>List</code> of {@link JobData} which matches the search criteria
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<JobData> getOccupiedJobs(String unitFullName, String code, String name, Long[] categoriesIds, long rankId) throws BusinessException {
	return searchJobs(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), new Integer[] { JobStatusEnum.OCCUPIED.getCode() }, unitFullName, code, name, categoriesIds, null, null, FlagsEnum.ALL.getCode(), new Integer[] { JobReservationStatusEnum.UN_RESERVED.getCode() }, null, rankId, (long) FlagsEnum.ALL.getCode(), (long) FlagsEnum.ALL.getCode());
    }

    /**
     * Gets all vacant and occupied jobs with specific unit full name, job code, job name, categories, rank, and job status.
     * 
     * </br>
     * wrapper for {@link #searchJobs(long, long, Integer[], String, String, String, Long[], Date, Date, int, int, String, long, long, long)}.
     * 
     * @param unitFullName
     *            a <code>String</code> containing the full name of the unit to get its jobs
     * @param code
     *            a <code>String</code> containing the job code to search for it
     * @param name
     *            a <code>String</code> containing the job name to search for it
     * @param categoriesIds
     *            a <code>List</code> of <code>Long</code> containing the categories IDs to search for the jobs with these categories
     * @param rankId
     *            a <code>long</code> value containing the job rank to search for it
     * @param status
     *            a <code>List</code> of <code>Integer</code> containing the jobs statues to search for them
     * @return a <code>List</code> of {@link JobData} which matches the search criteria
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<JobData> getVacantAndOccupiedJobs(String unitFullName, String code, String name, Long[] categoriesIds, long rankId, Integer[] status, long regionId) throws BusinessException {
	return searchJobs(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), status, unitFullName, code, name, categoriesIds, null, null, FlagsEnum.ALL.getCode(), new Integer[] { JobReservationStatusEnum.UN_RESERVED.getCode() }, null, rankId, regionId, (long) FlagsEnum.ALL.getCode());
    }

    /**
     * Gets a job with specific ID.
     * 
     * </br>
     * wrapper for {@link #searchJobs(long, long, Integer[], String, String, String, Long[], Date, Date, int, int, String, long, long, long)}.
     * 
     * @param jobId
     *            a <code>long</code> value containing the ID of the job to search for it
     * @return a {@link JobData} object with the input ID
     * @throws BusinessException
     *             if any error occurs
     */
    public static JobData getJobById(long jobId) throws BusinessException {
	List<JobData> jobsData = searchJobs(jobId, FlagsEnum.ALL.getCode(), null, null, null, null, null, null, null, FlagsEnum.ALL.getCode(), null, null, (long) FlagsEnum.ALL.getCode(), (long) FlagsEnum.ALL.getCode(), (long) FlagsEnum.ALL.getCode());
	if (jobsData == null || jobsData.size() == 0)
	    return null;
	return jobsData.get(0);

    }

    /**
     * Gets all jobs with specific unit ID, unit HKey, job status, job code, job name, categories, execution date from and to, approved flag, and reserved flag.
     * 
     * </br>
     * wrapper for {@link #searchJobs(long, long, Integer[], String, String, String, Long[], Date, Date, int, int, String, long, long, long)}.
     * 
     * @param unitId
     *            a <code>Long</code> containing the ID of the unit to get its jobs
     * @param unitHKey
     *            a <code>String</code> containing the HKey of the unit to get its jobs
     * @param status
     *            a <code>List</code> of <code>Integer</code> containing the jobs statues to search for them
     * @param code
     *            a <code>String</code> containing the job code to search for it
     * @param name
     *            a <code>String</code> containing the job name to search for it
     * @param categoriesIds
     *            a <code>List</code> of <code>Long</code> containing the categories IDs to search for the jobs with these categories
     * @param excutionDateFrom
     *            a <code>Date</code> containing a starting date to search for the transaction execution date from it
     * @param excutionDateTo
     *            a <code>Date</code> containing an end date to search for the transaction execution date to it
     * @param approvedFlag
     *            a <code>boolean</code> to indicate whether to get 1:approved, 0:not-approved or -1:all jobs
     * @param reservedFlag
     *            a <code>boolean</code> to indicate whether to get 1:reserved, 0:non-reserved or -1:all jobs
     * @return a <code>List</code> of {@link JobData} which matches the search criteria
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<JobData> getJobs(Long unitId, String unitHKey, Integer[] status, String code, String name, Long[] categoriesIds, Date excutionDateFrom, Date excutionDateTo, Integer approvedFlag, Integer[] reservationStatus) throws BusinessException {
	return searchJobs(FlagsEnum.ALL.getCode(), unitId, status, null, code, name, categoriesIds, excutionDateFrom, excutionDateTo, approvedFlag, reservationStatus, unitHKey, (long) FlagsEnum.ALL.getCode(), (long) FlagsEnum.ALL.getCode(), (long) FlagsEnum.ALL.getCode());
    }

    /**
     * Gets all jobs with specific search criteria.
     * 
     * @param jobId
     *            a <code>long</code> value containing the ID of the job to search for it
     * @param unitId
     *            a <code>Long</code> containing the ID of the unit to get its jobs
     * @param status
     *            a <code>List</code> of <code>Integer</code> containing the jobs statues to search for them
     * @param unitFullName
     *            a <code>String</code> containing the full name of the unit to get its jobs
     * @param code
     *            a <code>String</code> containing the job code to search for it
     * @param name
     *            a <code>String</code> containing the job name to search for it
     * @param categoriesIds
     *            a <code>List</code> of <code>Long</code> containing the categories IDs to search for the jobs with these categories
     * @param excutionDateFrom
     *            a <code>Date</code> containing a starting date to search for the transaction execution date from it
     * @param excutionDateTo
     *            a <code>Date</code> containing an end date to search for the transaction execution date to it
     * @param approvedFlag
     *            a <code>boolean</code> to indicate whether to get 1:approved, 0:not-approved or -1:all jobs
     * @param reservedFlag
     *            a <code>boolean</code> to indicate whether to get 1:reserved, 0:non-reserved or -1:all jobs
     * @param unitHKey
     *            a <code>String</code> containing the HKey of the unit to get its jobs
     * @param rankId
     *            a <code>long</code> value containing the job rank to search for it
     * @param regionId
     *            a <code>long</code> value containing the region ID to get the jobs in it
     * @param minorSpecId
     *            a <code>long</code> value containing the minor specialization of a job to search for it
     * @return a <code>List</code> of {@link JobData} which matches the search criteria
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<JobData> searchJobs(long jobId, long unitId, Integer[] status, String unitFullName, String code, String name, Long[] categoriesIds, Date excutionDateFrom, Date excutionDateTo, int approvedFlag, Integer[] jobReservationStatus, String unitHKey, long rankId, long regionId, long minorSpecId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    if (status != null && status.length > 0) {
		qParams.put("P_JOB_STATUSES_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_JOB_STATUSES", status);
	    } else {
		qParams.put("P_JOB_STATUSES_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_JOB_STATUSES", new Integer[] { FlagsEnum.ALL.getCode() });
	    }

	    if (categoriesIds != null && categoriesIds.length > 0) {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_CATEGORIES_IDS", categoriesIds);
	    } else {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_CATEGORIES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }

	    qParams.put("P_JOB_ID", jobId);
	    qParams.put("P_JOB_UNIT_ID", unitId);
	    qParams.put("P_JOB_UNIT_FULL_NAME", (unitFullName == null || unitFullName.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : ("%" + unitFullName + "%"));
	    qParams.put("P_JOB_CODE", (code == null || code.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : code);
	    qParams.put("P_JOB_NAME", (name == null || name.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : ("%" + name + "%"));
	    String usedHKey = UnitsService.getHKeyPrefix(unitHKey);
	    qParams.put("P_JOB_UNIT_HKEY", (usedHKey == null || usedHKey.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : (usedHKey + "%"));
	    qParams.put("P_JOB_REGION_ID", regionId);
	    qParams.put("P_JOB_RANK_ID", rankId);
	    qParams.put("P_JOB_MINOR_SPEC_ID", minorSpecId);

	    if (excutionDateFrom != null) {
		qParams.put("P_JOB_EXECUTION_DATE_FROM_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_JOB_EXECUTION_DATE_FROM", HijriDateService.getHijriDateString(excutionDateFrom));
	    } else {
		qParams.put("P_JOB_EXECUTION_DATE_FROM_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_JOB_EXECUTION_DATE_FROM", HijriDateService.getHijriSysDateString());
	    }

	    if (excutionDateTo != null) {
		qParams.put("P_JOB_EXECUTION_DATE_TO_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_JOB_EXECUTION_DATE_TO", HijriDateService.getHijriDateString(excutionDateTo));
	    } else {
		qParams.put("P_JOB_EXECUTION_DATE_TO_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_JOB_EXECUTION_DATE_TO", HijriDateService.getHijriSysDateString());
	    }

	    qParams.put("P_APPROVED_FLAG", approvedFlag);

	    if (jobReservationStatus != null && jobReservationStatus.length > 0) {
		qParams.put("P_JOB_RESERVATION_STATUSES_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_JOB_RESERVATION_STATUSES", jobReservationStatus);
	    } else {
		qParams.put("P_JOB_RESERVATION_STATUSES_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_JOB_RESERVATION_STATUSES", new Integer[] { FlagsEnum.ALL.getCode() });
	    }

	    return DataAccess.executeNamedQuery(JobData.class, QueryNamesEnum.HCM_SEARCH_JOBS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets the jobs with specific IDs given by a comma separated string.
     * 
     * @param jobsIdsString
     *            a comma separated <code>String</code> containing the IDs of the jobs to get their data
     * @return a <code>List</code> of {@link JobData} which matches the search criteria
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<JobData> getJobsByIdsString(String jobsIdsString) throws BusinessException {
	List<JobData> jobsList = new ArrayList<JobData>();
	if (jobsIdsString != null && jobsIdsString.length() > 0) {
	    String[] jobsIdsStrings = jobsIdsString.split(",");
	    Long[] jobsIds = new Long[jobsIdsStrings.length];
	    for (int i = 0; i < jobsIdsStrings.length; i++)
		jobsIds[i] = Long.parseLong(jobsIdsStrings[i]);

	    jobsList = JobsService.searchJobsByJobsIds(jobsIds);
	}
	return jobsList;
    }

    public static List<JobData> getJobsByJobsIds(Long[] jobsIds) throws BusinessException {
	return searchJobsByJobsIds(jobsIds);
    }

    private static List<JobData> searchJobsByJobsIds(Long[] jobsIds) throws BusinessException {
	if (jobsIds == null || jobsIds.length == 0)
	    return new ArrayList<JobData>();

	List<Object> queryInfo = new ArrayList<Object>();
	queryInfo.add(QueryNamesEnum.HCM_SEARCH_JOBS_BY_JOBS_IDS.getCode());

	Map<String, Object> qParams = new HashMap<String, Object>();
	qParams.put("P_JOBS_IDS", jobsIds);
	queryInfo.add(qParams);

	return getManyEntities(JobData.class, queryInfo, jobsIds);

    }

    /**
     * Gets all jobs with specific categories, rank, minor specialization, unit HKey, region, and code series.
     * 
     * @param categoriesIds
     *            a <code>List</code> of <code>Long</code> containing the categories IDs to search for the jobs with these categories
     * @param rankId
     *            a <code>long</code> value containing the job rank to search for it
     * @param minorSpecIds
     *            a <code>long</code> value containing the minor specialization of a job to search for it
     * @param unitHKey
     *            a <code>String</code> containing the HKey of the unit to get its jobs
     * @param regionId
     *            a <code>long</code> value containing the region ID to get the jobs in it
     * @param codeSeries
     *            a <code>String</code> containing a job code to get all the jobs with a code greater than it
     * @return a <code>List</code> of {@link JobData} which matches the search criteria
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<JobData> getJobsForRecruitment(long categoryId, long rankId, Long[] minorSpecIds, String unitHKey, long regionId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_CATEGORY_ID", categoryId);

	    if (minorSpecIds != null && minorSpecIds.length > 0) {
		qParams.put("P_JOB_MINOR_SPECS_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_JOB_MINOR_SPECS_IDS", minorSpecIds);
	    } else {
		qParams.put("P_JOB_MINOR_SPECS_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_JOB_MINOR_SPECS_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }

	    qParams.put("P_JOB_RANK_ID", rankId);
	    String usedHKey = UnitsService.getHKeyPrefix(unitHKey);
	    qParams.put("P_JOB_UNIT_HKEY", (usedHKey == null || usedHKey.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : (usedHKey + "%"));
	    qParams.put("P_JOB_REGION_ID", regionId);

	    return DataAccess.executeNamedQuery(JobData.class, QueryNamesEnum.HCM_GET_JOBS_FOR_RECRUITMENT.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets the number of jobs within specific basic job name id.
     * 
     * @param basicJobNameId
     * @return the number of jobs found in the input basicJobNameId
     * @throws BusinessException
     */
    public static long countJobsByBasicJobNameId(long basicJobNameId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_BASIC_JOB_NAME_ID", basicJobNameId);

	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_JOBS_BY_BASIC_JOB_NAME_ID.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets the number of jobs within specific units.
     * 
     * @param unitsIds
     *            a <code>List</code> of <code>Long</code> containing the IDs of the units to count the jobs in them
     * @return the number of jobs found in the input units
     * @throws BusinessException
     *             if any error occurs
     */
    public static long getJobsCountByUnitsIdsAndCategoriesIds(Long[] unitsIds, Long[] categoriesIds) throws BusinessException {
	try {
	    if (unitsIds == null || unitsIds.length == 0)
		return 0;
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_UNITS_IDS", unitsIds);
	    if (categoriesIds != null && categoriesIds.length > 0) {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_CATEGORIES_IDS", categoriesIds);
	    } else {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_CATEGORIES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_JOBS_BY_UNITS_IDS_AND_CATEGORIES_IDS.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets all the jobs within specific units.
     * 
     * </br>
     * wrapper for {@link #searchJobsByUnitsIds(Long[])}.
     * 
     * @param unitsIds
     *            a <code>List</code> of <code>Long</code> containing the IDs of the units to count the jobs in them
     * @return a <code>List</code> of {@link JobData} within the input units
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<JobData> getJobsByUnitsIds(Long[] unitsIds) throws BusinessException {
	return searchJobsByUnitsIds(unitsIds);
    }

    /**
     * Gets all the jobs within specific units.
     * 
     * @param unitsIds
     *            a <code>List</code> of <code>Long</code> containing the IDs of the units to count the jobs in them
     * @return a <code>List</code> of {@link JobData} within the input units
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<JobData> searchJobsByUnitsIds(Long[] unitsIds) throws BusinessException {
	try {
	    if (unitsIds == null || unitsIds.length == 0)
		return new ArrayList<JobData>();

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_UNITS_IDS", unitsIds);
	    return DataAccess.executeNamedQuery(JobData.class, QueryNamesEnum.HCM_SEARCH_JOBS_BY_UNITS_IDS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static long getJobsCountBySerials(String[] serials) throws BusinessException {
	try {
	    if (serials == null || serials.length == 0)
		return 0;
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_SERIALS", serials);
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_JOBS_BY_SERIALS.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    protected static List<Long> getJobsCountByUnitHKeyAndMinorSpecsIds(String requestingUnitHKey, Long[] minorSpecsIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_UNIT_HKEY_PREFIX", UnitsService.getHKeyPrefix(requestingUnitHKey) + "%");
	    qParams.put("P_MINOR_SPECS_IDS", minorSpecsIds);
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_JOBS_BY_UNIT_HKEY_AND_MINOR_SPECS_IDS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<JobData> getJobsByDecisionInfoForReservation(String decisionNumber, Date decisiondate, long regionId, long categoryId) throws BusinessException {
	return getJobsByDecisionInfo(decisionNumber, decisiondate, new Integer[] { JobStatusEnum.VACANT.getCode(), JobStatusEnum.FROZEN.getCode() }, new Integer[] { JobReservationStatusEnum.UN_RESERVED.getCode() }, regionId, categoryId);
    }

    public static List<JobData> getJobsByDecisionInfoForUnReserve(String decisionNumber, Date decisiondate, long regionId, long categoryId) throws BusinessException {
	return getJobsByDecisionInfo(decisionNumber, decisiondate, new Integer[] { JobStatusEnum.VACANT.getCode(), JobStatusEnum.FROZEN.getCode() }, new Integer[] { JobReservationStatusEnum.RESERVED.getCode(), JobReservationStatusEnum.RESERVED_FOR_RECRUITMENT.getCode(), JobReservationStatusEnum.RESERVED_FOR_PROMOTION.getCode() }, regionId, categoryId);
    }

    public static List<JobData> getJobsByDecisionInfoForUnFreeze(String decisionNumber, Date decisiondate, long regionId, long categoryId) throws BusinessException {
	return getJobsByDecisionInfo(decisionNumber, decisiondate, new Integer[] { JobStatusEnum.FROZEN.getCode() }, new Integer[] { JobReservationStatusEnum.UN_RESERVED.getCode() }, regionId, categoryId);
    }

    private static List<JobData> getJobsByDecisionInfo(String decisionNumber, Date decisiondate, Integer[] jobStatus, Integer[] jobReservationStatus, long regionId, long categoryId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    if (decisiondate == null || decisionNumber == null)
		return new ArrayList<JobData>();

	    qParams.put("P_CATEGORY_ID", categoryId);
	    qParams.put("P_REGION_ID", regionId);
	    qParams.put("P_JOB_STATUSES", jobStatus);
	    qParams.put("P_JOB_RESERVATION_STATUSES", jobReservationStatus);
	    qParams.put("P_DECISION_NUMBER", decisionNumber);
	    qParams.put("P_DECISION_DATE", HijriDateService.getHijriDateString(decisiondate));

	    return DataAccess.executeNamedQuery(JobData.class, QueryNamesEnum.HCM_GET_JOBS_BY_DECISION_INFO.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*---------------------------------------------- Jobs Classifications -------------------------------------------------*/

    /**
     * Gets the job classifications with specific major group, minor group, series, job code, job description, and classification rank.
     * 
     * </br>
     * wrapper for #{@link #searchJobClassifications(String, String, String, String, String, String)}
     * 
     * @param majorGroupDesc
     *            a <code>String</code> containing the major group description
     * @param minorGroupDesc
     *            a <code>String</code> containing the minor group description
     * @param seriesDesc
     *            a <code>String</code> containing the series description
     * @param jobDesc
     *            a <code>String</code> containing the job description to search for it
     * @param classificationRank
     *            a <code>String</code> containing the classification rank to search for it
     * @return a <code>List</code> of {@link JobClassification} which matches the search criteria
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<JobClassification> getJobClassifications(String majorGroupDesc, String minorGroupDesc, String seriesDesc, String jobDesc, String classificationRank) throws BusinessException {
	return searchJobClassifications(majorGroupDesc, minorGroupDesc, seriesDesc, jobDesc, classificationRank);
    }

    /**
     * Gets the job classifications with specific major group, minor group, series, job code, job description, and classification rank.
     * 
     * @param majorGroupDesc
     *            a <code>String</code> containing the major group description
     * @param minorGroupDesc
     *            a <code>String</code> containing the minor group description
     * @param seriesDesc
     *            a <code>String</code> containing the series description
     * @param jobDesc
     *            a <code>String</code> containing the job description to search for it
     * @param classificationRank
     *            a <code>String</code> containing the classification rank to search for it
     * @return a <code>List</code> of {@link JobClassification} which matches the search criteria
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<JobClassification> searchJobClassifications(String majorGroupDesc, String minorGroupDesc, String seriesDesc, String jobDesc, String classificationRank) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_JOB_DESC", (jobDesc == null || jobDesc.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : ("%" + jobDesc + "%"));
	    qParams.put("P_MAJOR_GROUP_DESC", (majorGroupDesc == null || majorGroupDesc.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : majorGroupDesc);
	    qParams.put("P_MINOR_GROUP_DESC", (minorGroupDesc == null || minorGroupDesc.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : minorGroupDesc);
	    qParams.put("P_SERIES_DESC", (seriesDesc == null || seriesDesc.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : seriesDesc);
	    qParams.put("P_RANK_CODE", (classificationRank == null || classificationRank.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : ("%" + classificationRank));

	    return DataAccess.executeNamedQuery(JobClassification.class, QueryNamesEnum.HCM_SEARCH_JOB_CLASSIFICATION.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets the job classification major group descriptions with specific classification rank.
     * 
     * @param classificationRank
     *            a <code>String</code> containing the classification rank to search for it
     * @return a <code>List</code> of <code>String</code> containing the classification major groups
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<String> getJobClassificationMajorGroupDescriptions(String classificationRank) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_RANK_CODE", (classificationRank == null || classificationRank.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : ("%" + classificationRank));

	    return DataAccess.executeNamedQuery(String.class, QueryNamesEnum.HCM_GET_JOB_CLASSIFICATION_MAJOR_GROUP_DESC.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets the job classification minor group descriptions with specific classification rank and major group.
     * 
     * @param majorGroupDesc
     *            a <code>String</code> containing the classification major group to search for it
     * @param classificationRank
     *            a <code>String</code> containing the classification rank to search for it
     * @return a <code>List</code> of <code>String</code> containing the classification minor groups
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<String> getJobClassificationMinorGroupDescriptions(String majorGroupDesc, String classificationRank) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_MAJOR_GROUP_DESC", (majorGroupDesc == null || majorGroupDesc.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : majorGroupDesc);
	    qParams.put("P_RANK_CODE", (classificationRank == null || classificationRank.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : ("%" + classificationRank));

	    return DataAccess.executeNamedQuery(String.class, QueryNamesEnum.HCM_GET_JOB_CLASSIFICATION_MINOR_GROUP_DESC.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets the job classification series descriptions with specific classification rank, major group, and minor group.
     * 
     * @param majorGroupDesc
     *            a <code>String</code> containing the classification major group to search for it
     * @param minorGroupDesc
     *            a <code>String</code> containing the classification minor group to search for it
     * @param classificationRank
     *            a <code>String</code> containing the classification rank to search for it
     * @return a <code>List</code> of <code>String</code> containing the classification series descriptions
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<String> getJobClassificationSeriesDescriptions(String majorGroupDesc, String minorGroupDesc, String classificationRank) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_MAJOR_GROUP_DESC", (majorGroupDesc == null || majorGroupDesc.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : majorGroupDesc);
	    qParams.put("P_MINOR_GROUP_DESC", (minorGroupDesc == null || minorGroupDesc.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : minorGroupDesc);
	    qParams.put("P_RANK_CODE", (classificationRank == null || classificationRank.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : ("%" + classificationRank));

	    return DataAccess.executeNamedQuery(String.class, QueryNamesEnum.HCM_GET_JOB_CLASSIFICATION_SERIES_DESC.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*---------------------------------------------- Jobs Transactions --------------------------------------------------*/

    /**
     * Adds a job transaction for all transactions types (create, rename, move, freeze, unfreeze, scale up, scale down, cancel, reserve, and unreserve) to keep the history of the operations for each job.
     * 
     * @param jobData
     *            a {@link JobData} object which the transaction performed for it
     * @param transactionType
     *            a <code>String</code> containing the transaction type (create, rename, move, freeze, unfreeze, scale up, scale down, cancel, reserve, and unreserve)
     * @param decisionNumber
     *            a <code>String</code> containing the job transaction decision number
     * @param decisionDate
     *            a <code>Date</code> containing the job transaction decision date
     * @param transactionUserId
     *            a <code>Long</code> containing the user ID who performed this transaction
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    private static void addJobTransaction(JobData jobData, Integer transactionType, JobTransaction decisionData,
	    String transCode, Long transBasicJobNameId, Long transUnitId, String transUnitFullName, Long transRankId, Long transMinorSpecId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    JobTransaction jobTransaction = new JobTransaction();
	    jobTransaction.setJobId(jobData.getId());
	    jobTransaction.setDecisionNumber(decisionData.getDecisionNumber());
	    jobTransaction.setDecisionDate(decisionData.getDecisionDate());
	    jobTransaction.setTransactionTypeId(CommonService.getTransactionTypeByCodeAndClass(transactionType, TransactionClassesEnum.JOBS.getCode()).getId());
	    jobTransaction.setCode(jobData.getCode());
	    jobTransaction.setBasicJobNameId(jobData.getBasicJobNameId());
	    jobTransaction.setRankId(jobData.getRankId());
	    jobTransaction.setRegionId(jobData.getRegionId());
	    jobTransaction.setUnitId(jobData.getUnitId());
	    jobTransaction.setUnitFullName(jobData.getUnitFullName());
	    jobTransaction.setClassificationId(jobData.getClassificationId());
	    jobTransaction.setDutiesDesc(jobData.getDutiesDesc());
	    jobTransaction.setJobDesc(jobData.getJobDesc());
	    jobTransaction.setGeneralSpecialization(jobData.getGeneralSpecialization());
	    jobTransaction.setMinorSpecializationId(jobData.getMinorSpecializationId());
	    jobTransaction.setGeneralType(jobData.getGeneralType());
	    jobTransaction.setExecutionDate(jobData.getExecutionDate());
	    jobTransaction.setRemarks(jobData.getRemarks());
	    jobTransaction.setReasons(jobData.getReasons());
	    jobTransaction.setTransactionUserId(decisionData.getTransactionUserId());
	    jobTransaction.setTransactionDate((decisionData.geteFlag() != null && decisionData.geteFlag() == FlagsEnum.ON.getCode()) ? decisionData.getDecisionDate() : HijriDateService.getHijriSysDate());
	    jobTransaction.setStatus(jobData.getStatus());
	    jobTransaction.setSerial(jobData.getSerial());
	    jobTransaction.setApprovedFlag(jobData.getApprovedFlag());
	    jobTransaction.setReservationStatus(jobData.getReservationStatus());
	    jobTransaction.setReservationRemarks(jobData.getReservationRemarks());

	    jobTransaction.setReferring(decisionData.getReferring());
	    jobTransaction.setAttachments(decisionData.getAttachments());
	    jobTransaction.setInternalCopies(decisionData.getInternalCopies());
	    jobTransaction.setExternalCopies(decisionData.getExternalCopies());
	    jobTransaction.setMigFlag(FlagsEnum.OFF.getCode());
	    jobTransaction.seteFlag(decisionData.geteFlag() == null ? FlagsEnum.OFF.getCode() : decisionData.geteFlag());
	    jobTransaction.setTransCode(transCode);
	    jobTransaction.setTransBasicJobNameId(transBasicJobNameId);
	    jobTransaction.setTransUnitId(transUnitId);
	    jobTransaction.setTransUnitFullName(transUnitFullName);
	    jobTransaction.setTransRankId(transRankId);
	    jobTransaction.setTransMinorSpecId(transMinorSpecId);
	    jobTransaction.setDecisionApprovedId(decisionData.getDecisionApprovedId());
	    jobTransaction.setOriginalDecisionApprovedId(decisionData.getOriginalDecisionApprovedId());
	    jobTransaction.setDecisionRegionId(decisionData.getDecisionRegionId());

	    DataAccess.addEntity(jobTransaction, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    if (e instanceof BusinessException) {
		throw (BusinessException) e;
	    } else {
		e.printStackTrace();
		throw new BusinessException("error_general");
	    }
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    /**
     * Gets the transactions of a specific job.
     * 
     * </br>
     * wrapper for #{@link #searchJobTransactions(Long)}.
     * 
     * @param jobId
     *            a <code>Long</code> containing the job ID to get its transactions
     * @return a <code>List</code> of {@link JobTransactionData} of a specific job
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<JobTransactionData> getJobTransactionsDataByJobId(Long jobId) throws BusinessException {
	return searchJobTransactions(jobId);
    }

    /**
     * Gets the transactions of a specific job.
     * 
     * @param jobId
     *            a <code>Long</code> containing the job ID to get its transactions
     * @return a <code>List</code> of {@link JobTransactionData} of a specific job
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<JobTransactionData> searchJobTransactions(Long jobId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_JOB_ID", jobId);

	    return DataAccess.executeNamedQuery(JobTransactionData.class, QueryNamesEnum.HCM_GET_JOB_TRANSACTIONS_DATA_BY_JOB_ID.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<JobTransaction> getJobsTransactionsDecisions(long regionId, long categoryId, String decisionNumber, Date decisionDateFrom, Date decisionDateTo) throws BusinessException {
	return searchJobsDecisions(regionId, categoryId, decisionNumber, FlagsEnum.ALL.getCode(), decisionDateFrom, decisionDateTo,
		new Long[] { CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.JOB_CONSTRUCTION.getCode(), TransactionClassesEnum.JOBS.getCode()).getId(),
			CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.JOB_RENAME.getCode(), TransactionClassesEnum.JOBS.getCode()).getId(),
			CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.JOB_MODIFY_MINOR_SPECIALIZATION.getCode(), TransactionClassesEnum.JOBS.getCode()).getId(),
			CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.JOB_MOVE.getCode(), TransactionClassesEnum.JOBS.getCode()).getId(),
			CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.JOB_FREEZE.getCode(), TransactionClassesEnum.JOBS.getCode()).getId(),
			CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.JOB_UNFREEZE.getCode(), TransactionClassesEnum.JOBS.getCode()).getId(),
			CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.JOB_SCALE_UP.getCode(), TransactionClassesEnum.JOBS.getCode()).getId(),
			CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.JOB_SCALE_DOWN.getCode(), TransactionClassesEnum.JOBS.getCode()).getId(),
			CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.JOB_CANCELLATION.getCode(), TransactionClassesEnum.JOBS.getCode()).getId() });
    }

    public static List<JobTransaction> getJobsReservationDecisions(long regionId, String decisionNumber, int decisionType, Date decisionDateFrom, Date decisionDateTo) throws BusinessException {
	return searchJobsDecisions(regionId, CategoriesEnum.SOLDIERS.getCode(), decisionNumber, decisionType, decisionDateFrom, decisionDateTo,
		new Long[] { CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.JOB_RESERVE.getCode(), TransactionClassesEnum.JOBS.getCode()).getId(),
			CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.JOB_UNRESERVE.getCode(), TransactionClassesEnum.JOBS.getCode()).getId() });
    }

    private static List<JobTransaction> searchJobsDecisions(long regionId, long categoryId, String decisionNumber, int decisionType, Date decisionDateFrom, Date decisionDateTo, Long[] transactionsTypesIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_DECISION_NUMBER", (decisionNumber == null || decisionNumber.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : decisionNumber);
	    qParams.put("P_REGION_ID", regionId);
	    qParams.put("P_CATEGORY_ID", categoryId);
	    qParams.put("P_RESERVATION_STATUS", decisionType);
	    qParams.put("P_TRANSACTIONS_TYPES_IDS", transactionsTypesIds);

	    if (decisionDateFrom != null) {
		qParams.put("P_DECISION_DATE_FROM_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_DECISION_DATE_FROM", HijriDateService.getHijriDateString(decisionDateFrom));
	    } else {
		qParams.put("P_DECISION_DATE_FROM_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_DECISION_DATE_FROM", HijriDateService.getHijriSysDateString());
	    }

	    if (decisionDateTo != null) {
		qParams.put("P_DECISION_DATE_TO_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_DECISION_DATE_TO", HijriDateService.getHijriDateString(decisionDateTo));
	    } else {
		qParams.put("P_DECISION_DATE_TO_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_DECISION_DATE_TO", HijriDateService.getHijriSysDateString());
	    }
	    return DataAccess.executeNamedQuery(JobTransaction.class, QueryNamesEnum.HCM_GET_JOBS_DECISIONS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets the number of jobs transactions within specific basic job name id.
     * 
     * @param basicJobNameId
     * @return the number of jobs found in the input basicJobNameId
     * @throws BusinessException
     */
    public static long countJobsTransactionsByBasicJobNameId(long basicJobNameId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_BASIC_JOB_NAME_ID", basicJobNameId);

	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_JOBS_TRANSACTIONS_BY_BASIC_JOB_NAME_ID.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*---------------------------------------------- Basic Jobs Names ----------------------------------------------------------*/

    /**
     * Adds a new basic job name.
     * 
     * @param basicJobsNamesData
     *            the {@link BasicJobNameData} object to be created
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    public static void addBasicJobName(BasicJobNameData basicJobNameData, long userId, CustomSession... useSession) throws BusinessException {

	validateBasicJobName(basicJobNameData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    basicJobNameData.setBasicJobName(basicJobNameData.getBasicJobName().trim());
	    basicJobNameData.getBasicJobNameEntity().setSystemUser(userId + ""); // For Auditing.
	    DataAccess.addEntity(basicJobNameData.getBasicJobNameEntity(), session);
	    basicJobNameData.setId(basicJobNameData.getBasicJobNameEntity().getId());

	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (Exception e) {

	    basicJobNameData.setId(null);

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
     * Modifies a basic job name.
     * 
     * @param basicJobNameData
     *            the {@link BasicJobNameData} object to be updated
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    public static void modifyBasicJobName(BasicJobNameData basicJobNameData, long userId, CustomSession... useSession) throws BusinessException {

	validateBasicJobName(basicJobNameData);
	checkBasicJobNameConflicts(basicJobNameData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    basicJobNameData.setBasicJobName(basicJobNameData.getBasicJobName().trim());
	    basicJobNameData.getBasicJobNameEntity().setSystemUser(userId + ""); // For Auditing.
	    DataAccess.updateEntity(basicJobNameData.getBasicJobNameEntity(), session);

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
     * Deletes a basic job name.
     * 
     * @param basicJobNameData
     *            the {@link BasicJobNameData} object to be deleted
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    public static void deleteBasicJobName(BasicJobNameData basicJobNameData, long userId, CustomSession... useSession) throws BusinessException {

	checkBasicJobNameConflicts(basicJobNameData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    basicJobNameData.getBasicJobNameEntity().setSystemUser(userId + ""); // For Auditing.
	    DataAccess.deleteEntity(basicJobNameData.getBasicJobNameEntity(), session);

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
     * Validates that :-
     * <ul>
     * <li>The basic job name is different from the original one if the transaction is modify basic job name</li>
     * <li>The basic job name is unique within the same category whatever the transaction is add or modify</li>
     * 
     * @param basicJobNameData
     *            the {@link BasicJobNameData} object to be updated
     * @throws BusinessException
     *             if any validation fails
     */
    private static void validateBasicJobName(BasicJobNameData basicJobNameData) throws BusinessException {

	if (basicJobNameData.getBasicJobName() == null || basicJobNameData.getBasicJobName().trim().length() == 0)
	    throw new BusinessException("error_basicJobNameIsMandatory");

	if (basicJobNameData.getId() != null) {
	    // modify transaction
	    BasicJobNameData originalBasicJobNameData = getBasicJobNameById(basicJobNameData.getId(), basicJobNameData.getCategoryId());
	    if (originalBasicJobNameData != null && basicJobNameData.getBasicJobName().trim().equals(originalBasicJobNameData.getBasicJobName().trim()))
		throw new BusinessException("error_newBasicJobNameMustBeDifferentFromOriginalBasicJobName");
	}

	// check the uniqueness of the basic job name within the same category.
	if (countBasicJobsNames(basicJobNameData.getBasicJobName().trim(), basicJobNameData.getCategoryId()) > 0)
	    throw new BusinessException("error_basicJobNameUniqueWithCategory");
    }

    private static void checkBasicJobNameConflicts(BasicJobNameData basicJobNameData) throws BusinessException {

	if (JobsService.countJobsByBasicJobNameId(basicJobNameData.getId()) > 0 ||
		JobsService.countJobsTransactionsByBasicJobNameId(basicJobNameData.getId()) > 0 ||
		BusinessWorkflowCooperation.countJobsRequestsByBasicJobNameId(basicJobNameData.getId()) > 0)
	    throw new BusinessException("error_basicJobNameConflicts");
    }

    /**
     * Gets the number of basic jobs names with the same category and name.
     * 
     * @param basicJobName
     *            the name of the job to search for it
     * @param categoryId
     *            the category of the basic job name to search for it
     * @return the count of the basic jobs names with the same category and name
     * @throws BusinessException
     *             if any database error occur
     */
    private static long countBasicJobsNames(String basicJobName, Long categoryId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_BASIC_JOB_NAME", basicJobName);
	    qParams.put("P_CATEGORY_ID", categoryId);

	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_BASIC_JOBS_NAMES.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets all basic jobs names with specific basic job name and category.
     * 
     * </br>
     * wrapper for {@link #searchBasicJobsNames(long, String, long)}.
     * 
     * @param basicJobName
     * @param categoryId
     * @return a <code>List</code> of {@link BasicJobNameData} which matches the search criteria
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<BasicJobNameData> getBasicJobsNames(String basicJobName, Long[] categoriesIds, Long[] basicJobsNamesIdsToExclude) throws BusinessException {
	return searchBasicJobsNames(FlagsEnum.ALL.getCode(), basicJobName, categoriesIds, basicJobsNamesIdsToExclude);
    }

    /**
     * Gets a specific basic job name by its ID.
     * 
     * </br>
     * wrapper for {@link #searchBasicJobsNames(long, String, long)}.
     * 
     * @param basicJobNameId
     * @return
     * @throws BusinessException
     *             if any error occurs
     */
    private static BasicJobNameData getBasicJobNameById(long basicJobNameId, long categoryId) throws BusinessException {
	List<BasicJobNameData> BasicJobNameDataList = searchBasicJobsNames(basicJobNameId, null, new Long[] { categoryId }, null);
	if (BasicJobNameDataList.size() > 0)
	    return BasicJobNameDataList.get(0);
	else
	    return null;
    }

    /**
     * Gets all basic jobs names with specific ID, name, and category.
     * 
     * @param basicJobNameId
     * @param basicJobName
     * @param categoryId
     * @param basicJobsNamesIdsToExclude
     * @return a <code>List</code> of {@link BasicJobNameData} which matches the search criteria
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<BasicJobNameData> searchBasicJobsNames(long basicJobNameId, String basicJobName, Long[] categoriesIds, Long[] basicJobsNamesIdsToExclude) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_BASIC_JOB_NAME_ID", basicJobNameId);
	    qParams.put("P_BASIC_JOB_NAME", (basicJobName == null || basicJobName.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : ("%" + basicJobName + "%"));

	    if (categoriesIds != null && categoriesIds.length > 0) {
		qParams.put("P_CATEGORIES_IDS", categoriesIds);
	    } else {
		qParams.put("P_CATEGORIES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }

	    if (basicJobsNamesIdsToExclude != null && basicJobsNamesIdsToExclude.length > 0) {
		qParams.put("P_BASIC_JOBS_NAMES_EXCLUDED_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_BASIC_JOBS_NAMES_EXCLUDED_IDS", basicJobsNamesIdsToExclude);
	    } else {
		qParams.put("P_BASIC_JOBS_NAMES_EXCLUDED_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_BASIC_JOBS_NAMES_EXCLUDED_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }

	    return DataAccess.executeNamedQuery(BasicJobNameData.class, QueryNamesEnum.HCM_SEARCH_BASIC_JOBS_NAMES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*---------------------------------------------- Reports -------------------------------------------------------------------*/

    /**
     * Gets the bytes to be sent to the report to print the jobs reports.
     * 
     * @param reportType
     *            an <code>int</code> containing the type of the report (10) jobs details report. (20) jobs transactions details report. (30) jobs statistics report.
     * @param categoryId
     *            a <code>long</code> containing the employee category which is (1) Officer, (2) Soldier or (3, 4, 5, 6, 9) Employee
     * @param selectedRegionId
     *            a <code>long</code> containing the ID of the region to get the jobs in it
     * @param regionDesc
     *            a <code>String</code> containing the description of the region to be displayed in the report
     * @param selectedUnitId
     *            a <code>long</code> containing the ID of the unit to get the jobs in it
     * @param selectedUnitHkey
     *            a <code>String</code> containing the HKey of the unit to get the jobs in it or its children
     * @param selectedUnitFullName
     *            a <code>String</code> containing the name of the unit to be displayed in the report
     * @param jobStatus
     *            a comma separated <code>String</code> containing the statuses of the jobs to search for them
     * @param jobStatusDesc
     *            a comma separated <code>String</code> containing the jobs statuses descriptions to be displayed in the report
     * @param jobReservedFlag
     *            an <code>int</code> containing the job reserved flag to search for the jobs which are reserved or not
     * @param fromDate
     *            a <code>Date</code> containing the from hijri date, in mm/MM/yyyy format, to start searching the transactions from it
     * @param toDate
     *            a <code>Date</code> containing the to hijri date, in mm/MM/yyyy format, to end searching the transactions at it
     * @param jobTransactionType
     *            a <code>long</code> containing the job transaction type to search for it
     * @param generalSpec
     *            an <code>int</code> containing the general specialization value to search for it
     * @param generalSpecDesc
     *            a <code>String</code> containing the general specialization description to be displayed in the report
     * @param jobType
     *            an <code>int</code> containing the job type value to search for it
     * @param jobTypeDesc
     *            a <code>String</code> containing the job type description to be displayed in the report
     * @param approvedFlag
     *            an <code>int</code> containing the job approved flag to search for the jobs which are approved or not
     * @param approvedFlagDesc
     *            a <code>String</code> containing the job approved flag description to be displayed in the report
     * @param minorSpec
     *            an <code>int</code> containing the minor specialization value to search for it
     * @param minorSpecDesc
     *            a <code>String</code> containing the minor specialization description to be displayed in the report
     * @param rankId
     *            a <code>long</code> containing the rank to to search for it
     * @param reportTitle
     *            a <code>String</code> containing the title of the report
     * @return an array of bytes to be sent to the report
     * @throws BusinessException
     *             if any error occurs
     */
    public static byte[] getJobsReportsBytes(int reportType, long categoryId, long selectedRegionId, String regionDesc, long selectedUnitId, String selectedUnitHkey,
	    String selectedUnitFullName, String jobName, String jobStatus, String jobStatusDesc, int jobReservationStatus, Date fromDate, Date toDate,
	    long jobTransactionType, int generalSpec, String generalSpecDesc, int jobType, String jobTypeDesc, int approvedFlag, String approvedFlagDesc,
	    int minorSpec, String minorSpecDesc, long civiliansCategoryId, long rankId, boolean recruitmentsFlag, boolean movementsFlag,
	    boolean promotionsFlag, String reportTitle) throws BusinessException {
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    String reportName = "";
	    if (reportType == 10) {
		reportName = ReportNamesEnum.JOBS_DETAILS.getCode();

		parameters.put("P_JOB_NAME", jobName);
		parameters.put("P_JOB_STATUS", jobStatus);
		parameters.put("P_JOB_STATUS_DESC", jobStatusDesc);
		parameters.put("P_JOB_RESERVATION_STATUS", jobReservationStatus);
		parameters.put("P_CIVILIANS_CATEGORY_ID", civiliansCategoryId);
		parameters.put("P_JOB_RANK", rankId);
		parameters.put("P_JOB_MINOR_SPECS", minorSpec);
		parameters.put("P_JOB_MINOR_SPECS_DESC", minorSpecDesc);

	    } else if (reportType == 20) {
		reportName = ReportNamesEnum.JOBS_TRANSACTIONS_DETAILS.getCode();

		parameters.put("P_FROM_DATE", HijriDateService.getHijriDateString(fromDate));
		parameters.put("P_TO_DATE", HijriDateService.getHijriDateString(toDate));
		parameters.put("P_JOB_TRANSACTION_TYPE", jobTransactionType);

	    } else if (reportType == 30) {
		reportName = ReportNamesEnum.JOBS_STATISTICAL.getCode();

		parameters.put("P_REGION_DESC", regionDesc);
		parameters.put("P_UNIT_ID", selectedUnitId);
		parameters.put("P_JOB_NAME", jobName);
		parameters.put("P_GENERAL_SPECIALIZATION", generalSpec);
		parameters.put("P_GENERAL_SPECIALIZATION_DESC", generalSpecDesc);
		parameters.put("P_JOB_TYPE", jobType);
		parameters.put("P_JOB_TYPE_DESC", jobTypeDesc);
		parameters.put("P_JOB_STATUS", jobStatus);
		parameters.put("P_JOB_STATUS_DESC", jobStatusDesc);
		parameters.put("P_JOB_APPROVED_FLAG", approvedFlag);
		parameters.put("P_JOB_APPROVED_FLAG_DESC", approvedFlagDesc);
		parameters.put("P_JOB_MINOR_SPECS", minorSpec);
		parameters.put("P_JOB_MINOR_SPECS_DESC", minorSpecDesc);

	    } else if (reportType == 40) {
		reportName = ReportNamesEnum.JOBS_IN_TRANSACTIONS.getCode();

		parameters.put("P_REGION_DESC", regionDesc);
		parameters.put("P_RECRUITMENTS_FLAG", recruitmentsFlag ? 1 : 0);
		parameters.put("P_MOVEMENTS_FLAG", movementsFlag ? 1 : 0);
		parameters.put("P_PROMOTIONS_FLAG", promotionsFlag ? 1 : 0);

	    } else if (reportType == 50) {
		reportName = ReportNamesEnum.JOBS_IN_TRANSACTIONS_STATISTICAL.getCode();

		parameters.put("P_REGION_DESC", regionDesc);
		parameters.put("P_RECRUITMENTS_FLAG", recruitmentsFlag ? 1 : 0);
		parameters.put("P_MOVEMENTS_FLAG", movementsFlag ? 1 : 0);
		parameters.put("P_PROMOTIONS_FLAG", promotionsFlag ? 1 : 0);

	    } else if (reportType == 60) {
		reportName = ReportNamesEnum.JOBS_FREEZED_BY_OTHER_SYSTEMS.getCode();

		parameters.put("P_REGION_DESC", regionDesc);
		parameters.put("P_MOVEMENTS_FLAG", movementsFlag ? 1 : 0);
		parameters.put("P_PROMOTIONS_FLAG", promotionsFlag ? 1 : 0);
		parameters.put("P_FROM_DATE", HijriDateService.getHijriDateString(fromDate));
		parameters.put("P_TO_DATE", HijriDateService.getHijriDateString(toDate));
	    }

	    parameters.put("P_REGION_ID", selectedRegionId);
	    parameters.put("P_UNIT_HKEY_PREFIX", UnitsService.getHKeyPrefix(selectedUnitHkey));
	    parameters.put("P_UNIT_FULL_NAME", selectedUnitFullName);

	    parameters.put("P_CATEGORY_ID", categoryId);

	    parameters.put("P_REPORT_TITLE", reportTitle);
	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    /**
     * Gets the bytes to be sent to the report to print the jobs decisions.
     * 
     * 
     * @param reportType
     *            an <code>int</code> containing the type of the report (10) officers jobs transactions decision. (20) soldiers jobs transactions decision. (30) soldiers jobs reservations decision.
     * @param decisionNumber
     *            the job transaction decision number
     * @param decisionDate
     *            the job transaction decision date in mm/MM/yyyy format
     * @param reservationStatus
     *            an Integer represents the reservation status (0:UnReserve, 1:Reserve, 2:Reserve for Recruitment)
     * @return an array of bytes to be sent to the report
     * @throws BusinessException
     *             if any error occurs
     */
    public static byte[] getJobDecisionBytes(int reportType, String decisionNumber, Date decisionDate) throws BusinessException {
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    String reportName = "";
	    if (reportType == 10)
		reportName = ReportNamesEnum.JOBS_DECISION_OFFICERS_COLLECTIVE.getCode();
	    else if (reportType == 20)
		reportName = ReportNamesEnum.JOBS_DECISION_SOLDIERS_COLLECTIVE.getCode();
	    else if (reportType == 30)
		reportName = ReportNamesEnum.JOBS_DECISION_SOLDIERS_COLLECTIVE_RESERVATIONS.getCode();

	    parameters.put("P_DECISION_NUMBER", decisionNumber);
	    parameters.put("P_DECISION_DATE", HijriDateService.getHijriDateString(decisionDate));

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

}