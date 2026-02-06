package com.kaplan.employeehub.data.repository

import com.kaplan.employeehub.data.dao.EmployeeDao
import com.kaplan.employeehub.data.entity.Employee
import kotlinx.coroutines.flow.Flow
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
    override fun getById(id: Long): Flow<Employee?> = dao.getById(id)

    override suspend fun insert(employee: Employee): Long = withContext(Dispatchers.IO) {
        dao.insert(employee)
    }

    override suspend fun update(employee: Employee) = withContext(Dispatchers.IO) {
        dao.update(employee)
    }

    override suspend fun delete(employee: Employee) = withContext(Dispatchers.IO) {
        dao.delete(employee)
    }
}
