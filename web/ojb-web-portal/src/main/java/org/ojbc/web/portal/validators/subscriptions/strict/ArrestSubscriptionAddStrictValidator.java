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
package org.ojbc.web.portal.validators.subscriptions.strict;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ojbc.util.xml.subscription.Subscription;
import org.ojbc.web.portal.validators.subscriptions.ArrestSubscriptionValidatorInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service("arrestSubscriptionAddStrictValidator")
public class ArrestSubscriptionAddStrictValidator implements ArrestSubscriptionValidatorInterface{
	
	private final Log logger = LogFactory.getLog(this.getClass());	
	
	@Value("${showSubscriptionPurposeDropDown:false}")
	Boolean showSubscriptionPurposeDropDown;
	
	@Value("${showCaseIdInput:false}")
	Boolean showCaseIdInput;
	
	public void validate(Subscription subscription, BindingResult errors){
						
		logger.info("* * * inside validate()");		
		
		if(subscription == null){
			return;
		}
				
		Map<String, String> fieldToErrorMap = getValidationErrorsList(subscription);
		
		if(fieldToErrorMap ==  null){
			return;
		}
		
		for(String iField : fieldToErrorMap.keySet()){
			
			String errorMessage = fieldToErrorMap.get(iField);
			
			errors.rejectValue(iField, errorMessage);			
		}				
					
	}
	
	
	public Map<String, String> getValidationErrorsList(Subscription subscription){
		
		if(subscription == null){
			return null;
		}
		
		Map<String, String> fieldToErrorMap = new HashMap<String, String>();		
						
		String topic = subscription.getTopic(); 		
		if(StringUtils.isBlank(topic)){			
			fieldToErrorMap.put("subscriptionType", "Subscription type must be specified");			
		}
				
		String sid = subscription.getStateId();		
		if(StringUtils.isBlank(sid)){			
			fieldToErrorMap.put("stateId", "SID must be specified");
		}
		
		String name = subscription.getFullName();		
		if(StringUtils.isBlank(name)){
			fieldToErrorMap.put("fullName", "Name must be specified");
		}
		
		Date subStartDate = subscription.getSubscriptionStartDate();
		if(subStartDate == null){
			fieldToErrorMap.put("subscriptionStartDate", "Start date must be specified");
		}
		
		Date subEndDate = subscription.getSubscriptionEndDate();
		if(subEndDate != null && subStartDate != null){			
			
			if(subEndDate.before(subStartDate)){
				
				fieldToErrorMap.put("subscriptionEndDate", "End date may not occur before start date");
			}else{
				Calendar oneYearAfterStartCal = Calendar.getInstance();
				oneYearAfterStartCal.setTime(subStartDate);
				oneYearAfterStartCal.add(Calendar.YEAR, 1);
				Date oneYearAfterStartDate = oneYearAfterStartCal.getTime();
						
				if(subEndDate.after(oneYearAfterStartDate)){
					fieldToErrorMap.put("subscriptionEndDate", "End date may not occur more than one year after the start date");
				}					
			}					
		}			
	
		if (showSubscriptionPurposeDropDown) {
			if(StringUtils.isBlank(subscription.getSubscriptionPurpose())){
				fieldToErrorMap.put("subscriptionPurpose", "Purpose must be specified");
			}
		}
		
		if (showCaseIdInput) {
			if(StringUtils.isBlank(subscription.getCaseId())){
				fieldToErrorMap.put("caseId", "Case Id must be specified");
			}
		}
				
		boolean hasEmail = false;
		
		for(String iEmail : subscription.getEmailList()){
			if(StringUtils.isNotBlank(iEmail)){
				hasEmail = true;
			}
		}
		
		if(!hasEmail){
			fieldToErrorMap.put("emailList", "Email Address must be specified");
		}			
		
		return fieldToErrorMap;
	}

}


