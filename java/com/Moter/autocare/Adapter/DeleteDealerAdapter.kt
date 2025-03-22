package com.Moter.autocare.Adapter


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.Moter.autocare.Constants.Constants
import com.Moter.autocare.Model.SellCarModel
import com.Moter.autocare.Model.UserModel
import com.Moter.autocare.databinding.ShowCarLayoutBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


open class DeleteDealerAdapter(val context : Context, options: FirebaseRecyclerOptions<SellCarModel> ) : FirebaseRecyclerAdapter<SellCarModel, DeleteDealerAdapter.onViewHolder>(
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
        holder.binding.layout.setOnLongClickListener(object : OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                showDialog(database , model.carId!!, model.registrationState!!)
                return true
            }

        })

    }

    fun showDialog(database : FirebaseDatabase , carId : String , state : String){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Car")
            .setMessage("Are You Sure You Want To Delete the Car")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                    database.reference.child("SellingCars").child(state).child(carId)
                        .removeValue().addOnCompleteListener{
                            if (it.isSuccessful)
                            {
                                Constants.success(context,"Car Deleted Successfuly")
                            }
                            else
                            {
                                Constants.error(context,"Failed to Delete the Car")
                            }
                        }
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                dialog.dismiss()
            })
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}