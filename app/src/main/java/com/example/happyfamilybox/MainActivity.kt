package com.example.happyfamilybox
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        var currentUser = auth.currentUser

        val logout = findViewById<Button>(R.id.idLogout)

        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        if (currentUser != null) {

            var type = ""
            val fireStore = FirebaseFirestore.getInstance()
            fireStore.collection("Users").document(auth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    type = it.getString("type").toString()
                    if (type == "Farmer") {

                        startActivity(Intent(this, HomePageActivity::class.java))
                        finish()
                    } else {
                        startActivity(Intent(this, HomePageCitizenActivity::class.java))
                        finish()
                    }
                }


        }
        logout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

