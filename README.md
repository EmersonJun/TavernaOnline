# 🍺 TavernaOnline

> Mesa virtual para sessões de RPG de mesa em tempo real.

![Status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-brightgreen)
![Angular](https://img.shields.io/badge/Angular-18-red)
![License](https://img.shields.io/badge/license-MIT-blue)

## 📖 Sobre

O TavernaOnline permite que um mestre crie campanhas, convide jogadores por código e conduza sessões em tempo real com chat, rolagem de dados, tracker de iniciativa e diário da campanha.

## ✨ Funcionalidades

- [x] Cadastro e login com JWT
- [ ] Campanhas com convite por código
- [ ] Fichas de personagem editáveis
- [ ] Sala de sessão em tempo real (WebSocket/STOMP)
- [ ] Rolagem de dados com notação (`2d6+3`) e rolagens secretas do mestre
- [ ] Tracker de iniciativa e controle de turnos
- [ ] Diário da campanha
- [ ] Indicação de jogadores online

## 🛠️ Stack

| Camada | Tecnologias |
|---|---|
| Backend | Java 21, Spring Boot 3, Spring Data JPA, Spring Security + JWT, Bean Validation, MapStruct, Flyway |
| Banco | PostgreSQL |
| Tempo real | WebSocket + STOMP |
| Frontend | Angular 18 (standalone), Angular Material, Reactive Forms |
| Testes | JUnit 5, Mockito, Jasmine/Karma |
| Infra | GitHub Actions (CI com PostgreSQL de serviço) |

## 🏗️ Arquitetura

_Diagrama em breve._

## 🚀 Como rodar

### Pré-requisitos
- Java 21
- Node 20+
- PostgreSQL 16 instalado localmente

### Banco de dados
No `psql` (ou pgAdmin), como superusuário:

```sql
CREATE USER taverna WITH PASSWORD 'taverna';
CREATE DATABASE tavernaonline OWNER taverna;
CREATE DATABASE tavernaonline_test OWNER taverna;
```

As tabelas são criadas automaticamente pelo Flyway quando o backend sobe.

_Instruções do backend e do frontend em breve._

## 🧠 Decisões técnicas

_Em breve._

## 🗺️ Roadmap

Veja [docs/ROADMAP.md](docs/ROADMAP.md).

## 📄 Licença

Distribuído sob a licença MIT. Veja [LICENSE](LICENSE).
