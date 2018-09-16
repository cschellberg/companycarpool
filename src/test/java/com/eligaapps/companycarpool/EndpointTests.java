package com.eligaapps.companycarpool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
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
	private static final String TEST_EMAIL = "dschellberg@gmail.com";
	private static final String TEST_PASSWORD = "p2345";
	private static final String TEST_ORGANIZATION = "Philadelphia Baha'i Community";

	@AfterClass
	public static void tearDown() {
		httpClientBuilder = HttpClientBuilder.create();
		try {
			client = httpClientBuilder.build();
			login(client, TEST_EMAIL, TEST_PASSWORD);
			String forgotPassword = host + "/forgotPassword?email=" + URLEncoder.encode(TEST_EMAIL, "UTF-8");
			HttpUtils.doGet(client, forgotPassword, 200);
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("email", TEST_EMAIL));
			urlParameters.add(new BasicNameValuePair("currentPassword", "blahxyz1863"));
			urlParameters.add(new BasicNameValuePair("newPassword", TEST_PASSWORD));
			HttpUtils.doPost(client, host + "/changePassword", urlParameters, HttpUtils.getFormHeaders(), 200);
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	@BeforeClass
	public static void setup() {
		httpClientBuilder = HttpClientBuilder.create();
		try {
			client = httpClientBuilder.build();
			Person person = new Person();
			person.setFirstName("Donald");
			person.setLastName("Schellberg");
			person.setPassword(TEST_PASSWORD);
			person.setEmail(TEST_EMAIL);
			person.setPhoneNumber("4846883222");
			person.setRole(ROLE.admin);

			person.setLocation(getLocation());
			Gson gson = new Gson();
			String jsonStr = gson.toJson(person);
			HttpUtils.doPost(client, host + "/register", jsonStr, HttpUtils.getJSONHeaders(), null);
			HttpUtils.doGet(client, host + "/activate?email=dschellberg@gmail.com&activationKey="
					+ PersonController.TEST_ACTIVATION_KEY, 200);

		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	private static Location getLocation() {
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
		login(client, TEST_EMAIL, TEST_PASSWORD);

	}

	private static void login(CloseableHttpClient httpClient, String email, String password) throws Exception {
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("username", TEST_EMAIL));
		urlParameters.add(new BasicNameValuePair("password", TEST_PASSWORD));
		HttpUtils.doPost(httpClient, host + "/login", urlParameters, HttpUtils.getFormHeaders(), null);
	}

	@Test
	public void mchangePasswordTest() throws Exception {
		client = httpClientBuilder.build();
		login(client, TEST_EMAIL, TEST_PASSWORD);
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("email", TEST_EMAIL));
		urlParameters.add(new BasicNameValuePair("currentPassword", TEST_PASSWORD));
		urlParameters.add(new BasicNameValuePair("newPassword", "p23456"));
		String responseText = HttpUtils.doPost(client, host + "/changePassword", urlParameters,
				HttpUtils.getFormHeaders(), null);
		assertTrue(responseText.contains("success"));
		urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("email", TEST_EMAIL));
		urlParameters.add(new BasicNameValuePair("currentPassword", "p23456"));
		urlParameters.add(new BasicNameValuePair("newPassword", TEST_PASSWORD));
		responseText = HttpUtils.doPost(client, host + "/changePassword", urlParameters, HttpUtils.getFormHeaders(),
				null);
		assertTrue(responseText.contains("success"));
		HttpUtils.doPost(client, host + "/logout", urlParameters, HttpUtils.getFormHeaders(), 302);

	}

	@Test
	public void organizationTest() throws Exception {
		client = httpClientBuilder.build();
		login(client, TEST_EMAIL, TEST_PASSWORD);
		Organization organization = new Organization();
		organization.setName(TEST_ORGANIZATION);

		Gson gson = TestUtils.getGson();

		String jsonStr = gson.toJson(organization);
		HttpUtils.doPost(client, host + "/organization", jsonStr, HttpUtils.getJSONHeaders(), null);

		organization = HttpUtils.getOrganization(client,
				host + "/organization/" + URLEncoder.encode(TEST_ORGANIZATION, "UTF-8"), gson);
		assertEquals(TEST_ORGANIZATION, organization.getName());

		OrgEvent orgEvent = new OrgEvent();
		orgEvent.setOrganization(organization);
		orgEvent.setName("Devotional");
		orgEvent.setTime(new Date());
		orgEvent.setLocation(getLocation());
		jsonStr = gson.toJson(orgEvent);
		HttpUtils.doPost(client, host + "/orgEvent", jsonStr, HttpUtils.getJSONHeaders(), null);

		organization = HttpUtils.getOrganization(client,
				host + "/organization/" + URLEncoder.encode(TEST_ORGANIZATION, "UTF-8"), gson);
		assertEquals(TEST_ORGANIZATION, organization.getName());
		assertTrue(organization.getEvents().size() > 0);
		assertEquals("Devotional", organization.getEvents().get(0).getName());

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("orgEventId", organization.getEvents().get(0).getId().toString()));
		urlParameters.add(new BasicNameValuePair("email", TEST_EMAIL));
		HttpUtils.doPost(client, host + "/addRideRequest", urlParameters, HttpUtils.getFormHeaders(), null);

		organization = HttpUtils.getOrganization(client,
				host + "/organization/" + URLEncoder.encode(TEST_ORGANIZATION, "UTF-8"), gson);
		assertEquals(TEST_EMAIL, organization.getEvents().get(0).getRideRequests().get(0).getEmail());

		HttpUtils.offerRide(client, host + "/offerRide", organization.getEvents().get(0).getId(), TEST_EMAIL,
				TEST_EMAIL);

		organization = HttpUtils.getOrganization(client,
				host + "/organization/" + URLEncoder.encode(TEST_ORGANIZATION, "UTF-8"), gson);
		assertTrue(organization.getEvents().get(0).getRideRequests().isEmpty());
		assertEquals(TEST_EMAIL, organization.getEvents().get(0).getRides().get(0).getDriver().getEmail());

		HttpUtils.offerRide(client, host + "/offerRide", organization.getEvents().get(0).getId(), TEST_EMAIL,
				TEST_EMAIL);

		
		HttpUtils.cancelRequest(client, host + "/cancelRequest", organization.getEvents().get(0).getId(), TEST_EMAIL);
		organization = HttpUtils.getOrganization(client,
				host + "/organization/" + URLEncoder.encode(TEST_ORGANIZATION, "UTF-8"), gson);
		assertTrue(organization.getEvents().get(0).getRideRequests().isEmpty());
		assertFalse(TEST_EMAIL, organization.getEvents().get(0).getRides().isEmpty());

		
		
		String deleteUrl = host + "/orgEvent/" + organization.getEvents().get(0).getId();
		HttpUtils.doDelete(client, deleteUrl, 200);

		organization = HttpUtils.getOrganization(client,
				host + "/organization/" + URLEncoder.encode(TEST_ORGANIZATION, "UTF-8"), gson);
		assertEquals(TEST_ORGANIZATION, organization.getName());
		assertTrue(organization.getEvents().size() == 0);

	}

	@Test
	public void zlockoutTest() throws Exception {
		client = httpClientBuilder.build();
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("username", TEST_EMAIL));
		urlParameters.add(new BasicNameValuePair("password", "p23456"));
		for (int ii = 0; ii < 20; ii++) {

			if (ii > 10) {
				HttpUtils.doPost(client, host + "/login", urlParameters, HttpUtils.getFormHeaders(), 406);
			} else {
				HttpUtils.doPost(client, host + "/login", urlParameters, HttpUtils.getFormHeaders(), null);
			}
		}
	}

}
