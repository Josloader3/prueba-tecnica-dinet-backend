package com.jbautista.solicitudbackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta detallada de una solicitud registrada")
public class SolicitudResponseDto {

    @Schema(description = "Identificador interno", example = "1")
    private Long id;

    @Schema(description = "Código autogenerado de la solicitud", example = "SOL-20261029-4821")
    private String codigo;

    @Schema(description = "Marca comercial", example = "Samsung")
    private String marca;

    @Schema(description = "Tipo de solicitud", example = "Cotización")
    private String tipoSolicitud;

    @Schema(description = "Fecha de envío", example = "2026-10-29")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaEnvio;

    @Schema(description = "Número telefónico de contacto principal", example = "987654321")
    private String numeroContacto;

    @Schema(description = "Nombre de contacto principal", example = "Carlos Mendoza")
    private String nombreContacto;

    @Schema(description = "Fecha y hora de registro en el sistema")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Lista de N contactos asociados a la solicitud")
    @Builder.Default
    private List<ContactoResponseDto> contactos = new ArrayList<>();
}
