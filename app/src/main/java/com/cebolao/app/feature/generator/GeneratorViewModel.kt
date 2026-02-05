package com.cebolao.app.feature.generator

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cebolao.app.util.toUserMessage
import com.cebolao.domain.model.Contest
import com.cebolao.domain.model.FilterConfig
import com.cebolao.domain.model.Game
import com.cebolao.domain.model.GenerationConfig
import com.cebolao.domain.model.GenerationFilter
import com.cebolao.domain.model.GenerationReport
import com.cebolao.domain.model.LotteryProfile
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.model.UserFilterPreset
import com.cebolao.domain.repository.LotteryRepository
import com.cebolao.domain.repository.ProfileRepository
import com.cebolao.domain.repository.UserPresetRepository
import com.cebolao.domain.repository.UserStatisticsRepository
import com.cebolao.domain.result.AppResult
import com.cebolao.domain.model.UserUsageStats
import com.cebolao.domain.rules.FilterPresets
import com.cebolao.domain.usecase.GenerateGamesUseCase
import com.cebolao.app.di.DefaultDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.cebolao.app.core.UiEvent
import javax.inject.Inject

@Immutable
data class GeneratorUiState(
    val selectedType: LotteryType = LotteryType.LOTOFACIL,
    val quantity: Int = 1,
    val profile: LotteryProfile? = null,
    val generatedGames: List<Game> = emptyList(),
    val isLoading: Boolean = false,

    val lastSavedCount: Int = 0,
    val selectedTeam: Int? = null,
    val activeFilters: List<GenerationFilter> = emptyList(),
    val filterConfigs: Map<GenerationFilter, FilterConfig> = emptyMap(),
    val showFilterConfigDialog: Boolean = false,
    val showReportDialog: Boolean = false,
    val generationReport: GenerationReport? = null,
    val userPresets: List<UserFilterPreset> = emptyList(),
    val lastContest: Contest? = null,
    val recommendation: UserUsageStats? = null,
    val activePresetName: String? = null,
)

@HiltViewModel
class GeneratorViewModel
    @Inject
    constructor(
        private val profileRepository: ProfileRepository,
        private val lotteryRepository: LotteryRepository,
        private val userPresetRepository: UserPresetRepository,
        private val userStatisticsRepository: UserStatisticsRepository,
        private val generateGamesUseCase: GenerateGamesUseCase,
        @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(GeneratorUiState())
        val uiState: StateFlow<GeneratorUiState> = _uiState.asStateFlow()

        private val _events = MutableSharedFlow<UiEvent>()
        val events: SharedFlow<UiEvent> = _events.asSharedFlow()

        private var latestContestJob: Job? = null

        init {
            // Carrega perfil inicial
            loadProfile(LotteryType.LOTOFACIL)

            // Observa presets do usuário e atualiza UI
            viewModelScope.launch {
                userPresetRepository.observePresets().collect { presets ->
                    _uiState.value = _uiState.value.copy(userPresets = presets)
                }
            }
        }

        fun onTypeSelected(type: LotteryType) {
            loadProfile(type)
        }

        fun onQuantityChanged(quantity: Int) {
            _uiState.value = _uiState.value.copy(quantity = quantity)
        }

        fun onTeamSelected(teamId: Int?) {
            _uiState.value = _uiState.value.copy(selectedTeam = teamId)
        }

        fun onGenerate() {
            val currentState = _uiState.value
            val profile = currentState.profile ?: return

            viewModelScope.launch {
                _uiState.value = currentState.copy(isLoading = true)
                try {
                    val config =
                        GenerationConfig(
                            quantity = currentState.quantity,
                            filters = currentState.activeFilters,
                            filterConfigs = currentState.filterConfigs,
                            fixedTeam = currentState.selectedTeam,
                            fixedNumbers = emptyList(),
                        )

                    val result =
                        withContext(defaultDispatcher) {
                            delay(800)

                            val lastContest = _uiState.value.lastContest ?: run {
                                when (val result = lotteryRepository.getLastContest(profile.type)) {
                                    is AppResult.Success -> result.value
                                    is AppResult.Failure -> null
                                }
                            }
                            generateGamesUseCase(profile, config, lastContest = lastContest)
                        }

                    val games = result.games

                    if (result.report.partial) {
                        Log.w(
                            "GeneratorVM",
                            "Geração parcial: ${result.report.generated}/${config.quantity} após ${result.report.attempts} tentativas",
                        )
                    }

                    _uiState.value =
                        currentState.copy(
                            generatedGames = games,
                            isLoading = false,
                            lastSavedCount = 0,
                            generationReport = result.report,
                        )
                    currentState.activePresetName?.let { presetName ->
                        userStatisticsRepository.recordUsage(presetName)
                    }
                } catch (e: Exception) {
                    Log.e("GeneratorVM", "Erro ao gerar jogos", e)
                    _uiState.value = currentState.copy(isLoading = false)
                    _events.emit(UiEvent.ShowSnackbar(e.message ?: "Erro ao gerar jogos"))
                }
            }
        }

        fun onSaveAll() {
            val currentState = _uiState.value
            if (currentState.generatedGames.isEmpty()) return

            viewModelScope.launch {
                _uiState.value = currentState.copy(isLoading = true)

                // Busca concursos para estatísticas
                val type = currentState.selectedType
                val recentContests = (lotteryRepository.getRecentContests(type, 10) as? AppResult.Success)?.value ?: emptyList()
                val historyContests = (lotteryRepository.getRecentContests(type, 100) as? AppResult.Success)?.value ?: emptyList()

                val gamesWithStats = currentState.generatedGames.map { game ->
                    game.copy(
                        recentHitRate = com.cebolao.domain.util.StatisticsUtil.calculateHitRate(game.numbers, recentContests),
                        historicalHitRate = com.cebolao.domain.util.StatisticsUtil.calculateHitRate(game.numbers, historyContests),
                        sourcePreset = currentState.activePresetName
                    )
                }

                when (val result = lotteryRepository.saveGames(gamesWithStats)) {
                    is AppResult.Success -> {
                        _uiState.value =
                            currentState.copy(
                                generatedGames = emptyList(),
                                lastSavedCount = currentState.generatedGames.size,
                                isLoading = false,
                            )
                        // Registra quantidade salva para o preset
                        currentState.activePresetName?.let { presetName ->
                            userStatisticsRepository.recordSavedGames(presetName, currentState.generatedGames.size)
                        }
                        _events.emit(UiEvent.ShowSuccess("Jogos salvos com sucesso!"))
                    }
                    is AppResult.Failure -> {
                        Log.e("GeneratorVM", "Erro ao salvar jogos", result.cause)
                        _uiState.value = currentState.copy(isLoading = false)
                        _events.emit(UiEvent.ShowSnackbar(result.error.toUserMessage()))
                    }
                }
            }
        }

        fun onClearGenerated() {
            _uiState.value = _uiState.value.copy(generatedGames = emptyList(), lastSavedCount = 0, generationReport = null)
        }

        fun onFilterToggled(filter: GenerationFilter) {
            val current = _uiState.value
            val newFilters = if (current.activeFilters.contains(filter)) current.activeFilters - filter else current.activeFilters + filter
            _uiState.value = current.copy(activeFilters = newFilters)
        }

        fun onOpenFilterConfig() {
            _uiState.value = _uiState.value.copy(showFilterConfigDialog = true)
        }

        fun onCloseFilterConfig() {
            _uiState.value = _uiState.value.copy(showFilterConfigDialog = false)
        }

        fun onUpdateFilterConfig(
            filter: GenerationFilter,
            cfg: FilterConfig,
        ) {
            val current = _uiState.value
            val newMap = current.filterConfigs.toMutableMap()
            newMap[filter] = cfg
            _uiState.value = current.copy(filterConfigs = newMap)
        }

        fun onApplyPresetForProfile() {
            val current = _uiState.value
            val profile = current.profile ?: return
            val preset = FilterPresets.presetForProfile(profile) ?: return
            _uiState.value =
                current.copy(
                    activeFilters = preset.filters,
                    filterConfigs = preset.configs,
                    activePresetName = "Perfil padrão" // Identificador para o preset do perfil
                )
        }

        fun onApplyUserPreset(preset: UserFilterPreset) {
             _uiState.value =
                _uiState.value.copy(
                    activeFilters = preset.filters,
                    filterConfigs = preset.filterConfigs,
                    activePresetName = preset.name
                )
        }

        fun onOpenReportDetails() {
            _uiState.value = _uiState.value.copy(showReportDialog = true)
        }

        fun onCloseReportDetails() {
            _uiState.value = _uiState.value.copy(showReportDialog = false)
        }

        fun onSaveUserPreset(name: String) {
            val current = _uiState.value
            val preset =
                UserFilterPreset(
                    name = name,
                    filters = current.activeFilters,
                    filterConfigs = current.filterConfigs,
                )
            viewModelScope.launch {
                when (val result = userPresetRepository.savePreset(preset)) {
                    is AppResult.Success -> _events.emit(UiEvent.ShowSuccess("Preset salvo!"))
                    is AppResult.Failure -> _events.emit(UiEvent.ShowSnackbar(result.error.toUserMessage()))
                }
            }
        }

        private fun loadProfile(type: LotteryType) {
            latestContestJob?.cancel()

            viewModelScope.launch {
                val profile = profileRepository.getProfile(type)
                val filteredFilters = _uiState.value.activeFilters.filter { it.isApplicable(profile) }
                val filteredConfigs = _uiState.value.filterConfigs.filterKeys { it.isApplicable(profile) }
                val lastContest = when (val result = lotteryRepository.getLastContest(type)) {
                    is AppResult.Success -> result.value
                    is AppResult.Failure -> null
                }

                _uiState.value =
                    _uiState.value.copy(
                        selectedType = type,
                        profile = profile,
                        generatedGames = emptyList(),
                        lastSavedCount = 0,
                        selectedTeam = null,
                        activeFilters = filteredFilters,
                        filterConfigs = filteredConfigs,
                        lastContest = lastContest,
                        recommendation = null, 
                        activePresetName = null
                    )
                
                // Fetch recommendations
                val recommendation = userStatisticsRepository.getBestPreset(type)
                if (recommendation != null && recommendation.usageCount > 2) {
                     _uiState.value = _uiState.value.copy(recommendation = recommendation)
                }
            }

            // Mantém lastContest reativo aos updates do Room (sync/worker)
            latestContestJob =
                viewModelScope.launch {
                    lotteryRepository.observeLatestContest(type).collect { contest ->
                        _uiState.value = _uiState.value.copy(lastContest = contest)
                    }
                }
        }
    }
