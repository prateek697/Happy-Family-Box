package com.example.happyfamilybox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.happyfamilybox.models.Crops
import com.example.happyfamilybox.models.Vegetable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddNewVegetableActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_vegetable)

        var uuid : UUID = UUID.randomUUID()
        var uuuuid : String = uuid.toString()

        var unique_id = intent.extras?.get("id_field") as String
        var emailId = intent.extras?.get("email") as String


        var id__ : TextView = findViewById(R.id.unique_ID)
        id__.setText("ID : " + unique_id)


        var ps11 : TextView = findViewById(R.id.crop_name)

        val signUpProgress: ProgressBar = findViewById(R.id.sign_up_progress)
        var sbutton : Button = findViewById(R.id.submit_button)

        sbutton.setOnClickListener {



            val fireStore = FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()
            val email = auth.currentUser!!.email


            val ps1 = ps11.text.toString()


            ps11.error=null

            if (TextUtils.isEmpty(ps1)) {
                ps11.error = "Crop Name is required"
                return@setOnClickListener
            }

            val newvegetable = Vegetable(
                uuuuid,
                unique_id,
                emailId,
                ps1
            )
            signUpProgress.visibility = View.VISIBLE
            fireStore.collection("Fields").document(unique_id).collection("Vegetable").document(uuuuid).set(newvegetable)
                .addOnSuccessListener {
                    Toast.makeText(this, "Successfully Added", Toast.LENGTH_LONG).show()
                    signUpProgress.visibility = View.GONE
                    val intent = Intent(this, EditVegetableSectionActivity::class.java)
                    intent.putExtra("id_field",unique_id)
                    intent.putExtra("email",emailId)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    signUpProgress.visibility = View.GONE
                    Toast.makeText(this, "Some Error Occured", Toast.LENGTH_LONG).show()
                    Log.e("No","Error")
                }




        }

    }
}