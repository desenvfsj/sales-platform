-- Tabela de carrinhos
-- device em cada item e no carrinho

CREATE TABLE carts
(
    id           UUID PRIMARY KEY,
    loja_id      BIGINT,
    customer_cpf VARCHAR(11)    NOT NULL,
    channel      VARCHAR(20)    NOT NULL,
    status       VARCHAR(20)    NOT NULL DEFAULT 'OPEN',
    gross_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    net_amount   DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    total_items  INTEGER        NOT NULL DEFAULT 0,
    created_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by   VARCHAR(50),
    updated_at   TIMESTAMP,
    updated_by   VARCHAR(50),

    -- Constraints
    CONSTRAINT chk_gross_amount CHECK (gross_amount >= 0),
    CONSTRAINT chk_net_amount CHECK (net_amount >= 0),
    CONSTRAINT chk_net_not_exceed_gross CHECK (net_amount <= gross_amount),
    CONSTRAINT chk_total_items CHECK (total_items >= 0),
    CONSTRAINT chk_channel CHECK (channel IN ('WEB', 'APP', 'PDV', 'MOBILE')),
    CONSTRAINT chk_status CHECK (status IN ('OPEN', 'CHECKOUT_PENDING', 'COMPLETED', 'CANCELLED', 'SAVED')),
    CONSTRAINT chk_customer_cpf CHECK (LENGTH(customer_cpf) = 11 AND customer_cpf ~ '^[0-9]+$'),
    
    -- Unique constraint: um cliente pode ter apenas um carrinho por canal
    CONSTRAINT uk_carts_customer_cpf_channel UNIQUE (customer_cpf, channel)
);

-- Índices para otimização de queries
CREATE INDEX idx_carts_status ON carts (status);
CREATE INDEX idx_carts_customer_cpf_status ON carts (customer_cpf, status);
CREATE INDEX idx_carts_updated_at ON carts (updated_at);

-- Comentários
COMMENT ON TABLE carts IS 'Tabela de carrinhos de compras multi-canal';
COMMENT ON COLUMN carts.id IS 'Identificador único do carrinho';
COMMENT ON COLUMN carts.customer_cpf IS 'CPF do cliente proprietário do carrinho (11 dígitos)';
COMMENT ON COLUMN carts.channel IS 'Canal de origem: WEB, APP, PDV, MOBILE';
COMMENT ON COLUMN carts.status IS 'Status do carrinho: OPEN, CHECKOUT_PENDING, COMPLETED, CANCELLED, SAVED';
COMMENT ON COLUMN carts.gross_amount IS 'Valor bruto total do carrinho (soma dos subtotais dos itens)';
COMMENT ON COLUMN carts.net_amount IS 'Valor líquido total do carrinho (valor bruto - descontos)';
COMMENT ON COLUMN carts.total_items IS 'Quantidade total de itens no carrinho';
COMMENT ON COLUMN carts.created_at IS 'Data/hora de criação do carrinho';
COMMENT ON COLUMN carts.created_by IS 'Usuário que criou o carrinho';
COMMENT ON COLUMN carts.updated_at IS 'Data/hora da última atualização do carrinho';
COMMENT ON COLUMN carts.updated_by IS 'Usuário que fez a última atualização';

-- Tabela de itens do carrinho
CREATE TABLE cart_items
(
    id              UUID PRIMARY KEY,
    cart_id         UUID           NOT NULL,
    product_id      BIGINT         NOT NULL,
    product_name    VARCHAR(255)   NOT NULL,
    quantity        INTEGER        NOT NULL,
    unit_price      DECIMAL(10, 2) NOT NULL,
    subtotal        DECIMAL(10, 2) NOT NULL,
    total_discount  DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    saved_for_later BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(50),
    updated_at      TIMESTAMP,
    updated_by      VARCHAR(50),

    -- Foreign Key
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id)
        REFERENCES carts (id) ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT chk_quantity CHECK (quantity > 0),
    CONSTRAINT chk_unit_price CHECK (unit_price >= 0),
    CONSTRAINT chk_subtotal CHECK (subtotal >= 0),
    CONSTRAINT chk_total_discount CHECK (total_discount >= 0),
    CONSTRAINT chk_discount_not_exceed_subtotal CHECK (total_discount <= subtotal),

    -- Unique constraint: não permitir produto duplicado no mesmo carrinho
    CONSTRAINT uk_cart_product UNIQUE (cart_id, product_id)
);

-- Índices para otimização de queries
CREATE INDEX idx_cart_items_cart_id ON cart_items (cart_id);
CREATE INDEX idx_cart_items_product_id ON cart_items (product_id);
CREATE INDEX idx_cart_items_saved_for_later ON cart_items (saved_for_later);

-- Comentários
COMMENT ON TABLE cart_items IS 'Tabela de itens dos carrinhos de compras';
COMMENT ON COLUMN cart_items.id IS 'Identificador único do item';
COMMENT ON COLUMN cart_items.cart_id IS 'Referência ao carrinho (FK)';
COMMENT ON COLUMN cart_items.product_id IS 'ID do produto';
COMMENT ON COLUMN cart_items.product_name IS 'Nome do produto (snapshot no momento da adição)';
COMMENT ON COLUMN cart_items.unit_price IS 'Preço unitário no momento da adição (snapshot)';
COMMENT ON COLUMN cart_items.quantity IS 'Quantidade do produto no carrinho';
COMMENT ON COLUMN cart_items.subtotal IS 'Subtotal calculado (unit_price * quantity)';
COMMENT ON COLUMN cart_items.total_discount IS 'Soma total de todos os descontos aplicados ao item';
COMMENT ON COLUMN cart_items.saved_for_later IS 'Flag para salvar item para depois (não incluir no total do carrinho)';
COMMENT ON COLUMN cart_items.created_at IS 'Data/hora de adição do item ao carrinho';
COMMENT ON COLUMN cart_items.created_by IS 'Usuário que adicionou o item';
COMMENT ON COLUMN cart_items.updated_at IS 'Data/hora da última atualização do item';
COMMENT ON COLUMN cart_items.updated_by IS 'Usuário que fez a última atualização';


CREATE TABLE cart_item_discounts
(
    id                  UUID PRIMARY KEY,
    cart_item_id        UUID           NOT NULL,
    type                VARCHAR(20)    NOT NULL,
    base_amount         DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    discount_percentage DECIMAL(5, 2)  NOT NULL DEFAULT 0.00,
    discount_amount     DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    created_at          TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(50),
    updated_at          TIMESTAMP,
    updated_by          VARCHAR(50),

    CONSTRAINT fk_cart_item
        FOREIGN KEY (cart_item_id)
            REFERENCES cart_items (id)
            ON DELETE CASCADE,

    CONSTRAINT chk_base_amount
        CHECK (base_amount >= 0),

    CONSTRAINT chk_discount_percentage
        CHECK (discount_percentage >= 0 AND discount_percentage <= 100),

    CONSTRAINT chk_discount_amount
        CHECK (discount_amount >= 0),

    CONSTRAINT chk_discount_not_exceed_base
        CHECK (discount_amount <= base_amount),

    CONSTRAINT chk_discount_type
        CHECK (type IN ('BALCAO', 'GESTOR', 'PROMOCAO', 'PBM', 'PV'))
);

-- Índices para performance
CREATE INDEX idx_cart_item_discounts_cart_item_id ON cart_item_discounts (cart_item_id);
CREATE INDEX idx_cart_item_discounts_type ON cart_item_discounts (type);

-- Comentários para documentação
COMMENT ON TABLE cart_item_discounts IS 'Armazena múltiplos descontos aplicados a itens do carrinho';
COMMENT ON COLUMN cart_item_discounts.id IS 'Identificador único do desconto';
COMMENT ON COLUMN cart_item_discounts.cart_item_id IS 'Referência ao item do carrinho (FK)';
COMMENT ON COLUMN cart_item_discounts.type IS 'Tipo do desconto: BALCAO, GESTOR, PROMOCAO, PBM, PV';
COMMENT ON COLUMN cart_item_discounts.base_amount IS 'Valor base do produto (quantidade * valor unitário) sobre o qual o desconto é calculado';
COMMENT ON COLUMN cart_item_discounts.discount_percentage IS 'Percentual de desconto aplicado (0-100)';
COMMENT ON COLUMN cart_item_discounts.discount_amount IS 'Valor absoluto do desconto em reais';
COMMENT ON COLUMN cart_item_discounts.created_at IS 'Data/hora de criação do desconto';
COMMENT ON COLUMN cart_item_discounts.created_by IS 'Usuário que aplicou o desconto';
COMMENT ON COLUMN cart_item_discounts.updated_at IS 'Data/hora da última atualização do desconto';
COMMENT ON COLUMN cart_item_discounts.updated_by IS 'Usuário que fez a última atualização do desconto';