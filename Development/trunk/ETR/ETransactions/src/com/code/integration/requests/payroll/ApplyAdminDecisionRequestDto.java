package com.code.integration.requests.payroll;

import java.util.List;

public class ApplyAdminDecisionRequestDto {

    private List<DecisionTypeDto> DecisionTypesList;

    public List<DecisionTypeDto> getDecisionTypesList() {
	return DecisionTypesList;
    }

    public void setDecisionTypesList(List<DecisionTypeDto> decisionTypesList) {
	DecisionTypesList = decisionTypesList;
    }

}
