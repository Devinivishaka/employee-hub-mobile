package com.kaplan.employeehub.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaplan.employeehub.data.entity.Employee

@Composable
fun EmployeeRow(employee: Employee, onClick: () -> Unit) {
    Row(modifier = Modifier
        .clickable { onClick() }
        .padding(12.dp)) {
        val initials = (employee.firstName.firstOrNull()?.toString() ?: "") + (employee.lastName.firstOrNull()?.toString() ?: "")
        Text(text = initials, style = MaterialTheme.typography.titleLarge, modifier = Modifier.size(40.dp))
        Spacer(modifier = Modifier.padding(8.dp))
        Column {
            Text(text = "${employee.firstName} ${employee.lastName}", style = MaterialTheme.typography.titleMedium)
            Text(text = employee.designation ?: "", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
