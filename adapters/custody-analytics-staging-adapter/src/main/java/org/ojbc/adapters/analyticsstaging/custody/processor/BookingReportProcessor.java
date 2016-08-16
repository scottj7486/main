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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.Address;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.Booking;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.BookingArrest;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.BookingCharge;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.CodeTable;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.KeyValue;
import org.ojbc.util.xml.XmlUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Component
public class BookingReportProcessor extends AbstractReportRepositoryProcessor {

	private static final Log log = LogFactory.getLog( BookingReportProcessor.class );
	
	@Transactional
	public void processReport(Document report) throws Exception
	{
		log.info("Processing booking report." );
		XmlUtils.printNode(report);
		
		Integer bookingId = processBookingReport(report);
		processBookingArrests(report, bookingId);
		
		log.info("Processed booking report.");
		
	}

	private void processBookingArrests(Document report, Integer bookingId) throws Exception {
		NodeList arrestNodes = XmlUtils.xPathNodeListSearch(report, 
				"/br-doc:BookingReport/jxdm51:Arrest[@s30:id = preceding-sibling::jxdm51:Booking/jxdm51:Arrest/@s30:ref]");
		
		for (int i = 0; i < arrestNodes.getLength(); i++) {
			Node arrestNode = arrestNodes.item(i);
			
			if (arrestNode != null){
				Integer bookingArrestId = processBookingArrest(arrestNode, bookingId);
				processBookingCharges(arrestNode, bookingArrestId);
			}
		}
	}

	private Integer processBookingArrest(Node arrestNode, Integer bookingId) throws Exception {
		BookingArrest bookingArrest = new BookingArrest();
		bookingArrest.setBookingId(bookingId);
		
		Address address = getArrestInfo(arrestNode);

		bookingArrest.setAddress(address);
        Integer bookingArrestId = analyticalDatastoreDAO.saveBookingArrest(bookingArrest);
		return bookingArrestId;
	}

	private void processBookingCharges(Node arrestNode, Integer bookingArrestId) throws Exception {
		NodeList chargeRefNodes = XmlUtils.xPathNodeListSearch(arrestNode, "jxdm51:ArrestCharge/@s30:ref");
		
		List<BookingCharge> bookingCharges = new ArrayList<BookingCharge>();
		for (int i=0; i<chargeRefNodes.getLength(); i++){
			Attr chargeRefNode = (Attr) chargeRefNodes.item(i);
			
			String chargeRef = chargeRefNode.getValue();
			Node chargeNode = XmlUtils.xPathNodeSearch(arrestNode,  
					"parent::br-doc:BookingReport/jxdm51:Charge[@s30:id = '" + chargeRef + "']");
			
			if (chargeNode != null){
				BookingCharge bookingCharge = new BookingCharge();
				bookingCharge.setBookingArrestId(bookingArrestId);
				
		        String sendingAgency = XmlUtils.xPathStringSearch(chargeNode, "br-ext:HoldForAgency/nc30:OrganizationName");
		        bookingCharge.setAgencyId(descriptionCodeLookupService.retrieveCode(CodeTable.AgencyType,sendingAgency));
		        
				KeyValue chargeType = new KeyValue(); 
				chargeType.setValue( XmlUtils.xPathStringSearch(chargeNode, "jxdm51:ChargeCategoryDescriptionText"));
				chargeType.setKey(descriptionCodeLookupService.retrieveCode(CodeTable.ChargeType, chargeType.getValue()));
				bookingCharge.setChargeType(chargeType);
				
				setBondInfo(chargeNode, chargeRef, bookingCharge);
				
				bookingCharges.add(bookingCharge);
			}
		}
		
		analyticalDatastoreDAO.saveBookingCharges(bookingCharges);
	}

	private void setBondInfo(Node chargeNode, String chargeRef, BookingCharge bookingCharge) throws Exception {
		
		String bondId = XmlUtils.xPathStringSearch(chargeNode, "parent::br-doc:BookingReport/"
				+ "jxdm51:BailBondChargeAssociation[jxdm51:Charge/@s30:ref='"+ chargeRef + "']/jxdm51:BailBond/@s30:ref");
		
		if (StringUtils.isNotBlank(bondId)){
			Node bondNode = XmlUtils.xPathNodeSearch(chargeNode, 
					"parent::br-doc:BookingReport/jxdm51:BailBond[@s30:id = '"+ bondId +  "']");
			
			String bondType = XmlUtils.xPathStringSearch(bondNode, "nc30:ActivityCategoryText");
			Integer bondTypeId = descriptionCodeLookupService.retrieveCode(CodeTable.BondType, bondType);
			KeyValue keyValue = new KeyValue(bondTypeId, bondType);
			bookingCharge.setBondType(keyValue);
			
			String bondAmount = XmlUtils.xPathStringSearch(bondNode, "jxdm51:BailBondAmount/nc30:Amount");
			if (StringUtils.isNotBlank(bondAmount)){
				bookingCharge.setBondAmount(new BigDecimal(bondAmount));
			}
			
		}
	}

	@Transactional
	private Integer processBookingReport(Document report) throws Exception {
		Booking booking = new Booking();
		
        Integer personId = processPersonAndBehavioralHealthInfo(report);
        booking.setPersonId(personId);
        
        Node bookingReportNode = XmlUtils.xPathNodeSearch(report, "/br-doc:BookingReport");
        
        String caseStatus = XmlUtils.xPathStringSearch(bookingReportNode, "jxdm51:Detention/nc30:SupervisionCustodyStatus/nc30:StatusDescriptionText");
        Integer caseStatusId = descriptionCodeLookupService.retrieveCode(CodeTable.CaseStatusType, caseStatus);
        booking.setCaseStatusId(caseStatusId);
        
        String bookingDate = XmlUtils.xPathStringSearch(bookingReportNode, "jxdm51:Booking/nc30:ActivityDate/nc30:DateTime");
        booking.setBookingDateTime(LocalDateTime.parse(bookingDate));

        String facility = XmlUtils.xPathStringSearch(bookingReportNode, "jxdm51:Booking/jxdm51:BookingDetentionFacility/nc30:FacilityIdentification/nc30:IdentificationID");
        Integer facilityId = descriptionCodeLookupService.retrieveCode(CodeTable.Facility, facility);
        booking.setFacilityId(facilityId);
        
        //TODO confirm the xPath with Jim.
        String bedType = XmlUtils.xPathStringSearch(bookingReportNode, "jxdm51:Detention/jxdm51:SupervisionAugmentation/jxdm51:SupervisionBedIdentification/ac-bkg-codes:BedCategoryCode");
        Integer bedTypeId = descriptionCodeLookupService.retrieveCode(CodeTable.BedType, bedType);
        booking.setBedTypeId(bedTypeId);
        
		String bookingNumber = XmlUtils.xPathStringSearch(bookingReportNode, "jxdm51:Booking/jxdm51:BookingAgencyRecordIdentification/nc30:IdentificationID");
		booking.setBookingNumber(bookingNumber);
		
		String supervisionReleaseEligibilityDate = XmlUtils.xPathStringSearch(bookingReportNode, 
        		"jxdm51:Detention/jxdm51:SupervisionAugmentation/jxdm51:SupervisionReleaseEligibilityDate/nc30:Date");
        booking.setScheduledReleaseDate(parseLocalDate(supervisionReleaseEligibilityDate));

        Integer bookingId = analyticalDatastoreDAO.saveBooking(booking);
        
        processCustodyReleaseInfo(bookingReportNode, bookingId);
        
		return bookingId;
	}

	private Integer processPersonAndBehavioralHealthInfo(Document report) throws Exception {
		
		Node personNode = XmlUtils.xPathNodeSearch(report, 
				"/br-doc:BookingReport/nc30:Person[@s30:id=/br-doc:BookingReport/jxdm51:Booking/jxdm51:BookingSubject/nc30:RoleOfPerson/@s30:ref]");
        
		String personUniqueIdentifier = getPersonUniqueIdentifier(personNode, "br-ext:PersonPersistentIdentification/nc30:IdentificationID");
		
		Integer personId = savePerson(personNode, personUniqueIdentifier, "br-ext");
		
		processBehavioralHealthInfo(personNode, personId, "br-ext");
		
		return personId;
	}

}
