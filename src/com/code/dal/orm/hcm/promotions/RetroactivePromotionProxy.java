package com.code.dal.orm.hcm.promotions;

import java.util.Date;

public class RetroactivePromotionProxy implements RetroactivePromotionDAO {

    @Override
    public Long calculateNewDegree(Long empId, Long rankId, Date effectiveDate) {
	if (empId == 3000072 && rankId == 109)
	    return 3L;
	else if (empId == 8372 && rankId == 109)
	    return null;
	else if (empId == 9 && rankId == 109)
	    return 5L;
	return null;
    }

}
