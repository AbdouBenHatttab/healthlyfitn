package com.healthapp.doctor.repository;

import com.healthapp.doctor.entity.DoctorPatient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorPatientRepository extends MongoRepository<DoctorPatient, String> {

    Optional<DoctorPatient> findByDoctorIdAndPatientUserId(String doctorId, String patientUserId);

    boolean existsByDoctorIdAndPatientUserId(String doctorId, String patientUserId);

    List<DoctorPatient> findByDoctorId(String doctorId);

    Page<DoctorPatient> findByDoctorId(String doctorId, Pageable pageable);

    List<DoctorPatient> findByDoctorIdAndStatus(String doctorId, String status);

    Page<DoctorPatient> findByDoctorIdAndStatus(String doctorId, String status, Pageable pageable);

    long countByDoctorId(String doctorId);

    long countByDoctorIdAndStatus(String doctorId, String status);

    List<DoctorPatient> findByPatientUserId(String patientUserId);
}