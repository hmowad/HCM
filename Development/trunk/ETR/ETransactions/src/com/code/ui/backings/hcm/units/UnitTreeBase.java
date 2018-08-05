package com.code.ui.backings.hcm.units;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.FlagsEnum;
import com.code.enums.UnitTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.UnitsService;
import com.code.ui.backings.base.BaseBacking;
import com.code.ui.util.UnitTreeNode;

public abstract class UnitTreeBase extends BaseBacking {
    protected String searchName = "";

    protected UnitTreeNode unitTree;
    protected UnitTreeNode selectedUnitTreeNode;
    protected UnitData selectedUnitData;

    public void init() {
	try {
	    selectedUnitData = UnitsService.getUnitsByType(UnitTypesEnum.PRESIDENCY.getCode()).get(0);
	    unitTree = new UnitTreeNode();
	    unitTree.setId(selectedUnitData.getId());
	    unitTree.setUnitName(selectedUnitData.getName());
	    unitTree.setUnitType(selectedUnitData.getUnitTypeCode().toString());
	    unitTree.setOrderUnderParent(selectedUnitData.getOrderUnderParent().toString());
	    treeToggleHandler(unitTree);
	    unitTree.setExpanded(true);
	    selectedUnitTreeNode = unitTree;
	    searchName = "";
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void search() {
	if (searchName.equals("")) {
	    init();
	} else {
	    try {
		List<UnitData> unitDataList = UnitsService.getAllLevelUnitsByName(searchName);
		if (unitDataList.size() > 0) {
		    selectedUnitData = unitDataList.get(0);
		    unitTree = fillChildren(unitDataList, selectedUnitData.getId());
		} else {
		    selectedUnitData = new UnitData();
		    unitTree = new UnitTreeNode();
		    this.setServerSideErrorMessages(getMessage("error_noData"));
		}
	    } catch (BusinessException e) {
		this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    }
	}
    }

    protected void removeSelected() {
	if (selectedUnitTreeNode.getParent().getChildrenList().size() == 1) {
	    selectedUnitTreeNode.getParent().setLeaf(true);
	}

	UnitTreeNode parent = (UnitTreeNode) selectedUnitTreeNode.getParent();
	parent.getChildrenList().remove(selectedUnitTreeNode);
	click(parent);
    }

    protected void addChildUnderSelected(Long id, String name, String unitTypeCode, String orderUnderParent) {
	UnitTreeNode child = new UnitTreeNode();
	child.setId(id);
	child.setUnitName(name);
	child.setUnitType(unitTypeCode);
	child.setOrderUnderParent(orderUnderParent);
	child.setParent(selectedUnitTreeNode);
	child.setLeaf(true);

	selectedUnitTreeNode.getChildrenList().add(child);
	selectedUnitTreeNode.setLeaf(false);
    }

    private UnitTreeNode fillChildren(List<UnitData> unitDataList, Long rootId) {
	Map<Long, UnitTreeNode> treeMap = new HashMap<Long, UnitTreeNode>();
	for (UnitData item : unitDataList) {
	    UnitTreeNode nodeParent = treeMap.get(item.getParentUnitId());

	    UnitTreeNode child = new UnitTreeNode();
	    child.setId(item.getId());
	    child.setUnitName(item.getName());
	    child.setUnitType(item.getUnitTypeCode().toString());
	    child.setOrderUnderParent(item.getOrderUnderParent().toString());
	    if (nodeParent != null) {
		nodeParent.setLeaf(false);
		child.setParentId(nodeParent.getId());
		child.setParent(nodeParent);
		nodeParent.getChildrenList().add(child);
	    }
	    child.setVisited(true);
	    child.setExpanded(true);
	    child.setLeaf(true);

	    if (!treeMap.containsKey(child.getId())) {
		treeMap.put(child.getId(), child);
	    }
	}

	return treeMap.get(rootId);
    }

    public void click(UnitTreeNode unitItem) {
	try {
	    selectedUnitTreeNode = unitItem;
	    if (selectedUnitData.getId().longValue() != unitItem.getId().longValue()) {
		selectedUnitData = UnitsService.getUnitById(unitItem.getId());
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void treeToggleHandler(UnitTreeNode selectedUnitItem) {
	if (!selectedUnitItem.isVisited()) {
	    try {
		List<UnitData> unitDataList = UnitsService.getUnitChildren(selectedUnitItem.getId(), false, false, FlagsEnum.ON.getCode());
		List<Long> unitDataParentIdList = UnitsService.getUnitParentChildrenIds(selectedUnitItem.getId());
		for (UnitData childItem : unitDataList) {
		    UnitTreeNode child = new UnitTreeNode();
		    child.setId(childItem.getId());
		    child.setUnitName(childItem.getName());
		    child.setUnitType(childItem.getUnitTypeCode().toString());
		    child.setOrderUnderParent(childItem.getOrderUnderParent().toString());
		    child.setParentId(selectedUnitItem.getId());
		    child.setParent(selectedUnitItem);
		    child.setLeaf(true);
		    for (Long id : unitDataParentIdList) {
			if (childItem.getId().longValue() == id.longValue()) {
			    child.setLeaf(false);
			    break;
			}
		    }
		    selectedUnitItem.getChildrenList().add(child);
		}
		selectedUnitItem.setVisited(true);

	    } catch (BusinessException e) {
		this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    }
	}
    }

    public UnitTreeNode getUnitTree() {
	return unitTree;
    }

    public void setUnitTree(UnitTreeNode unitTree) {
	this.unitTree = unitTree;
    }

    public UnitData getSelectedUnitData() {
	return selectedUnitData;
    }

    public void setSelectedUnitData(UnitData selectedUnitData) {
	this.selectedUnitData = selectedUnitData;
    }

    public String getSearchName() {
	return searchName;
    }

    public void setSearchName(String searchName) {
	this.searchName = searchName;
    }
}
