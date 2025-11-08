package com.healthapp.doctor.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * DoctorPatient - Relationship between doctors and their patients
 *
 * This entity stores ONLY the relationship, not patient data.
 * Patient data comes from user-service via Feign.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "doctor_patients")
@CompoundIndex(name = "doctor_patient_idx", def = "{'doctorId': 1, 'patientUserId': 1}", unique = true)
public class DoctorPatient {

    @Id
    private String id;

    @Indexed
    private String doctorId; // Reference to Doctor entity

    @Indexed
    private String patientUserId; // Reference to User.id in user-service

    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, INACTIVE, TERMINATED

    @CreatedDate
    private LocalDateTime assignedAt;

    private LocalDateTime firstConsultationDate;
    private LocalDateTime lastConsultationDate;

    @Builder.Default
    private Integer totalConsultations = 0;

    @Builder.Default
    private Integer totalAppointments = 0;

    private String medicalNotes;

    private LocalDateTime terminatedAt;
    private String terminationReason;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    public void terminate(String reason) {
        this.status = "TERMINATED";
        this.terminatedAt = LocalDateTime.now();
        this.terminationReason = reason;
    }

    public void activate() {
        this.status = "ACTIVE";
        this.terminatedAt = null;
        this.terminationReason = null;
    }

    public void incrementConsultations() {
        this.totalConsultations++;
        this.lastConsultationDate = LocalDateTime.now();
        if (this.firstConsultationDate == null) {
            this.firstConsultationDate = LocalDateTime.now();
        }
    }

    public void incrementAppointments() {
        this.totalAppointments++;
    }
}