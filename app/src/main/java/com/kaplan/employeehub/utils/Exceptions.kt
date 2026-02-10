package com.kaplan.employeehub.utils

/**
 * Custom exception hierarchy for the Employee Hub application.
 * All exceptions extend EmployeeHubException for consistent error handling.
 */

/** Base exception for all Employee Hub exceptions */
open class EmployeeHubException(message: String, cause: Throwable? = null) : Exception(message, cause)

/** Thrown when database operations fail (insert, update, delete, query) */
class DatabaseException(message: String = "Database operation failed", cause: Throwable? = null) : EmployeeHubException(message, cause)

/** Thrown when an employee record cannot be found in the database */
class EmployeeNotFoundException(message: String = "Employee not found", cause: Throwable? = null) : EmployeeHubException(message, cause)

/** Thrown when user input validation fails */
class ValidationException(message: String = "Validation failed", cause: Throwable? = null) : EmployeeHubException(message, cause)

/** Thrown when data loading operations fail */
class DataLoadingException(message: String = "Failed to load data", cause: Throwable? = null) : EmployeeHubException(message, cause)

/** Thrown when a general operation fails */
class OperationFailedException(message: String = "Operation failed", cause: Throwable? = null) : EmployeeHubException(message, cause)
