# MS-Envíos — Syncro Platform

Microservicio de gestión de envíos y despachos de la plataforma Syncro.  
Consume el evento `pedido.creado` desde RabbitMQ, crea el despacho automáticamente y envía una notificación por email al destinatario mediante MailerSend.

**Puerto:** `8084`  
**Base URL:** `http://localhost:8084`  
**Swagger UI:** `http://localhost:8084/swagger-ui/index.html`

---

## Tabla de Contenidos

1. [Descripción General](#descripción-general)
2. [Tecnologías Utilizadas](#tecnologías-utilizadas)
3. [Arquitectura y Patrones](#arquitectura-y-patrones)
4. [Requisitos Previos](#requisitos-previos)
5. [Instalación y Ejecución](#instalación-y-ejecución)
6. [Variables de Entorno](#variables-de-entorno)
7. [API REST — Endpoints](#api-rest--endpoints)
8. [Persistencia de Datos (JPA)](#persistencia-de-datos-jpa)
9. [Comunicación Asíncrona (RabbitMQ)](#comunicación-asíncrona-rabbitmq)
10. [Pruebas Unitarias](#pruebas-unitarias)
11. [Estructura del Proyecto](#estructura-del-proyecto)

---

## Descripción General

MS-Envíos es uno de los tres microservicios del ecosistema Syncro. Su responsabilidad única es coordinar el ciclo de vida de los despachos desde que un pedido es confirmado hasta que el producto llega al cliente.

**Flujo principal:**
1. MS-Pedidos publica el evento `pedido.creado` en RabbitMQ al confirmar un pedido.
2. MS-Envíos consume ese evento desde la cola `envio.generar`.
3. Se crea automáticamente un `Despacho` con estado inicial `PENDIENTE_RETIRO`.
4. Se envía un email de confirmación al destinatario vía MailerSend.
5. Los operadores pueden actualizar el estado del despacho mediante la API REST.

---

## Tecnologías Utilizadas

| Tecnología | Versión | Rol |
|---|---|---|
| Java | 21 | Lenguaje principal |
| Spring Boot | 3.3.5 | Framework base |
| Spring Data JPA | — | Persistencia ORM |
| Spring AMQP / RabbitMQ | — | Mensajería asíncrona |
| Spring Security | — | Seguridad HTTP |
| MySQL (Aiven) | — | Base de datos en producción |
| H2 (en memoria) | — | Base de datos en tests |
| MailerSend SDK / API | — | Notificaciones por email |
| SpringDoc OpenAPI | 2.6.0 | Documentación Swagger |
| JaCoCo | 0.8.11 | Cobertura de pruebas |
| Lombok | — | Reducción de boilerplate |
| Maven | 3.9.x | Gestión de dependencias y build |

---

## Arquitectura y Patrones

### Patrón Publisher/Subscriber (EDA)
MS-Envíos actúa como **suscriptor** en la arquitectura orientada a eventos. No conoce a MS-Pedidos; simplemente escucha la cola `envio.generar` del exchange `pedidos.exchange` (tipo Fanout). Esto garantiza desacoplamiento total: si MS-Envíos está caído, los mensajes se conservan en la cola hasta que el servicio vuelva.

### Patrón Repository (Spring Data JPA)
La capa de acceso a datos está completamente abstraída mediante interfaces que extienden `JpaRepository`. La lógica de negocio en `DespachoService` y `CostoEnvioService` no conoce detalles de SQL.

### Patrón Database per Service
MS-Envíos tiene su propia base de datos MySQL (`syncro_envios`) en Aiven, completamente independiente de MS-Pedidos y MS-Inventario. No existen foreign keys cruzadas entre bases de datos; la referencia a pedidos y empresas se realiza únicamente mediante IDs lógicos (`pedidoId`, `empresaId`).

### Retry con Backoff
El listener de RabbitMQ está configurado con reintentos automáticos: 3 intentos con un backoff fijo de 2000ms entre cada uno, previniendo la pérdida de mensajes ante fallos transitorios.

---

## Requisitos Previos

- **Java 21** instalado (`java -version`)
- **Maven 3.9+** instalado (`mvn -version`)
- Acceso a una instancia de **MySQL** (Aiven o local)
- Acceso a un broker **RabbitMQ** (CloudAMQP o local)
- API Key de **MailerSend** (para notificaciones por email)

---

## Instalación y Ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/sogomezc/syncro-ms-envios.git
cd syncro-ms-envios
```

### 2. Configurar variables de entorno

Crea un archivo `.env` en la raíz del proyecto o expórtalas en tu terminal (ver sección [Variables de Entorno](#variables-de-entorno)).

### 3. Compilar el proyecto

```bash
./mvnw clean package -DskipTests
```

### 4. Ejecutar el microservicio

```bash
./mvnw spring-boot:run
```

O con el JAR compilado:

```bash
java -jar target/envios-0.0.1-SNAPSHOT.jar
```

### 5. Verificar que está corriendo

```
GET http://localhost:8084/swagger-ui/index.html
```

---

## Variables de Entorno

Todas las credenciales se inyectan mediante variables de entorno. **Nunca** commitear valores reales en `application.properties`.

| Variable | Descripción | Ejemplo |
|---|---|---|
| `DB_URL` | URL de conexión JDBC a MySQL | `jdbc:mysql://host:port/syncro_envios?ssl-mode=REQUIRED` |
| `DB_USERNAME` | Usuario de la base de datos | `avnadmin` |
| `DB_PASSWORD` | Contraseña de la base de datos | `...` |
| `RABBITMQ_HOST` | Host del broker RabbitMQ | `hawk.rmq.cloudamqp.com` |
| `RABBITMQ_PORT` | Puerto RabbitMQ (default: 5672) | `5672` |
| `RABBITMQ_USERNAME` | Usuario de RabbitMQ | `usuario` |
| `RABBITMQ_PASSWORD` | Contraseña de RabbitMQ | `...` |
| `RABBITMQ_VHOST` | Virtual host de RabbitMQ | `/` |
| `MAILERSEND_API_KEY` | API Key de MailerSend | `mlsn.xxx...` |
| `MAILERSEND_FROM_EMAIL` | Email remitente verificado | `no-reply@syncro.cl` |

---

## API REST — Endpoints

La documentación interactiva completa está disponible en Swagger UI: `http://localhost:8084/swagger-ui/index.html`

### `GET /envios/pedido/{pedidoId}`
Obtiene el despacho asociado a un pedido específico, incluyendo historial de estados.

**Respuesta exitosa (200):**
```json
{
  "id": 1,
  "pedidoId": 5,
  "empresaId": 1,
  "estado": "PENDIENTE_RETIRO",
  "destinatarioNombre": "Juan Pérez",
  "destinatarioEmail": "juan@empresa.cl",
  "direccionCalle": "Av. Providencia",
  "direccionCiudad": "Santiago",
  "direccionRegion": "Región Metropolitana",
  "tipoEnvio": "ESTANDAR",
  "costoEnvio": 3990.00,
  "fechaCreacion": "2024-06-01T10:30:00",
  "historial": []
}
```

**Error (404):**
```json
{ "status": 404, "error": "Despacho no encontrado para pedido: 5" }
```

---

### `GET /envios/empresa/{empresaId}`
Lista todos los despachos de una empresa.

**Respuesta exitosa (200):** Array de objetos `DespachoResponse`.

---

### `PATCH /envios/{id}/estado`
Actualiza el estado de un despacho y registra el cambio en el historial.

**Body:**
```json
{
  "estado": "EN_PREPARACION",
  "observacion": "Preparando el paquete en bodega",
  "ubicacion": "Bodega Central Santiago"
}
```

**Estados válidos:** `PENDIENTE_RETIRO` → `EN_PREPARACION` → `DESPACHADO` → `EN_RUTA` → `ENTREGADO`

---

### `POST /envios/costo`
Calcula el costo de envío según tipo, región de destino y peso del paquete. Consulta la tabla `tarifa_envio` en base de datos.

**Body:**
```json
{
  "tipoEnvio": "ESTANDAR",
  "regionDestino": "Región Metropolitana",
  "pesoKg": 2.5
}
```

**Respuesta (200):**
```json
{
  "tipoEnvio": "ESTANDAR",
  "regionDestino": "Región Metropolitana",
  "pesoKg": 2.5,
  "costoTotal": 4240.00,
  "transportista": "Starken"
}
```

---

### `POST /envios/test/evento` *(solo para demo/testing)*
Simula la recepción del evento RabbitMQ `pedido.creado` sin necesitar el broker activo. Crea el despacho y envía el email de confirmación. Útil para demostrar el flujo EDA en la defensa.

**Body:**
```json
{
  "pedidoId": 10,
  "empresaId": 1,
  "destinatarioNombre": "Ana López",
  "destinatarioEmail": "ana@empresa.cl",
  "direccionCalle": "Calle Falsa",
  "direccionNumero": "123",
  "direccionCiudad": "Santiago",
  "direccionRegion": "Región Metropolitana",
  "direccionPais": "Chile",
  "items": [
    { "sku": "PROD-001", "nombre": "Laptop", "cantidad": 1 }
  ]
}
```

**Respuesta (200):**
```json
{
  "mensaje": "Evento procesado correctamente",
  "despachoId": 3,
  "pedidoId": 10,
  "estado": "PENDIENTE_RETIRO",
  "emailEnviado": "ana@empresa.cl"
}
```

---

## Persistencia de Datos (JPA)

La persistencia se implementa mediante **Spring Data JPA** con Hibernate como proveedor ORM. La configuración usa `ddl-auto=update`, por lo que las tablas se crean y actualizan automáticamente desde las entidades.

### Entidades principales

| Entidad | Tabla | Descripción |
|---|---|---|
| `Despacho` | `despacho` | Registro principal del envío asociado a un pedido |
| `HistorialEstadoEnvio` | `historial_estado_envio` | Auditoría de cada cambio de estado del despacho |
| `Transportista` | `transportista` | Empresas de transporte disponibles (Starken, Chilexpress, etc.) |
| `TarifaEnvio` | `tarifa_envio` | Tabla de tarifas por tipo de envío, región y rango de peso |
| `IncidenciaEnvio` | `incidencia_envio` | Registro de incidencias o problemas durante el despacho |

### Relaciones JPA

```
Despacho ─── ManyToOne ──▶ Transportista
Despacho ─── OneToMany ──▶ HistorialEstadoEnvio
Despacho ─── OneToMany ──▶ IncidenciaEnvio
TarifaEnvio ─ ManyToOne ──▶ Transportista
```

### Repositorios (Patrón Repository)

Todos los repositorios extienden `JpaRepository<Entidad, Long>`. Spring Data genera las implementaciones en tiempo de compilación a partir del nombre del método:

```java
// Ejemplo en DespachoRepository
Optional<Despacho> findByPedidoId(Long pedidoId);
List<Despacho> findByEmpresaId(Long empresaId);
boolean existsByPedidoId(Long pedidoId);
```

### Integridad referencial entre microservicios

No existen foreign keys cruzadas entre bases de datos. MS-Envíos almacena `pedidoId` y `empresaId` como columnas Long simples, garantizando independencia total entre servicios (patrón **Database per Service**).

---

## Comunicación Asíncrona (RabbitMQ)

### Exchange y colas

| Elemento | Nombre | Tipo |
|---|---|---|
| Exchange | `pedidos.exchange` | Fanout |
| Cola que escucha MS-Envíos | `envio.generar` | Durable |

El exchange tipo **Fanout** distribuye el mismo mensaje a todas las colas enlazadas simultáneamente. MS-Inventario recibe el mismo evento en su propia cola `inventario.sincronizar`, procesando en paralelo sin bloquearse mutuamente.

### Consumer

```java
@RabbitListener(queues = "${rabbitmq.queue.envio}")
public void consumir(PedidoCreadoEvent evento) {
    Despacho despacho = despachoService.crearDesdeEvento(evento);
    emailNotificacionService.enviarConfirmacionPedido(...);
}
```

### Idempotencia

Si el broker reenvía el mismo evento (por fallo o reintento), el servicio verifica con `existsByPedidoId()` antes de crear el despacho, retornando el existente sin duplicarlo.

---

## Pruebas Unitarias

### Ejecutar las pruebas

```bash
# Ejecutar todos los tests
./mvnw test

# Ejecutar y generar reporte de cobertura JaCoCo
./mvnw verify

# El reporte HTML se genera en:
# target/site/jacoco/index.html
```

### Configuración de tests

Los tests usan el perfil `test` con base de datos H2 en memoria y RabbitMQ mockeado, por lo que **no requieren infraestructura externa** para ejecutarse.

```properties
# src/test/resources/application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
```

### Cobertura requerida

JaCoCo está configurado para fallar el build (`mvn verify`) si la cobertura de líneas es inferior al **60%**.

### Tests implementados

| Clase de Test | Tipo | Tests | Descripción |
|---|---|---|---|
| `DespachoServiceTest` | Unitario (Mockito) | 6 | Creación de despacho, idempotencia, obtener por pedido/empresa, actualización de estado, not-found |
| `CostoEnvioServiceTest` | Unitario (Mockito) | 3 | Cálculo correcto, sin tarifa disponible, cálculo con kg extra |
| `EnviosControllerTest` | Web MVC (`@WebMvcTest`) | 4 | GET pedido, GET empresa, POST costo, POST test/evento |
| `EnviosApplicationTests` | Integración | 1 | Context loads con RabbitMQ mockeado |

**Total: 14 tests**

### Ejemplo de test de servicio

```java
@Test
@DisplayName("crearDesdeEvento - crea despacho correctamente")
void crearDesdeEvento_ok() {
    when(despachoRepository.existsByPedidoId(1L)).thenReturn(false);
    when(despachoRepository.save(any())).thenReturn(despacho);

    Despacho result = despachoService.crearDesdeEvento(evento);

    assertThat(result).isNotNull();
    assertThat(result.getPedidoId()).isEqualTo(1L);
    verify(despachoRepository).save(any());
}
```

### Ejemplo de test de controlador

```java
@Test
@WithMockUser
@DisplayName("POST /envios/costo - retorna costo calculado")
void calcularCosto_ok() throws Exception {
    when(costoEnvioService.calcular(any())).thenReturn(resp);

    mockMvc.perform(post("/envios/costo").with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.costoTotal").value(3990));
}
```

---

## Estructura del Proyecto

```
ms-envios/
├── src/
│   ├── main/
│   │   ├── java/com/Syncro/envios/
│   │   │   ├── EnviosApplication.java          # Entry point
│   │   │   ├── config/
│   │   │   │   ├── AppConfig.java              # RestTemplate, ObjectMapper
│   │   │   │   ├── RabbitMQConfig.java         # Exchange, colas, retry
│   │   │   │   ├── SecurityConfig.java         # Spring Security stateless
│   │   │   │   └── SwaggerConfig.java          # OpenAPI metadata
│   │   │   ├── controller/
│   │   │   │   └── EnviosController.java       # Endpoints REST
│   │   │   ├── dto/
│   │   │   │   ├── ActualizarEstadoRequest.java
│   │   │   │   ├── CostoEnvioRequest.java
│   │   │   │   ├── CostoEnvioResponse.java
│   │   │   │   ├── DespachoResponse.java
│   │   │   │   └── PedidoCreadoEvent.java      # DTO del evento RabbitMQ
│   │   │   ├── event/
│   │   │   │   └── PedidoCreadoConsumer.java   # @RabbitListener
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── model/
│   │   │   │   ├── Despacho.java
│   │   │   │   ├── HistorialEstadoEnvio.java
│   │   │   │   ├── IncidenciaEnvio.java
│   │   │   │   ├── TarifaEnvio.java
│   │   │   │   └── Transportista.java
│   │   │   ├── repository/
│   │   │   │   ├── DespachoRepository.java
│   │   │   │   ├── HistorialEstadoEnvioRepository.java
│   │   │   │   ├── IncidenciaEnvioRepository.java
│   │   │   │   ├── TarifaEnvioRepository.java
│   │   │   │   └── TransportistaRepository.java
│   │   │   └── service/
│   │   │       ├── CostoEnvioService.java
│   │   │       ├── DespachoService.java
│   │   │       └── EmailNotificacionService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/com/Syncro/envios/
│       │   ├── EnviosApplicationTests.java
│       │   ├── controller/EnviosControllerTest.java
│       │   └── service/
│       │       ├── CostoEnvioServiceTest.java
│       │       └── DespachoServiceTest.java
│       └── resources/
│           └── application-test.properties
├── pom.xml
└── README.md
```
