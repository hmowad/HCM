package com.code.ui.backings.minisearch;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.organization.Region;
import com.code.enums.FlagsEnum;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "regionsMiniSearch")
@ViewScoped
public class RegionsMiniSearch extends BaseBacking {

    private List<Region> regionsList = new ArrayList<Region>();

    private List<Region> selectedRegionsList = new ArrayList<Region>();

    private String totalRegionsCodes;

    private String totalRegionsNames;

    private int mode;

    public RegionsMiniSearch() {
	super.init();

	if (getRequest().getParameter("mode") != null)
	    mode = Integer.parseInt(getRequest().getParameter("mode"));

	List<Region> tempRegionsList = CommonService.getAllRegions();

	switch (mode) {
	case 1: /* Get all regions */
	    for (Region region : tempRegionsList) {
		regionsList.add(region);
	    }
	    break;
	case 2: /* Get all regions plus other region */

	    for (Region region : tempRegionsList) {
		regionsList.add(region);
	    }
	    Region otherRegion = new Region();
	    otherRegion.setId((long) FlagsEnum.ALL.getCode());
	    otherRegion.setCode(FlagsEnum.ALL.getCode() + "");
	    otherRegion.setDescription(getMessage("lable_otherRegions"));
	    regionsList.add(otherRegion);
	    break;
	}

    }

    public void addSelectedRegion(Region region) {
	selectedRegionsList.add(region);
	regionsList.remove(region);
	generateRegionsNames();
    }

    public void removeSelectedRegion(Region region) {
	selectedRegionsList.remove(region);
	regionsList.add(region);
	generateRegionsNames();
    }

    private void generateRegionsNames() {
	totalRegionsCodes = "";
	totalRegionsNames = "";
	String comma = "", slash = "";
	if (selectedRegionsList.size() > 0) {
	    for (Region region : selectedRegionsList) {
		totalRegionsCodes += comma + region.getCode();
		totalRegionsNames += slash + region.getDescription();
		comma = ",";
		slash = "/";
	    }
	}
    }

    public List<Region> getRegionsList() {
	return regionsList;
    }

    public void setRegionsList(List<Region> regionsList) {
	this.regionsList = regionsList;
    }

    public List<Region> getSelectedRegionsList() {
	return selectedRegionsList;
    }

    public void setSelectedRegionsList(List<Region> selectedRegionsList) {
	this.selectedRegionsList = selectedRegionsList;
    }

    public String getTotalRegionsCodes() {
	return totalRegionsCodes;
    }

    public void setTotalRegionsCodes(String totalRegionsCodes) {
	this.totalRegionsCodes = totalRegionsCodes;
    }

    public String getTotalRegionsNames() {
	return totalRegionsNames;
    }

    public void setTotalRegionsNames(String totalRegionsNames) {
	this.totalRegionsNames = totalRegionsNames;
    }
}