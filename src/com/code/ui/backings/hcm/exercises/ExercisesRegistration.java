package com.code.ui.backings.hcm.exercises;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.exercises.ExerciseData;
import com.code.enums.LocationFlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.ExercisesService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "exercisesRegistration")
@ViewScoped
public class ExercisesRegistration extends BaseBacking {

    private ExerciseData exerciseData;

    private boolean done;

    public ExercisesRegistration() {
	try {
	    super.init();
	    setScreenTitle(getMessage("title_exercisesRegistration"));
	    resetForm();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetForm() throws BusinessException {
	exerciseData = new ExerciseData();
	exerciseData.setLocationFlag(LocationFlagsEnum.INTERNAL.getCode());
	exerciseData.setStartDate(HijriDateService.getHijriSysDate());

	done = false;
    }

    public void save() {
	try {
	    ExercisesService.addExercise(exerciseData, this.loginEmpData.getEmpId());
	    done = true;
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    /*****************************************************************************************************************/

    public ExerciseData getExerciseData() {
	return exerciseData;
    }

    public void setExerciseData(ExerciseData exerciseData) {
	this.exerciseData = exerciseData;
    }

    public boolean isDone() {
	return done;
    }
}
