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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
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
import org.ojbc.adapters.analyticsstaging.custody.dao.model.CustodyRelease;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.CustodyStatusChange;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.CustodyStatusChangeArrest;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.CustodyStatusChangeCharge;
import org.ojbc.adapters.analyticsstaging.custody.dao.model.KeyValue;
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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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
        log.debug("Inserting row into PersonSexType table");

        final String personSexInsertStatement="INSERT into PersonSexType (PersonSexTypeDescription) values (?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        	    new PreparedStatementCreator() {
        	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        	            PreparedStatement ps =
        	                connection.prepareStatement(personSexInsertStatement, new String[] {"PersonSexTypeID"});
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

        final String personRaceInsertStatement="INSERT into PersonRaceType (PersonRaceDescription) values (?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        	    new PreparedStatementCreator() {
        	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        	            PreparedStatement ps =
        	                connection.prepareStatement(personRaceInsertStatement, new String[] {"PersonRaceTypeID"});
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

        final String personStatement="INSERT into Person (PersonSexTypeID, PersonRaceTypeID, PersonBirthDate, "
        		+ "PersonUniqueIdentifier, LanguageTypeID, "
        		+ "SexOffenderStatusTypeID, PersonAgeAtBooking, EducationLevel, Occupation, "
        		+ "DomicileStatusTypeID, militaryServiceStatusTypeID, "
        		+ "PersonEthnicityTypeID, ProgramEligibilityTypeID, WorkReleaseStatusTypeID, PersonUniqueIdentifier2) "
        		+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        	    new PreparedStatementCreator() {
        	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        	            PreparedStatement ps =
        	                connection.prepareStatement(personStatement, 
        	                		java.sql.Statement.RETURN_GENERATED_KEYS);

        	            
        	            setPreparedStatementVariable(person.getPersonSexId(), ps, 1);
        	            setPreparedStatementVariable(person.getPersonRaceId(), ps, 2);
        	            setPreparedStatementVariable(person.getPersonBirthDate(), ps, 3);
        	            
        	            ps.setString(4, String.valueOf(person.getPersonUniqueIdentifier()));
        	            setPreparedStatementVariable(person.getLanguageId(), ps, 5);
        	            setPreparedStatementVariable(person.getSexOffenderStatusTypeId(), ps, 6);
            
			        	setPreparedStatementVariable(person.getPersonAgeAtBooking(), ps, 7);
			        	setPreparedStatementVariable(person.getEducationLevel(), ps, 8);
			        	setPreparedStatementVariable(person.getOccupation(), ps, 9);
			        	setPreparedStatementVariable(person.getDomicileStatusTypeId(), ps, 10);
			        	setPreparedStatementVariable(person.getMilitaryServiceStatusType().getKey(), ps, 11);
			        	setPreparedStatementVariable(person.getPersonEthnicityTypeId(), ps, 12);
			        	setPreparedStatementVariable(person.getProgramEligibilityTypeId(), ps, 13);
			        	setPreparedStatementVariable(person.getWorkReleaseStatusTypeId(), ps, 14);
			        	setPreparedStatementVariable(person.getPersonUniqueIdentifier2(), ps, 15);
       	            
        	            return ps;
        	        }
        	    },
        	    keyHolder);

         return keyHolder.getKey().intValue();
	}

	@Override
	public Person getPerson(Integer personId) {
		final String PERSON_SELECT = "SELECT * FROM Person p "
				+ "LEFT JOIN PersonSexType s ON s.PersonSexTypeID = p.PersonSexTypeID "
				+ "LEFT JOIN PersonRaceType r ON r.PersonRaceTypeID = p.PersonRaceTypeID "
				+ "LEFT JOIN PersonEthnicityType pet ON pet.PersonEthnicityTypeID = p.PersonEthnicityTypeID "
				+ "LEFT JOIN LanguageType l on l.languageTypeID = p.languageTypeID "
				+ "LEFT JOIN DomicileStatusType h ON h.DomicileStatusTypeID = p.DomicileStatusTypeID "
				+ "LEFT JOIN WorkReleaseStatusType w on w.WorkReleaseStatusTypeID = p.WorkReleaseStatusTypeID "
				+ "LEFT JOIN ProgramEligibilityType pe on pe.ProgramEligibilityTypeID = p.ProgramEligibilityTypeID "
				+ "LEFT JOIN MilitaryServiceStatusType m on m.MilitaryServiceStatusTypeID = p.MilitaryServiceStatusTypeID "
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
			person.setPersonId(DaoUtils.getInteger(rs, "PersonID"));
			person.setPersonRaceDescription(rs.getString("PersonRaceTypeDescription"));
			person.setPersonSexDescription(rs.getString("PersonSexTypeDescription"));
			person.setPersonRaceId(DaoUtils.getInteger(rs, "PersonRaceTypeId"));
			person.setPersonSexId(DaoUtils.getInteger(rs,"PersonSexTypeId"));
			person.setPersonEthnicityTypeId(DaoUtils.getInteger(rs,"PersonEthnicityTypeId"));
			person.setPersonEthnicityTypeDescription(rs.getString("personEthnicityTypeDescription"));
			person.setPersonUniqueIdentifier(rs.getString("PersonUniqueIdentifier"));
			person.setPersonUniqueIdentifier2(rs.getString("PersonUniqueIdentifier2"));
			person.setLanguageId(DaoUtils.getInteger(rs,"LanguageTypeID"));
			person.setLanguage(rs.getString("LanguageTypeDescription"));
			person.setSexOffenderStatusTypeId(DaoUtils.getInteger(rs,"SexOffenderStatusTypeID"));
			person.setProgramEligibilityTypeId(DaoUtils.getInteger(rs,"ProgramEligibilityTypeId"));
			person.setWorkReleaseStatusTypeId(DaoUtils.getInteger(rs,"WorkReleaseStatusTypeId"));
			person.setPersonAgeAtBooking(DaoUtils.getInteger(rs,"PersonAgeAtBooking"));
			person.setDomicileStatusTypeId(DaoUtils.getInteger(rs,"DomicileStatusTypeID"));
			person.setEducationLevel(rs.getString("EducationLevel"));
			person.setOccupation(rs.getString("Occupation"));
			person.setMilitaryServiceStatusType(
					new KeyValue(DaoUtils.getInteger(rs,"MilitaryServiceStatusTypeID"), rs.getString("MilitaryServiceStatusTypeDescription")));
	    	return person;
		}

	}

	@Override
	public void saveBehavioralHealthAssessments(
			List<BehavioralHealthAssessment> behavioralHealthAssessments) {
		log.debug("Inserting row into BehavioralHealthAssessment table: " + behavioralHealthAssessments);
		final String sqlString=
				"INSERT INTO BehavioralHealthAssessment (PersonID, seriousMentalIllnessIndicator,"
				+ "CareEpisodeStartDate, CareEpisodeEndDate, "
				+ "MedicaidStatusTypeId, EnrolledProviderName ) values (?,?,?,?,?,?,?,?)";
		
        jdbcTemplate.batchUpdate(sqlString, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i)
                throws SQLException {
            	BehavioralHealthAssessment behavioralHealthAssessment = behavioralHealthAssessments.get(i);
                ps.setInt(1, behavioralHealthAssessment.getPersonId());
                setPreparedStatementVariable(behavioralHealthAssessment.getSeriousMentalIllness(), ps, 2);
                setPreparedStatementVariable(behavioralHealthAssessment.getCareEpisodeStartDate(), ps, 3);
                setPreparedStatementVariable(behavioralHealthAssessment.getCareEpisodeEndDate(), ps, 4);
                setPreparedStatementVariable(behavioralHealthAssessment.getMedicaidStatusTypeId(), ps, 5);
                setPreparedStatementVariable(behavioralHealthAssessment.getEnrolledProviderName(), ps, 6);
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
				"INSERT INTO BookingCharge (BookingArrestID, ChargeCode, AgencyID, "
				+ "BondAmount, BondTypeID, ChargeClassTypeID, BondStatusTypeId, ChargeJurisdictionTypeId, ChargeDisposition) "
				+ "values (?,?,?,?,?,?,?,?,?)";
		
        jdbcTemplate.batchUpdate(sqlString, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i)
                throws SQLException {
            	BookingCharge bookingCharge = bookingCharges.get(i);
                ps.setInt(1, bookingCharge.getBookingArrestId());
                setPreparedStatementVariable(bookingCharge.getChargeCode(), ps, 2);
                ps.setInt(3, bookingCharge.getAgencyId());
                setPreparedStatementVariable(bookingCharge.getBondAmount(), ps, 4);
                
                if (bookingCharge.getBondType() != null){
                    ps.setInt(5, bookingCharge.getBondType().getKey());
                }
                else{
                	ps.setNull(5, java.sql.Types.NULL);
                }
                
                setPreparedStatementVariable(bookingCharge.getChargeClassTypeId(), ps, 6);
                setPreparedStatementVariable(bookingCharge.getBondStatusTypeId(), ps, 7);
                setPreparedStatementVariable(bookingCharge.getChargeJurisdictionTypeId(), ps, 8);
                setPreparedStatementVariable(bookingCharge.getChargeDisposition(), ps, 9);
            }
	            
            public int getBatchSize() {
                return bookingCharges.size();
            }
        });

	}

	@Override
	public Integer saveBooking(final Booking booking) {
        log.debug("Inserting row into Booking table: " + booking.toString());
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        	    new PreparedStatementCreator() {
        	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        	        	
        	        	String sqlString="";
        	        	
        	        	if (booking.getBookingId() != null){

        	        		sqlString="INSERT into booking ("
        	        				+ "BookingDate, BookingTime, FacilityID, SupervisionUnitTypeID, "
        	        				+ "PersonID, BookingNumber, ScheduledReleaseDate, InmateJailResidentIndicator, BookingID) "
        	        				+ "values (?,?,?,?,?,?,?,?,?)";
        	        	}	
        	        	else{

        	        		sqlString="INSERT into booking ("
        	        				+ "BookingDate, BookingTime,"
        	        				+ "FacilityID, SupervisionUnitTypeID,  "
        	        				+ "PersonID, BookingNumber, ScheduledReleaseDate, InmateJailResidentIndicator) "
        	        				+ "values (?,?,?,?,?,?,?,?)";
        	        		
        	        	}	
        	        			
        	            PreparedStatement ps =
        	                connection.prepareStatement(sqlString, java.sql.Statement.RETURN_GENERATED_KEYS);
        	            
        	            setPreparedStatementVariable(booking.getBookingDate(), ps, 1);
        	            setPreparedStatementVariable(booking.getBookingTime(), ps, 2);
        	            setPreparedStatementVariable(booking.getFacilityId(), ps, 3);
        	            setPreparedStatementVariable(booking.getSupervisionUnitTypeId(), ps, 4);
        	            setPreparedStatementVariable(booking.getPersonId(), ps, 5);
        	            setPreparedStatementVariable(booking.getBookingNumber(), ps, 6);
        	            setPreparedStatementVariable(booking.getScheduledReleaseDate(), ps, 7);
        	            setPreparedStatementVariable(booking.getInmateJailResidentIndicator(), ps, 8);
                        
        	            if (booking.getBookingId() != null){
        	            	setPreparedStatementVariable(booking.getBookingId(), ps, 9);
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
		
		jdbcTemplate.update("DELETE FROM CustodyRelease WHERE bookingID = ?", bookingId);
		
		deleteCustodyStatusChanges(bookingId);
		deleteInitialBooking(bookingId);
		
		jdbcTemplate.update("DELETE FROM Location "
				+ "WHERE (SELECT count(*) FROM BookingArrest ba WHERE ba.locationId = locationId) = 0 "
				+ "	 AND (SELECT count(*) FROM CustodyStatusChangeArrest ba WHERE ba.locationId = locationId) = 0 ");
	}

	private void deleteInitialBooking(Integer bookingId) {
		
		jdbcTemplate.update("DELETE FROM BookingCharge  "
				+ "WHERE BookingArrestID IN "
				+ "	(SELECT ba.BookingArrestID from BookingArrest ba "
				+ "		LEFT JOIN Booking b ON b.BookingID = ba.BookingID "
				+ "		WHERE b.bookingID = ? )", bookingId); 
				
		jdbcTemplate.update("DELETE FROM BookingArrest "
				+ "WHERE BookingID = ? ", bookingId); 
		
		jdbcTemplate.update("DELETE FROM BehavioralHealthEvaluation "
				+ "WHERE BehavioralHealthAssessmentID IN "
				+ "	(SELECT bha.BehavioralHealthAssessmentID from BehavioralHealthAssessment bha "
				+ "		LEFT JOIN Person p ON p.PersonId = bha.PersonId "
				+ "		LEFT JOIN Booking b ON b.personId = p.personId "
				+ "		WHERE b.bookingId = ? )", bookingId); 
		
		jdbcTemplate.update("DELETE FROM BehavioralHealthAssessmentCategory "
				+ "WHERE BehavioralHealthAssessmentID IN "
				+ "	(SELECT bha.BehavioralHealthAssessmentID from BehavioralHealthAssessment bha "
				+ "		LEFT JOIN Person p ON p.PersonId = bha.PersonId "
				+ "		LEFT JOIN Booking b ON b.personId = p.personId "
				+ "		WHERE b.bookingId = ? )", bookingId); 
		
		jdbcTemplate.update("DELETE FROM PrescribedMedication "
				+ "WHERE BehavioralHealthAssessmentID IN "
				+ "	(SELECT bha.BehavioralHealthAssessmentID from BehavioralHealthAssessment bha "
				+ "		LEFT JOIN Person p ON p.PersonId = bha.PersonId "
				+ "		LEFT JOIN Booking b ON b.personId = p.personId "
				+ "		WHERE b.bookingId = ? )", bookingId); 
		
		jdbcTemplate.update("DELETE FROM Treatment "
				+ "WHERE BehavioralHealthAssessmentID IN "
				+ "	(SELECT bha.BehavioralHealthAssessmentID from BehavioralHealthAssessment bha "
				+ "		LEFT JOIN Person p ON p.PersonId = bha.PersonId "
				+ "		LEFT JOIN Booking b ON b.personId = p.personId "
				+ "		WHERE b.bookingId = ? )", bookingId); 
		
		jdbcTemplate.update("DELETE FROM BehavioralHealthAssessment "
				+ "WHERE PersonId IN "
				+ "	(SELECT p.PersonId from Person p "
				+ "		LEFT JOIN Booking b ON b.personId = p.personId "
				+ "		WHERE b.bookingId = ? )", bookingId); 
		
		Integer personId = jdbcTemplate.queryForObject("SELECT PersonID from Booking WHERE BookingID = ?", Integer.class, bookingId);
		
		jdbcTemplate.update("DELETE FROM Booking WHERE bookingId = ? ", bookingId); 
		
		jdbcTemplate.update("DELETE FROM Person "
				+ "WHERE personId = ? ", personId);
	}

	private void deleteCustodyStatusChanges(Integer bookingId) {
		
		jdbcTemplate.update("DELETE FROM CustodyStatusChangeCharge  "
				+ "WHERE CustodyStatusChangeArrestID IN "
				+ "	(SELECT csca.CustodyStatusChangeArrestID from CustodyStatusChangeArrest csca "
				+ " 	RIGHT JOIN CustodyStatusChange csc ON csc.CustodyStatusChangeID = csca.CustodyStatusChangeID "
				+ "		WHERE csc.bookingID = ? )", bookingId); 
				
		jdbcTemplate.update("DELETE FROM CustodyStatusChangeArrest "
				+ "WHERE CustodyStatusChangeID IN "
				+ "	(SELECT CustodyStatusChangeID from CustodyStatusChange "
				+ "		WHERE bookingID = ? )", bookingId); 
		
		jdbcTemplate.update("DELETE FROM BehavioralHealthEvaluation "
				+ "WHERE BehavioralHealthAssessmentID IN "
				+ "	(SELECT bha.BehavioralHealthAssessmentID from BehavioralHealthAssessment bha "
				+ "		RIGHT JOIN Person p ON p.PersonId = bha.PersonId "
				+ "		LEFT JOIN CustodyStatusChange csc ON csc.personId = p.personId "
				+ "		WHERE csc.bookingId = ? )", bookingId); 
		
		jdbcTemplate.update("DELETE FROM BehavioralHealthAssessmentCategory "
				+ "WHERE BehavioralHealthAssessmentID IN "
				+ "	(SELECT bha.BehavioralHealthAssessmentID from BehavioralHealthAssessment bha "
				+ "		RIGHT JOIN Person p ON p.PersonId = bha.PersonId "
				+ "		LEFT JOIN CustodyStatusChange csc ON csc.personId = p.personId "
				+ "		WHERE csc.bookingId = ? )", bookingId); 
		
		jdbcTemplate.update("DELETE FROM PrescribedMedication "
				+ "WHERE BehavioralHealthAssessmentID IN "
				+ "	(SELECT bha.BehavioralHealthAssessmentID from BehavioralHealthAssessment bha "
				+ "		LEFT JOIN Person p ON p.PersonId = bha.PersonId "
				+ "		LEFT JOIN CustodyStatusChange csc ON csc.personId = p.personId "
				+ "		WHERE csc.bookingId = ? )", bookingId); 
		
		jdbcTemplate.update("DELETE FROM Treatment "
				+ "WHERE BehavioralHealthAssessmentID IN "
				+ "	(SELECT bha.BehavioralHealthAssessmentID from BehavioralHealthAssessment bha "
				+ "		LEFT JOIN Person p ON p.PersonId = bha.PersonId "
				+ "		LEFT JOIN CustodyStatusChange csc ON csc.personId = p.personId "
				+ "		WHERE csc.bookingId = ? )", bookingId); 
		
		jdbcTemplate.update("DELETE FROM BehavioralHealthAssessment "
				+ "WHERE PersonId IN "
				+ "	(SELECT p.PersonId FROM Person p "
				+ "		LEFT JOIN CustodyStatusChange csc ON csc.personId = p.personId "
				+ "		WHERE csc.bookingId = ? )", bookingId); 
		
		List<Integer> personIds = jdbcTemplate.queryForList("SELECT PersonID from CustodyStatusChange WHERE BookingID = ?", Integer.class, bookingId);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("personIds", personIds);

		jdbcTemplate.update("DELETE FROM CustodyStatusChange WHERE bookingId = ? ", bookingId); 
		
		if ( personIds.size() > 0){
			namedParameterJdbcTemplate.update("DELETE FROM Person "
					+ "WHERE PersonID IN (:personIds) ", parameters);
		}
		
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
	    	
			booking.setBookingId(DaoUtils.getInteger(rs,"BookingID"));
			booking.setBookingDate(DaoUtils.getLocalDate(rs, "BookingDate"));
			booking.setBookingTime(DaoUtils.getLocalTime(rs, "BookingTime"));
			booking.setFacilityId(DaoUtils.getInteger(rs,"FacilityID"));
			booking.setSupervisionUnitTypeId(DaoUtils.getInteger(rs,"SupervisionUnitTypeID"));
			booking.setPersonId(DaoUtils.getInteger(rs,"PersonID"));
			booking.setBookingNumber(rs.getString("BookingNumber"));
			booking.setScheduledReleaseDate( DaoUtils.getLocalDate(rs, "ScheduledReleaseDate"));
			booking.setInmateJailResidentIndicator( rs.getBoolean("InmateJailResidentIndicator"));
			
	    	return booking;
		}

	}

	@Override
	public Integer getPersonIdByUniqueId(String uniqueId) {
		String sqlString = "SELECT top 1 PersonID FROM Person WHERE PersonUniqueIdentifier = ? order by PersonTimestamp desc";
		
		List<Integer> personIds = jdbcTemplate.queryForList(sqlString, Integer.class, uniqueId);

		return DataAccessUtils.uniqueResult(personIds);
	}

	@Override
	public List<BookingCharge> getBookingCharges(Integer bookingId) {
		final String sql = "SELECT * FROM BookingCharge b "
				+ "LEFT JOIN BookingArrest a ON a.BookingArrestID = b.BookingArrestID "
				+ "LEFT JOIN Booking bk ON bk.BookingID = a.BookingID "
				+ "LEFT JOIN BondType bt ON bt.BondTypeID = b.BondTypeID "
				+ "LEFT JOIN BondStatusType bst ON bst.BondStatusTypeID = b.BondStatusTypeID "
				+ "LEFT JOIN JurisdictionType j ON j.JurisdictionTypeID = b.ChargeJurisdictionTypeID "
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
	    	
			bookingCharge.setBookingChargeId(DaoUtils.getInteger(rs,"bookingChargeId"));
			bookingCharge.setBookingArrestId(DaoUtils.getInteger(rs,"bookingArrestId"));
			
			bookingCharge.setChargeCode( rs.getString("ChargeCode") );
			bookingCharge.setChargeDisposition( rs.getString("ChargeDisposition") );
			
			bookingCharge.setAgencyId(DaoUtils.getInteger(rs,"AgencyID"));
			bookingCharge.setChargeClassTypeId(DaoUtils.getInteger(rs,"ChargeClassTypeId"));
			bookingCharge.setBondAmount(rs.getBigDecimal("bondAmount"));
			
			Integer bondTypeId = DaoUtils.getInteger(rs,"bondTypeId"); 
			if (bondTypeId != null){
				KeyValue bondType = new KeyValue( DaoUtils.getInteger(rs,"bondTypeId"), rs.getString("bondTypeDescription"));
				bookingCharge.setBondType( bondType );
			}
			
			bookingCharge.setBondStatusTypeId(DaoUtils.getInteger(rs,"BondStatusTypeId"));
			bookingCharge.setChargeJurisdictionTypeId(DaoUtils.getInteger(rs,"ChargeJurisdictionTypeId"));
	    	return bookingCharge;
		}

	}

	@Override
	public List<BehavioralHealthAssessment> getBehavioralHealthAssessments(
			Integer personId) {
		final String sql = "SELECT * FROM BehavioralHealthAssessment b "
				+ "LEFT JOIN BehavioralHealthEvaluation e ON e.BehavioralHealthAssessmentID = b.BehavioralHealthAssessmentID "
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
	            Integer behavioralHealthAssessmentId = DaoUtils.getInteger(rs,"behavioralHealthAssessmentId" ); 
	            behavioralHealthAssessment  = map.get( behavioralHealthAssessmentId );
	            if ( behavioralHealthAssessment  == null){
	            	behavioralHealthAssessment = new BehavioralHealthAssessment();; 
	    			behavioralHealthAssessment.setBehavioralHealthAssessmentId(DaoUtils.getInteger(rs,"behavioralHealthAssessmentId"));
	    			behavioralHealthAssessment.setPersonId(DaoUtils.getInteger(rs,"PersonId"));
	    			behavioralHealthAssessment.setSeriousMentalIllness(rs.getBoolean("seriousMentalIllnessIndicator"));
	    			behavioralHealthAssessment.setMedicaidStatusTypeId(DaoUtils.getInteger(rs,"MedicaidStatusTypeId"));
	    			behavioralHealthAssessment.setCareEpisodeStartDate(DaoUtils.getLocalDate(rs, "careEpisodeStartDate") );
	    			behavioralHealthAssessment.setCareEpisodeEndDate(DaoUtils.getLocalDate(rs, "careEpisodeEndDate"));
	    			behavioralHealthAssessment.setEnrolledProviderName(rs.getString("EnrolledProviderName"));
	    			
	    			behavioralHealthAssessment.setBehavioralHealthDiagnoses(new ArrayList<String>());
	            	map.put(behavioralHealthAssessmentId, behavioralHealthAssessment); 
	            }
	            
				String behavioralHealthDiagnosis = rs.getString("BehavioralHealthDiagnosisDescription");
				behavioralHealthAssessment.getBehavioralHealthDiagnoses().add(behavioralHealthDiagnosis);
            }
            return (List<BehavioralHealthAssessment>) new ArrayList<BehavioralHealthAssessment>(map.values());
			
		}

	}

	@Override
	public void saveCustodyRelease(CustodyRelease custodyRelease) {

		saveCustodyRelease(custodyRelease.getBookingId(), custodyRelease.getBookingNumber(), 
				custodyRelease.getReleaseDate(), custodyRelease.getReleaseTime(), custodyRelease.getReleaseCondition());
	}

	private void saveCustodyRelease(Integer bookingId, String bookingNumber,
			LocalDate releaseDate, LocalTime releaseTime, String releaseCondition) {

		final String sql = "Insert into CustodyRelease (BookingID, BookingNumber, "
				+ "ReleaseDate, ReleaseTime, ReleaseCondition) "
				+ "values (:bookingId, :bookingNumber, :releaseDate, :releaseTime, :releaseCondition)";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("releaseDate", Date.valueOf(releaseDate) );
		params.put("releaseTime", Time.valueOf(releaseTime) );
		params.put("bookingId", bookingId );
		params.put("bookingNumber", bookingNumber );
		params.put("releaseCondition", releaseCondition);
		
		namedParameterJdbcTemplate.update(sql, params);
	}

	@Override
	public CustodyRelease getCustodyReleaseByBookingId(Integer bookingId) {
		final String sql = "Select top 1 * from CustodyRelease where BookingId = ? order by CustodyReleaseTimestamp desc";
		
		List<CustodyRelease> custodyReleases = 
				jdbcTemplate.query(sql, new CustodyReleaseRowMapper(), bookingId);
		return DataAccessUtils.singleResult(custodyReleases);
	}

	public class CustodyReleaseRowMapper implements RowMapper<CustodyRelease>
	{
		@Override
		public CustodyRelease mapRow(ResultSet rs, int rowNum) throws SQLException {
			CustodyRelease custodyRelease = new CustodyRelease();
	    	
			custodyRelease.setBookingId(DaoUtils.getInteger(rs,"bookingId"));
			custodyRelease.setBookingNumber(rs.getString("bookingNumber"));
			custodyRelease.setReleaseDate(DaoUtils.getLocalDate(rs, "ReleaseDate"));
			custodyRelease.setReleaseTime(DaoUtils.getLocalTime(rs, "ReleaseTime"));
			custodyRelease.setReleaseCondition( rs.getString("ReleaseCondition"));
			
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
        	        	
        	        	if (custodyStatusChange.getCustodyStatusChangeId() != null){

        	        		sqlString="INSERT into custodyStatusChange ("
        	        				+ "BookingDate, BookingTime,  "
        	        				+ "FacilityID, SupervisionUnitTypeID, "
        	        				+ "PersonID, BookingId, ScheduledReleaseDate, InmateJailResidentIndicator, BookingNumber, "
        	        				+ "CustodyStatusChangeID) "
        	        				+ "values (?,?,?,?,?,?,?,?,?,?)";
        	        	}	
        	        	else{

        	        		sqlString="INSERT into custodyStatusChange ("
        	        				+ "BookingDate, BookingTime, "
        	        				+ "FacilityID, SupervisionUnitTypeID, "
        	        				+ "PersonID, BookingId, ScheduledReleaseDate, InmateJailResidentIndicator, BookingNumber) "
        	        				+ "values (?,?,?,?,?,?,?,?,?)";
        	        		
        	        	}	
        	        			
        	        	
        	            PreparedStatement ps =
        	                connection.prepareStatement(sqlString, java.sql.Statement.RETURN_GENERATED_KEYS);
        	            
        	            setPreparedStatementVariable(custodyStatusChange.getBookingDate(), ps, 1);
        	            setPreparedStatementVariable(custodyStatusChange.getBookingTime(), ps, 2);
        	            setPreparedStatementVariable(custodyStatusChange.getFacilityId(), ps, 3);
        	            setPreparedStatementVariable(custodyStatusChange.getSupervisionUnitTypeId(), ps, 4);
        	            setPreparedStatementVariable(custodyStatusChange.getPersonId(), ps, 5);
        	            setPreparedStatementVariable(custodyStatusChange.getBookingId(), ps, 6);
        	            setPreparedStatementVariable(custodyStatusChange.getScheduledReleaseDate(), ps, 7);
        	            setPreparedStatementVariable(custodyStatusChange.getInmateJailResidentIndicator(), ps, 8);
        	            setPreparedStatementVariable(custodyStatusChange.getBookingNumber(), ps, 9);
        	            
        	            if (custodyStatusChange.getCustodyStatusChangeId() != null){
        	            	setPreparedStatementVariable(custodyStatusChange.getCustodyStatusChangeId(), ps, 10);
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
				"INSERT INTO CustodyStatusChangeCharge (CustodyStatusChangeArrestID, ChargeCode, AgencyID, "
				+ "BondAmount, BondTypeID, ChargeClassTypeId, BondStatusTypeId, ChargeJurisdictionTypeId, "
				+ "ChargeDisposition ) "
				+ "values (?,?,?,?,?,?,?,?,?)";
		
        jdbcTemplate.batchUpdate(sqlString, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i)
                throws SQLException {
            	CustodyStatusChangeCharge custodyStatusChangeCharge = custodyStatusChangeCharges.get(i);
                ps.setInt(1, custodyStatusChangeCharge.getCustodyStatusChangeArrestId());
                setPreparedStatementVariable(custodyStatusChangeCharge.getChargeCode(), ps, 2);
                ps.setInt(3, custodyStatusChangeCharge.getAgencyId());
                setPreparedStatementVariable(custodyStatusChangeCharge.getBondAmount(), ps, 4);
                
                if (custodyStatusChangeCharge.getBondType() != null){
                	 ps.setInt(5, custodyStatusChangeCharge.getBondType().getKey());
                }
                else{
                	ps.setNull(5, java.sql.Types.NULL);
                }
                setPreparedStatementVariable(custodyStatusChangeCharge.getChargeClassTypeId(), ps, 6);
                setPreparedStatementVariable(custodyStatusChangeCharge.getBondStatusTypeId(), ps, 7);
                setPreparedStatementVariable(custodyStatusChangeCharge.getChargeJurisdictionTypeId(), ps, 8);
                setPreparedStatementVariable(custodyStatusChangeCharge.getChargeDisposition(), ps, 9);
            }
	            
            public int getBatchSize() {
                return custodyStatusChangeCharges.size();
            }
        });

		
	}

	public List<CustodyStatusChange> getCustodyStatusChangesByBookingId(Integer bookingId) {
		final String sql = "SELECT * FROM CustodyStatusChange c "
				+ "WHERE BookingId = ?";
		
		List<CustodyStatusChange> custodyStatusChanges = 
				jdbcTemplate.query(sql, 
						new CustodyStatusChangeRowMapper(), bookingId);
		
		return custodyStatusChanges;
	}

	public class CustodyStatusChangeRowMapper implements RowMapper<CustodyStatusChange>
	{
		@Override
		public CustodyStatusChange mapRow(ResultSet rs, int rowNum) throws SQLException {
			CustodyStatusChange custodyStatusChange = new CustodyStatusChange();
	    	
			custodyStatusChange.setCustodyStatusChangeId(DaoUtils.getInteger(rs,"CustodyStatusChangeId"));
			custodyStatusChange.setBookingDate(DaoUtils.getLocalDate(rs, "BookingDate"));
			custodyStatusChange.setBookingTime(DaoUtils.getLocalTime(rs, "BookingTime"));
			custodyStatusChange.setFacilityId(DaoUtils.getInteger(rs,"FacilityID"));
			custodyStatusChange.setSupervisionUnitTypeId(DaoUtils.getInteger(rs,"SupervisionUnitTypeID"));
			custodyStatusChange.setPersonId(DaoUtils.getInteger(rs,"PersonID"));
			custodyStatusChange.setBookingId(DaoUtils.getInteger(rs,"BookingId"));
			custodyStatusChange.setBookingNumber(rs.getString("BookingNumber"));
			custodyStatusChange.setScheduledReleaseDate( DaoUtils.getLocalDate(rs, "ScheduledReleaseDate"));
			custodyStatusChange.setInmateJailResidentIndicator( rs.getBoolean("InmateJailResidentIndicator") );
			
	    	return custodyStatusChange;
		}

	}

	@Override
	public List<CustodyStatusChangeCharge> getCustodyStatusChangeCharges(Integer custodyStatusChangeId) {
		final String sql = "SELECT * FROM CustodyStatusChangeCharge b "
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
	    	
			custodyStatusChangeCharge.setCustodyStatusChangeChargeId(DaoUtils.getInteger(rs,"CustodyStatusChangeChargeId"));
			custodyStatusChangeCharge.setCustodyStatusChangeArrestId(DaoUtils.getInteger(rs,"CustodyStatusChangeArrestId"));
			
			custodyStatusChangeCharge.setChargeCode( rs.getString("ChargeCode") );
			custodyStatusChangeCharge.setChargeDisposition( rs.getString("ChargeDisposition") );
			
			custodyStatusChangeCharge.setAgencyId(DaoUtils.getInteger(rs,"AgencyID"));
			custodyStatusChangeCharge.setBondAmount(rs.getBigDecimal("bondAmount"));
			
			Integer bondTypeId = DaoUtils.getInteger(rs,"bondTypeId"); 
			if (bondTypeId != null){
				KeyValue bondType = new KeyValue( DaoUtils.getInteger(rs,"bondTypeId"), rs.getString("bondTypeDescription"));
				custodyStatusChangeCharge.setBondType( bondType );
			}
			custodyStatusChangeCharge.setChargeClassTypeId(DaoUtils.getInteger(rs,"ChargeClassTypeId"));
			custodyStatusChangeCharge.setBondStatusTypeId(DaoUtils.getInteger(rs,"BondStatusTypeId"));
			custodyStatusChangeCharge.setChargeJurisdictionTypeId(DaoUtils.getInteger(rs,"ChargeJurisdictionTypeId"));
			
	    	return custodyStatusChangeCharge;
		}

	}

	@Override
	public Integer saveBookingArrest(BookingArrest bookingArrest) {
        log.debug("Inserting row into BookingArrest table: " + bookingArrest.toString());
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        	    new PreparedStatementCreator() {
        	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        	        	
        	        	String sqlString="";
        	        	
        	        	if (bookingArrest.getBookingArrestId() != null){

        	        		sqlString="INSERT into bookingArrest (bookingId, locationId, arrestAgencyId, bookingArrestId) "
        	        				+ "values (?,?,?,?)";
        	        	}	
        	        	else{

        	        		sqlString="INSERT into bookingArrest (bookingId, locationId, arrestAgencyId) "
        	        				+ "values (?,?,?)";
        	        		
        	        	}	
        	        	
        	            PreparedStatement ps =
        	                connection.prepareStatement(sqlString, java.sql.Statement.RETURN_GENERATED_KEYS);
        	            ps.setInt(1, bookingArrest.getBookingId());
        	            
        	            setPreparedStatementVariable(bookingArrest.getAddress().getLocationId(), ps, 2);
        	            setPreparedStatementVariable(bookingArrest.getArrestAgencyId(), ps, 3);
        	            
        	            if (bookingArrest.getBookingArrestId() != null){
        	            	setPreparedStatementVariable(bookingArrest.getBookingArrestId(), ps, 4);
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
        	        	
        	        	if (assessment.getBehavioralHealthAssessmentId()!= null){
        	        		sqlString="INSERT into BehavioralHealthAssessment (personId, seriousMentalIllnessIndicator,"
        	        				+ "careEpisodeStartDate, careEpisodeEndDate,"
        	        				+ "MedicaidStatusTypeId, "
        	        				+ "EnrolledProviderName, behavioralHealthAssessmentId) "
        	        				+ "values (?,?,?,?,?,?,?)";
        	        	}	
        	        	else{

        	        		sqlString="INSERT into BehavioralHealthAssessment (personId, seriousMentalIllnessIndicator,"
        	        				+ "careEpisodeStartDate, careEpisodeEndDate,"
        	        				+ "MedicaidStatusTypeId, "
        	        				+ "EnrolledProviderName) "
        	        				+ "values (?,?,?,?,?,?)";
        	        	}	
        	        			
        	        	
        	            PreparedStatement ps =
        	                connection.prepareStatement(sqlString, java.sql.Statement.RETURN_GENERATED_KEYS);
        	            ps.setInt(1, assessment.getPersonId());
        	            
        	            setPreparedStatementVariable(assessment.getSeriousMentalIllness(), ps, 2);
        	            setPreparedStatementVariable(assessment.getCareEpisodeStartDate(), ps, 3);
        	            setPreparedStatementVariable(assessment.getCareEpisodeEndDate(), ps, 4);
        	            setPreparedStatementVariable(assessment.getMedicaidStatusTypeId(), ps, 5);
        	            setPreparedStatementVariable(assessment.getEnrolledProviderName(), ps, 6);
        	            
        	            if (assessment.getBehavioralHealthAssessmentId() != null){
        	            	setPreparedStatementVariable(assessment.getBehavioralHealthAssessmentId(), ps, 7);
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
	public Integer getMedicationTypeId(String genericProductIdentification, String medicationTypeDescription) {
        final String sql="SELECT medicationTypeId FROM MedicationType WHERE GenericProductIdentification = ? AND MedicationTypeDescription = ?";
        
        List<Integer> medicationTypeIds = jdbcTemplate.queryForList(sql, Integer.class, genericProductIdentification, medicationTypeDescription);
        
		return DataAccessUtils.singleResult(medicationTypeIds);
	}

	@Override
	public void saveTreatments(final List<Treatment> treatments) {
		log.info("Inserting row into Treatment table: " + treatments);
		final String sqlString=
				"INSERT INTO Treatment (BehavioralHealthAssessmentID, TreatmentStartDate, "
				+ "TreatmentAdmissionReasonTypeID, TreatmentProviderName, TreatmentStatusTypeID) "
				+ "values (?,?,?,?,?)";
		
        jdbcTemplate.batchUpdate(sqlString, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i)
                throws SQLException {
            	Treatment treatment = treatments.get(i);
                ps.setInt(1, treatment.getBehavioralHealthAssessmentID());
                setPreparedStatementVariable(treatment.getTreatmentStartDate(), ps, 2);
                setPreparedStatementVariable(treatment.getTreatmentAdmissionReasonTypeId(), ps, 3);
                setPreparedStatementVariable(treatment.getTreatmentProviderName(), ps, 4);
                setPreparedStatementVariable(treatment.getTreatmentStatusTypeId(), ps, 5);
            }
	            
            public int getBatchSize() {
                return treatments.size();
            }
        });
	}

	@Override
	public void saveBehavioralHealthEvaluations(
			Integer behavioralHealthAssessmentId, List<String> behavioralHealthDiagnoses) {
		log.info("Inserting row into BehavioralHealthEvaluation table: " + behavioralHealthDiagnoses + 
				" to BehavioralHealthAssessment " + behavioralHealthAssessmentId );
		final String sqlString=
				"INSERT INTO BehavioralHealthEvaluation (BehavioralHealthAssessmentID, BehavioralHealthDiagnosisDescription) "
				+ "values (?,?)";
		
        jdbcTemplate.batchUpdate(sqlString, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i)
                throws SQLException {
            	String behavioralHealthDiagnosis = behavioralHealthDiagnoses.get(i);
                ps.setInt(1, behavioralHealthAssessmentId);
                ps.setString(2, behavioralHealthDiagnosis);
            }
	            
            public int getBatchSize() {
                return behavioralHealthDiagnoses.size();
            }
        });
		
	}

	@Override
	public void savePrescribedMedications(
			List<PrescribedMedication> prescribedMedications) {
		log.info("Inserting row into PrescribedMedication table: " + prescribedMedications);
		final String sqlString=
				"INSERT INTO PrescribedMedication (BehavioralHealthAssessmentID, MedicationDescription, MedicationDispensingDate, MedicationDoseMeasure) "
				+ "values (?,?,?,?)";
		
        jdbcTemplate.batchUpdate(sqlString, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i)
                throws SQLException {
            	PrescribedMedication prescribedMedication = prescribedMedications.get(i);
                ps.setInt(1, prescribedMedication.getBehavioralHealthAssessmentID());
                setPreparedStatementVariable(prescribedMedication.getMedicationDescription(), ps, 2);
                setPreparedStatementVariable(prescribedMedication.getMedicationDispensingDate(), ps, 3);
                setPreparedStatementVariable(prescribedMedication.getMedicationDoseMeasure(), ps, 4);
            }
	            
            public int getBatchSize() {
                return prescribedMedications.size();
            }
        });
	}

	@Override
	public List<BookingArrest> getBookingArrests(Integer bookingId) {
		final String sql = "SELECT * FROM BookingArrest a "
				+ "LEFT JOIN location l ON l.locationId = a.locationId "
				+ "LEFT JOIN agency ag ON ag.agencyId = a.arrestAgencyId "
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
	    	
			bookingArrest.setBookingId(DaoUtils.getInteger(rs,"bookingId"));
			bookingArrest.setBookingArrestId(DaoUtils.getInteger(rs,"bookingArrestId"));
			bookingArrest.setArrestAgencyId(DaoUtils.getInteger(rs,"arrestAgencyId"));

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
	    	
			treatment.setTreatmentId(DaoUtils.getInteger(rs,"treatmentId"));
			treatment.setBehavioralHealthAssessmentID(DaoUtils.getInteger(rs,"behavioralHealthAssessmentID"));

			treatment.setTreatmentStartDate(DaoUtils.getLocalDate(rs, "TreatmentStartDate"));
			treatment.setTreatmentAdmissionReasonTypeId(DaoUtils.getInteger(rs,"TreatmentAdmissionReasonTypeID"));
			treatment.setTreatmentStatusTypeId(DaoUtils.getInteger(rs,"TreatmentStatusTypeID"));
			treatment.setTreatmentProviderName(rs.getString("TreatmentProviderName"));
	    	return treatment;
		}

	}


	@Override
	public List<PrescribedMedication> getPrescribedMedication(
			Integer behavioralHealthAssessmentId) {
		final String sql = "SELECT * FROM PrescribedMedication p "
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
	    	
			prescribedMedication.setPrescribedMedicationID(DaoUtils.getInteger(rs,"prescribedMedicationID"));
			prescribedMedication.setBehavioralHealthAssessmentID(DaoUtils.getInteger(rs,"behavioralHealthAssessmentID"));

			prescribedMedication.setMedicationDescription(rs.getString("MedicationDescription"));
			prescribedMedication.setMedicationDispensingDate(DaoUtils.getLocalDate(rs, "medicationDispensingDate"));
			prescribedMedication.setMedicationDoseMeasure(rs.getString("medicationDoseMeasure"));
			
	    	return prescribedMedication;
		}

	}

	@Override
	public Integer saveCustodyStatusChangeArrest(
			CustodyStatusChangeArrest custodyStatusChangeArrest) {
        log.debug("Inserting row into CustodyStatusChangeArrest table: " + custodyStatusChangeArrest.toString());
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        	    new PreparedStatementCreator() {
        	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        	        	
        	        	String sqlString="";
        	        	
        	        	if (custodyStatusChangeArrest.getCustodyStatusChangeArrestId() != null){
        	        		sqlString="INSERT into CustodyStatusChangeArrest (custodyStatusChangeId, locationId, arrestAgencyId, custodyStatusChangeArrestId) "
        	        				+ "values (?,?,?,?)";
        	        	}	
        	        	else{
        	        		sqlString="INSERT into CustodyStatusChangeArrest (custodyStatusChangeId, locationId, arrestAgencyId) "
        	        				+ "values (?,?,?)";
        	        	}	
        	        	
        	            PreparedStatement ps =
        	                connection.prepareStatement(sqlString, java.sql.Statement.RETURN_GENERATED_KEYS);
        	            ps.setInt(1, custodyStatusChangeArrest.getCustodyStatusChangeId());
        	            
        	            setPreparedStatementVariable(custodyStatusChangeArrest.getAddress().getLocationId(), ps, 2);
        	            setPreparedStatementVariable(custodyStatusChangeArrest.getArrestAgencyId(), ps, 3);
        	            
        	            if (custodyStatusChangeArrest.getCustodyStatusChangeArrestId() != null){
        	            	setPreparedStatementVariable(custodyStatusChangeArrest.getCustodyStatusChangeArrestId(), ps, 4);
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
				+ "LEFT JOIN agency ag ON ag.agencyId = a.arrestAgencyId "
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
	    	
			custodyStatusChangeArrest.setCustodyStatusChangeId(DaoUtils.getInteger(rs,"custodyStatusChangeId"));
			custodyStatusChangeArrest.setArrestAgencyId(DaoUtils.getInteger(rs,"arrestAgencyId"));
			custodyStatusChangeArrest.setCustodyStatusChangeArrestId(DaoUtils.getInteger(rs,"custodyStatusChangeArrestId"));

			Address address = buildAddress(rs);
			
			custodyStatusChangeArrest.setAddress(address);
	    	return custodyStatusChangeArrest;
		}

	}
	
	private Address buildAddress(ResultSet rs) throws SQLException {
		Address address = new Address(DaoUtils.getInteger(rs,"locationID"));
		address.setStreetNumber(rs.getString("streetNumber"));
		address.setStreetName(rs.getString("streetName"));
		address.setAddressSecondaryUnit(rs.getString("addressSecondaryUnit"));
		address.setCity(rs.getString("city"));
		address.setState(rs.getString("State"));
		address.setPostalcode(rs.getString("postalcode"));
		address.setLocationLatitude(rs.getBigDecimal("LocationLatitude"));
		address.setLocationLongitude(rs.getBigDecimal("LocationLongitude"));
		return address;
	}

	@Override
	public Integer getBookingIdByBookingNumber(String bookingNumber) {
		final String sql = "SELECT b.bookingId FROM Booking b "
				+ "WHERE bookingNumber = ?";
		
		List<Integer> bookingIds = 
				jdbcTemplate.queryForList(sql, Integer.class, bookingNumber);
		
		return DataAccessUtils.singleResult(bookingIds);
	}

	@Override
	public Integer saveAddress(final Address address) {
        log.debug("Inserting row into the Location table: " + address);

        if (address.isEmpty()){
        	log.info("The address is empty and is not stored. ");
        }
        
        final String sql="INSERT into Location(streetNumber,streetName, addressSecondaryUnit, "
        	        				+ "city, state, postalcode,  "
        	        				+ "LocationLatitude, LocationLongitude) "
        	        				+ "values (?,?,?,?,?,?,?,?)";;
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
        	    new PreparedStatementCreator() {
        	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        	            PreparedStatement ps =
        	                connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
        	            setPreparedStatementVariable(address.getStreetNumber(), ps, 1);
        	            setPreparedStatementVariable(address.getStreetName(), ps, 2);
        	            setPreparedStatementVariable(address.getAddressSecondaryUnit(), ps, 3);
        	            setPreparedStatementVariable(address.getCity(), ps, 4);
        	            setPreparedStatementVariable(address.getState(), ps, 5);
        	            setPreparedStatementVariable(address.getPostalcode(), ps, 6);
        	            setPreparedStatementVariable(address.getLocationLatitude(), ps, 7);
        	            setPreparedStatementVariable(address.getLocationLongitude(), ps, 8);
        	            return ps;
        	        }
        	    },
        	    keyHolder);

         return keyHolder.getKey().intValue();
	}

	@Override
	public Person getPersonByBookingNumber(String bookingNumber) {
		final String sql = "SELECT * FROM Person p "
				+ "LEFT JOIN Booking k ON k.PersonID = p.personID "
				+ "LEFT JOIN PersonSexType s ON s.PersonSexTypeID = p.PersonSexTypeID "
				+ "LEFT JOIN PersonRaceType r ON r.PersonRaceTypeID = p.PersonRaceTypeID "
				+ "LEFT JOIN LanguageType l on l.languageTypeID = p.languageTypeID "
				+ "LEFT JOIN HousingStatusType h ON h.HousingStatusTypeID = b.HousingStatusTypeID "
				+ "LEFT JOIN EducationLevelType e ON e.EducationLevelTypeID = b.EducationLevelTypeID "
				+ "LEFT JOIN OccupationType o on o.OccupationTypeID = b.OccupationTypeID "
				+ "LEFT JOIN IncomeLevelType i on i.IncomeLevelTypeID = b.IncomeLevelTypeID "
				+ "LEFT JOIN MilitaryServiceStatusType m on m.MilitaryServiceStatusTypeID = b.MilitaryServiceStatusTypeID "
				+ "WHERE p.BookingNumber = ?"; 
		List<Person> persons = 
				jdbcTemplate.query(sql, 
						new PersonRowMapper(), bookingNumber);
		return DataAccessUtils.singleResult(persons);
	}

	@Override
	public void updateCustodyStatusChangeBookingId(Integer bookingId, String bookingNumber) {
		final String sql = "UPDATE custodyStatusChange SET bookingId = ? WHERE bookingNumber = ? AND (bookingId is null OR bookingId != ?)"; 
		jdbcTemplate.update(sql, bookingId, bookingNumber, bookingId);
	}

	@Override
	public void updateCustodyReleaseBookingId(Integer bookingId, String bookingNumber) {
		final String sql = "UPDATE custodyRelease SET bookingId = ? WHERE bookingNumber = ? AND (bookingId is null OR bookingId != ?)"; 
		jdbcTemplate.update(sql, bookingId, bookingNumber, bookingId);
	}

	@Override
	public CustodyRelease getCustodyReleaseByBookingNumber(String bookingNumber) {
		final String sql = "Select top 1 * from CustodyRelease where BookingNumber = ? order by CustodyReleaseTimestamp desc";
		
		List<CustodyRelease> custodyReleases = 
				jdbcTemplate.query(sql, new CustodyReleaseRowMapper(), bookingNumber);
		return DataAccessUtils.singleResult(custodyReleases);
	}

	@Override
	public List<CustodyStatusChange> getCustodyStatusChangesByBookingNumber(
			String bookingNumber) {
		final String sql = "SELECT * FROM CustodyStatusChange c "
				+ "WHERE BookingNumber = ?";
		
		List<CustodyStatusChange> custodyStatusChanges = 
				jdbcTemplate.query(sql, 
						new CustodyStatusChangeRowMapper(), bookingNumber);
		
		return custodyStatusChanges;
	}

}
