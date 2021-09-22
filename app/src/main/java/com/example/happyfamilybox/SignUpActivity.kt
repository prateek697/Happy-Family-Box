package com.example.happyfamilybox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import android.text.TextUtils
import android.util.Patterns
import android.view.View.VISIBLE
import android.widget.*
import com.example.happyfamilybox.models.User
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var storedVerificationId:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val emailText: TextInputLayout = findViewById(R.id.email_text)
        val nameText: TextInputLayout = findViewById(R.id.name_text)
        val passwordText: TextInputLayout = findViewById(R.id.password_text)
        val confirmPasswordText: TextInputLayout = findViewById(R.id.confirm_password_text)
        val mobile_no: TextInputLayout = findViewById(R.id.mobile)

        val signUpButton: Button = findViewById(R.id.signup_button)
        val goToLogin: TextView = findViewById(R.id.go_to_login)

        val signUpProgress: ProgressBar = findViewById(R.id.sign_up_progress)

        auth=FirebaseAuth.getInstance()

        signUpButton.setOnClickListener{

            val email = emailText.editText?.text.toString()
            val name = nameText.editText?.text.toString()
            val password = passwordText.editText?.text.toString()
            val confirmPassword = confirmPasswordText.editText?.text.toString()
            val mobile_number = mobile_no.editText?.text.toString()

            emailText.error = null
            nameText.error = null
            passwordText.error = null
            confirmPasswordText.error = null

            if (TextUtils.isEmpty(email)) {
                emailText.error = "Email is required"
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailText.error = "Please enter a valid email address"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(name)) {
                nameText.error = "Name is required"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(mobile_number)) {
                nameText.error = "Mobile_Number is required"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                passwordText.error = "Password is required"
                return@setOnClickListener
            }

            if (!password.matches(passwordRegex)) {
                passwordText.error = "Password should contain minimum eight characters, at least one uppercase letter, one lowercase letter and one number"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(confirmPassword)) {
                confirmPasswordText.error = "Confirm password is required"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                confirmPasswordText.error = "Passwords do not match"
                return@setOnClickListener
            }

            // radio button
            val facultyRadio: RadioButton = findViewById(R.id.facultyRadio)
            var type:String
            if(facultyRadio.isChecked) type="Farmer"
            else type="Citizen"

            signUpProgress.visibility = VISIBLE




            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Successfully Registered", Toast.LENGTH_LONG).show()

                        verifyOTP()

                        val user = User(
                            auth.currentUser?.uid!!,
                            name,
                            mobile_number,
                            email,
                            type
                        )
                        val firestore = FirebaseFirestore.getInstance().collection("Users")
                        firestore.document(auth.currentUser?.uid!!).set(user)
                            .addOnCompleteListener { task2 ->
                                if (task2.isSuccessful) {
//                                    val intent = Intent(this, LoginActivity::class.java)
//                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show()
                                    Log.d("Error", task2.exception.toString())
                                }
                            }

                        signUpProgress.visibility = GONE
                    } else {
                        signUpProgress.visibility = GONE
                        Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
                        Log.d("Error", task.exception.toString())
                    }
                }
        }

        // Callback function for Phone Auth
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                startActivity(Intent(applicationContext, LoginActivity::class.java))
                finish()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(applicationContext,e.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                Log.d("TAG","onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token

                var intent = Intent(applicationContext,verify_mobile::class.java)
                intent.putExtra("storedVerificationId",storedVerificationId)
                startActivity(intent)
            }
        }

        goToLogin.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }

    }

    private fun verifyOTP() {
        val mobileNumber: TextInputLayout = findViewById(R.id.mobile)
        var number=mobileNumber.editText?.text.toString().trim()

        if(!number.isEmpty()){
            number="+91"+number
            sendVerificationcode (number)
        }else{
            Toast.makeText(this,"Enter mobile number",Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendVerificationcode(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}
