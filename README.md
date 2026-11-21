
# Sistema de Gestión de Inventario

API REST desarrollada con Java y Spring Boot para administrar el inventario de una tienda de tecnología.

## 1. Objetivo

Gestionar productos, categorías y movimientos de inventario, permitiendo controlar las entradas, salidas y disponibilidad de los productos.

## 2. Tecnologías utilizadas

- Java 21
- Spring Boot 4.1.2
- Spring Data JPA
- MySQL 8
- Maven
- JUnit 5
- Mockito
- Swagger / OpenAPI
- Postman

## 3. Funcionalidades

- Registrar, consultar, actualizar y eliminar categorías.
- Registrar, consultar, actualizar y eliminar productos.
- Registrar entradas de inventario.
- Registrar salidas de inventario.
- Consultar el historial de movimientos.
- Consultar productos con stock bajo.
- Consultar productos agotados.
- Validar los datos recibidos.
- Controlar las reglas de negocio.
- Documentar y probar los endpoints mediante Swagger.

## 4. Requisitos

- JDK 21.
- MySQL 8.
- Visual Studio Code o un IDE compatible.
- Conexión a internet para descargar las dependencias.

## 5. Configuración de la base de datos

El sistema utiliza MySQL 8 como gestor de base de datos y se conecta mediante Spring Data JPA.

La base de datos utilizada por el proyecto se llama `inventario_db`.

Para crearla manualmente en MySQL, ejecutar:

```sql
CREATE DATABASE inventario_db;
```

La conexión se configura en el archivo `src/main/resources/application.properties` mediante variables de entorno:

```properties
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:mysql://localhost:3306/inventario_db}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:root}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:}
```

Si se ejecuta el proyecto con Docker Compose, la contraseña de MySQL debe definirse en el archivo `.env` mediante la variable `MYSQL_ROOT_PASSWORD`.

El archivo `.env` contiene información privada y no debe publicarse en GitHub.

## 6. Instalación y ejecución

Clonar o descargar el proyecto.

Abrir una terminal en la carpeta principal y ejecutar:

```powershell
.\mvnw.cmd compile
```

Para ejecutar las pruebas automatizadas:

```powershell
.\mvnw.cmd test
```

Para iniciar la aplicación:

```powershell
.\mvnw.cmd spring-boot:run
```

La API estará disponible en:

http://localhost:8080

## 7. Endpoints principales

### Categorías

- POST `/api/categorias`
- GET `/api/categorias`
- GET `/api/categorias/{id}`
- PUT `/api/categorias/{id}`
- DELETE `/api/categorias/{id}`

### Productos

- POST `/api/productos`
- GET `/api/productos`
- GET `/api/productos/{id}`
- PUT `/api/productos/{id}`
- DELETE `/api/productos/{id}`
- GET `/api/productos/bajo-stock`
- GET `/api/productos/agotados`

### Movimientos

- POST `/api/movimientos/entrada`
- POST `/api/movimientos/salida`
- GET `/api/movimientos`
- GET `/api/movimientos/producto/{idProducto}`

## 8. Documentación Swagger

Con la aplicación iniciada, acceder a:

http://localhost:8080/swagger-ui/index.html

Desde allí se pueden consultar y ejecutar los endpoints de la API.

## 9. Pruebas automatizadas

El proyecto utiliza JUnit 5 y Mockito para verificar las reglas de negocio.

Actualmente cuenta con 10 pruebas automatizadas aprobadas.

Para ejecutarlas:

```powershell
.\mvnw.cmd test
```

## 10. Arquitectura

El proyecto utiliza una arquitectura por capas:

- **Model:** entidades del sistema.
- **Repository:** acceso a la base de datos.
- **Service:** lógica y reglas de negocio.
- **Controller:** exposición de los endpoints REST.
- **DTO:** transferencia y validación de datos.
- **Exception:** manejo centralizado de errores.
- **Config:** configuración de la documentación OpenAPI.

## 11. Ejecución con Docker

El proyecto cuenta con Docker y Docker Compose para facilitar la ejecución de la API REST y la base de datos MySQL mediante contenedores.

### Requisitos

- Docker Desktop instalado y en ejecución.
- Docker Compose.

### Iniciar el sistema

Desde la carpeta principal del proyecto, ejecutar:

```powershell
docker compose up --build -d
```

Este comando construye la imagen de la API e inicia los contenedores de MySQL y Spring Boot.

### Verificar los contenedores

```powershell
docker compose ps
```

Los servicios esperados son:

- `inventario-mysql`: servidor de base de datos MySQL.
- `inventario-api`: aplicación Spring Boot.

### Consultar los registros

```powershell
docker compose logs api
```

Para consultar los registros de MySQL:

```powershell
docker compose logs mysql
```

### Acceder a la API

Con los contenedores activos, la API estará disponible en:

http://localhost:8080

La documentación Swagger estará disponible en:

http://localhost:8080/swagger-ui/index.html

### Detener el sistema

```powershell
docker compose down
```

Este comando detiene y elimina los contenedores, pero conserva los datos almacenados en el volumen de MySQL.

**Importante:** No utilizar `docker compose down -v` si se desea conservar la información de la base de datos.

## 12. Configuración y protección de credenciales

El proyecto utiliza un archivo `.env` para almacenar variables de entorno necesarias para la configuración de Docker Compose.

La contraseña de MySQL se configura mediante la variable `MYSQL_ROOT_PASSWORD`.

Por seguridad:

- No se deben publicar contraseñas en el repositorio.
- El archivo `.env` debe mantenerse fuera del control de versiones.
- El archivo `.gitignore` debe incluir `.env`.
- Las credenciales utilizadas en entornos reales deben ser seguras y no compartirse.