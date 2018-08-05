package com.code.services.hcm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.dal.orm.hcm.organization.jobs.JobsBalanceData;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.util.CommonService;

public class JobsBalancesService extends BaseService {

    public static void modifyJobsBalance(JobsBalanceData jobsBalanceData, long userId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    jobsBalanceData.getJobsBalance().setSystemUser(userId + ""); // For Auditing
	    DataAccess.updateEntity(jobsBalanceData.getJobsBalance(), session);

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

    protected static void modifyJobsBalances(List<JobsBalanceData> jobsBalanceData, CustomSession session) throws BusinessException {

	if (jobsBalanceData == null || jobsBalanceData.size() == 0)
	    return;

	try {
	    for (JobsBalanceData jobBalance : jobsBalanceData) {
		DataAccess.updateEntity(jobBalance.getJobsBalance(), session);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    protected static List<JobsBalanceData> validateJobsBalances(List<JobData> jobsToAdd, List<JobData> jobsToCancel, List<JobData> jobsToFreeze, List<JobData> jobsToUnfreeze, List<JobData> jobsToScale, List<JobData> jobsToMove) throws BusinessException {

	Map<String, Long> requiredJobsMap = new HashMap<>(); // key=region:rank, value=count
	String key = "";

	// Jobs to be Added
	if (jobsToAdd != null && jobsToAdd.size() > 0) {
	    for (JobData jobData : jobsToAdd) {
		if (jobData.getApprovedFlag() == FlagsEnum.ON.getCode()) {
		    key = jobData.getRegionId() + ":" + jobData.getRankId();
		    requiredJobsMap.put(key, requiredJobsMap.containsKey(key) ? requiredJobsMap.get(key) + 1 : 1);
		}
	    }
	}

	// Jobs to be Cancelled
	if (jobsToCancel != null && jobsToCancel.size() > 0) {
	    for (JobData jobData : jobsToCancel) {
		if (jobData.getApprovedFlag() == FlagsEnum.ON.getCode()) {
		    key = jobData.getRegionId() + ":" + jobData.getRankId();
		    requiredJobsMap.put(key, requiredJobsMap.containsKey(key) ? requiredJobsMap.get(key) - 1 : -1);
		}
	    }
	}

	// Jobs to be Freezed
	if (jobsToFreeze != null && jobsToFreeze.size() > 0) {
	    for (JobData jobData : jobsToFreeze) {
		key = jobData.getRegionId() + ":" + jobData.getRankId();
		requiredJobsMap.put(key, requiredJobsMap.containsKey(key) ? requiredJobsMap.get(key) - 1 : -1);
	    }
	}

	// Jobs to be UnFreezed
	if (jobsToUnfreeze != null && jobsToUnfreeze.size() > 0) {
	    for (JobData jobData : jobsToUnfreeze) {
		key = jobData.getRegionId() + ":" + jobData.getRankId();
		requiredJobsMap.put(key, requiredJobsMap.containsKey(key) ? requiredJobsMap.get(key) + 1 : 1);
	    }
	}

	// Jobs to be Scaled
	if (jobsToScale != null && jobsToScale.size() > 0) {
	    for (JobData jobData : jobsToScale) {
		if (jobData.getApprovedFlag() == FlagsEnum.ON.getCode()) {
		    key = jobData.getRegionId() + ":" + jobData.getNewRankId();
		    requiredJobsMap.put(key, requiredJobsMap.containsKey(key) ? requiredJobsMap.get(key) + 1 : 1);

		    key = jobData.getRegionId() + ":" + jobData.getRankId();
		    requiredJobsMap.put(key, requiredJobsMap.containsKey(key) ? requiredJobsMap.get(key) - 1 : -1);
		}
	    }
	}

	// Jobs to be Moved - to New Region only
	if (jobsToMove != null && jobsToMove.size() > 0) {
	    for (JobData jobData : jobsToMove) {
		if (jobData.getApprovedFlag() == FlagsEnum.ON.getCode() && !jobData.getRegionId().equals(jobData.getNewRegionId())) {
		    key = jobData.getNewRegionId() + ":" + jobData.getRankId();
		    requiredJobsMap.put(key, requiredJobsMap.containsKey(key) ? requiredJobsMap.get(key) + 1 : 1);

		    key = jobData.getRegionId() + ":" + jobData.getRankId();
		    requiredJobsMap.put(key, requiredJobsMap.containsKey(key) ? requiredJobsMap.get(key) - 1 : -1);
		}
	    }
	}

	if (!requiredJobsMap.isEmpty()) {
	    List<JobsBalanceData> JobsBalances = JobsBalancesService.getJobsBalancesDataByRegionsAndRanks(requiredJobsMap.keySet().toArray(new String[0]));
	    for (JobsBalanceData jobsBalance : JobsBalances) {
		Long requiredJobsNumber = requiredJobsMap.get(jobsBalance.getRegionId() + ":" + jobsBalance.getRankId());
		if (requiredJobsNumber != null && requiredJobsNumber > Math.max(0, jobsBalance.getAvailableJobsBalance()))
		    throw new BusinessException("error_requiredJobsGreaterThanAvailableJobsForRegionAndRank",
			    new String[] { CommonService.getRegionById(jobsBalance.getRegionId()).getDescription(),
				    jobsBalance.getRankDescription(), jobsBalance.getAvailableJobsBalance() + "", requiredJobsNumber + "" });
	    }

	    return JobsBalances;
	}

	return null;
    }

    public static List<JobsBalanceData> getJobsBalancesData(long regionId, long categoryId) throws BusinessException {
	if (regionId == FlagsEnum.ALL.getCode())
	    return getTotalJobsBalancesData(categoryId);

	return searchJobsBalancesData(regionId, categoryId);
    }

    private static List<JobsBalanceData> getTotalJobsBalancesData(long categoryId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_CATEGORY_ID", categoryId);

	    return DataAccess.executeNamedQuery(JobsBalanceData.class, QueryNamesEnum.HCM_GET_TOTAL_JOBS_BALANCES_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<JobsBalanceData> searchJobsBalancesData(long regionId, long categoryId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_CATEGORY_ID", categoryId);
	    qParams.put("P_REGION_ID", regionId);
	    return DataAccess.executeNamedQuery(JobsBalanceData.class, QueryNamesEnum.HCM_GET_JOBS_BALANCES_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<JobsBalanceData> getJobsBalancesDataByRegionsAndRanks(String[] regionsIdsAndRanksIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_REGIONS_IDS_AND_RANKS_IDS", regionsIdsAndRanksIds);
	    return DataAccess.executeNamedQuery(JobsBalanceData.class, QueryNamesEnum.HCM_GET_JOBS_BALANCE_BY_REGIONS_AND_RANKS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

}
