package com.code.ui.backings.hcm.trainings;

import java.util.ArrayList;
import java.util.List;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.dal.orm.hcm.trainings.TrainingUnitData;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingData;
import com.code.enums.NavigationEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.services.workflow.hcm.TrainingEmployeesWorkFlow;
import com.code.ui.backings.base.WFBaseBacking;

public abstract class TrainingAndScholarshipBase extends WFBaseBacking {

    protected int mode;
    protected int pageSize = 10;
    protected WFTrainingData wfTraining;
    protected List<WFTrainingData> wfTrainingList;
    protected List<TrainingUnitData> trainingUnitsList;
    protected Long selectedReviewerEmpId;
    protected List<EmployeeData> reviewerEmps;
    protected boolean isDecisionRequest;
    protected TrainingCourseEventData selectedCourseEvent;
    protected List<EmployeeData> internalCopies;

    ///////////////////////////////////////////////////// CLAIM /////////////////////////////////////////
    public boolean canChangeRequestData() {
	if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode()))
	    return true;

	if ((this.getRole().equals(WFTaskRolesEnum.REVIEWER_EMP.getCode()) || this.getRole().equals(WFTaskRolesEnum.SECONDARY_REVIEWER_EMP.getCode()))
		&& this.currentEmployee.getEmpId().equals(this.requester.getEmpId()))
	    return true;

	return false;
    }

    public String initWFTraining() {
	try {
	    if (wfTraining != null) {
		wfTrainingList = new ArrayList<WFTrainingData>();
		wfTrainingList.add(wfTraining);
	    }

	    TrainingEmployeesWorkFlow.initWFTraining(requester, wfTrainingList, processId, taskUrl);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveWFTrainingDM() {
	try {
	    TrainingEmployeesWorkFlow.doWFTrainingDM(requester, instance, currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectWFTrainingDM() {
	try {
	    TrainingEmployeesWorkFlow.doWFTrainingDM(requester, instance, currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String redirectWFTrainingMR() {
	try {
	    TrainingEmployeesWorkFlow.doWFTrainingMR(requester, instance, currentTask, selectedReviewerEmpId);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveWFTrainingRE() {
	try {
	    if (wfTraining != null) {
		wfTrainingList = new ArrayList<WFTrainingData>();
		wfTrainingList.add(wfTraining);
	    }

	    TrainingEmployeesWorkFlow.doWFTrainingRE(requester, instance, wfTrainingList, currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectWFTrainingRE() {
	try {
	    if (wfTraining != null) {
		wfTrainingList = new ArrayList<WFTrainingData>();
		wfTrainingList.add(wfTraining);
	    }

	    TrainingEmployeesWorkFlow.doWFTrainingRE(requester, instance, wfTrainingList, currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveWFTrainingSM() {
	try {
	    if (wfTraining != null) {
		wfTrainingList = new ArrayList<WFTrainingData>();
		wfTrainingList.add(wfTraining);
	    }

	    TrainingEmployeesWorkFlow.doWFTrainingSM(requester, instance, wfTrainingList, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String returnWFTrainingSM() {
	try {
	    if (wfTraining != null) {
		wfTrainingList = new ArrayList<WFTrainingData>();
		wfTrainingList.add(wfTraining);
	    }

	    TrainingEmployeesWorkFlow.doWFTrainingSM(requester, instance, wfTrainingList, currentTask, WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectWFTrainingSM() {
	try {
	    if (wfTraining != null) {
		wfTrainingList = new ArrayList<WFTrainingData>();
		wfTrainingList.add(wfTraining);
	    }

	    TrainingEmployeesWorkFlow.doWFTrainingSM(requester, instance, wfTrainingList, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String closeProcess() {
	try {
	    TrainingEmployeesWorkFlow.closeWFInstanceByNotification(instance, currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    ////////////////////////////////////////////////////// Nomination /////////////////////////////////////////////////////////
    public String initNomination() {
	try {
	    if (wfTraining != null) {
		wfTrainingList = new ArrayList<WFTrainingData>();
		wfTrainingList.add(wfTraining);
	    }

	    TrainingEmployeesWorkFlow.initNomination(requester, wfTrainingList, selectedCourseEvent, processId, taskUrl, internalCopies);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveNominationDM() {
	try {
	    if (wfTraining != null) {
		wfTrainingList = new ArrayList<WFTrainingData>();
		wfTrainingList.add(wfTraining);
	    }

	    TrainingEmployeesWorkFlow.doNominationDM(requester, wfTrainingList, selectedCourseEvent, instance, currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectNominationDM() {
	try {
	    if (wfTraining != null) {
		wfTrainingList = new ArrayList<WFTrainingData>();
		wfTrainingList.add(wfTraining);
	    }

	    TrainingEmployeesWorkFlow.doNominationDM(requester, wfTrainingList, selectedCourseEvent, instance, currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String redirectNominationMR() {
	try {
	    TrainingEmployeesWorkFlow.doNominationMR(requester, instance, currentTask, selectedReviewerEmpId);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String redirectNominationSMR() {
	try {
	    TrainingEmployeesWorkFlow.doNominationSMR(requester, instance, currentTask, selectedReviewerEmpId);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String redirectNominationESMR() {
	try {
	    TrainingEmployeesWorkFlow.doNominationESMR(requester, instance, currentTask, selectedReviewerEmpId);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String redirectNominationMRTS() {
	try {
	    if (wfTraining != null) {
		wfTrainingList = new ArrayList<WFTrainingData>();
		wfTrainingList.add(wfTraining);
	    }

	    TrainingEmployeesWorkFlow.doNominationMRTS(requester, selectedCourseEvent.getTrainingUnitRegionId(), instance, currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveNominationRE() {
	try {
	    if (wfTraining != null) {
		wfTrainingList = new ArrayList<WFTrainingData>();
		wfTrainingList.add(wfTraining);
	    }

	    TrainingEmployeesWorkFlow.doNominationRE(requester, instance, wfTrainingList, selectedCourseEvent, currentTask, true, internalCopies);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectNominationRE() {
	try {
	    if (wfTraining != null) {
		wfTrainingList = new ArrayList<WFTrainingData>();
		wfTrainingList.add(wfTraining);
	    }

	    TrainingEmployeesWorkFlow.doNominationRE(requester, instance, wfTrainingList, selectedCourseEvent, currentTask, false, internalCopies);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveNominationSRE() {
	try {
	    if (wfTraining != null) {
		wfTrainingList = new ArrayList<WFTrainingData>();
		wfTrainingList.add(wfTraining);
	    }

	    TrainingEmployeesWorkFlow.doNominationSRE(requester, instance, wfTrainingList, selectedCourseEvent, currentTask, true, internalCopies);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectNominationSRE() {
	try {
	    if (wfTraining != null) {
		wfTrainingList = new ArrayList<WFTrainingData>();
		wfTrainingList.add(wfTraining);
	    }

	    TrainingEmployeesWorkFlow.doNominationSRE(requester, instance, wfTrainingList, selectedCourseEvent, currentTask, false, internalCopies);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveNominationESRE() {
	try {
	    if (wfTraining != null) {
		wfTrainingList = new ArrayList<WFTrainingData>();
		wfTrainingList.add(wfTraining);
	    }

	    TrainingEmployeesWorkFlow.doNominationESRE(requester, instance, currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectNominationESRE() {
	try {
	    if (wfTraining != null) {
		wfTrainingList = new ArrayList<WFTrainingData>();
		wfTrainingList.add(wfTraining);
	    }

	    TrainingEmployeesWorkFlow.doNominationESRE(requester, instance, currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveNominationSM() {
	try {
	    if (wfTraining != null) {
		wfTrainingList = new ArrayList<WFTrainingData>();
		wfTrainingList.add(wfTraining);
	    }

	    TrainingEmployeesWorkFlow.doNominationSM(requester, instance, wfTrainingList, selectedCourseEvent, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String returnNominationSM() {
	try {
	    if (wfTraining != null) {
		wfTrainingList = new ArrayList<WFTrainingData>();
		wfTrainingList.add(wfTraining);
	    }

	    TrainingEmployeesWorkFlow.doNominationSM(requester, instance, wfTrainingList, selectedCourseEvent, currentTask, WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectNominationSM() {
	try {
	    if (wfTraining != null) {
		wfTrainingList = new ArrayList<WFTrainingData>();
		wfTrainingList.add(wfTraining);
	    }

	    TrainingEmployeesWorkFlow.doNominationSM(requester, instance, wfTrainingList, selectedCourseEvent, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveNominationSSM() {
	try {
	    if (wfTraining != null) {
		wfTrainingList = new ArrayList<WFTrainingData>();
		wfTrainingList.add(wfTraining);
	    }
	    TrainingEmployeesWorkFlow.doNominationSSM(requester, instance, wfTrainingList, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String returnNominationSSM() {
	try {
	    TrainingEmployeesWorkFlow.doNominationSSM(requester, instance, null, currentTask, WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectNominationSSM() {
	try {
	    TrainingEmployeesWorkFlow.doNominationSSM(requester, instance, null, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveNominationESSM() {
	try {
	    if (wfTraining != null) {
		wfTrainingList = new ArrayList<WFTrainingData>();
		wfTrainingList.add(wfTraining);
	    }

	    TrainingEmployeesWorkFlow.doNominationESSM(requester, instance, wfTrainingList, selectedCourseEvent, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String returnNominationESSM() {
	try {
	    if (wfTraining != null) {
		wfTrainingList = new ArrayList<WFTrainingData>();
		wfTrainingList.add(wfTraining);
	    }

	    TrainingEmployeesWorkFlow.doNominationESSM(requester, instance, wfTrainingList, selectedCourseEvent, currentTask, WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectNominationESSM() {
	try {
	    if (wfTraining != null) {
		wfTrainingList = new ArrayList<WFTrainingData>();
		wfTrainingList.add(wfTraining);
	    }

	    TrainingEmployeesWorkFlow.doNominationESSM(requester, instance, wfTrainingList, selectedCourseEvent, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public void printScholarshipDecision(long trainingTransactionDetailId, long transactionTypeId) {
	try {
	    byte[] bytes = TrainingEmployeesService.getScholarshipDecisionBytes(trainingTransactionDetailId, transactionTypeId);
	    super.print(bytes);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public WFTrainingData getWfTraining() {
	return wfTraining;
    }

    public void setWfTraining(WFTrainingData wfTraining) {
	this.wfTraining = wfTraining;
    }

    public List<WFTrainingData> getWfTrainingList() {
	return wfTrainingList;
    }

    public void setWfTrainingList(List<WFTrainingData> wfTrainingList) {
	this.wfTrainingList = wfTrainingList;
    }

    public List<TrainingUnitData> getTrainingUnitsList() {
	return trainingUnitsList;
    }

    public void setTrainingUnitsList(List<TrainingUnitData> trainingUnitsList) {
	this.trainingUnitsList = trainingUnitsList;
    }

    public Long getSelectedReviewerEmpId() {
	return selectedReviewerEmpId;
    }

    public void setSelectedReviewerEmpId(Long selectedReviewerEmpId) {
	this.selectedReviewerEmpId = selectedReviewerEmpId;
    }

    public List<EmployeeData> getReviewerEmps() {
	return reviewerEmps;
    }

    public void setReviewerEmps(List<EmployeeData> reviewerEmps) {
	this.reviewerEmps = reviewerEmps;
    }

    public boolean isDecisionRequest() {
	return isDecisionRequest;
    }

    public void setDecisionRequest(boolean isDecisionRequest) {
	this.isDecisionRequest = isDecisionRequest;
    }

    public TrainingCourseEventData getSelectedCourseEvent() {
	return selectedCourseEvent;
    }

    public void setSelectedCourseEvent(TrainingCourseEventData selectedCourseEvent) {
	this.selectedCourseEvent = selectedCourseEvent;
    }

    public List<EmployeeData> getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(List<EmployeeData> internalCopies) {
	this.internalCopies = internalCopies;
    }
}
