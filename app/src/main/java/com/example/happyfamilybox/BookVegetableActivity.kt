package com.example.happyfamilybox

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.example.happyfamilybox.models.BookingCart
import com.example.happyfamilybox.models.Fields
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DateFormat
import java.util.*

class BookVegetableActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_vegetable)

        val goback: ImageView = findViewById(R.id.backB)
        goback.setOnClickListener {
            val intent = Intent(this, FindFieldActivity::class.java)
            startActivity(intent)
        }


        var unique_id = intent.extras?.get("id_field") as String
        var emailId = intent.extras?.get("email") as String

        var vegs = ""


        var can__be_add : TextView = findViewById(R.id.can_be_added)
        var fireStore = FirebaseFirestore.getInstance()
        fireStore.collection("Fields").document(unique_id).collection("Crops").get()
                .addOnSuccessListener { documents->
                    for(documentt in documents){

                        vegs = vegs + documentt.data.getValue("cropname").toString()+"  "
                    }
                    can__be_add.setText("List of Vegetables :  " + vegs)
                }
//        Log.d("id_field" , count.toString())


        var dateee : TextView  = findViewById(R.id.cropname11)
        var datee : Button =findViewById(R.id.submit_button3)
        var selected_date =""
        datee.setOnClickListener {
            val now = Calendar.getInstance()
            val datePicker = DatePickerDialog(this,DatePickerDialog.OnDateSetListener{view,year,month,dayOfMonth ->
                var xx = month.toInt()
                xx=xx+1
                var ss = xx.toString()
                if(xx<10)
                {
                    ss="0"+ss
                }
                selected_date=dayOfMonth.toString()+"/"+ss+"/"+year.toString()
                dateee.setText("Date : " + selected_date)
            },
                    now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }



        var subbmit : Button = findViewById(R.id.submit_button2)
        subbmit.setOnClickListener {
            var calender : Calendar = Calendar.getInstance()
            var currentDate : String =  DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(calender.getTime())
//            Log.d("currentdate",currentDate)
//            dateee.setText((currentDate))
            var d1 = currentDate.subSequence(0,2)
            var m1 = currentDate.subSequence(3,5)
            var y1 = currentDate.subSequence(6,10)

            var ENter : TextView = findViewById(R.id.entered_vegetables)
            var iiinn = ENter.text.toString()


            ENter.error=null

            if(TextUtils.isEmpty(iiinn)){
                ENter.error="This Field is Required"
                return@setOnClickListener
            }
            var tot : TextView = findViewById(R.id.total_weight)
            var tot1 = tot.text.toString()
            tot.error=null
            if(TextUtils.isEmpty(tot1))
            {
                tot.error="This Field is required"
                return@setOnClickListener
            }


            if(TextUtils.isEmpty(selected_date)){
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var d2 = selected_date.subSequence(0,2)
            var m2 = selected_date.subSequence(3,5)
            var y2 = selected_date.subSequence(6,10)

            if(y2.toString().toInt()<y1.toString().toInt())
            {
                Toast.makeText(this, "Please select a valid date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(m2.toString().toInt()<m1.toString().toInt())
            {
                Toast.makeText(this, "Please select a valid date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(d2.toString().toInt()<d1.toString().toInt())
            {
                Toast.makeText(this, "Please select a valid date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var AddrR : TextView = findViewById(R.id.delivery_address)
            var val1 = AddrR.text.toString()
            AddrR.error=null
            if(TextUtils.isEmpty(val1)){
                AddrR.error = "This Field is required"
                return@setOnClickListener
            }

            if(tot1.toInt()<5)
            {
                Toast.makeText(this, "Please book at least 5 kg", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val auth = FirebaseAuth.getInstance()
            var uuid : UUID = UUID.randomUUID()
            val user_email = auth.currentUser?.email.toString()
            val newbooking = BookingCart(
                    uuid.toString(),
                    unique_id,
                    emailId,
                    user_email,
                    iiinn,
                    tot1,
                    "0",
                    "Pending",
                    "-1",
                    val1
            )

            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("BookingCart").document(uuid.toString()).set(newbooking)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                    this,
                                    "SuccessFully Added to Cart",
                                    Toast.LENGTH_LONG
                            ).show()
                            val intent = Intent(this, CitizenCartActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Some Error Occured", Toast.LENGTH_LONG).show()
                        }
                    }

        }
    }
}