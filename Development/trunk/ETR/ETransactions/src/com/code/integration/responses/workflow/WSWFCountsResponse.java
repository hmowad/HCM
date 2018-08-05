package com.code.integration.responses.workflow;

import com.code.integration.responses.WSResponseBase;

public class WSWFCountsResponse extends WSResponseBase {

    private long inboxCount;
    private long notificationsCount;
    private long outboxCount;
    private long totalDelegationsFromCount;
    private long totalDelegationsToCount;
    private long partialDelegationsFromCount;
    private long partialDelegationsToCount;

    public long getInboxCount() {
	return inboxCount;
    }

    public void setInboxCount(long inboxCount) {
	this.inboxCount = inboxCount;
    }

    public long getNotificationsCount() {
	return notificationsCount;
    }

    public void setNotificationsCount(long notificationsCount) {
	this.notificationsCount = notificationsCount;
    }

    public long getOutboxCount() {
	return outboxCount;
    }

    public void setOutboxCount(long outboxCount) {
	this.outboxCount = outboxCount;
    }

    public long getTotalDelegationsToCount() {
	return totalDelegationsToCount;
    }

    public void setTotalDelegationsToCount(long totalDelegationsToCount) {
	this.totalDelegationsToCount = totalDelegationsToCount;
    }

    public long getPartialDelegationsToCount() {
	return partialDelegationsToCount;
    }

    public void setPartialDelegationsToCount(long partialDelegationsToCount) {
	this.partialDelegationsToCount = partialDelegationsToCount;
    }

    public long getTotalDelegationsFromCount() {
	return totalDelegationsFromCount;
    }

    public void setTotalDelegationsFromCount(long totalDelegationsFromCount) {
	this.totalDelegationsFromCount = totalDelegationsFromCount;
    }

    public long getPartialDelegationsFromCount() {
	return partialDelegationsFromCount;
    }

    public void setPartialDelegationsFromCount(long partialDelegationsFromCount) {
	this.partialDelegationsFromCount = partialDelegationsFromCount;
    }

}
