package com.jbautista.solicitudbackend.controller;

import com.jbautista.solicitudbackend.dto.SolicitudRequestDto;
import com.jbautista.solicitudbackend.dto.SolicitudResponseDto;
import com.jbautista.solicitudbackend.service.SolicitudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
@Tag(name = "Solicitudes", description = "Endpoints para la gestión reactiva de Solicitudes y Contactos")
public class SolicitudController {

    private final SolicitudService solicitudService;

    @Operation(summary = "Registrar una nueva solicitud", description = "Crea una solicitud con código autogenerado y almacena sus contactos asociados en tabla separada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Solicitud creada exitosamente",
                    content = @Content(schema = @Schema(implementation = SolicitudResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o errores de validación", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<SolicitudResponseDto>> crearSolicitud(
            @Valid @RequestBody SolicitudRequestDto requestDto) {
        return solicitudService.crearSolicitud(requestDto)
                .map(creada -> ResponseEntity.status(HttpStatus.CREATED).body(creada));
    }

    @Operation(summary = "Listar solicitudes con filtros y ordenamiento", description = "Permite filtrar por fecha de envío y tipo de solicitud, y ordenar dinámicamente por columnas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de solicitudes obtenida exitosamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SolicitudResponseDto.class))))
    })
    @GetMapping
    public Flux<SolicitudResponseDto> listarSolicitudes(
            @Parameter(description = "Filtrar por fecha de envío (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @Parameter(description = "Filtrar por tipo de solicitud")
            @RequestParam(required = false) String tipoSolicitud,
            @Parameter(description = "Campo de ordenamiento (codigo, marca, tipoSolicitud, fechaEnvio, nombreContacto, numeroContacto, fechaCreacion, id)")
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @Parameter(description = "Dirección de ordenamiento (asc, desc)")
            @RequestParam(required = false, defaultValue = "desc") String sortDirection) {
        return solicitudService.listarSolicitudes(fecha, tipoSolicitud, sortBy, sortDirection);
    }

    @Operation(summary = "Obtener solicitud por ID", description = "Retorna el detalle completo de una solicitud y su lista de contactos asociados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud encontrada",
                    content = @Content(schema = @Schema(implementation = SolicitudResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<SolicitudResponseDto>> obtenerPorId(
            @Parameter(description = "ID numérico de la solicitud", example = "1")
            @PathVariable Long id) {
        return solicitudService.obtenerPorId(id)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Obtener solicitud por Código autogenerado", description = "Retorna el detalle completo de una solicitud usando su código único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud encontrada",
                    content = @Content(schema = @Schema(implementation = SolicitudResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada con el código proporcionado", content = @Content)
    })
    @GetMapping("/codigo/{codigo}")
    public Mono<ResponseEntity<SolicitudResponseDto>> obtenerPorCodigo(
            @Parameter(description = "Código autogenerado de la solicitud", example = "SOL-20261029-1001")
            @PathVariable String codigo) {
        return solicitudService.obtenerPorCodigo(codigo)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Exportar solicitudes a formato CSV", description = "Genera un archivo CSV descargable con el listado de solicitudes y sus datos registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo CSV generado exitosamente",
                    content = @Content(mediaType = "text/csv"))
    })
    @GetMapping(value = "/export/csv")
    public Mono<ResponseEntity<String>> exportarCsv(
            @Parameter(description = "Filtrar por fecha de envío (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @Parameter(description = "Filtrar por tipo de solicitud")
            @RequestParam(required = false) String tipoSolicitud) {
        return solicitudService.exportarCsv(fecha, tipoSolicitud)
                .map(csvContent -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"solicitudes_" + LocalDate.now() + ".csv\"")
                        .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                        .body(csvContent));
    }

    @Operation(summary = "Listar tipos de solicitud únicos", description = "Retorna la lista de tipos de solicitud existentes para llenar combos de filtrado.")
    @GetMapping(value = "/tipos", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<String>> listarTiposSolicitud() {
        return solicitudService.listarTiposSolicitud().collectList();
    }

    @Operation(summary = "Listar marcas únicas", description = "Retorna la lista de marcas comerciales existentes en la base de datos.")
    @GetMapping(value = "/marcas", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<String>> listarMarcas() {
        return solicitudService.listarMarcas().collectList();
    }
}
