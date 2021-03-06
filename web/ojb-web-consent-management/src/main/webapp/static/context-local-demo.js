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
console.log('Local demo data context javascript.  Returning demo (fake) data for demo purposes.')

refreshData = function() {
    demodata = [ {
	"consentId" : 1,
	"bookingNumber" : "B1234",
	"nameNumber" : "N1234",
	"personFirstName" : "TestFirst",
	"personMiddleName" : "TestMiddle",
	"personLastName" : "TestLast",
	"personGender" : "F",
	"personDOBString" : "1975-02-01"
    }, {
	"consentId" : 2,
	"bookingNumber" : "B2345",
	"nameNumber" : "N234",
	"personFirstName" : "TestFirst1",
	"personMiddleName" : "TestMiddle1",
	"personLastName" : "TestLast1",
	"personGender" : "M",
	"personDOBString" : "1974-03-01"
    } ];
    var table = $('#inmate-table').DataTable();
    if (table.rows().count() == 0) {
	table.clear().rows.add(demodata).draw();
    }
}

submitConsentDecision = function() {
    console.log("Local demo mode - mimicing submission of consent decision json: " + JSON.stringify(getConsentDecisionJson()));
    clearDecisionRecordFields();
    updateUIState();
}
