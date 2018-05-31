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
package org.ojbc.adapters.rapbackdatastore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wss4j.common.principal.SAMLTokenPrincipal;
import org.joda.time.DateTime;
import org.ojbc.adapters.rapbackdatastore.dao.model.AgencyProfile;
import org.ojbc.adapters.rapbackdatastore.dao.model.CivilFingerPrints;
import org.ojbc.adapters.rapbackdatastore.dao.model.CivilInitialRapSheet;
import org.ojbc.adapters.rapbackdatastore.dao.model.CivilInitialResults;
import org.ojbc.adapters.rapbackdatastore.dao.model.CriminalInitialResults;
import org.ojbc.adapters.rapbackdatastore.dao.model.IdentificationTransaction;
import org.ojbc.adapters.rapbackdatastore.dao.model.Subject;
import org.ojbc.intermediaries.sn.dao.rapback.ResultSender;
import org.ojbc.intermediaries.sn.dao.rapback.SubsequentResults;
import org.ojbc.util.camel.security.saml.SAMLTokenUtils;
import org.ojbc.util.helper.ZipUtils;
import org.ojbc.util.model.rapback.FbiRapbackSubscription;
import org.ojbc.util.model.rapback.IdentificationResultSearchRequest;
import org.ojbc.util.model.rapback.IdentificationTransactionState;
import org.ojbc.util.model.rapback.Subscription;
import org.ojbc.util.model.saml.SamlAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("rapbackDAO")
public class RapbackDAOImpl implements RapbackDAO {
	
	private final Log log = LogFactory.getLog(this.getClass());
    
    @Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
	private JdbcTemplate jdbcTemplate;
    
    @Value("${rapbackDatastoreAdapter.civilIdlePeriod:60}")
    private Integer civilIdlePeriod;
    
    @Value("${rapbackDatastoreAdapter.criminalIdlePeriod:60}")
    private Integer criminalIdlePeriod;
    
	@Override
	public Integer saveSubject(final Subject subject) {
        log.debug("Inserting row into IDENTIFICATION_SUBJECT table : " + subject);
        
        final String SUBJECT_INSERT="INSERT into IDENTIFICATION_SUBJECT "
        		+ "(UCN, CRIMINAL_SID, CIVIL_SID, FIRST_NAME, LAST_NAME, MIDDLE_INITIAL, DOB, SEX_CODE) "
        		+ "values (?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        	    new PreparedStatementCreator() {
        	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        	            PreparedStatement ps =
        	                connection.prepareStatement(SUBJECT_INSERT, new String[] {"UCN","CRIMINAL_SID", "CIVIL_SID", "FIRST_NAME", "LAST_NAME", "MIDDLE_INITIAL", "DOB"});
        	            ps.setString(1, subject.getUcn());
        	            ps.setString(2, subject.getCriminalSid());
        	            ps.setString(3, subject.getCivilSid()); 
        	            ps.setString(4, subject.getFirstName());
        	            ps.setString(5, subject.getLastName());
        	            ps.setString(6, subject.getMiddleInitial());
        	            ps.setDate(7, toSqlDate(subject.getDob()));
        	            ps.setString(8, subject.getSexCode());
        	            return ps;
        	        }
        	    },
        	    keyHolder);

         return keyHolder.getKey().intValue();
	}
	
	@Override
	public Subject getSubject(Integer id) {
		final String SUBJECT_SELECT="SELECT * FROM IDENTIFICATION_SUBJECT WHERE SUBJECT_ID = ?";
		
		List<Subject> subjects = jdbcTemplate.query(SUBJECT_SELECT, new SubjectRowMapper(), id);
		return DataAccessUtils.singleResult(subjects);
	} 

	private final class SubjectRowMapper implements RowMapper<Subject> {
		public Subject mapRow(ResultSet rs, int rowNum)
				throws SQLException {
		Subject subject = buildSubject(rs);
	    return subject;
		}
	}

	private DateTime toDateTime(Date date){
		return date == null? null : new DateTime(date); 
	}
	
	private DateTime toDateTime(Timestamp timestamp){
		return timestamp == null? null : new DateTime(timestamp); 
	}
	
	private java.sql.Date toSqlDate(DateTime date){
		return date == null? null : new java.sql.Date(date.getMillis()); 
	}
	
	private Date toSqlDate(LocalDate date){
		return date == null? null : java.sql.Date.valueOf(date); 
	}

	@Override
	@Transactional
	public void saveIdentificationTransaction(
			IdentificationTransaction identificationTransaction) {
        log.debug("Inserting row into IDENTIFICATION_TRANSACTION table : " + identificationTransaction.toString());
        
        final String IDENTIFICATION_TRANSACTION_INSERT="INSERT into IDENTIFICATION_TRANSACTION "
        		+ "(TRANSACTION_NUMBER, SUBJECT_ID, OTN, OWNER_ORI, OWNER_PROGRAM_OCA, ARCHIVED, IDENTIFICATION_CATEGORY, AVAILABLE_FOR_SUBSCRIPTION_START_DATE) "
        		+ "values (?, ?, ?, ?, ?, ?, ?, ?)";
        
        Integer subjectId  = null; 
        if ( identificationTransaction.getSubject() == null){
        	throw new IllegalArgumentException("The subject should not be null when saving Identification Transaction :" + 
        			identificationTransaction.toString()); 
        }
        else{
        	subjectId = saveSubject(identificationTransaction.getSubject());
        	identificationTransaction.getSubject().setSubjectId(subjectId);
        	
        }
        
        jdbcTemplate.update(IDENTIFICATION_TRANSACTION_INSERT, 
        		identificationTransaction.getTransactionNumber(), 
        		subjectId, 
        		identificationTransaction.getOtn(),
        		identificationTransaction.getOwnerOri(),
        		identificationTransaction.getOwnerProgramOca(), 
        		BooleanUtils.isTrue(identificationTransaction.getArchived()),
        		identificationTransaction.getIdentificationCategory(), 
        		Calendar.getInstance().getTime()); 
	}

	@Override
	public Integer saveCivilFingerPrints(final CivilFingerPrints civilFingerPrints) {
        log.debug("Inserting row into CIVIL_FINGER_PRINTS table : " + civilFingerPrints.toString());

        final String CIVIL_FINGER_PRINTS_INSERT="insert into CIVIL_FINGER_PRINTS "
        		+ "(TRANSACTION_NUMBER, FINGER_PRINTS_FILE, FINGER_PRINTS_TYPE_ID) "
        		+ "values (?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        	    new PreparedStatementCreator() {
        	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        	            PreparedStatement ps =
        	                connection.prepareStatement(CIVIL_FINGER_PRINTS_INSERT, 
        	                		new String[] {"TRANSACTION_NUMBER", "FINGER_PRINTS_FILE", "FINGER_PRINTS_TYPE"});
        	            ps.setString(1, civilFingerPrints.getTransactionNumber());
        	            
        	            if (civilFingerPrints.getFingerPrintsFile() != null){
							ps.setBlob(2, new SerialBlob(ZipUtils.zip(civilFingerPrints.getFingerPrintsFile())));
        	            }
        	            ps.setInt(3, civilFingerPrints.getFingerPrintsType().ordinal()+1);
        	            return ps;
        	        }
        	    },
        	    keyHolder);

         return keyHolder.getKey().intValue();
	}

	@Override
	public Integer saveCivilInitialRapSheet(
			final CivilInitialRapSheet civilInitialRapSheet) {
        log.debug("Inserting row into CIVIL_INITIAL_RAP_SHEET table : " + civilInitialRapSheet.toString());

        final String CIVIL_INITIAL_RAP_SHEET_INSERT="insert into CIVIL_INITIAL_RAP_SHEET "
        		+ "(CIVIL_INITIAL_RESULT_ID, RAP_SHEET) "
        		+ "values (?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        	    new PreparedStatementCreator() {
        	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        	            PreparedStatement ps =
        	                connection.prepareStatement(CIVIL_INITIAL_RAP_SHEET_INSERT, 
        	                		new String[] {"CIVIL_INITIAL_RESULT_ID", "RAP_SHEET"});
        	            ps.setInt(1, civilInitialRapSheet.getCivilIntitialResultId());
						ps.setBlob(2, new SerialBlob(ZipUtils.zip(civilInitialRapSheet.getRapSheet())));
        	            return ps;
        	        }
        	    },
        	    keyHolder);

         return keyHolder.getKey().intValue();
	}

	@Override
	public Integer saveCivilInitialResults(
			final CivilInitialResults civilInitialResults) {
        log.debug("Inserting row into CIVIL_INITIAL_RESULTS table : " + civilInitialResults.toString());

        final String CIVIL_INITIAL_RESULTS_INSERT="insert into CIVIL_INITIAL_RESULTS "
        		+ "(TRANSACTION_NUMBER, SEARCH_RESULT_FILE, "
        		+ " RESULTS_SENDER_ID) "
        		+ "values (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        	    new PreparedStatementCreator() {
        	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        	            PreparedStatement ps =
        	                connection.prepareStatement(CIVIL_INITIAL_RESULTS_INSERT, 
        	                		new String[] {"TRANSACTION_NUMBER", "MATCH_NO_MATCH",  
        	                			"RESULTS_SENDER_ID"});
        	            ps.setString(1, civilInitialResults.getTransactionNumber());
						ps.setBlob(2, new SerialBlob(ZipUtils.zip(civilInitialResults.getSearchResultFile())));
        	            ps.setInt(3, civilInitialResults.getResultsSender().ordinal()+1);
        	            return ps;
        	        }
        	    },
        	    keyHolder);

         return keyHolder.getKey().intValue();
	}

	@Override
	public Integer saveCriminalInitialResults(
			final CriminalInitialResults criminalInitialResults) {
        log.debug("Inserting row into CRIMINAL_INITIAL_RESULTS table : " + criminalInitialResults.toString());

        final String CRIMINAL_INITIAL_RESULTS_INSERT="insert into CRIMINAL_INITIAL_RESULTS "
        		+ "(TRANSACTION_NUMBER, SEARCH_RESULT_FILE, RESULTS_SENDER_ID) "
        		+ "values (?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        	    new PreparedStatementCreator() {
        	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        	            PreparedStatement ps =
        	                connection.prepareStatement(CRIMINAL_INITIAL_RESULTS_INSERT, 
        	                		new String[] {"TRANSACTION_NUMBER", "SEARCH_RESULT_FILE",  
        	                			"RESULTS_SENDER_ID"});
        	            ps.setString(1, criminalInitialResults.getTransactionNumber());
						ps.setBlob(2, new SerialBlob(ZipUtils.zip(criminalInitialResults.getSearchResultFile())));
        	            ps.setInt(3, criminalInitialResults.getResultsSender().ordinal()+1);
        	            return ps;
        	        }
        	    },
        	    keyHolder);

         return keyHolder.getKey().intValue();
	}

	@Override
	public void saveFbiRapbackSubscription(
			final FbiRapbackSubscription fbiRapbackSubscription) {
        log.debug("Inserting row into FBI_RAP_BACK_SUBSCRIPTION table : " + fbiRapbackSubscription.toString());

        final String FBI_RAP_BACK_SUBSCRIPTION_INSERT="insert into FBI_RAP_BACK_SUBSCRIPTION "
        		+ "(FBI_SUBSCRIPTION_ID, UCN, RAP_BACK_CATEGORY_CODE, RAP_BACK_SUBSCRIPTION_TERM_CODE, "
        		+ " RAP_BACK_EXPIRATION_DATE, RAP_BACK_START_DATE, RAP_BACK_TERM_DATE, "
        		+ " RAP_BACK_OPT_OUT_IN_STATE_INDICATOR, RAP_BACK_ACTIVITY_NOTIFICATION_FORMAT_CODE, "
        		+ " SUBSCRIPTION_ID) "
        		+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(FBI_RAP_BACK_SUBSCRIPTION_INSERT, 
        				fbiRapbackSubscription.getFbiSubscriptionId(),
        	            fbiRapbackSubscription.getUcn(),
        	            fbiRapbackSubscription.getRapbackCategory(),
        	            fbiRapbackSubscription.getSubscriptionTerm(),
        	            toSqlDate(fbiRapbackSubscription.getRapbackExpirationDate()),
        	            toSqlDate(fbiRapbackSubscription.getRapbackStartDate()),
        	            toSqlDate(fbiRapbackSubscription.getRapbackTermDate()),
        	            fbiRapbackSubscription.getRapbackOptOutInState(),
        	            fbiRapbackSubscription.getRapbackActivityNotificationFormat(),
        	            fbiRapbackSubscription.getStateSubscriptionId());
	}

	
	@Override
	public IdentificationTransaction getIdentificationTransaction(
			String transactionNumber) {
		final String ID_TRANSACTION_SELECT_BY_TRANSACTION_NUMBER = 
				" SELECT * FROM identification_transaction i "
						+ "LEFT JOIN identification_subject s ON s.subject_id = i.subject_id "
						+ "WHERE transaction_number = ?";

		List<IdentificationTransaction> transactions = 
				jdbcTemplate.query(ID_TRANSACTION_SELECT_BY_TRANSACTION_NUMBER, 
						new IdentificationTransactionRowMapper(), transactionNumber);
		return DataAccessUtils.singleResult(transactions);
	}
	
	private final class IdentificationTransactionRowMapper 
		implements RowMapper<IdentificationTransaction> {
		public IdentificationTransaction mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			IdentificationTransaction identificationTransaction = buildIdentificationTransaction(rs, false);
			
			return identificationTransaction;
		}
	}
	
	private final class FullIdentificationTransactionRowMapper 
	implements RowMapper<IdentificationTransaction> {
		public IdentificationTransaction mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			IdentificationTransaction identificationTransaction = buildIdentificationTransaction(rs, true);
			
			return identificationTransaction;
		}
	}
	
	private IdentificationTransaction buildIdentificationTransaction(ResultSet rs, boolean includeSubscription)
			throws SQLException {
		IdentificationTransaction identificationTransaction = new IdentificationTransaction();
		identificationTransaction.setTransactionNumber( rs.getString("transaction_number") );
		identificationTransaction.setOtn(rs.getString("otn"));
		identificationTransaction.setTimestamp(toDateTime(rs.getTimestamp("report_timestamp")));
		identificationTransaction.setOwnerOri(rs.getString("owner_ori"));
		identificationTransaction.setOwnerProgramOca(rs.getString("owner_program_oca"));
		identificationTransaction.setIdentificationCategory(rs.getString("identification_category"));
		identificationTransaction.setArchived(BooleanUtils.isTrue(rs.getBoolean("archived")));
		identificationTransaction.setAvailableForSubscriptionStartDate(toDateTime(rs.getTimestamp("Available_For_Subscription_Start_Date")));

		Integer subjectId = rs.getInt("subject_id");
		
		if (subjectId != null){
			Subject subject = buildSubject(rs);
			identificationTransaction.setSubject(subject);
		}
		
		if (includeSubscription){
			identificationTransaction.setHavingSubsequentResults(rs.getBoolean("having_subsequent_result"));
			Integer subscriptionId = rs.getInt("id"); 
			
			if (subscriptionId != null && subscriptionId > 0){
				Subscription subscription = buildSubscription(rs); 
				identificationTransaction.setSubscription(subscription);
			}
		}
		return identificationTransaction;
	}
	
	private Subscription buildSubscription(ResultSet rs) throws SQLException {
		Subscription subscription = new Subscription(); 
		subscription.setId(rs.getInt("id"));
		subscription.setStartDate(toDateTime(rs.getDate("startDate")));
		subscription.setEndDate(toDateTime(rs.getDate("endDate")));
		subscription.setLastValidationDate(toDateTime(rs.getDate("lastValidationDate")));
		subscription.setActive(rs.getInt("active"));
		subscription.setTopic(rs.getString("topic"));
		subscription.setValidationDueDate(toDateTime(rs.getDate("validationDueDate")));
		return subscription;
	}

	private Subject buildSubject(ResultSet rs) throws SQLException {
		Subject subject = new Subject();

		subject.setSubjectId(rs.getInt("subject_id"));
		subject.setUcn(rs.getString("ucn"));
		subject.setCriminalSid(rs.getString("CRIMINAL_SID"));
		subject.setCivilSid(rs.getString("CIVIL_SID"));
		subject.setFirstName(rs.getString("FIRST_NAME")); 
		subject.setLastName(rs.getString("LAST_NAME")); 
		subject.setMiddleInitial(rs.getString("MIDDLE_INITIAL")); 
		subject.setDob(toDateTime(rs.getDate("DOB")));
		subject.setSexCode(rs.getString("SEX_CODE"));
		return subject;
	}

	@Override
	public void updateSubject(Subject subject) {
		Map<String, Object> paramMap = new HashMap<String, Object>(); 
		
		final String SUBJECT_UPDATE="UPDATE identification_subject SET "
				+ "ucn = :ucn, "
				+ "criminal_sid = :criminalSid, "
				+ "civil_sid = :civilSid, "
				+ "first_name = :firstName, "
				+ "last_name = :lastName, "
				+ "middle_initial = :middelInitial, "
				+ "dob = :dob, "
				+ "sex_code = :sexCode "
				+ "WHERE subject_id = :subjectId";

		paramMap.put("ucn", subject.getUcn()); 
		paramMap.put("criminalSid", subject.getCriminalSid()); 
		paramMap.put("civilSid", subject.getCivilSid()); 
		paramMap.put("firstName", subject.getFirstName()); 
		paramMap.put("lastName", subject.getLastName()); 
		paramMap.put("middelInitial", subject.getMiddleInitial()); 
		paramMap.put("dob", subject.getDob() == null? null:subject.getDob().toDate()); 
		paramMap.put("sexCode", subject.getSexCode()); 
		paramMap.put("subjectId", subject.getSubjectId()); 
		
		namedParameterJdbcTemplate.update(SUBJECT_UPDATE, paramMap);
	}
	
	@Override
	public List<CivilInitialResults> getCivilInitialResults(String ownerOri) {
		final String CIVIL_INITIAL_RESULTS_SELECT = "SELECT c.*, t.identification_category, t.report_timestamp, "
				+ "t.otn, t.owner_ori, t.owner_program_oca, t.archived, t.available_for_subscription_start_date, s.* "
				+ "FROM civil_initial_results c "
				+ "LEFT OUTER JOIN identification_transaction t ON t.transaction_number = c.transaction_number "
				+ "LEFT OUTER JOIN identification_subject s ON s.subject_id = t.subject_id "
				+ "WHERE t.owner_ori = ?";
		
		List<CivilInitialResults> civilIntialResults = 
				jdbcTemplate.query(CIVIL_INITIAL_RESULTS_SELECT, 
						new CivilInitialResultsRowMapper(), ownerOri);
		return civilIntialResults;
	}

	private final class CivilInitialResultsRowMapper implements
			RowMapper<CivilInitialResults> {
		public CivilInitialResults mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			CivilInitialResults civilInitialResults = buildCivilIntialResult(rs);
			
			civilInitialResults.setIdentificationTransaction(buildIdentificationTransaction(rs, false));

			return civilInitialResults;
		}

	}
	
	private CivilInitialResults buildCivilIntialResult(ResultSet rs) throws SQLException {
		CivilInitialResults civilInitialResults = new CivilInitialResults();
		civilInitialResults.setId(rs.getInt("civil_initial_result_id"));
		civilInitialResults.setTransactionNumber(rs.getString("transaction_number"));
		civilInitialResults.setResultsSender(ResultSender.values()[rs.getInt("results_sender_id") - 1]);
		try{
			civilInitialResults.setSearchResultFile(ZipUtils.unzip(rs.getBytes("search_result_file")));
		}
		catch(Exception e){
			log.error("Got exception extracting the search result file for " + 
					civilInitialResults.getTransactionNumber(), e);
		}
		civilInitialResults.setTimestamp(toDateTime(rs.getTimestamp("report_timestamp")));
		return civilInitialResults;
	}

	@Override
	public Integer getCivilIntialResultsId(String transactionNumber,
			ResultSender resultSender) {
		final String CIVIL_INITIAL_RESULTS_ID_SELECT = "SELECT t.civiL_INITIAL_RESULT_ID  "
				+ "FROM CIVIL_INITIAL_RESULTS t "
				+ "WHERE t.TRANSACTION_NUMBER  = ? AND RESULTS_SENDER_ID = ?";
		
		List<Integer> ids = jdbcTemplate.queryForList(CIVIL_INITIAL_RESULTS_ID_SELECT, 
				Integer.class, transactionNumber, resultSender.ordinal() + 1);
		
		return DataAccessUtils.singleResult(ids);
	}

	@Override
	public List<IdentificationTransaction> getCivilIdentificationTransactions(
			SAMLTokenPrincipal token, IdentificationResultSearchRequest searchRequest) {
		
		StringBuilder sb = new StringBuilder();
		sb.append( "SELECT t.transaction_number, t.identification_category, "
				+ "t.report_timestamp, t.otn, t.owner_ori,  t.owner_program_oca, t.archived, t.available_for_subscription_start_date, "
				+ "s.*, sub.*, "
				+ "(select count(*) > 0 from subsequent_results subsq where subsq.ucn = s.ucn) as having_subsequent_result "
				+ "FROM identification_transaction t "
				+ "LEFT OUTER JOIN identification_subject s ON s.subject_id = t.subject_id "
				+ "LEFT OUTER JOIN subscription sub ON sub.id = t.subscription_id "
				+ "WHERE (select count(*)>0 from "
				+ "	civil_initial_results c where c.transaction_number = t.transaction_number) "
				+ "	AND (:firstName is null OR upper(s.first_name) like concat(upper(:firstName), '%')) "
				+ " AND (:lastName is null OR upper(s.last_name) like concat(upper(:lastName), '%' ) )"
				+ "	AND (:otn is null OR t.otn = :otn ) "
				+ "	AND (:startDate is null OR t.report_timestamp >= :startDate ) "
				+ "	AND (:endDate is null OR t.report_timestamp <= :endDate ) "
				+ "	AND (:excludeArchived = false OR t.archived != true ) "
				+ "	AND (:excludeSubscribed = false OR (t.archived = true OR sub.id is null OR sub.id <= 0 OR sub.active = false )) "
				+ "	AND (:excludeAvailableForSubscription  = false OR (t.archived = true OR (sub.id > 0 AND sub.active = true))) "
				+ "	AND ( ( :identificationReasonCodeJoined ) is null OR t.identification_category in ( :identificationReasonCode )) ");
		
		Map<String, Object> paramMap = new HashMap<String, Object>(); 
		paramMap.put("firstName", searchRequest.getFirstName() );
		paramMap.put("lastName", searchRequest.getLastName()); 
		paramMap.put("otn", searchRequest.getOtn()); 
		paramMap.put("startDate", searchRequest.getReportedDateStartDate()); 
		paramMap.put("endDate", getMaxOfDay(searchRequest.getReportedDateEndDate())); 
		paramMap.put("excludeArchived", isExcluding(searchRequest.getIdentificationTransactionStatus(), IdentificationTransactionState.Archived)); 
		paramMap.put("excludeSubscribed", isExcluding(searchRequest.getIdentificationTransactionStatus(), IdentificationTransactionState.Subscribed)); 
		paramMap.put("excludeAvailableForSubscription", isExcluding(searchRequest.getIdentificationTransactionStatus(), IdentificationTransactionState.Available_for_Subscription)); 
		paramMap.put("identificationReasonCodeJoined", searchRequest.getCivilIdentificationReasonCodes().isEmpty() ? null : StringUtils.join(searchRequest.getCivilIdentificationReasonCodes(), "")); 
		paramMap.put("identificationReasonCode", searchRequest.getCivilIdentificationReasonCodes().isEmpty() ? null : new HashSet<String>(searchRequest.getCivilIdentificationReasonCodes())); 

        String ori = SAMLTokenUtils.getAttributeValueFromSamlToken(token, SamlAttribute.EmployerORI); 
        String federationId = SAMLTokenUtils.getAttributeValueFromSamlToken(token, SamlAttribute.FederationId);
        
        log.info("ORI: " + ori + " federation ID: " + federationId);
        
        boolean isNotSuperUser = isNotSuperUser(ori, federationId); 
        log.info("Is super super user? " + BooleanUtils.isNotTrue(isNotSuperUser));
        List<String> viewableAgencies = getViewableAgencies(ori, federationId); 
        
		if ( isNotSuperUser){
			
			if (viewableAgencies != null && !viewableAgencies.isEmpty()){
				sb.append( "AND (t.owner_ori in ( :oriList )) "); 
				paramMap.put("oriList", viewableAgencies);
			}
			else {
				sb.append( "AND (t.owner_ori = :ori) "); 
				paramMap.put("ori", ori);
			
				if ( isNotCivilAgencyUser(ori) ){
					sb.append ( " AND (t.identification_category in ( :identificationCategoryList )) ");
					List<String> identificationCategorys = getViewableIdentificationCategories(token, 
							"CIVIL"); 
					paramMap.put("identificationCategoryList", identificationCategorys.isEmpty() ? null : identificationCategorys);
				}
			}
		}
		
		sb.append("ORDER BY t.report_timestamp DESC "); 
		List<IdentificationTransaction> identificationTransactions = 
				namedParameterJdbcTemplate.query( sb.toString(), paramMap,
						new FullIdentificationTransactionRowMapper());
		return identificationTransactions;
	}

	private boolean isExcluding(List<String> statusCriteria, IdentificationTransactionState state) {
		return statusCriteria!= null && statusCriteria.size() > 0 && !statusCriteria.contains(state.toString());
	}

	private boolean isSuperUser(String ori, String federationId) {
		if (StringUtils.isBlank(ori) || StringUtils.isBlank(federationId)){
			return false; 
		}
		
		final String sql = "SELECT count(*) = 1 FROM ojbc_user u "
				+ "LEFT JOIN agency_profile a ON a.agency_id = u.agency_id "
				+ "WHERE a.agency_ori = ? AND u.federation_id = ? "
				+ "		AND super_user_indicator = true"; 
		
		return jdbcTemplate.queryForObject(sql, Boolean.class, ori, federationId);
	}
	
	private boolean isNotSuperUser(String ori, String federationId) {
		return !isSuperUser(ori , federationId);
	}

	private List<String> getViewableAgencies(String ori, String federationId) {
		
		if (StringUtils.isBlank(ori) || StringUtils.isBlank(federationId)){
			return null; 
		}
		
		final String sql = "SELECT ap.agency_ori FROM ojbc_user u "
				+ "LEFT JOIN agency_profile ua ON ua.agency_id = u.agency_id "
				+ "LEFT JOIN agency_super_user asu ON asu.ojbc_user_id = u.ojbc_user_id "
				+ "LEFT JOIN agency_profile ap ON ap.agency_id = asu.supervised_agency "
				+ "WHERE u.federation_id = ? and ua.agency_ori = ?";
		return jdbcTemplate.queryForList(sql, String.class, federationId, ori);
	}
	
	private boolean isCivilAgencyUser(String ori) {
		if (StringUtils.isBlank(ori)){
			return false;
		}
		
		final String sql = "SELECT count(*) = 1 FROM agency_profile a "
				+ "	WHERE a.agency_ori=? AND civil_agency_indicator = true"; 
		return jdbcTemplate.queryForObject(sql, Boolean.class, ori);
	}
	
	private boolean isNotCivilAgencyUser(String ori) {
		return !isCivilAgencyUser(ori);
	}
	
	public List<String> getViewableIdentificationCategories(
		SAMLTokenPrincipal token, String identificationCategoryType) {
		final String sql = "select i.identification_category_code from identification_category i "
				+ "left join job_title_privilege j on j.identification_category_id = i.identification_category_id "
				+ "left join job_title t on t.job_title_id = j.job_title_id "
				+ "left join department d on d.department_id = t.department_id "
				+ "left join agency_profile a on a.agency_id = d.agency_id "
				+ "where identification_category_type = :identificationCategoryType  "
				+ "		AND agency_ori = :agencyOri "
				+ "		AND department_name = :departmentName "
				+ "		AND ( title_description = :titleDescription OR title_description = 'Any')";
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("identificationCategoryType", identificationCategoryType);
		paramMap.put("agencyOri", SAMLTokenUtils.getAttributeValueFromSamlToken(token, SamlAttribute.EmployerORI));
		paramMap.put("departmentName", SAMLTokenUtils.getAttributeValueFromSamlToken(token, SamlAttribute.EmployerSubUnitName));
		paramMap.put("titleDescription", SAMLTokenUtils.getAttributeValueFromSamlToken(token, SamlAttribute.EmployeePositionName));
		
		List<String> identificationCategories = namedParameterJdbcTemplate.queryForList(sql, paramMap, String.class);
		return identificationCategories;
	}

	@Override
	public List<IdentificationTransaction> getCriminalIdentificationTransactions(
			SAMLTokenPrincipal token, IdentificationResultSearchRequest searchRequest) {
		StringBuilder sqlStringBuilder = new StringBuilder("SELECT t.transaction_number, t.identification_category, "
				+ "t.report_timestamp, t.otn, t.owner_ori,  t.owner_program_oca, t.archived, t.available_for_subscription_start_date, "
				+ "s.* "
				+ "FROM identification_transaction t "
				+ "LEFT OUTER JOIN identification_subject s ON s.subject_id = t.subject_id ");
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("firstName", searchRequest.getFirstName());
		paramMap.put("lastName", searchRequest.getLastName()); 
		paramMap.put("otn", searchRequest.getOtn()); 
		paramMap.put("startDate", searchRequest.getReportedDateStartDate()); 
		paramMap.put("endDate", getMaxOfDay(searchRequest.getReportedDateEndDate())); 
		paramMap.put("excludeArchived", isExcluding(searchRequest.getIdentificationTransactionStatus(), IdentificationTransactionState.Archived)); 
		paramMap.put("excludeAvailableForSubscription", isExcluding(searchRequest.getIdentificationTransactionStatus(), IdentificationTransactionState.Available_for_Subscription)); 
		paramMap.put("identificationReasonCodeJoined", searchRequest.getCriminalIdentificationReasonCodes().isEmpty() ? null : StringUtils.join(searchRequest.getCriminalIdentificationReasonCodes(),""));
		paramMap.put("identificationReasonCode", searchRequest.getCriminalIdentificationReasonCodes().isEmpty() ? null : searchRequest.getCriminalIdentificationReasonCodes());
		
        String ori = SAMLTokenUtils.getAttributeValueFromSamlToken(token, SamlAttribute.EmployerORI); 
        String federationId = SAMLTokenUtils.getAttributeValueFromSamlToken(token, SamlAttribute.FederationId); 
        
        log.info("ORI: " + ori + " federation ID: " + federationId);
        
		if ( isSuperUser(ori, federationId)){
			sqlStringBuilder.append(" WHERE " ); 
		}
		else {
	        List<String> viewableAgencies = getViewableAgencies(ori, federationId); 
	        
	        if (viewableAgencies != null && !viewableAgencies.isEmpty()){
				sqlStringBuilder.append(" WHERE t.owner_ori in (:oriList) AND " ); 
				paramMap.put("oriList", viewableAgencies);
	        }
	        else {
				sqlStringBuilder.append(" WHERE t.owner_ori = :ori AND " ); 
				paramMap.put("ori", ori);
			
				sqlStringBuilder.append(" t.identification_category in ( :identificationCategoryList ) AND ");
				
				List<String> identificationCategorys = 
						getViewableIdentificationCategories(token, "CRIMINAL"); 
				paramMap.put("identificationCategoryList", identificationCategorys.isEmpty() ? null : identificationCategorys);
			}
		}
		
		sqlStringBuilder.append(" (select count(*)>0 from criminal_initial_results c where c.transaction_number = t.transaction_number) ");
		sqlStringBuilder.append(
				  " AND (:firstName is null OR upper(s.first_name) like concat(upper(:firstName), '%')) "
				+ " AND (:lastName is null OR upper(s.last_name) like concat(upper(:lastName), '%' ) )"
				+ "	AND (:otn is null OR t.otn = :otn ) "
				+ "	AND (:startDate is null OR t.report_timestamp >= :startDate ) "
				+ "	AND (:endDate is null OR t.report_timestamp <= :endDate ) "
				+ "	AND (:excludeArchived = false OR t.archived != true ) "
				+ "	AND (:excludeAvailableForSubscription  = false OR t.archived = true) "
				+ "	AND (( :identificationReasonCodeJoined ) is null OR identification_category in (:identificationReasonCode)) ");
		
		sqlStringBuilder.append("ORDER BY t.report_timestamp DESC "); 
		List<IdentificationTransaction> identificationTransactions = 
				namedParameterJdbcTemplate.query( sqlStringBuilder.toString(), paramMap,  
						new IdentificationTransactionRowMapper());
		return identificationTransactions;
	}

	@Override
	public List<CivilInitialResults> getIdentificationCivilInitialResults(
			String transactionNumber) {
		final String CIVIL_INITIAL_RESULTS_BY_TRANSACTION_NUMBER = "SELECT c.*, r.*, i.*, s.*, a.AGENCY_NAME, sub.*, "
				+ "(select count(*) > 0 from subsequent_results subsq where subsq.ucn = s.ucn) as having_subsequent_result "
				+ "FROM civil_initial_results c "
				+ "LEFT OUTER JOIN IDENTIFICATION_TRANSACTION i ON i.transaction_number = c.transaction_number "
				+ "LEFT OUTER JOIN IDENTIFICATION_SUBJECT s ON s.SUBJECT_ID = i.SUBJECT_ID "
				+ "LEFT OUTER JOIN AGENCY_PROFILE a ON a.AGENCY_ORI = i.OWNER_ORI "
				+ "LEFT OUTER JOIN SUBSCRIPTION sub ON sub.id = i.subscription_id "
				+ "LEFT OUTER JOIN CIVIL_INITIAL_RAP_SHEET r ON r.CIVIL_INITIAL_RESULT_ID = c.CIVIL_INITIAL_RESULT_ID "
				+ "WHERE c.transaction_number = ?";
		
		List<CivilInitialResults> results= 
				jdbcTemplate.query(CIVIL_INITIAL_RESULTS_BY_TRANSACTION_NUMBER, 
						new CivilInitialResultsResultSetExtractor(), transactionNumber);
		return results;
	}

	private class CivilInitialResultsResultSetExtractor implements ResultSetExtractor<List<CivilInitialResults>> {

		@Override
		public List<CivilInitialResults> extractData(ResultSet rs)
				throws SQLException, DataAccessException {
            Map<Integer, CivilInitialResults> map = new HashMap<Integer, CivilInitialResults>();
            CivilInitialResults civilInitialResults = null;
            while (rs.next()) {
                Integer civilIntialResultId = rs.getInt("civil_initial_result_id" ); 
                civilInitialResults  = map.get( civilIntialResultId );
                if ( civilInitialResults  == null){
                	civilInitialResults = buildCivilIntialResult(rs); 
                	map.put(civilIntialResultId, civilInitialResults); 
                	
                	IdentificationTransaction identificationTransaction = 
                			buildIdentificationTransaction(rs, true);
        			identificationTransaction.setOwnerAgencyName(rs.getString("AGENCY_NAME"));
                	civilInitialResults.setIdentificationTransaction(identificationTransaction);
                }
	              
               byte[] rapSheet = rs.getBytes("rap_sheet" );
               
               if (rapSheet != null){
            	   try{
            		   civilInitialResults.getRapsheets().add( ZipUtils.unzip(rapSheet) );
            	   }
            	   catch(Exception e){
            		   log.error("Got exception extracting the rapsheet for " + 
            			   civilInitialResults.getTransactionNumber(), e);
            	   }
	           }
            }
            return (List<CivilInitialResults>) new ArrayList<CivilInitialResults>(map.values());
		}

	}

	@Override
	public void updateFbiRapbackSubscription(
			FbiRapbackSubscription fbiRapbackSubscription) {
		final String FBI_RAP_BACK_SUBSCRIPTION_UPDATE ="update FBI_RAP_BACK_SUBSCRIPTION SET "
				+ "RAP_BACK_SUBSCRIPTION_TERM_CODE = :rapbackSubscriptionTermCode, "
				+ "RAP_BACK_EXPIRATION_DATE = :rapbackExpirationDate , "
				+ "RAP_BACK_START_DATE = :rapbackStartDate, "
				+ "RAP_BACK_TERM_DATE = :rapbackTermDate, "
				+ "RAP_BACK_OPT_OUT_IN_STATE_INDICATOR = :rapbackOptOutInStateIndicator, "
				+ "RAP_BACK_ACTIVITY_NOTIFICATION_FORMAT_CODE = :rapbackActivityNotificationFormatCode "
				+ "where FBI_SUBSCRIPTION_ID = :fbiSubscriptionId ";
		
		Map<String, Object> paramMap = new HashMap<String, Object>(); 
		paramMap.put("rapbackSubscriptionTermCode", fbiRapbackSubscription.getSubscriptionTerm()); 
		paramMap.put("rapbackExpirationDate", toSqlDate(fbiRapbackSubscription.getRapbackExpirationDate())); 
		paramMap.put("rapbackStartDate", toSqlDate(fbiRapbackSubscription.getRapbackStartDate())); 
		paramMap.put("rapbackTermDate", toSqlDate(fbiRapbackSubscription.getRapbackTermDate())); 
		paramMap.put("rapbackOptOutInStateIndicator", fbiRapbackSubscription.getRapbackOptOutInState()); 
		paramMap.put("rapbackActivityNotificationFormatCode", fbiRapbackSubscription.getRapbackActivityNotificationFormat()); 
		paramMap.put("fbiSubscriptionId", fbiRapbackSubscription.getFbiSubscriptionId()); 
		
		namedParameterJdbcTemplate.update(FBI_RAP_BACK_SUBSCRIPTION_UPDATE, paramMap);
	}

	@Override
	public void consolidateSidFederal(String currentSid, String newSid) {
		final String SID_CONSOLIDATION = "UPDATE identification_subject "
				+ "SET criminal_sid =(CASE WHEN criminal_sid = :currentSid THEN :newSid ELSE criminal_sid END), "
				+ "	   civil_sid = (CASE WHEN civil_sid=:currentSid THEN :newSid ELSE civil_sid END)";
		
		Map<String, String> paramMap = new HashMap<String, String>(); 
		paramMap.put("currentSid", currentSid);
		paramMap.put("newSid", newSid);
		
		this.namedParameterJdbcTemplate.update(SID_CONSOLIDATION, paramMap);
	}

	@Override
	@Transactional
	public void consolidateUcnFederal(String currentUcn, String newUcn) {
		final String FBI_SUBSCRIPTION_UCN_CONSOLIDATION = "UPDATE fbi_rap_back_subscription "
				+ "SET ucn = :newUcn "
				+ "WHERE ucn = :currentUcn";
		final String IDENTIFICATION_SUBJECT_UCN_CONSOLIDATION = "UPDATE identification_subject "
				+ "SET ucn = :newUcn "
				+ "WHERE ucn = :currentUcn";
		final String SUBSEQUENT_RESULTS_UCN_CONSOLIDATION = "UPDATE subsequent_results "
				+ "SET ucn = :newUcn "
				+ "WHERE ucn = :currentUcn";
		
		Map<String, String> paramMap = new HashMap<String, String>(); 
		paramMap.put("currentUcn", currentUcn);
		paramMap.put("newUcn", newUcn);
		
		this.namedParameterJdbcTemplate.update(FBI_SUBSCRIPTION_UCN_CONSOLIDATION, paramMap); 
		this.namedParameterJdbcTemplate.update(IDENTIFICATION_SUBJECT_UCN_CONSOLIDATION, paramMap); 
		this.namedParameterJdbcTemplate.update(SUBSEQUENT_RESULTS_UCN_CONSOLIDATION, paramMap); 
	}

	@Override
	public AgencyProfile getAgencyProfile(String ori) {
		final String AGENCY_PROFILE_SELECT_BY_ORI = "SELECT * FROM agency_profile a "
				+ "LEFT JOIN agency_contact_email e ON e.agency_id = a.agency_id "
				+ "LEFT JOIN agency_triggering_event at ON at.agency_id = a.agency_id "
				+ "LEFT JOIN triggering_event t ON t.triggering_event_id = at.triggering_event_id "
				+ "WHERE agency_ori = ?";
		
		List<AgencyProfile> agencyProfiles = jdbcTemplate.query(AGENCY_PROFILE_SELECT_BY_ORI, new AgencyProfileResultSetExtractor(), ori);
		return DataAccessUtils.singleResult(agencyProfiles);
	}

	private class AgencyProfileResultSetExtractor implements ResultSetExtractor<List<AgencyProfile>> {

		@Override
		public List<AgencyProfile> extractData(ResultSet rs)
				throws SQLException, DataAccessException {
            Map<Integer, AgencyProfile> map = new HashMap<Integer, AgencyProfile>();
            AgencyProfile agencyProfile = null;
            while (rs.next()) {
                Integer agencyProfileId = rs.getInt("agency_id" ); 
                agencyProfile  = map.get( agencyProfileId );
                if ( agencyProfile  == null){
                	agencyProfile = new AgencyProfile();
                	agencyProfile.setId(agencyProfileId);
                	agencyProfile.setAgencyName(rs.getString("agency_name"));
                	agencyProfile.setAgencyOri(rs.getString("agency_ori"));
                	agencyProfile.setFbiSubscriptionQualified(rs.getBoolean("fbi_subscription_qualification"));
                	agencyProfile.setStateSubscriptionQualified(rs.getBoolean("state_subscription_qualification"));

                	List<String> emails = new ArrayList<String>();
                	String email = rs.getString("agency_email");
                	if (StringUtils.isNotBlank(email)){
                		emails.add(email);
                	}
                	agencyProfile.setEmails(emails);
                	
                	List<String> triggeringEventCodes = new ArrayList<String>();
                	String triggeringEventCode = rs.getString("triggering_event_code");
                	if (StringUtils.isNotBlank(triggeringEventCode)){
                		triggeringEventCodes.add(triggeringEventCode);
                	}
                	agencyProfile.setTriggeringEventCodes(triggeringEventCodes);
                	map.put(agencyProfileId, agencyProfile);
                }
                else{
                	String email = rs.getString("agency_email");
                	if (StringUtils.isNotBlank(email) && !agencyProfile.getEmails().contains(email)){
                		agencyProfile.getEmails().add(email);
                	}
                	
                	String triggeringEventCode = rs.getString("triggering_event_code");
                	if (StringUtils.isNotBlank(triggeringEventCode) 
                			&& !agencyProfile.getTriggeringEventCodes().contains(triggeringEventCode)){
                		agencyProfile.getTriggeringEventCodes().add(triggeringEventCode);
                	}
                	
                }
	              
            }
            
            return (List<AgencyProfile>) new ArrayList<AgencyProfile>(map.values());

		}

	}

	@Override
	public int archiveCivilIdentifications() {
		log.info("Archiving records that have been available "
				+ "for subscription for over " + civilIdlePeriod + " days.");
		final String sql = "UPDATE identification_transaction t "
				+ "SET t.archived = true "
				+ "WHERE (select count(*)>0 FROM civil_initial_results c where c.transaction_number = t.transaction_number) "
				+ "	AND t.archived = false  AND t.available_for_subscription_start_date < ?";
		
		DateTime currentDate = new DateTime(); 
		DateTime comparableDate = currentDate.minusDays(civilIdlePeriod);
		log.info("Comparable Date:" + comparableDate);
		
		int updatedRows = jdbcTemplate.update(sql, comparableDate.toDate());
		log.info("Archived " + updatedRows + " rows that have been idle for over " + civilIdlePeriod + " days ");
		return updatedRows;
	}
	
	@Override
	public int archiveCriminalIdentifications() {
		log.info("Archiving records that have been available "
				+ "for subscription for over " + criminalIdlePeriod + " days.");
		final String sql = "UPDATE identification_transaction t "
				+ "SET t.archived = true "
				+ "WHERE (select count(*)>0 FROM criminal_initial_results c where c.transaction_number = t.transaction_number) "
				+ "AND t.archived = false  AND t.available_for_subscription_start_date < ?";
		
		DateTime currentDate = new DateTime(); 
		DateTime comparableDate = currentDate.minusDays(criminalIdlePeriod);
		log.info("Comparable Date:" + comparableDate);
		
		int updatedRows = jdbcTemplate.update(sql, comparableDate.toDate());
		log.info("Archived " + updatedRows + " rows that have been idle for over " + criminalIdlePeriod + " days ");
		return updatedRows;
	}

	@Override
	public int archiveIdentificationResult(String transactionNumber) {
		log.info("Archiving record with transaction number " + transactionNumber);
		
		final String sql = "UPDATE identification_transaction t "
				+ "SET t.archived = true "
				+ "WHERE t.transaction_number = ?";
		int result = jdbcTemplate.update(sql, transactionNumber);
		return result;
	}

	@Override
	public List<SubsequentResults> getSubsequentResults(String transactionNumber) {
		log.info("Retreiving subsequent results by transaction number " + transactionNumber);
		
		final String sql ="SELECT subs.* FROM subsequent_results subs "
				+ "LEFT JOIN identification_subject s ON s.ucn = subs.ucn "
				+ "LEFT JOIN identification_transaction t ON t.subject_id = s.subject_id "
				+ "WHERE t.transaction_number = ?";
		
		List<SubsequentResults> subsequentResults = 
				jdbcTemplate.query(sql, new SubsequentResultRowMapper(), transactionNumber);
		return subsequentResults;
	}

	private final class SubsequentResultRowMapper implements RowMapper<SubsequentResults> {
		public SubsequentResults mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			SubsequentResults subsequentResult = new SubsequentResults();
			subsequentResult.setId(rs.getLong("subsequent_result_id"));
			subsequentResult.setUcn(rs.getString("ucn"));
			subsequentResult.setRapSheet(ZipUtils.unzip(rs.getBytes("rap_sheet")));
			subsequentResult.setResultsSender(ResultSender.values()[rs.getInt("results_sender_id") -1]);
			return subsequentResult;
		}
	}

	@Override
	public List<SubsequentResults> getSubsequentResultsByUcn(String ucn) {
		log.info("Retreiving subsequent results by FBI ID " + ucn);
		
		final String sql ="SELECT subs.* FROM subsequent_results subs "
				+ "WHERE subs.ucn = ?";
		
		List<SubsequentResults> subsequentResults = 
				jdbcTemplate.query(sql, new SubsequentResultRowMapper(), ucn);
		return subsequentResults;
	}

	@Override
	public List<CriminalInitialResults> getIdentificationCriminalInitialResults(
			String transactionNumber) {
		log.info("Retrieving criminal initial results by transaction number : " + transactionNumber);
		
		final String sql = "SELECT t.*, i.*, s.*, a.AGENCY_NAME "
				+ "FROM criminal_initial_results t "
				+ "LEFT OUTER JOIN IDENTIFICATION_TRANSACTION i ON i.transaction_number = t.transaction_number "
				+ "LEFT OUTER JOIN IDENTIFICATION_SUBJECT s ON s.SUBJECT_ID = i.SUBJECT_ID "
				+ "LEFT OUTER JOIN AGENCY_PROFILE a ON a.AGENCY_ORI = i.OWNER_ORI "
				+ "WHERE t.transaction_number = ?";
		List<CriminalInitialResults> criminalIntialResults = 
				jdbcTemplate.query(sql, new CriminalInitialResultsRowMapper(), transactionNumber);
		return criminalIntialResults;
	}

	private final class CriminalInitialResultsRowMapper implements
			RowMapper<CriminalInitialResults> {
		public CriminalInitialResults mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			CriminalInitialResults criminalInitialResults = new CriminalInitialResults();

			criminalInitialResults.setId(rs.getLong("criminal_initial_result_id"));
			criminalInitialResults.setTransactionNumber(rs
					.getString("transaction_number"));
			criminalInitialResults.setResultsSender(ResultSender.values()[rs
					.getInt("results_sender_id") - 1]);
			try {
				criminalInitialResults.setSearchResultFile(ZipUtils.unzip(rs
						.getBytes("search_result_file")));
			} catch (Exception e) {
				log.error("Got exception extracting the search result file for "
						+ criminalInitialResults.getTransactionNumber(), e);
			}
			criminalInitialResults.setTimestamp(toDateTime(rs
					.getTimestamp("report_timestamp")));

			IdentificationTransaction identificationTransaction = buildIdentificationTransaction(rs, false); 
			identificationTransaction.setOwnerAgencyName(rs.getString("AGENCY_NAME"));
			criminalInitialResults.setIdentificationTransaction(identificationTransaction);
			
			return criminalInitialResults;
		}

	}

	@Override
	public String getIdentificationCategoryType(String transactionNumber) {
		log.info("Retrieving identification category by transaction number : " + transactionNumber);
		
		final String sql = "SELECT identification_category_type FROM identification_transaction t "
				+ "LEFT JOIN identification_category c ON c.identification_category_code = t.identification_category "
				+ "WHERE t.transaction_number = ?"; 
		
		List<String> results = jdbcTemplate.queryForList(sql, String.class, transactionNumber);
		
		return DataAccessUtils.singleResult(results);
	}

	@Override
	public List<AgencyProfile> getAgencyProfiles(List<String> oris) {
		final String AGENCY_PROFILE_SELECT_BY_ORIS = "SELECT * FROM agency_profile a "
				+ "LEFT JOIN agency_contact_email e ON e.agency_id = a.agency_id "
				+ "LEFT JOIN agency_triggering_event at ON at.agency_id = a.agency_id "
				+ "LEFT JOIN triggering_event t ON t.triggering_event_id = at.triggering_event_id "
				+ "WHERE agency_ori in (:oris)";
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("oris", oris);
		
		List<AgencyProfile> agencyProfiles = namedParameterJdbcTemplate.query
				(AGENCY_PROFILE_SELECT_BY_ORIS, parameters, new AgencyProfileResultSetExtractor());
		return agencyProfiles;
	}

	@Override
	public Boolean isExistingTransactionNumber(String transactionNumber) {
		
		if (StringUtils.isBlank(transactionNumber)) return false; 
		
		final String sql = "SELECT count(*)>0 FROM identification_transaction t WHERE t.transaction_number = ?";
		
		Boolean existing = jdbcTemplate.queryForObject(sql, Boolean.class, transactionNumber);
		return existing;
	}

	@Override
	public List<CivilInitialResults> getCivilInitialResults(
			String transactionNumber, ResultSender resultSender) {
		final String CIVIL_INITIAL_RESULTS_SELECT = "SELECT c.*, t.identification_category, t.report_timestamp, "
				+ "t.otn, t.owner_ori, t.owner_program_oca, t.archived, t.available_for_subscription_start_date, s.* "
				+ "FROM civil_initial_results c "
				+ "LEFT OUTER JOIN identification_transaction t ON t.transaction_number = c.transaction_number "
				+ "LEFT OUTER JOIN identification_subject s ON s.subject_id = t.subject_id "
				+ "WHERE c.TRANSACTION_NUMBER  = ? AND c.RESULTS_SENDER_ID = ?";
		
		List<CivilInitialResults> civilIntialResults = 
				jdbcTemplate.query(CIVIL_INITIAL_RESULTS_SELECT, 
						new CivilInitialResultsRowMapper(), transactionNumber, resultSender.ordinal() + 1);
		return civilIntialResults;
	}

	public static java.sql.Timestamp getMaxOfDay(Date date){
		if (date != null){
			LocalDate localDate= new java.sql.Date(date.getTime()).toLocalDate();
			return java.sql.Timestamp.valueOf(LocalDateTime.of(localDate, LocalTime.MAX));
		}
		
		return null; 
	}

	@Override
	public void updateIdentificationCategory(String transactionNumber,
			String identificationCategory) {
		final String sql = "UPDATE identification_transaction "
				+ "SET identification_category = ? "
				+ "WHERE transaction_number  = ? ";
		jdbcTemplate.update(sql, identificationCategory, transactionNumber);
	}

}
