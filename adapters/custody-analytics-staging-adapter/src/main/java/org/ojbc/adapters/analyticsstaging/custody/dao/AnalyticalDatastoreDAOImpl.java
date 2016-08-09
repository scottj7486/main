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
package org.ojbc.adapters.analyticsstaging.custody.dao;

import static org.ojbc.util.helper.DaoUtils.setPreparedStatementVariable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.Address;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.BehavioralHealthAssessment;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.Booking;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.BookingArrest;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.BookingCharge;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.BookingSubject;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.CustodyRelease;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.CustodyStatusChange;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.CustodyStatusChangeArrest;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.CustodyStatusChangeCharge;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.KeyValue;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.Medication;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.Person;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.PersonRace;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.PersonSex;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.PrescribedMedication;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.Treatment;
import org.ojbc.util.helper.DaoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("analyticalDatastoreDAO")
public class AnalyticalDatastoreDAOImpl implements AnalyticalDatastoreDAO{

	private static final Log log = LogFactory.getLog(AnalyticalDatastoreDAOImpl.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;  
	
	@Override
	public Integer savePersonSex(final PersonSex personSex) {
        log.debug("Inserting row into PersonSex table");

        final String personSexInsertStatement="INSERT into PersonSex (PersonSexDescription) values (?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        	    new PreparedStatementCreator() {
        	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        	            PreparedStatement ps =
        	                connection.prepareStatement(personSexInsertStatement, new String[] {"PersonSexDescription"});
        	            ps.setString(1, personSex.getPersonSexDescription());
        	            return ps;
        	        }
        	    },
        	    keyHolder);

         return keyHolder.getKey().intValue();
	}
	
	@Override
	public Integer savePersonRace(final PersonRace personRace) {
        log.debug("Inserting row into PersonRace table");

        final String personRaceInsertStatement="INSERT into PersonRace (PersonRaceDescription) values (?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        	    new PreparedStatementCreator() {
        	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        	            PreparedStatement ps =
        	                connection.prepareStatement(personRaceInsertStatement, new String[] {"PersonRaceDescription"});
        	            ps.setString(1, personRace.getPersonRaceDescription());
        	            return ps;
        	        }
        	    },
        	    keyHolder);

         return keyHolder.getKey().intValue();
	}

	@Override
	public Integer savePerson(final Person person) {
        log.debug("Inserting row into Person table: " + person.toString());

        final String personStatement="INSERT into Person (PersonSexID, PersonRaceID, PersonBirthDate, "
        		+ "PersonUniqueIdentifier, BookingSubjectNumber, LanguageTypeID, "
        		+ "PersonEyeColor, PersonHairColor, PersonHeight, personHeightMeasureUnit,personWeight, "
        		+ "personWeightMeasureUnit, RegisteredSexOffender ) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        	    new PreparedStatementCreator() {
        	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        	            PreparedStatement ps =
        	                connection.prepareStatement(personStatement, 
        	                		new String[] {"PersonSexID", "PersonRaceID", "PersonBirthDate", 
        	                		"PersonUniqueIdentifier", "BookingSubjectNumber", "LanguageTypeID", 
        	                		"PersonEyeColor", "PersonHairColor", 
        	                		"PersonHeight", "personHeightMeasureUnit", "PersonWeight", 
        	                		"personWeightMeasureUnit", "RegisteredSexOffender"});
        	            
        	            setPreparedStatementVariable(person.getPersonSexId(), ps, 1);
        	            setPreparedStatementVariable(person.getPersonRaceId(), ps, 2);
        	            setPreparedStatementVariable(person.getPersonBirthDate(), ps, 3);
        	            
        	            ps.setString(4, String.valueOf(person.getPersonUniqueIdentifier()));
        	            setPreparedStatementVariable(person.getBookingSubjectNumber(), ps, 5);
        	            setPreparedStatementVariable(person.getLanguageId(), ps, 6);
        	            setPreparedStatementVariable(person.getPersonEyeColor(), ps, 7);
        	            setPreparedStatementVariable(person.getPersonHairColor(), ps, 8);
        	            setPreparedStatementVariable(person.getPersonHeight(), ps, 9);
        	            setPreparedStatementVariable(person.getPersonHeightMeasureUnit(), ps, 10);
        	            setPreparedStatementVariable(person.getPersonWeight(), ps, 11);
        	            setPreparedStatementVariable(person.getPersonWeightMeasureUnit(), ps, 12);
        	            setPreparedStatementVariable(person.getRegisteredSexOffender(), ps, 13);
        	            
        	            return ps;
        	        }
        	    },
        	    keyHolder);

         return keyHolder.getKey().intValue();
	}

	@Override
	public Person getPerson(Integer personId) {
		final String PERSON_SELECT = "SELECT * FROM Person p "
				+ "LEFT JOIN PersonSex s ON s.PersonSexID = p.PersonSexID "
				+ "LEFT JOIN PersonRace r ON r.PersonRaceID = p.PersonRaceID "
				+ "LEFT JOIN LanguageType l on l.languageTypeID = p.languageTypeID "
				+ "WHERE p.PersonID = ?"; 
		List<Person> persons = 
				jdbcTemplate.query(PERSON_SELECT, 
						new PersonRowMapper(), personId);
		return DataAccessUtils.singleResult(persons);
	}

	public class PersonRowMapper implements RowMapper<Person>
	{
		@Override
		public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
			Person person = new Person();
	    	
			person.setPersonBirthDate(rs.getDate("PersonBirthDate").toLocalDate());
			person.setPersonId(rs.getInt("PersonID"));
			person.setPersonRaceDescription(rs.getString("PersonRaceDescription"));
			person.setPersonSexDescription(rs.getString("PersonSexDescription"));
			person.setPersonRaceCode(rs.getString("PersonRaceCode"));
			person.setPersonSexCode(rs.getString("PersonSexCode"));
			person.setPersonRaceId(rs.getInt("PersonRaceId"));
			person.setPersonSexId(rs.getInt("PersonSexId"));
			person.setPersonUniqueIdentifier(rs.getString("PersonUniqueIdentifier"));
			person.setBookingSubjectNumber(rs.getString("BookingSubjectNumber"));
			person.setLanguageId(rs.getInt("LanguageTypeID"));
			person.setLanguage(rs.getString("LanguageTypeDescription"));
			person.setPersonEyeColor(rs.getString("personEyeColor"));
			person.setPersonHairColor(rs.getString("personHairColor"));
			person.setPersonHeight(rs.getString("personHeight"));
			person.setPersonHeightMeasureUnit(rs.getString("personHeightMeasureUnit"));
			person.setPersonWeight(rs.getString("personWeight"));
			person.setPersonWeightMeasureUnit(rs.getString("personWeightMeasureUnit"));
			person.setRegisteredSexOffender(rs.getBoolean("RegisteredSexOffender"));
			
	    	return person;
		}

	}

	@Override
	public void saveBehavioralHealthAssessments(
			List<BehavioralHealthAssessment> behavioralHealthAssessments) {
		log.debug("Inserting row into BehavioralHealthAssessment table: " + behavioralHealthAssessments);
		final String sqlString=
				"INSERT INTO BehavioralHealthAssessment (PersonID, SeriousMentalIllness,"
				+ "HighRiskNeeds, CareEpisodeStartDate, CareEpisodeEndDate, SubstanceAbuse, GeneralMentalHealthCondition,"
				+ "MedicaidIndicator, RegionalAuthorityAssignmentText ) values (?,?,?,?,?,?,?,?,?)";
		
        jdbcTemplate.batchUpdate(sqlString, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i)
                throws SQLException {
            	BehavioralHealthAssessment behavioralHealthAssessment = behavioralHealthAssessments.get(i);
                ps.setInt(1, behavioralHealthAssessment.getPersonId());
                setPreparedStatementVariable(behavioralHealthAssessment.getSeriousMentalIllness(), ps, 2);
                setPreparedStatementVariable(behavioralHealthAssessment.getHighRiskNeeds(), ps, 3);
                setPreparedStatementVariable(behavioralHealthAssessment.getCareEpisodeStartDate(), ps, 4);
                setPreparedStatementVariable(behavioralHealthAssessment.getCareEpisodeEndDate(), ps, 5);
                setPreparedStatementVariable(behavioralHealthAssessment.getSubstanceAbuse(), ps, 6);
                setPreparedStatementVariable(behavioralHealthAssessment.getGeneralMentalHealthCondition(), ps, 7);
                setPreparedStatementVariable(behavioralHealthAssessment.getMedicaidIndicator(), ps, 8);
                setPreparedStatementVariable(behavioralHealthAssessment.getRegionalAuthorityAssignmentText(), ps, 9);
            }
	            
            public int getBatchSize() {
                return behavioralHealthAssessments.size();
            }
        });

	}

	@Override
	public void saveBookingCharges(List<BookingCharge> bookingCharges) {
		log.info("Inserting row into BookingCharge table: " + bookingCharges);
		final String sqlString=
				"INSERT INTO BookingCharge (BookingArrestID, ChargeTypeID, AgencyTypeID, BondAmount, BondTypeID, NextCourtDate, NextCourtName) "
				+ "values (?,?,?,?,?,?,?)";
		
        jdbcTemplate.batchUpdate(sqlString, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i)
                throws SQLException {
            	BookingCharge bookingCharge = bookingCharges.get(i);
                ps.setInt(1, bookingCharge.getBookingArrestId());
                ps.setInt(2, bookingCharge.getChargeType().getKey());
                ps.setInt(3, bookingCharge.getAgencyId());
                setPreparedStatementVariable(bookingCharge.getBondAmount(), ps, 4);
                
                if (bookingCharge.getBondType() != null){
                    ps.setInt(5, bookingCharge.getBondType().getKey());
                }
                else{
                	ps.setNull(5, java.sql.Types.NULL);
                }
                setPreparedStatementVariable(bookingCharge.getNextCourtDate(), ps, 6);
                setPreparedStatementVariable(bookingCharge.getNextCourtName(), ps, 7);
            }
	            
            public int getBatchSize() {
                return bookingCharges.size();
            }
        });

	}

	@Override
	public Integer saveBookingSubject(BookingSubject bookingSubject) {
        log.info("Inserting row into BookingSubject table: " + bookingSubject.toString());

        final String sqlString="INSERT into BookingSubject ("
        		+ "RecidivistIndicator, " //1
        		+ "PersonID, " //2	
        		+ "PersonAge, " //3
        		+ "EducationLevelTypeID, " //4
        		+ "OccupationTypeID, " //5
        		+ "IncomeLevelTypeID, " //6
        		+ "HousingStatusTypeID, "
        		+ "militaryServiceStatusCode " //7
        		+ ") values (?,?,?,?,?,?,?,?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        	    new PreparedStatementCreator() {
        	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        	            PreparedStatement ps =
        	                connection.prepareStatement(sqlString, new String[] {"RecidivistIndicator", "PersonID", 
        	                		"PersonAge", "EducationLevelTypeID", "OccupationTypeID", "IncomeLevelTypeID",
        	                		"HousingStatusTypeID", "militaryServiceStatusCode"});
        	            
        	            ps.setShort(1, bookingSubject.getRecidivistIndicator().shortValue());
    	            	ps.setInt(2, bookingSubject.getPersonId());
    	            	setPreparedStatementVariable(bookingSubject.getPersonAge(), ps, 3);
    	            	setPreparedStatementVariable(bookingSubject.getEducationLevelId(), ps, 4);
    	            	setPreparedStatementVariable(bookingSubject.getOccupationId(), ps, 5);
    	            	setPreparedStatementVariable(bookingSubject.getIncomeLevelId(), ps, 6);
    	            	setPreparedStatementVariable(bookingSubject.getHousingStatusId(), ps, 7);
    	            	setPreparedStatementVariable(bookingSubject.getMilitaryServiceStatusCode(), ps, 8);
    	            	
        	            return ps;
        	        }
        	    },
        	    keyHolder);

         return keyHolder.getKey().intValue();
	}

	@Override
	public Integer saveBooking(final Booking booking) {
        log.debug("Inserting row into Booking table: " + booking.toString());
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        	    new PreparedStatementCreator() {
        	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        	        	
        	        	String sqlString="";
        	        	String[] insertArgs = null;
        	        	
        	        	if (booking.getBookingId() != null){
        	        		insertArgs = new String[] {"JurisdictionTypeID", "BookingReportDate", 
        	                		"BookingReportID" , "CaseStatusTypeID", "BookingDate", "FacilityID","BedTypeID",
        	                		"BookingSubjectID", "CommitDate", "BookingNumber", "ScheduledReleaseDate", "BookingID"};

        	        		sqlString="INSERT into booking (JurisdictionTypeID, BookingReportDate,"
        	        				+ "BookingReportID, CaseStatusTypeID, "
        	        				+ "BookingDate, FacilityID, BedTypeID, "
        	        				+ "BookingSubjectID, CommitDate, BookingNumber, ScheduledReleaseDate, BookingID) "
        	        				+ "values (?,?,?,?,?,?,?,?,?,?,?,?)";
        	        	}	
        	        	else{
        	        		insertArgs = new String[] {"JurisdictionTypeID", "BookingReportDate", 
        	                		"BookingReportID" , "CaseStatusTypeID", 
        	                		"BookingDate", "FacilityID","BedTypeID", 
        	                		"BookingSubjectID", "CommitDate", "BookingNumber", "ScheduledReleaseDate"};

        	        		sqlString="INSERT into booking (JurisdictionTypeID, BookingReportDate,"
        	        				+ "BookingReportID, CaseStatusTypeID, "
        	        				+ "BookingDate,"
        	        				+ "FacilityID, BedTypeID,  "
        	        				+ "BookingSubjectID, CommitDate, BookingNumber, ScheduledReleaseDate) "
        	        				+ "values (?,?,?,?,?,?,?,?,?,?,?)";
        	        		
        	        	}	
        	        			
        	        	
        	            PreparedStatement ps =
        	                connection.prepareStatement(sqlString, insertArgs);
        	            ps.setInt(1, booking.getJurisdictionId());
        	            
        	            setPreparedStatementVariable(booking.getBookingReportDate(), ps, 2);
        	            
        	            ps.setString(3, booking.getBookingReportId());

        	            setPreparedStatementVariable(booking.getCaseStatusId(), ps, 4);
        	            setPreparedStatementVariable(booking.getBookingDate(), ps, 5);
        	            setPreparedStatementVariable(booking.getFacilityId(), ps, 6);
        	            setPreparedStatementVariable(booking.getBedTypeId(), ps, 7);
        	            setPreparedStatementVariable(booking.getBookingSubjectId(), ps, 8);
        	            setPreparedStatementVariable(booking.getCommitDate(), ps, 9);
        	            setPreparedStatementVariable(booking.getBookingNumber(), ps, 10);
        	            setPreparedStatementVariable(booking.getScheduledReleaseDate(), ps, 11);
                        
        	            if (booking.getBookingId() != null){
        	            	setPreparedStatementVariable(booking.getBookingId(), ps, 12);
        	            }
        	            
        	            return ps;
        	        }
        	    },keyHolder);

        Integer returnValue = null;
        
        if (booking.getBookingId() != null)
        {
       	 	returnValue = booking.getBookingId();
        }	 
        else
        {
       	 	returnValue = keyHolder.getKey().intValue();
        }	 
        
        return returnValue;	
	}

	@Override
	@Transactional
	public void deleteBooking(Integer bookingId) {
		String bookingDeleteSql = "DELETE FROM booking WHERE bookingID = ?";
		String bookingArrestDeleteSql = "DELETE FROM BookingArrest WHERE bookingId = ?";
		String bookingChargeDeleteSql = "DELETE FROM BookingCharge WHERE bookingArrestId in (select bookingArrestID from BookingArrest a where a.bookingId = ?) ";
		String bookingSubjectIdSelectSql = "SELECT bookingSubjectId FROM booking WHERE bookingID = ?";
		String bookingSubjectDeleteSql="delete from BookingSubject where bookingSubjectId = ?";
		
		Integer bookingSubjectId = jdbcTemplate.queryForObject(bookingSubjectIdSelectSql, Integer.class, bookingId);
		jdbcTemplate.update(bookingChargeDeleteSql, bookingId);
		jdbcTemplate.update(bookingArrestDeleteSql, bookingId);
		jdbcTemplate.update(bookingDeleteSql, bookingId);
		
		if (bookingSubjectId != null){
			jdbcTemplate.update(bookingSubjectDeleteSql, bookingSubjectId);
		}
		
	}

	@Override
	public Booking getBookingByBookingReportId(String bookingReportId) {
		final String sql = "SELECT * FROM Booking b "
				+ "WHERE bookingReportId = ?";
		
		List<Booking> bookings = 
				jdbcTemplate.query(sql, 
						new BookingRowMapper(), bookingReportId);
		
		return DataAccessUtils.singleResult(bookings);
	}
	
	@Override
	public Booking getBookingByBookingNumber(String bookingNumber) {
		final String sql = "SELECT * FROM Booking b "
				+ "WHERE bookingNumber = ?";
		
		List<Booking> bookings = 
				jdbcTemplate.query(sql, 
						new BookingRowMapper(), bookingNumber);
		
		return DataAccessUtils.singleResult(bookings);
	}

	public class BookingRowMapper implements RowMapper<Booking>
	{
		@Override
		public Booking mapRow(ResultSet rs, int rowNum) throws SQLException {
			Booking booking = new Booking();
	    	
			booking.setBookingId(rs.getInt("BookingID"));
			booking.setJurisdictionId(rs.getInt("JurisdictionTypeID"));
			booking.setBookingReportDate(rs.getTimestamp("BookingReportDate").toLocalDateTime());
			booking.setBookingReportId(rs.getString("BookingReportID"));
			booking.setCaseStatusId(rs.getInt("CaseStatusTypeID"));
			booking.setBookingDate(rs.getTimestamp("BookingDate").toLocalDateTime());
			booking.setCommitDate(rs.getDate("CommitDate").toLocalDate());
			booking.setFacilityId(rs.getInt("FacilityID"));
			booking.setBedTypeId(rs.getInt("BedTypeID"));
			booking.setBookingSubjectId(rs.getInt("BookingSubjectID"));
			booking.setBookingNumber(rs.getString("BookingNumber"));
			booking.setScheduledReleaseDate( DaoUtils.getLocalDate(rs, "ScheduledReleaseDate"));
			
	    	return booking;
		}

	}

	@Override
	public Integer getPersonIdByUniqueId(String uniqueId) {
		String sqlString = "SELECT PersonID FROM Person WHERE PersonUniqueIdentifier = ?";
		
		List<Integer> personIds = jdbcTemplate.queryForList(sqlString, Integer.class, uniqueId);

		return DataAccessUtils.uniqueResult(personIds);
	}

	@Override
	public BookingSubject getBookingSubject(Integer bookingSubjectId) {
		final String sql = "SELECT * FROM BookingSubject b "
				+ "LEFT JOIN HousingStatusType s ON s.HousingStatusTypeID = b.HousingStatusTypeID "
				+ "LEFT JOIN EducationLevelType e ON e.EducationLevelTypeID = b.EducationLevelTypeID "
				+ "LEFT JOIN OccupationType o on o.OccupationTypeID = b.OccupationTypeID "
				+ "LEFT JOIN IncomeLevelType i on i.IncomeLevelTypeID = b.IncomeLevelTypeID "
				+ "WHERE b.bookingSubjectId = ?"; 
		List<BookingSubject> bookingSubjects = 
				jdbcTemplate.query(sql, new BookingSubjectRowMapper(), bookingSubjectId);
		return DataAccessUtils.uniqueResult(bookingSubjects);
	}

	public class BookingSubjectRowMapper implements RowMapper<BookingSubject>
	{
		@Override
		public BookingSubject mapRow(ResultSet rs, int rowNum) throws SQLException {
			BookingSubject bookingSubject = new BookingSubject();
	    	
			bookingSubject.setBookingSubjectId(rs.getInt("BookingSubjectID"));
			bookingSubject.setRecidivistIndicator(rs.getInt("RecidivistIndicator"));
			bookingSubject.setPersonId(rs.getInt("PersonId"));
			bookingSubject.setPersonAge(rs.getInt("PersonAge"));
			bookingSubject.setHousingStatusId(rs.getInt("HousingStatusTypeID"));
			bookingSubject.setEducationLevelId(rs.getInt("EducationLevelTypeID"));
			bookingSubject.setOccupationId(rs.getInt("OccupationTypeID"));
			bookingSubject.setIncomeLevelId(rs.getInt("IncomeLevelTypeID"));
			bookingSubject.setMilitaryServiceStatusCode(rs.getString("militaryServiceStatusCode"));
			
	    	return bookingSubject;
		}

	}

	@Override
	public List<BookingCharge> getBookingCharges(Integer bookingId) {
		final String sql = "SELECT * FROM BookingCharge b "
				+ "LEFT JOIN ChargeType c ON c.ChargeTypeID = b.ChargeTypeID "
				+ "LEFT JOIN BookingArrest a ON a.BookingArrestID = b.BookingArrestID "
				+ "LEFT JOIN Booking bk ON bk.BookingID = a.BookingID "
				+ "LEFT JOIN BondType bt ON bt.BondTypeID = b.BondTypeID "
				+ "WHERE bk.bookingID = ?"; 
		List<BookingCharge> bookingCharges = 
				jdbcTemplate.query(sql, new BookingChargeRowMapper(), bookingId);
		return bookingCharges;
	}

	public class BookingChargeRowMapper implements RowMapper<BookingCharge>
	{
		@Override
		public BookingCharge mapRow(ResultSet rs, int rowNum) throws SQLException {
			BookingCharge bookingCharge = new BookingCharge();
	    	
			bookingCharge.setBookingChargeId(rs.getInt("bookingChargeId"));
			bookingCharge.setBookingArrestId(rs.getInt("bookingArrestId"));
			
			KeyValue chargeType = new KeyValue( rs.getInt("chargeTypeId"), rs.getString("chargeTypeDescription"));
			bookingCharge.setChargeType( chargeType );
			
			bookingCharge.setAgencyId(rs.getInt("AgencyTypeID"));
			bookingCharge.setBondAmount(rs.getBigDecimal("bondAmount"));
			
			Integer bondTypeId = rs.getInt("bondTypeId"); 
			if (bondTypeId != null){
				KeyValue bondType = new KeyValue( rs.getInt("bondTypeId"), rs.getString("bondTypeDescription"));
				bookingCharge.setBondType( bondType );
			}
			
			bookingCharge.setNextCourtName( rs.getString("NextCourtName"));
			bookingCharge.setNextCourtDate( DaoUtils.getLocalDate(rs, "NextCourtDate"));
	    	return bookingCharge;
		}

	}

	@Override
	public List<BehavioralHealthAssessment> getBehavioralHealthAssessments(
			Integer personId) {
		final String sql = "SELECT * FROM BehavioralHealthAssessment b "
				+ "LEFT JOIN BehavioralHealthEvaluation e ON e.BehavioralHealthAssessmentID = b.BehavioralHealthAssessmentID "
				+ "LEFT JOIN BehavioralHealthType t ON t.BehavioralHealthTypeID = e.BehavioralHealthTypeID "
				+ "WHERE b.PersonID = ?"; 
		List<BehavioralHealthAssessment> behavioralHealthAssessments = 
				jdbcTemplate.query(sql, new BehavioralHealthAssessmentResultSetExtractor(), personId);
		return behavioralHealthAssessments;
	}

	public class BehavioralHealthAssessmentResultSetExtractor implements ResultSetExtractor<List<BehavioralHealthAssessment>>
	{
		@Override
		public List<BehavioralHealthAssessment> extractData(ResultSet rs) throws SQLException, DataAccessException {
			Map<Integer, BehavioralHealthAssessment> map = new HashMap<Integer, BehavioralHealthAssessment>();
			BehavioralHealthAssessment behavioralHealthAssessment = null;
            while (rs.next()) {
	            Integer behavioralHealthAssessmentId = rs.getInt("behavioralHealthAssessmentId" ); 
	            behavioralHealthAssessment  = map.get( behavioralHealthAssessmentId );
	            if ( behavioralHealthAssessment  == null){
	            	behavioralHealthAssessment = new BehavioralHealthAssessment();; 
	    			behavioralHealthAssessment.setBehavioralHealthAssessmentId(rs.getInt("behavioralHealthAssessmentId"));
	    			behavioralHealthAssessment.setPersonId(rs.getInt("PersonId"));
	    			behavioralHealthAssessment.setSeriousMentalIllness(rs.getBoolean("seriousMentalIllness"));
	    			behavioralHealthAssessment.setHighRiskNeeds(rs.getBoolean("highRiskNeeds"));
	    			behavioralHealthAssessment.setSubstanceAbuse(rs.getBoolean("substanceAbuse"));
	    			behavioralHealthAssessment.setGeneralMentalHealthCondition(rs.getBoolean("generalMentalHealthCondition"));
	    			behavioralHealthAssessment.setMedicaidIndicator(rs.getBoolean("medicaidIndicator"));
	    			behavioralHealthAssessment.setCareEpisodeStartDate(DaoUtils.getLocalDate(rs, "careEpisodeStartDate") );
	    			behavioralHealthAssessment.setCareEpisodeEndDate(DaoUtils.getLocalDate(rs, "careEpisodeEndDate"));
	    			behavioralHealthAssessment.setRegionalAuthorityAssignmentText(rs.getString("regionalAuthorityAssignmentText"));
	    			
	    			behavioralHealthAssessment.setBehavioralHealthTypes(new ArrayList<KeyValue>());
	            	map.put(behavioralHealthAssessmentId, behavioralHealthAssessment); 
	            }
	            
				KeyValue  behavioralHealthType = new KeyValue( rs.getInt("behavioralHealthTypeId"), rs.getString("BehavioralHealthDescription"));
				behavioralHealthAssessment.getBehavioralHealthTypes().add(behavioralHealthType);
            }
            return (List<BehavioralHealthAssessment>) new ArrayList<BehavioralHealthAssessment>(map.values());
			
		}

	}

	@Override
	public void saveCustodyRelease(CustodyRelease custodyRelease) {

		saveCustodyRelease(custodyRelease.getBookingId(),
				custodyRelease.getReleaseDate(), custodyRelease.getReportDate());
	}

	@Override
	public void saveCustodyRelease(Integer bookingId,
			LocalDateTime releaseDate, LocalDateTime reportDate) {

		final String sql = "Insert into CustodyRelease (BookingID, ReleaseDate, ReportDate) "
				+ "values (:bookingId, :releaseDate, :reportDate)";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("releaseDate", Timestamp.valueOf(releaseDate) );
		params.put("bookingId", bookingId );
		params.put("reportDate", Timestamp.valueOf(reportDate));
		
		namedParameterJdbcTemplate.update(sql, params);
	}

	@Override
	public CustodyRelease getCustodyReleaseByBookingId(Integer bookingId) {
		final String sql = "Select top 1 * from CustodyRelease where BookingId = ? order by ReportDate desc";
		
		List<CustodyRelease> custodyReleases = 
				jdbcTemplate.query(sql, new CustodyReleaseRowMapper(), bookingId);
		return DataAccessUtils.singleResult(custodyReleases);
		
	}

	public class CustodyReleaseRowMapper implements RowMapper<CustodyRelease>
	{
		@Override
		public CustodyRelease mapRow(ResultSet rs, int rowNum) throws SQLException {
			CustodyRelease custodyRelease = new CustodyRelease();
	    	
			custodyRelease.setBookingId(rs.getInt("bookingId"));
			custodyRelease.setReleaseDate(DaoUtils.getLocalDateTime(rs, "ReleaseDate"));
			custodyRelease.setReportDate( DaoUtils.getLocalDateTime(rs, "ReportDate"));
			
	    	return custodyRelease;
		}

	}

	@Override
	public Integer saveCustodyStatusChange(
			CustodyStatusChange custodyStatusChange) {
        log.debug("Inserting row into CustodyStatusChange table: " + custodyStatusChange.toString());
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        	    new PreparedStatementCreator() {
        	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        	        	
        	        	String sqlString="";
        	        	String[] insertArgs = null;
        	        	
        	        	if (custodyStatusChange.getCustodyStatusChangeId() != null){
       	        		insertArgs = new String[] {"JurisdictionTypeID", "ReportDate", 
        	                		"ReportID" , "CaseStatusTypeID", "BookingDate",
        	                		"FacilityID","BedTypeID", 
        	                		"BookingSubjectID", "CommitDate", "BookingId","ScheduledReleaseDate", 
        	                		"CustodyStatusChangeID"};

        	        		sqlString="INSERT into custodyStatusChange (JurisdictionTypeID, ReportDate,"
        	        				+ "ReportID, CaseStatusTypeID, BookingDate,  "
        	        				+ "FacilityID, BedTypeID, "
        	        				+ "BookingSubjectID, CommitDate, BookingId, ScheduledReleaseDate, "
        	        				+ "CustodyStatusChangeID) "
        	        				+ "values (?,?,?,?,?,?,?,?,?,?,?,?)";
        	        	}	
        	        	else{
        	        		insertArgs = new String[] {"JurisdictionTypeID", "ReportDate", 
        	                		"ReportID" , "CaseStatusTypeID", "BookingDate", 
        	                		"FacilityID","BedTypeID",
        	                		"BookingSubjectID", "CommitDate", "BookingId", "ScheduledReleaseDate"};

        	        		sqlString="INSERT into custodyStatusChange (JurisdictionTypeID, ReportDate,"
        	        				+ "ReportID, CaseStatusTypeID, BookingDate, "
        	        				+ "FacilityID, BedTypeID, "
        	        				+ "BookingSubjectID, CommitDate, BookingId, ScheduledReleaseDate) "
        	        				+ "values (?,?,?,?,?,?,?,?,?,?,?)";
        	        		
        	        	}	
        	        			
        	        	
        	            PreparedStatement ps =
        	                connection.prepareStatement(sqlString, insertArgs);
        	            ps.setInt(1, custodyStatusChange.getJurisdictionId());
        	            
        	            setPreparedStatementVariable(custodyStatusChange.getReportDate(), ps, 2);
        	            
        	            ps.setString(3, custodyStatusChange.getReportId());

        	            setPreparedStatementVariable(custodyStatusChange.getCaseStatusId(), ps, 4);
        	            setPreparedStatementVariable(custodyStatusChange.getBookingDate(), ps, 5);
        	            setPreparedStatementVariable(custodyStatusChange.getFacilityId(), ps, 6);
        	            setPreparedStatementVariable(custodyStatusChange.getBedTypeId(), ps, 7);
        	            setPreparedStatementVariable(custodyStatusChange.getBookingSubjectId(), ps, 8);
        	            setPreparedStatementVariable(custodyStatusChange.getCommitDate(), ps, 9);
        	            setPreparedStatementVariable(custodyStatusChange.getBookingId(), ps, 10);
        	            setPreparedStatementVariable(custodyStatusChange.getScheduledReleaseDate(), ps, 11);
        	            
        	            if (custodyStatusChange.getCustodyStatusChangeId() != null){
        	            	setPreparedStatementVariable(custodyStatusChange.getCustodyStatusChangeId(), ps, 12);
        	            }
        	            
        	            return ps;
        	        }
        	    },keyHolder);

        Integer returnValue = null;
        
        if (custodyStatusChange.getCustodyStatusChangeId() != null)
        {
       	 	returnValue = custodyStatusChange.getCustodyStatusChangeId();
        }	 
        else
        {
       	 	returnValue = keyHolder.getKey().intValue();
        }	 
        
        return returnValue;	
	}

	@Override
	public void saveCustodyStatusChangeCharges(
			List<CustodyStatusChangeCharge> custodyStatusChangeCharges) {
		log.info("Inserting row into CustodyStatusChangeCharge table: " + custodyStatusChangeCharges);
		final String sqlString=
				"INSERT INTO CustodyStatusChangeCharge (CustodyStatusChangeArrestID, ChargeTypeID, AgencyTypeID, "
				+ "BondAmount, BondTypeID, NextCourtDate, NextCourtName) "
				+ "values (?,?,?,?,?,?,?)";
		
        jdbcTemplate.batchUpdate(sqlString, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i)
                throws SQLException {
            	CustodyStatusChangeCharge custodyStatusChangeCharge = custodyStatusChangeCharges.get(i);
                ps.setInt(1, custodyStatusChangeCharge.getCustodyStatusChangeArrestId());
                ps.setInt(2, custodyStatusChangeCharge.getChargeType().getKey());
                ps.setInt(3, custodyStatusChangeCharge.getAgencyId());
                setPreparedStatementVariable(custodyStatusChangeCharge.getBondAmount(), ps, 4);
                
                if (custodyStatusChangeCharge.getBondType() != null){
                	 ps.setInt(5, custodyStatusChangeCharge.getBondType().getKey());
                }
                else{
                	ps.setNull(5, java.sql.Types.NULL);
                }
                
                setPreparedStatementVariable(custodyStatusChangeCharge.getNextCourtDate(), ps, 6);
                setPreparedStatementVariable(custodyStatusChangeCharge.getNextCourtName(), ps, 7);
            }
	            
            public int getBatchSize() {
                return custodyStatusChangeCharges.size();
            }
        });

		
	}

	public CustodyStatusChange getCustodyStatusChangeByReportId(String reportId) {
		final String sql = "SELECT * FROM CustodyStatusChange c "
				+ "WHERE ReportId = ?";
		
		List<CustodyStatusChange> custodyStatusChanges = 
				jdbcTemplate.query(sql, 
						new CustodyStatusChangeRowMapper(), reportId);
		
		return DataAccessUtils.singleResult(custodyStatusChanges);
	}

	public class CustodyStatusChangeRowMapper implements RowMapper<CustodyStatusChange>
	{
		@Override
		public CustodyStatusChange mapRow(ResultSet rs, int rowNum) throws SQLException {
			CustodyStatusChange custodyStatusChange = new CustodyStatusChange();
	    	
			custodyStatusChange.setCustodyStatusChangeId(rs.getInt("CustodyStatusChangeId"));
			custodyStatusChange.setJurisdictionId(rs.getInt("JurisdictionTypeID"));
			custodyStatusChange.setReportDate(DaoUtils.getLocalDateTime(rs, "ReportDate"));
			custodyStatusChange.setReportId(rs.getString("ReportID"));
			custodyStatusChange.setCaseStatusId(rs.getInt("CaseStatusTypeID"));
			custodyStatusChange.setBookingDate(DaoUtils.getLocalDateTime(rs, "BookingDate"));
			custodyStatusChange.setCommitDate(DaoUtils.getLocalDate(rs, "CommitDate"));
			custodyStatusChange.setFacilityId(rs.getInt("FacilityID"));
			custodyStatusChange.setBedTypeId(rs.getInt("BedTypeID"));
			custodyStatusChange.setBookingSubjectId(rs.getInt("BookingSubjectID"));
			custodyStatusChange.setBookingId(rs.getInt("BookingId"));
			custodyStatusChange.setScheduledReleaseDate( DaoUtils.getLocalDate(rs, "ScheduledReleaseDate"));
			
	    	return custodyStatusChange;
		}

	}

	@Override
	public List<CustodyStatusChangeCharge> getCustodyStatusChangeCharges(Integer custodyStatusChangeId) {
		final String sql = "SELECT * FROM CustodyStatusChangeCharge b "
				+ "LEFT JOIN ChargeType c ON c.ChargeTypeID = b.ChargeTypeID "
				+ "LEFT JOIN CustodyStatusChangeArrest a ON a.CustodyStatusChangeArrestId = b.CustodyStatusChangeArrestId "
				+ "LEFT JOIN CustodyStatusChange csc ON csc.CustodyStatusChangeId = a.CustodyStatusChangeId "
				+ "LEFT JOIN BondType bt ON bt.BondTypeID = b.BondTypeID "
				+ "WHERE csc.custodyStatusChangeId = ?"; 
		List<CustodyStatusChangeCharge> custodyStatusChangeCharges = 
				jdbcTemplate.query(sql, new CustodyStatusChangeChargeRowMapper(), custodyStatusChangeId);
		return custodyStatusChangeCharges;
	}

	public class CustodyStatusChangeChargeRowMapper implements RowMapper<CustodyStatusChangeCharge>
	{
		@Override
		public CustodyStatusChangeCharge mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			CustodyStatusChangeCharge custodyStatusChangeCharge = new CustodyStatusChangeCharge();
	    	
			custodyStatusChangeCharge.setCustodyStatusChangeChargeId(rs.getInt("CustodyStatusChangeChargeId"));
			custodyStatusChangeCharge.setCustodyStatusChangeArrestId(rs.getInt("CustodyStatusChangeArrestId"));
			
			KeyValue chargeType = new KeyValue( rs.getInt("chargeTypeId"), rs.getString("chargeTypeDescription"));
			custodyStatusChangeCharge.setChargeType( chargeType );
			
			custodyStatusChangeCharge.setAgencyId(rs.getInt("AgencyTypeID"));
			custodyStatusChangeCharge.setBondAmount(rs.getBigDecimal("bondAmount"));
			
			Integer bondTypeId = rs.getInt("bondTypeId"); 
			if (bondTypeId != null){
				KeyValue bondType = new KeyValue( rs.getInt("bondTypeId"), rs.getString("bondTypeDescription"));
				custodyStatusChangeCharge.setBondType( bondType );
			}
			
			custodyStatusChangeCharge.setNextCourtName( rs.getString("NextCourtName"));
			custodyStatusChangeCharge.setNextCourtDate( DaoUtils.getLocalDate(rs, "NextCourtDate"));
	    	return custodyStatusChangeCharge;
		}

	}

	@Override
	public BookingSubject getBookingSubjectByBookingNumberAndPersonId(String bookingNumber, Integer personId) {
		final String sql = "SELECT b.* FROM BookingSubject b "
				+ "LEFT JOIN Booking k ON k.BookingSubjectID = b.BookingSubjectID "
				+ "LEFT JOIN HousingStatusType s ON s.HousingStatusTypeID = b.HousingStatusTypeID "
				+ "LEFT JOIN EducationLevelType e ON e.EducationLevelTypeID = b.EducationLevelTypeID "
				+ "LEFT JOIN OccupationType o on o.OccupationTypeID = b.OccupationTypeID "
				+ "LEFT JOIN IncomeLevelType i on i.IncomeLevelTypeID = b.IncomeLevelTypeID "
				+ "WHERE k.bookingNumber = ? AND b.PersonId = ?"; 
		List<BookingSubject> bookingSubjects = 
				jdbcTemplate.query(sql, new BookingSubjectRowMapper(), bookingNumber, personId);
		return DataAccessUtils.uniqueResult(bookingSubjects);
	}

	@Override
	public Integer saveBookingArrest(BookingArrest bookingArrest) {
        log.debug("Inserting row into BookingArrest table: " + bookingArrest.toString());
        
        Integer locationId = saveAddress(bookingArrest.getAddress());
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        	    new PreparedStatementCreator() {
        	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        	        	
        	        	String sqlString="";
        	        	String[] insertArgs = null;
        	        	
        	        	if (bookingArrest.getBookingArrestId() != null){
        	        		insertArgs = new String[] {"bookingId", "locationId", "bookingArrestId"};

        	        		sqlString="INSERT into bookingArrest (bookingId, locationId, bookingArrestId) "
        	        				+ "values (?,?,?)";
        	        	}	
        	        	else{
        	        		insertArgs = new String[] {"bookingId", "locationId"};

        	        		sqlString="INSERT into bookingArrest (bookingId, locationId) "
        	        				+ "values (?,?)";
        	        		
        	        	}	
        	        	
        	            PreparedStatement ps =
        	                connection.prepareStatement(sqlString, insertArgs);
        	            ps.setInt(1, bookingArrest.getBookingId());
        	            
        	            setPreparedStatementVariable(locationId, ps, 2);
        	            
        	            if (bookingArrest.getBookingArrestId() != null){
        	            	setPreparedStatementVariable(bookingArrest.getBookingArrestId(), ps, 3);
        	            }
        	            
        	            return ps;
        	        }
        	    },keyHolder);

        Integer returnValue = null;
        
        if (bookingArrest.getBookingArrestId() != null)
        {
       	 	returnValue = bookingArrest.getBookingArrestId();
        }	 
        else
        {
       	 	returnValue = keyHolder.getKey().intValue();
        }	 
        
        return returnValue;	
	}

	@Override
	public Integer saveBehavioralHealthAssessment(BehavioralHealthAssessment assessment) {
        log.debug("Inserting row into BehavioralHealthAssessment table: " + assessment.toString());
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        	    new PreparedStatementCreator() {
        	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        	        	
        	        	String sqlString="";
        	        	String[] insertArgs = null;
        	        	
        	        	if (assessment.getBehavioralHealthAssessmentId()!= null){
        	        		insertArgs = new String[] {"personId", "seriousMentalIllness", 
        	                		"highRiskNeeds" , "careEpisodeStartDate", "careEpisodeEndDate", 
        	                		"substanceAbuse","generalMentalHealthCondition",
        	                		"medicaidIndicator", "regionalAuthorityAssignmentText", "behavioralHealthAssessmentId"};

        	        		sqlString="INSERT into BehavioralHealthAssessment (personId, seriousMentalIllness,"
        	        				+ "highRiskNeeds, careEpisodeStartDate, careEpisodeEndDate,"
        	        				+ "substanceAbuse, generalMentalHealthCondition, medicaidIndicator, "
        	        				+ "regionalAuthorityAssignmentText, behavioralHealthAssessmentId) "
        	        				+ "values (?,?,?,?,?,?,?,?,?,?)";
        	        	}	
        	        	else{
        	        		insertArgs = new String[] {"personId", "seriousMentalIllness", 
        	                		"highRiskNeeds" , "careEpisodeStartDate", "careEpisodeEndDate", 
        	                		"substanceAbuse","generalMentalHealthCondition",
        	                		"medicaidIndicator", "regionalAuthorityAssignmentText"};

        	        		sqlString="INSERT into BehavioralHealthAssessment (personId, seriousMentalIllness,"
        	        				+ "highRiskNeeds, careEpisodeStartDate, careEpisodeEndDate,"
        	        				+ "substanceAbuse, generalMentalHealthCondition, medicaidIndicator, "
        	        				+ "regionalAuthorityAssignmentText) "
        	        				+ "values (?,?,?,?,?,?,?,?,?)";
        	        	}	
        	        			
        	        	
        	            PreparedStatement ps =
        	                connection.prepareStatement(sqlString, insertArgs);
        	            ps.setInt(1, assessment.getPersonId());
        	            
        	            setPreparedStatementVariable(assessment.getSeriousMentalIllness(), ps, 2);
        	            setPreparedStatementVariable(assessment.getHighRiskNeeds(), ps, 3);
        	            setPreparedStatementVariable(assessment.getCareEpisodeStartDate(), ps, 4);
        	            setPreparedStatementVariable(assessment.getCareEpisodeEndDate(), ps, 5);
        	            setPreparedStatementVariable(assessment.getSubstanceAbuse(), ps, 6);
        	            setPreparedStatementVariable(assessment.getGeneralMentalHealthCondition(), ps, 7);
        	            setPreparedStatementVariable(assessment.getMedicaidIndicator(), ps, 8);
        	            setPreparedStatementVariable(assessment.getRegionalAuthorityAssignmentText(), ps, 9);
        	            
        	            if (assessment.getBehavioralHealthAssessmentId() != null){
        	            	setPreparedStatementVariable(assessment.getBehavioralHealthAssessmentId(), ps, 10);
        	            }
        	            
        	            return ps;
        	        }
        	    },keyHolder);

        Integer returnValue = null;
        
        if (assessment.getBehavioralHealthAssessmentId() != null)
        {
       	 	returnValue = assessment.getBehavioralHealthAssessmentId();
        }	 
        else
        {
       	 	returnValue = keyHolder.getKey().intValue();
        }	 
        
        return returnValue;	
	}

	@Override
	public Integer getMedicationId(String generalProductId, String itemName) {
        final String sql="SELECT medicationId FROM Medication WHERE GeneralProductID = ? AND ItemName = ?";
        
        List<Integer> medicationIds = jdbcTemplate.queryForList(sql, Integer.class, generalProductId, itemName);
        
		return DataAccessUtils.singleResult(medicationIds);
	}

	@Override
	public Integer saveMedication(String generalProductId, String itemName) {
        log.debug("Inserting row into the Medication table");

        final String sql="INSERT into Medication (GeneralProductID, ItemName) values (?,?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        	    new PreparedStatementCreator() {
        	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        	            PreparedStatement ps =
        	                connection.prepareStatement(sql, new String[] {"GeneralProductID","ItemName"});
        	            ps.setString(1, generalProductId);
        	            ps.setString(2, itemName);
        	            return ps;
        	        }
        	    },
        	    keyHolder);

         return keyHolder.getKey().intValue();
	}

	@Override
	public void saveTreatments(final List<Treatment> treatments) {
		log.info("Inserting row into Treatment table: " + treatments);
		final String sqlString=
				"INSERT INTO Treatment (BehavioralHealthAssessmentID, StartDate, EndDate, "
				+ "TreatmentCourtOrdered, TreatmentText, TreatmentProvider, TreatmentActive) "
				+ "values (?,?,?,?,?,?,?)";
		
        jdbcTemplate.batchUpdate(sqlString, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i)
                throws SQLException {
            	Treatment treatment = treatments.get(i);
                ps.setInt(1, treatment.getBehavioralHealthAssessmentID());
                setPreparedStatementVariable(treatment.getStartDate(), ps, 2);
                setPreparedStatementVariable(treatment.getEndDate(), ps, 3);
                setPreparedStatementVariable(treatment.getTreatmentCourtOrdered(), ps, 4);
                setPreparedStatementVariable(treatment.getTreatmentText(), ps, 5);
                setPreparedStatementVariable(treatment.getTreatmentProvider(), ps, 6);
                setPreparedStatementVariable(treatment.getTreatmentActive(), ps, 7);
            }
	            
            public int getBatchSize() {
                return treatments.size();
            }
        });
	}

	@Override
	public void saveBehavioralHealthEvaluations(
			Integer behavioralHealthAssessmentId, List<KeyValue> behavioralHealthTypes) {
		log.info("Inserting row into BehavioralHealthEvaluation table: " + behavioralHealthTypes);
		final String sqlString=
				"INSERT INTO BehavioralHealthEvaluation (BehavioralHealthAssessmentID, BehavioralHealthTypeID) "
				+ "values (?,?)";
		
        jdbcTemplate.batchUpdate(sqlString, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i)
                throws SQLException {
            	KeyValue behavioralHealthType = behavioralHealthTypes.get(i);
                ps.setInt(1, behavioralHealthAssessmentId);
                ps.setInt(2, behavioralHealthType.getKey());
            }
	            
            public int getBatchSize() {
                return behavioralHealthTypes.size();
            }
        });
		
	}

	@Override
	public void savePrescribedMedications(
			List<PrescribedMedication> prescribedMedications) {
		log.info("Inserting row into PrescribedMedication table: " + prescribedMedications);
		final String sqlString=
				"INSERT INTO PrescribedMedication (BehavioralHealthAssessmentID, MedicationID, MedicationDispensingDate, MedicationDoseMeasure) "
				+ "values (?,?,?,?)";
		
        jdbcTemplate.batchUpdate(sqlString, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i)
                throws SQLException {
            	PrescribedMedication prescribedMedication = prescribedMedications.get(i);
                ps.setInt(1, prescribedMedication.getBehavioralHealthAssessmentID());
                setPreparedStatementVariable(prescribedMedication.getMedicationId(), ps, 2);
                setPreparedStatementVariable(prescribedMedication.getMedicationDispensingDate(), ps, 3);
                setPreparedStatementVariable(prescribedMedication.getMedicationDoseMeasure(), ps, 4);
            }
	            
            public int getBatchSize() {
                return prescribedMedications.size();
            }
        });
	}

	@Override
	public Integer saveBehavioralHealthType(
			String evaluationDiagnosisDescriptionText) {
        log.debug("Inserting row into the BehavioralHealthType table");

        final String sql="INSERT into BehavioralHealthType (BehavioralHealthDescription) values (?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        	    new PreparedStatementCreator() {
        	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        	            PreparedStatement ps =
        	                connection.prepareStatement(sql, new String[] {"BehavioralHealthDescription"});
        	            ps.setString(1, evaluationDiagnosisDescriptionText);
        	            return ps;
        	        }
        	    },
        	    keyHolder);

         return keyHolder.getKey().intValue();
	}

	@Override
	public List<BookingArrest> getBookingArrests(Integer bookingId) {
		final String sql = "SELECT * FROM BookingArrest a "
				+ "LEFT JOIN location l ON l.locationId = a.locationId "
				+ "WHERE a.bookingID = ?"; 
		List<BookingArrest> bookingArrests = 
				jdbcTemplate.query(sql, new BookingArrestRowMapper(), bookingId);
		return bookingArrests;
	}

	public class BookingArrestRowMapper implements RowMapper<BookingArrest>
	{
		@Override
		public BookingArrest mapRow(ResultSet rs, int rowNum) throws SQLException {
			BookingArrest bookingArrest = new BookingArrest();
	    	
			bookingArrest.setBookingId(rs.getInt("bookingId"));
			bookingArrest.setBookingArrestId(rs.getInt("bookingArrestId"));

			Address address = buildAddress(rs);
			
			bookingArrest.setAddress(address);
	    	return bookingArrest;
		}

	}

	@Override
	public List<Treatment> getTreatments(Integer behavioralHealthAssessmentId) {
		final String sql = "SELECT * FROM Treatment t "
				+ "WHERE t.behavioralHealthAssessmentId = ?"; 
		List<Treatment> treatments = 
				jdbcTemplate.query(sql, new TreatmentRowMapper(), behavioralHealthAssessmentId);
		return treatments;
	}

	public class TreatmentRowMapper implements RowMapper<Treatment>
	{
		@Override
		public Treatment mapRow(ResultSet rs, int rowNum) throws SQLException {
			Treatment treatment = new Treatment();
	    	
			treatment.setTreatmentId(rs.getInt("treatmentId"));
			treatment.setBehavioralHealthAssessmentID(rs.getInt("behavioralHealthAssessmentID"));

			treatment.setStartDate(DaoUtils.getLocalDate(rs, "startDate"));
			treatment.setEndDate(DaoUtils.getLocalDate(rs, "endDate"));
			treatment.setTreatmentCourtOrdered(rs.getBoolean("treatmentCourtOrdered"));
			treatment.setTreatmentActive(rs.getBoolean("treatmentActive"));
			treatment.setTreatmentText(rs.getString("treatmentText"));
			treatment.setTreatmentProvider(rs.getString("treatmentProvider"));
	    	return treatment;
		}

	}


	@Override
	public List<PrescribedMedication> getPrescribedMedication(
			Integer behavioralHealthAssessmentId) {
		final String sql = "SELECT * FROM PrescribedMedication p "
				+ "LEFT OUTER JOIN Medication m ON m.MedicationID = p.MedicationID "
				+ "WHERE p.behavioralHealthAssessmentId = ?"; 
		List<PrescribedMedication> prescribedMedications = 
				jdbcTemplate.query(sql, new PrescribedMedicationRowMapper(), behavioralHealthAssessmentId);
		return prescribedMedications;
	}

	public class PrescribedMedicationRowMapper implements RowMapper<PrescribedMedication>
	{
		@Override
		public PrescribedMedication mapRow(ResultSet rs, int rowNum) throws SQLException {
			PrescribedMedication prescribedMedication = new PrescribedMedication();
	    	
			prescribedMedication.setPrescribedMedicationID(rs.getInt("prescribedMedicationID"));
			prescribedMedication.setBehavioralHealthAssessmentID(rs.getInt("behavioralHealthAssessmentID"));

			prescribedMedication.setMedicationId(rs.getInt("medicationID"));
			prescribedMedication.setMedicationDispensingDate(DaoUtils.getLocalDate(rs, "medicationDispensingDate"));
			prescribedMedication.setMedicationDoseMeasure(rs.getString("medicationDoseMeasure"));
			
			Medication medication = new Medication(); 
			
			medication.setMedicationId(prescribedMedication.getMedicationId());
			medication.setGeneralProductId(rs.getString("generalProductId"));
			medication.setItemName(rs.getString("itemName"));
			prescribedMedication.setMedication(medication);
			
	    	return prescribedMedication;
		}

	}

	@Override
	public Integer saveCustodyStatusChangeArrest(
			CustodyStatusChangeArrest custodyStatusChangeArrest) {
        log.debug("Inserting row into CustodyStatusChangeArrest table: " + custodyStatusChangeArrest.toString());
        
        Integer locationId = this.saveAddress(custodyStatusChangeArrest.getAddress());
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        	    new PreparedStatementCreator() {
        	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        	        	
        	        	String sqlString="";
        	        	String[] insertArgs = null;
        	        	
        	        	if (custodyStatusChangeArrest.getCustodyStatusChangeArrestId() != null){
        	        		insertArgs = new String[] {"custodyStatusChangeId", "locationId", "custodyStatusChangeArrestId"};

        	        		sqlString="INSERT into CustodyStatusChangeArrest (custodyStatusChangeId, locationId, custodyStatusChangeArrestId) "
        	        				+ "values (?,?,?)";
        	        	}	
        	        	else{
        	        		insertArgs = new String[] {"custodyStatusChangeId", "locationId"};

        	        		sqlString="INSERT into CustodyStatusChangeArrest (custodyStatusChangeId, locationId) "
        	        				+ "values (?,?)";
        	        		
        	        	}	
        	        	
        	            PreparedStatement ps =
        	                connection.prepareStatement(sqlString, insertArgs);
        	            ps.setInt(1, custodyStatusChangeArrest.getCustodyStatusChangeId());
        	            
        	            setPreparedStatementVariable(locationId, ps, 2);
        	            
        	            if (custodyStatusChangeArrest.getCustodyStatusChangeArrestId() != null){
        	            	setPreparedStatementVariable(custodyStatusChangeArrest.getCustodyStatusChangeArrestId(), ps, 3);
        	            }
        	            
        	            return ps;
        	        }
        	    },keyHolder);

        Integer returnValue = null;
        
        if (custodyStatusChangeArrest.getCustodyStatusChangeArrestId() != null)
        {
       	 	returnValue = custodyStatusChangeArrest.getCustodyStatusChangeArrestId();
        }	 
        else
        {
       	 	returnValue = keyHolder.getKey().intValue();
        }	 
        
        return returnValue;	
	}

	@Override
	public List<CustodyStatusChangeArrest> getCustodyStatusChangeArrests(
			Integer custodyStatusChangeId) {
		final String sql = "SELECT * FROM CustodyStatusChangeArrest a "
				+ "LEFT JOIN location l ON l.locationId = a.locationId "
				+ "WHERE a.CustodyStatusChangeID = ?"; 
		List<CustodyStatusChangeArrest> custodyStatusChangeArrests = 
				jdbcTemplate.query(sql, new CustodyStatusChangeArrestRowMapper(), custodyStatusChangeId);
		return custodyStatusChangeArrests;
	}

	public class CustodyStatusChangeArrestRowMapper implements RowMapper<CustodyStatusChangeArrest>
	{
		@Override
		public CustodyStatusChangeArrest mapRow(ResultSet rs, int rowNum) throws SQLException {
			CustodyStatusChangeArrest custodyStatusChangeArrest = new CustodyStatusChangeArrest();
	    	
			custodyStatusChangeArrest.setCustodyStatusChangeId(rs.getInt("custodyStatusChangeId"));
			custodyStatusChangeArrest.setCustodyStatusChangeArrestId(rs.getInt("custodyStatusChangeArrestId"));

			Address address = buildAddress(rs);
			
			custodyStatusChangeArrest.setAddress(address);
	    	return custodyStatusChangeArrest;
		}

	}
	
	private Address buildAddress(ResultSet rs) throws SQLException {
		Address address = new Address();
		address.setStreetNumber(rs.getString("streetNumber"));
		address.setStreetName(rs.getString("streetName"));
		address.setAddressSecondaryUnit(rs.getString("addressSecondaryUnit"));
		address.setCity(rs.getString("city"));
		address.setState(rs.getString("State"));
		address.setPostalcode(rs.getString("postalcode"));
		address.setArrestLocationLatitude(rs.getBigDecimal("ArrestLocationLatitude"));
		address.setArrestLocationLongitude(rs.getBigDecimal("ArrestLocationLongitude"));
		return address;
	}

	@Override
	public Integer getBookingIdByBookingNumber(String bookingNumber) {
		final String sql = "SELECT b.bookingId FROM Booking b "
				+ "WHERE bookingNumber = ?";
		
		Integer bookingId = 
				jdbcTemplate.queryForObject(sql, Integer.class, bookingNumber);
		
		return bookingId;
	}

	@Override
	public Integer saveAddress(final Address address) {
        log.debug("Inserting row into the Location table: " + address);

        final String sql="INSERT into Location(streetNumber,streetName, addressSecondaryUnit, "
        	        				+ "city, state, postalcode,  "
        	        				+ "arrestLocationLatitude, arrestLocationLongitude) "
        	        				+ "values (?,?,?,?,?,?,?,?)";;
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        	    new PreparedStatementCreator() {
        	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        	            PreparedStatement ps =
        	                connection.prepareStatement(sql, new String[] {"streetNumber","streetName", 
        	                		"addressSecondaryUnit", "city", "state", "postalcode",
        	                		"arrestLocationLatitude", "arrestLocationLongitude"});
        	            setPreparedStatementVariable(address.getStreetNumber(), ps, 1);
        	            setPreparedStatementVariable(address.getStreetName(), ps, 2);
        	            setPreparedStatementVariable(address.getAddressSecondaryUnit(), ps, 3);
        	            setPreparedStatementVariable(address.getCity(), ps, 4);
        	            setPreparedStatementVariable(address.getState(), ps, 5);
        	            setPreparedStatementVariable(address.getPostalcode(), ps, 6);
        	            setPreparedStatementVariable(address.getArrestLocationLatitude(), ps, 7);
        	            setPreparedStatementVariable(address.getArrestLocationLongitude(), ps, 8);
        	            return ps;
        	        }
        	    },
        	    keyHolder);

         return keyHolder.getKey().intValue();
	}

}
