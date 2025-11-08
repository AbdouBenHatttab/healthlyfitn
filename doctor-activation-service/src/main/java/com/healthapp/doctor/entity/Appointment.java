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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "appointments")
public class Appointment {

    @Id
    private String id;

    @Indexed
    private String doctorId;

    @Indexed
    private String patientUserId; // ✅ CHANGED: patientId → patientUserId (references User.id from user-service)

    // Cached patient info for performance (denormalized)
    private String patientName;
    private String patientEmail;
    private String patientPhone;

    // Appointment Scheduling
    @Indexed
    private LocalDateTime appointmentDate;
    private LocalDateTime appointmentEndDate;

    @Builder.Default
    private Integer durationMinutes = 30;

    // Appointment Details
    @Builder.Default
    private String appointmentType = "CONSULTATION";

    @Indexed
    @Builder.Default
    private String status = "SCHEDULED";

    private String reasonForVisit;
    private String symptoms;
    private String patientNotes;

    // Consultation Results
    private String diagnosis;
    private String prescription;
    private String treatmentPlan;
    private String doctorNotes;
    private String followUpInstructions;
    private LocalDateTime followUpDate;

    // Payment
    private Double consultationFee;
    @Builder.Default
    private String paymentStatus = "PENDING";
    private String paymentMethod;

    // Cancellation/Rescheduling
    private LocalDateTime cancelledAt;
    private String cancelledBy;
    private String cancellationReason;
    private LocalDateTime rescheduledFrom;
    private String rescheduledReason;

    // Completion
    private LocalDateTime completedAt;
    private LocalDateTime checkedInAt;

    // Patient Feedback
    private Integer rating;
    private String patientFeedback;

    // Reminders
    @Builder.Default
    private Boolean reminderSent = false;
    private LocalDateTime reminderSentAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String createdBy;

    // Business Methods
    public boolean isScheduled() {
        return "SCHEDULED".equals(status) || "CONFIRMED".equals(status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }

    public boolean canBeRescheduled() {
        return isScheduled() && appointmentDate.isAfter(LocalDateTime.now());
    }

    public boolean canBeCancelled() {
        return isScheduled() &&
                appointmentDate.isAfter(LocalDateTime.now().plusHours(24));
    }

    public void complete(String diagnosis, String prescription,
                         String treatmentPlan, String notes) {
        this.status = "COMPLETED";
        this.completedAt = LocalDateTime.now();
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.treatmentPlan = treatmentPlan;
        this.doctorNotes = notes;
    }

    public void cancel(String cancelledBy, String reason) {
        this.status = "CANCELLED";
        this.cancelledAt = LocalDateTime.now();
        this.cancelledBy = cancelledBy;
        this.cancellationReason = reason;
    }

    public void markAsNoShow() {
        this.status = "NO_SHOW";
        this.updatedAt = LocalDateTime.now();
    }

    public void confirm() {
        this.status = "CONFIRMED";
    }

    public void checkIn() {
        this.checkedInAt = LocalDateTime.now();
        this.status = "IN_PROGRESS";
    }
}