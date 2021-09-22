package com.example.happyfamilybox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.happyfamilybox.models.Fields
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MoreInfoAboutYourFieldActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_info_about_your_field)

        val goback : Button = findViewById(R.id.go_back)
        goback.setOnClickListener {
            val intent = Intent(this,YourFieldActivity::class.java)
            startActivity(intent)
        }
        var unique_id = intent.extras?.get("id_field") as String

        val fireStore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val ID : TextView = findViewById(R.id.unique_ID)
        ID.setText("ID : " + unique_id)
        val FieldName : TextView = findViewById(R.id.name_of_the_field)
        val lat : TextView = findViewById(R.id.tvLatitude)
        val lon : TextView = findViewById(R.id.tvLongitude)
        val addr : TextView = findViewById(R.id.tvAddress)
        val area : TextView = findViewById(R.id.area_of_the_field)
        fireStore.collection("Fields").whereEqualTo("id",unique_id).get()
            .addOnCompleteListener(){
                if(it.isSuccessful){
                    for(document in it.result!!){
                        FieldName.setText(document.data.getValue("fieldname").toString());
                        lat.setText(document.data.getValue("latitude").toString());
                        lon.setText(document.data.getValue("longitude").toString());
                        addr.setText(document.data.getValue("address").toString());
                        area.setText(document.data.getValue("fieldarea").toString());
                    }
                }
            }
            .addOnFailureListener{
                Toast.makeText(this,"Some Error Occured", Toast.LENGTH_LONG).show()
            }

    }
}