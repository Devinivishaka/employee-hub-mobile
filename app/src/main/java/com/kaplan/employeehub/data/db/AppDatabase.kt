package com.kaplan.employeehub.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kaplan.employeehub.data.dao.EmployeeDao
import com.kaplan.employeehub.data.entity.Employee
import com.kaplan.employeehub.utils.DatabaseException
import com.kaplan.employeehub.utils.ErrorHandler

@Database(entities = [Employee::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun employeeDao(): EmployeeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                try {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "employee_hub_db"
                    ).build()
                    INSTANCE = instance
                    instance
                } catch (e: Exception) {
                    ErrorHandler.logError("AppDatabase", "Failed to create database instance", e)
                    throw DatabaseException("Failed to initialize database", e)
                }
            }
        }
    }
}
