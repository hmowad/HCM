package com.code.ui.backings.hcm.terminations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.terminations.TerminationTransactionData;
import com.code.dal.orm.security.EmployeeDecisionPrivilege;
import com.code.enums.CategoriesEnum;
import com.code.enums.CategoryModesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.WFProcessesGroupsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TerminationsService;
import com.code.services.security.SecurityService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "terminationsInquiry")
@ViewScoped
public class TerminationsInquiry extends BaseBacking {
    /**
     * 1 for Officers, 2 for Soldiers and 3 for Civil
     **/
    private int mode;
    private int adminUser;
    private List<EmployeeDecisionPrivilege> decisionsPrivileges;

    // For Search
    private Long empId;
    private String empName;
    private String decisionNumber;
    private Date fromDate;
    private Date toDate;
    private long categoryId;

    private List<TerminationTransactionData> terminationTransactionDataList;
    private int pageSize = 10;

    public TerminationsInquiry() {
	try {
	    if (getRequest().getParameter("mode") != null) {
		mode = Integer.parseInt(getRequest().getParameter("mode"));
		if (mode == CategoryModesEnum.SOLDIERS.getCode()) {
		    setScreenTitle(getMessage("title_soldiersTerminationsInquiry"));
		    categoryId = CategoriesEnum.SOLDIERS.getCode();
		} else if (mode == CategoryModesEnum.CIVILIANS.getCode()) {
		    setScreenTitle(getMessage("title_civiliansTerminationsInquiry"));
		    categoryId = FlagsEnum.ALL.getCode();
		} else {
		    setServerSideErrorMessages(getMessage("error_general"));
		}
	    } else {
		setServerSideErrorMessages(getMessage("error_general"));
	    }

	    decisionsPrivileges = SecurityService.getEmployeesDecisionsPrivileges(this.loginEmpData.getEmpId(), WFProcessesGroupsEnum.TERMINATIONS.getCode(), mode);
	    adminUser = decisionsPrivileges.size() == 0 ? FlagsEnum.OFF.getCode() : FlagsEnum.ON.getCode();
	    resetForm();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetForm() {
	decisionNumber = "";
	fromDate = null;
	toDate = null;
	empId = null;
	empName = null;

	terminationTransactionDataList = new ArrayList<TerminationTransactionData>();

    }

    public void searchTerminations() {
	try {
	    if (!SecurityService.isDecisionPrivilegeGranted(this.loginEmpData, (empId == null || empId.equals("")) ? null : empId, decisionsPrivileges))
		throw new BusinessException("error_notAuthorized");

	    Long[] searchCategoriesIds = categoryId == FlagsEnum.ALL.getCode() ? getCategoriesIdsArrayByMode(mode) : new Long[] { categoryId };
	    terminationTransactionDataList = TerminationsService.getTerminationTransactions(empId, decisionNumber, fromDate, toDate, searchCategoriesIds);
	} catch (BusinessException e) {
	    terminationTransactionDataList = new ArrayList<TerminationTransactionData>();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	} catch (Exception e) {
	    terminationTransactionDataList = new ArrayList<TerminationTransactionData>();
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public void printTerminationTransaction(TerminationTransactionData terminationTransaction) {
	try {
	    byte[] bytes = TerminationsService.getTerminationDecisionBytes(terminationTransaction.getId(), mode, terminationTransaction.getTransactionTypeCode());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<TerminationTransactionData> getTerminationTransactionDataList() {
	return terminationTransactionDataList;
    }

    public void setTerminationTransactionDataList(List<TerminationTransactionData> terminationTransactionDataList) {
	this.terminationTransactionDataList = terminationTransactionDataList;
    }

    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    public String getEmpName() {
	return empName;
    }

    public void setEmpName(String empName) {
	this.empName = empName;
    }

    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    public Date getFromDate() {
	return fromDate;
    }

    public void setFromDate(Date fromDate) {
	this.fromDate = fromDate;
    }

    public Date getToDate() {
	return toDate;
    }

    public void setToDate(Date toDate) {
	this.toDate = toDate;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public int getAdminUser() {
	return adminUser;
    }

    public void setAdminUser(int adminUser) {
	this.adminUser = adminUser;
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public long getEmployeesSearchRegionId() {
	return getLoginEmpPhysicalRegionFlag(adminUser == FlagsEnum.OFF.getCode() ? false : true);
    }
}
