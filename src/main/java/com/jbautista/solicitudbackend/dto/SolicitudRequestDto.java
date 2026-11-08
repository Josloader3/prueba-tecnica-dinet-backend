package com.jbautista.solicitudbackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos requeridos para registrar una nueva solicitud")
public class SolicitudRequestDto {

    @Schema(description = "Marca comercial", example = "Samsung")
    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 100, message = "La marca no puede exceder 100 caracteres")
    private String marca;

    @Schema(description = "Tipo o motivo de la solicitud", example = "Cotización")
    @NotBlank(message = "El tipo de solicitud es obligatorio")
    @Size(max = 100, message = "El tipo de solicitud no puede exceder 100 caracteres")
    private String tipoSolicitud;

    @Schema(description = "Fecha de envío (formato YYYY-MM-DD)", example = "2026-10-29")
    @NotNull(message = "La fecha de envío es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaEnvio;

    @Schema(description = "Número telefónico de contacto principal (solo dígitos)", example = "987654321")
    @NotBlank(message = "El número de contacto es obligatorio")
    @Pattern(regexp = "^[0-9]+$", message = "El número de contacto solo debe permitir caracteres numéricos")
    @Size(min = 6, max = 20, message = "El número de contacto debe tener entre 6 y 20 dígitos")
    private String numeroContacto;

    @Schema(description = "Nombre del contacto principal", example = "Carlos Mendoza")
    @NotBlank(message = "El nombre de contacto es obligatorio")
    @Size(max = 150, message = "El nombre de contacto no puede exceder 150 caracteres")
    private String nombreContacto;

    @Schema(description = "Lista opcional de N contactos adicionales asociados a la solicitud")
    @Valid
    @Builder.Default
    private List<ContactoRequestDto> contactos = new ArrayList<>();
}
