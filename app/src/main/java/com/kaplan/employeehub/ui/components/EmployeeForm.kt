package com.kaplan.employeehub.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaplan.employeehub.ui.viewmodel.EmployeeFormState

@Composable
fun EmployeeForm(
    state: EmployeeFormState,
    onChange: (EmployeeFormState) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    isSaving: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        OutlinedTextField(
            value = state.firstName,
            onValueChange = { onChange(state.copy(firstName = it)) },
            label = { Text("First name*") },
            isError = state.firstNameError != null,
            modifier = Modifier.fillMaxWidth()
        )
        state.firstNameError?.let { Text(text = it, color = androidx.compose.material3.MaterialTheme.colorScheme.error) }

        OutlinedTextField(
            value = state.lastName,
            onValueChange = { onChange(state.copy(lastName = it)) },
            label = { Text("Last name*") },
            isError = state.lastNameError != null,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        state.lastNameError?.let { Text(text = it, color = androidx.compose.material3.MaterialTheme.colorScheme.error) }

        OutlinedTextField(
            value = state.email,
            onValueChange = { onChange(state.copy(email = it)) },
            label = { Text("Email") },
            isError = state.emailError != null,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        state.emailError?.let { Text(text = it, color = androidx.compose.material3.MaterialTheme.colorScheme.error) }

        OutlinedTextField(
            value = state.phone,
            onValueChange = { onChange(state.copy(phone = it)) },
            label = { Text("Phone") },
            isError = state.phoneError != null,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        state.phoneError?.let { Text(text = it, color = androidx.compose.material3.MaterialTheme.colorScheme.error) }

        OutlinedTextField(
            value = state.address,
            onValueChange = { onChange(state.copy(address = it)) },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        OutlinedTextField(
            value = state.designation,
            onValueChange = { onChange(state.copy(designation = it)) },
            label = { Text("Designation") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        OutlinedTextField(
            value = state.salary,
            onValueChange = { onChange(state.copy(salary = it)) },
            label = { Text("Salary*") },
            isError = state.salaryError != null,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        state.salaryError?.let { Text(text = it, color = androidx.compose.material3.MaterialTheme.colorScheme.error) }

        Row(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = if (isSaving) "Saving..." else "Save")
            }
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Cancel")
            }
        }
    }
}
