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

-- Person Sex Codes
insert into PersonSex(PersonSexID, PersonSexCode, PersonSexDescription) values(1, 'M', 'Male');
insert into PersonSex(PersonSexID, PersonSexCode, PersonSexDescription) values(2, 'F', 'Female');
insert into PersonSex(PersonSexID, PersonSexCode, PersonSexDescription) values(3, 'U', 'Unknown');

-- Person Race Codes
insert into PersonRace(PersonRaceID, PersonRaceCode, PersonRaceDescription) values(1, 'I', 'American Indian or Alaskan Native');
insert into PersonRace(PersonRaceID, PersonRaceCode, PersonRaceDescription) values(2, 'A', 'Asian or Pacific Islander');
insert into PersonRace(PersonRaceID, PersonRaceCode, PersonRaceDescription) values(3, 'U', 'Unknown');
insert into PersonRace(PersonRaceID, PersonRaceCode, PersonRaceDescription) values(4, 'W', 'White');
insert into PersonRace(PersonRaceID, PersonRaceCode, PersonRaceDescription) values(5, 'B', 'Black');

-- Agency - Placeholder while waiting for code values
insert into Agency(AgencyID, AgencyName) values (1, 'Adams County SO');
insert into Agency(AgencyID, AgencyName) values (2, 'Arvada PD');
insert into Agency(AgencyID, AgencyName) values (3, 'Brighton PD');
insert into Agency(AgencyID, AgencyName) values (4, 'Commerce City PD');
insert into Agency(AgencyID, AgencyName) values (5, 'Federal Heights PD');
insert into Agency(AgencyID, AgencyName) values (6, 'Northglenn PD');
insert into Agency(AgencyID, AgencyName) values (7, 'Thornton PD');
insert into Agency(AgencyID, AgencyName) values (8, 'Westminster PD');
insert into Agency(AgencyID, AgencyName) values (9, 'Unknown');

-- BedType - adding code table values
insert into BedType (BedTypeID, BedTypeDescription) values('1','Type 1');
insert into BedType (BedTypeID, BedTypeDescription) values('2','Type 2');
insert into BedType (BedTypeID, BedTypeDescription) values('3','Type 3');
insert into BedType (BedTypeID, BedTypeDescription) values('4','Type 4');
insert into BedType (BedTypeID, BedTypeDescription) values('5','Type 5');
insert into BedType (BedTypeID, BedTypeDescription) values('6','Other');
insert into BedType (BedTypeID, BedTypeDescription) values('7','Unknown');

-- BehavioralHealthType - adding code table values
insert into BehavioralHealthType (BehavioralHealthTypeID, BehavioralHealthDescription) values('1','Disorder 1');
insert into BehavioralHealthType (BehavioralHealthTypeID, BehavioralHealthDescription) values('2','Disorder 2');
insert into BehavioralHealthType (BehavioralHealthTypeID, BehavioralHealthDescription) values('3','Disorder 3');
insert into BehavioralHealthType (BehavioralHealthTypeID, BehavioralHealthDescription) values('4','Illness 1');
insert into BehavioralHealthType (BehavioralHealthTypeID, BehavioralHealthDescription) values('5','Illness 2');
insert into BehavioralHealthType (BehavioralHealthTypeID, BehavioralHealthDescription) values('6','Schizophrenia 295.10');
insert into BehavioralHealthType (BehavioralHealthTypeID, BehavioralHealthDescription) values('7','Unknown');

-- BondType
insert into BondType (BondType) values('Cash');
insert into BondType (BondType) values('Surety');
insert into BondType (BondType) values('Property');
insert into BondType (BondType) values('Deposit');
insert into BondType (BondType) values('Unknown');

-- CaseStatus
insert into CaseStatus (CaseStatusID, CaseStatus) values('1','Pretrial');
insert into CaseStatus (CaseStatusID, CaseStatus) values('2','Status 2');
insert into CaseStatus (CaseStatusID, CaseStatus) values('3','Status 3');
insert into CaseStatus (CaseStatusID, CaseStatus) values('4','Status 4');
insert into CaseStatus (CaseStatusID, CaseStatus) values('5','Status 5');
insert into CaseStatus (CaseStatusID, CaseStatus) values('6','Unknown');

-- ChargeType
insert into ChargeType (ChargeTypeID, ChargeType) values('1','Felony');
insert into ChargeType (ChargeTypeID, ChargeType) values('2','Misdemeanor');
insert into ChargeType (ChargeTypeID, ChargeType) values('3','Municipal');
insert into ChargeType (ChargeTypeID, ChargeType) values('8','Other');
insert into ChargeType (ChargeTypeID, ChargeType) values('9','Unknown');

-- Jurisdiction
insert into Jurisdiction (JurisdictionID, JurisdictionName) values('1','State Court');
insert into Jurisdiction (JurisdictionID, JurisdictionName) values('2','County Court');
insert into Jurisdiction (JurisdictionID, JurisdictionName) values('3','Municipal Court');
insert into Jurisdiction (JurisdictionID, JurisdictionName) values('4','Case Court');
insert into Jurisdiction (JurisdictionID, JurisdictionName) values('9','Unknown');

-- HousingStatus
insert into HousingStatus (HousingStatusID, HousingStatusDescription) values('1','Residence');
insert into HousingStatus (HousingStatusID, HousingStatusDescription) values('2','Housing Status 2');
insert into HousingStatus (HousingStatusID, HousingStatusDescription) values('3','Housing Status 3');
insert into HousingStatus (HousingStatusID, HousingStatusDescription) values('4','Housing Status 4');
insert into HousingStatus (HousingStatusID, HousingStatusDescription) values('5','Housing Status 5');
insert into HousingStatus (HousingStatusID, HousingStatusDescription) values('6','Housing Status 6');
insert into HousingStatus (HousingStatusID, HousingStatusDescription) values('7','Housing Status 7');
insert into HousingStatus (HousingStatusID, HousingStatusDescription) values('8','Unknown');

-- IncomeLevel
insert into IncomeLevel (IncomeLevelID, IncomeLevel) values('1', 'Middle Class');
insert into IncomeLevel (IncomeLevelID, IncomeLevel) values('2', 'Level 2');
insert into IncomeLevel (IncomeLevelID, IncomeLevel) values('3', 'Level 3');
insert into IncomeLevel (IncomeLevelID, IncomeLevel) values('4', 'Level 4');
insert into IncomeLevel (IncomeLevelID, IncomeLevel) values('5', 'Level 5');
insert into IncomeLevel (IncomeLevelID, IncomeLevel) values('8', 'Other');
insert into IncomeLevel (IncomeLevelID, IncomeLevel) values('9', 'Unknown');

-- Occupation
insert into Occupation (OccupationID, Occupation) values('1', 'Truck Driver');
insert into Occupation (OccupationID, Occupation) values('2', 'Occupation 2');
insert into Occupation (OccupationID, Occupation) values('3', 'Occupation 3');
insert into Occupation (OccupationID, Occupation) values('4', 'Occupation 4');
insert into Occupation (OccupationID, Occupation) values('5', 'Occupation 5');
insert into Occupation (OccupationID, Occupation) values('6', 'Occupation 6');
insert into Occupation (OccupationID, Occupation) values('7', 'Occupation 7');
insert into Occupation (OccupationID, Occupation) values('8', 'Occupation 8');
insert into Occupation (OccupationID, Occupation) values('9', 'Occupation 9');
insert into Occupation (OccupationID, Occupation) values('10', 'Occupation 10');
insert into Occupation (OccupationID, Occupation) values('11', 'Occupation 11');
insert into Occupation (OccupationID, Occupation) values('12', 'Occupation 12');
insert into Occupation (OccupationID, Occupation) values('98', 'Other');
insert into Occupation (OccupationID, Occupation) values('99', 'Unknown');

-- EducationLevel
insert into EducationLevel (EducationLevelID, EducationLevel) values('1', 'High School Graduate');
insert into EducationLevel (EducationLevelID, EducationLevel) values('2', 'Level 2');
insert into EducationLevel (EducationLevelID, EducationLevel) values('3', 'Level 3');
insert into EducationLevel (EducationLevelID, EducationLevel) values('4', 'Level 4');
insert into EducationLevel (EducationLevelID, EducationLevel) values('5', 'Level 5');
insert into EducationLevel (EducationLevelID, EducationLevel) values('6', 'Level 6');
insert into EducationLevel (EducationLevelID, EducationLevel) values('7', 'Level 7');
insert into EducationLevel (EducationLevelID, EducationLevel) values('8', 'Other');
insert into EducationLevel (EducationLevelID, EducationLevel) values('9', 'Unknown');

-- Language
insert into Language (LanguageID, Language) values('1', 'English');
insert into Language (LanguageID, Language) values('2', 'Language 2');
insert into Language (LanguageID, Language) values('3', 'Language 3');
insert into Language (LanguageID, Language) values('98', 'Other');
insert into Language (LanguageID, Language) values('99', 'Unknown');

-- Facility
insert into Facility (FacilityName, Capacity) values('Adams County Jail', 3000);
insert into Facility (FacilityName, Capacity) values('Pima County Jail', 5000);