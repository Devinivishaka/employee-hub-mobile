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

/**
 * Interface defining repository operations for Employee data access layer.
 * Abstracts database operations for better testability and separation of concerns.
 */
interface EmployeeRepository {
    /** Retrieves all employees as a Flow */
    fun getAll(): Flow<List<Employee>>

    /** Retrieves a single employee by ID as a Flow */
    fun getById(id: Long): Flow<Employee?>

    /** Inserts a new employee and returns the generated ID */
    suspend fun insert(employee: Employee): Long

    /** Updates an existing employee */
    suspend fun update(employee: Employee)

    /** Deletes an employee */
    suspend fun delete(employee: Employee)
}

/**
 * Room database implementation of EmployeeRepository.
 * Handles all database operations with comprehensive error handling and logging.
 */
class RoomEmployeeRepository(private val dao: EmployeeDao) : EmployeeRepository {

    /**
     * Retrieves all employees from the database.
     * Includes error handling for query failures.
     */
    override fun getAll(): Flow<List<Employee>> = dao.getAll()
        .map { it }
        .catch { exception ->
            ErrorHandler.logError("RoomEmployeeRepository", "Failed to fetch employees", exception as Exception)
            throw DatabaseException("Failed to fetch employees", exception)
        }

    /**
     * Retrieves a specific employee by ID.
     * Includes error handling for query failures.
     */
    override fun getById(id: Long): Flow<Employee?> = dao.getById(id)
        .map { it }
        .catch { exception ->
            ErrorHandler.logError("RoomEmployeeRepository", "Failed to fetch employee with id: $id", exception as Exception)
            throw DatabaseException("Failed to fetch employee", exception)
        }

    /**
     * Inserts a new employee into the database.
     * Executes on IO dispatcher and wraps exceptions in DatabaseException.
     *
     * @return The generated ID of the inserted employee
     * @throws DatabaseException if insertion fails
     */
    override suspend fun insert(employee: Employee): Long = withContext(Dispatchers.IO) {
        try {
            dao.insert(employee)
        } catch (e: Exception) {
            ErrorHandler.logError("RoomEmployeeRepository", "Failed to insert employee", e)
            throw DatabaseException("Failed to insert employee", e)
        }
    }

    /**
     * Updates an existing employee in the database.
     * Executes on IO dispatcher and wraps exceptions in DatabaseException.
     *
     * @throws DatabaseException if update fails
     */
    override suspend fun update(employee: Employee) = withContext(Dispatchers.IO) {
        try {
            dao.update(employee)
        } catch (e: Exception) {
            ErrorHandler.logError("RoomEmployeeRepository", "Failed to update employee", e)
            throw DatabaseException("Failed to update employee", e)
        }
    }

    /**
     * Deletes an employee from the database.
     * Executes on IO dispatcher and wraps exceptions in DatabaseException.
     *
     * @throws DatabaseException if deletion fails
     */
    override suspend fun delete(employee: Employee) = withContext(Dispatchers.IO) {
        try {
            dao.delete(employee)
        } catch (e: Exception) {
            ErrorHandler.logError("RoomEmployeeRepository", "Failed to delete employee", e)
            throw DatabaseException("Failed to delete employee", e)
        }
    }
}
