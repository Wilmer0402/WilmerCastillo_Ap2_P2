package edu.ucne.wilmercastillo_ap2_p2.presentation.repository

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.wilmercastillo_ap2_p2.data.remote.dto.RepositoryDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepositoryListScreen(
    drawerState: DrawerState,
    scope: CoroutineScope,
    viewModel: RepositoryViewModel = hiltViewModel()
    // createRepository: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var lastRetentionCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.getRepository("enelramon")
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
        reloadRepository = { viewModel.getRepository("enelramon") }
        // createRepository = createRepository
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun RepositoryListBodyScreen(
    drawerState: DrawerState,
    scope: CoroutineScope,
    uiState: RepositoryUiState,
    reloadRepository: () -> Unit
    // createRepository: () -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isLoading,
        onRefresh = reloadRepository
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Lista de Repositorios",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Crear nuevo repositorio")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
                .padding(paddingValues)
        ) {
            if (uiState.repository.isEmpty() && !uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se han encontrado repositorios",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.repository) { repository ->
                        RepositoryRow(repository)
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = uiState.isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun RepositoryRow(repository: RepositoryDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Name: ")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                        append(repository.name)
                    }
                },
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Description: ")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                        append(repository.description ?: "Sin descripción")
                    }
                },
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Html URL: ")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                        append(repository.htmlUrl ?: "No disponible")
                    }
                },
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
