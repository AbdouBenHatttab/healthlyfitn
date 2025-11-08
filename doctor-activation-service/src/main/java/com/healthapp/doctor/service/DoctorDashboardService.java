// ✅ UPDATE existing DoctorDashboardService.java
// Replace the entire getPatientList method and add new dependencies

package com.healthapp.doctor.service;

import com.healthapp.doctor.client.UserServiceClient; // ✅ ADD THIS
import com.healthapp.doctor.entity.DoctorPatient; // ✅ ADD THIS
import com.healthapp.doctor.repository.DoctorPatientRepository; // ✅ ADD THIS
import com.healthapp.shared.dto.UserDto; // ✅ ADD THIS

// ... other imports ...

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorDashboardService {

    private final DoctorRepository doctorRepository;
    private final DoctorPatientRepository doctorPatientRepository; // ✅ ADD THIS
    private final AppointmentRepository appointmentRepository;
    private final UserServiceClient userServiceClient; // ✅ ADD THIS

    // ===================================
    // ✅ REPLACE getPatientList method
    // ===================================

    public List<PatientSummaryResponse> getPatientList(
            String userId, String status, String search, int page, int size) {

        log.info("🏥 Fetching patients - Status: {}, Search: {}, Page: {}/{}",
                status, search, page, size);

        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "assignedAt"));

        Page<DoctorPatient> relationshipsPage;

        // ✅ Get relationships from DoctorPatient
        if ("ALL".equalsIgnoreCase(status)) {
            relationshipsPage = doctorPatientRepository.findByDoctorId(
                    doctor.getId(), pageable);
        } else {
            relationshipsPage = doctorPatientRepository.findByDoctorIdAndStatus(
                    doctor.getId(), status, pageable);
        }

        // ✅ Extract patient user IDs
        List<String> patientUserIds = relationshipsPage.getContent().stream()
                .map(DoctorPatient::getPatientUserId)
                .collect(Collectors.toList());

        if (patientUserIds.isEmpty()) {
            log.info("✅ No patients found");
            return Collections.emptyList();
        }

        // ✅ Fetch user data from user-service via Feign
        List<UserDto> users = userServiceClient.getUsersByIds(patientUserIds);

        // Create a map for quick lookup
        Map<String, UserDto> userMap = users.stream()
                .collect(Collectors.toMap(UserDto::getId, u -> u));

        // ✅ Combine relationship data with user data
        List<PatientSummaryResponse> patients = relationshipsPage.getContent().stream()
                .map(relationship -> {
                    UserDto user = userMap.get(relationship.getPatientUserId());
                    if (user == null) {
                        log.warn("⚠️ User not found for patientUserId: {}",
                                relationship.getPatientUserId());
                        return null;
                    }
                    return mapToPatientSummaryResponse(relationship, user);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // ✅ Apply search filter if needed
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            patients = patients.stream()
                    .filter(p ->
                            p.getFirstName().toLowerCase().contains(searchLower) ||
                                    p.getLastName().toLowerCase().contains(searchLower) ||
                                    p.getEmail().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
        }

        log.info("✅ Found {} patients", patients.size());
        return patients;
    }

    // ===================================
    // ✅ ADD NEW HELPER METHOD
    // ===================================

    private PatientSummaryResponse mapToPatientSummaryResponse(
            DoctorPatient relationship, UserDto user) {

        // Find next appointment
        List<Appointment> upcomingAppts = appointmentRepository
                .findByDoctorIdAndPatientUserIdOrderByAppointmentDateDesc(
                        relationship.getDoctorId(), relationship.getPatientUserId());

        Appointment nextAppt = upcomingAppts.stream()
                .filter(a -> a.isScheduled() &&
                        a.getAppointmentDate().isAfter(LocalDateTime.now()))
                .findFirst()
                .orElse(null);

        return PatientSummaryResponse.builder()
                .patientId(relationship.getId()) // DoctorPatient relationship ID
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .birthDate(user.getBirthDate())
                .age(calculateAge(user.getBirthDate()))
                .gender(user.getGender())
                .patientStatus(relationship.getStatus())
                .firstConsultationDate(relationship.getFirstConsultationDate())
                .lastConsultationDate(relationship.getLastConsultationDate())
                .totalConsultations(relationship.getTotalConsultations())
                .nextAppointmentDate(nextAppt != null ? nextAppt.getAppointmentDate() : null)
                .nextAppointmentType(nextAppt != null ? nextAppt.getAppointmentType() : null)
                .createdAt(relationship.getAssignedAt())
                .build();
    }

    private Integer calculateAge(LocalDate birthDate) {
        if (birthDate == null) return null;
        return java.time.Period.between(birthDate, LocalDate.now()).getYears();
    }

    // ... keep other methods (getDashboardStatistics, getScheduledAppointments, etc.)
}