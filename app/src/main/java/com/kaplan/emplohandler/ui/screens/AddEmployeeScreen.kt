package com.kaplan.emplohandler.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaplan.emplohandler.data.Employee
import com.kaplan.emplohandler.util.ErrorHandler
import com.kaplan.emplohandler.util.ValidationResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEmployeeScreen(
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onSave: (Employee) -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }

    var errors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add New Employee",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // First Name
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name *") },
                modifier = Modifier.fillMaxWidth(),
                isError = errors.containsKey("firstName"),
                supportingText = {
                    if (errors.containsKey("firstName")) {
                        Text(errors["firstName"] ?: "", color = MaterialTheme.colorScheme.error)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            // Last Name
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name *") },
                modifier = Modifier.fillMaxWidth(),
                isError = errors.containsKey("lastName"),
                supportingText = {
                    if (errors.containsKey("lastName")) {
                        Text(errors["lastName"] ?: "", color = MaterialTheme.colorScheme.error)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email *") },
                modifier = Modifier.fillMaxWidth(),
                isError = errors.containsKey("email"),
                supportingText = {
                    if (errors.containsKey("email")) {
                        Text(errors["email"] ?: "", color = MaterialTheme.colorScheme.error)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            // Phone Number
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number *") },
                modifier = Modifier.fillMaxWidth(),
                isError = errors.containsKey("phoneNumber"),
                supportingText = {
                    if (errors.containsKey("phoneNumber")) {
                        Text(errors["phoneNumber"] ?: "", color = MaterialTheme.colorScheme.error)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            // Address
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                isError = errors.containsKey("address"),
                supportingText = {
                    if (errors.containsKey("address")) {
                        Text(errors["address"] ?: "", color = MaterialTheme.colorScheme.error)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                maxLines = 4
            )

            // Designation
            OutlinedTextField(
                value = designation,
                onValueChange = { designation = it },
                label = { Text("Designation *") },
                modifier = Modifier.fillMaxWidth(),
                isError = errors.containsKey("designation"),
                supportingText = {
                    if (errors.containsKey("designation")) {
                        Text(errors["designation"] ?: "", color = MaterialTheme.colorScheme.error)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            // Salary
            OutlinedTextField(
                value = salary,
                onValueChange = { salary = it },
                label = { Text("Salary *") },
                modifier = Modifier.fillMaxWidth(),
                isError = errors.containsKey("salary"),
                supportingText = {
                    if (errors.containsKey("salary")) {
                        Text(errors["salary"] ?: "", color = MaterialTheme.colorScheme.error)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
                }

                Button(
                    onClick = {
                        // Use ErrorHandler for validation
                        val validationResult = ErrorHandler.validateEmployeeData(
                            firstName, lastName, email, phoneNumber, address, designation, salary
                        )

                        when (validationResult) {
                            is ValidationResult.Success -> {
                                // Validation passed, create and save employee
                                val employee = Employee(
                                    firstName = firstName,
                                    lastName = lastName,
                                    email = email,
                                    phoneNumber = phoneNumber,
                                    address = address,
                                    designation = designation,
                                    salary = salary.toDouble()
                                )
                                onSave(employee)
                            }
                            is ValidationResult.Failure -> {
                                // Map ErrorHandler errors to form field errors
                                errors = validationResult.errors.associate { error ->
                                    val fieldName = when {
                                        error.contains("First name", ignoreCase = true) -> "firstName"
                                        error.contains("Last name", ignoreCase = true) -> "lastName"
                                        error.contains("Email", ignoreCase = true) -> "email"
                                        error.contains("Phone", ignoreCase = true) -> "phoneNumber"
                                        error.contains("Address", ignoreCase = true) -> "address"
                                        error.contains("Designation", ignoreCase = true) -> "designation"
                                        error.contains("Salary", ignoreCase = true) -> "salary"
                                        else -> "general"
                                    }
                                    fieldName to error
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Save", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

