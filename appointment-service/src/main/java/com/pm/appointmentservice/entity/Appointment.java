package com.pm.appointmentservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull(message = "patientId is required")
    @Column(nullable = false)
    private UUID patientId;

    @NotNull(message = "startTime is required")
    @Column(nullable = false)
    @Future(message = "startTime must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "endTime is required")
    @Column(nullable = false)
    @Future(message = "endTime must be in the future")
    private LocalDateTime endTime;

    @NotNull(message = "reason is required")
    @Size(max = 255, message = "reason must be 255 characters or less")
    @Column(nullable = false, length = 255)
    private String reason;

    @Version
    @Column(nullable = false)
    private long version;
}
