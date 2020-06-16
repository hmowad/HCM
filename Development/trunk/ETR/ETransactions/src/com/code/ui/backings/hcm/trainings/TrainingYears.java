package com.code.ui.backings.hcm.trainings;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingYear;
import com.code.enums.FlagsEnum;
import com.code.enums.TrainingYearStatusEnum;
import com.code.enums.TrainingYearTypesEnum;
import com.code.enums.WFTaskActionsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.services.hcm.TrainingSetupService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "trainingYears")
@ViewScoped
public class TrainingYears extends BaseBacking {
    private int statusInitialDraft = TrainingYearStatusEnum.INITIAL_DRAFT.getCode();
    private int statusFinalDraft = TrainingYearStatusEnum.FINAL_DRAFT.getCode();
    private int statusApproved = TrainingYearStatusEnum.TRAINING_PLAN_APPROVED.getCode();
    private List<TrainingYear> years = null;

    private boolean disableAddButton = false;
    private long trainingYearTypeId = FlagsEnum.ALL.getCode();

    public TrainingYears() {
	try {
	    setScreenTitle(getMessage("title_trainingYears"));
	    years = TrainingSetupService.getAllTrainingYears();
	    TrainingYear unApprovedTrainingYear = TrainingSetupService.getUnApprovedTrainingYear();
	    if (unApprovedTrainingYear != null) {
		if (unApprovedTrainingYear.getRelatedYearId() == null)
		    trainingYearTypeId = TrainingYearTypesEnum.NEW_YEAR_WITH_NO_SEMESTERS.getCode();
		else if (unApprovedTrainingYear.getRelatedYearId().longValue() == unApprovedTrainingYear.getId().longValue())
		    trainingYearTypeId = TrainingYearTypesEnum.NEW_SEMESTER_IN_NEW_TRAINING_YEAR.getCode();
		else {
		    trainingYearTypeId = TrainingYearTypesEnum.NEW_SEMESTER_IN_EXISTING_TRAINING_YEAR.getCode();
		}

		disableAddButton = true;
	    }
	} catch (BusinessException e) {
	    e.printStackTrace();
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void add() {
	try {

	    if (trainingYearTypeId == FlagsEnum.ALL.getCode()) {
		throw new BusinessException("error_chooseTrainingYearTypeMandatory");
	    }

	    if (TrainingSetupService.getUnApprovedTrainingYear() != null) {
		throw new BusinessException("error_TrnMoreThanOneUnApprovedPlan");
	    }

	    if (trainingYearTypeId == TrainingYearTypesEnum.NEW_SEMESTER_IN_EXISTING_TRAINING_YEAR.getCode() && TrainingSetupService.getAllTrainingYears().isEmpty()) {
		throw new BusinessException("error_noExistingTrainingYear");
	    }

	    TrainingYear lastTrainingYear = TrainingSetupService.getLastApprovedTrainingYear();

	    if (lastTrainingYear != null && lastTrainingYear.getRelatedYearId() == null && trainingYearTypeId == TrainingYearTypesEnum.NEW_SEMESTER_IN_EXISTING_TRAINING_YEAR.getCode()) {
		throw new BusinessException("error_cannotAddSemesterToTrainingYear");
	    }

	    TrainingYear year = TrainingSetupService.constructTrainingYear();

	    // set related year id if needed
	    if (trainingYearTypeId == TrainingYearTypesEnum.NEW_SEMESTER_IN_EXISTING_TRAINING_YEAR.getCode()) {
		year.setRelatedYearId(lastTrainingYear.getRelatedYearId());
	    }

	    // generate semester name if needed
	    if (trainingYearTypeId == TrainingYearTypesEnum.NEW_SEMESTER_IN_NEW_TRAINING_YEAR.getCode()) {
		year.setSemesterName(generateSemesterName(0, true));
		year.setSemesterNameEnglish(generateSemesterName(0, false));
	    }
	    if (trainingYearTypeId == TrainingYearTypesEnum.NEW_SEMESTER_IN_EXISTING_TRAINING_YEAR.getCode()) {
		year.setSemesterName(generateSemesterName(TrainingSetupService.countNumberOfSemesterInTrainingYear(lastTrainingYear.getRelatedYearId()), true));
		year.setSemesterNameEnglish(generateSemesterName(TrainingSetupService.countNumberOfSemesterInTrainingYear(lastTrainingYear.getRelatedYearId()), false));
	    }

	    years.add(year);
	    disableAddButton = true;
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void save(TrainingYear year) {
	try {
	    year.setSystemUser(loginEmpData.getEmpId() + "");

	    if (year.getId() == null)
		TrainingSetupService.addTrainingYear(year, trainingYearTypeId);
	    else
		TrainingSetupService.updateTrainingYear(year, trainingYearTypeId);

	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public String generateSemesterName(long lastSemesterNumber, Boolean arabicFlag) {
	try {

	    String semesterName = getMessage(String.format("%s%d", arabicFlag ? "label_semester_" : "label_semesterEnglish_", lastSemesterNumber + 1));
	    return semesterName == null ? "" : semesterName;
	} catch (Exception e) {
	    return "";
	}
    }

    public void printTrainingYearDrafts(TrainingYear year) {
	try {
	    byte[] bytes = null;
	    bytes = TrainingEmployeesService.getTrainingYearDraftsBytes(year.getId(), year.getStatus(), WFTaskActionsEnum.REJECT.getCode());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public int getStatusInitialDraft() {
	return statusInitialDraft;
    }

    public int getStatusFinalDraft() {
	return statusFinalDraft;
    }

    public int getStatusApproved() {
	return statusApproved;
    }

    public List<TrainingYear> getYears() {
	return years;
    }

    public void setYears(List<TrainingYear> years) {
	this.years = years;
    }

    public long getTrainingYearTypeId() {
	return trainingYearTypeId;
    }

    public void setTrainingYearTypeId(long trainingYearTypeId) {
	this.trainingYearTypeId = trainingYearTypeId;
    }

    public boolean isDisableAddButton() {
	return disableAddButton;
    }

    public void setDisableAddButton(boolean disableAddButton) {
	this.disableAddButton = disableAddButton;
    }

}
