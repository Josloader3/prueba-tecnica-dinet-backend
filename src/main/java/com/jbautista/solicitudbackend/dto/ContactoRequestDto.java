package com.jbautista.solicitudbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear o asociar un contacto a la solicitud")
public class ContactoRequestDto {

    @Schema(description = "Nombre completo del contacto", example = "Pedro Ramirez")
    @NotBlank(message = "El nombre del contacto es obligatorio")
    @Size(max = 150, message = "El nombre del contacto no puede exceder 150 caracteres")
    private String nombreContacto;

    @Schema(description = "Número telefónico del contacto (solo dígitos numéricos)", example = "911223344")
    @NotBlank(message = "El número de contacto es obligatorio")
    @Pattern(regexp = "^[0-9]+$", message = "El número de contacto solo debe contener caracteres numéricos")
    @Size(min = 6, max = 20, message = "El número de contacto debe tener entre 6 y 20 dígitos")
    private String numeroContacto;
}
