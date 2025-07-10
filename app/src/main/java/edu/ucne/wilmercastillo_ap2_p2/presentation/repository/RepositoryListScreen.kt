
package edu.ucne.wilmercastillo_ap2_p2.presentation.repository

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.wilmercastillo_ap2_p2.data.remote.dto.RepositoryDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepositoryListScreen(
    drawerState: DrawerState,
    scope: CoroutineScope,
    viewModel: RepositoryViewModel = hiltViewModel(),
    goToRepository: (String) -> Unit,
    createRepository: () -> Unit,
    deleteRepository: ((RepositoryDto) -> Unit)? = null,
    goToContributors: (String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var lastRetentionCount by remember { mutableStateOf(0) }

    //Para la busqueda
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        delay(180000)
        viewModel.onEvent(RepositoryEvent.GetRepositories)
    }

    LaunchedEffect(uiState.repository) {
        if (uiState.repository.size > lastRetentionCount) {
            Toast.makeText(
                context,
                "Nueva repository: ${uiState.repository.lastOrNull()?.description}",
                Toast.LENGTH_LONG
            ).show()
        }
        lastRetentionCount = uiState.repository.size
    }

    RepositoryListBodyScreen(
        drawerState = drawerState,
        scope = scope,
        uiState = uiState,
        reloadRepository = { viewModel.onEvent(RepositoryEvent.GetRepositories) },
        goToRepository = goToRepository,
        createRepository = createRepository,
        deleteRepository = deleteRepository,
        query = query,
        searchResults = searchResults,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        goToContributors = goToContributors
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun RepositoryListBodyScreen(
    drawerState: DrawerState,
    scope: CoroutineScope,
    uiState: RepositoryUiState,
    reloadRepository: () -> Unit,
    goToRepository: (String) -> Unit,
    createRepository: () -> Unit,
    deleteRepository: ((RepositoryDto) -> Unit)? = null,
    query: String,
    searchResults: List<RepositoryDto>,
    onSearchQueryChanged: (String) -> Unit,
    goToContributors: (String, String) -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isLoading,
        onRefresh = reloadRepository
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Lista de Repositorios",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(
                        onClick = reloadRepository,
                        enabled = !uiState.isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = createRepository,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, "Crear nuevo repositorio")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            when {
                uiState.isLoading && uiState.repository.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Cyan
                    )
                }

                uiState.repository.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron repositorios",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        //Para la busqueda
                        item {
                            SearchBar(
                                query = query,
                                onQueryChanged = onSearchQueryChanged
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        val reposToShow = if (query.isNotBlank()) searchResults else uiState.repository

                        items(reposToShow) { repository ->
                            RepositoryCard(
                                repository = repository,
                                goToRepository = { goToRepository(repository.name) },
                                deleteRepository = deleteRepository,
                                { goToContributors("enelramon", repository.name) }

                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = uiState.isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                contentColor = Color.Cyan
            )

            if (!uiState.errorMessage.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                ) {
                    Text(
                        text = uiState.errorMessage,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun RepositoryCard(
    repository: RepositoryDto,
    goToRepository: () -> Unit,
    deleteRepository: ((RepositoryDto) -> Unit)?,
    onViewContributors: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = repository.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Description: ")
                        }
                        append(repository.description ?: "Sin descripción")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Html URL: ")
                        }
                        append(repository.htmlUrl ?: "Sin URL")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    overflow = TextOverflow.Ellipsis
                )

                TextButton(
                    onClick = onViewContributors,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "Ver Colaboradores",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

//Para la busqueda
@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit
) {
    androidx.compose.material3.OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        label = { Text("Buscar repositorio...") },
        singleLine = true
    )
}