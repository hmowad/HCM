package com.code.ui.backings.hcm.raises;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.hcm.payroll.Degree;
import com.code.dal.orm.hcm.raises.Raise;
import com.code.dal.orm.hcm.raises.RaiseEmployeeData;
import com.code.dal.orm.hcm.raises.RaiseTransactionData;
import com.code.enums.EmployeeStatusEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.RaiseStatusEnum;
import com.code.enums.RaiseTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.PayrollsService;
import com.code.services.hcm.RaisesService;
import com.code.services.security.SecurityService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "additionalRaisesRegistration")
@ViewScoped
public class AdditionalRaisesRegistration extends BaseBacking implements Serializable {
    private Long raiseId;
    private int pageSize = 10;
    private Raise raise;
    private List<Category> categories;
    private List<Degree> degrees;
    private List<RaiseEmployeeData> deservedEmployeesList;
    private Long selectedEmpId;
    private Long physicalRegionId;
    private boolean addAdminFlag;
    private boolean approveAdminFlag;
    private boolean modifyAdminFlag;
    private String categoryDesc;
    private RaiseTransactionData raiseTransactionData;
    private List<RaiseEmployeeData> addedEmployeesList;
    private List<RaiseEmployeeData> deletedEmployeesList;
    private String statusIds;

    public AdditionalRaisesRegistration() {
	try {
	    raiseTransactionData = new RaiseTransactionData();
	    categories = CommonService.getAllCategories();
	    degrees = PayrollsService.getAllDegrees();
	    physicalRegionId = loginEmpData.getPhysicalRegionId();
	    setScreenTitle(getMessage("title_additionalRaisesRegistration"));
	    addedEmployeesList = new ArrayList<RaiseEmployeeData>();
	    deletedEmployeesList = new ArrayList<RaiseEmployeeData>();
	    StringBuilder statusBuilder = new StringBuilder();
	    statusBuilder.append(EmployeeStatusEnum.ON_DUTY.getCode());
	    statusBuilder.append(",");
	    statusBuilder.append(EmployeeStatusEnum.PERSONS_SUBJOINED.getCode());
	    statusBuilder.append(",");
	    statusBuilder.append(EmployeeStatusEnum.ASSIGNED.getCode());
	    statusBuilder.append(",");
	    statusBuilder.append(EmployeeStatusEnum.MANDATED.getCode());
	    statusBuilder.append(",");
	    statusBuilder.append(EmployeeStatusEnum.SECONDMENTED.getCode());
	    statusBuilder.append(",");
	    statusBuilder.append(EmployeeStatusEnum.ASSIGNED_EXTERNALLY.getCode());
	    statusBuilder.append(",");
	    statusBuilder.append(EmployeeStatusEnum.PERSONS_SUBJOINED_EXTERNALLY.getCode());
	    statusBuilder.append(",");
	    statusBuilder.append(EmployeeStatusEnum.SUBJOINED_EXTERNALLY.getCode());
	    statusIds = statusBuilder.toString();
	    // 7atet value 34an a test el update bs
	    raiseId = (long) 26;
	    // raiseId = Long.parseLong(getRequest().getParameter("raiseId"));// get raiseId value from get request paramter mn page 5aled
	    if (raiseId == null) {
		addAdminFlag = true;
		raise = new Raise();
		deservedEmployeesList = new ArrayList<RaiseEmployeeData>();
		if (!categories.isEmpty())
		    raise.setCategoryId(categories.get(0).getId());// 34an a7ot default value ll select one menu lw ma e5tar4y 7aga
		// 3amla comment 34an a test el save bs
		if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.RAISES_ADDITIONAL_RAISES_REGISTERATION.getCode(), MenuActionsEnum.RAISES_APPROVE_ADDITIONAL_RAISE.getCode()))
		    approveAdminFlag = true;
	    } else {
		raise = RaisesService.getRaiseById(raiseId);
		deservedEmployeesList = RaisesService.getRaiseEmployeeByRaiseId(raiseId);
		categoryDesc = CommonService.getCategoryById(raise.getCategoryId()).getDescription();
		if (raise.getStatus() == RaiseStatusEnum.INITIAL.getCode()) {
		    modifyAdminFlag = true;
		    // 3amla comment 34an a test el update bs
		    // if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.RAISES_ADDITIONAL_RAISES_REGISTERATION.getCode(), MenuActionsEnum.RAISES_APPROVE_ADDITIONAL_RAISE.getCode()))
		    // approveAdminFlag = true;

		}
	    }
	} catch (

	BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addNewDeservedEmployee() {
	try {
	    RaiseEmployeeData newDeservedEmployee = RaisesService.constructDeservedEmployee(selectedEmpId);
	    deservedEmployeesList.add(newDeservedEmployee);
	    if (modifyAdminFlag) {
		if (newDeservedEmployee.getId() == null)
		    addedEmployeesList.add(newDeservedEmployee);
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void deleteDeservedEmployee(RaiseEmployeeData employee) {
	deservedEmployeesList.remove(employee);
	if (modifyAdminFlag) {
	    if (employee.getId() == null)
		addedEmployeesList.remove(employee);
	    else
		deletedEmployeesList.add(employee);
	}
    }

    public void deleteAllDeservedEmployee() {
	try {
	    if (modifyAdminFlag) {
		if (!deservedEmployeesList.isEmpty()) {
		    for (int i = deservedEmployeesList.size() - 1; i >= 0; i--) {
			deleteDeservedEmployee(deservedEmployeesList.get(i));
		    }
		    RaisesService.deleteRaiseEmployees(deletedEmployeesList);
		    deletedEmployeesList.clear();
		}
	    }
	} catch (

	BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void save() {
	try {
	    if (addAdminFlag) {
		raise.setType(RaiseTypesEnum.ADDITIONAL.getCode());
		RaisesService.addAdditionalRaise(raise, deservedEmployeesList);
	    } else if (modifyAdminFlag) {
		RaisesService.updateRaiseAndEmployees(raise, addedEmployeesList, deletedEmployeesList);
	    }
	    if (!approveAdminFlag)
		super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void saveAndApprove() {
	try {
	    save();
	    RaisesService.approveAdditionalRaise(raise, loginEmpData.getEmpId());
	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}

    }

    public Long getRaiseId() {
	return raiseId;
    }

    public void setRaiseId(Long raiseId) {
	this.raiseId = raiseId;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public Raise getRaise() {
	return raise;
    }

    public void setRaise(Raise raise) {
	this.raise = raise;
    }

    public List<Category> getCategories() {
	return categories;
    }

    public void setCategories(List<Category> categories) {
	this.categories = categories;
    }

    public List<RaiseEmployeeData> getDeservedEmployeesList() {
	return deservedEmployeesList;
    }

    public void setDeservedEmployeesList(List<RaiseEmployeeData> deservedEmployeesList) {
	this.deservedEmployeesList = deservedEmployeesList;
    }

    public Long getSelectedEmpId() {
	return selectedEmpId;
    }

    public void setSelectedEmpId(Long selectedEmpId) {
	this.selectedEmpId = selectedEmpId;
    }

    public List<Degree> getDegrees() {
	return degrees;
    }

    public void setDegrees(List<Degree> degrees) {
	this.degrees = degrees;
    }

    public Long getPhysicalRegionId() {
	return physicalRegionId;
    }

    public void setPhysicalRegionId(Long physicalRegionId) {
	this.physicalRegionId = physicalRegionId;
    }

    public boolean isAddAdminFlag() {
	return addAdminFlag;
    }

    public void setAddAdminFlag(boolean addAdminFlag) {
	this.addAdminFlag = addAdminFlag;
    }

    public boolean isApproveAdminFlag() {
	return approveAdminFlag;
    }

    public void setApproveAdminFlag(boolean approveAdminFlag) {
	this.approveAdminFlag = approveAdminFlag;
    }

    public boolean isModifyAdminFlag() {
	return modifyAdminFlag;
    }

    public void setModifyAdminFlag(boolean modifyAdminFlag) {
	this.modifyAdminFlag = modifyAdminFlag;
    }

    public String getCategoryDesc() {
	return categoryDesc;
    }

    public void setCategoryDesc(String categoryDesc) {
	this.categoryDesc = categoryDesc;
    }

    public RaiseTransactionData getRaiseTransactionData() {
	return raiseTransactionData;
    }

    public void setRaiseTransactionData(RaiseTransactionData raiseTransactionData) {
	this.raiseTransactionData = raiseTransactionData;
    }

    public List<RaiseEmployeeData> getAddedEmployeesList() {
	return addedEmployeesList;
    }

    public void setAddedEmployeesList(List<RaiseEmployeeData> addedEmployeesList) {
	this.addedEmployeesList = addedEmployeesList;
    }

    public List<RaiseEmployeeData> getDeletedEmployeesList() {
	return deletedEmployeesList;
    }

    public void setDeletedEmployeesList(List<RaiseEmployeeData> deletedEmployeesList) {
	this.deletedEmployeesList = deletedEmployeesList;
    }

    public String getStatusIds() {
	return statusIds;
    }

    public void setStatusIds(String statusIds) {
	this.statusIds = statusIds;
    }

}