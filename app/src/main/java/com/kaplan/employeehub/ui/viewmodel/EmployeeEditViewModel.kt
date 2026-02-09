package com.kaplan.employeehub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import com.kaplan.employeehub.data.entity.Employee
import com.kaplan.employeehub.data.repository.EmployeeRepository
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

    fun load(employeeId: Long) {
        viewModelScope.launch {
            val emp = repository.getById(employeeId).first()
            emp?.let {
                _form.value = EmployeeFormState(
                    firstName = it.firstName,
                    lastName = it.lastName,
                    email = it.email ?: "",
                    phone = it.phone ?: "",
                    address = it.address ?: "",
                    designation = it.designation ?: "",
                    salary = it.salary.toString()
                )
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
            _isSaving.value = true
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
            _isSaving.value = false
            onComplete?.invoke()
        }
    }

    fun delete(employeeId: Long, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            val emp = repository.getById(employeeId).first()
            emp?.let {
                repository.delete(it)
            }
            onComplete?.invoke()
        }
    }
}
