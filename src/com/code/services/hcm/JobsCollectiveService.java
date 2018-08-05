package com.code.services.hcm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.JobStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.services.BaseService;

/**
 * Provides methods for handling the jobs collective operations such as construct, rename, scale or cancel jobs.
 * 
 */
public class JobsCollectiveService extends BaseService {

    /**
     * Constructs the jobs collective service.
     */
    private JobsCollectiveService() {
    }

    /*-----------------------------------------------------Jobs collective operations---------------------------------------------*/

    public static void constructJobs(String decisionNumber, Date decisionDate, Date executionDate,
	    List<UnitData> units, List<JobData> jobs, Long userId, CustomSession... useSession) throws BusinessException {

	validateConstructJobs(units, jobs);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    List<JobData> jobsToAdd = constructJobsToAdd(units, jobs);
	    JobsService.handleJobsTransactions(jobsToAdd, null, null, null, null, null, null, null, decisionNumber, decisionDate, executionDate, userId, false, false, false, true, useSession);

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

    public static void renameScaleJobs(String decisionNumber, Date decisionDate, Date executionDate,
	    List<JobData> selectedJobs, String newJobName, Long newRankId, Long newClassificationId, String reasons,
	    Long userId, CustomSession... useSession) throws BusinessException {

	List<JobData> jobsToRename = new ArrayList<JobData>();
	List<JobData> jobsToScale = new ArrayList<JobData>();
	for (JobData job : selectedJobs) {
	    // add all data required for service
	    job.setNewName(newJobName);
	    job.setNewRankId(newRankId);
	    job.setNewClassificationId(newClassificationId);
	    job.setReasons(reasons);

	    if ((job.getNewRankId() != null && !job.getNewRankId().equals((long) FlagsEnum.ALL.getCode()) && !job.getRankId().equals(job.getNewRankId()))) {
		jobsToScale.add(job);
	    } else {
		if (CategoriesEnum.OFFICERS.getCode() != job.getCategoryId().longValue()
			&& CategoriesEnum.SOLDIERS.getCode() != job.getCategoryId().longValue())
		    job.setSequence(job.getSerial().substring(3));

		jobsToRename.add(job);
	    }
	}

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    JobsService.handleJobsTransactions(null, jobsToRename, null, null, null, jobsToScale, null, null, decisionNumber, decisionDate, executionDate, userId, true, true, false, true, useSession);

	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (BusinessException e) {
	    for (JobData job : selectedJobs) {
		job.setNewName(null);
		job.setNewRankId(null);
		job.setNewClassificationId(null);
		job.setReasons(null);
	    }
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    for (JobData job : selectedJobs) {
		job.setNewName(null);
		job.setNewRankId(null);
		job.setNewClassificationId(null);
		job.setReasons(null);
	    }
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void moveJobs(String decisionNumber, Date decisionDate, Date executionDate,
	    List<JobData> jobsToMove, UnitData newUnit, String reasons, Long userId, CustomSession... useSession) throws BusinessException {

	for (JobData job : jobsToMove) {
	    // add all data required for service
	    job.setNewUnitId(newUnit.getId());
	    job.setNewUnitFullName(newUnit.getFullName());
	    job.setNewRegionId(newUnit.getRegionId());
	    job.setReasons(reasons);
	}

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    JobsService.handleJobsTransactions(null, null, null, null, null, null, jobsToMove, null, decisionNumber, decisionDate, executionDate, userId, true, false, false, true, useSession);

	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (BusinessException e) {
	    for (JobData job : jobsToMove) {
		job.setNewUnitId(null);
		job.setNewUnitFullName(null);
		job.setNewRegionId(null);
		job.setReasons(null);
	    }
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    for (JobData job : jobsToMove) {
		job.setNewUnitId(null);
		job.setNewUnitFullName(null);
		job.setNewRegionId(null);
		job.setReasons(null);
	    }
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    private static void validateConstructJobs(List<UnitData> units, List<JobData> jobsToAdd) throws BusinessException {

	if (jobsToAdd == null || jobsToAdd.size() == 0)
	    throw new BusinessException("error_emptyJobsTransactionsCollective");

	if (units == null || units.size() == 0)
	    throw new BusinessException("error_unitsListEmpty");

	for (JobData job : jobsToAdd) {
	    if (job.getName() == null || job.getName().length() == 0)
		throw new BusinessException("error_jobNameIsMandatory");
	    if (job.getRankId() == null)
		throw new BusinessException("error_rankIsMandatory");
	    if ((job.getCategoryId().longValue() == CategoriesEnum.OFFICERS.getCode() || job.getCategoryId().longValue() == CategoriesEnum.SOLDIERS
		    .getCode()) && job.getGeneralSpecialization() == null)
		throw new BusinessException("error_generalSpecializationIsMandatory");
	    if (job.getMinorSpecializationId() == null)
		throw new BusinessException("error_minorSpecializationIsMandatory");
	    if (job.getGeneralType() == null)
		throw new BusinessException("error_typeIsMandatory");
	    if (job.getStatus() == null)
		throw new BusinessException("error_statusIsMandatory");
	    if (job.getCategoryId().longValue() == CategoriesEnum.SOLDIERS.getCode() && job.getApprovedFlag() == null)
		throw new BusinessException("error_approvalStatusIsMandatory");
	    if (job.getCategoryId().longValue() == CategoriesEnum.PERSONS.getCode() && job.getClassificationId() == null)
		throw new BusinessException("error_classificationIsMandatory");

	    if (job.getStatus().intValue() != JobStatusEnum.VACANT.getCode() && job.getStatus().intValue() != JobStatusEnum.FROZEN.getCode())
		throw new BusinessException("error_jobStatusMustBeVacantOrFrozenJustAfterCreation");

	    if (job.getJobsCount() == null || job.getJobsCount().intValue() <= 0 || job.getJobsCount().intValue() > 999)
		throw new BusinessException("error_jobsCountMandatory");
	}
    }

    private static List<JobData> constructJobsToAdd(List<UnitData> units, List<JobData> jobs) {
	List<JobData> jobsToAdd = new ArrayList<JobData>();
	for (UnitData unit : units) {
	    for (JobData job : jobs) {
		for (int i = 0; i < job.getJobsCount().intValue(); i++) {
		    JobData jobToAdd = new JobData();
		    jobToAdd.setName(job.getName());
		    jobToAdd.setCategoryId(job.getCategoryId());
		    jobToAdd.setRankId(job.getRankId());
		    jobToAdd.setClassificationJobCode(job.getClassificationJobCode());
		    jobToAdd.setClassificationId(job.getClassificationId());
		    jobToAdd.setClassificationJobDescription(job.getClassificationJobDescription());
		    jobToAdd.setGeneralSpecialization(job.getGeneralSpecialization());
		    jobToAdd.setMinorSpecializationId(job.getMinorSpecializationId());
		    jobToAdd.setMinorSpecializationCode(job.getMinorSpecializationCode());
		    jobToAdd.setMinorSpecializationDescription(job.getMinorSpecializationDescription());
		    jobToAdd.setStatus(job.getStatus());

		    jobToAdd.setUnitId(unit.getId());
		    jobToAdd.setUnitFullName(unit.getFullName());
		    jobToAdd.setRegionId(unit.getRegionId());

		    jobToAdd.setGeneralType(job.getGeneralType());
		    jobToAdd.setApprovedFlag(job.getApprovedFlag());

		    jobToAdd.setReasons(job.getReasons());
		    jobToAdd.setJobDesc(job.getJobDesc());
		    jobToAdd.setDutiesDesc(job.getDutiesDesc());
		    jobToAdd.setRemarks(job.getRemarks());
		    jobsToAdd.add(jobToAdd);
		}
	    }
	}

	return jobsToAdd;
    }
}