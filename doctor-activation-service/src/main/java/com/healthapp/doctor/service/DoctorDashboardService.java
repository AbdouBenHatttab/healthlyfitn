package com.healthapp.doctor.service;

import com.healthapp.doctor.dto.response.AppointmentResponse;
import com.healthapp.doctor.dto.response.DoctorDashboardResponse;
import com.healthapp.doctor.dto.response.PatientSummaryResponse;
import com.healthapp.doctor.entity.Appointment;
import com.healthapp.doctor.entity.Doctor;
import com.healthapp.doctor.entity.Patient;
import com.healthapp.doctor.repository.AppointmentRepository;
import com.healthapp.doctor.repository.DoctorRepository;
import com.healthapp.doctor.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DoctorDashboardService - Business logic for doctor dashboard
 *
 * Provides dashboard statistics, patient lists, and appointment lists
 * using real data from MongoDB.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorDashboardService {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    // ===================================
    // FEATURE 1: Dashboard Statistics
    // ===================================

    /**
     * Get dashboard statistics for a doctor
     *
     * @param userId The user ID from JWT token
     * @return Dashboard statistics
     */
    public DoctorDashboardResponse getDashboardStatistics(String userId) {
        log.info("📊 Generating dashboard for userId: {}", userId);

        // Find doctor by userId
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Calculate date ranges
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfToday = now.toLocalDate().atTime(LocalTime.MAX);
        LocalDateTime startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1)
                .toLocalDate().atStartOfDay();
        LocalDateTime startOfMonth = YearMonth.now().atDay(1).atStartOfDay();

        // Get statistics from database
        long totalPatients = patientRepository.countByPrimaryDoctorId(doctor.getId());
        long activePatients = patientRepository.countByPrimaryDoctorIdAndPatientStatus(
                doctor.getId(), "ACTIVE");

        long totalAppointments = appointmentRepository.countByDoctorId(doctor.getId());
        long upcomingAppointments = appointmentRepository
                .findUpcomingAppointments(doctor.getId(), now).size();

        // Today's appointments
        List<Appointment> todayAppts = appointmentRepository.findTodayAppointments(
                doctor.getId(), startOfToday, endOfToday);
        long todayAppointments = todayAppts.size();
        long completedToday = todayAppts.stream()
                .filter(a -> "COMPLETED".equals(a.getStatus())).count();
        long pendingToday = todayAppts.stream()
                .filter(a -> "SCHEDULED".equals(a.getStatus()) ||
                        "CONFIRMED".equals(a.getStatus())).count();

        // This week appointments
        List<Appointment> weekAppts = appointmentRepository.findByDoctorIdAndDateRange(
                doctor.getId(), startOfWeek, now);
        long thisWeekAppointments = weekAppts.size();

        // This month consultations
        List<Appointment> monthAppts = appointmentRepository.findByDoctorIdAndDateRange(
                doctor.getId(), startOfMonth, now);
        long thisMonthConsultations = monthAppts.stream()
                .filter(a -> "COMPLETED".equals(a.getStatus())).count();

        // New patients this month
        List<Patient> patients = patientRepository.findByPrimaryDoctorId(doctor.getId());
        long newPatientsThisMonth = patients.stream()
                .filter(p -> p.getCreatedAt() != null &&
                        p.getCreatedAt().isAfter(startOfMonth))
                .count();

        log.info("✅ Dashboard generated: {} patients, {} appointments today",
                totalPatients, todayAppointments);

        return DoctorDashboardResponse.builder()
                .doctorId(doctor.getId())
                .doctorName(doctor.getFullName())
                .specialization(doctor.getSpecialization())
                .totalPatients((int) totalPatients)
                .totalConsultations(doctor.getTotalConsultations() != null ?
                        doctor.getTotalConsultations() : 0)
                .averageRating(doctor.getAverageRating() != null ?
                        doctor.getAverageRating() : 0.0)
                .upcomingAppointments((int) upcomingAppointments)
                .todayAppointments((int) todayAppointments)
                .thisWeekAppointments((int) thisWeekAppointments)
                .thisMonthConsultations((int) thisMonthConsultations)
                .newPatientsThisMonth((int) newPatientsThisMonth)
                .activePatients((int) activePatients)
                .completedAppointmentsToday((int) completedToday)
                .pendingAppointments((int) pendingToday)
                .lastLoginAt(doctor.getUpdatedAt())
                .generatedAt(LocalDateTime.now())
                .build();
    }

    // ===================================
    // FEATURE 2: Patient List
    // ===================================

    /**
     * Get patient list for a doctor
     *
     * @param userId Doctor's user ID
     * @param status Filter by status (ACTIVE, INACTIVE, ALL)
     * @param search Search term (firstName, lastName, email)
     * @param page Page number (0-based)
     * @param size Page size
     * @return List of patients
     */
    public List<PatientSummaryResponse> getPatientList(
            String userId, String status, String search, int page, int size) {

        log.info("🏥 Fetching patients - Status: {}, Search: {}, Page: {}/{}",
                status, search, page, size);

        // Find doctor
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Create pageable with sorting (newest first)
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Patient> patientsPage;

        // Query based on search and status
        if (search != null && !search.trim().isEmpty()) {
            // With search term
            if ("ALL".equalsIgnoreCase(status)) {
                patientsPage = patientRepository.searchPatientsByDoctor(
                        doctor.getId(), search, pageable);
            } else {
                patientsPage = patientRepository.searchPatientsByDoctorAndStatus(
                        doctor.getId(), status, search, pageable);
            }
        } else {
            // Without search term
            if ("ALL".equalsIgnoreCase(status)) {
                patientsPage = patientRepository.findByPrimaryDoctorId(
                        doctor.getId(), pageable);
            } else {
                patientsPage = patientRepository.findByPrimaryDoctorIdAndPatientStatus(
                        doctor.getId(), status, pageable);
            }
        }

        log.info("✅ Found {} patients", patientsPage.getTotalElements());

        return patientsPage.getContent().stream()
                .map(this::mapToPatientSummaryResponse)
                .collect(Collectors.toList());
    }

    // ===================================
    // FEATURE 3: Scheduled Appointments
    // ===================================

    /**
     * Get scheduled appointments for a doctor
     *
     * @param userId Doctor's user ID
     * @param status Filter by status (SCHEDULED, COMPLETED, ALL)
     * @param from Start date (yyyy-MM-dd)
     * @param to End date (yyyy-MM-dd)
     * @param page Page number
     * @param size Page size
     * @return List of appointments
     */
    public List<AppointmentResponse> getScheduledAppointments(
            String userId, String status, String from, String to, int page, int size) {

        log.info("📅 Fetching appointments - Status: {}, From: {}, To: {}",
                status, from, to);

        // Find doctor
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Create pageable (sorted by appointment date)
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.ASC, "appointmentDate"));

        // Parse date range
        LocalDateTime startDate = from != null ?
                LocalDate.parse(from).atStartOfDay() : LocalDate.now().atStartOfDay();
        LocalDateTime endDate = to != null ?
                LocalDate.parse(to).atTime(LocalTime.MAX) : LocalDate.now().plusYears(1).atTime(LocalTime.MAX);

        Page<Appointment> appointmentsPage;

        // Query based on status
        if ("ALL".equalsIgnoreCase(status)) {
            appointmentsPage = appointmentRepository.findByDoctorIdAndDateRange(
                    doctor.getId(), startDate, endDate, pageable);
        } else {
            appointmentsPage = appointmentRepository.findByDoctorIdAndStatusAndDateRange(
                    doctor.getId(), status, startDate, endDate, pageable);
        }

        log.info("✅ Found {} appointments", appointmentsPage.getTotalElements());

        return appointmentsPage.getContent().stream()
                .map(this::mapToAppointmentResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get upcoming appointments (next 7 days)
     */
    public List<AppointmentResponse> getUpcomingAppointments(String userId) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysLater = now.plusDays(7);

        List<Appointment> appointments = appointmentRepository
                .findUpcomingAppointments(doctor.getId(), now);

        return appointments.stream()
                .filter(a -> a.getAppointmentDate().isBefore(sevenDaysLater))
                .sorted((a1, a2) -> a1.getAppointmentDate().compareTo(a2.getAppointmentDate()))
                .map(this::mapToAppointmentResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get today's appointments
     */
    public List<AppointmentResponse> getTodayAppointments(String userId) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        List<Appointment> appointments = appointmentRepository.findTodayAppointments(
                doctor.getId(), startOfDay, endOfDay);

        return appointments.stream()
                .sorted((a1, a2) -> a1.getAppointmentDate().compareTo(a2.getAppointmentDate()))
                .map(this::mapToAppointmentResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get patient count by status
     */
    public Map<String, Long> getPatientCountByStatus(String userId) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Map<String, Long> counts = new HashMap<>();
        counts.put("active", patientRepository.countByPrimaryDoctorIdAndPatientStatus(
                doctor.getId(), "ACTIVE"));
        counts.put("inactive", patientRepository.countByPrimaryDoctorIdAndPatientStatus(
                doctor.getId(), "INACTIVE"));
        counts.put("total", patientRepository.countByPrimaryDoctorId(doctor.getId()));

        return counts;
    }

    /**
     * Get appointment count by status
     */
    public Map<String, Long> getAppointmentCountByStatus(String userId) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Map<String, Long> counts = new HashMap<>();
        counts.put("scheduled", appointmentRepository.countByDoctorIdAndStatus(
                doctor.getId(), "SCHEDULED"));
        counts.put("confirmed", appointmentRepository.countByDoctorIdAndStatus(
                doctor.getId(), "CONFIRMED"));
        counts.put("completed", appointmentRepository.countByDoctorIdAndStatus(
                doctor.getId(), "COMPLETED"));
        counts.put("cancelled", appointmentRepository.countByDoctorIdAndStatus(
                doctor.getId(), "CANCELLED"));
        counts.put("no_show", appointmentRepository.countByDoctorIdAndStatus(
                doctor.getId(), "NO_SHOW"));
        counts.put("total", appointmentRepository.countByDoctorId(doctor.getId()));

        return counts;
    }

    // ===================================
    // Mapping Methods
    // ===================================

    private PatientSummaryResponse mapToPatientSummaryResponse(Patient patient) {
        // Find next appointment
        List<Appointment> upcomingAppts = appointmentRepository
                .findByDoctorIdAndPatientIdOrderByAppointmentDateDesc(
                        patient.getPrimaryDoctorId(), patient.getId());

        Appointment nextAppt = upcomingAppts.stream()
                .filter(a -> a.isScheduled() &&
                        a.getAppointmentDate().isAfter(LocalDateTime.now()))
                .findFirst()
                .orElse(null);

        return PatientSummaryResponse.builder()
                .patientId(patient.getId())
                .userId(patient.getUserId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .fullName(patient.getFullName())
                .email(patient.getEmail())
                .phoneNumber(patient.getPhoneNumber())
                .birthDate(patient.getBirthDate())
                .age(patient.getAge())
                .gender(patient.getGender())
                .bloodType(patient.getBloodType())
                .allergies(patient.getAllergies())
                .chronicConditions(patient.getChronicConditions())
                .patientStatus(patient.getPatientStatus())
                .firstConsultationDate(patient.getFirstConsultationDate())
                .lastConsultationDate(patient.getLastConsultationDate())
                .totalConsultations(patient.getTotalConsultations())
                .nextAppointmentDate(nextAppt != null ? nextAppt.getAppointmentDate() : null)
                .nextAppointmentType(nextAppt != null ? nextAppt.getAppointmentType() : null)
                .createdAt(patient.getCreatedAt())
                .build();
    }

    private AppointmentResponse mapToAppointmentResponse(Appointment appointment) {
        // Get patient info
        Patient patient = patientRepository.findById(appointment.getPatientId())
                .orElse(null);

        return AppointmentResponse.builder()
                .appointmentId(appointment.getId())
                .doctorId(appointment.getDoctorId())
                .patientId(appointment.getPatientId())
                .patientName(appointment.getPatientName())
                .patientEmail(appointment.getPatientEmail())
                .patientPhone(appointment.getPatientPhone())
                .patientAge(patient != null ? patient.getAge() : null)
                .patientGender(patient != null && patient.getGender() != null ?
                        patient.getGender().toString() : null)
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentType(appointment.getAppointmentType())
                .status(appointment.getStatus())
                .durationMinutes(appointment.getDurationMinutes())
                .reasonForVisit(appointment.getReasonForVisit())
                .symptoms(appointment.getSymptoms())
                .notes(appointment.getPatientNotes())
                .diagnosis(appointment.getDiagnosis())
                .prescription(appointment.getPrescription())
                .treatmentPlan(appointment.getTreatmentPlan())
                .createdAt(appointment.getCreatedAt())
                .completedAt(appointment.getCompletedAt())
                .cancelledAt(appointment.getCancelledAt())
                .cancellationReason(appointment.getCancellationReason())
                .build();
    }
}