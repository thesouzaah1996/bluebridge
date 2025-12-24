package com.blue.bridge.doctor.service;

import com.blue.bridge.doctor.dto.DoctorDTO;
import com.blue.bridge.enums.Specialization;
import com.blue.bridge.res.Response;

import java.util.List;

public interface DoctorService {

    Response<DoctorDTO> getDoctorProfile();

    Response<?> updateDoctorProfile(DoctorDTO doctorDTO);

    Response<List<DoctorDTO>> getAllDoctors();

    Response<DoctorDTO> getDoctorById(Long doctorId);

    Response<List<DoctorDTO>> searchDoctorsBySpecialization(Specialization specialization);

    Response<List<Specialization>> getAllSpecializationEnums();
}
