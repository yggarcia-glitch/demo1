# demo1 — API REST CRUD de `projects` (Kotlin + Spring Boot 3 + PostgreSQL)

API REST sencilla que gestiona una tabla `projects` con las operaciones CRUD
(crear, listar, obtener, actualizar, eliminar). Es un ejercicio de curso.

> **Para quien clona esto (o para el Claude de otra persona):** este README está
> escrito paso a paso. Si sigues las secciones en orden —requisitos → base de datos
> → ejecutar → probar— el proyecto levanta y responde. Al final está la lista exacta
> de comandos y los errores típicos con su solución.

---

## 1. Stack y versiones (exactas)

| Pieza              | Versión        | Nota |
|--------------------|----------------|------|
| Lenguaje           | Kotlin 2.3.20  | |
| Framework          | Spring Boot 3.3.4 | starter-web, data-jpa, validation |
| JDK                | Java 21        | obligatorio (el `pom.xml` fija `java.version=21` y `jvmTarget=21`) |
| Build              | Maven Wrapper **3.9.9** | se usa `./mvnw`, **no** hace falta instalar Maven aparte |
| Base de datos      | PostgreSQL     | base llamada `temporal`, puerto `5432` |
| Migraciones        | Flyway         | crea la tabla `projects` automáticamente al arrancar |
| Puerto de la app   | **8081**       | definido en `application.yaml` |

> ⚠️ **No uses Maven 3.10.0.** Con IntelliJ IDEA 2026.2 rompe (error Guice
> `ArtifactHandlerManager`). El wrapper de este repo ya está fijado en 3.9.9, así que
> si ejecutas con `./mvnw` no tienes que preocuparte. Si IntelliJ te marca ese error,
> ve a *Settings → Build Tools → Maven* y apunta a Maven 3.9.9.

---

## 2. Requisitos previos

1. **JDK 21** instalado (`java -version` debe decir 21).
2. **PostgreSQL** instalado y corriendo en `localhost:5432`.
3. **Git**.
4. No necesitas Maven global: el proyecto trae `mvnw` / `mvnw.cmd`.

---

## 3. Configuración de la base de datos

La app se conecta con estos datos (definidos en
`src/main/resources/application.yaml`):

- **URL:** `jdbc:postgresql://localhost:5432/temporal`
- **Usuario:** `postgres`
- **Contraseña:** `010101`

### 3.1. Crear la base `temporal`

Entra a `psql` (o pgAdmin) y crea la base **una sola vez**:

```sql
CREATE DATABASE temporal;
```

> Si tu PostgreSQL usa **otro usuario o contraseña**, edita esos tres valores en
> `src/main/resources/application.yaml` (sección `spring.datasource`) para que
> coincidan con tu instalación. No cambies nada más de ese archivo.

### 3.2. La tabla la crea Flyway solo

**No** tienes que crear la tabla `projects` a mano. Al arrancar, Flyway ejecuta
`src/main/resources/db/migration/V1__init.sql`, que la crea:

```sql
CREATE TABLE IF NOT EXISTS projects (
    id BIGSERIAL,
    nombre VARCHAR(1500),
    descripcion VARCHAR(10000),
    precio FLOAT,
    PRIMARY KEY (id)
);
```

> La app usa `jpa.hibernate.ddl-auto=validate`: Hibernate **no** crea ni modifica
> tablas, solo valida que la tabla de Flyway coincida con la entidad. Por eso el
> orden importa: Flyway crea, Hibernate valida.

---

## 4. Cómo ejecutar

Desde la carpeta raíz del proyecto:

**Windows (PowerShell o CMD):**
```powershell
.\mvnw.cmd spring-boot:run
```

**Linux / macOS / Git Bash:**
```bash
./mvnw spring-boot:run
```

La app queda escuchando en **http://localhost:8081**.
Cuando veas en consola que arrancó (Flyway aplicó `V1__init` y Tomcat está en 8081),
ya puedes probar los endpoints.

> También puedes abrir el proyecto en IntelliJ IDEA y darle *Run* a
> `Demo1Application.kt`.

---

## 5. Endpoints

Base: `http://localhost:8081/projects`

> 🔴 **Es `/projects` en PLURAL.** Si pones `/project` (singular) te sale
> `404 Not Found → "No static resource project."`. Ese es el error más común.

| Método | Ruta              | Qué hace                | Respuesta OK |
|--------|-------------------|-------------------------|--------------|
| GET    | `/projects`       | Lista todos             | `200` + array JSON |
| GET    | `/projects/{id}`  | Obtiene uno por id      | `200` + objeto / `404` si no existe |
| POST   | `/projects`       | Crea uno                | `201 Created` + `Location: /projects/{id}` |
| PUT    | `/projects/{id}`  | Reemplaza uno existente | `200` / `404` si no existe |
| DELETE | `/projects/{id}`  | Elimina uno             | `204 No Content` / `404` si no existe |

### Cuerpo JSON (para POST y PUT)

```json
{
  "nombre": "Proyecto Demo",
  "descripcion": "Texto de ejemplo",
  "precio": 150.0
}
```

Reglas de validación de la entidad:
- `nombre`: obligatorio, no vacío (`@NotBlank`).
- `precio`: debe ser `>= 0` (`@PositiveOrZero`).
- `id`: lo genera la base, **no lo mandes** en el cuerpo.

---

## 6. Cómo probar

### 6.1. Con curl

```bash
# POST (crear) -> debe responder 201
curl -i -X POST http://localhost:8081/projects \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Prueba","descripcion":"demo","precio":99.99}'

# GET (listar) -> debe responder 200
curl -i http://localhost:8081/projects
```

### 6.2. Con Postman

1. **POST** → URL `http://localhost:8081/projects`
   → pestaña **Body → raw → JSON** → pega el JSON de arriba → **Send** → esperas `201`.
2. **GET** → URL `http://localhost:8081/projects` → **Send** → esperas `200` con la lista.

Atajo: en Postman, botón **Import → Raw text**, pega el comando `curl` de POST y te
crea la request lista.

---

## 7. Estructura del proyecto

```
demo1/
├─ pom.xml                         # dependencias y config de build (Kotlin + Spring Boot)
├─ mvnw / mvnw.cmd                 # Maven Wrapper (3.9.9)
└─ src/
   ├─ main/
   │  ├─ kotlin/com/example/demo1/
   │  │  ├─ Demo1Application.kt            # arranque de Spring Boot
   │  │  └─ project/
   │  │     ├─ Project.kt                  # entidad JPA (tabla projects)
   │  │     ├─ ProjectRepository.kt        # interfaz Spring Data (extiende Repository, no JpaRepository)
   │  │     ├─ ProjectService.kt           # lógica CRUD
   │  │     └─ ProjectsController.kt        # endpoints REST en /projects
   │  └─ resources/
   │     ├─ application.yaml               # puerto 8081, datasource, flyway, jpa
   │     └─ db/migration/V1__init.sql      # crea la tabla projects (Flyway)
   └─ test/kotlin/...                      # test de contexto
```

Detalle de diseño: `ProjectRepository` extiende la interfaz base `Repository`
(marcador vacío) en lugar de `JpaRepository`, y declara cada método por su nombre
(`findAll`, `findById`, `save`, `deleteById`, `existsById`). Spring Data los
implementa solo. Así la interfaz expone únicamente lo que se usa.

---

## 8. Errores típicos y solución

| Síntoma | Causa | Solución |
|---------|-------|----------|
| `404 "No static resource project."` | URL en singular `/project` | Usa `/projects` (plural) |
| La app no arranca, error de conexión a BD | PostgreSQL apagado o base `temporal` no existe | Arranca PostgreSQL y crea la base `temporal` |
| Error de autenticación a la BD | usuario/clave distintos a `postgres` / `010101` | Ajusta `spring.datasource` en `application.yaml` |
| Error Guice `ArtifactHandlerManager` en IntelliJ | Maven 3.10.0 con IntelliJ 2026.2 | Usa Maven 3.9.9 (o ejecuta con `./mvnw`) |
| `Schema validation` / columna no coincide | la tabla no coincide con la entidad | deja que Flyway cree la tabla; no la edites a mano |
| Puerto 8081 ocupado | otra app usa el puerto | cierra esa app o cambia `server.port` en `application.yaml` |

---

## 9. Cambios hechos en esta sesión

- Se **verificaron los endpoints** levantando la app con `./mvnw spring-boot:run`
  (Maven wrapper 3.9.9) contra PostgreSQL local:
  - `POST /projects` → respondió `201 Created` con `Location: /projects/{id}`.
  - `GET /projects` → respondió `200 OK` con la lista en JSON.
- Se añadió este **`README.md`** con instrucciones completas de instalación,
  ejecución y pruebas.

> No se modificó el código fuente; estaba funcionando. Solo se documentó.

---

## 10. Comandos rápidos (resumen)

```bash
# 1. Crear la base (una vez, en psql)
CREATE DATABASE temporal;

# 2. Clonar y entrar
git clone https://github.com/yggarcia-glitch/demo1.git
cd demo1

# 3. Ejecutar
./mvnw spring-boot:run          # Windows: .\mvnw.cmd spring-boot:run

# 4. Probar (en otra terminal)
curl -i -X POST http://localhost:8081/projects -H "Content-Type: application/json" -d '{"nombre":"Prueba","descripcion":"demo","precio":99.99}'
curl -i http://localhost:8081/projects
```
