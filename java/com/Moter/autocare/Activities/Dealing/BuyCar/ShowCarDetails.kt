package com.Moter.autocare.Activities.Dealing.BuyCar

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.Moter.autocare.Adapter.ShowImageAdapter
import com.Moter.autocare.Constants.Constants
import com.Moter.autocare.Constants.CustomDialog
import com.Moter.autocare.Constants.WrapContentLinearLayoutManager
import com.Moter.autocare.Model.ImageModel
import com.Moter.autocare.Model.SellCarModel
import com.Moter.autocare.databinding.ActivityShowCarDetailsBinding
import com.bumptech.glide.Glide

class ShowCarDetails : AppCompatActivity() {

    private lateinit var binding : ActivityShowCarDetailsBinding
    private lateinit var database : FirebaseDatabase
    private lateinit var auth : FirebaseAuth
    private lateinit var adapter : ShowImageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowCarDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        val loading = CustomDialog(this@ShowCarDetails)
        loading.show()

        binding.actionBar.activityName.text = "Car Details"
        binding.actionBar.backButton.setOnClickListener{
            finish()
        }
        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        val imageUrl = sharedPreferences.getString("imageUrl", "")
        Glide.with(this@ShowCarDetails).load(imageUrl).into(binding.actionBar.profilePic)

        val intent = intent
        val state = intent.getStringExtra("state").toString()
        val carId = intent.getStringExtra("carId").toString()

        database.reference.child("SellingCars").child(state).child(carId)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists())
                    {
                        val model = snapshot.getValue(SellCarModel::class.java)
                        if (model!=null)
                        {
                            loading.dismiss()
                            binding.carBrand.text = model.carBrand
                            binding.carName.text = model.carName
                            binding.year.text = model.year
                            binding.ownerType.text = model.ownerType
                            binding.variant.text = model.fuelType
                            binding.kilometerDriven.text = model.kilometerDriven
                            binding.vechileNum.text = model.vehicleNo
                            binding.price.text = model.price
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Constants.error(this@ShowCarDetails,"Failed to get the Car details")
                    loading.dismiss()
                }

            })

        binding.contact.setOnClickListener {

        }

        binding.recView.layoutManager = WrapContentLinearLayoutManager(this@ShowCarDetails,WrapContentLinearLayoutManager.HORIZONTAL,false)
        val query  = database.reference.child("SellingCars").child(state).child(carId).child("Images")
        val options  = FirebaseRecyclerOptions.Builder<ImageModel>().setQuery(query,ImageModel::class.java).build()
        adapter = object : ShowImageAdapter(this@ShowCarDetails,options){

            override fun onDataChanged() {
                super.onDataChanged()
                loading.dismiss()
            }

            override fun onError(error: DatabaseError) {
                super.onError(error)
                loading.dismiss()
                Constants.error(this@ShowCarDetails,"Failed to get the images")
            }
        }

        binding.recView.adapter = adapter


        binding.contact.setOnClickListener {
            startActivity(Intent(this@ShowCarDetails,ContactActivity::class.java))
        }


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