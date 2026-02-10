package com.kaplan.employeehub.utils

import android.util.Log

/**
 * Utility object for consistent error handling, logging, and user-friendly message generation.
 * Centralizes error handling logic to avoid duplication across ViewModels.
 */
object ErrorHandler {
    private const val TAG = "EmployeeHub"

    /**
     * Converts an exception to a user-friendly error message.
     * Different exception types receive context-specific messages.
     *
     * @param exception The exception to handle
     * @return A user-friendly error message suitable for display in UI
     */
    fun handleException(exception: Exception): String {
        Log.e(TAG, "Error occurred: ${exception.message}", exception)

        return when (exception) {
            is EmployeeNotFoundException -> "Employee not found. Please try again."
            is ValidationException -> exception.message ?: "Validation failed. Please check your input."
            is DatabaseException -> "Database error. Please try again later."
            is DataLoadingException -> "Failed to load data. Please try again."
            is OperationFailedException -> exception.message ?: "Operation failed. Please try again."
            else -> "An unexpected error occurred: ${exception.message ?: "Unknown error"}"
        }
    }

    /**
     * Logs an error with stack trace for debugging purposes.
     *
     * @param tag Log tag for filtering logs
     * @param message Descriptive message about the error
     * @param exception The exception to log (optional)
     */
    fun logError(tag: String = TAG, message: String, exception: Exception? = null) {
        if (exception != null) {
            Log.e(tag, message, exception)
        } else {
            Log.e(tag, message)
        }
    }

    /**
     * Logs a warning message for potentially problematic situations.
     *
     * @param tag Log tag for filtering logs
     * @param message Warning message
     */
    fun logWarning(tag: String = TAG, message: String) {
        Log.w(tag, message)
    }

    /**
     * Logs a debug message for development and troubleshooting.
     *
     * @param tag Log tag for filtering logs
     * @param message Debug message
     */
    fun logDebug(tag: String = TAG, message: String) {
        Log.d(tag, message)
    }
}
