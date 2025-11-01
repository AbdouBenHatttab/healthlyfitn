package com.healthapp.doctor.controller;

import com.healthapp.doctor.dto.response.DoctorDashboardResponse;
import com.healthapp.doctor.dto.response.PatientSummaryResponse;
import com.healthapp.doctor.dto.response.AppointmentResponse;
import com.healthapp.doctor.service.DoctorDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * DoctorDashboardController - Dashboard endpoints for authenticated doctors
 *
 * All endpoints require DOCTOR role authentication.
 * Doctors can only access their own data.
 */
@RestController
@RequestMapping("/api/doctors/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
@Slf4j
public class DoctorDashboardController {

    private final DoctorDashboardService dashboardService;

    // ===================================
    // FEATURE 1: Dashboard Statistics
    // ===================================

    /**
     * Get doctor dashboard with statistics
     *
     * Returns comprehensive statistics including:
     * - Total patients, consultations, ratings
     * - Today's appointments
     * - Weekly and monthly activity
     * - Active patients count
     *
     * @param authentication JWT authentication (contains userId)
     * @return Dashboard statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<DoctorDashboardResponse> getDashboardStatistics(
            Authentication authentication) {

        String userId = authentication.getName(); // Extract userId from JWT

        log.info("📊 Doctor {} requesting dashboard statistics", userId);

        DoctorDashboardResponse dashboard = dashboardService.getDashboardStatistics(userId);

        return ResponseEntity.ok(dashboard);
    }

    // ===================================
    // FEATURE 2: Patient List
    // ===================================

    /**
     * Get doctor's patient list
     *
     * Supports filtering and search:
     * - status: ACTIVE, INACTIVE, ALL (default: ACTIVE)
     * - search: Search by name or email
     * - Pagination support
     *
     * Examples:
     * - GET /api/doctors/dashboard/patients
     * - GET /api/doctors/dashboard/patients?status=ACTIVE
     * - GET /api/doctors/dashboard/patients?search=John&page=0&size=20
     *
     * @param authentication JWT authentication
     * @param status Filter by patient status
     * @param search Search term (firstName, lastName, email)
     * @param page Page number (0-based)
     * @param size Page size
     * @return List of patients
     */
    @GetMapping("/patients")
    public ResponseEntity<List<PatientSummaryResponse>> getPatientList(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "ACTIVE") String status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {

        String userId = authentication.getName();

        log.info("🏥 Doctor {} requesting patient list - Status: {}, Search: {}, Page: {}/{}",
                userId, status, search, page, size);

        List<PatientSummaryResponse> patients = dashboardService.getPatientList(
                userId, status, search, page, size);

        log.info("✅ Returning {} patients", patients.size());

        return ResponseEntity.ok(patients);
    }

    // ===================================
    // FEATURE 3: Scheduled Appointments
    // ===================================

    /**
     * Get doctor's scheduled appointments
     *
     * Supports filtering by:
     * - status: SCHEDULED, COMPLETED, CANCELLED, ALL (default: SCHEDULED)
     * - Date range (from/to)
     * - Pagination
     *
     * Examples:
     * - GET /api/doctors/dashboard/appointments
     * - GET /api/doctors/dashboard/appointments?status=SCHEDULED
     * - GET /api/doctors/dashboard/appointments?from=2025-11-01&to=2025-11-30
     *
     * @param authentication JWT authentication
     * @param status Filter by appointment status
     * @param from Start date (ISO format: yyyy-MM-dd)
     * @param to End date (ISO format: yyyy-MM-dd)
     * @param page Page number
     * @param size Page size
     * @return List of appointments
     */
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponse>> getScheduledAppointments(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "SCHEDULED") String status,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {

        String userId = authentication.getName();

        log.info("📅 Doctor {} requesting appointments - Status: {}, From: {}, To: {}",
                userId, status, from, to);

        List<AppointmentResponse> appointments = dashboardService.getScheduledAppointments(
                userId, status, from, to, page, size);

        log.info("✅ Returning {} appointments", appointments.size());

        return ResponseEntity.ok(appointments);
    }

    // ===================================
    // Convenience Endpoints
    // ===================================

    /**
     * Get upcoming appointments (next 7 days)
     *
     * Convenience endpoint for getting appointments in the near future.
     *
     * @param authentication JWT authentication
     * @return List of upcoming appointments
     */
    @GetMapping("/appointments/upcoming")
    public ResponseEntity<List<AppointmentResponse>> getUpcomingAppointments(
            Authentication authentication) {

        String userId = authentication.getName();

        log.info("📅 Doctor {} requesting upcoming appointments", userId);

        List<AppointmentResponse> appointments =
                dashboardService.getUpcomingAppointments(userId);

        return ResponseEntity.ok(appointments);
    }

    /**
     * Get today's appointments
     *
     * Quick access to today's schedule.
     *
     * @param authentication JWT authentication
     * @return List of today's appointments
     */
    @GetMapping("/appointments/today")
    public ResponseEntity<List<AppointmentResponse>> getTodayAppointments(
            Authentication authentication) {

        String userId = authentication.getName();

        log.info("📅 Doctor {} requesting today's appointments", userId);

        List<AppointmentResponse> appointments =
                dashboardService.getTodayAppointments(userId);

        return ResponseEntity.ok(appointments);
    }

    /**
     * Get patient count by status
     *
     * Returns counts for:
     * - active: Active patients
     * - inactive: Inactive patients
     * - total: All patients
     *
     * @param authentication JWT authentication
     * @return Map of status -> count
     */
    @GetMapping("/patients/count")
    public ResponseEntity<Map<String, Long>> getPatientCount(
            Authentication authentication) {

        String userId = authentication.getName();

        log.info("🔢 Doctor {} requesting patient count", userId);

        Map<String, Long> count = dashboardService.getPatientCountByStatus(userId);

        return ResponseEntity.ok(count);
    }

    /**
     * Get appointment count by status
     *
     * Returns counts for:
     * - scheduled, confirmed, completed, cancelled, no_show, total
     *
     * @param authentication JWT authentication
     * @return Map of status -> count
     */
    @GetMapping("/appointments/count")
    public ResponseEntity<Map<String, Long>> getAppointmentCount(
            Authentication authentication) {

        String userId = authentication.getName();

        log.info("🔢 Doctor {} requesting appointment count", userId);

        Map<String, Long> count = dashboardService.getAppointmentCountByStatus(userId);

        return ResponseEntity.ok(count);
    }
}