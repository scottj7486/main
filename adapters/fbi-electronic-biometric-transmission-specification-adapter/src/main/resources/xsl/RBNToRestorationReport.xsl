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

    Copyright 2012-2017 Open Justice Broker Consortium

-->
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:chr-report-doc="http://ojbc.org/IEPD/Exchange/CriminalHistoryRestorationReport/1.0"
	xmlns:chr-report-ext="http://ojbc.org/IEPD/Extensions/CriminalHistoryRestorationReport/Extension/1.0"
	xmlns:nc="http://release.niem.gov/niem/niem-core/3.0/" xmlns:j="http://release.niem.gov/niem/domains/jxdm/5.0/"
	xmlns:niem-xs="http://release.niem.gov/niem/proxy/xsd/3.0/"
	xmlns:structures="http://release.niem.gov/niem/structures/3.0/"
	xmlns:ebts="http://cjis.fbi.gov/fbi_ebts/10.0" xmlns:ansi-nist="http://niem.gov/niem/biometrics/1.0"
	xmlns:itl="http://biometrics.nist.gov/standard/2011" xmlns:nc20="http://niem.gov/niem/niem-core/2.0"
	xmlns:j41="http://niem.gov/niem/domains/jxdm/4.1"
	exclude-result-prefixes="ebts ansi-nist itl nc20 j41">
	<xsl:output indent="yes" method="xml" omit-xml-declaration="yes" />
	<xsl:param name="restoredIdentity" />
	<xsl:param name="jurisdictionCode" select="'FBI'" />
	<xsl:template match="/itl:NISTBiometricInformationExchangePackage">
		<chr-report-doc:CriminalHistoryRestorationReport>
			<xsl:apply-templates select="." mode="jurisdiction" />
			<xsl:apply-templates select="." mode="UCN" />
		</chr-report-doc:CriminalHistoryRestorationReport>
	</xsl:template>
	<xsl:template match="itl:NISTBiometricInformationExchangePackage"
		mode="UCN">
		<nc:Person>
			<xsl:apply-templates select="." mode="postUCN" />
		</nc:Person>
	</xsl:template>
	<xsl:template match="itl:NISTBiometricInformationExchangePackage"
		mode="postUCN">
		<chr-report-ext:RestorationIdentifiers>
			<j:PersonFBIIdentification>
				<nc:IdentificationID>
					<xsl:value-of select="$restoredIdentity" />
				</nc:IdentificationID>
			</j:PersonFBIIdentification>
		</chr-report-ext:RestorationIdentifiers>
	</xsl:template>
	<xsl:template match="itl:NISTBiometricInformationExchangePackage"
		mode="jurisdiction">
		<chr-report-ext:CriminalHistoryReportJurisdictionCode>
			<xsl:value-of select="normalize-space($jurisdictionCode)" />
		</chr-report-ext:CriminalHistoryReportJurisdictionCode>
	</xsl:template>
</xsl:stylesheet>