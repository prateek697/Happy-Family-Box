package com.example.happyfamilybox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happyfamilybox.models.Fields
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class YourFieldActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var markAttendanceAdapter: YourFieldAdapterActivity
    lateinit var msq : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_field)

        val fireStore = FirebaseFirestore.getInstance()

        var list: MutableList<Fields> = mutableListOf()

        val goback: ImageView = findViewById(R.id.backB)
        goback.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.recyclerHistory)
        markAttendanceAdapter = YourFieldAdapterActivity(this, list)
        var tocheck : Boolean = false
        var count: Int = 0
        msq = findViewById(R.id.textView4)
        recyclerView.adapter = markAttendanceAdapter

        recyclerView.layoutManager = LinearLayoutManager(this)

            fetchToDoList()

            msq.visibility=VISIBLE

    }

    private fun fetchToDoList() {
        doAsync {
            var list: MutableList<Fields> = mutableListOf()

            val fireStore = FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()
            val email = auth.currentUser!!.email
            var count : Int = 0
            fireStore.collection("Fields").whereEqualTo("email",email)
                    .get()
                    .addOnSuccessListener { documents ->
                                for (document in documents) {

                                    msq.visibility = GONE
                                    list.add(document.toObject(Fields::class.java))
                                    count = count + 1
                                }
                                if(count==0)
                                {
                                    msq.visibility = VISIBLE
                                    msq.setText("No Fields has been added")
                                }
                                (recyclerView.adapter as YourFieldAdapterActivity).notifyDataSetChanged()
                        }
                    .addOnFailureListener {
                        Log.e("No","Error")
                    }

            uiThread {

                markAttendanceAdapter.setList(list)
            }
        }
    }


}