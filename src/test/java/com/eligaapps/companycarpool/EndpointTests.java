package com.eligaapps.companycarpool;

import static org.junit.Assert.*;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.BeforeClass;
import org.junit.Test;

import com.eligaapps.companycarpool.model.Location;
import com.eligaapps.companycarpool.model.Person;
import com.eligaapps.companycarpool.types.ROLE;
import com.eligaapps.companycarpool.utils.GsonUtils;
import com.google.gson.Gson;

public class EndpointTests {

	private static CloseableHttpClient client;
	private static HttpClientBuilder httpClientBuilder;

	private static String host = "http://localhost:8080";

	@BeforeClass
	public static void setup() {
		httpClientBuilder = HttpClientBuilder.create();
		try {
			client = httpClientBuilder.build();

			HttpPost httpPost = new HttpPost(host + "/register");
			Person person = new Person();
			person.setFirstName("Donald");
			person.setLastName("Schellberg");
			person.setPassword("p2345");
			person.setEmail("dschellberg@gmail.com");
			person.setPhoneNumber("4846883222");
			Location location = new Location();
			location.setStreet("407 Krams Ave");
			location.setCity("Philadelphia");
			location.setState("Pa");
			location.setCountry("USA");
			location.setLatitude(12.4);
			location.setLongitude(-34.0);
			person.setLocation(location);
;

			httpPost.addHeader("accept", "application/json");
			httpPost.addHeader("content-type", "application/json");
			Gson gson = new Gson();
			String jsonStr = gson.toJson(person);
			HttpEntity entity = new StringEntity(jsonStr);
			httpPost.setEntity(entity);
			HttpResponse response = client.execute(httpPost);
			System.out.println(response.getStatusLine().getStatusCode());
			System.out.println("test");
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	@Test
	public void loginTest() throws Exception {
		client = httpClientBuilder.build();
		HttpPost httpPost = new HttpPost(host + "/login");
		httpPost.addHeader("content-type", "application/x-www-form-urlencoded");
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("username", "dschellberg@gmail.com"));
		urlParameters.add(new BasicNameValuePair("password", "p2345"));
		httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
		HttpResponse response = client.execute(httpPost);
		assertEquals(200, response.getStatusLine().getStatusCode());

	}

	@Test
	public void mchangePasswordTest() throws Exception {
		client = httpClientBuilder.build();
		HttpPost httpPost = new HttpPost(host + "/login");
		httpPost.addHeader("content-type", "application/x-www-form-urlencoded");
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("username", "dschellberg@gmail.com"));
		urlParameters.add(new BasicNameValuePair("password", "p2345"));
		httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
		HttpResponse response = client.execute(httpPost);
		assertEquals(200, response.getStatusLine().getStatusCode());
		httpPost.releaseConnection();
		httpPost = new HttpPost(host + "/changePassword");

		urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("email", "dschellberg@gmail.com"));
		urlParameters.add(new BasicNameValuePair("currentPassword", "p2345"));
		urlParameters.add(new BasicNameValuePair("newPassword", "p23456"));
		httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
		response = client.execute(httpPost);
		String responseText = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
		assertTrue(responseText.contains("success"));
		httpPost = new HttpPost(host + "/changePassword");
		urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("email", "dschellberg@gmail.com"));
		urlParameters.add(new BasicNameValuePair("currentPassword", "p23456"));
		urlParameters.add(new BasicNameValuePair("newPassword", "p2345"));
		httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
		response = client.execute(httpPost);
		responseText = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
		assertTrue(responseText.contains("success"));
		httpPost.releaseConnection();
		httpPost = new HttpPost(host + "/logout");
		response = client.execute(httpPost);
		assertEquals(302, response.getStatusLine().getStatusCode());

	}

	@Test
	public void zlockoutTest() throws Exception {
		client = httpClientBuilder.build();
		HttpPost httpPost = new HttpPost(host + "/login");
		httpPost.addHeader("content-type", "application/x-www-form-urlencoded");
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("username", "dschellberg@gmail.com"));
		urlParameters.add(new BasicNameValuePair("password", "p23456"));
		httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
		for (int ii = 0; ii < 20; ii++) {
			HttpResponse response = client.execute(httpPost);
			if (ii > 10) {
				assertEquals("failed on iteration " + ii, 406, response.getStatusLine().getStatusCode());
			}
			httpPost.releaseConnection();
			System.out.println(response.getStatusLine());
		}
	}

}
