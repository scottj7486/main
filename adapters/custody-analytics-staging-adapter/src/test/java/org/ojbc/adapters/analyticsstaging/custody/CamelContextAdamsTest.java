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
 * Copyright 2012-2015 Open Justice Broker Consortium
 */
package org.ojbc.adapters.analyticsstaging.custody;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Assert;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.headers.Header;
import org.apache.cxf.message.MessageImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ojbc.adapters.analyticsstaging.custody.dao.AnalyticalDatastoreDAO;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.BehavioralHealthAssessment;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.Booking;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.BookingArrest;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.BookingCharge;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.CustodyRelease;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.CustodyStatusChange;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.CustodyStatusChangeArrest;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.CustodyStatusChangeCharge;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.Person;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.PrescribedMedication;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.Treatment;
import org.ojbc.util.camel.helper.OJBUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/camel-context.xml",
        "classpath:META-INF/spring/cxf-endpoints.xml",      
        "classpath:META-INF/spring/properties-context-adams.xml",
        "classpath:META-INF/spring/dao-adams.xml",
        })
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class CamelContextAdamsTest {
	
	private static final Log log = LogFactory.getLog( CamelContextAdamsTest.class );
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
    @Resource
    private ModelCamelContext context;
    
    @Produce
    protected ProducerTemplate template;

	@Autowired
	private AnalyticalDatastoreDAO analyticalDatastoreDAO;
	
	@EndpointInject(uri = "mock:direct:failedInvocation")
	protected MockEndpoint failedInvocationEndpoint;
	
	@Test
	@DirtiesContext
	public void contextStartup() {
		assertTrue(true);
	}

	@Before
	public void setUp() throws Exception {
    	//We replace the 'from' web service endpoint with a direct endpoint we call in our test
    	context.getRouteDefinition("booking_reporting_service").adviceWith(context, new AdviceWithRouteBuilder() {
    	    @Override
    	    public void configure() throws Exception {
    	    	// The line below allows us to bypass CXF and send a message directly into the route
    	    	replaceFromWith("direct:bookingReportServiceEndpoint");
    	    }              
    	});

    	context.getRouteDefinition("booking_reporting_service_process_booking_report").adviceWith(context, new AdviceWithRouteBuilder() {
    	    @Override
    	    public void configure() throws Exception {
    	    	//This assists testing an invocation failure
    	    	interceptSendToEndpoint("direct:failedInvocation").to("mock:direct:failedInvocation").stop();
    	    }              
    	});
    	
    	context.getRouteDefinition("custody_release_reporting_service").adviceWith(context, new AdviceWithRouteBuilder() {
    		@Override
    		public void configure() throws Exception {
    			// The line below allows us to bypass CXF and send a message directly into the route
    			replaceFromWith("direct:custodyReleaseServiceEndpoint");
    		}              
    	});
    	
    	context.getRouteDefinition("custody_release_reporting_service_process_report").adviceWith(context, new AdviceWithRouteBuilder() {
    		@Override
    		public void configure() throws Exception {
    			//This assists testing an invocation failure
    			interceptSendToEndpoint("direct:failedInvocation").to("mock:direct:failedInvocation").stop();
    		}              
    	});
    	
    	context.getRouteDefinition("custody_status_change_reporting_service_process_report").adviceWith(context, new AdviceWithRouteBuilder() {
    		@Override
    		public void configure() throws Exception {
    			// The line below allows us to bypass CXF and send a message directly into the route
    			replaceFromWith("direct:custodyStatusChangeReportingService");
    		}              
    	});
    	
    	context.getRouteDefinition("custody_status_change_reporting_service_process_report").adviceWith(context, new AdviceWithRouteBuilder() {
    		@Override
    		public void configure() throws Exception {
    			// The line below allows us to bypass CXF and send a message directly into the route
    			interceptSendToEndpoint("direct:failedInvocation").to("mock:direct:failedInvocation").stop();
    		}              
    	});
    	
    	context.start();
	}	
	
	@Test
	public void testAllServices() throws Exception
	{
		testBookingReportServiceRoute();	
		testCustodyStatusChangeReportService();
		testCustodyReleaseReportServiceRoute();
		testBookingReportServiceRouteDup();	
	}
	
	public void testCustodyStatusChangeReportService() throws Exception
	{
		Exchange senderExchange = createSenderExchange("src/test/resources/xmlInstances/custodyStatusChangeReport/CustodyStatusChangeReport-Adams.xml");

		Person person = analyticalDatastoreDAO.getPerson(1);
		Assert.assertNotNull(person);
		
		List<CustodyStatusChange> custodyStatusChanges = analyticalDatastoreDAO.getCustodyStatusChangesByBookingId(1);
		assertTrue(custodyStatusChanges.isEmpty());
		
		List<CustodyStatusChangeCharge> custodyStatusChangeCharges = analyticalDatastoreDAO.getCustodyStatusChangeCharges( 1 ); 
		assertTrue(custodyStatusChangeCharges.isEmpty());
		
	    //Send the one-way exchange.  Using template.send will send an one way message
		Exchange returnExchange = template.send("direct:custodyStatusChangeReportingService", senderExchange);
		
		//Use getException to see if we received an exception
		if (returnExchange.getException() != null)
		{	
			throw new Exception(returnExchange.getException());
		}
		
		person = analyticalDatastoreDAO.getPerson(1);
		Assert.assertNotNull(person);

		Person person2 = analyticalDatastoreDAO.getPerson(2);
		Assert.assertNotNull(person2);

		Assert.assertEquals(Integer.valueOf(2), person2.getPersonId());
		assertThat(person2.getDomicileStatusTypeId(), nullValue());
		assertThat(person2.getPersonUniqueIdentifier2(), is("Booking Subject Number"));
		assertThat(person2.getEducationLevel(), is("High School Graduate"));
		assertThat(person2.getOccupation(), is("Truck Driver"));
		assertThat(person2.getMilitaryServiceStatusType().getValue(), is("Active"));
		
		List<BehavioralHealthAssessment> behavioralHealthAssessments = analyticalDatastoreDAO.getBehavioralHealthAssessments(2);
		assertThat(behavioralHealthAssessments.size(), is(1));
		
		BehavioralHealthAssessment behavioralHealthAssessment = behavioralHealthAssessments.get(0);
		
		assertTrue(behavioralHealthAssessment.getBehavioralHealthDiagnoses().size() == 1);
		assertThat(behavioralHealthAssessment.getBehavioralHealthDiagnoses().get(0), is("Schizophrenia 295.10"));
		assertThat(behavioralHealthAssessment.getPersonId(), is(2));
		assertThat(behavioralHealthAssessment.getBehavioralHealthAssessmentId(), is(2));
		assertThat(behavioralHealthAssessment.getSeriousMentalIllness(), is(true));
		assertThat(behavioralHealthAssessment.getCareEpisodeStartDate(), is(LocalDate.parse("2016-01-01")));
		assertThat(behavioralHealthAssessment.getCareEpisodeEndDate(), is(LocalDate.parse("2016-04-01")));
		assertThat(behavioralHealthAssessment.getEnrolledProviderName(), is("79"));
		assertThat(behavioralHealthAssessment.getMedicaidStatusTypeId(), nullValue());

		List<Treatment> treatments = analyticalDatastoreDAO.getTreatments(2);
		assertThat(treatments.size(), is(1));
		
		Treatment treatment = treatments.get(0);
		assertThat(treatment.getBehavioralHealthAssessmentID(), is(2));
		assertThat(treatment.getTreatmentStartDate(), is(LocalDate.parse("2016-01-01"))); 
		assertThat(treatment.getTreatmentAdmissionReasonTypeId(), nullValue());
		assertThat(treatment.getTreatmentStatusTypeId(), nullValue());
		assertThat(treatment.getTreatmentProviderName(), is("Treatment Providing Organization Name"));
		
		
		List<PrescribedMedication> prescribedMedications = analyticalDatastoreDAO.getPrescribedMedication(2);
		assertThat(prescribedMedications.size(), is(1));
		
		PrescribedMedication  prescribedMedication = prescribedMedications.get(0);
		assertThat(prescribedMedication.getBehavioralHealthAssessmentID(), is(2));
		assertThat(prescribedMedication.getMedicationDescription(), is("Zyprexa"));
		assertThat(prescribedMedication.getMedicationDispensingDate(), is(LocalDate.parse("2016-01-01"))); 
		assertThat(prescribedMedication.getMedicationDoseMeasure(), is("3mg"));
		
		custodyStatusChanges = analyticalDatastoreDAO.getCustodyStatusChangesByBookingId(1);
		assertThat(custodyStatusChanges.size(), is(1));

		CustodyStatusChange custodyStatusChange = custodyStatusChanges.get(0);
		assertEquals(LocalDate.parse("2013-12-17"), custodyStatusChange.getBookingDate());
		assertEquals(LocalTime.parse("09:30"), custodyStatusChange.getBookingTime());
		assertThat(custodyStatusChange.getFacilityId(), is(2));
		assertThat(custodyStatusChange.getSupervisionUnitTypeId(), is(10)); 
		assertThat(custodyStatusChange.getBookingId(), is(1));
		assertThat(custodyStatusChange.getBookingNumber(), is("Booking Number"));
		assertThat(custodyStatusChange.getScheduledReleaseDate(), is(LocalDate.parse("2014-12-17")));
		assertThat(custodyStatusChange.getInmateJailResidentIndicator(), is(false));
		
		List<CustodyStatusChangeArrest> custodyStatusChangeArrests = analyticalDatastoreDAO.getCustodyStatusChangeArrests(1);
		assertThat(custodyStatusChangeArrests.size(), is(1));
		CustodyStatusChangeArrest custodyStatusChangeArrest = custodyStatusChangeArrests.get(0);
		
		assertThat(custodyStatusChangeArrest.getCustodyStatusChangeId(), is(1)); 
		assertThat(custodyStatusChangeArrest.getCustodyStatusChangeArrestId(), is(1)); 
		assertEquals("30", custodyStatusChangeArrest.getAddress().getStreetNumber()); 
		assertEquals("Main Street", custodyStatusChangeArrest.getAddress().getStreetName()); 
		assertEquals("Denton", custodyStatusChangeArrest.getAddress().getCity()); 
		assertEquals("CO", custodyStatusChangeArrest.getAddress().getState()); 
		assertEquals("99999", custodyStatusChangeArrest.getAddress().getPostalcode()); 
		assertTrue(custodyStatusChangeArrest.getAddress().getLocationLatitude().doubleValue() == 56.1111 ); 
		assertTrue(custodyStatusChangeArrest.getAddress().getLocationLongitude().doubleValue() == 32.1111 );
		assertThat(custodyStatusChangeArrest.getArrestAgencyId(), is(22));
		
		custodyStatusChangeCharges = analyticalDatastoreDAO.getCustodyStatusChangeCharges( 1 ); 
		assertThat(custodyStatusChangeCharges.size(), is(1));
		
		CustodyStatusChangeCharge custodyStatusChangeCharge = custodyStatusChangeCharges.get(0);
		assertThat(custodyStatusChangeCharge.getChargeCode(), is("Charge Code ID"));
		assertTrue(custodyStatusChangeCharge.getCustodyStatusChangeArrestId() == 1);
		assertTrue(custodyStatusChangeCharge.getBondAmount().doubleValue() == 500.00); 
		assertThat(custodyStatusChangeCharge.getBondType().getValue(), is("PR BOND"));
		assertThat(custodyStatusChangeCharge.getAgencyId(), is(21));
		assertThat(custodyStatusChangeCharge.getChargeClassTypeId(), is(2));
		assertThat(custodyStatusChangeCharge.getBondStatusTypeId(), is(11));
		assertThat(custodyStatusChangeCharge.getChargeJurisdictionTypeId(), is(2));
		assertThat(custodyStatusChangeCharge.getChargeDisposition(), is("Disposition"));
		
	}
	
	public void testBookingReportServiceRoute() throws Exception, IOException {
		Exchange senderExchange = createSenderExchange("src/test/resources/xmlInstances/bookingReport/BookingReport-Adams.xml");

		Person person = analyticalDatastoreDAO.getPerson(1);
		Assert.assertNull(person);
		
		Booking booking = analyticalDatastoreDAO.getBookingByBookingNumber("Booking Number");
		assertNull(booking);
		
		List<BookingCharge> bookingCharges = analyticalDatastoreDAO.getBookingCharges( 1 ); 
		assertTrue(bookingCharges.isEmpty());
		
		List<BookingArrest> bookingArrests = analyticalDatastoreDAO.getBookingArrests( 1 ); 
		assertTrue(bookingArrests.isEmpty());
		
	    //Send the one-way exchange.  Using template.send will send an one way message
		Exchange returnExchange = template.send("direct:bookingReportServiceEndpoint", senderExchange);
		
		//Use getException to see if we received an exception
		if (returnExchange.getException() != null)
		{	
			throw new Exception(returnExchange.getException());
		}
		
		assertThat(jdbcTemplate.queryForObject("select count(*) from Booking", Integer.class), is(1));

		person = analyticalDatastoreDAO.getPerson(1);
		Assert.assertNotNull(person);
		
		Assert.assertEquals(Integer.valueOf(1), person.getPersonId());
		assertThat(person.getPersonSexId(), is(2));
		assertThat(person.getPersonRaceId(), is(6));
		assertThat(person.getPersonEthnicityTypeId(), is(1));
		assertThat(person.getPersonSexDescription(), is("Female"));
		assertThat(person.getPersonRaceDescription(), is("White"));
		assertThat(person.getLanguage(), is("English"));
		assertThat(person.getPersonBirthDate(), is(LocalDate.parse("1968-12-17")));
		assertThat(person.getPersonUniqueIdentifier(), is("e807f1fcf82d132f9bb018ca6738a19f"));
		assertThat(person.getPersonUniqueIdentifier2(), is("Booking Subject Number"));
		assertThat(person.getLanguageId(), is(1));
		assertThat(person.getSexOffenderStatusTypeId(), is(1));
		assertThat(person.getMilitaryServiceStatusType().getValue(), is("Honorable Discharge"));
		
		assertThat(person.getEducationLevel(), is("High School Graduate"));
		assertThat(person.getOccupation(), is("Truck Driver"));
		
		List<BehavioralHealthAssessment> behavioralHealthAssessments = analyticalDatastoreDAO.getBehavioralHealthAssessments(1);
		assertFalse(behavioralHealthAssessments.isEmpty());
		
		BehavioralHealthAssessment behavioralHealthAssessment = behavioralHealthAssessments.get(0);
		
		assertTrue(behavioralHealthAssessment.getBehavioralHealthDiagnoses().size() == 1);
		assertThat(behavioralHealthAssessment.getBehavioralHealthDiagnoses().get(0), is("Schizophrenia 295.10"));
		assertThat(behavioralHealthAssessment.getPersonId(), is(1));
		assertThat(behavioralHealthAssessment.getBehavioralHealthAssessmentId(), is(1));
		assertThat(behavioralHealthAssessment.getSeriousMentalIllness(), is(true));
		assertThat(behavioralHealthAssessment.getCareEpisodeStartDate(), is(LocalDate.parse("2016-01-01")));
		assertThat(behavioralHealthAssessment.getCareEpisodeEndDate(), is(LocalDate.parse("2016-04-01")));
		assertThat(behavioralHealthAssessment.getEnrolledProviderName(), is("79"));
		assertThat(behavioralHealthAssessment.getMedicaidStatusTypeId(), nullValue());

		List<Treatment> treatments = analyticalDatastoreDAO.getTreatments(1);
		assertThat(treatments.size(), is(1));
		
		Treatment treatment = treatments.get(0);
		assertThat(treatment.getBehavioralHealthAssessmentID(), is(1));
		assertThat(treatment.getTreatmentStartDate(), is(LocalDate.parse("2016-01-01"))); 
		assertThat(treatment.getTreatmentAdmissionReasonTypeId(), nullValue());
		assertThat(treatment.getTreatmentStatusTypeId(), nullValue());
		assertThat(treatment.getTreatmentProviderName(), is("Treatment Providing Organization Name"));
		
		
		List<PrescribedMedication> prescribedMedications = analyticalDatastoreDAO.getPrescribedMedication(1);
		assertThat(prescribedMedications.size(), is(1));
		
		PrescribedMedication  prescribedMedication = prescribedMedications.get(0);
		assertThat(prescribedMedication.getBehavioralHealthAssessmentID(), is(1));
		assertThat(prescribedMedication.getMedicationDescription(), is("Zyprexa"));
		assertThat(prescribedMedication.getMedicationDispensingDate(), is(LocalDate.parse("2016-01-01"))); 
		assertThat(prescribedMedication.getMedicationDoseMeasure(), is("3mg"));
		
		booking = analyticalDatastoreDAO.getBookingByBookingNumber("Booking Number");
		assertNotNull(booking);

		assertEquals(LocalDate.parse("2013-12-17"), booking.getBookingDate());
		assertEquals(LocalTime.parse("09:30"), booking.getBookingTime());
		assertThat(booking.getFacilityId(), is(1));
		assertThat(booking.getSupervisionUnitTypeId(), nullValue()); 
		assertEquals("Booking Number", booking.getBookingNumber());
		assertEquals(LocalDate.parse("2014-12-17"), booking.getScheduledReleaseDate());
		assertThat(booking.getInmateJailResidentIndicator(), is(false)); 
		
		bookingArrests = analyticalDatastoreDAO.getBookingArrests(1);
		assertFalse(bookingArrests.isEmpty());
		BookingArrest bookingArrest = bookingArrests.get(0);
		
		assertTrue(bookingArrest.getBookingId() == 1); 
		assertTrue(bookingArrest.getBookingArrestId() == 1); 
		assertEquals("392", bookingArrest.getAddress().getStreetNumber()); 
		assertEquals("Woodlawn Ave", bookingArrest.getAddress().getStreetName()); 
		assertEquals("Burlington", bookingArrest.getAddress().getCity()); 
		assertEquals("NY", bookingArrest.getAddress().getState()); 
		assertEquals("05408", bookingArrest.getAddress().getPostalcode()); 
		assertTrue(bookingArrest.getAddress().getLocationLatitude().doubleValue() == 56.1111 ); 
		assertTrue(bookingArrest.getAddress().getLocationLongitude().doubleValue() == 32.1111 );
		assertThat(bookingArrest.getArrestAgencyId(), is(29));

		bookingCharges = analyticalDatastoreDAO.getBookingCharges( 1 ); 
		assertThat(bookingCharges.size(), is(2));
		
		BookingCharge bookingCharge = bookingCharges.get(0);
		assertThat(bookingCharge.getChargeCode(), is("Charge Code ID"));
		assertTrue(bookingCharge.getBookingArrestId() == 1);
		assertTrue(bookingCharge.getBondAmount().doubleValue() == 500.00); 
		assertThat(bookingCharge.getBondType().getValue(), is("CASH/SURETY/PROPERTY"));
		assertThat(bookingCharge.getAgencyId(), is(21));
		assertThat(bookingCharge.getChargeClassTypeId(), is(1));
		assertThat(bookingCharge.getBondStatusTypeId(), is(17));
		assertThat(bookingCharge.getChargeJurisdictionTypeId(), is(1));
		assertThat(bookingCharge.getChargeDisposition(), is("Disposition"));
		
		CustodyRelease custodyRelease = analyticalDatastoreDAO.getCustodyReleaseByBookingId(1);
		log.info(custodyRelease.toString());
		assertEquals(LocalDate.parse("2014-12-17"), custodyRelease.getReleaseDate());
		assertEquals(LocalTime.parse("10:30"), custodyRelease.getReleaseTime());
		assertThat(custodyRelease.getBookingNumber(), is("Booking Number"));
		
	}
	
	public void testBookingReportServiceRouteDup() throws Exception, IOException {
		Exchange senderExchange = createSenderExchange("src/test/resources/xmlInstances/bookingReport/BookingReport-Adams-dup.xml");
		
		Person person = analyticalDatastoreDAO.getPerson(1);
		Assert.assertNotNull(person);
		
		Booking booking = analyticalDatastoreDAO.getBookingByBookingNumber("Booking Number");
		assertNotNull(booking);
		
		List<BookingCharge> bookingCharges = analyticalDatastoreDAO.getBookingCharges( 1 ); 
		assertFalse(bookingCharges.isEmpty());
		
		List<BookingArrest> bookingArrests = analyticalDatastoreDAO.getBookingArrests( 1 ); 
		assertFalse(bookingArrests.isEmpty());
		
		//Send the one-way exchange.  Using template.send will send an one way message
		Exchange returnExchange = template.send("direct:bookingReportServiceEndpoint", senderExchange);
		
		//Use getException to see if we received an exception
		if (returnExchange.getException() != null)
		{	
			throw new Exception(returnExchange.getException());
		}
		
		assertTrue(jdbcTemplate.queryForObject("select count(*)=1 from Booking", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=2 from BookingArrest", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=2 from BookingCharge", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=1 from Location", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=1 from Person", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=1 from BehavioralHealthAssessment", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=1 from Treatment", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=1 from BehavioralHealthEvaluation", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=0 from BehavioralHealthAssessmentCategory", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=1 from PrescribedMedication", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=1 from CustodyRelease", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=0 from CustodyStatusChange", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=0 from CustodyStatusChangeArrest", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=0 from CustodyStatusChangeCharge", Boolean.class));
		
		booking = analyticalDatastoreDAO.getBookingByBookingNumber("Booking Number");
		assertNotNull(booking);
		
		assertEquals(LocalDate.parse("2013-12-17"), booking.getBookingDate());
		assertEquals(LocalTime.parse("09:30"), booking.getBookingTime());
		assertThat(booking.getFacilityId(), is(1));
		assertThat(booking.getSupervisionUnitTypeId(), nullValue()); 
		assertEquals("Booking Number", booking.getBookingNumber());
		assertEquals(LocalDate.parse("2014-12-17"), booking.getScheduledReleaseDate());
		assertThat(booking.getInmateJailResidentIndicator(), is(false));
		
		person = analyticalDatastoreDAO.getPerson(booking.getPersonId());
		Assert.assertNotNull(person);
		
		assertThat(person.getPersonId(), is(not(1)));
		assertThat(person.getPersonSexId(), is(1));
		assertThat(person.getPersonRaceId(), is(1));
		assertThat(person.getPersonSexDescription(), is("Male"));
		assertThat(person.getPersonRaceDescription(), is("Asian"));
		assertThat(person.getLanguage(), is("English"));
		assertThat(person.getPersonBirthDate(), is(LocalDate.parse("1968-12-17")));
		Assert.assertEquals("e807f1fcf82d132f9bb018ca6738a19f", person.getPersonUniqueIdentifier());
		assertThat(person.getLanguageId(), is(1));
		assertThat(person.getSexOffenderStatusTypeId(), is(1));
		assertThat(person.getMilitaryServiceStatusType().getValue(), is("Honorable Discharge"));
		
		assertThat(person.getEducationLevel(), is("High School Graduate"));
		assertThat(person.getOccupation(), is("Truck Driver"));
		
		List<BehavioralHealthAssessment> behavioralHealthAssessments = analyticalDatastoreDAO.getBehavioralHealthAssessments(booking.getPersonId());
		assertFalse(behavioralHealthAssessments.isEmpty());
		
		BehavioralHealthAssessment behavioralHealthAssessment = behavioralHealthAssessments.get(0);
		
		assertTrue(behavioralHealthAssessment.getBehavioralHealthDiagnoses().size() == 1);
		assertThat(behavioralHealthAssessment.getBehavioralHealthDiagnoses().get(0), is("Schizophrenia 295.10"));
		assertThat(behavioralHealthAssessment.getPersonId(), is(not(1)));
		assertThat(behavioralHealthAssessment.getBehavioralHealthAssessmentId(), is(not(1)));
		assertThat(behavioralHealthAssessment.getSeriousMentalIllness(), is(true));
		assertThat(behavioralHealthAssessment.getCareEpisodeStartDate(), is(LocalDate.parse("2016-01-01")));
		assertThat(behavioralHealthAssessment.getCareEpisodeEndDate(), is(LocalDate.parse("2016-04-01")));
		assertThat(behavioralHealthAssessment.getEnrolledProviderName(), is("79"));
		assertThat(behavioralHealthAssessment.getMedicaidStatusTypeId(), nullValue());
		
		List<Treatment> treatments = analyticalDatastoreDAO.getTreatments(behavioralHealthAssessment.getBehavioralHealthAssessmentId());
		assertThat(treatments.size(), is(1));
		
		Treatment treatment = treatments.get(0);
		assertThat(treatment.getBehavioralHealthAssessmentID(), is(not(1)));
		assertThat(treatment.getTreatmentStartDate(), is(LocalDate.parse("2016-01-01"))); 
		assertThat(treatment.getTreatmentAdmissionReasonTypeId(), nullValue());
		assertThat(treatment.getTreatmentStatusTypeId(), nullValue());
		assertThat(treatment.getTreatmentProviderName(), is("Treatment Providing Organization Name"));
		
		
		List<PrescribedMedication> prescribedMedications = analyticalDatastoreDAO.getPrescribedMedication(behavioralHealthAssessment.getBehavioralHealthAssessmentId());
		assertThat(prescribedMedications.size(), is(1));
		
		PrescribedMedication  prescribedMedication = prescribedMedications.get(0);
		assertThat(prescribedMedication.getBehavioralHealthAssessmentID(), is(not(1)));
		assertThat(prescribedMedication.getMedicationDescription(), is("Zyprexa"));
		assertThat(prescribedMedication.getMedicationDispensingDate(), is(LocalDate.parse("2016-01-01"))); 
		assertThat(prescribedMedication.getMedicationDoseMeasure(), is("3mg"));
		
		bookingArrests = analyticalDatastoreDAO.getBookingArrests(booking.getBookingId());
		assertFalse(bookingArrests.isEmpty());
		BookingArrest bookingArrest = bookingArrests.get(0);
		
		assertThat(bookingArrest.getBookingId(), is(booking.getBookingId())); 
		assertThat(bookingArrest.getBookingArrestId(), is(not(1))); 
		assertEquals("392", bookingArrest.getAddress().getStreetNumber()); 
		assertEquals("Woodlawn Ave", bookingArrest.getAddress().getStreetName()); 
		assertEquals("Burlington", bookingArrest.getAddress().getCity()); 
		assertEquals("NY", bookingArrest.getAddress().getState()); 
		assertEquals("05408", bookingArrest.getAddress().getPostalcode()); 
		assertTrue(bookingArrest.getAddress().getLocationLatitude().doubleValue() == 56.1111 ); 
		assertTrue(bookingArrest.getAddress().getLocationLongitude().doubleValue() == 32.1111 );
		assertThat(bookingArrest.getArrestAgencyId(), is(29));
		
		bookingCharges = analyticalDatastoreDAO.getBookingCharges( booking.getBookingId()); 
		assertThat(bookingCharges.size(), is(2));
		
		BookingCharge bookingCharge = bookingCharges.get(0);
		assertThat(bookingCharge.getChargeCode(), is("Charge Code ID"));
		assertThat(bookingCharge.getBookingArrestId(), is(not(1)));
		assertTrue(bookingCharge.getBondAmount().doubleValue() == 500.00); 
		assertThat(bookingCharge.getBondType().getValue(), is("CASH/SURETY/PROPERTY"));
		assertThat(bookingCharge.getAgencyId(), is(21));
		assertThat(bookingCharge.getChargeClassTypeId(), is(1));
		assertThat(bookingCharge.getBondStatusTypeId(), is(17));
		assertThat(bookingCharge.getChargeJurisdictionTypeId(), is(3));
		assertThat(bookingCharge.getChargeDisposition(), is("Disposition"));
		
		CustodyRelease custodyRelease = analyticalDatastoreDAO.getCustodyReleaseByBookingId(booking.getBookingId());
		log.info(custodyRelease.toString());
		assertEquals(LocalDate.parse("2014-12-17"), custodyRelease.getReleaseDate());
		assertEquals(LocalTime.parse("10:30"), custodyRelease.getReleaseTime());
		
		analyticalDatastoreDAO.deleteBooking(booking.getBookingId());
		
		assertTrue(jdbcTemplate.queryForObject("select count(*)=0 from Booking", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=0 from BookingArrest", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=0 from BookingCharge", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=0 from Location", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=0 from Person", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=0 from BehavioralHealthAssessment", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=0 from Treatment", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=0 from BehavioralHealthEvaluation", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=0 from BehavioralHealthAssessmentCategory", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=0 from PrescribedMedication", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=0 from CustodyRelease", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=0 from CustodyStatusChange", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=0 from CustodyStatusChangeArrest", Boolean.class));
		assertTrue(jdbcTemplate.queryForObject("select count(*)=0 from CustodyStatusChangeCharge", Boolean.class));
	}
	
	public void testCustodyReleaseReportServiceRoute() throws Exception
	{
		CustodyRelease custodyRelease = analyticalDatastoreDAO.getCustodyReleaseByBookingId(1); 
		assertEquals(LocalDate.parse("2014-12-17"), custodyRelease.getReleaseDate());
		assertEquals(LocalTime.parse("10:30"), custodyRelease.getReleaseTime());
		
		Exchange senderExchange = createSenderExchange("src/test/resources/xmlInstances/custodyReleaseReport/CustodyReleaseReport-Adams.xml");
		
		//Send the one-way exchange.  Using template.send will send an one way message
		Exchange returnExchange = template.send("direct:custodyReleaseServiceEndpoint", senderExchange);
		
		//Use getException to see if we received an exception
		if (returnExchange.getException() != null)
		{	
			throw new Exception(returnExchange.getException());
		}	
		
		custodyRelease = analyticalDatastoreDAO.getCustodyReleaseByBookingId(1);
		assertEquals( LocalDate.parse("2001-12-17"), custodyRelease.getReleaseDate());
		assertEquals( LocalTime.parse("09:30:47"), custodyRelease.getReleaseTime());
		assertThat(custodyRelease.getBookingNumber(), is("Booking Number"));
		
		List<BehavioralHealthAssessment> behavioralHealthAssessments = analyticalDatastoreDAO.getBehavioralHealthAssessments(2);
		assertThat(behavioralHealthAssessments.size(), is(2));
		
		BehavioralHealthAssessment behavioralHealthAssessment = behavioralHealthAssessments.get(1);
		
		assertTrue(behavioralHealthAssessment.getBehavioralHealthDiagnoses().size() == 1);
		assertThat(behavioralHealthAssessment.getBehavioralHealthDiagnoses().get(0), is("Schizophrenia 295.10"));
		assertThat(behavioralHealthAssessment.getPersonId(), is(2));
		assertThat(behavioralHealthAssessment.getBehavioralHealthAssessmentId(), is(3));
		assertThat(behavioralHealthAssessment.getSeriousMentalIllness(), is(true));
		assertThat(behavioralHealthAssessment.getCareEpisodeStartDate(), is(LocalDate.parse("2016-01-01")));
		assertThat(behavioralHealthAssessment.getCareEpisodeEndDate(), is(LocalDate.parse("2016-04-01")));
		assertThat(behavioralHealthAssessment.getEnrolledProviderName(), is("79"));
		assertThat(behavioralHealthAssessment.getMedicaidStatusTypeId(), nullValue());

		List<Treatment> treatments = analyticalDatastoreDAO.getTreatments(3);
		assertThat(treatments.size(), is(1));
		
		Treatment treatment = treatments.get(0);
		assertThat(treatment.getBehavioralHealthAssessmentID(), is(3));
		assertThat(treatment.getTreatmentStartDate(), is(LocalDate.parse("2016-01-01"))); 
		assertThat(treatment.getTreatmentAdmissionReasonTypeId(), nullValue());
		assertThat(treatment.getTreatmentStatusTypeId(), nullValue());
		assertThat(treatment.getTreatmentProviderName(), is("Treatment Providing Organization Name"));
		
		
		List<PrescribedMedication> prescribedMedications = analyticalDatastoreDAO.getPrescribedMedication(3);
		assertThat(prescribedMedications.size(), is(1));
		
		PrescribedMedication  prescribedMedication = prescribedMedications.get(0);
		assertThat(prescribedMedication.getBehavioralHealthAssessmentID(), is(3));
		assertThat(prescribedMedication.getMedicationDescription(), is("Zyprexa"));
		assertThat(prescribedMedication.getMedicationDispensingDate(), is(LocalDate.parse("2016-01-01"))); 
		assertThat(prescribedMedication.getMedicationDoseMeasure(), is("3mg"));

	}
	
	protected Exchange createSenderExchange(String inputFilePath) throws Exception, IOException {
		//Create a new exchange
    	Exchange senderExchange = new DefaultExchange(context);

    	//Set the WS-Address Message ID
		Map<String, Object> requestContext = OJBUtils.setWSAddressingMessageID("123456789");
		
		//Set the operation name and operation namespace for the CXF exchange
		senderExchange.getIn().setHeader(Client.REQUEST_CONTEXT , requestContext);
		
		Document doc = createDocument();
		List<SoapHeader> soapHeaders = new ArrayList<SoapHeader>();
		soapHeaders.add(makeSoapHeader(doc, "http://www.w3.org/2005/08/addressing", "MessageID", "12345"));
		senderExchange.getIn().setHeader(Header.HEADER_LIST , soapHeaders);
		
		org.apache.cxf.message.Message message = new MessageImpl();

		senderExchange.getIn().setHeader(CxfConstants.CAMEL_CXF_MESSAGE, message);
    	
	    //Read the firearm search request file from the file system
	    File inputFile = new File(inputFilePath);
	    String inputStr = FileUtils.readFileToString(inputFile);

	    assertNotNull(inputStr);
	    
	    //Set it as the message message body
	    senderExchange.getIn().setBody(inputStr);
		return senderExchange;
	}
	
	private SoapHeader makeSoapHeader(Document doc, String namespace, String localName, String value) {
		Element messageId = doc.createElementNS(namespace, localName);
		messageId.setTextContent(value);
		SoapHeader soapHeader = new SoapHeader(new QName(namespace, localName), messageId);
		return soapHeader;
	}	
	
	public static Document createDocument() throws Exception{

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc = dbf.newDocumentBuilder().newDocument();

		return doc;
	}

}
