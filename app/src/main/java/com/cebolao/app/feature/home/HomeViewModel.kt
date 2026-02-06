package com.cebolao.app.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cebolao.app.core.UiEvent
import com.cebolao.app.util.toUserMessage
import com.cebolao.domain.error.AppError
import com.cebolao.domain.model.Contest
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.repository.LotteryRepository
import com.cebolao.domain.repository.observeLatestContests
import com.cebolao.domain.result.AppResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val contests: Map<LotteryType, Contest?> = emptyMap(),
    val isSyncing: Boolean = false,
    val isLoading: Boolean = false, // Initial load or empty state load
    val error: AppError? = null,
    val upcomingDrawsExpanded: Boolean = false,
    val latestResultsExpanded: Boolean = false,
)

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val repository: LotteryRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(HomeUiState())
        val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

        private val _events = MutableSharedFlow<UiEvent>()
        val events: SharedFlow<UiEvent> = _events.asSharedFlow()

        init {
            observeContests()
            refreshData()
        }

        private fun observeContests() {
            viewModelScope.launch {
                repository.observeLatestContests(LotteryType.entries)
                    .collect { contestsMap ->
                        _uiState.value = _uiState.value.copy(contests = contestsMap)
                    }
            }
        }

        fun toggleUpcomingDrawsExpanded() {
            _uiState.value = _uiState.value.copy(
                upcomingDrawsExpanded = !_uiState.value.upcomingDrawsExpanded
            )
        }

        fun toggleLatestResultsExpanded() {
            _uiState.value = _uiState.value.copy(
                latestResultsExpanded = !_uiState.value.latestResultsExpanded
            )
        }
            fun refreshData() {
            viewModelScope.launch {
                val hasLocalData =
                    when (val local = repository.getLastContest(LotteryType.LOTOFACIL)) {
                        is AppResult.Success -> local.value != null
                        is AppResult.Failure -> false
                    }
                val hasAnyContest = hasLocalData || _uiState.value.contests.values.any { it != null }
                val isInitial = !hasAnyContest
                _uiState.value =
                    _uiState.value.copy(
                        isSyncing = true,
                        isLoading = isInitial,
                        error = null,
                    )
                when (val result = repository.refresh()) {
                    is AppResult.Success -> {
                        _uiState.value =
                            _uiState.value.copy(
                                isSyncing = false,
                                isLoading = false,
                            )
                    }
                    is AppResult.Failure -> {
                        Log.e("HomeVM", "Erro no refresh", result.cause)
                        _uiState.value =
                            _uiState.value.copy(
                                isSyncing = false,
                                isLoading = false,
                                error = result.error,
                            )
                        // Show snackbar only if we have content (otherwise ErrorState is shown)
                        if (!isInitial) {
                            _events.emit(UiEvent.ShowSnackbar(result.error.toUserMessage()))
                        }
                    }
                }
            }
        }
    }
