package com.example.happyfamilybox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happyfamilybox.models.BookingCart
import com.example.happyfamilybox.models.Cart
import com.example.happyfamilybox.models.Fields
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class CitizenCartActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var bookingCartAdapterActivity: BookingCartAdapterActivity
    lateinit var msq : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_citizen_cart)

        val fireStore = FirebaseFirestore.getInstance()



        val goback: ImageView = findViewById(R.id.backB)
        goback.setOnClickListener {
            val intent = Intent(this, HomePageCitizenActivity::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.recyclerHistory)
        msq = findViewById(R.id.textView4)






            var list: MutableList<BookingCart> = mutableListOf()
            bookingCartAdapterActivity = BookingCartAdapterActivity(this, list)
            recyclerView.adapter = bookingCartAdapterActivity
            recyclerView.layoutManager = LinearLayoutManager(this)
            fetchToDoList_booking()
            msq.visibility = View.VISIBLE



    }



    private fun fetchToDoList_booking() {
        doAsync {
            var list: MutableList<BookingCart> = mutableListOf()

            val fireStore = FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()
            val email = auth.currentUser!!.email
            var count : Int = 0

            fireStore.collection("BookingCart").whereEqualTo("bookedby",email)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            if(document.data.getValue("status").toString()=="Pending") {
                                msq.visibility = View.GONE
                                list.add(document.toObject(BookingCart::class.java))
                                count = count + 1
                            }
                        }
                        if(count==0)
                        {
                            msq.visibility = View.VISIBLE
                            msq.setText("No item in Cart")
                        }
                        (recyclerView.adapter as BookingCartAdapterActivity).notifyDataSetChanged()
                    }
                    .addOnFailureListener {
                        Log.e("No","Error")
                    }

            uiThread {

                bookingCartAdapterActivity.setList(list)
            }
        }
    }


}