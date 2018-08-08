package com.eligaapps.companycarpool.utils;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GsonUtils {

	public static <T> List<T> toList(String json, Class<T> clazz) {
	    if (null == json) {
	        return null;
	    }
	    Gson gson = new Gson();
	    return gson.fromJson(json, new TypeToken<T>(){}.getType());
	}
}
