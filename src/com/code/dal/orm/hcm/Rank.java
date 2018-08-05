package com.code.dal.orm.hcm;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_rank_searchRank",
		query = " select r from Rank r where " +
			" (:P_ID = -1 or r.id = :P_ID) " +
			" and (:P_DESC = '-1' or r.description like :P_DESC) " +
			" and (:P_CATEGORIES_IDS_FLAG = -1 or r.categoryId in ( :P_CATEGORIES_IDS )) " +
			" order by r.id "
	)
})
@Entity
@Table(name = "HCM_RANKS")
public class Rank extends BaseEntity {
    private Long id;
    private String description;
    private String latinDescription;
    private Long categoryId;
    private Long subCategoryId;
    private Long subCategoryLevel;

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "DESCRIPTION")
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    @Basic
    @Column(name = "L_DESC")
    public String getLatinDescription() {
	return latinDescription;
    }

    public void setLatinDescription(String latinDescription) {
	this.latinDescription = latinDescription;
    }

    @Basic
    @Column(name = "CATEGORY_ID")
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
    }

    @Basic
    @Column(name = "SUBCATEGORY_ID")
    public Long getSubCategoryId() {
	return subCategoryId;
    }

    public void setSubCategoryId(Long subCategoryId) {
	this.subCategoryId = subCategoryId;
    }

    @Basic
    @Column(name = "SUBCATEGORY_LEVEL")
    public Long getSubCategoryLevel() {
	return subCategoryLevel;
    }

    public void setSubCategoryLevel(Long subCategoryLevel) {
	this.subCategoryLevel = subCategoryLevel;
    }
}
