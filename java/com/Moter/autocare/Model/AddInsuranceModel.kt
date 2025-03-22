package com.Moter.autocare.Model

data class AddInsuranceModel(
    val dealerId : String?="",
    val companyName : String?="",
    val registerNo : String?="",
    val registeredAddress : String?="" ,
    val gstinNo : String?="",
    val carName : String?="",
    val carModel : String?="",
    val standardInsurance : String?="",
    val premiumInsurance : String?="",
)
