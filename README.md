<div align="center">

<img src="docs/banner.png" alt="TavernaOnline — Sua mesa de RPG, sem limites" width="100%">

### Mesa virtual para sessões de RPG de mesa, em tempo real

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-18-DD0031?logo=angular&logoColor=white)](https://angular.dev/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/license-MIT-blue)](LICENSE)

Mestre cria a campanha, convida os jogadores por um código, e todos jogam juntos numa sala com **chat, rolagem de dados, tracker de iniciativa e diário** — tudo sincronizado em tempo real via WebSocket.

</div>

---

## 📖 Sobre

O **TavernaOnline** é uma aplicação full-stack que simula uma mesa de RPG online. O foco do projeto é demonstrar, de ponta a ponta, decisões de arquitetura reais: autenticação com JWT, comunicação em tempo real com STOMP sobre WebSocket, um motor de dados testável e uma separação limpa entre camadas.

> Projeto de portfólio construído em etapas pequenas e bem documentadas, pensado para ser explicado em entrevista.

---

## 🖼️ Demonstração

> Interface **"Reino das Bandeiras Vermelhas"**: quase-preto com carmesim de dragão, títulos em Cinzel e UI em Inter. Login em split-screen, salão de campanhas com selo de convite, ficha de personagem com barra de vida e perícias, e a sala ao vivo com tracker de iniciativa e cartões de rolagem (as secretas visíveis só para o mestre).

<div align="center">

### Login
<img src="docs/screenshots/login.png" alt="Tela de login" width="720">

### Campanhas — criar, entrar por convite e abrir sessões
<img src="docs/screenshots/campanhas.png" alt="Tela de campanhas" width="820">

### Ficha de personagem editável
<img src="docs/screenshots/ficha.png" alt="Ficha de personagem" width="820">

### Sala de sessão em tempo real
<img src="docs/screenshots/sala.png" alt="Sala de sessão com chat, dados, online e combate" width="820">

</div>

---

## ✨ Funcionalidades

- [x] Cadastro e login com **JWT**
- [x] Campanhas com **convite por código**
- [x] **Fichas de personagem** editáveis (atributos, perícias, inventário, pontos de vida)
- [x] **Sala de sessão em tempo real** (WebSocket / STOMP)
- [x] Rolagem de dados com notação (`2d6+3`, `1d20+1d4+2`) e **rolagens secretas do mestre**
- [x] **Tracker de iniciativa** e controle de turnos
- [x] **Diário da campanha** com anotações públicas ou só-do-mestre
- [x] Indicação de **jogadores online** na sala

---

## 🛠️ Stack

| Camada | Tecnologias |
|---|---|
| **Backend** | Java 21, Spring Boot 3, Spring Data JPA, Spring Security + JWT, Bean Validation, MapStruct, Flyway |
| **Banco** | PostgreSQL 16 |
| **Tempo real** | WebSocket + STOMP |
| **Frontend** | Angular 18 (standalone components), Angular Material (tema real customizado), Reactive Forms, `@stomp/stompjs` |
| **Testes** | JUnit 5, Mockito, Jasmine/Karma |
| **Infra** | GitHub Actions (CI com PostgreSQL de serviço) |

---

## 🏗️ Arquitetura

```mermaid
flowchart LR
    subgraph Cliente["Frontend — Angular 18"]
        UI["Componentes standalone<br/>(Material + Reactive Forms)"]
        WS_C["WebsocketService<br/>(STOMP)"]
        HTTP_C["Services HTTP<br/>+ interceptors JWT"]
    end

    subgraph API["Backend — Spring Boot 3"]
        SEC["Spring Security<br/>Filtro JWT"]
        REST["Controllers REST"]
        WSB["Controllers WebSocket<br/>@MessageMapping"]
        SRV["Services<br/>(regras de negócio)"]
        REPO["Repositories<br/>Spring Data JPA"]
    end

    DB[("PostgreSQL")]

    HTTP_C -->|"REST /api + Bearer token"| SEC
    WS_C -->|"STOMP /ws + Bearer token"| WSB
    SEC --> REST
    REST --> SRV
    WSB --> SRV
    SRV --> REPO
    REPO --> DB
    SRV -->|"broadcast /topic e /user/queue"| WS_C
```

As camadas do backend seguem o fluxo **Controller → Service → Repository**, com DTOs nas bordas (mapeados por MapStruct) e tratamento global de erros via `@RestControllerAdvice`. O domínio é organizado **por funcionalidade** (`auth`, `campanha`, `personagem`, `sessao`, `chat`, `rolagem`, `diario`), e não por tipo técnico.

---

## 🔄 Fluxos principais

### 1. Autenticação com JWT

```mermaid
sequenceDiagram
    participant U as Usuário
    participant F as Frontend
    participant A as AuthController
    participant S as Spring Security

    U->>F: e-mail + senha
    F->>A: POST /api/auth/login
    A->>A: valida credenciais (BCrypt)
    A-->>F: { token JWT }
    F->>F: guarda o token
    Note over F: jwtInterceptor injeta<br/>"Authorization: Bearer" em toda requisição
    F->>S: GET /api/campanhas (com Bearer)
    S->>S: filtro valida o token
    S-->>F: 200 OK
```

### 2. Rolagem secreta do mestre (WebSocket)

```mermaid
sequenceDiagram
    participant M as Mestre
    participant J as Jogadores
    participant WS as RolagemWsController
    participant D as DiceRoller

    M->>WS: /app/sessao/{id}/rolagem (secreta=true)
    WS->>WS: exige papel MESTRE
    WS->>D: rola a notação
    D-->>WS: resultado
    WS-->>M: /user/queue/...rolagem
    Note over J: os jogadores NÃO recebem nada
```

> Numa rolagem pública o servidor transmite para `/topic/sessao.{id}.rolagem` (todos recebem). Na secreta, usa `/user/queue` — roteamento direcionado do STOMP pelo `Principal` setado no handshake.

### 3. Turno de combate

```mermaid
sequenceDiagram
    participant M as Mestre
    participant C as CombateService
    participant T as /topic/...combate

    M->>C: POST /combate/iniciar (lista de iniciativa)
    C->>C: ordena por iniciativa (desc)
    C->>T: estado do combate
    M->>C: POST /combate/proximo-turno
    C->>C: avança índice; ao dar a volta, +1 rodada
    C->>T: novo estado (quem é a vez)
```

---

## 🚀 Como rodar

### Pré-requisitos
- Java 21
- Node 20+
- PostgreSQL 16

### 1. Banco de dados
No `psql` ou pgAdmin, como superusuário:

```sql
CREATE USER taverna WITH PASSWORD 'taverna';
CREATE DATABASE tavernaonline OWNER taverna;
CREATE DATABASE tavernaonline_test OWNER taverna;
```

As tabelas são criadas automaticamente pelo **Flyway** quando o backend sobe.

### 2. Backend

```bash
cd backend
mvn spring-boot:run
```

- API em `http://localhost:8080`
- Documentação Swagger em `http://localhost:8080/swagger-ui.html`

### 3. Frontend

```bash
cd frontend
npm install
npm start
```

- Aplicação em `http://localhost:4200`

---

## 🗂️ Estrutura do projeto

```
tavernaonline/
├── backend/                 # Spring Boot 3 (Java 21)
│   └── src/main/java/br/com/tavernaonline/
│       ├── config/          # Security, WebSocket, OpenAPI, CORS
│       ├── security/        # JwtService, filtros, auth do WebSocket
│       ├── common/          # EntidadeBase, exceções, handler global
│       ├── auth/ campanha/ personagem/ sessao/ chat/ rolagem/ diario/
│       └── rolagem/motor/   # DiceParser + DiceRoller (com testes)
├── frontend/                # Angular 18 (standalone)
│   └── src/app/
│       ├── core/            # auth, interceptors, services, websocket
│       └── features/        # auth, campanhas, personagens, sessao, diario
├── docs/                    # diagramas, prints, roadmap
└── .github/workflows/       # CI (build + testes)
```

---

## 🧪 Testes

```bash
# Backend
cd backend && mvn verify

# Frontend
cd frontend && npm run test:ci
```

O destaque dos testes é o **motor de dados**: o `DiceRoller` recebe um `RandomGenerator` injetado, então nos testes um gerador fixo torna o resultado **determinístico** — dá para afirmar que `2d6+3` com os dados `[4,2]` resulta em `9`. Há também cobertura das notações inválidas, das regras de convite/permissão e das transições de turno do combate.

---

## 🧠 Decisões técnicas

Algumas escolhas e o porquê (as demais estão em [`docs/DECISOES.md`](docs/DECISOES.md)):

- **Presença online fora do banco** — quem está na sala é um dado efêmero, então vive em memória (`ConcurrentHashMap`) alimentado pelos eventos de conexão/desconexão do STOMP.
- **IDs em UUID** — evitam enumeração de recursos pela URL.
- **`ddl-auto: validate` + Flyway** — o schema é versionado por migrations; o Hibernate apenas confere, nunca gera as tabelas.
- **Optimistic lock (`@Version`) nas fichas** — duas edições simultâneas resultam em `409 Conflict` em vez de sobrescrita silenciosa.
- **`RandomGenerator` injetado** — torna o motor de dados testável e determinístico.

---

## 📄 Licença

Distribuído sob a licença MIT. Veja [LICENSE](LICENSE).
