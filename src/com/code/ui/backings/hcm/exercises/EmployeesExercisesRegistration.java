package com.code.ui.backings.hcm.exercises;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.exercises.EmployeeExerciseData;
import com.code.dal.orm.hcm.exercises.ExerciseData;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.ExercisesService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "employeesExercisesRegistration")
@ViewScoped
public class EmployeesExercisesRegistration extends BaseBacking {

    /**
     * 1 for Officers, 2 for Soldiers
     **/
    private int mode;

    private EmployeeExerciseData employeeExerciseData;

    private String selectedEmployeeName;
    private List<ExerciseData> exercisesData;

    private boolean done;

    public EmployeesExercisesRegistration() {
	try {
	    super.init();

	    if (getRequest().getParameter("mode") != null) {
		mode = Integer.parseInt(getRequest().getParameter("mode"));
		switch (mode) {
		case 1:
		    setScreenTitle(getMessage("title_employeesExercisesRegistrationForOfficers"));
		    break;
		case 2:
		    setScreenTitle(getMessage("title_employeesExercisesRegistrationForSoldiers"));
		    break;
		default:
		    this.setServerSideErrorMessages(getMessage("error_URLError"));
		}
	    } else {
		this.setServerSideErrorMessages(getMessage("error_URLError"));
	    }

	    exercisesData = ExercisesService.getExercises();

	    resetForm();

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetForm() {
	selectedEmployeeName = "";

	employeeExerciseData = new EmployeeExerciseData();
	if (exercisesData.size() > 0) {
	    employeeExerciseData.setPeriod(exercisesData.get(0).getPeriod());
	    employeeExerciseData.setStartDate(exercisesData.get(0).getStartDate());
	    employeeExerciseData.setEndDate(exercisesData.get(0).getEndDate());
	}
	done = false;
    }

    public void exerciseChangeListener() {
	try {
	    ExerciseData exerciseData = ExercisesService.getExerciseById(employeeExerciseData.getExerciseId());
	    employeeExerciseData.setPeriod(exerciseData.getPeriod());
	    employeeExerciseData.setStartDate(exerciseData.getStartDate());
	    employeeExerciseData.setEndDate(exerciseData.getEndDate());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void save() {
	try {
	    ExercisesService.addEmployeeExercise(employeeExerciseData, this.loginEmpData.getEmpId());
	    done = true;
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    /*****************************************************************************************************************/

    public int getMode() {
	return mode;
    }

    public EmployeeExerciseData getEmployeeExerciseData() {
	return employeeExerciseData;
    }

    public void setEmployeeExerciseData(EmployeeExerciseData employeeExerciseData) {
	this.employeeExerciseData = employeeExerciseData;
    }

    public String getSelectedEmployeeName() {
	return selectedEmployeeName;
    }

    public void setSelectedEmployeeName(String selectedEmployeeName) {
	this.selectedEmployeeName = selectedEmployeeName;
    }

    public List<ExerciseData> getExercisesData() {
	return exercisesData;
    }

    public void setExercisesData(List<ExerciseData> exercisesData) {
	this.exercisesData = exercisesData;
    }

    public boolean isDone() {
	return done;
    }
}
