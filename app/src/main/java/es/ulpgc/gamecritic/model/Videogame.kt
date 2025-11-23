package es.ulpgc.gamecritic.model

data class Videogame(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val description: String = "",
    val category: List<String> = emptyList(),
    val imageCarousel: String = "",
    val imageProfile: String = ""
)

