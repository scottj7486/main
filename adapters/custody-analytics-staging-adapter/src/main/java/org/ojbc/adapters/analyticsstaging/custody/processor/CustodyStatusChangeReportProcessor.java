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
package org.ojbc.adapters.analyticsstaging.custody.processor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.BookingSubject;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.CodeTable;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.CustodyRelease;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.CustodyStatusChange;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.CustodyStatusChangeArrest;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.CustodyStatusChangeCharge;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.KeyValue;
import org.ojbc.util.xml.XmlUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Component
public class CustodyStatusChangeReportProcessor extends AbstractReportRepositoryProcessor {

	private static final Log log = LogFactory.getLog( CustodyStatusChangeReportProcessor.class );
	
	@Transactional
	public void processReport(Document report) throws Exception
	{
		log.info("Processing custody status change report." );
		XmlUtils.printNode(report);
		
		Integer custodyStatusChangeId = processCustodyStatusChangeReport(report);
		processCustodyStatusChangeArrests(report, custodyStatusChangeId);
		
		log.info("Processed custody status change report successfully.");
		
	}

	private void processCustodyStatusChangeArrests(Document report, Integer custodyStatusChangeId) throws Exception {
		NodeList arrestNodes = XmlUtils.xPathNodeListSearch(report, 
				"/cscr-doc:CustodyStatusChangeReport/cscr-ext:Custody/jxdm51:Arrest[@s30:id = preceding-sibling::jxdm51:Booking/jxdm51:Arrest/@s30:ref]");
		
		for (int i = 0; i < arrestNodes.getLength(); i++) {
			Node arrestNode = arrestNodes.item(i);
			Integer custodyStatusChangeArrestId = processCustodyStatusChangeArrest(arrestNode, custodyStatusChangeId);
			processCustodyStatusChangeCharges(arrestNode, custodyStatusChangeArrestId);
		}
	}
	
	private Integer processCustodyStatusChangeArrest(Node arrestNode, Integer custodyStatusChangeId) throws Exception {
		CustodyStatusChangeArrest custodyStatusChangeArrest = new CustodyStatusChangeArrest();
		custodyStatusChangeArrest.setCustodyStatusChangeId(custodyStatusChangeId);;
		
		custodyStatusChangeArrest.setAddress(getArrestInfo(arrestNode));
		
        Integer custodyStatusChangeArrestId = analyticalDatastoreDAO.saveCustodyStatusChangeArrest(custodyStatusChangeArrest);
		return custodyStatusChangeArrestId;
	}

	
	private void processCustodyStatusChangeCharges(Node arrestNode, Integer custodyStatusChangeArrestId) throws Exception {
		
		NodeList chargeRefNodes = XmlUtils.xPathNodeListSearch(arrestNode, "jxdm51:ArrestCharge/@s30:ref");
		
		List<CustodyStatusChangeCharge> custodyStatusChangeCharges = new ArrayList<CustodyStatusChangeCharge>();
		
		for (int i = 0; i < chargeRefNodes.getLength(); i++) {
			Attr chargeRefNode = (Attr) chargeRefNodes.item(i);
			String chargeRef = chargeRefNode.getValue();
			
			Node chargeNode = XmlUtils.xPathNodeSearch(arrestNode,  
					"parent::cscr-ext:Custody/jxdm51:Charge[@s30:id = '" + chargeRef + "']");

			CustodyStatusChangeCharge custodyStatusChangeCharge = new CustodyStatusChangeCharge();
			custodyStatusChangeCharge.setCustodyStatusChangeArrestId(custodyStatusChangeArrestId);
		
	        String sendingAgency = XmlUtils.xPathStringSearch(chargeNode, "cscr-ext:HoldForAgency/nc30:OrganizationName");
	        custodyStatusChangeCharge.setAgencyId(descriptionCodeLookupService.retrieveCode(CodeTable.Agency,sendingAgency));

			KeyValue chargeType = new KeyValue(); 
			chargeType.setValue( StringUtils.trimToEmpty(XmlUtils.xPathStringSearch(chargeNode, "jxdm51:ChargeCategoryDescriptionText")));
			chargeType.setKey(descriptionCodeLookupService.retrieveCode(CodeTable.ChargeType, chargeType.getValue()));
			custodyStatusChangeCharge.setChargeType(chargeType);
			
			setBondInfo(chargeNode, custodyStatusChangeCharge);

			String nextCourtEventRef = XmlUtils.xPathStringSearch(arrestNode, 
					"following-sibling::jxdm51:ActivityChargeAssociation[jxdm51:Charge/@s30:ref='" + chargeRef + "']/nc30:Activity/@s30:ref");
			
			String nextCourtDateString = XmlUtils.xPathStringSearch(arrestNode, 
					"following-sibling::cyfs31:NextCourtEvent[@s30:id='"+nextCourtEventRef + "']/nc30:ActivityDate/nc30:Date");
			custodyStatusChangeCharge.setNextCourtDate(StringUtils.isNotBlank(nextCourtDateString)? LocalDate.parse(nextCourtDateString):null);
			
			String nextCourtName = XmlUtils.xPathStringSearch(arrestNode, 
					"following-sibling::cyfs31:NextCourtEvent[@s30:id='"+nextCourtEventRef + "']/jxdm51:CourtEventCourt/jxdm51:CourtName");
			custodyStatusChangeCharge.setNextCourtName(nextCourtName);
			
			custodyStatusChangeCharges.add(custodyStatusChangeCharge);
		}
		analyticalDatastoreDAO.saveCustodyStatusChangeCharges(custodyStatusChangeCharges);
	}

	private void setBondInfo(Node chargeNode, CustodyStatusChangeCharge custodyStatusChangeCharge) throws Exception {
		
		String chargeRef = XmlUtils.xPathStringSearch(chargeNode, "@s30:id");
		
		String bondId = XmlUtils.xPathStringSearch(chargeNode, "following-sibling::"
				+ "jxdm51:BailBondChargeAssociation[jxdm51:Charge/@s30:ref='"+ chargeRef + "']/jxdm51:BailBond/@s30:ref");
		
		if (StringUtils.isNotBlank(bondId)){
			Node bondNode = XmlUtils.xPathNodeSearch(chargeNode, 
					"preceding-sibling::jxdm51:BailBond[@s30:id = '"+ bondId +  "']");
			
			String bondType = XmlUtils.xPathStringSearch(bondNode, "nc30:ActivityCategoryText");
			Integer bondTypeId = descriptionCodeLookupService.retrieveCode(CodeTable.BondType, bondType);
			KeyValue keyValue = new KeyValue(bondTypeId, bondType);
			custodyStatusChangeCharge.setBondType(keyValue);
			
			String bondAmount = XmlUtils.xPathStringSearch(bondNode, "jxdm51:BailBondAmount/nc30:Amount");
			if (StringUtils.isNotBlank(bondAmount)){
				custodyStatusChangeCharge.setBondAmount(new BigDecimal(bondAmount));
			}
		}
	}

	@Transactional
	private Integer processCustodyStatusChangeReport(Document report) throws Exception {
		CustodyStatusChange custodyStatusChange = new CustodyStatusChange();
		
		Node personNode = XmlUtils.xPathNodeSearch(report, "/cscr-doc:CustodyStatusChangeReport/cscr-ext:Custody/nc30:Person");
        
        Node custodyNode = XmlUtils.xPathNodeSearch(report, "/cscr-doc:CustodyStatusChangeReport/cscr-ext:Custody");
		String bookingNumber = XmlUtils.xPathStringSearch(custodyNode, "jxdm51:Booking/jxdm51:BookingAgencyRecordIdentification/nc30:IdentificationID");
		custodyStatusChange.setBookingNumber(bookingNumber);
		
        Integer bookingSubjectId = processBookingSubjectAndBehavioralHealthInfo(personNode, bookingNumber);
        custodyStatusChange.setBookingSubjectId(bookingSubjectId);
        
        String courtName = XmlUtils.xPathStringSearch(custodyNode, "nc30:Case/jxdm51:CaseAugmentation/jxdm51:CaseCourt/jxdm51:CourtName");
        Integer courtId = descriptionCodeLookupService.retrieveCode(CodeTable.Jurisdiction, courtName);
        custodyStatusChange.setJurisdictionId(courtId);
        
        String reportDate = XmlUtils.xPathStringSearch(report, "/cscr-doc:CustodyStatusChangeReport/nc30:DocumentCreationDate/nc30:DateTime");
        custodyStatusChange.setReportDate(parseLocalDateTime(reportDate));
        
        String reportId = XmlUtils.xPathStringSearch(report, "/cscr-doc:CustodyStatusChangeReport/nc30:DocumentIdentification/nc30:IdentificationID");
        custodyStatusChange.setReportId(reportId);
        
        String caseStatus = XmlUtils.xPathStringSearch(custodyNode, "jxdm51:Detention/nc30:SupervisionCustodyStatus/nc30:StatusDescriptionText");
        Integer caseStatusId = descriptionCodeLookupService.retrieveCode(CodeTable.CaseStatus, caseStatus);
        custodyStatusChange.setCaseStatusId(caseStatusId);
        
        String bookingDate = XmlUtils.xPathStringSearch(custodyNode, "jxdm51:Booking/nc30:ActivityDate/nc30:DateTime");
        custodyStatusChange.setBookingDate(parseLocalDateTime(bookingDate));

        String commitDate = XmlUtils.xPathStringSearch(custodyNode, "jxdm51:Detention/nc30:ActivityDate/nc30:Date");
        custodyStatusChange.setCommitDate(parseLocalDate(commitDate));
        
        String facility = XmlUtils.xPathStringSearch(custodyNode, "jxdm51:Booking/jxdm51:BookingDetentionFacility/nc30:FacilityIdentification/nc30:IdentificationID");
        Integer facilityId = descriptionCodeLookupService.retrieveCode(CodeTable.Facility, facility);
        custodyStatusChange.setFacilityId(facilityId);
        
        String bedType = XmlUtils.xPathStringSearch(custodyNode, "jxdm51:Detention/jxdm51:SupervisionAugmentation/jxdm51:SupervisionBedIdentification/ac-bkg-codes:BedCategoryCode");
        Integer bedTypeId = descriptionCodeLookupService.retrieveCode(CodeTable.BedType, bedType);
        custodyStatusChange.setBedTypeId(bedTypeId);
        
        CustodyRelease custodyRelease = processCustodyReleaseInfo(custodyStatusChange.getReportDate(), custodyNode,
				bookingNumber);
        
        custodyStatusChange.setCustodyRelease(custodyRelease);
        
        Integer custodyStatusChangeId = analyticalDatastoreDAO.saveCustodyStatusChange(custodyStatusChange);
		return custodyStatusChangeId;
	}

	private Integer processBookingSubjectAndBehavioralHealthInfo(Node personNode, String bookingNumber) throws Exception {
		
		String personUniqueIdentifier = getPersonUniqueIdentifier(personNode, "cscr-ext:PersonPersistentIdentification/nc30:IdentificationID");
		
		BookingSubject bookingSubject = new BookingSubject();
		
		Integer personId = analyticalDatastoreDAO.getPersonIdByUniqueId(personUniqueIdentifier);

		if (personId != null){
			BookingSubject formerBookingSubject = analyticalDatastoreDAO.getBookingSubjectByBookingNumberAndPersonId(bookingNumber, personId); 
			
			if (formerBookingSubject != null){
				bookingSubject.setRecidivistIndicator(formerBookingSubject.getRecidivistIndicator());
			}
			else{
				bookingSubject.setRecidivistIndicator(1);
			}
		}
		else{
			personId = savePerson(personNode, personUniqueIdentifier);
		}
		
		processBehavioralHealthInfo(personNode, personId, "cscr-ext");

		return saveBookingSubject(personNode, bookingSubject, personId, "cscr-ext");
	}

}
