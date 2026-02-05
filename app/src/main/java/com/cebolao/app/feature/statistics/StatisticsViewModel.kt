package com.cebolao.app.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cebolao.app.di.DefaultDispatcher
import com.cebolao.domain.model.DistributionStats
import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.model.NumberStat
import com.cebolao.domain.repository.LotteryRepository
import com.cebolao.domain.repository.ProfileRepository
import com.cebolao.domain.usecase.CalculateStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class StatisticsUiState(
    val selectedType: LotteryType = LotteryType.MEGA_SENA,
    val profile: LotteryProfile? = null,
    val numberStats: List<NumberStat> = emptyList(),
    val distributionStats: DistributionStats? = null,
    val isLoading: Boolean = false,
    val totalContestsAnalyzed: Int = 0,
    val contestRange: Int = 100, // Default to last 100 contests
)

@HiltViewModel
class StatisticsViewModel
    @Inject
    constructor(
        private val repository: LotteryRepository,
        private val profileRepository: ProfileRepository,
        private val calculateStatisticsUseCase: CalculateStatisticsUseCase,
        @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(StatisticsUiState())
        val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

        private var currentJob: Job? = null

        init {
            loadStatistics(LotteryType.MEGA_SENA)
        }

        fun onTypeSelected(type: LotteryType) {
            if (_uiState.value.selectedType != type) {
                _uiState.value = _uiState.value.copy(selectedType = type, isLoading = true)
                loadStatistics(type)
            }
        }

        fun onRangeSelected(range: Int) {
            if (_uiState.value.contestRange != range) {
                _uiState.value = _uiState.value.copy(contestRange = range, isLoading = true)
                loadStatistics(_uiState.value.selectedType)
            }
        }

        private fun loadStatistics(type: LotteryType) {
            currentJob?.cancel()
            currentJob =
                viewModelScope.launch {
                    _uiState.value = _uiState.value.copy(isLoading = true)

                    val profile = profileRepository.getProfile(type)

                    // Collect contests flow
                    // Note: In a real scenario we might want to limit the query at the DB level,
                    // but for now we'll take from the flow and limit locally.
                    repository.observeContests(type).collect { allContests ->
                        val range = _uiState.value.contestRange
                        val contestsToAnalyze = allContests.take(range) // Assuming list is sorted desc or we prefer latest

                        val (numberStats, distribution) =
                            withContext(defaultDispatcher) {
                                // Heavy aggregation stays off the main thread.
                                val computedNumberStats =
                                    calculateStatisticsUseCase.calculateNumberStats(
                                        contestsToAnalyze,
                                        profile,
                                    )
                                val computedDistribution =
                                    calculateStatisticsUseCase.calculateDistributionStats(
                                        contestsToAnalyze,
                                        profile,
                                    )
                                computedNumberStats to computedDistribution
                            }

                        _uiState.value =
                            _uiState.value.copy(
                                profile = profile,
                                numberStats = numberStats.sortedByDescending { it.frequency },
                                distributionStats = distribution,
                                totalContestsAnalyzed = contestsToAnalyze.size,
                                isLoading = false,
                            )
                    }
                }
        }
    }
