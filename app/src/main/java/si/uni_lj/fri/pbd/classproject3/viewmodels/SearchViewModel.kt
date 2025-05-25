package si.uni_lj.fri.pbd.classproject3.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import si.uni_lj.fri.pbd.classproject3.models.IngredientIM
import si.uni_lj.fri.pbd.classproject3.models.RecipeSummaryIM
import si.uni_lj.fri.pbd.classproject3.repository.RecipeRepository

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val recipeRepository = RecipeRepository(application)

    private val _ingredientsLoading = MutableLiveData<Boolean>()
    val ingredientsLoading: LiveData<Boolean> = _ingredientsLoading
    private val _recipesLoading = MutableLiveData<Boolean>()
    val recipesLoading: LiveData<Boolean> = _recipesLoading

    private val _ingredients = MutableLiveData<List<IngredientIM>>()
    val ingredients: LiveData<List<IngredientIM>> = _ingredients
    private val _recipes = MutableLiveData<List<RecipeSummaryIM>>()
    val recipes: LiveData<List<RecipeSummaryIM>> = _recipes
    private val _selectedIngredient = MutableLiveData<IngredientIM?>()
    val selectedIngredient: LiveData<IngredientIM?> = _selectedIngredient

    // To display the error!
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private var lastRefreshTime = 0L
    private val refreshCooldownMs = 5000L


    init {
        viewModelScope.launch {
            _ingredientsLoading.value = true
            _errorMessage.value = null

            try {
                val ingredientList = recipeRepository.getAllIngredients()
                _ingredients.value = ingredientList

                if (ingredientList.isEmpty()) {
                    _errorMessage.value = "Ingredient list is empty. Check your internet connection"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load ingredients due to error: ${e.message}"
                _ingredients.value = emptyList()
            } finally {
                _ingredientsLoading.value = false
            }
        }
    }

    fun searchRecipesByIngredient(ingredient: IngredientIM) {
        viewModelScope.launch {
            _selectedIngredient.value = ingredient
            _recipesLoading.value = true
            _errorMessage.value = null

            try {
                val recipeList = recipeRepository.getRecipesByIngredient(ingredient.strIngredient)
                _recipes.value = recipeList

                if (recipeList.isEmpty()) {
                    _errorMessage.value = "No recipes were found for ${ingredient.strIngredient}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load recipes due to error: ${e.message}"
                _recipes.value = emptyList()
            } finally {
                _recipesLoading.value = false
            }
        }
    }


    fun refreshRecipes() {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastRefreshTime < refreshCooldownMs) {
            return
        }

        lastRefreshTime = currentTime
        _selectedIngredient.value?.let { ingredient ->
            searchRecipesByIngredient(ingredient)
        }
    }

    fun clearSearch() {
        _recipes.value = emptyList()
        _selectedIngredient.value = null
        _errorMessage.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

}