package com.kaplan.emplohandler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

    NavHost(
        navController = navController,
        startDestination = "employee_list"
    ) {
        composable("employee_list") {
            val employees by viewModel.allEmployees.collectAsState()

            EmployeeListScreen(
                employees = employees,
                onAddClick = {
                    navController.navigate("add_employee")
                },
                onEmployeeClick = { employee ->
                    viewModel.setSelectedEmployee(employee)
                    navController.navigate("employee_detail/${employee.id}")
                }
            )
        }

        composable("add_employee") {
            AddEmployeeScreen(
                onBack = {
                    navController.navigateUp()
                },
                onSave = { employee ->
                    viewModel.addEmployee(employee)
                    navController.popBackStack("employee_list", inclusive = false)
                }
            )
        }

        composable("employee_detail/{employeeId}") { backStackEntry ->
            val employeeId = backStackEntry.arguments?.getString("employeeId")?.toIntOrNull() ?: 0
            val selectedEmployee = viewModel.selectedEmployee

            if (selectedEmployee != null && selectedEmployee.id == employeeId) {
                EmployeeDetailScreen(
                    employee = selectedEmployee,
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