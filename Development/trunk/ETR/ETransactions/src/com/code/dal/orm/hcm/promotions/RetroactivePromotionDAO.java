package com.code.dal.orm.hcm.promotions;

import java.util.Date;

public interface RetroactivePromotionDAO {

    public Long calculateNewDegree(Long empId, Long rankId, Date effectiveDate);
}
