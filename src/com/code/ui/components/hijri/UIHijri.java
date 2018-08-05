package com.code.ui.components.hijri;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.el.ValueExpression;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import com.code.services.util.HijriDateService;

@FacesComponent("com.code.ui.components.hijri.UIHijri")
public class UIHijri extends UINamingContainer{
	
	private final static String DAY_LABEL = "day";
    private final static String MONTH_LABEL = "month";
    private final static String YEAR_LABEL = "year";	
    
    public void encodeBegin(FacesContext context) throws IOException {
        super.encodeBegin(context);
        init();
    }
    
    public void updateSubmittedValue() {
        if(this.getDay() == null || this.getMonth() == null || this.getYear() == null)
        	return;
        
    	try{
        	Object dateValue = null;
        	if(!(this.getDay().equals("0") || this.getMonth().equals("0") || this.getYear().equals("0"))) {
	            Calendar calendar = Calendar.getInstance(getFacesContext().getViewRoot().getLocale());
	            calendar.clear();
	            calendar.set(Calendar.MINUTE, Integer.parseInt(this.getDay()));
	            calendar.set(Calendar.MONTH, Integer.parseInt(this.getMonth()) - 1);
	            calendar.set(Calendar.YEAR, Integer.parseInt(this.getYear()));
	            dateValue = calendar.getTime();
        	}
            
            ValueExpression valueExpression = getValueExpression("value");
            valueExpression.setValue(FacesContext.getCurrentInstance().getELContext(), dateValue);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }    

    private void init(){
        Date dateValue = (Date)FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{cc.attrs.value}", Object.class);
        
        String date = (dateValue == null ? "0/0/0" : HijriDateService.getHijriDateString(dateValue));
        
        setDay(date.split("/")[0]);
        setMonth(date.split("/")[1]);
        setYear(date.split("/")[2]);
    }

    public void setDay(String day) {
        this.getStateHelper().put(DAY_LABEL, day);
        updateSubmittedValue();
    }

    public String getDay() {
        return (String)getStateHelper().get(DAY_LABEL);
    }

    public void setMonth(String month) {
        this.getStateHelper().put(MONTH_LABEL, month);
        updateSubmittedValue();
    }

    public String getMonth() {
        return (String)getStateHelper().get(MONTH_LABEL);
    }

    public void setYear(String year) {
        getStateHelper().put(YEAR_LABEL, year);
        updateSubmittedValue();
    }

    public String getYear() {
        return (String)getStateHelper().get(YEAR_LABEL);
    }
}