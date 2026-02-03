---
name: skill-offline-first
description: stale-while-revalidate
---

Regras centrais:
- A UI deve ser guiada por dados locais (offline-first).
- Persistência local primária é via Room (SQLite) com Flows reativos.
- Seed inicial de concursos vem dos assets na primeira execução (fonte read-only).
- Migração legada: caso exista `lottery_data.json` (formato antigo), migrar para o Room no startup e remover o arquivo.
- Sync via WorkManager quando houver rede disponível (atualizar silenciosamente).
- API remota via Retrofit; mapear DTO → entidade/domínio antes de persistir.
- Usar backoff/retry em falhas; nunca bloquear a UI.
- Versionar schema dos assets e documentar compatibilidade (parsers com fallback controlado).
- Acompanhar staleness por modalidade; informar usuário quando dados estiverem desatualizados mantendo o app funcional.
- Repositórios devem emitir atualizações via Flow após sync/seed/migração.

Faça:
- Priorizar integridade e consistência de dados.
- Manter sync resiliente e observável (logs/telemetria quando aplicável).

Não faça:
- Bloquear UI em rede ou I/O longo.
