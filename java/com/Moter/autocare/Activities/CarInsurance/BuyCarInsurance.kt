package com.Moter.autocare.Activities.CarInsurance

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.Moter.autocare.Adapter.BuyInsuranceAdapter
import com.Moter.autocare.Constants.Constants
import com.Moter.autocare.Constants.CustomDialog
import com.Moter.autocare.Constants.WrapContentLinearLayoutManager
import com.Moter.autocare.Model.CarRegisterModel
import com.Moter.autocare.databinding.ActivityBuyCarInsyranceBinding
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.security.AccessController.getContext

class BuyCarInsurance : AppCompatActivity() {

    private lateinit var binding: ActivityBuyCarInsyranceBinding
    private lateinit var adapter : BuyInsuranceAdapter
    private lateinit var database : FirebaseDatabase
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyCarInsyranceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loading = CustomDialog(this@BuyCarInsurance)
        loading.show()

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        binding.actionBar.activityName.text = "Buy Insurance"
        binding.actionBar.backButton.setOnClickListener{
            finish()
        }
        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        val imageUrl = sharedPreferences.getString("imageUrl", "")
        Glide.with(this@BuyCarInsurance).load(imageUrl).into(binding.actionBar.profilePic)

        binding.recView.layoutManager = WrapContentLinearLayoutManager(this@BuyCarInsurance)
        val query = database.reference.child("RegisteredCars").child(auth.currentUser!!.uid)
        val options = FirebaseRecyclerOptions.Builder<CarRegisterModel>().setQuery(query,CarRegisterModel::class.java).build()

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val search = query
                process_search(search, loading)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                process_search(newText, loading)
                return false
            }
        })

        adapter = object : BuyInsuranceAdapter(this@BuyCarInsurance,options)
        {
            override fun onDataChanged() {
                super.onDataChanged()
                loading.dismiss()

            }

            override fun onError(error: DatabaseError) {
                super.onError(error)
                loading.dismiss()
                Constants.error(this@BuyCarInsurance,"Error : ${error.message}")
            }
        }

        binding.recView.adapter = adapter

    }


    private fun process_search(search: String, pd: CustomDialog) {
        val searchQuery = database.getReference().child("RegisteredCars").child(
            auth.currentUser!!.uid
        ).orderByChild("carBrand").startAt(search).endAt(search + "\uf8ff")
        val searchOptions: FirebaseRecyclerOptions<CarRegisterModel> =
            FirebaseRecyclerOptions.Builder<CarRegisterModel>()
                .setQuery(searchQuery, CarRegisterModel::class.java)
                .build()
        adapter = object : BuyInsuranceAdapter(this@BuyCarInsurance,searchOptions) {
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