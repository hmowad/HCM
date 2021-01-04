package com.code.ui.backings.hcm.trainings;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingCourseEventData;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingData;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.workflow.hcm.TrainingCoursesEventsWorkFlow;
import com.code.services.workflow.hcm.TrainingEmployeesWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "trainingsCollectiveApproval")
@ViewScoped
public class TrainingsCollectiveApproval extends BaseBacking {
    // object[0] - WFTrainingData
    // object[1] - WFTask
    // object[2] - WFInstance
    // object[3] - processName
    // object[4] - requester
    // object[5] - delegatingName
    private List<Object> trainingsTasks;
    private List<Object> trainingCoursesTasks;
    private List<Object> tasksAndTrainingsObjects;
    private List<Object> tasksAndTrainingCoursesObjects;
    private boolean selectAllTraining;
    private boolean selectAllTrainingCourse;
    private int trainingsTasksListSize;
    private int trainingCoursesTasksListSize;

    private final static String taskUrlParam = "&taskId=";
    private final static int pageSize = 10;
    private int mode = -1; // 1 means acceptance and 2 means approval

    public TrainingsCollectiveApproval() {
	super();
	try {
	    if (getRequest().getAttribute("mode") != null)
		this.mode = Integer.parseInt(getRequest().getAttribute("mode").toString());

	    if (mode == 1) {
		this.setScreenTitle(getMessage("title_trainingsCollectiveAcceptance"));
	    } else if (mode == 2) {
		this.setScreenTitle(getMessage("title_trainingsCollectiveApproval"));
	    } else {
		throw new BusinessException("error_URLError");
	    }

	    // Load the training tasks data
	    searchTrainingsTasks();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void searchTrainingsTasks() {
	try {
	    selectAllTraining = false;
	    selectAllTrainingCourse = false;
	    // EmployeeTrainingTasks
	    tasksAndTrainingsObjects = TrainingEmployeesWorkFlow.getWFTrainingTasks(this.loginEmpData.getEmpId(), mode == 1 ? new String[] { WFTaskRolesEnum.DIRECT_MANAGER.getCode() } : new String[] { WFTaskRolesEnum.SIGN_MANAGER.getCode(), WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode(), WFTaskRolesEnum.EXTRA_SECONDARY_SIGN_MANAGER.getCode() });
	    trainingsTasks = new ArrayList<>();
	    for (int i = 0; i < tasksAndTrainingsObjects.size(); i++) {
		WFTask currentTask = (WFTask) (((Object[]) tasksAndTrainingsObjects.get(i))[1]);
		WFTask previousTask = i == 0 ? null : (WFTask) (((Object[]) tasksAndTrainingsObjects.get(i - 1))[1]);
		if (previousTask == null || currentTask.getTaskId().longValue() != previousTask.getTaskId().longValue()) {
		    trainingsTasks.add(tasksAndTrainingsObjects.get(i));
		}
	    }
	    trainingsTasksListSize = trainingsTasks.size();
	    // TrainingCoursesTasks

	    tasksAndTrainingCoursesObjects = TrainingCoursesEventsWorkFlow.getWFTrainingCourseEventTasks(this.loginEmpData.getEmpId(), mode == 1 ? new String[] { WFTaskRolesEnum.DIRECT_MANAGER.getCode() } : new String[] { WFTaskRolesEnum.SIGN_MANAGER.getCode(), WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode(), WFTaskRolesEnum.EXTRA_SECONDARY_SIGN_MANAGER.getCode() });
	    trainingCoursesTasks = new ArrayList<>();
	    for (int i = 0; i < tasksAndTrainingCoursesObjects.size(); i++) {
		WFTask currentTask = (WFTask) (((Object[]) tasksAndTrainingCoursesObjects.get(i))[1]);
		WFTask previousTask = i == 0 ? null : (WFTask) (((Object[]) tasksAndTrainingCoursesObjects.get(i - 1))[1]);
		if (previousTask == null || currentTask.getTaskId().longValue() != previousTask.getTaskId().longValue()) {
		    trainingCoursesTasks.add(tasksAndTrainingCoursesObjects.get(i));
		}
	    }
	    trainingCoursesTasksListSize = trainingCoursesTasks.size();
	} catch (BusinessException e) {
	    trainingsTasks = new ArrayList<Object>();
	    trainingCoursesTasks = new ArrayList<Object>();
	    tasksAndTrainingsObjects = new ArrayList<Object>();
	    tasksAndTrainingCoursesObjects = new ArrayList<Object>();
	    trainingsTasksListSize = 0;
	    trainingCoursesTasksListSize = 0;
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    // called from the xhtml on change of the selectAllTraining check box
    public void selectUnselectAllRows() {
	for (int i = 0; i < trainingsTasks.size(); i++) {
	    ((WFTrainingData) (((Object[]) trainingsTasks.get(i))[0])).setSelected(selectAllTraining);
	}
    }

    public void selectUnselectAllTrainingCourseRows() {
	for (int i = 0; i < trainingCoursesTasks.size(); i++) {
	    ((WFTrainingCourseEventData) (((Object[]) trainingCoursesTasks.get(i))[0])).setSelected(selectAllTrainingCourse);
	}
    }

    // Loop over the selected tasks for approval
    // called from the xhtml when "Approve" button clicked
    public void doTrainingCollectiveAction() {
	String unsuccessfulTaskIdsIfAny = "";
	String comma = "";
	int unsuccessfulTasksCount = 0;
	for (Object obj : trainingsTasks) {
	    try {
		WFTrainingData trainingRequest = ((WFTrainingData) ((Object[]) obj)[0]);
		if (trainingRequest.getSelected())
		    TrainingEmployeesWorkFlow.doTrainingsCollectiveAction(obj, tasksAndTrainingsObjects, mode);
	    } catch (BusinessException e) {
		unsuccessfulTaskIdsIfAny += comma + ((WFTask) ((Object[]) obj)[1]).getTaskId();
		unsuccessfulTasksCount++;
		comma = ", ";
	    }
	}

	if (unsuccessfulTasksCount > 0)
	    this.setServerSideErrorMessages(getParameterizedMessage("error_thereAreErrorsForNOfTasks", new Object[] { unsuccessfulTasksCount, unsuccessfulTaskIdsIfAny }));
	else
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));

	// Call the search method here to Reload the tasks
	searchTrainingsTasks();
    }

    public void doTrainingCourseEventCollectiveAction() {
	String unsuccessfulTaskIdsIfAny = "";
	String comma = "";
	int unsuccessfulTasksCount = 0;
	for (Object obj : trainingCoursesTasks) {
	    try {
		WFTrainingCourseEventData trainingCourseRequest = ((WFTrainingCourseEventData) ((Object[]) obj)[0]);
		if (trainingCourseRequest.getSelected())
		    TrainingCoursesEventsWorkFlow.doTrainingCourseEventsCollectiveAction(obj, tasksAndTrainingCoursesObjects);
	    } catch (BusinessException e) {
		unsuccessfulTaskIdsIfAny += comma + ((WFTask) ((Object[]) obj)[1]).getTaskId();
		unsuccessfulTasksCount++;
		comma = ", ";
	    }
	}

	if (unsuccessfulTasksCount > 0)
	    this.setServerSideErrorMessages(getParameterizedMessage("error_thereAreErrorsForNOfTasks", new Object[] { unsuccessfulTasksCount, unsuccessfulTaskIdsIfAny }));
	else
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));

	// Call the search method here to Reload the tasks
	searchTrainingsTasks();
    }

    public void setTrainingsTasks(List<Object> trainingsTasks) {
	this.trainingsTasks = trainingsTasks;
    }

    public List<Object> getTrainingsTasks() {
	return trainingsTasks;
    }

    public List<Object> getTrainingCoursesTasks() {
	return trainingCoursesTasks;
    }

    public void setTrainingCoursesTasks(List<Object> trainingCoursesTasks) {
	this.trainingCoursesTasks = trainingCoursesTasks;
    }

    public void setTrainingsTasksListSize(int trainingsTasksListSize) {
	this.trainingsTasksListSize = trainingsTasksListSize;
    }

    public int getTrainingsTasksListSize() {
	return trainingsTasksListSize;
    }

    public int getTrainingCoursesTasksListSize() {
	return trainingCoursesTasksListSize;
    }

    public void setTrainingCoursesTasksListSize(int trainingCoursesTasksListSize) {
	this.trainingCoursesTasksListSize = trainingCoursesTasksListSize;
    }

    public String getTaskUrlParam() {
	return taskUrlParam;
    }

    public boolean isSelectAllTraining() {
	return selectAllTraining;
    }

    public void setSelectAllTraining(boolean selectAllTraining) {
	this.selectAllTraining = selectAllTraining;
    }

    public boolean isSelectAllTrainingCourse() {
	return selectAllTrainingCourse;
    }

    public void setSelectAllTrainingCourse(boolean selectAllTrainingCourse) {
	this.selectAllTrainingCourse = selectAllTrainingCourse;
    }

    public int getPageSize() {
	return pageSize;
    }

    public static int getPagesize() {
	return pageSize;
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }
}
