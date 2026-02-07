package com.kaplan.emplohandler.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employees")
data class Employee(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    val designation: String,
    val salary: Double
) {
    val initials: String
        get() = "${firstName.firstOrNull()?.uppercaseChar() ?: ""}${lastName.firstOrNull()?.uppercaseChar() ?: ""}"
}
