package com.example.androidfinanceapp.ui

sealed class Screens(val route: String){
    object LoginScreen : Screens("login_route")
    object SignupScreen : Screens("signup_route")
    object OverviewScreen: Screens("Overview_route")
}