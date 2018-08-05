package com.code.ui.backings.hcm.movements;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.organization.Region;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.MovementsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "movementsReports")
@ViewScoped
public class MovementsReports extends BaseBacking implements Serializable {

    private List<Region> regionList;
    private long selectedRegionId;

    private long selectedCategoryId;
    private Date fromDate;
    private Date toDate;

    public MovementsReports() {
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
	    String regionDesc = getMessage("label_all");
	    if (selectedRegionId != FlagsEnum.ALL.getCode()) {
		for (Region region : regionList) {
		    if (region.getId().longValue() == selectedRegionId) {
			regionDesc = region.getDescription();
			break;
		    }
		}
	    }

	    byte[] bytes = MovementsService.getMovementsReportsBytes(selectedRegionId, regionDesc, selectedCategoryId, fromDate, toDate, getMessage("title_movementStatisticalReport"));
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
