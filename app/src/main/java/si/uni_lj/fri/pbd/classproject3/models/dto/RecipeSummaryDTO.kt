package si.uni_lj.fri.pbd.classproject3.models.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RecipeSummaryDTO {
    @SerializedName("idMeal")
    @Expose
    val id: String = ""

    @SerializedName("strMeal")
    @Expose
    val name: String = ""

    @SerializedName("strMealThumb")
    @Expose
    val thumbnailUrl: String = ""
}