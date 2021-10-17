package com.code.dal.orm.security;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

@NamedQueries({
	@NamedQuery(name = "sec_menu_getEmployeeMenus",
		query = " select m from Menu m, EmployeeMenu em " +
			" where m.menuId = em.menuId " +
			"   and em.empId = :P_EMP_ID " +
			"   and m.menuType = :P_MENU_TYPE " +
			"   and m.moduleId = :P_MODULE_ID " +
			"   and m.activeFlag = 1 " +
			" order by m.orderBy "),

	@NamedQuery(name = "sec_menu_getExternalMenus",
		query = " select m from Menu m " +
			" where m.externalMenu = 1 " +
			" and m.activeFlag = 1 " +
			" and m.moduleId = :P_MODULE_ID" +
			" order by m.orderBy "),

	@NamedQuery(name = "sec_menu_getEmployeeMenusShowInMobile",
		query = " select m from Menu m, EmployeeMenu em " +
			" where m.menuId = em.menuId " +
			"   and em.empId = :P_EMP_ID " +
			"   and m.menuType in (1,2,3) " +
			"   and m.moduleId = :P_MODULE_ID " +
			"   and (:P_SHOW_IN_MOBILE = -1 or m.showInMobile = :P_SHOW_IN_MOBILE) " +
			"   and m.activeFlag = 1 " +
			" order by m.orderBy "),
})

@Entity
@Table(name = "ETR_VW_MENUS")
public class Menu {
    private Long menuId;
    private String nameKey;
    private Long parentId;
    private Integer orderBy;
    private String url;
    private Integer menuType;
    private Integer activeFlag;
    private Integer menuFlag;
    private Integer externalMenu;
    private Long moduleId;
    private String arabicName;
    private String englishName;
    private Integer showInMobile;
    private List<Menu> subMenus;

    @Id
    @Column(name = "ID")
    public Long getMenuId() {
	return menuId;
    }

    public void setMenuId(Long menuId) {
	this.menuId = menuId;
    }

    @Basic
    @Column(name = "NAME_KEY")
    public String getNameKey() {
	return nameKey;
    }

    public void setNameKey(String nameKey) {
	this.nameKey = nameKey;
    }

    @Basic
    @Column(name = "PARENT_ID")
    public Long getParentId() {
	return parentId;
    }

    public void setParentId(Long parentId) {
	this.parentId = parentId;
    }

    @Basic
    @Column(name = "ORDER_BY")
    public Integer getOrderBy() {
	return orderBy;
    }

    public void setOrderBy(Integer orderBy) {
	this.orderBy = orderBy;
    }

    @Basic
    @Column(name = "URL")
    public String getUrl() {
	return url;
    }

    public void setUrl(String url) {
	this.url = url;
    }

    @Basic
    @Column(name = "MENU_TYPE")
    public Integer getMenuType() {
	return menuType;
    }

    public void setMenuType(Integer menuType) {
	this.menuType = menuType;
    }

    @Basic
    @Column(name = "ACTIVE_FLAG")
    public Integer getActiveFlag() {
	return activeFlag;
    }

    public void setActiveFlag(Integer activeFlag) {
	this.activeFlag = activeFlag;
    }

    @Basic
    @Column(name = "MENU_FLAG")
    public Integer getMenuFlag() {
	return menuFlag;
    }

    public void setMenuFlag(Integer menuFlag) {
	this.menuFlag = menuFlag;
    }

    @Basic
    @Column(name = "EXTERNAL_MENU")
    public Integer getExternalMenu() {
	return externalMenu;
    }

    public void setExternalMenu(Integer externalMenu) {
	this.externalMenu = externalMenu;
    }

    @Basic
    @Column(name = "MODULE_ID")
    public Long getModuleId() {
	return moduleId;
    }

    public void setModuleId(Long moduleId) {
	this.moduleId = moduleId;
    }

    @Basic
    @Column(name = "ARABIC_NAME")
    public String getArabicName() {
	return arabicName;
    }

    public void setArabicName(String arabicName) {
	this.arabicName = arabicName;
    }

    @Basic
    @Column(name = "LATIN_NAME")
    public String getEnglishName() {
	return englishName;
    }

    public void setEnglishName(String englishName) {
	this.englishName = englishName;
    }

    @Basic
    @Column(name = "SHOW_IN_MOBILE")
    @XmlTransient
    public Integer getShowInMobile() {
	return showInMobile;
    }

    public void setShowInMobile(Integer showInMobile) {
	this.showInMobile = showInMobile;
    }

    @Transient
    public List<Menu> getSubMenus() {
	return subMenus;
    }

    public void setSubMenus(List<Menu> subMenus) {
	this.subMenus = subMenus;
    }
}