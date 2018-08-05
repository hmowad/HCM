package com.code.ui.backings.hcm.movements;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.organization.Region;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.RanksEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.MovementsWishesService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "movementsWishesReports")
@ViewScoped
public class MovementsWishesReports extends BaseBacking {

    private long fromRegionId;
    private long toRegionId;
    private long rankId;
    private Integer serviceYearsFrom;
    private Integer serviceYearsTo;
    private Integer employeeCount;
    private List<Rank> ranksList;
    private List<Region> regions;
    private Integer minServicePeriod;
    private String jobDesc;

    public MovementsWishesReports() {
	try {
	    regions = CommonService.getAllRegions();
	    minServicePeriod = MovementsWishesService.getMovementWishesMinServicePeriod();
	    prepareRanksList();
	    reset();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    private void prepareRanksList() {
	try {
	    ranksList = CommonService.getRanks(null, (Long[]) getCategoriesIdsArrayByMode((int) CategoriesEnum.SOLDIERS.getCode()));
	    for (int i = 0; i < ranksList.size(); i++) {
		if (ranksList.get(i).getId().equals(RanksEnum.STUDENT_SOLDIER.getCode())) {
		    ranksList.remove(i);
		    break;
		}
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void reset() {

	fromRegionId = toRegionId = rankId = FlagsEnum.ALL.getCode();
	serviceYearsTo = employeeCount = null;
	serviceYearsFrom = minServicePeriod;
	jobDesc = null;
    }

    public void print() {
	try {
	    byte[] bytes = MovementsWishesService.getMovementWishTransactionsBytes(fromRegionId, toRegionId, rankId, serviceYearsFrom == null ? FlagsEnum.ALL.getCode() : serviceYearsFrom, serviceYearsTo == null ? FlagsEnum.ALL.getCode() : serviceYearsTo, employeeCount == null ? FlagsEnum.ALL.getCode() : employeeCount, jobDesc);
	    super.print(bytes);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public long getFromRegionId() {
	return fromRegionId;
    }

    public void setFromRegionId(long fromRegionId) {
	this.fromRegionId = fromRegionId;
    }

    public long getToRegionId() {
	return toRegionId;
    }

    public void setToRegionId(long toRegionId) {
	this.toRegionId = toRegionId;
    }

    public long getRankId() {
	return rankId;
    }

    public void setRankId(long rankId) {
	this.rankId = rankId;
    }

    public Integer getServiceYearsFrom() {
	return serviceYearsFrom;
    }

    public void setServiceYearsFrom(Integer serviceYearsFrom) {
	this.serviceYearsFrom = serviceYearsFrom;
    }

    public Integer getServiceYearsTo() {
	return serviceYearsTo;
    }

    public void setServiceYearsTo(Integer serviceYearsTo) {
	this.serviceYearsTo = serviceYearsTo;
    }

    public Integer getEmployeeCount() {
	return employeeCount;
    }

    public void setEmployeeCount(Integer employeeCount) {
	this.employeeCount = employeeCount;
    }

    public List<Region> getRegions() {
	return regions;
    }

    public void setRegions(List<Region> regions) {
	this.regions = regions;
    }

    public List<Rank> getRanksList() {
	return ranksList;
    }

    public void setRanksList(List<Rank> ranksList) {
	this.ranksList = ranksList;
    }

    public Integer getMinServicePeriod() {
	return minServicePeriod;
    }

    public String getJobDesc() {
	return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
	this.jobDesc = jobDesc;
    }

}
