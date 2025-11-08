package com.healthapp.doctor.service;

import com.healthapp.doctor.client.UserServiceClient;
import com.healthapp.doctor.entity.Doctor;
import com.healthapp.doctor.entity.DoctorPatient;
import com.healthapp.doctor.repository.DoctorPatientRepository;
import com.healthapp.doctor.repository.DoctorRepository;
import com.healthapp.shared.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * PatientAssignmentService - Manages patient-doctor assignments
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PatientAssignmentService {

    private final DoctorRepository doctorRepository;
    private final DoctorPatientRepository doctorPatientRepository;
    private final UserServiceClient userServiceClient;

    /**
     * ✅ Assign a patient to a doctor (or return existing relationship)
     *
     * Called when:
     * - A patient books their first appointment with a doctor
     * - A doctor manually adds a patient
     */
    public DoctorPatient assignPatientToDoctor(String doctorId, String patientUserId) {
        log.info("📋 Assigning patient {} to doctor {}", patientUserId, doctorId);

        // Check if doctor exists
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found: " + doctorId));

        // Verify patient exists in user-service
        UserDto patient = userServiceClient.getUserById(patientUserId);
        if (patient == null) {
            throw new RuntimeException("Patient not found in user service: " + patientUserId);
        }

        // Check if relationship already exists
        return doctorPatientRepository.findByDoctorIdAndPatientUserId(doctorId, patientUserId)
                .orElseGet(() -> {
                    log.info("✅ Creating new doctor-patient relationship");

                    DoctorPatient relationship = DoctorPatient.builder()
                            .doctorId(doctorId)
                            .patientUserId(patientUserId)
                            .status("ACTIVE")
                            .assignedAt(LocalDateTime.now())
                            .totalConsultations(0)
                            .totalAppointments(0)
                            .build();

                    DoctorPatient saved = doctorPatientRepository.save(relationship);

                    // Update doctor's total patients count
                    updateDoctorPatientCount(doctorId);

                    log.info("✅ Patient assigned successfully");
                    return saved;
                });
    }

    /**
     * Verify if a patient belongs to a doctor
     */
    public boolean verifyPatientBelongsToDoctor(String doctorId, String patientUserId) {
        return doctorPatientRepository.existsByDoctorIdAndPatientUserId(doctorId, patientUserId);
    }

    /**
     * Get doctor-patient relationship
     */
    public DoctorPatient getRelationship(String doctorId, String patientUserId) {
        return doctorPatientRepository.findByDoctorIdAndPatientUserId(doctorId, patientUserId)
                .orElseThrow(() -> new RuntimeException(
                        "Patient is not assigned to this doctor"));
    }

    /**
     * Update doctor's total patient count
     */
    private void updateDoctorPatientCount(String doctorId) {
        long activePatientCount = doctorPatientRepository
                .countByDoctorIdAndStatus(doctorId, "ACTIVE");

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        doctor.setTotalPatients((int) activePatientCount);
        doctorRepository.save(doctor);

        log.debug("Updated doctor {} patient count to {}", doctorId, activePatientCount);
    }

    /**
     * ✅ Increment consultation count when appointment is completed
     */
    public void recordConsultation(String doctorId, String patientUserId) {
        DoctorPatient relationship = getRelationship(doctorId, patientUserId);
        relationship.incrementConsultations();
        doctorPatientRepository.save(relationship);

        log.info("📊 Recorded consultation for patient {} with doctor {}",
                patientUserId, doctorId);
    }

    /**
     * ✅ Increment appointment count when appointment is created
     */
    public void recordAppointment(String doctorId, String patientUserId) {
        DoctorPatient relationship = getRelationship(doctorId, patientUserId);
        relationship.incrementAppointments();
        doctorPatientRepository.save(relationship);

        log.info("📅 Recorded appointment for patient {} with doctor {}",
                patientUserId, doctorId);
    }
}