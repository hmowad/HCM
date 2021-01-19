package com.code.ui.backings.minisearch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.attachments.EmployeeFileAttachmentDetailData;
import com.code.exceptions.BusinessException;
import com.code.services.config.ETRConfigurationService;
import com.code.services.util.EmployeeFileAttachmentService;
import com.code.services.util.EncryptionUtil;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "employeeFileAttachmentsMiniSearch")
@ViewScoped
public class EmployeeFileAttachmentsMiniSearch extends BaseBacking {

    private long employeeId;
    private int transactionClass;
    private boolean isModifyAdmin;

    private String decisionNumber = "";
    private String description = "";
    private Date fromDate;
    private Date toDate;

    private String downloadURL;
    private String fileDownloadParams;

    private List<EmployeeFileAttachmentDetailData> empFileAttachmentsDetailList;

    private int rowsCount = 10;

    public EmployeeFileAttachmentsMiniSearch() {
	if (this.getRequest().getParameter("empId") != null)
	    employeeId = Long.parseLong(this.getRequest().getParameter("empId"));
	if (this.getRequest().getParameter("transClass") != null)
	    transactionClass = Integer.parseInt(this.getRequest().getParameter("transClass"));
	if (this.getRequest().getParameter("isModifyAdmin") != null)
	    isModifyAdmin = Boolean.parseBoolean(this.getRequest().getParameter("isModifyAdmin"));

	downloadURL = ETRConfigurationService.getAttachmentDownloadURL();

	searchEmpFileAttachments();
    }

    public void searchEmpFileAttachments() {
	try {
	    empFileAttachmentsDetailList = new ArrayList<EmployeeFileAttachmentDetailData>();
	    empFileAttachmentsDetailList = EmployeeFileAttachmentService.searchEmpFileAttachmentsDetails(employeeId, transactionClass, decisionNumber, description, fromDate, toDate);
	} catch (BusinessException e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void getDownloadParam(EmployeeFileAttachmentDetailData empFileAttachmentDetailData) {
	try {
	    fileDownloadParams = EncryptionUtil.encryptSymmetrically("contentId=" + empFileAttachmentDetailData.getContentId());
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void reset() {
	decisionNumber = "";
	description = "";
	fromDate = null;
	toDate = null;
	searchEmpFileAttachments();
    }

    public void deleteEmpFileAttachment(EmployeeFileAttachmentDetailData empFileAttachmentDetails) {
	try {
	    EmployeeFileAttachmentService.deleteAttachment(empFileAttachmentDetails, this.loginEmpData.getEmpId());
	    empFileAttachmentsDetailList.remove(empFileAttachmentDetails);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public long getEmployeeId() {
	return employeeId;
    }

    public int getTransactionClass() {
	return transactionClass;
    }

    public boolean getIsModifyAdmin() {
	return isModifyAdmin;
    }

    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
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

    public List<EmployeeFileAttachmentDetailData> getEmpFileAttachmentsDetailList() {
	return empFileAttachmentsDetailList;
    }

    public void setEmpFileAttachmentsDetailList(List<EmployeeFileAttachmentDetailData> empFileAttachmentsDetailList) {
	this.empFileAttachmentsDetailList = empFileAttachmentsDetailList;
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public String getFileDownloadParams() {
	return fileDownloadParams;
    }

    public void setFileDownloadParams(String fileDownloadParams) {
	this.fileDownloadParams = fileDownloadParams;
    }

    public String getDownloadURL() {
	return downloadURL;
    }

}
