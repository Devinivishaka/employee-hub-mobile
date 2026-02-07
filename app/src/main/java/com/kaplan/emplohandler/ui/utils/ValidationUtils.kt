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

    if (firstName.isBlank()) {
        errors["firstName"] = "First name is required"
    }

    if (lastName.isBlank()) {
        errors["lastName"] = "Last name is required"
    }

    if (email.isBlank()) {
        errors["email"] = "Email is required"
    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        errors["email"] = "Invalid email format"
    }

    if (phoneNumber.isBlank()) {
        errors["phoneNumber"] = "Phone number is required"
    } else if (phoneNumber.length < 10) {
        errors["phoneNumber"] = "Phone number must be at least 10 digits"
    }

    if (address.isBlank()) {
        errors["address"] = "Address is required"
    }

    if (designation.isBlank()) {
        errors["designation"] = "Designation is required"
    }

    if (salary.isBlank()) {
        errors["salary"] = "Salary is required"
    } else {
        try {
            val salaryValue = salary.toDouble()
            if (salaryValue < 0) {
                errors["salary"] = "Salary must be a positive number"
            }
        } catch (e: NumberFormatException) {
            errors["salary"] = "Salary must be a valid number"
        }
    }

    return errors
}
