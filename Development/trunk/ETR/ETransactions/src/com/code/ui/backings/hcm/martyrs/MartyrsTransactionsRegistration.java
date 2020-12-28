package com.code.ui.backings.hcm.martyrs;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.martyrs.MartyrHonor;
import com.code.dal.orm.hcm.martyrs.MartyrTransactionData;
import com.code.dal.orm.hcm.martyrs.MartyrdomReason;
import com.code.dal.orm.hcm.organization.Region;
import com.code.enums.MartyrdomTypesEnum;
import com.code.enums.MartyrsHonorsTypesEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.MartyrsService;
import com.code.services.security.SecurityService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "martyrsTransactionsRegistration")
@ViewScoped
public class MartyrsTransactionsRegistration extends BaseBacking {
    private int mode;
    private boolean modifyDeleteAdmin = false;
    private boolean martyrContentFlag = false;
    private boolean martyrsListFlag = false;
    private boolean martyrdomTypeFlag = false;
    private EmployeeData employee;
    private List<MartyrTransactionData> martyrTransactionDataList;
    private MartyrTransactionData martyrTransactionData;
    private List<MartyrdomReason> martyrdomReasons;
    private List<MartyrHonor> financialMartyrHonors;
    private List<MartyrHonor> moralMartyrHonors;
    private List<Region> regions;

    private boolean viewOnlyFlag = false;

    private int pageSize = 5;

    public MartyrsTransactionsRegistration() {
	try {
	    if (getRequest().getParameter("mode") != null) {
		mode = Integer.parseInt(getRequest().getParameter("mode"));

		switch (mode) {
		case 1:
		    setScreenTitle(getMessage("title_martyrsOfficiersTransactionsRegistration"));
		    if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.MARTYRS_OFFICIERS_TRANSACTION_REGISTRATION.getCode(), MenuActionsEnum.MARTYRS_TRANSACTIONS_DATA_MODIFY_DELETE.getCode()))
			modifyDeleteAdmin = true;
		    break;
		case 2:
		    setScreenTitle(getMessage("title_martyrsSoldiersTransactionsRegistration"));
		    if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.MARTYRS_SOLDIERS_TRANSACTION_REGISTRATION.getCode(), MenuActionsEnum.MARTYRS_TRANSACTIONS_DATA_MODIFY_DELETE.getCode()))
			modifyDeleteAdmin = true;
		    break;
		}

		martyrdomReasons = MartyrsService.getMartyrdomReasons();
		regions = CommonService.getAllRegions();

		resetForm();
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void getEmployeeMartyrdomData() {
	try {
	    if (employee.getEmpId() != null) {
		martyrsListFlag = true;
		martyrContentFlag = false;
		employee = EmployeesService.getEmployeeData(employee.getEmpId());
		martyrTransactionDataList = MartyrsService.getMartyrTransactionByEmployeeId(employee.getEmpId());

	    } else {
		throw new BusinessException("error_UIError");
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addMartyr() {
	reset();
	viewOnlyFlag = false;
	martyrContentFlag = true;
	martyrTransactionData.setEmployeeId(employee.getEmpId());
	for (MartyrTransactionData martyrTransaction : martyrTransactionDataList) {
	    if (martyrTransaction.getMartyrdomType() != MartyrdomTypesEnum.INJURED.getCode())
		martyrdomTypeFlag = true;
	    martyrTransactionData.setMartyrdomType(MartyrdomTypesEnum.INJURED.getCode());
	    break;
	}

    }

    public void selectMartyr(MartyrTransactionData martyrTransaction) {
	try {
	    viewOnlyFlag = false;
	    martyrContentFlag = true;
	    martyrTransactionData = martyrTransaction;
	    financialMartyrHonors = MartyrsService.getMartyrHonors(martyrTransactionData.getId(), MartyrsHonorsTypesEnum.FINANCIAL.getCode());
	    moralMartyrHonors = MartyrsService.getMartyrHonors(martyrTransactionData.getId(), MartyrsHonorsTypesEnum.MORAL.getCode());

	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void martyrdomTypeChangeListener() {
	martyrTransactionData.setFirstContactNumber(null);
	martyrTransactionData.setSecondContactNumber(null);

	if (martyrTransactionData.getMartyrdomType().equals(MartyrdomTypesEnum.INJURED.getCode()) && employee.getEmpId() != null) {
	    martyrTransactionData.setFirstContactNumber(employee.getOfficialMobileNumber());
	    martyrTransactionData.setSecondContactNumber(employee.getPrivateMobileNumber());
	}
    }

    public void addMartyrHonor(int honorType) {
	MartyrHonor martyrHonor = new MartyrHonor();
	martyrHonor.setHonorType(honorType);

	if (honorType == MartyrsHonorsTypesEnum.FINANCIAL.getCode())
	    financialMartyrHonors.add(martyrHonor);
	else
	    moralMartyrHonors.add(martyrHonor);
    }

    public void deleteMartyrHonor(MartyrHonor martyrHonor, int honorType) {
	try {
	    if (martyrHonor.getId() != null)
		MartyrsService.deleteMartyrHonor(martyrHonor);

	    if (honorType == MartyrsHonorsTypesEnum.FINANCIAL.getCode())
		financialMartyrHonors.remove(martyrHonor);
	    else
		moralMartyrHonors.remove(martyrHonor);

	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void saveMartyrTransactionData() throws BusinessException {
	try {
	    List<MartyrHonor> martyrsHonorList = new ArrayList<>();
	    martyrsHonorList.addAll(financialMartyrHonors);
	    martyrsHonorList.addAll(moralMartyrHonors);

	    martyrTransactionData.getMartyrTransaction().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
	    for (MartyrHonor martyrHonor : martyrsHonorList) {
		if (martyrHonor.getId() == null)
		    martyrHonor.setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
	    }

	    if (martyrTransactionData.getMartyrdomType().equals(MartyrdomTypesEnum.INJURED.getCode())) {
		martyrTransactionData.setDeathCompensationNumber(null);
		martyrTransactionData.setDeathCompensationDate(null);
	    } else {
		martyrTransactionData.setInjuryCompensation(null);
		martyrTransactionData.setInjuryCompensationNumber(null);
		martyrTransactionData.setInjuryCompensationDate(null);
	    }

	    if (martyrTransactionData.getId() == null)
		MartyrsService.addMartyrTransactionAndHonors(martyrTransactionData, martyrTransactionDataList, martyrsHonorList);
	    else
		MartyrsService.updateMartyrTransactionAndHonors(martyrTransactionData, martyrTransactionDataList, martyrsHonorList);

	    viewOnlyFlag = true;
	    martyrTransactionDataList = MartyrsService.getMartyrTransactionByEmployeeId(employee.getEmpId());

	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    martyrTransactionDataList = MartyrsService.getMartyrTransactionByEmployeeId(employee.getEmpId());
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void deleteMartyrTransactionData() {
	try {
	    martyrTransactionData.getMartyrTransaction().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
	    MartyrsService.deleteMartyrTransactionAndHonors(martyrTransactionData);
	    martyrContentFlag = false;
	    reset();
	    martyrTransactionDataList = MartyrsService.getMartyrTransactionByEmployeeId(employee.getEmpId());

	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void print() {
	try {
	    byte[] bytes = MartyrsService.getMartyrFileBytes(martyrTransactionData.getId(), martyrTransactionData.getEmployeeId());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetForm() {
	reset();
	employee = new EmployeeData();
	employee.setCategoryId(new Long(mode));
	martyrContentFlag = false;
	martyrsListFlag = false;
	viewOnlyFlag = false;
    }

    public void reset() {
	martyrTransactionData = new MartyrTransactionData();
	financialMartyrHonors = new ArrayList<MartyrHonor>();
	moralMartyrHonors = new ArrayList<MartyrHonor>();
	martyrTransactionData.setMartyrdomRegionId(getLoginEmpPhysicalRegionFlag(true));
	martyrdomTypeFlag = false;

    }

    public List<MartyrTransactionData> getMartyrTransactionDataList() {
	return martyrTransactionDataList;
    }

    public void setMartyrTransactionDataList(List<MartyrTransactionData> martyrTransactionDataList) {
	this.martyrTransactionDataList = martyrTransactionDataList;
    }

    public boolean isMartyrsListFlag() {
	return martyrsListFlag;
    }

    public void setMartyrsListFlag(boolean martyrsListFlag) {
	this.martyrsListFlag = martyrsListFlag;
    }

    public boolean isMartyrdomTypeFlag() {
	return martyrdomTypeFlag;
    }

    public void setMartyrdomTypeFlag(boolean martyrdomTypeFlag) {
	this.martyrdomTypeFlag = martyrdomTypeFlag;
    }

    public boolean editFieldsEnabled() {
	return !viewOnlyFlag && (martyrTransactionData.getId() == null || (martyrTransactionData.getId() != null && modifyDeleteAdmin));
    }

    public boolean viewFieldsEnabled() {
	return viewOnlyFlag || (martyrTransactionData.getId() != null && !modifyDeleteAdmin);
    }

    public boolean isMartyrContentFlag() {
	return martyrContentFlag;
    }

    public void setMartyrContentFlag(boolean martyrContentFlag) {
	this.martyrContentFlag = martyrContentFlag;
    }

    public EmployeeData getEmployee() {
	return employee;
    }

    public void setEmployee(EmployeeData employee) {
	this.employee = employee;
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public boolean isModifyDeleteAdmin() {
	return modifyDeleteAdmin;
    }

    public void setModifyDeleteAdmin(boolean modifyDeleteAdmin) {
	this.modifyDeleteAdmin = modifyDeleteAdmin;
    }

    public List<MartyrdomReason> getMartyrdomReasons() {
	return martyrdomReasons;
    }

    public void setMartyrdomReasons(List<MartyrdomReason> martyrdomReasons) {
	this.martyrdomReasons = martyrdomReasons;
    }

    public MartyrTransactionData getMartyrTransactionData() {
	return martyrTransactionData;
    }

    public void setMartyrTransactionData(MartyrTransactionData martyrTransactionData) {
	this.martyrTransactionData = martyrTransactionData;
    }

    public List<Region> getRegions() {
	return regions;
    }

    public void setRegions(List<Region> regions) {
	this.regions = regions;
    }

    public List<MartyrHonor> getFinancialMartyrHonors() {
	return financialMartyrHonors;
    }

    public void setFinancialMartyrHonors(List<MartyrHonor> financialMartyrHonors) {
	this.financialMartyrHonors = financialMartyrHonors;
    }

    public List<MartyrHonor> getMoralMartyrHonors() {
	return moralMartyrHonors;
    }

    public void setMoralMartyrHonors(List<MartyrHonor> moralMartyrHonors) {
	this.moralMartyrHonors = moralMartyrHonors;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public boolean isViewOnlyFlag() {
	return viewOnlyFlag;
    }

    public void setViewOnlyFlag(boolean viewOnlyFlag) {
	this.viewOnlyFlag = viewOnlyFlag;
    }

}
