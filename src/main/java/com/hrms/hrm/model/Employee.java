package com.hrms.hrm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "employee_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String employeeId;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String designation;

    private Double salary;

    private LocalDate joiningDate;

    private LocalDate dateOfBirth;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attendance> attendanceList;
}
