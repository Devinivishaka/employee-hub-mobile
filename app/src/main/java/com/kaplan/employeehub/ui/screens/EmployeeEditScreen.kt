package com.kaplan.employeehub.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.kaplan.employeehub.ui.components.EmployeeForm
import com.kaplan.employeehub.ui.components.ErrorDialog
import com.kaplan.employeehub.ui.viewmodel.EmployeeEditViewModel

/**
 * Screen for editing an existing employee's information.
 * Loads employee data and allows updates to all fields.
 * Displays error dialogs for any operation failures.
 *
 * @param viewModel The EmployeeEditViewModel managing the screen state
 * @param employeeId The ID of the employee being edited
 * @param onSaved Callback when employee is successfully saved
 * @param onDeleted Callback when employee is successfully deleted
 * @param onCancel Callback when user cancels the operation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeEditScreen(
    viewModel: EmployeeEditViewModel,
    employeeId: Long,
    onSaved: () -> Unit,
    onDeleted: () -> Unit,
    onCancel: () -> Unit
) {
    // Collect all relevant state from ViewModel
    val state = viewModel.form.collectAsState()
    val isSaving = viewModel.isSaving.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()
    val errorMessage = viewModel.errorMessage.collectAsState()

    // Display error dialog if error message exists
    if (errorMessage.value != null) {
        ErrorDialog(
            title = "Error",
            message = errorMessage.value ?: "Unknown error",
            onDismiss = { viewModel.clearError() }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Employee Hub") })
        }
    ) { padding ->
        EmployeeForm(
            state = state.value,
            onChange = {
                viewModel.onFieldChange(
                    firstName = it.firstName,
                    lastName = it.lastName,
                    email = it.email,
                    phone = it.phone,
                    address = it.address,
                    designation = it.designation,
                    salary = it.salary
                )
            },
            onSave = { viewModel.save(existingId = employeeId, onComplete = onSaved) },
            onCancel = onCancel,
            // Show loading text during save or load operations
            isSaving = isSaving.value || isLoading.value,
            modifier = Modifier.padding(padding),
            // Disable form during load operations only
            isEnabled = !isLoading.value && !isSaving.value
        )
    }
}
