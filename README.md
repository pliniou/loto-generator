# Loto Generator (Cebolão)

O **Loto Generator** é um aplicativo Android nativo, desenvolvido em Kotlin e Jetpack Compose, focado na geração, filtragem e conferência de jogos das Loterias CAIXA. O projeto segue uma arquitetura moderna, **Offline-first**, garantindo que o usuário tenha acesso aos seus dados e funcionalidades mesmo sem conexão com a internet.

## 📱 Funcionalidades Principais

### Gerador de Jogos Inteligente
*   **Filtros Configuráveis:** Ajuste fino de paridade (pares/ímpares), múltiplos de 3, moldura/miolo (Lotofácil), números primos e repetições do concurso anterior
*   **Presets Personalizados:** Salve e reutilize configurações frequentes de filtros por modalidade
*   **Relatórios Detalhados:** Quando a geração é parcial, visualize métricas de tentativas, rejeições por filtro e exemplos de jogos rejeitados
*   **Números Fixos:** Force a presença de números específicos em todos os jogos gerados

### Conferidor Automático
*   **Conferência Inteligente:** Compare seus jogos contra resultados oficiais sincronizados automaticamente
*   **Dupla Sena:** Suporte aos 3 modos de conferência (1º sorteio, 2º sorteio, ou melhor dos dois)
*   **Timemania:** Detecção automática de acerto do Time do Coração
*   **Super Sete:** Comparação posicional coluna por coluna

### Sete Modalidades Completas
Suporte total para **Mega-Sena, Lotofácil, Quina, Lotomania, Dupla Sena, Timemania e Super Sete**, cada uma com lógica específica de validação e conferência.

### Offline-First
*   Funciona **100% offline** com banco local (Room)
*   Seed automático de concursos históricos a partir dos assets na primeira execução
*   Sincronização inteligente em segundo plano via WorkManager quando há conexão
*   Migração automática de dados legados (`lottery_data.json`)

### Design Moderno
*   Interface construída com **Material Design 3**
*   Suporte completo a **Dark Mode**
*   Cores seguindo rigorosamente o manual da marca das Loterias CAIXA
*   Acessibilidade e alvos de toque otimizados

## 🛠️ Tech Stack

*   **Linguagem:** Kotlin
*   **UI:** Jetpack Compose (Material 3)
*   **Arquitetura:** Clean Architecture + MVVM
*   **Injeção de Dependência:** Hilt
*   **Concorrência:** Coroutines & Flow
*   **Background Jobs:** WorkManager
*   **Navegação:** Navigation Compose
*   **Build:** Gradle Kotlin DSL

## 🚀 Como Rodar o Projeto

### Pré-requisitos
*   Android Studio Ladybug ou superior.
*   JDK 17 configurado.

### Passos
1.  Clone o repositório.
2.  Abra o projeto no Android Studio.
3.  Aguarde a sincronização do Gradle.
4.  Execute o app no emulador ou dispositivo físico (`Shift + F10`).

### Testes

O projeto possui **cobertura de testes** abrangente para engines de domínio e lógica de negócio.

**Executar todos os testes unitários:**
```bash
./gradlew :app:testDebugUnitTest
```

**Cobertura atual:**
- ✅ `GeneratorEngine` — geração, validação e relatórios
- ✅ `CheckerEngine` — conferência para todas as modalidades
- ✅ `FilterEngine` — validação de filtros configuráveis
- ✅ `AssetsReader` — leitura e parse de fixtures
- ✅ `LotteryRepository` — persistência e sincronização
- ✅ ViewModels — estado de UI e interações

### Personalização Avançada

**Filtros Configuráveis:**
- Ajuste limiares de paridade (min/max de pares vs. ímpares)
- Configure limite de repetições do concurso anterior
- Ative/desative filtros específicos por modalidade
- Acesse via botão **"Configurar filtros"** na tela de geração

**Sistema de Presets:**
- Presets padrão otimizados por modalidade (Lotofácil, Mega-Sena, Quina, Timemania, Super Sete)
- Crie e salve **presets personalizados** com suas configurações favoritas
- Reutilize configurações frequentes com um toque
- Gerencie presets salvos diretamente na interface

**Assets & Schema** 📦
- O leitor de fixtures aceita tanto o array simples quanto um wrapper com `schemaVersion` e `contests` (recomendado). Veja `docs/ASSETS_SCHEMA.md` para detalhes de versão, validação e fallback.
- Em versões legadas, os dados locais eram persistidos em `lottery_data.json`. O app migra automaticamente esse arquivo para o Room no startup quando encontrado.

> **Nota:** O projeto utiliza configurações estritas de versão do Kotlin para garantir estabilidade. Se encontrar problemas de build, verifique se está usando as versões definidas no `gradle/libs.versions.toml` ou `build.gradle.kts`. Também assegure que o JDK esteja instalado e `JAVA_HOME` configurado para rodar os testes localmente.

## 🎨 Design System

As cores e identidade visual seguem rigorosamente o manual da marca das Loterias CAIXA, garantindo fidelidade visual e fácil identificação de cada modalidade.

## 📚 Documentação adicional

- [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) — visão da arquitetura, fluxos e responsabilidades de cada camada
- [`docs/SCREENS.md`](docs/SCREENS.md) — documentação detalhada de telas e componentes
- [`docs/ASSETS_SCHEMA.md`](docs/ASSETS_SCHEMA.md) — schema, versionamento e validação dos assets
- [`docs/DECISIONS.md`](docs/DECISIONS.md) — decisões técnicas e evoluções do projeto
- [`docs/FOUNDATION_AUDIT.md`](docs/FOUNDATION_AUDIT.md) — auditoria de fundação, build e performance

---
*Desenvolvido com foco em qualidade de código e experiência do usuário.*
