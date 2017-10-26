package org.ojbc.audit.enhanced.processor;

import org.apache.camel.Body;
import org.apache.camel.Header;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ojbc.audit.enhanced.dao.EnhancedAuditDAO;
import org.ojbc.audit.enhanced.dao.model.PersonSearchResult;
import org.w3c.dom.Document;

public class PersonSearchResponseSQLProcessor extends AbstractPersonSearchResponseProcessor{

	private static final Log log = LogFactory.getLog(PersonSearchResponseSQLProcessor.class);
	
	private EnhancedAuditDAO enhancedAuditDAO;
	
	@Override
	public void auditPersonSearchResponse(@Body Document document,  @Header(value = "federatedQueryRequestGUID")String messageID) {
		
		try {
			
			PersonSearchResult personSearchResult = processPersonSearchResponse(document);
			
			Integer systemToSearchPK = enhancedAuditDAO.retrieveSystemToSearchIDFromURI(personSearchResult.getSystemSearchResultURI());
			
			if (systemToSearchPK != null)
			{
				personSearchResult.setSystemSearchResultID(systemToSearchPK);
			}	

			Integer personSearchRequestPK = enhancedAuditDAO.retrievePersonSearchIDfromMessageID(messageID);
			
			if (personSearchRequestPK != null)
			{
				personSearchResult.setPersonSearchRequestId(personSearchRequestPK);
			}	
			
			enhancedAuditDAO.savePersonSearchResult(personSearchResult);
			
			log.info(personSearchResult.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Unable to audit person search request: " + ExceptionUtils.getStackTrace(e));
		}
		
		
	}

	public EnhancedAuditDAO getEnhancedAuditDAO() {
		return enhancedAuditDAO;
	}

	public void setEnhancedAuditDAO(EnhancedAuditDAO enhancedAuditDAO) {
		this.enhancedAuditDAO = enhancedAuditDAO;
	}
	
	
}
