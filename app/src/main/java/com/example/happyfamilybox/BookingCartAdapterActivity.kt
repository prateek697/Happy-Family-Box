package com.example.happyfamilybox

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.happyfamilybox.R
import com.example.happyfamilybox.models.BookingCart
import com.example.happyfamilybox.models.Cart
import com.example.happyfamilybox.models.Fields
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text


import java.text.SimpleDateFormat
import java.util.*


class BookingCartAdapterActivity(var context: Context, var ongoingList: MutableList<BookingCart>):
        RecyclerView.Adapter<BookingCartAdapterActivity.DetailsViewHolder>() {

    class DetailsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var FieldName: TextView = itemView.findViewById(R.id.field_name)
        var request_: TextView = itemView.findViewById(R.id.request)
        var Order_D: TextView = itemView.findViewById(R.id.order_details)
        var Owner_c: TextView = itemView.findViewById(R.id.owner_mobile)
        var delete_but : ImageView = itemView.findViewById(R.id.delete_button)

    }
    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {

        if(ongoingList[position].status=="Accepted")
        {
            holder.delete_but.visibility = View.GONE
        }
        var fireStore = FirebaseFirestore.getInstance()
        var emaill = ""
        fireStore.collection("Fields").document(ongoingList[position].id_field).get()
                .addOnSuccessListener {
                    holder.FieldName.text = "Booked From : "+it.getString("fieldname")
                    emaill = it.getString("email").toString()
                    holder.Owner_c.text = "Owner Email : " + emaill
                }


        holder.request_.text = "Status : " + ongoingList[position].status
        holder.Order_D.text = ongoingList[position].bookingdetails





        holder.delete_but.setOnClickListener {

            var firestore = FirebaseFirestore.getInstance().collection("BookingCart").document(ongoingList[position].id)
            AlertDialog.Builder(context)
                    .setTitle("Delete item")
                    .setMessage("Are you sure you want to delete this?")
                    .setPositiveButton("Yes") { dialogInterface, i ->
                        firestore.delete()
                                .addOnSuccessListener {
                                    var poss : Int = position
                                    Toast.makeText(context, "Successfully Deleted from Cart", Toast.LENGTH_LONG).show()
                                    ongoingList.removeAt(poss)
                                    notifyDataSetChanged()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
                                }
                    }
                    .setNegativeButton("No") { dialogInterface, i ->
                        dialogInterface.dismiss()
                    }
                    .create()
                    .show()

        }

    }

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): DetailsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.booking_cart_card,parent,false)
        return DetailsViewHolder(
                itemView
        )
    }

    override fun getItemCount(): Int {
        return ongoingList.size
    }

    fun setList(list: MutableList<BookingCart>){
        ongoingList = list

        notifyDataSetChanged()
    }
}

