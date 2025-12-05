package com.hrms.hrm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeRequestDto {

    private String employeeId;
    private String firstName;
    private String lastName;
    private String designation;
    private String email;
    private String phone;
    private Double salary;
    private LocalDate joiningDate;
    private LocalDate dateOfBirth;
}
