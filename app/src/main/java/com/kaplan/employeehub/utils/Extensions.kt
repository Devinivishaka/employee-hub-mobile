package com.kaplan.employeehub.utils

/**
 * Extension function to safely execute repository operations with automatic error handling.
 * Wraps the operation in a try-catch block and returns a Result wrapper.
 *
 * @param T The return type of the repository operation
 * @param operation A suspend function that performs the repository operation
 * @return Result.Success if operation completes successfully, Result.Error otherwise
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
