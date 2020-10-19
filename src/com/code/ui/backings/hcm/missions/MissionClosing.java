package com.code.ui.backings.hcm.missions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import com.code.dal.orm.hcm.missions.MissionData;
import com.code.dal.orm.hcm.missions.MissionDetailData;
import com.code.enums.AdminDecisionsEnum;
import com.code.enums.CategoryModesEnum;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.MissionsService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "missionClosing")
@ViewScoped
public class MissionClosing extends BaseBacking {
    /**
     * 1 for Officers, 2 for Soldiers and 3 for Civil
     **/
    private int mode;

    private String decisionNumber;
    private Date decisionDate;

    private MissionData mission;
    private MissionDetailData selectedMissionDetail;
    private List<MissionDetailData> missionDetailList = new ArrayList<MissionDetailData>();
    private Boolean editPanelFlag;
    private Boolean savePanelFlag;
    private Boolean cancelMissionFlag;
    private Boolean savePaymentFlag;

    private int pageSize = 10;

    public MissionClosing() {

	if (getRequest().getParameter("mode") != null) {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    if (mode == CategoryModesEnum.OFFICERS.getCode()) {
		setScreenTitle(getMessage("title_officersMissionClosing"));
	    } else if (mode == CategoryModesEnum.SOLDIERS.getCode()) {
		setScreenTitle(getMessage("title_soldiersMissionClosing"));
	    } else if (mode == CategoryModesEnum.CIVILIANS.getCode()) {
		setScreenTitle(getMessage("title_personsMissionClosing"));
	    } else {
		setServerSideErrorMessages(getMessage("error_URLError"));
	    }
	} else {
	    setServerSideErrorMessages(getMessage("error_URLError"));
	}
	resetForm();

    }

    public void resetForm() {
	try {
	    decisionNumber = "";
	    decisionDate = HijriDateService.getHijriSysDate();
	    mission = new MissionData();
	    missionDetailList = new ArrayList<MissionDetailData>();
	    selectedMissionDetail = new MissionDetailData();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void resetAbsenceReasons(AjaxBehaviorEvent event) {
	if (!selectedMissionDetail.getAbsenceFlagBoolean())
	    selectedMissionDetail.setAbsenceReasons(null);
    }

    public void resetPaymentDecision(AjaxBehaviorEvent event) {
	if (!selectedMissionDetail.getPaymentDecisionIssuedFlagBoolean()) {
	    selectedMissionDetail.setPaymentDecisionDate(null);
	    selectedMissionDetail.setPaymentDecisionNumber(null);
	}
    }

    public void searchMissionDetails() {
	try {

	    mission = MissionsService.getMissionByDecisionNoAndDecisionDate(decisionNumber, decisionDate, mode, getLoginEmpPhysicalRegionFlag(false));
	    if (mission != null) {
		missionDetailList = MissionsService.getMissionDetailsByMissionId(mission.getId());
		if (missionDetailList.size() > 0)
		    selectedMissionDetail = missionDetailList.get(0);
		else
		    selectedMissionDetail = new MissionDetailData();
	    } else {
		missionDetailList = new ArrayList<MissionDetailData>();
		selectedMissionDetail = new MissionDetailData();
	    }

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void showMissionDetail(MissionDetailData missionDetail) {
	selectedMissionDetail = missionDetail;
	hideAllExtraPanels();
    }

    public void adjustMissionDetailEndDate() {
	if (selectedMissionDetail.getActualPeriod() == null || selectedMissionDetail.getRoadPeriod() == null || selectedMissionDetail.getActualStartDate() == null)
	    return;

	selectedMissionDetail.setActualEndDateString(MissionsService.calculateMissionEndDate(selectedMissionDetail.getActualStartDateString(), selectedMissionDetail.getActualPeriod(), selectedMissionDetail.getRoadPeriod()));
    }

    public void modifyMissionDetail() {
	try {
	    selectedMissionDetail.getMissionDetail().setSystemUser(this.loginEmpData.getEmpId() + "");
	    MissionsService.modifyActualMissionDetails(mission, selectedMissionDetail, selectedMissionDetail.getAbsenceFlagBoolean() ? null : AdminDecisionsEnum.OFFICERS_MISSION_DECISION.getCode());
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    selectedMissionDetail.setActualDataSavedFlag(FlagsEnum.OFF.getCode());
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}

    }

    public void editMissionDetail() {
	try {
	    selectedMissionDetail.getMissionDetail().setSystemUser(this.loginEmpData.getEmpId() + "");
	    MissionsService.modifyActualMissionDetails(mission, selectedMissionDetail, AdminDecisionsEnum.OFFICERS_MISSION_DIFFERENCES_DECISION.getCode());
	    editPanelFlag = false;
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}

    }

    public void saveMissionDetail() {
	try {
	    selectedMissionDetail.getMissionDetail().setSystemUser(this.loginEmpData.getEmpId() + "");
	    selectedMissionDetail.setAbsenceFlagBoolean(false);
	    selectedMissionDetail.setAbsenceReasons("");
	    selectedMissionDetail.setActualDataSavedFlag(FlagsEnum.ON.getCode());
	    MissionsService.modifyActualMissionDetails(mission, selectedMissionDetail, AdminDecisionsEnum.OFFICERS_MISSION_DECISION.getCode());
	    savePanelFlag = false;
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}

    }

    public void cancelMissionDetail() {
	try {
	    selectedMissionDetail.setActualPeriod(mission.getPeriod());
	    selectedMissionDetail.setActualStartDate(mission.getStartDate());
	    selectedMissionDetail.setActualEndDate(mission.getEndDate());
	    selectedMissionDetail.setAbsenceFlagBoolean(true);
	    MissionsService.modifyActualMissionDetails(mission, selectedMissionDetail, AdminDecisionsEnum.OFFICERS_MISSION_CANCELLATION_DECISION.getCode());
	    cancelMissionFlag = false;
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    selectedMissionDetail.setActualDataSavedFlag(FlagsEnum.OFF.getCode());
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void savePaymentData() {
	try {
	    selectedMissionDetail.setPaymentDecisionIssuedFlagBoolean(true);
	    MissionsService.modifyActualMissionDetails(mission, selectedMissionDetail, null);
	    savePaymentFlag = false;
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    selectedMissionDetail.setActualDataSavedFlag(FlagsEnum.OFF.getCode());
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void hideAllExtraPanels() {
	savePanelFlag = false;
	editPanelFlag = false;
	cancelMissionFlag = false;
	savePaymentFlag = false;
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public MissionData getMission() {
	return mission;
    }

    public void setMission(MissionData mission) {
	this.mission = mission;
    }

    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public List<MissionDetailData> getMissionDetailList() {
	return missionDetailList;
    }

    public void setMissionDetailList(List<MissionDetailData> missionDetailList) {
	this.missionDetailList = missionDetailList;
    }

    public MissionDetailData getSelectedMissionDetail() {
	return selectedMissionDetail;
    }

    public void setSelectedMissionDetail(MissionDetailData selectedMissionDetail) {
	this.selectedMissionDetail = selectedMissionDetail;
    }

    public Boolean getEditPanelFlag() {
	return editPanelFlag;
    }

    public void setEditPanelFlag(Boolean editPanelFlag) {
	hideAllExtraPanels();
	this.editPanelFlag = editPanelFlag;
    }

    public Boolean getSavePanelFlag() {
	return savePanelFlag;
    }

    public void setSavePanelFlag(Boolean savePanelFlag) {
	hideAllExtraPanels();
	this.savePanelFlag = savePanelFlag;
    }

    public Boolean getCancelMissionFlag() {
	return cancelMissionFlag;
    }

    public void setCancelMissionFlag(Boolean cancelMissionFlag) {
	hideAllExtraPanels();
	this.cancelMissionFlag = cancelMissionFlag;
    }

    public Boolean getSavePaymentFlag() {
	return savePaymentFlag;
    }

    public void setSavePaymentFlag(Boolean savePaymentFlag) {
	hideAllExtraPanels();
	this.savePaymentFlag = savePaymentFlag;
    }

}
