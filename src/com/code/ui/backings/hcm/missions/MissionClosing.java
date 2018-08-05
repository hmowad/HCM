package com.code.ui.backings.hcm.missions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import com.code.dal.orm.hcm.missions.MissionData;
import com.code.dal.orm.hcm.missions.MissionDetailData;
import com.code.enums.CategoryModesEnum;
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
		setServerSideErrorMessages(getMessage("error_general"));
	    }
	} else {
	    setServerSideErrorMessages(getMessage("error_general"));
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
    }

    public void adjustMissionDetailEndDate() {
	if (selectedMissionDetail.getActualPeriod() == null || selectedMissionDetail.getRoadPeriod() == null || selectedMissionDetail.getActualStartDate() == null)
	    return;

	selectedMissionDetail.setActualEndDateString(MissionsService.calculateMissionEndDate(selectedMissionDetail.getActualStartDateString(), selectedMissionDetail.getActualPeriod(), selectedMissionDetail.getRoadPeriod()));
    }

    public void modifyMissionDetail() {
	try {
	    selectedMissionDetail.getMissionDetail().setSystemUser(this.loginEmpData.getEmpId() + "");
	    MissionsService.modifyActualMissionDetails(mission, selectedMissionDetail);
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}

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

}
