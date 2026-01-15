# Beatbox API

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

### Observabilidade

- **Prometheus** - Coleta de métricas
- **Grafana** - Visualização e dashboards
- **Loki** - Agregação de logs
- **Tempo** - Distributed tracing
- **Micrometer** - Instrumentação de métricas
- **OpenTelemetry** - Tracing distribuído

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
- Prometheus na porta `9090`
- Grafana na porta `3000`
- Loki na porta `3100`
- Tempo na porta `3200`

### 2. Rodar a aplicação

```bash
./mvnw spring-boot:run
```

### 3. Acessar

- **API**: http://localhost:8080/api/v1
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs
- **Grafana**: http://localhost:3000 (admin/admin)
- **Prometheus**: http://localhost:9090

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
    "name": "John LennoAnAAAAA",
    "email": "johAAn@bAeatles.com"
  }'
```

### Criar uma música

```bash
curl -X POST http://localhost:8080/api/v1/musics \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Imagine234",
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

## Observabilidade

A stack de observabilidade inclui:

### Grafana

Acesse em http://localhost:3000 (admin/admin)

**Dashboards disponíveis:**
- **Beatbox API - Spring Boot Dashboard**: Métricas da aplicação, JVM, conexões, cache

**Datasources pré-configurados:**
- Prometheus (métricas)
- Loki (logs)
- Tempo (traces)

### Prometheus

Acesse em http://localhost:9090

Coleta métricas da aplicação via endpoint `/actuator/prometheus`:
- Request rate e latência por endpoint
- JVM memory e threads
- HikariCP connection pool
- Redis cache hits/misses

### Loki

Agregação de logs em formato estruturado (JSON).

Os logs são enviados diretamente da aplicação via `loki4j-logback-appender`.

Labels disponíveis: `application`, `host`, `level`

### Tempo

Distributed tracing via OpenTelemetry.

Traces são correlacionados com logs através do `traceId`.

### Métricas Disponíveis

```bash
# Ver métricas no Prometheus format
curl http://localhost:8080/actuator/prometheus

# Ver métricas específicas
curl http://localhost:8080/actuator/metrics/http.server.requests
curl http://localhost:8080/actuator/metrics/jvm.memory.used
curl http://localhost:8080/actuator/metrics/cache.gets
```

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

### Perfil de Produção (Otimizado)

O perfil `prod` inclui otimizações para reduzir consumo de memória e CPU:

```bash
# Rodar com perfil de produção
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

# Ou via variável de ambiente
SPRING_PROFILES_ACTIVE=prod java -jar app.jar
```

**Otimizações aplicadas:**

| Configuração | Dev | Prod | Economia |
|--------------|-----|------|----------|
| HikariCP pool | 10 conexões | 5 conexões | ~50% memória DB |
| Redis pool | 8 conexões | 4 conexões | ~50% memória Redis |
| Tracing sample | 100% | 10% | ~90% CPU tracing |
| Log level | DEBUG | INFO/WARN | Menos I/O |
| Swagger | Habilitado | Desabilitado | Menos memória |
| Async logging | Síncrono | Assíncrono | Menos bloqueio |

**JVM (Dockerfile):**
- Heap: 256MB-512MB
- Metaspace: 128MB máximo
- G1GC com pause de 100ms
- String deduplication

## Docker

### Build da imagem

```bash
docker build -t beatbox-api .
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

## Kubernetes

Os manifests Kubernetes estão em `k8s/`:

```
k8s/
├── deployment.yaml    # Deployment com 2 réplicas
├── service.yaml       # ClusterIP service
├── configmap.yaml     # Configurações da aplicação
├── secret.yaml        # Credenciais (template)
├── hpa.yaml           # Horizontal Pod Autoscaler
├── ingress.yaml       # Ingress NGINX
└── pdb.yaml           # Pod Disruption Budget
```

### Deploy no cluster

```bash
# Criar namespace (opcional)
kubectl create namespace beatbox

# Aplicar todos os manifests
kubectl apply -f k8s/ -n beatbox

# Verificar status
kubectl get pods -n beatbox
kubectl get svc -n beatbox
```

### Recursos configurados

| Recurso | Request | Limit |
|---------|---------|-------|
| CPU | 100m | 500m |
| Memória | 256Mi | 512Mi |

### Autoscaling

- **Min replicas**: 2
- **Max replicas**: 10
- **Scale up**: CPU > 70% ou Memória > 80%

### Probes

- **Liveness**: `/actuator/health/liveness`
- **Readiness**: `/actuator/health/readiness`

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

              Observabilidade
┌─────────────────────────────────────────────────────┐
│                                                     │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐          │
│  │Prometheus│  │   Loki   │  │  Tempo   │          │
│  │ (metrics)│  │  (logs)  │  │ (traces) │          │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘          │
│       │             │             │                 │
│       └─────────────┼─────────────┘                 │
│                     ▼                               │
│              ┌───────────┐                          │
│              │  Grafana  │                          │
│              └───────────┘                          │
└─────────────────────────────────────────────────────┘
```

## Licença

MIT
