# Schema dos Assets

Formato esperado para os arquivos de concursos em `app/src/main/assets/{modalidade}.json`.

Esses arquivos são usados para **seed inicial** do banco local (Room) na primeira execução e também como base para testes/fixtures.

## Formatos suportados

1. **Array simples**:
```json
[
  {
    "id": 123,
    "date": "01/01/2026",
    "numbers": [1,2,3,4,5,6]
  }
]
```

1. **Wrapper com versão de schema** (recomendado):
```json
{
  "schemaVersion": "1.0",
  "contests": [
    {
      "id": 123,
      "date": "01/01/2026",
      "numbers": [1,2,3,4,5,6]
    }
  ]
}
```

## Campos do Contest

- `id`: inteiro > 0
- `date`: string (dd/MM/yyyy ou ISO-8601)
- `numbers`: array de inteiros (tamanho compatível com o profile da modalidade)
- `secondDrawNumbers`: array de inteiros (opcional, apenas Dupla Sena)
- `teamNumber`: inteiro (opcional, apenas Timemania)

Observações:
- `lotteryType` não é enviado no JSON; é inferido pelo nome do arquivo.
- A ordenação dos números é normalizada na leitura.

## Validações aplicadas na leitura

- `id` precisa ser positivo.
- `date` não pode ser vazio.
- `numbers` não pode ser vazio.
- `secondDrawNumbers`, quando presente, não pode ser vazio.

## Comportamento de versão

- `schemaVersion` suportado: **"1.0"**.
- Se o `schemaVersion` for diferente:
  - **DEBUG:** o app lança exceção para facilitar correção.
  - **Release:** o arquivo é ignorado e um log de erro é emitido.

## Fixtures de teste

Manter fixtures em `app/src/test/resources/fixtures/`.
