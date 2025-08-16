package com.pm.patientservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedPatientResponseDto {
    private List<PatientResponseDto> patients;
    private int page;
    private int size;
    private int totalPages;
    private int totalElements;
}
