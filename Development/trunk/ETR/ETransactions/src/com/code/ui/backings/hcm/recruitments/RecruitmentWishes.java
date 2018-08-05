package com.code.ui.backings.hcm.recruitments;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.hcm.recruitments.RecruitmentWishData;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.RanksEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.RecruitmentsService;
import com.code.services.security.SecurityService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "recruitmentWishes")
@ViewScoped
public class RecruitmentWishes extends BaseBacking implements Serializable {

    private Long rankId;
    private Long minorSpecId;
    private String employeeName;
    private Long employeeId;
    private RecruitmentWishData selectedRecruitmentWish;
    private boolean canEdit;
    private int pageSize = 10;

    private List<Rank> ranks;
    private List<Region> regions;
    private Map<Long, String> regionsHashMap;
    private List<RecruitmentWishData> recruitmentWishDataList;

    public RecruitmentWishes() {
	try {
	    init();
	    setScreenTitle(getMessage("title_recruitmentEvaluationDegreesAndWishesRegistration"));

	    if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.REC_RECRUITMENT_WISHES.getCode(), MenuActionsEnum.REC_RECRUITMENT_WISHES_MODIFY.getCode()))
		canEdit = true;

	    ranks = CommonService.getRanks(null, CommonService.getSoldiersCategoriesIdsArray());
	    for (int i = 0; i < ranks.size(); i++) {
		if (ranks.get(i).getId().equals(RanksEnum.STUDENT_SOLDIER.getCode())) {
		    ranks.remove(i);
		    break;
		}
	    }

	    regions = CommonService.getAllRegions();
	    regionsHashMap = new HashMap<Long, String>();
	    for (Region r : regions)
		regionsHashMap.put(r.getId(), r.getDescription());
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void init() {
	try {
	    recruitmentWishDataList = RecruitmentsService.getRecruitmentWishes();
	    rankId = -1L;
	    minorSpecId = -1L;
	    employeeName = null;
	    selectedRecruitmentWish = null;
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void searchRecruitmentWishes() {
	try {
	    recruitmentWishDataList = RecruitmentsService.getRecruitmentWishes(employeeName, minorSpecId, rankId);
	    selectedRecruitmentWish = null;
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addNewRecruitmentWish() {
	try {
	    selectedRecruitmentWish = RecruitmentsService.constructRecruitmentWishByEmpId(employeeId);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectNewRecruitmentWish(RecruitmentWishData recWish) {
	selectedRecruitmentWish = recWish;
    }

    public void saveRecruitmentWish() {
	try {
	    if (selectedRecruitmentWish != null) {
		selectedRecruitmentWish.getRecruitmentWish().setSystemUser(loginEmpData.getEmpId() + ""); // For auditing
		if (selectedRecruitmentWish.getId() == null) {
		    RecruitmentsService.addRecruitmentWish(selectedRecruitmentWish);
		    setRegionsDescriptions();
		    recruitmentWishDataList.add(selectedRecruitmentWish);
		} else
		    RecruitmentsService.updateRecruitmentWish(selectedRecruitmentWish);

		this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void setRegionsDescriptions() {
	selectedRecruitmentWish.setRegionsFirstWishDesc(regionsHashMap.get(selectedRecruitmentWish.getRegionsFirstWishId()));
	selectedRecruitmentWish.setRegionsSecondWishDesc(regionsHashMap.get(selectedRecruitmentWish.getRegionsSecondWishId()));
	selectedRecruitmentWish.setRegionsThirdWishDesc(regionsHashMap.get(selectedRecruitmentWish.getRegionsThirdWishId()));
	selectedRecruitmentWish.setRegionsFourthWishDesc(regionsHashMap.get(selectedRecruitmentWish.getRegionsFourthWishId()));
	selectedRecruitmentWish.setRegionsFifthWishDesc(regionsHashMap.get(selectedRecruitmentWish.getRegionsFifthWishId()));
	selectedRecruitmentWish.setRegionsSixthWishDesc(regionsHashMap.get(selectedRecruitmentWish.getRegionsSixthWishId()));
	selectedRecruitmentWish.setRegionsSeventhWishDesc(regionsHashMap.get(selectedRecruitmentWish.getRegionsSeventhWishId()));
	selectedRecruitmentWish.setRegionsEighthWishDesc(regionsHashMap.get(selectedRecruitmentWish.getRegionsEighthWishId()));
	selectedRecruitmentWish.setRegionsNinthWishDesc(regionsHashMap.get(selectedRecruitmentWish.getRegionsNinthWishId()));
	selectedRecruitmentWish.setRegionsTenthWishDesc(regionsHashMap.get(selectedRecruitmentWish.getRegionsTenthWishId()));
	selectedRecruitmentWish.setRegionsEleventhWishDesc(regionsHashMap.get(selectedRecruitmentWish.getRegionsEleventhWishId()));
    }

    public Long getRankId() {
	return rankId;
    }

    public void setRankId(Long rankId) {
	this.rankId = rankId;
    }

    public Long getMinorSpecId() {
	return minorSpecId;
    }

    public void setMinorSpecId(Long minorSpecId) {
	this.minorSpecId = minorSpecId;
    }

    public String getEmployeeName() {
	return employeeName;
    }

    public void setEmployeeName(String employeeName) {
	this.employeeName = employeeName;
    }

    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
    }

    public RecruitmentWishData getSelectedRecruitmentWish() {
	return selectedRecruitmentWish;
    }

    public void setSelectedRecruitmentWish(RecruitmentWishData selectedRecruitmentWish) {
	this.selectedRecruitmentWish = selectedRecruitmentWish;
    }

    public boolean isCanEdit() {
	return canEdit;
    }

    public int getPageSize() {
	return pageSize;
    }

    public List<Rank> getRanks() {
	return ranks;
    }

    public List<Region> getRegions() {
	return regions;
    }

    public List<RecruitmentWishData> getRecruitmentWishDataList() {
	return recruitmentWishDataList;
    }
}
