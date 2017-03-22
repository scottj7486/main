<?xml version="1.0" encoding="UTF-8"?>
<!--

    Unless explicitly acquired and licensed from Licensor under another license, the contents of
    this file are subject to the Reciprocal Public License ("RPL") Version 1.5, or subsequent
    versions as allowed by the RPL, and You may not copy or use this file in either source code
    or executable form, except in compliance with the terms and conditions of the RPL

    All software distributed under the RPL is provided strictly on an "AS IS" basis, WITHOUT
    WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, AND LICENSOR HEREBY DISCLAIMS ALL SUCH
    WARRANTIES, INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
    PARTICULAR PURPOSE, QUIET ENJOYMENT, OR NON-INFRINGEMENT. See the RPL for specific language
    governing rights and limitations under the RPL.

    http://opensource.org/licenses/RPL-1.5

    Copyright 2012-2015 Open Justice Broker Consortium

-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:b="http://docs.oasis-open.org/wsn/b-2"
	xmlns:add="http://www.w3.org/2005/08/addressing"
	xmlns:fchr-doc="http://ojbc.org/IEPD/Exchange/FederalCriminalHistoryReport/1.0"
	xmlns:fchr-ext="http://ojbc.org/IEPD/Extensions/FederalCriminalHistoryReportExtension/1.0"
	xmlns:nc30="http://release.niem.gov/niem/niem-core/3.0/" xmlns:j51="http://release.niem.gov/niem/domains/jxdm/5.1/"
	xmlns:niem-xs="http://release.niem.gov/niem/proxy/xsd/3.0/"
	xmlns:structures="http://release.niem.gov/niem/structures/3.0/"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:j="http://niem.gov/niem/domains/jxdm/4.1"
	xmlns:nc="http://niem.gov/niem/niem-core/2.0" xmlns:niem-xsd="http://niem.gov/niem/proxy/xsd/2.0"
	xmlns:s="http://niem.gov/niem/structures/2.0" xmlns:notfm-doc="http://ojbc.org/IEPD/Exchange/NotificationMessage/1.0"
	xmlns:notfm-ext="http://ojbc.org/IEPD/Extensions/Notification/1.0"
	xmlns:xmime="http://www.w3.org/2005/05/xmlmime" xmlns:xop="http://www.w3.org/2004/08/xop/include"
	exclude-result-prefixes="fchr-doc fchr-ext nc30 j51 structures niem-xs"
	version="2.0">
	<xsl:output indent="yes" method="xml" />
	<xsl:strip-space elements="*" />
	<xsl:template match="/fchr-doc:FederalCriminalHistoryReport">
		<b:Notify>
			<b:NotificationMessage>
				<b:SubscriptionReference>
					<add:Address>http://www.ojbc.org/SubscriptionManager</add:Address>
					<!--Optional: -->
					<add:ReferenceParameters>
						<!--You may enter ANY elements at this point -->
					</add:ReferenceParameters>
					<!--Optional: -->
					<add:Metadata>
						<!--You may enter ANY elements at this point -->
					</add:Metadata>
					<!--You may enter ANY elements at this point -->
				</b:SubscriptionReference>
				<!--Optional: -->
				<b:Topic
					Dialect="http://docs.oasis-open.org/wsn/t-1/TopicExpression/Concrete"
					xmlns:topics="http://ojbc.org/wsn/topics"> criminal history
				</b:Topic>
				<!--Optional: -->
				<b:ProducerReference>
					<add:Address>http://www.ojbc.org/arrestNotificationProducer
					</add:Address>
					<!--Optional: -->
					<add:ReferenceParameters>
						<!--You may enter ANY elements at this point -->
					</add:ReferenceParameters>
					<!--Optional: -->
					<add:Metadata>
						<!--You may enter ANY elements at this point -->
					</add:Metadata>
					<!--You may enter ANY elements at this point -->
				</b:ProducerReference>
				<b:Message>
					<notfm-doc:NotificationMessage>
						<notfm-ext:NotifyingFederalCriminalHistoryUpdate>
							<xsl:attribute name="s:id"><xsl:value-of
								select="generate-id(.)" /></xsl:attribute>
							<notfm-ext:NotifyingActivityReportingOrganization>
								<nc:OrganizationName>FBI</nc:OrganizationName>
							</notfm-ext:NotifyingActivityReportingOrganization>
							<notfm-ext:NotifyingActivityReportingSystemNameText>SystemName
							</notfm-ext:NotifyingActivityReportingSystemNameText>
							<notfm-ext:NotifyingActivityReportingSystemURI>SystemURIHere
							</notfm-ext:NotifyingActivityReportingSystemURI>
							<xsl:apply-templates
								select="fchr-ext:TransactionDescriptiveRecord/fchr-ext:RapBackSubscription" />
							<xsl:apply-templates
								select="fchr-ext:TransactionDescriptiveRecord/fchr-ext:FederalCriminalHistoryRecordDocument" />
						</notfm-ext:NotifyingFederalCriminalHistoryUpdate>
						<xsl:apply-templates select="." mode="association" />
						<xsl:apply-templates select="nc30:Person" />
					</notfm-doc:NotificationMessage>
				</b:Message>
			</b:NotificationMessage>
		</b:Notify>
	</xsl:template>
	<xsl:template match="fchr-ext:RapBackSubscription">
		<notfm-ext:RelatedFBISubscription>
			<xsl:apply-templates select="fchr-ext:RapBackExpirationDate" />
			<xsl:apply-templates select="fchr-ext:RapBackSubscriptionDate" />
			<xsl:apply-templates select="fchr-ext:RapBackSubscriptionIdentification" />
			<xsl:apply-templates select="fchr-ext:RapBackSubscriptionTermCode" />
			<xsl:apply-templates select="fchr-ext:RapBackTermDate" />
			<xsl:apply-templates select="fchr-ext:SubscriptionQualifierIdentification" />
			<xsl:apply-templates select="../fchr-ext:RapBackNotificationEvent" />
		</notfm-ext:RelatedFBISubscription>
	</xsl:template>
	<xsl:template match="fchr-ext:RapBackExpirationDate">
		<notfm-ext:RapBackExpirationDate>
			<xsl:apply-templates select="nc30:Date" />
		</notfm-ext:RapBackExpirationDate>
	</xsl:template>
	<xsl:template match="fchr-ext:RapBackSubscriptionDate">
		<notfm-ext:RapBackSubscriptionDate>
			<xsl:apply-templates select="nc30:Date" />
		</notfm-ext:RapBackSubscriptionDate>
	</xsl:template>
	<xsl:template match="fchr-ext:RapBackSubscriptionIdentification">
		<notfm-ext:RecordRapBackSubscriptionIdentification>
			<xsl:apply-templates select="nc30:IdentificationID" />
		</notfm-ext:RecordRapBackSubscriptionIdentification>
	</xsl:template>
	<xsl:template match="fchr-ext:RapBackSubscriptionTermCode">
		<notfm-ext:RapBackSubscriptionTermCode>
			<xsl:value-of select="." />
		</notfm-ext:RapBackSubscriptionTermCode>
	</xsl:template>
	<xsl:template match="fchr-ext:RapBackTermDate">
		<notfm-ext:RapBackTermDate>
			<xsl:apply-templates select="nc30:Date" />
		</notfm-ext:RapBackTermDate>
	</xsl:template>
	<xsl:template match="fchr-ext:SubscriptionQualifierIdentification">
		<notfm-ext:SubscriptionQualifierIdentification>
			<xsl:apply-templates select="nc30:IdentificationID" />
		</notfm-ext:SubscriptionQualifierIdentification>
	</xsl:template>
	<xsl:template match="fchr-ext:RapBackNotificationEvent">
		<xsl:apply-templates
			select="fchr-ext:RapBackActivityNotificationIdentification" />
		<xsl:apply-templates select="fchr-ext:RapBackAttentionText" />
		<xsl:apply-templates select="fchr-ext:RapBackEventDate" />
		<xsl:apply-templates
			select="fchr-ext:TriggeringEvents/fchr-ext:FederalTriggeringEventCode" />
		<xsl:apply-templates select="fchr-ext:RapBackEventText" />
	</xsl:template>
	<xsl:template match="fchr-ext:RapBackActivityNotificationIdentification">
		<notfm-ext:RapBackActivityNotificationIdentification>
			<xsl:apply-templates select="nc30:IdentificationID" />
		</notfm-ext:RapBackActivityNotificationIdentification>
	</xsl:template>
	<xsl:template match="fchr-ext:RapBackAttentionText">
		<notfm-ext:RapBackAttentionText>
			<xsl:value-of select="." />
		</notfm-ext:RapBackAttentionText>
	</xsl:template>
	<xsl:template match="fchr-ext:RapBackEventDate">
		<notfm-ext:RapBackEventDate>
			<xsl:apply-templates select="nc30:Date" />
		</notfm-ext:RapBackEventDate>
	</xsl:template>
	<xsl:template match="fchr-ext:FederalTriggeringEventCode">
		<notfm-ext:TriggeringEvents>
			<notfm-ext:FederalTriggeringEventCode>
				<xsl:value-of select="." />
			</notfm-ext:FederalTriggeringEventCode>
		</notfm-ext:TriggeringEvents>
	</xsl:template>
	<xsl:template match="fchr-ext:RapBackEventText">
		<notfm-ext:RapBackEventText>
			<xsl:value-of select="." />
		</notfm-ext:RapBackEventText>
	</xsl:template>
	<xsl:template match="fchr-ext:FederalCriminalHistoryRecordDocument">
		<notfm-ext:CriminalHistoryRecordDocument>
			<nc:DocumentBinary>
				<notfm-ext:Base64BinaryObject>
					<xsl:value-of select="." />
				</notfm-ext:Base64BinaryObject>
			</nc:DocumentBinary>
		</notfm-ext:CriminalHistoryRecordDocument>
	</xsl:template>
	<xsl:template match="fchr-doc:FederalCriminalHistoryReport"
		mode="association">
		<nc:ActivityInvolvedPersonAssociation>
			<nc:ActivityReference>
				<xsl:attribute name="s:ref"><xsl:value-of select="generate-id(.)" /></xsl:attribute>
			</nc:ActivityReference>
			<nc:PersonReference>
				<xsl:attribute name="s:ref"><xsl:value-of
					select="generate-id(./nc30:Person)" /></xsl:attribute>
			</nc:PersonReference>
		</nc:ActivityInvolvedPersonAssociation>
	</xsl:template>
	<xsl:template match="nc30:Person">
		<j:Person>
			<xsl:attribute name="s:id"><xsl:value-of select="generate-id(.)" /></xsl:attribute>
			<xsl:apply-templates select="nc30:PersonBirthDate" />
			<xsl:apply-templates select="nc30:PersonName" />
			<xsl:apply-templates select="j51:PersonAugmentation" />
		</j:Person>
	</xsl:template>
	<xsl:template match="nc30:PersonBirthDate">
		<nc:PersonBirthDate>
			<xsl:apply-templates select="nc30:Date" />
		</nc:PersonBirthDate>
	</xsl:template>
	<xsl:template match="nc30:PersonName">
		<nc:PersonName>
			<xsl:apply-templates select="nc30:PersonGivenName" />
			<xsl:apply-templates select="nc30:PersonMiddleName" />
			<xsl:apply-templates select="nc30:PersonSurName" />
		</nc:PersonName>
	</xsl:template>
	<xsl:template match="nc30:PersonGivenName">
		<nc:PersonGivenName>
			<xsl:value-of select="." />
		</nc:PersonGivenName>
	</xsl:template>
	<xsl:template match="nc30:PersonMiddleName">
		<nc:PersonMiddleName>
			<xsl:value-of select="." />
		</nc:PersonMiddleName>
	</xsl:template>
	<xsl:template match="nc30:PersonSurName">
		<nc:PersonSurName>
			<xsl:value-of select="." />
		</nc:PersonSurName>
	</xsl:template>
	<xsl:template match="j51:PersonAugmentation">
		<j:PersonAugmentation>
			<xsl:apply-templates select="j51:PersonFBIIdentification" />
			<xsl:apply-templates select="j51:PersonStateFingerprintIdentification" />
		</j:PersonAugmentation>
	</xsl:template>
	<xsl:template match="j51:PersonFBIIdentification">
		<j:PersonFBIIdentification>
			<xsl:apply-templates select="nc30:IdentificationID" />
		</j:PersonFBIIdentification>
	</xsl:template>
	<xsl:template match="j51:PersonStateFingerprintIdentification">
		<j:PersonStateFingerprintIdentification>
			<xsl:apply-templates select="nc30:IdentificationID" />
		</j:PersonStateFingerprintIdentification>
	</xsl:template>
	<xsl:template match="nc30:Date">
		<nc:Date>
			<xsl:value-of select="." />
		</nc:Date>
	</xsl:template>
	<xsl:template match="nc30:IdentificationID">
		<nc:IdentificationID>
			<xsl:value-of select="." />
		</nc:IdentificationID>
	</xsl:template>
</xsl:stylesheet>