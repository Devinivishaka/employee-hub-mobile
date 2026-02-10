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
 * Screen for adding a new employee to the system.
 * Provides an empty form for user to fill in employee details.
 * Displays error dialogs for any operation failures.
 *
 * @param viewModel The EmployeeEditViewModel managing the screen state
 * @param onSaved Callback when new employee is successfully created
 * @param onCancel Callback when user cancels the operation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeAddScreen(
    viewModel: EmployeeEditViewModel,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    // Collect relevant state from ViewModel
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
            onSave = { viewModel.save(onComplete = onSaved) },
            onCancel = onCancel,
            // Show loading text during save or load operations
            isSaving = isSaving.value || isLoading.value,
            modifier = Modifier.padding(padding),
            // Disable form during load operations
            isEnabled = !isLoading.value && !isSaving.value
        )
    }
}
