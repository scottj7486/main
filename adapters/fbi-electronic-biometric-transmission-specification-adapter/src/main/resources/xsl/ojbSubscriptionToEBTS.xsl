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
	xmlns:xs="http://www.w3.org/2001/XMLSchema" 
	xmlns:ebts="http://cjis.fbi.gov/fbi_ebts/10.0" 
    xmlns:ansi-nist="http://niem.gov/niem/biometrics/1.0" 
    xmlns:itl="http://biometrics.nist.gov/standard/2011" 
    xmlns:nc20="http://niem.gov/niem/niem-core/2.0" 
    xmlns:j="http://niem.gov/niem/domains/jxdm/4.1"
    xmlns:submsg-doc="http://ojbc.org/IEPD/Exchange/SubscriptionMessage/1.0"
    xmlns:submsg-ext="http://ojbc.org/IEPD/Extensions/Subscription/1.0"
    xmlns:unsubmsg-doc="http://ojbc.org/IEPD/Exchange/UnsubscriptionMessage/1.0"
    xmlns:b-2="http://docs.oasis-open.org/wsn/b-2">
	
	<xsl:output indent="yes" method="xml"/>
	
	<!-- These are implementation-specific parameters that must be passed in when calling this stylesheet -->
					
	<!-- Assumes to be string: yyyy-MM-dd -->	
	<xsl:param name="rapBackTransactionDate"/>
	
	<!-- RBNF (2.2062)-->
	<xsl:param name="rapBackNotificatonFormat" />
	
	<!-- RBC 2.2065-->
	<xsl:param name="recordRapBackCategoryCode" />
	
	<xsl:param name="rapBackDisclosureIndicator"/>
		
	<!-- RBOO (2.2063) -->
	<xsl:param name="rapBackInStateOptOutIndicator" />
	
	<!-- This field corresponds to RBT 2.2040 and specifies which Events will result in notifications sent to the subscriber -->
	<xsl:param name="rapBackTriggeringEvent" />
	
	<!-- DAI 1.007 -->
	<xsl:param name="destinationOrganizationORI" />
	
	<!-- ORI 1.007 -->
	<xsl:param name="originatorOrganizationORI" />
	
	<!-- TCN 1.009 -->
	<xsl:param name="controlID" />
	
	<!-- DOM 1.013 -->
	<xsl:param name="domainVersion" />
	<xsl:param name="domainName" />
	
	<!-- VER 1.002 -->
	<xsl:param name="transactionMajorVersion"/>
	<xsl:param name="transactionMinorVersion"/>
	
	<!-- RAP 2.070 -->
	<xsl:param name="rapSheetRequestIndicator"/>
	
	<!-- RBR 2.020 -->
	<xsl:param name="rapBackRecipient"/>
	
	<!-- CRI 2.073 -->
	<xsl:param name="controllingAgencyID"/>
	
	<!-- OCA 2.009 -->
	<xsl:param name="originatingAgencyCaseNumber"/>
	
	<!-- Native Scanning Resolution (NSR 1.011) -->
	<xsl:param name="nativeScanningResolution"/>
	
	<!--  Nominal Transmitting Resolution (NTR 1.012 -->
	<xsl:param name="nominalTransmittingResolution"/>
	
	<!-- CNT 1.003 -->
	<xsl:param name="transactionContentSummaryContentFirstRecordCategoryCode"/>
	<xsl:param name="transactionContentSummaryContentRecordCountCriminal"/>
	<xsl:param name="transactionContentSummaryContentRecordCountCivil"/>
	
	<!-- IDC 2.002 -->
	<xsl:param name="imageReferenceID"/>
	
	<xsl:template match="/">
		<xsl:apply-templates select="b-2:Subscribe"/>
		<xsl:apply-templates select="b-2:Unsubscribe"/>
	</xsl:template>
	
	<xsl:template match="b-2:Subscribe">
	
	<!-- We need to determine up front if we're requesting a modification of an existing subscription or requesting a new subscription. 
	If the subscription message contains information about a related FBI subscription, that means we need to modify an 
	existing FBI subscription-->
	
		<xsl:variable name="purpose">
			<xsl:choose>
				<xsl:when test="submsg-doc:SubscriptionMessage/submsg-ext:RelatedFBISubscription">modifySubscription</xsl:when>
				<xsl:otherwise>newSubscription</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:apply-templates select="submsg-doc:SubscriptionMessage">
			<xsl:with-param name="purpose" select="$purpose"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="b-2:Unsubscribe">
		<xsl:variable name="purpose">cancelSubscription</xsl:variable>
		<xsl:apply-templates select="unsubmsg-doc:UnsubscriptionMessage">
			<xsl:with-param name="purpose" select="$purpose"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="submsg-doc:SubscriptionMessage">
		<xsl:param name="purpose"/>
		<itl:NISTBiometricInformationExchangePackage>
		
			<!-- EBTS Record Type 1 -->
			<xsl:call-template name="buildType1Record">
				<xsl:with-param name="purpose" select="$purpose"/>
			</xsl:call-template>
			
			<!-- EBTS Record Type 2 -->
			<xsl:call-template name="buildType2Record">
				<xsl:with-param name="purpose" select="$purpose"/>
			</xsl:call-template>
			
		</itl:NISTBiometricInformationExchangePackage>
		
	</xsl:template>
	
	
	<xsl:template match="unsubmsg-doc:UnsubscriptionMessage">
		<xsl:param name="purpose"/>
		<itl:NISTBiometricInformationExchangePackage>
		
			<!-- EBTS Record Type 1 -->
			<xsl:call-template name="buildType1Record">
				<xsl:with-param name="purpose" select="$purpose"/>
			</xsl:call-template>
			
			<!-- EBTS Record Type 2 -->
			<xsl:call-template name="buildType2Record">
				<xsl:with-param name="purpose" select="$purpose"/>
			</xsl:call-template>
			
		</itl:NISTBiometricInformationExchangePackage>
	</xsl:template>
	
	<xsl:template name="buildType1Record">
		<xsl:param name="purpose"/>
		<itl:PackageInformationRecord>
				<ansi-nist:RecordCategoryCode>01</ansi-nist:RecordCategoryCode> 
				 <ansi-nist:Transaction>
				 	<ansi-nist:TransactionDate>
				 		<nc20:Date>
				 			<xsl:value-of select="$rapBackTransactionDate"/>
				 		</nc20:Date>
				 	</ansi-nist:TransactionDate>
				 	<ansi-nist:TransactionDestinationOrganization>
               			 <nc20:OrganizationIdentification>
                    		<nc20:IdentificationID>
                    			<xsl:value-of select="$destinationOrganizationORI"/>
                    		</nc20:IdentificationID>
               			 </nc20:OrganizationIdentification>
          		 	</ansi-nist:TransactionDestinationOrganization>
          		 	<ansi-nist:TransactionOriginatingOrganization>
               			 <nc20:OrganizationIdentification>
                   			 <!--ORI 1.008-->
                    		<nc20:IdentificationID>
                    			<xsl:value-of select="$originatorOrganizationORI"/>
                    		</nc20:IdentificationID>
                		</nc20:OrganizationIdentification>
            		</ansi-nist:TransactionOriginatingOrganization>
            		<ansi-nist:TransactionControlIdentification>
                		<nc20:IdentificationID>
                			<xsl:value-of select="$controlID"/>
                		</nc20:IdentificationID>
            		</ansi-nist:TransactionControlIdentification>
            		<ansi-nist:TransactionDomain>
               			 <ansi-nist:DomainVersionNumberIdentification>
                    		<nc20:IdentificationID>
                    			<xsl:value-of select="$domainVersion"/>
                    		</nc20:IdentificationID>
               			 </ansi-nist:DomainVersionNumberIdentification>
               			 <ansi-nist:TransactionDomainName>
               			 	<xsl:value-of select="$domainName"/>
               			 </ansi-nist:TransactionDomainName>
           			</ansi-nist:TransactionDomain>
           			<ansi-nist:TransactionImageResolutionDetails>
           				 <ansi-nist:NativeScanningResolutionValue>
           				 	<xsl:value-of select="$nativeScanningResolution"/>
           				 </ansi-nist:NativeScanningResolutionValue>
               			 <ansi-nist:NominalTransmittingResolutionValue>
               			 	<xsl:value-of select="$nominalTransmittingResolution"/>
               			 </ansi-nist:NominalTransmittingResolutionValue>
           			</ansi-nist:TransactionImageResolutionDetails>
           			<ansi-nist:TransactionMajorVersionValue>
           				<xsl:value-of select="$transactionMajorVersion"/>
           			</ansi-nist:TransactionMajorVersionValue>
           			<ansi-nist:TransactionMinorVersionValue>
           				<xsl:value-of select="$transactionMinorVersion"/>
           			</ansi-nist:TransactionMinorVersionValue>
				 	<!-- This determines whether we are requesting a new subscription or modifying an existing one -->
				 	<!-- TODO: we will need to update this once we begin processing civil subscriptions -->
				 	<xsl:choose>
				 		<xsl:when test="$purpose = 'modifySubscription' or $purpose ='cancelSubscription'">
				 			<ebts:TransactionCategoryCode>RBMNT</ebts:TransactionCategoryCode>
				 		</xsl:when>
				 		<xsl:otherwise>
				 			<xsl:apply-templates select="/b-2:Subscribe/submsg-doc:SubscriptionMessage/submsg-ext:CriminalSubscriptionReasonCode[. != '']" mode="transactionCategory"/>
				 		</xsl:otherwise>
				 	</xsl:choose>
				 	 <ansi-nist:TransactionContentSummary>
				 	 	<ansi-nist:ContentFirstRecordCategoryCode>
				 	 		<xsl:value-of select="$transactionContentSummaryContentFirstRecordCategoryCode"/>
				 	 	</ansi-nist:ContentFirstRecordCategoryCode>
				 	 	<ansi-nist:ContentRecordQuantity>
				 	 		<xsl:choose>
								<xsl:when test="/b-2:Subscribe/submsg-doc:SubscriptionMessage/submsg-ext:CriminalSubscriptionReasonCode = 'CI'">
									<xsl:value-of select="$transactionContentSummaryContentRecordCountCriminal"/>
								</xsl:when>
								<xsl:when test="/b-2:Subscribe/submsg-doc:SubscriptionMessage/submsg-ext:CriminalSubscriptionReasonCode = 'CS'">
									<xsl:value-of select="$transactionContentSummaryContentRecordCountCriminal"/>
								</xsl:when>
								<xsl:otherwise>01</xsl:otherwise>
							</xsl:choose>
				 	 	</ansi-nist:ContentRecordQuantity>
				 	 	<!-- TODO: pass these in as params -->
				 	 	<ansi-nist:ContentRecordSummary>
                    		<ansi-nist:ImageReferenceIdentification>
                        		<nc20:IdentificationID>
                        			<xsl:value-of select="$imageReferenceID"/>
                        		</nc20:IdentificationID>
                   			 </ansi-nist:ImageReferenceIdentification>
                    		<ansi-nist:RecordCategoryCode>02</ansi-nist:RecordCategoryCode>
                		</ansi-nist:ContentRecordSummary>
				 	 </ansi-nist:TransactionContentSummary>
				 </ansi-nist:Transaction>
			</itl:PackageInformationRecord>
	</xsl:template>
	
	<xsl:template name="buildType2Record">
		<xsl:param name="purpose"/>
		<itl:PackageDescriptiveTextRecord>
			
				<ansi-nist:RecordCategoryCode>02</ansi-nist:RecordCategoryCode>
				<ansi-nist:ImageReferenceIdentification>
            		<nc20:IdentificationID>
            			<xsl:value-of select="$imageReferenceID"/>
            		</nc20:IdentificationID>
     		   </ansi-nist:ImageReferenceIdentification>
				<itl:UserDefinedDescriptiveDetail>
					<ebts:DomainDefinedDescriptiveFields>
						<xsl:choose>
							<xsl:when test="$purpose = 'newSubscription'">
								<ansi-nist:RecordRapSheetRequestIndicator>
									<xsl:value-of select="$rapSheetRequestIndicator"/>
								</ansi-nist:RecordRapSheetRequestIndicator>
							</xsl:when>
						</xsl:choose>
						<ebts:RecordRapBackData>
													
							<ebts:RecordRapBackActivityNotificationFormatCode>
								<xsl:value-of select="$rapBackNotificatonFormat" />
							</ebts:RecordRapBackActivityNotificationFormatCode>
							
							<!--Rap Back Category RBC 2.2065-->
							<!-- This is not allowed in RBMNT messages -->
							<xsl:choose>
								<xsl:when test="$purpose = 'newSubscription'">
									<xsl:apply-templates select="/b-2:Subscribe/submsg-doc:SubscriptionMessage/submsg-ext:CriminalSubscriptionReasonCode[. != '']" mode="rapBackCategory"/>
									<xsl:apply-templates select="/b-2:Subscribe/submsg-doc:SubscriptionMessage/submsg-ext:CivilSubscriptionReasonCode[. != '']" mode="rapBackCategory"/>
								</xsl:when>
							</xsl:choose>
							
							<!-- RBDI 2.2067, Optional-->
    						<ebts:RecordRapBackDisclosureIndicator>
    							<xsl:value-of select="$rapBackDisclosureIndicator"/>
   							 </ebts:RecordRapBackDisclosureIndicator>		
							
							<!-- This is important, this is where we determine the proper end date for a subscription -->
							<!-- TODO: should we even call the XSLT if the new end date is less than existing end date? -->
							<xsl:choose>
								<xsl:when test=" /b-2:Subscribe/submsg-doc:SubscriptionMessage/submsg-ext:RelatedFBISubscription/nc20:DateRange/nc20:EndDate/nc20:Date >  /b-2:Subscribe/submsg-doc:SubscriptionMessage/nc20:DateRange/nc20:EndDate/nc20:Date">
									<xsl:apply-templates select=" /b-2:Subscribe/submsg-doc:SubscriptionMessage/submsg-ext:RelatedFBISubscription/nc20:DateRange/nc20:EndDate/nc20:Date" mode="extendExpirationDate"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:apply-templates select="/b-2:Subscribe/submsg-doc:SubscriptionMessage/nc20:DateRange/nc20:EndDate/nc20:Date" mode="expirationDate"/>
								</xsl:otherwise>
							</xsl:choose>
								
							<ebts:RecordRapBackInStateOptOutIndicator>
								<xsl:value-of select="$rapBackInStateOptOutIndicator" />
							</ebts:RecordRapBackInStateOptOutIndicator>
							
							<ansi-nist:RecordForwardOrganizations>
		                        <nc20:OrganizationIdentification>
		                            <nc20:IdentificationID>
		                            	<xsl:value-of select="$rapBackRecipient"/>
		                            </nc20:IdentificationID>
		                        </nc20:OrganizationIdentification>
		                     </ansi-nist:RecordForwardOrganizations>
		                     
		                     <xsl:apply-templates select="/*/*/submsg-ext:RelatedFBISubscription/submsg-ext:SubscriptionFBIIdentification/nc20:IdentificationID" mode="fbiSubscriptionID"/>
														
							<ebts:RecordRapBackTriggeringEventCode>
								<xsl:value-of select="$rapBackTriggeringEvent" />
							</ebts:RecordRapBackTriggeringEventCode>							
														
							<xsl:apply-templates select="submsg-ext:Subject/j:PersonAugmentation/j:PersonStateFingerprintIdentification[nc20:IdentificationID !='']" mode="userDefinedElement"/>																
							<xsl:choose>
								<!-- This indicates that the maintenance is a "replace" -->
								<xsl:when test="$purpose = 'modifySubscription'">
									<ebts:TransactionRapBackMaintenanceCode>R</ebts:TransactionRapBackMaintenanceCode>
								</xsl:when>
								<xsl:when test="$purpose = 'cancelSubscription'">
									<ebts:TransactionRapBackMaintenanceCode>C</ebts:TransactionRapBackMaintenanceCode>
								</xsl:when>
								<xsl:when test="$purpose = 'newSubscription'"/>
							</xsl:choose>
						</ebts:RecordRapBackData>	
						<ebts:RecordTransactionActivity>
							<nc20:CaseTrackingID>
								<xsl:value-of select="$originatingAgencyCaseNumber"/>
							</nc20:CaseTrackingID>
							<ebts:RecordControllingAgency>
                       			 <nc20:OrganizationIdentification>
                            			<nc20:IdentificationID>
                            				<xsl:value-of select="$controllingAgencyID"/>
                            			</nc20:IdentificationID>
                       			 </nc20:OrganizationIdentification>
                   			</ebts:RecordControllingAgency>
						</ebts:RecordTransactionActivity>
						<xsl:apply-templates select="submsg-ext:Subject"/>	
					</ebts:DomainDefinedDescriptiveFields>
				</itl:UserDefinedDescriptiveDetail>
			</itl:PackageDescriptiveTextRecord>
	</xsl:template>

	<xsl:template match="submsg-ext:Subject">
		<ebts:RecordSubject>			
			<xsl:apply-templates select="nc20:PersonBirthDate"/>			
			<xsl:apply-templates select="nc20:PersonName"/>												
			<xsl:apply-templates select="j:PersonAugmentation" />					
		</ebts:RecordSubject>
	</xsl:template>
	
	<xsl:template match="nc20:PersonBirthDate">
		<xsl:copy-of select="." copy-namespaces="no"/>
	</xsl:template>
	
	<xsl:template match="nc20:PersonName">
		<ebts:PersonName>
			<xsl:apply-templates select="nc20:PersonGivenName"/>
			<xsl:apply-templates select="nc20:PersonSurName"/>
		</ebts:PersonName>
	</xsl:template>
	
	<xsl:template match="nc20:PersonGivenName">
		<xsl:copy-of select="." copy-namespaces="no"/>
	</xsl:template>
	
	<xsl:template match="nc20:PersonSurName">
		<xsl:copy-of select="." copy-namespaces="no"/>
	</xsl:template>
	
	<xsl:template match="j:PersonAugmentation">
		<xsl:apply-templates select="j:PersonFBIIdentification"/>			
	</xsl:template>
	
	<xsl:template match="j:PersonFBIIdentification">		
		<xsl:copy-of select="." copy-namespaces="no" />
	</xsl:template>
		
	<xsl:template match="j:PersonStateFingerprintIdentification" mode="userDefinedElement">
		<ebts:RecordRapBackUserDefinedElement>
			<ebts:UserDefinedElementName>State Fingerprint ID</ebts:UserDefinedElementName>
			<ebts:UserDefinedElementText>
				<xsl:value-of select="normalize-space(.)"/>
			</ebts:UserDefinedElementText>
		</ebts:RecordRapBackUserDefinedElement>
	</xsl:template>
	
	<xsl:template match="submsg-ext:CriminalSubscriptionReasonCode" mode="transactionCategory">
		<ebts:TransactionCategoryCode>
			<xsl:choose>
				<xsl:when test=". = 'CI'">RBSCRM</xsl:when>
				<xsl:when test=". = 'CS'">RBSCRM</xsl:when>
			</xsl:choose>
		</ebts:TransactionCategoryCode>
	</xsl:template>
	<xsl:template match="submsg-ext:CriminalSubscriptionReasonCode" mode="rapBackCategory">
		<ebts:RecordRapBackCategoryCode>
			<xsl:value-of select="."/>
		</ebts:RecordRapBackCategoryCode>
	</xsl:template>
	<xsl:template match="submsg-ext:CivilSubscriptionReasonCode" mode="rapBackCategory">
		<ebts:RecordRapBackCategoryCode>
			<xsl:value-of select="."/>
		</ebts:RecordRapBackCategoryCode>
	</xsl:template>
	
	<xsl:template match="nc20:Date" mode="expirationDate">
		<ebts:RecordRapBackExpirationDate>
			<nc20:Date>								
				<xsl:value-of select="." />
			</nc20:Date>
		</ebts:RecordRapBackExpirationDate>		
	</xsl:template>
	
	<xsl:template match="nc20:Date" mode="extendExpirationDate">
		<ebts:RecordRapBackExpirationDate>
			<nc20:Date>								
				<xsl:value-of select="." />
			</nc20:Date>
		</ebts:RecordRapBackExpirationDate>		
	</xsl:template>
	
	<xsl:template match="nc20:IdentificationID" mode="fbiSubscriptionID">
		 <ebts:RecordRapBackSubscriptionID>
		 	<xsl:value-of select="."/>
		 </ebts:RecordRapBackSubscriptionID>
	</xsl:template>
	
</xsl:stylesheet>
