# Decisões técnicas

Registro das principais escolhas de projeto e o raciocínio por trás delas.

## Backend

### Organização por funcionalidade (package-by-feature)
Cada domínio (`auth`, `campanha`, `personagem`, `sessao`, `chat`, `rolagem`, `diario`) agrupa seu controller, service, repository, DTOs e mapper. As camadas continuam existindo dentro de cada pacote. Isso mantém o código de um mesmo assunto junto e facilita a navegação.

### IDs em UUID
Todas as entidades usam UUID como chave primária. Evita que recursos sejam enumerados pela URL (`/campanhas/1`, `/campanhas/2`…) e simplifica eventuais integrações distribuídas.

### Flyway com `ddl-auto: validate`
Quem cria e evolui o schema é o Flyway, com migrations versionadas (`V1`, `V2`, …). O Hibernate fica em modo `validate`: ele apenas confere se o mapeamento das entidades bate com o banco, mas nunca altera tabelas. Uma migration já aplicada nunca é editada — toda mudança vira um novo arquivo.

### Papel do mestre na tabela de participação
O papel (`MESTRE` / `JOGADOR`) fica na entidade `Participacao`, não como campo da `Campanha`. Assim, qualquer verificação de permissão é uma única consulta booleana (`existsByUsuarioIdAndCampanhaIdAndPapel`).

### Tratamento global de exceções
Um `@RestControllerAdvice` central traduz exceções de domínio em respostas HTTP padronizadas: `404` (não encontrado), `409` (regra de negócio / conflito de versão), `403` (acesso negado), `400` (validação e notação de dados inválida), `401` (credenciais).

### Optimistic lock nas fichas
`Personagem` tem um campo `@Version`. Se dois jogadores abrem a mesma ficha e salvam, o segundo recebe `409 Conflict` em vez de sobrescrever as mudanças do primeiro.

### Armadilha do `@ElementCollection`
Perícias e inventário são coleções de *value objects* (`@ElementCollection`). Ao atualizar, o código limpa a coleção existente (`clear()` + `addAll()`) em vez de trocar a referência da lista — trocar a referência quebra o gerenciamento do Hibernate e gera erro de *orphan*.

### Motor de dados com aleatoriedade injetada
O `DiceRoller` recebe um `RandomGenerator` pelo construtor. Em produção é um `SecureRandom`; nos testes, um gerador de valores fixos. Resultado: as rolagens ficam determinísticas e testáveis, sem `mock` de métodos estáticos.

## Tempo real

### STOMP sobre WebSocket
O canal de tempo real usa STOMP, que dá um modelo de publicação/assinatura pronto: `/topic/...` para broadcast e `/user/queue/...` para mensagens direcionadas a um usuário.

### Autenticação no handshake
Um `ChannelInterceptor` valida o JWT no comando `CONNECT` e define o `Principal` da conexão. É o mesmo token do REST, reaproveitado. Sem token válido, a conexão não sobe.

### Rolagem secreta via `/user/queue`
Como o `Principal` é setado no handshake, o servidor consegue enviar uma rolagem secreta apenas para o(s) mestre(s) com `convertAndSendToUser`, sem que os jogadores recebam qualquer coisa.

### Presença em memória
A lista de quem está online é um dado efêmero. Fica num `ConcurrentHashMap`, alimentado pela entrada na sala e por um listener de `SessionDisconnectEvent`. Se o projeto precisasse escalar horizontalmente, esse estado migraria para o Redis.

## Frontend

### Standalone components
Angular 18 sem NgModules. Cada rota usa `loadComponent` (lazy loading), reduzindo o bundle inicial.

### Interceptors funcionais
`jwtInterceptor` injeta o token em toda requisição; `errorInterceptor` centraliza o tratamento de erro (snackbar + logout no `401`).

### WebSocket encapsulado
O `WebsocketService` esconde o STOMP atrás de um `Observable`. Os componentes só fazem `observar('/topic/...')` e `subscribe`, com a mesma mentalidade dos services HTTP.

### Banco local em vez de Docker
O projeto roda com um PostgreSQL instalado localmente, mantendo o setup simples. No CI, o GitHub Actions sobe um PostgreSQL próprio como *service container*, então os testes de integração rodam sem depender da máquina do desenvolvedor.
