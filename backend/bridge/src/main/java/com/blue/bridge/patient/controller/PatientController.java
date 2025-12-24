package com.blue.bridge.patient.controller;

import com.blue.bridge.enums.BloodGroup;
import com.blue.bridge.enums.Genotype;
import com.blue.bridge.patient.dto.PatientDTO;
import com.blue.bridge.patient.service.PatientService;
import com.blue.bridge.res.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<Response<PatientDTO>> getPatientProfile() {
        return ResponseEntity.ok(patientService.getPatientProfile());
    }

    @PutMapping("/me")
    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<Response<?>> updatePatientProfile(@RequestBody PatientDTO patient) {
        return ResponseEntity.ok(patientService.updatePatientProfile(patient));
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<Response<PatientDTO>> getPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientService.getPatientById(patientId));
    }

    @GetMapping("/bloodgroup")
    public ResponseEntity<Response<List<BloodGroup>>> getAllBloodGroupEnum() {
        return ResponseEntity.ok(patientService.getAllBloodGroupEnums());
    }

    @GetMapping("/genotype")
    public ResponseEntity<Response<List<Genotype>>> getAllGenotypeEnum() {
        return ResponseEntity.ok(patientService.getAllGenoTypeEnums());
    }
}