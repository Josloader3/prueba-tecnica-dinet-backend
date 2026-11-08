package com.jbautista.solicitudbackend.repository;

import com.jbautista.solicitudbackend.model.Contacto;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ContactoRepository extends R2dbcRepository<Contacto, Long> {

    Flux<Contacto> findBySolicitudIdOrderByIdAsc(Long solicitudId);

    Mono<Void> deleteBySolicitudId(Long solicitudId);
}
