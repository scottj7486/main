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
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.saxon.dom.DocumentBuilderFactoryImpl;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.ojbc.util.xml.OjbcNamespaceContext;
import org.ojbc.util.xml.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CourtCaseSampleGenerator extends AbstractSampleGenerator {
	
	private static final String CURRENT_DATE = DateTime.now().toString("yyyy-MM-dd");
	
	public CourtCaseSampleGenerator() throws ParserConfigurationException,
			IOException {
		
		super();
	}

	public List<Document> generateCourtCaseSamples(int recordCount) throws IOException, ParserConfigurationException{
		
		List<Document> rCourtCaseDocList = new ArrayList<Document>(recordCount);
		
		for(int i=0; i<recordCount; i++){
			
			PersonElementWrapper iPerson = getRandomIdentity(null);
			
			Document courtCaseDoc = buildCourtCaseDetailDoc(iPerson);
			
			rCourtCaseDocList.add(courtCaseDoc);
		}
		
		return null;
	}
	
	
	Document buildCourtCaseDetailDoc(PersonElementWrapper person) throws ParserConfigurationException{
			
		Document rCourtCaseDetailDoc = getNewDocument();
		
		Element rootCourtCaseElement = rCourtCaseDetailDoc.createElementNS(OjbcNamespaceContext.NS_COURT_CASE_QUERY_RESULTS_EXCH_DOC, "CourtCaseQueryResults");
		rootCourtCaseElement.setPrefix(OjbcNamespaceContext.NS_PREFIX_COURT_CASE_QUERY_RESULTS_EXCH_DOC);

		rCourtCaseDetailDoc.appendChild(rootCourtCaseElement);
				
		Element docCreateDateElement = XmlUtils.appendElement(rootCourtCaseElement, OjbcNamespaceContext.NS_NC_30, "DocumentCreationDate");		
		
		Element docCreateDateValElement = XmlUtils.appendElement(docCreateDateElement, OjbcNamespaceContext.NS_NC_30, "DateTime");
		docCreateDateValElement.setTextContent(CURRENT_DATE);

		Element docIdElement = XmlUtils.appendElement(rootCourtCaseElement, OjbcNamespaceContext.NS_NC_30, "DocumentIdentification");
		Element docIdValElement = XmlUtils.appendElement(docIdElement, OjbcNamespaceContext.NS_NC_30, "IdentificationID");		
		docIdValElement.setTextContent("TODO");
		
		Element sysIdElement = XmlUtils.appendElement(rootCourtCaseElement, OjbcNamespaceContext.NS_INTEL_31, "SystemIdentification");		
		
		Element sysIdValElement = XmlUtils.appendElement(sysIdElement, OjbcNamespaceContext.NS_NC_30, "IdentificationID");		
		sysIdValElement.setTextContent("TODO");
		
		Element sysNameElement = XmlUtils.appendElement(sysIdElement, OjbcNamespaceContext.NS_NC_30, "SystemName");
		sysNameElement.setTextContent("TODO");
		
		
		
		Element caseElement = XmlUtils.appendElement(rootCourtCaseElement, OjbcNamespaceContext.NS_NC_30, "Case"); 		
		XmlUtils.addAttribute(caseElement, OjbcNamespaceContext.NS_STRUCTURES_30, "id", "Case_01");
		
		Element activIdElement = XmlUtils.appendElement(caseElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ActivityIdentification");
		Element activIdValElement = XmlUtils.appendElement(activIdElement, OjbcNamespaceContext.NS_NC_30, "IdentificationID");		
		String sampleCaseId = RandomStringUtils.randomNumeric(8);		
		activIdValElement.setTextContent(sampleCaseId);
		
		Element activStatusElement = XmlUtils.appendElement(caseElement, OjbcNamespaceContext.NS_NC_30, "ActivityStatus");		
		Element actStatDescTxtElement = XmlUtils.appendElement(activStatusElement, OjbcNamespaceContext.NS_NC_30, "StatusDescriptionText");		
		actStatDescTxtElement.setTextContent("TODO");
		
		Element caseDispElement = XmlUtils.appendElement(caseElement, OjbcNamespaceContext.NS_NC_30, "CaseDisposition");
		Element dispDateElement = XmlUtils.appendElement(caseDispElement, OjbcNamespaceContext.NS_NC_30, "DispositionDate");
		Element caseDispDatValElement = XmlUtils.appendElement(dispDateElement, OjbcNamespaceContext.NS_NC_30, "Date");
		caseDispDatValElement.setTextContent(CURRENT_DATE);
		
		Element caseGenCatTxtElement = XmlUtils.appendElement(caseElement, OjbcNamespaceContext.NS_NC_30, "CaseGeneralCategoryText");
		caseGenCatTxtElement.setTextContent("TODO");
		
		Element caseTrackIdElement = XmlUtils.appendElement(caseElement, OjbcNamespaceContext.NS_NC_30, "CaseTrackingID");
		String sampleCaseTrackId = RandomStringUtils.randomNumeric(8);
		caseTrackIdElement.setTextContent(sampleCaseTrackId);
		
		Element caseDocketIdElement = XmlUtils.appendElement(caseElement, OjbcNamespaceContext.NS_NC_30, "CaseDocketID");
		caseDocketIdElement.setTextContent("TODO");
		
		Element caseFilingElement = XmlUtils.appendElement(caseElement, OjbcNamespaceContext.NS_NC_30, "CaseFiling");		
		Element caseFileDocCreateDate = XmlUtils.appendElement(caseFilingElement, OjbcNamespaceContext.NS_NC_30, "DocumentCreationDate");		
		Element caseFileDocCreateDateValElement = XmlUtils.appendElement(caseFileDocCreateDate, OjbcNamespaceContext.NS_NC_30, "DateTime"); 
		caseFileDocCreateDateValElement.setTextContent(CURRENT_DATE);
		
		
		Element caseAugmentElement = XmlUtils.appendElement(caseFilingElement, OjbcNamespaceContext.NS_JXDM_51, "CaseAugmentation");		
		Element caseAmendChargeElement = XmlUtils.appendElement(caseAugmentElement, OjbcNamespaceContext.NS_JXDM_51, "CaseAmendedCharge");
		
		Element chargeCountElement = XmlUtils.appendElement(caseAmendChargeElement, OjbcNamespaceContext.NS_JXDM_51, "ChargeCountQuantity"); 		
		String sampleChargeCount = RandomStringUtils.randomNumeric(2);		
		chargeCountElement.setTextContent(sampleChargeCount);
		
		
		Element chargeDescTxtElement = XmlUtils.appendElement(caseAmendChargeElement, OjbcNamespaceContext.NS_JXDM_51, "ChargeDescriptionText");		
		String sampleChargeDesc = getRandomString("TODO", "Speeding");		
		chargeDescTxtElement.setTextContent(sampleChargeDesc);
				
		Element chargeFilingDateElement = XmlUtils.appendElement(caseAmendChargeElement, OjbcNamespaceContext.NS_JXDM_51, "ChargeFilingDate");		
		Element chargeFilingDateValElement = XmlUtils.appendElement(chargeFilingDateElement, OjbcNamespaceContext.NS_NC_30, "Date");		
		chargeFilingDateValElement.setTextContent("TODO");
		
		Element chargeStatuteElement = XmlUtils.appendElement(chargeFilingDateElement, OjbcNamespaceContext.NS_JXDM_51, "ChargeStatute");
		
		Element chargeStatuteCodeIdElement = XmlUtils.appendElement(chargeStatuteElement, OjbcNamespaceContext.NS_JXDM_51, "StatuteCodeIdentification");
		
		Element statCodeIdValElement = XmlUtils.appendElement(chargeStatuteCodeIdElement, OjbcNamespaceContext.NS_NC_30, "IdentificationID");		
		String sampleChargeStatuteId = RandomStringUtils.randomNumeric(6);	
		statCodeIdValElement.setTextContent(sampleChargeStatuteId);
		
		Element chargeStatIdCatDescTxtElement = XmlUtils.appendElement(chargeStatuteCodeIdElement, OjbcNamespaceContext.NS_NC_30, "IdentificationCategoryDescriptionText");
		chargeStatIdCatDescTxtElement.setTextContent("TODO");
		
		Element caseChargeElement = XmlUtils.appendElement(caseAugmentElement, OjbcNamespaceContext.NS_JXDM_51, "CaseCharge");
		
		Element caseChargeCountElement = XmlUtils.appendElement(caseChargeElement, OjbcNamespaceContext.NS_JXDM_51, "ChargeCountQuantity");	
		String sampleCaseChargeCount = RandomStringUtils.randomNumeric(1);		
		caseChargeCountElement.setTextContent(sampleCaseChargeCount);
		
		Element caseChargeDescTxtElement = XmlUtils.appendElement(caseChargeElement, OjbcNamespaceContext.NS_JXDM_51, "ChargeDescriptionText");
		caseChargeDescTxtElement.setTextContent("TODO");
		
		Element chargeDispElement = XmlUtils.appendElement(caseChargeElement, OjbcNamespaceContext.NS_JXDM_51, "ChargeDisposition");
		
		Element chargeDispDateElement = XmlUtils.appendElement(chargeDispElement, OjbcNamespaceContext.NS_NC_30, "DispositionDate");		
		Element chargeDispDateValElement = XmlUtils.appendElement(chargeDispDateElement, OjbcNamespaceContext.NS_NC_30, "Date");		
		chargeDispDateValElement.setTextContent(CURRENT_DATE);
		
		
		Element chargeDispDescTxt = XmlUtils.appendElement(chargeDispElement, OjbcNamespaceContext.NS_NC_30, "DispositionDescriptionText");		
		chargeDispDescTxt.setTextContent("TODO");
		
		Element chargeDispOtherTxtElement = XmlUtils.appendElement(chargeDispElement, OjbcNamespaceContext.NS_JXDM_51, "ChargeDispositionOtherText");
		chargeDispOtherTxtElement.setTextContent("TODO");
		
		Element caseChargeFilingDateElement = XmlUtils.appendElement(caseChargeElement, OjbcNamespaceContext.NS_JXDM_51, "ChargeFilingDate");		
		Element caseChargeFilingDateValElement = XmlUtils.appendElement(caseChargeFilingDateElement, OjbcNamespaceContext.NS_NC_30, "Date");		
		caseChargeFilingDateValElement.setTextContent(CURRENT_DATE);
		
		Element chargePleaElement = XmlUtils.appendElement(caseChargeFilingDateElement, OjbcNamespaceContext.NS_JXDM_51, "ChargePlea");
		
		Element chargePleaActivityDateElement = XmlUtils.appendElement(chargePleaElement, OjbcNamespaceContext.NS_NC_30, "ActivityDate");		
		Element chargePleaActivityDateValElement = XmlUtils.appendElement(chargePleaActivityDateElement, OjbcNamespaceContext.NS_NC_30, "Date");		
		chargePleaActivityDateValElement.setTextContent(CURRENT_DATE);
		
		Element pleaCatCodeElement = XmlUtils.appendElement(chargePleaElement, OjbcNamespaceContext.NS_JXDM_51, "PleaCategoryCode");		
		String samplePleaCatCode = RandomStringUtils.randomAlphabetic(1);		
		pleaCatCodeElement.setTextContent(samplePleaCatCode);
		
		Element chargeSentenceElement = XmlUtils.appendElement(caseChargeFilingDateElement, OjbcNamespaceContext.NS_JXDM_51, "ChargeSentence");
		
		Element chargeSentActivityIdElement = XmlUtils.appendElement(chargeSentenceElement, OjbcNamespaceContext.NS_NC_30, "ActivityIdentification");
		
		Element chargeSentActivIdValElement = XmlUtils.appendElement(chargeSentActivityIdElement, OjbcNamespaceContext.NS_NC_30, "IdentificationID");
		
		String sampleChargeActivId = RandomStringUtils.randomNumeric(8);		
		chargeSentActivIdValElement.setTextContent(sampleChargeActivId);
		
		Element chargeSentenceActivDateElement = XmlUtils.appendElement(chargeSentenceElement, OjbcNamespaceContext.NS_NC_30, "ActivityDate");		
		Element chargeSentActivDateValElement = XmlUtils.appendElement(chargeSentenceActivDateElement, OjbcNamespaceContext.NS_NC_30, "Date");		
		chargeSentActivDateValElement.setTextContent(CURRENT_DATE);
				
		Element chargeSentActivStatElement= XmlUtils.appendElement(chargeSentenceElement, OjbcNamespaceContext.NS_NC_30, "ActivityStatus");		
		Element chargeSentActivStatDescTxtElement = XmlUtils.appendElement(chargeSentActivStatElement, OjbcNamespaceContext.NS_NC_30, "StatusDescriptionText");		
		chargeSentActivStatDescTxtElement.setTextContent("TODO");
		
		Element chargeSentenceChargeElement = XmlUtils.appendElement(chargeSentenceElement, OjbcNamespaceContext.NS_JXDM_51, "SentenceCharge");		
		Element sentChargeStatElement = XmlUtils.appendElement(chargeSentenceChargeElement, OjbcNamespaceContext.NS_JXDM_51, "ChargeStatute");
		
		Element sentStatCodeIdElement = XmlUtils.appendElement(sentChargeStatElement, OjbcNamespaceContext.NS_JXDM_51, "StatuteCodeIdentification"); 
		
		Element sentStatCodeIdValElement = XmlUtils.appendElement(sentStatCodeIdElement, OjbcNamespaceContext.NS_NC_30, "IdentificationID");		
		String sampleSentStatId = RandomStringUtils.randomNumeric(8);		
		sentStatCodeIdValElement.setTextContent(sampleSentStatId);
		
		Element chargeSentenceTermElement = XmlUtils.appendElement(chargeSentenceChargeElement, OjbcNamespaceContext.NS_JXDM_51, "SentenceTerm");
		
		Element chargeSentMaxTermElement = XmlUtils.appendElement(chargeSentenceTermElement, OjbcNamespaceContext.NS_JXDM_51, "TermMaximumDuration");
		
		String sampleMaxTerm = getRandomString("TOOD", "TODO");		
		chargeSentMaxTermElement.setTextContent(sampleMaxTerm);
		
		Element chargeSentMinTermElement = XmlUtils.appendElement(chargeSentenceTermElement, OjbcNamespaceContext.NS_JXDM_51, "TermMinimumDuration");
		chargeSentMinTermElement.setTextContent(getRandomString("TODO", "TODO"));
		
		Element chargeSentSuperviseFineAmountElement = XmlUtils.appendElement(chargeSentenceElement, OjbcNamespaceContext.NS_JXDM_51, "SupervisionFineAmount");
		
		Element chargeSentFineAmountValElement = XmlUtils.appendElement(chargeSentSuperviseFineAmountElement, OjbcNamespaceContext.NS_NC_30, "Amount");		
		chargeSentFineAmountValElement.setTextContent(getRandomString("25.70", "9800.99"));
		
		Element caseChargeSeqIdElement = XmlUtils.appendElement(caseChargeFilingDateElement, OjbcNamespaceContext.NS_JXDM_51, "ChargeSequenceID");		
		caseChargeSeqIdElement.setTextContent(RandomStringUtils.randomNumeric(2));
				
		Element caseChargeStatuteElement = XmlUtils.appendElement(caseChargeElement, OjbcNamespaceContext.NS_JXDM_51, "ChargeStatute");
		
		Element caseChargeStatuteCodeIdElement = XmlUtils.appendElement(caseChargeStatuteElement, OjbcNamespaceContext.NS_JXDM_51, "StatuteCodeIdentification");
		
		Element caseChargeStatCodeIdValElement = XmlUtils.appendElement(caseChargeStatuteCodeIdElement, OjbcNamespaceContext.NS_NC_30, "IdentificationID");
		caseChargeStatCodeIdValElement.setTextContent(RandomStringUtils.randomNumeric(8));
		
		Element caseChargeStatIdCatDescTxtElement = XmlUtils.appendElement(caseChargeStatuteCodeIdElement, OjbcNamespaceContext.NS_NC_30, "IdentificationCategoryDescriptionText");
		caseChargeStatIdCatDescTxtElement.setTextContent("TODO");
		
		// TODO confirm 2nd Case Charge element is needed
		
		Element caseCourtElement = XmlUtils.appendElement(caseAugmentElement, OjbcNamespaceContext.NS_JXDM_51, "CaseCourt");
		
		Element caseCourtOrgAugmentElement = XmlUtils.appendElement(caseCourtElement, OjbcNamespaceContext.NS_JXDM_51, "OrganizationAugmentation");
		
		Element caseCourtOrgJurisdictElement = XmlUtils.appendElement(caseCourtOrgAugmentElement, OjbcNamespaceContext.NS_JXDM_51, "OrganizationJurisdiction");
		
		Element caseCourtOrgAutJurisdicTxtElement = XmlUtils.appendElement(caseCourtOrgJurisdictElement, OjbcNamespaceContext.NS_NC_30, "JurisdictionText");		
		caseCourtOrgAutJurisdicTxtElement.setTextContent("TODO");
		
		Element caseCourtNameElement = XmlUtils.appendElement(caseCourtElement, OjbcNamespaceContext.NS_JXDM_51, "CourtName");
		caseCourtNameElement.setTextContent("TODO");
		
		Element caseCourtDivTxtElement = XmlUtils.appendElement(caseCourtElement, OjbcNamespaceContext.NS_JXDM_51, "CourtDivisionText");
		caseCourtDivTxtElement.setTextContent("TODO");
		
		
		Element caseCourtEventElement = XmlUtils.appendElement(caseAugmentElement, OjbcNamespaceContext.NS_JXDM_51, "CaseCourtEvent");
		
		Element caseCourtEventActivIdElement = XmlUtils.appendElement(caseCourtEventElement, OjbcNamespaceContext.NS_NC_30, "ActivityIdentification");
		
		Element caseCourtActivIdValElement = XmlUtils.appendElement(caseCourtEventActivIdElement, OjbcNamespaceContext.NS_NC_30, "IdentificationID");		
		caseCourtActivIdValElement.setTextContent(RandomStringUtils.randomNumeric(8));
		
		Element caseCourtActivDateElement = XmlUtils.appendElement(caseCourtEventElement, OjbcNamespaceContext.NS_NC_30, "ActivityDate");
		
		Element caseCourtActivityDateValElement = XmlUtils.appendElement(caseCourtActivDateElement, OjbcNamespaceContext.NS_NC_30, "Date");		
		caseCourtActivityDateValElement.setTextContent(CURRENT_DATE);
		
		Element caseCourtActivDescTxxtElement = XmlUtils.appendElement(caseCourtEventElement, OjbcNamespaceContext.NS_NC_30, "ActivityDescriptionText");		
		caseCourtActivDescTxxtElement.setTextContent("TODO");
				
		Element activityNameElement = XmlUtils.appendElement(caseCourtEventElement, OjbcNamespaceContext.NS_NC_30, "ActivityName");
		activityNameElement.setTextContent("TODO");
		
		Element courtEventJudgeElement = XmlUtils.appendElement(caseCourtEventElement, OjbcNamespaceContext.NS_JXDM_51, "CourtEventJudge");
		
		Element roleOfPersonElement = XmlUtils.appendElement(courtEventJudgeElement, OjbcNamespaceContext.NS_NC_30, "RoleOfPerson");
		
		Element judgeNameElement = XmlUtils.appendElement(roleOfPersonElement, OjbcNamespaceContext.NS_NC_30, "PersonName");
		
		Element judgeFullNameElement = XmlUtils.appendElement(judgeNameElement, OjbcNamespaceContext.NS_NC_30, "PersonFullName");		
		judgeFullNameElement.setTextContent("TODO");
		
		Element firstCourtAprncElement = XmlUtils.appendElement(courtEventJudgeElement, OjbcNamespaceContext.NS_COURT_CASE_QUERY_RESULTS_EXT, "FirstCourtAppearance");
		
		Element courtAprncDatElement = XmlUtils.appendElement(firstCourtAprncElement, OjbcNamespaceContext.NS_JXDM_51, "CourtAppearanceDate");
		
		Element courtAprncDatValElement = XmlUtils.appendElement(courtAprncDatElement, OjbcNamespaceContext.NS_NC_30, "Date");		
		courtAprncDatValElement.setTextContent("TODO");
		
		Element courtEventCommentsTxtElement = XmlUtils.appendElement(courtEventJudgeElement, OjbcNamespaceContext.NS_COURT_CASE_QUERY_RESULTS_EXT, "CourtEventCommentsText");		
		courtEventCommentsTxtElement.setTextContent("TODO");
		
		Element defendantSelfRepIndicElement = XmlUtils.appendElement(caseAugmentElement, OjbcNamespaceContext.NS_JXDM_51, "CaseDefendantSelfRepresentationIndicator");		
		defendantSelfRepIndicElement.setTextContent(getRandomBooleanString());
		
		Element caseDefendantPartyElement = XmlUtils.appendElement(caseAugmentElement, OjbcNamespaceContext.NS_JXDM_51, "CaseDefendantParty");
		
		Element defendantEntOrgElement = XmlUtils.appendElement(caseDefendantPartyElement, OjbcNamespaceContext.NS_NC_30, "EntityOrganization");
		
		Element entOrgNameElement = XmlUtils.appendElement(defendantEntOrgElement, OjbcNamespaceContext.NS_NC_30, "OrganizationName");
		entOrgNameElement.setTextContent(getRandomString("TODO", "TODO"));
		
		Element entPersonElement = XmlUtils.appendElement(caseDefendantPartyElement, OjbcNamespaceContext.NS_NC_30, "EntityPerson");
		
		XmlUtils.addAttribute(entPersonElement, OjbcNamespaceContext.NS_STRUCTURES_30, "ref", "Person_01");
		
		
		Element caseDefenseAtterneyElement = XmlUtils.appendElement(caseAugmentElement, OjbcNamespaceContext.NS_JXDM_51, "CaseDefenseAttorney");
		
		Element defenseAttortyPersonRoleElement = XmlUtils.appendElement(caseDefenseAtterneyElement, OjbcNamespaceContext.NS_NC_30, "RoleOfPerson");
		
		Element defenseAtrnyPersonNameElement = XmlUtils.appendElement(defenseAttortyPersonRoleElement, OjbcNamespaceContext.NS_NC_30, "PersonName");;
		
		Element defenseAtrnyFullNameElement = XmlUtils.appendElement(defenseAtrnyPersonNameElement, OjbcNamespaceContext.NS_NC_30, "PersonFullName");
		defenseAtrnyFullNameElement.setTextContent("TODO");
		
		Element caseDomViolenceElement = XmlUtils.appendElement(caseAugmentElement, OjbcNamespaceContext.NS_JXDM_51, "CaseDomesticViolenceIndicator");
		caseDomViolenceElement.setTextContent(getRandomBooleanString());
		
		Element caseHearingElement = XmlUtils.appendElement(caseAugmentElement, OjbcNamespaceContext.NS_JXDM_51, "CaseHearing");
		
		Element caseHearingActivIdElement = XmlUtils.appendElement(caseHearingElement, OjbcNamespaceContext.NS_NC_30, "ActivityIdentification");		
		caseHearingActivIdElement.setTextContent(RandomStringUtils.randomNumeric(8));
		
		Element activCatTxtElement = XmlUtils.appendElement(caseHearingElement, OjbcNamespaceContext.NS_NC_30, "ActivityCategoryText");		
		activCatTxtElement.setTextContent("TODO");
		
		Element caseHearingActivDateRangeElement = XmlUtils.appendElement(caseHearingElement, OjbcNamespaceContext.NS_NC_30, "ActivityDateRange");
		
		Element caseHearingStartDateElement = XmlUtils.appendElement(caseHearingActivDateRangeElement, OjbcNamespaceContext.NS_NC_30, "StartDate");
		
		Element caseHearingStartDateValElement = XmlUtils.appendElement(caseHearingStartDateElement, OjbcNamespaceContext.NS_NC_30, "DateTime");		
		caseHearingStartDateValElement.setTextContent(CURRENT_DATE);
		
		Element caseHearingEndDateElement = XmlUtils.appendElement(caseHearingActivDateRangeElement, OjbcNamespaceContext.NS_NC_30, "EndDate");
		
		Element caseHearingEndDateValElement = XmlUtils.appendElement(caseHearingEndDateElement, OjbcNamespaceContext.NS_NC_30, "DateTime");
		caseHearingEndDateValElement.setTextContent(CURRENT_DATE);
		
		Element activDescTxtElement = XmlUtils.appendElement(caseHearingElement, OjbcNamespaceContext.NS_NC_30, "ActivityDescriptionText");
		activDescTxtElement.setTextContent("TODO");
		
		Element hearingActivNameElement = XmlUtils.appendElement(caseHearingElement, OjbcNamespaceContext.NS_NC_30, "ActivityName");
		hearingActivNameElement.setTextContent(getRandomString("TODO", ""));
		
		 Element activReasonElement = XmlUtils.appendElement(caseHearingElement, OjbcNamespaceContext.NS_NC_30, "ActivityReasonText");
		 activReasonElement.setTextContent(getRandomString("TODO", "", ""));
		 
		Element activDispElement = XmlUtils.appendElement(caseHearingElement, OjbcNamespaceContext.NS_NC_30, "ActivityDisposition");
		
		Element activDispDescTxtElement = XmlUtils.appendElement(activDispElement, OjbcNamespaceContext.NS_NC_30, "DispositionDescriptionText");		
		activDispDescTxtElement.setTextContent(getRandomString("TODO", ""));
		
		
		Element caseHearingCourtEventJudgeElement = XmlUtils.appendElement(caseHearingElement, OjbcNamespaceContext.NS_JXDM_51, "CourtEventJudge");
		
		Element caseHearingJudgeRoleElement = XmlUtils.appendElement(caseHearingCourtEventJudgeElement, OjbcNamespaceContext.NS_NC_30, "RoleOfPerson");
		
		Element caseHearingJudgeNameElement = XmlUtils.appendElement(caseHearingJudgeRoleElement, OjbcNamespaceContext.NS_NC_30, "PersonName");
		
		Element caseJudgeFullNameElement = XmlUtils.appendElement(caseHearingJudgeNameElement, OjbcNamespaceContext.NS_NC_30, "PersonFullName");
		
		caseJudgeFullNameElement.setTextContent("TODO");
		
		Element caseHearingCommentsTxtElement = XmlUtils.appendElement(caseHearingElement, OjbcNamespaceContext.NS_COURT_CASE_QUERY_RESULTS_EXT, "CourtEventCommentsText");
		
		caseHearingCommentsTxtElement.setTextContent("TODO");
		
		Element caseJudgeElement = XmlUtils.appendElement(caseAugmentElement, OjbcNamespaceContext.NS_JXDM_51, "CaseJudge");
		
		Element caseJudgeRolePersonElement = XmlUtils.appendElement(caseJudgeElement, OjbcNamespaceContext.NS_JXDM_51, "RoleOfPerson");
		
		Element caseJudgeNameElement = XmlUtils.appendElement(caseJudgeRolePersonElement, OjbcNamespaceContext.NS_NC_30, "PersonName");		
		
		Element caseJudgePersonNameElement = XmlUtils.appendElement(caseJudgeNameElement, OjbcNamespaceContext.NS_NC_30, "PersonFullName");		
		caseJudgePersonNameElement.setTextContent(getRandomName());
		
		Element judicBarMemberElement = XmlUtils.appendElement(caseJudgeElement, OjbcNamespaceContext.NS_JXDM_51, "JudicialOfficialBarMembership");
		
		Element judicBarIdElement = XmlUtils.appendElement(judicBarMemberElement, OjbcNamespaceContext.NS_JXDM_51, "JudicialOfficialBarIdentification");
		
		Element judicBarIdValElement = XmlUtils.appendElement(judicBarIdElement, OjbcNamespaceContext.NS_NC_30, "IdentificationID");
		
		judicBarIdValElement.setTextContent(RandomStringUtils.randomNumeric(8));
		
		Element caseLieneageElement = XmlUtils.appendElement(caseAugmentElement, OjbcNamespaceContext.NS_JXDM_51, "CaseLineageCase");
		
		Element caseTrackingIdElement = XmlUtils.appendElement(caseLieneageElement, OjbcNamespaceContext.NS_NC_30, "CaseTrackingID");		
		caseTrackingIdElement.setTextContent(RandomStringUtils.randomNumeric(8));
		
		Element caseProsecAtrnyElement = XmlUtils.appendElement(caseAugmentElement, OjbcNamespaceContext.NS_JXDM_51, "CaseProsecutionAttorney");
		
		Element prosecAtrnyRoleElement = XmlUtils.appendElement(caseProsecAtrnyElement, OjbcNamespaceContext.NS_NC_30, "RoleOfPerson");
		
		Element prosecPersonElement = XmlUtils.appendElement(prosecAtrnyRoleElement, OjbcNamespaceContext.NS_NC_30, "PersonName");
		
		Element prosecAtrnyNameElement = XmlUtils.appendElement(prosecPersonElement, OjbcNamespaceContext.NS_NC_30, "PersonFullName");		
		prosecAtrnyNameElement.setTextContent(getRandomName());
		
		Element prosecutorBarElement = XmlUtils.appendElement(caseProsecAtrnyElement, OjbcNamespaceContext.NS_JXDM_51, "JudicialOfficialBarMembership");
		
		Element prosecutorOfficBarIdElement = XmlUtils.appendElement(prosecutorBarElement, OjbcNamespaceContext.NS_JXDM_51, "JudicialOfficialBarIdentification");
		
		Element prosecutorOfficBarIdValElement = XmlUtils.appendElement(prosecutorOfficBarIdElement, OjbcNamespaceContext.NS_NC_30, "IdentificationID");
		prosecutorOfficBarIdValElement.setTextContent(RandomStringUtils.randomNumeric(8));
						
		Element caseAugmentExtElement = XmlUtils.appendElement(caseFilingElement, OjbcNamespaceContext.NS_COURT_CASE_QUERY_RESULTS_EXT, "CaseAugmentation");
		
		Element caseSealedIndicatorElement = XmlUtils.appendElement(caseAugmentExtElement, OjbcNamespaceContext.NS_COURT_CASE_QUERY_RESULTS_EXT, "CaseSealedIndicator");		
		caseSealedIndicatorElement.setTextContent(getRandomBooleanString());
		
		Element remandDateElement = XmlUtils.appendElement(caseAugmentExtElement, OjbcNamespaceContext.NS_COURT_CASE_QUERY_RESULTS_EXT, "RemandDate");
		
		Element remandDateValElement = XmlUtils.appendElement(remandDateElement, OjbcNamespaceContext.NS_NC_30, "Date");		
		remandDateValElement.setTextContent(CURRENT_DATE);
		
		Element apelateCaseElement = XmlUtils.appendElement(caseElement, OjbcNamespaceContext.NS_JXDM_51, "AppellateCase");		
		XmlUtils.addAttribute(apelateCaseElement, OjbcNamespaceContext.NS_STRUCTURES_30, "id", "Appel_Case_01");
		
		Element apelateCaseTrackIdElement = XmlUtils.appendElement(apelateCaseElement, OjbcNamespaceContext.NS_NC_30, "CaseTrackingID");
		apelateCaseTrackIdElement.setTextContent(RandomStringUtils.randomNumeric(8));
		
		Element apelateCaseFilingElement = XmlUtils.appendElement(apelateCaseElement, OjbcNamespaceContext.NS_NC_30, "CaseFiling");
		
		Element apelateCaseFileDocCreateDateElement = XmlUtils.appendElement(apelateCaseFilingElement, OjbcNamespaceContext.NS_NC_30, "DocumentCreationDate");
		
		Element apelateCaseFileDocDateVal = XmlUtils.appendElement(apelateCaseFileDocCreateDateElement, OjbcNamespaceContext.NS_NC_30, "Date");		
		apelateCaseFileDocDateVal.setTextContent(CURRENT_DATE);
		
		Element identityElement = XmlUtils.appendElement(caseElement, OjbcNamespaceContext.NS_NC_30, "Identity");		
		XmlUtils.addAttribute(identityElement, OjbcNamespaceContext.NS_STRUCTURES_30, "id", "Identity_01");
		
		
		Element idPersonRepElement = XmlUtils.appendElement(identityElement, OjbcNamespaceContext.NS_NC_30, "IdentityPersonRepresentation");
		
		Element idPersonDobElement = XmlUtils.appendElement(idPersonRepElement, OjbcNamespaceContext.NS_NC_30, "PersonBirthDate");
		
		Element idPersonDobValElement = XmlUtils.appendElement(idPersonDobElement, OjbcNamespaceContext.NS_NC_30, "Date");		
		idPersonDobValElement.setTextContent(CURRENT_DATE);
		
		Element idPersonNameElement = XmlUtils.appendElement(idPersonRepElement, OjbcNamespaceContext.NS_NC_30, "PersonName");
		
		Element idPersonGivenName = XmlUtils.appendElement(idPersonNameElement, OjbcNamespaceContext.NS_NC_30, "PersonGivenName");		
		idPersonGivenName.setTextContent("TODO");
		
		Element idPersonMiddleName = XmlUtils.appendElement(idPersonNameElement, OjbcNamespaceContext.NS_NC_30, "PersonMiddleName");
		idPersonMiddleName.setTextContent("TODO");
		
		Element idPersonSurNameElement = XmlUtils.appendElement(idPersonNameElement, OjbcNamespaceContext.NS_NC_30, "PersonSurName"); 
		idPersonSurNameElement.setTextContent("TODO");
		
		Element idPersonFullNameElement = XmlUtils.appendElement(idPersonNameElement, OjbcNamespaceContext.NS_NC_30, "PersonFullName");
		idPersonFullNameElement.setTextContent("TODO");
		
		Element idPersonSsnElement = XmlUtils.appendElement(idPersonRepElement, OjbcNamespaceContext.NS_NC_30, "PersonSSNIdentification");
		
		Element idPersonSsnIdElement = XmlUtils.appendElement(idPersonSsnElement, OjbcNamespaceContext.NS_NC_30, "IdentificationID");		
		idPersonSsnIdElement.setTextContent(RandomStringUtils.randomNumeric(8));
		
		
		// TODO build document
		
		OjbcNamespaceContext ojbcNamespaceContext = new OjbcNamespaceContext();
		
		ojbcNamespaceContext.populateRootNamespaceDeclarations(rootCourtCaseElement);
		
		return rCourtCaseDetailDoc;
	}
	
	
	private Document getNewDocument() throws ParserConfigurationException{
		
		DocumentBuilderFactory dbf = DocumentBuilderFactoryImpl.newInstance();
		
		DocumentBuilder docBuilder = dbf.newDocumentBuilder();		

		Document doc = docBuilder.newDocument();
		
		return doc;
	}	

}
