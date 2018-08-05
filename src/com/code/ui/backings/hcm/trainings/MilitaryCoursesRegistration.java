package com.code.ui.backings.hcm.trainings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.trainings.TrainingCourseData;
import com.code.enums.CategoriesEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.MilitaryCivillianEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.SpecializationsService;
import com.code.services.hcm.TrainingCoursesService;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.security.SecurityService;
import com.code.services.util.CommonService;
import com.code.services.workflow.hcm.TrainingEmployeesWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "militaryCoursesRegistration")
@ViewScoped
public class MilitaryCoursesRegistration extends BaseBacking implements Serializable {

    private TrainingCourseData trainingCourseData;
    private String qualificationLevelsDesc;
    private String minorSpecializationDesc;

    private List<Rank> ranks;
    private List<Category> employeesCategories;

    private boolean viewAdmin = false;
    private boolean modifyAndInsertAdmin = false;
    private boolean modifySyllabusAttachmentsAdmin = false;

    private String originalCourseName;

    public MilitaryCoursesRegistration() {
	try {
	    setScreenTitle(getMessage("title_militaryCoursesRegistration"));
	    resetForm();

	    String idParam = getRequest().getParameter("trainingCourseId");
	    if (idParam != null) {
		long trainingCourseId = Long.parseLong(idParam);
		trainingCourseData = TrainingCoursesService.getTrainingCoursesById(trainingCourseId);
		originalCourseName = trainingCourseData.getName();

		getCategoryRanks();

		if (trainingCourseData.getQualificationLevelIds() != null)
		    qualificationLevelsDesc = generateQualificationLevelsDesc();
		if (trainingCourseData.getMinorSpecializationIds() != null)
		    minorSpecializationDesc = generateMinorSpecializationDesc();

		if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.TRN_MILITARY_COURSES_REGISTERATION.getCode(), MenuActionsEnum.TRN_MILITARY_COURSES_INSERT_MODIFY.getCode()))
		    modifyAndInsertAdmin = true;
		else
		    viewAdmin = true;

	    } else if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.TRN_MILITARY_COURSES_REGISTERATION.getCode(), MenuActionsEnum.TRN_MILITARY_COURSES_INSERT_MODIFY.getCode())) {
		modifyAndInsertAdmin = true;
	    }
	    modifySyllabusAttachmentsAdmin = SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.TRN_MILITARY_COURSES_REGISTERATION.getCode(), MenuActionsEnum.TRN_MILITARY_COURSES_INSERT_MODIFY_ATTACHMENTS.getCode());
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void clearQualificationLevel() {
	trainingCourseData.setQualificationLevelIds("");

    }

    public void clearMinorSpec() {
	trainingCourseData.setMinorSpecializationIds("");
    }

    private String generateQualificationLevelsDesc() throws BusinessException {
	return TrainingSetupService.getQualificationLevelsDescStringByIdsString(trainingCourseData.getQualificationLevelIds());
    }

    private String generateMinorSpecializationDesc() throws BusinessException {
	return SpecializationsService.getMinorSpecializationsDescStringByIdsString(trainingCourseData.getMinorSpecializationIds());
    }

    public void categoryChangeListener() {
	try {
	    getCategoryRanks();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void ranksChangeListener() {
	if (trainingCourseData.getFromRankId() == null) // all
	    trainingCourseData.setToRankId(null);

	if (trainingCourseData.getFromRankId() != null && trainingCourseData.getToRankId() == null)
	    trainingCourseData.setToRankId(trainingCourseData.getFromRankId());
    }

    public void getCategoryRanks() throws BusinessException {
	if (trainingCourseData.getCategoryId() != null) {
	    if (trainingCourseData.getCategoryId() == CategoriesEnum.OFFICERS.getCode()) {
		setRanks(CommonService.getRanks(null, new Long[] { CategoriesEnum.OFFICERS.getCode() }));
		ranks.remove(ranks.size() - 1); // to remove CADET from the list
	    } else if (trainingCourseData.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()) {
		setRanks(CommonService.getRanks(null, new Long[] { CategoriesEnum.SOLDIERS.getCode() }));
		ranks.remove(ranks.size() - 1); // to remove STUDENT_SOLDIER from the list
	    }
	} else {
	    ranks = new ArrayList<>();
	}
    }

    public void resetForm() throws BusinessException {
	trainingCourseData = new TrainingCourseData();
	trainingCourseData.setType(MilitaryCivillianEnum.Military.getCode());
	qualificationLevelsDesc = null;
	minorSpecializationDesc = null;
	ranks = new ArrayList<>();
	employeesCategories = Arrays.asList(CommonService.getCategoryById(CategoriesEnum.OFFICERS.getCode()), CommonService.getCategoryById(CategoriesEnum.SOLDIERS.getCode()));
	getCategoryRanks();
    }

    public void save() {
	try {
	    TrainingEmployeesWorkFlow.validateTrainingCourseWFBusinessRules(trainingCourseData, originalCourseName, false);

	    trainingCourseData.getTrainingCourse().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.

	    if (trainingCourseData.getId() == null) {
		TrainingCoursesService.addTrainingCourse(trainingCourseData);
	    } else {
		TrainingCoursesService.updateTrainingCourse(trainingCourseData, originalCourseName);
	    }

	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public List<Category> getEmployeesCategories() {
	return employeesCategories;
    }

    public void setEmployeesCategories(List<Category> employeesCategories) {
	this.employeesCategories = employeesCategories;
    }

    public List<Rank> getRanks() {
	return ranks;
    }

    public void setRanks(List<Rank> ranks) {
	this.ranks = ranks;
    }

    public TrainingCourseData gettrainingCourseData() {
	return trainingCourseData;
    }

    public void settrainingCourseData(TrainingCourseData trainingCourseData) {
	this.trainingCourseData = trainingCourseData;
    }

    public String getQualificationLevelsDesc() {
	return qualificationLevelsDesc;
    }

    public void setQualificationLevelsDesc(String qualificationLevelsDesc) {
	this.qualificationLevelsDesc = qualificationLevelsDesc;
    }

    public String getMinorSpecializationDesc() {
	return minorSpecializationDesc;
    }

    public void setMinorSpecializationDesc(String minorSpecializationDesc) {
	this.minorSpecializationDesc = minorSpecializationDesc;
    }

    public boolean isViewAdmin() {
	return viewAdmin;
    }

    public boolean isModifyAndInsertAdmin() {
	return modifyAndInsertAdmin;
    }

    public boolean isModifySyllabusAttachmentsAdmin() {
	return modifySyllabusAttachmentsAdmin;
    }

}
