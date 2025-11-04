# [cite_start]📦 Trabajo Práctico Integrador - Backend de Aplicaciones [cite: 1, 2]

## [cite_start]Sistema de Logística de Transporte de Contenedores [cite: 68]

[cite_start]Este proyecto es el Trabajo Práctico Integrador (1° Entrega) para la materia **Backend de Aplicaciones**[cite: 2]. El objetivo es diseñar e implementar la arquitectura backend para un sistema completo de logística, siguiendo un enfoque basado en microservicios.

---

## 👥 Integrantes

| Legajo | Nombre y Apellido | Email |
| :--- | :--- | :--- |
| 40032 | García Osella, Olivia | oliviaosella@gmail.com |
| 400621 | Samocachan, Ignacio | ignasamo2@gmail.com |
| 402597 | Quiroga, Lucía | luquiroga402597@gmail.com |
| 400202 | Eusebi, Gina | ginaeusebi9@gmail.com |

[cite_start][cite: 3]

---

## 🏛️ Arquitectura Planteada

[cite_start]La solución se basa en una arquitectura de **3 microservicios principales**, cada uno con responsabilidades claras[cite: 10]. [cite_start]La comunicación se centraliza a través de un **API Gateway** que gestiona las solicitudes bajo el protocolo REST[cite: 10].

[cite_start]Para la persistencia, se utiliza una **única base de datos PostgreSQL centralizada**[cite: 10, 17], compartida por todos los servicios. [cite_start]La seguridad y autenticación se delegan en **Keycloak**, que emite tokens JWT para validar los roles de los usuarios[cite: 210, 213].

### Diagrama de Contenedores (C4)

[cite_start]Este diagrama muestra la vista de alto nivel del sistema, sus contenedores (microservicios, base de datos, servicios externos) y las relaciones entre ellos[cite: 58].

*(**Nota**: Para que se vea, sube la captura de tu diagrama C4 al repositorio y reemplaza el path de abajo)*
![Diagrama C4](URL_A_TU_IMAGEN_DEL_DIAGRAMA_C4.png)

### Diagrama Entidad-Relación (DER)

[cite_start]El modelo de datos completo que da soporte a la solución[cite: 22].

*(**Nota**: Sube la captura de tu DER y reemplaza el path)*
![Diagrama DER](URL_A_TU_IMAGEN_DEL_DER.png)

---

## 🚚 Componentes Principales

El sistema se divide en los siguientes componentes:

### [cite_start]1. Microservicio Solicitudes [cite: 12]
* [cite_start]**Responsabilidad:** Administra la información de clientes, los contenedores y las solicitudes de traslado[cite: 12, 124].
* [cite_start]**Recursos que gestiona:** `CLIENTES`, `CONTENEDORES`, `SOLICITUDES`[cite: 144].

### [cite_start]2. Microservicio Rutas [cite: 152]
* [cite_start]**Responsabilidad:** Planifica y controla los recorridos logísticos, los depósitos intermedios y los estados de los tramos[cite: 13, 154].
* [cite_start]**Recursos que gestiona:** `RUTAS`, `TRAMOS`, `DEPOSITOS`, `GEOLOCALIZACION`, `TIPO_TRAMO`, `ESTADOS`[cite: 173].

### [cite_start]3. Microservicio Flota [cite: 180]
* [cite_start]**Responsabilidad:** Administra los camiones, transportistas y las tarifas aplicadas a los servicios[cite: 14, 182].
* [cite_start]**Recursos que gestiona:** `CAMIONES`, `TRANSPORTISTAS`, `TARIFAS`[cite: 198].

### Servicios Adicionales
* **API Gateway:** Punto de entrada único para todas las solicitudes. [cite_start]Valida tokens JWT [cite: 212] y enruta a los microservicios correspondientes.
* [cite_start]**Keycloak:** Servicio externo de autenticación y autorización[cite: 61, 210].
* [cite_start]**API Google Maps:** Servicio externo utilizado por el Microservicio Rutas para el cálculo de distancias[cite: 81, 168].

---

## 🛠️ Tecnologías Utilizadas

* **Lenguaje:** Java (implícito en el logo [pág. 1])
* [cite_start]**Framework:** Spring Boot (implícito en diagramas [cite: 66, 73, 83])
* [cite_start]**Base de Datos:** PostgreSQL [cite: 17]
* [cite_start]**Contenedores:** Docker [cite: 64, 67, 74, 78, 86]
* [cite_start]**Seguridad:** Keycloak (JWT) [cite: 61, 210]
* [cite_start]**APIs Externas:** Google Maps API [cite: 81]

---

## 🚀 Cómo Empezar (Próximamente)

*(Esta sección la pueden completar a medida que avancen con el código)*

### Prerrequisitos

* Java 17+
* Docker y Docker Compose
* Maven o Gradle

### Instalación y Ejecución

1.  Clonar el repositorio:
    ```bash
    git clone [https://github.com/NachoSamo/TP-Contenedores-Backend.git](https://github.com/NachoSamo/TP-Contenedores-Backend.git)
    cd TP-Contenedores-Backend
    ```

2.  Levantar todos los servicios con Docker Compose (cuando tengan el `docker-compose.yml`):
    ```bash
    docker-compose up -d --build
    ```

3.  El API Gateway estará disponible en `http://localhost:8080`.

---

## 🎥 Video 1° Entrega

* [cite_start][Enlace al video de la presentación](URL_DEL_VIDEO) [cite: 19] *(Reemplazar por el link real)*


---
## 📚 Comandos para ver la creacion de las tablas en PostgreSQL
docker exec -it postgres-db-contenedores bash
psql -U postgres -d tp_contenedores
\dt
