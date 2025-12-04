package es.ulpgc.gamecritic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import es.ulpgc.gamecritic.model.RecentSearch
import es.ulpgc.gamecritic.model.User
import es.ulpgc.gamecritic.model.Videogame
import es.ulpgc.gamecritic.viewmodel.SearchState
import es.ulpgc.gamecritic.viewmodel.SearchTab
import es.ulpgc.gamecritic.viewmodel.SearchViewModel
import es.ulpgc.gamecritic.viewmodel.SearchViewModelFactory

private val LightBackground = Color(0xFFF0ECE3)
private val LightSurface = Color(0xE2FFFFFF)
private val TextBlack = Color(0xFF111827)
private val TextDarkGray = Color(0xFF4B5563)
private val MyYellow = Color(0xFFF4D73E)
private val MyYellowDark = Color(0xFFF4D73E)

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = run {
        val context = LocalContext.current
        viewModel(factory = SearchViewModelFactory(context))
    },
    onVideogameClick: (Videogame) -> Unit = {},
    onUserClick: (User) -> Unit = {},
    onRecentSearchNavigate: (RecentSearch) -> Unit = {}
) {
    val state: SearchState = viewModel.uiState

    Scaffold(
        containerColor = LightBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Buscar",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = TextBlack,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Encuentra videojuegos y usuarios de la comunidad",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextDarkGray
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SearchBar(
                        query = state.query,
                        onQueryChange = { viewModel.onQueryChange(it) },
                        onSearchTriggered = { viewModel.onSearchTriggered() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(24.dp)
                        .background(MyYellow, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Resultados",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = TextBlack,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            SearchTabs(
                activeTab = state.activeTab,
                onTabSelected = { viewModel.onTabSelected(it) },
                videogamesCount = state.videogames.size,
                usersCount = state.users.size
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MyYellowDark)
                        }
                    }
                    state.error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.error,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Red
                            )
                        }
                    }
                    state.query.isBlank() -> {
                        if (state.recentSearches.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Empieza escribiendo para buscar videojuegos o usuarios",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = TextDarkGray)
                                )
                            }
                        } else {
                            RecentSearchesSection(
                                recentSearches = state.recentSearches,
                                onRecentClick = { recent ->
                                    onRecentSearchNavigate(recent)
                                },
                                onRecentDelete = { viewModel.onRecentSearchDeleteClicked(it) },
                                onClearAll = { viewModel.onClearAllRecentSearches() }
                            )
                        }
                    }
                    state.videogames.isEmpty() && state.users.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No se encontraron resultados para \"${state.query}\"",
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextDarkGray)
                            )
                        }
                    }
                    else -> {
                        when (state.activeTab) {
                            SearchTab.VIDEOGAMES -> VideogamesResultList(
                                videogames = state.videogames,
                                onVideogameClick = {
                                    viewModel.onVideogameResultClick(it)
                                    onVideogameClick(it)
                                }
                            )
                            SearchTab.USERS -> UsersResultList(
                                users = state.users,
                                onUserClick = {
                                    viewModel.onUserResultClick(it)
                                    onUserClick(it)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchTriggered: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        textStyle = TextStyle(color = TextBlack),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = MyYellowDark
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpiar",
                        tint = TextDarkGray
                    )
                }
            }
        },
        label = {
            Text(
                text = "Buscar videojuegos o usuarios",
                color = TextDarkGray
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MyYellowDark,
            unfocusedBorderColor = TextDarkGray.copy(alpha = 0.5f),
            focusedLabelColor = MyYellowDark,
            unfocusedLabelColor = TextDarkGray,
            cursorColor = MyYellowDark,
            focusedLeadingIconColor = MyYellowDark,
            unfocusedLeadingIconColor = MyYellowDark,
            focusedTrailingIconColor = TextDarkGray,
            unfocusedTrailingIconColor = TextDarkGray,
            focusedTextColor = TextBlack,
            unfocusedTextColor = TextBlack
        )
    )
}

@Composable
private fun SearchTabs(
    activeTab: SearchTab,
    onTabSelected: (SearchTab) -> Unit,
    videogamesCount: Int,
    usersCount: Int
) {
    val tabs = listOf(SearchTab.VIDEOGAMES, SearchTab.USERS)
    val selectedIndex = tabs.indexOf(activeTab)

    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = Color.Transparent,
        contentColor = TextBlack,
        indicator = { tabPositions ->
            Box {}
        }
    ) {
        tabs.forEachIndexed { index, tab ->
            val selected = index == selectedIndex
            val title = when (tab) {
                SearchTab.VIDEOGAMES -> "Videojuegos ($videogamesCount)"
                SearchTab.USERS -> "Usuarios ($usersCount)"
            }
            Tab(
                selected = selected,
                onClick = { onTabSelected(tab) },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = title,
                            color = if (selected) TextBlack else TextDarkGray,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .height(3.dp)
                                .fillMaxWidth(0.4f)
                                .background(
                                    color = if (selected) Color.Transparent else Color.Transparent,
                                )
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun VideogamesResultList(
    videogames: List<Videogame>,
    onVideogameClick: (Videogame) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(videogames) { game ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onVideogameClick(game) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextBlack,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    if (game.subtitle.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = game.subtitle,
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextDarkGray)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UsersResultList(
    users: List<User>,
    onUserClick: (User) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(users) { user ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onUserClick(user) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = user.username,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextBlack,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    if (user.name.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextDarkGray)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentSearchesSection(
    recentSearches: List<RecentSearch>,
    onRecentClick: (RecentSearch) -> Unit,
    onRecentDelete: (RecentSearch) -> Unit,
    onClearAll: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(24.dp)
                        .background(MyYellow, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Búsquedas recientes",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = TextBlack,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Text(
                text = "Borrar todas",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextDarkGray,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.clickable { onClearAll() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(recentSearches) { recent ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRecentClick(recent) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = LightSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Búsqueda reciente",
                            tint = MyYellowDark
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = recent.displayText,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = TextBlack,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                        IconButton(onClick = { onRecentDelete(recent) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Eliminar búsqueda",
                                tint = TextDarkGray
                            )
                        }
                    }
                }
            }
        }
    }
}
