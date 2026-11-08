package com.jbautista.solicitudbackend.service;

import com.jbautista.solicitudbackend.dto.SolicitudRequestDto;
import com.jbautista.solicitudbackend.dto.SolicitudResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface SolicitudService {

    Mono<SolicitudResponseDto> crearSolicitud(SolicitudRequestDto requestDto);

    Flux<SolicitudResponseDto> listarSolicitudes(LocalDate fecha, String tipoSolicitud, String sortBy, String sortDirection);

    Mono<SolicitudResponseDto> obtenerPorId(Long id);

    Mono<SolicitudResponseDto> obtenerPorCodigo(String codigo);

    Flux<String> listarTiposSolicitud();

    Flux<String> listarMarcas();

    Mono<String> exportarCsv(LocalDate fecha, String tipoSolicitud);
}
