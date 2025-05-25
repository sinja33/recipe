package si.uni_lj.fri.pbd.classproject3.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import si.uni_lj.fri.pbd.classproject3.models.RecipeSummaryIM
import si.uni_lj.fri.pbd.classproject3.repository.RecipeRepository

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val recipeRepository = RecipeRepository(application)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _favoriteRecipes = MutableLiveData<List<RecipeSummaryIM>>()
    val favoriteRecipes: LiveData<List<RecipeSummaryIM>> = _favoriteRecipes
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage


    init {
        loadFavoriteRecipes()
    }

    fun loadFavoriteRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val favorites = recipeRepository.getAllFavoriteRecipes()
                _favoriteRecipes.value = favorites

                if (favorites.isEmpty()) {
                    _errorMessage.value = "No favorite recipes yet."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load recipes due to error: ${e.message}"
                _favoriteRecipes.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // when you return to the page
    fun refreshFavorites() {
        loadFavoriteRecipes()
    }
}