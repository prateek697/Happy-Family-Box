package com.example.happyfamilybox

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import com.example.happyfamilybox.R
import com.example.happyfamilybox.models.BookingCart
import com.example.happyfamilybox.models.Crops
import com.example.happyfamilybox.models.Fields
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text


import java.text.SimpleDateFormat
import java.util.*


class RateFarmerAdapterActivity(var context: Context, var ongoingList: MutableList<BookingCart>):
        RecyclerView.Adapter<RateFarmerAdapterActivity.DetailsViewHolder>() {

    class DetailsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var fieldNAME: TextView = itemView.findViewById(R.id.name_of_field)
        var vIewMore: TextView = itemView.findViewById(R.id.view_history)

    }
    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {

        var fireStore = FirebaseFirestore.getInstance()
        fireStore.collection("Fields").document(ongoingList[position].id_field).get()
                .addOnSuccessListener {
                    holder.fieldNAME.text = it.getString("fieldname")
                }

        holder.vIewMore.setOnClickListener {
            val intent = Intent(context, ViewHistoryActivity::class.java)
            intent.putExtra("field_ki_id",ongoingList[position].id_field)
            context.startActivity(intent)
        }

    }

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): DetailsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.farm_list_card,parent,false)
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

