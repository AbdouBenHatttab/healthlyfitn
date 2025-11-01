package com.healthapp.doctor.repository;

import com.healthapp.doctor.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AppointmentRepository - MongoDB repository for Appointment entity
 *
 * Provides methods to query appointments by doctor, patient, date range, and status.
 */
@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {

    // ===================================
    // Find Appointments by Doctor
    // ===================================

    /**
     * Find all appointments for a doctor
     */
    List<Appointment> findByDoctorId(String doctorId);

    /**
     * Find all appointments for a doctor (with pagination)
     */
    Page<Appointment> findByDoctorId(String doctorId, Pageable pageable);

    /**
     * Find appointments by doctor and status
     */
    List<Appointment> findByDoctorIdAndStatus(String doctorId, String status);

    /**
     * Find appointments by doctor and status (with pagination)
     */
    Page<Appointment> findByDoctorIdAndStatus(String doctorId, String status, Pageable pageable);

    // ===================================
    // Find Appointments by Patient
    // ===================================

    /**
     * Find all appointments for a patient
     */
    List<Appointment> findByPatientId(String patientId);

    /**
     * Find all appointments for a patient (with pagination)
     */
    Page<Appointment> findByPatientId(String patientId, Pageable pageable);

    // ===================================
    // Find Appointments by Date Range
    // ===================================

    /**
     * Find appointments by doctor within date range
     */
    @Query("{ 'doctorId': ?0, 'appointmentDate': { $gte: ?1, $lte: ?2 } }")
    List<Appointment> findByDoctorIdAndDateRange(
            String doctorId, LocalDateTime start, LocalDateTime end);

    /**
     * Find appointments by doctor within date range (with pagination)
     */
    @Query("{ 'doctorId': ?0, 'appointmentDate': { $gte: ?1, $lte: ?2 } }")
    Page<Appointment> findByDoctorIdAndDateRange(
            String doctorId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    /**
     * Find appointments by doctor, status, and date range
     */
    @Query("{ 'doctorId': ?0, 'status': ?1, 'appointmentDate': { $gte: ?2, $lte: ?3 } }")
    List<Appointment> findByDoctorIdAndStatusAndDateRange(
            String doctorId, String status, LocalDateTime start, LocalDateTime end);

    /**
     * Find appointments by doctor, status, and date range (with pagination)
     */
    @Query("{ 'doctorId': ?0, 'status': ?1, 'appointmentDate': { $gte: ?2, $lte: ?3 } }")
    Page<Appointment> findByDoctorIdAndStatusAndDateRange(
            String doctorId, String status, LocalDateTime start, LocalDateTime end, Pageable pageable);

    // ===================================
    // Specialized Queries
    // ===================================

    /**
     * Find upcoming appointments (SCHEDULED or CONFIRMED)
     */
    @Query("{ 'doctorId': ?0, 'status': { $in: ['SCHEDULED', 'CONFIRMED'] }, 'appointmentDate': { $gte: ?1 } }")
    List<Appointment> findUpcomingAppointments(String doctorId, LocalDateTime fromDate);

    /**
     * Find today's appointments
     */
    @Query("{ 'doctorId': ?0, 'appointmentDate': { $gte: ?1, $lt: ?2 } }")
    List<Appointment> findTodayAppointments(
            String doctorId, LocalDateTime startOfDay, LocalDateTime endOfDay);

    /**
     * Find appointments by doctor and patient
     */
    List<Appointment> findByDoctorIdAndPatientId(String doctorId, String patientId);

    /**
     * Find latest appointments for a patient (ordered by date desc)
     */
    List<Appointment> findByDoctorIdAndPatientIdOrderByAppointmentDateDesc(
            String doctorId, String patientId);

    // ===================================
    // Count Queries
    // ===================================

    /**
     * Count appointments by doctor
     */
    long countByDoctorId(String doctorId);

    /**
     * Count appointments by doctor and status
     */
    long countByDoctorIdAndStatus(String doctorId, String status);

    /**
     * Count appointments by patient
     */
    long countByPatientId(String patientId);

    // ===================================
    // Validation Queries
    // ===================================

    /**
     * Check if appointment exists and belongs to doctor
     */
    boolean existsByIdAndDoctorId(String appointmentId, String doctorId);

    /**
     * Check for conflicting appointments (overlapping time slots)
     */
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