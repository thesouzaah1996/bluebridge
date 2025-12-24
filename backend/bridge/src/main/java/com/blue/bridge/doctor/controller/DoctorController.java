package com.blue.bridge.doctor.controller;

import com.blue.bridge.doctor.dto.DoctorDTO;
import com.blue.bridge.doctor.service.DoctorService;
import com.blue.bridge.enums.Specialization;
import com.blue.bridge.res.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<Response<DoctorDTO>> getDoctorProfile() {
        return ResponseEntity.ok(doctorService.getDoctorProfile());
    }

    @PutMapping("/me")
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<Response<?>> updateDoctorProfile(@RequestBody DoctorDTO doctorDTO) {
        return ResponseEntity.ok(doctorService.updateDoctorProfile(doctorDTO));
    }

    @GetMapping("/{doctorId}")
    public ResponseEntity<Response<?>> getDoctorId(@PathVariable Long doctorId) {
        return ResponseEntity.ok(doctorService.getDoctorById(doctorId));
    }

    @GetMapping("/filter")
    public ResponseEntity<Response<List<DoctorDTO>>> searchDoctorsBySpecialization(
            @RequestParam(required = true)Specialization specialization) {

        return ResponseEntity.ok(doctorService.searchDoctorsBySpecialization(specialization));
    }

    @GetMapping("/specializations")
    public ResponseEntity<Response<List<Specialization>>> getAllSpecializationsEnums() {
        return ResponseEntity.ok(doctorService.getAllSpecializationEnums());
    }
}









