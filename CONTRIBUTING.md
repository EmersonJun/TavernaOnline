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

- `main` — versão estável, recebe o merge final
- `master` — branch de desenvolvimento onde todos os commits são feitos

Fluxo: commits na `master` → CI verde → merge da `master` na `main` ao final.

## Migrations (Flyway)

Uma migration aplicada **nunca** é editada. Toda mudança de schema vira um novo arquivo `V<n>__descricao.sql`.
