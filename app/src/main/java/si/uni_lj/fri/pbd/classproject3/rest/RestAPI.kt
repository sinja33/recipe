package si.uni_lj.fri.pbd.classproject3.rest

import retrofit2.http.GET
import retrofit2.http.Query
import si.uni_lj.fri.pbd.classproject3.models.dto.*

interface RestAPI {

    @GET("list.php?i=list")
    suspend fun getAllIngredients(): IngredientsDTO?

    // TODO: Add missing endpoints

}