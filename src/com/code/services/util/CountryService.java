package com.code.services.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.DataAccess;
import com.code.dal.orm.setup.Country;
import com.code.dal.orm.setup.CountryBlacklist;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;

public class CountryService {

    private CountryService() {
    }

    // ------------------------------- Country entity operations -------------------
    public static List<Country> listEmbassies() throws BusinessException {
	return getCountries(FlagsEnum.ALL.getCode() + "", null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ON.getCode());
    }

    public static Country getEmbassyById(long embassyId) throws BusinessException {
	List<Country> result = getCountries(FlagsEnum.ALL.getCode() + "", null, FlagsEnum.ALL.getCode(), embassyId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
	if (result.isEmpty())
	    return null;
	else
	    return result.get(0);
    }

    public static List<Country> getCountryByCountryDesc(String countryDesc, int blackList) throws BusinessException {
	return getCountries(FlagsEnum.ALL.getCode() + "", countryDesc, blackList, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
    }

    public static Country getCountryByCode(String countryCode) throws BusinessException {
	List<Country> result = getCountries(countryCode, FlagsEnum.ALL.getCode() + "", FlagsEnum.ALL.getCode(), (long) FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
	if (result.isEmpty())
	    return null;
	else
	    return result.get(0);
    }

    private static List<Country> getCountries(String countryCode, String countryDesc, int blackList, long Id, int countryFlag, int embassyFlag) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_COUNTRY_CODE", (countryCode == null || countryCode.equals("")) ? FlagsEnum.ALL.getCode() + "" : countryCode);
	    qParams.put("P_COUNTRY_DESC", (countryDesc == null || countryDesc.equals("") || countryDesc.equals(FlagsEnum.ALL.getCode() + "")) ? FlagsEnum.ALL.getCode() + "" : "%" + countryDesc + "%");
	    qParams.put("P_BLACK_LIST", blackList);
	    qParams.put("P_ID", Id);
	    qParams.put("P_COUNTRY_FLAG", countryFlag);
	    qParams.put("P_EMBASSY_FLAG", embassyFlag);

	    return DataAccess.executeNamedQuery(Country.class, QueryNamesEnum.HCM_GET_COUNTRY_BY_COUNTRY_DESC.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static Country getCountryByYaqeenName(String yaqeenName) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    if (yaqeenName != null && !yaqeenName.trim().isEmpty()) {
		qParams.put("P_YAQEEN_NAME", new String[] { yaqeenName, "NA" });
	    } else {
		qParams.put("P_YAQEEN_NAME", new String[] { FlagsEnum.ALL.getCode() + "" });
	    }
	    List<Country> result = DataAccess.executeNamedQuery(Country.class, QueryNamesEnum.HCM_GET_COUNTRY_BY_YAQEEN_NAME.getCode(), qParams);
	    if (result.isEmpty())
		return null;
	    else {
		return result.get(0);
	    }
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    // ------------------------------- Country Blacklist entity operations -------------------
    public static void updateCountryBlackListFlag(Country country, Long userId) throws BusinessException {
	CountryBlacklist result = getCountryBlacklistByCountryId(country.getId());

	if (country.getBlackListFlag() == FlagsEnum.OFF.getCode() && result != null) {
	    result.setSystemUser(userId + "");
	    deleteCountryBlacklistRecord(result);
	}

	if (country.getBlackListFlag() == FlagsEnum.ON.getCode() && result == null) {
	    CountryBlacklist countryBlacklist = new CountryBlacklist();
	    countryBlacklist.setCountryId(country.getId());
	    countryBlacklist.setSystemUser(userId + "");
	    insertCountryBlacklistRecord(countryBlacklist);
	}

    }

    private static CountryBlacklist getCountryBlacklistByCountryId(Long countryId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_COUNTRY_ID", countryId);

	    List<CountryBlacklist> result = DataAccess.executeNamedQuery(CountryBlacklist.class, QueryNamesEnum.HCM_GET_COUNTRY_BLACKLIST_BY_COUNTRY_ID.getCode(), qParams);
	    if (result.isEmpty())
		return null;
	    else
		return result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static void insertCountryBlacklistRecord(CountryBlacklist countryBlacklist) throws BusinessException {
	try {
	    DataAccess.addEntity(countryBlacklist);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static void deleteCountryBlacklistRecord(CountryBlacklist countryBlacklist) throws BusinessException {
	try {
	    DataAccess.deleteEntity(countryBlacklist);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }
}
