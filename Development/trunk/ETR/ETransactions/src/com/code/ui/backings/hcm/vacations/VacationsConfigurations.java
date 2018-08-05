package com.code.ui.backings.hcm.vacations;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.hcm.vacations.VacationConfiguration;
import com.code.dal.orm.hcm.vacations.VacationType;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.VacationsService;
import com.code.services.security.SecurityService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "vacationsConfigurations")
@ViewScoped
public class VacationsConfigurations extends BaseBacking {

    private List<Category> categories;
    private long selectedCategoryId;

    private List<VacationType> vacationTypes;
    private long selectedVacationTypeId;

    // object[0] - VacationConfiguration
    // object[1] - VacationType
    // object[2] - Category Description
    private ArrayList<Object> vacationsConfigurationsList;
    private Object[] selectedVacConfig;
    private VacationConfiguration originalVacConfig;

    private boolean hasModifyPrivilege = false;

    private int pageSize = 5;

    public VacationsConfigurations() {
	try {
	    categories = CommonService.getAllCategories();
	    vacationTypes = VacationsService.getVacationTypes(FlagsEnum.ALL.getCode());
	    hasModifyPrivilege = SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.VAC_VACATIONS_CONFIGURATIONS.getCode(), MenuActionsEnum.VAC_CONFIGURATIONS_MODIFY.getCode());
	    resetForm();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetForm() {
	selectedCategoryId = FlagsEnum.ALL.getCode();
	selectedVacationTypeId = FlagsEnum.ALL.getCode();

	vacationsConfigurationsList = new ArrayList<Object>();
	selectedVacConfig = null;
    }

    public void search() {
	try {
	    vacationsConfigurationsList = (ArrayList<Object>) VacationsService.searchVacationsConfigurations(selectedCategoryId, selectedVacationTypeId);
	    selectedVacConfig = null;
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectVacationConfiguration(Object[] vacConfig) {

	selectedVacConfig = vacConfig;

	originalVacConfig = new VacationConfiguration();
	originalVacConfig.setId(((VacationConfiguration) vacConfig[0]).getId());
	originalVacConfig.setBalance(((VacationConfiguration) vacConfig[0]).getBalance());
	originalVacConfig.setMinValue(((VacationConfiguration) vacConfig[0]).getMinValue());
	originalVacConfig.setMaxValue(((VacationConfiguration) vacConfig[0]).getMaxValue());
	originalVacConfig.setAllowedValues(((VacationConfiguration) vacConfig[0]).getAllowedValues());
	originalVacConfig.setAdditionalVacationDays(((VacationConfiguration) vacConfig[0]).getAdditionalVacationDays());
	originalVacConfig.setBalanceFrame(((VacationConfiguration) vacConfig[0]).getBalanceFrame());
	originalVacConfig.setExternalMinVacationDays(((VacationConfiguration) vacConfig[0]).getExternalMinVacationDays());
	originalVacConfig.setFirstYearMaxVacationDays(((VacationConfiguration) vacConfig[0]).getFirstYearMaxVacationDays());
	originalVacConfig.setMaxVacationsDaysPerYear(((VacationConfiguration) vacConfig[0]).getMaxVacationsDaysPerYear());
	originalVacConfig.setMaxVacationsDaysPerYearExceptional(((VacationConfiguration) vacConfig[0]).getMaxVacationsDaysPerYearExceptional());
	originalVacConfig.setMaxValueExceptional(((VacationConfiguration) vacConfig[0]).getMaxValueExceptional());
	originalVacConfig.setPeriodBeforeFirstVacation(((VacationConfiguration) vacConfig[0]).getPeriodBeforeFirstVacation());
	originalVacConfig.setPeriods(((VacationConfiguration) vacConfig[0]).getPeriods());
    }

    public void saveVacationConfig() {
	try {
	    VacationsService.modifyVacationConfiguration((VacationConfiguration) selectedVacConfig[0], originalVacConfig);
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<Category> getCategories() {
	return categories;
    }

    public void setCategories(List<Category> categories) {
	this.categories = categories;
    }

    public long getSelectedCategoryId() {
	return selectedCategoryId;
    }

    public void setSelectedCategoryId(long selectedCategoryId) {
	this.selectedCategoryId = selectedCategoryId;
    }

    public List<VacationType> getVacationTypes() {
	return vacationTypes;
    }

    public void setVacationTypes(List<VacationType> vacationTypes) {
	this.vacationTypes = vacationTypes;
    }

    public long getSelectedVacationTypeId() {
	return selectedVacationTypeId;
    }

    public void setSelectedVacationTypeId(long selectedVacationTypeId) {
	this.selectedVacationTypeId = selectedVacationTypeId;
    }

    public ArrayList<Object> getVacationsConfigurationsList() {
	return vacationsConfigurationsList;
    }

    public void setVacationsConfigurationsList(ArrayList<Object> vacationsConfigurationsList) {
	this.vacationsConfigurationsList = vacationsConfigurationsList;
    }

    public Object[] getSelectedVacConfig() {
	return selectedVacConfig;
    }

    public void selectedVacConfig(Object[] selectedVacConfig) {
	this.selectedVacConfig = selectedVacConfig;
    }

    public VacationConfiguration getOriginalVacConfig() {
	return originalVacConfig;
    }

    public void setOriginalVacConfig(VacationConfiguration originalVacConfig) {
	this.originalVacConfig = originalVacConfig;
    }

    public boolean getHasModifyPrivilege() {
	return hasModifyPrivilege;
    }

    public int getPageSize() {
	return pageSize;
    }
}
