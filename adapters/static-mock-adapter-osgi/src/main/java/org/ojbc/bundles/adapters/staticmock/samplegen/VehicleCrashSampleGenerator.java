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
package org.ojbc.bundles.adapters.staticmock.samplegen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.saxon.dom.DocumentBuilderFactoryImpl;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.xml.security.configuration.InOutAttrType;
import org.joda.time.DateTime;
import org.ojbc.util.xml.OjbcNamespaceContext;
import org.ojbc.util.xml.XmlUtils;
import org.opensaml.common.impl.RandomIdentifierGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class VehicleCrashSampleGenerator extends AbstractSampleGenerator{
	
	private static final Random RANDOM = new Random();
	
	private static final String CURRENT_DATE = DateTime.now().toString("yyyy-MM-dd");
	
	
	public VehicleCrashSampleGenerator() throws ParserConfigurationException,
			IOException {
		
		super();
	}

	
	public List<Document> generateVehicleCrashDetailSamples(int recordCount) throws Exception {
	
		List<Document> rVehicleCrashDocList = new ArrayList<Document>();
						
		for(int i=0; i < recordCount; i++){
			
			PersonElementWrapper iGeneratedPerson = getRandomIdentity(null);
			
			String recordId = String.valueOf(i);
			
			Document vehicleCrashDetailDoc = buildVehicleCrashDetailDoc(iGeneratedPerson, recordId);
						
			rVehicleCrashDocList.add(vehicleCrashDetailDoc);
		}
		
		return rVehicleCrashDocList;
	}


	private Document buildVehicleCrashDetailDoc(
			PersonElementWrapper iGeneratedPerson, String recordId) throws ParserConfigurationException, IOException {

		PersonElementWrapper randomPerson2 = getRandomIdentity(null);
		
		Document rVehicleCrashDetailDoc = getNewDocument();
				
		Element rootVehicCrashElement = rVehicleCrashDetailDoc.createElementNS(OjbcNamespaceContext.NS_VEHICLE_CRASH_QUERY_RESULT_EXCH_DOC, "VehicleCrashQueryResults");		
		rootVehicCrashElement.setPrefix(OjbcNamespaceContext.NS_PREFIX_VEHICLE_CRASH_QUERY_RESULT_EXCH_DOC);
				
		rVehicleCrashDetailDoc.appendChild(rootVehicCrashElement);		
						
		Element vehicleCrashReportElement = XmlUtils.appendElement(rootVehicCrashElement, OjbcNamespaceContext.NS_VEHICLE_CRASH_QUERY_RESULT_EXT, "VehicleCrashReport");
						
		Element docCreateDateElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_NC_30, "DocumentCreationDate");
		
		Element docCreateDateValElement = XmlUtils.appendElement(docCreateDateElement, OjbcNamespaceContext.NS_NC_30, "Date");
		
		docCreateDateValElement.setTextContent(CURRENT_DATE);
		
		
		Element docIdElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_NC_30, "DocumentIdentification");
						
		Element docIdValElement = XmlUtils.appendElement(docIdElement, OjbcNamespaceContext.NS_NC_30, "IdentificationID");		
		
		String sampleDocId = RandomStringUtils.randomNumeric(9);		
		docIdValElement.setTextContent(sampleDocId);				
		
		Element docApprovedDateElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_VEHICLE_CRASH_QUERY_RESULT_EXT, "DocumentApprovedDate");
		
		Element docApprovedDateValElement = XmlUtils.appendElement(docApprovedDateElement, OjbcNamespaceContext.NS_NC_30, "Date");		
		docApprovedDateValElement.setTextContent(CURRENT_DATE);
		
		
		Element crashElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_JXDM_51, "Crash");
		
		XmlUtils.addAttribute(crashElement, OjbcNamespaceContext.NS_STRUCTURES_30, "id", "Crash_" + recordId);
		
		
		Element activityDateElement = XmlUtils.appendElement(crashElement, OjbcNamespaceContext.NS_NC_30, "ActivityDate");
		
		Element dateTimeElement = XmlUtils.appendElement(activityDateElement, OjbcNamespaceContext.NS_NC_30, "DateTime");		
		dateTimeElement.setTextContent(CURRENT_DATE);
		
		
		Element activityAugmentElement = XmlUtils.appendElement(crashElement, OjbcNamespaceContext.NS_JXDM_51, "ActivityAugmentation");
		
		
		Element narrativeElement = XmlUtils.appendElement(activityAugmentElement, OjbcNamespaceContext.NS_JXDM_51, "Narrative");
		
		Element narrativeCommentTxtElement = XmlUtils.appendElement(narrativeElement, OjbcNamespaceContext.NS_NC_30, "CommentText");
		
		
		List<String> sampleNarrativeList = Arrays.asList("Pileup", "Rush Hour", "Car hit a truck");
		
		String sampleNarrative = (String)generateRandomValueFromList(sampleNarrativeList);
				
		narrativeCommentTxtElement.setTextContent(sampleNarrative);
		
		
		Element incidentAugmentElement = XmlUtils.appendElement(crashElement, OjbcNamespaceContext.NS_JXDM_51, "IncidentAugmentation");
		
		Element incidentDamagedItemElement = XmlUtils.appendElement(incidentAugmentElement, OjbcNamespaceContext.NS_JXDM_51, "IncidentDamagedItem");
		
		Element itemDescTxtElement = XmlUtils.appendElement(incidentDamagedItemElement, OjbcNamespaceContext.NS_NC_30, "ItemDescriptionText");
		
		
		List<String> sampleDamagedItemList = Arrays.asList("Ferrari", "School Bus", "Hummer", "Lamborghini", "Camaro");
		
		String sampleDamagedItem = (String)generateRandomValueFromList(sampleDamagedItemList);
		
		itemDescTxtElement.setTextContent(sampleDamagedItem);
		
		
		Element itemOwnerElement = XmlUtils.appendElement(incidentDamagedItemElement, OjbcNamespaceContext.NS_NC_30, "ItemOwner");
		
		Element itemOwnerEntOrgElement = XmlUtils.appendElement(itemOwnerElement, OjbcNamespaceContext.NS_NC_30, "EntityOrganization");
		
		XmlUtils.addAttribute(itemOwnerEntOrgElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Property_Owner_" + "TODO");
		
		
		Element entityPersonElement = XmlUtils.appendElement(itemOwnerEntOrgElement, OjbcNamespaceContext.NS_NC_30, "EntityPerson");
		
		XmlUtils.addAttribute(entityPersonElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Property_Owner_" + "TODO");
		
		Element crashCodesElement = XmlUtils.appendElement(incidentDamagedItemElement, OjbcNamespaceContext.NS_ME_VEHICLE_CRASH_CODES, "PropertyCategoryCode");	
		
		List<String> sampleCrashCodeList = Arrays.asList("SB", "CA", "FER", "LMB");
		
		String sampleCrashCode = (String)generateRandomValueFromList(sampleCrashCodeList);
				
		crashCodesElement.setTextContent(sampleCrashCode);
		
		Element incidentReportingOfficialElement = XmlUtils.appendElement(incidentAugmentElement, OjbcNamespaceContext.NS_JXDM_51, "IncidentReportingOfficial");
		
		Element roleOfPersonElement = XmlUtils.appendElement(incidentReportingOfficialElement, OjbcNamespaceContext.NS_NC_30, "RoleOfPerson");
		
		XmlUtils.addAttribute(roleOfPersonElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Reporting_Official_" + recordId);
		
		
		Element badgeIdElement = XmlUtils.appendElement(incidentReportingOfficialElement, OjbcNamespaceContext.NS_JXDM_51, "EnforcementOfficialBadgeIdentification");
				
		Element badgeIdValElement = XmlUtils.appendElement(badgeIdElement, OjbcNamespaceContext.NS_NC_30, "IdentificationID");
		
		String sampleBadgeId = RandomStringUtils.randomNumeric(8);
		
		badgeIdValElement.setTextContent(sampleBadgeId);
		
		Element incidentWitnessElement = XmlUtils.appendElement(incidentAugmentElement, OjbcNamespaceContext.NS_JXDM_51, "IncidentWitness");
		
		Element witnessRolePersonElement = XmlUtils.appendElement(incidentWitnessElement, OjbcNamespaceContext.NS_NC_30, "RoleOfPerson");
		
		XmlUtils.addAttribute(witnessRolePersonElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Witness_" + recordId);
		
		
		Element crashInfoSourceElement = XmlUtils.appendElement(crashElement, OjbcNamespaceContext.NS_JXDM_51, "CrashInformationSource");
		
		Element orgORIIdElement = XmlUtils.appendElement(crashInfoSourceElement, OjbcNamespaceContext.NS_JXDM_51, "OrganizationORIIdentification");
		
		Element orgOriIdValElement = XmlUtils.appendElement(orgORIIdElement, OjbcNamespaceContext.NS_NC_30, "IdentificationID");		
		
		String sampleOriId = RandomStringUtils.randomNumeric(8);
		
		orgOriIdValElement.setTextContent(sampleOriId);
		
		
		Element crashWorkZoneElement = XmlUtils.appendElement(crashElement, OjbcNamespaceContext.NS_JXDM_51, "CrashWorkZone");
		
		Element workersPresentElement = XmlUtils.appendElement(crashWorkZoneElement, OjbcNamespaceContext.NS_JXDM_51, "WorkZoneWorkersPresenceIndicationCode");
		
				
		workersPresentElement.setTextContent(randomBooleanString());		
		
		
		Element copsAtWorkZoneCodeElement = XmlUtils.appendElement(crashWorkZoneElement, OjbcNamespaceContext.NS_ME_VEHICLE_CRASH_CODES, "LawEnforcementPresentAtWorkZoneCode");
		
		copsAtWorkZoneCodeElement.setTextContent(randomBooleanString());
				
		Element nearWorkZoneElement = XmlUtils.appendElement(crashWorkZoneElement, OjbcNamespaceContext.NS_VEHICLE_CRASH_QUERY_RESULT_EXT, "NearWorkZoneIndicator");
		
		nearWorkZoneElement.setTextContent(randomBooleanString());
				

		Element workZoneWorkersPresentIndicator = XmlUtils.appendElement(crashWorkZoneElement, OjbcNamespaceContext.NS_VEHICLE_CRASH_QUERY_RESULT_EXT, "WorkZoneWorkersPresentIndicator");
		
		workZoneWorkersPresentIndicator.setTextContent(randomBooleanString());				
		
		
		Element crashVehicleElement = XmlUtils.appendElement(crashElement, OjbcNamespaceContext.NS_JXDM_51, "CrashVehicle");
		
		Element roleOfItemElement = XmlUtils.appendElement(crashVehicleElement, OjbcNamespaceContext.NS_NC_30, "RoleOfItem");
		
		XmlUtils.addAttribute(roleOfItemElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Vehicle_" + recordId);
		
		
		Element trafficDeviceWorksElement = XmlUtils.appendElement(crashVehicleElement, OjbcNamespaceContext.NS_JXDM_51, "VehicleTrafficControlDeviceOperationalIndicator");
				
		trafficDeviceWorksElement.setTextContent(randomBooleanString());
		
		Element crashVehicleSpeedElement = XmlUtils.appendElement(crashVehicleElement, OjbcNamespaceContext.NS_JXDM_51, "CrashVehicleLegalSpeedRateMeasure");
		
		Element vehicleSpeedValElement = XmlUtils.appendElement(crashVehicleSpeedElement, OjbcNamespaceContext.NS_NC_30, "MeasureValueText");
		
		String sampleSpeed = RandomStringUtils.randomNumeric(3);
		
		vehicleSpeedValElement.setTextContent(sampleSpeed);
		
		Element crashDriverElement = XmlUtils.appendElement(crashVehicleElement, OjbcNamespaceContext.NS_JXDM_51, "CrashDriver");
		
		Element crashDriverRoleOfPersonElement = XmlUtils.appendElement(crashDriverElement, OjbcNamespaceContext.NS_NC_30, "RoleOfPerson");		
		
		XmlUtils.addAttribute(crashDriverRoleOfPersonElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Driver_" + recordId);
		
		Element driverLicenseElement = XmlUtils.appendElement(crashDriverElement, OjbcNamespaceContext.NS_JXDM_51, "DriverLicense");
		
		XmlUtils.addAttribute(driverLicenseElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Driver_License_" + recordId);
		
		Element driverCatCodeElement = XmlUtils.appendElement(crashDriverElement, OjbcNamespaceContext.NS_ME_VEHICLE_CRASH_CODES, "DriverCategoryCode");	
		
		String sampleDriverCatCode = RandomStringUtils.randomAlphabetic(2);
		
		driverCatCodeElement.setTextContent(sampleDriverCatCode);
		
		Element alcoholTestCatCodeElement = XmlUtils.appendElement(crashDriverElement, OjbcNamespaceContext.NS_ME_VEHICLE_CRASH_CODES, "AlcoholTestCategoryCode");
		
		String sampleAlcoholCode = RandomStringUtils.randomAlphabetic(2);
		
		alcoholTestCatCodeElement.setTextContent(sampleAlcoholCode);
		
		Element drugTestCatCodeElement = XmlUtils.appendElement(crashDriverElement, OjbcNamespaceContext.NS_ME_VEHICLE_CRASH_CODES, "DrugTestCategoryCode");
		
		String sampleDrugTestCode = RandomStringUtils.randomNumeric(1);
		
		drugTestCatCodeElement.setTextContent(sampleDrugTestCode);
		
		Element drugTestResultCodeElement = XmlUtils.appendElement(crashDriverElement, OjbcNamespaceContext.NS_ME_VEHICLE_CRASH_CODES, "DrugTestResultCode");
				
		drugTestResultCodeElement.setTextContent(randomBooleanString());
		
		Element alcoholTestResultsPendingElement = XmlUtils.appendElement(crashDriverElement, OjbcNamespaceContext.NS_VEHICLE_CRASH_QUERY_RESULT_EXT, "AlcoholTestResultsPendingtIndicator");
		
		alcoholTestResultsPendingElement.setTextContent(randomBooleanString());
		
		Element crashVehicleOccupantElement = XmlUtils.appendElement(crashVehicleElement, OjbcNamespaceContext.NS_JXDM_51, "CrashVehicleOccupant"); 
		
		Element driverRoleOfPersonElement = XmlUtils.appendElement(crashVehicleOccupantElement, OjbcNamespaceContext.NS_NC_30, "RoleOfPerson");
		XmlUtils.addAttribute(driverRoleOfPersonElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Driver_" + recordId);
		
		Element occupantRoleOfPersonElement = XmlUtils.appendElement(crashVehicleOccupantElement, OjbcNamespaceContext.NS_NC_30, "RoleOfPerson");
		XmlUtils.addAttribute(occupantRoleOfPersonElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Occupant_" + recordId);
		
		Element vehicleOwnerRoleOfPersonElement = XmlUtils.appendElement(crashVehicleOccupantElement, OjbcNamespaceContext.NS_NC_30, "RoleOfPerson");
		XmlUtils.addAttribute(vehicleOwnerRoleOfPersonElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Vehicle_Owner_" + recordId);
		
		Element hazmatElement = XmlUtils.appendElement(crashVehicleElement, OjbcNamespaceContext.NS_VEHICLE_CRASH_QUERY_RESULT_EXT, "HazmatPlacardIndicator");
		hazmatElement.setTextContent(randomBooleanString());
		
		Element carDirectionElement = XmlUtils.appendElement(crashVehicleElement, OjbcNamespaceContext.NS_ME_VEHICLE_CRASH_CODES, "VehicleTravelDirectionCode");
		
		List<String> sampleDirectionList = Arrays.asList("North", "South", "East", "West");
		
		String sampleDirection = (String)generateRandomValueFromList(sampleDirectionList);
		
		carDirectionElement.setTextContent(sampleDirection);
				
		Element carWeightElement = XmlUtils.appendElement(crashVehicleElement, OjbcNamespaceContext.NS_ME_VEHICLE_CRASH_CODES, "GrossVehicleWeightRatingCode");
		
		String sampleWeight = RandomStringUtils.randomNumeric(4);
		
		carWeightElement.setTextContent(sampleWeight);
		
		Element damageElement = XmlUtils.appendElement(crashVehicleElement, OjbcNamespaceContext.NS_ME_VEHICLE_CRASH_CODES, "ExtentOfDamageCode");
		
		List<String> sampleDamageList = Arrays.asList("Very Bad", "Really Bad", "Light", "Totaled", "$500.75", "$6800.99");
		
		String sampleDamage = (String)generateRandomValueFromList(sampleDamageList);
		
		damageElement.setTextContent(sampleDamage);
		
		Element speedLimitElement = XmlUtils.appendElement(crashVehicleElement, OjbcNamespaceContext.NS_ME_VEHICLE_CRASH_CODES, "PostedSpeedLimitCode");
				
		String sampleSpeedLimit = RandomStringUtils.randomNumeric(2);
		
		speedLimitElement.setTextContent(sampleSpeedLimit);
		
		
		Element schoolBusElement = XmlUtils.appendElement(crashVehicleElement, OjbcNamespaceContext.NS_ME_VEHICLE_CRASH_CODES, "SchoolBusRelatedCode");
		schoolBusElement.setTextContent(randomBooleanString());
		
		Element damageOverThreshElement = XmlUtils.appendElement(crashVehicleElement, OjbcNamespaceContext.NS_VEHICLE_CRASH_QUERY_RESULT_EXT, "TotalDamageOverThresholdtIndicator");
						
		damageOverThreshElement.setTextContent(randomBooleanString());
		
		Element nineSeatsElement = XmlUtils.appendElement(crashVehicleElement, OjbcNamespaceContext.NS_VEHICLE_CRASH_QUERY_RESULT_EXT, "VehicleNineOrMoreSeatstIndicator");
		
		nineSeatsElement.setTextContent(randomBooleanString());
		
		Element exemptVehicleElement = XmlUtils.appendElement(crashVehicleElement, OjbcNamespaceContext.NS_VEHICLE_CRASH_QUERY_RESULT_EXT, "ExemptVehicleIndicator");
		
		exemptVehicleElement.setTextContent(randomBooleanString());				
		
		
		Element locationElement = XmlUtils.appendElement(crashElement, OjbcNamespaceContext.NS_NC_30, "Location");
		XmlUtils.addAttribute(locationElement, OjbcNamespaceContext.NS_STRUCTURES_30, "id", "Incident_Loc_" + recordId);
				
		Element addressElement = XmlUtils.appendElement(locationElement, OjbcNamespaceContext.NS_NC_30, "Address");		
		Element cityElement = XmlUtils.appendElement(addressElement, OjbcNamespaceContext.NS_NC_30, "LocationCityName");
		
		String sampleCity = iGeneratedPerson.city;
		
		cityElement.setTextContent(sampleCity);
		
		Element hwyElement = XmlUtils.appendElement(locationElement, OjbcNamespaceContext.NS_NC_30, "AddressHighway");		
		Element hwyFullTxtElement = XmlUtils.appendElement(hwyElement, OjbcNamespaceContext.NS_NC_30, "HighwayFullText");
		
		hwyFullTxtElement.setTextContent(iGeneratedPerson.streetAddress);
		
		Element loc2DElement = XmlUtils.appendElement(locationElement, OjbcNamespaceContext.NS_NC_30, "Location2DGeospatialCoordinate");
		
		Element latElement = XmlUtils.appendElement(loc2DElement, OjbcNamespaceContext.NS_NC_30, "GeographicCoordinateLatitude");
		
		Element latValElement = XmlUtils.appendElement(latElement, OjbcNamespaceContext.NS_NC_30, "LatitudeDegreeValue");	
		
		String sampleLatitude =RandomStringUtils.randomNumeric(2);
		
		latValElement.setTextContent(sampleLatitude);
		
		Element longitudeElement = XmlUtils.appendElement(loc2DElement, OjbcNamespaceContext.NS_NC_30, "GeographicCoordinateLongitude");
		
		Element longValElement = XmlUtils.appendElement(longitudeElement, OjbcNamespaceContext.NS_NC_30, "LongitudeDegreeValue");	
		
		String sampleLongitude = RandomStringUtils.randomNumeric(2);
		
		longValElement.setTextContent(sampleLongitude);		
		
		Element intersectionElement = XmlUtils.appendElement(locationElement, OjbcNamespaceContext.NS_VEHICLE_CRASH_QUERY_RESULT_EXT, "NearestIntersectingStreetFullText");
		
		String sampleIntersection = randomPerson2.streetAddress;
		
		intersectionElement.setTextContent(sampleIntersection);
		
		Element distanceToIntersectionElement = XmlUtils.appendElement(locationElement, OjbcNamespaceContext.NS_VEHICLE_CRASH_QUERY_RESULT_EXT, 
				"DistanceFromNearestIntersectionNumberText");
		
		String sampleDistance = RandomStringUtils.randomNumeric(2);
		
		distanceToIntersectionElement.setTextContent(sampleDistance);
		
		Element directionFromIntersectionElement = XmlUtils.appendElement(locationElement, OjbcNamespaceContext.NS_ME_VEHICLE_CRASH_CODES, "DirectionFromNearestIntersectionCode");
		
		String sampleIntersectionDirection = (String)generateRandomValueFromList(sampleDirectionList);
				
		directionFromIntersectionElement.setTextContent(sampleIntersectionDirection);
		
		Element intersectDistUnitsElement = XmlUtils.appendElement(locationElement, OjbcNamespaceContext.NS_ME_VEHICLE_CRASH_CODES, "DistanceFromNearestIntersectionNumberUnitCode");
		
		
		List<String> sampleUnitsForDistanceList = Arrays.asList("km", "mi", "yd", "me", "cm");
		
		String sampleDistanceUnit = (String)generateRandomValueFromList(sampleUnitsForDistanceList);
		
		intersectDistUnitsElement.setTextContent(sampleDistanceUnit);
		
		
		Element atSceneDateElement = XmlUtils.appendElement(crashElement, OjbcNamespaceContext.NS_VEHICLE_CRASH_QUERY_RESULT_EXT, "AtSceneDateTime");
		
		Element atSceneDateValElement = XmlUtils.appendElement(atSceneDateElement, OjbcNamespaceContext.NS_NC_30, "DateTime");
				
		atSceneDateValElement.setTextContent(CURRENT_DATE);
				
		Element offRoadElement = XmlUtils.appendElement(crashElement, OjbcNamespaceContext.NS_VEHICLE_CRASH_QUERY_RESULT_EXT, "OffRoadIncidentIndicator");
		
		offRoadElement.setTextContent(randomBooleanString());
		
		Element emergVehicle = XmlUtils.appendElement(crashElement, OjbcNamespaceContext.NS_VEHICLE_CRASH_QUERY_RESULT_EXT, "EmergencyVehicleRespondingToSceneIndicator");
		
		emergVehicle.setTextContent(randomBooleanString());
		
		
		Element citationElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_JXDM_51, "Citation");		
		XmlUtils.addAttribute(citationElement, OjbcNamespaceContext.NS_STRUCTURES_30, "id", "Citation_" + recordId);
				
		Element activityIdElement = XmlUtils.appendElement(citationElement, OjbcNamespaceContext.NS_NC_30, "ActivityIdentification");
		
		Element activityIdValElement = XmlUtils.appendElement(activityIdElement, OjbcNamespaceContext.NS_NC_30, "IdentificationID");	
				
		String sampleCitationId = RandomStringUtils.randomNumeric(8);
		
		activityIdValElement.setTextContent(sampleCitationId);
				
		Element citationNumPendingElement = XmlUtils.appendElement(citationElement, OjbcNamespaceContext.NS_VEHICLE_CRASH_QUERY_RESULT_EXT, "CitationNumberPendingIndicator");
		
		citationNumPendingElement.setTextContent(randomBooleanString());
		
		Element firstVilationDescTxtElement = XmlUtils.appendElement(citationElement, OjbcNamespaceContext.NS_VEHICLE_CRASH_QUERY_RESULT_EXT, "FirstViolationDescriptionText");
		
		List<String> sampleCitationList = Arrays.asList("Speeding", "J Walking", "No Seatbelt", "Loud Music", "Ran Stop Sign", "Reckless Driving");
		
		String sampleCitation = (String)generateRandomValueFromList(sampleCitationList);
		
		firstVilationDescTxtElement.setTextContent(sampleCitation);
		
		Element secondViolationElement = XmlUtils.appendElement(citationElement, OjbcNamespaceContext.NS_VEHICLE_CRASH_QUERY_RESULT_EXT, "SecondViolationDescriptionText");
		
		String sampleCitation2 = (String)generateRandomValueFromList(sampleCitationList);
		
		secondViolationElement.setTextContent(sampleCitation2);
		
		
		PersonElementWrapper reportOfficalRandomPerson = getRandomIdentity(null);
		
		Element reportOfficialElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_NC_30, "Person");		
		XmlUtils.addAttribute(reportOfficialElement, OjbcNamespaceContext.NS_STRUCTURES_30, "id", "Reporting_Official_" + recordId);
		
		Element reportOfficialNameElement = XmlUtils.appendElement(reportOfficialElement, OjbcNamespaceContext.NS_NC_30, "PersonName");
		
		Element reportOfficialFullName = XmlUtils.appendElement(reportOfficialNameElement, OjbcNamespaceContext.NS_NC_30, "PersonFullName");	
		
		String sampleReportOfficialFullName = reportOfficalRandomPerson.firstName + " " + reportOfficalRandomPerson.lastName;
		
		reportOfficialFullName.setTextContent(sampleReportOfficialFullName);
				
		Element witnessPersonElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_NC_30, "Person");
		XmlUtils.addAttribute(witnessPersonElement, OjbcNamespaceContext.NS_STRUCTURES_30, "id", "Witness_" + recordId);
		
		Element witnessPersonNameElement = XmlUtils.appendElement(witnessPersonElement, OjbcNamespaceContext.NS_NC_30, "PersonName");
		
		Element witnessGivenName = XmlUtils.appendElement(witnessPersonNameElement, OjbcNamespaceContext.NS_NC_30, "PersonGivenName");
		
		witnessGivenName.setTextContent(reportOfficalRandomPerson.firstName);
		
		Element witnessMiddleName = XmlUtils.appendElement(witnessPersonNameElement, OjbcNamespaceContext.NS_NC_30, "PersonMiddleName");
		
		witnessMiddleName.setTextContent(reportOfficalRandomPerson.middleName);
		
		Element witnessSurName = XmlUtils.appendElement(witnessPersonNameElement, OjbcNamespaceContext.NS_NC_30, "PersonSurName");
		
		witnessSurName.setTextContent(reportOfficalRandomPerson.lastName);
		
				
		PersonElementWrapper infoApprovPerson = getRandomIdentity(null);
		
		Element infoApproverPersonElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_NC_30, "Person");
		XmlUtils.addAttribute(infoApproverPersonElement, OjbcNamespaceContext.NS_STRUCTURES_30, "id", "Information_Approver_" + recordId);
		
		Element infoApproverPersonNameElement = XmlUtils.appendElement(infoApproverPersonElement, OjbcNamespaceContext.NS_NC_30, "PersonName");
		
		Element personFullNameElement = XmlUtils.appendElement(infoApproverPersonNameElement, OjbcNamespaceContext.NS_NC_30, "PersonFullName");
				
		String infoApprovFullName = infoApprovPerson.firstName + " " + infoApprovPerson.lastName;
				
		personFullNameElement.setTextContent(infoApprovFullName);						
		
		PersonElementWrapper sampleDriverPerson = getRandomIdentity(null);
		
		Element driverPersonElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_NC_30, "Person");
		XmlUtils.addAttribute(driverPersonElement, OjbcNamespaceContext.NS_STRUCTURES_30, "id", "Driver_" + recordId);
		
		Element driverDobElement = XmlUtils.appendElement(driverPersonElement, OjbcNamespaceContext.NS_NC_30, "PersonBirthDate");				
		
		Element driverDobValElement = XmlUtils.appendElement(driverDobElement, OjbcNamespaceContext.NS_NC_30, "Date");	
								
		driverDobValElement.setTextContent(sampleDriverPerson.birthday);
		
		Element driverNameElement = XmlUtils.appendElement(driverPersonElement, OjbcNamespaceContext.NS_NC_30, "PersonName");
		
		Element driverGivenNameElement = XmlUtils.appendElement(driverNameElement, OjbcNamespaceContext.NS_NC_30, "PersonGivenName");	
		
		String sampleDriverFirstName = sampleDriverPerson.firstName;
		
		driverGivenNameElement.setTextContent(sampleDriverFirstName);
		
		Element driverMiddleName = XmlUtils.appendElement(driverNameElement, OjbcNamespaceContext.NS_NC_30, "PersonMiddleName");
		
		String sampleDriverMiddleName = sampleDriverPerson.middleName;
		
		driverMiddleName.setTextContent(sampleDriverMiddleName);
		
		Element driverSurNameElement = XmlUtils.appendElement(driverNameElement, OjbcNamespaceContext.NS_NC_30, "PersonSurName");
		
		String sampleDriverLastName = sampleDriverPerson.lastName;
		
		driverSurNameElement.setTextContent(sampleDriverLastName);
		
		Element driverSexCodeElement = XmlUtils.appendElement(driverPersonElement, OjbcNamespaceContext.NS_JXDM_51, "PersonSexCode");
		
		String sampleDriverSexCode = sampleDriverPerson.sex;
		
		driverSexCodeElement.setTextContent(sampleDriverSexCode);
		
		Element driverPersonCatCodeElement = XmlUtils.appendElement(driverPersonElement, OjbcNamespaceContext.NS_ME_VEHICLE_CRASH_CODES, "PersonCategoryCode");
		
		String vehicleCrashCode = RandomStringUtils.randomAlphabetic(2);
		
		driverPersonCatCodeElement.setTextContent(vehicleCrashCode);
				
		
		
		PersonElementWrapper sampleVehicleOwner = getRandomIdentity(null);
		
		Element vehicleOwnerElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_NC_30, "Person");
		
		XmlUtils.addAttribute(vehicleOwnerElement, OjbcNamespaceContext.NS_STRUCTURES_30, "id", "Vehicle_Owner_TODO" + recordId);

		Element vehicleOwnerDobElement = XmlUtils.appendElement(vehicleOwnerElement, OjbcNamespaceContext.NS_NC_30, "PersonBirthDate");				
		
		Element vehicleOwnerDobValElement = XmlUtils.appendElement(vehicleOwnerDobElement, OjbcNamespaceContext.NS_NC_30, "Date");
		
		vehicleOwnerDobValElement.setTextContent(sampleVehicleOwner.birthday);

		Element vehicleOwnerNameElement = XmlUtils.appendElement(vehicleOwnerElement, OjbcNamespaceContext.NS_NC_30, "PersonName");

		Element vehicleOwnerGivenNameElement = XmlUtils.appendElement(vehicleOwnerNameElement, OjbcNamespaceContext.NS_NC_30, "PersonGivenName");		
		vehicleOwnerGivenNameElement.setTextContent(sampleVehicleOwner.firstName);

		Element vehicleOwnerMiddleName = XmlUtils.appendElement(vehicleOwnerNameElement, OjbcNamespaceContext.NS_NC_30, "PersonMiddleName");
		vehicleOwnerMiddleName.setTextContent(sampleVehicleOwner.middleName);

		Element vehicleOwnerSurNameElement = XmlUtils.appendElement(vehicleOwnerNameElement, OjbcNamespaceContext.NS_NC_30, "PersonSurName");
		vehicleOwnerSurNameElement.setTextContent(sampleVehicleOwner.lastName);

		Element vehicleOwnerSexCodeElement = XmlUtils.appendElement(vehicleOwnerElement, OjbcNamespaceContext.NS_JXDM_51, "PersonSexCode");
		vehicleOwnerSexCodeElement.setTextContent(sampleVehicleOwner.sex);

		Element vehicleOwnerCatCodeElement = XmlUtils.appendElement(vehicleOwnerElement, OjbcNamespaceContext.NS_ME_VEHICLE_CRASH_CODES, "PersonCategoryCode");
		
		String sampleVehicleOwnerCatCode = RandomStringUtils.randomAlphanumeric(2);
		
		vehicleOwnerCatCodeElement.setTextContent(sampleVehicleOwnerCatCode);
		
		
		Element propOwner1EntOrgElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_NC_30, "EntityOrganization");		
		XmlUtils.addAttribute(propOwner1EntOrgElement, OjbcNamespaceContext.NS_STRUCTURES_30, "id", "Property_Owner_TODO" + recordId);
		
		Element propOwner1EntOrgNameElement = XmlUtils.appendElement(propOwner1EntOrgElement, OjbcNamespaceContext.NS_NC_30, "OrganizationName");		
								
		propOwner1EntOrgNameElement.setTextContent(sampleVehicleOwner.company);
		
		
		PersonElementWrapper samplePropertyOwner = getRandomIdentity(null);		
		
		Element propOwner2EntPersonElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_NC_30, "EntityPerson");
		
		XmlUtils.addAttribute(propOwner2EntPersonElement, OjbcNamespaceContext.NS_STRUCTURES_30, "id", "Property_Owner_TODO" + recordId);
		
		Element propOwnerEntPersonName = XmlUtils.appendElement(propOwner2EntPersonElement, OjbcNamespaceContext.NS_NC_30, "PersonName");
		
		Element propOwnerEntFullName = XmlUtils.appendElement(propOwnerEntPersonName, OjbcNamespaceContext.NS_NC_30, "PersonFullName");		
		
		String samplePropOwnerFullName = samplePropertyOwner.firstName + "" + samplePropertyOwner.lastName;
		
		propOwnerEntFullName.setTextContent(samplePropOwnerFullName);		
		
		Element occupant1EntPersonElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_NC_30, "EntityPerson");
		XmlUtils.addAttribute(occupant1EntPersonElement, OjbcNamespaceContext.NS_STRUCTURES_30, "id", "Occupant_01");
		
		Element occupantDobElement = XmlUtils.appendElement(occupant1EntPersonElement, OjbcNamespaceContext.NS_NC_30, "PersonBirthDate");
		
		Element occupantDobValElement = XmlUtils.appendElement(occupantDobElement, OjbcNamespaceContext.NS_NC_30, "Date");
				
		occupantDobValElement.setTextContent(samplePropertyOwner.birthday);
		
		Element occupantPersonNameElement = XmlUtils.appendElement(occupant1EntPersonElement, OjbcNamespaceContext.NS_NC_30, "PersonName");
		
		Element occupantGivenNameElement = XmlUtils.appendElement(occupantPersonNameElement, OjbcNamespaceContext.NS_NC_30, "PersonGivenName");
		
		occupantGivenNameElement.setTextContent(samplePropertyOwner.firstName);
		
		Element occupantMiddleName = XmlUtils.appendElement(occupantPersonNameElement, OjbcNamespaceContext.NS_NC_30, "PersonMiddleName");
		
		occupantMiddleName.setTextContent(samplePropertyOwner.middleName);
		
		Element occupantSurName = XmlUtils.appendElement(occupantPersonNameElement, OjbcNamespaceContext.NS_NC_30, "PersonSurName");
		occupantSurName.setTextContent(samplePropertyOwner.lastName);
		
		Element occupantSexCodeElement = XmlUtils.appendElement(occupantPersonNameElement, OjbcNamespaceContext.NS_JXDM_51, "PersonSexCode");
		occupantSexCodeElement.setTextContent(samplePropertyOwner.sex);
		
		Element occupantCatCodeElement = XmlUtils.appendElement(occupantPersonNameElement, OjbcNamespaceContext.NS_ME_VEHICLE_CRASH_CODES, "PersonCategoryCode");
		
		String sampleOccupantCatCode = RandomStringUtils.randomAlphanumeric(2);
		
		occupantCatCodeElement.setTextContent(sampleOccupantCatCode);
		
		
		PersonElementWrapper sampleDriver = getRandomIdentity(null);
		
		Element driverLocElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_NC_30, "Location");		
		XmlUtils.addAttribute(driverLocElement, OjbcNamespaceContext.NS_STRUCTURES_30, "id", "Driver_Loc_" + recordId);
		
		Element driverLocAddressElement = XmlUtils.appendElement(driverLocElement, OjbcNamespaceContext.NS_NC_30, "Address");
		
		Element driverLocStreetElement =  XmlUtils.appendElement(driverLocAddressElement, OjbcNamespaceContext.NS_NC_30, "LocationStreet");
				
		Element driverLocStreetValElement = XmlUtils.appendElement(driverLocStreetElement, OjbcNamespaceContext.NS_NC_30, "StreetFullText");
		
		
		driverLocStreetValElement.setTextContent(sampleDriver.streetAddress);		
		
 		Element driverLocCityElement = XmlUtils.appendElement(driverLocAddressElement, OjbcNamespaceContext.NS_NC_30, "LocationCityName");
 		driverLocCityElement.setTextContent(sampleDriver.city);
 		
 		Element driverLocStateElement = XmlUtils.appendElement(driverLocAddressElement, OjbcNamespaceContext.NS_JXDM_51, "LocationStateNCICLISCode");
 		
 		driverLocStateElement.setTextContent(sampleDriver.state);
 		
 		Element driverLocZipElement = XmlUtils.appendElement(driverLocAddressElement, OjbcNamespaceContext.NS_NC_30, "LocationPostalCode");
 		
 		driverLocZipElement.setTextContent(sampleDriver.zipCode);
 		
 		

 		PersonElementWrapper sampleWitness = getRandomIdentity(null);
 		
 		Element witnessLocElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_NC_30, "Location");		
 		XmlUtils.addAttribute(witnessLocElement, OjbcNamespaceContext.NS_STRUCTURES_30, "id", "Witness_Loc_" + recordId);

 		Element witnessLocAddressElement = XmlUtils.appendElement(witnessLocElement, OjbcNamespaceContext.NS_NC_30, "Address");

 		Element witnessLocStreetElement =  XmlUtils.appendElement(witnessLocAddressElement, OjbcNamespaceContext.NS_NC_30, "LocationStreet");
 		    
 		Element witnessLocStreetValElement = XmlUtils.appendElement(witnessLocStreetElement, OjbcNamespaceContext.NS_NC_30, "StreetFullText");
 		
 		witnessLocStreetValElement.setTextContent(sampleWitness.streetAddress);		

 		Element witnessLocCityElement = XmlUtils.appendElement(witnessLocAddressElement, OjbcNamespaceContext.NS_NC_30, "LocationCityName");
 		
 		witnessLocCityElement.setTextContent(sampleWitness.city);

 		Element witnessLocStateElement = XmlUtils.appendElement(witnessLocAddressElement, OjbcNamespaceContext.NS_JXDM_51, "LocationStateNCICLISCode");
 		
 		witnessLocStateElement.setTextContent(sampleWitness.state);

 		Element witnessLocZipElement = XmlUtils.appendElement(witnessLocAddressElement, OjbcNamespaceContext.NS_NC_30, "LocationPostalCode");
 		
 		witnessLocZipElement.setTextContent(sampleWitness.zipCode);
 		
 		
 		PersonElementWrapper propOwnerSample = getRandomIdentity(null);
 		
 		Element propOwnerLocElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_NC_30, "Location");		
 		XmlUtils.addAttribute(propOwnerLocElement, OjbcNamespaceContext.NS_STRUCTURES_30, "id", "Property_Owner_Loc" + recordId);

 		Element propOwnerLocAddressElement = XmlUtils.appendElement(propOwnerLocElement, OjbcNamespaceContext.NS_NC_30, "Address");

 		Element propOwnerLocStreetElement =  XmlUtils.appendElement(propOwnerLocAddressElement, OjbcNamespaceContext.NS_NC_30, "LocationStreet");
 		    
 		Element propOwnerLocStreetValElement = XmlUtils.appendElement(propOwnerLocStreetElement, OjbcNamespaceContext.NS_NC_30, "StreetFullText");		
 		propOwnerLocStreetValElement.setTextContent(propOwnerSample.streetAddress);		

 		Element propOwnerLocCityElement = XmlUtils.appendElement(propOwnerLocAddressElement, OjbcNamespaceContext.NS_NC_30, "LocationCityName");
 		propOwnerLocCityElement.setTextContent(propOwnerSample.city);

 		Element propOwnerLocStateElement = XmlUtils.appendElement(propOwnerLocAddressElement, OjbcNamespaceContext.NS_JXDM_51, "LocationStateNCICLISCode");
 		propOwnerLocStateElement.setTextContent(propOwnerSample.state);

 		Element propOwnerLocZipElement = XmlUtils.appendElement(propOwnerLocAddressElement, OjbcNamespaceContext.NS_NC_30, "LocationPostalCode");
 		propOwnerLocZipElement.setTextContent(propOwnerSample.zipCode); 		
 		
		
 		PersonElementWrapper vehicOwnerSample = getRandomIdentity(null);
 		
 		
		Element vehicleOwnerLocElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_NC_30, "Location");		
		XmlUtils.addAttribute(vehicleOwnerLocElement, OjbcNamespaceContext.NS_STRUCTURES_30, "id", "Vehicle_Owner_Loc_" + recordId);
		
		Element vehicleOwnerLocAddressElement = XmlUtils.appendElement(vehicleOwnerLocElement, OjbcNamespaceContext.NS_NC_30, "Address");
		
		Element vehicleOwnerLocStreetElement =  XmlUtils.appendElement(vehicleOwnerLocAddressElement, OjbcNamespaceContext.NS_NC_30, "LocationStreet");
		    
		Element vehicleOwnerLocStreetValElement = XmlUtils.appendElement(vehicleOwnerLocStreetElement, OjbcNamespaceContext.NS_NC_30, "StreetFullText");		
		vehicleOwnerLocStreetValElement.setTextContent(vehicOwnerSample.addressStreetNumber);		
		
		Element vehicleOwnerLocCityElement = XmlUtils.appendElement(vehicleOwnerLocAddressElement, OjbcNamespaceContext.NS_NC_30, "LocationCityName");
		vehicleOwnerLocCityElement.setTextContent(vehicOwnerSample.city);
		
		Element vehicleOwnerLocStateElement = XmlUtils.appendElement(vehicleOwnerLocAddressElement, OjbcNamespaceContext.NS_JXDM_51, "LocationStateNCICLISCode");
		vehicleOwnerLocStateElement.setTextContent(vehicOwnerSample.state);
		
		Element vehicleOwnerLocZipElement = XmlUtils.appendElement(vehicleOwnerLocAddressElement, OjbcNamespaceContext.NS_NC_30, "LocationPostalCode");
		vehicleOwnerLocZipElement.setTextContent(vehicOwnerSample.zipCode);

 		
		Element vehicleElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_NC_30, "Vehicle");
 		XmlUtils.addAttribute(vehicleElement, OjbcNamespaceContext.NS_STRUCTURES_30, "id", "Vehicle_" + recordId);
 		
 		Element vehicleColorElement = XmlUtils.appendElement(vehicleElement, OjbcNamespaceContext.NS_JXDM_51, "ConveyanceColorPrimaryCode");
 		
 		List<String> sampleColorList = Arrays.asList("BLO", "BLK", "BLU", "RED", "PUR");
 		
 		String sampleColor = (String)generateRandomValueFromList(sampleColorList);
 		
 		vehicleColorElement.setTextContent(sampleColor);
 		
 		
 		
 		List<String> sampleYearList = Arrays.asList("1970", "1989", "1996", "2016");
 		
 		String sampleYear = (String)generateRandomValueFromList(sampleYearList);
 		
 		Element vehcicleYearElement = XmlUtils.appendElement(vehicleColorElement, OjbcNamespaceContext.NS_NC_30, "ItemModelYearDate");
 		
 		vehcicleYearElement.setTextContent(sampleYear);
 		
 		Element vehicleIdElement = XmlUtils.appendElement(vehicleElement, OjbcNamespaceContext.NS_NC_30, "VehicleIdentification");
 		
 		Element vehicleIdValElement = XmlUtils.appendElement(vehicleIdElement, OjbcNamespaceContext.NS_NC_30, "IdentificationID");
 		
 		String sampleVin = RandomStringUtils.randomAlphanumeric(10);
 		
 		vehicleIdValElement.setTextContent(sampleVin);
 		
 		Element vehicleIdJurisdictionElement = XmlUtils.appendElement(vehicleIdElement, OjbcNamespaceContext.NS_NC_30, "IdentificationJurisdiction");
 		
 		Element vehicleIdStateCodeElement = XmlUtils.appendElement(vehicleIdJurisdictionElement, OjbcNamespaceContext.NS_JXDM_51, "LocationStateNCICLISCode");
 		
 		 		
 		vehicleIdStateCodeElement.setTextContent(vehicOwnerSample.state);
 		
 		Element vehicleMakeElement = XmlUtils.appendElement(vehicleElement, OjbcNamespaceContext.NS_JXDM_51, "VehicleMakeCode");
 		
 		List<String> sampleMakeList = Arrays.asList("Ford", "Chevy", "Dodge", "GM", "Kia");
 		
 		String sampleMake = (String)generateRandomValueFromList(sampleMakeList);
 		
 		vehicleMakeElement.setTextContent(sampleMake);
 		
 		Element vehicleUnitIdElement = XmlUtils.appendElement(vehicleElement, OjbcNamespaceContext.NS_VEHICLE_CRASH_QUERY_RESULT_EXT, "VehicleUnitIdentification");
 		
 		Element vehicleUnitIdValElement = XmlUtils.appendElement(vehicleUnitIdElement, OjbcNamespaceContext.NS_NC_30, "IdentificationID");
 		
 		String sampleVehicUnitId = RandomStringUtils.randomAlphanumeric(8);
 		
 		vehicleUnitIdValElement.setTextContent(sampleVehicUnitId);
 		
 		Element hitRunIndicatorElement = XmlUtils.appendElement(vehicleElement, OjbcNamespaceContext.NS_VEHICLE_CRASH_QUERY_RESULT_EXT, "HitRunIndicator");
 		
 		hitRunIndicatorElement.setTextContent(randomBooleanString());
 		 		
 		Element conveyRegElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_JXDM_51, "ConveyanceRegistration"); 		
 		XmlUtils.addAttribute(conveyRegElement, OjbcNamespaceContext.NS_STRUCTURES_30, "id", "Conveyance_Registration_" + recordId);
 		
 		Element conveyRegIdElement = XmlUtils.appendElement(conveyRegElement, OjbcNamespaceContext.NS_JXDM_51, "ConveyanceRegistrationPlateIdentification");
 		
 		Element conveyRegIdValElement = XmlUtils.appendElement(conveyRegIdElement, OjbcNamespaceContext.NS_NC_30, "IdentificationID"); 		
 		
 		String samplePlateId = RandomStringUtils.randomAlphanumeric(7);
 		
 		conveyRegIdValElement.setTextContent(samplePlateId);
 		
 		
 		Element conveyIdRegJurisdictElement = XmlUtils.appendElement(conveyRegIdElement, OjbcNamespaceContext.NS_NC_30, "IdentificationJurisdiction");
 	
 		Element conveyIdRegStateCodeElement = XmlUtils.appendElement(conveyIdRegJurisdictElement, OjbcNamespaceContext.NS_JXDM_51, "LocationStateNCICLISCode");
 		 		 		
 		conveyIdRegStateCodeElement.setTextContent(sampleVehicleOwner.state);
 		
 		Element insuranceElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_NC_30, "Insurance"); 		
 		XmlUtils.addAttribute(insuranceElement, OjbcNamespaceContext.NS_STRUCTURES_30, "id", "Insurance_" + recordId);
 		
 		Element insPolicyIdElement = XmlUtils.appendElement(insuranceElement, OjbcNamespaceContext.NS_NC_30, "InsurancePolicyIdentification");
 		
 		Element insPolicyIdValElement = XmlUtils.appendElement(insPolicyIdElement, OjbcNamespaceContext.NS_NC_30, "IdentificationID");
 		
 		String insPolicyId = RandomStringUtils.randomAlphanumeric(10);
 		 		
 		insPolicyIdValElement.setTextContent(insPolicyId);
 		
 		Element insCarrierNameElement = XmlUtils.appendElement(insuranceElement, OjbcNamespaceContext.NS_NC_30, "InsuranceCarrierName"); 
 		
 		List<String> insCarrierList = Arrays.asList("Geico", "Allstate", "Progressive");
 		
 		String insCarrier = (String)generateRandomValueFromList(insCarrierList);
 		
 		insCarrierNameElement.setTextContent(insCarrier);
 		
 		Element noInsuranceElement = XmlUtils.appendElement(insuranceElement, OjbcNamespaceContext.NS_VEHICLE_CRASH_QUERY_RESULT_EXT, "NoInsuranceIndicator");
 		
 		noInsuranceElement.setTextContent(randomBooleanString());
 		
 		
 		Element crashDriverLicElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_JXDM_51, "CrashDriverLicense"); 		
 		XmlUtils.addAttribute(crashDriverLicElement, OjbcNamespaceContext.NS_STRUCTURES_30, "id", "Driver_License_" + recordId);
 		
 		Element dlCardIdElement = XmlUtils.appendElement(crashDriverLicElement, OjbcNamespaceContext.NS_JXDM_51, "DriverLicenseCardIdentification");
 		
 		Element dlCardIdValueElement = XmlUtils.appendElement(dlCardIdElement, OjbcNamespaceContext.NS_NC_30, "IdentificationID");
 		
 		String dlId = RandomStringUtils.randomAlphanumeric(9);
 		
 		dlCardIdValueElement.setTextContent(dlId);
 		 		
 		Element dlJurisdictElement = XmlUtils.appendElement(dlCardIdElement, OjbcNamespaceContext.NS_NC_30, "IdentificationJurisdiction");
 		
 		Element dlStateElement = XmlUtils.appendElement(dlJurisdictElement, OjbcNamespaceContext.NS_JXDM_51, "LocationStateNCICLISCode");
 		
 		
 		dlStateElement.setTextContent(getRandomIdentity(null).state);
 		
 		Element dlRestrictElement = XmlUtils.appendElement(crashDriverLicElement, OjbcNamespaceContext.NS_JXDM_51, "DriverLicenseRestriction");
 		
 		Element dlRestrictTxtElement = XmlUtils.appendElement(dlRestrictElement, OjbcNamespaceContext.NS_JXDM_51, "DrivingRestrictionText");
 		
 		
 		List<String> dlRestrictList = Arrays.asList("Glasses", "Daylight", "Evening");
 		 		
 		String sampleRestriction = (String)generateRandomValueFromList(dlRestrictList);
 		
 		dlRestrictTxtElement.setTextContent(sampleRestriction);
 		
 		
 		Element dlEndorsementElement = XmlUtils.appendElement(crashDriverLicElement, OjbcNamespaceContext.NS_JXDM_51, "DriverLicenseEndorsement"); 		
 		Element dlEndorsementTxtElement = XmlUtils.appendElement(dlEndorsementElement, OjbcNamespaceContext.NS_JXDM_51, "DriverLicenseEndorsementText");
 		
 		List<String> endorsementList = Arrays.asList("motorcycle", "trailor", "commercial");
 		
 		String sampleEndorsement = (String)generateRandomValueFromList(endorsementList);
 		
 		dlEndorsementTxtElement.setTextContent(sampleEndorsement);
 		
 		Element dlCatCodeElement = XmlUtils.appendElement(crashDriverLicElement, OjbcNamespaceContext.NS_ME_VEHICLE_CRASH_CODES, "DriverLicenseCategoryCode");
 		
 		List<String> dlCatList = Arrays.asList("Civil", "Military", "Racing");
 		
 		String sampleDlCatCode = (String)generateRandomValueFromList(dlCatList);
 		
 		dlCatCodeElement.setTextContent(sampleDlCatCode);
 		 		
 		Element dlClassCodeElement = XmlUtils.appendElement(crashDriverLicElement, OjbcNamespaceContext.NS_VEHICLE_CRASH_QUERY_RESULT_EXT, "DriverLicenseClassCodeText");
 		
 		String dlClass = RandomStringUtils.randomAlphabetic(1);
 		
 		dlClassCodeElement.setTextContent(dlClass);
 		
 		Element activityInfoApproverAssocElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_JXDM_51, "ActivityInformationApproverAssociation");
 		
 		Element aprovAsocActivElement = XmlUtils.appendElement(activityInfoApproverAssocElement, OjbcNamespaceContext.NS_NC_30, "Activity"); 		
 		XmlUtils.addAttribute(aprovAsocActivElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Crash_TODO"); 		 		
 		 		
 		Element activInfoAprovAsocPersonElement = XmlUtils.appendElement(activityInfoApproverAssocElement, OjbcNamespaceContext.NS_NC_30, "Person"); 		
 		XmlUtils.addAttribute(activInfoAprovAsocPersonElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Information_Approver_TODO"); 		
 		
 		Element bloodAlcAssocElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_JXDM_51, "PersonBloodAlcoholContentAssociation");
 		
 		Element bloodTxtElement = XmlUtils.appendElement(bloodAlcAssocElement, OjbcNamespaceContext.NS_JXDM_51, "PersonBloodAlcoholContentNumberText");
 		
 		String sampleBloodContent = RandomStringUtils.randomNumeric(2);
 		
 		bloodTxtElement.setTextContent(sampleBloodContent);
 		
 		Element bloodActivityElement = XmlUtils.appendElement(bloodAlcAssocElement, OjbcNamespaceContext.NS_NC_30, "Activity"); 		
 		XmlUtils.addAttribute(bloodActivityElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Crash_TODO");
 		
 		Element bloodPersonElement = XmlUtils.appendElement(bloodAlcAssocElement, OjbcNamespaceContext.NS_NC_30, "Person"); 		
 		XmlUtils.addAttribute(bloodPersonElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Driver_TODO");
 		 		 		
 		Element itemInsAssocElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_NC_30, "ItemInsuranceAssociation");
 		
 		Element itemAssocInsElement = XmlUtils.appendElement(itemInsAssocElement, OjbcNamespaceContext.NS_NC_30, "Insurance"); 		
 		XmlUtils.addAttribute(itemAssocInsElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Insurance_TODO");
 		
 		
 		Element insuranceItemElement = XmlUtils.appendElement(itemInsAssocElement, OjbcNamespaceContext.NS_NC_30, "Item");
 		XmlUtils.addAttribute(insuranceItemElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Vehicle_TODO");
 		
 		
 		Element personResAsocElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_NC_30, "PersonResidenceAssociation");
 		
 		Element prsnResAssocPrsnElement = XmlUtils.appendElement(personResAsocElement, OjbcNamespaceContext.NS_NC_30, "Person"); 		
 		XmlUtils.addAttribute(prsnResAssocPrsnElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Driver_01");
 		
 		Element prsnRestAsocLocElement = XmlUtils.appendElement(personResAsocElement, OjbcNamespaceContext.NS_NC_30, "Location");
 		XmlUtils.addAttribute(prsnRestAsocLocElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Driver_Loc_01");
 		
 		
 		Element witnessAssocElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_NC_30, "PersonResidenceAssociation");
 		
 		Element witnessPersonResElement = XmlUtils.appendElement(witnessAssocElement, OjbcNamespaceContext.NS_NC_30, "Person"); 		
 		XmlUtils.addAttribute(witnessPersonResElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Witness_01");
 		
 		Element witnessPrsnResLocElement = XmlUtils.appendElement(witnessAssocElement, OjbcNamespaceContext.NS_NC_30, "nc:Location"); 		
 		XmlUtils.addAttribute(witnessPrsnResLocElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Witness_Loc_01");
 		
 		
 		Element propOwnerResAsocElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_NC_30, "PersonResidenceAssociation");
 		
 		Element propOwnerPersonElement = XmlUtils.appendElement(propOwnerResAsocElement, OjbcNamespaceContext.NS_NC_30, "Person"); 		
 		XmlUtils.addAttribute(propOwnerPersonElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Property_Owner_TODO");
 		
 		Element propOwnerResLocElement = XmlUtils.appendElement(propOwnerResAsocElement, OjbcNamespaceContext.NS_NC_30, "Location");
 		XmlUtils.addAttribute(propOwnerResLocElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Property_Owner_Loc_TODO");
 		
 		
 		
 		Element vehicOwnerResAsocElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_NC_30, "PersonResidenceAssociation");
 		
 		Element vehicOwnerPersonElement = XmlUtils.appendElement(vehicOwnerResAsocElement, OjbcNamespaceContext.NS_NC_30, "Person");
 		XmlUtils.addAttribute(vehicOwnerPersonElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Vehicle_Owner_01");
 		
 		Element vehicOwnerLocElement = XmlUtils.appendElement(vehicOwnerResAsocElement, OjbcNamespaceContext.NS_NC_30, "Location");
 		XmlUtils.addAttribute(vehicOwnerLocElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Vehicle_Owner_Loc_01");
 		
 		
 		
 		Element activityDocAssocElement = XmlUtils.appendElement(vehicleCrashReportElement, OjbcNamespaceContext.NS_NC_30, "ActivityDocumentAssociation");
 		
 		Element actDocAssocActivityElement = XmlUtils.appendElement(activityDocAssocElement, OjbcNamespaceContext.NS_NC_30, "Activity"); 		
 		XmlUtils.addAttribute(actDocAssocActivityElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Crash_01"); 		
 		
 		Element activDocAssocDocElement = XmlUtils.appendElement(activityDocAssocElement, OjbcNamespaceContext.NS_NC_30, "Document");
 		XmlUtils.addAttribute(activDocAssocDocElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Citation_01");
 		
 		
 		Element conveyRegAssocElement = XmlUtils.appendElement(activityDocAssocElement, OjbcNamespaceContext.NS_JXDM_51, "ConveyanceRegistrationAssociation");
 		
 		Element conveyItemRegElement = XmlUtils.appendElement(conveyRegAssocElement, OjbcNamespaceContext.NS_JXDM_51, "ItemRegistration"); 		
 		XmlUtils.addAttribute(conveyItemRegElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Conveyance_Registration_TODO");
 		
 		
 		Element conveyRetAsocItemElement = XmlUtils.appendElement(conveyRegAssocElement, OjbcNamespaceContext.NS_NC_30, "Item"); 		
 		XmlUtils.addAttribute(conveyRetAsocItemElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Vehicle_01");
 		
 		
		OjbcNamespaceContext ojbcNamespaceContext = new OjbcNamespaceContext();
		
		ojbcNamespaceContext.populateRootNamespaceDeclarations(rootVehicCrashElement);
				
		return rVehicleCrashDetailDoc;
	}

	
	private Document getNewDocument() throws ParserConfigurationException{
		
		DocumentBuilderFactory dbf = DocumentBuilderFactoryImpl.newInstance();
		
		DocumentBuilder docBuilder = dbf.newDocumentBuilder();		

		Document doc = docBuilder.newDocument();
		
		return doc;
	}	

}

