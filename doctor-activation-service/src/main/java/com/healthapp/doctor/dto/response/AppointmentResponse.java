package com.healthapp.doctor.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AppointmentResponse - Complete appointment information
 *
 * Used when displaying appointment details to doctors.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {

    // IDs
    private String appointmentId;
    private String doctorId;
    private String patientId;

    // Patient Information
    private String patientName;
    private String patientEmail;
    private String patientPhone;
    private Integer patientAge;
    private String patientGender;

    // Appointment Details
    private LocalDateTime appointmentDate;
    private String appointmentType; // CONSULTATION, FOLLOW_UP, EMERGENCY
    private String status; // SCHEDULED, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED
    private Integer durationMinutes;

    // Clinical Information
    private String reasonForVisit;
    private String symptoms;
    private String notes;

    // Consultation Results (for completed appointments)
    private String diagnosis;
    private String prescription;
    private String treatmentPlan;

    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
}