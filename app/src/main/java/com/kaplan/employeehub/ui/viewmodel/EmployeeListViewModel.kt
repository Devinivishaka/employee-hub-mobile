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

    /**
     * Deletes an employee from the database.
     * Updates error state if operation fails and notifies success via callback.
     *
     * @param employee The employee to delete
     * @param onSuccess Optional callback invoked when deletion succeeds
     */
    fun delete(employee: Employee, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                _isDeleting.value = true
                _errorMessage.value = null
                repository.delete(employee)
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

    /** Clears the current error message from display */
    fun clearError() {
        _errorMessage.value = null
    }
}
