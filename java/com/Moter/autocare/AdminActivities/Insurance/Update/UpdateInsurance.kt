package com.Moter.autocare.AdminActivities.Insurance.Update

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.Moter.autocare.Adapter.InsuranceDealerAdapter
import com.Moter.autocare.Adapter.UpdateInsuranceAdapter
import com.Moter.autocare.Constants.Constants
import com.Moter.autocare.Constants.CustomDialog
import com.Moter.autocare.Constants.WrapContentLinearLayoutManager
import com.Moter.autocare.Model.AddInsuranceModel
import com.Moter.autocare.databinding.ActivityUpdateInsuranceBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class UpdateInsurance: AppCompatActivity() {

    private lateinit var binding: ActivityUpdateInsuranceBinding
    private lateinit var adapter : UpdateInsuranceAdapter
    private lateinit var database : FirebaseDatabase
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateInsuranceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val loading = CustomDialog(this@UpdateInsurance)
        loading.show()

        val intent = intent
        val carBrand = intent.getStringExtra("carBrand").toString()

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        binding.actionBar.activityName.text = "Select the Insurance Dealer"
        binding.actionBar.backButton.setOnClickListener{
            finish()
        }


        binding.recView.layoutManager = WrapContentLinearLayoutManager(this@UpdateInsurance)
        val query = database.reference.child("InsuranceDealers").child(carBrand)
        val options = FirebaseRecyclerOptions.Builder<AddInsuranceModel>().setQuery(query,
            AddInsuranceModel::class.java).build()

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val search = query
                process_search(search, loading , carBrand)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                process_search(newText, loading , carBrand)
                return false
            }
        })
        adapter = object : UpdateInsuranceAdapter(this@UpdateInsurance,options , 0)
        {
            override fun onDataChanged() {
                super.onDataChanged()
                loading.dismiss()

            }

            override fun onError(error: DatabaseError) {
                super.onError(error)
                loading.dismiss()
                Constants.error(this@UpdateInsurance,"Error : ${error.message}")
            }
        }

        binding.recView.adapter = adapter
    }

    private fun process_search(search: String, pd: CustomDialog, carbrand : String) {
        val searchQuery = database.getReference().child("InsuranceDealers").child(
            carbrand
        ).orderByChild("companyName").startAt(search).endAt(search + "\uf8ff")
        val searchOptions: FirebaseRecyclerOptions<AddInsuranceModel> =
            FirebaseRecyclerOptions.Builder<AddInsuranceModel>()
                .setQuery(searchQuery, AddInsuranceModel::class.java)
                .build()
        adapter = object : UpdateInsuranceAdapter(this@UpdateInsurance,searchOptions,0) {
            override fun onDataChanged() {
                super.onDataChanged()
                pd.dismiss()
            }

            override fun onError(error: DatabaseError) {
                super.onError(error)
                pd.dismiss()
            }
        }
        adapter.startListening()
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