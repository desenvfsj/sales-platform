# 📊 Documentação da Estrutura do Banco de Dados

**Projeto:** Plataforma de Vendas Unificada  
**Banco de Dados:** PostgreSQL  
**Versão do Schema:** 3.0  
**Última Atualização:** 30/12/2025

---

## 📑 Índice

1. [Visão Geral](#visão-geral)
2. [Diagrama ER](#diagrama-er)
3. [Tabelas](#tabelas)
   - [carts](#tabela-carts)
   - [cart_items](#tabela-cart_items)
   - [cart_item_discounts](#tabela-cart_item_discounts)
4. [Relacionamentos](#relacionamentos)
5. [Índices](#índices)
6. [Constraints e Validações](#constraints-e-validações)
7. [Migrations](#migrations)
8. [Queries Comuns](#queries-comuns)

---

## 🎯 Visão Geral

O banco de dados da Plataforma de Vendas é projetado para suportar operações de carrinho de compras multi-canal (Web, App, PDV e Parceiros).

### Características Principais:

- ✅ **Multi-canal**: Suporta vendas via WEB, APP, PDV e PARTNER
- ✅ **Descontos Flexíveis**: Múltiplos descontos por item (BALCAO, GESTOR, PROMOCAO, PBM, PV)
- ✅ **Auditoria**: Timestamps automáticos (created_at, updated_at)
- ✅ **Integridade**: Constraints e validações no banco
- ✅ **Performance**: Índices otimizados para queries frequentes
- ✅ **Versionamento**: Gerenciado via Flyway

### Tecnologias:

- **SGBD**: PostgreSQL 15+
- **Versionamento**: Flyway
- **ORM**: Spring Data JPA + Hibernate

---

## 📐 Diagrama ER

```
┌─────────────────────────────────────┐
│            CARTS                    │
├─────────────────────────────────────┤
│ PK  id              BIGSERIAL       │
│ UK  customer_cpf    VARCHAR(11)     │
│ UK  channel         VARCHAR(20)     │
│     status          VARCHAR(20)     │
│     total_amount    DECIMAL(10,2)   │
│     total_items     INTEGER         │
│     created_at      TIMESTAMP       │
│     created_by      VARCHAR(50)     │
│     updated_at      TIMESTAMP       │
│     updated_by      VARCHAR(50)     │
└─────────────────────────────────────┘
                 │
                 │ 1
                 │
                 │
                 │ N
                 ▼
┌─────────────────────────────────────┐
│          CART_ITEMS                 │
├─────────────────────────────────────┤
│ PK  id                BIGSERIAL     │
│ FK  cart_id           BIGINT        │
│     product_id        BIGINT        │
│     product_name      VARCHAR(255)  │
│     quantity          INTEGER       │
│     product_price     DECIMAL(10,2) │
│     subtotal          DECIMAL(10,2) │
│     total_discount    DECIMAL(10,2) │
│     saved_for_later   BOOLEAN       │
│     created_at        TIMESTAMP     │
│     created_by        VARCHAR(50)   │
│     updated_at        TIMESTAMP     │
│     updated_by        VARCHAR(50)   │
└─────────────────────────────────────┘
                 │
                 │ 1
                 │
                 │
                 │ N
                 ▼
┌─────────────────────────────────────┐
│      CART_ITEM_DISCOUNTS            │
├─────────────────────────────────────┤
│ PK  id                BIGSERIAL     │
│ FK  cart_item_id      BIGINT        │
│     type              VARCHAR(20)   │
│     value             DECIMAL(10,2) │
│     created_at        TIMESTAMP     │
│     created_by        VARCHAR(50)   │
│     updated_at        TIMESTAMP     │
│     updated_by        VARCHAR(50)   │
└─────────────────────────────────────┘
```

**Legenda:**
- `PK` = Primary Key
- `FK` = Foreign Key
- `1:N` = Relacionamento Um-para-Muitos

---

## 📋 Tabelas

### Tabela: `carts`

Armazena os carrinhos de compras dos clientes em diferentes canais.

#### Estrutura:

| Coluna | Tipo | Nulo | Padrão | Descrição |
|--------|------|------|--------|-----------|
| `id` | BIGSERIAL | NOT NULL | AUTO | Identificador único do carrinho |
| `customer_cpf` | VARCHAR(11) | NOT NULL | - | CPF do cliente (11 dígitos numéricos) |
| `channel` | VARCHAR(20) | NOT NULL | - | Canal de origem (WEB, APP, PDV, PARTNER) |
| `status` | VARCHAR(20) | NOT NULL | 'OPEN' | Status do carrinho |
| `total_amount` | DECIMAL(10,2) | NOT NULL | 0.00 | Valor total do carrinho |
| `total_items` | INTEGER | NOT NULL | 0 | Quantidade total de itens |
| `created_at` | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | Data/hora de criação |
| `created_by` | VARCHAR(50) | NULL | - | Usuário que criou o carrinho |
| `updated_at` | TIMESTAMP | NULL | - | Data/hora da última atualização |
| `updated_by` | VARCHAR(50) | NULL | - | Usuário que fez a última atualização |

#### Enumerações:

**channel** (Canal de Venda):
- `WEB` - Loja virtual (e-commerce)
- `APP` - Aplicativo mobile
- `PDV` - Ponto de Venda (loja física)
- `PARTNER` - Parceiros/Integrações

**status** (Status do Carrinho):
- `OPEN` - Carrinho aberto (em uso)
- `CHECKOUT_PENDING` - Aguardando finalização do checkout
- `COMPLETED` - Carrinho finalizado (venda concluída)
- `CANCELLED` - Carrinho cancelado
- `SAVED` - Carrinho salvo para depois

#### Constraints:

```sql
-- Primary Key
PRIMARY KEY (id)

-- Unique Key (um cliente pode ter apenas um carrinho por canal)
CONSTRAINT uk_carts_customer_cpf_channel UNIQUE (customer_cpf, channel)

-- Validações
CONSTRAINT chk_total_amount CHECK (total_amount >= 0)
CONSTRAINT chk_total_items CHECK (total_items >= 0)
CONSTRAINT chk_channel CHECK (channel IN ('WEB', 'APP', 'PDV', 'PARTNER'))
CONSTRAINT chk_status CHECK (status IN ('OPEN', 'CHECKOUT_PENDING', 'COMPLETED', 'CANCELLED', 'SAVED'))
CONSTRAINT chk_customer_cpf CHECK (LENGTH(customer_cpf) = 11 AND customer_cpf ~ '^[0-9]+$')
```

#### Índices:

```sql
-- Nota: uk_carts_customer_cpf_channel já cria índice para (customer_cpf, channel)
CREATE INDEX idx_carts_status ON carts(status);
CREATE INDEX idx_carts_customer_cpf_status ON carts(customer_cpf, status);
CREATE INDEX idx_carts_updated_at ON carts(updated_at);
```

#### Exemplo de Dados:

```sql
INSERT INTO carts (customer_cpf, channel, status, total_amount, total_items)
VALUES ('12345678901', 'WEB', 'OPEN', 150.50, 3);
```

---

### Tabela: `cart_items`

Armazena os itens (produtos) de cada carrinho.

#### Estrutura:

| Coluna | Tipo | Nulo | Padrão | Descrição |
|--------|------|------|--------|-----------|
| `id` | BIGSERIAL | NOT NULL | AUTO | Identificador único do item |
| `cart_id` | BIGINT | NOT NULL | - | Referência ao carrinho (FK) |
| `product_id` | BIGINT | NOT NULL | - | ID do produto |
| `product_name` | VARCHAR(255) | NOT NULL | - | Nome do produto (snapshot) |
| `quantity` | INTEGER | NOT NULL | - | Quantidade do produto |
| `product_price` | DECIMAL(10,2) | NOT NULL | - | Preço unitário (snapshot) |
| `subtotal` | DECIMAL(10,2) | NOT NULL | - | Subtotal (price × quantity) |
| `total_discount` | DECIMAL(10,2) | NOT NULL | 0.00 | Soma total dos descontos aplicados |
| `saved_for_later` | BOOLEAN | NOT NULL | FALSE | Flag "salvar para depois" |
| `created_at` | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | Data/hora de adição |
| `created_by` | VARCHAR(50) | NULL | - | Usuário que adicionou o item |
| `updated_at` | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | Data/hora da última atualização |
| `updated_by` | VARCHAR(50) | NULL | - | Usuário que fez a última atualização |

#### Constraints:

```sql
-- Primary Key
PRIMARY KEY (id)

-- Foreign Key
CONSTRAINT fk_cart_items_cart 
    FOREIGN KEY (cart_id) REFERENCES carts(id) 
    ON DELETE CASCADE

-- Validações
CONSTRAINT chk_quantity CHECK (quantity > 0)
CONSTRAINT chk_product_price CHECK (product_price >= 0)
CONSTRAINT chk_subtotal CHECK (subtotal >= 0)
CONSTRAINT chk_total_discount CHECK (total_discount >= 0)
CONSTRAINT chk_discount_not_exceed_subtotal CHECK (total_discount <= subtotal)

-- Unique: não permite produto duplicado no mesmo carrinho
CONSTRAINT uk_cart_product UNIQUE (cart_id, product_id)
```

#### Índices:

```sql
CREATE INDEX idx_cart_items_cart_id ON cart_items(cart_id);
CREATE INDEX idx_cart_items_product_id ON cart_items(product_id);
CREATE INDEX idx_cart_items_saved_for_later ON cart_items(saved_for_later);
```

#### Exemplo de Dados:

```sql
INSERT INTO cart_items (cart_id, product_id, product_name, quantity, product_price, subtotal)
VALUES (1, 101, 'Dipirona Sódica 500mg', 2, 8.50, 17.00);
```

---

### Tabela: `cart_item_discounts`

Armazena múltiplos descontos aplicados a itens do carrinho.

#### Estrutura:

| Coluna | Tipo | Nulo | Padrão | Descrição |
|--------|------|------|--------|-----------|
| `id` | BIGSERIAL | NOT NULL | AUTO | Identificador único do desconto |
| `cart_item_id` | BIGINT | NOT NULL | - | Referência ao item do carrinho (FK) |
| `type` | VARCHAR(20) | NOT NULL | - | Tipo do desconto |
| `value` | DECIMAL(10,2) | NOT NULL | 0.00 | Valor do desconto em reais |
| `created_at` | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | Data/hora de criação |
| `created_by` | VARCHAR(50) | NULL | - | Usuário que aplicou o desconto |
| `updated_at` | TIMESTAMP | NULL | - | Data/hora da última atualização |
| `updated_by` | VARCHAR(50) | NULL | - | Usuário que atualizou |

#### Enumerações:

**type** (Tipo de Desconto):
- `BALCAO` - Desconto aplicado no balcão/PDV
- `GESTOR` - Desconto autorizado por gestor
- `PROMOCAO` - Desconto de promoção automática
- `PBM` - Programa de Benefício de Medicamentos
- `PV` - Desconto de Preço Variável

#### Constraints:

```sql
-- Primary Key
PRIMARY KEY (id)

-- Foreign Key
CONSTRAINT fk_cart_item 
    FOREIGN KEY (cart_item_id) REFERENCES cart_items(id) 
    ON DELETE CASCADE

-- Validações
CONSTRAINT chk_discount_value CHECK (value >= 0)
CONSTRAINT chk_discount_type CHECK (type IN ('BALCAO', 'GESTOR', 'PROMOCAO', 'PBM', 'PV'))
```

#### Índices:

```sql
CREATE INDEX idx_cart_item_discounts_cart_item_id ON cart_item_discounts(cart_item_id);
CREATE INDEX idx_cart_item_discounts_type ON cart_item_discounts(type);
```

#### Exemplo de Dados:

```sql
-- Adicionar desconto de promoção
INSERT INTO cart_item_discounts (cart_item_id, type, value, created_by)
VALUES (1, 'PROMOCAO', 5.00, 'system');

-- Adicionar desconto de gestor
INSERT INTO cart_item_discounts (cart_item_id, type, value, created_by)
VALUES (1, 'GESTOR', 10.00, 'manager');
```

#### Regras de Negócio:

1. **Múltiplos Descontos**: Um item pode ter múltiplos descontos de diferentes tipos
2. **Validação de Valor**: A soma dos descontos não pode exceder o subtotal do item
3. **Auditoria**: Sempre registrar quem aplicou/atualizou o desconto
4. **Cascade Delete**: Descontos são removidos automaticamente quando o item é deletado

---

## 🔗 Relacionamentos

### 1. `carts` → `cart_items` (1:N)

**Tipo:** Um-para-Muitos  
**Descrição:** Um carrinho pode ter vários itens

```sql
-- Foreign Key
cart_items.cart_id → carts.id

-- Cascade Delete
ON DELETE CASCADE
```

**Comportamento:**
- ✅ Quando um carrinho é deletado, todos os seus itens são deletados automaticamente
- ✅ Um item sempre pertence a um único carrinho
- ✅ Um carrinho pode ter 0 ou mais itens

### 2. `cart_items` → `cart_item_discounts` (1:N)

**Tipo:** Um-para-Muitos  
**Descrição:** Um item do carrinho pode ter vários descontos

```sql
-- Foreign Key
cart_item_discounts.cart_item_id → cart_items.id

-- Cascade Delete
ON DELETE CASCADE
```

**Comportamento:**
- ✅ Quando um item é deletado, todos os seus descontos são deletados automaticamente
- ✅ Um desconto sempre pertence a um único item
- ✅ Um item pode ter 0 ou mais descontos
- ✅ Múltiplos descontos do mesmo tipo são permitidos

---

## 📊 Índices

### Índices da Tabela `carts`

| Nome do Índice | Colunas | Tipo | Propósito |
|----------------|---------|------|-----------|
| `carts_pkey` | `id` | PRIMARY KEY | Chave primária |
| `idx_carts_customer_cpf` | `customer_cpf` | BTREE | Buscar carrinhos por CPF |
| `idx_carts_status` | `status` | BTREE | Filtrar por status |
| `idx_carts_channel` | `channel` | BTREE | Filtrar por canal |
| `idx_carts_customer_cpf_status` | `customer_cpf, status` | BTREE | Buscar carrinho aberto do cliente |
| `idx_carts_updated_at` | `updated_at` | BTREE | Ordenar por atualização |

### Índices da Tabela `cart_items`

| Nome do Índice | Colunas | Tipo | Propósito |
|----------------|---------|------|-----------|
| `cart_items_pkey` | `id` | PRIMARY KEY | Chave primária |
| `idx_cart_items_cart_id` | `cart_id` | BTREE | Buscar itens do carrinho |
| `idx_cart_items_product_id` | `product_id` | BTREE | Buscar por produto |
| `idx_cart_items_saved_for_later` | `saved_for_later` | BTREE | Filtrar itens salvos |
| `uk_cart_product` | `cart_id, product_id` | UNIQUE | Evitar duplicação |

### Índices da Tabela `cart_item_discounts`

| Nome do Índice | Colunas | Tipo | Propósito |
|----------------|---------|------|-----------|
| `cart_item_discounts_pkey` | `id` | PRIMARY KEY | Chave primária |
| `idx_cart_item_discounts_cart_item_id` | `cart_item_id` | BTREE | Buscar descontos do item |
| `idx_cart_item_discounts_type` | `type` | BTREE | Filtrar por tipo de desconto |

---

## ✅ Constraints e Validações

### Validações de Negócio

#### Tabela `carts`:

1. **CPF Válido**
   ```sql
   LENGTH(customer_cpf) = 11 AND customer_cpf ~ '^[0-9]+$'
   ```
   - Exatamente 11 dígitos
   - Apenas números

2. **Valores Não Negativos**
   ```sql
   total_amount >= 0
   total_items >= 0
   ```

3. **Canal Válido**
   ```sql
   channel IN ('WEB', 'APP', 'PDV', 'PARTNER')
   ```

4. **Status Válido**
   ```sql
   status IN ('OPEN', 'CHECKOUT_PENDING', 'COMPLETED', 'CANCELLED', 'SAVED')
   ```

#### Tabela `cart_items`:

1. **Quantidade Positiva**
   ```sql
   quantity > 0
   ```

2. **Preço Não Negativo**
   ```sql
   product_price >= 0
   subtotal >= 0
   ```

3. **Produto Único por Carrinho**
   ```sql
   UNIQUE (cart_id, product_id)
   ```

---

## 🔄 Migrations

### Histórico de Migrations (Flyway)

| Versão | Arquivo | Descrição | Data |
|--------|---------|-----------|------|
| V1 | `V1__create_carts_table.sql` | Criação da tabela `carts` | 2025-12-26 |
| V2 | `V2__create_cart_items_table.sql` | Criação da tabela `cart_items` | 2025-12-26 |
| V3 | `V3__create_cart_item_discounts_table.sql` | Criação da tabela `cart_item_discounts` | 2025-12-30 |

### Como Executar Migrations:

```bash
# Via Gradle
./gradlew flywayMigrate

# Via Spring Boot (automático)
./gradlew bootRun
```

### Verificar Status:

```bash
# Via Gradle
./gradlew flywayInfo

# Via SQL
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

---

## 🔍 Queries Comuns

### 1. Buscar Carrinho Aberto do Cliente

```sql
SELECT * FROM carts
WHERE customer_cpf = '12345678901'
  AND status = 'OPEN'
LIMIT 1;
```

**Índice Utilizado:** `idx_carts_customer_cpf_status`

---

### 2. Listar Itens de um Carrinho

```sql
SELECT 
    ci.id,
    ci.product_id,
    ci.product_name,
    ci.quantity,
    ci.product_price,
    ci.subtotal
FROM cart_items ci
WHERE ci.cart_id = 1
  AND ci.saved_for_later = FALSE
ORDER BY ci.created_at;
```

**Índice Utilizado:** `idx_cart_items_cart_id`

---

### 3. Calcular Total do Carrinho

```sql
SELECT 
    c.id,
    c.customer_cpf,
    c.total_amount,
    SUM(ci.subtotal) as calculated_total,
    COUNT(ci.id) as item_count
FROM carts c
LEFT JOIN cart_items ci ON c.id = ci.cart_id
WHERE c.id = 1
GROUP BY c.id, c.customer_cpf, c.total_amount;
```

---

### 4. Carrinhos Abandonados (últimas 24h)

```sql
SELECT 
    c.id,
    c.customer_cpf,
    c.channel,
    c.total_amount,
    c.total_items,
    c.updated_at
FROM carts c
WHERE c.status = 'OPEN'
  AND c.updated_at < NOW() - INTERVAL '24 hours'
  AND c.total_items > 0
ORDER BY c.updated_at DESC;
```

**Índice Utilizado:** `idx_carts_status`, `idx_carts_updated_at`

---

### 5. Produtos Mais Adicionados ao Carrinho

```sql
SELECT 
    ci.product_id,
    ci.product_name,
    COUNT(*) as times_added,
    SUM(ci.quantity) as total_quantity,
    AVG(ci.product_price) as avg_price
FROM cart_items ci
JOIN carts c ON ci.cart_id = c.id
WHERE c.created_at >= NOW() - INTERVAL '30 days'
GROUP BY ci.product_id, ci.product_name
ORDER BY times_added DESC
LIMIT 10;
```

---

### 6. Calcular Preço Final de um Item (com descontos)

```sql
SELECT 
    ci.id,
    ci.product_name,
    ci.quantity,
    ci.product_price,
    ci.subtotal,
    COALESCE(SUM(d.value), 0) as total_discount,
    ci.subtotal - COALESCE(SUM(d.value), 0) as final_price
FROM cart_items ci
LEFT JOIN cart_item_discounts d ON ci.id = d.cart_item_id
WHERE ci.id = 1
GROUP BY ci.id, ci.product_name, ci.quantity, ci.product_price, ci.subtotal;
```

**Índice Utilizado:** `idx_cart_item_discounts_cart_item_id`

---

### 7. Listar Itens do Carrinho com Descontos

```sql
SELECT 
    ci.id,
    ci.product_name,
    ci.quantity,
    ci.product_price,
    ci.subtotal,
    COALESCE(SUM(d.value), 0) as total_discount,
    ci.subtotal - COALESCE(SUM(d.value), 0) as final_price,
    COUNT(d.id) as discount_count
FROM cart_items ci
LEFT JOIN cart_item_discounts d ON ci.id = d.cart_item_id
WHERE ci.cart_id = 1
  AND ci.saved_for_later = FALSE
GROUP BY ci.id, ci.product_name, ci.quantity, ci.product_price, ci.subtotal
ORDER BY ci.created_at;
```

---

### 8. Listar Descontos de um Item

```sql
SELECT 
    d.id,
    d.type,
    d.value,
    d.created_at,
    d.created_by
FROM cart_item_discounts d
WHERE d.cart_item_id = 1
ORDER BY d.created_at;
```

---

### 9. Calcular Total do Carrinho (com descontos)

```sql
SELECT 
    c.id,
    c.customer_cpf,
    c.total_amount,
    SUM(ci.subtotal) as subtotal_without_discounts,
    COALESCE(SUM(d.value), 0) as total_discounts,
    SUM(ci.subtotal) - COALESCE(SUM(d.value), 0) as final_total,
    COUNT(DISTINCT ci.id) as item_count
FROM carts c
LEFT JOIN cart_items ci ON c.id = ci.cart_id AND ci.saved_for_later = FALSE
LEFT JOIN cart_item_discounts d ON ci.id = d.cart_item_id
WHERE c.id = 1
GROUP BY c.id, c.customer_cpf, c.total_amount;
```

---

### 10. Atualizar Total do Carrinho

```sql
UPDATE carts
SET 
    total_amount = (
        SELECT COALESCE(SUM(subtotal), 0)
        FROM cart_items
        WHERE cart_id = carts.id
    ),
    total_items = (
        SELECT COALESCE(SUM(quantity), 0)
        FROM cart_items
        WHERE cart_id = carts.id
    ),
    updated_at = CURRENT_TIMESTAMP
WHERE id = 1;
```

---

### 11. Finalizar Carrinho (Checkout)

```sql
UPDATE carts
SET 
    status = 'COMPLETED',
    completed_at = CURRENT_TIMESTAMP,
    updated_at = CURRENT_TIMESTAMP
WHERE id = 1
  AND status = 'OPEN';
```

---

### 12. Limpar Carrinhos Antigos

```sql
-- Deletar carrinhos vazios com mais de 30 dias
DELETE FROM carts
WHERE status = 'OPEN'
  AND total_items = 0
  AND created_at < NOW() - INTERVAL '30 days';

-- Arquivar carrinhos completados com mais de 90 dias
UPDATE carts
SET status = 'SAVED'
WHERE status = 'COMPLETED'
  AND completed_at < NOW() - INTERVAL '90 days';
```

---

## 📈 Estatísticas e Monitoramento

### Tamanho das Tabelas

```sql
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

### Uso dos Índices

```sql
SELECT 
    schemaname,
    tablename,
    indexname,
    idx_scan as index_scans,
    idx_tup_read as tuples_read,
    idx_tup_fetch as tuples_fetched
FROM pg_stat_user_indexes
WHERE schemaname = 'public'
ORDER BY idx_scan DESC;
```

### Estatísticas de Carrinhos

```sql
SELECT 
    status,
    channel,
    COUNT(*) as total,
    AVG(total_amount) as avg_amount,
    SUM(total_amount) as sum_amount,
    AVG(total_items) as avg_items
FROM carts
WHERE created_at >= NOW() - INTERVAL '30 days'
GROUP BY status, channel
ORDER BY status, channel;
```

---

## 🔐 Segurança e Permissões

### Usuários Recomendados

```sql
-- Usuário da aplicação (read/write)
CREATE USER sales_app WITH PASSWORD 'secure_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON carts, cart_items, cart_item_discounts TO sales_app;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO sales_app;

-- Usuário de leitura (analytics)
CREATE USER sales_readonly WITH PASSWORD 'secure_password';
GRANT SELECT ON carts, cart_items, cart_item_discounts TO sales_readonly;
```

---

## 🚀 Performance e Otimização

### Recomendações:

1. **Vacuum Regular**
   ```sql
   VACUUM ANALYZE carts;
   VACUUM ANALYZE cart_items;
   VACUUM ANALYZE cart_item_discounts;
   ```

2. **Atualizar Estatísticas**
   ```sql
   ANALYZE carts;
   ANALYZE cart_items;
   ANALYZE cart_item_discounts;
   ```

3. **Monitorar Queries Lentas**
   ```sql
   SELECT 
       query,
       calls,
       total_time,
       mean_time,
       max_time
   FROM pg_stat_statements
   WHERE query LIKE '%carts%'
   ORDER BY mean_time DESC
   LIMIT 10;
   ```

---

## 📚 Referências

- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)

---

## 📝 Changelog

| Data | Versão | Mudanças |
|------|--------|----------|
| 2025-12-26 | 1.0 | Criação inicial das tabelas carts e cart_items |
| 2025-12-30 | 2.0 | Documentação completa do schema |
| 2025-12-30 | 3.0 | Adição da tabela cart_item_discounts para múltiplos descontos por item |

---

**Autor:** Sales Platform Team  
**Última Revisão:** 30/12/2025

