package es.ulpgc.gamecritic.model

data class Comment(
    val id: String = "",
    val videogameId: String = "",
    val userId: String = "",
    val username: String = "",
    val userProfileIcon: String = "",
    val content: String = "",
    val rating: Double = 0.0,
    val createdAt: String = "",
    val createdAtFormatted: String = ""
)
