package com.kaplan.emplohandler.data

import kotlinx.coroutines.flow.Flow

/**
 * EmployeeRepository - Data access layer for employee operations
 *
 * Responsibilities:
 * - Provide data access abstraction (DAO layer)
 * - Validate employee data before database operations
 * - Map database exceptions to meaningful error messages
 * - Enforce business rules and constraints
 */
class EmployeeRepository(private val employeeDao: EmployeeDao) {

    // StateFlow from DAO: All employees from database, ordered by first name
    val allEmployees: Flow<List<Employee>> = employeeDao.getAllEmployees()

    /**
     * Get single employee by ID
     * @param id Employee ID to retrieve
     * @return Employee if found, null otherwise
     * @throws IllegalArgumentException if ID is invalid (≤ 0)
     */
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

    /**
     * Insert new employee into database
     * @param employee Employee object to insert
     * @return Row ID of inserted employee
     * @throws IllegalArgumentException if employee data is invalid
     * @throws Exception if duplicate email or database error
     */
    suspend fun insertEmployee(employee: Employee): Long {
        validateEmployee(employee)
        return try {
            employeeDao.insertEmployee(employee)
        } catch (e: android.database.sqlite.SQLiteConstraintException) {
            // Email is unique constraint - handle duplicate
            throw Exception("Employee with this email already exists")
        } catch (e: Exception) {
            throw Exception("Failed to insert employee: ${e.message}")
        }
    }

    /**
     * Update existing employee in database
     * @param employee Employee object with updated data
     * @throws IllegalArgumentException if employee data is invalid or ID is invalid
     */
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

    /**
     * Delete employee from database
     * @param employee Employee object to delete
     * @throws IllegalArgumentException if employee ID is invalid
     */
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

    /**
     * Delete employee by ID from database
     * @param id Employee ID to delete
     * @throws IllegalArgumentException if ID is invalid (≤ 0)
     */
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

    /**
     * Validate employee data before database operations
     * Checks: field presence, length limits, value ranges
     * @param employee Employee to validate
     * @throws IllegalArgumentException if any validation fails
     */
    private fun validateEmployee(employee: Employee) {
        // Required field validation
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

        // Salary range validation
        if (employee.salary < 0) {
            throw IllegalArgumentException("Salary cannot be negative")
        }

        // Field length validation (enforce database schema limits)
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
