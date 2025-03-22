package com.Moter.autocare.Activities.CarInsurance

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.Moter.autocare.Constants.Constants
import com.Moter.autocare.Constants.CustomDialog
import com.Moter.autocare.Model.AddInsuranceModel
import com.Moter.autocare.Model.UserModel
import com.Moter.autocare.R
import com.Moter.autocare.databinding.ActivityPaymentIntegrationBinding
import com.bumptech.glide.Glide
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class PaymentIntegration : AppCompatActivity() , PaymentResultListener {

    private lateinit var binding : ActivityPaymentIntegrationBinding
    private lateinit var database : FirebaseDatabase
    private var number : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentIntegrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()

        val loading = CustomDialog(this@PaymentIntegration)
        loading.show()

        binding.actionBar.activityName.text = "Buy Insurance"
        binding.actionBar.backButton.setOnClickListener{
            finish()
        }
        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        val imageUrl = sharedPreferences.getString("imageUrl", "")
        Glide.with(this@PaymentIntegration).load(imageUrl).into(binding.actionBar.profilePic)

        val intent = intent
        val dealerId = intent.getStringExtra("dealerId").toString()
        val carBrand = intent.getStringExtra("carBrand").toString()
        val planName = intent.getStringExtra("planName").toString()
        val amount = intent.getStringExtra("amount").toString()
        val userId = intent.getStringExtra("userId").toString()
        val updatedPrice = amount+"00"

        database.reference.child("Users").child(userId).addValueEventListener(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists())
                    {
                        val model = snapshot.getValue(UserModel::class.java)
                        if (model!=null)
                        {
                            number = model.phone.toString()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Constants.error(this@PaymentIntegration,"Failed to get the User Details")
                }

            }
        )

        database.reference.child("InsuranceDealers").child(carBrand).child(dealerId).addValueEventListener(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists())
                    {
                        val model = snapshot.getValue(AddInsuranceModel::class.java)
                        if (model!=null)
                        {
                            loading.dismiss()
                            binding.companyName.text = model.companyName
                            binding.gstNo.text = model.gstinNo
                            binding.registerNo.text = model.registerNo
                            binding.registerAdd.text = model.registeredAddress
                            binding.carName.text = model.carName
                            binding.carModel.text = model.carModel
                            binding.dealerId.text = model.dealerId
                            binding.price.text = amount
                            binding. plan.text = planName
                            binding.buyInsurance.setOnClickListener {

                                makePayment(model.companyName.toString(),model.registeredAddress.toString(),updatedPrice)

                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    loading.dismiss()
                    Constants.error(this@PaymentIntegration,"Failed to get the data")
                }

            }
        )

    }

    private fun makePayment(companyName : String , address : String  , amount : String) {

        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name",companyName)
            options.put("description",address)
            options.put("theme.color", R.color.primary);
            options.put("currency","INR");
            options.put("amount",amount)
            co.open(this@PaymentIntegration,options)
        }catch (e: Exception){
            Constants.error(this@PaymentIntegration,"Error in payment : "+e.message)
            e.printStackTrace()
        }

    }

    override fun onPaymentSuccess(p0: String?) {

        val message  = "You have successfully Purchase Insurance for your vehicle ${binding.carName.text} ${binding.carModel.text} at price ${binding.price.text}"
        if (number!=null)
        {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(number, null, message, null, null)
            Toast.makeText(this, "Message sent successfully", Toast.LENGTH_SHORT).show()
        }

        Constants.success(this@PaymentIntegration,"Payment SuccessFull With PaymentId $p0")
        startActivity(Intent(this@PaymentIntegration,InvoiceActivity::class.java)
            .putExtra("companyName",binding.companyName.text.toString())
            .putExtra("paymentId",p0)
            .putExtra("amount",binding.price.text.toString())
            .putExtra("address",binding.registerAdd.text.toString())
        )
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Constants.error(this@PaymentIntegration,"Failed to proceed the payment please try after some time")
    }


}