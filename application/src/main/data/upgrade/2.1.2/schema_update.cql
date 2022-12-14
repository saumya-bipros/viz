DROP MATERIALIZED VIEW IF EXISTS vizzionnaire.entity_view_by_tenant_and_name;
DROP MATERIALIZED VIEW IF EXISTS vizzionnaire.entity_view_by_tenant_and_search_text;
DROP MATERIALIZED VIEW IF EXISTS vizzionnaire.entity_view_by_tenant_and_customer;
DROP MATERIALIZED VIEW IF EXISTS vizzionnaire.entity_view_by_tenant_and_entity_id;

DROP TABLE IF EXISTS vizzionnaire.entity_views;

CREATE TABLE IF NOT EXISTS vizzionnaire.entity_view (
    id timeuuid,
    entity_id timeuuid,
    entity_type text,
    tenant_id timeuuid,
    customer_id timeuuid,
    name text,
    type text,
    keys text,
    start_ts bigint,
    end_ts bigint,
    search_text text,
    additional_info text,
    PRIMARY KEY (id, entity_id, tenant_id, customer_id, type)
);

CREATE MATERIALIZED VIEW IF NOT EXISTS vizzionnaire.entity_view_by_tenant_and_name AS
    SELECT *
    from vizzionnaire.entity_view
    WHERE tenant_id IS NOT NULL
      AND entity_id IS NOT NULL
      AND customer_id IS NOT NULL
      AND type IS NOT NULL
      AND name IS NOT NULL
      AND id IS NOT NULL
    PRIMARY KEY (tenant_id, name, id, customer_id, entity_id, type)
    WITH CLUSTERING ORDER BY (name ASC, id DESC, customer_id DESC);

CREATE MATERIALIZED VIEW IF NOT EXISTS vizzionnaire.entity_view_by_tenant_and_search_text AS
    SELECT *
    from vizzionnaire.entity_view
    WHERE tenant_id IS NOT NULL
      AND entity_id IS NOT NULL
      AND customer_id IS NOT NULL
      AND type IS NOT NULL
      AND search_text IS NOT NULL
      AND id IS NOT NULL
    PRIMARY KEY (tenant_id, search_text, id, customer_id, entity_id, type)
    WITH CLUSTERING ORDER BY (search_text ASC, id DESC, customer_id DESC);

CREATE MATERIALIZED VIEW IF NOT EXISTS vizzionnaire.entity_view_by_tenant_by_type_and_search_text AS
    SELECT *
    from vizzionnaire.entity_view
    WHERE tenant_id IS NOT NULL
      AND entity_id IS NOT NULL
      AND customer_id IS NOT NULL
      AND type IS NOT NULL
      AND search_text IS NOT NULL
      AND id IS NOT NULL
    PRIMARY KEY (tenant_id, type, search_text, id, customer_id, entity_id)
    WITH CLUSTERING ORDER BY (type ASC, search_text ASC, id DESC, customer_id DESC);

CREATE MATERIALIZED VIEW IF NOT EXISTS vizzionnaire.entity_view_by_tenant_and_customer AS
    SELECT *
    from vizzionnaire.entity_view
    WHERE tenant_id IS NOT NULL
      AND customer_id IS NOT NULL
      AND entity_id IS NOT NULL
      AND type IS NOT NULL
      AND search_text IS NOT NULL
      AND id IS NOT NULL
    PRIMARY KEY (tenant_id, customer_id, search_text, id, entity_id, type)
    WITH CLUSTERING ORDER BY (customer_id DESC, search_text ASC, id DESC);

CREATE MATERIALIZED VIEW IF NOT EXISTS vizzionnaire.entity_view_by_tenant_and_customer_and_type AS
    SELECT *
    from vizzionnaire.entity_view
    WHERE tenant_id IS NOT NULL
      AND customer_id IS NOT NULL
      AND entity_id IS NOT NULL
      AND type IS NOT NULL
      AND search_text IS NOT NULL
      AND id IS NOT NULL
    PRIMARY KEY (tenant_id, type, customer_id, search_text, id, entity_id)
    WITH CLUSTERING ORDER BY (type ASC, customer_id DESC, search_text ASC, id DESC);

CREATE MATERIALIZED VIEW IF NOT EXISTS vizzionnaire.entity_view_by_tenant_and_entity_id AS
    SELECT *
    from vizzionnaire.entity_view
    WHERE tenant_id IS NOT NULL
      AND customer_id IS NOT NULL
      AND entity_id IS NOT NULL
      AND type IS NOT NULL
      AND search_text IS NOT NULL
      AND id IS NOT NULL
    PRIMARY KEY (tenant_id, entity_id, customer_id, search_text, id, type)
    WITH CLUSTERING ORDER BY (entity_id DESC, customer_id DESC, search_text ASC, id DESC);