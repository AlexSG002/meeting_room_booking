package com.alejandro.meetingbooking.repository;

import com.alejandro.meetingbooking.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
