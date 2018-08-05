package com.code.ui.backings.hcm.trainings;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingUnitData;
import com.code.enums.RegionsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingSetupService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "trainingUnitsConfiguration")
@ViewScoped
public class TrainingUnitsConfiguration extends BaseBacking {

    private List<TrainingUnitData> trainingUnitsData;

    private Map<Long, Map<Long, Boolean>> regionsTrainingConfigMap; // Long -> UnitId, Map<Long, Boolean> Long->RegionId Boolean-> Nomination allowance

    private int pageSize = 10;

    public TrainingUnitsConfiguration() {
	try {
	    setScreenTitle(getMessage("title_trainingUnitsConfiguration"));
	    trainingUnitsData = TrainingSetupService.getAllTrainingUnitsData();
	    regionsTrainingConfigMap = TrainingSetupService.getRegionsTrainingConfigMapForTrainingUnitsList(trainingUnitsData);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void manipulateRegionsTrainingConfig(String enumConstant, TrainingUnitData trainingUnitData) {
	try {
	    long regionId = getRegionIdByEnumConstant(enumConstant);
	    if (regionsTrainingConfigMap.get(trainingUnitData.getUnitId()).get(regionId)) {
		trainingUnitData.setRegionsTrainingConfig(trainingUnitData.getRegionsTrainingConfig().replace(',' + String.valueOf(regionId) + '-' + '1' + ',', ',' + String.valueOf(regionId) + '-' + '0' + ','));
		regionsTrainingConfigMap.get(trainingUnitData.getUnitId()).put(regionId, false);
	    } else {
		trainingUnitData.setRegionsTrainingConfig(trainingUnitData.getRegionsTrainingConfig().replace(',' + String.valueOf(regionId) + '-' + '0' + ',', ',' + String.valueOf(regionId) + '-' + '1' + ','));
		regionsTrainingConfigMap.get(trainingUnitData.getUnitId()).put(regionId, true);
	    }

	    trainingUnitData.getTrainingUnit().setSystemUser(loginEmpData.getEmpId() + "");
	    TrainingSetupService.updateTrainingUnit(trainingUnitData);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public boolean isNominationAllowed(String enumConstant, TrainingUnitData trainingUnitData) {
	long regionId = getRegionIdByEnumConstant(enumConstant);
	return regionsTrainingConfigMap.get(trainingUnitData.getUnitId()).get(regionId);
    }

    public boolean isDisabled(String enumConstant, TrainingUnitData trainingUnitData) {
	long regionId = getRegionIdByEnumConstant(enumConstant);

	if (trainingUnitData.getRegionId() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() || trainingUnitData.getRegionId() == RegionsEnum.ACADEMY.getCode() || regionId == trainingUnitData.getRegionId())
	    return true;

	return false;
    }

    private long getRegionIdByEnumConstant(String enumConstant) {
	return Enum.valueOf(RegionsEnum.class, enumConstant).getCode();
    }

    public List<TrainingUnitData> getTrainingUnitsData() {
	return trainingUnitsData;
    }

    public void setTrainingUnitsData(List<TrainingUnitData> trainingUnitsData) {
	this.trainingUnitsData = trainingUnitsData;
    }

    public Map<Long, Map<Long, Boolean>> getRegionsTrainingConfigMap() {
	return regionsTrainingConfigMap;
    }

    public void setRegionsTrainingConfigMap(Map<Long, Map<Long, Boolean>> regionsTrainingConfigMap) {
	this.regionsTrainingConfigMap = regionsTrainingConfigMap;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }
}
