package com.kaplan.employeehub.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaplan.employeehub.data.entity.Employee
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete

@Composable
fun EmployeeRow(
    employee: Employee,
    onClick: () -> Unit,
    onDelete: (Employee) -> Unit,
    isDeleting: Boolean = false
) {
    val cardColor = MaterialTheme.colorScheme.secondaryContainer
    val textColor = MaterialTheme.colorScheme.onSecondaryContainer

    Card(
        onClick = onClick,
        enabled = !isDeleting,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val initials = (employee.firstName.firstOrNull()?.toString() ?: "") +
                (employee.lastName.firstOrNull()?.toString() ?: "")
            Surface(
                shape = CircleShape,
                color = cardColor,
                modifier = Modifier.size(44.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = initials.uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${employee.firstName} ${employee.lastName}",
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor
                )
                if (!employee.designation.isNullOrBlank()) {
                    Text(
                        text = employee.designation,
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor
                    )
                }
            }
            IconButton(
                onClick = { onDelete(employee) },
                enabled = !isDeleting
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = textColor
                )
            }
        }
    }
}
