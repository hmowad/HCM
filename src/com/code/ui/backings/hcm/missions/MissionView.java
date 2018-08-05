package com.code.ui.backings.hcm.missions;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.missions.MissionData;
import com.code.dal.orm.hcm.missions.MissionDetailData;
import com.code.dal.orm.security.EmployeeDecisionPrivilege;
import com.code.enums.CategoryModesEnum;
import com.code.enums.WFProcessesGroupsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.MissionsService;
import com.code.services.security.SecurityService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "missionView")
@ViewScoped
public class MissionView extends BaseBacking {
    /**
     * 1 for Officers, 2 for Soldiers and 3 for Civil
     **/
    private int mode;

    private MissionData mission;
    private MissionDetailData selectedMissionDetail;
    private List<MissionDetailData> missionDetailList = new ArrayList<MissionDetailData>();

    private int pageSize = 10;

    public MissionView() {
	try {
	    String missionIdAsString = getRequest().getParameter("missionId");
	    if (missionIdAsString != null && !missionIdAsString.equals("")) {
		long missionId = Long.parseLong(missionIdAsString);
		mission = MissionsService.getMissionsById(missionId);
		if (mission != null) {
		    mode = mission.getCategoryMode();
		    if (mode == CategoryModesEnum.OFFICERS.getCode())
			setScreenTitle(getMessage("title_officersMissionView"));
		    else if (mode == CategoryModesEnum.SOLDIERS.getCode())
			setScreenTitle(getMessage("title_soldiersMissionView"));
		    else if (mode == CategoryModesEnum.CIVILIANS.getCode())
			setScreenTitle(getMessage("title_personsMissionView"));
		    else
			setServerSideErrorMessages(getMessage("error_general"));

		    missionDetailList = MissionsService.getMissionDetailsByMissionId(missionId);
		    if (missionDetailList.size() > 0) {
			showMissionDetail(missionDetailList.get(0));

			// Check Security
			List<EmployeeDecisionPrivilege> decisionsPrivileges = SecurityService.getEmployeesDecisionsPrivileges(this.loginEmpData.getEmpId(), WFProcessesGroupsEnum.MISSIONS.getCode(), mode);
			boolean foundAuthority = false;
			for (MissionDetailData missionDetail : missionDetailList) {
			    if (SecurityService.isDecisionPrivilegeGranted(this.loginEmpData, missionDetail.getEmpId(), decisionsPrivileges)) {
				foundAuthority = true;
				break;
			    }
			}

			if (!foundAuthority) {
			    mission = null;
			    missionDetailList = null;
			    selectedMissionDetail = null;
			}
		    }
		} else {
		    missionDetailList = new ArrayList<MissionDetailData>();
		    selectedMissionDetail = new MissionDetailData();
		}
	    } else
		setServerSideErrorMessages(getMessage("error_general"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public String getRegionsNames() {
	String regionsNames = "";
	String comma = "";
	if (mission.getRegionsCodes() != null) {
	    String[] codes = mission.getRegionsCodes().split(",");
	    for (String code : codes) {
		if (!code.equals("-1")) {
		    regionsNames += comma + CommonService.getRegionByCode(code).getDescription();
		    comma = ",";
		}
	    }

	    if (mission.getRegionsCodes().contains("-1"))
		regionsNames += comma + getMessage("lable_otherRegions");
	}

	return regionsNames;
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

    public void showMissionDetail(MissionDetailData missionDetail) {
	selectedMissionDetail = missionDetail;
    }

    public MissionDetailData getSelectedMissionDetail() {
	return selectedMissionDetail;
    }

    public void setSelectedMissionDetail(MissionDetailData selectedMissionDetail) {
	this.selectedMissionDetail = selectedMissionDetail;
    }

}
