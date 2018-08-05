package com.code.ui.backings.hcm.terminations;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.hcm.terminations.TerminationTransactionData;
import com.code.enums.CategoriesEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.TerminationRequestTypeEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFPositionsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.config.ETRConfigurationService;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TerminationsService;
import com.code.services.util.AttachmentsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.TerminationsWorkflow;

@ManagedBean(name = "terminationsCiviliansRecords")
@ViewScoped
public class TerminationsCiviliansRecords extends TerminationsRecordsBase {

    private boolean privilegesFlag = true;

    public TerminationsCiviliansRecords() throws BusinessException {
	super.initialize();
	checkEmployeePrivileges();
	loadReasons();
	// civilianCategories = CommonService.getPersonsCategories();
    }

    public void addAttachmentsId() {
	try {
	    terminationRecordData.setAttachments(AttachmentsService.getNextAttachmentsId());
	    TerminationsService.saveTerminationRecord(terminationRecordData, loginEmpData.getEmpId());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    private void checkEmployeePrivileges() throws BusinessException {
	try {
	    // reset civilianCategories first
	    civilianCategories = new ArrayList<Category>();
	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode()) && terminationRecordData.getId() == null) {
		// access to create record
		if (requester.getPhysicalUnitId().equals(TerminationsWorkflow.getWFPositionByCodeAndRegionId(WFPositionsEnum.PERSONS_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()).getUnitId())) {
		    civilianCategories.add(CommonService.getCategoryById(CategoriesEnum.PERSONS.getCode()));
		    civilianCategories.add(CommonService.getCategoryById(CategoriesEnum.MEDICAL_STAFF.getCode()));
		    terminationRecordData.setCategoryId(CategoriesEnum.PERSONS.getCode());
		} else if (requester.getPhysicalUnitId().equals(TerminationsWorkflow.getWFPositionByCodeAndRegionId(WFPositionsEnum.USERS_WAGES_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()).getUnitId())) {
		    civilianCategories.add(CommonService.getCategoryById(CategoriesEnum.USERS.getCode()));
		    civilianCategories.add(CommonService.getCategoryById(CategoriesEnum.WAGES.getCode()));
		    terminationRecordData.setCategoryId(CategoriesEnum.USERS.getCode());
		} else if (requester.getPhysicalUnitId().equals(TerminationsWorkflow.getWFPositionByCodeAndRegionId(WFPositionsEnum.CONTRACTORS_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()).getUnitId())) {
		    civilianCategories.add(CommonService.getCategoryById(CategoriesEnum.CONTRACTORS.getCode()));
		    terminationRecordData.setCategoryId(CategoriesEnum.CONTRACTORS.getCode());
		} else {
		    privilegesFlag = false;
		}
	    } else {
		civilianCategories.add(CommonService.getCategoryById(terminationRecordData.getCategoryId()));
		if (terminationRecordData.getCategoryId().equals(CategoriesEnum.PERSONS.getCode()) || terminationRecordData.getCategoryId().equals(CategoriesEnum.MEDICAL_STAFF.getCode())) {
		    if (!requester.getPhysicalUnitId().equals(TerminationsWorkflow.getWFPositionByCodeAndRegionId(WFPositionsEnum.PERSONS_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()).getUnitId())) {
			privilegesFlag = false;
		    }
		} else if (terminationRecordData.getCategoryId().equals(CategoriesEnum.USERS.getCode()) || terminationRecordData.getCategoryId().equals(CategoriesEnum.WAGES.getCode())) {
		    if (!requester.getPhysicalUnitId().equals(TerminationsWorkflow.getWFPositionByCodeAndRegionId(WFPositionsEnum.USERS_WAGES_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()).getUnitId())) {
			privilegesFlag = false;
		    }
		} else if (terminationRecordData.getCategoryId().equals(CategoriesEnum.CONTRACTORS.getCode())) {
		    if (!requester.getPhysicalUnitId().equals(TerminationsWorkflow.getWFPositionByCodeAndRegionId(WFPositionsEnum.CONTRACTORS_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()).getUnitId())) {
			privilegesFlag = false;
		    }
		}
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	} catch (Exception e) {
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public void loadReasons() {
	try {
	    if (terminationRecordData.getCategoryId() != CategoriesEnum.CONTRACTORS.getCode())
		terminationReasons = TerminationsService.getTerminationReasons(CategoriesEnum.PERSONS.getCode());
	    else {
		terminationReasons = TerminationsService.getTerminationReasons(CategoriesEnum.CONTRACTORS.getCode());
	    }
	    ranks = CommonService.getRanks(null, new Long[] { terminationRecordData.getCategoryId() });
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	} catch (Exception e) {
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public void nationalityLossChanged() {
	selectedTerminationRecordDetailData.setNationalityLossReason(null);
    }

    public void printForNotification(Long terminationRecordDetailId) {
	try {
	    TerminationTransactionData terminationTransactionData = TerminationsService.getTerminationTransactionByRecordDetailId(terminationRecordDetailId);
	    if (terminationTransactionData != null) {
		byte[] bytes = TerminationsService.getTerminationDecisionBytes(terminationTransactionData.getId(), mode, terminationTransactionData.getTransactionTypeCode());
		super.print(bytes);
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void desiredTerminationDateChange() {
	if (HijriDateService.hijriDateDiffByHijriYear(selectedTerminationRecordDetailData.getEmpRecruitmentDate(), selectedTerminationRecordDetailData.getDesiredTerminationDate()) < ETRConfigurationService.getTerminationsCiviliansServicePeriodRetirementYearsMin()) {
	    selectedTerminationRecordDetailData.setTerminationRequestType(TerminationRequestTypeEnum.RESIGNATION.getCode());
	    setResignation(true);
	} else {
	    selectedTerminationRecordDetailData.setTerminationRequestType(TerminationRequestTypeEnum.RETIREMENT.getCode());
	    setResignation(false);
	}
    }

    public String initProcess() {
	String internalCopiesString = EmployeesService.getEmployeesIdsString(internalCopies);
	terminationRecordData.setInternalCopies(internalCopiesString);
	try {
	    TerminationsWorkflow.initTermination(requester, wfTermination, terminationRecordData, originalTerminationRecordDetailDataList, processId, null, taskUrl);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveRE() {
	String internalCopiesString = EmployeesService.getEmployeesIdsString(internalCopies);
	terminationRecordData.setInternalCopies(internalCopiesString);
	try {
	    TerminationsWorkflow.doTerminationRE(requester, instance, wfTermination, terminationRecordData, originalTerminationRecordDetailDataList, currentTask, null, true);
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
	    TerminationsWorkflow.doTerminationRE(requester, instance, wfTermination, terminationRecordData, originalTerminationRecordDetailDataList, currentTask, attachments, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String signSM() {
	try {
	    TerminationsWorkflow.doTerminationSM(requester, instance, wfTermination, terminationRecordData, originalTerminationRecordDetailDataList, currentTask, CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.STE_TERMINATION.getCode(), TransactionClassesEnum.TERMINATIONS.getCode()).getId(), WFActionFlagsEnum.APPROVE.getCode());
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
	    TerminationsWorkflow.doTerminationSM(requester, instance, wfTermination, terminationRecordData, originalTerminationRecordDetailDataList, currentTask, CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.STE_TERMINATION.getCode(), TransactionClassesEnum.TERMINATIONS.getCode()).getId(), WFActionFlagsEnum.REJECT.getCode());
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
	    TerminationsWorkflow.doTerminationSM(requester, instance, wfTermination, terminationRecordData, originalTerminationRecordDetailDataList, currentTask, CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.STE_TERMINATION.getCode(), TransactionClassesEnum.TERMINATIONS.getCode()).getId(), WFActionFlagsEnum.RETURN_REVIEWER.getCode());
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

    public boolean getPrivilegesFlag() {
	return privilegesFlag;
    }

    public void setPrivilegesFlag(boolean privilegesFlag) {
	this.privilegesFlag = privilegesFlag;
    }

}
