package com.Moter.autocare.AdminActivities.Insurance.Add

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.google.firebase.database.FirebaseDatabase
import com.Moter.autocare.Constants.Constants
import com.Moter.autocare.Constants.CustomDialog
import com.Moter.autocare.Model.AddInsuranceModel
import com.Moter.autocare.R
import com.Moter.autocare.databinding.ActivityAddInsuranceBinding
import java.util.UUID

class AddInsuranceActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddInsuranceBinding
    private val dealerId = UUID.randomUUID().toString()
    private lateinit var database : FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddInsuranceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()

        binding.actionBar.activityName.text = "Add Insurance Dealer"
        binding.actionBar.backButton.setOnClickListener{
                finish()
        }

        val adapter = ArrayAdapter.createFromResource(this@AddInsuranceActivity  ,R.array.car_brands , android.R.layout.simple_spinner_item)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.carBrand.adapter = adapter

        binding.addInsurance.setOnClickListener {

            val companyName = binding.companyName.text.toString()
            val regNo = binding.regNo.text.toString()
            val regAddress = binding.regAddress.text.toString()
            val gstNo = binding.gstNo.text.toString()
            val carName = binding.carBrand.selectedItem.toString()
            val carModel = binding.carModel.text.toString()
            val standardPlan = binding.standardPlan.text.toString()
            val premiumPlan = binding.premiumPlan.text.toString()

           if (validateInput(companyName,regNo,regAddress,gstNo,carName,carModel,standardPlan,premiumPlan))
           {
               val loading = CustomDialog(this@AddInsuranceActivity)
               loading.show()
               val model = AddInsuranceModel(dealerId,companyName , regNo,regAddress,gstNo,carName,carModel,standardPlan,premiumPlan)
               database.reference.child("InsuranceDealers").child(carName).child(dealerId).setValue(model)
                   .addOnCompleteListener{upload->

                       if (upload.isSuccessful)
                       {
                           Constants.success(this@AddInsuranceActivity,"Insurance Has Successfully added")
                           onBackPressedDispatcher.onBackPressed()
                           loading.dismiss()
                       }
                       else
                       {
                           Constants.error(this@AddInsuranceActivity,"Failed to Add the Insurance ${upload.exception!!.message}")
                           loading.dismiss()
                       }
                   }
           }
        }
    }

    private fun validateInput(
        companyName: String,
        regNo: String,
        regAddress: String,
        gstNo: String,
        carName: String,
        carModel: String,
        standardPlan: String,
        premiumPlan: String
    ) : Boolean {

        if (companyName.isEmpty())
        {
            Constants.error(this@AddInsuranceActivity,"Company Name Required")
            return false
        }
        if (regNo.isEmpty())
        {
            Constants.error(this@AddInsuranceActivity,"RegistrationNo Required")
            return false
        }
        if (regAddress.isEmpty())
        {
            Constants.error(this@AddInsuranceActivity,"Registration Address is required")
            return false
        }
        if (gstNo.isEmpty())
        {
            Constants.error(this@AddInsuranceActivity,"GSTNO is Required")
            return false
        }
        if (carName.isEmpty())
        {
            Constants.error(this@AddInsuranceActivity,"Car Name Required")
            return false
        }
        if (carModel.isEmpty())
        {
            Constants.error(this@AddInsuranceActivity,"Car Model Required")
            return false
        }
        if (standardPlan.isEmpty())
        {
            Constants.error(this@AddInsuranceActivity,"Standard Plan Required")
            return false
        }
        if (premiumPlan.isEmpty())
        {
            Constants.error(this@AddInsuranceActivity,"Premium Plan Required")
            return false
        }

        return true
    }
}