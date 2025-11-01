package com.healthapp.doctor.dto.response;

import com.healthapp.shared.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * PatientSummaryResponse - Summary information about a patient
 *
 * Used in patient lists and search results.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientSummaryResponse {

    // Basic Information
    private String patientId;
    private String userId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phoneNumber;

    // Demographics
    private LocalDate birthDate;
    private Integer age;
    private Gender gender;

    // Medical Information
    private String bloodType;
    private String[] allergies;
    private String[] chronicConditions;

    // Status
    private String patientStatus; // ACTIVE, INACTIVE

    // Consultation History
    private LocalDateTime firstConsultationDate;
    private LocalDateTime lastConsultationDate;
    private Integer totalConsultations;

    // Upcoming Appointments
    private LocalDateTime nextAppointmentDate;
    private String nextAppointmentType;

    // Metadata
    private LocalDateTime createdAt;
}