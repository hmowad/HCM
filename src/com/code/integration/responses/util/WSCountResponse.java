package com.code.integration.responses.util;

import com.code.integration.responses.WSResponseBase;

public class WSCountResponse extends WSResponseBase {

    private Long count;

    public Long getCount() {
	return count;
    }

    public void setCount(Long count) {
	this.count = count;
    }

}
