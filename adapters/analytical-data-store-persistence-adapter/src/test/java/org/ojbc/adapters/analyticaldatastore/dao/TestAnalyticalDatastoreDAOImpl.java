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
package org.ojbc.adapters.analyticaldatastore.dao;

import static org.junit.Assert.*;

import java.util.Date;

import javax.annotation.Resource;
import javax.sql.DataSource;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ojbc.adapters.analyticaldatastore.dao.model.Agency;
import org.ojbc.adapters.analyticaldatastore.dao.model.County;
import org.ojbc.adapters.analyticaldatastore.dao.model.Incident;
import org.ojbc.adapters.analyticaldatastore.dao.model.IncidentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
		"classpath:META-INF/spring/dao.xml",
		"classpath:META-INF/spring/properties-context.xml"
		})
@DirtiesContext
public class TestAnalyticalDatastoreDAOImpl {

	private static final Log log = LogFactory.getLog(TestAnalyticalDatastoreDAOImpl.class);
	
    @Resource  
    private DataSource dataSource;  
	
	@Autowired
	private AnalyticalDatastoreDAOImpl analyticalDatastoreDAOImpl;
	
	@Before
	public void setUp() throws Exception {
		Assert.assertNotNull(analyticalDatastoreDAOImpl);
		Assert.assertNotNull(dataSource);
	}
		
	@Test
	public void testSaveIncident() throws Exception
	{
		Agency agency = new Agency();
		agency.setAgencyName("Some PD");
		
		int agencyPk = analyticalDatastoreDAOImpl.saveAgency(agency );
		assertEquals(1, agencyPk);
		
		log.debug("Agency primary key: " + agencyPk);

		IncidentType incidentType = new IncidentType();
		incidentType.setIncidentTypeDescription("Incident Type Description");
		
		int incidentTypePk = analyticalDatastoreDAOImpl.saveIncidentType(incidentType);
		assertEquals(1, incidentTypePk);
		
		log.debug("Incident Type primary key: " + incidentTypePk);

		County county = new County();
		county.setCountyName("County Name");
		
		int countyTypePk = analyticalDatastoreDAOImpl.saveCounty(county);
		assertEquals(1, countyTypePk);
		
		log.debug("County Type primary key: " + countyTypePk);
		
		Incident incident = new Incident();
		
		incident.setIncidentTypeID(incidentTypePk);
		incident.setReportingAgencyID(agencyPk);
		incident.setCountyID(countyTypePk);
		incident.setIncidentCaseNumber("999999");
		incident.setRecordType('N');
		incident.setIncidentLocationStreetAddress("Street address");
		incident.setIncidentLocationTown("Town");
		
		incident.setIncidentDate(new Date());
		incident.setIncidentTime(new java.sql.Time(incident.getIncidentDate().getTime()));
		
		int incidentPk = analyticalDatastoreDAOImpl.saveIncident(incident);
		assertEquals(1, incidentPk);

		
	}

}
