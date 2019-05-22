package com.code.ui.backings.hcm.trainings;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingYear;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.ReportOutputFormatsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.security.SecurityService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "trainingPlanDecisionsInquiry")
@ViewScoped
public class TrainingPlanDecisionsInquiry extends BaseBacking {

    private int pageSize = 10;
    private List<TrainingYear> trainingYearsList;
    private boolean excelPrintFlag = false;

    public TrainingPlanDecisionsInquiry() {
	try {
	    setScreenTitle(getMessage("title_trainingPlanDecisions"));
	    trainingYearsList = TrainingSetupService.getApprovedTrainingYears();
	    if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.TRN_PLAN_DECISIONS_INQUIRY.getCode(), MenuActionsEnum.TRN_TRAINING_PLAN_DETAILS_EXCEL_PRINT.getCode()))
		excelPrintFlag = true;
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void printTrainingPlanDecisionApproval(TrainingYear trainingYear) {
	try {
	    byte[] bytes = TrainingCoursesEventsService.getTrainingPlanDecisionApprovalBytes(trainingYear.getId());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printPlanDetailsPrint(TrainingYear trainingYear, int reportType) {
	try {
	    byte[] bytes;
	    if (reportType == 1) {// PDF
		bytes = TrainingCoursesEventsService.getTrainingPlanDecisionDetailsBytes(trainingYear.getId(), ReportOutputFormatsEnum.PDF.getCode());
		super.print(bytes);
	    } else if (reportType == 2) {// WORD
		bytes = TrainingCoursesEventsService.getTrainingPlanDecisionDetailsBytes(trainingYear.getId(), ReportOutputFormatsEnum.RTF.getCode());
		super.printRTF(bytes);
	    } else {// EXCEL
		bytes = TrainingCoursesEventsService.getTrainingPlanDecisionDetailsBytes(trainingYear.getId(), ReportOutputFormatsEnum.XLS.getCode());
		super.printXls(bytes);
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<TrainingYear> getTrainingYearsList() {
	return trainingYearsList;
    }

    public void setTrainingYearsList(List<TrainingYear> trainingYearsList) {
	this.trainingYearsList = trainingYearsList;
    }

    public int getPageSize() {
	return pageSize;
    }

    public boolean getExcelPrintFlag() {
	return excelPrintFlag;
    }
}
