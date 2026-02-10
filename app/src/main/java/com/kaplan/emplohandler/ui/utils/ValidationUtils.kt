package com.kaplan.emplohandler.ui.utils

import android.util.Patterns

fun validateEmployeeInput(
    firstName: String,
    lastName: String,
    email: String,
    phoneNumber: String,
    address: String,
    designation: String,
    salary: String
): Map<String, String> {
    val errors = mutableMapOf<String, String>()

    // First Name validation
    when {
        firstName.isBlank() -> errors["firstName"] = "First name is required"
        firstName.length > 50 -> errors["firstName"] = "First name must be less than 50 characters"
        !firstName.all { it.isLetter() || it.isWhitespace() } -> errors["firstName"] = "First name can only contain letters"
    }

    // Last Name validation
    when {
        lastName.isBlank() -> errors["lastName"] = "Last name is required"
        lastName.length > 50 -> errors["lastName"] = "Last name must be less than 50 characters"
        !lastName.all { it.isLetter() || it.isWhitespace() } -> errors["lastName"] = "Last name can only contain letters"
    }

    // Email validation
    when {
        email.isBlank() -> errors["email"] = "Email is required"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> errors["email"] = "Invalid email format"
        email.length > 100 -> errors["email"] = "Email must be less than 100 characters"
    }

    // Phone Number validation
    when {
        phoneNumber.isBlank() -> errors["phoneNumber"] = "Phone number is required"
        phoneNumber.length < 10 -> errors["phoneNumber"] = "Phone number must be at least 10 digits"
        phoneNumber.length > 20 -> errors["phoneNumber"] = "Phone number must be less than 20 characters"
        !phoneNumber.all { it.isDigit() || it == '+' || it == '-' || it == ' ' } ->
            errors["phoneNumber"] = "Phone number contains invalid characters"
    }

    // Address validation
    when {
        address.isBlank() -> errors["address"] = "Address is required"
        address.length > 200 -> errors["address"] = "Address must be less than 200 characters"
    }

    // Designation validation
    when {
        designation.isBlank() -> errors["designation"] = "Designation is required"
        designation.length > 100 -> errors["designation"] = "Designation must be less than 100 characters"
    }

    // Salary validation
    when {
        salary.isBlank() -> errors["salary"] = "Salary is required"
        salary.length > 15 -> errors["salary"] = "Salary value is too large"
        else -> {
            try {
                val salaryValue = salary.toDouble()
                when {
                    salaryValue < 0 -> errors["salary"] = "Salary must be a positive number"
                    salaryValue > 9999999.99 -> errors["salary"] = "Salary cannot exceed 9,999,999.99"
                }
            } catch (e: NumberFormatException) {
                errors["salary"] = "Salary must be a valid number (e.g., 50000 or 50000.50)"
            }
        }
    }

    return errors
}
