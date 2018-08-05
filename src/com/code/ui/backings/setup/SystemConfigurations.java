package com.code.ui.backings.setup;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.config.ETRConfig;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.config.ETRConfigurationService;
import com.code.services.security.SecurityService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "systemConfigurations")
@ViewScoped
public class SystemConfigurations extends BaseBacking {

    private List<ETRConfig> configurationList;
    private boolean adminFlag = false;
    private int transactionClass = FlagsEnum.ALL.getCode();
    private final int pageSize = 25;
    private int pageNum = 1;

    public SystemConfigurations() {
	try {
	    searchETRConfig();
	    adminFlag = SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.ETR_SYSTEM_CONFIGURATIONS.getCode(), MenuActionsEnum.ETR_SYSTEM_CONFIGURATIONS_ADMIN.getCode());
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    public void searchETRConfig() {
	configurationList = ETRConfigurationService.getETRConfigsByTransactionClass(transactionClass);
	pageNum = 1;
    }

    public void updateETRConfig(ETRConfig etrConfig) {
	try {
	    if (etrConfig.getValue() == null || etrConfig.getValue().trim().isEmpty())
		throw new BusinessException("error_configurationValueMandatory");

	    ETRConfigurationService.updateETRConfig(etrConfig, this.loginEmpData.getEmpId());
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<ETRConfig> getConfigurationList() {
	return configurationList;
    }

    public void setConfigurationList(List<ETRConfig> configurationList) {
	this.configurationList = configurationList;
    }

    public boolean getAdminFlag() {
	return adminFlag;
    }

    public int getTransactionClass() {
	return transactionClass;
    }

    public void setTransactionClass(int transactionClass) {
	this.transactionClass = transactionClass;
    }

    public int getPageSize() {
	return pageSize;
    }

    public int getPageNum() {
	return pageNum;
    }

    public void setPageNum(int pageNum) {
	this.pageNum = pageNum;
    }

}
