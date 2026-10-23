# Convenções do projeto

## Commits — Conventional Commits

Formato: `tipo(escopo): descrição no imperativo, em minúsculas`

| Tipo | Quando usar |
|---|---|
| `feat` | Nova funcionalidade |
| `fix` | Correção de bug |
| `test` | Adição ou ajuste de testes |
| `refactor` | Mudança interna sem alterar comportamento |
| `docs` | Documentação |
| `build` | Dependências, Maven, npm |
| `ci` | GitHub Actions |
| `chore` | Tarefas de manutenção (configs, formatação) |

Escopos usados: `backend`, `frontend`, `auth`, `campanha`, `personagem`, `sessao`, `chat`, `rolagem`, `combate`, `diario`, `presenca`, `infra`.

Exemplos:

```
feat(rolagem): implementa parser de notação de dados
test(rolagem): cobre notações inválidas no DiceParser
fix(auth): retorna 401 quando o token JWT expira
```

## Branches

- `main` — sempre estável, só recebe merge via Pull Request
- `feat/<nome>` — uma branch por funcionalidade (ex.: `feat/motor-de-dados`)
- `fix/<nome>`, `docs/<nome>`, `ci/<nome>` — idem

Fluxo: criar branch → commits pequenos → abrir PR → CI verde → merge (squash ou merge commit) → apagar a branch.

## Migrations (Flyway)

Uma migration aplicada **nunca** é editada. Toda mudança de schema vira um novo arquivo `V<n>__descricao.sql`.
