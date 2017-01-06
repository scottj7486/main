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
<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:fppq-res-doc="http://ojbc.org/IEPD/Exchange/FirearmPurchaseProhibitionQueryResults/1.0"
	xmlns:fppq-res-ext="http://ojbc.org/IEPD/Extensions/FirearmPurchaseProhibitionQueryResultsExtension/1.0"
	xmlns:me-fpp-codes="http://ojbc.org/IEPD/Extensions/Maine/FirearmPurchaseProhibitionCodes/1.0"
	xmlns:iad="http://ojbc.org/IEPD/Extensions/InformationAccessDenial/1.0"
	xmlns:j="http://release.niem.gov/niem/domains/jxdm/5.1/" xmlns:nc="http://release.niem.gov/niem/niem-core/3.0/"
	xmlns:ncic="http://release.niem.gov/niem/codes/fbi_ncic/3.1/"
	xmlns:niem-xs="http://release.niem.gov/niem/proxy/xsd/3.0/"
	xmlns:qrer="http://ojbc.org/IEPD/Extensions/QueryRequestErrorReporting/1.0"
	xmlns:qrm="http://ojbc.org/IEPD/Extensions/QueryResultsMetadata/1.0"
	xmlns:structures="http://release.niem.gov/niem/structures/3.0/"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    exclude-result-prefixes="#all">
	<xsl:import href="_formatters.xsl" />
	
    <xsl:output method="html" encoding="UTF-8" />

	<xsl:template match="/fppq-res-doc:FirearmPurchaseProhibitionQueryResults">
    	<xsl:choose>
    		<xsl:when test="qrm:QueryResultsMetadata/qrer:QueryRequestError 
    				| qrm:QueryResultsMetadata/iad:InformationAccessDenial">
    			<table id="searchResultsError" class="detailsTable">
		            <tr>
		                <td class="detailsTitle" >SEARCH RESULTS ERROR</td>
		            </tr>
		            <tr>
			            <td>
			            	<xsl:apply-templates select="qrm:QueryResultsMetadata/qrer:QueryRequestError" /> 
			            	<xsl:apply-templates select="qrm:QueryResultsMetadata/iad:InformationAccessDenial" /> 
			            </td>
		            </tr>
		        </table>
    		</xsl:when>
    		<xsl:otherwise>
    			<xsl:choose>
    				<xsl:when test="count(fppq-res-ext:FirearmPurchaseProhibitionReport) = 0">
    					<table id="incidentsError" class="detailsTable">
    						<tr>
    							<td class="detailsTitle" >NO ASSOCIATED Firearm Purchase Prohibition Report</td>
    						</tr>
    						<tr>
    							<td>
    								<span class="error">There is no firearm purchase prohibition report associated with this person record.</span><br /> 
    							</td>
    						</tr>
    					</table>
    				</xsl:when>
    				<xsl:otherwise>
    					<table id="courtCaseTable" class="detailsTable">
    						<tr>
    							<td class="detailsTitle" >CASE NUMBER</td>
    							<td class="detailsTitle">CAPTION/STYLE</td>
    							<td class="detailsTitle">CASE STATUS</td>
    						</tr>
    						<xsl:apply-templates /> 
    					</table>
    					<div id="instanceDetailTabsHolder"></div>   
    				</xsl:otherwise>
    			</xsl:choose>
	        </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
	
	<xsl:template match="fppq-res-ext:FirearmPurchaseProhibitionReport">
		FPP detail
	</xsl:template>
	<xsl:template match="qrer:QueryRequestError">
		<p class="error">
			ID: <xsl:value-of select="qrer:QueryRequestIdentification/nc:IdentificationID" />
			<br/> Jurisdiction: <xsl:value-of select="qrer:QueryRequestIdentification/nc:IdentificationJurisdiction/j:LocationStateNCICLISCode"/>	
			<br/> Error: <xsl:value-of select="qrer:ErrorText"/>
		</p>
	</xsl:template>
	
	<xsl:template match="iad:InformationAccessDenial">
    	<span class="error">Access to System <xsl:value-of select="iad:InformationAccessDenyingSystemNameText" /> Denied. Denied Reason: <xsl:value-of select="iad:InformationAccessDenialReasonText"/></span><br />
    </xsl:template>
</xsl:stylesheet>
