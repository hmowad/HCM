package com.code.dal.orm.hcm.promotions;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;

public class PromotionRankingDataId implements Serializable {

    private Long reportId;
    private Long reportDetailId;

    public PromotionRankingDataId() {
    }

    public PromotionRankingDataId(Long reportId, Long reportDetailId) {
	this.reportId = reportId;
	this.reportDetailId = reportDetailId;
    }

    @Id
    @Column(name = "REPORT_ID")
    public Long getReportId() {
	return reportId;
    }

    public void setReportId(Long reportId) {
	this.reportId = reportId;
    }

    @Id
    @Column(name = "REPORT_DETAIL_ID")
    public Long getReportDetailId() {
	return reportDetailId;
    }

    public void setReportDetailId(Long reportDetailId) {
	this.reportDetailId = reportDetailId;
    }

    public boolean equals(Object object) {

	if (this == object)
	    return true;

	if (!(object instanceof PromotionRankingDataId))
	    return false;

	final PromotionRankingDataId other = (PromotionRankingDataId) object;

	if (!(reportId == null ? other.reportId == null : reportId.equals(other.reportId)))
	    return false;

	if (!(reportDetailId == null ? other.reportDetailId == null : reportDetailId.equals(other.reportDetailId)))
	    return false;

	return true;
    }

    public int hashCode() {
	return reportId.hashCode() + reportDetailId.hashCode();
    }

}
