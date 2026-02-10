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

/**
 * MainActivity - Main entry point of the application
 *
 * Responsibilities:
 * - Initialize database and dependency injection
 * - Create and provide ViewModel instance
 * - Setup Compose UI with theme
 * - Host navigation controller
 */
class MainActivity : ComponentActivity() {
    private lateinit var viewModel: EmployeeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize database using singleton pattern
        val database = EmployeeDatabase.getDatabase(this)

        // 2. Create repository with DAO (data access layer)
        val repository = EmployeeRepository(database.employeeDao())

        // 3. Create factory for ViewModel dependency injection
        val factory = EmployeeViewModelFactory(repository)

        // 4. Get or create ViewModel from factory
        viewModel = ViewModelProvider(this, factory).get(EmployeeViewModel::class.java)

        // 5. Set Compose content with theme
        setContent {
            EmploHandlerTheme {
                AppNavigation(viewModel)
            }
        }
    }
}

/**
 * AppNavigation - Main navigation graph
 *
 * Manages:
 * - Navigation between screens (list, add, detail)
 * - Shared snackbar for error/success messages
 * - Automatic error message display
 * - Employee data flow between screens
 *
 * Navigation Routes:
 * - employee_list: Main employee list screen
 * - add_employee: Add new employee screen
 * - employee_detail/{employeeId}: View/edit employee details
 */
@Composable
fun AppNavigation(viewModel: EmployeeViewModel) {
    // Navigation controller for managing screen transitions
    val navController = rememberNavController()

    // Shared snackbar state for displaying messages across all screens
    val snackbarHostState = remember { SnackbarHostState() }

    // Observe UI message state from ViewModel
    val uiMessage by viewModel.uiMessage.collectAsState()

    // Show snackbar when message updates (success/error from operations)
    LaunchedEffect(uiMessage) {
        uiMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessage()  // Clear after displaying
        }
    }

    // Navigation host with composable routes
    NavHost(
        navController = navController,
        startDestination = "employee_list"
    ) {
        // ROUTE 1: Employee List Screen (main/home screen)
        composable("employee_list") {
            val employees by viewModel.allEmployees.collectAsState()

            EmployeeListScreen(
                employees = employees,
                snackbarHostState = snackbarHostState,
                // Navigate to add employee screen
                onAddClick = {
                    navController.navigate("add_employee")
                },
                // Navigate to detail screen with employee ID
                onEmployeeClick = { employee ->
                    viewModel.setSelectedEmployee(employee)
                    navController.navigate("employee_detail/${employee.id}")
                },
                // Delete employee (triggers snackbar message)
                onDelete = { employee ->
                    viewModel.deleteEmployee(employee)
                }
            )
        }

        // ROUTE 2: Add Employee Screen (new employee form)
        composable("add_employee") {
            AddEmployeeScreen(
                snackbarHostState = snackbarHostState,
                // Go back to list
                onBack = {
                    navController.navigateUp()
                },
                // Save new employee and return to list
                onSave = { employee ->
                    viewModel.addEmployee(employee)
                    // Navigate back after a short delay to allow message to show
                    navController.popBackStack("employee_list", inclusive = false)
                }
            )
        }

        // ROUTE 3: Employee Detail Screen (view/edit existing employee)
        composable("employee_detail/{employeeId}") { backStackEntry ->
            // Extract employee ID from route parameter
            val employeeId = backStackEntry.arguments?.getString("employeeId")?.toIntOrNull()

            // Observe selected employee from ViewModel
            val selectedEmployee by viewModel.selectedEmployee.collectAsState()

            // Load employee when screen opens (if not already loaded)
            LaunchedEffect(employeeId) {
                if (employeeId != null && (selectedEmployee == null || selectedEmployee?.id != employeeId)) {
                    viewModel.loadEmployeeById(employeeId)
                }
            }

            // Only show detail screen if employee is loaded and ID matches
            if (employeeId != null && selectedEmployee != null && selectedEmployee?.id == employeeId) {
                EmployeeDetailScreen(
                    employee = selectedEmployee!!,
                    snackbarHostState = snackbarHostState,
                    // Go back to list
                    onBack = {
                        navController.navigateUp()
                    },
                    // Update employee and return to list
                    onUpdate = { updatedEmployee ->
                        viewModel.updateEmployee(updatedEmployee)
                        navController.popBackStack("employee_list", inclusive = false)
                    },
                    // Delete employee and return to list
                    onDelete = { employee ->
                        viewModel.deleteEmployee(employee)
                        navController.popBackStack("employee_list", inclusive = false)
                    }
                )
            }
        }
    }
}