package com.code.ui.backings.hcm.recruitments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

@ManagedBean(name = "soldiersRecruitment")
@ViewScoped
public class SoldiersRecruitment extends RecruitmentsBase implements Serializable {
    /**
     * mode: 0 for recruitments decisions(soldiers/first soldiers), 1 for recruitments decisions(corporal or higher), 2 for graduation letters
     **/
    private int recruitmentMode;

    private Long trainingInstituteOrCenterUnitId;
    private String trainingInstituteOrCenterUnitFullName;
    private String trainingInstituteOrCenterUnitHKey;
    private Long selectedRankId;
    private Long selectedDegreeId;
    private Long selectedRegionId;
    private Date recruitmentDate;
    private Integer qualificationLevelReward;

    public SoldiersRecruitment() {
	super.init();
	if (getRequest().getParameter("mode") != null) {
	    recruitmentMode = Integer.parseInt(getRequest().getParameter("mode"));
	    switch (recruitmentMode) {
	    case 0:
		setScreenTitle(getMessage("title_soldierAndFirstSoldierRecruitment"));
		this.processId = WFProcessesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_RECRUITMENT.getCode();
		break;
	    case 1:
		setScreenTitle(getMessage("title_corporalSoldierOrHigherRecruitment"));
		this.processId = WFProcessesEnum.SOLDIERS_CORPORAL_OR_HIGHER_RECRUITMENT.getCode();
		break;
	    case 2:
		setScreenTitle(getMessage("title_soldiersGraduationLetterDecision"));
		this.processId = WFProcessesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_RECRUITMENT.getCode();
		break;
	    default:
		setServerSideErrorMessages(getMessage("error_general"));
	    }
	} else
	    setServerSideErrorMessages(getMessage("error_general"));
	init();
    }

    public void init() {
	try {
	    List<Rank> allSoldiersRanks = CommonService.getRanks(null, new Long[] { CategoriesEnum.SOLDIERS.getCode() });
	    ranks = new ArrayList<Rank>();
	    for (Rank rank : allSoldiersRanks)
		if ((recruitmentMode != 1 && (rank.getId().equals(RanksEnum.SOLDIER.getCode()) || rank.getId().equals(RanksEnum.FIRST_SOLDIER.getCode()))) || (recruitmentMode == 1 && !(rank.getId().equals(RanksEnum.STUDENT_SOLDIER.getCode()) || rank.getId().equals(RanksEnum.SOLDIER.getCode()) || rank.getId().equals(RanksEnum.FIRST_SOLDIER.getCode()))))
		    ranks.add(rank);

	    if (recruitmentMode != 0)
		regions = CommonService.getAllRegions();
	    if (recruitmentMode != 2)
		degrees = PayrollsService.getAllDegrees();

	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		recruitmentsList = new ArrayList<WFRecruitmentData>();
		internalCopies = new ArrayList<EmployeeData>();
		externalCopies = RecruitmentsWorkFlow.getSoldiersRecruitmentExternalCopies();
		basedOnOrderDate = HijriDateService.getHijriSysDate();
		if (recruitmentMode == 1)
		    recruitmentDate = HijriDateService.getHijriSysDate();
	    } else {
		recruitmentsList = RecruitmentsWorkFlow.getWFRecruitmentsByInstanceId(instance.getInstanceId());
		selectedRegionId = recruitmentsList.get(0).getRecruitmentRegionId();
		selectedRankId = recruitmentsList.get(0).getEmpRecruitmentRankId();
		selectedDegreeId = recruitmentsList.get(0).getDegreeId();
		basedOnOrderNumber = recruitmentsList.get(0).getBasedOnOrderNumber();
		basedOnOrderDate = recruitmentsList.get(0).getBasedOnOrderDate();
		recruitmentDate = recruitmentsList.get(0).getRecruitmentDate();
		qualificationLevelReward = recruitmentsList.get(0).getQualificationLevelReward();
		trainingInstituteOrCenterUnitId = recruitmentsList.get(0).getRecTrainingUnitId();
		trainingInstituteOrCenterUnitFullName = recruitmentsList.get(0).getRecTrainingUnitFullName();
		trainingInstituteOrCenterUnitHKey = recruitmentsList.get(0).getRecTrainingUnitHKey();
		internalCopies = EmployeesService.getEmployeesByIdsString(recruitmentsList.get(0).getInternalCopies());
		externalCopies = recruitmentsList.get(0).getExternalCopies();
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void getDistributedSoldiers() {
	try {
	    recruitmentsList.addAll(RecruitmentsWorkFlow.getDistributedSoldiers(processId, selectedRegionId, trainingInstituteOrCenterUnitId, trainingInstituteOrCenterUnitHKey, selectedDegreeId, selectedRankId, recruitmentMode == 2 ? basedOnOrderDate : recruitmentDate, qualificationLevelReward, basedOnOrderNumber, basedOnOrderDate));
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
	    recruitmentsList.add(RecruitmentsWorkFlow.constructSoldiersWFRecruitment(processId, selectedEmpId, basedOnOrderNumber, basedOnOrderDate, selectedRegionId, selectedRankId, selectedDegreeId, recruitmentDate, qualificationLevelReward, null));
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

    public int getRecruitmentMode() {
	return recruitmentMode;
    }

    public void setRecruitmentMode(int recruitmentMode) {
	this.recruitmentMode = recruitmentMode;
    }

    public Long getTrainingInstituteOrCenterUnitId() {
	return trainingInstituteOrCenterUnitId;
    }

    public void setTrainingInstituteOrCenterUnitId(Long trainingInstituteOrCenterUnitId) {
	this.trainingInstituteOrCenterUnitId = trainingInstituteOrCenterUnitId;
    }

    public String getTrainingInstituteOrCenterUnitFullName() {
	return trainingInstituteOrCenterUnitFullName;
    }

    public void setTrainingInstituteOrCenterUnitFullName(String trainingInstituteOrCenterUnitFullName) {
	this.trainingInstituteOrCenterUnitFullName = trainingInstituteOrCenterUnitFullName;
    }

    public String getTrainingInstituteOrCenterUnitHKey() {
	return trainingInstituteOrCenterUnitHKey;
    }

    public void setTrainingInstituteOrCenterUnitHKey(String trainingInstituteOrCenterUnitHKey) {
	this.trainingInstituteOrCenterUnitHKey = trainingInstituteOrCenterUnitHKey;
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

    public Date getRecruitmentDate() {
	return recruitmentDate;
    }

    public void setRecruitmentDate(Date recruitmentDate) {
	this.recruitmentDate = recruitmentDate;
    }

    public String getRecruitmentDateSring() {
	return HijriDateService.getHijriDateString(recruitmentDate);
    }

    public Integer getQualificationLevelReward() {
	return qualificationLevelReward;
    }

    public void setQualificationLevelReward(Integer qualificationLevelReward) {
	this.qualificationLevelReward = qualificationLevelReward;
    }
}
