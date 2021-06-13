package com.code.dal.orm.hcm.promotions;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_promotionNotification_searchPromotionNotification",
		query = " select d from PromotionNotification d " +
			" where (:P_INSTANCE_ID = -1 or d.instanceId = :P_INSTANCE_ID) "
			+ "and (:P_TRANSACTION_ID = -1 or d.transactionId = :P_TRANSACTION_ID)"),
})

@Entity
@Table(name = "WF_PROMOTION_NOTIFICATIONS")
public class PromotionNotification extends BaseEntity {

    private Long instanceId;
    private Long transactionId;

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
    }

    @Id
    @Column(name = "TRANSACTION_ID")
    public Long getTransactionId() {
	return transactionId;
    }

    public void setTransactionId(Long transactionId) {
	this.transactionId = transactionId;
    }

}
