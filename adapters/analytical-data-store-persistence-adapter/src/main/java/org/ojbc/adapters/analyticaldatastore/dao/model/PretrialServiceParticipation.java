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
package org.ojbc.adapters.analyticaldatastore.dao.model;

import java.util.Date;

public class PretrialServiceParticipation {

	//pk
    private int pretrialServiceParticipationID;

    //fk
    private int pretrialServiceID;
    
    //fk
    private int personID;
    
    //fk
    private int countyID;
    
    //fk
    private int riskScoreID;
    
    //fk
    private int assessedNeedID;

    private String pretrialServiceCaseNumber;
    
    private Date intakeDate;
    
    private char recordType;

	public int getPretrialServiceParticipationID() {
		return pretrialServiceParticipationID;
	}

	public void setPretrialServiceParticipationID(int pretrialServiceParticipationID) {
		this.pretrialServiceParticipationID = pretrialServiceParticipationID;
	}

	public int getPretrialServiceID() {
		return pretrialServiceID;
	}

	public void setPretrialServiceID(int pretrialServiceID) {
		this.pretrialServiceID = pretrialServiceID;
	}

	public int getPersonID() {
		return personID;
	}

	public void setPersonID(int personID) {
		this.personID = personID;
	}

	public int getCountyID() {
		return countyID;
	}

	public void setCountyID(int countyID) {
		this.countyID = countyID;
	}

	public int getRiskScoreID() {
		return riskScoreID;
	}

	public void setRiskScoreID(int riskScoreID) {
		this.riskScoreID = riskScoreID;
	}

	public int getAssessedNeedID() {
		return assessedNeedID;
	}

	public void setAssessedNeedID(int assessedNeedID) {
		this.assessedNeedID = assessedNeedID;
	}

	public String getPretrialServiceCaseNumber() {
		return pretrialServiceCaseNumber;
	}

	public void setPretrialServiceCaseNumber(String pretrialServiceCaseNumber) {
		this.pretrialServiceCaseNumber = pretrialServiceCaseNumber;
	}

	public Date getIntakeDate() {
		return intakeDate;
	}

	public void setIntakeDate(Date intakeDate) {
		this.intakeDate = intakeDate;
	}

	public char getRecordType() {
		return recordType;
	}

	public void setRecordType(char recordType) {
		this.recordType = recordType;
	}
	
}
