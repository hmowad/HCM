package com.code.ui.backings.hcm.vacations;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.organization.Region;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.VacationsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "vacationsReports")
@ViewScoped
public class VacationsReports extends BaseBacking implements Serializable {

    private List<Region> regionList;
    private long selectedRegionId;

    private long selectedCategoryId;
    private Date fromDate;
    private Date toDate;

    public VacationsReports() {
	regionList = CommonService.getAllRegions();
	resetForm();
    }

    public void resetForm() {
	try {
	    selectedCategoryId = FlagsEnum.ALL.getCode();
	    selectedRegionId = getLoginEmpPhysicalRegionFlag(true);
	    fromDate = toDate = HijriDateService.getHijriSysDate();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void print() {
	try {
	    String reportTitle = getMessage("title_vacationsReportsTitle");

	    String regionDesc = getMessage("label_all");
	    if (selectedRegionId != FlagsEnum.ALL.getCode()) {
		for (Region region : regionList) {
		    if (region.getId().longValue() == selectedRegionId) {
			regionDesc = region.getDescription();
			break;
		    }
		}
	    }

	    byte[] bytes = VacationsService.getVacationsReportsBytes(10, selectedRegionId, regionDesc, null, null, selectedCategoryId, fromDate, toDate,
		    FlagsEnum.OFF.getCode(), FlagsEnum.OFF.getCode(), (long) FlagsEnum.OFF.getCode(), null, null, (long) FlagsEnum.OFF.getCode(), "",
		    (long) FlagsEnum.OFF.getCode(), "", null, null, null, reportTitle);
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public long getSelectedRegionId() {
	return selectedRegionId;
    }

    public void setSelectedRegionId(long selectedRegionId) {
	this.selectedRegionId = selectedRegionId;
    }

    public long getSelectedCategoryId() {
	return selectedCategoryId;
    }

    public void setSelectedCategoryId(long selectedCategoryId) {
	this.selectedCategoryId = selectedCategoryId;
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

    public List<Region> getRegionList() {
	return regionList;
    }

    public void setRegionList(List<Region> regionList) {
	this.regionList = regionList;
    }
}
