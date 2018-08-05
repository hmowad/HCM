package com.code.ui.backings.hcm.terminations;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.movements.MovementTransactionData;
import com.code.dal.orm.hcm.terminations.TerminationTransactionData;
import com.code.dal.orm.workflow.hcm.terminations.WFTerminationCancellationMovementData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.MovementsService;
import com.code.services.hcm.TerminationsService;
import com.code.services.util.CommonService;
import com.code.services.workflow.hcm.TerminationsWorkflow;
import com.code.ui.backings.base.WFBaseBacking;

@ManagedBean(name = "terminationCivilianMovement")
@ViewScoped
public class TerminationCivilianMovement extends WFBaseBacking {

    private EmployeeData selectedEmpData;
    private WFTerminationCancellationMovementData wfCancellationMovementData;
    private MovementTransactionData selectedEmpMovementTransactionData;
    private Long selectedEmpId;
    private List<EmployeeData> internalCopies;

    public TerminationCivilianMovement() {
	super.init();
	this.processId = WFProcessesEnum.CIVILIANS_TERMINATION_MOVEMENT.getCode();
	this.initialize();
    }

    private void initialize() {
	try {
	    if (!this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		wfCancellationMovementData = TerminationsWorkflow.getWFTerminationCancellationMovementByInstanceId(instance.getInstanceId());
		selectedEmpData = EmployeesService.getEmployeeData(wfCancellationMovementData.getEmpId());
		selectedEmpMovementTransactionData = MovementsService.getExternalMoveTransactionByEmployeeId(wfCancellationMovementData.getEmpId());
		internalCopies = EmployeesService.getEmployeesByIdsString(wfCancellationMovementData.getInternalCopies());
	    } else {
		selectedEmpData = new EmployeeData();
		selectedEmpMovementTransactionData = new MovementTransactionData();
		selectedEmpId = null;
		wfCancellationMovementData = new WFTerminationCancellationMovementData();
		internalCopies = new ArrayList<EmployeeData>();
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	} catch (Exception e) {
	    this.setServerSideErrorMessages(getMessage("error_general"));
	    e.printStackTrace();
	}
    }

    public void constructEmpData() {
	try {
	    if (selectedEmpId != null && selectedEmpId != FlagsEnum.OFF.getCode()) {
		selectedEmpData = EmployeesService.getEmployeeData(selectedEmpId);
		if (selectedEmpData.getCategoryId().equals(CategoriesEnum.CONTRACTORS.getCode()))
		    throw new BusinessException("error_terminationCiviliansMovementInvalidCategory");

		selectedEmpMovementTransactionData = MovementsService.getExternalMoveTransactionByEmployeeId(selectedEmpId);
		if (selectedEmpMovementTransactionData == null)
		    throw new BusinessException("error_terminationCiviliansMovementInvalidEmp");

		wfCancellationMovementData.setEmpId(selectedEmpId);
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    selectedEmpId = null;
	    selectedEmpData = new EmployeeData();
	}
    }

    public void printForNotification() {
	try {
	    TerminationTransactionData terminationTransactionData = TerminationsService.searchTerminationMovementTransaction(null, wfCancellationMovementData.getEmpId(), wfCancellationMovementData.getMovementJoiningDate(), wfCancellationMovementData.getDisclaimerDate(), wfCancellationMovementData.getServiceTerminationDate(), wfCancellationMovementData.getMovementJoiningDesc());

	    if (terminationTransactionData != null) {
		byte[] bytes = TerminationsService.getTerminationDecisionBytes(terminationTransactionData.getId(), CategoriesEnum.PERSONS.getCode(), terminationTransactionData.getTransactionTypeCode());
		super.print(bytes);
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public String initProcess() {
	try {
	    String internalCopiesString = EmployeesService.getEmployeesIdsString(internalCopies);
	    wfCancellationMovementData.setInternalCopies(internalCopiesString);
	    TerminationsWorkflow.initTerminationCancellationMovement(wfCancellationMovementData, selectedEmpMovementTransactionData.getExecutionDate(), requester, processId, attachments, taskUrl);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveRE() {
	String internalCopiesString = EmployeesService.getEmployeesIdsString(internalCopies);
	wfCancellationMovementData.setInternalCopies(internalCopiesString);
	try {
	    TerminationsWorkflow.doTerminationCancellationMovementRE(requester, instance, wfCancellationMovementData, selectedEmpMovementTransactionData.getExecutionDate(), currentTask, attachments, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage("error_general"));
	    return null;
	}
    }

    // reject by reviewer employee
    public String rejectRE() {
	try {
	    TerminationsWorkflow.doTerminationCancellationMovementRE(requester, instance, wfCancellationMovementData, selectedEmpMovementTransactionData.getExecutionDate(), currentTask, attachments, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage("error_general"));
	    return null;
	}
    }

    public String signSM() {
	try {
	    TerminationsWorkflow.doTerminationCancellationMovementSM(requester, instance, wfCancellationMovementData, null, currentTask, loginEmpData.getEmpId(), CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.STE_TERMINATION_MOVEMENT.getCode(), TransactionClassesEnum.TERMINATIONS.getCode()).getId(), selectedEmpData.getCategoryId(), WFActionFlagsEnum.APPROVE.getCode());

	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage("error_general"));
	    return null;
	}
    }

    public String rejectSM() {
	try {
	    TerminationsWorkflow.doTerminationCancellationMovementSM(requester, instance, wfCancellationMovementData, null, currentTask, loginEmpData.getEmpId(), CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.STE_TERMINATION_MOVEMENT.getCode(), TransactionClassesEnum.TERMINATIONS.getCode()).getId(), selectedEmpData.getCategoryId(), WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage("error_general"));
	    return null;
	}
    }

    public String modifySM() {
	try {
	    TerminationsWorkflow.doTerminationCancellationMovementSM(requester, instance, wfCancellationMovementData, null, currentTask, loginEmpData.getEmpId(), CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.STE_TERMINATION_MOVEMENT.getCode(), TransactionClassesEnum.TERMINATIONS.getCode()).getId(), selectedEmpData.getCategoryId(), WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage("error_general"));
	    return null;
	}
    }

    public String closeProcess() {
	try {
	    TerminationsWorkflow.closeWFInstanceByNotification(instance, currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage("error_general"));
	    return null;
	}
    }

    public EmployeeData getSelectedEmpData() {
	return selectedEmpData;
    }

    public void setSelectedEmpData(EmployeeData selectedEmpData) {
	this.selectedEmpData = selectedEmpData;
    }

    public WFTerminationCancellationMovementData getWfCancellationMovementData() {
	return wfCancellationMovementData;
    }

    public void setWfCancellationMovementData(WFTerminationCancellationMovementData wfCancellationMovementData) {
	this.wfCancellationMovementData = wfCancellationMovementData;
    }

    public MovementTransactionData getSelectedEmpMovementTransactionData() {
	return selectedEmpMovementTransactionData;
    }

    public void setSelectedEmpMovementTransactionData(MovementTransactionData selectedEmpMovementTransactionData) {
	this.selectedEmpMovementTransactionData = selectedEmpMovementTransactionData;
    }

    public Long getSelectedEmpId() {
	return selectedEmpId;
    }

    public void setSelectedEmpId(Long selectedEmpId) {
	this.selectedEmpId = selectedEmpId;
    }

    public List<EmployeeData> getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(List<EmployeeData> internalCopies) {
	this.internalCopies = internalCopies;
    }

}
