package com.code.services.hcm;

import java.util.List;

import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.generalNews.GeneralNews;
import com.code.enums.QueryNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;

public class GeneralNewsService {

    private GeneralNewsService() {
    }

    public static List<GeneralNews> getGeneralNews() throws BusinessException {
	try {
	    return DataAccess.executeNamedQuery(GeneralNews.class, QueryNamesEnum.HCM_GET_GENERAL_NEWS.getCode(), null);
	} catch (DatabaseException e) {
	    throw new BusinessException("error_general");
	}
    }
}
