package com.jbautista.solicitudbackend.service.impl;

import com.jbautista.solicitudbackend.dto.ContactoRequestDto;
import com.jbautista.solicitudbackend.dto.ContactoResponseDto;
import com.jbautista.solicitudbackend.dto.SolicitudRequestDto;
import com.jbautista.solicitudbackend.dto.SolicitudResponseDto;
import com.jbautista.solicitudbackend.exception.ResourceNotFoundException;
import com.jbautista.solicitudbackend.model.Contacto;
import com.jbautista.solicitudbackend.model.Solicitud;
import com.jbautista.solicitudbackend.repository.ContactoRepository;
import com.jbautista.solicitudbackend.repository.SolicitudRepository;
import com.jbautista.solicitudbackend.service.SolicitudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolicitudServiceImpl implements SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final ContactoRepository contactoRepository;

    private static final DateTimeFormatter DATE_CODE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    @Transactional
    public Mono<SolicitudResponseDto> crearSolicitud(SolicitudRequestDto requestDto) {
        log.info("Iniciando creación de solicitud para marca: {}, tipo: {}", requestDto.getMarca(), requestDto.getTipoSolicitud());

        return generarCodigoUnico()
                .flatMap(codigoGenerado -> {
                    Solicitud solicitud = Solicitud.builder()
                            .codigo(codigoGenerado)
                            .marca(requestDto.getMarca().trim())
                            .tipoSolicitud(requestDto.getTipoSolicitud().trim())
                            .fechaEnvio(requestDto.getFechaEnvio())
                            .numeroContacto(requestDto.getNumeroContacto().trim())
                            .nombreContacto(requestDto.getNombreContacto().trim())
                            .fechaCreacion(LocalDateTime.now())
                            .build();

                    return solicitudRepository.save(solicitud)
                            .flatMap(solicitudGuardada -> {
                                List<ContactoRequestDto> contactosDto = requestDto.getContactos();
                                if (contactosDto == null || contactosDto.isEmpty()) {
                                    return Mono.just(mapToResponseDto(solicitudGuardada, List.of()));
                                }

                                List<Contacto> contactosEntidad = contactosDto.stream()
                                        .filter(c -> c.getNombreContacto() != null && !c.getNombreContacto().isBlank())
                                        .map(c -> Contacto.builder()
                                                .solicitudId(solicitudGuardada.getId())
                                                .nombreContacto(c.getNombreContacto().trim())
                                                .numeroContacto(c.getNumeroContacto().trim())
                                                .build())
                                        .collect(Collectors.toList());

                                if (contactosEntidad.isEmpty()) {
                                    return Mono.just(mapToResponseDto(solicitudGuardada, List.of()));
                                }

                                return contactoRepository.saveAll(contactosEntidad)
                                        .collectList()
                                        .map(contactosGuardados -> mapToResponseDto(solicitudGuardada, contactosGuardados));
                            });
                })
                .doOnSuccess(dto -> log.info("Solicitud creada exitosamente con código: {}", dto.getCodigo()))
                .doOnError(err -> log.error("Error al crear la solicitud: {}", err.getMessage()));
    }

    @Override
    public Flux<SolicitudResponseDto> listarSolicitudes(LocalDate fecha, String tipoSolicitud, String sortBy, String sortDirection) {
        log.info("Consultando solicitudes con filtros - fecha: {}, tipo: {}, sortBy: {}, dir: {}", 
                fecha, tipoSolicitud, sortBy, sortDirection);

        Flux<Solicitud> solicitudesFlux;
        boolean hasFecha = (fecha != null);
        boolean hasTipo = (tipoSolicitud != null && !tipoSolicitud.isBlank() && !tipoSolicitud.equalsIgnoreCase("TODOS"));

        if (hasFecha && hasTipo) {
            solicitudesFlux = solicitudRepository.findByFechaEnvioAndTipoSolicitudIgnoreCase(fecha, tipoSolicitud.trim());
        } else if (hasFecha) {
            solicitudesFlux = solicitudRepository.findByFechaEnvio(fecha);
        } else if (hasTipo) {
            solicitudesFlux = solicitudRepository.findByTipoSolicitudIgnoreCase(tipoSolicitud.trim());
        } else {
            solicitudesFlux = solicitudRepository.findAll();
        }

        return solicitudesFlux
                .flatMap(solicitud -> 
                    contactoRepository.findBySolicitudIdOrderByIdAsc(solicitud.getId())
                            .collectList()
                            .map(contactos -> mapToResponseDto(solicitud, contactos))
                )
                .collectList()
                .flatMapMany(lista -> {
                    aplicarOrdenamiento(lista, sortBy, sortDirection);
                    return Flux.fromIterable(lista);
                });
    }

    @Override
    public Mono<SolicitudResponseDto> obtenerPorId(Long id) {
        return solicitudRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Solicitud no encontrada con ID: " + id)))
                .flatMap(solicitud -> 
                    contactoRepository.findBySolicitudIdOrderByIdAsc(solicitud.getId())
                            .collectList()
                            .map(contactos -> mapToResponseDto(solicitud, contactos))
                );
    }

    @Override
    public Mono<SolicitudResponseDto> obtenerPorCodigo(String codigo) {
        return solicitudRepository.findByCodigo(codigo)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Solicitud no encontrada con código: " + codigo)))
                .flatMap(solicitud -> 
                    contactoRepository.findBySolicitudIdOrderByIdAsc(solicitud.getId())
                            .collectList()
                            .map(contactos -> mapToResponseDto(solicitud, contactos))
                );
    }

    @Override
    public Flux<String> listarTiposSolicitud() {
        return solicitudRepository.findDistinctTiposSolicitud();
    }

    @Override
    public Flux<String> listarMarcas() {
        return solicitudRepository.findDistinctMarcas();
    }

    @Override
    public Mono<String> exportarCsv(LocalDate fecha, String tipoSolicitud) {
        return listarSolicitudes(fecha, tipoSolicitud, "fechaCreacion", "desc")
                .collectList()
                .map(this::generarContenidoCsv);
    }

    private Mono<String> generarCodigoUnico() {
        String fechaParte = LocalDate.now().format(DATE_CODE_FORMATTER);
        int aleatorio = ThreadLocalRandom.current().nextInt(1000, 9999);
        String candidato = String.format("SOL-%s-%d", fechaParte, aleatorio);

        return solicitudRepository.existsByCodigo(candidato)
                .flatMap(existe -> {
                    if (Boolean.TRUE.equals(existe)) {
                        return generarCodigoUnico();
                    }
                    return Mono.just(candidato);
                });
    }

    private void aplicarOrdenamiento(List<SolicitudResponseDto> lista, String sortBy, String sortDirection) {
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "id";
        }
        boolean desc = "desc".equalsIgnoreCase(sortDirection);

        Comparator<SolicitudResponseDto> comparator;
        switch (sortBy.toLowerCase()) {
            case "codigo":
                comparator = Comparator.comparing(SolicitudResponseDto::getCodigo, Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            case "marca":
                comparator = Comparator.comparing(SolicitudResponseDto::getMarca, Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            case "tiposolicitud":
            case "tipo":
                comparator = Comparator.comparing(SolicitudResponseDto::getTipoSolicitud, Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            case "fechaenvio":
            case "fecha":
                comparator = Comparator.comparing(SolicitudResponseDto::getFechaEnvio, Comparator.nullsLast(LocalDate::compareTo));
                break;
            case "nombrecontacto":
                comparator = Comparator.comparing(SolicitudResponseDto::getNombreContacto, Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            case "numerocontacto":
                comparator = Comparator.comparing(SolicitudResponseDto::getNumeroContacto, Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            case "fechacreacion":
                comparator = Comparator.comparing(SolicitudResponseDto::getFechaCreacion, Comparator.nullsLast(LocalDateTime::compareTo));
                break;
            case "id":
            default:
                comparator = Comparator.comparing(SolicitudResponseDto::getId, Comparator.nullsLast(Long::compareTo));
                break;
        }

        if (desc) {
            comparator = comparator.reversed();
        }
        lista.sort(comparator);
    }

    private String generarContenidoCsv(List<SolicitudResponseDto> lista) {
        StringBuilder sb = new StringBuilder();
        // BOM UTF-8 for Excel compatibility
        sb.append('\ufeff');
        sb.append("Código,Marca,Tipo de Solicitud,Fecha de Envío,Nombre Contacto Principal,Número Contacto Principal,Cantidad Contactos Adicionales,Fecha Creación\n");

        for (SolicitudResponseDto sol : lista) {
            sb.append(escapeCsv(sol.getCodigo())).append(",")
              .append(escapeCsv(sol.getMarca())).append(",")
              .append(escapeCsv(sol.getTipoSolicitud())).append(",")
              .append(sol.getFechaEnvio() != null ? sol.getFechaEnvio().toString() : "").append(",")
              .append(escapeCsv(sol.getNombreContacto())).append(",")
              .append(escapeCsv(sol.getNumeroContacto())).append(",")
              .append(sol.getContactos() != null ? sol.getContactos().size() : 0).append(",")
              .append(sol.getFechaCreacion() != null ? sol.getFechaCreacion().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "")
              .append("\n");
        }
        return sb.toString();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private SolicitudResponseDto mapToResponseDto(Solicitud solicitud, List<Contacto> contactos) {
        List<ContactoResponseDto> contactosDto = (contactos == null) ? new ArrayList<>() :
                contactos.stream()
                        .map(c -> ContactoResponseDto.builder()
                                .id(c.getId())
                                .solicitudId(c.getSolicitudId())
                                .nombreContacto(c.getNombreContacto())
                                .numeroContacto(c.getNumeroContacto())
                                .build())
                        .collect(Collectors.toList());

        return SolicitudResponseDto.builder()
                .id(solicitud.getId())
                .codigo(solicitud.getCodigo())
                .marca(solicitud.getMarca())
                .tipoSolicitud(solicitud.getTipoSolicitud())
                .fechaEnvio(solicitud.getFechaEnvio())
                .numeroContacto(solicitud.getNumeroContacto())
                .nombreContacto(solicitud.getNombreContacto())
                .fechaCreacion(solicitud.getFechaCreacion())
                .contactos(contactosDto)
                .build();
    }
}
