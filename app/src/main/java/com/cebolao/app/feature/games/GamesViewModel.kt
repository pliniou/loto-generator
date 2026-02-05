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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GamesUiState(
    val filterType: LotteryType? = null,
    val games: List<Game> = emptyList(),
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

        @OptIn(ExperimentalCoroutinesApi::class)
        private fun observeGames() {
            viewModelScope.launch {
                _filterType
                    .flatMapLatest { type ->
                        if (type == null) {
                            repository.observeGames()
                        } else {
                            repository.observeGamesByType(type)
                        }
                    }
                    .catch {
                        // Em caso de erro no flow, emite lista vazia para não quebrar a UI
                        emit(emptyList())
                    }
                    .collect { games ->
                        _uiState.value =
                            _uiState.value.copy(
                                games = games,
                                isLoading = false,
                            )
                    }
            }
        }

        fun onFilterChanged(type: LotteryType?) {
            _filterType.value = type
            _uiState.value = _uiState.value.copy(filterType = type)
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
