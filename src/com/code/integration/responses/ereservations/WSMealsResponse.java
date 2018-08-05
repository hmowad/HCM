package com.code.integration.responses.ereservations;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.integration.responses.WSResponseBase;

public class WSMealsResponse extends WSResponseBase {

    private List<MealData> meals;

    @XmlElementWrapper(name = "meals")
    @XmlElement(name = "meal")
    public List<MealData> getMeals() {
	return meals;
    }

    public void setMeals(List<MealData> meals) {
	this.meals = meals;
    }
}
