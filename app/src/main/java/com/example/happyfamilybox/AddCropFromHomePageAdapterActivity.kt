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
import com.example.happyfamilybox.models.Cart
import com.example.happyfamilybox.models.Fields
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text


import java.text.SimpleDateFormat
import java.util.*


class AddCropFromHomePageAdapterActivity(var context: Context, var ongoingList: MutableList<Fields>):
    RecyclerView.Adapter<AddCropFromHomePageAdapterActivity.DetailsViewHolder>() {

    class DetailsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var FieldName: TextView = itemView.findViewById(R.id.name_of_field)
        var add_but : ImageView = itemView.findViewById(R.id.add_button)

    }
    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {

        holder.FieldName.text = ongoingList[position].fieldname

        holder.add_but.setOnClickListener {
            val intent = Intent(context, EditCropSectionActivity::class.java)
            intent.putExtra("id_field",ongoingList[position].id)
            intent.putExtra("email",ongoingList[position].email)
            context.startActivity(intent)

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fields_card_from_homepage,parent,false)
        return DetailsViewHolder(
            itemView
        )
    }

    override fun getItemCount(): Int {
        return ongoingList.size
    }

    fun setList(list: MutableList<Fields>){
        ongoingList = list

        notifyDataSetChanged()
    }
}

