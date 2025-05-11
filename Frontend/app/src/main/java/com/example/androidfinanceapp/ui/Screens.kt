package com.example.androidfinanceapp.ui

sealed class Screens(val route: String){
    object LoginScreen : Screens("login_route")
    object SignupScreen : Screens("signup_route")
    object OverviewScreen: Screens("overview_route")
    object IncomeAndExpenseScreen: Screens("incomeAndExpense_route")
    object TargetScreen: Screens("target_route")
    object AssetStatisticScreen: Screens("asset_statistic_route")
    object AssetManagementScreen: Screens("asset_management_route")
}

