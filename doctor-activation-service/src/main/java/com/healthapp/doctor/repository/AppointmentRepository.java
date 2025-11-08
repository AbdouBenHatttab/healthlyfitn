package com.healthapp.doctor.repository;

import com.healthapp.doctor.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {

    // Find by doctor
    List<Appointment> findByDoctorId(String doctorId);
    Page<Appointment> findByDoctorId(String doctorId, Pageable pageable);
    List<Appointment> findByDoctorIdAndStatus(String doctorId, String status);
    Page<Appointment> findByDoctorIdAndStatus(String doctorId, String status, Pageable pageable);

    // ✅ CHANGED: patientId → patientUserId
    List<Appointment> findByPatientUserId(String patientUserId);
    Page<Appointment> findByPatientUserId(String patientUserId, Pageable pageable);

    // Date range queries
    @Query("{ 'doctorId': ?0, 'appointmentDate': { $gte: ?1, $lte: ?2 } }")
    List<Appointment> findByDoctorIdAndDateRange(
            String doctorId, LocalDateTime start, LocalDateTime end);

    @Query("{ 'doctorId': ?0, 'appointmentDate': { $gte: ?1, $lte: ?2 } }")
    Page<Appointment> findByDoctorIdAndDateRange(
            String doctorId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("{ 'doctorId': ?0, 'status': ?1, 'appointmentDate': { $gte: ?2, $lte: ?3 } }")
    List<Appointment> findByDoctorIdAndStatusAndDateRange(
            String doctorId, String status, LocalDateTime start, LocalDateTime end);

    @Query("{ 'doctorId': ?0, 'status': ?1, 'appointmentDate': { $gte: ?2, $lte: ?3 } }")
    Page<Appointment> findByDoctorIdAndStatusAndDateRange(
            String doctorId, String status, LocalDateTime start, LocalDateTime end, Pageable pageable);

    // Specialized queries
    @Query("{ 'doctorId': ?0, 'status': { $in: ['SCHEDULED', 'CONFIRMED'] }, 'appointmentDate': { $gte: ?1 } }")
    List<Appointment> findUpcomingAppointments(String doctorId, LocalDateTime fromDate);

    @Query("{ 'doctorId': ?0, 'appointmentDate': { $gte: ?1, $lt: ?2 } }")
    List<Appointment> findTodayAppointments(
            String doctorId, LocalDateTime startOfDay, LocalDateTime endOfDay);

    // ✅ CHANGED: patientId → patientUserId
    List<Appointment> findByDoctorIdAndPatientUserId(String doctorId, String patientUserId);

    List<Appointment> findByDoctorIdAndPatientUserIdOrderByAppointmentDateDesc(
            String doctorId, String patientUserId);

    // Count queries
    long countByDoctorId(String doctorId);
    long countByDoctorIdAndStatus(String doctorId, String status);

    // ✅ CHANGED: patientId → patientUserId
    long countByPatientUserId(String patientUserId);

    // Validation
    boolean existsByIdAndDoctorId(String appointmentId, String doctorId);

    @Query("{ 'doctorId': ?0, " +
            "'status': { $in: ['SCHEDULED', 'CONFIRMED', 'IN_PROGRESS'] }, " +
            "$or: [ " +
            "  { 'appointmentDate': { $gte: ?1, $lt: ?2 } }, " +
            "  { 'appointmentEndDate': { $gt: ?1, $lte: ?2 } }, " +
            "  { 'appointmentDate': { $lte: ?1 }, 'appointmentEndDate': { $gte: ?2 } } " +
            "] }")
    List<Appointment> findConflictingAppointments(
            String doctorId, LocalDateTime start, LocalDateTime end);
}