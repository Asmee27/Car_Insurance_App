package com.Moter.autocare.Authentication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.Moter.autocare.Constants.Constants
import com.Moter.autocare.Constants.CustomDialog
import com.Moter.autocare.Model.UserModel
import com.Moter.autocare.databinding.ActivityRegisterBinding


class Register : AppCompatActivity() {

    private lateinit var binding : ActivityRegisterBinding
    private var logo : Uri? =null
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseDatabase
    private lateinit var storage : FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()


        binding.btnLogin.setOnClickListener{
            startActivity(Intent(this@Register,Login::class.java))
            finishAffinity()
        }

        binding.profilePic.setOnClickListener {

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            logoImage.launch(Intent.createChooser(intent, "Select Images"))

        }

        binding.btnSignup.setOnClickListener {

            val loading = CustomDialog(this@Register)

            val name = binding.etFullname.text.toString()
            val phone = binding.etPhoneno.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val address = binding.etAddress.text.toString()

            if (Validate(name,phone,email,password,address))
            {
                if (logo!=null)
                {
                    loading.show()
                    CreateUser(name,phone,email,password,address,loading, logo!!)
                }
                else
                {
                    Constants.error(this@Register,"Please select the profile image")
                    return@setOnClickListener
                }
            }
        }
    }

    private fun CreateUser(name: String, phone: String, email: String, password: String, address: String, loading: CustomDialog , logo : Uri) {

        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{account->
                if (account.isSuccessful)
                {
                    storeToDatabase(name,phone,email,password,address,loading,auth.currentUser!!.uid,logo)
                }
                else
                {
                    Constants.error(this@Register,"Failed to create the account ${account.exception!!.message}")
                    loading.dismiss()
                }
            }
    }

    private fun storeToDatabase(name: String, phone: String, email: String, password: String, address: String, loading: CustomDialog , userid : String,logo: Uri) {

        val storageReference  = storage.reference.child("Profile_Pic").child(auth.currentUser!!.uid)
        storageReference.putFile(logo)
            .addOnCompleteListener {upload->
                if (upload.isSuccessful)
                {
                    storageReference.downloadUrl.addOnSuccessListener {url->

                        val model = UserModel(url.toString(),name,phone,email, password, address,auth.currentUser!!.uid)
                        database.reference.child("Users").child(userid).setValue(model)
                            .addOnCompleteListener{
                                if (it.isSuccessful)
                                {
                                    Constants.success(this@Register,"User Details has successfully stored in database")
                                    startActivity(Intent(this@Register,Login::class.java))
                                    finish()
                                    loading.dismiss()
                                }
                                else
                                {
                                    Constants.error(this@Register,"Failed to store details in the database ${it.exception!!.message}")
                                    loading.dismiss()
                                }
                            }

                    }.addOnFailureListener{
                        Constants.error(this@Register,"Failed to get the Profile Url ${it.message}")
                        loading.dismiss()
                    }
                }
                else
                {
                    Constants.error(this@Register,"Failed to upload the profile Picture ${upload.exception!!.message}")
                    loading.dismiss()
                }
            }
    }

    private fun Validate(name: String, phone: String, email: String, password: String, address: String ) : Boolean {

        if (name.isEmpty())
        {
            binding.etFullname.error = "Name should not be empty"
            return false
        }
        if (phone.isEmpty())
        {
            binding.etPhoneno.error = "Phone No should not be empty"
            return false
        }
        if (email.isEmpty())
        {
            binding.etEmail.error = "Email should not be empty"
            return false
        }
        if (password.isEmpty())
        {
            binding.etPassword.error = "Password should not be empty"
            return false
        }
        if (address.isEmpty())
        {
            binding.etAddress.error = "Address should not be empty"
            return false
        }

        return true
    }

    private fun handleLogoImage(intent: Intent) {
        logo = intent.data
        if (logo!=null)
        {
            Glide.with(this@Register).load(logo).into(binding.profilePic)
        }
        else
        {
            Constants.error(this@Register,"No image selected")
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private val logoImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { intent ->
                handleLogoImage(intent)
            }
        }
    }
}