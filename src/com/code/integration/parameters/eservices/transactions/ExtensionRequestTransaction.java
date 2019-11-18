package com.code.integration.parameters.eservices.transactions;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class ExtensionRequestTransaction implements Serializable{

	private Long id;
	private Long empId;
	private Integer extensionPeriodMonths;
	private String extensionReason;
	private Long attachmentId;
	private Long transactionTypeId;
	private Date terminationDate;
	private Long terminationTransactionId;
	private String terminationReason;
	private Integer totalExtensionPeriodMonths;
	private Long requesterId;
	private Long wfInstanceId;
	private Integer status;
	private String rejectionReason;
	private Long extensionRequestId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getEmpId() {
		return empId;
	}

	public void setEmpId(Long empId) {
		this.empId = empId;
	}

	public Integer getExtensionPeriodMonths() {
		return extensionPeriodMonths;
	}

	public void setExtensionPeriodMonths(Integer extensionPeriodMonths) {
		this.extensionPeriodMonths = extensionPeriodMonths;
	}

	public String getExtensionReason() {
		return extensionReason;
	}

	public void setExtensionReason(String extensionReason) {
		this.extensionReason = extensionReason;
	}

	public Long getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(Long attachmentId) {
		this.attachmentId = attachmentId;
	}

	public Long getTransactionTypeId() {
		return transactionTypeId;
	}

	public void setTransactionTypeId(Long transactionTypeId) {
		this.transactionTypeId = transactionTypeId;
	}

	public Date getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}

	public Long getTerminationTransactionId() {
		return terminationTransactionId;
	}

	public void setTerminationTransactionId(Long terminationTransactionId) {
		this.terminationTransactionId = terminationTransactionId;
	}

	public String getTerminationReason() {
		return terminationReason;
	}

	public void setTerminationReason(String terminationReason) {
		this.terminationReason = terminationReason;
	}

	public Integer getTotalExtensionPeriodMonths() {
		return totalExtensionPeriodMonths;
	}

	public void setTotalExtensionPeriodMonths(Integer totalExtensionPeriodMonths) {
		this.totalExtensionPeriodMonths = totalExtensionPeriodMonths;
	}

	public Long getRequesterId() {
		return requesterId;
	}

	public void setRequesterId(Long requesterId) {
		this.requesterId = requesterId;
	}

	public Long getWfInstanceId() {
		return wfInstanceId;
	}

	public void setWfInstanceId(Long wfInstanceId) {
		this.wfInstanceId = wfInstanceId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public Long getExtensionRequestId() {
		return extensionRequestId;
	}

	public void setExtensionRequestId(Long extensionRequestId) {
		this.extensionRequestId = extensionRequestId;
	}
}
