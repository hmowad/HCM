package com.code.ui.backings.hcm.recruitments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.GraduationGroupPlace;
import com.code.dal.orm.hcm.RankTitle;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.workflow.hcm.recruitments.WFRecruitmentData;
import com.code.enums.CategoryModesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.RanksEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.util.CommonService;
import com.code.services.workflow.hcm.RecruitmentsWorkFlow;

@ManagedBean(name = "officersRecruitment")
@ViewScoped
public class OfficersRecruitment extends RecruitmentsBase implements Serializable {
    private List<RankTitle> ranksTitles;
    private List<GraduationGroupPlace> graduationGroupPlaces;
    private Integer graduationGroupNumber;
    private Integer graduationGroupPlace;
    private Long recruitmentRegionId;

    public OfficersRecruitment() {
	super.init();
	setScreenTitle(getMessage("title_officersRecruitment"));
	this.processId = WFProcessesEnum.OFFICERS_RECRUITMENT.getCode();
	this.init();
    }

    public void init() {
	try {
	    regions = CommonService.getAllRegions();
	    ranks = CommonService.getRanks(null, getCategoriesIdsArrayByMode(CategoryModesEnum.OFFICERS.getCode()));
	    for (int i = 0; i < ranks.size(); i++) {
		if (ranks.get(i).getId() == RanksEnum.CADET.getCode())
		    ranks.remove(i);
	    }
	    ranksTitles = CommonService.getAllRanksTitles();
	    graduationGroupPlaces = CommonService.getAllGraduationGroupPlaces();
	    selectedRec = new WFRecruitmentData();
	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		recruitmentsList = new ArrayList<WFRecruitmentData>();
		internalCopies = new ArrayList<EmployeeData>();
	    } else {
		recruitmentsList = RecruitmentsWorkFlow.getWFRecruitmentsByInstanceIdAndRecRegionId(instance.getInstanceId(), this.role.equals(WFTaskRolesEnum.SECONDARY_REVIEWER_EMP.getCode()) ? currentEmployee.getPhysicalRegionId() : FlagsEnum.ALL.getCode());
		graduationGroupNumber = recruitmentsList.get(0).getGraduationGroupNumber();
		graduationGroupPlace = recruitmentsList.get(0).getGraduationGroupPlace();

		recruitmentRegionId = recruitmentsList.get(0).getRecruitmentRegionId();
		for (int i = 1; i < recruitmentsList.size(); i++) {
		    if (recruitmentsList.get(i).getRecruitmentRegionId() != recruitmentRegionId) {
			recruitmentRegionId = (long) FlagsEnum.ALL.getCode();
			break;
		    }
		}

		basedOnOrderNumber = recruitmentsList.get(0).getBasedOnOrderNumber();
		basedOnOrderDate = recruitmentsList.get(0).getBasedOnOrderDate();
		internalCopies = EmployeesService.getEmployeesByIdsString(recruitmentsList.get(0).getInternalCopies());
		externalCopies = recruitmentsList.get(0).getExternalCopies();
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void getDistributedOfficers() {
	try {
	    recruitmentsList = RecruitmentsWorkFlow.getDistributedOfficers(processId, recruitmentRegionId, graduationGroupNumber, graduationGroupPlace, basedOnOrderNumber, basedOnOrderDate);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addNewRecruitment() {
	try {
	    if (!validateEmployeesDuplicate()) {
		setServerSideErrorMessages(getMessage("error_duplicateEmp"));
		return;
	    }
	    selectedRec = RecruitmentsWorkFlow.constructOfficersWFRecruitment(processId, selectedEmpId, basedOnOrderNumber, basedOnOrderDate);
	    recruitmentsList.add(selectedRec);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void setDegree() {
	selectedRec.setDegreeId(new Long((selectedRec.getSeniorityMonths() % 12) + 1));
    }

    public void saveWFRecruitment() {
	if (!validateJobsConflict(selectedRec)) {
	    setServerSideErrorMessages(getMessage("error_recruitmentJobRepeated"));
	    return;
	}

	setDegree();
	setRankDescription();
	setRankTitle();
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
	    if (selectedRec.equals(rec)) {
		selectedRec = new WFRecruitmentData();
		selectedEmpId = null;
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public String getConcatenatedPeriod(WFRecruitmentData wfRecruitment) {
	StringBuilder period = new StringBuilder();
	if (wfRecruitment.getSeniorityMonths() != null && wfRecruitment.getSeniorityMonths() != 0) {
	    period.append(wfRecruitment.getSeniorityMonths() + " " + getMessage("label_month") + " ");
	}
	if (wfRecruitment.getSeniorityDays() != null && wfRecruitment.getSeniorityDays() != 0)
	    period.append(wfRecruitment.getSeniorityDays() + " " + getMessage("label_day"));
	return period.toString();
    }

    private void setRankTitle() {
	if (selectedRec.getRankTitleId() != null) {
	    selectedRec.setRankTitleDesc(CommonService.getRankTitleById(selectedRec.getRankTitleId()).getDescription());
	} else {
	    selectedRec.setRankTitleDesc(null);
	}
    }

    public List<RankTitle> getRanksTitles() {
	return ranksTitles;
    }

    public List<GraduationGroupPlace> getGraduationGroupPlaces() {
	return graduationGroupPlaces;
    }

    public Integer getGraduationGroupNumber() {
	return graduationGroupNumber;
    }

    public void setGraduationGroupNumber(Integer graduationGroupNumber) {
	this.graduationGroupNumber = graduationGroupNumber;
    }

    public Integer getGraduationGroupPlace() {
	return graduationGroupPlace;
    }

    public void setGraduationGroupPlace(Integer graduationGroupPlace) {
	this.graduationGroupPlace = graduationGroupPlace;
    }

    public Long getRecruitmentRegionId() {
	return recruitmentRegionId;
    }

    public void setRecruitmentRegionId(Long recruitmentRegionId) {
	this.recruitmentRegionId = recruitmentRegionId;
    }
}
