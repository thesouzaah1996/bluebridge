package com.blue.bridge.patient.service;

import com.blue.bridge.enums.BloodGroup;
import com.blue.bridge.enums.Genotype;
import com.blue.bridge.exceptions.NotFoundException;
import com.blue.bridge.patient.dto.PatientDTO;
import com.blue.bridge.patient.entity.Patient;
import com.blue.bridge.patient.repo.PatientRepo;
import com.blue.bridge.res.Response;
import com.blue.bridge.users.entity.User;
import com.blue.bridge.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImp implements PatientService {

    private final PatientRepo patientRepo;
    private final UserService userService;
    private final ModelMapper modelMapper;


    @Override
    public Response<PatientDTO> getPatientProfile() {

        User user = userService.getCurrentUser();

        Patient patient = patientRepo.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Patient not found."));

        return Response.<PatientDTO>builder()
                .statusCode(200)
                .message("Patient profile retrived successfully.")
                .data(modelMapper.map(patient, PatientDTO.class))
                .build();
    }

    @Override
    public Response<?> updatePatientProfile(PatientDTO patientDTO) {

        User currentUser = userService.getCurrentUser();

        Patient patient = patientRepo.findByUser(currentUser)
                .orElseThrow(() -> new NotFoundException("Patient profile not found."));

        if (StringUtils.hasText(patientDTO.getFirstName())) {
            patient.setFirstName(patientDTO.getFirstName());
        }

        if (StringUtils.hasText(patientDTO.getLastName())) {
            patient.setLastName(patientDTO.getLastName());
        }

        if (StringUtils.hasText(patientDTO.getPhone())) {
            patient.setPhone(patientDTO.getPhone());
        }

        Optional.ofNullable(patientDTO.getDateOfBirth()).ifPresent(patient::setDateOfBirth);

        if (StringUtils.hasText(patientDTO.getKnownAllergies())) {
            patient.setKnownAllergies(patient.getKnownAllergies());
        }

        Optional.ofNullable(patientDTO.getBloodGroup()).ifPresent(patient::setBloodGroup);
        Optional.ofNullable(patientDTO.getGenotype()).ifPresent(patient::setGenotype);

        patientRepo.save(patient);

        return Response.builder()
                .statusCode(200)
                .message("Patient profile updated successfully.")
                .build();
    }

    @Override
    public Response<PatientDTO> getPatientById(Long patientId) {

        Patient patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + patientId));

        PatientDTO patientDTO = modelMapper.map(patient, PatientDTO.class);

        return Response.<PatientDTO>builder()
                .statusCode(200)
                .message("Patient retrieved successfully.")
                .data(patientDTO)
                .build();
    }

    @Override
    public Response<List<BloodGroup>> getAllBloodGroupEnums() {

        List<BloodGroup> bloodGroups = Arrays.asList(BloodGroup.values());

        return Response.<List<BloodGroup>>builder()
                .statusCode(200)
                .message("BloodGroups retrived successfully.")
                .data(bloodGroups)
                .build();
    }

    @Override
    public Response<List<Genotype>> getAllGenoTypeEnums() {

        List<Genotype> genotypes = Arrays.asList(Genotype.values());

        return Response.<List<Genotype>>builder()
                .statusCode(200)
                .message("Genotype retrieved successfully")
                .data(genotypes)
                .build();
    }
}
