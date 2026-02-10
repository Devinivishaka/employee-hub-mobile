package com.kaplan.emplohandler.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaplan.emplohandler.data.Employee
import com.kaplan.emplohandler.data.EmployeeRepository
import com.kaplan.emplohandler.util.ErrorHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * EmployeeViewModel - Manages employee data and operations
 *
 * Responsibilities:
 * - Orchestrate CRUD operations via repository
 * - Manage UI state (loading, messages)
 * - Handle errors with user-friendly messages
 * - Maintain employee selection state
 */
class EmployeeViewModel(private val repository: EmployeeRepository) : ViewModel() {

    // StateFlow: All employees from database, updated in real-time
    val allEmployees: StateFlow<List<Employee>> = repository.allEmployees
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ...existing code...

    // StateFlow: Currently selected employee for detail view
    private val _selectedEmployee = MutableStateFlow<Employee?>(null)
    val selectedEmployee: StateFlow<Employee?> = _selectedEmployee

    // StateFlow: UI message for snackbar display (success/error)
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage

    // StateFlow: Loading indicator for ongoing operations
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /**
     * Set the currently selected employee
     * Used when navigating to employee detail screen
     */
    fun setSelectedEmployee(employee: Employee?) {
        _selectedEmployee.value = employee
    }

    /**
     * Clear UI message (typically called after snackbar is shown)
     */
    fun clearMessage() {
        _uiMessage.value = null
    }

    /**
     * Post a message to be displayed in snackbar
     */
    fun postMessage(message: String) {
        _uiMessage.value = message
    }

    /**
     * Add new employee to database with error handling
     * Shows user-friendly error messages from ErrorHandler
     */
    fun addEmployee(employee: Employee) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.insertEmployee(employee)
                _uiMessage.value = ErrorHandler.getOperationMessage(true, "add")
            } catch (e: Exception) {
                _uiMessage.value = ErrorHandler.getErrorMessage(e, "add employee")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Update existing employee with error handling
     * Shows user-friendly error messages from ErrorHandler
     */
    fun updateEmployee(employee: Employee) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updateEmployee(employee)
                _uiMessage.value = ErrorHandler.getOperationMessage(true, "update")
            } catch (e: Exception) {
                _uiMessage.value = ErrorHandler.getErrorMessage(e, "update employee")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Delete employee with error handling
     * Shows user-friendly error messages from ErrorHandler
     */
    fun deleteEmployee(employee: Employee) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteEmployee(employee)
                _uiMessage.value = ErrorHandler.getOperationMessage(true, "delete")
            } catch (e: Exception) {
                _uiMessage.value = ErrorHandler.getErrorMessage(e, "delete employee")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Delete employee by ID with error handling
     * Shows user-friendly error messages from ErrorHandler
     */
    fun deleteEmployeeById(id: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteEmployeeById(id)
                _uiMessage.value = ErrorHandler.getOperationMessage(true, "delete")
            } catch (e: Exception) {
                _uiMessage.value = ErrorHandler.getErrorMessage(e, "delete employee")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Load employee by ID with error handling
     * Shows user-friendly error messages from ErrorHandler
     */
    fun loadEmployeeById(id: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val employee = repository.getEmployeeById(id)
                if (employee == null) {
                    _uiMessage.value = "Employee not found."
                } else {
                    _selectedEmployee.value = employee
                }
            } catch (e: Exception) {
                _uiMessage.value = ErrorHandler.getErrorMessage(e, "load employee")
            } finally {
                _isLoading.value = false
            }
        }
    }
}

/**
 * Factory for creating EmployeeViewModel instances with repository
 * Required by ViewModelProvider to pass dependencies
 */

class EmployeeViewModelFactory(private val repository: EmployeeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmployeeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EmployeeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
