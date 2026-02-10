# Error Handling Implementation Guide - Employee Hub

## Overview
This document describes the comprehensive error handling system implemented in the Employee Hub mobile application.

## Architecture

### 1. Custom Exception Hierarchy
Located in `utils/Exceptions.kt`

All custom exceptions extend `EmployeeHubException` for consistent handling:
- `DatabaseException` - Database operation failures
- `EmployeeNotFoundException` - When employee record not found
- `ValidationException` - Input validation failures
- `DataLoadingException` - Data fetch operation failures
- `OperationFailedException` - Generic operation failures

### 2. Result Wrapper Class
Located in `utils/Result.kt`

A sealed class that wraps operation results:
```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception, val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

Benefits:
- Type-safe error handling
- Prevents null pointer exceptions
- Allows functional error handling with `mapResult()`

### 3. Error Handler Utility
Located in `utils/ErrorHandler.kt`

Provides:
- `handleException()` - Converts exceptions to user-friendly messages
- `logError()` - Logs errors with stack traces
- `logWarning()` - Logs warnings
- `logDebug()` - Logs debug messages

### 4. Extension Functions
Located in `utils/Extensions.kt`

- `safeRepositoryCall()` - Wraps repository operations with try-catch

## Implementation Details

### Repository Layer
**File**: `data/repository/EmployeeRepository.kt`

All database operations now include:
- Try-catch blocks for insert/update/delete
- Flow error handling with `.catch()` operator
- Custom exceptions thrown
- Error logging

Example:
```kotlin
override suspend fun insert(employee: Employee): Long = withContext(Dispatchers.IO) {
    try {
        dao.insert(employee)
    } catch (e: Exception) {
        ErrorHandler.logError("Repository", "Failed to insert", e)
        throw DatabaseException("Failed to insert employee", e)
    }
}
```

### Database Layer
**File**: `data/db/AppDatabase.kt`

Database instance creation includes:
- Try-catch for Room database initialization
- Custom exception wrapping
- Error logging

### ViewModel Layer

#### EmployeeListViewModel
**File**: `ui/viewmodel/EmployeeListViewModel.kt`

Added:
- `_errorMessage` StateFlow for displaying errors
- `_isDeleting` StateFlow for tracking delete state
- Try-catch in `delete()` method with callback
- `clearError()` function for dismissing errors

#### EmployeeEditViewModel
**File**: `ui/viewmodel/EmployeeEditViewModel.kt`

Added:
- `_errorMessage` StateFlow for error messages
- `_isLoading` StateFlow for load operations
- `_isDeleting` StateFlow for delete operations
- Exception handling in:
  - `load()` - With employee not found check
  - `save()` - With try-catch and finally block
  - `delete()` - With try-catch and employee existence check
- `clearError()` function

### UI Layer

#### Screens
All screens (`EmployeeListScreen`, `EmployeeEditScreen`, `EmployeeAddScreen`) now:
- Collect error message state
- Display `ErrorDialog` when error occurs
- Call `clearError()` on dialog dismiss
- Disable UI elements during operations

#### Error Dialog Component
**File**: `ui/components/ErrorDialog.kt`

Displays errors in a Material 3 AlertDialog:
- User-friendly error message
- Dismiss button
- Customizable title and button text

#### Form Component
**File**: `ui/components/EmployeeForm.kt`

Enhanced with:
- `isEnabled` parameter to disable fields during operations
- Disabled buttons during saving/loading
- Visual feedback for disabled state

#### Employee Row Component
**File**: `ui/components/EmployeeRow.kt`

Enhanced with:
- `isDeleting` parameter to track deletion state
- Disabled click and delete button during operation
- Visual feedback during deletion

## Error Flow Diagram

```
User Action
    ↓
ViewModel Method
    ↓
Try-Catch Block
    ↓
├─ Success → Call onComplete callback
└─ Exception
     ↓
  ErrorHandler.handleException()
     ↓
  Convert to User Message
     ↓
  Update _errorMessage StateFlow
     ↓
  UI Layer
     ↓
  Display ErrorDialog
     ↓
  User Clicks OK
     ↓
  clearError()
```

## Error Messages

The `ErrorHandler.handleException()` function provides context-specific messages:

| Exception Type | Message |
|---|---|
| `EmployeeNotFoundException` | "Employee not found. Please try again." |
| `ValidationException` | Validation error message or "Validation failed" |
| `DatabaseException` | "Database error. Please try again later." |
| `DataLoadingException` | "Failed to load data. Please try again." |
| `OperationFailedException` | Operation-specific message or "Operation failed" |
| Other Exceptions | "An unexpected error occurred: {error message}" |

## Usage Examples

### Adding Error Handling to New Operations

1. **In ViewModel**:
```kotlin
fun myOperation() {
    viewModelScope.launch {
        try {
            _isLoading.value = true
            _errorMessage.value = null
            
            // Your operation here
            val result = repository.someOperation()
            
            // Success callback
            onSuccess?.invoke()
        } catch (e: Exception) {
            val userMessage = ErrorHandler.handleException(e)
            _errorMessage.value = userMessage
            ErrorHandler.logError("MyViewModel", "Operation failed", e)
        } finally {
            _isLoading.value = false
        }
    }
}
```

2. **In Composable**:
```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel) {
    val errorMessage = viewModel.errorMessage.collectAsState()
    
    if (errorMessage.value != null) {
        ErrorDialog(
            title = "Error",
            message = errorMessage.value ?: "Unknown error",
            onDismiss = { viewModel.clearError() }
        )
    }
    
    // Rest of UI
}
```

3. **In Repository**:
```kotlin
override suspend fun myDatabaseOperation() = withContext(Dispatchers.IO) {
    try {
        dao.myOperation()
    } catch (e: Exception) {
        ErrorHandler.logError("MyRepository", "Database operation failed", e)
        throw DatabaseException("Operation failed", e)
    }
}
```

## Testing Error Scenarios

### Unit Tests
Test exception handling in ViewModels:
```kotlin
@Test
fun testDeleteErrorHandling() {
    // Mock repository to throw exception
    whenever(repository.delete(any())).thenThrow(DatabaseException("DB Error"))
    
    // Verify error message is set
    viewModel.delete(employee)
    assertEquals("Database error. Please try again later.", viewModel.errorMessage.value)
}
```

### Integration Tests
Test error flow from repository to UI:
1. Mock database to throw exception
2. Verify ViewModel captures error
3. Verify UI displays error dialog

## Best Practices

1. **Always wrap database operations** in try-catch or use custom exceptions
2. **Provide user-friendly messages** - Never show technical stack traces to users
3. **Log all errors** for debugging and monitoring
4. **Clear errors** when users dismiss dialogs or navigate
5. **Disable UI elements** during operations to prevent duplicate actions
6. **Test error scenarios** including network failures, database errors, and validation errors
7. **Use StateFlow** for error state instead of Callbacks for reactive updates
8. **Handle null safely** - Check for null before using optional fields

## Monitoring and Debugging

All errors are logged using Android's Log class with:
- Tag: "EmployeeHub" (customizable)
- Level: ERROR or WARNING
- Full stack trace
- Contextual information

View logs using:
```bash
adb logcat | grep EmployeeHub
```

## Files Modified/Created

### New Files Created:
- `utils/Result.kt` - Result wrapper class
- `utils/Exceptions.kt` - Custom exception definitions
- `utils/ErrorHandler.kt` - Error handling utility
- `utils/Extensions.kt` - Extension functions
- `ui/components/ErrorDialog.kt` - Error display component

### Files Modified:
- `data/repository/EmployeeRepository.kt` - Added exception handling
- `data/db/AppDatabase.kt` - Added database initialization error handling
- `ui/viewmodel/EmployeeListViewModel.kt` - Added error state management
- `ui/viewmodel/EmployeeEditViewModel.kt` - Added comprehensive error handling
- `ui/screens/EmployeeListScreen.kt` - Added error dialog display
- `ui/screens/EmployeeEditScreen.kt` - Added error dialog display
- `ui/screens/EmployeeAddScreen.kt` - Added error dialog display
- `ui/components/EmployeeForm.kt` - Added enabled state
- `ui/components/EmployeeRow.kt` - Added deleting state

## Future Enhancements

1. **Retry Mechanism** - Add ability to retry failed operations
2. **Offline Support** - Queue operations when offline, sync when online
3. **Error Analytics** - Send error reports to analytics service
4. **Crash Reporting** - Integrate with Crashlytics for monitoring
5. **Network Error Handling** - Specific handling for network timeouts and connectivity issues
6. **Validation Error Display** - Enhanced validation with field-specific error hints
7. **Toast Notifications** - Add snackbars for non-blocking error notifications
8. **Custom Error Pages** - Show dedicated error screens for critical failures

## Support

For questions or issues regarding error handling, refer to:
- Individual exception definitions in `utils/Exceptions.kt`
- `ErrorHandler` class documentation
- Example implementations in `EmployeeEditViewModel` and related screens
