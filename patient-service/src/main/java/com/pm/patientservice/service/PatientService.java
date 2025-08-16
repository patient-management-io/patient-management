package com.pm.patientservice.service;

import com.pm.patientservice.dto.PagedPatientResponseDto;
import com.pm.patientservice.dto.PatientRequestDto;
import com.pm.patientservice.dto.PatientResponseDto;
import com.pm.patientservice.exceptions.EmailAlreadyExistsException;
import com.pm.patientservice.exceptions.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.kafka.KafkaProducer;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingService;
    public final KafkaProducer eventProducer;

    @Cacheable(
            value = "patients",
            key = "#page + '-' + #size + '-' + #sort + '-' + #sortField",
            condition = "#searchValue == ''"
    )
    public PagedPatientResponseDto getPatients(
            int page,
            int size,
            String sort,
            String sortField,
            String searchValue
    ) {
        Pageable pageable = PageRequest.of(page - 1, size,
                sort.equalsIgnoreCase("desc")
                        ? Sort.by(sortField).descending()
                        : Sort.by(sortField).ascending()
        );

        Page<Patient> patients;

        if (searchValue == null || searchValue.isBlank()) {
            patients = patientRepository.findAll(pageable);
        } else {
            patients = patientRepository.findByNameContainingIgnoreCase(searchValue, pageable);
        }

        List<PatientResponseDto> patientDtos = patients.getContent().stream().map(PatientMapper::toDto).toList();

        return PagedPatientResponseDto.builder()
                .patients(patientDtos)
                .size(patients.getSize())
                .page(patients.getNumber() + 1)
                .totalPages(patients.getTotalPages())
                .totalElements(patients.getNumberOfElements())
                .build();
    }

    public PatientResponseDto createPatient(PatientRequestDto patientRequestDto) {
        if (patientRepository.existsByEmail(patientRequestDto.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "A patient with email `%s` already exists".formatted(patientRequestDto.getEmail())
            );
        }

        Patient patient = patientRepository.save(PatientMapper.toModel(patientRequestDto));
        billingService.createBillingAccount(patient.getId().toString(), patient.getName(), patient.getEmail());
        eventProducer.sendEvent(patient);

        return PatientMapper.toDto(patient);
    }

    public PatientResponseDto updatePatient(UUID id, PatientRequestDto patientRequestDto) {
        Patient patient = patientRepository.findById(id).orElseThrow(
                () -> new PatientNotFoundException("Patient with id `%s` not found".formatted(id))
        );

        if (patientRepository.existsByEmailAndIdNot(patientRequestDto.getEmail(), id)) {
            throw new EmailAlreadyExistsException(
                    "A patient with email `%s` already exists".formatted(patientRequestDto.getEmail())
            );
        }

        patient.setName(patientRequestDto.getName());
        patient.setAddress(patientRequestDto.getAddress());
        patient.setEmail(patientRequestDto.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDto.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);

        return PatientMapper.toDto(updatedPatient);
    }

    public void deletePatient(UUID id) {
        patientRepository.deleteById(id);
    }
}
