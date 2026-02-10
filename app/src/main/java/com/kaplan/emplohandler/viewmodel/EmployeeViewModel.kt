package com.kaplan.emplohandler.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaplan.emplohandler.data.Employee
import com.kaplan.emplohandler.data.EmployeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EmployeeViewModel(private val repository: EmployeeRepository) : ViewModel() {
    val allEmployees: StateFlow<List<Employee>> = repository.allEmployees
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedEmployee = MutableStateFlow<Employee?>(null)
    val selectedEmployee: StateFlow<Employee?> = _selectedEmployee

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun setSelectedEmployee(employee: Employee?) {
        _selectedEmployee.value = employee
    }

    fun clearMessage() {
        _uiMessage.value = null
    }

    fun postMessage(message: String) {
        _uiMessage.value = message
    }

    fun addEmployee(employee: Employee) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.insertEmployee(employee)
                _uiMessage.value = "Employee added successfully!"
            } catch (e: IllegalArgumentException) {
                _uiMessage.value = "Invalid employee data. Please check all fields."
            } catch (e: Exception) {
                _uiMessage.value = "Failed to add employee. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateEmployee(employee: Employee) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updateEmployee(employee)
                _uiMessage.value = "Employee updated successfully!"
            } catch (e: IllegalArgumentException) {
                _uiMessage.value = "Invalid employee data. Please check all fields."
            } catch (e: Exception) {
                _uiMessage.value = "Failed to update employee. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteEmployee(employee: Employee) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteEmployee(employee)
                _uiMessage.value = "Employee deleted successfully!"
            } catch (e: Exception) {
                _uiMessage.value = "Failed to delete employee. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteEmployeeById(id: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteEmployeeById(id)
                _uiMessage.value = "Employee deleted successfully!"
            } catch (e: Exception) {
                _uiMessage.value = "Failed to delete employee. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

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
                _uiMessage.value = "Failed to load employee details. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class EmployeeViewModelFactory(private val repository: EmployeeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmployeeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EmployeeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
