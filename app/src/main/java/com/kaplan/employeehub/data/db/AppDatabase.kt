package com.kaplan.employeehub.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kaplan.employeehub.data.dao.EmployeeDao
import com.kaplan.employeehub.data.entity.Employee
import com.kaplan.employeehub.utils.DatabaseException
import com.kaplan.employeehub.utils.ErrorHandler

/**
 * Room database class for Employee Hub application.
 * Manages SQLite database creation and provides DAO access.
 * Uses singleton pattern to ensure only one database instance exists.
 */
@Database(entities = [Employee::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    /** Returns the Employee Data Access Object */
    abstract fun employeeDao(): EmployeeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Gets or creates the database instance using singleton pattern.
         * Thread-safe initialization with synchronized block.
         *
         * @param context Application context for database creation
         * @return The singleton AppDatabase instance
         * @throws DatabaseException if database initialization fails
         */
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
