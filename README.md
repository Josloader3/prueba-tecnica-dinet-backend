# Sistema de Gestion de Solicitudes - Backend

Servicio REST reactivo desarrollado con Spring Boot y Spring WebFlux sobre Netty, con persistencia no bloqueante en PostgreSQL mediante Spring Data R2DBC.

## Requisitos
- Java 21 o superior
- PostgreSQL 14+ (local o en contenedor)

## Configuracion
Los parametros de conexion a la base de datos se encuentran en `src/main/resources/application.yaml`:

- Host: `localhost:5432`
- Base de datos: `dinet_db`
- Usuario: 
- Contrasena: 

Las tablas e indices se crean de forma automatica al iniciar la aplicacion mediante los scripts `src/main/resources/schema.sql` y `src/main/resources/data.sql`.

## Ejecucion
```bash
# Windows
.\gradlew.bat bootRun

# Linux / macOS
./gradlew bootRun
```

El servicio estara disponible en `http://localhost:8080`.

## Documentacion y Contrato OpenAPI
- JSON OpenAPI: http://localhost:8080/v3/api-docs
- Archivo de especificacion YAML: `docs/openapi.yaml`

## Endpoints Principales
- `POST /api/solicitudes`: Registro de solicitud y lista de contactos asociados. Devuelve el codigo autogenerado.
- `GET /api/solicitudes`: Listado con soporte de ordenamiento por columnas (`sortBy`, `sortDirection`) y filtros (`fecha`, `tipoSolicitud`).
- `GET /api/solicitudes/codigo/{codigo}`: Detalle de una solicitud y sus contactos.
- `GET /api/solicitudes/export/csv`: Descarga del reporte en formato CSV.
- `GET /api/solicitudes/tipos`: Listado de tipos de solicitud unicos para filtros.
- `GET /api/solicitudes/marcas`: Listado de marcas comerciales registradas.
