package com.code.ui.backings.hcm.recruitments;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.workflow.hcm.recruitments.WFRecruitmentData;
import com.code.enums.CategoriesEnum;
import com.code.enums.RanksEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.PayrollsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.RecruitmentsWorkFlow;

@ManagedBean(name = "inspectorsRecruitment")
@ViewScoped
public class InspectorsRecruitment extends RecruitmentsBase implements Serializable {

    private Long selectedRankId;
    private Long selectedDegreeId;
    private Long selectedRegionId;
    private Integer qualificationLevelReward;

    public InspectorsRecruitment() {
	super.init();
	setScreenTitle(getMessage("title_inspectorsRecruitment"));
	this.processId = WFProcessesEnum.INSPECTORS_RECRUITMENT.getCode();
	init();
    }

    public void init() {
	try {
	    ranks = CommonService.getRanks(null, new Long[] { CategoriesEnum.SOLDIERS.getCode() });
	    for (Rank rank : ranks)
		if (rank.getId().equals(RanksEnum.STUDENT_SOLDIER.getCode())) {
		    ranks.remove(rank);
		    break;
		}

	    degrees = PayrollsService.getAllDegrees();
	    regions = CommonService.getAllRegions();

	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		recruitmentsList = new ArrayList<WFRecruitmentData>();
		internalCopies = new ArrayList<EmployeeData>();
		externalCopies = RecruitmentsWorkFlow.getSoldiersRecruitmentExternalCopies();
		basedOnOrderDate = HijriDateService.getHijriSysDate();

	    } else {
		recruitmentsList = RecruitmentsWorkFlow.getWFRecruitmentsByInstanceId(instance.getInstanceId());
		selectedRegionId = recruitmentsList.get(0).getRecruitmentRegionId();
		selectedRankId = recruitmentsList.get(0).getEmpRecruitmentRankId();
		selectedDegreeId = recruitmentsList.get(0).getDegreeId();
		basedOnOrderNumber = recruitmentsList.get(0).getBasedOnOrderNumber();
		basedOnOrderDate = recruitmentsList.get(0).getBasedOnOrderDate();
		qualificationLevelReward = recruitmentsList.get(0).getQualificationLevelReward();
		internalCopies = EmployeesService.getEmployeesByIdsString(recruitmentsList.get(0).getInternalCopies());
		externalCopies = recruitmentsList.get(0).getExternalCopies();
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addNewRecruitment() {
	try {
	    if (!validateEmployeesDuplicate()) {
		setServerSideErrorMessages(getMessage("error_duplicateEmp"));
		return;
	    }
	    recruitmentsList.add(RecruitmentsWorkFlow.constructSoldiersWFRecruitment(processId, selectedEmpId, basedOnOrderNumber, basedOnOrderDate, selectedRegionId, selectedRankId, selectedDegreeId, null, qualificationLevelReward, null));
	    selectedEmpId = null;
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addRecruitmentJob(WFRecruitmentData r) {
	if (!validateJobsConflict(r)) {
	    setServerSideErrorMessages(getMessage("error_recruitmentJobRepeated"));
	    return;
	}
    }

    public void deleteRecruitment(WFRecruitmentData rec) {
	try {

	    if (this.role.equals(WFTaskRolesEnum.REVIEWER_EMP.getCode())) {

		if (recruitmentsList.size() == 1)
		    throw new BusinessException("error_recruitmentListEmpty");

		boolean isAllNewRecords = true;
		for (WFRecruitmentData wfRecruitmentData : recruitmentsList) {
		    if (wfRecruitmentData.getInstanceId() != null && !wfRecruitmentData.getEmployeeId().equals(rec.getEmployeeId())) {
			isAllNewRecords = false;
			break;
		    }
		}

		if (isAllNewRecords)
		    throw new BusinessException("error_saveRecordsBeforeDeleting");
	    }

	    if (rec.getInstanceId() != null)
		RecruitmentsWorkFlow.deleteWFRecruitment(rec);

	    recruitmentsList.remove(rec);

	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public Long getSelectedRankId() {
	return selectedRankId;
    }

    public void setSelectedRankId(Long selectedRankId) {
	this.selectedRankId = selectedRankId;
    }

    public Long getSelectedDegreeId() {
	return selectedDegreeId;
    }

    public void setSelectedDegreeId(Long selectedDegreeId) {
	this.selectedDegreeId = selectedDegreeId;
    }

    public Long getSelectedRegionId() {
	return selectedRegionId;
    }

    public void setSelectedRegionId(Long selectedRegionId) {
	this.selectedRegionId = selectedRegionId;
    }

    public Integer getQualificationLevelReward() {
	return qualificationLevelReward;
    }

    public void setQualificationLevelReward(Integer qualificationLevelReward) {
	this.qualificationLevelReward = qualificationLevelReward;
    }
}
