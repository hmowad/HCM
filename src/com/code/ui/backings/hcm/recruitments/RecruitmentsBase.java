package com.code.ui.backings.hcm.recruitments;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.hcm.payroll.Degree;
import com.code.dal.orm.hcm.recruitments.RecruitmentTransactionData;
import com.code.dal.orm.workflow.hcm.recruitments.WFRecruitmentData;
import com.code.enums.NavigationEnum;
import com.code.enums.RecruitmentTypeEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.RecruitmentsService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.RecruitmentsWorkFlow;
import com.code.ui.backings.base.WFBaseBacking;

public class RecruitmentsBase extends WFBaseBacking implements Serializable {

    protected List<WFRecruitmentData> recruitmentsList;
    protected String basedOnOrderNumber;
    protected Date basedOnOrderDate;
    protected List<Rank> ranks;
    protected List<Degree> degrees;
    protected List<Region> regions;
    protected Long selectedEmpId;
    protected List<EmployeeData> internalCopies;
    protected String externalCopies;

    protected WFRecruitmentData selectedRec;

    protected int pageSize = 10;

    protected boolean validateEmployeesDuplicate() {
	for (WFRecruitmentData rec : recruitmentsList) {
	    if (rec.getEmployeeId() != null && selectedEmpId != null && rec.getEmployeeId().longValue() == selectedEmpId.longValue())
		return false;
	}
	return true;
    }

    protected boolean validateJobsConflict(WFRecruitmentData recruitment) {
	for (WFRecruitmentData rec : recruitmentsList) {
	    if (!recruitment.equals(rec) && rec.getJobId() != null && recruitment.getJobId() != null && rec.getJobId().longValue() == recruitment.getJobId().longValue())
		return false;
	}
	return true;
    }

    protected void setRankDescription() {
	if (selectedRec.getRankId() != null) {
	    for (Rank r : ranks) {
		if (selectedRec.getRankId().equals(r.getId())) {
		    selectedRec.setRankDescription(r.getDescription());
		    break;
		}
	    }
	}
    }

    protected void setDegree() {
	if (selectedRec.getDegreeId() != null) {
	    for (Degree d : degrees) {
		if (selectedRec.getDegreeId().equals(d.getId())) {
		    selectedRec.setDegreeDescription(d.getDescription());
		    break;
		}
	    }
	}
    }

    public void selectNewRecruitment(WFRecruitmentData r) {
	selectedRec = r;
	selectedEmpId = r.getEmployeeId();
    }

    public String initProcess() {
	try {
	    RecruitmentsWorkFlow.initRecruitment(requester, recruitmentsList, processId, attachments, taskUrl, internalCopies, externalCopies);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String sendSRE() {
	try {
	    RecruitmentsWorkFlow.doRecruitmentSRE(requester, instance, processId, recruitmentsList, currentTask, internalCopies, externalCopies, attachments);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String signRM() {
	try {
	    RecruitmentsWorkFlow.doRecruitmentSM(requester, instance, recruitmentsList, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String modifyRM() {
	try {
	    RecruitmentsWorkFlow.doRecruitmentSM(requester, instance, recruitmentsList, currentTask, WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    public String rejectRM() {
	try {
	    RecruitmentsWorkFlow.doRecruitmentSM(requester, instance, recruitmentsList, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    public String reviewRecruitments() {
	try {
	    RecruitmentsWorkFlow.doRecruitmentRE(requester, recruitmentsList, instance, currentTask, attachments, internalCopies, externalCopies, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectRE() {
	try {
	    RecruitmentsWorkFlow.doRecruitmentRE(requester, recruitmentsList, instance, currentTask, attachments, internalCopies, externalCopies, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    public String closeProcess() {
	try {
	    RecruitmentsWorkFlow.closeWFInstanceByNotification(instance, currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    public void print() {
	try {
	    WFRecruitmentData rec = recruitmentsList.get(0);
	    List<RecruitmentTransactionData> recTrans = RecruitmentsService.getRecruitmentTransactionsByEmployeeIdAndRecDateAndRecTypes(rec.getEmployeeId(), rec.getRecruitmentDate(), rec.getRecruitmentDate(),
		    (instance.getProcessId().longValue() == WFProcessesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_RECRUITMENT.getCode() || instance.getProcessId().longValue() == WFProcessesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_EXCEPTIONAL_RECRUITMENT.getCode()) ? new Integer[] { RecruitmentTypeEnum.GRADUATION_LETTER.getCode() } : new Integer[] { RecruitmentTypeEnum.RECRUITMENT.getCode() });
	    if (recTrans.size() > 0) {
		RecruitmentTransactionData recTran = recTrans.get(0);
		byte[] bytes = RecruitmentsService.getRecruitmentDecisionBytes(recTran);
		super.print(bytes);
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<WFRecruitmentData> getRecruitmentsList() {
	return recruitmentsList;
    }

    public void setRecruitmentsList(List<WFRecruitmentData> recruitmentsList) {
	this.recruitmentsList = recruitmentsList;
    }

    public String getBasedOnOrderNumber() {
	return basedOnOrderNumber;
    }

    public void setBasedOnOrderNumber(String basedOnOrderNumber) {
	this.basedOnOrderNumber = basedOnOrderNumber;
    }

    public Date getBasedOnOrderDate() {
	return basedOnOrderDate;
    }

    public void setBasedOnOrderDate(Date basedOnOrderDate) {
	this.basedOnOrderDate = basedOnOrderDate;
    }

    public String getBasedOnOrderDateString() {
	return HijriDateService.getHijriDateString(basedOnOrderDate);
    }

    public List<Rank> getRanks() {
	return ranks;
    }

    public List<Degree> getDegrees() {
	return degrees;
    }

    public List<Region> getRegions() {
	return regions;
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

    public String getExternalCopies() {
	return externalCopies;
    }

    public void setExternalCopies(String externalCopies) {
	this.externalCopies = externalCopies;
    }

    public WFRecruitmentData getSelectedRec() {
	return selectedRec;
    }

    public void setSelectedRec(WFRecruitmentData selectedRec) {
	this.selectedRec = selectedRec;
    }

    public int getPageSize() {
	return pageSize;
    }
}
