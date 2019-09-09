package com.code.services.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.hcm.GraduationGroupPlace;
import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.RankTitle;
import com.code.dal.orm.hcm.SocialIdIssuePlace;
import com.code.dal.orm.hcm.TransactionType;
import com.code.dal.orm.hcm.employees.medicalstuff.MedicalStaffLevel;
import com.code.dal.orm.hcm.employees.medicalstuff.MedicalStaffRank;
import com.code.dal.orm.hcm.organization.Region;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;

public class CommonService extends BaseService {
    private static Map<Long, Category> categoriesMap;
    private static Map<String, Region> regionsMap;
    private static Map<String, TransactionType> transactionTypesMap;
    private static Map<Long, Rank> ranksMap;
    private static Map<Long, RankTitle> rankTitlesMap;

    private static List<Region> regionsList;

    public static void init() {
	List<Category> categoriesList;
	List<TransactionType> transactionTypesList;
	List<Rank> ranksList;
	List<RankTitle> rankTitlesList;
	try {
	    categoriesMap = new HashMap<Long, Category>();
	    regionsMap = new HashMap<String, Region>();
	    transactionTypesMap = new HashMap<String, TransactionType>();
	    ranksMap = new HashMap<Long, Rank>();
	    rankTitlesMap = new HashMap<Long, RankTitle>();
	    categoriesList = searchCategories();
	    regionsList = searchRegions();
	    transactionTypesList = searchTransactionTypes();
	    ranksList = getRanks(null, null);
	    rankTitlesList = searchRankTitles();

	    for (Category category : categoriesList) {
		categoriesMap.put(category.getId(), category);
	    }

	    for (Region region : regionsList) {
		regionsMap.put(region.getCode(), region);
	    }

	    for (TransactionType transactionType : transactionTypesList) {
		transactionTypesMap.put(transactionType.getCode().intValue() + "_" + transactionType.getTransactionClass().intValue(), transactionType);
	    }

	    for (Rank rank : ranksList) {
		ranksMap.put(rank.getId(), rank);
	    }

	    for (RankTitle rankTitle : rankTitlesList) {
		rankTitlesMap.put(rankTitle.getId(), rankTitle);
	    }

	} catch (Exception e) {
	    System.out.println("Common Service initialization failed !!");
	    e.printStackTrace();
	}
    }

    private CommonService() {
    }

    /*-------------------------------------- Regions ------------------------------------*/
    public static List<Region> getAllRegions() {
	return regionsList;
    }

    public static Region getRegionByCode(String code) {
	return regionsMap.get(code);
    }

    public static Region getRegionById(long id) {
	return getRegionByCode(id < 10 ? "0" + id : "" + id);
    }

    private static List<Region> searchRegions() throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    return DataAccess.executeNamedQuery(Region.class, QueryNamesEnum.HCM_GET_REGION.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*---------------------------------- Social ID Issue Places -------------------------------*/

    public static List<SocialIdIssuePlace> getSocialIdIssuePlacesByDescription(String description) throws BusinessException {
	return searchSocialIdIssuePlaces(FlagsEnum.ALL.getCode(), description, false);
    }

    public static List<SocialIdIssuePlace> getSocialIdIssuePlacesByExactDescription(String description) throws BusinessException {
	return searchSocialIdIssuePlaces(FlagsEnum.ALL.getCode(), description, true);
    }

    private static List<SocialIdIssuePlace> searchSocialIdIssuePlaces(long id, String description, boolean exact) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ID", id);
	    qParams.put("P_DESC", (description == null || description.length() == 0) ? FlagsEnum.ALL.getCode() + "" : (exact ? description : "%" + description + "%"));

	    return DataAccess.executeNamedQuery(SocialIdIssuePlace.class, QueryNamesEnum.HCM_SEARCH_SOCIAL_ID_ISSUE_PLACES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*---------------------------------- Graduation Group Places ------------------------------*/
    public static GraduationGroupPlace getGraduationGroupPlaceById(long id) throws BusinessException {
	List<GraduationGroupPlace> list = searchGraduationGroupPlaces(id);
	if (list.size() > 0)
	    return list.get(0);

	return null;
    }

    public static List<GraduationGroupPlace> getAllGraduationGroupPlaces() throws BusinessException {
	return searchGraduationGroupPlaces(FlagsEnum.ALL.getCode());
    }

    private static List<GraduationGroupPlace> searchGraduationGroupPlaces(long id) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ID", id);
	    return DataAccess.executeNamedQuery(GraduationGroupPlace.class, QueryNamesEnum.HCM_GET_ALL_GRADUATION_GROUP_PLACES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*-------------------------------------- Transactions ------------------------------------*/
    /**
     * Gets transaction type by code and classCode
     * 
     * @param code
     *            : code value of transaction type
     * @param classCode
     *            : class code value of transaction
     * @return : returns only one record of TransactionType; null otherwise
     */
    public static TransactionType getTransactionTypeByCodeAndClass(int code, int classCode) {
	return transactionTypesMap.get(code + "_" + classCode);
    }

    private static List<TransactionType> searchTransactionTypes() throws BusinessException {
	try {
	    return DataAccess.executeNamedQuery(TransactionType.class, QueryNamesEnum.HCM_GET_TRANSACTION_TYPES.getCode(), null);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*-------------------------------------- Categories --------------------------------------*/
    public static List<Category> getMilitariesCategories() {
	List<Category> categories = new ArrayList<Category>();

	categories.add(categoriesMap.get(CategoriesEnum.OFFICERS.getCode()));
	categories.add(categoriesMap.get(CategoriesEnum.SOLDIERS.getCode()));

	return categories;
    }

    public static List<Category> getPersonsCategories() {
	List<Category> categories = new ArrayList<Category>();

	categories.add(categoriesMap.get(CategoriesEnum.PERSONS.getCode()));
	categories.add(categoriesMap.get(CategoriesEnum.USERS.getCode()));
	categories.add(categoriesMap.get(CategoriesEnum.WAGES.getCode()));
	categories.add(categoriesMap.get(CategoriesEnum.CONTRACTORS.getCode()));
	categories.add(categoriesMap.get(CategoriesEnum.MEDICAL_STAFF.getCode()));

	return categories;
    }

    public static List<Category> getCategoryClasses() {
	List<Category> categories = new ArrayList<Category>();
	for (Entry<Long, Category> entry : categoriesMap.entrySet()) {

	    if (entry.getValue().getId().longValue() == CategoriesEnum.SOLDIERS.getCode())
		continue;
	    Category category = new Category();
	    category.setId(entry.getValue().getId());
	    if (entry.getValue().getId().longValue() == CategoriesEnum.OFFICERS.getCode())
		category.setDescription(getMessage("label_militaryType"));
	    else
		category.setDescription(entry.getValue().getDescription());
	    categories.add(category);
	}
	return categories;
    }

    public static List<Category> getAllCategories() {
	return new ArrayList<Category>(categoriesMap.values());
    }

    public static Category getCategoryById(Long id) {
	return categoriesMap.get(id);
    }

    public static Long[] getOfficerCategoriesIdsArray() {
	return new Long[] { CategoriesEnum.OFFICERS.getCode() };
    }

    public static Long[] getSoldiersCategoriesIdsArray() {
	return new Long[] { CategoriesEnum.SOLDIERS.getCode() };
    }

    public static Long[] getCivilCategoriesIdsArray() {
	return new Long[] { CategoriesEnum.PERSONS.getCode(), CategoriesEnum.USERS.getCode(), CategoriesEnum.WAGES.getCode(), CategoriesEnum.CONTRACTORS.getCode(), CategoriesEnum.MEDICAL_STAFF.getCode() };
    }

    public static Long[] getCivilCategoriesIdsWithOutContractorsArray() {
	return new Long[] { CategoriesEnum.PERSONS.getCode(), CategoriesEnum.USERS.getCode(), CategoriesEnum.WAGES.getCode(), CategoriesEnum.MEDICAL_STAFF.getCode() };
    }

    public static Long[] getCivilContractorsCategoriesIdsArray() {
	return new Long[] { CategoriesEnum.CONTRACTORS.getCode() };
    }

    public static Long[] getAllCategoriesIdsArray() {
	return new Long[] { CategoriesEnum.OFFICERS.getCode(), CategoriesEnum.SOLDIERS.getCode(), CategoriesEnum.PERSONS.getCode(), CategoriesEnum.USERS.getCode(), CategoriesEnum.WAGES.getCode(), CategoriesEnum.CONTRACTORS.getCode(), CategoriesEnum.MEDICAL_STAFF.getCode() };
    }

    private static List<Category> searchCategories() throws BusinessException {
	try {
	    return DataAccess.executeNamedQuery(Category.class, QueryNamesEnum.HCM_GET_ALL_CATEGORIES.getCode(), null);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*----------------------------------------- Ranks ----------------------------------------*/
    /* The description is not used in any reference also we can manipulate the map and get the needed values instead of loading from DB */
    public static List<Rank> getRanks(String description, Long[] categoriesIds) throws BusinessException {
	return searchRanks(FlagsEnum.ALL.getCode(), description, categoriesIds);
    }

    public static Rank getRankById(long id) {
	return ranksMap.get(id);
    }

    private static List<Rank> searchRanks(long id, String description, Long[] categoriesIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ID", id);
	    qParams.put("P_DESC", (description == null || description.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : "%" + description + "%");
	    if (categoriesIds != null && categoriesIds.length > 0) {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_CATEGORIES_IDS", categoriesIds);
	    } else {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_CATEGORIES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }

	    return DataAccess.executeNamedQuery(Rank.class, QueryNamesEnum.HCM_SEARCH_RANKS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*---------------------------------- Ranks Titles ----------------------------------------*/
    private static List<RankTitle> searchRankTitles() throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    return DataAccess.executeNamedQuery(RankTitle.class, QueryNamesEnum.HCM_GET_All_RanksTitles.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<RankTitle> getAllRanksTitles() {
	return new ArrayList<RankTitle>(rankTitlesMap.values());
    }

    public static RankTitle getRankTitleById(long id) {
	return rankTitlesMap.get(id);
    }

    /*---------------------------------- Medical Stuff ----------------------------------------*/
    private static List<MedicalStaffRank> searchMedicalStaffRanks() throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    return DataAccess.executeNamedQuery(MedicalStaffRank.class, QueryNamesEnum.HCM_GET_ALL_MEDICAL_STAFF_RANKS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<MedicalStaffLevel> searchMedicalStaffLevels() throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    return DataAccess.executeNamedQuery(MedicalStaffLevel.class, QueryNamesEnum.HCM_GET_ALL_MEDICAL_STAFF_LEVELS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<MedicalStaffRank> getAllMedicalStaffRanks() throws BusinessException {
	return searchMedicalStaffRanks();
    }

    public static List<MedicalStaffLevel> getAllMedicalStaffLevels() throws BusinessException {
	return searchMedicalStaffLevels();
    }
}
