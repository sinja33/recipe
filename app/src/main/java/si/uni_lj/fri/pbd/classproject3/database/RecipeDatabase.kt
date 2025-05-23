package si.uni_lj.fri.pbd.classproject3.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import si.uni_lj.fri.pbd.classproject3.Constants
import si.uni_lj.fri.pbd.classproject3.database.entity.RecipeDetails

@androidx.room.Database(entities = [RecipeDetails::class], version = 1, exportSchema = false)
abstract class RecipeDatabase : RoomDatabase() {

    // TODO: add a DAO reference

    companion object {
        @Volatile
        private var INSTANCE: RecipeDatabase? = null


        fun getDatabase(context: Context): RecipeDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext,
                            RecipeDatabase::class.java, Constants.DB_NAME)
                            .fallbackToDestructiveMigration()
                            .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}