package com.kaplan.employeehub.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import com.kaplan.employeehub.ui.viewmodel.EmployeeListViewModel
import com.kaplan.employeehub.ui.components.EmployeeRow
import androidx.compose.runtime.collectAsState

@Composable
fun EmployeeListScreen(viewModel: EmployeeListViewModel, onAdd: () -> Unit, onEdit: (Long) -> Unit) {
    val employees = viewModel.employees.collectAsState()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(employees.value) { emp ->
                EmployeeRow(employee = emp, onClick = { onEdit(emp.id) })
            }
        }
    }
}
