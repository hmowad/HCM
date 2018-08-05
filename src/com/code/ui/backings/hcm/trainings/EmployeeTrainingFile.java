package com.code.ui.backings.hcm.trainings;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingTransactionData;
import com.code.dal.orm.hcm.trainings.TrainingTransactionDetailData;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.services.security.SecurityService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "employeeTrainingFile")
@ViewScoped
public class EmployeeTrainingFile extends BaseBacking {
    private boolean isModifyAdmin;
    private int pageSize = 5;
    private long employeeId;
    private String employeeName;

    private int internalMilitaryCoursesStatus;
    private int externalMilitaryCoursesStatus;

    private List<TrainingTransactionData> internalMilitaryCourses;
    private List<TrainingTransactionData> externalMilitaryCourses;
    private List<TrainingTransactionData> scholarships;
    private List<TrainingTransactionDetailData> scholarshipDetails;
    private List<TrainingTransactionData> studyEnrollments;
    private List<TrainingTransactionData> morningCourses;
    private List<TrainingTransactionData> eveningCourses;
    private boolean deleteClaimAdmin;

    public EmployeeTrainingFile() {
	try {
	    internalMilitaryCoursesStatus = externalMilitaryCoursesStatus = FlagsEnum.ALL.getCode();
	    employeeId = this.loginEmpData.getEmpId();
	    employeeName = this.loginEmpData.getName();
	    isModifyAdmin = SecurityService.isEmployeeMenuActionGranted(employeeId, MenuCodesEnum.TRN_EMPLOYEE_TRAINING_FILE.getCode(), MenuActionsEnum.TRN_EMPLOYEE_TRAINING_FILE_MODIFY.getCode());
	    deleteClaimAdmin = SecurityService.isEmployeeMenuActionGranted(employeeId, MenuCodesEnum.TRN_EMPLOYEE_TRAINING_FILE.getCode(), MenuActionsEnum.TRN_EMPLOYEE_TRAINING_FILE_DELETE_CLAIM.getCode());
	    getEmployeeTrainingFileData();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public String successIndicatorDetails(TrainingTransactionData transactionData) {
	StringBuilder details = new StringBuilder();
	if (transactionData.getTrainingTypeId() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode() || transactionData.getTrainingTypeId() == TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode()) {

	    details.append(transactionData.getSuccessRanking() == null ? "" : getMessage("label_theRanking") + " : " + transactionData.getSuccessRankingDesc() + "\n");

	    details.append(getMessage("label_qualificationGradePercentage")).append(" : ").append(transactionData.getQualificationGradePercentage() != null ? transactionData.getQualificationGradePercentage() + " % " : "")
		    .append(transactionData.getQualificationGrade() == 1 ? getMessage("label_excellent") : (transactionData.getQualificationGrade() == 2 ? getMessage("label_veryGood") : (transactionData.getQualificationGrade() == 3 ? getMessage("label_good") : (transactionData.getQualificationGrade() == 4 ? getMessage("label_accepted") : getMessage("label_notAvailable"))))).append("\n");

	    details.append(getMessage("label_attendanceGradePercentage")).append(" : ").append(transactionData.getAttendanceGradePercentage() != null ? transactionData.getAttendanceGradePercentage() + " % " : "")
		    .append(transactionData.getAttendanceGrade() == 1 ? getMessage("label_excellent") : (transactionData.getAttendanceGrade() == 2 ? getMessage("label_veryGood") : (transactionData.getAttendanceGrade() == 3 ? getMessage("label_good") : (transactionData.getAttendanceGrade() == 4 ? getMessage("label_accepted") : getMessage("label_notAvailable"))))).append("\n");

	    details.append(getMessage("label_behaviourGradePercentage")).append(" : ").append(transactionData.getBehaviorGradePercentage() != null ? transactionData.getBehaviorGradePercentage() + " % " : "")
		    .append(transactionData.getBehaviorGrade() == 1 ? getMessage("label_excellent") : (transactionData.getBehaviorGrade() == 2 ? getMessage("label_veryGood") : (transactionData.getBehaviorGrade() == 3 ? getMessage("label_good") : (transactionData.getBehaviorGrade() == 4 ? getMessage("label_accepted") : getMessage("label_notAvailable"))))).append("\n");

	} else { // scholarship , Morning ,evening , Study enrollment
	    details.append(transactionData.getQualificationGrade() == null ? "" : getMessage("label_evaluation") + " : " + (transactionData.getQualificationGrade() == 1 ? getMessage("label_excellent") : (transactionData.getQualificationGrade() == 2 ? getMessage("label_veryGood") : (transactionData.getQualificationGrade() == 3 ? getMessage("label_good") : (transactionData.getQualificationGrade() == 4 ? getMessage("label_accepted") : getMessage("label_notAvailable"))))));
	}

	return details.toString();
    }

    public boolean showSelectEmployee() {
	return isModifyAdmin || (this.loginEmpData.getIsManager() == FlagsEnum.ON.getCode());
    }

    public void updateTrainingTransaction(TrainingTransactionData transaction) {
	try {
	    TrainingEmployeesService.updateTrainingTransactionJoiningDate(transaction.getId(), transaction.getJoiningDate(), this.loginEmpData.getEmpId());
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void deleteTrainingTransactionClaim(TrainingTransactionData transaction) {
	try {
	    TrainingEmployeesService.deleteTrainingTransactionClaim(transaction.getId(), this.loginEmpData.getEmpId());
	    if (transaction.getTrainingTypeId() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode())
		internalMilitaryCourses.remove(transaction);
	    else if (transaction.getTrainingTypeId() == TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode())
		externalMilitaryCourses.remove(transaction);
	    else if (transaction.getTrainingTypeId() == TrainingTypesEnum.SCHOLARSHIP.getCode())
		scholarships.remove(transaction);
	    else if (transaction.getTrainingTypeId() == TrainingTypesEnum.MORNING_COURSE.getCode())
		morningCourses.remove(transaction);
	    else if (transaction.getTrainingTypeId() == TrainingTypesEnum.EVENING_COURSE.getCode())
		eveningCourses.remove(transaction);
	    else if (transaction.getTrainingTypeId() == TrainingTypesEnum.STUDY_ENROLLMENT.getCode())
		studyEnrollments.remove(transaction);

	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void getEmployeeTrainingFileData() {
	fetchInternalMilitaryCourses();
	fetchExternalMilitaryCourses();
	fetchScholarships();
	fetchStudyEnrollments();
	fetchMorningCourses();
	fetchEveningCourses();
    }

    public void fetchInternalMilitaryCourses() {
	try {
	    internalMilitaryCourses = TrainingEmployeesService.getTrainingTransactionsData(new Long[] { TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode() }, internalMilitaryCoursesStatus == FlagsEnum.ALL.getCode() ? null : new Integer[] { internalMilitaryCoursesStatus }, FlagsEnum.ALL.getCode(), employeeId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void fetchExternalMilitaryCourses() {
	try {
	    externalMilitaryCourses = TrainingEmployeesService.getTrainingTransactionsData(new Long[] { TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode() }, externalMilitaryCoursesStatus == FlagsEnum.ALL.getCode() ? null : new Integer[] { externalMilitaryCoursesStatus }, FlagsEnum.ALL.getCode(), employeeId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void fetchScholarships() {
	try {
	    scholarships = TrainingEmployeesService.getTrainingTransactionsData(new Long[] { TrainingTypesEnum.SCHOLARSHIP.getCode() }, null, FlagsEnum.ALL.getCode(), employeeId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
	    if (scholarships.size() > 0)
		fetchScholarshipDetails(scholarships.get(0));
	    else
		scholarshipDetails = new ArrayList<TrainingTransactionDetailData>();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void fetchScholarshipDetails(TrainingTransactionData scholarship) {
	try {
	    scholarshipDetails = TrainingEmployeesService.getTrainingTransactionDetailDataByTrainingTransactionId(scholarship.getId());
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void fetchMorningCourses() {
	try {
	    morningCourses = TrainingEmployeesService.getTrainingTransactionsData(new Long[] { TrainingTypesEnum.MORNING_COURSE.getCode() }, null, FlagsEnum.ALL.getCode(), employeeId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void fetchEveningCourses() {
	try {
	    eveningCourses = TrainingEmployeesService.getTrainingTransactionsData(new Long[] { TrainingTypesEnum.EVENING_COURSE.getCode() }, null, FlagsEnum.ALL.getCode(), employeeId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void fetchStudyEnrollments() {
	try {
	    studyEnrollments = TrainingEmployeesService.getTrainingTransactionsData(new Long[] { TrainingTypesEnum.STUDY_ENROLLMENT.getCode() }, null, FlagsEnum.ALL.getCode(), employeeId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printTraineeCourseCancellationDecision(long trainingTransactionId) {
	try {
	    long trainingTransactionDetailId = TrainingEmployeesService.getTrainingTransactionDetailDataByTrainingTransactionId(trainingTransactionId).get(0).getId();
	    byte[] bytes = TrainingEmployeesService.getTraineeCourseCancellationDecisionBytes(trainingTransactionDetailId);
	    super.print(bytes);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printCertificate(long trainingTransactionId) {
	try {
	    long trainingTransactionDetailId = TrainingEmployeesService.getTrainingTransactionDetailDataByTrainingTransactionId(trainingTransactionId).get(0).getId();
	    byte[] bytes = TrainingEmployeesService.getTraineeCertificateBytes(trainingTransactionDetailId);
	    super.print(bytes);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printScholarShipDecisions(TrainingTransactionDetailData trainingTransactionDetail) {
	try {
	    byte[] bytes = TrainingEmployeesService.getScholarshipDecisionBytes(trainingTransactionDetail.getId(), trainingTransactionDetail.getTransactionTypeId());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printJoiningApproval(TrainingTransactionData trainingTransaction) {
	try {
	    byte[] bytes = TrainingEmployeesService.getJoiningApprovalBytes(trainingTransaction.getId());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public boolean isModifyAdmin() {
	return isModifyAdmin;
    }

    public void setModifyAdmin(boolean isModifyAdmin) {
	this.isModifyAdmin = isModifyAdmin;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public int getInternalMilitaryCoursesStatus() {
	return internalMilitaryCoursesStatus;
    }

    public void setInternalMilitaryCoursesStatus(int internalMilitaryCoursesStatus) {
	this.internalMilitaryCoursesStatus = internalMilitaryCoursesStatus;
    }

    public int getExternalMilitaryCoursesStatus() {
	return externalMilitaryCoursesStatus;
    }

    public void setExternalMilitaryCoursesStatus(int externalMilitaryCoursesStatus) {
	this.externalMilitaryCoursesStatus = externalMilitaryCoursesStatus;
    }

    public List<TrainingTransactionData> getInternalMilitaryCourses() {
	return internalMilitaryCourses;
    }

    public void setInternalMilitaryCourses(List<TrainingTransactionData> internalMilitaryCourses) {
	this.internalMilitaryCourses = internalMilitaryCourses;
    }

    public List<TrainingTransactionData> getExternalMilitaryCourses() {
	return externalMilitaryCourses;
    }

    public void setExternalMilitaryCourses(List<TrainingTransactionData> externalMilitaryCourses) {
	this.externalMilitaryCourses = externalMilitaryCourses;
    }

    public List<TrainingTransactionData> getScholarships() {
	return scholarships;
    }

    public void setScholarships(List<TrainingTransactionData> scholarships) {
	this.scholarships = scholarships;
    }

    public List<TrainingTransactionDetailData> getScholarshipDetails() {
	return scholarshipDetails;
    }

    public void setScholarshipDetails(List<TrainingTransactionDetailData> scholarshipDetails) {
	this.scholarshipDetails = scholarshipDetails;
    }

    public List<TrainingTransactionData> getStudyEnrollments() {
	return studyEnrollments;
    }

    public void setStudyEnrollments(List<TrainingTransactionData> studyEnrollments) {
	this.studyEnrollments = studyEnrollments;
    }

    public List<TrainingTransactionData> getMorningCourses() {
	return morningCourses;
    }

    public void setMorningCourses(List<TrainingTransactionData> morningCourses) {
	this.morningCourses = morningCourses;
    }

    public List<TrainingTransactionData> getEveningCourses() {
	return eveningCourses;
    }

    public void setEveningCourses(List<TrainingTransactionData> eveningCourses) {
	this.eveningCourses = eveningCourses;
    }

    public long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(long employeeId) {
	this.employeeId = employeeId;
    }

    public String getEmployeeName() {
	return employeeName;
    }

    public void setEmployeeName(String employeeName) {
	this.employeeName = employeeName;
    }

    public boolean isDeleteClaimAdmin() {
	return deleteClaimAdmin;
    }

    public void setDeleteClaimAdmin(boolean deleteClaimAdmin) {
	this.deleteClaimAdmin = deleteClaimAdmin;
    }

}
