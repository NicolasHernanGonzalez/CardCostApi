# 💳 Card Cost API

## 🧾 Descripción

La **Card Cost API** es una solución desarrollada en Java como parte del proceso técnico de Etraveli Group. Su objetivo es exponer una API REST que permita consultar el costo de clearing de una tarjeta de crédito/débito utilizando su número (PAN), así como realizar operaciones CRUD sobre la tabla de costos asociados a cada país emisor.

---

## ⚙️ Tecnologías y herramientas utilizadas

- **Java**: 17
- **Spring Boot**: 3.x
- **Maven**: Para gestión de dependencias y build
- **Spring Web**: Para la creación de endpoints REST
- **Spring Validation**: Para validaciones automáticas de entrada
- **Spring Cache + Caffeine**: Caché con tamaño máximo y expiración para el bin lookup
- **Resilience4j**: Circuit breaker para llamadas a servicios externos
- **JUnit & Mockito**: Tests unitarios
- **Testcontainers & SpringBootTest**: Tests de integración
- **RestTemplate**: Cliente HTTP para llamar a la API pública de [https://binlist.net](https://binlist.net)
- **H2 Database**: Base de datos en memoria para testing y desarrollo
- **Docker (futuro)**: Se planifica contenerización para facilitar el despliegue

---

## 🏗️ Estructura del proyecto

```plaintext
com.cardcostapi
├── controller       # Endpoints REST
├── service          # Lógica de negocio
├── repository       # Acceso a base de datos
├── domain/model     # Entidades y DTOs
├── external         # Cliente a binlist.net
├── config           # Configuraciones (cache, restTemplate, etc.)
├── exception        # Manejo centralizado de errores
└── CardCostApiApplication.java
```

---

## 🔧 Endpoints expuestos

### Consultar costo por número de tarjeta
- **POST** `/api/payment-cards-cost`
```json
{
  "card_number": "4571736009872913"
}
```
**Response:**
```json
{
  "country": "AR",
  "cost": 77
}
```

---

### CRUD sobre los costos por país

#### 📥 Crear nuevo costo
- **POST** `/api/cost`
- **Request Body**:
```json
{
  "country": "UY",
  "cost": 90.1
}
```
- **Response (201 Created)**:
```json
{
  "country": "BR",
  "cost": 8.1
}
```

#### 📖 Obtener costo por país
- **GET** `/api/cost/{country}`
- **Path Param**: código de país (ej. `US`, `GR`)
- **Response (200 OK)**:
```json
{
  "country": "US",
  "cost": 5.0
}
```
- **Response (404 Not Found)** si no existe el país solicitado

#### 📝 Actualizar costo por país
- **PUT** `/api/cost/{id}`
- **Path Param**: `id` interno de la entidad (numérico)
- **Request Body**:
```json
{
  "country": "US",
  "cost": 6.5
}
```
- **Response (200 OK)**:
```json
{
  "country": "US",
  "cost": 6.5
}
```
- **Response (404 Not Found)** si no existe

#### ❌ Eliminar costo por país
- **DELETE** `/api/cost/{country}`
- **Path Param**: código de país
- **Response (204 No Content)**

---

## 🚨 Manejo de errores

La API maneja errores de forma centralizada con respuestas consistentes. Algunos escenarios contemplados:

- **400 Bad Request**: Datos inválidos o errores de validación.
- **404 Not Found**: País o recurso no encontrado.
- **409 Conflict**: Violación de integridad referencial o duplicados.
- **424 Failed Dependency**: Error en dependencias externas (como binlist.net).

Ejemplo de respuesta:
```json
{
  "timestamp": "2025-07-09T22:15:30",
  "message": "Country code is invalid"
}
```

---

## 🚀 Instrucciones de ejecución

### Requisitos previos
- Java 17
- Maven 3.x

### Ejecución local
```bash
mvn clean install
mvn spring-boot:run
```

---

## 🧪 Testing

- Para correr todos los tests:
```bash
mvn test
```

- Se incluyen tests unitarios para lógica de negocio y tests de integración para endpoints y llamadas a binlist.net (mockeadas).

---

## ⚠️ Consideraciones
- El BIN por lo investigado es de 8 DIGITOS. (el enunciado dice que puede ser entre 6 y 8).
- Se incluye **cache con Caffeine** para evitar múltiples llamadas al endpoint de binlist por el mismo BIN, con configuración de tamaño máximo (`1000`) y expiración (`24h`).
- Se implementa un **Circuit Breaker (Resilience4j)** para proteger la API ante fallos externos.
- Validaciones exhaustivas de entrada y manejo centralizado de excepciones.
- Arquitectura orientada a separación de responsabilidades (controller, service, repository, external client).
- Uso de **H2 in-memory DB** para facilitar testing y desarrollo rápido.

---

## 📈 Escalabilidad y mejoras futuras

- Preparado para escalar horizontalmente (stateless, sin sesión de usuario ni estado en memoria)
- Uso de **Spring Cache con Caffeine**, configurable y con políticas de expiración para mitigar la carga externa
- La capa de persistencia fue desacoplada mediante una interfaz (`ClearingCostRepository`), lo que permite reemplazar fácilmente la implementación actual basada en JPA por otras fuentes como Redis, DynamoDB o servicios externos, sin modificar la lógica del servicio.
- El diseño sigue principios SOLID, en particular **Open/Closed** y **Dependency Inversion**, lo que facilita la extensibilidad y el testing.
- **Separación de responsabilidades clara** (Controller, Service, Repository, External Client)
- Fácil de contenerizar (Docker-ready) y listo para integrarse en pipelines CI/CD

---

## 🐳 Uso con Docker Compose

Si tenés Docker y Docker Compose instalados, podés levantar la aplicación fácilmente con:

```bash
docker-compose up --build
```

Esto construirá la imagen a partir del `Dockerfile` y expondrá la API en:

> http://localhost:8080

No se requiere base de datos externa, ya que la aplicación utiliza **H2 embebido** para persistencia temporal.

## 👨‍💻 Autor

- **Nombre**: Nicolas Gonzalez

---
