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
    val matchedNumbers: Set<Int> = emptySet(),
    val statsFilter: StatsFilter = StatsFilter.ALL,
    val isContestSelectorExpanded: Boolean = false,
    val contestSearchQuery: String = "",
    val availableContests: List<Contest> = emptyList(),
)

enum class StatsFilter(val labelRes: Int) {
    ALL(com.cebolao.R.string.filter_all),
    LAST_10(com.cebolao.R.string.filter_last_10),
    LAST_20(com.cebolao.R.string.filter_last_20),
    LAST_50(com.cebolao.R.string.filter_last_50),
    LAST_100(com.cebolao.R.string.filter_last_100),
}

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
        private var hasAppliedPrefill = false

        init {
            loadData(LotteryType.LOTOFACIL)
        }

        fun onTypeSelected(type: LotteryType) {
            _uiState.value =
                _uiState.value.resetForType(
                    type = type,
                    selectedNumbers = initialSelectionFor(type),
                    selectedTeam = null,
                    isAnalyzing = _uiState.value.isAnalyzing,
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

        fun applyPrefillOnce(
            type: LotteryType,
            numbers: List<Int>,
            teamNumber: Int?,
        ) {
            if (hasAppliedPrefill) return
            hasAppliedPrefill = true
            prefillGame(type, numbers, teamNumber)
        }

        fun onCheck() {
            val state = _uiState.value
            val contest = state.lastContest ?: return
            val profile = state.profile ?: return
            val gameNumbers = state.selectedNumbers

            if (gameNumbers.isEmpty()) return
            if (!profile.isValidGame(gameNumbers)) return

            val gameToCheck = buildTransientGame(state)

            val latestResult = checkGameUseCase(gameToCheck, contest, profile, state.selectedDuplaMode)

            val history =
                cachedContests.map { c ->
                    val r = checkGameUseCase(gameToCheck, c, profile, state.selectedDuplaMode)
                    HistoryHit(
                        contestNumber = c.id,
                        contestDate = c.drawDate,
                        hits = r.hits,
                        prizeTier = r.prizeTier,
                        isPrize = r.isPrize,
                    )
                }

            val filteredHistory = applyFilter(history, state.statsFilter)
            val filteredContests = applyContestFilter(cachedContests, state.statsFilter)

            val bestHit = filteredHistory.maxOfOrNull { it.hits } ?: 0
            val prizeCount = filteredHistory.count { it.isPrize }

            val numberStats = calculateStatisticsUseCase.calculateNumberStats(filteredContests, profile)

            val resultNumbers = contest.getAllNumbers().toSet()
            val matched = gameNumbers.filter { it in resultNumbers }.toSet()

            _uiState.value =
                state.copy(
                    checkResult =
                        CheckerResult(
                            hits = latestResult.hits,
                            teamHit = if (profile.hasTeam) latestResult.teamHit else null,
                            contestNumber = contest.id,
                            contestDate = contest.drawDate,
                            prizeTier = latestResult.prizeTier,
                            isPrize = latestResult.isPrize,
                        ),
                    matchedNumbers = matched,
                    historyResults = filteredHistory.sortedByDescending { it.contestNumber },
                    bestHit = bestHit,
                    prizeCount = prizeCount,
                    totalContestsChecked = filteredHistory.size,
                    numberStats = numberStats,
                )
        }

        fun onStatsFilterSelected(filter: StatsFilter) {
            _uiState.value = _uiState.value.copy(statsFilter = filter)
            // Re-run check if we already have a result, to update stats based on new filter
            if (_uiState.value.checkResult != null) {
                onCheck()
            }
        }

        fun onContestSearchQueryChanged(query: String) {
            _uiState.value = _uiState.value.copy(contestSearchQuery = query)
        }

        fun onContestSelected(contest: Contest) {
            _uiState.value =
                _uiState.value.copy(
                    lastContest = contest,
                    isContestSelectorExpanded = false,
                    contestSearchQuery = "",
                    checkResult = null, // Reset result when changing contest
                    matchedNumbers = emptySet(),
                )
        }

        fun onContestSelectorToggle(expanded: Boolean) {
            _uiState.value = _uiState.value.copy(isContestSelectorExpanded = expanded)
        }

        private fun applyFilter(history: List<HistoryHit>, filter: StatsFilter): List<HistoryHit> {
            return applyLatestFilter(history, filter) { it.contestNumber }
        }

        private fun applyContestFilter(contests: List<Contest>, filter: StatsFilter): List<Contest> {
            return applyLatestFilter(contests, filter) { it.id }
        }

        fun onAnalyzeHistory() {
            val state = _uiState.value
            val profile = state.profile ?: return
            val gameNumbers = state.selectedNumbers

            if (state.selectedType == LotteryType.SUPER_SETE) {
                if (gameNumbers.any { it == -1 }) return
            } else {
                if (gameNumbers.isEmpty()) return
            }

            viewModelScope.launch {
                _uiState.value = state.copy(isAnalyzing = true)
                val stats = calculateStatisticsUseCase.checkHistory(gameNumbers, cachedContests, profile)
                _uiState.value =
                    _uiState.value.copy(
                        analysisResults = stats,
                        isAnalyzing = false,
                    )
            }
        }

        fun onCloseAnalysis() {
            _uiState.value = _uiState.value.copy(showAnalysisDialog = false)
        }

        private fun prefillGame(
            type: LotteryType,
            numbers: List<Int>,
            teamNumber: Int?,
        ) {
            val normalizedNumbers =
                if (type == LotteryType.SUPER_SETE) {
                    val padded = numbers.take(7).toMutableList()
                    while (padded.size < 7) padded.add(-1)
                    padded.toList()
                } else {
                    numbers.sorted()
                }

            _uiState.value =
                _uiState.value.resetForType(
                    type = type,
                    selectedNumbers = normalizedNumbers,
                    selectedTeam = teamNumber,
                    isAnalyzing = false,
                )
            loadData(type)
        }

        private fun initialSelectionFor(type: LotteryType): List<Int> =
            if (type == LotteryType.SUPER_SETE) List(7) { -1 } else emptyList()

        private fun buildTransientGame(state: CheckerUiState): Game =
            Game(
                id = TEMP_GAME_ID,
                lotteryType = state.selectedType,
                numbers = state.selectedNumbers,
                teamNumber = state.selectedTeam,
                createdAt = System.currentTimeMillis(),
            )

        private fun CheckerUiState.resetForType(
            type: LotteryType,
            selectedNumbers: List<Int>,
            selectedTeam: Int?,
            isAnalyzing: Boolean,
        ): CheckerUiState =
            copy(
                selectedType = type,
                selectedNumbers = selectedNumbers,
                checkResult = null,
                lastContest = null,
                selectedTeam = if (type == LotteryType.TIMEMANIA) selectedTeam else null,
                teamHit = null,
                profile = null,
                selectedDuplaMode = DuplaMode.BEST,
                analysisResults = emptyList(),
                historyResults = emptyList(),
                bestHit = 0,
                prizeCount = 0,
                totalContestsChecked = 0,
                numberStats = emptyList(),
                isAnalyzing = isAnalyzing,
                showAnalysisDialog = false,
                matchedNumbers = emptySet(),
                statsFilter = StatsFilter.ALL,
                isContestSelectorExpanded = false,
                contestSearchQuery = "",
                availableContests = emptyList(),
            )

        private fun <T> applyLatestFilter(
            items: List<T>,
            filter: StatsFilter,
            contestNumberSelector: (T) -> Int,
        ): List<T> {
            val sorted = items.sortedByDescending(contestNumberSelector)
            val limit = filter.itemLimit ?: return sorted
            return sorted.take(limit)
        }

        private val StatsFilter.itemLimit: Int?
            get() =
                when (this) {
                    StatsFilter.ALL -> null
                    StatsFilter.LAST_10 -> 10
                    StatsFilter.LAST_20 -> 20
                    StatsFilter.LAST_50 -> 50
                    StatsFilter.LAST_100 -> 100
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
                        val latest = contests.firstOrNull()
                        val currentLast = _uiState.value.lastContest
                        // Keep current selected contest if it exists, otherwise use latest
                        val activeContest = if (currentLast != null && contests.any { it.id == currentLast.id }) currentLast else latest

                        _uiState.value = _uiState.value.copy(
                            lastContest = activeContest,
                            availableContests = contests
                        )
                    }
                }
        }

        companion object {
            private const val TEMP_GAME_ID = "temp"
        }
    }
