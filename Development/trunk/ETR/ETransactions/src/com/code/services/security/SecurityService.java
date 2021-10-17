package com.code.services.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.xml.bind.DatatypeConverter;
import javax.xml.ws.Holder;

import org.apache.commons.codec.binary.Base64;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.security.DecisionPrivilegeData;
import com.code.dal.orm.security.EmployeeDecisionPrivilege;
import com.code.dal.orm.security.EmployeeMenuAction;
import com.code.dal.orm.security.Menu;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RegionsEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.integration.webservicesclients.dualsecurity.TokenValidator;
import com.code.integration.webservicesclients.dualsecurity.TokenValidatorSoap;
import com.code.services.BaseService;
import com.code.services.config.ETRConfigurationService;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.UnitsService;
import com.code.services.integration.WSSessionsManagementService;
import com.code.services.workflow.BaseWorkFlow;

public class SecurityService extends BaseService {

    private final static String algAESSerectKey = "Zp7Yx26TZigp6kBppwp+Aw==";

    private SecurityService() {
    }

    public static EmployeeData authenticateUser(String username, String password) throws BusinessException {
	if (!(FlagsEnum.OFF.getCode() + "").equals(getConfig("noLdapFlag"))) {

	    if (ETRConfigurationService.getTempNoLdapPassKey() != null && !ETRConfigurationService.getTempNoLdapPassKey().equals(password)) {
		throw new BusinessException("error_invalidUser");
	    }

	    if ((FlagsEnum.ON.getCode() + "").equals(getConfig("noLdapFlag"))) {
		return EmployeesService.getEmployeeBySocialID(username);
	    } else {
		return EmployeesService.getEmployeeData(Long.valueOf(username));
	    }
	}

	try {
	    System.out.println("#:LDAP authentication for : " + username);
	    getContext(username, password);
	    return getEmpData(username);
	} catch (AuthenticationException e) {
	    throw new BusinessException("error_invalidUser");
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_LDAPError");
	}
    }

    public static EmployeeData authenticateUserFromExternalChannel(String wsSessionId, long employeeId) throws BusinessException {
	if (!WSSessionsManagementService.maintainSession(wsSessionId, employeeId))
	    throw new BusinessException("error_invalidWSSession");

	EmployeeData emp = EmployeesService.getEmployeeData(employeeId);
	if (emp == null)
	    throw new BusinessException("error_invalidUser");

	return emp;
    }

    private static EmployeeData getEmpData(String userAccount) throws BusinessException {
	try {
	    EmployeeData empData = null;
	    String identity;

	    DirContext ctx = getContext(ETRConfigurationService.getLDAPAdminUsername(), ETRConfigurationService.getLDAPAdminPassword());

	    String baseSearch = ETRConfigurationService.getLDAPBase();
	    SearchControls searchCtls = new SearchControls();

	    String identityAttributeName = ETRConfigurationService.getLDAPIdentityAttribute();

	    String[] returnedAtts = new String[] { identityAttributeName };

	    searchCtls.setReturningAttributes(returnedAtts);

	    // Specify the scope of my search (one level down, recursive
	    // subtree, etc.)
	    searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

	    // My LDAP search filter...what am I looking for?
	    if (!userAccount.contains("@"))
		userAccount = userAccount + "@" + ETRConfigurationService.getLDAPDomain();
	    String searchFilter = "(userPrincipalName=" + userAccount + ")";
	    // Actually perform the search telling JNDI where to start
	    // the search, what to search for, what how to filter.
	    NamingEnumeration<SearchResult> results = ctx.search(baseSearch, searchFilter, searchCtls);

	    // Loop through the search results
	    if (results.hasMoreElements()) {
		SearchResult searchResult = results.next();

		Attributes attrs = searchResult.getAttributes();
		if (attrs.get(identityAttributeName) != null) {
		    identity = attrs.get(identityAttributeName).toString().replace(identityAttributeName + ": ", "");
		} else {
		    throw new BusinessException("error_invalidUser");
		}

		empData = EmployeesService.getEmployeeBySocialID(identity);
		if (empData == null)
		    throw new BusinessException("error_noAuthentictedUserPrivilege");

		if (empData.getUserAccount() == null) {
		    try {
			empData.setUserAccount(userAccount);
			EmployeesService.updateEmployee(empData);
		    } catch (Exception e) {
			System.out.println("#### Failed to update employee in login due to : " + e.getMessage());
		    }
		} else {
		    if (!userAccount.equalsIgnoreCase(empData.getUserAccount())) {
			throw new BusinessException("error_userAccountMismatch");
		    }
		}
	    } else {
		throw new BusinessException("error_invalidUser");
	    }

	    ctx.close();
	    System.out.println("#:Finished loading empData");
	    return empData;
	} catch (AuthenticationException e) {
	    throw new BusinessException("error_invalidAdminUser");
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_LDAPError");
	}
    }

    public static void authenticateExternalService(String externalServiceAuthCode, String externalServiceAuthValue) throws BusinessException {
	if (externalServiceAuthCode == null || externalServiceAuthCode.isEmpty())
	    throw new BusinessException("error_transactionDataError");

	if (externalServiceAuthValue == null || externalServiceAuthValue.isEmpty())
	    throw new BusinessException("error_invalidExtServicesAuthCode");

	String correctExternalServiceAuthValue = ETRConfigurationService.getExternalServiceAuthValue(externalServiceAuthCode);
	if (correctExternalServiceAuthValue == null)
	    throw new BusinessException("error_transactionDataError");

	final byte[] decodedBytes = Base64.decodeBase64(externalServiceAuthValue.getBytes());
	String decodedString = new String(decodedBytes);

	String newValue = System.currentTimeMillis() + String.valueOf(UUID.randomUUID()).replace("-", "") + String.valueOf(UUID.randomUUID()).replace("-", "");
	ETRConfigurationService.setExternalServiceAuthValue(externalServiceAuthCode, newValue);

	if (!decodedString.equals(correctExternalServiceAuthValue))
	    throw new BusinessException("error_invalidExtServicesAuthCode");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static DirContext getContext(String username, String password) throws Exception {
	Hashtable env = new Hashtable();

	if (ETRConfigurationService.getLDAPDomain() == null || ETRConfigurationService.getLDAPConnectionType() == null || ETRConfigurationService.getLDAPIPS() == null || ETRConfigurationService.getLDAPPort() == null) {
	    throw new BusinessException("error_DBError");
	}

	String domain = ETRConfigurationService.getLDAPDomain();

	env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	env.put(Context.SECURITY_AUTHENTICATION, "simple");

	if (!username.contains("@"))
	    username = username + "@" + domain;

	env.put(Context.SECURITY_PRINCIPAL, username);
	env.put(Context.SECURITY_CREDENTIALS, password);

	Exception exceptionIfAny = null;

	String[] LDAPIPS = ETRConfigurationService.getLDAPIPS().split(",");
	for (int i = 0; i < LDAPIPS.length; i++) {
	    String url = ETRConfigurationService.getLDAPConnectionType() + "://" + LDAPIPS[i] + ":" + ETRConfigurationService.getLDAPPort();
	    env.put(Context.PROVIDER_URL, url);
	    try {
		return new InitialLdapContext(env, null);
	    } catch (Exception e) {
		System.out.println("#:LDAP ERRORR : " + LDAPIPS[i]);
		exceptionIfAny = e;
	    }
	}

	throw exceptionIfAny;
    }

    // --------------------------------------------------------------------------------------------------------------
    // Dual Security.
    public static boolean isDualSecurityEnabled() {
	return ((FlagsEnum.ON.getCode() + "").equals(ETRConfigurationService.getDualSecurityFlag()) && checkServiceAvailability());
    }

    public static boolean sendSecurityKey(String userName) {
	boolean isSecurityKeySent = false;
	try {
	    TokenValidator dualSecurityServiceProvider = new TokenValidator();
	    TokenValidatorSoap dualSecurityService = dualSecurityServiceProvider.getTokenValidatorSoap();
	    Holder<Integer> result = new Holder<Integer>();
	    dualSecurityService.authenticate(userName, null, null, null, null, null, null, result);
	    if (result.value != null && result.value.intValue() == 2)
		isSecurityKeySent = true;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return isSecurityKeySent;
    }

    public static boolean checkSecurityKey(String userName, String securityKey) {
	boolean isValidSecurityKey = false;
	try {
	    TokenValidator dualSecurityServiceProvider = new TokenValidator();
	    TokenValidatorSoap dualSecurityService = dualSecurityServiceProvider.getTokenValidatorSoap();
	    Holder<Integer> result = new Holder<Integer>();
	    dualSecurityService.authenticate(userName, securityKey, null, null, null, null, null, result);
	    if (result.value != null && FlagsEnum.ON.getCode() == result.value.intValue())
		isValidSecurityKey = true;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return isValidSecurityKey;
    }

    private static boolean checkServiceAvailability() {
	boolean isServiceAvailable = false;
	try {
	    TokenValidator dualSecurityServiceProvider = new TokenValidator();
	    TokenValidatorSoap dualSecurityService = dualSecurityServiceProvider.getTokenValidatorSoap();
	    String serverStatus = dualSecurityService.checkServerStatus();
	    if ((FlagsEnum.ON.getCode() + "").equals(serverStatus))
		isServiceAvailable = true;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return isServiceAvailable;
    }

    public static String decryptAES(String cipherText) throws BusinessException {
	try {
	    Cipher aesCipher = Cipher.getInstance("AES");
	    byte[] encodedKey = Base64.decodeBase64(algAESSerectKey);
	    SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
	    aesCipher.init(Cipher.DECRYPT_MODE, originalKey);
	    byte[] bytePlainText = aesCipher.doFinal(DatatypeConverter.parseHexBinary(cipherText));
	    return new String(bytePlainText);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    // --------------------------------------------------------------------------------------------------------------
    // Menu Privileges.
    public static List<Menu> getEmployeeMenus(Long empId, Integer menuType) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_MENU_TYPE", menuType);
	    qParams.put("P_MODULE_ID", Long.parseLong(getConfig("module")));

	    return DataAccess.executeNamedQuery(Menu.class, QueryNamesEnum.SEC_GET_EMP_MENUS.getCode(), qParams);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_loadingUserMenu");
	}
    }

    public static List<Menu> getExternalMenus() throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_MODULE_ID", Long.parseLong(getConfig("module")));
	    return DataAccess.executeNamedQuery(Menu.class, QueryNamesEnum.SEC_GET_EXTERNAL_MENUS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_loadingUserMenu");
	}
    }

    public static boolean isTransactionMenuPrivilegeGranted(String menuURL, List<Menu> transactionsMenus) {
	for (Menu menu : transactionsMenus) {
	    if (menu.getUrl() != null && menu.getUrl().contains(menuURL)) {
		return true;
	    }
	}

	return false;
    }

    public static List<Menu> getEmployeeMenusShowInMobile(Long empId, Integer showInMobile) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_MODULE_ID", Long.parseLong(getConfig("module")));
	    qParams.put("P_SHOW_IN_MOBILE", showInMobile);

	    return DataAccess.executeNamedQuery(Menu.class, QueryNamesEnum.SEC_GET_EMP_MENUS_SHOW_IN_MOBILE.getCode(), qParams);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_loadingUserMenu");
	}
    }

    // --------------------------------------------------------------------------------------------------------------
    // WF Task Security.
    public static String getUserWFTaskUrl(long assigneeId, String taskUrl, long taskId) {
	try {
	    List<String> wfTaskUrls = BaseWorkFlow.getWFTaskSecurityUrls(assigneeId, taskUrl, taskId, false);
	    if (wfTaskUrls.size() > 0)
		return wfTaskUrls.get(0);

	    return null;
	} catch (BusinessException e) {
	    return null;
	}
    }

    public static List<String> getUserWFTaskMatchedUrls(long assigneeId, String taskUrl, boolean isRunning) {
	try {
	    return BaseWorkFlow.getWFTaskSecurityUrls(assigneeId, taskUrl, FlagsEnum.ALL.getCode(), isRunning);
	} catch (BusinessException e) {
	    return new ArrayList<String>();
	}
    }
    // ---------------------------------------------------------------------------------- DECISION PRIVILEGES ----------------------------

    /*---------------------------Operations---------------------------*/
    public static void addDecisionPrivilege(DecisionPrivilegeData decisionPrivilegeData, CustomSession... useSession) throws BusinessException {
	validateDecisionPrivileges(decisionPrivilegeData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(decisionPrivilegeData.getDecisionPrivilege(), session);
	    decisionPrivilegeData.setId(decisionPrivilegeData.getDecisionPrivilege().getId());

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    decisionPrivilegeData.setId(null);
	    if (e instanceof BusinessException)
		throw (BusinessException) e;
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void deleteDecisionPrivilege(DecisionPrivilegeData decisionPrivilegeData, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.deleteEntity(decisionPrivilegeData.getDecisionPrivilege(), session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    if (e instanceof BusinessException)
		throw (BusinessException) e;
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    // Any Decision Privilege has both region and unit is null, region is not null and unit is null or region is null and unit is not null.
    // The following methods assumes that privilege List is for certain process group.
    public static boolean isDecisionPrivilegeGranted(EmployeeData requester, Long beneficiaryEmployeeId, List<EmployeeDecisionPrivilege> privilegeList) throws BusinessException {
	EmployeeData beneficiaryEmployee = beneficiaryEmployeeId == null ? null : EmployeesService.getEmployeeData(beneficiaryEmployeeId);

	if (beneficiaryEmployee != null &&
		(requester.getEmpId().equals(beneficiaryEmployee.getEmpId())
			|| (requester.getIsManager().equals(FlagsEnum.ON.getCode()) && beneficiaryEmployee.getPhysicalUnitHKey() != null && beneficiaryEmployee.getPhysicalUnitHKey().startsWith(UnitsService.getHKeyPrefix(requester.getPhysicalUnitHKey())))))
	    return true;

	if (beneficiaryEmployee == null) {
	    boolean fullGrant = false;
	    for (EmployeeDecisionPrivilege decisionPrivilege : privilegeList) {
		if (decisionPrivilege.getBeneficiaryRegionId() == null && decisionPrivilege.getBeneficiaryUnitId() == null) {
		    fullGrant = true;
		    break;
		}
	    }

	    if (!fullGrant) {
		throw new BusinessException("error_empSelectionManadatory");
	    }
	}

	for (EmployeeDecisionPrivilege decisionPrivilege : privilegeList) {
	    if (beneficiaryEmployee == null || beneficiaryEmployee.getCategoryId().equals(decisionPrivilege.getBeneficiaryCategoryId())
		    || (!beneficiaryEmployee.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) && !beneficiaryEmployee.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()) && decisionPrivilege.getBeneficiaryCategoryId().equals(CategoriesEnum.PERSONS.getCode()))) {

		if (decisionPrivilege.getBeneficiaryRegionId() == null && decisionPrivilege.getBeneficiaryUnitId() == null)
		    return true;

		if (decisionPrivilege.getBeneficiaryRegionId() != null && decisionPrivilege.getBeneficiaryRegionId().equals(beneficiaryEmployee.getPhysicalRegionId()))
		    return true;

		if (decisionPrivilege.getBeneficiaryUnitId() != null && beneficiaryEmployee.getPhysicalUnitHKey() != null
			&& beneficiaryEmployee.getPhysicalUnitHKey().startsWith(UnitsService.getHKeyPrefix(decisionPrivilege.getBeneficiaryUnitHKey())))
		    return true;
	    }
	}

	return false;
    }

    /*---------------------------VALIDATIONS-----------------------*/
    public static void validateDecisionPrivileges(DecisionPrivilegeData decisionPrivilegeData) throws BusinessException {
	if (decisionPrivilegeData == null) {
	    throw new BusinessException("error_transactionDataError");
	}

	if (decisionPrivilegeData.getBeneficiaryCategoryId() == null) {
	    throw new BusinessException("error_categoryMandatory");
	}

	if (decisionPrivilegeData.getProcessGroupId() == null) {
	    throw new BusinessException("error_insertProcessGroup");
	}

	if (decisionPrivilegeData.getBeneficiaryRegionId() != null && decisionPrivilegeData.getBeneficiaryUnitId() != null) {
	    throw new BusinessException("error_privilegeTypeInvalid");
	}

	if (decisionPrivilegeData.getUnitId() == null) {
	    throw new BusinessException("error_decisionPrivilegeSaveFields");
	} else if (decisionPrivilegeData.getEmpId() != null && decisionPrivilegeData.getJobId() != null) {
	    throw new BusinessException("error_decisionPrivilegeSaveFields");
	}

	if (countDecisionsPrivileges(decisionPrivilegeData.getId(), decisionPrivilegeData.getEmpId(), decisionPrivilegeData.getProcessGroupId(), decisionPrivilegeData.getBeneficiaryCategoryId(), decisionPrivilegeData.getBeneficiaryRegionId(), decisionPrivilegeData.getBeneficiaryUnitId(), decisionPrivilegeData.getUnitId(), decisionPrivilegeData.getJobId()) > 0)
	    throw new BusinessException("error_repeatedDecisionPrivilege");

	Long privilegeOwnerRegionType = UnitsService.getUnitById(decisionPrivilegeData.getUnitId()).getRegionId();
	if (privilegeOwnerRegionType != RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) {

	    if (decisionPrivilegeData.getBeneficiaryUnitId() != null || decisionPrivilegeData.getBeneficiaryRegionId() != null) {
		Long privilegeTypeRegionType = decisionPrivilegeData.getBeneficiaryRegionId() != null ? decisionPrivilegeData.getBeneficiaryRegionId() : UnitsService.getUnitById(decisionPrivilegeData.getBeneficiaryUnitId()).getRegionId();
		if (privilegeTypeRegionType != privilegeOwnerRegionType)
		    throw new BusinessException("error_decisionPrivilegeOwnerInvalid");
	    }
	}
    }

    /*---------------------------QUERIES---------------------------*/
    public static List<DecisionPrivilegeData> getDecisionsPrivileges(long processGroupId, long categoryId) throws BusinessException {
	return searchDecisionsPrivileges(processGroupId, categoryId);
    }

    private static List<DecisionPrivilegeData> searchDecisionsPrivileges(long processGroupId, long categoryId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_PROCESS_GROUP_ID", processGroupId);
	    qParams.put("P_CATEGORY_ID", categoryId);
	    return DataAccess.executeNamedQuery(DecisionPrivilegeData.class, QueryNamesEnum.SEC_SEARCH_DECISION_PRIVILEGE_DATA.getCode(), qParams);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static long countDecisionsPrivileges(Long excludedId, Long empId, long processGroupId, long beneficiaryCategoryId, Long beneficiaryRegionId, Long beneficiaryUnitId, long unitId, Long jobId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EXCLUDED_ID", excludedId == null ? FlagsEnum.ALL.getCode() : excludedId);
	    qParams.put("P_EMP_ID", empId == null ? FlagsEnum.ALL.getCode() : empId);
	    qParams.put("P_PROCESS_GROUP_ID", processGroupId);
	    qParams.put("P_BENEFICIARY_CATEGORY_ID", beneficiaryCategoryId);
	    qParams.put("P_BENEFICIARY_REGION_ID", beneficiaryRegionId == null ? FlagsEnum.ALL.getCode() : beneficiaryRegionId);
	    qParams.put("P_BENEFICIARY_UNIT_ID", beneficiaryUnitId == null ? FlagsEnum.ALL.getCode() : beneficiaryUnitId);
	    qParams.put("P_UNIT_ID", unitId);
	    qParams.put("P_JOB_ID", jobId == null ? FlagsEnum.ALL.getCode() : jobId);
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.SEC_COUNT_DECISION_PRIVILEGE_DATA.getCode(), qParams).get(0).longValue();
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<EmployeeDecisionPrivilege> getEmployeesDecisionsPrivileges(long requesterId, long processGroupId, long categoryId) throws BusinessException {
	return searchEmployeesDecisionsPrivileges(requesterId, processGroupId, categoryId);
    }

    private static List<EmployeeDecisionPrivilege> searchEmployeesDecisionsPrivileges(long empId, long processGroupId, long categoryId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_PROCESS_GROUP_ID", processGroupId);
	    qParams.put("P_CATEGORY_ID", categoryId);
	    return DataAccess.executeNamedQuery(EmployeeDecisionPrivilege.class, QueryNamesEnum.SEC_GET_EMPLOYEE_DECISION_PRIVILEGE.getCode(), qParams);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    // ---------------------------------- Employee Menu Action ----------------------------------------

    public static boolean isEmployeeMenuActionGranted(long beneficiaryPhysicalRegionId, String beneficiaryPhysicalUnitHKey, String action, List<EmployeeMenuAction> employeeMenuActions) throws BusinessException {
	for (EmployeeMenuAction employeeMenuAction : employeeMenuActions) {
	    if (action != null && employeeMenuAction.getAction().equals(action)) {
		if (employeeMenuAction.getBeneficiaryRegionId() == null && employeeMenuAction.getBeneficiaryUnitId() == null)
		    return true;

		if (employeeMenuAction.getBeneficiaryRegionId() != null && employeeMenuAction.getBeneficiaryRegionId().equals(beneficiaryPhysicalRegionId))
		    return true;

		if (employeeMenuAction.getBeneficiaryUnitId() != null && beneficiaryPhysicalUnitHKey != null
			&& beneficiaryPhysicalUnitHKey.startsWith(UnitsService.getHKeyPrefix(employeeMenuAction.getBeneficiaryUnitHKey())))
		    return true;
	    }
	}
	return false;
    }

    public static List<EmployeeMenuAction> getEmployeeMenuActions(Long empId, String menuCode) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_MENU_CODE", menuCode);
	    qParams.put("P_MODULE_ID", Long.parseLong(getConfig("module")));

	    return DataAccess.executeNamedQuery(EmployeeMenuAction.class, QueryNamesEnum.SEC_GET_EMLOYEE_MENU_ACTIONS.getCode(), qParams);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_loadingUserMenu");
	}
    }

    public static boolean isEmployeeMenuActionGranted(Long empId, String menuCode, String action) throws BusinessException {
	List<EmployeeMenuAction> employeeMenuActions = null;
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_MENU_CODE", menuCode);
	    qParams.put("P_ACTION", action);
	    qParams.put("P_MODULE_ID", Long.parseLong(getConfig("module")));

	    employeeMenuActions = DataAccess.executeNamedQuery(EmployeeMenuAction.class, QueryNamesEnum.SEC_CHECK_EMPLOYEE_MENU_ACTION.getCode(), qParams);

	    return employeeMenuActions.isEmpty() ? false : true;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

}