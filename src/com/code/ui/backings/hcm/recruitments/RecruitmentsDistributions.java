package com.code.ui.backings.hcm.recruitments;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.hcm.recruitments.RecruitmentDistributionData;
import com.code.enums.RanksEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.RecruitmentsService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "recruitmentsDistributions")
@ViewScoped
public class RecruitmentsDistributions extends BaseBacking implements Serializable {
    private List<RecruitmentDistributionData> distributions;
    private List<Rank> ranks;
    private List<Region> regions;
    private int pageSize = 10;

    public RecruitmentsDistributions() {
	try {
	    distributions = RecruitmentsService.getAllRecruitmentDistributions();

	    ranks = CommonService.getRanks(null, CommonService.getSoldiersCategoriesIdsArray());
	    for (int i = 0; i < ranks.size(); i++) {
		if (ranks.get(i).getId().equals(RanksEnum.STUDENT_SOLDIER.getCode())) {
		    ranks.remove(i);
		    break;
		}
	    }

	    regions = CommonService.getAllRegions();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addNewDistribution() {
	distributions.add(new RecruitmentDistributionData());
    }

    public void deleteDistribution(RecruitmentDistributionData distribution) {
	try {
	    if (distribution.getId() != null) {
		distribution.getRecruitmentDistribution().setSystemUser(loginEmpData.getEmpId() + ""); // For auditing
		RecruitmentsService.deleteRecruitmentDistribution(distribution);
	    }

	    distributions.remove(distribution);
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void saveDistribution(RecruitmentDistributionData distribution) {
	try {
	    distribution.getRecruitmentDistribution().setSystemUser(loginEmpData.getEmpId() + ""); // For auditing
	    RecruitmentsService.addRecruitmentDistribution(distribution);
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    public void updateDistribution(RecruitmentDistributionData distribution) {
	try {
	    distribution.getRecruitmentDistribution().setSystemUser(loginEmpData.getEmpId() + ""); // For auditing
	    RecruitmentsService.updateRecruitmentDistribution(distribution);
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printNewSoldiersNumbersStatement() {
	try {
	    byte[] reportBytes = RecruitmentsService.getNewSoldiersCountStatementDecisionBytes();
	    this.print(reportBytes);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void distributeSoldiers() {
	try {
	    RecruitmentsService.distributeSoldiers();
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<RecruitmentDistributionData> getDistributions() {
	return distributions;
    }

    public void setDistributions(List<RecruitmentDistributionData> distributions) {
	this.distributions = distributions;
    }

    public int getPageSize() {
	return pageSize;
    }

    public List<Rank> getRanks() {
	return ranks;
    }

    public void setRanks(List<Rank> ranks) {
	this.ranks = ranks;
    }

    public List<Region> getRegions() {
	return regions;
    }

    public void setRegions(List<Region> regions) {
	this.regions = regions;
    }

}
