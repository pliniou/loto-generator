package com.cebolao.app.feature.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cebolao.app.util.toUserMessage
import com.cebolao.domain.model.Game
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.repository.LotteryRepository
import com.cebolao.domain.result.AppResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GamesUiState(
    val filterType: LotteryType? = null,
    val games: List<Game> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class GamesViewModel
    @Inject
    constructor(
        private val repository: LotteryRepository,
    ) : ViewModel() {
        private val _filterType = MutableStateFlow<LotteryType?>(null)
        private val _errorMessage = MutableStateFlow<String?>(null)

        // Combina filtro com fluxo do repositório
        @OptIn(ExperimentalCoroutinesApi::class)
        val uiState: StateFlow<GamesUiState> =
            _filterType
                .flatMapLatest { type ->
                    val flow =
                        if (type == null) {
                            repository.observeGames()
                        } else {
                            repository.observeGamesByType(type)
                        }
                    kotlinx.coroutines.flow.combine(
                        flow,
                        _filterType,
                        _errorMessage,
                    ) { games: List<Game>, filter: LotteryType?, error: String? ->
                        GamesUiState(
                            filterType = filter,
                            games =
                                games.sortedWith(
                                    compareByDescending<Game> { it.isPinned }
                                        .thenByDescending { it.createdAt },
                                ),
                            isLoading = false,
                            errorMessage = error,
                        )
                    }
                }
                .catch {
                    emit(GamesUiState(games = emptyList())) // Tratamento básico de erro
                }
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    GamesUiState(isLoading = true), // Carregamento inicial no stateIn não funciona bem com flatMap, mas ok
                )

        fun onFilterChanged(type: LotteryType?) {
            _filterType.value = type
            _errorMessage.value = null
        }

        fun onDeleteGame(game: Game) {
            viewModelScope.launch {
                when (val result = repository.deleteGame(game.id)) {
                    is AppResult.Success -> _errorMessage.value = null
                    is AppResult.Failure -> _errorMessage.value = result.error.toUserMessage()
                }
            }
        }

        fun onTogglePin(game: Game) {
            viewModelScope.launch {
                when (val result = repository.togglePinGame(game.id)) {
                    is AppResult.Success -> _errorMessage.value = null
                    is AppResult.Failure -> _errorMessage.value = result.error.toUserMessage()
                }
            }
        }
    }
