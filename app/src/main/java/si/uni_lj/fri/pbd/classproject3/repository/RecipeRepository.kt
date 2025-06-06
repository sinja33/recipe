package si.uni_lj.fri.pbd.classproject3.repository

import android.content.Context
import si.uni_lj.fri.pbd.classproject3.database.RecipeDatabase
import si.uni_lj.fri.pbd.classproject3.database.dao.RecipeDao
import si.uni_lj.fri.pbd.classproject3.models.IngredientIM
import si.uni_lj.fri.pbd.classproject3.models.dto.IngredientDTO
import si.uni_lj.fri.pbd.classproject3.models.dto.IngredientsDTO
import si.uni_lj.fri.pbd.classproject3.models.dto.RecipeSummaryDTO
import si.uni_lj.fri.pbd.classproject3.models.dto.RecipeDetailsDTO
import si.uni_lj.fri.pbd.classproject3.rest.RestAPI
import si.uni_lj.fri.pbd.classproject3.rest.ServiceGenerator
import si.uni_lj.fri.pbd.classproject3.models.Mapper
import si.uni_lj.fri.pbd.classproject3.models.RecipeDetailsIM
import si.uni_lj.fri.pbd.classproject3.models.RecipeSummaryIM

class RecipeRepository(context: Context) {

    private val restApi: RestAPI = ServiceGenerator.createService(RestAPI::class.java)
    private val database: RecipeDatabase = RecipeDatabase.getDatabase(context)
    private val recipeDao: RecipeDao = database.recipeDao()

    // API calls
    // we will need: ingredients for dropdown, recipes by ingredient and recipe details

    // Made an IM so it is consistent with the later functions
    suspend fun getAllIngredients(): List<IngredientIM> {
        return try {
            val response: IngredientsDTO? = restApi.getAllIngredients()
            val ingredientDTOs: List<IngredientDTO> = response?.ingredients ?: emptyList()
            ingredientDTOs.map { ingredientDto ->
                Mapper.mapIngredientDtoToIngredientIm(ingredientDto)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRecipesByIngredient(ingredient: String): List<RecipeSummaryIM> {
        return try {
            val response = restApi.getRecipesByIngredient(ingredient)
            val recipeSummaries = response?.recipes ?: emptyList()
            recipeSummaries.map { recipeDto ->
                Mapper.mapRecipSummaryDtoToRecipeSummaryIm(recipeDto)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRecipeDetailsFromAPI(recipeId: String): RecipeDetailsIM? {
        return try {
            val response = restApi.getRecipeById(recipeId)
            val recipeDetails = response?.recipes?.firstOrNull()

            recipeDetails?.let { dto ->
                val isFavorite = recipeDao.isRecipeFavorite(recipeId)
                Mapper.mapRecipeDetailsDtoToRecipeDetailsIm(isFavorite, dto)
            }
        } catch (e: Exception) {
            null
        }
    }


    // Database functions - we will need for favorite recipes and to get details for a fav recipe
    // and to put a recipe into faves, or out of faves

    suspend fun getAllFavoriteRecipes(): List<RecipeSummaryIM> {
        return try {
            val favoriteRecipes = recipeDao.getAllFavoriteRecipes()
            favoriteRecipes.map { recipe ->
                Mapper.mapRecipeDetailsToRecipeSummaryIm(recipe)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRecipeDetailsFromDatabase(recipeId: String): RecipeDetailsIM? {
        return try {
            val recipe = recipeDao.getRecipeById(recipeId)

            recipe?.let {
                Mapper.mapRecipeDetailsToRecipeDetailsIm(it.isFavorite, it)
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addRecipeToFavorites(recipeId: String): Boolean {
        return try {
            val existingRecipe = recipeDao.getRecipeById(recipeId)

            // in database
            if (existingRecipe != null) {
                existingRecipe.isFavorite = true
                recipeDao.updateRecipe(existingRecipe)
            } else { // not yet in database
                val response = restApi.getRecipeById(recipeId)
                val recipeDetailsDto = response?.recipes?.firstOrNull()

                recipeDetailsDto?.let { dto ->
                    val recipeEntity = Mapper.mapRecipeDetailsDtoToRecipeDetails(true, dto)
                    recipeDao.insertRecipe(recipeEntity)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    // unclick the fave button
    suspend fun removeRecipeFromFavorites(recipeId: String): Boolean {
        return try {
            val recipe = recipeDao.getRecipeById(recipeId)
            recipe?.let {
                it.isFavorite = false
                recipeDao.updateRecipe(it)
                recipeDao.deleteRecipe(recipe)
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}