# Roadmap

O projeto é entregue em 7 commits, cada um deixando a aplicação compilando e com testes passando.
O commit 1 fica na `main`. Os commits 2 a 7 são feitos na branch `master` e, ao final, a `master` é mergeada na `main`.

| # | Commit | Conteúdo |
|---|---|---|
| 1 | `chore: estrutura inicial do repositório` | README, LICENSE, convenções, configurações do Git e do editor |
| 2 | `feat(backend): fundação da api com autenticação jwt` | Spring Boot, PostgreSQL, Flyway, auditoria, exceções globais, Swagger, Spring Security, cadastro e login, CI |
| 3 | `feat(backend): campanhas com convite e fichas de personagem` | Campanha, Participacao, Personagem, permissões de mestre e jogador |
| 4 | `feat(backend): motor de dados, sessões e diário da campanha` | Parser de notação de dados, Sessao, Anotacao |
| 5 | `feat(backend): sala em tempo real com chat, rolagens, presença e combate` | WebSocket/STOMP, MensagemChat, Rolagem, jogadores online, tracker de iniciativa |
| 6 | `feat(frontend): aplicação angular da mesa virtual` | Angular 18, Material, autenticação, campanhas, fichas, sala de sessão, diário, testes |
| 7 | `docs: documentação final do projeto` | CI do frontend, diagrama de arquitetura, prints, decisões técnicas, release v1.0.0 |
