package com.cebolao.app.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cebolao.app.util.toUserMessage
import com.cebolao.domain.model.Contest
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.repository.LotteryRepository
import com.cebolao.domain.repository.observeLatestContests
import com.cebolao.domain.result.AppResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val contests: Map<LotteryType, Contest?> = emptyMap(),
    val isSyncing: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val repository: LotteryRepository,
    ) : ViewModel() {
        private val _isSyncing = MutableStateFlow(false)
        private val _errorMessage = MutableStateFlow<String?>(null)

        // Agregando fluxos de todas as loterias em um único estado
        val uiState: StateFlow<HomeUiState> =
            combine(
                _isSyncing,
                _errorMessage,
                repository.observeLatestContests(LotteryType.entries),
            ) { syncing, error, contestsMap ->
                HomeUiState(
                    contests = contestsMap,
                    isSyncing = syncing,
                    errorMessage = error,
                )
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                HomeUiState(),
            )

        fun refreshData() {
            viewModelScope.launch {
                _isSyncing.value = true
                when (val result = repository.refresh()) {
                    is AppResult.Success -> _errorMessage.value = null
                    is AppResult.Failure -> {
                        Log.e("HomeVM", "Erro no refresh", result.cause)
                        _errorMessage.value = result.error.toUserMessage()
                    }
                }
                _isSyncing.value = false
            }
        }

        init {
            refreshData()
        }
    }
