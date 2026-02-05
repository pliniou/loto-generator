# Foundation Audit - Build, Architecture, and Performance

Date: 2026-02-05

## Scope
- Full project structure review (`app`, `baselineprofile`, Gradle root).
- Build configuration and manifest hardening.
- Architecture sanity check (data/domain/presentation boundaries).
- Global performance pass (startup, main-thread work, recomposition risks).

## Implemented In This Phase

### Build and Tooling Stability
- Added root-level Kotlin compiler defaults for consistency across modules:
  - `-Xjsr305=strict`
  - `-Xjvm-default=all`
  - File: `build.gradle.kts`
- Strengthened Gradle configuration cache/build performance properties:
  - `org.gradle.configuration-cache.problems=warn`
  - `android.nonFinalResIds=true`
  - `kotlin.caching.enabled=true`
  - File: `gradle.properties`
- App build hardening:
  - `release.isDebuggable = false`
  - Lint release checking explicitly enabled
  - Removed duplicate Material 3 dependency declaration to avoid version skew with Compose BOM
  - File: `app/build.gradle.kts`

### Manifest and Security Baseline
- Explicitly disabled backup/export of app data:
  - `android:allowBackup="false"`
  - `android:fullBackupContent="false"`
  - File: `app/src/main/AndroidManifest.xml`

### Performance Quick Wins
- Statistics screen switched to lifecycle-aware collection:
  - `collectAsStateWithLifecycle()`
  - File: `app/src/main/java/com/cebolao/app/feature/statistics/StatisticsScreen.kt`
- Statistics heavy aggregation moved off main thread:
  - `withContext(defaultDispatcher)` around number/distribution calculations
  - File: `app/src/main/java/com/cebolao/app/feature/statistics/StatisticsViewModel.kt`
- Removed redundant re-sorting in games flow (DAO already sorts):
  - File: `app/src/main/java/com/cebolao/app/feature/games/GamesViewModel.kt`
- Removed artificial generation delay in generator path:
  - File: `app/src/main/java/com/cebolao/app/feature/generator/GeneratorViewModel.kt`

## Validation Checkpoint

### Successful
- `:app:assembleDebug`
- `:app:testDebugUnitTest`
- `:baselineprofile:assembleBenchmarkRelease`
- `:baselineprofile:assembleNonMinifiedRelease`
- `:app:lintDebug`

### Environment Constraint Observed
- `:baselineprofile:connectedNonMinifiedReleaseAndroidTest` failed in this environment due to no connected device (expected for connected benchmark tests).

## Architecture Misalignments Observed

### 1) Presentation Layer Uses Domain Utilities/Rules Directly
- Examples:
  - `GeneratorViewModel` uses `FilterPresets` and `StatisticsUtil`
  - UI components/screens import domain utility classes directly
- Impact:
  - Tight coupling between UI and domain implementation details.
  - Harder test isolation and future modularization.

### 2) App Module Contains Data + Domain + Presentation In One Module
- Clean architecture is package-layered but not module-layered.
- Impact:
  - Build graph is coarse; changes in one layer invalidate others.
  - Boundary violations are not enforced by module dependencies.

### 3) Application Startup Orchestration Is App-Centric
- `CebolaoApplication` directly triggers initialization and scheduling.
- Impact:
  - Startup responsibilities are concentrated in app bootstrap.
  - Harder to evolve to deferred/lazy startup policies.

## Performance Hotspots Observed (Remaining)

### 1) Startup Cost Concentration
- Data initializer and sync scheduling kick off from `Application.onCreate`.
- Recommendation:
  - Keep only essential startup logic on cold start.
  - Defer non-critical startup work by app state/connectivity/user action.

### 2) Large List Processing in ViewModels
- Statistics and game-related transforms can still be heavy as dataset grows.
- Recommendation:
  - Keep expensive transforms on background dispatcher.
  - Consider DB-level bounded queries for statistics to reduce memory churn.

### 3) Compose Recomposition Surfaces
- Some screens hold broad state and compute derived data in composables.
- Recommendation:
  - Promote stable UI models and memoized selectors where needed.
  - Use compiler reports (`cebolao.enableComposeReports=true`) to target hotspots.

## Recommended Next Phases

1. Modularization foundation:
- Split into `:core:model`, `:core:common`, `:domain`, `:data`, `:feature:*`, `:app`.
- Enforce dependency direction with module boundaries.

2. Domain/application use-case boundary cleanup:
- Move UI-facing domain utility calls into use cases/interactors.
- Expose screen-specific UI models from presentation mappers.

3. Startup optimization phase:
- Introduce startup tracing and lazy initialization strategy.
- Revisit WorkManager scheduling trigger timing and initialization breadth.

4. Data/perf scaling:
- Add targeted Room indexing/migrations for high-frequency queries.
- Push contest range filtering into DAO queries for statistics.

5. Lint debt burn-down:
- Address `lintDebug` warning backlog (unused resources, icon placement, version drift warnings).
- Enable stricter lint gates incrementally.
