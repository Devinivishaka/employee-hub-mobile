package com.kaplan.employeehub.utils

/**
 * Extension function to safely execute repository operations with error handling
 */
suspend inline fun <T> safeRepositoryCall(
    operation: suspend () -> T
): Result<T> {
    return try {
        Result.Success(operation())
    } catch (e: Exception) {
        ErrorHandler.logError("safeRepositoryCall", "Repository operation failed", e)
        Result.Error(e)
    }
}
