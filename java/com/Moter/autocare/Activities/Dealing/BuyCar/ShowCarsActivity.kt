package com.Moter.autocare.Activities.Dealing.BuyCar

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.Moter.autocare.Adapter.SellCarAdapter
import com.Moter.autocare.Constants.Constants
import com.Moter.autocare.Constants.CustomDialog
import com.Moter.autocare.Constants.WrapContentLinearLayoutManager
import com.Moter.autocare.Model.SellCarModel
import com.Moter.autocare.databinding.ActivityShowCarsBinding
import com.bumptech.glide.Glide

class ShowCarsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityShowCarsBinding
    private lateinit var database : FirebaseDatabase
    private lateinit var  adapter : SellCarAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowCarsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()

        val loading = CustomDialog(this@ShowCarsActivity)
        loading.show()

        binding.actionBar.activityName.text = "Select the car"
        binding.actionBar.backButton.setOnClickListener{
            finish()
        }
        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        val imageUrl = sharedPreferences.getString("imageUrl", "")
        Glide.with(this@ShowCarsActivity).load(imageUrl).into(binding.actionBar.profilePic)

        val intent = intent
        val state = intent.getStringExtra("state").toString()

        binding.recView.layoutManager = WrapContentLinearLayoutManager(this@ShowCarsActivity)
        val query = database.reference.child("SellingCars").child(state)
        val options = FirebaseRecyclerOptions.Builder<SellCarModel>().setQuery(query,SellCarModel::class.java).build()
        adapter = object : SellCarAdapter(this@ShowCarsActivity,options){

            override fun onDataChanged() {
                super.onDataChanged()
                loading.dismiss()
            }

            override fun onError(error: DatabaseError) {
                super.onError(error)
                loading.dismiss()
                Constants.error(this@ShowCarsActivity,"Failed to get the Cars")
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