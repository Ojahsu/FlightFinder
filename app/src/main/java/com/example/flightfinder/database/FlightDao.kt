package com.example.flightfinder.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flightfinder.models.FlightFromBDD

@Dao
interface FlightDao {
    @Query("SELECT * FROM FlightFromBDD")
    suspend fun getAllFlights(): List<FlightFromBDD>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlight(FlightFromBDD: FlightFromBDD)

    @Query("SELECT * FROM FlightFromBDD WHERE id = :id")
    suspend fun getFlightById(id: Int): FlightFromBDD?

    @Query("DELETE FROM FlightFromBDD")
    suspend fun deleteAllFlights()

    @Query("DELETE FROM FlightFromBDD WHERE id = :id")
    suspend fun deleteFlight(id: Int)
}