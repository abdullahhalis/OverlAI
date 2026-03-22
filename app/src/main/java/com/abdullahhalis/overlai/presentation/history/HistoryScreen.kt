package com.abdullahhalis.overlai.presentation.history

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.HistoryToggleOff
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.abdullahhalis.overlai.data.local.entity.TranslationHistoryEntity
import com.abdullahhalis.overlai.presentation.history.component.HistoryItem
import com.abdullahhalis.overlai.presentation.ui.common.LoadingScreen
import com.abdullahhalis.overlai.utils.HistoryListItem

@Composable
fun HistoryScreen(
    navigateToMain: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val historyPagingItems = viewModel.historyPagingData.collectAsLazyPagingItems()

    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.deleteEvent.collect { deletedEntity ->
            val result = snackbarHostState.showSnackbar(
                message = "Translation deleted",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoDelete(deletedEntity)
            }
        }
    }

    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false},
            title = { Text("Delete All History") },
            text = { Text("Are you sure? This action cannot be undone")},
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAll()
                        showDeleteAllDialog = false
                    }
                ) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    HistoryContent(
        historyPagingItems,
        snackbarHostState,
        onDelete = { showDeleteAllDialog = true },
        onSwipeToDelete = { viewModel.delete(it) },
        navigateToMain,
        modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryContent(
    historyPagingItems: LazyPagingItems<HistoryListItem>,
    snackbarHostState: SnackbarHostState,
    onDelete: () -> Unit,
    onSwipeToDelete: (TranslationHistoryEntity) -> Unit,
    navigateToMain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text("History", fontWeight = FontWeight.Bold)
                },
                actions = {
                    if (historyPagingItems.itemCount > 0) {
                        TextButton(onClick = onDelete) {
                            Text("Clear All")
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navigateToMain() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            historyPagingItems.loadState.refresh is LoadState.Loading &&
                    historyPagingItems.itemCount == 0 -> {
                LoadingScreen(modifier.padding(innerPadding))
            }

            historyPagingItems.itemCount == 0 -> {
                Box(
                    modifier = modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.HistoryToggleOff,
                            contentDescription = null,
                            modifier = Modifier.size(96.dp)
                        )
                        Text(
                            text = "No History Yet",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Your translated phrases will appear here once you start translating using the overlay",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                        )
                        Button(
                            onClick = navigateToMain,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(top = 24.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.RocketLaunch,
                                    contentDescription = "start translating"
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Start Translating",
                                )
                            }
                        }
                    }
                }
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    items(
                        count = historyPagingItems.itemCount,
                        key = { index ->
                            when (val item = historyPagingItems.peek(index)) {
                                is HistoryListItem.Item -> item.entity.id
                                is HistoryListItem.Separator -> "sep_${item.label}_$index"
                                null -> "null_$index"
                            }
                        },
                        contentType = { index ->
                            historyPagingItems.peek(index)?.let { it::class.simpleName } ?: "null"
                        }
                    ) { index ->
                        val animatedModifier = Modifier.animateItem(
                            fadeInSpec = tween(durationMillis = 300),
                            fadeOutSpec = tween(durationMillis = 200),
                            placementSpec = spring(
                                stiffness = Spring.StiffnessMediumLow
                            )
                        )

                        when (val item = historyPagingItems[index]) {
                            is HistoryListItem.Item -> {
                                HistoryItem(
                                    historyEntity = item.entity,
                                    onDelete = { onSwipeToDelete(item.entity) },
                                    modifier = animatedModifier
                                )
                            }
                            is HistoryListItem.Separator -> {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = animatedModifier
                                        .padding(vertical = 8.dp)
                                )
                            }
                            else -> {}
                        }
                    }

                    if (historyPagingItems.loadState.append is LoadState.Loading) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        }

                    }
                }
            }
        }
    }
}