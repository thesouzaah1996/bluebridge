package com.blue.bridge.doctor.service;

import com.blue.bridge.doctor.dto.DoctorDTO;
import com.blue.bridge.doctor.entity.Doctor;
import com.blue.bridge.doctor.repo.DoctorRepository;
import com.blue.bridge.enums.Specialization;
import com.blue.bridge.exceptions.NotFoundException;
import com.blue.bridge.res.Response;
import com.blue.bridge.users.entity.User;
import com.blue.bridge.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.print.Doc;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorServiceImp implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    public Response<DoctorDTO> getDoctorProfile() {

        User user = userService.getCurrentUser();

        Doctor doctor = doctorRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Doctor not found."));

        return Response.<DoctorDTO>builder()
                .statusCode(200)
                .message("Doctor profile retrieved successfully.")
                .data(modelMapper.map(doctor, DoctorDTO.class))
                .build();
    }

    @Override
    public Response<?> updateDoctorProfile(DoctorDTO doctorDTO) {

        User user = userService.getCurrentUser();

        Doctor doctor = doctorRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Doctor not found."));

        if (StringUtils.hasText(doctorDTO.getFirstName())) {
            doctor.setFirstName(doctorDTO.getFirstName());
        }
        if (StringUtils.hasText(doctorDTO.getLastName())) {
            doctor.setLastName(doctorDTO.getLastName());
        }

        Optional.ofNullable(doctorDTO.getSpecialization()).ifPresent(doctor::setSpecialization);

        doctorRepository.save(doctor);
        log.info("Doctor profile updated");

        return Response.builder()
                .statusCode(200)
                .message("Doctor profile updated successfully.")
                .build();
    }

    @Override
    public Response<List<DoctorDTO>> getAllDoctors() {

        List<Doctor> doctors = doctorRepository.findAll();

        List<DoctorDTO> doctorDTOSs = doctors.stream()
                .map(doctor -> modelMapper.map(doctor, DoctorDTO.class))
                .toList();

        return Response.<List<DoctorDTO>>builder()
                .statusCode(200)
                .message("All doctors retrieved successfully")
                .data(doctorDTOSs)
                .build();
    }

    @Override
    public Response<DoctorDTO> getDoctorById(Long doctorId) {

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor not found."));

        return Response.<DoctorDTO>builder()
                .statusCode(200)
                .message("All doctors retrieved successfully")
                .data(modelMapper.map(doctor, DoctorDTO.class))
                .build();
    }

    @Override
    public Response<List<DoctorDTO>> searchDoctorsBySpecialization(Specialization specialization) {

        List<Doctor> doctors = doctorRepository.findBySpecialization(specialization);

        List<DoctorDTO> doctorDTOS = doctors.stream()
                .map(doctor -> modelMapper.map(doctor, DoctorDTO.class))
                .toList();

        String message = doctors.isEmpty() ?
                "No doctors found for specialization: " + specialization.name() :
                "Doctors retrieved successfully for specialization: " + specialization.name();


        return Response.<List<DoctorDTO>>builder()
                .statusCode(200)
                .message(message)
                .data(doctorDTOS)
                .build();
    }

    @Override
    public Response<List<Specialization>> getAllSpecializationEnums() {

        List<Specialization> specializations = Arrays.asList(Specialization.values());

        return Response.<List<Specialization>>builder()
                .statusCode(200)
                .message("Specialization retrieved successfully.")
                .data(specializations)
                .build();
    }
}
