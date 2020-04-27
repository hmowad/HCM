package com.code.ui.backings.audit;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.audit.AuditLog;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.enums.AuditOperationsEnum;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.audit.AuditService;
import com.code.services.hcm.EmployeesService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "auditInquiry")
@ViewScoped
public class AuditInquiry extends BaseBacking {

    private List<Object> contentEntities;
    private List<String> operations;

    // For Search
    private String contentEntity;
    private Long contentId;
    private String operation;
    private Long systemUserId;
    private String systemUserName;
    private Date operationDateFrom;
    private Date operationDateTo;
    private String content;

    private List<AuditLog> auditLogs;
    private AuditLog selectedAuditLog;
    private String selectedAuditLogSystemUserName;
    private HashMap<String, String> contentAttributes;

    private int pageSize = 10;

    public AuditInquiry() {
	try {
	    contentEntities = (ArrayList<Object>) AuditService.getContentEntites();

	    operations = new ArrayList<String>();
	    operations.add(AuditOperationsEnum.INSERT.toString());
	    operations.add(AuditOperationsEnum.UPDATE.toString());
	    operations.add(AuditOperationsEnum.DELETE.toString());

	    resetForm();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void resetForm() {
	contentEntity = FlagsEnum.ALL.getCode() + "";
	contentId = null;
	operation = FlagsEnum.ALL.getCode() + "";
	systemUserId = null;
	systemUserName = null;
	operationDateFrom = null;
	operationDateTo = null;
	content = null;

	auditLogs = new ArrayList<AuditLog>();
	selectedAuditLog = null;
	selectedAuditLogSystemUserName = "";
	contentAttributes = new HashMap<>();
    }

    public void search() {
	try {
	    selectedAuditLog = null;
	    selectedAuditLogSystemUserName = "";
	    auditLogs = AuditService.getAuditLogs(contentEntity, contentId, operation, systemUserId, operationDateFrom, operationDateTo, content);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void selectAuditLog(AuditLog auditLog) {
	try {
	    selectedAuditLog = auditLog;
	    EmployeeData selectedAuditLogSystemUser = EmployeesService.getEmployeeData(auditLog.getSystemUser());
	    selectedAuditLogSystemUserName = selectedAuditLogSystemUser == null ? "" : selectedAuditLogSystemUser.getName();
	    contentAttributes = new HashMap<>();
	    String[] contentFields = auditLog.getContent().split("\\*\\|#");
	    for (String contentField : contentFields) {
		String[] entry = contentField.split(":", 2);
		contentAttributes.put(entry[0], (entry.length > 1 && !entry[1].equals("null")) ? entry[1] : null);
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public List<Object> getContentEntities() {
	return contentEntities;
    }

    public void setContentEntities(List<Object> contentEntities) {
	this.contentEntities = contentEntities;
    }

    public List<String> getOperations() {
	return operations;
    }

    public void setOperations(List<String> operations) {
	this.operations = operations;
    }

    public String getContentEntity() {
	return contentEntity;
    }

    public void setContentEntity(String contentEntity) {
	this.contentEntity = contentEntity;
    }

    public Long getContentId() {
	return contentId;
    }

    public void setContentId(Long contentId) {
	this.contentId = contentId;
    }

    public String getOperation() {
	return operation;
    }

    public void setOperation(String operation) {
	this.operation = operation;
    }

    public Long getSystemUserId() {
	return systemUserId;
    }

    public void setSystemUserId(Long systemUserId) {
	this.systemUserId = systemUserId;
    }

    public String getSystemUserName() {
	return systemUserName;
    }

    public void setSystemUserName(String systemUserName) {
	this.systemUserName = systemUserName;
    }

    public Date getOperationDateFrom() {
	return operationDateFrom;
    }

    public void setOperationDateFrom(Date operationDateFrom) {
	this.operationDateFrom = operationDateFrom;
    }

    public Date getOperationDateTo() {
	return operationDateTo;
    }

    public void setOperationDateTo(Date operationDateTo) {
	this.operationDateTo = operationDateTo;
    }

    public String getContent() {
	return content;
    }

    public void setContent(String content) {
	this.content = content;
    }

    public List<AuditLog> getAuditLogs() {
	return auditLogs;
    }

    public void setAuditLogs(List<AuditLog> auditLogs) {
	this.auditLogs = auditLogs;
    }

    public AuditLog getSelectedAuditLog() {
	return selectedAuditLog;
    }

    public void setSelectedAuditLog(AuditLog selectedAuditLog) {
	this.selectedAuditLog = selectedAuditLog;
    }

    public String getSelectedAuditLogSystemUserName() {
	return selectedAuditLogSystemUserName;
    }

    public void setSelectedAuditLogSystemUserName(String selectedAuditLogSystemUserName) {
	this.selectedAuditLogSystemUserName = selectedAuditLogSystemUserName;
    }

    public HashMap<String, String> getContentAttributes() {
	return contentAttributes;
    }

    public void setContentAttributes(HashMap<String, String> contentAttributes) {
	this.contentAttributes = contentAttributes;
    }

    public int getPageSize() {
	return pageSize;
    }

}
