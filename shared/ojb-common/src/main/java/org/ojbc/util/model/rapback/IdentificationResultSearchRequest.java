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
package org.ojbc.util.model.rapback;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


public class IdentificationResultSearchRequest implements Serializable{

	private static final long serialVersionUID = 2814315765098291193L;
	
	private IdentificationResultCategory identificationResultCategory;
	private List<String> identificationTransactionStatus;  
	private LocalDate reportedDateStartDate; 
	private LocalDate reportedDateEndDate;
	
	private String firstName; 
	private String lastName; 
	private String otn; //IdentifiedPersonTrackingIdentification
	private List<String> civilIdentificationReasonCodes;  
	private List<String> criminalIdentificationReasonCodes;  

	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
	
	public IdentificationResultCategory getIdentificationResultCategory() {
		return identificationResultCategory;
	}

	public void setIdentificationResultCategory(
			IdentificationResultCategory identificationResultCategory) {
		this.identificationResultCategory = identificationResultCategory;
	}

	public List<String> getIdentificationTransactionStatus() {
		return identificationTransactionStatus;
	}

	public void setIdentificationTransactionStatus(
			List<String> identificationTransactionStatus) {
		this.identificationTransactionStatus = identificationTransactionStatus;
	}

	public LocalDate getReportedDateStartDate() {
		return reportedDateStartDate;
	}

	public void setReportedDateStartDate(LocalDate reportedDateStartDate) {
		this.reportedDateStartDate = reportedDateStartDate;
	}

	public LocalDate getReportedDateEndDate() {
		return reportedDateEndDate;
	}

	public void setReportedDateEndDate(LocalDate reportedDateEndDate) {
		this.reportedDateEndDate = reportedDateEndDate;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getOtn() {
		return otn;
	}

	public void setOtn(String otn) {
		this.otn = otn;
	}

	public List<String> getCivilIdentificationReasonCodes() {
		return civilIdentificationReasonCodes;
	}

	public void setCivilIdentificationReasonCodes(
			List<String> civilIdentificationReasonCodes) {
		this.civilIdentificationReasonCodes = civilIdentificationReasonCodes;
	}

	public List<String> getCriminalIdentificationReasonCodes() {
		return criminalIdentificationReasonCodes;
	}

	public void setCriminalIdentificationReasonCodes(
			List<String> criminalIdentificationReasonCodes) {
		this.criminalIdentificationReasonCodes = criminalIdentificationReasonCodes;
	}
    
}
