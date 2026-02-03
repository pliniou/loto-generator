package com.cebolao.app.feature.checker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cebolao.domain.model.Contest
import com.cebolao.domain.model.DuplaMode
import com.cebolao.domain.model.Game
import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.model.NumberStat
import com.cebolao.domain.model.PrizeStat
import com.cebolao.domain.repository.LotteryRepository
import com.cebolao.domain.repository.ProfileRepository
import com.cebolao.domain.usecase.CalculateStatisticsUseCase
import com.cebolao.domain.usecase.CheckGameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckerUiState(
    val selectedType: LotteryType = LotteryType.LOTOFACIL,
    val selectedNumbers: List<Int> = emptyList(),
    val checkResult: CheckerResult? = null,
    val lastContest: Contest? = null,
    val selectedTeam: Int? = null,
    val teamHit: Boolean? = null,
    val profile: LotteryProfile? = null,
    val selectedDuplaMode: DuplaMode = DuplaMode.BEST,
    val analysisResults: List<PrizeStat> = emptyList(),
    val historyResults: List<HistoryHit> = emptyList(),
    val bestHit: Int = 0,
    val prizeCount: Int = 0,
    val totalContestsChecked: Int = 0,
    val numberStats: List<NumberStat> = emptyList(),
    val isAnalyzing: Boolean = false,
    val showAnalysisDialog: Boolean = false,
)

data class CheckerResult(
    val hits: Int,
    val teamHit: Boolean?,
    val contestNumber: Int,
    val contestDate: String,
    val prizeTier: Int = 0,
    val isPrize: Boolean = false,
)

data class HistoryHit(
    val contestNumber: Int,
    val contestDate: String,
    val hits: Int,
    val prizeTier: Int,
    val isPrize: Boolean,
)

@HiltViewModel
class CheckerViewModel
    @Inject
    constructor(
        private val repository: LotteryRepository,
        private val profileRepository: ProfileRepository,
        private val checkGameUseCase: CheckGameUseCase,
        private val calculateStatisticsUseCase: CalculateStatisticsUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(CheckerUiState())
        val uiState: StateFlow<CheckerUiState> = _uiState.asStateFlow()
        private var contestsJob: Job? = null

        // Cache for analysis to avoid re-collecting massive flows
        private var cachedContests: List<Contest> = emptyList()

        init {
            loadData(LotteryType.LOTOFACIL)
        }

        fun onTypeSelected(type: LotteryType) {
            val initialSelection = if (type == LotteryType.SUPER_SETE) List(7) { -1 } else emptyList()

            _uiState.value =
                _uiState.value.copy(
                    selectedType = type,
                    selectedNumbers = initialSelection,
                    checkResult = null,
                    lastContest = null,
                    selectedTeam = null,
                    teamHit = null,
                    profile = null,
                    selectedDuplaMode = DuplaMode.BEST,
                    analysisResults = emptyList(),
                    historyResults = emptyList(),
                    bestHit = 0,
                    prizeCount = 0,
                    totalContestsChecked = 0,
                    numberStats = emptyList(),
                    showAnalysisDialog = false,
                )
            loadData(type)
        }

        fun onDuplaModeSelected(mode: DuplaMode) {
            _uiState.value = _uiState.value.copy(selectedDuplaMode = mode, checkResult = null)
        }

        fun onNumberToggle(number: Int) {
            if (_uiState.value.selectedType == LotteryType.SUPER_SETE) return

            val currentList = _uiState.value.selectedNumbers.toMutableList()
            val maxAllowed = _uiState.value.profile?.numbersPerGame
            if (currentList.contains(number)) {
                currentList.remove(number)
            } else {
                if (maxAllowed != null && currentList.size >= maxAllowed) return
                currentList.add(number)
                currentList.sort()
            }
            _uiState.value = _uiState.value.copy(selectedNumbers = currentList, checkResult = null)
        }

        fun onSuperSeteNumberSelected(
            colIndex: Int,
            number: Int,
        ) {
            if (_uiState.value.selectedType != LotteryType.SUPER_SETE) return

            val currentList = _uiState.value.selectedNumbers.toMutableList()
            if (currentList.size != 7) return

            if (currentList[colIndex] == number) {
                currentList[colIndex] = -1
            } else {
                currentList[colIndex] = number
            }
            _uiState.value = _uiState.value.copy(selectedNumbers = currentList, checkResult = null)
        }

        fun onTeamSelected(teamId: Int) {
            _uiState.value = _uiState.value.copy(selectedTeam = teamId, checkResult = null)
        }

        fun onCheck() {
            val state = _uiState.value
            val contest = state.lastContest ?: return
            val profile = state.profile ?: return
            val gameNumbers = state.selectedNumbers

            if (gameNumbers.isEmpty()) return
            if (!profile.isValidGame(gameNumbers)) return

            val tempGame =
                Game(
                    id = "temp",
                    lotteryType = state.selectedType,
                    numbers = gameNumbers,
                    teamNumber = state.selectedTeam,
                    createdAt = System.currentTimeMillis(),
                )

            // Resultado contra o último concurso (exibição principal)
            val latestResult = checkGameUseCase(tempGame, contest, profile, state.selectedDuplaMode)

            // Conferência contra todos os concursos disponíveis
            val history =
                cachedContests.map { c ->
                    val r = checkGameUseCase(tempGame, c, profile, state.selectedDuplaMode)
                    HistoryHit(
                        contestNumber = c.id,
                        contestDate = c.drawDate,
                        hits = r.hits,
                        prizeTier = r.prizeTier,
                        isPrize = r.isPrize,
                    )
                }

            val bestHit = history.maxOfOrNull { it.hits } ?: 0
            val prizeCount = history.count { it.isPrize }

            // Estatísticas de frequência/atraso das dezenas para insights
            val numberStats = calculateStatisticsUseCase.calculateNumberStats(cachedContests, profile)

            _uiState.value =
                state.copy(
                    checkResult =
                        CheckerResult(
                            hits = latestResult.hits,
                            // BUG FIX: Only set teamHit if the lottery actually has a team (Timemania)
                            teamHit = if (profile.hasTeam) latestResult.teamHit else null,
                            contestNumber = contest.id,
                            contestDate = contest.drawDate,
                            prizeTier = latestResult.prizeTier,
                            isPrize = latestResult.isPrize,
                        ),
                    historyResults = history.sortedByDescending { it.contestNumber },
                    bestHit = bestHit,
                    prizeCount = prizeCount,
                    totalContestsChecked = history.size,
                    numberStats = numberStats,
                )
        }

        fun onAnalyzeHistory() {
            val state = _uiState.value
            val profile = state.profile ?: return
            val gameNumbers = state.selectedNumbers

            // Basic validation before analysis
            if (state.selectedType == LotteryType.SUPER_SETE) {
                if (gameNumbers.any { it == -1 }) return
            } else {
                if (gameNumbers.isEmpty()) return
            }

            viewModelScope.launch {
                _uiState.value = state.copy(isAnalyzing = true)
                // Use cached data for heavy calculation
                val stats = calculateStatisticsUseCase.checkHistory(gameNumbers, cachedContests, profile)
                _uiState.value =
                    _uiState.value.copy(
                        analysisResults = stats,
                        isAnalyzing = false,
                        // Inline display - no dialog needed
                    )
            }
        }

        fun onCloseAnalysis() {
            _uiState.value = _uiState.value.copy(showAnalysisDialog = false)
        }

        private fun loadData(type: LotteryType) {
            contestsJob?.cancel()
            cachedContests = emptyList()

            viewModelScope.launch {
                val profile = profileRepository.getProfile(type)
                _uiState.value = _uiState.value.copy(profile = profile)
            }
            contestsJob =
                viewModelScope.launch {
                    repository.observeContests(type).collect { contests ->
                        cachedContests = contests
                        // Flow já vem ordenado por contestNumber DESC; usar o primeiro garante o mais recente.
                        val latest = contests.firstOrNull()
                        _uiState.value = _uiState.value.copy(lastContest = latest)
                    }
                }
        }
    }
