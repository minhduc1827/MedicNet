package com.medic.net.app

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

import com.medic.net.server.infraestructure.ServerDao
import com.medic.net.server.infraestructure.ServerEntity

@Database(entities = arrayOf(ServerEntity::class), version = 1, exportSchema = false)
abstract class RocketChatDatabase : RoomDatabase() {
    abstract fun serverDao(): ServerDao
}
