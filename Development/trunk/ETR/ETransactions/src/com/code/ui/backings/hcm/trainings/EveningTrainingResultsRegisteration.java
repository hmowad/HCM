package com.code.ui.backings.hcm.trainings;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.trainings.TrainingCourseData;
import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.dal.orm.hcm.trainings.TrainingTransactionData;
import com.code.enums.FlagsEnum;
import com.code.enums.GradesEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.TraineeStatusEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.hcm.TrainingCoursesService;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "eveningTrainingResultsRegisteration")
@ViewScoped
public class EveningTrainingResultsRegisteration extends BaseBacking {
    private TrainingTransactionData trainingTransaction;
    private TrainingCourseEventData selectedCourseEvent;
    private TrainingCourseData selectedCourseData;
    private long selectedCourseEventId;
    private long selectedEmployeeId;
    private List<TrainingCourseEventData> courseEvents;
    protected EmployeeData beneficiary;
    long trainingTypeId;
    Integer[] statusIds;

    public EveningTrainingResultsRegisteration() {

	super.init();
	setScreenTitle(getMessage("title_eveningTrainingResultsRegisteration"));

	trainingTypeId = TrainingTypesEnum.EVENING_COURSE.getCode();
	statusIds = new Integer[] { TraineeStatusEnum.NOMINATION_ACCEPTED_FROM_FRONTIER_GAURDS.getCode() };
	trainingTransaction = new TrainingTransactionData();
	selectedCourseEventId = FlagsEnum.ALL.getCode();
	selectedEmployeeId = FlagsEnum.ALL.getCode();
	courseEvents = new ArrayList<TrainingCourseEventData>();
    }

    public void selectEmployee() {
	try {
	    beneficiary = EmployeesService.getEmployeeData(selectedEmployeeId);
	    courseEvents = TrainingCoursesEventsService.getCoursesEventsDataForNomination(selectedEmployeeId, trainingTypeId, statusIds, FlagsEnum.ALL.getCode());
	    if (courseEvents.size() == 1) {
		selectedCourseEventId = courseEvents.get(0).getId();
		selectCourse();
	    } else if (courseEvents.isEmpty())
		throw new BusinessException("error_noCoursesForEmployee");
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectCourse() {
	try {
	    selectedCourseEvent = selectedCourseEventId == FlagsEnum.ALL.getCode() ? null : TrainingCoursesEventsService.getCourseEventById(selectedCourseEventId);
	    setSelectedCourseData(selectedCourseEventId == FlagsEnum.ALL.getCode() ? null : TrainingCoursesService.getTrainingCoursesById(selectedCourseEvent.getCourseId()));
	    if (selectedCourseEvent == null)
		trainingTransaction = new TrainingTransactionData();
	    else {
		trainingTransaction = TrainingEmployeesService.getTrainingTransactionsDataByPhysicalRegionId(new Long[] { trainingTypeId }, statusIds, selectedEmployeeId, selectedCourseEventId, loginEmpData.getPhysicalRegionId() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() ? FlagsEnum.ALL.getCode() : loginEmpData.getPhysicalRegionId()).get(0);
		if (trainingTransaction.getSuccessFlag() == null) {
		    trainingTransaction.setSuccessFlag(FlagsEnum.ON.getCode());
		    trainingTransaction.setQualificationGrade(GradesEnum.NOT_AVAILABLE.getCode());
		}
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void save() {
	try {
	    trainingTransaction.getTrainingTransaction().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
	    TrainingEmployeesService.saveTrainingTransactionResult(trainingTransaction, selectedCourseEventId);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void successFlagChangeListener() {
	trainingTransaction.setQualificationGrade(GradesEnum.NOT_AVAILABLE.getCode());
    }

    public TrainingTransactionData getTrainingTransaction() {
	return trainingTransaction;
    }

    public void setTrainingTransaction(TrainingTransactionData trainingTransaction) {
	this.trainingTransaction = trainingTransaction;
    }

    public EmployeeData getBeneficiary() {
	return beneficiary;
    }

    public void setBeneficiary(EmployeeData beneficiary) {
	this.beneficiary = beneficiary;
    }

    public List<TrainingCourseEventData> getCourseEvents() {
	return courseEvents;
    }

    public void setCourseEvents(List<TrainingCourseEventData> courseEvents) {
	this.courseEvents = courseEvents;
    }

    public TrainingCourseEventData getSelectedCourseEvent() {
	return selectedCourseEvent;
    }

    public void setSelectedCourseEvent(TrainingCourseEventData selectedCourseEvent) {
	this.selectedCourseEvent = selectedCourseEvent;
    }

    public TrainingCourseData getSelectedCourseData() {
	return selectedCourseData;
    }

    public void setSelectedCourseData(TrainingCourseData selectedCourseData) {
	this.selectedCourseData = selectedCourseData;
    }

    public long getSelectedCourseEventId() {
	return selectedCourseEventId;
    }

    public void setSelectedCourseEventId(long selectedCourseEventId) {
	this.selectedCourseEventId = selectedCourseEventId;
    }

    public long getSelectedEmployeeId() {
	return selectedEmployeeId;
    }

    public void setSelectedEmployeeId(long selectedEmployeeId) {
	this.selectedEmployeeId = selectedEmployeeId;
    }
}