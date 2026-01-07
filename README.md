# Sales Platform - Microsserviço de Plataforma de Vendas

Microsserviço central do ecossistema de vendas, responsável pela gestão de carrinho, finalização de vendas e exposição de APIs REST para múltiplos canais.

## 🚀 Tecnologias Principais

- **Java 25** - Linguagem de programação (versão mais recente)
- **Spring Boot 4.0.1** - Framework principal (nova versão major)
- **Gradle 9.2.1** - Ferramenta de build
- **PostgreSQL 16** - Banco de dados relacional
- **Flyway** - Versionamento de banco de dados
- **MapStruct** - Conversão entre DTOs e Entidades
- **Springdoc OpenAPI** - Documentação automática da API
- **JUnit 5 + Mockito** - Testes unitários

## 📖 Documentação

### Desenvolvimento

### Funcionalidades

### Segurança

### Arquitetura

Este projeto segue uma arquitetura em camadas:

```
Controller (API) → Service (Negócio) → Repository (Dados)
       ↓                ↓
      DTOs          Entidades
```

**Convenções:**
- Controllers apenas roteiam e validam entrada
- Services contêm toda a lógica de negócio
- Repositories acessam dados (Spring Data JPA)
- DTOs para entrada/saída da API (nunca expor entidades)
- Tratamento de exceções centralizado (RFC 9457 - Problem Details)

### Estrutura de Pacotes

```
br.com.fsj.salesplatform/
├── config/          → Configurações (MapStruct, OpenAPI, etc)
├── controller/      → Controllers REST
├── dto/             → Request/Response DTOs
├── exception/       → Exceções customizadas e handlers
├── mapper/          → Configurações MapStruct customizadas
├── model/           → Entidades JPA
├── repository/      → Interfaces Spring Data JPA
├── service/         → Regras de negócio
└── util/            → Utilitários comuns
```

## 🏗️ Como Começar

### Pré-requisitos

- Java 25 (JDK)
- Docker e Docker Compose (para PostgreSQL)
- Gradle 9.2.1 (ou usar o wrapper incluído)

### Instalação

1. **Clone o repositório**
   ```bash
   git clone <url-do-repositorio>
   cd sales-platform
   ```

2. **Inicie o PostgreSQL**
   ```bash
   docker-compose up -d
   ```

3. **Execute a aplicação**
   ```bash
   ./gradlew bootRun
   ```

   Ou no Windows:
   ```bash
   gradlew.bat bootRun
   ```

4. **Acesse a documentação**
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - OpenAPI JSON: http://localhost:8080/v3/api-docs
   - Health Check: http://localhost:8080/api/v1/health

### Build

```bash
# Compilar o projeto
./gradlew build

# Executar testes
./gradlew test

# Limpar build anterior
./gradlew clean

# Build sem testes (não recomendado)
./gradlew build -x test
```

## 🧪 Testes

```bash
# Executar todos os testes
./gradlew test

# Executar testes com relatório detalhado
./gradlew test --info

# Executar testes de uma classe específica
./gradlew test --tests HealthControllerTest
```

## 🗄️ Banco de Dados

### Configuração Local

O arquivo `docker-compose.yml` configura um PostgreSQL local:

- **Host:** localhost
- **Porta:** 5432
- **Database:** salesplatform_db
- **Usuário:** postgres
- **Senha:** postgres

### Migrations (Flyway)

As migrations estão em `src/main/resources/db/migration/`:

- `V1__initial_schema.sql` - Schema inicial
- Próximas migrations devem seguir o padrão: `V<numero>__<descricao>.sql`

**Regras:**
- Migrations são imutáveis após aplicadas
- Sempre criar nova migration para correções
- Testar localmente antes de commitar
- Nunca editar migrations já aplicadas em algum ambiente

### Comandos Úteis

```bash
# Ver informações das migrations
./gradlew flywayInfo

# Validar migrations
./gradlew flywayValidate

# Limpar banco (APENAS DEV!)
./gradlew flywayClean

# Aplicar migrations
./gradlew flywayMigrate
```

## 🔒 Padrões de API

### Versionamento

Todas as APIs seguem versionamento via URL:
- `/api/v1/...` - Versão 1

### Tratamento de Erros (RFC 9457)

Todos os erros seguem o padrão RFC 9457 (Problem Details):

```json
{
  "type": "https://api.fsj.com.br/problems/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "Um ou mais campos possuem valores inválidos",
  "instance": "/api/v1/carts/123",
  "timestamp": "2025-12-26T10:30:00Z",
  "errors": [
    {"field": "email", "message": "Email inválido"}
  ]
}
```

**Content-Type:** `application/problem+json`

### Códigos de Status HTTP

- `200` OK - Sucesso
- `201` Created - Recurso criado
- `204` No Content - Sucesso sem corpo de resposta
- `400` Bad Request - Validação/entrada inválida
- `404` Not Found - Recurso não encontrado
- `409` Conflict - Conflito de estado
- `422` Unprocessable Entity - Regra de negócio impedindo processamento
- `500` Internal Server Error - Erro inesperado

## 🤝 Contribuindo

1. Siga as regras de arquitetura definidas em `.cursor/rules/`
2. Escreva testes para novas funcionalidades
3. Documente endpoints com anotações OpenAPI
4. Use commits semânticos (feat, fix, refactor, etc)
5. Nunca coloque lógica de negócio em Controllers
6. Sempre use DTOs para entrada/saída da API

## 📝 Convenções de Código

- **Nomenclatura:** PascalCase para classes, camelCase para métodos/variáveis
- **Records:** Usar para DTOs imutáveis
- **Switch Expressions:** Preferir sobre switch tradicional
- **Text Blocks:** Usar para strings multilinha (SQL, JSON, etc)
- **Logs:** Usar níveis apropriados (DEBUG, INFO, WARN, ERROR)
- **Exceções:** Nunca engolir exceções, sempre logar ou propagar

## 📊 Monitoramento

### Actuator Endpoints

- `/actuator/health` - Status da aplicação
- `/actuator/info` - Informações da aplicação
- `/actuator/metrics` - Métricas

## 🐳 Docker

### Executar PostgreSQL

```bash
docker-compose up -d
```

### Parar PostgreSQL

```bash
docker-compose down
```

### Ver logs do PostgreSQL

```bash
docker-compose logs -f postgres
```

## 📄 Licença

[Definir licença do projeto]

---

**Versão:** 1.0.0  
**Última atualização:** 2025-12-26  
**Equipe:** Sales Platform Team

