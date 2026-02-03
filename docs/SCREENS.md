# Documentação de Telas

## Onboarding

**Objetivo:** apresentar proposta do app e principais funcionalidades.

**Componentes principais:**
- `HorizontalPager` com 4 páginas informativas.
- Indicadores de página e botões (Próximo / Pular / Começar).

**Melhorias sugeridas:**
- Persistir a última página para retomar caso o app feche.
- Adicionar animação leve nos elementos (entrada/saída).
- Validar contraste em dark mode nas ilustrações.

## Início (Home)

**Objetivo:** visão geral dos últimos concursos por modalidade.

**Componentes principais:**
- `WelcomeBanner` com calendário semanal.
- Lista de cards por modalidade (`LotteryCard`).
- FAB para sincronização manual.

**Melhorias sugeridas:**
- Exibir “dados desatualizados” por modalidade (staleness).
- Destacar a próxima data de sorteio por modalidade.
- Incluir atalho direto para o Conferidor com último concurso selecionado.

## Gerador

**Objetivo:** gerar jogos com filtros por modalidade.

**Componentes principais:**
- Seletor de modalidade (chips).
- Configuração de quantidade e custo total.
- Filtros configuráveis (com presets) + diálogo de “Configurar filtros” (sliders e toggles).
- Relatório de geração parcial (banner + detalhes).
- Time do Coração para Timemania.

**Melhorias sugeridas:**
- Mostrar contador de seleção ativa por filtro.
- Exibir alertas de “filtros restritivos” antes de gerar.
- Permitir salvamento de “configuração de geração” por modalidade.

## Jogos (Meus Jogos)

**Objetivo:** listar, filtrar e gerenciar jogos salvos.

**Componentes principais:**
- Filtro por modalidade.
- Lista de jogos com data e ações.

**Melhorias sugeridas:**
- Implementar pin/favoritos na UI (já existe no modelo; o Room já ordena `pin` primeiro).
- Agrupar por modalidade e/ou data.
- Adicionar ação de compartilhar jogo.

## Conferidor

**Objetivo:** conferir jogos manuais contra o último concurso.

**Componentes principais:**
- Seletor de modalidade.
- Grid de números (ou colunas para Super Sete).
- Seleção de modo Dupla Sena.
- Seleção de Time do Coração (Timemania).
- Resultado com badge de premiação.

**Melhorias sugeridas:**
- Mostrar quantidade de números selecionados vs. requerida.
- Exibir acertos por faixa (quando houver mais detalhes).
- Permitir conferência contra concursos anteriores.

## Sobre

**Objetivo:** detalhes do app e informações das modalidades.

**Componentes principais:**
- Header do app com versão.
- Cards informativos por modalidade.
- Regras de bolão e disclaimer.

**Melhorias sugeridas:**
- Link para termos e política de privacidade.
- Atualização automática da versão via BuildConfig.
- Separar seção “Fontes de dados”.
