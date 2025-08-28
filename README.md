# Recruiting-Platform

Recruiting-Platform es un sistema backend de una plataforma de reclutamiento, diseñado para ayudar a los candidatos y a los empleadores a coincidir de forma eficiente.

## Características

- Registro e inicio de sesión de usuarios (autenticación JWT)
- Publicación y consulta de ofertas de trabajo
- Envío de candidaturas y seguimiento de estado
- Manejo de excepciones y respuestas de error globales

## Stack tecnológico

- Spring Boot 3.3.2
- Spring Security (autenticación JWT)
- Spring Data JPA
- H2 Database (persistencia en archivo)
- Maven 3.x

## Endpoints

### Autenticación

1. Registro de usuario
   - Método: POST
   - URL: `/api/auth/register`
   - Body (JSON):
     ```json
     {
       "username": "usuario_prueba",
       "email": "usuario@correo.com",
       "password": "MiPassword123",
       "firstName": "Nombre",
       "lastName": "Apellido"
     }
     ```

2. Inicio de sesión
   - Método: POST
   - URL: `/api/auth/login`
   - Body (JSON):
     ```json
     {
       "email": "usuario@correo.com",
       "password": "MiPassword123"
     }
     ```
   - La respuesta incluye el token JWT y la información del usuario (el token solo se devuelve al iniciar sesión).

### Usuarios

3. Obtener información del usuario actual
   - Método: GET
   - URL: `/api/user/me`
   - Header: `Authorization: Bearer <TU_TOKEN>`

4. Listar todos los usuarios (solo ADMIN)
   - Método: GET
   - URL: `/api/user`
   - Header: `Authorization: Bearer <TU_TOKEN>`

5. Obtener un usuario por ID (solo ADMIN)
   - Método: GET
   - URL: `/api/user/{id}`
   - Header: `Authorization: Bearer <TU_TOKEN>`

6. Eliminar usuario por ID (solo ADMIN)
   - Método: DELETE
   - URL: `/api/user/{id}`
   - Header: `Authorization: Bearer <TU_TOKEN>`

7. Eliminar usuario por email (solo ADMIN)
   - Método: DELETE
   - URL: `/api/user`
   - Parámetro: `email=usuario@correo.com`
   - Header: `Authorization: Bearer <TU_TOKEN>`

8. Registrar administrador (solo ADMIN)
   - Método: POST
   - URL: `/api/user/admin`
   - Header: `Authorization: Bearer <TU_TOKEN>`
   - Body (JSON):
     ```json
     {
       "email": "admin@correo.com",
       "password": "MiPassword123",
       "firstName": "Nombre",
       "lastName": "Apellido",
       "role": "ROLE_ADMIN"
     }
     ```

### Jobs (Ofertas de trabajo)

1. Listar ofertas (público)
   - Método: GET
   - URL: `/api/jobs`
   - Auth: no requiere token
   - Respuesta: 200 con lista de jobs (puede estar vacía)

2. Obtener oferta por ID (público)
   - Método: GET
   - URL: `/api/jobs/{id}`
   - Auth: no requiere token
   - Respuesta: 200 con el job si existe; 404 si no existe

Notas sobre creación/gestión de jobs:
- Actualmente no hay endpoints para crear/editar/eliminar jobs vía API. Los jobs suelen tener un recruiter (usuario con rol ADMIN o RECRUITER) asociado.
- Para pruebas rápidas, puedes insertar registros desde la consola H2 (http://localhost:8080/h2-console) asegurándote de usar un recruiter_id válido.
  Ejemplo SQL de inserción (ajusta IDs y fechas):
  ```sql
  INSERT INTO jobs (title, description, location, posted_date, recruiter_id)
  VALUES ('Desarrollador Backend', 'Java/Spring', 'Remoto', CURRENT_TIMESTAMP, 1);
  ```

### Candidaturas (Applications)

1. Aplicar a una oferta
   - Método: POST
   - URL: `/api/applications/apply/{jobId}`
   - Header: `Authorization: Bearer <TU_TOKEN>`
   - Body: vacío
   - Respuestas:
     - 200 OK: "Application submitted successfully"
     - 401 Unauthorized: si no envías token
     - 404 Not Found: si el job no existe
     - 409 Conflict: si ya aplicaste antes a ese job

2. Ver mis candidaturas
   - Método: GET
   - URL: `/api/applications/my-applications`
   - Header: `Authorization: Bearer <TU_TOKEN>`
   - Respuesta: 200 con lista de candidaturas del usuario autenticado. Cada elemento incluye:
     - id, appliedDate, status (APPLIED/REVIEWING/REJECTED/HIRED), y referencias a job y candidate.

Notas y permisos
- Todos los endpoints bajo `/api/applications/**` requieren un token JWT válido (cualquier usuario autenticado).
- Los estados de candidatura posibles son: APPLIED, REVIEWING, REJECTED, HIRED.

## Persistencia de datos

La base de datos está configurada en modo de archivo; los datos persisten entre reinicios de la aplicación. Los archivos de la base se almacenan en la carpeta `data` en la raíz del proyecto.

## Ejecutar el proyecto

1. Asegúrate de tener instalado JDK 21 y Maven 3.x
2. En la raíz del proyecto ejecuta:
   ```
   mvn spring-boot:run
   ```
3. Accede a la consola H2: http://localhost:8080/h2-console
   - JDBC URL: jdbc:h2:file:./data/recruitingdb
   - Usuario: sa
   - Contraseña: password

## Uso de JWT

1. Tras registrarte no se devuelve token automáticamente; usa tu email y contraseña en `/api/auth/login` para obtener el token JWT.
2. Al iniciar sesión recibirás un token JWT (vigencia por defecto: 24 horas).
3. Para acceder a endpoints protegidos, añade en el header:
   ```
   Authorization: Bearer TU_TOKEN_JWT
   ```


## Guía paso a paso con Postman

Sigue estos pasos para verificar que la aplicación funciona y evitar errores comunes (401/403):

- Base URL: http://localhost:8080
- Todas las rutas que empiezan con /api/auth/** no requieren token.
- Las rutas bajo /api/user/** requieren token (excepto POST /api/user/admin para el primer admin o /api/auth/register/admin).

1) Crear el primer ADMIN (sin token)
- Método: POST
- URL: http://localhost:8080/api/auth/register/admin
- Body (JSON):
  {
    "email": "admin@correo.com",
    "password": "Admin12345",
    "firstName": "Admin",
    "lastName": "Inicial"
  }
- Respuesta esperada: 201 Created con mensaje “Administrador registrado con éxito. Ahora inicia sesión para obtener tu token”.
- Importante: Solo permite anónimo si todavía no existe un ADMIN. Si ya existe, verás 403.

2) Iniciar sesión (obtener JWT)
- Método: POST
- URL: http://localhost:8080/api/auth/login
- Body (JSON):
  {
    "email": "admin@correo.com",
    "password": "Admin12345"
  }
- Respuesta: 200 OK con campo token. Copia el token.

3) Configurar token en Postman
- En la pestaña Authorization de la request protegida, elige “Bearer Token” y pega el token.
- Alternativamente, crea un Environment con variable token y usa {{token}}.

4) Consultar tu perfil (privado)
- Método: GET
- URL: http://localhost:8080/api/user/me
- Header: Authorization: Bearer TU_TOKEN
- Respuesta: 200 con los datos del usuario (id, email, role).

5) Listar usuarios (solo ADMIN)
- Método: GET
- URL: http://localhost:8080/api/user
- Header: Authorization: Bearer TU_TOKEN
- Respuesta: 200 con lista de usuarios. Si no eres ADMIN, verás 403 Forbidden.

6) Registrar un usuario normal (sin token)
- Método: POST
- URL: http://localhost:8080/api/auth/register
- Body (JSON):
  {
    "email": "usuario@correo.com",
    "password": "Usuario12345",
    "firstName": "Juan",
    "lastName": "Pérez",
    "role": "USER"  
  }
- Nota: "USER" es un alias y se guardará como ROLE_CANDIDATE.
- Respuesta: 201 Created con datos del usuario y sin token (debes loguearte para obtener token).

7) Iniciar sesión como usuario normal (opcional)
- Método: POST
- URL: http://localhost:8080/api/auth/login
- Body (JSON):
  {
    "email": "usuario@correo.com",
    "password": "Usuario12345"
  }
- Respuesta: 200 con token. Con este token podrás acceder a /api/user/me, pero NO a /api/user (lista) porque requiere ADMIN.

8) Crear más administradores (cuando ya existe uno)
- Opción A (recomendada): POST /api/auth/register/admin con token de ADMIN
  - URL: http://localhost:8080/api/auth/register/admin
  - Header: Authorization: Bearer TOKEN_ADMIN
  - Body: email, password, firstName, lastName
  - Respuesta: 201
- Opción B: POST /api/user/admin con token de ADMIN
  - URL: http://localhost:8080/api/user/admin
  - Header: Authorization: Bearer TOKEN_ADMIN
  - Body (JSON):
    {
      "email": "admin2@correo.com",
      "firstName": "Admin2",
      "lastName": "Secundario"
    }
  - Respuesta: 200

9) Endpoints públicos de Jobs (verificación rápida)
- Método: GET
- URL: http://localhost:8080/api/jobs
- No necesita token. Debe responder 200 y listar vacío o con datos si ya existen.

Solución de problemas (401/403 más comunes)
- 401 Unauthorized:
  - Falta el header Authorization o el formato no es correcto. Debe ser: Authorization: Bearer TU_TOKEN
  - Token expirado o inválido. Vuelve a loguearte y reemplaza el token en Postman.
  - Estás llamando una URL distinta a la documentada (por ejemplo /api/auth/register/admin con barra final no debería fallar, pero verifica ortografía y método).
- 403 Forbidden:
  - Tienes token válido, pero tu rol no tiene permisos. Por ejemplo, usuario CANDIDATE intentando GET /api/user.
  - Intentas crear admin cuando ya existe uno, pero no envías token de ADMIN.

Consejos de Postman
- Guarda una colección con las 5 requests principales: Registrar Admin, Login, Me, Listar Usuarios, Registrar Usuario.
- Usa un Environment con variables baseUrl (http://localhost:8080) y token. Luego define:
  - {{baseUrl}}/api/auth/login
  - Header: Authorization -> Bearer {{token}}
- En la pestaña Tests de la request de login, puedes guardar el token automáticamente:
  pm.environment.set("token", pm.response.json().token);

Verificación rápida (checklist)
- [ ] POST /api/auth/register/admin sin token funciona solo si no hay ADMIN.
- [ ] POST /api/auth/login devuelve token.
- [ ] GET /api/user/me con token responde 200.
- [ ] GET /api/user con token ADMIN responde 200; con token de usuario normal responde 403.
- [ ] POST /api/auth/register crea usuario y NO devuelve token.


## Estructura del proyecto (limpia)

Este repositorio usa la estructura estándar de Maven:
- src/main/java: código fuente de la aplicación
- src/main/resources: configuración y recursos (application.properties)
- src/test/java: tests
- pom.xml: configuración de Maven

