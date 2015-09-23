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
package org.ojbc.adapters.rapbackdatastore.processor;

import static org.ojbc.adapters.rapbackdatastore.RapbackDataStoreAdapterConstants.REPORT_FEDERAL_SUBSCRIPTION_CREATION;
import static org.ojbc.adapters.rapbackdatastore.RapbackDataStoreAdapterConstants.REPORT_FEDERAL_SUBSCRIPTION_UPDATE;

import org.apache.camel.Body;
import org.apache.camel.Header;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ojbc.adapters.rapbackdatastore.dao.RapbackDAO;
import org.ojbc.intermediaries.sn.fbi.rapback.FbiRapbackSubscription;
import org.ojbc.util.xml.XmlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

@Service
public class subscriptionReportingProcessor {

	private static final Log log = LogFactory.getLog( subscriptionReportingProcessor.class );
	
	@Autowired
	protected RapbackDAO rapbackDAO;
	
	@Transactional
	public void processFbiSubscriptionReport(@Body Document report, @Header("operationName") String operationName) throws Exception
	{
		log.info("Processing FBI Subscription Report.");
		
		if (REPORT_FEDERAL_SUBSCRIPTION_CREATION.equals(operationName)){
			processFbiSubscriptionCreationReport(report);
		}
		else if (REPORT_FEDERAL_SUBSCRIPTION_UPDATE.equals(operationName)){
			processFbiSubscriptionUpdateReport(report);
		}
		
		
	}

	private void processFbiSubscriptionUpdateReport(Document report) throws Exception {
		FbiRapbackSubscription fbiRapbackSubscription = buildFbiSubscriptionFromUpdate(report);
		rapbackDAO.updateFbiRapbackSubscription(fbiRapbackSubscription);
	}

	private void processFbiSubscriptionCreationReport(Document report) throws Exception {
		FbiRapbackSubscription fbiRapbackSubscription = buildNewFbiSubscription(report);
		rapbackDAO.saveFbiRapbackSubscription(fbiRapbackSubscription);
	}

	private FbiRapbackSubscription buildFbiSubscriptionFromUpdate(
			Document report) throws Exception {
		Node rootNode = XmlUtils.xPathNodeSearch(report, 
				"/fed_subcr_upd-doc:FederalSubscriptionUpdateReport");
		Node rapbackSubscriptionData = XmlUtils.xPathNodeSearch(rootNode, "fed_subcr_upd-ext:RapBackSubscriptionData"); 
		FbiRapbackSubscription fbiRapbackSubscription = new FbiRapbackSubscription();
		
		String rapBackActivityNotificationFormatCode = XmlUtils.xPathStringSearch(rapbackSubscriptionData, "fed_subcr_upd-ext:RapBackActivityNotificationFormatCode");
		fbiRapbackSubscription.setRapbackActivityNotificationFormat(rapBackActivityNotificationFormatCode);
		
		String rapBackExpirationDate = XmlUtils.xPathStringSearch(rapbackSubscriptionData, "fed_subcr_upd-ext:RapBackExpirationDate/nc30:Date");
		fbiRapbackSubscription.setRapbackExpirationDate(XmlUtils.parseXmlDate(rapBackExpirationDate));
		
		String rapBackInStateOptOutIndicator = XmlUtils.xPathStringSearch(rapbackSubscriptionData, "fed_subcr_upd-ext:RapBackInStateOptOutIndicator");
		fbiRapbackSubscription.setRapbackOptOutInState(BooleanUtils.toBooleanObject(rapBackInStateOptOutIndicator));
		
		String rapBackSubscriptionDate = XmlUtils.xPathStringSearch(rapbackSubscriptionData, "fed_subcr_upd-ext:RapBackSubscriptionDate/nc30:Date");
		fbiRapbackSubscription.setRapbackStartDate(XmlUtils.parseXmlDate(rapBackSubscriptionDate));
		
		String rapBackSubscriptionIdentification = XmlUtils.xPathStringSearch(rapbackSubscriptionData, "fed_subcr_upd-ext:RapBackSubscriptionIdentification/nc30:IdentificationID");
		fbiRapbackSubscription.setFbiSubscriptionId(rapBackSubscriptionIdentification);
		
		String rapBackSubscriptionTermCode = XmlUtils.xPathStringSearch(rapbackSubscriptionData, "fed_subcr_upd-ext:RapBackSubscriptionTermCode");
		fbiRapbackSubscription.setSubscriptionTerm(rapBackSubscriptionTermCode);
		
		String rapBackTermDate = XmlUtils.xPathStringSearch(rapbackSubscriptionData, "fed_subcr_upd-ext:RapBackTermDate/nc30:Date");
		fbiRapbackSubscription.setRapbackTermDate(XmlUtils.parseXmlDate(rapBackTermDate));
		
		String ucn = XmlUtils.xPathStringSearch(rootNode, "nc30:Person[@s30:id=../jxdm50:Subject/nc30:RoleOfPerson/@s30:ref]/jxdm50:PersonAugmentation/jxdm50:PersonFBIIdentification/nc30:IdentificationID");
		fbiRapbackSubscription.setUcn(ucn);
		return fbiRapbackSubscription;
	}

	private FbiRapbackSubscription buildNewFbiSubscription(Document report) throws Exception {
		Node rootNode = XmlUtils.xPathNodeSearch(report, 
				"/fed_subcr-doc:FederalSubscriptionCreationReport");
		Node rapbackSubscriptionData = XmlUtils.xPathNodeSearch(rootNode, "fed_subcr-ext:RapBackSubscriptionData"); 
		FbiRapbackSubscription fbiRapbackSubscription = new FbiRapbackSubscription();
		
		String rapBackActivityNotificationFormatCode = XmlUtils.xPathStringSearch(rapbackSubscriptionData, "fed_subcr-ext:RapBackActivityNotificationFormatCode");
		fbiRapbackSubscription.setRapbackActivityNotificationFormat(rapBackActivityNotificationFormatCode);
		
		String rapBackCategoryCode = XmlUtils.xPathStringSearch(rapbackSubscriptionData, "fed_subcr-ext:RapBackCategoryCode");
		fbiRapbackSubscription.setRapbackCategory(rapBackCategoryCode);
		
		String rapBackExpirationDate = XmlUtils.xPathStringSearch(rapbackSubscriptionData, "fed_subcr-ext:RapBackExpirationDate/nc30:Date");
		fbiRapbackSubscription.setRapbackExpirationDate(XmlUtils.parseXmlDate(rapBackExpirationDate));
		
		String rapBackInStateOptOutIndicator = XmlUtils.xPathStringSearch(rapbackSubscriptionData, "fed_subcr-ext:RapBackInStateOptOutIndicator");
		fbiRapbackSubscription.setRapbackOptOutInState(BooleanUtils.toBooleanObject(rapBackInStateOptOutIndicator));
		
		String rapBackSubscriptionDate = XmlUtils.xPathStringSearch(rapbackSubscriptionData, "fed_subcr-ext:RapBackSubscriptionDate/nc30:Date");
		fbiRapbackSubscription.setRapbackStartDate(XmlUtils.parseXmlDate(rapBackSubscriptionDate));
		
		String rapBackSubscriptionIdentification = XmlUtils.xPathStringSearch(rapbackSubscriptionData, "fed_subcr-ext:RapBackSubscriptionIdentification/nc30:IdentificationID");
		fbiRapbackSubscription.setFbiSubscriptionId(rapBackSubscriptionIdentification);
		
		String rapBackSubscriptionTermCode = XmlUtils.xPathStringSearch(rapbackSubscriptionData, "fed_subcr-ext:RapBackSubscriptionTermCode");
		fbiRapbackSubscription.setSubscriptionTerm(rapBackSubscriptionTermCode);
		
		String rapBackTermDate = XmlUtils.xPathStringSearch(rapbackSubscriptionData, "fed_subcr-ext:RapBackTermDate/nc30:Date");
		fbiRapbackSubscription.setRapbackTermDate(XmlUtils.parseXmlDate(rapBackTermDate));
		
		String ucn = XmlUtils.xPathStringSearch(rootNode, "nc30:Person[@s30:id=../jxdm50:Subject/nc30:RoleOfPerson/@s30:ref]/jxdm50:PersonAugmentation/jxdm50:PersonFBIIdentification/nc30:IdentificationID");
		fbiRapbackSubscription.setUcn(ucn);
		return fbiRapbackSubscription;
	}

}
