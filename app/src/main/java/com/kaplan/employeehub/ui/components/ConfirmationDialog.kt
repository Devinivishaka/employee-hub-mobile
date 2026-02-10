package com.kaplan.employeehub.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * A reusable confirmation dialog component for destructive actions.
 * Displays a warning message and asks user to confirm before proceeding.
 *
 * @param title The dialog title
 * @param message The confirmation message to display
 * @param onConfirm Callback when user confirms the action
 * @param onCancel Callback when user cancels the action
 * @param confirmButtonText The text for the confirm button (default: "Delete")
 * @param cancelButtonText The text for the cancel button (default: "Cancel")
 */
@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    confirmButtonText: String = "Delete",
    cancelButtonText: String = "Cancel"
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text(cancelButtonText)
            }
        }
    )
}
