package com.example.happyfamilybox

import android.app.AlertDialog
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
import androidx.core.content.ContextCompat.startActivity
import com.example.happyfamilybox.R
import com.example.happyfamilybox.models.BookedFields
import com.example.happyfamilybox.models.Cart
import com.example.happyfamilybox.models.Fields
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text
import java.text.DateFormat


import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class ReserveFieldOfCitizenAdapterActivity(var context: Context, var ongoingList: MutableList<BookedFields>):
    RecyclerView.Adapter<ReserveFieldOfCitizenAdapterActivity.DetailsViewHolder>() {

    class DetailsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var FieldName: TextView = itemView.findViewById(R.id.field_name)
        var Contactdd: TextView = itemView.findViewById(R.id.contact_details)
        var areaReservv: TextView = itemView.findViewById(R.id.area_reserved)
        var aDDress: TextView = itemView.findViewById(R.id.address_details)
        var validitiy : TextView = itemView.findViewById(R.id.valid_till)
        var acCeptOn : TextView = itemView.findViewById(R.id.accept_on)

    }
    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {
        var field__id = ongoingList[position].id_field
        var fireStore = FirebaseFirestore.getInstance()
        fireStore.collection("Fields").whereEqualTo("id",field__id).get()
            .addOnSuccessListener { documents->
                for(document in documents){
                    holder.FieldName.text = document.data.getValue("fieldname").toString()
                    holder.aDDress.text = document.data.getValue("address").toString()
                }
            }
        fireStore.collection("Users").whereEqualTo("email",ongoingList[position].email_owner).get()
            .addOnSuccessListener { documents->
                for(document in documents){
                    holder.Contactdd.text = "Owner Contact : " + document.data.getValue("mobile").toString()
                }
            }

        holder.validitiy.text = ongoingList[position].validity + " Months"
        holder.acCeptOn.text = "Request Accepted On : " + ongoingList[position].currdate
        holder.areaReservv.text = "Area Reserved : "  + ongoingList[position].areareserved

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.reserve_field_citizen_card,parent,false)
        return DetailsViewHolder(
            itemView
        )
    }

    override fun getItemCount(): Int {
        return ongoingList.size
    }

    fun setList(list: MutableList<BookedFields>){
        ongoingList = list

        notifyDataSetChanged()
    }
}

