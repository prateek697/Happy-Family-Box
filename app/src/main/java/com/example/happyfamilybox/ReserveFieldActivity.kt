package com.example.happyfamilybox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.happyfamilybox.models.Cart
import com.example.happyfamilybox.models.Crops
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.find
import org.w3c.dom.Text
import java.util.*

class ReserveFieldActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserve_field)

        var unique_id = intent.extras?.get("id_field") as String
        var emailId = intent.extras?.get("email") as String
        var field_name = intent.extras?.get("field_name") as String

        var idoffield : TextView = findViewById(R.id.unique_ID)
        idoffield.setText("ID : " + unique_id )

        var field__name : TextView = findViewById(R.id.name_of_the_field)
        field__name.setText(field_name)

        var area_of___field : TextView = findViewById(R.id.area__field)


        val fireStore = FirebaseFirestore.getInstance()
        var count : Int = 0

        fireStore.collection("Fields").get()
            .addOnSuccessListener { documents->
                for(document in documents) {
                    var whichid = document.data.getValue("id").toString()
                    var value = document.data.getValue("fieldarea").toString()
                    if (whichid == unique_id) {
                        area_of___field.setText(value)
                    }
                }
            }



        var reserve_button : Button = findViewById(R.id.submit_button)
        reserve_button.setOnClickListener {

            var area_want_to_reserve : EditText = findViewById(R.id.area_wanted)
            var arreaa = area_want_to_reserve.text.toString()

            var months_want_to_reserve : EditText = findViewById(R.id.months_wanted)
            var mmonnth = months_want_to_reserve.text.toString()

            area_want_to_reserve.error = null
            months_want_to_reserve.error = null
            if(TextUtils.isEmpty(arreaa))
            {
                area_want_to_reserve.error = "This field is Required"
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(mmonnth)){
                months_want_to_reserve.error = "This field is Required"
                return@setOnClickListener
            }

            var val1 = count
            var val2 = area_of___field.text.toString().toInt()
            val boundd = (val2-val1).toString()
            Log.d("area",boundd)
            if( arreaa.toInt() > (val2-val1) ){
                area_want_to_reserve.error = "Please enter area less than " + boundd
                return@setOnClickListener
            }

            var uuid : UUID = UUID.randomUUID()
            var uuuuid : String = uuid.toString()

            var auth = FirebaseAuth.getInstance()
            var currentUser = auth.currentUser.email


            val newcart = Cart(
                uuuuid,
                unique_id,
                emailId,
                currentUser,
                mmonnth,
                arreaa
            )

            val fireStore = FirebaseFirestore.getInstance()
            fireStore.collection("Cart").document(uuuuid).set(newcart)
                .addOnSuccessListener {
                    Toast.makeText(this, "Successfully Added", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, CitizenCartActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Some Error Occured", Toast.LENGTH_LONG).show()
                }
        }

    }
}