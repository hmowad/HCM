package com.code.services.hcm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.incentives.IncentivePort;
import com.code.dal.orm.hcm.incentives.IncentiveTransaction;
import com.code.dal.orm.setup.GovernmentalCommitteeCategory;
import com.code.enums.IncentiveTypesEnum;
import com.code.enums.QueryNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.util.HijriDateService;

public class IncentivesService extends BaseService {

    private IncentivesService() {
    }

    public static void addIncentiveTransaction(IncentiveTransaction incentiveTransaction) throws Exception {
	validateIncentiveTransaction(incentiveTransaction);
	CustomSession session = DataAccess.getSession();

	try {
	    session.beginTransaction();

	    incentiveTransaction.setTransactionDate(HijriDateService.getHijriSysDate());
	    DataAccess.addEntity(incentiveTransaction, session);

	    session.commitTransaction();
	} catch (Exception e) {
	    session.rollbackTransaction();
	    throw e;
	} finally {
	    session.close();
	}
    }

    private static void validateIncentiveTransaction(IncentiveTransaction incentiveTransaction) throws BusinessException {
	if (incentiveTransaction == null)
	    throw new BusinessException("error_general");
	if (incentiveTransaction.getIncentiveTypeId() == null)
	    throw new BusinessException("error_integrationMandatoryFields");
	if (incentiveTransaction.getEmployeeId() == null)
	    throw new BusinessException("error_integrationMandatoryFields");
	if (incentiveTransaction.getStartDate() == null)
	    throw new BusinessException("error_integrationMandatoryFields");
	if (incentiveTransaction.getIncentiveTypeId().equals(IncentiveTypesEnum.FINANCIAL_LOSS_COMPENSATION.getCode())) {
	    if (incentiveTransaction.getCompensationAmount() == null)
		throw new BusinessException("error_integrationMandatoryFields");
	} else if (incentiveTransaction.getIncentiveTypeId().equals(IncentiveTypesEnum.ADVISORY_COUNCILS.getCode())) {
	    if (incentiveTransaction.getPortId() == null)
		throw new BusinessException("error_integrationMandatoryFields");
	} else if (incentiveTransaction.getIncentiveTypeId().equals(IncentiveTypesEnum.GOVERNMENTAL_COMMITTEES.getCode())) {
	    if (incentiveTransaction.getSessionsCount() == null)
		throw new BusinessException("error_integrationMandatoryFields");
	    if (incentiveTransaction.getCommittieeCategoryId() == null)
		throw new BusinessException("error_integrationMandatoryFields");
	} else if (incentiveTransaction.getIncentiveTypeId().equals(IncentiveTypesEnum.DRUGS_DESTRUCTION.getCode())) {
	    if (incentiveTransaction.getDestructionsCount() == null)
		throw new BusinessException("error_integrationMandatoryFields");
	} else if (incentiveTransaction.getIncentiveTypeId().equals(IncentiveTypesEnum.TEST_COMMITTEES.getCode())) {
	    if (incentiveTransaction.getPeriod() == null)
		throw new BusinessException("error_integrationMandatoryFields");
	} else if (incentiveTransaction.getIncentiveTypeId().equals(IncentiveTypesEnum.MISSIONS_DIFFERENCES.getCode())) {
	    if (incentiveTransaction.getMissionTransactionId() == null)
		throw new BusinessException("error_integrationMandatoryFields");
	}
    }

    public static List<IncentivePort> getIncentivePorts() throws DatabaseException {
	Map<String, Object> qParams = new HashMap<String, Object>();
	return DataAccess.executeNamedQuery(IncentivePort.class, QueryNamesEnum.HCM_INCENTIVE_PORT_GET_INCENTIVE_PORTS.getCode(), qParams);
    }

    public static List<GovernmentalCommitteeCategory> getGovernmentalCommitteesCategories() throws DatabaseException {
	Map<String, Object> qParams = new HashMap<String, Object>();
	return DataAccess.executeNamedQuery(GovernmentalCommitteeCategory.class, QueryNamesEnum.HCM_GOVERNMENTAL_COMMITTEE_CATEGORY_GET_GOVERNMENTAL_COMMITTEES_CATEGORIES.getCode(), qParams);
    }
}
