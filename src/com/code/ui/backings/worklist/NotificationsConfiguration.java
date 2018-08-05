package com.code.ui.backings.worklist;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.workflow.WFNotificationsConfigData;
import com.code.dal.orm.workflow.WFNotificationsConfigDetailData;
import com.code.dal.orm.workflow.WFProcess;
import com.code.dal.orm.workflow.WFProcessGroup;
import com.code.enums.FlagsEnum;
import com.code.enums.WFProcessesGroupsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.util.CommonService;
import com.code.services.workflow.BaseWorkFlow;
import com.code.services.workflow.NotificationsConfigService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "notificationsConfig")
@ViewScoped
public class NotificationsConfiguration extends BaseBacking {

    private String description;
    private Long processGroupId;
    private Long categoryId;

    private Long notificationType; // 1 Decision region, 2 employee region or unit who are in the decision

    private List<WFProcessGroup> processesGroups;
    private List<WFProcess> processes;
    private List<Region> regions;
    private List<Category> categories;

    private List<WFNotificationsConfigData> notifications;
    private List<WFNotificationsConfigDetailData> notificationDetails;

    private WFNotificationsConfigData selectedNotification;

    private int pageSize = 5;

    public NotificationsConfiguration() {
	super();
	try {
	    resetSearch();

	    processesGroups = BaseWorkFlow.getWFProcessesGroups();
	    regions = CommonService.getAllRegions();
	    categories = CommonService.getAllCategories();

	    notificationType = 1L;

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetSearch() {
	processGroupId = (long) FlagsEnum.ALL.getCode();
	categoryId = (long) FlagsEnum.ALL.getCode();
	description = null;
	notifications = new ArrayList<WFNotificationsConfigData>();

	resetDetails();
    }

    public void resetDetails() {
	selectedNotification = null;
	notificationType = 1L;
	notificationDetails = new ArrayList<WFNotificationsConfigDetailData>();
    }

    public void resetNotificationConfigData() {
	selectedNotification.setDecisionRegionId(null);
	selectedNotification.setDecisionRegionDescription(null);
	selectedNotification.setBeneficiaryRegionId(null);
	selectedNotification.setBeneficiaryRegionDescription(null);
	selectedNotification.setBeneficiaryUnitId(null);
	selectedNotification.setBeneficiaryUnitFullName(null);
    }

    public void searchNotifications() {
	try {
	    notifications = NotificationsConfigService.getNotificationsConfigData(processGroupId, categoryId, description);
	    resetDetails();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void processGroupChangeListener() {
	try {
	    if (selectedNotification.getWfProcessGroupId() != null)
		processes = BaseWorkFlow.getWFGroupProcesses(selectedNotification.getWfProcessGroupId());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void beneficiaryRegionChangeListener() {
	selectedNotification.setBeneficiaryUnitId(null);
	selectedNotification.setBeneficiaryUnitFullName(null);
    }

    /****************************** Notifications Config Operations ******************************/

    public void selectNotification(WFNotificationsConfigData notification) {
	try {
	    notificationType = notification.getDecisionRegionId() != null ? 1L : 2L;
	    selectedNotification = notification;
	    processGroupChangeListener();
	    notificationDetails = NotificationsConfigService.searchNotificationsConfigDetailData(notification.getId());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addNewNotification() {
	WFNotificationsConfigData newNotification = new WFNotificationsConfigData();
	newNotification.setWfProcessGroupId((long) WFProcessesGroupsEnum.VACATIONS.getCode());

	selectedNotification = newNotification;
	processGroupChangeListener();
	notificationDetails = new ArrayList<WFNotificationsConfigDetailData>();
    }

    public void saveNotifiation() {
	try {
	    if (selectedNotification.getId() != null) {
		NotificationsConfigService.updateNotificationConfig(selectedNotification, this.loginEmpData.getEmpId());
		List<WFNotificationsConfigDetailData> newNotificationDetails = new ArrayList<WFNotificationsConfigDetailData>();
		for (WFNotificationsConfigDetailData notificationDetail : notificationDetails) {
		    if (notificationDetail.getId() == null)
			newNotificationDetails.add(notificationDetail);
		}
		if (!newNotificationDetails.isEmpty())
		    NotificationsConfigService.addNotificationConfigDetails(newNotificationDetails, this.loginEmpData.getEmpId());
	    } else {
		NotificationsConfigService.addNotificationConfig(selectedNotification, notificationDetails, this.loginEmpData.getEmpId());
	    }

	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void deleteNotification(WFNotificationsConfigData notification) {
	try {
	    if (notification.getId() != null)
		NotificationsConfigService.deleteNotificationConfig(notification, this.loginEmpData.getEmpId());

	    notifications.remove(notification);
	    resetDetails();
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    /************************** Notifications Config Details Operations **************************/

    public void addNotificationsConfigDetail() {
	WFNotificationsConfigDetailData wfNotificationsConfigDetailData = new WFNotificationsConfigDetailData();
	wfNotificationsConfigDetailData.setWfNotificationsConfigId(selectedNotification.getId());
	notificationDetails.add(wfNotificationsConfigDetailData);
    }

    public void deleteNotificationsConfigDetail(WFNotificationsConfigDetailData wfNotificationsConfigDetailData) {
	try {
	    if (wfNotificationsConfigDetailData.getId() != null) {
		NotificationsConfigService.deleteNotificationConfigDetail(wfNotificationsConfigDetailData, notificationDetails, this.loginEmpData.getEmpId());
		this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	    }
	    notificationDetails.remove(wfNotificationsConfigDetailData);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    /************************************ Setters and Getters ************************************/

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public Long getProcessGroupId() {
	return processGroupId;
    }

    public void setProcessGroupId(Long processGroupId) {
	this.processGroupId = processGroupId;
    }

    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
    }

    public Long getNotificationType() {
	return notificationType;
    }

    public void setNotificationType(Long notificationType) {
	this.notificationType = notificationType;
    }

    public List<WFProcessGroup> getProcessesGroups() {
	return processesGroups;
    }

    public List<WFProcess> getProcesses() {
	return processes;
    }

    public List<Region> getRegions() {
	return regions;
    }

    public List<Category> getCategories() {
	return categories;
    }

    public List<WFNotificationsConfigData> getNotifications() {
	return notifications;
    }

    public void setNotifications(List<WFNotificationsConfigData> notifications) {
	this.notifications = notifications;
    }

    public List<WFNotificationsConfigDetailData> getNotificationDetails() {
	return notificationDetails;
    }

    public void setNotificationDetails(List<WFNotificationsConfigDetailData> notificationDetails) {
	this.notificationDetails = notificationDetails;
    }

    public WFNotificationsConfigData getSelectedNotification() {
	return selectedNotification;
    }

    public int getPageSize() {
	return pageSize;
    }

}
