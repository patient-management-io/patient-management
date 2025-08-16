package com.pm.patientservice.dto;

import com.pm.patientservice.dto.validators.CreatePatientValidationGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PatientRequestDto {
    @NotBlank(message = "Name should not be blank")
    @Size(max = 100, message = "Name should be less than 100 characters")
    private String name;

    @NotBlank(message = "Email should not be blank")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Address should not be blank")
    private String address;

    @NotBlank(message = "Date of birth should not be blank")
    private String dateOfBirth;

    @NotBlank(groups = CreatePatientValidationGroup.class, message = "Registered date should not be blank")
    private String registeredDate;
}
