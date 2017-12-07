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






CREATE TABLE PRINT_RESULTS (
                PRINT_RESULTS_ID IDENTITY NOT NULL,
                SYSTEM_NAME VARCHAR(200),
                DESCRIPTION VARCHAR(200),
                MESSAGE_ID VARCHAR(50) NOT NULL,
                TIMESTAMP TIMESTAMP DEFAULT CURRENT_TIMESTAMP() NOT NULL,
                CONSTRAINT PRINT_RESULTS_ID PRIMARY KEY (PRINT_RESULTS_ID)
);

CREATE TABLE IDENTIFICATION_SEARCH_REASON_CODE (
                IDENTIFICATION_SEARCH_REASON_CODE_ID IDENTITY NOT NULL,
                IDENTIFICATION_REASON_CODE_DESCRIPTION VARCHAR(80) NOT NULL,
                CONSTRAINT IDENTIFICATION_SEARCH_REASON_CODE_ID PRIMARY KEY (IDENTIFICATION_SEARCH_REASON_CODE_ID)
);

CREATE TABLE FEDERAL_RAPBACK_SUBSCRIPTION (
                FEDERAL_RAPBACK_SUBSCRIPTION_ID IDENTITY NOT NULL,
                TRANSACTION_CONTROL_REFERENCE_IDENTIFICATION VARCHAR(100),
                TRANSACTION_CATEGORY_CODE VARCHAR(20),
                PATH_TO_REQUEST_FILE VARCHAR(200),
                PATH_TO_RESPONSE_FILE VARCHAR(200),
                REQUEST_SENT_TIMESTAMP TIMESTAMP,
                RESPONSE_RECIEVED_TIMESTAMP TIMESTAMP,
                CONSTRAINT FEDERAL_RAPBACK_SUBSCRIPTION_ID PRIMARY KEY (FEDERAL_RAPBACK_SUBSCRIPTION_ID)
);

CREATE TABLE SYSTEMS_TO_SEARCH (
                SYSTEMS_TO_SEARCH_ID IDENTITY NOT NULL,
                SYSTEM_NAME VARCHAR(80) NOT NULL,
                SYSTEM_URI VARCHAR(200) NOT NULL,
                CONSTRAINT SYSTEMS_TO_SEARCH_ID PRIMARY KEY (SYSTEMS_TO_SEARCH_ID)
);

CREATE TABLE USER_INFO (
                USER_INFO_ID IDENTITY NOT NULL,
                FEDERATION_ID VARCHAR(100),
                EMPLOYER_NAME VARCHAR(100),
                EMPLOYER_SUBUNIT_NAME VARCHAR(100),
                USER_FIRST_NAME VARCHAR(100),
                USER_LAST_NAME VARCHAR(100),
                USER_EMAIL_ADDRESS VARCHAR(100),
                IDENTITY_PROVIDER_ID VARCHAR(100),
                CONSTRAINT USER_INFO_ID PRIMARY KEY (USER_INFO_ID)
);

CREATE TABLE USER_LOGIN (
                USER_LOGIN_ID IDENTITY NOT NULL,
                USER_INFO_ID INTEGER NOT NULL,
                ACTION VARCHAR NOT NULL,
                TIMESTAMP TIMESTAMP DEFAULT CURRENT_TIMESTAMP() NOT NULL,
                CONSTRAINT USER_LOGIN_ID PRIMARY KEY (USER_LOGIN_ID)
);

CREATE TABLE IDENTIFICATION_SEARCH_REQUEST (
                IDENTIFICATION_SEARCH_REQUEST_ID IDENTITY NOT NULL,
                FIRST_NAME VARCHAR(80),
                LAST_NAME VARCHAR(80),
                IDENTIFICATION_RESULTS_STATUS VARCHAR(80),
                REPORTED_FROM_DATE DATE,
                REPORTED_TO_DATE DATE,
                OTN VARCHAR(80),
                MESSAGE_ID VARCHAR(50) NOT NULL,
                USER_INFO_ID INTEGER,
                TIMESTAMP TIMESTAMP DEFAULT CURRENT_TIMESTAMP() NOT NULL,
                CONSTRAINT IDENTIFICATION_SEARCH_REQUEST_ID PRIMARY KEY (IDENTIFICATION_SEARCH_REQUEST_ID)
);

CREATE TABLE IDENTIFICATION_REASON_CODE_JOINER (
                IDENTIFICATION_REASON_CODE_JOINER_ID IDENTITY NOT NULL,
                IDENTIFICATION_SEARCH_REASON_CODE_ID INTEGER NOT NULL,
                IDENTIFICATION_SEARCH_REQUEST_ID INTEGER NOT NULL,
                CONSTRAINT IDENTIFICATION_REASON_CODE_JOINER_ID PRIMARY KEY (IDENTIFICATION_REASON_CODE_JOINER_ID)
);

CREATE TABLE IDENTIFICATION_SEARCH_RESULTS (
                IDENTIFICATION_SEARCH_RESULTS_ID IDENTITY NOT NULL,
                IDENTIFICATION_SEARCH_REQUEST_ID INTEGER NOT NULL,
                AVAILABLE_RESULTS INTEGER NOT NULL,
                MESSAGE_ID VARCHAR(50) NOT NULL,
                TIMESTAMP TIMESTAMP DEFAULT CURRENT_TIMESTAMP() NOT NULL,
                CONSTRAINT IDENTIFICATION_SEARCH_RESULTS_ID PRIMARY KEY (IDENTIFICATION_SEARCH_RESULTS_ID)
);

CREATE TABLE QUERY_REQUEST (
                QUERY_REQUEST_ID IDENTITY NOT NULL,
                IDENTIFICATION_ID VARCHAR(80) NOT NULL,
                SYSTEM_NAME VARCHAR(80) NOT NULL,
                MESSAGE_ID VARCHAR(50) NOT NULL,
                USER_INFO_ID INTEGER,
                TIMESTAMP TIMESTAMP DEFAULT CURRENT_TIMESTAMP() NOT NULL,
                CONSTRAINT QUERY_REQUEST_ID PRIMARY KEY (QUERY_REQUEST_ID)
);

CREATE TABLE FIREARMS_QUERY_RESULTS (
                FIREARMS_QUERY_RESULTS_ID IDENTITY NOT NULL,
                QUERY_REQUEST_ID INTEGER NOT NULL,
                SYSTEM_NAME VARCHAR(80) NOT NULL,
                QUERY_RESULTS_ERROR_INDICATOR BOOLEAN,
                QUERY_RESULTS_ERROR_TEXT VARCHAR(300),
                MESSAGE_ID VARCHAR(50) NOT NULL,
                QUERY_RESULTS_TIMEOUT_INDICATOR BOOLEAN,
                TIMESTAMP TIMESTAMP DEFAULT CURRENT_TIMESTAMP() NOT NULL,
                CONSTRAINT FIREARMS_QUERY_RESULTS_ID PRIMARY KEY (FIREARMS_QUERY_RESULTS_ID)
);

CREATE TABLE IDENTIFICATION_RESULTS_QUERY_DETAIL (
                IDENTIFICATION_RESULTS_QUERY_DETAIL_ID IDENTITY NOT NULL,
                QUERY_REQUEST_ID INTEGER NOT NULL,
                STATE_PERSON_LAST_NAME VARCHAR(80),
                STATE_PERSON_MIDDLE_NAME VARCHAR(80),
                STATE_PERSON_FIRST_NAME VARCHAR(80),
                STATE_SID VARCHAR(80),
                STATE_OCA VARCHAR(80),
                STATE_ID_DATE DATE NOT NULL,
                STATE_OTN VARCHAR(80),
                FEDERAL_FBI_ID VARCHAR(80),
                TIMESTAMP TIMESTAMP DEFAULT CURRENT_TIMESTAMP() NOT NULL,
                CONSTRAINT IDENTIFICATION_RESULTS_QUERY_DETAIL_ID PRIMARY KEY (IDENTIFICATION_RESULTS_QUERY_DETAIL_ID)
);

CREATE TABLE WARRANT_QUERY_RESULTS (
                WARRANT_QUERY_RESULTS_ID IDENTITY NOT NULL,
                QUERY_REQUEST_ID INTEGER NOT NULL,
                SYSTEM_NAME VARCHAR(80) NOT NULL,
                FIRST_NAME VARCHAR(80),
                MIDDLE_NAME VARCHAR(80),
                LAST_NAME VARCHAR(80),
                FBI_ID VARCHAR(20),
                SID VARCHAR(20),
                QUERY_RESULTS_ERROR_INDICATOR BOOLEAN,
                QUERY_RESULTS_ACCESS_DENIED_INDICATOR BOOLEAN,
                QUERY_RESULTS_ERROR_TEXT VARCHAR(300),
                MESSAGE_ID VARCHAR(50) NOT NULL,
                QUERY_RESULTS_TIMEOUT_INDICATOR BOOLEAN,
                TIMESTAMP TIMESTAMP DEFAULT CURRENT_TIMESTAMP() NOT NULL,
                CONSTRAINT WARRANT_QUERY_RESULTS_ID PRIMARY KEY (WARRANT_QUERY_RESULTS_ID)
);

CREATE TABLE CRIMINAL_HISTORY_QUERY_RESULTS (
                CRIMINAL_HISTORY_QUERY_RESULTS_ID IDENTITY NOT NULL,
                QUERY_REQUEST_ID INTEGER NOT NULL,
                SYSTEM_NAME VARCHAR(80) NOT NULL,
                FIRST_NAME VARCHAR(80),
                MIDDLE_NAME VARCHAR(80),
                LAST_NAME VARCHAR(80),
                FBI_ID VARCHAR(20),
                SID VARCHAR(20),
                QUERY_RESULTS_ERROR_INDICATOR BOOLEAN,
                QUERY_RESULTS_ERROR_TEXT VARCHAR(300),
                MESSAGE_ID VARCHAR(50) NOT NULL,
                QUERY_RESULTS_TIMEOUT_INDICATOR BOOLEAN,
                TIMESTAMP TIMESTAMP DEFAULT CURRENT_TIMESTAMP() NOT NULL,
                CONSTRAINT CRIMINAL_HISTORY_QUERY_RESULTS_ID PRIMARY KEY (CRIMINAL_HISTORY_QUERY_RESULTS_ID)
);

CREATE TABLE SEARCH_QUALIFIER_CODES (
                SEARCH_QUALIFIER_CODES_ID IDENTITY NOT NULL,
                CODE_NAME VARCHAR(80),
                CONSTRAINT SEARCH_QUALIFIER_CODES_ID PRIMARY KEY (SEARCH_QUALIFIER_CODES_ID)
);

CREATE TABLE PERSON_SEARCH_REQUEST (
                PERSON_SEARCH_REQUEST_ID IDENTITY NOT NULL,
                FIRST_NAME VARCHAR(80),
                FIRST_NAME_QUALIFIER_CODE_ID INTEGER,
                MIDDLE_NAME VARCHAR(80),
                LAST_NAME VARCHAR(80),
                LAST_NAME_QUALIFIER_CODE_ID INTEGER,
                FBI_ID VARCHAR(20),
                SID VARCHAR(20),
                DOB_START_DATE DATE,
                DOB_END_DATE DATE,
                DRIVERS_LICENSE_ISSUER VARCHAR(20),
                DRIVERS_LICENSE_NUMBER VARCHAR(30),
                RACE VARCHAR(30),
                GENDER VARCHAR(15),
                EYE_COLOR VARCHAR(20),
                PURPOSE VARCHAR(80),
                ON_BEHALF_OF VARCHAR(80),
                HAIR_COLOR VARCHAR(20),
                MESSAGE_ID VARCHAR(50) NOT NULL,
                USER_INFO_ID INTEGER,
                TIMESTAMP TIMESTAMP DEFAULT CURRENT_TIMESTAMP() NOT NULL,
                CONSTRAINT PERSON_SEARCH_REQUEST_ID PRIMARY KEY (PERSON_SEARCH_REQUEST_ID)
);

CREATE TABLE PERSON_SYSTEMS_TO_SEARCH (
                PERSON_SYSTEMS_TO_SEARCH_ID IDENTITY NOT NULL,
                SYSTEMS_TO_SEARCH_ID INTEGER NOT NULL,
                PERSON_SEARCH_REQUEST_ID INTEGER NOT NULL,
                CONSTRAINT PERSON_SYSTEMS_TO_SEARCH_ID PRIMARY KEY (PERSON_SYSTEMS_TO_SEARCH_ID)
);

CREATE TABLE PERSON_SEARCH_RESULTS (
                PERSON_SEARCH_RESULTS_ID IDENTITY NOT NULL,
                PERSON_SEARCH_REQUEST_ID INTEGER NOT NULL,
                SYSTEMS_TO_SEARCH_ID INTEGER,
                SEARCH_RESULTS_COUNT INTEGER,
                SEARCH_RESULTS_ERROR_INDICATOR BOOLEAN,
                SEARCH_RESULTS_ACCESS_DENIED_INDICATOR BOOLEAN,
                SEARCH_RESULTS_ERROR_TEXT VARCHAR(300),
                SEARCH_RESULTS_TIMEOUT_INDICATOR BOOLEAN,
                TIMESTAMP TIMESTAMP DEFAULT CURRENT_TIMESTAMP() NOT NULL,
                CONSTRAINT PERSON_SEARCH_RESULTS_ID PRIMARY KEY (PERSON_SEARCH_RESULTS_ID)
);

CREATE TABLE NOTIFICATIONS_SENT (
                NOTIFICATIONS_SENT_ID IDENTITY NOT NULL,
                SUBSCRIPTION_TYPE VARCHAR(80) NOT NULL,
                TOPIC VARCHAR(100) NOT NULL,
                SUBSCRIPTION_IDENTIFIER INTEGER NOT NULL,
                SUBSCRIPTION_OWNER_AGENCY VARCHAR(80),
                SUBSCRIPTION_OWNER_AGENCY_TYPE VARCHAR(80),
                SUBSCRIPTION_OWNER_EMAIL_ADDRESS VARCHAR(80),
                SUBSCRIPTION_OWNER VARCHAR(80) NOT NULL,
                NOTIFYING_SYSTEM_NAME VARCHAR(80) NOT NULL,
                SUBSCRIBING_SYSTEM_IDENTIFIER VARCHAR(80) NOT NULL,
                TIMESTAMP TIMESTAMP DEFAULT CURRENT_TIMESTAMP() NOT NULL,
                CONSTRAINT NOTIFICATIONS_SENT_ID PRIMARY KEY (NOTIFICATIONS_SENT_ID)
);

CREATE TABLE NOTIFICATION_MECHANISM (
                NOTIFICATION_MECHANISM_ID IDENTITY NOT NULL,
                NOTIFICATIONS_SENT_ID INTEGER NOT NULL,
                NOTIFICATION_MECHANSIM VARCHAR(80) NOT NULL,
                NOTIFICATION_ADDRESS VARCHAR(80) NOT NULL,
                NOTIFICATION_RECIPIENT_TYPE VARCHAR(10) NOT NULL,
                CONSTRAINT NOTIFICATION_MECHANISM_ID PRIMARY KEY (NOTIFICATION_MECHANISM_ID)
);

CREATE TABLE NOTIFICATION_PROPERTIES (
                NOTIFICATION_PROPERTIES_ID IDENTITY NOT NULL,
                NOTIFICATIONS_SENT_ID INTEGER NOT NULL,
                PROPERTY_NAME VARCHAR(100) NOT NULL,
                PROPERTY_VALUE VARCHAR(100) NOT NULL,
                CONSTRAINT NOTIFICATION_PROPERTIES_pk PRIMARY KEY (NOTIFICATION_PROPERTIES_ID)
);

ALTER TABLE IDENTIFICATION_REASON_CODE_JOINER ADD CONSTRAINT IDENTIFICATION_SEARCH_REASON_CODE_IDENTIFICATION_REASON_CODE_JOINER_fk
FOREIGN KEY (IDENTIFICATION_SEARCH_REASON_CODE_ID)
REFERENCES IDENTIFICATION_SEARCH_REASON_CODE (IDENTIFICATION_SEARCH_REASON_CODE_ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PERSON_SYSTEMS_TO_SEARCH ADD CONSTRAINT SYSTEMS_TO_SEARCH_PERSON_SYSTEMS_TO_SEARCH_fk
FOREIGN KEY (SYSTEMS_TO_SEARCH_ID)
REFERENCES SYSTEMS_TO_SEARCH (SYSTEMS_TO_SEARCH_ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PERSON_SEARCH_RESULTS ADD CONSTRAINT SYSTEMS_TO_SEARCH_PERSON_SEARCH_RESULTS_fk
FOREIGN KEY (SYSTEMS_TO_SEARCH_ID)
REFERENCES SYSTEMS_TO_SEARCH (SYSTEMS_TO_SEARCH_ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PERSON_SEARCH_REQUEST ADD CONSTRAINT USER_INFO_PERSON_SEARCH_REQUEST_fk
FOREIGN KEY (USER_INFO_ID)
REFERENCES USER_INFO (USER_INFO_ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE QUERY_REQUEST ADD CONSTRAINT USER_INFO_QUERY_REQUEST_fk
FOREIGN KEY (USER_INFO_ID)
REFERENCES USER_INFO (USER_INFO_ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE IDENTIFICATION_SEARCH_REQUEST ADD CONSTRAINT USER_INFO_CRIMINAL_IDENTIFICATION_SEARCH_REQUEST_fk
FOREIGN KEY (USER_INFO_ID)
REFERENCES USER_INFO (USER_INFO_ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE USER_LOGIN ADD CONSTRAINT USER_INFO_USER_LOGIN_fk
FOREIGN KEY (USER_INFO_ID)
REFERENCES USER_INFO (USER_INFO_ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE IDENTIFICATION_SEARCH_RESULTS ADD CONSTRAINT IDENTIFICATION_SEARCH_REQUEST_IDENTIFICATION_SEARCH_RESULTS_fk
FOREIGN KEY (IDENTIFICATION_SEARCH_REQUEST_ID)
REFERENCES IDENTIFICATION_SEARCH_REQUEST (IDENTIFICATION_SEARCH_REQUEST_ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE IDENTIFICATION_REASON_CODE_JOINER ADD CONSTRAINT IDENTIFICATION_SEARCH_REQUEST_IDENTIFICATION_REASON_CODE_JOINER_fk
FOREIGN KEY (IDENTIFICATION_SEARCH_REQUEST_ID)
REFERENCES IDENTIFICATION_SEARCH_REQUEST (IDENTIFICATION_SEARCH_REQUEST_ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE CRIMINAL_HISTORY_QUERY_RESULTS ADD CONSTRAINT QUERY_REQUEST_CRIMINAL_HISTORY_QUERY_RESULTS_fk
FOREIGN KEY (QUERY_REQUEST_ID)
REFERENCES QUERY_REQUEST (QUERY_REQUEST_ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE WARRANT_QUERY_RESULTS ADD CONSTRAINT QUERY_REQUEST_WARRANT_QUERY_RESULTS_fk
FOREIGN KEY (QUERY_REQUEST_ID)
REFERENCES QUERY_REQUEST (QUERY_REQUEST_ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE IDENTIFICATION_RESULTS_QUERY_DETAIL ADD CONSTRAINT QUERY_REQUEST_IDENTIFICATION_RESULTS_QUERY_DETAIL_fk
FOREIGN KEY (QUERY_REQUEST_ID)
REFERENCES QUERY_REQUEST (QUERY_REQUEST_ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE FIREARMS_QUERY_RESULTS ADD CONSTRAINT QUERY_REQUEST_FIREARMS_QUERY_RESULTS_fk
FOREIGN KEY (QUERY_REQUEST_ID)
REFERENCES QUERY_REQUEST (QUERY_REQUEST_ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PERSON_SEARCH_REQUEST ADD CONSTRAINT SEARCH_QUALIFIER_CODES_PERSON_SEARCH_REQUEST_fk
FOREIGN KEY (LAST_NAME_QUALIFIER_CODE_ID)
REFERENCES SEARCH_QUALIFIER_CODES (SEARCH_QUALIFIER_CODES_ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PERSON_SEARCH_REQUEST ADD CONSTRAINT SEARCH_QUALIFIER_CODES_PERSON_SEARCH_REQUEST_fk1
FOREIGN KEY (FIRST_NAME_QUALIFIER_CODE_ID)
REFERENCES SEARCH_QUALIFIER_CODES (SEARCH_QUALIFIER_CODES_ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PERSON_SEARCH_RESULTS ADD CONSTRAINT PERSON_SEARCH_REQUEST_PERSON_SEARCH_RESULTS_fk
FOREIGN KEY (PERSON_SEARCH_REQUEST_ID)
REFERENCES PERSON_SEARCH_REQUEST (PERSON_SEARCH_REQUEST_ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE PERSON_SYSTEMS_TO_SEARCH ADD CONSTRAINT PERSON_SEARCH_REQUEST_PERSON_SYSTEMS_TO_SEARCH_fk
FOREIGN KEY (PERSON_SEARCH_REQUEST_ID)
REFERENCES PERSON_SEARCH_REQUEST (PERSON_SEARCH_REQUEST_ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE NOTIFICATION_PROPERTIES ADD CONSTRAINT NOTIFICATIONS_SENT_SUBSCRIPTION_PROPERTIES_fk
FOREIGN KEY (NOTIFICATIONS_SENT_ID)
REFERENCES NOTIFICATIONS_SENT (NOTIFICATIONS_SENT_ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE NOTIFICATION_MECHANISM ADD CONSTRAINT NOTIFICATIONS_SENT_EMAIL_ADDRESS_fk
FOREIGN KEY (NOTIFICATIONS_SENT_ID)
REFERENCES NOTIFICATIONS_SENT (NOTIFICATIONS_SENT_ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION;