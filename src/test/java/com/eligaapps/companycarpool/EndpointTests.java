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

	@BeforeClass
	public static void setup() {
	}

	@Test
	public void tests() throws Exception {
		String host = "http://localhost:8080";
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		CloseableHttpClient client = httpClientBuilder.build();
		HttpPost httpPost = new HttpPost(host + "/register");
		Person person = new Person();
		person.setFirstName("Donald");
		person.setLastName("Schellberg");
		person.setPassword("p2345");
		person.setEmail("dschellberg@gmail.com");
		person.setPhoneNumber("4846883222");
		Location location =new Location();
		location.setStreet("407 Krams Ave");
		location.setCity("Philadelphia");
		location.setState("Pa");
		location.setCountry("USA");
		location.setLatitude(12.4);
		location.setLongitude(-34.0);
		person.setLocation(location);
		person.setRole(ROLE.admin);

		httpPost.addHeader("accept", "application/json");
		httpPost.addHeader("content-type", "application/json");
		Gson gson = new Gson();
		String jsonStr = gson.toJson(person);
		HttpEntity entity = new StringEntity(jsonStr);
		httpPost.setEntity(entity);
		HttpResponse response = client.execute(httpPost);
		System.out.println(IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset()));
		httpPost = new HttpPost(host + "/login");
		httpPost.addHeader("content-type", "application/x-www-form-urlencoded");
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("username", "dschellberg@gmail.com"));
		urlParameters.add(new BasicNameValuePair("password", "p2345"));
		httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
		response = client.execute(httpPost);
		HttpGet httpGet=new HttpGet(host+"/person/list");
		response = client.execute(httpGet);
		assertEquals(200,response.getStatusLine().getStatusCode());
		jsonStr=IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
		List<Person> personList=GsonUtils.toList(jsonStr, Person.class);
		assertTrue(personList.size()>0);
		System.out.println("SUCCESS !!!!");
	}
}
