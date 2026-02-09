package com.kaplan.employeehub.ui.navigation

// Simplified routes holder to avoid requiring navigation-compose in this environment.
object Routes {
    const val LIST = "employee_list"
    const val ADD = "employee_add"
    const val EDIT = "employee_edit"
    const val ARG_EMPLOYEE_ID = "employeeId"
    const val EDIT_WITH_ID = "$EDIT/{$ARG_EMPLOYEE_ID}"
}
