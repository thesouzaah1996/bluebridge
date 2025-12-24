package com.blue.bridge.doctor.dto;

import com.blue.bridge.enums.Specialization;
import com.blue.bridge.users.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DoctorDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private Specialization specialization;

    private String licenseNumber;

    private UserDTO user;
}
