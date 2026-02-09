package com.kaplan.employeehub.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kaplan.employeehub.data.db.AppDatabase
import com.kaplan.employeehub.data.repository.EmployeeRepository
import com.kaplan.employeehub.data.repository.RoomEmployeeRepository
import com.kaplan.employeehub.ui.screens.EmployeeAddScreen
import com.kaplan.employeehub.ui.screens.EmployeeEditScreen
import com.kaplan.employeehub.ui.screens.EmployeeListScreen
import com.kaplan.employeehub.ui.viewmodel.EmployeeEditViewModel
import com.kaplan.employeehub.ui.viewmodel.EmployeeListViewModel

private class EmployeeListViewModelFactory(
    private val repository: EmployeeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmployeeListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EmployeeListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

private class EmployeeEditViewModelFactory(
    private val repository: EmployeeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmployeeEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EmployeeEditViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun EmployeeNavHost(
    modifier: Modifier = Modifier,
    appContext: Context
) {
    val navController = rememberNavController()
    val database = remember(appContext) { AppDatabase.getInstance(appContext) }
    val repository = remember(database) { RoomEmployeeRepository(database.employeeDao()) }
    val listFactory = remember(repository) { EmployeeListViewModelFactory(repository) }
    val editFactory = remember(repository) { EmployeeEditViewModelFactory(repository) }

    NavHost(
        navController = navController,
        startDestination = Routes.LIST,
        modifier = modifier
    ) {
        composable(Routes.LIST) {
            val viewModel: EmployeeListViewModel = viewModel(factory = listFactory)
            EmployeeListScreen(
                viewModel = viewModel,
                onAdd = { navController.navigate(Routes.ADD) },
                onEdit = { employeeId -> navController.navigate("${Routes.EDIT}/$employeeId") }
            )
        }
        composable(Routes.ADD) {
            val viewModel: EmployeeEditViewModel = viewModel(factory = editFactory)
            EmployeeAddScreen(
                viewModel = viewModel,
                onSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.EDIT_WITH_ID,
            arguments = listOf(navArgument(Routes.ARG_EMPLOYEE_ID) { type = NavType.LongType })
        ) { backStackEntry ->
            val viewModel: EmployeeEditViewModel = viewModel(factory = editFactory)
            val employeeId = backStackEntry.arguments?.getLong(Routes.ARG_EMPLOYEE_ID) ?: 0L

            LaunchedEffect(employeeId) {
                if (employeeId != 0L) {
                    viewModel.load(employeeId)
                }
            }

            EmployeeEditScreen(
                viewModel = viewModel,
                employeeId = employeeId,
                onSaved = { navController.popBackStack() },
                onDeleted = {
                    viewModel.delete(employeeId) {
                        navController.popBackStack()
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}
