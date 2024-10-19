package com.example.socialratingdatadase.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Member::class], version = 1)
abstract class MainDb : RoomDatabase(){
    abstract val dao: Dao
}