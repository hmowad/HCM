package com.code.ui.backings.hcm.retirements;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.terminations.TerminationTransactionData;
import com.code.dal.orm.workflow.WFTask;
import com.code.enums.EmployeeStatusEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.SequenceNamesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WFProcessRolesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskActionsEnum;
import com.code.enums.eservices.EservicesProcessesEnum;
import com.code.enums.eservices.ExtensionPeriodMonthsEnum;
import com.code.enums.eservices.TransactionStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.parameters.eservices.transactions.ExtensionRequestTransaction;
import com.code.integration.webservicesclients.eservices.ExtensionRequestTransactionsClient;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TerminationsService;
import com.code.services.util.Log4jService;
import com.code.services.workflow.EServicesBaseWorkFlowService;
import com.code.ui.backings.base.WFBaseBacking;

@ManagedBean(name = "extensionRequest")
@ViewScoped
@SuppressWarnings("serial")
public class ExtensionRequest extends WFBaseBacking {

    private int mode;
    private Long selectedEmpId;
    private EmployeeData selectedEmp;
    private TerminationTransactionData empTerminationTrans;
    private Long seqId;
    private ExtensionRequestTransaction extensionRequestTransaction;
    private String downloadFileParamId;
    private boolean openDownloadPopupFlag;
    private boolean openDownloadDialogueFlag;

    /**
     * Default Constructor
     * 
     * @throws BusinessException
     */
    public ExtensionRequest() throws BusinessException {
	try {
	    processCode = EservicesProcessesEnum.EXTENSION_REQUEST.getCode();
	    setScreenTitle(getMessage("title_extentionRequest"));
	    super.init();
	    if (role.equals(WFProcessRolesEnum.REQUESTER.getCode())) {
		setMode(0);
		seqId = Long.parseLong(ExtensionRequestTransactionsClient.getNextSequenceValue(SequenceNamesEnum.EXTENSION_REQUEST_TRANS.getCode()));
		extensionRequestTransaction = new ExtensionRequestTransaction();
		extensionRequestTransaction.setStatus(TransactionStatusEnum.UNDER_PROCESSING.getCode());
		processId = WFProcessesEnum.EXTENSION_REQUEST.getCode();
	    } else if (role.equals(WFProcessRolesEnum.APPROVAL.getCode())) {
		setMode(1);
		extensionRequestTransaction = ExtensionRequestTransactionsClient.getExtensionRequestTransactionByInstanceId(instance.getInstanceId());
		selectedEmp = EmployeesService.getEmployeeData(extensionRequestTransaction.getEmpId());
	    } else if (role.equals(WFProcessRolesEnum.NOTIFICATION.getCode())) {
		setMode(2);
		extensionRequestTransaction = ExtensionRequestTransactionsClient.getExtensionRequestTransactionByInstanceId(instance.getInstanceId());
		selectedEmp = EmployeesService.getEmployeeData(extensionRequestTransaction.getEmpId());
	    } else if (role.equals(WFProcessRolesEnum.HISTORY.getCode())) {
		setMode(3);
	    }
	} catch (BusinessException e) {
	    Log4jService.traceErrorException(ExtensionRequest.class, e, "ExtensionRequest");
	    setServerSideErrorMessages(getMessage("error_loadingPage"));
	}
    }

    /**
     * Reset Selected Employee and Employee Termination Objects
     */
    public void reset() {
	selectedEmp = null;
	empTerminationTrans = null;
    }

    /**
     * Validate Selected Employee
     * 
     * @param event
     */
    public void getSelectedEmployee() {
	try {
	    selectedEmp = EmployeesService.getEmployeesByEmpsIds(new Long[] { selectedEmpId }).get(0);
	    empTerminationTrans = TerminationsService.getEffectiveTerminationTransaction(selectedEmp.getEmpId());
	    if (selectedEmp.getStatusId() != EmployeeStatusEnum.SERVICE_TERMINATED.getCode()
		    || empTerminationTrans == null) {
		setServerSideErrorMessages(getMessage("error_noTerminationForEmployee"));
		reset();
		return;
	    }
	} catch (BusinessException e) {
	    reset();
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    /**
     * Create new Extension Transaction
     * 
     * @return
     */
    public String save() {
	try {
	    extensionRequestTransaction.setEmpId(selectedEmp.getEmpId());
	    extensionRequestTransaction.setTransactionTypeId(new Long(TransactionTypesEnum.EXTENSION_REQUEST.getCode()));
	    extensionRequestTransaction.setTerminationDate(empTerminationTrans.getServiceTerminationDate());
	    extensionRequestTransaction.setTerminationTransactionId(empTerminationTrans.getId());
	    extensionRequestTransaction.setTerminationReason(empTerminationTrans.getReasonDesc());
	    extensionRequestTransaction.setTotalExtensionPeriodMonths(extensionRequestTransaction.getExtensionPeriodMonths());
	    extensionRequestTransaction.setRequesterId(loginEmpData.getEmpId());
	    extensionRequestTransaction.setId(seqId);
	    extensionRequestTransaction = ExtensionRequestTransactionsClient.initTransaction(new Long(processId), taskUrl, extensionRequestTransaction);
	    this.setServerSideSuccessMessages(getParameterizedMessage("notify_successOperation"));
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    Log4jService.traceErrorException(ExtensionRequest.class, e, "ExtensionRequest");
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    /**
     * Direct Manager Rejection
     * 
     * @return
     */
    public String reject() {
	if (extensionRequestTransaction.getRejectionReason() == null || extensionRequestTransaction.getRejectionReason().isEmpty()) {
	    this.setServerSideErrorMessages(getParameterizedMessage("error_mandatoryRejectionReason"));
	    return null;
	}
	currentTask.setAction(WFTaskActionsEnum.REJECT_ESERVICE.getCode());
	currentTask.setRefuseReasons(extensionRequestTransaction.getRejectionReason());
	return rejectEserviceTransaction(extensionRequestTransaction.getId());

    }

    /**
     * Direct Manager Approval
     * 
     * @return
     */
    public String approve() {
	currentTask.setAction(WFTaskActionsEnum.APPROVE_ESERVICE.getCode());
	return approveEserviceTransaction(extensionRequestTransaction.getId());
    }

    public String doNotifyTasks() {
	try {
	    List<WFTask> notificationTasks = new ArrayList<WFTask>();
	    notificationTasks.add(currentTask);
	    EServicesBaseWorkFlowService.doNotifyTasks(notificationTasks, processCode);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    /**************************** Setters&Getters *************************************/

    public EmployeeData getSelectedEmp() {
	return selectedEmp;
    }

    public void setSelectedEmp(EmployeeData selectedEmp) {
	this.selectedEmp = selectedEmp;
    }

    public ExtensionRequestTransaction getExtensionRequestTransaction() {
	return extensionRequestTransaction;
    }

    public void setExtensionRequestTransaction(ExtensionRequestTransaction extensionRequestTransaction) {
	this.extensionRequestTransaction = extensionRequestTransaction;
    }

    public String getExtensionPeriodOneMonth() {
	return ExtensionPeriodMonthsEnum.ONE_MONTH.getCode();
    }

    public String getExtensionPeriodTwoMonths() {
	return ExtensionPeriodMonthsEnum.TWO_MONTHS.getCode();
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

    public TerminationTransactionData getEmpTerminationTrans() {
	return empTerminationTrans;
    }

    public void setEmpTerminationTrans(TerminationTransactionData empTerminationTrans) {
	this.empTerminationTrans = empTerminationTrans;
    }

    public Long getSelectedEmpId() {
	return selectedEmpId;
    }

    public void setSelectedEmpId(Long selectedEmpId) {
	this.selectedEmpId = selectedEmpId;
    }

}
