# TaskManager API

API REST para gerenciamento de tarefas internas desenvolvida em Spring Boot.

## Como executar

### Com Docker (Recomendado)

**Pré-requisitos:**
- Docker Desktop (Windows/Mac) ou Docker Engine (Linux)
- Docker Compose

1. **Clone o repositório:**
```bash
# Linux/Mac
git clone <url-do-repositorio>
cd taskmanager

# Windows (PowerShell)
git clone <url-do-repositorio>
cd taskmanager
```

2. **Execute com Docker Compose:**
```bash
# Linux/Mac
docker-compose up -d

# Windows (PowerShell)
docker-compose up -d

# Aguarde alguns segundos e verifique se está tudo rodando
docker-compose ps

# Para ver os logs da inicialização
docker-compose logs -f
```

3. **Acesse a aplicação:**
- API: http://localhost:8080
- **Documentação completa:** http://localhost:8080/swagger-ui.html

**Teste se está funcionando:**
```bash
# Linux/Mac
curl http://localhost:8080/api/v1/users

# Windows (PowerShell)
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/users" -Method GET

# Ou use o navegador: http://localhost:8080/api/v1/users
```

4. **Comandos úteis do Docker:**
```bash
# Linux/Mac/Windows
# Parar serviços
docker-compose down

# Parar e remover volumes (dados do banco)
docker-compose down -v

# Reconstruir imagem da aplicação
docker-compose build

# Ver logs de um serviço específico
docker-compose logs app
docker-compose logs postgres
```

### Sem Docker

**Pré-requisitos:**
- Java 17+
- Maven 3.6+

**Execute a aplicação:**
```bash
# Linux/Mac
./mvnw spring-boot:run

# Windows (PowerShell)
./mvnw.cmd spring-boot:run

# Windows (Command Prompt)
mvnw.cmd spring-boot:run
```

## Endpoints

**Documentação completa disponível em:** http://localhost:8080/swagger-ui.html

### Usuários

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/users` | Criar usuário |
| GET | `/api/v1/users/{id}` | Buscar usuário por ID |
| PATCH | `/api/v1/users/{id}` | Atualizar usuário |
| DELETE | `/api/v1/users/{id}` | Deletar usuário (soft delete) |

### Tarefas

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/tasks` | Criar tarefa |
| GET | `/api/v1/tasks/{id}` | Buscar tarefa por ID |
| GET | `/api/v1/tasks` | Listar tarefas com filtros |
| PATCH | `/api/v1/tasks/{id}/status` | Atualizar status da tarefa |

### Subtarefas

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/tasks/{taskId}/subtasks` | Criar subtarefa |
| GET | `/api/v1/tasks/{taskId}/subtasks` | Listar subtarefas de uma tarefa |
| GET | `/api/v1/subtasks/{subtaskId}` | Buscar subtarefa por ID |
| PATCH | `/api/v1/subtasks/{subtaskId}/status` | Atualizar status da subtarefa |

## Tecnologias

- **Java 21**
- **Spring Boot 3.5.4**
- **Spring Data JPA**
- **PostgreSQL**
- **Maven**
- **Docker & Docker Compose**
- **JUnit 5 + Mockito**
- **Lombok**
- **Bean Validation**
- **SpringDoc OpenAPI**

## Decisões Técnicas

### Mapeamento de Objetos
Optei por implementar os Mappers de forma manual (`TaskMapper`, `UserMapper`, `SubtaskMapper`) para ter controle total sobre o mapeamento entre entidades e DTOs. Em uma implementação futura, consideraria adicionar o **MapStruct** para automatizar esse processo, mantendo a performance e reduzindo boilerplate code.

### Queries e Filtros
Implementei queries customizadas usando `@Query` do Spring Data JPA para filtros de tarefas. Em uma implementação futura, consideraria migrar para **QueryDSL** por ser mais performática e type-safe, especialmente para queries complexas e dinâmicas. A escolha atual foi adequada para a simplicidade do projeto.

### Framework de Testes
Utilizei **JUnit 5** e **Mockito** como framework de testes por familiaridade e maturidade das ferramentas. Os testes cobrem cenários de sucesso e exceções, garantindo a robustez da aplicação.

### Segurança
A aplicação atual não possui autenticação implementada. Em uma implementação futura, consideraria adicionar:
- **Spring Security** com **OAuth2**
- **JWT (JSON Web Tokens)** para autenticação stateless
- Controle de acesso baseado em roles (RBAC)

### Arquitetura
A arquitetura atual segue o padrão **Layered Architecture** (Controller → Service → Repository). Para projetos mais complexos, consideraria migrar para **Domain-Driven Design (DDD)** com:
- Camadas bem definidas (Domain, Application, Infrastructure)
- Aggregates e Value Objects
- Domain Events
- CQRS (Command Query Responsibility Segregation)

### Banco de Dados
Utilizei **PostgreSQL** por ser robusto e adequado para aplicações em produção. Para implementações futuras, consideraria:
- Migrations com **Flyway** ou **Liquibase**
- Índices otimizados para queries frequentes
- Soft delete implementado nas entidades

### Documentação da API
Implementei **SpringDoc OpenAPI** para documentação automática da API, facilitando o consumo pelos clientes e testes via Swagger UI.

## Testes

Execute os testes:
```bash
# Linux/Mac
./mvnw test

# Windows (PowerShell)
./mvnw.cmd test

# Ou com Maven instalado (todas as plataformas)
mvn test
```

## Gerenciamento dos Containers

```bash
# Linux/Mac/Windows
# Ver status dos containers
docker-compose ps

# Ver logs em tempo real
docker-compose logs -f

# Ver logs de um serviço específico
docker-compose logs app
docker-compose logs postgres

# Parar todos os serviços
docker-compose down

# Parar e remover volumes (limpa dados do banco)
docker-compose down -v

# Reconstruir e reiniciar
docker-compose up --build -d
```

## Troubleshooting

**Problema:** Aplicação não inicia
```bash
# Verificar se o banco está pronto
docker-compose logs postgres

# Reconstruir imagem
docker-compose build --no-cache
```

**Problema:** Erro de conexão com banco
```bash
# Verificar se o PostgreSQL está rodando
docker-compose ps

# Reiniciar apenas o banco
docker-compose restart postgres
```

**Problema:** Porta 8080 já está em uso
```bash
# Linux/Mac
sudo lsof -ti:8080 | xargs kill -9

# Windows (PowerShell)
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Ou usar outra porta no docker-compose.yml
```

**Problema:** Docker não está rodando
```bash
# Linux
sudo systemctl start docker

# Windows
# Abrir Docker Desktop

# Verificar se está funcionando
docker --version
``` 
