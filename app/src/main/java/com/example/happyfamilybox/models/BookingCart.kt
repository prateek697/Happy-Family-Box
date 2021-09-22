package com.example.happyfamilybox.models

import java.io.Serializable


data class BookingCart(
        var id: String = "",
        var id_field : String = "",
        var email_owner : String = "",
        var bookedby : String ="",
        var bookingdetails : String = "",
        var totalweight : String = "",
        var rating : String = "",
        var status : String = "",
        var flag : String = "",
        var address : String = ""
) : Serializable