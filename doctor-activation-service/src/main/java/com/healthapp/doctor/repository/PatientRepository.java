package com.healthapp.doctor.repository;

import com.healthapp.doctor.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * PatientRepository - MongoDB repository for Patient entity
 *
 * Provides methods to query patients by doctor, status, and search terms.
 */
@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {

    // ===================================
    // Basic Queries
    // ===================================

    Optional<Patient> findByEmail(String email);

    boolean existsByEmail(String email);

    // ===================================
    // Find Patients by Doctor
    // ===================================

    /**
     * Find all patients assigned to a doctor
     */
    List<Patient> findByPrimaryDoctorId(String doctorId);

    /**
     * Find all patients assigned to a doctor (with pagination)
     */
    Page<Patient> findByPrimaryDoctorId(String doctorId, Pageable pageable);

    /**
     * Find patients by doctor and status
     */
    List<Patient> findByPrimaryDoctorIdAndPatientStatus(String doctorId, String status);

    /**
     * Find patients by doctor and status (with pagination)
     */
    Page<Patient> findByPrimaryDoctorIdAndPatientStatus(
            String doctorId, String status, Pageable pageable);

    // ===================================
    // Search Patients
    // ===================================

    /**
     * Search patients by doctor and search term (firstName, lastName, or email)
     * Case-insensitive search using regex
     */
    @Query("{ 'primaryDoctorId': ?0, $or: [ " +
            "{ 'firstName': { $regex: ?1, $options: 'i' } }, " +
            "{ 'lastName': { $regex: ?1, $options: 'i' } }, " +
            "{ 'email': { $regex: ?1, $options: 'i' } } " +
            "] }")
    Page<Patient> searchPatientsByDoctor(String doctorId, String searchTerm, Pageable pageable);

    /**
     * Search patients by doctor, status, and search term
     */
    @Query("{ 'primaryDoctorId': ?0, 'patientStatus': ?1, $or: [ " +
            "{ 'firstName': { $regex: ?2, $options: 'i' } }, " +
            "{ 'lastName': { $regex: ?2, $options: 'i' } }, " +
            "{ 'email': { $regex: ?2, $options: 'i' } } " +
            "] }")
    Page<Patient> searchPatientsByDoctorAndStatus(
            String doctorId, String status, String searchTerm, Pageable pageable);

    // ===================================
    // Count Queries
    // ===================================

    /**
     * Count patients by doctor
     */
    long countByPrimaryDoctorId(String doctorId);

    /**
     * Count patients by doctor and status
     */
    long countByPrimaryDoctorIdAndPatientStatus(String doctorId, String status);

    // ===================================
    // Validation Queries
    // ===================================

    /**
     * Check if patient exists and belongs to doctor
     */
    boolean existsByIdAndPrimaryDoctorId(String patientId, String doctorId);
}