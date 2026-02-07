package com.kaplan.emplohandler.data

import kotlinx.coroutines.flow.Flow

class EmployeeRepository(private val employeeDao: EmployeeDao) {
    val allEmployees: Flow<List<Employee>> = employeeDao.getAllEmployees()

    suspend fun getEmployeeById(id: Int): Employee? {
        return employeeDao.getEmployeeById(id)
    }

    suspend fun insertEmployee(employee: Employee): Long {
        return employeeDao.insertEmployee(employee)
    }

    suspend fun updateEmployee(employee: Employee) {
        employeeDao.updateEmployee(employee)
    }

    suspend fun deleteEmployee(employee: Employee) {
        employeeDao.deleteEmployee(employee)
    }

    suspend fun deleteEmployeeById(id: Int) {
        employeeDao.deleteEmployeeById(id)
    }
}
