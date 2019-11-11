package com.code.integration.webservicesclients.eservices;

import java.util.ArrayList;
import java.util.List;

import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONException;

import com.code.exceptions.BusinessException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class BaseClient {
		
	protected static <T> List<T> getEntityList(Class<T> dataClass, String jsonString) throws BusinessException{
		List<T> returnedList = new ArrayList<T>();
		try {
 			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JSONArray jsonArr = new JSONArray(jsonString);

			for (int i = 0; i < jsonArr.length(); i++) {
				returnedList.add(gson.fromJson(jsonArr.getJSONObject(i).toString(), dataClass));
			}
		} catch (JsonSyntaxException | JSONException e) {
			throw new BusinessException("error_inConnectionToExtensionSystem");
		}
		return returnedList;
	}
}
