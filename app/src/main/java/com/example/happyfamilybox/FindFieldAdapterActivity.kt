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
import com.example.happyfamilybox.models.Fields
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.reserve_field_citizen_card.view.*
import org.w3c.dom.Text


import java.text.SimpleDateFormat
import java.util.*


class FindFieldAdapterActivity(var context: Context, var ongoingList: MutableList<Fields>):
        RecyclerView.Adapter<FindFieldAdapterActivity.DetailsViewHolder>() {

    class DetailsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var FieldName: TextView = itemView.findViewById(R.id.field_name)
        var owner_mobile: TextView = itemView.findViewById(R.id.owner_mobile)
        var Moreinfo: Button = itemView.findViewById(R.id.reserve_more_info_button)
        var crop_detail: Button = itemView.findViewById(R.id.all_crops)
        var addres: TextView = itemView.findViewById(R.id.address)

    }
    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {

        holder.addres.text = ongoingList[position].address
        var x : String = ""
        if(ongoingList[position].count.toInt()==0) x="5/5"
        else{
            var t1=ongoingList[position].count.toInt()
            var t2=ongoingList[position].rating.toInt()
            var ratt = t2/t1

            x=ratt.toString()+"/5"

        }
        holder.FieldName.text = "Field Name - " + ongoingList[position].fieldname + " , " + x
        val fireStore = FirebaseFirestore.getInstance()
        var id_user = ongoingList[position].email
        fireStore.collection("Users").whereEqualTo("email",id_user).get()
                .addOnSuccessListener { documents ->

                    for(document in documents) {
                        holder.owner_mobile.text = "Owner Contact : "+document.data.getValue("mobile").toString()
                    }

                }
        holder.Moreinfo.setOnClickListener {
            val intent = Intent(context, BookVegetableActivity::class.java)
            intent.putExtra("id_field",ongoingList[position].id)
            intent.putExtra("email",ongoingList[position].email)
            context.startActivity(intent)
        }

        holder.crop_detail.setOnClickListener {
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
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.find_field_card,parent,false)
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

