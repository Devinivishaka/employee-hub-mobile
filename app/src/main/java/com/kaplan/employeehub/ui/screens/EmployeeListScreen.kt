package com.kaplan.employeehub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import com.kaplan.employeehub.ui.viewmodel.EmployeeListViewModel
import com.kaplan.employeehub.ui.components.EmployeeRow
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.unit.dp
import com.kaplan.employeehub.ui.components.ErrorDialog
import com.kaplan.employeehub.ui.components.ConfirmationDialog

/**
 * Screen that displays a list of all employees.
 * Supports adding new employees and editing/deleting existing ones.
 * Includes error dialog display for user feedback on failures.
 *
 * @param viewModel The EmployeeListViewModel managing the screen state
 * @param onAdd Callback when user clicks the Add Employee button
 * @param onEdit Callback when user clicks an employee to edit (receives employeeId)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeListScreen(viewModel: EmployeeListViewModel, onAdd: () -> Unit, onEdit: (Long) -> Unit) {
    // Collect state from ViewModel for reactive UI updates
    val employees = viewModel.employees.collectAsState()
    val errorMessage = viewModel.errorMessage.collectAsState()
    val isDeleting = viewModel.isDeleting.collectAsState()
    val employeePendingDeletion = viewModel.employeePendingDeletion.collectAsState()

    // Configure app bar colors
    val appBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.primary,
        titleContentColor = MaterialTheme.colorScheme.onPrimary
    )

    // Display error dialog if error message exists
    if (errorMessage.value != null) {
        ErrorDialog(
            title = "Error",
            message = errorMessage.value ?: "Unknown error",
            onDismiss = { viewModel.clearError() }
        )
    }

    // Display confirmation dialog when deletion is initiated
    val pendingEmployee = employeePendingDeletion.value
    if (pendingEmployee != null) {
        ConfirmationDialog(
            title = "Delete Employee",
            message = "Are you sure you want to delete ${pendingEmployee.firstName} ${pendingEmployee.lastName}?",
            onConfirm = { viewModel.confirmDelete() },
            onCancel = { viewModel.cancelDelete() }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Employee Hub") },
                colors = appBarColors
            )
        },
        floatingActionButton = {
            // FAB disabled during deletion to prevent duplicate operations
            FloatingActionButton(
                onClick = onAdd,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Display each employee as a row
            items(employees.value) { emp ->
                EmployeeRow(
                    employee = emp,
                    onClick = { onEdit(emp.id) },
                    onDelete = { toDelete -> viewModel.initiateDelete(toDelete) },
                    isDeleting = isDeleting.value
                )
            }
        }
    }
}
