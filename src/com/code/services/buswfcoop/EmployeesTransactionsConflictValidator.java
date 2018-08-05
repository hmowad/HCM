package com.code.services.buswfcoop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.orm.workflow.hcm.missions.WFMissionDetail;
import com.code.enums.FlagsEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.BaseService;
import com.code.services.hcm.MissionsService;
import com.code.services.hcm.VacationsService;
import com.code.services.workflow.hcm.MissionsWorkFlow;
import com.code.services.workflow.hcm.VacationsWorkFlow;

public class EmployeesTransactionsConflictValidator extends BaseService {

    private EmployeesTransactionsConflictValidator() {
    }

    public static void validateEmployeesTransactionsConflicts(Long[] empsIds, String[] empsNames, int transactionClass, int transactionType, long transactionBusinessType, long processId, String[] startDatesStrings, String[] endDatesStrings, Long excludedTransactionId, Long excludedInstanceId) throws BusinessException {
	if (empsIds == null || empsNames == null || startDatesStrings == null || endDatesStrings == null || empsIds.length != empsNames.length || empsIds.length != startDatesStrings.length || empsIds.length != endDatesStrings.length)
	    throw new BusinessException("error_general");

	Map<Long, Integer> employeesIdsIndexesMap = new HashMap<Long, Integer>();
	for (int i = 0; i < empsIds.length; i++)
	    employeesIdsIndexesMap.put(empsIds[i], i);

	if (transactionClass == TransactionClassesEnum.VACATIONS.getCode()) {

	    checkRunningVacationsRequestsConflict(empsIds, transactionBusinessType);

	    if (transactionType != TransactionTypesEnum.VACATION_CANCELLATION.getCode()) {

		checkRunningVacationsRequestsDatesConflict(empsIds, empsNames, startDatesStrings, endDatesStrings, employeesIdsIndexesMap);
		checkRunningMissionsRequestsConflict(empsIds, empsNames, FlagsEnum.ALL.getCode(), startDatesStrings, endDatesStrings, null, employeesIdsIndexesMap);

		checkVacationsTransactionsConflict(empsIds, empsNames, startDatesStrings, endDatesStrings, excludedTransactionId, employeesIdsIndexesMap);
		checkMissionsTransactionsConflict(empsIds, empsNames, startDatesStrings, endDatesStrings, null, employeesIdsIndexesMap);
	    }

	} else if (transactionClass == TransactionClassesEnum.MISSIONS.getCode()) {

	    checkRunningVacationsRequestsDatesConflict(empsIds, empsNames, startDatesStrings, endDatesStrings, employeesIdsIndexesMap);
	    checkRunningMissionsRequestsConflict(empsIds, empsNames, processId, startDatesStrings, endDatesStrings, excludedInstanceId, employeesIdsIndexesMap);

	    checkVacationsTransactionsConflict(empsIds, empsNames, startDatesStrings, endDatesStrings, null, employeesIdsIndexesMap);
	    checkMissionsTransactionsConflict(empsIds, empsNames, startDatesStrings, endDatesStrings, excludedTransactionId, employeesIdsIndexesMap);
	}
    }

    private static void checkRunningVacationsRequestsConflict(Long[] empsIds, long transactionBusinessType) throws BusinessException {
	VacationsWorkFlow.validateRunningVacationRequests(empsIds[0], transactionBusinessType);
    }

    private static void checkRunningVacationsRequestsDatesConflict(Long[] empsIds, String[] empsNames, String[] startDatesStrings, String[] endDatesStrings, Map<Long, Integer> employeesIdsIndexesMap) throws BusinessException {
	List<Long> conflictedEmpsIds = new ArrayList<Long>();
	for (int i = 0; i < empsIds.length; i++) {
	    if (VacationsWorkFlow.validateVacationRunningWFDatesConflict(empsIds[i], startDatesStrings[i], endDatesStrings[i]) > 0)
		conflictedEmpsIds.add(empsIds[i]);
	}

	if (conflictedEmpsIds.size() > 0)
	    constructBusinessException(conflictedEmpsIds, empsNames, "error_wfVacDatesConflict", employeesIdsIndexesMap);
    }

    private static void checkRunningMissionsRequestsConflict(Long[] empsIds, String[] empsNames, long processId, String[] startDatesStrings, String[] endDatesStrings, Long excludedInstanceId, Map<Long, Integer> employeesIdsIndexesMap) throws BusinessException {
	List<WFMissionDetail> missionDetails = new ArrayList<WFMissionDetail>();
	String errorMessage = "";

	if (processId != FlagsEnum.ALL.getCode()) {
	    missionDetails = MissionsWorkFlow.validateRunningMissionRequests(empsIds, null, null, excludedInstanceId);
	    errorMessage = "error_wfMissionsRequestsConflict";
	} else {
	    errorMessage = "error_wfMissionsDatesConflict";
	    for (int i = 0; i < empsIds.length; i++)
		missionDetails.addAll(MissionsWorkFlow.validateRunningMissionRequests(new Long[] { empsIds[i] }, startDatesStrings[i], endDatesStrings[i], excludedInstanceId));
	}

	if (missionDetails.size() > 0) {
	    List<Long> conflictedEmpsIds = new ArrayList<Long>();
	    for (WFMissionDetail detail : missionDetails) {
		conflictedEmpsIds.add(detail.getEmpId());
	    }

	    constructBusinessException(conflictedEmpsIds, empsNames, errorMessage, employeesIdsIndexesMap);
	}
    }

    private static void checkVacationsTransactionsConflict(Long[] empsIds, String[] empsNames, String[] startDatesStrings, String[] endDatesStrings, Long excludedTransactionId, Map<Long, Integer> employeesIdsIndexesMap) throws BusinessException {
	List<Long> conflictedEmpsIds = new ArrayList<Long>();
	for (int i = 0; i < empsIds.length; i++) {
	    if (VacationsService.checkDatesConflict(empsIds[i], startDatesStrings[i], endDatesStrings[i], excludedTransactionId) > 0)
		conflictedEmpsIds.add(empsIds[i]);
	}

	if (conflictedEmpsIds.size() > 0)
	    constructBusinessException(conflictedEmpsIds, empsNames, "error_vacDatesConflict", employeesIdsIndexesMap);

    }

    private static void checkMissionsTransactionsConflict(Long[] empsIds, String[] empsNames, String[] startDatesStrings, String[] endDatesStrings, Long excludedTransactionId, Map<Long, Integer> employeesIdsIndexesMap) throws BusinessException {

	List<Long> conflictedEmpsIds = new ArrayList<Long>();
	for (int i = 0; i < empsIds.length; i++) {
	    if (MissionsService.getEmpMissionsOverlap(empsIds[i], startDatesStrings[i], endDatesStrings[i], excludedTransactionId) > 0)
		conflictedEmpsIds.add(empsIds[i]);
	}

	if (conflictedEmpsIds.size() > 0)
	    constructBusinessException(conflictedEmpsIds, empsNames, "error_empMissionOverlapNotAllow", employeesIdsIndexesMap);

    }

    private static void constructBusinessException(List<Long> employeesIds, String[] employeesNames, String errorMessage, Map<Long, Integer> employeesIdsIndexesMap) throws BusinessException {

	String comma = ",\n";
	StringBuilder conflictedEmployeesNames = new StringBuilder();

	if (employeesIds != null && employeesIds.size() > 0) {
	    for (int i = 0; i < employeesIds.size(); i++)
		conflictedEmployeesNames.append(employeesNames[employeesIdsIndexesMap.get(employeesIds.get(i))]).append(comma);

	    throw new BusinessException(errorMessage, new Object[] { conflictedEmployeesNames.toString().substring(0, conflictedEmployeesNames.length() - 2) });
	}
    }

}
