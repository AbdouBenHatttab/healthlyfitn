package com.healthapp.doctor.entity;

import com.healthapp.shared.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

/**
 * Patient Entity - Stored in health_doctor_db
 *
 * Each patient is assigned to a primary doctor.
 * Patients can have appointments with their assigned doctor.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "patients")
public class Patient {

    @Id
    private String id;

    // User reference (if integrated with user-service later)
    @Indexed
    private String userId;

    @Indexed
    private String email;

    // Personal Information
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private String phoneNumber;
    private String profilePictureUrl;

    // Medical Information
    private String bloodType; // A+, B+, O-, AB+, etc.
    private String[] allergies; // ["Penicillin", "Peanuts"]
    private String[] chronicConditions; // ["Diabetes", "Hypertension"]

    // Emergency Contact
    private String emergencyContactName;
    private String emergencyContactPhone;

    // Address
    private String address;
    private String city;
    private String postalCode;
    private String country;

    // Doctor Assignment
    @Indexed
    private String primaryDoctorId; // The main doctor managing this patient

    // Status
    @Builder.Default
    private String patientStatus = "ACTIVE"; // ACTIVE, INACTIVE, SUSPENDED

    // Medical History Summary
    private LocalDateTime firstConsultationDate;
    private LocalDateTime lastConsultationDate;

    // Statistics
    @Builder.Default
    private Integer totalConsultations = 0;

    @Builder.Default
    private Integer totalAppointments = 0;

    // Medical notes from doctor
    private String medicalNotes;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // ===================================
    // Business Methods
    // ===================================

    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Calculate age from birth date
     */
    public Integer getAge() {
        if (birthDate == null) {
            return null;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Check if patient is active
     */
    public boolean isActive() {
        return "ACTIVE".equals(patientStatus);
    }

    /**
     * Check if patient belongs to specific doctor
     */
    public boolean belongsToDoctor(String doctorId) {
        return primaryDoctorId != null && primaryDoctorId.equals(doctorId);
    }
}