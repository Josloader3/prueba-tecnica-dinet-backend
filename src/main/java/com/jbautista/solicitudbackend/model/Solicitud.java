package com.jbautista.solicitudbackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("solicitudes")
public class Solicitud {

    @Id
    private Long id;

    @Column("codigo")
    private String codigo;

    @Column("marca")
    private String marca;

    @Column("tipo_solicitud")
    private String tipoSolicitud;

    @Column("fecha_envio")
    private LocalDate fechaEnvio;

    @Column("numero_contacto")
    private String numeroContacto;

    @Column("nombre_contacto")
    private String nombreContacto;

    @Column("fecha_creacion")
    private LocalDateTime fechaCreacion;
}
