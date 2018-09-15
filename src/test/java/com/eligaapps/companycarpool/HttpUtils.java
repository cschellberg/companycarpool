package com.eligaapps.companycarpool;

import static org.junit.Assert.assertEquals;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

public class HttpUtils {

	public static String doGet(CloseableHttpClient client, String url, Integer expectedCode) throws Exception {
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse response = client.execute(httpGet);
			if (expectedCode != null) {
				assertEquals(expectedCode.intValue(), response.getStatusLine().getStatusCode());
			}
			String responseText = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
			return responseText;
		} finally {
			httpGet.releaseConnection();
		}

	}

	public static String doPost(CloseableHttpClient client, String url, List<NameValuePair> urlParameters,
			Map<String, String> headers, Integer expectedCode) throws Exception {
		HttpPost httpPost = new HttpPost(url);
		try {
			for (Entry<String, String> entry : headers.entrySet()) {
				httpPost.addHeader(entry.getKey(), entry.getValue());
			}
			httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
			HttpResponse response = client.execute(httpPost);
			if (expectedCode != null) {
				assertEquals(expectedCode.intValue(), response.getStatusLine().getStatusCode());
			}
			String responseText = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
			return responseText;
		} finally {
			httpPost.releaseConnection();
		}

	}

	public static String doPost(CloseableHttpClient client, String url, String body, Map<String, String> headers,
			Integer expectedCode) throws Exception {
		HttpPost httpPost = new HttpPost(url);
		try {
			for (Entry<String, String> entry : headers.entrySet()) {
				httpPost.addHeader(entry.getKey(), entry.getValue());
			}
			httpPost.setEntity(new StringEntity(body));
			HttpResponse response = client.execute(httpPost);
			if (expectedCode != null) {
				assertEquals(expectedCode.intValue(), response.getStatusLine().getStatusCode());
			}
			String responseText = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
			return responseText;
		} finally {
			httpPost.releaseConnection();
		}

	}
	
	public static void doDelete(CloseableHttpClient client, String url,Integer expectedCode)throws Exception{
		HttpDelete httpDelete= new HttpDelete(url);
		try{
        HttpResponse response= client.execute(httpDelete);
		if (expectedCode != null) {
			assertEquals(expectedCode.intValue(), response.getStatusLine().getStatusCode());
		}
        httpDelete.releaseConnection();
	    } finally{
	    	httpDelete.releaseConnection();
	    }
        
	}

	public static Map<String, String> getJSONHeaders() {
		Map<String, String> headers = new HashMap<>();
		headers.put("accept", "application/json");
		headers.put("content-type", "application/json");
		return headers;
	}

	public static Map<String, String> getFormHeaders() {
		Map<String, String> headers = new HashMap<>();
		headers.put("content-type", "application/x-www-form-urlencoded");
		return headers;
	}
}
