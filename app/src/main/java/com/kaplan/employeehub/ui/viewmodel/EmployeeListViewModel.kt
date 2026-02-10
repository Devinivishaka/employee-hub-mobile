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

class EmployeeListViewModel(private val repository: EmployeeRepository) : ViewModel() {
    val employees: StateFlow<List<Employee>> = repository.getAll()
        .map { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

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

    fun clearError() {
        _errorMessage.value = null
    }
}
