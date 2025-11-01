package com.healthapp.doctor.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Appointment Entity - Stored in health_doctor_db
 *
 * Represents medical appointments between doctors and patients.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "appointments")
public class Appointment {

    @Id
    private String id;

    // References
    @Indexed
    private String doctorId;

    @Indexed
    private String patientId;

    // Cached patient info (denormalized for performance)
    private String patientName;
    private String patientEmail;
    private String patientPhone;

    // Appointment Scheduling
    @Indexed
    private LocalDateTime appointmentDate;

    private LocalDateTime appointmentEndDate; // appointmentDate + duration

    @Builder.Default
    private Integer durationMinutes = 30; // Default 30 minutes

    // Appointment Details
    @Builder.Default
    private String appointmentType = "CONSULTATION";
    // Types: CONSULTATION, FOLLOW_UP, EMERGENCY, CHECK_UP, VACCINATION

    @Indexed
    @Builder.Default
    private String status = "SCHEDULED";
    // Status: SCHEDULED, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW

    // Patient's reason for visit
    private String reasonForVisit;
    private String symptoms;
    private String patientNotes;

    // Consultation Results (filled after appointment)
    private String diagnosis;
    private String prescription;
    private String treatmentPlan;
    private String doctorNotes;
    private String followUpInstructions;
    private LocalDateTime followUpDate;

    // Payment (optional)
    private Double consultationFee;

    @Builder.Default
    private String paymentStatus = "PENDING"; // PENDING, PAID, REFUNDED

    private String paymentMethod; // CASH, CARD, INSURANCE

    // Cancellation/Rescheduling
    private LocalDateTime cancelledAt;
    private String cancelledBy; // USER_ID or DOCTOR_ID
    private String cancellationReason;

    private LocalDateTime rescheduledFrom; // Original date if rescheduled
    private String rescheduledReason;

    // Completion
    private LocalDateTime completedAt;
    private LocalDateTime checkedInAt; // When patient arrived

    // Patient Feedback (optional)
    private Integer rating; // 1-5 stars
    private String patientFeedback;

    // Reminders
    @Builder.Default
    private Boolean reminderSent = false;

    private LocalDateTime reminderSentAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String createdBy; // USER_ID who created

    // ===================================
    // Business Methods
    // ===================================

    public boolean isScheduled() {
        return "SCHEDULED".equals(status) || "CONFIRMED".equals(status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }

    /**
     * Check if appointment can be rescheduled
     */
    public boolean canBeRescheduled() {
        return isScheduled() && appointmentDate.isAfter(LocalDateTime.now());
    }

    /**
     * Check if appointment can be cancelled (24h notice)
     */
    public boolean canBeCancelled() {
        return isScheduled() &&
                appointmentDate.isAfter(LocalDateTime.now().plusHours(24));
    }

    /**
     * Complete appointment with consultation details
     */
    public void complete(String diagnosis, String prescription,
                         String treatmentPlan, String notes) {
        this.status = "COMPLETED";
        this.completedAt = LocalDateTime.now();
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.treatmentPlan = treatmentPlan;
        this.doctorNotes = notes;
    }

    /**
     * Cancel appointment
     */
    public void cancel(String cancelledBy, String reason) {
        this.status = "CANCELLED";
        this.cancelledAt = LocalDateTime.now();
        this.cancelledBy = cancelledBy;
        this.cancellationReason = reason;
    }

    /**
     * Mark patient as no-show
     */
    public void markAsNoShow() {
        this.status = "NO_SHOW";
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Confirm appointment
     */
    public void confirm() {
        this.status = "CONFIRMED";
    }

    /**
     * Patient checked in
     */
    public void checkIn() {
        this.checkedInAt = LocalDateTime.now();
        this.status = "IN_PROGRESS";
    }
}