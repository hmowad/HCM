package com.code.ui.backings.hcm.retirements;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.enums.EservicesProcessesEnum;
import com.code.enums.ExtensionPeriodMonthsEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.SequenceNamesEnum;
import com.code.enums.TransactionStatusEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WFProcessRolesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskActionsEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.parameters.extensionrequest.ExtensionRequestTransaction;
import com.code.integration.webservicesclients.eservices.ExtensionRequestTransactionsClient;
import com.code.services.hcm.EmployeesService;
import com.code.services.util.Log4jService;
import com.code.ui.backings.base.WFBaseBacking;

@ViewScoped
@ManagedBean(name = "reExtensionRequest")
@SuppressWarnings("serial")
public class ReExtensionRequest extends WFBaseBacking {

	private int mode;
	private Long selectedEmpId;
	private Long seqId;
	private String extensionReason;
	private Integer extensionPeriodMonths;
	private EmployeeData selectedEmployee;
	private ExtensionRequestTransaction oldExtensionRequestTransaction;
	private ExtensionRequestTransaction extensionRequestTransaction;
	private String downloadFileParamId;
	private boolean openDownloadPopupFlag;
	private boolean openDownloadDialogueFlag;

	public ReExtensionRequest() throws BusinessException {
		processCode = EservicesProcessesEnum.REEXTENSION_REQUEST.getCode();
		super.init();
		if (role.equals(WFProcessRolesEnum.REQUESTER.getCode())) { // Role 0
			setMode(0);
			setScreenTitle(getMessage("title_reExtensionRequest"));
			seqId = Long.parseLong(ExtensionRequestTransactionsClient
					.getNextSequenceValue(SequenceNamesEnum.EXTENSION_REQUEST_TRANS.getCode()));
			extensionRequestTransaction = new ExtensionRequestTransaction();
			extensionRequestTransaction.setStatus(TransactionStatusEnum.UNDER_PROCESSING.getCode());
			processId = WFProcessesEnum.REEXTENSION_REQUEST.getCode();
		} else if (role.equals(WFProcessRolesEnum.APPROVAL.getCode())) { // Role
																			// 1
			try {
				setMode(1);
				setScreenTitle(getMessage("title_approveReExtensionRequest"));
				extensionRequestTransaction = ExtensionRequestTransactionsClient.getExtensionRequestTransactionByInstanceId(instance.getInstanceId());
				selectedEmployee = EmployeesService.getEmployeeData(extensionRequestTransaction.getEmpId());
			} catch (BusinessException e) {
				reset();
				Log4jService.traceErrorException(ExtensionRequest.class, e, "ExtensionRequest");
				setServerSideErrorMessages(getMessage("error_loadingPage"));
			}
		}
	}

	public void getSelectedEmployeeData() {
		try {
			selectedEmployee = EmployeesService.getEmployeesByEmpsIds(new Long[] { selectedEmpId }).get(0);
			oldExtensionRequestTransaction = ExtensionRequestTransactionsClient.validateReExtensionTransaction(selectedEmployee.getEmpId());
		} catch (BusinessException e) {
			reset();
			setServerSideErrorMessages(getMessage(e.getMessage()));
		}
	}

	public void reset() {
		selectedEmployee = null;
		oldExtensionRequestTransaction = null;
	}

	/**************************** ActionButtons *************************************/
	public String send() {
		if (selectedEmployee == null) {
			setServerSideErrorMessages(getMessage("error_noEmpSelected"));
			return null;
		} else

		{
			extensionRequestTransaction.setId(seqId);
			extensionRequestTransaction.setEmpId(selectedEmployee.getEmpId());
//			extensionRequestTransaction.setAttachmentId(attachments);
			extensionRequestTransaction.setExtensionPeriodMonths(extensionPeriodMonths);
			extensionRequestTransaction.setTerminationDate(oldExtensionRequestTransaction.getTerminationDate());
			extensionRequestTransaction.setRequesterId(requester.getEmpId());
			extensionRequestTransaction.setTerminationTransactionId(oldExtensionRequestTransaction.getTerminationTransactionId());
			extensionRequestTransaction.setTotalExtensionPeriodMonths(oldExtensionRequestTransaction.getTotalExtensionPeriodMonths() + extensionPeriodMonths);
			extensionRequestTransaction.setTerminationReason(oldExtensionRequestTransaction.getTerminationReason());
			extensionRequestTransaction.setTransactionTypeId(new Long(TransactionTypesEnum.REEXTENSION_TRANSACTION.getCode()));
			extensionRequestTransaction.setExtensionRequestId(oldExtensionRequestTransaction.getId());
			try {
				extensionRequestTransaction = ExtensionRequestTransactionsClient.initTransaction(new Long(processId), taskUrl, extensionRequestTransaction);
				setServerSideSuccessMessages(getParameterizedMessage("notify_successOperation"));
				return NavigationEnum.OUTBOX.toString();
			} catch (BusinessException e) {
				Log4jService.traceErrorException(ReExtensionRequest.class, e, "ReExtensionRequest");
				setServerSideErrorMessages(getMessage(e.getMessage()));
				return null;
			}
		}
	}

	public String reject() {
		currentTask.setAction(WFTaskActionsEnum.REJECT.getCode());
		currentTask.setRefuseReasons(extensionRequestTransaction.getRejectionReason());
		return rejectEserviceTransaction(extensionRequestTransaction.getId());
	}

	public String approve() {
		currentTask.setAction(WFTaskActionsEnum.APPROVE.getCode());
		return approveEserviceTransaction(extensionRequestTransaction.getId());
	}

	/**************************** Setters&Getters *************************************/

	public String getExtensionPeriodOneMonth() {
		return ExtensionPeriodMonthsEnum.ONE_MONTH.getCode();
	}

	public String getLastExtensionPeriodByMonths() {
		if (oldExtensionRequestTransaction == null && mode == 0) {
			return "";
		} else if (mode == 1) {
			return ExtensionPeriodMonthsEnum.ONE_MONTH.getCode();
		} else {
			return ExtensionPeriodMonthsEnum.ONE_MONTH.getCode();
		}
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public Long getSeqId() {
		return seqId;
	}

	public void setSeqId(Long seqId) {
		this.seqId = seqId;
	}

	public String getExtensionReason() {
		return extensionReason;
	}

	public void setExtensionReason(String extensionReason) {
		this.extensionReason = extensionReason;
	}

	public Integer getExtensionPeriodMonths() {
		return extensionPeriodMonths;
	}

	public void setExtensionPeriodMonths(Integer extensionPeriodMonths) {
		this.extensionPeriodMonths = extensionPeriodMonths;
	}

	public EmployeeData getSelectedEmployee() {
		return selectedEmployee;
	}

	public void setSelectedEmployee(EmployeeData selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}

	public ExtensionRequestTransaction getOldExtensionRequestTransaction() {
		return oldExtensionRequestTransaction;
	}

	public void setOldExtensionRequestTransaction(ExtensionRequestTransaction oldExtensionRequestTransaction) {
		this.oldExtensionRequestTransaction = oldExtensionRequestTransaction;
	}

	public ExtensionRequestTransaction getExtensionRequestTransaction() {
		return extensionRequestTransaction;
	}

	public void setExtensionRequestTransaction(ExtensionRequestTransaction extensionRequestTransaction) {
		this.extensionRequestTransaction = extensionRequestTransaction;
	}

	public String getDownloadFileParamId() {
		return downloadFileParamId;
	}

	public void setDownloadFileParamId(String downloadFileParamId) {
		this.downloadFileParamId = downloadFileParamId;
	}

	public boolean isOpenDownloadPopupFlag() {
		return openDownloadPopupFlag;
	}

	public void setOpenDownloadPopupFlag(boolean openDownloadPopupFlag) {
		this.openDownloadPopupFlag = openDownloadPopupFlag;
	}

	public boolean isOpenDownloadDialogueFlag() {
		return openDownloadDialogueFlag;
	}

	public void setOpenDownloadDialogueFlag(boolean openDownloadDialogueFlag) {
		this.openDownloadDialogueFlag = openDownloadDialogueFlag;
	}

	public Long getSelectedEmpId() {
		return selectedEmpId;
	}

	public void setSelectedEmpId(Long selectedEmpId) {
		this.selectedEmpId = selectedEmpId;
	}
}
