/*
 * Unless explicitly acquired and licensed from Licensor under another license, the contents of
 * this file are subject to the Reciprocal Public License ("RPL") Version 1.5, or subsequent
 * versions as allowed by the RPL, and You may not copy or use this file in either source code
 * or executable form, except in compliance with the terms and conditions of the RPL
 *
 * All software distributed under the RPL is provided strictly on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, AND LICENSOR HEREBY DISCLAIMS ALL SUCH
 * WARRANTIES, INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, QUIET ENJOYMENT, OR NON-INFRINGEMENT. See the RPL for specific language
 * governing rights and limitations under the RPL.
 *
 * http://opensource.org/licenses/RPL-1.5
 *
 * Copyright 2012-2017 Open Justice Broker Consortium
 */
package org.ojbc.bundles.utilities.auditing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.apache.camel.test.spring.UseAdviceWith;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ojbc.audit.enhanced.dao.EnhancedAuditDAO;
import org.ojbc.audit.enhanced.dao.model.FederalRapbackNotification;
import org.ojbc.audit.enhanced.dao.model.FederalRapbackSubscription;
import org.ojbc.audit.enhanced.dao.model.PrintResults;
import org.ojbc.audit.enhanced.dao.model.QueryRequestByDateRange;
import org.ojbc.audit.enhanced.dao.model.UserInfo;
import org.ojbc.util.model.rapback.AgencyProfile;
import org.ojbc.util.model.rapback.ExpiringSubscriptionRequest;
import org.ojbc.util.model.rapback.FederalRapbackSubscriptionDetail;
import org.ojbc.util.model.rapback.Subscription;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

@UseAdviceWith	// NOTE: this causes Camel contexts to not start up automatically
@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
		"classpath:META-INF/spring/properties-context.xml",
		"classpath:META-INF/spring/camel-context.xml",
		"classpath:META-INF/spring/test-beans.xml",
		"classpath:META-INF/spring/cxf-endpoints.xml",
		"classpath:META-INF/spring/h2-mock-database-application-context.xml",
		"classpath:META-INF/spring/h2-mock-database-context-rapback-datastore.xml" })
public class TestAuditRestImpl {

	private Logger logger = Logger.getLogger(TestAuditRestImpl.class.getName());
	
    @Resource
    private ModelCamelContext context;

    @Resource
    private RestTemplate restTemplate;
    
	@Resource
	private EnhancedAuditDAO enhancedAuditDao;
    
    //This is used to update database to achieve desired state for test
	private JdbcTemplate jdbcTemplate;
	
	@Resource(name="dataSourceSubscriptions")
	private DataSource dataSource;

	@Before
	public void setUp() throws Exception {
		
    	context.start();
    	this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
    
	@Test
	public void testAuditRestImplSend() throws Exception
	{
		final String uri = "http://localhost:9898/auditServer/audit/printResults";
		
		PrintResults printResults = new PrintResults();
		
		printResults.setDescription("description");
		printResults.setMessageId("12345");
		printResults.setSystemName("system name");
		
		PrintResults printResultsResponse = restTemplate.postForObject(uri, printResults, PrintResults.class);
		
		logger.info(printResultsResponse.toString());
		
		assertEquals("description", printResultsResponse.getDescription());
		assertEquals("12345", printResultsResponse.getMessageId());
		assertEquals("system name", printResultsResponse.getSystemName());
		
		PrintResults printResultsFromDB = enhancedAuditDao.retrievePrintResultsfromMessageID("12345");
		
		assertEquals("description", printResultsFromDB.getDescription());
		assertEquals("12345", printResultsFromDB.getMessageId());
		assertEquals("system name", printResultsFromDB.getSystemName());
	}
	
	@Test
	public void testAuditRestUserLogin() throws Exception
	{
		final String uri = "http://localhost:9898/auditServer/audit/userLogin";
		
		UserInfo userInfo = new UserInfo();
		
		userInfo.setEmployerName("employer");
		userInfo.setEmployerSubunitName("sub");
		userInfo.setFederationId("fed");
		userInfo.setIdentityProviderId("idpID");
		userInfo.setUserEmailAddress("email");
		userInfo.setUserFirstName("first");
		userInfo.setUserLastName("last");
		
		UserInfo userInfoResults = restTemplate.postForObject(uri, userInfo, UserInfo.class);
		
		logger.info(userInfoResults.toString());
		
		assertEquals("employer", userInfoResults.getEmployerName());
		assertEquals("sub", userInfoResults.getEmployerSubunitName());
		assertEquals("fed", userInfoResults.getFederationId());
		assertEquals("idpID", userInfoResults.getIdentityProviderId());
		assertEquals("email", userInfoResults.getUserEmailAddress());
		assertEquals("first", userInfoResults.getUserFirstName());
		assertEquals("last", userInfoResults.getUserLastName());
		
	}
	
	@Test
	public void testAuditRestUserLogout() throws Exception
	{
		final String uri = "http://localhost:9898/auditServer/audit/userLogout";
		
		UserInfo userInfo = new UserInfo();
		
		userInfo.setEmployerName("employer");
		userInfo.setEmployerSubunitName("sub");
		userInfo.setFederationId("fed");
		userInfo.setIdentityProviderId("idpID");
		userInfo.setUserEmailAddress("email");
		userInfo.setUserFirstName("first");
		userInfo.setUserLastName("last");
		
		UserInfo userInfoResults = restTemplate.postForObject(uri, userInfo, UserInfo.class);
		
		logger.info(userInfoResults.toString());
		
		assertEquals("employer", userInfoResults.getEmployerName());
		assertEquals("sub", userInfoResults.getEmployerSubunitName());
		assertEquals("fed", userInfoResults.getFederationId());
		assertEquals("idpID", userInfoResults.getIdentityProviderId());
		assertEquals("email", userInfoResults.getUserEmailAddress());
		assertEquals("first", userInfoResults.getUserFirstName());
		assertEquals("last", userInfoResults.getUserLastName());
		
	}	
	
	@Test
	public void testSearchForFederalRapbackSubscriptionsByStateSubscriptionId() throws Exception
	{
		String uri = "http://localhost:9898/auditServer/audit/searchForFederalRapbackSubscriptionsByStateSubscriptionId";
		
		saveFederalRapbackSubscription("456", "text", "");

		ResponseEntity<List<FederalRapbackSubscription>> fedSubscriptionsResponse =
		        restTemplate.exchange(uri + "/456",
		                    HttpMethod.GET, null, new ParameterizedTypeReference<List<FederalRapbackSubscription>>() {
		            });
		List<FederalRapbackSubscription> fedSubscriptions = fedSubscriptionsResponse.getBody();
		
		assertNotNull(fedSubscriptions);
		assertEquals(1, fedSubscriptions.size());
		
		FederalRapbackSubscription federalRapbackSubscriptionFromDatabase =  fedSubscriptions.get(0);
		
		assertEquals("9999999", federalRapbackSubscriptionFromDatabase.getTransactionControlReferenceIdentification());
		assertEquals("/some/path/to/requestFile", federalRapbackSubscriptionFromDatabase.getPathToRequestFile());
		
		assertEquals("123", federalRapbackSubscriptionFromDatabase.getSid());
		assertEquals("456", federalRapbackSubscriptionFromDatabase.getStateSubscriptionId());
		assertEquals("CS", federalRapbackSubscriptionFromDatabase.getSubscriptonCategoryCode());
		assertEquals("text", federalRapbackSubscriptionFromDatabase.getTransactionStatusText());
		
	}

	private FederalRapbackSubscription saveFederalRapbackSubscription(String stateSubscriptionID, String transactionStatusText, String transactionCategoryCodeRequest) {
		FederalRapbackSubscription federalRapbackSubscription = new FederalRapbackSubscription();
		
		federalRapbackSubscription.setTransactionControlReferenceIdentification("9999999");
		federalRapbackSubscription.setPathToRequestFile("/some/path/to/requestFile");
		federalRapbackSubscription.setRequestSentTimestamp(LocalDateTime.now());
		federalRapbackSubscription.setSid("123");
		federalRapbackSubscription.setStateSubscriptionId(stateSubscriptionID);
		federalRapbackSubscription.setSubscriptonCategoryCode("CS");
		federalRapbackSubscription.setTransactionStatusText(transactionStatusText);
		federalRapbackSubscription.setTransactionCategoryCodeRequest(transactionCategoryCodeRequest);
		
		Integer federalRapbackSubscriptionPk = enhancedAuditDao.saveFederalRapbackSubscription(federalRapbackSubscription);
		
		federalRapbackSubscription.setFederalRapbackSubscriptionId(federalRapbackSubscriptionPk);
		
		return federalRapbackSubscription;
	}
	
	@Test
	public void testRetrieveExpiringSubscriptions() throws Exception
	{
		String uri = "http://localhost:9898/auditServer/audit/retrieveExpiringSubscriptions";
		
    	DateTime now = new DateTime();
    	DateTime updatedEndDate= now.plusDays(1);
    	String updatedEndDateAsString = updatedEndDate.toString("yyyy-MM-dd");

    	//We will use these subscriptions for our tests, update their validation dates so they aren't filtered out
    	int rowsUpdated = this.jdbcTemplate.update("update SUBSCRIPTION set enddate='" + updatedEndDateAsString  + "', agency_case_number=12345 where ID ='62724'");
    	assertEquals(1, rowsUpdated);

    	rowsUpdated = this.jdbcTemplate.update("update SUBSCRIPTION set validationduedate='" + updatedEndDateAsString  + "', agency_case_number=56789 where ID ='62725'");
    	assertEquals(1, rowsUpdated);
		
		ExpiringSubscriptionRequest expiringSubscriptionRequest = new ExpiringSubscriptionRequest();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		List<String> oris = new ArrayList<String>();
		oris.add("1234567890");
		expiringSubscriptionRequest.setOris(oris);
		
		expiringSubscriptionRequest.setDaysUntilExpiry(2);
		expiringSubscriptionRequest.setSystemName("{http://demostate.gov/SystemNames/1.0}SystemC");
		
		HttpEntity<ExpiringSubscriptionRequest> entity = new HttpEntity<ExpiringSubscriptionRequest>(expiringSubscriptionRequest, headers);
		
		ResponseEntity<List<Subscription>> response = restTemplate.exchange(uri, HttpMethod.POST, entity, new ParameterizedTypeReference<List<Subscription>>() {});
		
		List<Subscription> subscriptions = response.getBody();
		
		assertEquals(2, subscriptions.size());
		assertEquals("2014-10-15",subscriptions.get(0).getStartDate().toString("yyyy-MM-dd"));
		assertEquals(updatedEndDateAsString,subscriptions.get(0).getEndDate().toString("yyyy-MM-dd"));
		assertEquals(now.toString("yyyy-MM-dd"),subscriptions.get(0).getValidationDueDate().toString("yyyy-MM-dd"));
		assertEquals("2014-10-15",subscriptions.get(0).getCreationDate().toString("yyyy-MM-dd"));
		
		assertEquals("12345",subscriptions.get(0).getAgencyCaseNumber());
		assertEquals("Test W Jane",subscriptions.get(0).getPersonFullName());
		assertEquals("1234567890",subscriptions.get(0).getOri());
		assertEquals("Demo Agency",subscriptions.get(0).getAgencyName());
		assertEquals("bill",subscriptions.get(0).getSubscriptionOwnerFirstName());
		assertEquals("padmanabhan",subscriptions.get(0).getSubscriptionOwnerLastName());
		assertEquals("12345",subscriptions.get(0).getAgencyCaseNumber());

		assertEquals("56789",subscriptions.get(1).getAgencyCaseNumber());

	}

	@Test
	public void testRetrieveExpiredSubscriptionsPojo() throws Exception
	{
		String uri = "http://localhost:9898/auditServer/audit/retrieveExpiredSubscriptions";
		
    	DateTime now = new DateTime();
    	DateTime updatedEndDate= now.minusDays(1);
    	String updatedEndDateAsString = updatedEndDate.toString("yyyy-MM-dd");

    	//We will use these subscriptions for our tests, update their validation dates so they aren't filtered out
    	int rowsUpdated = this.jdbcTemplate.update("update SUBSCRIPTION set enddate='" + updatedEndDateAsString  + "', agency_case_number='12345' where ID ='62724'");
    	assertEquals(1, rowsUpdated);
    	
    	rowsUpdated = this.jdbcTemplate.update("update SUBSCRIPTION set validationduedate='" + updatedEndDateAsString  + "', agency_case_number=56789 where ID ='62725'");
    	assertEquals(1, rowsUpdated);    	
    			
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		ExpiringSubscriptionRequest expiringSubscriptionRequest = new ExpiringSubscriptionRequest();
		
		List<String> oris = new ArrayList<String>();
		oris.add("1234567890");
		
		expiringSubscriptionRequest.setDaysUntilExpiry(2);

		expiringSubscriptionRequest.setOris(oris);
		
		expiringSubscriptionRequest.setSystemName("{http://demostate.gov/SystemNames/1.0}SystemC");
		
		HttpEntity<ExpiringSubscriptionRequest> entity = new HttpEntity<ExpiringSubscriptionRequest>(expiringSubscriptionRequest, headers);
		
		ResponseEntity<List<Subscription>> response = restTemplate.exchange(uri, HttpMethod.POST, entity, new ParameterizedTypeReference<List<Subscription>>() {});
		
		List<Subscription> subscriptions = response.getBody();
		
		assertEquals(2, subscriptions.size());
		assertEquals("2014-10-15",subscriptions.get(0).getStartDate().toString("yyyy-MM-dd"));
		assertEquals(updatedEndDateAsString,subscriptions.get(0).getEndDate().toString("yyyy-MM-dd"));
		assertEquals(now.toString("yyyy-MM-dd"),subscriptions.get(0).getValidationDueDate().toString("yyyy-MM-dd"));
		assertEquals("2014-10-15",subscriptions.get(0).getCreationDate().toString("yyyy-MM-dd"));
		
		assertEquals("12345",subscriptions.get(0).getAgencyCaseNumber());
		assertEquals("Test W Jane",subscriptions.get(0).getPersonFullName());
		assertEquals("1234567890",subscriptions.get(0).getOri());
		assertEquals("Demo Agency",subscriptions.get(0).getAgencyName());
		assertEquals("bill",subscriptions.get(0).getSubscriptionOwnerFirstName());
		assertEquals("padmanabhan",subscriptions.get(0).getSubscriptionOwnerLastName());
		assertEquals("12345",subscriptions.get(0).getAgencyCaseNumber());

		assertEquals("56789",subscriptions.get(1).getAgencyCaseNumber());
		
	}
	
	
	@Test
	public void testReturnAllAgencies() throws Exception
	{
		String uri = "http://localhost:9898/auditServer/audit/retrieveAllAgencies";
		
		ResponseEntity<List<AgencyProfile>> agencyProfileResponse =
		        restTemplate.exchange(uri,
		                    HttpMethod.GET, null, new ParameterizedTypeReference<List<AgencyProfile>>() {
		            });
		
		List<AgencyProfile> agencyProfiles = agencyProfileResponse.getBody();
		
		assertNotNull(agencyProfiles);
		assertEquals(3, agencyProfiles.size());
		
		AgencyProfile agency1 = agencyProfiles.get(0);
		
		assertEquals("Demo Agency",agency1.getAgencyName());
		assertEquals("1234567890",agency1.getAgencyOri());
		assertEquals(false,agency1.getCivilAgencyIndicator());
		assertEquals(true,agency1.getFbiSubscriptionQualification());

	}
	
	@Test
	public void testRetrieveRapbackNotifications() throws Exception
	{
		FederalRapbackNotification federalRapbackNotification = new FederalRapbackNotification();

		federalRapbackNotification.setNotificationRecievedTimestamp(LocalDateTime.now());
		federalRapbackNotification.setOriginalIdentifier("123");
		federalRapbackNotification.setUpdatedIdentifier("456");
		federalRapbackNotification.setPathToNotificationFile("/tmp/path/toNotificationFile");
		federalRapbackNotification.setRapBackEventText("Rapback event text");
		federalRapbackNotification.setStateSubscriptionId("State12345");
		federalRapbackNotification.setTransactionType("NOTIFICATION_MATCHING_SUBSCRIPTION");
		
		Integer pk = enhancedAuditDao.saveFederalRapbackNotification(federalRapbackNotification);
		
		enhancedAuditDao.saveTriggeringEvent(pk, 1);
		enhancedAuditDao.saveTriggeringEvent(pk, 2);
		
		final String uri = "http://localhost:9898/auditServer/audit/retrieveRapbackNotifications";
		
		QueryRequestByDateRange queryRequestByDateRange = new QueryRequestByDateRange();
		
		queryRequestByDateRange.setStartDate(LocalDate.now().minusDays(7));
		queryRequestByDateRange.setEndDate(LocalDate.now());
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<QueryRequestByDateRange> entity = new HttpEntity<QueryRequestByDateRange>(queryRequestByDateRange, headers);
		
		ResponseEntity<List<FederalRapbackNotification>> response = restTemplate.exchange(uri, HttpMethod.POST, entity, new ParameterizedTypeReference<List<FederalRapbackNotification>>() {});
		
		List<FederalRapbackNotification> federalRapbackNotificationFromService = response.getBody();
		
		assertEquals(1, federalRapbackNotificationFromService.size());
		
		assertEquals("123",federalRapbackNotificationFromService.get(0).getOriginalIdentifier());
		assertEquals("456",federalRapbackNotificationFromService.get(0).getUpdatedIdentifier());
		assertEquals("/tmp/path/toNotificationFile",federalRapbackNotificationFromService.get(0).getPathToNotificationFile());
		assertEquals("Rapback event text",federalRapbackNotificationFromService.get(0).getRapBackEventText());
		assertEquals("State12345",federalRapbackNotificationFromService.get(0).getStateSubscriptionId());
		assertEquals("NOTIFICATION_MATCHING_SUBSCRIPTION",federalRapbackNotificationFromService.get(0).getTransactionType());
		assertEquals(2, federalRapbackNotificationFromService.get(0).getTriggeringEvents().size());
		
	}		
	
	@Test
	public void testSearchForFederalRapbackNotificationsByStateSubscriptionId() throws Exception
	{
		String uri = "http://localhost:9898/auditServer/audit/searchForFederalRapbackNotificationsByStateSubscriptionId";
		
		FederalRapbackNotification federalRapbackNotification = new FederalRapbackNotification();

		federalRapbackNotification.setNotificationRecievedTimestamp(LocalDateTime.now());
		federalRapbackNotification.setOriginalIdentifier("123");
		federalRapbackNotification.setUpdatedIdentifier("456");
		federalRapbackNotification.setPathToNotificationFile("/tmp/path/toNotificationFile");
		federalRapbackNotification.setRapBackEventText("Rapback event text");
		federalRapbackNotification.setStateSubscriptionId("State123456789");
		federalRapbackNotification.setTransactionType("NOTIFICATION_MATCHING_SUBSCRIPTION");
		
		Integer pk = enhancedAuditDao.saveFederalRapbackNotification(federalRapbackNotification);
		
		enhancedAuditDao.saveTriggeringEvent(pk, 1);
		enhancedAuditDao.saveTriggeringEvent(pk, 2);

		ResponseEntity<List<FederalRapbackNotification>> fedNotificationsResponseResponse =
		        restTemplate.exchange(uri + "/State123456789",
		                    HttpMethod.GET, null, new ParameterizedTypeReference<List<FederalRapbackNotification>>() {
		            });
		
		List<FederalRapbackNotification> fedNotification = fedNotificationsResponseResponse.getBody();
		
		assertNotNull(fedNotification);
		assertEquals(1, fedNotification.size());
		
		FederalRapbackNotification federalRapbackNotificationFromRestService =  fedNotification.get(0);
		
		assertEquals("123",federalRapbackNotificationFromRestService.getOriginalIdentifier());
		assertEquals("456",federalRapbackNotificationFromRestService.getUpdatedIdentifier());
		assertEquals("/tmp/path/toNotificationFile",federalRapbackNotificationFromRestService.getPathToNotificationFile());
		assertEquals("Rapback event text",federalRapbackNotificationFromRestService.getRapBackEventText());
		assertEquals("State123456789",federalRapbackNotificationFromRestService.getStateSubscriptionId());
		assertEquals("NOTIFICATION_MATCHING_SUBSCRIPTION",federalRapbackNotificationFromRestService.getTransactionType());
		assertEquals(2, federalRapbackNotificationFromRestService.getTriggeringEvents().size());
		
	}
	
	@Test
	public void testRetrieveFederalRapbackSubscriptionErrors() throws Exception
	{
		String uri = "http://localhost:9898/auditServer/audit/retrieveFederalRapbackSubscriptionErrors";
		
		String stateSubscriptionId = "State1112233";
		
		FederalRapbackSubscription federalRapbackSubscription = new FederalRapbackSubscription();
		
		federalRapbackSubscription.setTransactionControlReferenceIdentification("9999999");
		federalRapbackSubscription.setPathToRequestFile("/some/path/to/requestFile");
		federalRapbackSubscription.setRequestSentTimestamp(LocalDateTime.now());
		federalRapbackSubscription.setSid("123");
		federalRapbackSubscription.setStateSubscriptionId(stateSubscriptionId);
		federalRapbackSubscription.setFbiSubscriptionId("789");
		federalRapbackSubscription.setSubscriptonCategoryCode("CS");
		federalRapbackSubscription.setTransactionCategoryCodeRequest("RBSCRM");
		federalRapbackSubscription.setTransactionStatusText("This is an FBI error");
		
		//Save subscription with error
		Integer federalSubcriptionId = enhancedAuditDao.saveFederalRapbackSubscription(federalRapbackSubscription);
		
		//Now save error
		enhancedAuditDao.saveFederalRapbackSubscriptionError(federalSubcriptionId, stateSubscriptionId);
	
		ResponseEntity<List<FederalRapbackSubscription>> fedSubscriptionsResponse =
		        restTemplate.exchange(uri,
		                    HttpMethod.GET, null, new ParameterizedTypeReference<List<FederalRapbackSubscription>>() {
		            });
		List<FederalRapbackSubscription> fedSubscriptions = fedSubscriptionsResponse.getBody();
		
		assertNotNull(fedSubscriptions);
		assertEquals(1, fedSubscriptions.size());
		
		FederalRapbackSubscription federalRapbackSubscriptionFromDatabase =  fedSubscriptions.get(0);
		
		assertEquals("9999999", federalRapbackSubscriptionFromDatabase.getTransactionControlReferenceIdentification());
		assertEquals("/some/path/to/requestFile", federalRapbackSubscriptionFromDatabase.getPathToRequestFile());
		
		assertEquals("123", federalRapbackSubscriptionFromDatabase.getSid());
		assertEquals("State1112233", federalRapbackSubscriptionFromDatabase.getStateSubscriptionId());
		assertEquals("CS", federalRapbackSubscriptionFromDatabase.getSubscriptonCategoryCode());
		assertEquals("This is an FBI error", federalRapbackSubscriptionFromDatabase.getTransactionStatusText());
	}
	
	@Test
	public void testReturnFederalRapbackSubscriptionDetail() throws Exception
	{
		String uri = "http://localhost:9898/auditServer/audit/subscriptions/stateSub123/federalRapbackSubscriptions/detail";
		
		//Save a federal subscription creation request
		FederalRapbackSubscription federalRapbackSubscription = saveFederalRapbackSubscription("stateSub123", "", "RBSCRM");
		
		FederalRapbackSubscriptionDetail federalRapbackSubscriptionDetail = restTemplate.getForObject(uri, FederalRapbackSubscriptionDetail.class);
		
		logger.info(federalRapbackSubscriptionDetail.toString());
		
		//Confirm FBI subscription was sent but not yet created
		assertEquals(true, federalRapbackSubscriptionDetail.isFbiSubscriptionSent());
		assertEquals(false, federalRapbackSubscriptionDetail.isFbiSubscriptionCreated());
		
		//Set the FBI subscription ID and response code
		federalRapbackSubscription.setFbiSubscriptionId("12345");
		federalRapbackSubscription.setTransactionCategoryCodeResponse("RBSR");
		
		//Update database
		enhancedAuditDao.updateFederalRapbackSubscriptionWithResponse(federalRapbackSubscription);
		
		federalRapbackSubscriptionDetail = restTemplate.getForObject(uri, FederalRapbackSubscriptionDetail.class);
		
		//Confirm subscription was sent and created
		assertEquals(true, federalRapbackSubscriptionDetail.isFbiSubscriptionSent());
		assertEquals(true, federalRapbackSubscriptionDetail.isFbiSubscriptionCreated());
		
		
		//Now send a maintenance message
		federalRapbackSubscription = saveFederalRapbackSubscription("stateSub123", "", "RBMNT");
		
		federalRapbackSubscriptionDetail = restTemplate.getForObject(uri, FederalRapbackSubscriptionDetail.class);

		//Confirm subscription was sent and created
		assertEquals(true, federalRapbackSubscriptionDetail.isFbiSubscriptionSent());
		assertEquals(true, federalRapbackSubscriptionDetail.isFbiSubscriptionCreated());
		
		//Confirm maintenance was sent
		assertEquals(true, federalRapbackSubscriptionDetail.isFbiRapbackMaintenanceSent());
		assertEquals(false, federalRapbackSubscriptionDetail.isFbiRapbackMaintenanceConfirmed());

		//Set the FBI subscription ID and respnose code
		federalRapbackSubscription.setTransactionCategoryCodeResponse("RBMNTR");
		federalRapbackSubscription.setTransactionStatusText("there was an error in the RBMTR.");
		federalRapbackSubscription.setFbiSubscriptionId("12345");
		
		//Update database
		enhancedAuditDao.updateFederalRapbackSubscriptionWithResponse(federalRapbackSubscription);

		federalRapbackSubscriptionDetail = restTemplate.getForObject(uri, FederalRapbackSubscriptionDetail.class);

		//Confirm subscription was sent and created
		assertEquals(true, federalRapbackSubscriptionDetail.isFbiSubscriptionSent());
		assertEquals(true, federalRapbackSubscriptionDetail.isFbiSubscriptionCreated());
		
		//Confirm maintenance was sent
		assertEquals(true, federalRapbackSubscriptionDetail.isFbiRapbackMaintenanceSent());
		assertEquals(true, federalRapbackSubscriptionDetail.isFbiRapbackMaintenanceConfirmed());
		assertEquals("there was an error in the RBMTR.", federalRapbackSubscriptionDetail.getFbiRapbackMaintenanceErrorText());
		
	
		//Save a federal subscription creation request, but this with an error
		federalRapbackSubscription = saveFederalRapbackSubscription("stateSubError", "", "RBSCRM");
		
		uri = "http://localhost:9898/auditServer/audit/subscriptions/stateSubError/federalRapbackSubscriptions/detail";
		
		federalRapbackSubscriptionDetail = restTemplate.getForObject(uri, FederalRapbackSubscriptionDetail.class);
		
		logger.info(federalRapbackSubscriptionDetail.toString());
		
		//Confirm FBI subscription was sent but not yet created
		assertEquals(true, federalRapbackSubscriptionDetail.isFbiSubscriptionSent());
		assertEquals(false, federalRapbackSubscriptionDetail.isFbiSubscriptionCreated());
		
		//Set the FBI subscription ID and response code
		federalRapbackSubscription.setTransactionStatusText("There was an error creating the subscription.");
		federalRapbackSubscription.setTransactionCategoryCodeResponse("RBSR");
		
		//Update database
		enhancedAuditDao.updateFederalRapbackSubscriptionWithResponse(federalRapbackSubscription);
		
		federalRapbackSubscriptionDetail = restTemplate.getForObject(uri, FederalRapbackSubscriptionDetail.class);
		
		//Confirm subscription was sent but an error returned
		assertEquals(true, federalRapbackSubscriptionDetail.isFbiSubscriptionSent());
		assertEquals(false, federalRapbackSubscriptionDetail.isFbiSubscriptionCreated());
		assertEquals("There was an error creating the subscription.", federalRapbackSubscriptionDetail.getFbiSubscriptionErrorText());
	}
}
