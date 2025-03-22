package com.Moter.autocare.Adapter


import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.Moter.autocare.Activities.CarInsurance.PaymentIntegration
import com.Moter.autocare.AdminActivities.Insurance.Update.UpdateInsuranceDetail
import com.Moter.autocare.Constants.Constants
import com.Moter.autocare.Model.AddInsuranceModel
import com.Moter.autocare.R
import com.google.firebase.database.FirebaseDatabase

open class UpdateInsuranceAdapter(val context : Context , options: FirebaseRecyclerOptions<AddInsuranceModel> , val id : Int) :
    FirebaseRecyclerAdapter<AddInsuranceModel, UpdateInsuranceAdapter.onViewHolder>(options) {

        private  var database : FirebaseDatabase = FirebaseDatabase.getInstance()

    class onViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val companyName : MaterialTextView = itemView.findViewById(R.id.companyName)
        val carName : MaterialTextView = itemView.findViewById(R.id.carName)
        val carModel : MaterialTextView = itemView.findViewById(R.id.carModel)
        val layout : MaterialCardView = itemView.findViewById(R.id.layout)
        val update : MaterialButton = itemView.findViewById(R.id.update)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): onViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.update_insurance_layout_file , parent , false)
        return onViewHolder(view)
    }

    override fun onBindViewHolder(holder: onViewHolder, position: Int, model: AddInsuranceModel) {

        holder.companyName.text = model.companyName
        holder.carName.text = model.carName
        holder.carModel.text = model.carModel


        if (id==1)
        {
            holder.update.setText("Delete")
            holder.update.setOnClickListener {
                showDialog(database,model.carName.toString(),model.dealerId.toString())
            }
        }

        if (id == 0)
        {
            holder.update.setText("Update")
            holder.update.setOnClickListener {
                context.startActivity(Intent(context,UpdateInsuranceDetail::class.java)
                    .putExtra("carBrand",model.carName)
                    .putExtra("dealerId",model.dealerId)
                )
            }
        }



    }

    fun showDialog(database : FirebaseDatabase , carbrand : String , dealerId : String ){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Insurance")
            .setMessage("Are You Sure You Want To Delete the Insurance")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                database.reference.child("InsuranceDealers").child(carbrand).child(dealerId)
                    .removeValue().addOnCompleteListener{
                        if (it.isSuccessful)
                        {
                            Constants.success(context,"Insurance  Deleted Successfuly")
                        }
                        else
                        {
                            Constants.error(context,"Failed to Delete the Insurance")
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