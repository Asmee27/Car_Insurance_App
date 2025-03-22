package com.Moter.autocare.Activities.CarInsurance

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import com.Moter.autocare.Adapter.BuyInsuranceAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.Moter.autocare.Adapter.InsuranceDealerAdapter
import com.Moter.autocare.Constants.Constants
import com.Moter.autocare.Constants.CustomDialog
import com.Moter.autocare.Constants.WrapContentLinearLayoutManager
import com.Moter.autocare.Model.AddInsuranceModel
import com.Moter.autocare.Model.CarRegisterModel
import com.Moter.autocare.databinding.ActivityInsuranceDealerBinding
import com.bumptech.glide.Glide

class InsuranceDealerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInsuranceDealerBinding
    private lateinit var adapter : InsuranceDealerAdapter
    private lateinit var database : FirebaseDatabase
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsuranceDealerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val loading = CustomDialog(this@InsuranceDealerActivity)
        loading.show()

        val intent = intent
        val carBrand = intent.getStringExtra("carBrand").toString()
        val userId = intent.getStringExtra("userId").toString()

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        binding.actionBar.activityName.text = "Select the Insurance Dealer"
        binding.actionBar.backButton.setOnClickListener{
            finish()
        }

        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        val imageUrl = sharedPreferences.getString("imageUrl", "")
        Glide.with(this@InsuranceDealerActivity).load(imageUrl).into(binding.actionBar.profilePic)

        binding.recView.layoutManager = WrapContentLinearLayoutManager(this@InsuranceDealerActivity)
        val query = database.reference.child("InsuranceDealers").child(carBrand)
        val options = FirebaseRecyclerOptions.Builder<AddInsuranceModel>().setQuery(query,
            AddInsuranceModel::class.java).build()

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val search = query
                process_search(search, loading , carBrand , userId)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                process_search(newText, loading , carBrand , userId)
                return false
            }
        })
        adapter = object : InsuranceDealerAdapter(this@InsuranceDealerActivity,options , userId)
        {
            override fun onDataChanged() {
                super.onDataChanged()
                loading.dismiss()

            }

            override fun onError(error: DatabaseError) {
                super.onError(error)
                loading.dismiss()
                Constants.error(this@InsuranceDealerActivity,"Error : ${error.message}")
            }
        }

        binding.recView.adapter = adapter
    }

    private fun process_search(search: String, pd: CustomDialog , carbrand : String , userId : String) {
        val searchQuery = database.getReference().child("InsuranceDealers").child(
            carbrand
        ).orderByChild("carName").startAt(search).endAt(search + "\uf8ff")
        val searchOptions: FirebaseRecyclerOptions<AddInsuranceModel> =
            FirebaseRecyclerOptions.Builder<AddInsuranceModel>()
                .setQuery(searchQuery, AddInsuranceModel::class.java)
                .build()
        adapter = object : InsuranceDealerAdapter(this@InsuranceDealerActivity,searchOptions, userId) {
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