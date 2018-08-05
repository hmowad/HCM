
package com.code.integration.webservicesclients.ereservations;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getMealsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getMealsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mealsEnumListResult" type="{http://webservice.services.code.com/}mealResult" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getMealsResponse", propOrder = {
    "mealsEnumListResult"
})
public class GetMealsResponse {

    protected List<MealResult> mealsEnumListResult;

    /**
     * Gets the value of the mealsEnumListResult property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mealsEnumListResult property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMealsEnumListResult().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MealResult }
     * 
     * 
     */
    public List<MealResult> getMealsEnumListResult() {
        if (mealsEnumListResult == null) {
            mealsEnumListResult = new ArrayList<MealResult>();
        }
        return this.mealsEnumListResult;
    }

}
