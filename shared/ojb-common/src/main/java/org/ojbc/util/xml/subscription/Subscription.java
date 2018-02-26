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
package org.ojbc.util.xml.subscription;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.ojbc.util.model.rapback.FbiRapbackSubscription;

public class Subscription implements Serializable {
	private static final long serialVersionUID = 7990280609495398189L;
	
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

	private String stateId;	
	
	private String fbiId;
	
	private String systemId;
	
	private String fullName;
	
	private Boolean active; 
	
	private LocalDate gracePeriodStartDate;
	
	private LocalDate gracePeriodEndDate; 
	
	//note f/l name only used for incident(not arrest)
	private String firstName;	
	
	private String lastName;
	
	private Date dateOfBirth;
	
	private Date subscriptionStartDate;
	
	private Date subscriptionEndDate;
	
	private LocalDate lastValidationDate;

	private LocalDate validationDueDate;
	
	private LocalDate creationDate;

	private LocalDate lastUpdatedDate;
	
	//Email notification recipients
	private List<String> emailList = new ArrayList<String>();
	
	//Category
	private String subscriptionPurpose;
	
	//Agency case number
	private String caseId;

	private String systemName;

	private String topic;
	
	//If set, the value provided will be used other wise it is autogenerated
	private String subscriptionQualificationId;
	
	private Boolean federalRapSheetDisclosureIndicator;
	
	private String federalRapSheetDisclosureAttentionDesignationText;
	
	private List<String> federalTriggeringEventCode = new ArrayList<String>();
	
	private String fbiSubscriptionID;
	
	private FbiRapbackSubscription fbiRapbackSubscription; 
	
	private String ownerFederationId;
	private String ownerEmailAddress;
	private String ownerFirstName;
	private String ownerLastName;
	private String agencyName;
	private String ori;

	public String getStateId() {
		return stateId;
	}

	public String getFbiId() {
		return fbiId;
	}

	public String getSystemId() {
		return systemId;
	}

	public String getFullName() {
		return fullName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public Date getSubscriptionStartDate() {
		return subscriptionStartDate;
	}

	public Date getSubscriptionEndDate() {
		return subscriptionEndDate;
	}

	public List<String> getEmailList() {
		return emailList;
	}

	public String getSubscriptionPurpose() {
		return subscriptionPurpose;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setStateId(String stateId) {
		this.stateId = StringUtils.trimToEmpty(stateId);
	}

	public void setFbiId(String fbiId) {
		this.fbiId = fbiId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public void setSubscriptionStartDate(Date subscriptionStartDate) {
		this.subscriptionStartDate = subscriptionStartDate;
	}

	public void setSubscriptionEndDate(Date subscriptionEndDate) {
		this.subscriptionEndDate = subscriptionEndDate;
	}

	public void setEmailList(List<String> emailList) {
		this.emailList = emailList;
	}

	public void setSubscriptionPurpose(String subscriptionPurpose) {
		this.subscriptionPurpose = subscriptionPurpose;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getSubscriptionQualificationId() {
		return subscriptionQualificationId;
	}

	public void setSubscriptionQualificationId(String subscriptionQualificationId) {
		this.subscriptionQualificationId = subscriptionQualificationId;
	}

	public Boolean getFederalRapSheetDisclosureIndicator() {
		return federalRapSheetDisclosureIndicator;
	}

	public void setFederalRapSheetDisclosureIndicator(
			Boolean federalRapSheetDisclosureIndicator) {
		this.federalRapSheetDisclosureIndicator = federalRapSheetDisclosureIndicator;
	}

	public String getFederalRapSheetDisclosureAttentionDesignationText() {
		return federalRapSheetDisclosureAttentionDesignationText;
	}

	public void setFederalRapSheetDisclosureAttentionDesignationText(
			String federalRapSheetDisclosureAttentionDesignationText) {
		this.federalRapSheetDisclosureAttentionDesignationText = federalRapSheetDisclosureAttentionDesignationText;
	}

	public List<String> getFederalTriggeringEventCode() {
		return federalTriggeringEventCode;
	}

	public void setFederalTriggeringEventCode(
			List<String> federalTriggeringEventCode) {
		this.federalTriggeringEventCode = federalTriggeringEventCode;
	}
	
	public String getFormattedFbiId() {
		return fbiId == null? "N/A" : fbiId;
	}

	@Override
	 public String toString() {
	  return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE); 
	 }

	public String getOri() {
		return ori;
	}

	public void setOri(String ori) {
		this.ori = ori;
	}

	public String getFbiSubscriptionID() {
		return fbiSubscriptionID;
	}

	public void setFbiSubscriptionID(String fbiSubscriptionID) {
		this.fbiSubscriptionID = fbiSubscriptionID;
	}

	public FbiRapbackSubscription getFbiRapbackSubscription() {
		return fbiRapbackSubscription;
	}

	public void setFbiRapbackSubscription(FbiRapbackSubscription fbiRapbackSubscription) {
		this.fbiRapbackSubscription = fbiRapbackSubscription;
	}

	public String getOwnerFederationId() {
		return ownerFederationId;
	}

	public void setOwnerFederationId(String ownerFederationId) {
		this.ownerFederationId = ownerFederationId;
	}

	public String getOwnerEmailAddress() {
		return ownerEmailAddress;
	}

	public void setOwnerEmailAddress(String ownerEmailAddress) {
		this.ownerEmailAddress = ownerEmailAddress;
	}

	public String getOwnerFirstName() {
		return ownerFirstName;
	}

	public void setOwnerFirstName(String ownerFirstName) {
		this.ownerFirstName = ownerFirstName;
	}

	public String getOwnerLastName() {
		return ownerLastName;
	}

	public void setOwnerLastName(String ownerLastName) {
		this.ownerLastName = ownerLastName;
	}

	public LocalDate getLastValidationDate() {
		return lastValidationDate;
	}

	public String getLastValidationDateString() {
		return Optional.ofNullable(lastValidationDate)
				.map(date-> date.format(formatter))
				.orElse("");
	}
	
	public void setLastValidationDate(LocalDate lastValidationDate) {
		this.lastValidationDate = lastValidationDate;
	}

	public LocalDate getCreationDate() {
		return creationDate;
	}

	public String getCreationDateString() {
		return Optional.ofNullable(creationDate)
					.map(date-> date.format(formatter))
					.orElse("");
	}
	
	public void setCreationDate(LocalDate creationDate) {
		this.creationDate = creationDate;
	}

	public LocalDate getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public String getLastUpdatedDateString() {
		return Optional.ofNullable(lastUpdatedDate).map(date-> date.format(formatter)).orElse("");
	}
	
	public void setLastUpdatedDate(LocalDate lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public LocalDate getGracePeriodStartDate() {
		return gracePeriodStartDate;
	}

	public void setGracePeriodStartDate(LocalDate gracePeriodStartDate) {
		this.gracePeriodStartDate = gracePeriodStartDate;
	}

	public LocalDate getGracePeriodEndDate() {
		return gracePeriodEndDate;
	}

	public void setGracePeriodEndDate(LocalDate gracePeriodEndDate) {
		this.gracePeriodEndDate = gracePeriodEndDate;
	}

	public LocalDate getValidationDueDate() {
		return validationDueDate;
	}

	public String getValidationDueDateString() {
		return Optional.ofNullable(validationDueDate).map(date-> date.format(formatter)).orElse("");
	}
	
	public void setValidationDueDate(LocalDate validationDueDate) {
		this.validationDueDate = validationDueDate;
	}

	public String getAgencyName() {
		return agencyName;
	}

	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}

}
