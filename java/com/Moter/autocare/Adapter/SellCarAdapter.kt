package com.Moter.autocare.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.Moter.autocare.Activities.Dealing.BuyCar.ShowCarDetails
import com.Moter.autocare.Constants.Constants
import com.Moter.autocare.Model.SellCarModel
import com.Moter.autocare.Model.UserModel
import com.Moter.autocare.databinding.ShowCarLayoutBinding

open class SellCarAdapter(val context : Context, options: FirebaseRecyclerOptions<SellCarModel>) : FirebaseRecyclerAdapter<SellCarModel, SellCarAdapter.onViewHolder>(
    options
) {

    private val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    class onViewHolder(val binding : ShowCarLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): onViewHolder {
        val view = ShowCarLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return onViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: onViewHolder, position: Int, model: SellCarModel) {
        holder.binding.carBrand.text = model.carBrand
        holder.binding.carModel.text = "${model.carName} ( ${model.year} )"
        database.reference.child("Users").child(model.userId.toString()).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    val data = snapshot.getValue(UserModel::class.java)
                    if (data!=null)
                    {
                        holder.binding.ownerName.text = data.name
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Constants.error(context,"Failed to get the Owner Info")
            }

        })

        holder.binding.price.text = model.price
        holder.binding.layout.setOnClickListener {
            context.startActivity(Intent(context,ShowCarDetails::class.java)
                .putExtra("state",model.registrationState)
                .putExtra("carId",model.carId)
            )
        }

    }
}