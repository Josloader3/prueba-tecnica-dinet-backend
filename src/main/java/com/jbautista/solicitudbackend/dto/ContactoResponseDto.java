package com.jbautista.solicitudbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detalle del contacto registrado")
public class ContactoResponseDto {

    @Schema(description = "Identificador único del contacto", example = "1")
    private Long id;

    @Schema(description = "Identificador de la solicitud a la que pertenece", example = "1")
    private Long solicitudId;

    @Schema(description = "Nombre del contacto", example = "Pedro Ramirez")
    private String nombreContacto;

    @Schema(description = "Número telefónico del contacto", example = "911223344")
    private String numeroContacto;
}
