package com.Moter.autocare.AdminActivities.Insurance.Update

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.Moter.autocare.Constants.Constants
import com.Moter.autocare.Constants.CustomDialog
import com.Moter.autocare.Model.AddInsuranceModel
import com.Moter.autocare.R
import com.Moter.autocare.databinding.ActivityUpdateInsuranceDetaolBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UpdateInsuranceDetail : AppCompatActivity() {

    private lateinit var binding : ActivityUpdateInsuranceDetaolBinding
    private lateinit var database : FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateInsuranceDetaolBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()

        val loading = CustomDialog(this@UpdateInsuranceDetail)
        loading.show()

        val intent = intent
        val carBrandintent = intent.getStringExtra("carBrand").toString()
        val dealerId = intent.getStringExtra("dealerId").toString()

        binding.actionBar.activityName.text = "Update Insurance"
        binding.actionBar.backButton.setOnClickListener {
            finish()
        }

        database.reference.child("InsuranceDealers").child(carBrandintent).child(dealerId)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists())
                    {
                        val model = snapshot.getValue(AddInsuranceModel::class.java)
                        if (model!=null)
                        {
                            loading.dismiss()
                            binding.companyName.setText(model.companyName)
                            binding.regNo.setText(model.registerNo)
                            binding.regAddress.setText(model.registeredAddress)
                            binding.gstNo.setText(model.gstinNo)
                            binding.carModel.setText(model.carModel)
                            binding.standardPlan.setText(model.standardInsurance)
                            binding.premiumPlan.setText(model.premiumInsurance)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Constants.error(this@UpdateInsuranceDetail,"Error :${error.message}")
                    loading.dismiss()
                }

            })

        binding.update.setOnClickListener {

            val companyName = binding.companyName.text.toString()
            val regNo = binding.regNo.text.toString()
            val regAddress = binding.regAddress.text.toString()
            val gst = binding.gstNo.text.toString()
            val modelName = binding.carModel.text.toString()
            val standardPlan = binding.standardPlan.text.toString()
            val premiumPlan = binding.premiumPlan.text.toString()

            if (validateInput(companyName,regNo,regAddress,gst,modelName,standardPlan,premiumPlan))
            {

                val loading = CustomDialog(this@UpdateInsuranceDetail)
                loading.show()

                val map = HashMap<String,Any>()
                map.put("companyName",companyName)
                map.put("registerNo",regNo)
                map.put("registeredAddress",regAddress)
                map.put("gstinNo",gst)
                map.put("carModel",modelName)
                map.put("standardInsurance",standardPlan)
                map.put("premiumInsurance",premiumPlan)

                database.reference.child("InsuranceDealers").child(carBrandintent).child(dealerId).updateChildren(map)
                    .addOnCompleteListener{task->
                        if (task.isSuccessful)
                        {
                         Constants.success(this@UpdateInsuranceDetail,"Insurance Updated Successfully")
                            onBackPressedDispatcher.onBackPressed()
                         loading.dismiss()
                        }
                        else
                        {
                            Constants.error(this@UpdateInsuranceDetail,"Failed to Update the insurance")
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
        carModel: String,
        standardPlan: String,
        premiumPlan: String
    ) : Boolean {

        if (companyName.isEmpty())
        {
            Constants.error(this@UpdateInsuranceDetail,"Company Name Required")
            return false
        }
        if (regNo.isEmpty())
        {
            Constants.error(this@UpdateInsuranceDetail,"RegistrationNo Required")
            return false
        }
        if (regAddress.isEmpty())
        {
            Constants.error(this@UpdateInsuranceDetail,"Registration Address is required")
            return false
        }
        if (gstNo.isEmpty())
        {
            Constants.error(this@UpdateInsuranceDetail,"GSTNO is Required")
            return false
        }
        if (carModel.isEmpty())
        {
            Constants.error(this@UpdateInsuranceDetail,"Car Model Required")
            return false
        }
        if (standardPlan.isEmpty())
        {
            Constants.error(this@UpdateInsuranceDetail,"Standard Plan Required")
            return false
        }
        if (premiumPlan.isEmpty())
        {
            Constants.error(this@UpdateInsuranceDetail,"Premium Plan Required")
            return false
        }

        return true
    }

}