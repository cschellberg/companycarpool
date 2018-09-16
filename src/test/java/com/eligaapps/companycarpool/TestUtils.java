package com.eligaapps.companycarpool;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestUtils {
	
	public static Gson getGson(){
		Gson gson = new GsonBuilder().addDeserializationExclusionStrategy(new ExclusionStrategy(){
		@Override
		public boolean shouldSkipField(FieldAttributes fa) {
			 String className = fa.getDeclaringClass().getName();
		        String fieldName = fa.getName();
		        return 
		            className.equals("com.eligaapps.companycarpool.model.OrgEvent")
		                && fieldName.equals("organization");
		}

		@Override
		public boolean shouldSkipClass(Class<?> clazz) {
			// TODO Auto-generated method stub
			return false;
		}
		
	})
	        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	        .create();
		return gson;
	}

}
