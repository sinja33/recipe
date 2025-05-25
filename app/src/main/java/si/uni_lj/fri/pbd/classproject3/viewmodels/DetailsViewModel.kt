package si.uni_lj.fri.pbd.classproject3.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import si.uni_lj.fri.pbd.classproject3.models.RecipeDetailsIM
import si.uni_lj.fri.pbd.classproject3.repository.RecipeRepository

class DetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val recipeRepository = RecipeRepository(application)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _recipeDetails = MutableLiveData<RecipeDetailsIM?>()
    val recipeDetails: LiveData<RecipeDetailsIM?> = _recipeDetails
    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite
    private val _isUpdatingFavorite = MutableLiveData<Boolean>()
    val isUpdatingFavorite: LiveData<Boolean> = _isUpdatingFavorite
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Where is the recipe from - api or database
    private val _isFromFavorites = MutableLiveData<Boolean>()
    val isFromFavorites: LiveData<Boolean> = _isFromFavorites


    fun loadRecipeDetails(recipeId: String, fromFavorites: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _isFromFavorites.value = fromFavorites

            try {
                val details = if (fromFavorites) {
                    recipeRepository.getRecipeDetailsFromDatabase(recipeId)
                } else {
                    recipeRepository.getRecipeDetailsFromAPI(recipeId)
                }

                if (details != null) {
                    _recipeDetails.value = details
                    _isFavorite.value = details.isFavorite ?: false
                } else {
                    _errorMessage.value = if (fromFavorites) {
                        "Recipe not found in favorites"
                    } else {
                        "Could not load recipe. Check your internet connection."
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load recipe due to error: ${e.message}"
                _recipeDetails.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite() {
        val currentRecipe = _recipeDetails.value ?: return
        val currentFavoriteStatus = _isFavorite.value ?: false

        viewModelScope.launch {
            _isUpdatingFavorite.value = true

            try {
                val success = if (currentFavoriteStatus) {
                    recipeRepository.removeRecipeFromFavorites(currentRecipe.idMeal ?: "")
                } else {
                    recipeRepository.addRecipeToFavorites(currentRecipe.idMeal ?: "")
                }

                if (success) {
                    val newFavoriteStatus = !currentFavoriteStatus
                    _isFavorite.value = newFavoriteStatus
                    _recipeDetails.value = currentRecipe.copy(isFavorite = newFavoriteStatus)
                } else {
                    _errorMessage.value = "Failed to update favorite status"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error updating favorites: ${e.message}"
            } finally {
                _isUpdatingFavorite.value = false
            }
        }
    }


    fun getIngredientsList(): List<String> {
        val recipe = _recipeDetails.value ?: return emptyList()
        val ingredients = mutableListOf<String>()

        // Combine ingredients and measurements (up to 20)
        for (i in 1..20) {
            val ingredient = getIngredientByIndex(recipe, i)
            val measure = getMeasureByIndex(recipe, i)

            if (!ingredient.isNullOrBlank()) {
                val formattedItem = if (!measure.isNullOrBlank()) {
                    "$measure $ingredient"
                } else {
                    ingredient
                }
                ingredients.add(formattedItem.trim())
            }
        }

        return ingredients
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun getIngredientByIndex(recipe: RecipeDetailsIM, index: Int): String? {
        return when (index) {
            1 -> recipe.strIngredient1
            2 -> recipe.strIngredient2
            3 -> recipe.strIngredient3
            4 -> recipe.strIngredient4
            5 -> recipe.strIngredient5
            6 -> recipe.strIngredient6
            7 -> recipe.strIngredient7
            8 -> recipe.strIngredient8
            9 -> recipe.strIngredient9
            10 -> recipe.strIngredient10
            11 -> recipe.strIngredient11
            12 -> recipe.strIngredient12
            13 -> recipe.strIngredient13
            14 -> recipe.strIngredient14
            15 -> recipe.strIngredient15
            16 -> recipe.strIngredient16
            17 -> recipe.strIngredient17
            18 -> recipe.strIngredient18
            19 -> recipe.strIngredient19
            20 -> recipe.strIngredient20
            else -> null
        }
    }

    private fun getMeasureByIndex(recipe: RecipeDetailsIM, index: Int): String? {
        return when (index) {
            1 -> recipe.strMeasure1
            2 -> recipe.strMeasure2
            3 -> recipe.strMeasure3
            4 -> recipe.strMeasure4
            5 -> recipe.strMeasure5
            6 -> recipe.strMeasure6
            7 -> recipe.strMeasure7
            8 -> recipe.strMeasure8
            9 -> recipe.strMeasure9
            10 -> recipe.strMeasure10
            11 -> recipe.strMeasure11
            12 -> recipe.strMeasure12
            13 -> recipe.strMeasure13
            14 -> recipe.strMeasure14
            15 -> recipe.strMeasure15
            16 -> recipe.strMeasure16
            17 -> recipe.strMeasure17
            18 -> recipe.strMeasure18
            19 -> recipe.strMeasure19
            20 -> recipe.strMeasure20
            else -> null
        }
    }
}