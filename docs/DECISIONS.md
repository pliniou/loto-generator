# Decisões de Implementação — Loto Generator (Cebolão)

Data: 2026-01-31

Resumo rápido das alterações e decisões recentes (pt-BR):

- Engine de geração:
  - Adicionada função `generateWithReport` que retorna `GenerationResult (games + GenerationReport)` com métricas de `attempts`, `rejectedByFilter` e flag `partial`.
  - Adicionado `rejectedPerFilter: Map<GenerationFilter, Int>` no `GenerationReport` para permitir diagnóstico por filtro quando a geração é parcial.
  - Limite de tentativas (`MAX_RETRY`) para evitar loops infinitos e permitir fallback quando espaços de combinação esgotam.
  - Fonte de aleatoriedade injetável (`kotlin.random.Random`) para testes determinísticos.

- Checker/Conferência:
  - Suporte explícito a modos da **Dupla Sena** (`1º`, `2º`, `Melhor dos dois`) via `DuplaMode`.
  - `CheckerEngine.check` aceita `duplaMode` (default `BEST`) e retorna `CheckResult` consistente.

- Dados/Assets:
  - `AssetsReader` agora lida com campos opcionais nos assets: `secondDrawNumbers` e `teamNumber` (quando aplicáveis).

- UI:
  - `CheckerScreen` adiciona seleção de modo para Dupla Sena quando aplicável.
  - `GeneratorViewModel` usa `generateWithReport` e a UI exibe relatório de geração parcial (banner + detalhes).

- Testes:
  - Adicionados testes unitários iniciais para `CheckerEngine`, `GeneratorEngine` e `FilterEngine`.

Motivação:
- Garantir comportamento correto por modalidade e facilitar testes e diagnóstico em cenários de geração parcial.

Próximos passos recomendados:
- (Concluído) Expor relatório de geração na UI (banner + detalhes quando parcial).
- (Concluído) Implementar filtros configuráveis com ajuste fino (paridade e repetidas do anterior) e presets.
- Expandir testes com fixtures reais (JSON por modalidade) e mock de `AssetsReader`.
- Revisar e formalizar o schema do assets JSON (versão e validação no startup).

Próximos passos recomendados (atuais):
- Implementar pin/favoritos na UI e ação de toggle no item de jogo.
- Exibir staleness por modalidade na Home (com base na última atualização/sync).
- Consolidar regras por modalidade em um registry/strategy no domínio para reduzir `when`s espalhados.

--

Data: 2026-02-01

Atualizações e correções recentes:

- Filtros de geração:
  - Correção do nome interno do filtro "Moldura e miolo" com compatibilidade para presets já salvos.
  - Aplicabilidade por modalidade (ex.: Moldura/Miolo apenas Lotofácil; paridade e múltiplos não aplicáveis ao Super Sete).
  - Rótulos e descrições centralizados em recursos de string.

- Validações de geração:
  - Validação explícita de `quantity`, `fixedNumbers` e `fixedTeam`.
  - Proteção contra números fixos duplicados ou fora do range do profile.
- Modelos de domínio:
  - `teamNumber` agora validado entre 1..80 em `Game` e `Contest`.

- Conferidor:
  - Correção do jogo temporário com `createdAt` válido (uso interno no `StatisticsEngine`).
  - Validação de seleção respeitando `numbersPerGame` e regras do profile.

- UI e i18n:
  - Remoção de textos hardcoded em telas/diálogos (strings centralizadas).
  - Ajuste de mensagens e conteúdo descritivo em PT-BR.

- Dados e assets:
  - Log em produção ao falhar leitura de assets (evita crash e facilita diagnóstico).
  - Schema de assets revisado e documentado.
- Persistência / Offline-first:
  - Seed inicial de concursos via assets diretamente no Room (startup não depende de rede).
  - Migração automática de `lottery_data.json` (legado) para o Room no startup, quando presente.
  - Repositório ganhou `observeLatestContest` para obter o “último concurso” sem depender de ordenação de listas.
- UI e recursos:
  - Padronização de chaves de strings e correção de referências inconsistentes (evita erros de build).
- Integração/API:
  - Slug da Super Sete confirmado como `super-sete` para consultas na API.
- Timemania:
  - Normalização de nomes/UF para mapear times recebidos da API com maior robustez.

Próximos passos recomendados:
- Revisar comentários remanescentes em inglês e padronizar para PT-BR.
- Criar testes específicos para aplicabilidade de filtros por modalidade.
- Definir estratégia de migração para nomes de filtros serializados (quando necessário).

--
