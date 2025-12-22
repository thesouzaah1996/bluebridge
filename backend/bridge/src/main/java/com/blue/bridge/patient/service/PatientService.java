package com.blue.bridge.patient.service;

import com.blue.bridge.enums.BloodGroup;
import com.blue.bridge.enums.Genotype;
import com.blue.bridge.patient.dto.PatientDTO;
import com.blue.bridge.res.Response;

import java.util.List;

public interface PatientService {

    Response<PatientDTO> getPatientProfile();

    Response<?> updatePatientProfile(PatientDTO patientDTO);

    Response<PatientDTO> getPatientById(Long patientId);

    Response<List<BloodGroup>> getAllBloodGroupEnums();

    Response<List<Genotype>> getAllGenoTypeEnums();
}
