# Melodify API

REST API para gerenciamento de autores e músicas, construída com Spring Boot 3.4 e Java 21.

## Tecnologias

- **Java 21** - LTS com Records e Pattern Matching
- **Spring Boot 3.4** - Framework principal
- **Spring Data JPA** - Persistência com Hibernate
- **PostgreSQL 16** - Banco de dados relacional
- **Redis 7** - Cache distribuído
- **Flyway** - Migrations de banco de dados
- **OpenAPI 3 / Swagger** - Documentação da API
- **Docker Compose** - Orquestração de containers

## Estrutura do Projeto

```
src/main/java/br/com/labs/
├── config/                 # Configurações (Redis, OpenAPI, JPA)
├── controller/             # Controllers REST
├── dto/
│   ├── request/            # DTOs de entrada
│   └── response/           # DTOs de saída
├── exception/              # Exceções customizadas e handler global
├── model/                  # Entidades JPA
├── repository/
│   └── specification/      # Criteria API Specifications
└── service/                # Regras de negócio
```

## Pré-requisitos

- Java 21+
- Docker e Docker Compose
- Maven 3.9+ (ou use o wrapper `./mvnw`)

## Quick Start

### 1. Subir a infraestrutura

```bash
docker-compose up -d
```

Isso inicia:
- PostgreSQL na porta `5438`
- Redis na porta `6379`

### 2. Rodar a aplicação

```bash
./mvnw spring-boot:run
```

### 3. Acessar

- **API**: http://localhost:8080/api/v1
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

## Endpoints

### Authors

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/authors` | Criar autor |
| GET | `/api/v1/authors/{id}` | Buscar autor por ID |
| GET | `/api/v1/authors/{id}/musics` | Buscar autor com músicas (cached) |
| PUT | `/api/v1/authors/{id}` | Atualizar autor |
| DELETE | `/api/v1/authors/{id}` | Deletar autor |
| GET | `/api/v1/authors` | Listar autores (paginado) |

### Musics

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/musics` | Criar música |
| GET | `/api/v1/musics/{id}` | Buscar música por ID |
| PUT | `/api/v1/musics/{id}` | Atualizar música |
| DELETE | `/api/v1/musics/{id}` | Deletar música |
| GET | `/api/v1/musics` | Listar músicas (paginado) |
| GET | `/api/v1/musics/search` | Buscar com filtros (Criteria API) |
| GET | `/api/v1/musics/author/{authorId}` | Músicas por autor |

## Exemplos de Uso

### Criar um autor

```bash
curl -X POST http://localhost:8080/api/v1/authors \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Lennon",
    "email": "john@beatles.com"
  }'
```

### Criar uma música

```bash
curl -X POST http://localhost:8080/api/v1/musics \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Imagine",
    "durationSeconds": 183,
    "genre": "Rock",
    "authorId": 1
  }'
```

### Buscar músicas com filtros

```bash
curl "http://localhost:8080/api/v1/musics/search?genre=Rock&minDuration=120&maxDuration=300"
```

## Cache Redis

O endpoint `GET /api/v1/authors/{id}/musics` utiliza cache Redis:

- **TTL**: 5 minutos
- **Eviction**: Automático ao atualizar/deletar autor
- **Cache Key**: `authorMusics::{id}`

Para monitorar o cache, suba o Redis Commander:

```bash
docker-compose --profile dev up -d
```

Acesse: http://localhost:8081

## Configuração

### application.yml

| Propriedade | Valor Padrão | Descrição |
|-------------|--------------|-----------|
| `server.port` | 8080 | Porta da aplicação |
| `spring.datasource.url` | localhost:5438 | URL do PostgreSQL |
| `spring.data.redis.host` | localhost | Host do Redis |
| `spring.data.redis.port` | 6379 | Porta do Redis |

### Variáveis de Ambiente (Docker)

```yaml
SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/music_db
SPRING_DATA_REDIS_HOST: redis
```

## Docker

### Build da imagem

```bash
docker build -t melodify-api .
```

### Rodar tudo em containers

Descomente o serviço `app` no `docker-compose.yml` e execute:

```bash
docker-compose up -d
```

## Testes

```bash
./mvnw test
```

## Migrations

As migrations Flyway estão em `src/main/resources/db/migration/`:

- `V1__create_initial_schema.sql` - Schema inicial
- `V2__fix_id_columns_to_bigint.sql` - Correção de tipos

## Arquitetura

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  Controller │────▶│   Service   │────▶│ Repository  │
└─────────────┘     └─────────────┘     └─────────────┘
       │                   │                   │
       ▼                   ▼                   ▼
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│    DTOs     │     │    Cache    │     │  Database   │
│  (Records)  │     │   (Redis)   │     │ (PostgreSQL)│
└─────────────┘     └─────────────┘     └─────────────┘
```

## Licença

MIT
