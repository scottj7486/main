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
drop database if exists ojbc_booking_staging;
CREATE DATABASE ojbc_booking_staging; 
use ojbc_booking_staging;

/**
* Copy DDL from SQL PA below here.  Modify timestamps in fact tables like this:
*                Timestamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
**/

CREATE TABLE MedicaidStatusType (
                MedicaidStatusTypeID INT NOT NULL,
                MedicaidStatusTypeDescription VARCHAR(50) NOT NULL,
                PRIMARY KEY (MedicaidStatusTypeID)
);


CREATE TABLE AssessmentCategoryType (
                AssessmentCategoryTypeID INT NOT NULL,
                AssessmentCategoryTypeDescription VARCHAR(50) NOT NULL,
                PRIMARY KEY (AssessmentCategoryTypeID)
);


CREATE TABLE BondStatusType (
                BondStatusTypeID INT NOT NULL,
                BondStatusTypeDescription VARCHAR(50) NOT NULL,
                PRIMARY KEY (BondStatusTypeID)
);


CREATE TABLE ChargeClassType (
                ChargeClassTypeID INT NOT NULL,
                ChargeClassTypeDescription VARCHAR(50) NOT NULL,
                PRIMARY KEY (ChargeClassTypeID)
);


CREATE TABLE TreatmentStatusType (
                TreatmentStatusTypeID INT NOT NULL,
                TreatmentStatusTypeDescription VARCHAR(50) NOT NULL,
                PRIMARY KEY (TreatmentStatusTypeID)
);


CREATE TABLE TreatmentAdmissionReasonType (
                TreatmentAdmissionReasonTypeID INT NOT NULL,
                TreatmentAdmissionReasonTypeDescription VARCHAR(50) NOT NULL,
                PRIMARY KEY (TreatmentAdmissionReasonTypeID)
);


CREATE TABLE SexOffenderStatusType (
                SexOffenderStatusTypeID INT NOT NULL,
                SexOffenderStatusTypeDescription VARCHAR(50) NOT NULL,
                PRIMARY KEY (SexOffenderStatusTypeID)
);


CREATE TABLE WorkReleaseStatusType (
                WorkReleaseStatusTypeID INT NOT NULL,
                WorkReleaseStatusTypeDescription VARCHAR(50) NOT NULL,
                PRIMARY KEY (WorkReleaseStatusTypeID)
);


CREATE TABLE ProgramEligibilityType (
                ProgramEligibilityTypeID INT NOT NULL,
                ProgramEligibilityTypeDescription VARCHAR(50) NOT NULL,
                PRIMARY KEY (ProgramEligibilityTypeID)
);


CREATE TABLE DomicileStatusType (
                DomicileStatusTypeID INT NOT NULL,
                DomicileStatusTypeDescription VARCHAR(50) NOT NULL,
                PRIMARY KEY (DomicileStatusTypeID)
);


CREATE TABLE PersonEthnicityType (
                PersonEthnicityTypeID INT NOT NULL,
                PersonEthnicityTypeDescription VARCHAR(50) NOT NULL,
                PRIMARY KEY (PersonEthnicityTypeID)
);


CREATE TABLE MilitaryServiceStatusType (
                MilitaryServiceStatusTypeID INT NOT NULL,
                MilitaryServiceStatusTypeDescription VARCHAR(100) NOT NULL,
                PRIMARY KEY (MilitaryServiceStatusTypeID)
);


CREATE TABLE Location (
                LocationID INT AUTO_INCREMENT NOT NULL,
                AddressSecondaryUnit VARCHAR(150),
                StreetNumber VARCHAR(50),
                StreetName VARCHAR(150),
                City VARCHAR(100),
                State VARCHAR(10),
                PostalCode VARCHAR(10),
                LocationLatitude NUMERIC(14,10),
                LocationLongitude NUMERIC(14,10),
                LocationTimestamp Timestamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                PRIMARY KEY (LocationID)
);


CREATE TABLE LanguageType (
                LanguageTypeID INT NOT NULL,
                LanguageTypeDescription VARCHAR(50) NOT NULL,
                PRIMARY KEY (LanguageTypeID)
);


CREATE TABLE Facility (
                FacilityID INT NOT NULL,
                FacilityDescription VARCHAR(100) NOT NULL,
                Capacity INT DEFAULT 0 NOT NULL,
                PRIMARY KEY (FacilityID)
);

ALTER TABLE Facility COMMENT 'Booking Detention Facility';


CREATE TABLE BondType (
                BondTypeID INT NOT NULL,
                BondTypeDescription VARCHAR(100) NOT NULL,
                PRIMARY KEY (BondTypeID)
);


CREATE TABLE SupervisionUnitType (
                SupervisionUnitTypeID INT NOT NULL,
                SupervisionUnitTypeDescription VARCHAR(50) NOT NULL,
                PRIMARY KEY (SupervisionUnitTypeID)
);


CREATE TABLE PersonRaceType (
                PersonRaceTypeID INT NOT NULL,
                PersonRaceTypeDescription VARCHAR(50) NOT NULL,
                PRIMARY KEY (PersonRaceTypeID)
);


CREATE TABLE PersonSexType (
                PersonSexTypeID INT NOT NULL,
                PersonSexTypeDescription VARCHAR(7) NOT NULL,
                PRIMARY KEY (PersonSexTypeID)
);


CREATE TABLE Person (
                PersonID INT AUTO_INCREMENT NOT NULL,
                PersonUniqueIdentifier VARCHAR(100) NOT NULL,
                PersonUniqueIdentifier2 VARCHAR(100),
                PersonAgeAtEvent INT,
                PersonBirthDate DATE,
                EducationLevel VARCHAR(50),
                Occupation VARCHAR(50),
                LanguageTypeID INT,
                PersonSexTypeID INT,
                PersonRaceTypeID INT,
                PersonEthnicityTypeID INT,
                MilitaryServiceStatusTypeID INT,
                DomicileStatusTypeID INT,
                ProgramEligibilityTypeID INT,
                WorkReleaseStatusTypeID INT,
                SexOffenderStatusTypeID INT,
                PersonTimestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                PRIMARY KEY (PersonID)
);


CREATE TABLE Incident (
                IncidentID INT NOT NULL,
                IncidentNumber VARCHAR(40) NOT NULL,
                ReportingAgency VARCHAR(80),
                PersonID INT NOT NULL,
                IncidentReportedDate DATE,
                IncidentReportedTime TIME,
                IncidentTimeSpanText VARCHAR(20),
                OfficerCount INT,
                TotalOfficerTimeSeconds INT,
                DispositionLocation VARCHAR(120),
                CallNature VARCHAR(120),
                PendingCriminalCharges VARCHAR(100),
                LocationID INT,
                SubstanceAbuseInvolvementIndicator BOOLEAN,
                CrisisInterventionTeamInvolvementIndicator BOOLEAN,
                OffenseSeverityText VARCHAR(20),
                IncidentTimestamp DATETIME DEFAULT now() NOT NULL,
                PRIMARY KEY (IncidentID)
);


CREATE TABLE IncidentResponseUnit (
                IncidentResponseUnitID INT NOT NULL,
                IncidentID INT NOT NULL,
                UnitIdentifier VARCHAR(20) NOT NULL,
                UnitArrivalDate DATE,
                UnitArrivalTime TIME,
                UnitClearDate DATE,
                UnitClearTime TIME,
                IncidentResponseUnitTimestamp DATETIME DEFAULT now() NOT NULL,
                PRIMARY KEY (IncidentResponseUnitID)
);


CREATE TABLE BehavioralHealthAssessment (
                BehavioralHealthAssessmentID INT AUTO_INCREMENT NOT NULL,
                PersonID INT NOT NULL,
                SeriousMentalIllnessIndicator BOOLEAN,
                CareEpisodeStartDate DATE,
                CareEpisodeEndDate DATE,
                MedicaidStatusTypeID INT,
                EnrolledProviderName VARCHAR(100),
                BehavioralHealthAssessmentTimestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                PRIMARY KEY (BehavioralHealthAssessmentID)
);


CREATE TABLE BehavioralHealthCategory (
                BehavioralHealthCategoryOD INT AUTO_INCREMENT NOT NULL,
                BehavioralHealthAssessmentID INT NOT NULL,
                BehavioralHealthCategoryText VARCHAR(100) NOT NULL,
                BehavioralHealthCategoryTimestamp DATETIME DEFAULT now() NOT NULL,
                PRIMARY KEY (BehavioralHealthCategoryOD)
);


CREATE TABLE BehavioralHealthAssessmentCategory (
                BehavioralHealthAssessmentCategoryID INT AUTO_INCREMENT NOT NULL,
                BehavioralHealthAssessmentID INT NOT NULL,
                AssessmentCategoryTypeID INT NOT NULL,
                BehavioralHealthAssessmentCategoryTimestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                PRIMARY KEY (BehavioralHealthAssessmentCategoryID)
);


CREATE TABLE PrescribedMedication (
                PrescribedMedicationID INT AUTO_INCREMENT NOT NULL,
                BehavioralHealthAssessmentID INT NOT NULL,
                MedicationDescription VARCHAR(80),
                MedicationDispensingDate DATE,
                MedicationDoseMeasure VARCHAR(10),
                PrescribedMedicationTimestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                PRIMARY KEY (PrescribedMedicationID)
);


CREATE TABLE Treatment (
                TreatmentID INT AUTO_INCREMENT NOT NULL,
                BehavioralHealthAssessmentID INT NOT NULL,
                TreatmentStartDate DATE,
                TreatmentAdmissionReasonTypeID INT,
                TreatmentStatusTypeID INT,
                TreatmentProviderName VARCHAR(100),
                TreatmentTimestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                PRIMARY KEY (TreatmentID)
);


CREATE TABLE BehavioralHealthEvaluation (
                BehavioralHealthEvaluationID INT AUTO_INCREMENT NOT NULL,
                BehavioralHealthAssessmentID INT NOT NULL,
                BehavioralHealthDiagnosisDescription VARCHAR(100),
                BehavioralHealthEvaluationTimestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                PRIMARY KEY (BehavioralHealthEvaluationID)
);


CREATE TABLE JurisdictionType (
                JurisdictionTypeID INT NOT NULL,
                JurisdictionTypeDescription VARCHAR(100) NOT NULL,
                PRIMARY KEY (JurisdictionTypeID)
);


CREATE TABLE Agency (
                AgencyID INT NOT NULL,
                AgencyDescription VARCHAR(50) NOT NULL,
                PRIMARY KEY (AgencyID)
);


CREATE TABLE Booking (
                BookingID INT AUTO_INCREMENT NOT NULL,
                BookingNumber VARCHAR(100) NOT NULL,
                PersonID INT NOT NULL,
                BookingDate DATE NOT NULL,
                BookingTime TIME,
                ScheduledReleaseDate DATE,
                FacilityID INT,
                SupervisionUnitTypeID INT,
                InmateJailResidentIndicator BOOLEAN,
                InmateCurrentLocation VARCHAR(100),
                BookingStatus VARCHAR(20) NOT NULL,
                BookingTimestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                PRIMARY KEY (BookingID)
);


CREATE TABLE CustodyRelease (
                CustodyReleaseID INT AUTO_INCREMENT NOT NULL,
                BookingID INT,
                BookingNumber VARCHAR(100) NOT NULL,
                ReleaseDate DATE NOT NULL,
                ReleaseTime TIME,
                ReleaseCondition VARCHAR(200),
                BookingStatus VARCHAR(20),
				CustodyReleaseTimestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                PRIMARY KEY (CustodyReleaseID)
);


CREATE TABLE CustodyStatusChange (
                CustodyStatusChangeID INT AUTO_INCREMENT NOT NULL,
                BookingID INT,
                PersonID INT NOT NULL,
                BookingDate DATE NOT NULL,
                BookingTime TIME,
                BookingNumber VARCHAR(100) NOT NULL,
                ScheduledReleaseDate DATE,
                FacilityID INT,
                SupervisionUnitTypeID INT,
                InmateJailResidentIndicator BOOLEAN,
                InmateCurrentLocation VARCHAR(100),
                BookingStatus VARCHAR(20),
				CustodyStatusChangeTimestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                PRIMARY KEY (CustodyStatusChangeID)
);


CREATE TABLE CustodyStatusChangeArrest (
                CustodyStatusChangeArrestID INT AUTO_INCREMENT NOT NULL,
                CustodyStatusChangeID INT NOT NULL,
                LocationID INT,
                ArrestAgencyID INT,
                CustodyStatusChangeArrestTimestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                PRIMARY KEY (CustodyStatusChangeArrestID)
);


CREATE TABLE CustodyStatusChangeCharge (
                CustodyStatusChangeChargeID INT AUTO_INCREMENT NOT NULL,
                CustodyStatusChangeArrestID INT NOT NULL,
                ChargeCode VARCHAR(100),
                ChargeDisposition VARCHAR(100),
                AgencyID INT,
                BondTypeID INT,
                BondAmount NUMERIC(10,2),
                BondRemainingAmount NUMERIC(10,2),
                ChargeJurisdictionTypeID INT,
                ChargeClassTypeID INT,
                BondStatusTypeID INT,
                CustodyStatusChangeChargeTimestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                PRIMARY KEY (CustodyStatusChangeChargeID)
);


CREATE TABLE BookingArrest (
                BookingArrestID INT AUTO_INCREMENT NOT NULL,
                BookingID INT NOT NULL,
                LocationID INT,
                ArrestAgencyID INT,
                BookingArrestTimestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                PRIMARY KEY (BookingArrestID)
);


CREATE TABLE BookingCharge (
                BookingChargeID INT AUTO_INCREMENT NOT NULL,
                BookingArrestID INT NOT NULL,
                ChargeCode VARCHAR(100),
                ChargeDisposition VARCHAR(100),
                AgencyID INT,
                BondTypeID INT,
                BondAmount NUMERIC(10,2),
                ChargeJurisdictionTypeID INT,
                ChargeClassTypeID INT,
                BondStatusTypeID INT,
                BookingChargeTimestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                PRIMARY KEY (BookingChargeID)
);


ALTER TABLE BehavioralHealthAssessment ADD CONSTRAINT medicaidstatustype_behavioralhealthassessment_fk
FOREIGN KEY (MedicaidStatusTypeID)
REFERENCES MedicaidStatusType (MedicaidStatusTypeID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE BehavioralHealthAssessmentCategory ADD CONSTRAINT assessmentcategorytype_behavioralhealthassessmentcategory_fk
FOREIGN KEY (AssessmentCategoryTypeID)
REFERENCES AssessmentCategoryType (AssessmentCategoryTypeID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE CustodyStatusChangeCharge ADD CONSTRAINT bondstatustype_custodystatuschangecharge_fk
FOREIGN KEY (BondStatusTypeID)
REFERENCES BondStatusType (BondStatusTypeID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE BookingCharge ADD CONSTRAINT bondstatustype_bookingcharge_fk
FOREIGN KEY (BondStatusTypeID)
REFERENCES BondStatusType (BondStatusTypeID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE BookingCharge ADD CONSTRAINT chargeclasstype_bookingcharge_fk
FOREIGN KEY (ChargeClassTypeID)
REFERENCES ChargeClassType (ChargeClassTypeID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE CustodyStatusChangeCharge ADD CONSTRAINT chargeclasstype_custodystatuschangecharge_fk
FOREIGN KEY (ChargeClassTypeID)
REFERENCES ChargeClassType (ChargeClassTypeID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Treatment ADD CONSTRAINT treatmentstatustype_treatment_fk
FOREIGN KEY (TreatmentStatusTypeID)
REFERENCES TreatmentStatusType (TreatmentStatusTypeID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Treatment ADD CONSTRAINT treatmentinitiationtype_treatment_fk
FOREIGN KEY (TreatmentAdmissionReasonTypeID)
REFERENCES TreatmentAdmissionReasonType (TreatmentAdmissionReasonTypeID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Person ADD CONSTRAINT sexoffenderregistrationstatustype_person_fk
FOREIGN KEY (SexOffenderStatusTypeID)
REFERENCES SexOffenderStatusType (SexOffenderStatusTypeID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Person ADD CONSTRAINT workreleasestatustype_person_fk
FOREIGN KEY (WorkReleaseStatusTypeID)
REFERENCES WorkReleaseStatusType (WorkReleaseStatusTypeID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Person ADD CONSTRAINT programeligibilitytype_person_fk
FOREIGN KEY (ProgramEligibilityTypeID)
REFERENCES ProgramEligibilityType (ProgramEligibilityTypeID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Person ADD CONSTRAINT domicilestatustype_person_fk
FOREIGN KEY (DomicileStatusTypeID)
REFERENCES DomicileStatusType (DomicileStatusTypeID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Person ADD CONSTRAINT personethnicitytype_person_fk
FOREIGN KEY (PersonEthnicityTypeID)
REFERENCES PersonEthnicityType (PersonEthnicityTypeID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Person ADD CONSTRAINT militaryservicestatustype_person_fk
FOREIGN KEY (MilitaryServiceStatusTypeID)
REFERENCES MilitaryServiceStatusType (MilitaryServiceStatusTypeID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE BookingArrest ADD CONSTRAINT location_bookingarrest_fk
FOREIGN KEY (LocationID)
REFERENCES Location (LocationID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE CustodyStatusChangeArrest ADD CONSTRAINT location_custodystatuschangearrest_fk
FOREIGN KEY (LocationID)
REFERENCES Location (LocationID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Incident ADD CONSTRAINT location_incident_fk
FOREIGN KEY (LocationID)
REFERENCES Location (LocationID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Person ADD CONSTRAINT language_person_fk
FOREIGN KEY (LanguageTypeID)
REFERENCES LanguageType (LanguageTypeID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Booking ADD CONSTRAINT facility_booking_fk
FOREIGN KEY (FacilityID)
REFERENCES Facility (FacilityID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE CustodyStatusChange ADD CONSTRAINT facility_custody_status_change_fk
FOREIGN KEY (FacilityID)
REFERENCES Facility (FacilityID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE BookingCharge ADD CONSTRAINT bondtype_bookingcharge_fk
FOREIGN KEY (BondTypeID)
REFERENCES BondType (BondTypeID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE CustodyStatusChangeCharge ADD CONSTRAINT bondtype_custodystatuschangecharge_fk
FOREIGN KEY (BondTypeID)
REFERENCES BondType (BondTypeID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Booking ADD CONSTRAINT bedtype_booking_fk
FOREIGN KEY (SupervisionUnitTypeID)
REFERENCES SupervisionUnitType (SupervisionUnitTypeID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE CustodyStatusChange ADD CONSTRAINT bedtype_custody_status_change_fk
FOREIGN KEY (SupervisionUnitTypeID)
REFERENCES SupervisionUnitType (SupervisionUnitTypeID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Person ADD CONSTRAINT personrace_person_fk
FOREIGN KEY (PersonRaceTypeID)
REFERENCES PersonRaceType (PersonRaceTypeID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Person ADD CONSTRAINT personsex_person_fk
FOREIGN KEY (PersonSexTypeID)
REFERENCES PersonSexType (PersonSexTypeID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE BehavioralHealthAssessment ADD CONSTRAINT person_behaviorhealthassessment_fk
FOREIGN KEY (PersonID)
REFERENCES Person (PersonID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Booking ADD CONSTRAINT person_booking_fk
FOREIGN KEY (PersonID)
REFERENCES Person (PersonID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE CustodyStatusChange ADD CONSTRAINT person_custodystatuschange_fk
FOREIGN KEY (PersonID)
REFERENCES Person (PersonID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Incident ADD CONSTRAINT person_incident_fk
FOREIGN KEY (PersonID)
REFERENCES Person (PersonID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE IncidentResponseUnit ADD CONSTRAINT incident_incidentresponseunit_fk
FOREIGN KEY (IncidentID)
REFERENCES Incident (IncidentID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE BehavioralHealthEvaluation ADD CONSTRAINT behavioralhealthassessment_behavioralhealthevaluation_fk
FOREIGN KEY (BehavioralHealthAssessmentID)
REFERENCES BehavioralHealthAssessment (BehavioralHealthAssessmentID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Treatment ADD CONSTRAINT behavioralhealthassessment_treatment_fk
FOREIGN KEY (BehavioralHealthAssessmentID)
REFERENCES BehavioralHealthAssessment (BehavioralHealthAssessmentID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PrescribedMedication ADD CONSTRAINT behavioralhealthassessment_prescribedmedication_fk
FOREIGN KEY (BehavioralHealthAssessmentID)
REFERENCES BehavioralHealthAssessment (BehavioralHealthAssessmentID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE BehavioralHealthAssessmentCategory ADD CONSTRAINT behavioralhealthassessment_behavioralhealthassessmentcategory_fk
FOREIGN KEY (BehavioralHealthAssessmentID)
REFERENCES BehavioralHealthAssessment (BehavioralHealthAssessmentID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE BehavioralHealthCategory ADD CONSTRAINT behavioralhealthassessment_behavioralhealthcategory_fk
FOREIGN KEY (BehavioralHealthAssessmentID)
REFERENCES BehavioralHealthAssessment (BehavioralHealthAssessmentID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE BookingCharge ADD CONSTRAINT jurisdictiontype_bookingcharge_fk
FOREIGN KEY (ChargeJurisdictionTypeID)
REFERENCES JurisdictionType (JurisdictionTypeID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE CustodyStatusChangeCharge ADD CONSTRAINT jurisdictiontype_custodystatuschangecharge_fk
FOREIGN KEY (ChargeJurisdictionTypeID)
REFERENCES JurisdictionType (JurisdictionTypeID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE BookingCharge ADD CONSTRAINT agency_bookingcharge_fk
FOREIGN KEY (AgencyID)
REFERENCES Agency (AgencyID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE CustodyStatusChangeCharge ADD CONSTRAINT agency_custodystatuschangecharge_fk
FOREIGN KEY (AgencyID)
REFERENCES Agency (AgencyID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE BookingArrest ADD CONSTRAINT agencytype_bookingarrest_fk
FOREIGN KEY (ArrestAgencyID)
REFERENCES Agency (AgencyID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE CustodyStatusChangeArrest ADD CONSTRAINT agencytype_custodystatuschangearrest_fk
FOREIGN KEY (ArrestAgencyID)
REFERENCES Agency (AgencyID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE BookingArrest ADD CONSTRAINT booking_bookingarrest_fk
FOREIGN KEY (BookingID)
REFERENCES Booking (BookingID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE CustodyStatusChange ADD CONSTRAINT booking_custodystatuschange_fk
FOREIGN KEY (BookingID)
REFERENCES Booking (BookingID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE CustodyRelease ADD CONSTRAINT booking_custodyrelease_fk
FOREIGN KEY (BookingID)
REFERENCES Booking (BookingID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE CustodyStatusChangeArrest ADD CONSTRAINT custodystatuschange_custodystatuschangearrest_fk
FOREIGN KEY (CustodyStatusChangeID)
REFERENCES CustodyStatusChange (CustodyStatusChangeID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE CustodyStatusChangeCharge ADD CONSTRAINT custodystatuschangearrest_custodystatuschangecharge_fk
FOREIGN KEY (CustodyStatusChangeArrestID)
REFERENCES CustodyStatusChangeArrest (CustodyStatusChangeArrestID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE BookingCharge ADD CONSTRAINT bookingarrest_bookingcharge_fk
FOREIGN KEY (BookingArrestID)
REFERENCES BookingArrest (BookingArrestID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

CREATE INDEX idx_Booking_BookingNumber  ON Booking (BookingNumber)  ALGORITHM DEFAULT LOCK DEFAULT;

CREATE INDEX idx_CustodyRelease_BookingNumber  ON CustodyRelease (BookingNumber)  ALGORITHM DEFAULT LOCK DEFAULT;

CREATE INDEX idx_CustodyStatusChange_BookingNumber  ON CustodyStatusChange (BookingNumber)  ALGORITHM DEFAULT LOCK DEFAULT;

CREATE INDEX idx_Person_PersonUniqueIdentifier  ON Person (PersonUniqueIdentifier)  ALGORITHM DEFAULT LOCK DEFAULT;

CREATE INDEX idx_Person_PersonUniqueIdentifier2  ON Person (PersonUniqueIdentifier2)  ALGORITHM DEFAULT LOCK DEFAULT;
