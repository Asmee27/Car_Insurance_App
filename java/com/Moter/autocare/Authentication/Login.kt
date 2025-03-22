package com.Moter.autocare.Authentication

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.Moter.autocare.AdminActivities.AdminMenuActivity
import com.Moter.autocare.Constants.Constants
import com.Moter.autocare.Constants.CustomDialog
import com.Moter.autocare.MainActivity
import com.Moter.autocare.Model.UserModel
import com.Moter.autocare.R
import com.Moter.autocare.databinding.ActivityLoginBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class Login : AppCompatActivity() {

    val EMAIL = "email"
    val SHAREDPREF = "sharedpref"
    val PASSWORD = "password"

    private var mail: String? = null
    private var pass: String? = null

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val dialog: CustomDialog = CustomDialog(this@Login)

        loadData()
        binding.etEmail.setText(mail)
        binding.etPassword.setText(pass)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                Constants.error(this@Login, "Please fill all the credentials");
                return@setOnClickListener
            }

            savedata(email, password)

            dialog.show()

            if (email.equals("admin") && password.equals("admin"))
            {
                dialog.dismiss()
                startActivity(Intent(this@Login,AdminMenuActivity::class.java))
                finish()
            }
            else{
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        database.reference.child("Users").child(auth.currentUser!!.uid).addValueEventListener(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists())
                                {
                                    val data = snapshot.getValue(UserModel::class.java)
                                    if (data!=null)
                                    {
                                        val sharedPreferences = getSharedPreferences("User",
                                            Context.MODE_PRIVATE)
                                        val editor = sharedPreferences.edit()
                                        editor.putString("imageUrl",data.profile_Pic)
                                        editor.apply()
                                        Constants.success(this@Login, "Login Successful")
                                        startActivity(Intent(this@Login,MainActivity::class.java))
                                        finish()
                                        dialog.dismiss()
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Constants.error(this@Login,"Failed to get the user data")
                            }
                        })
                    } else {
                        Constants.error(
                            this@Login,
                            "Failed to update token try again : " + task.exception!!.message
                        )
                        dialog.dismiss()
                    }

                }
            }
        }

        binding.tvForgotPassword.setOnClickListener {

            val loading: CustomDialog = CustomDialog(this@Login)

            val dialog: MaterialAlertDialogBuilder = Constants.dialog(
                this@Login,
                "Reset Password",
                "Enter Your Registered Email To reset the Password"
            )
            val mail: EditText = EditText(this@Login)
            dialog.setView(mail)
            dialog.setIcon(R.mipmap.logo)


            val yes = object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    loading.show()
                    val email: String = mail.text.toString()

                    if (email.isEmpty()) {
                        Constants.error(this@Login, "Please enter your register email")
                        loading.dismiss()
                        return
                    } else {
                        resetpassword(email, loading)
                    }
                }

            }

            val cancel = object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.cancel()
                }

            }

            dialog.setPositiveButton("YES", yes)
            dialog.setNeutralButton("CANCEL", cancel)

            val alertdialog: AlertDialog = dialog.create()
            alertdialog.show()

        }


        binding.btnSignup.setOnClickListener {
            startActivity(Intent(this@Login, Register::class.java))
            finish()
        }

    }

    private fun loadData() {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences(SHAREDPREF, MODE_PRIVATE)
        mail = sharedPreferences.getString(EMAIL, "")
        pass = sharedPreferences.getString(PASSWORD, "")
    }

    private fun savedata(email: String, password: String) {
        val sharedPreferences = getSharedPreferences(SHAREDPREF, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(EMAIL, email)
        editor.putString(PASSWORD, password)
        editor.apply()
    }

    private fun resetpassword(email: String, loading: CustomDialog) {

        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Constants.success(this@Login, "Email has sent to your registered email address")
                loading.dismiss()

            } else {
                Constants.error(
                    this@Login,
                    "Failed to send email : " + task.exception!!.message
                );
                loading.dismiss()
            }
        }

    }
}
