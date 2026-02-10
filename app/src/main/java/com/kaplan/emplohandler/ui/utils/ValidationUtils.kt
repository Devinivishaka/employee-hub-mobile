package com.kaplan.emplohandler.ui.utils

import android.util.Patterns

/**
 * validateEmployeeInput - Client-side field validation
 *
 * Performs real-time validation on all employee input fields
 * Returns map of field names to error messages
 * Empty map means all validations passed
 *
 * @param firstName Employee's first name
 * @param lastName Employee's last name
 * @param email Employee's email address
 * @param phoneNumber Employee's phone number
 * @param address Employee's address
 * @param designation Employee's job designation
 * @param salary Employee's salary
 * @return Map<String, String> - field name to error message (empty if valid)
 */
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

    // Validate First Name: required, letters only, max 50 chars
    when {
        firstName.isBlank() -> errors["firstName"] = "First name is required"
        firstName.length > 50 -> errors["firstName"] = "First name must be less than 50 characters"
        !firstName.all { it.isLetter() || it.isWhitespace() } -> errors["firstName"] = "First name can only contain letters"
    }

    // Validate Last Name: required, letters only, max 50 chars
    when {
        lastName.isBlank() -> errors["lastName"] = "Last name is required"
        lastName.length > 50 -> errors["lastName"] = "Last name must be less than 50 characters"
        !lastName.all { it.isLetter() || it.isWhitespace() } -> errors["lastName"] = "Last name can only contain letters"
    }

    // Validate Email: required, valid format, max 100 chars
    when {
        email.isBlank() -> errors["email"] = "Email is required"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> errors["email"] = "Invalid email format"
        email.length > 100 -> errors["email"] = "Email must be less than 100 characters"
    }

    // Validate Phone: required, at least 10 digits, max 20 chars, numeric + symbols
    when {
        phoneNumber.isBlank() -> errors["phoneNumber"] = "Phone number is required"
        phoneNumber.length < 10 -> errors["phoneNumber"] = "Phone number must be at least 10 digits"
        phoneNumber.length > 20 -> errors["phoneNumber"] = "Phone number must be less than 20 characters"
        !phoneNumber.all { it.isDigit() || it == '+' || it == '-' || it == ' ' } ->
            errors["phoneNumber"] = "Phone number contains invalid characters"
    }

    // Validate Address: required, max 200 chars
    when {
        address.isBlank() -> errors["address"] = "Address is required"
        address.length > 200 -> errors["address"] = "Address must be less than 200 characters"
    }

    // Validate Designation: required, max 100 chars
    when {
        designation.isBlank() -> errors["designation"] = "Designation is required"
        designation.length > 100 -> errors["designation"] = "Designation must be less than 100 characters"
    }

    // Validate Salary: required, valid number format, positive, max 9,999,999.99
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
