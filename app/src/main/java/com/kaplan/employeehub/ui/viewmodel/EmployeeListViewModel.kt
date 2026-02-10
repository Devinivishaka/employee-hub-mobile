package com.kaplan.employeehub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaplan.employeehub.data.entity.Employee
import com.kaplan.employeehub.data.repository.EmployeeRepository
import com.kaplan.employeehub.utils.ErrorHandler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing the Employee List screen.
 * Handles employee data loading, deletion, and error state management.
 */
class EmployeeListViewModel(private val repository: EmployeeRepository) : ViewModel() {

    /** StateFlow of all employees, populated from repository with empty list as default */
    val employees: StateFlow<List<Employee>> = repository.getAll()
        .map { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** StateFlow for error messages displayed to the user */
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /** StateFlow to track if a delete operation is in progress */
    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    /** StateFlow to track the employee pending deletion confirmation (null if none) */
    private val _employeePendingDeletion = MutableStateFlow<Employee?>(null)
    val employeePendingDeletion: StateFlow<Employee?> = _employeePendingDeletion.asStateFlow()

    /**
     * Initiates deletion by showing confirmation dialog.
     * Sets the employee pending deletion without deleting immediately.
     *
     * @param employee The employee to delete
     */
    fun initiateDelete(employee: Employee) {
        _employeePendingDeletion.value = employee
    }

    /**
     * Confirms and executes the deletion of the pending employee.
     * Updates error state if operation fails and notifies success via callback.
     *
     * @param onSuccess Optional callback invoked when deletion succeeds
     */
    fun confirmDelete(onSuccess: (() -> Unit)? = null) {
        val employee = _employeePendingDeletion.value
        if (employee == null) {
            ErrorHandler.logWarning("EmployeeListViewModel", "No employee pending deletion")
            return
        }

        viewModelScope.launch {
            try {
                _isDeleting.value = true
                _errorMessage.value = null
                repository.delete(employee)
                _employeePendingDeletion.value = null
                onSuccess?.invoke()
            } catch (e: Exception) {
                val userMessage = ErrorHandler.handleException(e)
                _errorMessage.value = userMessage
                ErrorHandler.logError("EmployeeListViewModel", "Delete failed", e)
            } finally {
                _isDeleting.value = false
            }
        }
    }

    /** Cancels the pending deletion and clears the confirmation dialog */
    fun cancelDelete() {
        _employeePendingDeletion.value = null
    }

    /** Clears the current error message from display */
    fun clearError() {
        _errorMessage.value = null
    }
}
