package com.abdullahhalis.overlai.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.abdullahhalis.overlai.data.local.entity.TranslationHistoryEntity
import com.abdullahhalis.overlai.data.repository.AppRepository
import com.abdullahhalis.overlai.utils.HistoryListItem
import com.abdullahhalis.overlai.utils.toDateLabel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: AppRepository,
) : ViewModel() {
    private val _deleteEvent = Channel<TranslationHistoryEntity>(Channel.BUFFERED)
    val deleteEvent = _deleteEvent.receiveAsFlow()

    val historyPagingData: Flow<PagingData<HistoryListItem>> = repository.getHistoryPaged()
        .map { pagingData ->
            pagingData.map { HistoryListItem.Item(it) }
                .insertSeparators { before, after ->
                    after ?: return@insertSeparators null
                    val afterLabel = after.entity.timestamp.toDateLabel()
                    val beforeLabel = before?.entity?.timestamp?.toDateLabel()
                    HistoryListItem.Separator(afterLabel).takeIf { beforeLabel != afterLabel }
                }
        }.cachedIn(viewModelScope)

    fun undoDelete(entity: TranslationHistoryEntity) {
        viewModelScope.launch {
            repository.insertHistory(entity)
        }
    }

    fun delete(entity: TranslationHistoryEntity) {
        viewModelScope.launch {
            repository.deleteHistory(entity)
            _deleteEvent.send(entity)
        }
    }

    fun deleteAll() {
        viewModelScope.launch { repository.deleteAllHistory() }
    }
}