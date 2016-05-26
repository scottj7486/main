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
/*This is the default test data loaded into h2 when this is deployed */
use warrant_repository;

INSERT INTO WARRANT(WARRANTID, DATEOFWARRANT, DATEOFEXPIRATION, BROADCASTAREA, WARRANTENTRYTYPE, COURTAGENCYORI, LAWENFORCEMENTORI, COURTDOCKETNUMBER, OCACOMPLAINTNUMBER, OPERATOR, PACCCODE, ORIGINALOFFENSECODE, OFFENSECODE, GENERALOFFENSECHARACTER, CRIMINALTRACKINGNUMBER, EXTRADITE, EXTRADITIONLIMITS, PICKUPLIMITS, BONDAMOUNT, WARRANTSTATUS, WARRANTSTATUSTIMESTAMP) VALUES
(1, DATE '2016-05-26', NULL, NULL, 'AC', NULL, NULL, '123456', '1234567', 'FB', NULL, NULL, NULL, NULL, NULL, FALSE, NULL, NULL, '123', 'Accepted', NULL);

INSERT INTO WARRANTREMARKS(WARRANTREMARKSID, WARRANTID, WARRANTREMARKTEXT) VALUES
(1, 1, 'Warrant remarks text');

INSERT INTO PERSON(PERSONID, PERSONENTRYNUMBER, FIRSTNAME, MIDDLENAME, LASTNAME, NAMESUFFIX, FULLPERSONNAME, ADDRESSFULLTEXT, ADDRESSCITY, ADDRESSSTATE, ADDRESSZIP, SOCIALSECURITYNUMBERBASE, DATEOFBIRTH, PLACEOFBIRTH, PERSONAGE, OPERATORLICENSENUMBERBASE, OPERATORLICENSESTATEBASE, PERSONETHNICITYDESCRIPTION, PERSONEYECOLORDESCRIPTION, PERSONHAIRCOLORDESCRIPTION, PERSONSEXDESCRIPTION, PERSONRACEDESCRIPTION, PERSONSKINTONEDESCRIPTION, PERSONHEIGHT, PERSONWEIGHT, PERSONSCARSMARKSTATTOSBASE, PERSONCITIZENSHIPCOUNTRY, USCITIZENSHIPINDICATOR, PERSONIMMIGRATIONALIENQUERYIND, PERSONSTATEIDENTIFICATION, FBIIDENTIFICATIONNUMBER, MISCELLANEOUSIDBASE, PRISONRECORDNUMBER, PERSONCAUTIONDESCRIPTION, WARRANTMODREQUESTSENT, WARRANTMODRESPONSERECEIVED) VALUES
(1, '87654321', 'Bill', 'M2', 'Doe2', 'JR2', 'John M Doe JR2', '123 full address2', 'city2', 'WA', '999992', NULL, DATE '1977-01-01', NULL, '22', 'OL Base2', 'WA', 'B', 'eye2', 'hair2', 'F', 'A', NULL, '502', '300', 'SMM', 'CN', NULL, TRUE, 'SID2', 'FBI1232', 'MISC1232', NULL, '2', FALSE, FALSE);

INSERT INTO VEHICLE(VEHICLEID, PERSONID, LICENSEPLATETYPE, VEHICLELICENSEPLATEEXPIRATIOND, VEHICLENONEXPIRINGINDICATOR, VEHICLELICENSEPLATENUMBER, VEHICLELICENSESTATECODE, VEHICLEIDENTIFICATIONNUMBER, VEHICLEYEAR, VEHICLEMODEL, VEHICLEMAKE, VEHICLEPRIMARYCOLOR, VEHICLESECONDARYCOLOR, VEHICLESTYLE) VALUES
(1, 1, 'WI', '2011-06', NULL, '555ABC', 'MI', 'VIN123', '2010', 'FSN', 'FRD', 'RED', 'PINK', 'SN'),
(2, 1, 'WI', NULL, TRUE, '555ABC', 'MI', 'VIN124', '2010', 'FSN', 'FRD', 'RED', 'PINK', 'SN');

INSERT INTO PERSONIDADDITIONAL(PERSONIDADDITIONALID, PERSONID, PERSONADDITIONALID) VALUES
(1, 1, 'MISC1234');

INSERT INTO PERSONSMTADDITIONAL(PERSONSMTSUPPLEMENTALID, PERSONID, PERSONSCARSMARKSTATTOOS) VALUES
(1, 1, 'SMT');

INSERT INTO PERSONOLNADDITIONAL(PERSONOLNID, PERSONID, OPERATORLICENSENUMBER, OPERATORLICENSESTATE) VALUES
(1, 1, 'LI12335', 'MI');

INSERT INTO PERSONSSNADDITIONAL(PERSONSSNID, PERSONID, SOCIALSECURITYNUMBER) VALUES
(1, 1, '999999999');

INSERT INTO PERSONALTERNATENAME(PERSONALTERNATENAMEID, PERSONID, FIRSTNAME, FULLPERSONNAME, LASTNAME, MIDDLENAME, NAMESUFFIX) VALUES
(1, 1, 'Jonnie', 'Jonnie M Doze', 'Doze', 'M', 'JR');

INSERT INTO CHARGEREF(CHARGEREFID, PERSONID, REPORTINGAGENCYORI, CASEAGENCYCOMPLAINTNUMBER, TRANSACTIONCONTROLNUMBER, REPORTINGAGENCYNAME) VALUES
(1, 1, 'ORI12345', 'ATN12345', 'TCN123', 'Agency Name');

INSERT INTO CHARGE(CHARGEID, CHARGEREFID, CHARGESEVERITYLEVEL) VALUES
(1, 1, 'SEV1'),
(2, 1, 'SEV2');

INSERT INTO WARRANTCHARGEREF(WARRANTCHARGEREFID, WARRANTID, CHARGEREFID) VALUES
(1, 1, 1);

INSERT INTO OFFICER(OFFICERID, CHARGEREFID, OFFICERNAME, OFFICERBADGENUMBER) VALUES
(1, 1, 'Joe Law Enforcement', '123');