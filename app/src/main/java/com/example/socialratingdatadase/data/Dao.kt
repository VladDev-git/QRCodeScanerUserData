package com.example.socialratingdatadase.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {

    @Insert
    suspend fun insertMember(member: Member)

    @Update
    suspend fun updateMember(member: Member)

    @Query("SELECT * FROM members")
    fun getAllMembers(): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE numberQR = :qr")
    fun getMemberByQr(qr: String): Member?
}