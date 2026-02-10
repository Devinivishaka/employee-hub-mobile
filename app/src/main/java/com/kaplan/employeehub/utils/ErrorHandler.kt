package com.kaplan.employeehub.utils

import android.util.Log

/**
 * Utility class for handling and logging errors
 */
object ErrorHandler {
    private const val TAG = "EmployeeHub"

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

    fun logError(tag: String = TAG, message: String, exception: Exception? = null) {
        if (exception != null) {
            Log.e(tag, message, exception)
        } else {
            Log.e(tag, message)
        }
    }

    fun logWarning(tag: String = TAG, message: String) {
        Log.w(tag, message)
    }

    fun logDebug(tag: String = TAG, message: String) {
        Log.d(tag, message)
    }
}
