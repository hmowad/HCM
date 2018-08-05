package com.code.ui.backings.generalnotifications;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.UnitTypesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.config.ETRConfigurationService;
import com.code.services.hcm.UnitsService;
import com.code.services.security.SecurityService;
import com.code.services.util.CommonService;
import com.code.services.workflow.generalnotifications.GeneralNotificationsWorkFlow;
import com.code.ui.backings.base.WFBaseBacking;

@ManagedBean(name = "notificationMessages")
@ViewScoped
public class NotificationMessages extends WFBaseBacking {

    private List<Region> regionsList;
    private long selectedRegionId;

    private long selectedUnitId;
    private String selectedUnitFullName;
    private String selectedUnitHKey;

    private List<Category> categorieslist;
    private Long selectedCategoryId;

    private String message;

    private boolean manager = false;
    private boolean admin = false;

    public NotificationMessages() {
	super.init();

	try {
	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		admin = SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.WF_NOTIFICATION_MESSAGES.getCode(), MenuActionsEnum.WF_SEND_NOTIFICATION_MESSAGES.getCode());
		if (this.loginEmpData.getIsManager() == FlagsEnum.ON.getCode())
		    manager = true;

		if (manager || admin) {
		    selectedUnitId = this.loginEmpData.getPhysicalUnitId();
		    selectedUnitFullName = this.loginEmpData.getPhysicalUnitFullName();
		    selectedUnitHKey = this.loginEmpData.getPhysicalUnitHKey();

		    categorieslist = CommonService.getAllCategories();
		    regionsList = CommonService.getAllRegions();
		    selectedRegionId = getLoginEmpPhysicalRegionFlag(admin);
		}
	    } else {
		message = this.currentTask.getFlexField1();
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public void resetUnits(AjaxBehaviorEvent event) {
	try {
	    UnitData unit = null;
	    if (selectedRegionId == FlagsEnum.ALL.getCode() || selectedRegionId == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) {
		unit = UnitsService.getUnitsByType(UnitTypesEnum.PRESIDENCY.getCode()).get(0);
	    } else {
		unit = UnitsService.getUnitsByTypeAndRegion(UnitTypesEnum.REGION_COMMANDER.getCode(), selectedRegionId).get(0);
	    }
	    selectedUnitId = unit.getId();
	    selectedUnitFullName = unit.getFullName();
	    selectedUnitHKey = unit.gethKey();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public String initProcess() {
	try {
	    GeneralNotificationsWorkFlow.sendNotificationMessages(admin ? ETRConfigurationService.getETransactionsRequesterId() : this.requester.getEmpId(), this.requester.getEmpId(), selectedRegionId, selectedUnitHKey, selectedCategoryId, message, this.attachments, this.taskUrl);
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    public String closeProcess() {
	try {
	    GeneralNotificationsWorkFlow.closeWFInstanceByNotification(this.instance, this.currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    public List<Region> getRegionsList() {
	return regionsList;
    }

    public void setRegionsList(List<Region> regionsList) {
	this.regionsList = regionsList;
    }

    public long getSelectedRegionId() {
	return selectedRegionId;
    }

    public void setSelectedRegionId(long selectedRegionId) {
	this.selectedRegionId = selectedRegionId;
    }

    public long getSelectedUnitId() {
	return selectedUnitId;
    }

    public void setSelectedUnitId(long selectedUnitId) {
	this.selectedUnitId = selectedUnitId;
    }

    public String getSelectedUnitFullName() {
	return selectedUnitFullName;
    }

    public void setSelectedUnitFullName(String selectedUnitFullName) {
	this.selectedUnitFullName = selectedUnitFullName;
    }

    public String getSelectedUnitHKey() {
	return selectedUnitHKey;
    }

    public void setSelectedUnitHKey(String selectedUnitHKey) {
	this.selectedUnitHKey = selectedUnitHKey;
    }

    public List<Category> getCategorieslist() {
	return categorieslist;
    }

    public void setCategorieslist(List<Category> categorieslist) {
	this.categorieslist = categorieslist;
    }

    public Long getSelectedCategoryId() {
	return selectedCategoryId;
    }

    public void setSelectedCategoryId(Long selectedCategoryId) {
	this.selectedCategoryId = selectedCategoryId;
    }

    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    public boolean isManager() {
	return manager;
    }

    public void setManager(boolean manager) {
	this.manager = manager;
    }

    public boolean isAdmin() {
	return admin;
    }

    public void setAdmin(boolean admin) {
	this.admin = admin;
    }

}
