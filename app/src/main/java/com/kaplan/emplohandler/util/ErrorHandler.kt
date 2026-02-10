package com.kaplan.emplohandler.util

import android.database.sqlite.SQLiteException
import java.io.IOException

/**
 * Centralized error handling for consistent user-friendly messages
 */
object ErrorHandler {

    fun getErrorMessage(exception: Throwable, actionType: String = "perform action"): String {
        return when (exception) {
            is IllegalArgumentException -> {
                "Invalid data: ${exception.message ?: "Please check all required fields and try again."}"
            }
            is SQLiteException -> {
                when {
                    exception.message?.contains("UNIQUE constraint failed", ignoreCase = true) == true ->
                        "This record already exists in the database."
                    exception.message?.contains("FOREIGN KEY constraint failed", ignoreCase = true) == true ->
                        "Cannot perform this action due to data dependencies."
                    else -> "Database error occurred. Please try again."
                }
            }
            is IOException -> {
                "Failed to access database. Please check your storage and try again."
            }
            is NullPointerException -> {
                "Required data is missing. Please try again."
            }
            is NumberFormatException -> {
                "Invalid number format. Please check salary and numeric fields."
            }
            else -> {
                "Failed to $actionType. Please try again or contact support if the problem persists."
            }
        }
    }

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

        if (firstName.isBlank()) {
            errors.add("First name is required")
        } else if (firstName.length > 50) {
            errors.add("First name must be less than 50 characters")
        }

        if (lastName.isBlank()) {
            errors.add("Last name is required")
        } else if (lastName.length > 50) {
            errors.add("Last name must be less than 50 characters")
        }

        if (email.isBlank()) {
            errors.add("Email is required")
        } else if (!isValidEmail(email)) {
            errors.add("Invalid email format")
        }

        if (phoneNumber.isBlank()) {
            errors.add("Phone number is required")
        } else if (!isValidPhoneNumber(phoneNumber)) {
            errors.add("Phone number must be at least 10 digits")
        }

        if (address.isBlank()) {
            errors.add("Address is required")
        } else if (address.length > 200) {
            errors.add("Address must be less than 200 characters")
        }

        if (designation.isBlank()) {
            errors.add("Designation is required")
        } else if (designation.length > 100) {
            errors.add("Designation must be less than 100 characters")
        }

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

        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
        return email.matches(emailRegex.toRegex())
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        return phoneNumber.length >= 10 && phoneNumber.all { it.isDigit() || it == '+' || it == '-' || it == ' ' }
    }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Failure(val errors: List<String>) : ValidationResult()
}
