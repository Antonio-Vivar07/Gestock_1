# Gestock

## Nombre de la app
Gestock

## Integrantes
- Daniel Bravo  
- Antonio Vivar

## Descripción
Gestock es una aplicación móvil Android (Kotlin) para la gestión de inventarios, productos y movimientos. La app se comunica con un backend REST (Java / Spring Boot, Maven). Usa Retrofit/OkHttp en el cliente móvil y JWT para autenticación en el backend.

## Tecnologías principales
- App móvil: Kotlin, Android (Android Studio, Gradle)  
- Comunicación HTTP: Retrofit + OkHttp  
- Backend: Java, Spring Boot, Maven  
- Intellij IDEA
- Persistencia backend: MongoDB (clases anotadas con @Document)  
- Tests: JUnit (tests locales e instrumentados detectados en app)

## Funcionalidades principales
- Listado y consulta de inventarios y productos.  
- Creación, edición, borrado lógico y restauración de productos.  
- Registro de movimientos de inventario (INGRESO / EGRESO / AJUSTE) con cálculo automático de stock.  
- Autenticación por JWT y control de roles (ADMINISTRADOR / TRABAJADOR).  
- Reportes de inventario (endpoint para reportes).  
- Captura mediante cámara (permiso declarado en AndroidManifest).  
- Pruebas unitarias básicas.

## Endpoints usados
Base URL : http://10.0.2.2:8080/  
(La app define esta URL en `app/src/main/java/.../RetrofitClient.kt` para uso con emulador Android.)

Rutas y comportamiento principales:

1) Usuarios / Autenticación
- POST /api/users/register  
  - Registra usuario. Público.
  - Body: { "username","email","password","role" }
- POST /api/users/login  
  - Autentica y devuelve token JWT. Público. Respuesta incluye { username, email, role, token }.
- GET /api/users  
  - Lista usuarios. Requiere rol ADMINISTRADOR.
- PUT /api/users/{username}/role  
  - Actualiza rol de un usuario. Requiere rol ADMINISTRADOR.

2) Productos
- GET /api/products  
  - Lista productos activos. Autenticado.
- POST /api/products  
  - Crear producto. Autenticado. Header opcional: X-User-Info para auditoría.
  - Body ejemplo: { "nombre","codigoQr","stockActual","categoria","ubicacion","minStock" }
- GET /api/products/{id}  
  - Obtener producto por id. Autenticado.
- DELETE /api/products/{id}  
  - Borrado lógico (status -> DELETED). Requiere ADMINISTRADOR.
- PUT /api/products/{id}/restore  
  - Restaurar producto (DELETED -> ACTIVE). Requiere ADMINISTRADOR.
- PUT /api/products/{id}  
  - Actualizar producto. Autenticado. X-User-Info opcional.
- GET /api/products/status/{status}  
  - Listar por estado (ACTIVE / DELETED). Autenticado.
- PUT /api/products/status/{id}/{status}  
  - Cambiar status (requiere ADMINISTRADOR).

3) Movimientos
- POST /api/movements  
  - Registrar movimiento. Autenticado. Headers: Authorization + X-User-Info (se usa para auditoría y para asignar lastModifiedBy). Body ejemplo:
    {
      "productCode":"QR001",
      "quantity":5,
      "type":"INGRESO" | "EGRESO" | "AJUSTE",
      "referenceDocument":"Guia123",
      "reason":"Recepción"
    }
  - El servicio actualiza el stock del producto (previousStock, newStock) y guarda el Movement.
- GET /api/movements  
  - Listar movimientos. Autenticado.

4) Inventario / Reportes
- GET /api/inventory/report  
  - Devuelve reporte de productos (ProductReportDTO). Autenticado.

Headers relevantes
- Authorization: Bearer <token> (la mayoría de endpoints requieren autenticación)
- X-User-Info: <username> (opcional en muchos endpoints; usado para auditoría y para rellenar lastModifiedBy)

Referencias de código (controladores)
- ProductController, MovementController, UserController, InventoryController  
(Se encuentran en el repo backend_gestock bajo `src/main/java/com/antonio/msvc_inventory/controllers/`).

## Instrucciones para ejecutar el proyecto (desarrollo)

Requisitos
- Java 17+  
- Android Studio (para la app)  
- Maven o usar `mvnw` incluido (para backend)  
- MongoDB (si el backend lo requiere; revisar `application.properties`)  
- Emulador Android o dispositivo físico
-Intellij IDEA

Backend
1. Clonar:
   - git clone https://github.com/Antonio-Vivar07/backend_gestock.git
2. Entrar al directorio:
   - cd backend_gestock
3. Ejecutar con wrapper:
  - Windows: mvnw.cmd spring-boot:run
5. Revisar `application.properties`/`application.yml` para `server.port` y configuración de MongoDB.

App Android
1. Clonar:
   - git clone https://github.com/Antonio-Vivar07/Gestock_1.git
2. Abrir en Android Studio.
3. Asegurarse que `RetrofitClient.kt` tiene la BASE_URL correcta (emulador: `http://10.0.2.2:8080/`; dispositivo físico: `http://<IP_HOST>:8080/`).
4. Build & Run en Android Studio (o terminal):
   - ./gradlew assembleDebug
   - ./gradlew installDebug (requiere dispositivo/emulador conectado)

Pruebas
- Ejecutar pruebas unitarias: ./gradlew test
- Ejecutar instrumented tests: ./gradlew connectedAndroidTest (requiere dispositivo/emulador)

## APK firmado y ubicación .jks
Ruta está de Android Studio:
  - APK firmado: app/release/app-release.apk
  - Ubicación del Archivo .jks: El archivo .jks no se incluye en el Repositorio por razones de seguridad.
  - Ubicación Local C:\Users\daniel\workspace\archivo.jks

## Código fuente (ubicaciones)
- App móvil (Kotlin): `app/` en https://github.com/Antonio-Vivar07/Gestock_1  
  - Cliente Retrofit: `app/src/main/java/com/example/uinavegacion/data/remote/RetrofitClient.kt`
  - Manifest: `app/src/main/AndroidManifest.xml`
  - Tests: `app/src/test/` y `app/src/androidTest/`
- Backend (Java/Maven): `backend_gestock` Repositorio  
  - Controladores: `src/main/java/com/antonio/msvc_inventory/controllers/`
  - Servicios: `src/main/java/com/antonio/msvc_inventory/services/`
  - Modelos: `src/main/java/com/antonio/msvc_inventory/models/`
  - Seguridad: `src/main/java/com/antonio/msvc_inventory/config/` y `.../security/`
- Revisar que no se hayan agregado credenciales ni archivos sensibles (ej. contraseñas, archivos .jks en repos públicos)

## Enlaces útiles
- Repo app: https://github.com/Antonio-Vivar07/Gestock_1  
- Repo backend: https://github.com/Antonio-Vivar07/backend_gestock  
- Controladores backend :  
  - ProductController: https://github.com/Antonio-Vivar07/backend_gestock/blob/main/src/main/java/com/antonio/msvc_inventory/controllers/ProductController.java  
  - MovementController: https://github.com/Antonio-Vivar07/backend_gestock/blob/main/src/main/java/com/antonio/msvc_inventory/controllers/MovementController.java  
  - UserController: https://github.com/Antonio-Vivar07/backend_gestock/blob/main/src/main/java/com/antonio/msvc_inventory/controllers/UserController.java

