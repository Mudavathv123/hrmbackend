package com.hrms.hrm.service.impl;

import com.hrms.hrm.dto.AttendanceResponseDto;
import com.hrms.hrm.error.ResourceNotFoundException;
import com.hrms.hrm.model.Attendance;
import com.hrms.hrm.repository.AttendanceRepository;
import com.hrms.hrm.repository.EmployeeRepository;
import com.hrms.hrm.util.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AttendanceServiceImpl implements com.hrms.hrm.service.AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public AttendanceResponseDto checkIn(UUID employeeId) {
        LocalDate today = LocalDate.now();

        attendanceRepository.findByEmployeeIdAndDate(employeeId, today)
                .ifPresent(ex -> {
                    throw new RuntimeException("Employee already checked today");
                });

        Attendance attendance = Attendance.builder()
                .employee(employeeRepository.findById(employeeId).orElseThrow(() ->  new ResourceNotFoundException("Employee not found!!")))
                .checkInTime(LocalTime.now())
                .date(today)
                .build();

        attendance.setStatus(Attendance.AttendanceStatus.PRESENT);
        attendance.setHoursWorked(0.0);

        attendance = attendanceRepository.save(attendance);
        return DtoMapper.toDto(attendance);
    }

    @Override
    public AttendanceResponseDto checkOut(UUID employeeId) {
        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository
                .findByEmployeeIdAndDate(employeeId, today)
                .orElseThrow(() -> new RuntimeException("Employee has not checked in today"));

        if(attendance.getCheckOutTime() != null) {
            throw new RuntimeException("Employee already check out today");
        }

        attendance.setCheckOutTime(LocalTime.now());

        if(attendance.getCheckOutTime() != null) {
            long minutes = Duration.between(attendance.getCheckOutTime(), attendance.getCheckOutTime()).toMinutes();
            double hours = minutes / 60.0;
            attendance.setHoursWorked(hours);

            if(hours < 4.0) {
                attendance.setStatus(Attendance.AttendanceStatus.HALF_DAY);
            }else {
                attendance.setStatus(Attendance.AttendanceStatus.PRESENT);
            }
        }
        attendance = attendanceRepository.save(attendance);
        return DtoMapper.toDto(attendance);
    }

    @Override
    public AttendanceResponseDto getTodayAttendance(UUID employeeId) {
        return attendanceRepository
                .findByEmployeeIdAndDate(employeeId, LocalDate.now())
                .map(DtoMapper::toDto).orElse(null);
    }

    @Override
    public List<AttendanceResponseDto> getAttendanceHistory(UUID employeeId) {
        return attendanceRepository
                .findByEmployeeId(employeeId)
                .stream()
                .map(DtoMapper::toDto)
                .toList();
    }

    @Override
    public List<AttendanceResponseDto> getMonthlyAttendance(UUID employeeId, int year, int month) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);
        return attendanceRepository
                .findByEmployeeIdAndDateBetween(employeeId, start, end)
                .stream()
                .map(DtoMapper::toDto)
                .toList();
    }
}
