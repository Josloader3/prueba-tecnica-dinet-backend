package com.jbautista.solicitudbackend.repository;

import com.jbautista.solicitudbackend.model.Solicitud;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface SolicitudRepository extends R2dbcRepository<Solicitud, Long> {

    Mono<Solicitud> findByCodigo(String codigo);

    Mono<Boolean> existsByCodigo(String codigo);

    Flux<Solicitud> findByFechaEnvio(LocalDate fechaEnvio);

    Flux<Solicitud> findByTipoSolicitudIgnoreCase(String tipoSolicitud);

    Flux<Solicitud> findByFechaEnvioAndTipoSolicitudIgnoreCase(LocalDate fechaEnvio, String tipoSolicitud);

    @Query("SELECT DISTINCT tipo_solicitud FROM solicitudes ORDER BY tipo_solicitud ASC")
    Flux<String> findDistinctTiposSolicitud();

    @Query("SELECT DISTINCT marca FROM solicitudes ORDER BY marca ASC")
    Flux<String> findDistinctMarcas();
}
