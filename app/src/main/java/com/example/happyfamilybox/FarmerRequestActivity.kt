package com.example.happyfamilybox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
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

class FarmerRequestActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var farmerRequestAdapterActivity: FarmerRequestAdapterActivity
    lateinit var msq : TextView
    
    lateinit var bookingRequestAdapterActivity: BookingRequestAdapterActivity

    var ids: MutableList<String> = mutableListOf()


    class Run {
        companion object {
            fun after(delay: Long, process: () -> Unit) {
                Handler().postDelayed({
                    process()
                }, delay)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farmer_request)

        val fireStore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val email = auth.currentUser!!.email

        fireStore.collection("Fields").whereEqualTo("email",email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    ids.add(document.data.getValue("id").toString())
                }
            }
            .addOnFailureListener {
                Log.e("No","Error")
            }


        val goback: ImageView = findViewById(R.id.backB)
        goback.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }



        recyclerView = findViewById(R.id.recyclerHistory)
        msq = findViewById(R.id.textView4)


        var list: MutableList<BookingCart> = mutableListOf()
        bookingRequestAdapterActivity = BookingRequestAdapterActivity(this, list)
        recyclerView.adapter = bookingRequestAdapterActivity
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchToDoList_bookingrequest()

        msq.visibility= View.VISIBLE





    }



    private fun fetchToDoList() {
        doAsync {
            var list: MutableList<Cart> = mutableListOf()

            val fireStore = FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()
            val email = auth.currentUser!!.email
            var count : Int = 0

            fireStore.collection("Cart")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {

                        msq.visibility = View.GONE
                        var whattocheck  = document.data.getValue("id_field").toString()
                        var flag : Boolean = false
                        for(i in ids.indices)
                        {
                            if(ids[i]==whattocheck)
                            {
                                flag=true
                                break
                            }
                        }
                        if(flag==true){
                            list.add(document.toObject(Cart::class.java))
                            count = count + 1
                        }
                    }
                    if(count==0)
                    {
                        msq.visibility = View.VISIBLE
                        msq.setText("No item in Cart")
                    }
                    (recyclerView.adapter as FarmerRequestAdapterActivity).notifyDataSetChanged()
                }
                .addOnFailureListener {
                    Log.e("No","Error")
                }

            uiThread {

                farmerRequestAdapterActivity.setList(list)
            }
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
                            msq.setText("No item in Cart")
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



}