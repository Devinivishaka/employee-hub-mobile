package com.kaplan.emplohandler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kaplan.emplohandler.data.EmployeeDatabase
import com.kaplan.emplohandler.data.EmployeeRepository
import com.kaplan.emplohandler.ui.screens.AddEmployeeScreen
import com.kaplan.emplohandler.ui.screens.EmployeeDetailScreen
import com.kaplan.emplohandler.ui.screens.EmployeeListScreen
import com.kaplan.emplohandler.ui.theme.EmploHandlerTheme
import com.kaplan.emplohandler.viewmodel.EmployeeViewModel
import com.kaplan.emplohandler.viewmodel.EmployeeViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: EmployeeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize database and repository
        val database = EmployeeDatabase.getDatabase(this)
        val repository = EmployeeRepository(database.employeeDao())
        val factory = EmployeeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(EmployeeViewModel::class.java)

        setContent {
            EmploHandlerTheme {
                AppNavigation(viewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: EmployeeViewModel) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val uiMessage by viewModel.uiMessage.collectAsState()

    // Show snackbar when message updates
    LaunchedEffect(uiMessage) {
        uiMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessage()
        }
    }

    NavHost(
        navController = navController,
        startDestination = "employee_list"
    ) {
        composable("employee_list") {
            val employees by viewModel.allEmployees.collectAsState()

            EmployeeListScreen(
                employees = employees,
                snackbarHostState = snackbarHostState,
                onAddClick = {
                    navController.navigate("add_employee")
                },
                onEmployeeClick = { employee ->
                    viewModel.setSelectedEmployee(employee)
                    navController.navigate("employee_detail/${employee.id}")
                },
                onDelete = { employee ->
                    viewModel.deleteEmployee(employee)
                }
            )
        }

        composable("add_employee") {
            AddEmployeeScreen(
                snackbarHostState = snackbarHostState,
                onBack = {
                    navController.navigateUp()
                },
                onSave = { employee ->
                    viewModel.addEmployee(employee)
                    // Navigate back after a short delay to allow message to show
                    navController.popBackStack("employee_list", inclusive = false)
                }
            )
        }

        composable("employee_detail/{employeeId}") { backStackEntry ->
            val employeeId = backStackEntry.arguments?.getString("employeeId")?.toIntOrNull()
            val selectedEmployee by viewModel.selectedEmployee.collectAsState()

            // Load employee if not already loaded
            LaunchedEffect(employeeId) {
                if (employeeId != null && (selectedEmployee == null || selectedEmployee?.id != employeeId)) {
                    viewModel.loadEmployeeById(employeeId)
                }
            }

            if (employeeId != null && selectedEmployee != null && selectedEmployee?.id == employeeId) {
                EmployeeDetailScreen(
                    employee = selectedEmployee!!,
                    snackbarHostState = snackbarHostState,
                    onBack = {
                        navController.navigateUp()
                    },
                    onUpdate = { updatedEmployee ->
                        viewModel.updateEmployee(updatedEmployee)
                        navController.popBackStack("employee_list", inclusive = false)
                    },
                    onDelete = { employee ->
                        viewModel.deleteEmployee(employee)
                        navController.popBackStack("employee_list", inclusive = false)
                    }
                )
            }
        }
    }
}