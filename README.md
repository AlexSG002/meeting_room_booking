# Meeting Booking

Aplicación para consultar salas, ver su disponibilidad y crear reservas. El repositorio contiene una API REST en Spring Boot, una interfaz web en Angular y un servicio separado para enviar notificaciones por correo.

## Tecnologías

- Java 21, Spring Boot, Spring Data JPA y MySQL
- Flyway para aplicar las migraciones de base de datos
- Apache Kafka para comunicar la API con el servicio de notificaciones
- Angular, TypeScript y Node.js
- Docker Compose para iniciar MySQL y Kafka en local
- JUnit, Mockito y Testcontainers para las pruebas del backend

## Requisitos

- JDK 21
- Node.js 24 y npm
- Docker Desktop con Docker Compose
- Git
- Una clave de Resend solo si quieres iniciar el servicio que envía correos

## Arranque local

Abre una terminal en la carpeta raíz del repositorio y arranca MySQL y Kafka:

```powershell
docker compose up -d
docker compose ps
```

La API usa MySQL en `localhost:3306` y Kafka en `localhost:9092`. Flyway crea y actualiza las tablas automáticamente al iniciar la API.

### API

En una terminal nueva:

```powershell
cd meeting-booking-api
.\mvnw.cmd spring-boot:run
```

La API queda disponible en `http://localhost:8080`. La documentación interactiva de OpenAPI, si la aplicación ha arrancado correctamente, está en `http://localhost:8080/swagger-ui/index.html`.

### Datos iniciales de prueba

En una base de datos nueva, Flyway crea las tablas pero no añade salas ni empleados. La API todavía no ofrece endpoints para crear esos registros, así que para probar reservas hay que añadirlos una vez desde MySQL. Primero inicia la API para que Flyway cree el esquema. Después, desde la raíz del repositorio, abre el cliente MySQL del contenedor:

```powershell
docker compose exec mysql mysql -uroot -proot meetingBooking
```

En el prompt de MySQL, inserta ejemplos y consulta los IDs generados:

```sql
INSERT INTO room (name, capacity, location)
VALUES ('Sala de reuniones', 8, 'Madrid');

INSERT INTO employee (first_name, last_name, email)
VALUES ('Ada', 'Lovelace', 'ada@example.com');

SELECT id, name FROM room;
SELECT id, first_name, last_name, email FROM employee;
```

Usa los IDs devueltos al crear una reserva. Estos datos permanecen al detener Docker con `docker compose down`, porque la base usa un volumen persistente. No repitas los `INSERT` con el mismo correo: `employee.email` es único.

### Interfaz web

En otra terminal:

```powershell
cd meeting-booking-frontend
npm ci
npm start
```

Abre `http://localhost:4200`. La interfaz llama a la API local en el puerto `8080`.

### Servicio de notificaciones (opcional)

La API puede guardar y publicar eventos de reserva aunque no se inicie este servicio. Para enviar correos, necesitas una clave de Resend. En otra terminal de PowerShell:

```powershell
$env:RESEND_API_KEY = "tu_clave_de_resend"
cd notification-service
.\mvnw.cmd spring-boot:run
```

El servicio escucha los eventos de reserva en Kafka y envía el correo al empleado asociado.

Para detener MySQL y Kafka, ejecuta desde la raíz:

```powershell
docker compose down
```

## Ejecutar build y pruebas localmente

Las pruebas de los servicios Java usan Testcontainers; Docker debe estar en ejecución.

API:

```powershell
cd meeting-booking-api
.\mvnw.cmd verify
```

Servicio de notificaciones (la clave de prueba solo permite cargar el contexto; las pruebas no envían correos reales):

```powershell
$env:RESEND_API_KEY = "test-key"
cd notification-service
.\mvnw.cmd verify
```

Frontend:

```powershell
cd meeting-booking-frontend
npm ci
npm test -- --watch=false
npm run build
```

## Integración continua

GitHub Actions ejecuta automáticamente el build y las pruebas al hacer `push` o abrir/actualizar un pull request:

- `meeting-booking-api`: `mvnw verify`, con Java 21 y Testcontainers.
- `notification-service`: `mvnw verify`, con Java 21 y Testcontainers.
- `meeting-booking-frontend`: instalación reproducible con `npm ci`, pruebas y build de producción.

El flujo está en [`.github/workflows/ci.yml`](.github/workflows/ci.yml). Si alguna comprobación falla, GitHub marca el flujo como fallido y muestra los logs del paso correspondiente.

## Flujo de reservas y eventos

La API guarda la reserva y su evento en la tabla Outbox dentro de una transacción. Un proceso programado publica los eventos pendientes en Kafka y reintenta los fallidos. El servicio de notificaciones consume el evento y envía el correo.
