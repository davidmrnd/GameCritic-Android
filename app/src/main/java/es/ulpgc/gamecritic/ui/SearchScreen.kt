package es.ulpgc.gamecritic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import es.ulpgc.gamecritic.model.RecentSearch
import es.ulpgc.gamecritic.model.User
import es.ulpgc.gamecritic.model.Videogame
import es.ulpgc.gamecritic.viewmodel.SearchState
import es.ulpgc.gamecritic.viewmodel.SearchTab
import es.ulpgc.gamecritic.viewmodel.SearchViewModel
import es.ulpgc.gamecritic.viewmodel.SearchViewModelFactory

private val LightBackground = Color(0xFFF0ECE3)
private val LightSurface = Color(0xFFFFFFFF)
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SearchHeaderBackground()

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(140.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-40).dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = MyYellowDark.copy(alpha = 0.5f)
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = LightSurface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Juegos y comunidad",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = TextBlack,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            SearchBar(
                                query = state.query,
                                onQueryChange = { viewModel.onQueryChange(it) },
                                onSearchTriggered = { viewModel.onSearchTriggered() }
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset(y = (-20).dp)
                    ) {
                        Column {
                            if (state.query.isNotEmpty() || state.videogames.isNotEmpty() || state.users.isNotEmpty()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
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
                            }

                            Box(modifier = Modifier.weight(1f)) {
                                SearchContentState(
                                    state = state,
                                    viewModel = viewModel,
                                    onVideogameClick = onVideogameClick,
                                    onUserClick = onUserClick,
                                    onRecentSearchNavigate = onRecentSearchNavigate
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text = "BUSCADOR",
                style = MaterialTheme.typography.displaySmall.copy(
                    color = TextBlack,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                ),
                modifier = Modifier
                    .padding(top = 40.dp, start = 24.dp)
            )
        }
    }
}

@Composable
fun SearchHeaderBackground() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(MyYellow, MyYellowDark),
                        startY = 0f,
                        endY = 1000f
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-40).dp)
                .background(Color.White.copy(alpha = 0.15f), CircleShape)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, LightBackground)
                    )
                )
        )
    }
}

@Composable
private fun SearchContentState(
    state: SearchState,
    viewModel: SearchViewModel,
    onVideogameClick: (Videogame) -> Unit,
    onUserClick: (User) -> Unit,
    onRecentSearchNavigate: (RecentSearch) -> Unit
) {
    when {
        state.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MyYellowDark)
            }
        }
        state.error != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.error, color = Color.Red)
            }
        }
        state.query.isBlank() -> {
            if (state.recentSearches.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TextDarkGray.copy(alpha = 0.3f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Empieza a buscar...",
                            style = MaterialTheme.typography.bodyLarge.copy(color = TextDarkGray)
                        )
                    }
                }
            } else {
                RecentSearchesSection(
                    recentSearches = state.recentSearches,
                    onRecentClick = onRecentSearchNavigate,
                    onRecentDelete = { viewModel.onRecentSearchDeleteClicked(it) },
                    onClearAll = { viewModel.onClearAllRecentSearches() }
                )
            }
        }
        state.videogames.isEmpty() && state.users.isEmpty() -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay resultados", color = TextDarkGray)
            }
        }
        else -> {
            when (state.activeTab) {
                SearchTab.VIDEOGAMES -> VideogamesResultList(
                    videogames = state.videogames,
                    onVideogameClick = { game ->
                        viewModel.onVideogameResultClick(game)
                        onVideogameClick(game)
                    }
                )
                SearchTab.USERS -> UsersResultList(
                    users = state.users,
                    onUserClick = { user ->
                        viewModel.onUserResultClick(user)
                        onUserClick(user)
                    }
                )
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
            Icon(Icons.Default.Search, contentDescription = "Buscar", tint = MyYellowDark)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Limpiar", tint = TextDarkGray)
                }
            }
        },
        placeholder = { Text("Buscar", color = TextDarkGray.copy(alpha = 0.5f)) },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MyYellowDark,
            unfocusedBorderColor = TextDarkGray.copy(alpha = 0.3f),
            cursorColor = MyYellowDark,
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
        indicator = { tabPositions -> Box {} },
        divider = {}
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
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        if (selected) {
                            Box(
                                modifier = Modifier
                                    .height(3.dp)
                                    .width(40.dp)
                                    .background(MyYellow, RoundedCornerShape(1.5.dp))
                            )
                        }
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
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(videogames) { game ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onVideogameClick(game) }
                    .shadow(2.dp, RoundedCornerShape(12.dp), spotColor = TextDarkGray.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = LightSurface)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(
                            text = game.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextBlack)
                        )
                        if (game.subtitle.isNotBlank()) {
                            Text(
                                text = game.subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(color = TextDarkGray)
                            )
                        }
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
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(users) { user ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onUserClick(user) }
                    .shadow(2.dp, RoundedCornerShape(12.dp), spotColor = TextDarkGray.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = LightSurface)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_myplaces),
                        contentDescription = null,
                        tint = MyYellowDark,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = user.username,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold, color = TextBlack)
                    )
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
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.width(4.dp).height(24.dp).background(MyYellow, RoundedCornerShape(2.dp)))
                Spacer(Modifier.width(8.dp))
                Text("Recientes", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextBlack))
            }
            Text(
                "Borrar todo",
                style = MaterialTheme.typography.bodySmall.copy(color = TextDarkGray, fontWeight = FontWeight.Bold),
                modifier = Modifier.clickable { onClearAll() }
            )
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(recentSearches) { recent ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onRecentClick(recent) },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = TextDarkGray, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(recent.displayText, modifier = Modifier.weight(1f), color = TextBlack)
                        IconButton(onClick = { onRecentDelete(recent) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Clear, contentDescription = null, tint = TextDarkGray, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}