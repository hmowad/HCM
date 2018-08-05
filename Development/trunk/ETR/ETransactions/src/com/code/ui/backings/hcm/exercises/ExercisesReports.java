package com.code.ui.backings.hcm.exercises;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.exercises.ExerciseData;
import com.code.dal.orm.hcm.organization.Region;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.ExercisesService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "exercisesReports")
@ViewScoped
public class ExercisesReports extends BaseBacking {

    private int reportType;

    private List<Region> regionList;
    private long selectedRegionId;

    private long selectedUnitId;
    private String selectedUnitHKey;
    private String selectedUnitFullName;

    private Long selectedEmployeeId;
    private String selectedEmployeeName;

    private long selectedCategoryId;

    private List<ExerciseData> exercisesData;
    private Long selectedExerciseId;

    private Date fromDate;
    private Date toDate;

    public ExercisesReports() {
	try {
	    regionList = CommonService.getAllRegions();
	    exercisesData = ExercisesService.getExercises();
	    reportType = 10;
	    resetForm();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetForm() {
	selectedRegionId = getLoginEmpPhysicalRegionFlag(true);
	selectedUnitId = FlagsEnum.ALL.getCode();
	selectedUnitHKey = null;
	selectedUnitFullName = "";
	selectedEmployeeId = null;
	selectedEmployeeName = "";
	selectedCategoryId = FlagsEnum.ALL.getCode();
	selectedExerciseId = null;
	fromDate = toDate = null;
    }

    public void print() {
	try {
	    String regionDesc = getMessage("label_all");
	    if (selectedRegionId != FlagsEnum.ALL.getCode())
		regionDesc = CommonService.getRegionById(selectedRegionId).getDescription();

	    byte[] bytes = ExercisesService.getExercisesReportsBytes(reportType, selectedRegionId, regionDesc, selectedUnitHKey, selectedUnitFullName,
		    selectedEmployeeId, selectedCategoryId, selectedExerciseId, fromDate, toDate);

	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    /*****************************************************************************************************************/

    public int getReportType() {
	return reportType;
    }

    public void setReportType(int reportType) {
	this.reportType = reportType;
    }

    public List<Region> getRegionList() {
	return regionList;
    }

    public void setRegionList(List<Region> regionList) {
	this.regionList = regionList;
    }

    public long getSelectedRegionId() {
	return selectedRegionId;
    }

    public void setSelectedRegionId(long selectedRegionId) {
	this.selectedRegionId = selectedRegionId;
    }

    public long getSelectedUnitId() {
	return selectedUnitId;
    }

    public void setSelectedUnitId(long selectedUnitId) {
	this.selectedUnitId = selectedUnitId;
    }

    public String getSelectedUnitHKey() {
	return selectedUnitHKey;
    }

    public void setSelectedUnitHKey(String selectedUnitHKey) {
	this.selectedUnitHKey = selectedUnitHKey;
    }

    public String getSelectedUnitFullName() {
	return selectedUnitFullName;
    }

    public void setSelectedUnitFullName(String selectedUnitFullName) {
	this.selectedUnitFullName = selectedUnitFullName;
    }

    public Long getSelectedEmployeeId() {
	return selectedEmployeeId;
    }

    public void setSelectedEmployeeId(Long selectedEmployeeId) {
	this.selectedEmployeeId = selectedEmployeeId;
    }

    public String getSelectedEmployeeName() {
	return selectedEmployeeName;
    }

    public void setSelectedEmployeeName(String selectedEmployeeName) {
	this.selectedEmployeeName = selectedEmployeeName;
    }

    public long getSelectedCategoryId() {
	return selectedCategoryId;
    }

    public void setSelectedCategoryId(long selectedCategoryId) {
	this.selectedCategoryId = selectedCategoryId;
    }

    public List<ExerciseData> getExercisesData() {
	return exercisesData;
    }

    public void setExercisesData(List<ExerciseData> exercisesData) {
	this.exercisesData = exercisesData;
    }

    public Long getSelectedExerciseId() {
	return selectedExerciseId;
    }

    public void setSelectedExerciseId(Long selectedExerciseId) {
	this.selectedExerciseId = selectedExerciseId;
    }

    public Date getFromDate() {
	return fromDate;
    }

    public void setFromDate(Date fromDate) {
	this.fromDate = fromDate;
    }

    public Date getToDate() {
	return toDate;
    }

    public void setToDate(Date toDate) {
	this.toDate = toDate;
    }

}
