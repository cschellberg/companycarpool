package com.eligaapps.companycarpool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.eligaapps.companycarpool.controllers.PersonController;
import com.eligaapps.companycarpool.model.Location;
import com.eligaapps.companycarpool.model.OrgEvent;
import com.eligaapps.companycarpool.model.Organization;
import com.eligaapps.companycarpool.model.Person;
import com.eligaapps.companycarpool.types.ROLE;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EndpointTests {

	private static CloseableHttpClient client;
	private static HttpClientBuilder httpClientBuilder;

	private static final String host = "http://localhost:8080";
    private static final String TEST_EMAIL="dschellberg@gmail.com";
    private static final String TEST_PASSWORD="p2345";
    private static final String TEST_ORGANIZATION="Philadelphia Baha'i Community";
	
	@AfterClass
	public static void tearDown (){
		httpClientBuilder = HttpClientBuilder.create();
		try {
			client = httpClientBuilder.build();
			login(client,TEST_EMAIL,TEST_PASSWORD);
			String forgotPassword=host+"/forgotPassword?email="+URLEncoder.encode(TEST_EMAIL, "UTF-8");
			HttpGet httpGet=new HttpGet(forgotPassword);
			HttpResponse response=client.execute(httpGet);
			assertEquals(200, response.getStatusLine().getStatusCode());
		    httpGet.releaseConnection();
			HttpPost httpPost = new HttpPost(host + "/changePassword");
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("email", TEST_EMAIL));
			urlParameters.add(new BasicNameValuePair("currentPassword", "blahxyz1863"));
			urlParameters.add(new BasicNameValuePair("newPassword", TEST_PASSWORD));
			httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
			response = client.execute(httpPost);
			assertEquals(200, response.getStatusLine().getStatusCode());
			httpPost.releaseConnection();
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}
	
	@BeforeClass
	public static void setup() {
		httpClientBuilder = HttpClientBuilder.create();
		try {
			client = httpClientBuilder.build();
			HttpPost httpPost = new HttpPost(host + "/register");
			Person person = new Person();
			person.setFirstName("Donald");
			person.setLastName("Schellberg");
			person.setPassword(TEST_PASSWORD);
			person.setEmail(TEST_EMAIL);
			person.setPhoneNumber("4846883222");
			person.setRole(ROLE.admin);

			person.setLocation(getLocation());
			httpPost.addHeader("accept", "application/json");
			httpPost.addHeader("content-type", "application/json");
			Gson gson = new Gson();
			String jsonStr = gson.toJson(person);
			HttpEntity entity = new StringEntity(jsonStr);
			httpPost.setEntity(entity);
			HttpResponse response = client.execute(httpPost);
			httpPost.releaseConnection();
			HttpGet httpGet=new HttpGet(host+"/activate?email=dschellberg@gmail.com&activationKey="+PersonController.TEST_ACTIVATION_KEY);
		    response=client.execute(httpGet);
		    httpGet.releaseConnection();
		    assertEquals(200,response.getStatusLine().getStatusCode());
		    
		    
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}
	
	private static Location getLocation(){
		Location location = new Location();
		location.setStreet("407 Krams Ave");
		location.setCity("Philadelphia");
		location.setState("Pa");
		location.setCountry("USA");
		location.setLatitude(12.4);
		location.setLongitude(-34.0);
		return location;
	}

	@Test
	public void loginTest() throws Exception {
		client = httpClientBuilder.build();
        login(client,TEST_EMAIL,TEST_PASSWORD);
        
	}
	
	private static void login(CloseableHttpClient httpClient,String email, String password) throws Exception{
		HttpPost httpPost = new HttpPost(host + "/login");
		httpPost.addHeader("content-type", "application/x-www-form-urlencoded");
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("username", TEST_EMAIL));
		urlParameters.add(new BasicNameValuePair("password", TEST_PASSWORD));
		httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
		HttpResponse response = httpClient.execute(httpPost);
		assertEquals(200, response.getStatusLine().getStatusCode());
		httpPost.releaseConnection();
	}

	@Test
	public void mchangePasswordTest() throws Exception {
		client = httpClientBuilder.build();
		login(client,TEST_EMAIL,TEST_PASSWORD);
		HttpPost httpPost = new HttpPost(host + "/changePassword");

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("email", TEST_EMAIL));
		urlParameters.add(new BasicNameValuePair("currentPassword", TEST_PASSWORD));
		urlParameters.add(new BasicNameValuePair("newPassword", "p23456"));
		httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
		HttpResponse response = client.execute(httpPost);
		String responseText = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
		assertTrue(responseText.contains("success"));
		httpPost = new HttpPost(host + "/changePassword");
		urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("email", TEST_EMAIL));
		urlParameters.add(new BasicNameValuePair("currentPassword", "p23456"));
		urlParameters.add(new BasicNameValuePair("newPassword", TEST_PASSWORD));
		httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
		response = client.execute(httpPost);
		responseText = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
		assertTrue(responseText.contains("success"));
		httpPost.releaseConnection();
		httpPost = new HttpPost(host + "/logout");
		response = client.execute(httpPost);
		assertEquals(302, response.getStatusLine().getStatusCode());
		httpPost.releaseConnection();

	}

    @Test
    public void organizationTest () throws Exception{
    	client = httpClientBuilder.build();
    	login(client,TEST_EMAIL,TEST_PASSWORD);
    	Organization organization=new Organization();
    	organization.setName(TEST_ORGANIZATION);
    	HttpPost httpPost = new HttpPost(host + "/organization");
    	httpPost.addHeader("accept", "application/json");
		httpPost.addHeader("content-type", "application/json");
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
		String jsonStr = gson.toJson(organization);
		HttpEntity entity = new StringEntity(jsonStr);
		httpPost.setEntity(entity);
		HttpResponse response = client.execute(httpPost);
    	
		String organizationUri=host+"/organization/"+URLEncoder.encode(TEST_ORGANIZATION, "UTF-8");
		HttpGet httpGet=new HttpGet(organizationUri);
		response=client.execute(httpGet);
		assertEquals(200, response.getStatusLine().getStatusCode());
		String responseText = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
        organization=gson.fromJson(responseText, Organization.class);
        assertEquals(TEST_ORGANIZATION, organization.getName());
        httpGet.releaseConnection();
        
		httpPost = new HttpPost(host + "/orgEvent");
    	httpPost.addHeader("accept", "application/json");
		httpPost.addHeader("content-type", "application/json");
		OrgEvent orgEvent=new OrgEvent();
		orgEvent.setOrganization(organization);
		orgEvent.setName("Devotional");
		orgEvent.setTime(new Date());
		orgEvent.setLocation(getLocation());
		jsonStr = gson.toJson(orgEvent);
		entity = new StringEntity(jsonStr);
		httpPost.setEntity(entity);
		response = client.execute(httpPost);

		
		httpPost.releaseConnection();
		organizationUri=host+"/organization/"+URLEncoder.encode(TEST_ORGANIZATION, "UTF-8");
		httpGet=new HttpGet(organizationUri);
		response=client.execute(httpGet);
		assertEquals(200, response.getStatusLine().getStatusCode());
		responseText = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
        organization=gson.fromJson(responseText, Organization.class);
        assertEquals(TEST_ORGANIZATION, organization.getName());
        assertTrue(organization.getEvents().size() > 0);
        assertEquals("Devotional",organization.getEvents().get(0).getName());
	    httpGet.releaseConnection();
	    
		httpPost = new HttpPost(host + "/addRideRequest");
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("orgEventId", organization.getEvents().get(0).getId().toString()));
		urlParameters.add(new BasicNameValuePair("email", TEST_EMAIL));
		httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
		response = client.execute(httpPost);
		httpPost.releaseConnection();
		
		httpPost.releaseConnection();
		organizationUri=host+"/organization/"+URLEncoder.encode(TEST_ORGANIZATION, "UTF-8");
		httpGet=new HttpGet(organizationUri);
		response=client.execute(httpGet);
		assertEquals(200, response.getStatusLine().getStatusCode());
		responseText = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
        organization=gson.fromJson(responseText, Organization.class);
        assertEquals(TEST_EMAIL,organization.getEvents().get(0).getRideRequests().get(0).getEmail());
	    httpGet.releaseConnection();
	    
		httpPost = new HttpPost(host + "/offerRide");
		urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("orgEventId", organization.getEvents().get(0).getId().toString()));
		urlParameters.add(new BasicNameValuePair("driverEmail", TEST_EMAIL));
		urlParameters.add(new BasicNameValuePair("passengerEmail", TEST_EMAIL));
		httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
		response = client.execute(httpPost);
		httpPost.releaseConnection();
	    
		
		httpPost.releaseConnection();
		organizationUri=host+"/organization/"+URLEncoder.encode(TEST_ORGANIZATION, "UTF-8");
		httpGet=new HttpGet(organizationUri);
		response=client.execute(httpGet);
		assertEquals(200, response.getStatusLine().getStatusCode());
		responseText = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
        organization=gson.fromJson(responseText, Organization.class);
        assertTrue(organization.getEvents().get(0).getRideRequests().isEmpty());
        assertEquals(TEST_EMAIL,organization.getEvents().get(0).getRides().get(0).getDriver().getEmail());
	    httpGet.releaseConnection();
	    
	    HttpDelete httpDelete= new HttpDelete(host + "/orgEvent/"+organization.getEvents().get(0).getId());
        client.execute(httpDelete);
        httpDelete.releaseConnection();
        
        organizationUri=host+"/organization/"+URLEncoder.encode(TEST_ORGANIZATION, "UTF-8");
		httpGet=new HttpGet(organizationUri);
		response=client.execute(httpGet);
		assertEquals(200, response.getStatusLine().getStatusCode());
		responseText = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
        organization=gson.fromJson(responseText, Organization.class);
        assertEquals(TEST_ORGANIZATION, organization.getName());
        assertTrue(organization.getEvents().size() == 0);
    	
    }
	
	@Test
	public void zlockoutTest() throws Exception {
		client = httpClientBuilder.build();
		HttpPost httpPost = new HttpPost(host + "/login");
		httpPost.addHeader("content-type", "application/x-www-form-urlencoded");
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("username", TEST_EMAIL));
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
