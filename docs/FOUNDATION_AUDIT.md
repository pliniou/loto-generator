# Auditoria de Fundação (build, arquitetura, performance)

Data: 2026-02-01

Este documento consolida a auditoria inicial do projeto com foco em:
1) estabilidade/configuração de build, 2) arquitetura base, 3) características globais de performance.

## 1) Estrutura atual (estado)

- Módulos Gradle: `:app` (único módulo).
- Camadas por pacote (no mesmo módulo):
  - `com.cebolao.domain`: modelos + engines (lógica de negócio).
  - `com.cebolao.data`: local/remoto/repos/workers (IO e persistência).
  - `com.cebolao.app`: UI Compose + navegação + ViewModels + DI.

## 2) Build/Gradle (estado e melhorias aplicadas)

Estado observado:
- AGP e Kotlin já modernos (AGP 8.7.x, Kotlin 2.0.x), `compileSdk/targetSdk = 35`, Java 17.
- Performance de build já favorecida via `configuration-cache`, `parallel`, `caching` e `nonTransitiveRClass`.

Melhorias aplicadas nesta fase:
- Centralização de versões via Version Catalog (`gradle/libs.versions.toml`), reduzindo drift e melhorando manutenção.
- `kotlin { jvmToolchain(17) }` no módulo `:app` para consistência de toolchain.
- `org.gradle.vfs.watch=true` em `gradle.properties` para builds incrementais mais rápidos (quando suportado).

Roadmap de build (fases futuras):
- Extrair “convention plugins” (ex.: `build-logic`) quando houver múltiplos módulos.
- Adicionar Baseline Profile / macrobenchmark (se objetivo incluir perf/startup em produção).
- Considerar Dependency Analysis (ex.: remover dependências não usadas) e version alignment por “platforms”.

## 3) Manifests (estado)

Estado observado:
- Manifest do `:app` está enxuto e alinhado a boas práticas:
  - `android:allowBackup="false"` e `android:usesCleartextTraffic="false"`.
  - `android:exported="true"` apenas para a `MainActivity` (LAUNCHER).
  - Permissões mínimas: `INTERNET` e `ACCESS_NETWORK_STATE`.

Roadmap (fases futuras):
- Adicionar “App Links”/“Deep Links” somente quando necessário, evitando declarações extras.
- Reavaliar backups (caso exista requisito de backup/restore) com estratégia explícita.

## 4) Arquitetura (alinhamentos e desalinhamentos)

Pontos fortes:
- Domínio bem modelado (engines e modelos) e UI por feature (`app/feature/*`) com ViewModels.
- Repositório principal implementa um padrão reativo (`StateFlow`) e persistência local.

Desalinhamentos (importantes, mas não bloqueantes nesta fase):
- **Limites de camada não são enforceáveis**: como `domain`, `data` e `app` estão no mesmo módulo, qualquer dependência cruzada é tecnicamente possível.
- **Bootstrap e seed/migração**: garantir que o startup não dependa de rede e que migrações legadas ocorram antes da UI (hoje o `DataInitializer` migra `lottery_data.json` e faz seed de assets diretamente no Room).

Estratégia corretiva (fases futuras):
1) Modularizar:
   - `:domain` (Kotlin/JVM puro), `:data` (Android library), `:app` (application).
2) Mover contratos para `:domain`:
   - Repositórios e “use cases” (orquestração) no domínio.
3) Tornar bootstrap explícito:
   - Criar um contrato de inicialização (ex.: `AppBootstrapper`/`DataBootstrapper`) ou expor `initialize(data)` via interface adequada.
4) Isolar UI do `data`:
   - ViewModels dependem de interfaces/use cases, não de implementações.

## 5) Performance (auditoria global e ações)

Áreas analisadas:
- Startup: trabalho em `Application.onCreate`, inicialização de dados, agendamento de WorkManager.
- Main thread: chamadas potencialmente pesadas e IO indevido.
- Compose: recomposição, coleta de flows, alocações em composição.

Otimizações “fáceis e de alto impacto” priorizadas nesta fase:
- Trocar coletas de `StateFlow` na UI para APIs lifecycle-aware (`collectAsStateWithLifecycle`) para evitar trabalho quando a tela não está ativa.

Roadmap de performance (fases futuras):
- Startup: mover work de agendamento/boot para “after first frame” quando fizer sentido, e medir com Macrobenchmark.
- Compose: marcar tipos estáveis (quando aplicável), reduzir alocações dentro de `@Composable`, usar `derivedStateOf` para cálculos derivados.
- Dados: avaliar tamanho do dataset (concursos por modalidade no Room) e política de retenção/evicção se crescer.
