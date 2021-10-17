package com.code.services.hcm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<GeneralNews> getGeneralNewsWithPagination(Integer offset, Integer pageSize) throws BusinessException {
	try {

	    Map<String, Object> qParams = new HashMap<String, Object>();

	    return DataAccess.executeNamedQueryWithPagination(QueryNamesEnum.HCM_GET_GENERAL_NEWS.getCode(), offset, pageSize, qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static Long getGeneralNewsCount() throws BusinessException {
	try {

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    List<Long> count = DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_GENERAL_NEWS.getCode(), qParams);
	    return (count.isEmpty() || count == null ? 0 : count.get(0));
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }
}
