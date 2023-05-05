package com.example.meuscontatos

import androidx.room.*

@Dao
interface ContatoDAO {

    //CREATE
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insert(contato: Contato)

    //READ
    @Query ("Select * from Contato")
    fun getAll(): List<Contato>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(contato: Contato)

    @Query("Delete from Contato")
    fun deleteAll()

    @Query ("Delete from Contato where id =:id")
    fun deleteById(id : Int)


}