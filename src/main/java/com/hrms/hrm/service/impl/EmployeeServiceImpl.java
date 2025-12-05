package com.hrms.hrm.service.impl;

import com.hrms.hrm.dto.EmployeeRequestDto;
import com.hrms.hrm.dto.EmployeeResponseDto;
import com.hrms.hrm.error.EmployeeAlreadyExistException;
import com.hrms.hrm.error.ResourceNotFoundException;
import com.hrms.hrm.model.Employee;
import com.hrms.hrm.repository.EmployeeRepository;
import com.hrms.hrm.service.EmployeeService;
import com.hrms.hrm.util.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    @Override
    public List<EmployeeResponseDto> getAllEmployees() {
       return employeeRepository.findAll().stream()
               .map(DtoMapper::toDto).toList();
    }

    @Override
    public EmployeeResponseDto createEmployee(EmployeeRequestDto request) {
        Employee existingEmployee = employeeRepository.findByEmail(request.getEmail());

        if(existingEmployee != null) throw new EmployeeAlreadyExistException("Employee is already exist with email : " +request.getEmail());
        Employee employee = employeeRepository.save(DtoMapper.toEntity(request));
        return DtoMapper.toDto(employee);

    }

    @Override
    public EmployeeResponseDto updateEmployee(EmployeeRequestDto request, UUID id) {

        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee is not found with id: " +id));
        if(request.getFirstName() != null)
            employee.setFirstName(request.getFirstName());
        if(request.getLastName() != null)
            employee.setLastName(request.getLastName());
        if(request.getEmail() != null)
            employee.setEmail(request.getEmail());
        if(request.getPhone() != null)
            employee.setPhone(request.getPhone());
        if(request.getDesignation() != null)
            employee.setDesignation(request.getDesignation());
        if(request.getSalary() != null)
            employee.setSalary(request.getSalary());
        if(request.getJoiningDate() != null)
            employee.setJoiningDate(request.getJoiningDate());
        if(request.getDateOfBirth() != null)
            employee.setDateOfBirth(request.getDateOfBirth());

        employee = employeeRepository.save(employee);

        return DtoMapper.toDto(employee);
    }

    @Override
    public EmployeeResponseDto getEmployeeById(UUID id) {
        Employee emp = employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("The employee not found with id : " +id));
        return DtoMapper.toDto(emp);
    }

    @Override
    public Void deleteEmployeeById(UUID id) {

       getEmployeeById(id);
       employeeRepository.deleteById(id);
       return null;
    }
}
