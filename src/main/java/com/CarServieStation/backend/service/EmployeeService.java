package com.CarServieStation.backend.service;

import com.CarServieStation.backend.dto.EmployeePatchDto;
import com.CarServieStation.backend.entity.Employee;
import com.CarServieStation.backend.exception.NotFoundException;
import com.CarServieStation.backend.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;


    @Transactional
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Optional<Employee> getEmployee(Integer id) {
        return employeeRepository.findById(id);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> getAllUnassignedEmployees() {
        return employeeRepository.findAllUnassignedEmployees();
    }


    @Transactional
    public Employee updateEmployee(Integer id, EmployeePatchDto employeeDetails) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found with id: " + id));
        employee.setFirstname(employeeDetails.getFirstname());
        employee.setLastname(employeeDetails.getLastname());
        employee.setBirthDate(employeeDetails.getBirthDate());
        employee.setEmail(employeeDetails.getEmail());
        employee.setPhoneNumber(employeeDetails.getPhoneNumber());
        employee.setSalary(employeeDetails.getSalary());
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found with id: " + id));
        if (employee.getStation() != null) {
            employee.getStation().getEmployees().remove(employee);
            employee.setStation(null);
        }
        employeeRepository.delete(employee);
    }

    public List<Employee> searchEmployeesByFirstNameOrLastName(String name) {
        return employeeRepository.findByFirstnameOrLastname(name, name);
    }
}