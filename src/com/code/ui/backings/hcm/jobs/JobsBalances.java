package com.code.ui.backings.hcm.jobs;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.hcm.organization.jobs.JobsBalanceData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.JobsBalancesService;
import com.code.services.security.SecurityService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "jobsBalances")
@ViewScoped
public class JobsBalances extends BaseBacking {

    private int mode;
    private List<JobsBalanceData> jobsBalancesData;
    private List<Region> regions;
    private Long regionId;
    private Long categoryId;

    private boolean hasModifyPrivilege = false;

    public JobsBalances() {
	try {
	    if (getRequest().getParameter("mode") != null) {
		mode = Integer.parseInt(getRequest().getParameter("mode"));
		switch (mode) {
		case 1:
		    setScreenTitle(getMessage("title_officersJobsBalances"));
		    categoryId = CategoriesEnum.OFFICERS.getCode();
		    hasModifyPrivilege = SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.JOBS_BALANCES_FOR_OFFICERS.getCode(), MenuActionsEnum.JOBS_BALANCES_MODIFY.getCode());
		    break;
		case 2:
		    setScreenTitle(getMessage("title_soldiersJobsBalances"));
		    categoryId = CategoriesEnum.SOLDIERS.getCode();
		    hasModifyPrivilege = SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.JOBS_BALANCES_FOR_SOLDIERS.getCode(), MenuActionsEnum.JOBS_BALANCES_MODIFY.getCode());
		    break;

		default:
		    setServerSideErrorMessages(getMessage("error_general"));
		}
	    } else {
		setServerSideErrorMessages(getMessage("error_general"));
	    }

	    regionId = (long) FlagsEnum.ALL.getCode();
	    regions = CommonService.getAllRegions();

	    getJobsBalances();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void getJobsBalances() {
	try {
	    jobsBalancesData = JobsBalancesService.getJobsBalancesData(regionId, categoryId);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void modifyJobsBalance(JobsBalanceData jobsBalanceData) {
	try {
	    JobsBalancesService.modifyJobsBalance(jobsBalanceData, this.loginEmpData.getEmpId());
	    getJobsBalances();
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<Region> getRegions() {
	return regions;
    }

    public void setRegions(List<Region> regions) {
	this.regions = regions;
    }

    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
    }

    public List<JobsBalanceData> getJobsBalancesData() {
	return jobsBalancesData;
    }

    public void setJobsBalancesData(List<JobsBalanceData> jobsBalancesData) {
	this.jobsBalancesData = jobsBalancesData;
    }

    public boolean getHasModifyPrivilege() {
	return hasModifyPrivilege;
    }

}
