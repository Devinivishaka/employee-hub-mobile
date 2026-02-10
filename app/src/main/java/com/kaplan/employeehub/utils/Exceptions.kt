package com.kaplan.employeehub.utils

/**
 * Custom exceptions for Employee Hub application
 */

open class EmployeeHubException(message: String, cause: Throwable? = null) : Exception(message, cause)

class DatabaseException(message: String = "Database operation failed", cause: Throwable? = null) : EmployeeHubException(message, cause)

class EmployeeNotFoundException(message: String = "Employee not found", cause: Throwable? = null) : EmployeeHubException(message, cause)

class ValidationException(message: String = "Validation failed", cause: Throwable? = null) : EmployeeHubException(message, cause)

class DataLoadingException(message: String = "Failed to load data", cause: Throwable? = null) : EmployeeHubException(message, cause)

class OperationFailedException(message: String = "Operation failed", cause: Throwable? = null) : EmployeeHubException(message, cause)
