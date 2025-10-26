-- =========================================================
--  Esquema logístico - PostgreSQL
--  Notas:
--   - Usa ENUM para Estado.contexto (CONTENEDOR | SOLICITUD).
--   - Ajustá longitudes/precisión según tu dominio.
--   - Cambiá ON DELETE/UPDATE según tus reglas de negocio.
-- =========================================================

-- (Opcional) Schema dedicado
-- CREATE SCHEMA logistico;
-- SET search_path TO logistico, public;

-- =========================================================
-- Tipos y tablas de referencia primero
-- =========================================================

-- 1) Enum para el campo Estado.contexto
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'contexto_estado') THEN
        CREATE TYPE contexto_estado AS ENUM ('CONTENEDOR', 'SOLICITUD');
    END IF;
END$$;

-- 2) GEOLOCALIZACION
CREATE TABLE geolocalizacion (
    id_geo              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    direccion           VARCHAR(200),
    latitud             DOUBLE PRECISION NOT NULL,
    longitud            DOUBLE PRECISION NOT NULL,
    CONSTRAINT geoloc_lat_chk CHECK (latitud BETWEEN -90 AND 90),
    CONSTRAINT geoloc_lon_chk CHECK (longitud BETWEEN -180 AND 180)
);

-- 3) ESTADOS
CREATE TABLE estados (
    id_estado           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contexto            contexto_estado NOT NULL,
    descripcion         VARCHAR(200)
);

-- 4) CLIENTES
CREATE TABLE clientes (
    dni_cliente         BIGINT PRIMARY KEY,              -- según tu clase: PK int sin autogen
    nombre              VARCHAR(100)  NOT NULL,
    apellido            VARCHAR(100)  NOT NULL,
    telefono            VARCHAR(40)
);

-- 5) TRANSPORTISTAS
CREATE TABLE transportistas (
    id_transportista    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre              VARCHAR(100) NOT NULL,
    apellido            VARCHAR(100) NOT NULL,
    telefono            VARCHAR(40),
    email               VARCHAR(120),
    dni                 VARCHAR(30),
    fecha_nacimiento    DATE,
    activo              BOOLEAN NOT NULL DEFAULT TRUE
    -- Podés agregar UNIQUE(dni), UNIQUE(email) si aplica
);

-- 6) CAMIONES
CREATE TABLE camiones (
    dominio_camion          VARCHAR(20) PRIMARY KEY,          -- Patente
    id_transportista        BIGINT       NOT NULL,
    id_geo                  BIGINT,
    capacidad_peso_max      NUMERIC(12,3) NOT NULL,           -- kg
    capacidad_volumen_max   NUMERIC(12,3) NOT NULL,           -- m3
    disponibilidad          BOOLEAN       NOT NULL DEFAULT TRUE,
    consumo_prom_km         NUMERIC(10,3),                    -- l/100km o l/km (definí unidad)
    costo_traslado          NUMERIC(12,2),                    -- $/km o $ fijo (definí unidad)
    CONSTRAINT fk_camion_transportista
        FOREIGN KEY (id_transportista) REFERENCES transportistas(id_transportista)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_camion_geo
        FOREIGN KEY (id_geo) REFERENCES geolocalizacion(id_geo)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT chk_camion_caps CHECK (capacidad_peso_max > 0 AND capacidad_volumen_max > 0)
);

-- 7) DEPOSITOS
CREATE TABLE depositos (
    id_deposito             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre                  VARCHAR(120) NOT NULL,
    id_geolocalizacion      BIGINT      NOT NULL,
    costo_estadia_diaria    NUMERIC(12,2),
    CONSTRAINT fk_deposito_geo
        FOREIGN KEY (id_geolocalizacion) REFERENCES geolocalizacion(id_geo)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- 8) TIPO_TRAMO
CREATE TABLE tipo_tramo (
    id_tipo_tramo   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_tipo     VARCHAR(80) NOT NULL
    -- Podrías agregar UNIQUE(nombre_tipo)
);

-- =========================================================
-- Entidades operativas
-- =========================================================

-- 9) CONTENEDORES
CREATE TABLE contenedores (
    id_contenedor       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_estado           BIGINT NOT NULL,
    id_cliente          BIGINT NOT NULL,
    peso_kg             NUMERIC(12,3),          -- >0 si querés
    volumen_m3          NUMERIC(12,3),
    costo_base_km       NUMERIC(12,2),
    CONSTRAINT fk_cont_estado
        FOREIGN KEY (id_estado)  REFERENCES estados(id_estado)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_cont_cliente
        FOREIGN KEY (id_cliente) REFERENCES clientes(dni_cliente)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- 10) SOLICITUDES
CREATE TABLE solicitudes (
    nro_solicitud       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_contenedor       BIGINT NOT NULL,
    id_cliente          BIGINT NOT NULL,
    id_estado           BIGINT NOT NULL,
    costo_estimado      NUMERIC(12,2),
    tiempo_estimado     INTEGER,                -- mins/hrs/días (definí unidad)
    costo_real          NUMERIC(12,2),
    tiempo_real         INTEGER,
    fecha_creacion      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_sol_contenedor
        FOREIGN KEY (id_contenedor) REFERENCES contenedores(id_contenedor)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_sol_cliente
        FOREIGN KEY (id_cliente)    REFERENCES clientes(dni_cliente)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_sol_estado
        FOREIGN KEY (id_estado)     REFERENCES estados(id_estado)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- 11) RUTAS
CREATE TABLE rutas (
    id_ruta             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nro_solicitud       BIGINT NOT NULL,
    cantidad_tramos     INTEGER DEFAULT 0,
    cantidad_depositos  INTEGER DEFAULT 0,
    CONSTRAINT fk_ruta_solicitud
        FOREIGN KEY (nro_solicitud) REFERENCES solicitudes(nro_solicitud)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- 12) TRAMOS
CREATE TABLE tramos (
    id_tramo                        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_ruta                         BIGINT NOT NULL,
    origen_geo                      BIGINT NOT NULL,
    destino_geo                     BIGINT NOT NULL,
    origen_deposito_id              BIGINT,
    destino_deposito_id             BIGINT,
    tipo_tramo                      BIGINT NOT NULL,
    id_estado                       BIGINT NOT NULL,
    orden                           INTEGER NOT NULL,
    fechahora_inicio_estimada       TIMESTAMP WITHOUT TIME ZONE,
    fechahora_fin_estimada          TIMESTAMP WITHOUT TIME ZONE,
    fechahora_inicio_real           TIMESTAMP WITHOUT TIME ZONE,
    fechahora_fin_real              TIMESTAMP WITHOUT TIME ZONE,
    costo_aproximado                NUMERIC(12,2),
    costo_real                      NUMERIC(12,2),
    dominio_camion                  VARCHAR(20),     -- asignación del camión (puede ser NULL)
    CONSTRAINT fk_tramo_ruta
        FOREIGN KEY (id_ruta) REFERENCES rutas(id_ruta)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_tramo_origen_geo
        FOREIGN KEY (origen_geo) REFERENCES geolocalizacion(id_geo)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_tramo_destino_geo
        FOREIGN KEY (destino_geo) REFERENCES geolocalizacion(id_geo)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_tramo_ori_dep
        FOREIGN KEY (origen_deposito_id) REFERENCES depositos(id_deposito)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_tramo_des_dep
        FOREIGN KEY (destino_deposito_id) REFERENCES depositos(id_deposito)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_tramo_tipo
        FOREIGN KEY (tipo_tramo) REFERENCES tipo_tramo(id_tipo_tramo)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_tramo_estado
        FOREIGN KEY (id_estado) REFERENCES estados(id_estado)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_tramo_camion
        FOREIGN KEY (dominio_camion) REFERENCES camiones(dominio_camion)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT chk_tramo_orden CHECK (orden >= 1),
    CONSTRAINT chk_tramo_costos CHECK (
        (costo_aproximado IS NULL OR costo_aproximado >= 0) AND
        (costo_real IS NULL OR costo_real >= 0)
    )
    -- Si realmente querés 1:1 entre TRAMOS y CAMIONES en un momento dado,
    -- podrías agregar: UNIQUE(dominio_camion) (ojo: limitaría reutilización histórica)
);

-- 13) TARIFAS
CREATE TABLE tarifas (
    id_tarifa               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    dominio_camion          VARCHAR(20) NOT NULL,
    tipo_tarifa             VARCHAR(40) NOT NULL,        -- p.ej. 'POR_KM', 'FIJO', etc.
    costo_litro_combustible NUMERIC(10,3),
    cargo_gestion_tramo     NUMERIC(12,2),
    CONSTRAINT fk_tarifa_camion
        FOREIGN KEY (dominio_camion) REFERENCES camiones(dominio_camion)
        ON UPDATE CASCADE ON DELETE CASCADE
);

-- =========================================================
-- Índices sugeridos (optimizan joins y búsquedas comunes)
-- =========================================================

CREATE INDEX idx_contenedores_estado   ON contenedores (id_estado);
CREATE INDEX idx_contenedores_cliente  ON contenedores (id_cliente);

CREATE INDEX idx_solicitudes_estado    ON solicitudes (id_estado);
CREATE INDEX idx_solicitudes_contenedor ON solicitudes (id_contenedor);
CREATE INDEX idx_solicitudes_cliente   ON solicitudes (id_cliente);
CREATE INDEX idx_solicitudes_creacion  ON solicitudes (fecha_creacion DESC);

CREATE INDEX idx_rutas_solicitud       ON rutas (nro_solicitud);

CREATE INDEX idx_tramos_ruta           ON tramos (id_ruta, orden);
CREATE INDEX idx_tramos_estado         ON tramos (id_estado);
CREATE INDEX idx_tramos_camion         ON tramos (dominio_camion);
CREATE INDEX idx_tramos_origen_geo     ON tramos (origen_geo);
CREATE INDEX idx_tramos_destino_geo     ON tramos (destino_geo);

CREATE INDEX idx_depositos_geo         ON depositos (id_geolocalizacion);

CREATE INDEX idx_camiones_transportista ON camiones (id_transportista);
CREATE INDEX idx_camiones_geo          ON camiones (id_geo);
