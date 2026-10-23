# Roadmap de commits

Cada bloco é uma **branch + Pull Request**. Cada linha é **um commit**.
A ordem respeita dependências: nada é usado antes de existir.

---

## 0. Repositório — `main` (commit direto)

1. `chore: estrutura inicial do repositório`
   README, LICENSE, .gitignore, .gitattributes, .editorconfig, .env.example, CONTRIBUTING, ROADMAP, template de PR

---

## Fase 1 — Fundação do backend

### PR #1 `feat/backend-setup`
2. `build(backend): cria projeto Spring Boot 3 com Java 21`
3. `feat(backend): configura conexão com PostgreSQL local`
4. `feat(backend): configura Flyway e perfis de ambiente`
5. `feat(backend): adiciona EntidadeBase com auditoria JPA`
6. `feat(backend): implementa tratamento global de exceções`
7. `docs(backend): configura Swagger/OpenAPI`

### PR #2 `ci/github-actions`
8. `ci: adiciona workflow de build e testes do backend com PostgreSQL de serviço`

### PR #3 `feat/autenticacao`
9. `feat(auth): cria entidade Usuario e migration`
10. `feat(auth): implementa geração e validação de JWT`
11. `feat(auth): configura Spring Security stateless`
12. `feat(auth): adiciona endpoints de cadastro e login`
13. `test(auth): testes unitários do AuthService`
14. `test(auth): testes de integração com banco de teste`

---

## Fase 2 — Domínio

### PR #4 `feat/campanhas`
15. `feat(campanha): cria entidades Campanha e Participacao`
16. `feat(campanha): implementa criação e listagem de campanhas`
17. `feat(campanha): permite entrar em campanha por código de convite`
18. `feat(campanha): adiciona verificação de papel (mestre/jogador)`
19. `test(campanha): cobre regras de convite e permissão`

### PR #5 `feat/fichas`
20. `feat(personagem): cria entidade Personagem com atributos e perícias`
21. `feat(personagem): adiciona inventário`
22. `feat(personagem): CRUD de fichas com MapStruct`
23. `feat(personagem): aplica lock otimista na edição`
24. `test(personagem): testes do PersonagemService`

### PR #6 `feat/motor-de-dados` ⭐
25. `feat(rolagem): implementa parser de notação de dados`
26. `feat(rolagem): implementa rolagem com gerador injetável`
27. `test(rolagem): cobre notações válidas e expressões compostas`
28. `test(rolagem): cobre notações inválidas e limites`

### PR #7 `feat/sessoes`
29. `feat(sessao): cria entidade Sessao e ciclo de vida`
30. `feat(diario): implementa anotações por sessão`
31. `test(sessao): testes de sessão e diário`

---

## Fase 3 — Tempo real

### PR #8 `feat/websocket`
32. `feat(sessao): configura WebSocket com STOMP`
33. `feat(auth): autentica conexões WebSocket via JWT`
34. `feat(chat): implementa chat da sessão`
35. `feat(rolagem): publica rolagens públicas e secretas do mestre`
36. `feat(presenca): rastreia jogadores online na sala`

### PR #9 `feat/combate`
37. `feat(combate): cria tracker de iniciativa`
38. `feat(combate): implementa avanço de turnos e rodadas`
39. `test(combate): cobre ordenação e troca de turno`

---

## Fase 4 — Frontend

### PR #10 `feat/frontend-setup`
40. `build(frontend): cria projeto Angular 18 com Material`
41. `feat(frontend): adiciona interceptors de JWT e erros`
42. `feat(frontend): implementa telas de login e cadastro`

### PR #11 `feat/frontend-campanhas`
43. `feat(frontend): lista e cria campanhas`
44. `feat(frontend): entrada por código de convite`
45. `feat(frontend): ficha de personagem editável`

### PR #12 `feat/frontend-sala`
46. `feat(frontend): conecta à sala via STOMP`
47. `feat(frontend): chat e painel de rolagens`
48. `feat(frontend): tracker de iniciativa`
49. `feat(frontend): lista de jogadores online`
50. `feat(frontend): diário da campanha`
51. `test(frontend): testes dos principais componentes`

---

## Fase 5 — Entrega

### PR #13 `ci/frontend`
52. `ci: adiciona build e testes do frontend ao workflow`
53. `build(frontend): configura proxy do Angular para a API local`
54. `chore(backend): adiciona perfil de produção`

### PR #14 `docs/readme`
55. `docs: adiciona diagrama de arquitetura`
56. `docs: adiciona prints e instruções de execução`
57. `docs: documenta decisões técnicas`

### Release
- Tag `v1.0.0` + GitHub Release com changelog
