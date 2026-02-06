package com.kaplan.employeehub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import com.kaplan.employeehub.ui.navigation.EmployeeNavHost
import com.kaplan.employeehub.ui.theme.EmployeeHubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EmployeeHubTheme {
                EmployeeNavHost(modifier = Modifier, appContext = applicationContext)
            }
        }
    }
}
