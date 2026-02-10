package com.kaplan.emplohandler.util

import android.database.sqlite.SQLiteException
import java.io.IOException

/**
 * ErrorHandler - Centralized error message mapping and utilities
 *
 * Responsibilities:
 * - Convert exceptions to user-friendly messages
 * - Provide consistent error messaging across app
 * - Handle validation result sealed class
 * - Email and phone validation utilities
 *
 * Usage:
 *   val message = ErrorHandler.getErrorMessage(exception, "add")
 *   val result = ErrorHandler.validateEmployeeData(...)
 */
object ErrorHandler {

    /**
     * Map exception to user-friendly error message
     * @param exception The exception that occurred
     * @param actionType Action being performed (add, update, delete, load)
     * @return User-friendly error message
     */
    fun getErrorMessage(exception: Throwable, actionType: String = "perform action"): String {
        return when (exception) {
            // Handle validation errors - show specific details
            is IllegalArgumentException -> {
                "Invalid data: ${exception.message ?: "Please check all required fields and try again."}"
            }
            // Handle database constraint violations
            is SQLiteException -> {
                when {
                    exception.message?.contains("UNIQUE constraint failed", ignoreCase = true) == true ->
                        "This record already exists in the database."
                    exception.message?.contains("FOREIGN KEY constraint failed", ignoreCase = true) == true ->
                        "Cannot perform this action due to data dependencies."
                    else -> "Database error occurred. Please try again."
                }
            }
            // Handle file system errors
            is IOException -> {
                "Failed to access database. Please check your storage and try again."
            }
            // Handle null pointer errors
            is NullPointerException -> {
                "Required data is missing. Please try again."
            }
            // Handle number format errors
            is NumberFormatException -> {
                "Invalid number format. Please check salary and numeric fields."
            }
            // Default fallback for unknown exceptions
            else -> {
                "Failed to $actionType. Please try again or contact support if the problem persists."
            }
        }
    }

    /**
     * Get success/failure message for specific operation
     * @param success Whether operation was successful
     * @param operationType Type of operation (add, update, delete, load)
     * @return Appropriate success or failure message
     */
    fun getOperationMessage(success: Boolean, operationType: String): String {
        return if (success) {
            when (operationType) {
                "add" -> "Employee added successfully!"
                "update" -> "Employee updated successfully!"
                "delete" -> "Employee deleted successfully!"
                "load" -> "Employee loaded successfully!"
                else -> "Operation completed successfully!"
            }
        } else {
            when (operationType) {
                "add" -> "Failed to add employee. Please try again."
                "update" -> "Failed to update employee. Please try again."
                "delete" -> "Failed to delete employee. Please try again."
                "load" -> "Failed to load employee. Please try again."
                else -> "Operation failed. Please try again."
            }
        }
    }

    /**
     * Comprehensive employee data validation
     * Validates all fields for presence, format, and length
     *
     * @return ValidationResult.Success if all valid, ValidationResult.Failure with error list otherwise
     */
    fun validateEmployeeData(
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        address: String,
        designation: String,
        salary: String
    ): ValidationResult {
        val errors = mutableListOf<String>()

        // First Name: presence and length check
        if (firstName.isBlank()) {
            errors.add("First name is required")
        } else if (firstName.length > 50) {
            errors.add("First name must be less than 50 characters")
        }

        // Last Name: presence and length check
        if (lastName.isBlank()) {
            errors.add("Last name is required")
        } else if (lastName.length > 50) {
            errors.add("Last name must be less than 50 characters")
        }

        // Email: presence and format check
        if (email.isBlank()) {
            errors.add("Email is required")
        } else if (!isValidEmail(email)) {
            errors.add("Invalid email format")
        }

        // Phone: presence and format check
        if (phoneNumber.isBlank()) {
            errors.add("Phone number is required")
        } else if (!isValidPhoneNumber(phoneNumber)) {
            errors.add("Phone number must be at least 10 digits")
        }

        // Address: presence and length check
        if (address.isBlank()) {
            errors.add("Address is required")
        } else if (address.length > 200) {
            errors.add("Address must be less than 200 characters")
        }

        // Designation: presence and length check
        if (designation.isBlank()) {
            errors.add("Designation is required")
        } else if (designation.length > 100) {
            errors.add("Designation must be less than 100 characters")
        }

        // Salary: presence and value check
        if (salary.isBlank()) {
            errors.add("Salary is required")
        } else {
            try {
                val salaryValue = salary.toDouble()
                if (salaryValue < 0) {
                    errors.add("Salary must be a positive number")
                }
            } catch (e: NumberFormatException) {
                errors.add("Salary must be a valid number")
            }
        }

        // Return result based on validation
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }

    /**
     * Validate email format using regex
     * @param email Email address to validate
     * @return True if email is valid format, false otherwise
     */
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
        return email.matches(emailRegex.toRegex())
    }

    /**
     * Validate phone number format
     * Accepts 10+ digits, can contain +, -, space
     * @param phoneNumber Phone number to validate
     * @return True if valid format, false otherwise
     */
    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        return phoneNumber.length >= 10 && phoneNumber.all { it.isDigit() || it == '+' || it == '-' || it == ' ' }
    }
}

/**
 * ValidationResult - Type-safe result representation
 * Sealed class for exhaustive when expressions
 *
 * Success: All validations passed
 * Failure: Contains list of validation error messages
 */
sealed class ValidationResult {
    object Success : ValidationResult()
    data class Failure(val errors: List<String>) : ValidationResult()
}
