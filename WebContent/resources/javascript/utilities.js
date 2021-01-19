function adjustSelectedHeaderTree(){
	var headerImg1 = document.getElementById('headerImg1Id');
	var headerImg2 = document.getElementById('headerImg2Id');
	var headerImg3 = document.getElementById('headerImg3Id');

	var menuType = 1;
	if (document.getElementById('menuTypeHId') != null) {
		menuType = document.getElementById('menuTypeHId').value;
	}
	
	if(menuType == 1){
	    if(headerImg1 != null)	headerImg1.className = 'selected-1';
	    if(headerImg2 != null)	headerImg2.className = '';
	    if(headerImg3 != null)	headerImg3.className = '';
	}
	else if(menuType == 2){
		if(headerImg1 != null)	headerImg1.className = '';
	    if(headerImg2 != null)	headerImg2.className = 'selected-2';
	    if(headerImg3 != null)	headerImg3.className = '';
	}
	else if(menuType == 3){
    	if(headerImg1 != null)	headerImg1.className = '';
	    if(headerImg2 != null)	headerImg2.className = '';
	    if(headerImg3 != null)	headerImg3.className = 'selected-3';
    }
}

String.prototype.format = function() {
    var args = arguments;

    return this.replace(/\{(\d+)\}/g, function() {
        return args[arguments[1]];
    });
};


function isDigit(e , acceptNegative)
{
	var keynum = '';
	var keychar;
	var numcheck;
	
	if(window.event) // IE
	{
		keynum = e.keyCode;
	}
	else if(e.which) // Net escape/Fire fox/Opera
	{
		keynum = e.which;
	}
	keychar = String.fromCharCode(keynum);
  
  if(acceptNegative)
    numcheck = /(\-)|(\d)/;  
  else
    numcheck = /(\d)/;
  		
	return numcheck.test(keychar);
}

function isLessOrEqualNumber(e, oldValue, maxValue)
{
	var keynum = '';
	var keychar;
	var numcheck;
	
	if(window.event) // IE
	{
		keynum = e.keyCode;
	}
	else if(e.which) // Net escape/Fire fox/Opera
	{
		keynum = e.which;
	}
	keychar = String.fromCharCode(keynum);
	numcheck = /(\d)/;
	
	if(!numcheck.test(keychar) || parseInt(oldValue + keychar) > maxValue)
		return false;
	
	return true;
}

function isFloat(e, value, acceptNegative)
{
	var keynum = '';
	var keychar;
	var numcheck;
	if(window.event) // IE
	{
		keynum = e.keyCode;
	}
	else if(e.which) // Net escape/Fire fox/Opera
	{
		keynum = e.which;
	}
	keychar = String.fromCharCode(keynum);
	if(acceptNegative)
    numcheck = /(\-)|(\d)|(\.)/;
  else
    numcheck = /(\d)|(\.)/;
  
  if(value.indexOf('.') != -1 && keychar == '.') return false;
  
	return numcheck.test(keychar);
}

function enforceMaxLength(element, maxLength) {
	
	if (element != null)
		return ( element.value.length < maxLength );
}

function enforceTextAreaMaxLength(e, element, maxLength, allowNewLine) {
	if (!allowNewLine) {
		var keyCode = window.event ? e.keyCode : (e.which ? e.which : '');
		if (keyCode == '13')
			return false;
	}

	if (element != null)
		return (element.value.length < maxLength);
}

/*
 * This method check the maximum number of characters at a specific field (element).
 * It trim the data of the element to the maxLength.
 * It should be called on the following events :- onkeyup and onchange.
 * onkeyup for typing and pasting by CTRL+V.
 * onchange for pasting by mouse.
 * 
 * */
function limitMaxLength(element,message, maxLength) {
    if (element.value.length > maxLength) {
    	alert(message.format(maxLength));
    	element.value = element.value.substring(0, maxLength);
    	return false;
    } 
}

function hijriDateOnChange(curId, nullable){
    if(nullable) {
        var pId = curId.substring(0, curId.lastIndexOf(':') + 1);
        if(document.getElementById(curId).value == '0'){
            document.getElementById(pId + 'day').value = '0';
            document.getElementById(pId + 'month').value = '0';
            document.getElementById(pId + 'year').value = '0';
        }
        else{
            if(document.getElementById(pId + 'day').value == '0') document.getElementById(pId + 'day').value = '01';
            if(document.getElementById(pId + 'month').value == '0') document.getElementById(pId + 'month').value = '01';
            if(document.getElementById(pId + 'year').value == '0') document.getElementById(pId + 'year').value = '1425';
        }									
    }
}

function controlMenuDiv(menuDivId){
    var menuDiv = document.getElementById(menuDivId);
    var menuDivPlusMenus = document.getElementById(menuDivId + '_PlusMinus');
    if(menuDiv.style.display == 'none'){
        menuDiv.style.display = '';
        menuDivPlusMenus.src = menuDivPlusMenus.src.replace('Plus', 'Minus');
    }
    else{
        menuDiv.style.display = 'none';
        menuDivPlusMenus.src = menuDivPlusMenus.src.replace('Minus', 'Plus');
    }
}


function controlDiv(divId, displayMode){
	if(displayMode != null){
		document.getElementById(divId).style.display = displayMode;
		return;
	}
		
    var div = document.getElementById(divId);
    if(div.style.display == 'none'){
    	div.style.display = '';
    }
    else{
    	div.style.display = 'none';
    }
}


function controlPrevTasks(refuseRoles){
    for(var i=0; ; i++){
        try{
            var role = document.getElementById('prevTasks:taskRoleId:' + i).value + ',';
            if(refuseRoles.indexOf(role) > -1) 
                document.getElementById('prevTasks:refuseReasonsId:' + i).style.display = '';
            else
                document.getElementById('prevTasks:refuseReasonsId:' + i).style.display = 'none';                
        }
        catch(exception){break;}
    }    
}

//---------------------------------------------------------------------------------------------------------------------

function handleOnAction(confirmMessage, okMethod, cancelMethod){

    if(confirmMessage != null){
        if(confirm(confirmMessage)){
            if(okMethod != null &&  !eval('(' + okMethod + ')')){
                return false;
            }
        }
        else{
            if(cancelMethod != null)
                eval('(' + cancelMethod + ')');
            return false;
        }
    }
    return true;
}

//---------------------------------------------------------------------------------------------------------------------
// mode: 0 refuse is mandatory - 1 & 2 refuse must be empty - 2 notes mandatory
function validateRefuseAndNotes(mode, refuseId, notesId){
    var refuse = null;    
    
    if(mode == 0){
        refuse = document.getElementById(refuseId).value;
        if(refuse == null || refuse == ''){
            alert(document.getElementById('refuseReasonsManadatoryId').value);
            return false;
        }
    }
    else{    
        if(mode == 1 || (mode == 2 && refuseId != null)){
            refuse = document.getElementById(refuseId).value;
            if(refuse != null && refuse != ''){
                alert(document.getElementById('refuseReasonsEmptyId').value);
                return false;
            }
        }
        
        if(mode == 2){
            var notes = document.getElementById(notesId).value;
            if(notes == null || notes == ''){
                alert(document.getElementById('notesManadatoryId').value);
                return false;
            }
        }
    }
    
    return true;
}

function validateDecisionApprovalNotes(decisionApprovalRemarksId, refuseId, notesId) {
	if(refuseId != null) {
		refuse = document.getElementById(refuseId).value;
	    if(refuse != null && refuse != ''){
	        alert(document.getElementById('refuseReasonsEmptyId').value);
	        return false;
	    }
	}
	
	decisionApprovalRemarks = document.getElementById(decisionApprovalRemarksId).value;
	notes = document.getElementById(notesId).value;
	
	if((decisionApprovalRemarks == null || decisionApprovalRemarks == '') && (notes == null || notes == '')) {
        alert(document.getElementById('notesManadatoryId').value);
        return false;
	}
	
	return true;
}

function validateEmpSelection(empsId){
    if(document.getElementById(empsId).value == 0){
        alert(document.getElementById('empSelectionManadatoryId').value);
        return false;
    }
    
    return true;
}
//---------------------------------------------------------------------------------------------------------------------
var oldSelectedRow = null;
function changeSelectedRowStyle(selectedRow){
    if(oldSelectedRow != null){
    	oldSelectedRow.style.backgroundColor = '';
	}
	selectedRow.style.backgroundColor='#CBE6F7';
	oldSelectedRow = selectedRow;
}

function changeRowOverStyle(overRow){
	if(oldSelectedRow == null || overRow.id != oldSelectedRow.id)
		overRow.style.backgroundColor='#9BE0CB';
}

function resetRowOverStyle(overRow){
	if(oldSelectedRow == null || overRow.id != oldSelectedRow.id)
		overRow.style.backgroundColor = '';
}
//---------------------------------------------------------------------------------------------------------------------
function getSelectOneRadioValue(radioId){
    for(var i=0; ; i++){
        try{
            if(document.getElementById(radioId + ':' + i).checked == true)
                return document.getElementById(radioId + ':' + i).value;
        }
        catch(exception){break;}
    }
    return null;
}

function openPopUpWindow(url){
    window.open(url);
}

function openEmpsMiniSearchForTerminatedEmps(contextPath, miniSearchReturnHandler, mode,unitId,multipleSelectFlag){
	var specsStr = getPopupWindowSpecsString(0.75, 0.75, 'no', 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/MiniSearch/EmpsMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler+'&mode=' + mode+'&unitId='+unitId+'&multipleSelectFlag='+multipleSelectFlag, null, specsStr);
    unMaskOnPopupClose(childWindow);
}

function openEmpsMiniSearchForRecruitmentModule(contextPath, miniSearchReturnHandler, mode, categoryMode, recruitmentRegionId, graduationGroupNumber , graduationGroupPlace, recruitmentRankIdValue, recruitmentTrainingUnitId, gender, exceptionalRecruitmentFlag, statusIds) {
	empsMiniSearch(contextPath, miniSearchReturnHandler, mode, categoryMode, -1, recruitmentRegionId, graduationGroupNumber , graduationGroupPlace, recruitmentRankIdValue, -1, -1, -1, recruitmentTrainingUnitId, -1, -1 , gender, statusIds, exceptionalRecruitmentFlag, 0,-1);
}

function openEmpsMiniSearchWithRankIdAndPromotionDueDate(contextPath, miniSearchReturnHandler, mode, categoryMode, rankId, promotionDueDate, officialRegionId){
	empsMiniSearch(contextPath, miniSearchReturnHandler, mode, categoryMode, -1, -1, -1 , -1, -1, -1, rankId, promotionDueDate, -1, -1, officialRegionId, -1, -1, -1, 0,-1);
}

function openEmpsMiniSearch(contextPath, miniSearchReturnHandler, mode, categoryMode, recruitmentRegionId, graduationGroupNumber , graduationGroupPlace, recruitmentRankId, managerPhysicalUnitHKey){
	empsMiniSearch(contextPath, miniSearchReturnHandler, mode, categoryMode, -1, recruitmentRegionId, graduationGroupNumber , graduationGroupPlace, recruitmentRankId, managerPhysicalUnitHKey, -1, -1, -1, -1, -1, -1, -1, -1, 0,-1);
}

function openEmployeesMiniSearch(contextPath, miniSearchReturnHandler, mode, categoryMode, recruitmentRegionId, graduationGroupNumber , graduationGroupPlace, recruitmentRankId, managerPhysicalUnitHKey, physicalRegionId){
	empsMiniSearch(contextPath, miniSearchReturnHandler, mode, categoryMode, -1, recruitmentRegionId, graduationGroupNumber , graduationGroupPlace, recruitmentRankId, managerPhysicalUnitHKey, -1, -1, -1, physicalRegionId, -1, -1, -1, -1, 0,-1);
}

function openEmployeesMiniSearchByCategoriesIds(contextPath, miniSearchReturnHandler, mode, categoryIds, recruitmentRegionId, graduationGroupNumber , graduationGroupPlace, recruitmentRankId, managerPhysicalUnitHKey, physicalRegionId){
	empsMiniSearch(contextPath, miniSearchReturnHandler, mode, -1, categoryIds, recruitmentRegionId, graduationGroupNumber , graduationGroupPlace, recruitmentRankId, managerPhysicalUnitHKey, -1, -1, -1, physicalRegionId, -1, -1, -1, -1, 0,-1);
}

function openEmpsMiniSearchForHistorical(contextPath, miniSearchReturnHandler, mode, categoryIds){
	empsMiniSearch(contextPath, miniSearchReturnHandler, mode, -1,categoryIds, -1, -1 , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0,-1);
}

function openEmployeesMiniSearchForFutureVacation(contextPath, miniSearchReturnHandler, mode, categoryIds,employeeIds) {
	empsMiniSearch(contextPath, miniSearchReturnHandler, mode, -1,categoryIds, -1, -1 , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0,employeeIds);
} 

function openEmployeesMiniSearchForBeneficiary(contextPath, miniSearchReturnHandler, mode, categoryMode,employeeIds) {
	empsMiniSearch(contextPath, miniSearchReturnHandler, mode, categoryMode,-1, -1, -1 , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0,employeeIds);
} 

function openEmployeesMiniSearchForDecisionCopies(contextPath, miniSearchReturnHandler, mode){
	empsMiniSearch(contextPath, miniSearchReturnHandler, mode,-1, -1, -1, -1 , -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1,-1);
}

function openEmployeesMiniSearchByOfficialRegionId(contextPath, miniSearchReturnHandler, mode, categoryMode, officialRegionId){
	empsMiniSearch(contextPath, miniSearchReturnHandler, mode, categoryMode,-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, officialRegionId, -1, -1, -1, 0,-1);
}

function openEmpsMiniSearchByPhysicalRegionForMultipleEmployees(contextPath, miniSearchReturnHandler, mode, categoryMode,physicalRegionId){
	empsMiniSearch(contextPath, miniSearchReturnHandler, mode, categoryMode, -1, -1, -1 , -1, -1, -1, -1, -1, -1, physicalRegionId,-1, -1, -1, -1, 1,-1);
}
function openEmpsMiniSearchForAdditionalRaises(contextPath, miniSearchReturnHandler, mode, categoryMode, physicalRegionId, statusIds){
	empsMiniSearch(contextPath, miniSearchReturnHandler, mode, categoryMode, -1, -1, -1 , -1, -1, -1, -1, -1, -1, physicalRegionId,-1, -1, statusIds, -1, 0,-1);
}
function empsMiniSearch(contextPath, miniSearchReturnHandler, mode, categoryMode, categoryIds, recruitmentRegionId, graduationGroupNumber , graduationGroupPlace, recruitmentRankId, managerPhysicalUnitHKey, rankId, promotionDueDate, recruitmentTrainingUnitId, physicalRegionId, officialRegionId, gender, statusIds, exceptionalRecruitmentFlag, multipleSelectFlag,employeeIds){
	var specsStr = getPopupWindowSpecsString(0.75, 0.75, 'no', 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/MiniSearch/EmpsMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler+'&mode=' + mode+'&categoryMode=' + categoryMode+'&categoryIds=' + categoryIds+'&recruitmentRegionId='+recruitmentRegionId+'&graduationGroupNumber='+graduationGroupNumber+'&graduationGroupPlace='+graduationGroupPlace+'&recruitmentRankId='+recruitmentRankId+'&managerPhysicalUnitHKey='+managerPhysicalUnitHKey+'&rankId='+rankId+'&promotionDueDate='+promotionDueDate+'&recruitmentTrainingUnitId='+recruitmentTrainingUnitId+'&physicalRegionId='+physicalRegionId+'&officialRegionId='+officialRegionId+'&gender='+gender+'&statusIds='+statusIds+'&exceptionalRecruitmentFlag='+exceptionalRecruitmentFlag+'&multipleSelectFlag='+multipleSelectFlag+'&employeeIds='+employeeIds, null, specsStr);
    unMaskOnPopupClose(childWindow);
}

function deservedEmpsForAnnualRaiseMiniSearch(contextPath, miniSearchReturnHandler, decisionDateString, decisionNumber){
	var specsStr = getPopupWindowSpecsString(0.75, 0.75, 'no', 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/MiniSearch/DeservedEmpsForAnnualRaiseMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler+'&decisionDateString='+decisionDateString+'&decisionNumber='+decisionNumber, null, specsStr);
    unMaskOnPopupClose(childWindow);
}

function openTransactionsTimeline(contextPath){
	var specsStr = getPopupWindowSpecsString(0.50, 0.50, 'no', 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/TransactionsTimeline/TransactionsTimeline.jsf', null, specsStr);
    unMaskOnPopupClose(childWindow);
}

function openAdditionalSpecializationsMiniSearch(contextPath, miniSearchReturnHandler){
	 var specsStr = getPopupWindowSpecsString(0.75, 0.75, 'no', 'yes');
	 maskOpenerScreen();
	 var childWindow = window.open(contextPath+'/MiniSearch/AdditionalSpecializationsMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler, null, specsStr);
     unMaskOnPopupClose(childWindow);
}

function openJobsMiniSearch(contextPath, miniSearchReturnHandler, mode, categoryMode, unitHKey, rankId, regionId, minorSpecId, multipleSelectFlag){
	if (typeof multipleSelectFlag === 'undefined')
		jobsMiniSearch(contextPath, miniSearchReturnHandler, mode, categoryMode, unitHKey, rankId, regionId, minorSpecId, 0);
	else 
		jobsMiniSearch(contextPath, miniSearchReturnHandler, mode, categoryMode, unitHKey, rankId, regionId, minorSpecId, multipleSelectFlag);
}

function openBasicJobsNamesMiniSearch(contextPath, miniSearchReturnHandler, mode, category, multipleSelectFlag){
	var specsStr = getPopupWindowSpecsString(0.75, 0.75, 'no', 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/MiniSearch/BasicJobsNamesMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler+'&mode='+mode+'&category='+category+'&multipleSelectFlag='+multipleSelectFlag, null, specsStr);
    unMaskOnPopupClose(childWindow);
}

function jobsMiniSearch(contextPath, miniSearchReturnHandler, mode, categoryMode, unitHKey, rankId, regionId, minorSpecId, multipleSelectFlag){
	 var specsStr = getPopupWindowSpecsString(0.75, 0.75, 'no', 'yes');
	 maskOpenerScreen();
	 var childWindow = window.open(contextPath+'/MiniSearch/JobsMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler+'&mode=' + mode + "&categoryMode=" + categoryMode+'&unitHKey='+unitHKey+'&rankId='+rankId+'&regionId='+regionId+'&minorSpecId='+minorSpecId+'&multipleSelectFlag='+multipleSelectFlag, null, specsStr);
     unMaskOnPopupClose(childWindow);
}

function openManpowerNeedsRequestsMiniSearch(contextPath, miniSearchReturnHandler, categoryId, regionId, multipleSelectFlag){
	var specsStr = getPopupWindowSpecsString(0.75, 0.75, 'no', 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/MiniSearch/ManpowerNeedsRequestsMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler+'&categoryId='+categoryId+'&regionId='+regionId+'&multipleSelectFlag='+multipleSelectFlag, null, specsStr);
    unMaskOnPopupClose(childWindow);
}

function openSocialIdIssuePlacesMiniSearch(contextPath, miniSearchReturnHandler){
	var specsStr = getPopupWindowSpecsString(0.75, 0.75, 'no', 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/MiniSearch/SocialIdIssuePlacesMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler, null, specsStr);
    unMaskOnPopupClose(childWindow);
}

function openEmpFileAttachmentsMiniSearch(contextPath, miniSearchReturnHandler, empId, transClass, isModifyAdmin){
	var specsStr = getPopupWindowSpecsString(0.75, 0.75, 'no', 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/MiniSearch/EmployeeFileAttachmentsMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler+'&empId='+empId+'&transClass='+transClass+'&isModifyAdmin='+isModifyAdmin, null, specsStr);
    unMaskOnPopupClose(childWindow);
}

function openMinorSpecializationsMiniSearch(contextPath, miniSearchReturnHandler){
	minorSpecializationsMiniSearch(contextPath, miniSearchReturnHandler, 1, 0);
}

function openMinorSpecializationsMiniSearch(contextPath, miniSearchReturnHandler,multipleSelectFlag){
	if (typeof multipleSelectFlag === 'undefined')
		minorSpecializationsMiniSearch(contextPath, miniSearchReturnHandler, 1, 0);
	else 
		minorSpecializationsMiniSearch(contextPath, miniSearchReturnHandler, 1, multipleSelectFlag);	
}

function minorSpecializationsMiniSearch(contextPath, miniSearchReturnHandler, mode, multipleSelectFlag){
	var specsStr = getPopupWindowSpecsString(0.75, 0.75, 'no', 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/MiniSearch/MinorSpecializationsMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler + '&mode=' + mode+ '&multipleSelectFlag='+multipleSelectFlag, null, specsStr);
    unMaskOnPopupClose(childWindow);
}

function openJobClassificationsMiniSearch(contextPath, miniSearchReturnHandler, rankCode){
	var specsStr = getPopupWindowSpecsString(0.75, 0.75, 'no', 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/MiniSearch/JobClassificationsMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler+'&rankCode='+rankCode, null, specsStr);
    unMaskOnPopupClose(childWindow);
}

function openSelectRegions(mode, contextPath, miniSearchReturnHandler){
	var specsStr = getPopupWindowSpecsString(0.85, 0.75, 'no', 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/MiniSearch/RegionsMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler+'&mode='+mode, null, specsStr);
    unMaskOnPopupClose(childWindow);
}

function openSelectCountries(mode, contextPath, miniSearchReturnHandler){
	var specsStr = getPopupWindowSpecsString(0.85, 0.75, 'no', 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/MiniSearch/CountriesMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler+'&mode='+mode, null, specsStr);
    unMaskOnPopupClose(childWindow);
}

function openSelectAdminDecision(contextPath, miniSearchReturnHandler){
	var specsStr = getPopupWindowSpecsString(0.85, 0.75, 'no', 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/MiniSearch/AdminDecisionMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler, null, specsStr);
    unMaskOnPopupClose(childWindow);
}

function openUnitsMiniSearchByUnitsIdsString(contextPath, miniSearchReturnHandler, unitsIdsString, multipleSelectFlag){
	var specsStr = getPopupWindowSpecsString(0.75, 0.75, 'no', 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/MiniSearch/UnitsMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler+'&mode=8&unitTypeCode=-1&unitRegionId=-1&unitParentId=-1&unitHKeyPrefix=-1&unitsIdsString='+ unitsIdsString +'&multipleSelectFlag='+ multipleSelectFlag, null, specsStr);
	unMaskOnPopupClose(childWindow);
}

function openUnitsMiniSearch(mode, contextPath, miniSearchReturnHandler, unitTypeCode, unitRegionId, unitParentId){
	unitsMiniSearch(mode, contextPath, miniSearchReturnHandler, unitTypeCode, unitRegionId, unitParentId, null, 0);
}

function unitsMiniSearch(mode, contextPath, miniSearchReturnHandler, unitTypeCode, unitRegionId, unitParentId, unitHKeyPrefix, multipleSelectFlag){
	var specsStr = getPopupWindowSpecsString(0.75, 0.75, 'no', 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/MiniSearch/UnitsMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler+'&mode='+mode+'&unitTypeCode='+unitTypeCode+'&unitRegionId='+unitRegionId+'&unitParentId='+unitParentId + '&unitHKeyPrefix=' + unitHKeyPrefix + '&multipleSelectFlag=' + multipleSelectFlag, null, specsStr);
	unMaskOnPopupClose(childWindow);
}


function openSelectProcesses(mode, contextPath, miniSearchReturnHandler){
	var specsStr = getPopupWindowSpecsString(0.75, 0.75, 'no', 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/MiniSearch/ProcessesMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler+'&mode='+mode, null, specsStr);
    unMaskOnPopupClose(childWindow);
}

function openNavyFormationsMiniSearch(contextPath , miniSearchReturnHandler,regionId){
	var specsStr = getPopupWindowSpecsString(0.75, 0.75, 'no' , 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/MiniSearch/NavyFormationsMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler+'&regionId='+regionId, null, specsStr);
    unMaskOnPopupClose(childWindow);
}

function openRanksMiniSearch(contextPath,  miniSearchReturnHandler, category, mode , multipleSelectFlag){
	var specsStr = getPopupWindowSpecsString(0.75, 0.75, 'no', 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/MiniSearch/RanksMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler+'&category='+category+'&mode='+mode+'&multipleSelectFlag='+ multipleSelectFlag, null, specsStr);
	unMaskOnPopupClose(childWindow);
}


/********************************************** Training mini search *****************************************************/
function openGraduationPlacesMiniSearch(contextPath, miniSearchReturnHandler,multipleSelectFlag){
	var specsStr = getPopupWindowSpecsString(0.75, 0.75, 'no', 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/MiniSearch/GraduationPlacesMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler+'&multipleSelectFlag='+multipleSelectFlag, null, specsStr);
    unMaskOnPopupClose(childWindow);
}

function openQualificationMinorSpecsMiniSearch(contextPath, miniSearchReturnHandler, militaryClassificationFlag){
	var specsStr = getPopupWindowSpecsString(0.75, 0.75, 'no', 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/MiniSearch/QualificationMinorSpecsMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler+'&militaryClassificationFlag='+militaryClassificationFlag, null, specsStr);
    unMaskOnPopupClose(childWindow);
}

function openQualificationMajorSpecsMiniSearch(contextPath, miniSearchReturnHandler,militaryClassificationFlag){
	var specsStr = getPopupWindowSpecsString(0.75, 0.75, 'no', 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/MiniSearch/QualificationMajorSpecsMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler+'&militaryClassificationFlag='+militaryClassificationFlag, null, specsStr);
    unMaskOnPopupClose(childWindow);
}

function openQualificationLevelsMiniSearch(contextPath,miniSearchReturnHandler){
	var specsStr = getPopupWindowSpecsString(0.75, 0.75, 'no', 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/MiniSearch/QualificationLevelsMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler,null, specsStr);
    unMaskOnPopupClose(childWindow);
}

function openTrainingCoursesMiniSearch(contextPath, miniSearchReturnHandler, courseType){
	var specsStr = getPopupWindowSpecsString(0.75, 0.75, 'no', 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/MiniSearch/TrainingCoursesMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler+'&courseType='+courseType, null, specsStr);
    unMaskOnPopupClose(childWindow);
}

function openExternalPartiesMiniSearch(contextPath,miniSearchReturnHandler, mode, multipleSelectFlag){
	var specsStr = getPopupWindowSpecsString(0.75, 0.75, 'no', 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/MiniSearch/ExternalPartiesMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler+'&mode='+mode+ '&multipleSelectFlag='+multipleSelectFlag,null, specsStr);
    unMaskOnPopupClose(childWindow);
}

function openTrainingIntCourseEventsMiniSearch(contextPath, miniSearchReturnHandler, statuses, trainingYearId, trainingUnitId, requestingRegionId, nominationFlag) {
	trainingCourseEventsMiniSearch(contextPath, miniSearchReturnHandler, 1, statuses, trainingYearId, trainingUnitId, -1, requestingRegionId, nominationFlag); 
}

function openTrainingExtCourseEventsMiniSearch(contextPath, miniSearchReturnHandler,statuses,externalPartyId, nominationFlag) {
	trainingCourseEventsMiniSearch(contextPath, miniSearchReturnHandler, 2, statuses, -1, -1, externalPartyId, -1,  nominationFlag);
}
function openTrainingCivilCourseEventsMiniSearch(contextPath, miniSearchReturnHandler,trainingTypeId,externalPartyId,nominationFlag) {
	trainingCourseEventsMiniSearch(contextPath, miniSearchReturnHandler, trainingTypeId, 1 , -1, -1,externalPartyId , -1, nominationFlag);
}
function trainingCourseEventsMiniSearch(contextPath, miniSearchReturnHandler, trainingTypeId, statuses, trainingYearId, trainingUnitId, externalPartyId, requestingRegionId, nominationFlag) {
    var specsStr = getPopupWindowSpecsString(0.75, 0.75, 'no', 'yes');
    maskOpenerScreen();
    var childWindow = window.open(contextPath+'/MiniSearch/CoursesEventsMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler+'&trainingTypeId='+trainingTypeId+'&statuses='+statuses+'&trainingYearId='+trainingYearId+'&trainingUnitId='+trainingUnitId+'&externalPartyId='+externalPartyId+'&requestingRegionId='+requestingRegionId+'&nominationFlag='+nominationFlag, null, specsStr);
    unMaskOnPopupClose(childWindow);
}


/********************************************** Training mini search *****************************************************/


function getPopupWindowSpecsString(popupHeightPercentage, popupWidthPercentage, isResizable, hasScrollbars) {
	
	// get the window visible area height (the scroll isn't included).
	var windowHeight = window.innerHeight // the more standards compliant browsers (mozilla/netscape/opera/IE7) use window.innerWidth and window.innerHeight
			|| document.documentElement.clientHeight // IE6 in standards compliant mode (i.e. with a valid doctype as the first line in the document)
			|| document.getElementsByTagName('body')[0].clientHeight; // older versions of IE
	
	// get the window visible area width (the scroll isn't included).
	var windowWidth = window.innerWidth // the more standards compliant browsers (mozilla/netscape/opera/IE7) use window.innerWidth and window.innerHeight
			|| document.documentElement.clientWidth // IE6 in standards compliant mode (i.e. with a valid doctype as the first line in the document)
			|| document.getElementsByTagName('body')[0].clientWidth; // older versions of IE
		
	var windowTop = window.outerHeight - windowHeight;
	
	var height = windowHeight * popupHeightPercentage;
	var width = windowWidth  * popupWidthPercentage;
	var top = ((windowHeight - height) / 2) + (windowTop / 2);
	var left = ((windowWidth  - width) / 2);
    
	return "height=" + height + ", width=" + width + ", top=" + top + ", left=" + left + ", resizable=" + isResizable + ", scrollbars=" + hasScrollbars;
}

function unMaskOnPopupClose(childWindow) {
	var timer = setInterval(checkChild, 500);
    function checkChild() {
	    if (childWindow.closed) {
	    	unMaskOpenerScreen();
	    	clearInterval(timer);
	    }
    }
}

function openImageUpload(mode, contextPath, imageUploadReturnHandler, objectId, isNew){
    var specsStr = getPopupWindowSpecsString(0.7, 0.5, 'no', 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/Upload/ImageUpload.jsf?mode='+mode+'&imageUploadReturnHandler='+imageUploadReturnHandler+'&objectId='+objectId+"&isNew="+isNew, null, specsStr);
    unMaskOnPopupClose(childWindow);
}

function addAttachments(attachmentsAddURL){
	var specsStr = getPopupWindowSpecsString(0.85, 0.85, 'no', 'yes');
	window.open(attachmentsAddURL.replace(/&amp;/g, '&'), null, specsStr);
}

function viewAttachments(attachmentsViewURL){
	var specsStr = getPopupWindowSpecsString(0.85, 0.85, 'no', 'yes');
	window.open(attachmentsViewURL.replace(/&amp;/g, '&'), null, specsStr);
}

function openUploadFile(url, param) {
	var specsStr = getPopupWindowSpecsString(0.85, 0.85, 'no', 'yes');
	window.open(url + param , null, specsStr);
}

function openDownloadFile(url, param) {
	window.open(url + param , '_blank' );
}

/********************************************** historical vacations mini search *****************************************************/
function openHistoricalVacationsMiniSearch(contextPath, miniSearchReturnHandler, categoryCode, empId,searchMode){
	var specsStr = getPopupWindowSpecsString(0.75, 0.75, 'no', 'yes');
	maskOpenerScreen();
	var childWindow = window.open(contextPath+'/MiniSearch/HistoricalVacationsMiniSearch.jsf?miniSearchReturnHandler='+miniSearchReturnHandler+'&categoryMode='+categoryCode+'&empId='+empId+'&searchMode='+searchMode,null, specsStr);
	   unMaskOnPopupClose(childWindow);
	}