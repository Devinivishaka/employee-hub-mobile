package com.kaplan.employeehub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import com.kaplan.employeehub.data.entity.Employee
import com.kaplan.employeehub.data.repository.EmployeeRepository
import com.kaplan.employeehub.utils.ErrorHandler
import com.kaplan.employeehub.utils.EmployeeNotFoundException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class EmployeeFormState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val designation: String = "",
    val salary: String = "",
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val salaryError: String? = null,
)

class EmployeeEditViewModel(
    private val repository: EmployeeRepository,
    private val savedStateHandle: SavedStateHandle? = null
) : ViewModel() {

    private val _form = MutableStateFlow(EmployeeFormState())
    val form: StateFlow<EmployeeFormState> = _form.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun load(employeeId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                val emp = repository.getById(employeeId).first()
                if (emp != null) {
                    _form.value = EmployeeFormState(
                        firstName = emp.firstName,
                        lastName = emp.lastName,
                        email = emp.email ?: "",
                        phone = emp.phone ?: "",
                        address = emp.address ?: "",
                        designation = emp.designation ?: "",
                        salary = emp.salary.toString()
                    )
                } else {
                    throw EmployeeNotFoundException("Employee with id $employeeId not found")
                }
            } catch (e: Exception) {
                val userMessage = ErrorHandler.handleException(e)
                _errorMessage.value = userMessage
                ErrorHandler.logError("EmployeeEditViewModel", "Failed to load employee", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onFieldChange(
        firstName: String = _form.value.firstName,
        lastName: String = _form.value.lastName,
        email: String = _form.value.email,
        phone: String = _form.value.phone,
        address: String = _form.value.address,
        designation: String = _form.value.designation,
        salary: String = _form.value.salary,
    ) {
        _form.value = _form.value.copy(
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone,
            address = address,
            designation = designation,
            salary = salary,
            firstNameError = null,
            lastNameError = null,
            emailError = null,
            phoneError = null,
            salaryError = null,
        )
    }

    private fun validate(): Boolean {
        var valid = true
        val current = _form.value
        val firstName = current.firstName.trim()
        val lastName = current.lastName.trim()
        val email = current.email.trim()
        val phone = current.phone.trim()
        val salaryText = current.salary.trim()
        var firstNameError: String? = null
        var lastNameError: String? = null
        var emailError: String? = null
        var phoneError: String? = null
        var salaryError: String? = null

        if (firstName.isBlank()) {
            firstNameError = "First name is required"
            valid = false
        }
        if (lastName.isBlank()) {
            lastNameError = "Last name is required"
            valid = false
        }
        if (email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Invalid email"
            valid = false
        }
        if (phone.isNotBlank() && !android.util.Patterns.PHONE.matcher(phone).matches()) {
            phoneError = "Invalid phone"
            valid = false
        }
        val salaryValue = salaryText.toDoubleOrNull()
        if (salaryText.isBlank() || salaryValue == null || salaryValue < 0.0) {
            salaryError = "Salary must be a non-negative number"
            valid = false
        }

        _form.value = current.copy(
            firstNameError = firstNameError,
            lastNameError = lastNameError,
            emailError = emailError,
            phoneError = phoneError,
            salaryError = salaryError
        )

        return valid
    }

    fun save(existingId: Long? = null, onComplete: (() -> Unit)? = null) {
        if (!validate()) return
        viewModelScope.launch {
            try {
                _isSaving.value = true
                _errorMessage.value = null
                val current = _form.value
                val email = current.email.trim()
                val phone = current.phone.trim()
                val address = current.address.trim()
                val designation = current.designation.trim()
                val salaryText = current.salary.trim()
                val employee = Employee(
                    id = existingId ?: 0,
                    firstName = current.firstName.trim(),
                    lastName = current.lastName.trim(),
                    email = email.takeIf { it.isNotEmpty() },
                    phone = phone.takeIf { it.isNotEmpty() },
                    address = address.takeIf { it.isNotEmpty() },
                    designation = designation.takeIf { it.isNotEmpty() },
                    salary = salaryText.toDoubleOrNull() ?: 0.0
                )
                if (existingId == null || existingId == 0L) {
                    repository.insert(employee)
                } else {
                    repository.update(employee)
                }
                onComplete?.invoke()
            } catch (e: Exception) {
                val userMessage = ErrorHandler.handleException(e)
                _errorMessage.value = userMessage
                ErrorHandler.logError("EmployeeEditViewModel", "Failed to save employee", e)
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun delete(employeeId: Long, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                _isDeleting.value = true
                _errorMessage.value = null
                val emp = repository.getById(employeeId).first()
                if (emp != null) {
                    repository.delete(emp)
                    onComplete?.invoke()
                } else {
                    throw EmployeeNotFoundException("Employee not found")
                }
            } catch (e: Exception) {
                val userMessage = ErrorHandler.handleException(e)
                _errorMessage.value = userMessage
                ErrorHandler.logError("EmployeeEditViewModel", "Failed to delete employee", e)
            } finally {
                _isDeleting.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
