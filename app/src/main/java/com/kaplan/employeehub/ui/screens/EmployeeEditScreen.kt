package com.kaplan.employeehub.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.kaplan.employeehub.ui.components.EmployeeForm
import com.kaplan.employeehub.ui.viewmodel.EmployeeEditViewModel

@Composable
fun EmployeeEditScreen(viewModel: EmployeeEditViewModel, employeeId: Long, onSaved: () -> Unit, onDeleted: () -> Unit, onCancel: () -> Unit) {
    val state = viewModel.form.collectAsState()
    val isSaving = viewModel.isSaving.collectAsState()

    EmployeeForm(state = state.value, onChange = { viewModel.onFieldChange(
        firstName = it.firstName,
        lastName = it.lastName,
        email = it.email,
        phone = it.phone,
        address = it.address,
        designation = it.designation,
        salary = it.salary
    ) }, onSave = { viewModel.save(existingId = employeeId, onComplete = onSaved) }, onCancel = onCancel, isSaving = isSaving.value)

    // For deletion, we could add a button here in UI. For simplicity, users can edit and save. Deletion is provided via ViewModel.delete if wired to a UI action.
}
