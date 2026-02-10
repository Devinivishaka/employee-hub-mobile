package com.kaplan.employeehub.data.repository

import com.kaplan.employeehub.data.dao.EmployeeDao
import com.kaplan.employeehub.data.entity.Employee
import com.kaplan.employeehub.utils.DatabaseException
import com.kaplan.employeehub.utils.ErrorHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface EmployeeRepository {
    fun getAll(): Flow<List<Employee>>
    fun getById(id: Long): Flow<Employee?>
    suspend fun insert(employee: Employee): Long
    suspend fun update(employee: Employee)
    suspend fun delete(employee: Employee)
}

class RoomEmployeeRepository(private val dao: EmployeeDao) : EmployeeRepository {
    override fun getAll(): Flow<List<Employee>> = dao.getAll()
        .map { it }
        .catch { exception ->
            ErrorHandler.logError("RoomEmployeeRepository", "Failed to fetch employees", exception as Exception)
            throw DatabaseException("Failed to fetch employees", exception)
        }

    override fun getById(id: Long): Flow<Employee?> = dao.getById(id)
        .map { it }
        .catch { exception ->
            ErrorHandler.logError("RoomEmployeeRepository", "Failed to fetch employee with id: $id", exception as Exception)
            throw DatabaseException("Failed to fetch employee", exception)
        }

    override suspend fun insert(employee: Employee): Long = withContext(Dispatchers.IO) {
        try {
            dao.insert(employee)
        } catch (e: Exception) {
            ErrorHandler.logError("RoomEmployeeRepository", "Failed to insert employee", e)
            throw DatabaseException("Failed to insert employee", e)
        }
    }

    override suspend fun update(employee: Employee) = withContext(Dispatchers.IO) {
        try {
            dao.update(employee)
        } catch (e: Exception) {
            ErrorHandler.logError("RoomEmployeeRepository", "Failed to update employee", e)
            throw DatabaseException("Failed to update employee", e)
        }
    }

    override suspend fun delete(employee: Employee) = withContext(Dispatchers.IO) {
        try {
            dao.delete(employee)
        } catch (e: Exception) {
            ErrorHandler.logError("RoomEmployeeRepository", "Failed to delete employee", e)
            throw DatabaseException("Failed to delete employee", e)
        }
    }
}
