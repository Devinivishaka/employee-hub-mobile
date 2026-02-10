package com.kaplan.emplohandler.data

import kotlinx.coroutines.flow.Flow

class EmployeeRepository(private val employeeDao: EmployeeDao) {
    val allEmployees: Flow<List<Employee>> = employeeDao.getAllEmployees()

    suspend fun getEmployeeById(id: Int): Employee? {
        if (id <= 0) {
            throw IllegalArgumentException("Employee ID must be greater than 0")
        }
        return try {
            employeeDao.getEmployeeById(id)
        } catch (e: Exception) {
            throw Exception("Failed to retrieve employee: ${e.message}")
        }
    }

    suspend fun insertEmployee(employee: Employee): Long {
        validateEmployee(employee)
        return try {
            employeeDao.insertEmployee(employee)
        } catch (e: android.database.sqlite.SQLiteConstraintException) {
            throw Exception("Employee with this email already exists")
        } catch (e: Exception) {
            throw Exception("Failed to insert employee: ${e.message}")
        }
    }

    suspend fun updateEmployee(employee: Employee) {
        validateEmployee(employee)
        if (employee.id <= 0) {
            throw IllegalArgumentException("Invalid employee ID")
        }
        return try {
            employeeDao.updateEmployee(employee)
        } catch (e: Exception) {
            throw Exception("Failed to update employee: ${e.message}")
        }
    }

    suspend fun deleteEmployee(employee: Employee) {
        if (employee.id <= 0) {
            throw IllegalArgumentException("Invalid employee ID")
        }
        return try {
            employeeDao.deleteEmployee(employee)
        } catch (e: Exception) {
            throw Exception("Failed to delete employee: ${e.message}")
        }
    }

    suspend fun deleteEmployeeById(id: Int) {
        if (id <= 0) {
            throw IllegalArgumentException("Employee ID must be greater than 0")
        }
        return try {
            employeeDao.deleteEmployeeById(id)
        } catch (e: Exception) {
            throw Exception("Failed to delete employee: ${e.message}")
        }
    }

    private fun validateEmployee(employee: Employee) {
        // Validate all required fields
        if (employee.firstName.isBlank()) {
            throw IllegalArgumentException("First name cannot be empty")
        }
        if (employee.lastName.isBlank()) {
            throw IllegalArgumentException("Last name cannot be empty")
        }
        if (employee.email.isBlank()) {
            throw IllegalArgumentException("Email cannot be empty")
        }
        if (employee.phoneNumber.isBlank()) {
            throw IllegalArgumentException("Phone number cannot be empty")
        }
        if (employee.address.isBlank()) {
            throw IllegalArgumentException("Address cannot be empty")
        }
        if (employee.designation.isBlank()) {
            throw IllegalArgumentException("Designation cannot be empty")
        }
        if (employee.salary < 0) {
            throw IllegalArgumentException("Salary cannot be negative")
        }

        // Validate field lengths
        if (employee.firstName.length > 50) {
            throw IllegalArgumentException("First name is too long")
        }
        if (employee.lastName.length > 50) {
            throw IllegalArgumentException("Last name is too long")
        }
        if (employee.email.length > 100) {
            throw IllegalArgumentException("Email is too long")
        }
        if (employee.address.length > 200) {
            throw IllegalArgumentException("Address is too long")
        }
    }
}
