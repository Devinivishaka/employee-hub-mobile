package com.kaplan.employeehub.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * A reusable Composable that displays error messages in a Material 3 AlertDialog.
 * Used throughout the app to show user-friendly error messages.
 *
 * @param title The dialog title (default: "Error")
 * @param message The error message body to display
 * @param onDismiss Callback when user dismisses the dialog
 * @param dismissButtonText The text for the dismiss button (default: "OK")
 */
@Composable
fun ErrorDialog(
    title: String = "Error",
    message: String,
    onDismiss: () -> Unit,
    dismissButtonText: String = "OK"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(dismissButtonText)
            }
        }
    )
}
