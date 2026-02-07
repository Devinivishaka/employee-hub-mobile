package com.kaplan.emplohandler.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaplan.emplohandler.data.Employee
import com.kaplan.emplohandler.data.EmployeeRepository
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

    var selectedEmployee: Employee? = null
        private set

    fun setSelectedEmployee(employee: Employee?) {
        selectedEmployee = employee
    }

    fun addEmployee(employee: Employee) {
        viewModelScope.launch {
            repository.insertEmployee(employee)
        }
    }

    fun updateEmployee(employee: Employee) {
        viewModelScope.launch {
            repository.updateEmployee(employee)
        }
    }

    fun deleteEmployee(employee: Employee) {
        viewModelScope.launch {
            repository.deleteEmployee(employee)
        }
    }

    fun deleteEmployeeById(id: Int) {
        viewModelScope.launch {
            repository.deleteEmployeeById(id)
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
