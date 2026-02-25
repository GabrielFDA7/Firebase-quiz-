package com.quizapp.firebase.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.quizapp.firebase.data.local.dao.QuestionDao
import com.quizapp.firebase.data.local.dao.QuizResultDao
import com.quizapp.firebase.data.local.dao.UserDao
import com.quizapp.firebase.data.local.entity.QuestionEntity
import com.quizapp.firebase.data.local.entity.QuizResultEntity
import com.quizapp.firebase.data.local.entity.UserEntity

/**
 * Banco de dados Room principal do aplicativo.
 * Singleton thread-safe usando double-checked locking.
 */
@Database(
    entities = [
        UserEntity::class,
        QuestionEntity::class,
        QuizResultEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun questionDao(): QuestionDao
    abstract fun quizResultDao(): QuizResultDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "quiz_app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
