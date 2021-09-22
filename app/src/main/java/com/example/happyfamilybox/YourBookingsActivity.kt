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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class YourBookingsActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var msq : TextView
    lateinit var bookingRequestAdapterActivity: BookingRequestAdapterActivity
    lateinit var completedBookingAdapterActivity: CompletedBookingAdapterActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_bookings)

        val goback: ImageView = findViewById(R.id.backB)
        goback.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.recyclerHistory)
        msq = findViewById(R.id.textView4)

        val FieldCart: RadioButton = findViewById(R.id.field_cart)
        val BookingCart: RadioButton = findViewById(R.id.booking_cart)
        FieldCart.isChecked=true
        var list: MutableList<BookingCart> = mutableListOf()
        bookingRequestAdapterActivity = BookingRequestAdapterActivity(this, list)
        recyclerView.adapter = bookingRequestAdapterActivity
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchToDoList_bookingrequest()

        msq.visibility= View.VISIBLE
        FieldCart.setOnClickListener {


            var list: MutableList<BookingCart> = mutableListOf()
            bookingRequestAdapterActivity = BookingRequestAdapterActivity(this, list)
            recyclerView.adapter = bookingRequestAdapterActivity
            recyclerView.layoutManager = LinearLayoutManager(this)

            fetchToDoList_bookingrequest()

            msq.visibility= View.VISIBLE
        }

        BookingCart.setOnClickListener {
            var list: MutableList<BookingCart> = mutableListOf()
            completedBookingAdapterActivity = CompletedBookingAdapterActivity(this, list)
            recyclerView.adapter = completedBookingAdapterActivity
            recyclerView.layoutManager = LinearLayoutManager(this)
            fetchToDoList_completedbooking()
            msq.visibility = View.VISIBLE
        }

    }


    private fun fetchToDoList_bookingrequest() {
        doAsync {
            var list: MutableList<BookingCart> = mutableListOf()

            val fireStore = FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()
            val email = auth.currentUser!!.email
            var count : Int = 0

            fireStore.collection("BookingCart")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {

                        msq.visibility = View.GONE
                        var whattocheck  = document.data.getValue("email_owner").toString()
                        var stat = document.data.getValue("status").toString()
                        if(whattocheck==email && stat=="Pending") {
                            list.add(document.toObject(BookingCart::class.java))
                            count = count + 1
                        }
                    }
                    if(count==0)
                    {
                        msq.visibility = View.VISIBLE
                        msq.setText("No item ")
                    }
                    (recyclerView.adapter as BookingRequestAdapterActivity).notifyDataSetChanged()
                }
                .addOnFailureListener {
                    Log.e("No","Error")
                }

            uiThread {

                bookingRequestAdapterActivity.setList(list)
            }
        }
    }


    private fun fetchToDoList_completedbooking() {
        doAsync {
            var list: MutableList<BookingCart> = mutableListOf()

            val fireStore = FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()
            val email = auth.currentUser!!.email
            var count : Int = 0

            fireStore.collection("BookingCart")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {

                        msq.visibility = View.GONE
                        var whattocheck  = document.data.getValue("email_owner").toString()
                        var stat = document.data.getValue("status").toString()
                        if(whattocheck==email && stat=="Accepted") {
                            list.add(document.toObject(BookingCart::class.java))
                            count = count + 1
                        }
                    }
                    if(count==0)
                    {
                        msq.visibility = View.VISIBLE
                        msq.setText("No item ")
                    }
                    (recyclerView.adapter as CompletedBookingAdapterActivity).notifyDataSetChanged()
                }
                .addOnFailureListener {
                    Log.e("No","Error")
                }

            uiThread {

                completedBookingAdapterActivity.setList(list)
            }
        }
    }


}