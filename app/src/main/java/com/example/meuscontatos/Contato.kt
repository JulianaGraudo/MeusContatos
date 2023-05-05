package com.example.meuscontatos

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Contato(
    @PrimaryKey (autoGenerate = true)
    val id : Int = 0,
    val nome : String,
    val celular : String
    ) : Serializable

