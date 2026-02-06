package com.cebolao.app.feature.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cebolao.app.core.UiEvent
import com.cebolao.app.util.toUserMessage
import com.cebolao.domain.model.Game
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.repository.LotteryRepository
import com.cebolao.domain.result.AppResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GamesUiState(
    val filterType: LotteryType? = null,
    val savedGames: List<SavedGameCardUiState> = emptyList(),
    val countsByType: Map<LotteryType, Int> = emptyMap(),
    val totalCount: Int = 0,
    val isLoading: Boolean = false,
)

@HiltViewModel
class GamesViewModel
    @Inject
    constructor(
        private val repository: LotteryRepository,
    ) : ViewModel() {
        private val _filterType = MutableStateFlow<LotteryType?>(null)

        // Combina filtro com fluxo do repositório
        private val _uiState = MutableStateFlow(GamesUiState(isLoading = true))
        val uiState: StateFlow<GamesUiState> = _uiState.asStateFlow()

        private val _events = MutableSharedFlow<UiEvent>()
        val events: SharedFlow<UiEvent> = _events.asSharedFlow()

        init {
            observeGames()
        }

        private fun observeGames() {
            viewModelScope.launch {
                combine(repository.observeGames(), _filterType) { games, filter ->
                    val filtered =
                        if (filter == null) {
                            games
                        } else {
                            games.filter { it.lotteryType == filter }
                        }

                    val counts = games.groupingBy { it.lotteryType }.eachCount()

                    GamesUiState(
                        filterType = filter,
                        savedGames = filtered.map { it.toSavedGameCardUiState() },
                        countsByType = counts,
                        totalCount = games.size,
                        isLoading = false,
                    )
                }
                    .catch {
                        emit(
                            GamesUiState(
                                filterType = _filterType.value,
                                savedGames = emptyList(),
                                countsByType = emptyMap(),
                                totalCount = 0,
                                isLoading = false,
                            ),
                        )
                    }
                    .collect { state ->
                        _uiState.value = state
                    }
            }
        }

        fun onFilterChanged(type: LotteryType?) {
            _filterType.value = type
        }

        fun onDeleteGame(game: Game) {
            viewModelScope.launch {
                when (val result = repository.deleteGame(game.id)) {
                    is AppResult.Success -> { /* Remocao bem-sucedida, fluxo reativo atualiza a lista */ }
                    is AppResult.Failure -> {
                        _events.emit(UiEvent.ShowSnackbar(result.error.toUserMessage()))
                    }
                }
            }
        }

        fun onTogglePin(game: Game) {
            viewModelScope.launch {
                when (val result = repository.togglePinGame(game.id)) {
                    is AppResult.Success -> { /* Sucesso, fluxo reativo atualiza a lista */ }
                    is AppResult.Failure -> {
                        _events.emit(UiEvent.ShowSnackbar(result.error.toUserMessage()))
                    }
                }
            }
        }
    }
