package es.ulpgc.gamecritic.model

data class User(
    val id: String = "",
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val profileIcon: String = "",
    val description: String = "",
    val following: List<String> = emptyList(),
    val followers: List<String> = emptyList()
)

