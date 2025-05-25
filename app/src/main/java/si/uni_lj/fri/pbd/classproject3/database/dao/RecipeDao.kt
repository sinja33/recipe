package si.uni_lj.fri.pbd.classproject3.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import si.uni_lj.fri.pbd.classproject3.database.entity.RecipeDetails

// Used for local storage
@Dao
interface RecipeDao {
    @Query("SELECT * FROM RecipeDetails WHERE idMeal = :idMeal")
    suspend fun getRecipeById(idMeal: String?): RecipeDetails?

    // When recipe is first favorited
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeDetails)

    // Update to fav
    @Update
    suspend fun updateRecipe(recipe: RecipeDetails)

    // Remove from faves (from database)
    @Delete
    suspend fun deleteRecipe(recipe: RecipeDetails)

    @Query("SELECT * FROM RecipeDetails WHERE isFavorite = 1")
    suspend fun getAllFavoriteRecipes(): List<RecipeDetails>

    @Query("SELECT EXISTS(SELECT 1 FROM RecipeDetails WHERE idMeal = :idMeal AND isFavorite = 1)")
    suspend fun isRecipeFavorite(idMeal: String): Boolean
}