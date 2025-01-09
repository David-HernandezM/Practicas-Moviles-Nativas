package com.example.practica2.api

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

data class ShowResponse(
    val show: Show
)

data class Show(
    val id: Int,
    val name: String,
    val image: Image?,
    val summary: String?,
    var cast: List<CastMember> = emptyList()
)

data class Image(
    val medium: String,
    val original: String
)

data class CastMember(
    val person: Person
)

data class Person(
    val name: String
)

