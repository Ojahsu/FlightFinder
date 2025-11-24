package com.example.flightfinder.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.flightfinder.models.FlightFromBDD

@Database(
    entities = [FlightFromBDD::class],
    version = 2,
)
@TypeConverters(FlightConvecters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun flightDao(): FlightDao
}