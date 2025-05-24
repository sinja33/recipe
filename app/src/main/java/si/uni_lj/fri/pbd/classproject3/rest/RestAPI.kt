package si.uni_lj.fri.pbd.classproject3.rest

import retrofit2.http.GET
import retrofit2.http.Query
import si.uni_lj.fri.pbd.classproject3.models.dto.*

interface RestAPI {

    @GET("list.php?i=list")
    suspend fun getAllIngredients(): IngredientsDTO?

    // https://www.themealdb.com/api/json/v1/1/lookup.php?i=RECIPE_ID
    @GET("lookup.php")
    suspend fun getRecipeById(@Query("i") recipeId: String): RecipesByIdDTO?

    // https://www.themealdb.com/api/json/v1/1/filter.php?i=INGREDIENT
    @GET("filter.php")
    suspend fun getRecipesByIngredient(@Query("i") ingredient: String): RecipesByIngredientDTO?

}