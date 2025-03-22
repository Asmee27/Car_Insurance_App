package com.Moter.autocare.AdminActivities.Insurance.Delete

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.Moter.autocare.Adapter.UpdateInsuranceAdapter
import com.Moter.autocare.Constants.Constants
import com.Moter.autocare.Constants.CustomDialog
import com.Moter.autocare.Constants.WrapContentLinearLayoutManager
import com.Moter.autocare.Model.AddInsuranceModel
import com.Moter.autocare.databinding.ActivityDeleteInsuranceBinding
import com.Moter.autocare.databinding.ActivityUpdateInsuranceBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class DeleteInsurance: AppCompatActivity() {

    private lateinit var binding: ActivityDeleteInsuranceBinding
    private lateinit var adapter : UpdateInsuranceAdapter
    private lateinit var database : FirebaseDatabase
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteInsuranceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val loading = CustomDialog(this@DeleteInsurance)
        loading.show()

        val intent = intent
        val carBrand = intent.getStringExtra("carBrand").toString()

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        binding.actionBar.activityName.text = "Select the Insurance Dealer"
        binding.actionBar.backButton.setOnClickListener{
            finish()
        }


        binding.recView.layoutManager = WrapContentLinearLayoutManager(this@DeleteInsurance)
        val query = database.reference.child("InsuranceDealers").child(carBrand)
        val options = FirebaseRecyclerOptions.Builder<AddInsuranceModel>().setQuery(query,
            AddInsuranceModel::class.java).build()


        adapter = object : UpdateInsuranceAdapter(this@DeleteInsurance,options , 1)
        {
            override fun onDataChanged() {
                super.onDataChanged()
                loading.dismiss()

            }

            override fun onError(error: DatabaseError) {
                super.onError(error)
                loading.dismiss()
                Constants.error(this@DeleteInsurance,"Error : ${error.message}")
            }
        }

        binding.recView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}