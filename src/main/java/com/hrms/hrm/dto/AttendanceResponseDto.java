package com.hrms.hrm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceResponseDto {

    private UUID id;
    private UUID employeeId;
    private LocalDate date;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private String attendanceStatus;
    private Double hoursWorked;
}
