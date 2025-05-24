package si.uni_lj.fri.pbd.classproject3.models

data class IngredientIM(
    val strIngredient: String,
    val strDescription: String,
    val idIngredient: String
) {
    override fun toString(): String {
        return "IngredientIM(strIngredient='$strIngredient', strDescription='$strDescription', idIngredient='$idIngredient')"
    }
}