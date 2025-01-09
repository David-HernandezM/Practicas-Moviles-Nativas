package com.example.practica2.api.searchdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [SearchHistory::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "search_history_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class SearchHistoryRepository(private val searchHistoryDao: SearchHistoryDao) {

    suspend fun insertSearchHistory(searchHistory: SearchHistory) {
        searchHistoryDao.insertSearchHistory(searchHistory)
    }

    suspend fun getAllSearchHistories(): List<SearchHistory> {
        return searchHistoryDao.getAllSearchHistories()
    }
}


