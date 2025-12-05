package com.hrms.hrm.service;

import com.hrms.hrm.dto.AttendanceResponseDto;

import java.util.List;
import java.util.UUID;

public interface AttendanceService {

    AttendanceResponseDto checkIn(UUID employeeId);

    AttendanceResponseDto checkOut(UUID employeeId);

    AttendanceResponseDto getTodayAttendance(UUID employeeId);

    List<AttendanceResponseDto> getAttendanceHistory(UUID employeeId);

    List<AttendanceResponseDto> getMonthlyAttendance(UUID employeeId, int year, int month);

}
