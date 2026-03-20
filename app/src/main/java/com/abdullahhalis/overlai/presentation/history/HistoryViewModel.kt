package com.abdullahhalis.overlai.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdullahhalis.overlai.data.local.entity.TranslationHistoryEntity
import com.abdullahhalis.overlai.data.repository.AppRepository
import com.abdullahhalis.overlai.presentation.ui.common.UiState
import com.abdullahhalis.overlai.utils.groupByDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: AppRepository
): ViewModel() {

    private val _uiHistory = MutableStateFlow<List<TranslationHistoryEntity>?>(null)

    private var pendingDelete: TranslationHistoryEntity? = null

    private val _deleteEvent = Channel<TranslationHistoryEntity>(Channel.BUFFERED)
    val deleteEvent = _deleteEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            repository.getHistory().collect { dbList ->
                _uiHistory.value = pendingDelete?.let { pending ->
                        dbList.filter { it.id != pending.id }
                    } ?: dbList
            }
        }
    }

    val historyUiState = _uiHistory
        .map { list ->
            if (list == null) UiState.Loading
            else UiState.Success(list.groupByDate())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    fun removeFromUi(entity: TranslationHistoryEntity) {
        pendingDelete = entity

        _uiHistory.value = _uiHistory.value?.filter { it.id != entity.id }

        viewModelScope.launch {
            _deleteEvent.send(entity)
        }
    }

    fun confirmDelete() {
        pendingDelete?.let {
            viewModelScope.launch {
                repository.deleteHistoryById(it.id)
            }
        }
        pendingDelete = null
    }

    fun undoDelete() {
        pendingDelete?.let { entity ->
            _uiHistory.value =
                (_uiHistory.value.orEmpty() + entity)
                    .sortedByDescending { it.timestamp }
        }

        pendingDelete = null
    }

    fun deleteAll() {
        viewModelScope.launch { repository.deleteAllHistory() }
    }
}