package com.code.ui.backings.hcm.movements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.hcm.movements.MovementTransactionData;
import com.code.dal.orm.hcm.movements.MovementType;
import com.code.dal.orm.security.EmployeeDecisionPrivilege;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.MovementTypesEnum;
import com.code.enums.WFProcessesGroupsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.MovementsService;
import com.code.services.security.SecurityService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "movementsInquiry")
@ViewScoped
public class MovementsInquiry extends BaseBacking implements Serializable {
    private int mode;
    private boolean adminUser;
    private List<EmployeeDecisionPrivilege> decisionsPrivileges;

    List<MovementTransactionData> movementsList;
    List<MovementType> movementTypesList;
    List<Category> personsCategories;

    // For search
    private long empId;
    private String empName;
    private long categoryId;
    private long movementTypeId;
    private String decisionNumber;
    private Date fromDate;
    private Date toDate;
    private long regionId;

    private int pageSize = 10;

    public MovementsInquiry() {
	try {
	    if (getRequest().getParameter("mode") == null) {
		throw new BusinessException("error_general");
	    }

	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    switch (mode) {
	    case 1:
		setScreenTitle(getMessage("title_officersMovementInquiry"));
		categoryId = CategoriesEnum.OFFICERS.getCode();
		break;
	    case 2:
		setScreenTitle(getMessage("title_soldiersMovementInquiry"));
		categoryId = CategoriesEnum.SOLDIERS.getCode();
		break;
	    case 3:
		setScreenTitle(getMessage("title_personsMovementInquiry"));
		categoryId = FlagsEnum.ALL.getCode();
		personsCategories = CommonService.getPersonsCategories();
		break;
	    default:
		setServerSideErrorMessages(getMessage("error_general"));
	    }

	    decisionsPrivileges = SecurityService.getEmployeesDecisionsPrivileges(this.loginEmpData.getEmpId(), WFProcessesGroupsEnum.MOVEMENTS.getCode(), mode);
	    adminUser = decisionsPrivileges.size() == 0 ? false : true;

	    regionId = getLoginEmpPhysicalRegionFlag(adminUser);

	    movementTypesList = MovementsService.getAllMovementTypes();
	    calculateMovementTypesList();
	    resetForm();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetForm() {
	if (mode == 3)
	    categoryId = FlagsEnum.ALL.getCode();

	empId = FlagsEnum.ALL.getCode();
	movementTypeId = FlagsEnum.ALL.getCode();
	empName = null;
	decisionNumber = null;
	fromDate = null;
	toDate = null;
	movementsList = null;
    }

    public void searchMovement() {
	try {
	    Long[] searchCategoriesIds = categoryId == FlagsEnum.ALL.getCode() ? getCategoriesIdsArrayByMode(mode) : new Long[] { categoryId };

	    if (!SecurityService.isDecisionPrivilegeGranted(this.loginEmpData, empId == FlagsEnum.ALL.getCode() ? null : empId, decisionsPrivileges))
		throw new BusinessException("error_notAuthorized");

	    movementsList = MovementsService.getMovementTransactionsForInquiry(empId, searchCategoriesIds, decisionNumber, fromDate, toDate, movementTypeId, regionId);

	} catch (BusinessException e) {
	    movementsList = new ArrayList<MovementTransactionData>();
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void print(MovementTransactionData movement) {
	try {
	    byte[] bytes = MovementsService.getMovementDecisionBytes(movement);
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    private void calculateMovementTypesList() {
	if (mode == 1) { // Officers
	    for (int i = 0; i < movementTypesList.size(); i++) {
		if (movementTypesList.get(i).getId().longValue() == MovementTypesEnum.MANDATE.getCode() || movementTypesList.get(i).getId().longValue() == MovementTypesEnum.SECONDMENT.getCode() || movementTypesList.get(i).getId().longValue() == MovementTypesEnum.INTERNAL_ASSIGNMENT.getCode()) {
		    movementTypesList.remove(i);
		    i--;
		}
	    }
	} else if (mode == 2) { // Soldiers
	    for (int i = 0; i < movementTypesList.size(); i++) {
		if (movementTypesList.get(i).getId().longValue() == MovementTypesEnum.ASSIGNMENT.getCode() || movementTypesList.get(i).getId().longValue() == MovementTypesEnum.INTERNAL_ASSIGNMENT.getCode()) {
		    movementTypesList.remove(i);
		    i--;
		}
	    }
	} else { // Persons
	    for (int i = 0; i < movementTypesList.size(); i++) {
		if (movementTypesList.get(i).getId().longValue() == MovementTypesEnum.ASSIGNMENT.getCode() || movementTypesList.get(i).getId().longValue() == MovementTypesEnum.SECONDMENT.getCode() || movementTypesList.get(i).getId().longValue() == MovementTypesEnum.MANDATE.getCode() || movementTypesList.get(i).getId().longValue() == MovementTypesEnum.INTERNAL_ASSIGNMENT.getCode()) {
		    movementTypesList.remove(i);
		    i--;
		} else if (movementTypesList.get(i).getId().longValue() == MovementTypesEnum.SUBJOIN.getCode()) {
		    // Adjust label only
		    movementTypesList.get(i).setDescription(getMessage("label_personsSubjoin"));
		}
	    }
	}
    }

    public long getEmpId() {
	return empId;
    }

    public void setEmpId(long empId) {
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

    public long getMovementTypeId() {
	return movementTypeId;
    }

    public void setMovementTypeId(long movementTypeId) {
	this.movementTypeId = movementTypeId;
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

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public List<MovementTransactionData> getMovementsList() {
	return movementsList;
    }

    public void setMovementsList(List<MovementTransactionData> movementList) {
	this.movementsList = movementList;
    }

    public long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(long categoryId) {
	this.categoryId = categoryId;
    }

    public List<MovementType> getMovementTypesList() {
	return movementTypesList;
    }

    public void setMovementTypesList(List<MovementType> movementTypesList) {
	this.movementTypesList = movementTypesList;
    }

    public List<Category> getPersonsCategories() {
	return personsCategories;
    }

    public void setPersonsCategories(List<Category> personsCategories) {
	this.personsCategories = personsCategories;
    }

    public boolean isAdminUser() {
	return adminUser;
    }

    public long getRegionId() {
	return regionId;
    }

    public void setRegionId(long regionId) {
	this.regionId = regionId;
    }
}