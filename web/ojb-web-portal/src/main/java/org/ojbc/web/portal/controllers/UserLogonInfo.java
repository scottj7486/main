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
package org.ojbc.web.portal.controllers;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class UserLogonInfo implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String userNameString;
	private String userName;
	private String userFirstName; 
	private String userLastName; 
	
	private String employer;
	private String employerSubunitName;
	private String identityProviderId;
	private String federationId;

	private String timeOnlineString;
	private String emailAddress;
	private String employerOri; 
	private Boolean criminalJusticeEmployerIndicator; 
	private Boolean lawEnforcementEmployerIndicator;


	UserLogonInfo() {
		setUserNameString(PortalController.DEFAULT_USER_LOGON_MESSAGE);
		setTimeOnlineString(PortalController.DEFAULT_USER_TIME_ONLINE);
	}

	public String getUserNameString() {
		return userNameString;
	}

	public void setUserNameString(String userNameString) {
		this.userNameString = userNameString;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmployer() {
		return employer;
	}

	public void setEmployer(String employer) {
		this.employer = employer;
	}

	public String getTimeOnlineString() {
		return timeOnlineString;
	}

	public void setTimeOnlineString(String timeOnlineString) {
		this.timeOnlineString = timeOnlineString;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);    
    }

	public String getEmployerOri() {
		return employerOri;
	}

	public void setEmployerOri(String employerOri) {
		this.employerOri = employerOri;
	}

	public Boolean getCriminalJusticeEmployerIndicator() {
		return criminalJusticeEmployerIndicator;
	}

	public void setCriminalJusticeEmployerIndicator(
			Boolean criminalJusticeEmployerIndicator) {
		this.criminalJusticeEmployerIndicator = criminalJusticeEmployerIndicator;
	}

	public Boolean getLawEnforcementEmployerIndicator() {
		return lawEnforcementEmployerIndicator;
	}

	public void setLawEnforcementEmployerIndicator(
			Boolean lawEnforcementEmployerIndicator) {
		this.lawEnforcementEmployerIndicator = lawEnforcementEmployerIndicator;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	public String getEmployerSubunitName() {
		return employerSubunitName;
	}

	public void setEmployerSubunitName(String employerSubunitName) {
		this.employerSubunitName = employerSubunitName;
	}

	public String getIdentityProviderId() {
		return identityProviderId;
	}

	public void setIdentityProviderId(String identityProviderId) {
		this.identityProviderId = identityProviderId;
	}

	public String getFederationId() {
		return federationId;
	}

	public void setFederationId(String federationId) {
		this.federationId = federationId;
	}
	
}