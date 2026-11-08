package com.jbautista.solicitudbackend.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("contactos")
public class Contacto {

    @Id
    private Long id;

    @Column("solicitud_id")
    private Long solicitudId;

    @Column("nombre_contacto")
    private String nombreContacto;

    @Column("numero_contacto")
    private String numeroContacto;
}
