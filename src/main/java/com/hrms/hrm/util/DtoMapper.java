package com.hrms.hrm.util;

import com.hrms.hrm.dto.*;
import com.hrms.hrm.model.Attendance;
import com.hrms.hrm.model.Department;
import com.hrms.hrm.model.Employee;
import jakarta.persistence.Enumerated;

public class DtoMapper {

    public static EmployeeResponseDto toDto(Employee employee) {
        return EmployeeResponseDto.builder()
                .id(employee.getId())
                .employeeId(employee.getEmployeeId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .designation(employee.getDesignation())
                .joiningDate(employee.getJoiningDate())
                .dateOfBirth(employee.getDateOfBirth())
                .build();
    }


    public static Employee toEntity(EmployeeRequestDto request) {
        return Employee.builder()
                .employeeId(request.getEmployeeId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .salary(request.getSalary())
                .designation(request.getDesignation())
                .joiningDate(request.getJoiningDate())
                .dateOfBirth(request.getDateOfBirth())
                .build();
    }

    public static DepartmentResponseDto toDto(Department department) {
        return DepartmentResponseDto.builder()
                .id(department.getId())
                .name(department.getName())
                .build();
    }

    public static Department toEntity(DepartmentRequestDto request) {
        return Department.builder()
                .name(request.getName())
                .build();
    }

    public static AttendanceResponseDto toDto(Attendance attendance) {
        return AttendanceResponseDto.builder()
                .id(attendance.getId())
                .employeeId(attendance.getEmployee().getId())
                .checkInTime(attendance.getCheckInTime())
                .checkOutTime(attendance.getCheckOutTime())
                .attendanceStatus(String.valueOf(attendance.getStatus()))
                .hoursWorked(attendance.getHoursWorked())
                .date(attendance.getDate())
                .build();
    }

//    public static Attendance toEntity(AttendanceRequestDto requestDto) {
//        return Attendance.builder()
//                .employee(requestDto.getEmployeeId())
//                .build();
//    }
}
